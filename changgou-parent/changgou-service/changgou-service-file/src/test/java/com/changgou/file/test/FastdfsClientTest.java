package com.changgou.file.test;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-12 16:18
 */
public class FastdfsClientTest {
    @Test
    public void upload() throws Exception{

        ClientGlobal.init("E:\\myproject\\changgou\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_clent.conf");

        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient(trackerServer, null);

        String[] jpgs = storageClient.upload_file("E:\\111.jpg", "jpg", null);

        for (String jpg:jpgs){
            System.out.println(jpg);
        }
    }
}
