package com.xuecheng.content.service;


import com.xuecheng.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author 咏鹅
 * @description 针对表【course_category(课程分类)】的数据库操作Service
 * @createDate 2023-02-25 16:25:56
 */
public interface CourseCategoryService {

    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
