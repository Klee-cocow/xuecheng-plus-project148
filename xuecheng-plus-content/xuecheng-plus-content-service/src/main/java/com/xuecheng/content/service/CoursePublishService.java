package com.xuecheng.content.service;

import com.xuecheng.model.dto.CoursePreviewDto;

import java.io.File;

/**
* @author 咏鹅
* @description 针对表【course_publish(课程发布)】的数据库操作Service
* @createDate 2023-03-09 00:05:31
*/
public interface CoursePublishService{
    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     * @author Mr.M
     * @date 2022/9/16 15:36
     */
     CoursePreviewDto getCoursePreviewInfo(Long courseId);
    /**
     * @description 提交审核
     * @param courseId  课程id
     * @return void
     * @author Mr.M
     * @date 2022/9/18 10:31
     */
     void commitAudit(Long companyId,Long courseId);
    /**
     * @description 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     * @author Mr.M
     * @date 2022/9/20 16:23
     */
     void publish(Long companyId,Long courseId);

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
     File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
     void  uploadCourseHtml(Long courseId, File file);

    //创建索引
     Boolean saveCourseIndex(Long courseId) ;
}
