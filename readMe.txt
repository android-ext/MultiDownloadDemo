FileInfo类记录文件的实体
id,
url
fileName    文件名称
length      文件的总长度
finished    文件已经下载的长度   用于更新ui进度

ThreadInfo记录线程信息
id;
url;
start;      每个线程块下载的起始位置
end;        每个线程块下载的结束位置
finished;   每个线程块已经完成的进度  用于保存到数据库