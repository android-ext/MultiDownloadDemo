package com.zxxk.ext.multidownloaddemo.services;

import android.content.Context;
import android.content.Intent;

import com.zxxk.ext.multidownloaddemo.db.ThreadDAO;
import com.zxxk.ext.multidownloaddemo.db.ThreadDAOImpl;
import com.zxxk.ext.multidownloaddemo.entities.FileInfo;
import com.zxxk.ext.multidownloaddemo.entities.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 下载任务类
 * Created by Ext on 2015/11/27.
 */
public class DownloadTask {

    private Context mContext = null;
    private FileInfo mFileInfo = null;
    private ThreadDAO mDao = null;
    private int mFinished = 0;
    public boolean isPause = false;
    private int mThreadCount = 1; // 线程数量
    private List<DownloadThread> mDownloadThreadList = null; // 线程集合
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();


    public DownloadTask(Context mContext, FileInfo mFileInfo, int mThreadCount) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        this.mThreadCount = mThreadCount;
        mDao = new ThreadDAOImpl(mContext);
    }



    public void download() {
        // 读取数据库的线程信息
        List<ThreadInfo> threads = mDao.getThreads(mFileInfo.getUrl());
        if (threads.size() == 0) {
            // 获得每个线程下载的长度
            int length = mFileInfo.getLength() / mThreadCount + 1;
            // 创建线程信息
            for (int i = 0; i < mThreadCount; i++) {
                // 计算每个线程下载区段的起始和结束位置
                ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), length * i, (i + 1) * length - 1, 0);
                if (i == mThreadCount - 1) {
                    threadInfo.setEnd(mFileInfo.getLength() - 1);
                }
                // 向数据库插入线程信息
                mDao.insertThread(threadInfo);
                // 添加到线程信息集合中
                threads.add(threadInfo);
            }
        }
        mDownloadThreadList = new ArrayList<DownloadThread>();
        // 启动多个线程进行下载
        for (ThreadInfo threadInfo : threads) {
            DownloadThread thread = new DownloadThread(threadInfo);
            DownloadTask.sExecutorService.execute(thread);
            // 添加线程到集合中
            mDownloadThreadList.add(thread);
        }

    }

    // 同步方法 判断是否所有线程都执行完毕
    private synchronized void checkAllThreadsFinished() {
        boolean allFinished = true;
        // 判断线程是否都执行完毕
        for (DownloadThread thread : mDownloadThreadList) {
            if (!thread.isFinished) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            // 删除下载记录
            mDao.deleteThread(mFileInfo.getUrl());
            // 发送广播通知UI下载任务结束
            Intent intent = new Intent(DownloadService.ACTION_FINISH);
            intent.putExtra("fileInfo", mFileInfo);
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * 下载线程
     */
    class DownloadThread extends Thread {
        private ThreadInfo mThreadInfo = null;
        public boolean isFinished = false; // 标识线程是否结束

        public DownloadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream input = null;
            try {
                URL url = new URL(mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                // 设置下载位置
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                // 设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                mFinished += mThreadInfo.getFinished();
                // 开始下载
                if (conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    // 读取数据
                    input = conn.getInputStream();
                    byte[] buffer = new byte[1024 << 2];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = input.read(buffer)) != -1) {
                        // 写入文件
                        raf.write(buffer, 0, len);
                        // 累加整个文件完成进度
                        mFinished += len;
                        // 累加每个线程完成的进度
                        mThreadInfo.setFinished(mThreadInfo.getFinished() + len);
                        if (System.currentTimeMillis() - time > 1000) {
                            time = System.currentTimeMillis();
                            int f = mFinished * 100 / mFileInfo.getLength();
                            if (f > mFileInfo.getFinished()) {
                                intent.putExtra("finished", f);
                                intent.putExtra("id", mFileInfo.getId());
                                mContext.sendBroadcast(intent);
                            }
                        }
                        // 在下载暂停时，保存下载进度
                        if (isPause) {
                            mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mThreadInfo.getFinished());
                            return;
                        }
                    }
                    // 标识线程执行完毕
                    isFinished = true;
                    // 检查下载任务是否完毕
                    checkAllThreadsFinished();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    raf.close();
                    conn.disconnect();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
