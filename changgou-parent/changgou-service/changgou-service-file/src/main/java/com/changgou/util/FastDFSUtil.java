package com.changgou.util;

import com.changgou.file.FastDFSFile;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-12 16:55
 */
public class FastDFSUtil {
    /**
     * 初始化tracker信息
     */
    static {
        try {
            //获取tracker的配置文件fdfs_clent.conf的位置
            String filePath = new ClassPathResource("fdfs_clent.conf").getPath();
            //加载tracker配置信息
            ClientGlobal.init(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****
     * 文件上传
     * @param file : 要上传的文件信息封装->FastDFSFile
     * @return String[]
     *          1:文件上传所存储的组名
     *          2:文件存储路径
     */
    public static String[] upload(FastDFSFile file) {
        //获取文件作者
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author",file.getAuthor());
        /***
         * 文件上传后的返回值
         * uploadResults[0]:文件上传所存储的组名，例如:group1
         * uploadResults[1]:文件存储路径,例如：M00/00/00/wKjThF0DBzaAP23MAAXz2mMp9oM26.jpeg
         */
        String[] uploadResults = null;
        try {
            //获取StorageClient对象
            StorageClient storageClient = getStorageClient();
            //执行文件上传
            uploadResults = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadResults;
    }

    /**
     * 获取文件信息
     * @param groupName 组名
     * @param remoteFileName 文件存储完整名
     * @return
     */
    public static FileInfo getFile(String groupName,String remoteFileName){
        try {
            StorageClient storageClient = getStorageClient();
            return storageClient.get_file_info(groupName,remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  文件下载
     * @param groupName 组名
     * @param remoteFileName 文件存储完整名
     * @return
     */
    public static InputStream downFiile(String groupName,String remoteFileName){
        try {
            //获取StorageClient对象
            StorageClient storageClient = getStorageClient();
            byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
            return new ByteArrayInputStream(fileByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  文件删除
     * @param groupName 组名
     * @param remoteFileName 文件存储完整名
     */
    public static void deleteFile(String groupName,String remoteFileName){
        try {
            StorageClient storageClient = getStorageClient();
            storageClient.delete_file(groupName,remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取组信息
     * @param groupName 组名
     * @return
     */
    public static StorageServer getStorage(String groupName){
        try {
            //创建TrackerClient客户端对象
            TrackerClient trackerClient = new TrackerClient();
            //通过TrackerClient对象获取TrackerServer信息
            TrackerServer trackerServer = trackerClient.getConnection();
            return trackerClient.getStoreStorage(trackerServer,groupName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件组名和文件存储路径获取Strorage服务的ip、端口信息
     * @param groupName 组名
     * @param remoteFileName 文件存储完整名
     * @return
     */
    public static ServerInfo[] getServerInfo(String groupName,String remoteFileName){
        try {
            //创建TrackerClient客户端对象
            TrackerClient trackerClient = new TrackerClient();
            //通过TrackerClient对象获取TrackerServer信息
            TrackerServer trackerServer = trackerClient.getConnection();
            return trackerClient.getFetchStorages(trackerServer,groupName,remoteFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取tracker服务地址
     * @return
     */
    public static String getTrackerUrl(){
        try {
            //创建TrackerClient客户端对象
            TrackerClient trackerClient = new TrackerClient();
            //通过TrackerClient对象获取TrackerServer信息
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取tracker地址
            return "http://"+trackerServer.getInetSocketAddress().getHostString()+":"+ClientGlobal.getG_tracker_http_port();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取trackerServer
     * @return
     * @throws Exception
     */
    public static TrackerServer getTrackerServer() throws Exception{
        //创建TrackerClient客户端对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象获取TrackerServer信息
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerServer;
    }

    /**
     * 获取StorageClient
     * @return
     * @throws Exception
     */
    public static StorageClient getStorageClient() throws Exception{
        //创建TrackerClient客户端对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象获取TrackerServer信息
        TrackerServer trackerServer = trackerClient.getConnection();
        //获取StorageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }
}