package com.zxxk.ext.multidownloaddemo.entities;

import java.io.Serializable;

/**
 * Created by Ext on 2015/11/27.
 */
public class FileInfo implements Serializable{

    private int id;
    private String url;
    private String fileName;
    private int length;
    private int finished;

    public FileInfo(int id, int finished, int length, String fileName, String url) {
        this.id = id;
        this.finished = finished;
        this.length = length;
        this.fileName = fileName;
        this.url = url;
    }

    public FileInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                '}';
    }
}
