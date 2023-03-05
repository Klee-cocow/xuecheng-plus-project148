package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/5 17:47
 */
public interface MediaFileProcessService {

    List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
