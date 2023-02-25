package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.model.dto.QueryCourseParamsDto;
import com.xuecheng.model.po.CourseBase;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/24 22:33
 */
public interface CourseBaseInfoService {

   PageResult<CourseBase> queryCourseBaseList(PageParams pageParams,@RequestBody QueryCourseParamsDto queryCourseParamsDto);
}
