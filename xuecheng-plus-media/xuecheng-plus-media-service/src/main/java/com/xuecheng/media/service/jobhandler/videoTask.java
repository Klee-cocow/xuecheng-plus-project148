package com.xuecheng.media.service.jobhandler;

import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/5 22:52
 */
@Slf4j
public class videoTask {

    @Resource
    private MediaFileProcessService mediaFileProcessService;

    @Resource
    private MediaFileService mediaFileService;

    @XxlJob("videoJobHander")
    public void videoJobHander() throws Exception {
        // 分片序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片数量
        int shardTotal = XxlJobHelper.getShardTotal();

        //查询待处理的任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, 2);
        if(mediaProcessList == null || mediaProcessList.size() <=0){
            log.debug("查询到待处理的任务为0");
            return;
        }

        //需要处理的任务数
        int size = mediaProcessList.size();

        //创建size个线程数量的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);

        //遍历mediaProcessList
        mediaProcessList.forEach(mediaProcess -> {
            threadPool.execute(()->{
                //视频处理状态
                String status = mediaProcess.getStatus();
                if(status.equals("2")){
                    log.debug("视频已经处理不用再次处理，视频信息：{}",mediaProcess);
                    return;
                }
                //桶
                String bucket = mediaProcess.getBucket();
                //存储路径
                String filePath = mediaProcess.getFilePath();
                //原始视频的md5值
                String fileId = mediaProcess.getFileId();
                //原始文件名称
                String filename = mediaProcess.getFilename();

                //创建文件
                File originalFile = null;
                File mp4File = null;

                try {
                    originalFile = File.createTempFile("original", null);
                    mp4File = File.createTempFile("mp4", ".mp4");

                }catch (IOException e){
                    log.error("处理视频前创建临时文件失败");
                    return;
                }

                try {

                    //将原始视频下载到本地
                    mediaFileService.downloadFileFromMinIO(originalFile,bucket,filePath);

                }catch (Exception e){
                    log.error("下载原始文件过程出错:{}，文件信息{}",e.getMessage(),mediaProcess);
                    return;
                }



                //调用工具类将avi格式转换成mp4

                //上传到minio

                //记录进数据库

            });
        });



    }
}
