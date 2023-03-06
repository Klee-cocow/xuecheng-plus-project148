package com.xuecheng.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/6 22:59
 */
@Data
public class CoursePreviewDto {

    //课程基本信息，课程营销信息
    CourseBaseInfoDto courseBase;

    //课程计划信息
    List<TeachplanDto> teachplans;

    //师资信息
}
