package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.model.dto.AddCourseDto;
import com.xuecheng.model.dto.CourseBaseInfoDto;
import com.xuecheng.model.dto.QueryCourseParamsDto;
import com.xuecheng.model.dto.UpdateCourseDto;
import com.xuecheng.model.po.CourseBase;
import com.xuecheng.model.po.CourseMarket;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/24 22:33
 */
public interface CourseBaseInfoService {

   /**
    * @description 课程查询
    * @param pageParams
    * @param queryCourseParamsDto
    * @return com.xuecheng.base.model.PageResult<com.xuecheng.model.po.CourseBase>
    * @author 咏鹅
    * @date 2023/2/26 16:01
   */
   PageResult<CourseBase> queryCourseBaseList(PageParams pageParams,@RequestBody QueryCourseParamsDto queryCourseParamsDto);

   /**
    * @description //创建课程信息
    * @param companyId  机构id
    * @param addCourseDto 新增课程信息
    * @return 基本信息，营销信息
    * @author 咏鹅
    * @date 2023/2/25 17:49
   */
   CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto addCourseDto);

   /**
    * @description 查询课程信息
    * @param courseId
    * @return com.xuecheng.model.dto.CourseBaseInfoDto
    * @author 咏鹅
    * @date 2023/2/26 16:00
   */
   CourseBaseInfoDto getCourseBaseInfo(Long courseId);

   /**
    * @description 查询营销信息
    * @param courseId
    * @return com.xuecheng.model.po.CourseMarket
    * @author 咏鹅
    * @date 2023/2/26 16:34
   */
   CourseMarket getCourseMarketByCourseId(Long courseId);

   CourseBaseInfoDto updateCourseBase(Long companyId,UpdateCourseDto dto);

}
