package com.zxxk.ext.multidownloaddemo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zxxk.ext.multidownloaddemo.entities.FileInfo;
import com.zxxk.ext.multidownloaddemo.services.DownloadService;

import java.util.List;

/**
 * 文件列表适配器
 * Created by Ext on 2015/11/30.
 */
public class FileListAdapter extends BaseAdapter {

    private Context mContext = null;
    private List<FileInfo> mFileList = null;

    public FileListAdapter(List<FileInfo> mFileList, Context mContext) {
        this.mFileList = mFileList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        // 设置视图中的控件
        final FileInfo fileInfo = mFileList.get(position);

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
            if (!holder.tvFile.getTag().equals(Integer.valueOf(fileInfo.getId()))) {
                convertView = null;
            }
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

            holder.tvFile.setText(fileInfo.getFileName());
            holder.pbFile.setMax(100);
            holder.btStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 通过Intent传递参数给Service
                    Intent intent = new Intent(mContext, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);
                    intent.putExtra("fileInfo", fileInfo);
                    mContext.startService(intent);
                }
            });
            holder.btStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 通过Intent传递参数给Service
                    Intent intent = new Intent(mContext, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_STOP);
                    intent.putExtra("fileInfo", fileInfo);
                    mContext.startService(intent);
                }
            });
            holder.tvFile.setTag(Integer.valueOf(fileInfo.getId()));
        }
        holder.pbFile.setProgress(fileInfo.getFinished());
        return convertView;
    }

    /**
     * 更新列表项中的进度条
     */
    public void updateProgress(int id, int progress) {
        FileInfo fileInfo = mFileList.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();
    }

    static class ViewHolder { // 内部类在程序运行过程中只会被加载一次
        TextView tvFile;
        Button btStop, btStart;
        ProgressBar pbFile;

        public ViewHolder(View convertView) {
            tvFile = (TextView) convertView.findViewById(R.id.fileTile);
            btStart = (Button) convertView.findViewById(R.id.startBtn);
            btStop = (Button) convertView.findViewById(R.id.pauseBtn);
            pbFile = (ProgressBar) convertView.findViewById(R.id.progressBar);
        }
    }
}
