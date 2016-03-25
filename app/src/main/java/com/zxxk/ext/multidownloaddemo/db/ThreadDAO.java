package com.zxxk.ext.multidownloaddemo.db;

import com.zxxk.ext.multidownloaddemo.entities.ThreadInfo;

import java.util.List;

/**
 * 数据访问接口
 * Created by Ext on 2015/11/27.
 */
public interface ThreadDAO {

    /**
     * 插入线程信息
     * @param threadInfo
     */
    public void insertThread(ThreadInfo threadInfo);
    /**
     * 删除线程信息可能存在多线程
     */
    public void deleteThread(String url);
    /**
     * 更新线程下载速度
     */

    public void updateThread(String url, int thread_id, int finished);

    /**
     * 查询文件的线程信息
     */
    public List<ThreadInfo> getThreads(String url);

    /**
     * 线程信息是否存在
     */
    public boolean isExists(String url, int thread_id);
}
