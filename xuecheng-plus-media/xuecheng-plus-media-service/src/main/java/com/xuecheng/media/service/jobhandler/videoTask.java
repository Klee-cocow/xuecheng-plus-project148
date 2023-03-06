package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/5 22:52
 */
@Slf4j
@Component
public class videoTask {

    @Resource
    private MediaFileProcessService mediaFileProcessService;

    @Resource
    private MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    @XxlJob("videoJobHander")
    public void videoJobHander() throws Exception {
        // 分片序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片数量
        int shardTotal = XxlJobHelper.getShardTotal();

        //查询待处理的任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardTotal, shardIndex, 2);
        if(mediaProcessList == null || mediaProcessList.size() <=0){
            log.debug("查询到待处理的任务为0");
            return;
        }

        //需要处理的任务数
        int size = mediaProcessList.size();

        //创建size个线程数量的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);
        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);


        //遍历mediaProcessList
        mediaProcessList.forEach(mediaProcess -> {
            threadPool.execute(()->{
                //视频处理状态
                String status = mediaProcess.getStatus();
                if(status.equals("2")){
                    log.debug("视频已经处理不用再次处理，视频信息：{}",mediaProcess);
                    //计数器减一
                    countDownLatch.countDown();
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
                    //计数器减一
                    countDownLatch.countDown();
                    return;
                }

                try {

                    //将原始视频下载到本地
                    mediaFileService.downloadFileFromMinIO(originalFile,bucket,filePath);
                }catch (Exception e){
                    log.error("下载原始文件过程出错:{}，文件信息{}",e.getMessage(),mediaProcess);
                    //计数器减一
                    countDownLatch.countDown();
                    return;
                }




                //调用工具类将avi格式转换成mp4
                String mp4_name = fileId + ".mp4";
                String mp4_path =  mp4File.getAbsolutePath();

                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, originalFile.getAbsolutePath(),mp4_name, mp4_path);
                //开始视频转换，成功返回success，失败返回失败原因
                String result = videoUtil.generateMp4();
                String statusNew = "3"; //表示处理失败
                String url = null; //最终访问路径

                if(result.equals("success")|| result != null){
                    result = "success";
                    String objectName = getFilePathByMd5(fileId, ".mp4");
                    try {
                        //上传到minio
                        mediaFileService.addMediaFilesToMinIO(mp4_path,bucket,objectName);
                    }catch (Exception e){
                        log.debug("上传文件出错：{}",e.getMessage());
                        //计数器减一
                        countDownLatch.countDown();
                        return;
                    }
                    statusNew = "2"; //表示处理成功
                    url = "/"+bucket+"/"+objectName;
                    try {
                        //记录进数据库
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(),statusNew,fileId,url,result);
                    }catch (Exception e){
                        log.debug("保存文件任务出错：{}",e.getMessage());
                        //计数器减一
                        countDownLatch.countDown();
                        return;
                    }

                }else{
                    log.debug("视频转换失败");
                }

                //计数器减一
                countDownLatch.countDown();

            });
        });

        //阻塞到任务执行完成，当countDownLatch计数器归零，这里的阻塞解除;
        countDownLatch.await(30, TimeUnit.MINUTES);


    }
    //获取文件路径md5名称
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }
}
