package com.xuecheng.media;

import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;

/***
 * @description TODO
 * @param null 
 * @return 测试minio上传文件、删除文件、查询文件
 * @author 咏鹅
 * @date 2023/5/8 20:25
*/
public class MinIOTest {

    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();


    @Test
    public void upload() {

        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("video")
                    .object("bear.mp4")//同一个桶内对象名不能重复
                    .filename("O:\\bigfile_test\\bear.mp4")
                    .build();
            //上传
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传成功了");
        } catch (Exception e) {
            System.out.println("上传失败");
        }


    }
    //指定桶内的子目录
    @Test
    public void upload2() {

        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbcket")
                    .object("test/bear.mp4")//同一个桶内对象名不能重复
                    .filename("O:\\project\\bear.mp4")
                    .build();
            //上传
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传成功了");
        } catch (Exception e) {
            System.out.println("上传失败");
        }


    }
    //删除文件
    @Test
    public void delete() {

        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket("testbcket").object("test/bear.mp4").build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
        }

    }
    //查询文件
    @Test
    public void getFile() {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbcket").object("1.mp4").build();
        try(
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream = new FileOutputStream(new File("D:\\develop\\upload\\1_1.mp4"));
                ) {

            if(inputStream!=null){
                IOUtils.copy(inputStream,outputStream);
            }
        } catch (Exception e) {
        }

    }

}
