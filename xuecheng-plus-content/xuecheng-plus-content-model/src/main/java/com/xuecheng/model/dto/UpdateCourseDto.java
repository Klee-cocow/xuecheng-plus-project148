package com.xuecheng.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/26 15:52
 */
@Data
@ApiModel(value = "UpdateCourseDto", description = "修改课程基本信息")
public class UpdateCourseDto extends AddCourseDto {

    @ApiModelProperty(value = "课程名称",required = true)
    private Long id;

}
