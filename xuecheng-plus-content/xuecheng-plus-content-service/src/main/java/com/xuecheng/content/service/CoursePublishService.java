package com.xuecheng.content.service;

import com.xuecheng.model.dto.CoursePreviewDto;
import com.xuecheng.model.po.CoursePublish;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;

/**
* @author 咏鹅
* @description 针对表【course_publish(课程发布)】的数据库操作Service
* @createDate 2023-03-09 00:05:31
*/
public interface CoursePublishService{


    /***
     * @description 获取课程预览信息
     * @param courseId  课程id
     * @return com.xuecheng.model.dto.CoursePreviewDto
     * @author 咏鹅
     * @date 2023/5/8 20:20
    */
     CoursePreviewDto getCoursePreviewInfo(Long courseId);


    /**
     * @description 提交审核
     * @param courseId  课程id
     * @return void
     * @author 咏鹅
     * @date 2023/5/8 20:20
     */
     void commitAudit(Long companyId,Long courseId);
    /**
     * @description 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     * @author 咏鹅
     * @date 2023/5/8 20:20
     */
     void publish(Long companyId,Long courseId);

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     * @author 咏鹅
     * @date 2023/5/8 20:20
     */
     File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     * @author 咏鹅
     * @date 2023/5/8 20:20
     */
     void  uploadCourseHtml(Long courseId, File file);

    //创建索引
     Boolean saveCourseIndex(Long courseId) ;

     /***
      * @description 查询课程发布信息
      * @param courseId
      * @return com.xuecheng.model.po.CoursePublish
      * @author 咏鹅
      * @date 2023/5/8 11:00
     */
    CoursePublish getCoursePublish(Long courseId);
}
