package com.zxxk.ext.multidownloaddemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zxxk.ext.multidownloaddemo.entities.FileInfo;
import com.zxxk.ext.multidownloaddemo.services.DownloadService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity  {

    private ListView mLvFile = null;
    private List<FileInfo> mFileList = null;
    private FileListAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_FINISH);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void initData() {
        // 创建文件集合
        mFileList = new ArrayList<FileInfo>();
        // 创建文件对象
        FileInfo fileInfoImooc = new FileInfo(0, 0, 0, "imooc.apk", "http://www.imooc.com/mobile/imooc.apk");
        FileInfo fileInfoKky = new FileInfo(1, 0, 0, "kky.apk", "http://kky.zxxk.com/download/kky_v1.3.0_20151030_zxxk.apk");
        FileInfo fileInfoActivator = new FileInfo(2, 0, 0, "Activator.exe", "http://www.imooc.com/download/Activator.exe");
        FileInfo fileInfoText = new FileInfo(3, 0, 0, "hello.txt", "http://192.168.253.1:8080/VideoDemo/hello.txt");
        FileInfo fileInfoActivator2 = new FileInfo(4, 0, 0, "Activator2", "http://www.imooc.com/download/Activator.exe");

        mFileList.add(fileInfoImooc);
        mFileList.add(fileInfoKky);
        mFileList.add(fileInfoActivator);
        mFileList.add(fileInfoText);
        mFileList.add(fileInfoActivator2);

        mAdapter = new FileListAdapter(mFileList, this);
        mLvFile.setAdapter(mAdapter);
    }

    private void initView() {
        mLvFile = (ListView) findViewById(R.id.lvFile);
    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                // 更新进度条
                int finished = intent.getIntExtra("finished", 0);
                int id = intent.getIntExtra("id", 0);
                mAdapter.updateProgress(id, finished);
            } else if (DownloadService.ACTION_FINISH.equals(intent.getAction())){
                // 下载结束
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                // 更新进度为0
                mAdapter.updateProgress(fileInfo.getId(), 0);
                Toast.makeText(MainActivity.this, mFileList.get(fileInfo.getId()).getFileName()+"下载完毕", Toast.LENGTH_LONG).show();
            }
        }
    };
}
