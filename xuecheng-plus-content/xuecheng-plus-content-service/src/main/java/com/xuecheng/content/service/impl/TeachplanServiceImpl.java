package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.model.dto.BindTeachplanMediaDto;
import com.xuecheng.model.dto.SaveTeachplanDto;
import com.xuecheng.model.dto.TeachplanDto;
import com.xuecheng.model.po.Teachplan;
import com.xuecheng.model.po.TeachplanMedia;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/26 21:40
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

     @Resource
     TeachplanMapper teachplanMapper;

     @Resource
     TeachplanMediaMapper teachplanMediaMapper;

     @Override
     public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
     }


     @Transactional
     @Override
     public void saveTeachplan(SaveTeachplanDto dto) {
          Long id = dto.getId();

          Teachplan teachplan = teachplanMapper.selectById(id);

          if(id == null ){
               teachplan = new Teachplan();
               BeanUtils.copyProperties(dto,teachplan);

               //找到同级课程计划的数量
               int count = getTeachplanCount(dto.getCourseId(), dto.getParentid());
               //新课程计划的值
               teachplan.setOrderby(count+1);

               teachplanMapper.insert(teachplan);
          }else{
               BeanUtils.copyProperties(dto,teachplan);
               teachplanMapper.updateById(teachplan);
          }

     }

     @Override
     public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachPlanMediaDto) {

          Long teachplanId = bindTeachPlanMediaDto.getTeachplanId();
          Teachplan teachplan = teachplanMapper.selectById(teachplanId);
          //约束校验
          //教学计划不存在,无法绑定
          if(teachplan == null){
               XueChengException.cast("教学计划不存在");
          }
          //只有二级目录才可以绑定视频
          Integer grade = teachplan.getGrade();
          if(grade != 2){
               XueChengException.cast("只有二级目录才可以绑定视频");
          }

          //删除原来的绑定关系
          LambdaQueryWrapper<TeachplanMedia> lambdaQueryWrapper = new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId);
          teachplanMediaMapper.delete(lambdaQueryWrapper);

          //添加新的绑定关系
          TeachplanMedia teachplanMedia = new TeachplanMedia();
          teachplanMedia.setTeachplanId(teachplanId);
          teachplanMedia.setMediaFilename(bindTeachPlanMediaDto.getFileName());
          teachplanMedia.setMediaId(bindTeachPlanMediaDto.getMediaId());
          teachplanMedia.setCreateDate(LocalDateTime.now());
          teachplanMedia.setCourseId(teachplan.getCourseId());
          teachplanMediaMapper.insert(teachplanMedia);
          return teachplanMedia;

     }

     public int getTeachplanCount(Long courseId,Long parentId){
          LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
          queryWrapper.eq(Teachplan::getCourseId,courseId);
          queryWrapper.eq(Teachplan::getParentid,parentId);

          Integer count = teachplanMapper.selectCount(queryWrapper);
          return count;
     }
}
