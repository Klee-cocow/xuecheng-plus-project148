package com.xuecheng.content.service;

import com.xuecheng.model.dto.CoursePreviewDto;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/6 23:04
 */
public interface CoursePublishService {
    /**
     * @description 获取课程预览信息
     * @param courseId
     * @return com.xuecheng.model.dto.CoursePreviewDto
     * @author 咏鹅
     * @date 2023/3/6 23:05
    */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);
}
