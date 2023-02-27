package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.model.dto.SaveTeachplanDto;
import com.xuecheng.model.dto.TeachplanDto;
import com.xuecheng.model.po.Teachplan;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

     @Override
     public List<TeachplanDto> findTeachplayTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
     }


     @Transactional
     @Override
     public void saveTeachplan(SaveTeachplanDto dto) {
          Long id = dto.getId();

          Teachplan teachplan = teachplanMapper.selectById(id);

          if(id == null ){
               teachplan = new Teachplan();
               BeanUtils.copyProperties(teachplan,dto);

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

     public int getTeachplanCount(Long courseId,Long parentId){
          LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
          queryWrapper.eq(Teachplan::getCourseId,courseId);
          queryWrapper.eq(Teachplan::getParentid,parentId);

          Integer count = teachplanMapper.selectCount(queryWrapper);
          return count;
     }
}
