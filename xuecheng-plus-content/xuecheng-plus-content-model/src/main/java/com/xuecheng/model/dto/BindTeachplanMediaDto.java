package com.xuecheng.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/6 19:51
 */
@Data
@ApiModel(value="BindTeachplanMediaDto")
public class BindTeachplanMediaDto {

 @ApiModelProperty(value = "媒资文件id",required = true)
 private String mediaId;

 @ApiModelProperty(value = "媒资文件名称",required = true)
 private String fileName;

 @ApiModelProperty(value = "课程计划标识",required = true)
 private Long teachplanId;
}
