package com.xuecheng.model.dto;

import lombok.Data;

/***
 * @description TODO
 * @param null 
 * @return 
 * @author 咏鹅
 * @date 2023/5/8 20:26
*/
@Data
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
