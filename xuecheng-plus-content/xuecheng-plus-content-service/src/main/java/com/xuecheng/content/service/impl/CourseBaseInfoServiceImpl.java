package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.model.dto.*;
import com.xuecheng.model.po.CourseBase;
import com.xuecheng.model.po.CourseCategory;
import com.xuecheng.model.po.CourseMarket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

     @Resource
     private CourseMarketMapper courseMarketMapper;

     @Resource
     private CourseBaseInfoService courseBaseInfoService;

     @Resource
     private TeachplanService teachplanService;


     @Resource
     private CourseCategoryMapper courseCategoryMapper;

     @Resource
     private CourseMarketServiceImpl courseMarketService;

     @Resource
     private RedisTemplate redisTemplate;


     /**
      * @description TODO
      * @param params 
      * @param queryCourseParamsDto 
      * @return com.xuecheng.base.model.PageResult<com.xuecheng.model.po.CourseBase>
      * @author 咏鹅
      * @date 2023/2/25 18:20
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





     @Transactional
     @Override
     public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

          //合法性校验
          if (StringUtils.isBlank(dto.getName())) throw new XueChengException("课程名称为空");


          if (StringUtils.isBlank(dto.getMt())) throw new XueChengException("课程分类为空");


          if (StringUtils.isBlank(dto.getSt())) throw new XueChengException("课程分类为空");


          if (StringUtils.isBlank(dto.getGrade())) throw new XueChengException("课程等级为空");


          if (StringUtils.isBlank(dto.getTeachmode())) throw new XueChengException("教育模式为空");


          if (StringUtils.isBlank(dto.getUsers())) throw new XueChengException("适应人群为空");


          if (StringUtils.isBlank(dto.getCharge())) throw new XueChengException("收费规则为空");


          String charge = dto.getCharge();
          if(charge.equals("201001")){
               if(dto.getPrice() == null || dto.getPrice() == 0) throw new XueChengException("收费课程没有输入价格");
          }


          CourseBase courseBase = new CourseBase();

          BeanUtils.copyProperties(dto,courseBase);
          //设置机构id
          courseBase.setCompanyId(companyId);
          //创建时间
          courseBase.setCreateDate(LocalDateTime.now());
          //审核状态默认未提交
          courseBase.setAuditStatus("202004");
          //发布状态默认为未发布
          courseBase.setStatus("203001");

          //课程基本信息表插入记录
          int insert = courseBaseMapper.insert(courseBase);
          Long courseId = courseBase.getId();
          CourseMarket courseMarket =new CourseMarket();
          BeanUtils.copyProperties(dto,courseMarket);
          courseMarket.setId(courseId);
          //课程营销表插入记录
          int insert1 = courseMarketMapper.insert(courseMarket);

          if(insert<0 || insert1 <0)  throw new XueChengException("添加课程失败");

          CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

          return courseBaseInfo;
     }



     public CourseBaseInfoDto getCourseBaseInfo(Long courseId){


          Object jsonObj = redisTemplate.opsForValue().get("coursebase:" + courseId);

          if(jsonObj !=null){
               String result = jsonObj.toString();
               CourseBaseInfoDto courseBaseInfoDto = JSON.parseObject(result, CourseBaseInfoDto.class);
               return  courseBaseInfoDto;
          }else {
               synchronized (this){
                    Object jsonObj1 = redisTemplate.opsForValue().get("coursebase:" + courseId);
                    if(jsonObj1!=null){
                         String result = jsonObj1.toString();
                         if(result.equals("null")) return null;
                         CourseBaseInfoDto courseBaseInfoDto = JSON.parseObject(result, CourseBaseInfoDto.class);
                         return  courseBaseInfoDto;
                    }

                    //基本信息
                    CourseBase courseBase = courseBaseMapper.selectById(courseId);
                    //营销信息
                    CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

                    CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
                    BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
                    if(courseBase != null){
                         BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
                    }


                    //根据课程分类id查询分类名称
                    String mt = courseBase.getMt();
                    String st = courseBase.getSt();

                    CourseCategory mtCategory = courseCategoryMapper.selectById(mt);
                    CourseCategory stCategory = courseCategoryMapper.selectById(st);

                    if(mtCategory != null){
                         //分类名称
                         String mtName = mtCategory.getName();
                         courseBaseInfoDto.setMtName(mtName);
                    }
                    if(stCategory != null){
                         //分类名称
                         String stName = stCategory.getName();
                         courseBaseInfoDto.setStName(stName);
                    }

                    int min = new Random().nextInt(20);
                    if(courseBaseInfoDto != null){
                         redisTemplate.opsForValue().set("coursebase:"+courseId,JSON.toJSONString(courseBaseInfoDto),min, TimeUnit.MINUTES);
                    }else{
                         redisTemplate.opsForValue().set("coursebase:"+courseId,null,min, TimeUnit.MINUTES);
                         return null;
                    }
                    return courseBaseInfoDto;
               }
          }
     }

     public CourseMarket getCourseMarketByCourseId(Long courseId){
          if(courseId <=0) XueChengException.cast("机构id错误");
          CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
          return courseMarket;
     }


     @Transactional
     @Override
     public CourseBaseInfoDto updateCourseBase(Long companyId,UpdateCourseDto dto) {
          Long courseId = dto.getId();
          CourseBase courseBaseUpdate = courseBaseMapper.selectById(courseId);

          if(courseBaseUpdate == null) XueChengException.cast("课程不存在");

//          if(!companyId.equals(courseBaseUpdate.getCompanyId())) XueChengException.cast("只允许修改本机构的课程");

          BeanUtils.copyProperties(dto,courseBaseUpdate);
          courseBaseUpdate.setChangeDate(LocalDateTime.now());

          //更新课程基本信息
          courseBaseMapper.updateById(courseBaseUpdate);

          //查询营销信息
          CourseMarket courseMarket = new CourseMarket();
          BeanUtils.copyProperties(dto,courseMarket);


          String charge = courseMarket.getCharge();
          if(!charge.equals("201001")){
//               if(dto.getPrice() == null || dto.getPrice() == 0) throw new XueChengException("收费课程没有输入价格");
          }


          //保存课程营销信息，没有则添加，有则更新
          boolean save = courseMarketService.saveOrUpdate(courseMarket);

          if(!save) XueChengException.cast("修改失败");

          //返回课程信息
          return getCourseBaseInfo(courseId);
     }

     @Override
     public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
          //基本信息、营销信息
          CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

          //教学计划
          List<TeachplanDto> teachplayTree = teachplanService.findTeachplanTree(courseId);

          CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
          coursePreviewDto.setCourseBase(courseBaseInfo);
          coursePreviewDto.setTeachplans(teachplayTree);

          return coursePreviewDto;
     }


}
