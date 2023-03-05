package com.xuecheng.media.api;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/1 16:17
 */
@RestController
public class BigFilesController {
    @Resource
    private MediaFileService mediaFileService;

    @ApiOperation(value = "文件上传前查看文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5){

        return mediaFileService.checkFile(fileMd5);
    }

    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk)throws Exception
    {
//        return RestResponse.success(true);
        return mediaFileService.checkChunk(fileMd5,chunk);
    }


    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadChunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception
    {
        return mediaFileService.uploadChunk(fileMd5,chunk,file.getBytes());
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("upload/mergechunks")
    public RestResponse mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception
    {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setFileType("001002");//视频类型
        uploadFileParamsDto.setTags("课程视频");
        return mediaFileService.mergeChunks(companyId,fileMd5,chunkTotal,uploadFileParamsDto);
    }
}
