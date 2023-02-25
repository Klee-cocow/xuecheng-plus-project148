package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.model.dto.CourseCategoryTreeDto;
import com.xuecheng.model.dto.QueryCourseParamsDto;
import com.xuecheng.model.po.CourseBase;
import com.xuecheng.model.po.CourseCategory;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 咏鹅
 * @description 针对表【course_category(课程分类)】的数据库操作Mapper
 * @createDate 2023-02-25 16:25:56
 * @Entity generator.domain.CourseCategory
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    List<CourseCategoryTreeDto> selectTreeNodes(String id);
}




