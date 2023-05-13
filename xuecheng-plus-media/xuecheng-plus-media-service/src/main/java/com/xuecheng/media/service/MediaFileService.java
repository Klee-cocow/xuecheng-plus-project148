package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/***
 * @description TODO
 * @param null 
 * @return
 * @author 咏鹅
 * @date 2023/5/8 20:22
*/
public interface MediaFileService {

    /***
     * @description 媒资文件查询方法
     * @param companyId
     * @param pageParams
     * @param queryMediaParamsDto
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @author 咏鹅
     * @date 2023/5/8 20:23
    */
    PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


    /**
     * @param companyId           机构id
     * @param uploadFileParamsDto 文件信息
     * @param bytes               文件字节数组
     * @param folder              桶下边的子目录
     * @param objectName          对象名称
     * @return com.xuecheng.media.model.dto.UploadFileResultDto
     * @description 上传文件的通用接口
     * @author 咏鹅
     * @date 2023/5/8 20:23
     */
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName);

    @Transactional
    MediaFiles addMediaFilesToMinDb(Long companyId, UploadFileParamsDto uploadFileParamsDto, String fileId, String bucket, String objectName);


    /**
     * @description 检查文件是否存在
     * @param fileMd5
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean>
     * @author 咏鹅
     * @date 2023/3/1 16:19
    */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @description 检查分块是否存在
     * @param fileMd5
     * @param chunkIndex
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean>
     * @author 咏鹅
     * @date 2023/3/1 16:20
    */
    RestResponse<Boolean> checkChunk(String fileMd5,int chunkIndex);


    //上传分块文件
    RestResponse uploadChunk(String fileMd5,int chunk,byte[] bytes);


    RestResponse mergeChunks(Long companyId,String fileMd5,int chunkTotal, UploadFileParamsDto dto) throws IOException;

    MediaFiles getFileById(String id);


    //根据桶和文件路径从minio下载文件
    File downloadFileFromMinIO(File file,String bucket,String objectName);

    void addMediaFilesToMinIO(String filePath, String bucket, String objectName);

}
