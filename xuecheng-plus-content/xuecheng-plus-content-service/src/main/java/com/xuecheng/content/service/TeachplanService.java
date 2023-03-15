package com.xuecheng.content.service;

import com.pbteach.pbmain.api.content.model.vo.BindTeachplanMediaVO;
import com.xuecheng.model.dto.BindTeachplanMediaDto;
import com.xuecheng.model.dto.SaveTeachplanDto;
import com.xuecheng.model.dto.TeachplanDto;
import com.xuecheng.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/26 21:37
 */
public interface TeachplanService {

    /**
     * @description 查询课程树形结构
     * @param courseId
     * @return java.util.List<com.xuecheng.model.dto.TeachplanDto>
     * @author 咏鹅
     * @date 2023/2/26 21:42
    */
    List<TeachplanDto> findTeachplanTree(long courseId);

    void saveTeachplan(SaveTeachplanDto dto);

    TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachPlanMediaDto);


}
