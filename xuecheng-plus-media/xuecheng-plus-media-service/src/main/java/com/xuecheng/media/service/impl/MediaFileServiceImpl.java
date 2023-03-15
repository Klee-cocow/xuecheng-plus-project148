package com.xuecheng.media.service.impl;

import com.alibaba.nacos.common.utils.IoUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.awt.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Resource
    private MediaFilesMapper mediaFilesMapper;

    @Resource
    private MediaProcessMapper mediaProcessMapper;
    @Resource
    private MediaFileService mediaFileService;

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket.files}")
    private String bucket_image;

    @Value("${minio.bucket.videofiles}")
    private String bucket_video;


    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getFileType()), MediaFiles::getFileType, queryMediaParamsDto.getFileType());
        queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()), MediaFiles::getFilename, queryMediaParamsDto.getFilename());
        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }


    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {

        String bucket_files;
        //默认视频文件
        bucket_files = bucket_video;

        if (uploadFileParamsDto.getFileType().equals("001001")) {
            //不是视频文件，更改存储bucket路径
            bucket_files = bucket_image;
        }



        //得到文件的md5值
        String fileMd5 = DigestUtils.md5Hex(bytes);


        if (StringUtils.isEmpty(folder)) {
            //自动生成目录的路径 按年月日生成，
            folder = getFileFolder(new Date(), true, true, true);
        } else if (folder.indexOf("/") < 0) {
            folder = folder + "/";
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();

        if (StringUtils.isEmpty(objectName)) {
            //如果objectName为空，使用文件的md5值为objectName
            objectName = folder + fileMd5 + filename.substring(filename.lastIndexOf("."));
        }


        try {
            addMediaFilesToMinIO(bytes, bucket_files, objectName);

            MediaFiles mediaFiles = mediaFileService.addMediaFilesToMinDb(companyId, uploadFileParamsDto, fileMd5, bucket_files, objectName);

            //准备返回数据
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);

            return uploadFileResultDto;

        } catch (Exception e) {
            log.debug("上传文件失败：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    //根据日期拼接目录
    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuffer folderString = new StringBuffer();
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }

    //将文件上传到分布式文件系统
    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName) {

        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)//同一个桶内对象名不能重复
                    .filename(filePath)
                    .build();
            //上传
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("文件上传成功:{}", filePath);
        } catch (Exception e) {
            XueChengException.cast("文件上传到文件系统失败");
        }
    }


    //将文件上传到分布式文件系统
    private void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName) {

        //资源媒体类型
        String contentType = getMimeTypeByExtension(objectName);


        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            PutObjectArgs builder = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            minioClient.putObject(builder);
        } catch (Exception e) {
            log.debug("上传文件系统出错:{}", e.getMessage());
        }

    }

    //获取文件后缀
    private String getMimeTypeByExtension(String objectName){
        //资源媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流

        if (objectName.indexOf(".") >= 0) {
            //取objectName中的扩展名
            String extension = objectName.substring(objectName.lastIndexOf("."));
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }

    /**
     * @param companyId
     * @param uploadFileParamsDto
     * @param fileId
     * @param bucket
     * @param objectName
     * @return com.xuecheng.media.model.po.MediaFiles
     * @description 将文件信息保存进数据库
     * @author 咏鹅
     * @date 2023/2/28 19:55
     */
    @Transactional
    public MediaFiles addMediaFilesToMinDb(Long companyId, UploadFileParamsDto uploadFileParamsDto, String fileId, String bucket, String objectName) {
        //保存到数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();

            //封装数据
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileId);
            mediaFiles.setFileId(fileId);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setFilename(uploadFileParamsDto.getFilename());
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);

            String extension = null;
            String filename = uploadFileParamsDto.getFilename();
            if(StringUtils.isNotEmpty(filename) && filename.indexOf(".")>=0){
                extension = filename.substring(filename.lastIndexOf("."));
            }

            String mimeType = getMimeTypeByExtension(extension);
            //图片、mp4视频可以设置URL
            if(mimeType.contains("image") || mimeType.contains("mp4")){
                mediaFiles.setUrl("/" + bucket + "/" + objectName);
            }

            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus("002003");

            //插入文件表
            mediaFilesMapper.insert(mediaFiles);

            //对avi视频添加到待处理任务列表
            if(mimeType.equals("video/x-msvideo")){
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles,mediaProcess);
                //设置一个状态
                mediaProcess.setStatus("1"); //未处理
                mediaProcessMapper.insert(mediaProcess);
            }

        }
        return mediaFiles;

    }

    //检查文件
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {

        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            return RestResponse.success(false);
        }
        //查看是否在文件系统
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(mediaFiles.getBucket()).object(mediaFiles.getFilePath()).build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream == null) {
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            return RestResponse.success(false);
        }

        return RestResponse.success(true);
    }

    //检查分块
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {

        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;


        //查询文件系统分块是否存在
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucket_video).object(chunkFilePath).build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream == null) {
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {
        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            addMediaFilesToMinIO(bytes, bucket_video, chunkFilePath);
            return RestResponse.success(true);
        } catch (Exception e) {
            log.error("上传文件失败:{}", e.getMessage());
            return RestResponse.validfail(false, "上传文件失败");
        }

    }

    private File[] checkChunkStatus(String fileMd5, int chunkTotal) {
        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块分拣数组
        File[] chunkFiles = new File[chunkTotal];
        //开始下载
        for (int i = 0; i < chunkTotal; i++) {
            //分块文件路径
            String chunkFilePath = chunkFileFolderPath + i;
            File chunkFile = null;
            try {
                chunkFile = File.createTempFile("chunk", null);
            } catch (IOException e) {
                e.printStackTrace();
                XueChengException.cast("创建分块临时文件出错:" + e.getMessage());
            }

            //查询文件系统中的视频分块是否存在
            chunkFile = downloadFileFromMinIO(chunkFile, bucket_video, chunkFilePath);
            chunkFiles[i] = chunkFile;

        }

        return chunkFiles;
    }

    @Override
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) throws IOException {
        //下载分块
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);

        //得到合并后文件的扩展名
        String filename = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        File tempMergeFile = null;

        try {
            try {
                //创建一个临时文件作为合并文件
                tempMergeFile = File.createTempFile("'merge'", extension); //生成要合并的新文件
            } catch (IOException e) {
                XueChengException.cast("创建临时合并文件出错");
            }
            //创建合并文件的流对象
            try (RandomAccessFile raf_write = new RandomAccessFile(tempMergeFile, "rw")) {
                byte[] bytes = new byte[1024];
                for (File file : chunkFiles) {
                    //读取分块文件的流程
                    try (RandomAccessFile raf_read = new RandomAccessFile(file, "r")) {
                        int len = -1;
                        while ((len = raf_read.read(bytes)) != -1) {
                            raf_write.write(bytes, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                XueChengException.cast("合并文件过程出错");
            }

            //校验合并后的文件是否正确
            try {
                FileInputStream mergeFileStream = new FileInputStream(tempMergeFile);
                String mergeMd5Hex = "";
                if (mergeFileStream != null) {
                    mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
                }

                if (!fileMd5.equals(mergeMd5Hex)) {
                    log.debug("合并文件校验未通过,文件路径:{},原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
                    XueChengException.cast("合并文件校验未通过,文件不匹配");
                }
            } catch (IOException e) {
                XueChengException.cast("合并文件校验出错");
            }

            //将合并后的文件上传到文件系统
            //拿到合并后的存储路径
            String mergeFilePath = getFilePathByMd5(fileMd5, extension);

            addMediaFilesToMinIO(tempMergeFile.getAbsolutePath(), bucket_video, mergeFilePath);
            //将文件信息入库保存
            uploadFileParamsDto.setFileSize(tempMergeFile.length()); //合并文件大小
            addMediaFilesToMinDb(companyId, uploadFileParamsDto, fileMd5, bucket_video, mergeFilePath);

            return RestResponse.success(true);
        } finally {
            //删除临时分块文件
            if (chunkFiles != null) {
                for (File chunkFile : chunkFiles) {
                    chunkFile.delete();
                }
            }
            //删除合并临时文件
            if (tempMergeFile != null) {
                tempMergeFile.delete();

            }
        }
    }

    @Override
    public MediaFiles getFileById(String id) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(id);
        if(mediaFiles == null){
            XueChengException.cast("文件不存在");
        }
        String url = mediaFiles.getUrl();
        if(StringUtils.isEmpty(url)){
            XueChengException.cast("文件还没有处理，请稍后预览");
        }
        return mediaFiles;
    }


    //获取分块文件路径名称
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    //获取文件路径md5名称
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    //从minio中下载文件
    public File downloadFileFromMinIO(File file, String bucket, String objectName) {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucket).object(objectName).build();

        try (
                InputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream = new FileOutputStream(file);
        ) {
            IoUtils.copy(inputStream, outputStream);
            //将分块文件加入数组
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            XueChengException.cast("查询分块文件出错");
        }
        return null;
    }

}
