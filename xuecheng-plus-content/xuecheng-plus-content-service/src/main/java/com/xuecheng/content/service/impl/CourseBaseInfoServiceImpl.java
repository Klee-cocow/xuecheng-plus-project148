package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.model.dto.QueryCourseParamsDto;
import com.xuecheng.model.po.CourseBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/24 22:35
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {


     @Resource
     private CourseBaseMapper courseBaseMapper;


     /**
      * @description TODO
      * @param pageParams
      * @param queryCourseParamsDto
      * @return com.xuecheng.base.model.PageResult<com.xuecheng.model.po.CourseBase>
      * @author 咏鹅
      * @date 2023/2/24 22:36
     */
     @Override
     public PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamsDto queryCourseParamsDto) {
          LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
          //根据课程名模糊查询
          queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
          //根据审核状态
          queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
          //根据课程发布状态
          queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());
          //分页查询
          Page<CourseBase> page = new Page<>(params.getPageNo(), params.getPageSize());
          Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
          List<CourseBase> items = pageResult.getRecords();
          long total = pageResult.getTotal();
          //准备返回数据
          PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, params.getPageNo(), params.getPageSize());

          return courseBasePageResult;
     }
}
