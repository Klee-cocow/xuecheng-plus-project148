package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/5 17:47
 */
@Slf4j
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Resource
    private MediaProcessMapper mediaProcessMapper;

    @Resource
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Resource
    private MediaFilesMapper mediaFilesMapper;

    /**
     * @description 查找待处理任务
     * @param shardTotal  处理器数量
     * @param shardIndex 处理器编号
     * @param count
     * @return java.util.List<com.xuecheng.media.model.po.MediaProcess>
     * @author 咏鹅
     * @date 2023/3/5 18:07
    */
    @Override
    public List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex, int count) {
        return mediaProcessMapper.selecListByShardIndex(shardTotal,shardIndex,count);
    }

    /**
     * @description 更新任务状态
     * @param taskId 任务Id
     * @param status 任务状态
     * @param fileId 文件id
     * @param url     url
     * @param errorMsg 错误信息
     * @return void
     * @author 咏鹅
     * @date 2023/3/5 18:06
    */
    @Transactional
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        if(taskId <=0){
            log.error("taskId不正确");
            return;
        }
        //查询这个任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(mediaProcess == null){
            log.error("更新任务状态时，此任务:{}为空",taskId);
            return;
        }
        LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId,taskId);
        if(status.equals("3")){
            //任务失败
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("3");
            mediaProcess_u.setErrormsg(errorMsg);
            mediaProcessMapper.update(mediaProcess_u,queryWrapperById);
            return;
        }
        if(status.equals("2")){
            //更新待处理表
            mediaProcess.setStatus("2");
            mediaProcess.setUrl(url);
            mediaProcess.setFinishDate(LocalDateTime.now());
            mediaProcessMapper.updateById(mediaProcess);

            //更新文件表中的url字段
            MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }
        //如果处理成功将待处理表记录删除
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //如果处理成功将任务添加到历史记录表
        mediaProcessMapper.deleteById(taskId);

    }
}
