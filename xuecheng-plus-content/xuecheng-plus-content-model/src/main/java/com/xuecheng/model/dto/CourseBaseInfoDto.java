package com.xuecheng.model.dto;

import com.xuecheng.model.po.CourseBase;
import lombok.Data;

import java.math.BigDecimal;

/***
 * @description TODO
 * @param null 
 * @return
 * @author 咏鹅
 * @date 2023/5/8 20:22
*/
@Data
public class CourseBaseInfoDto extends CourseBase {


 /**
  * 收费规则，对应数据字典
  */
 private String charge;

 /**
  * 价格
  */
 private Float price;


 /**
  * 原价
  */
 private Float originalPrice;

 /**
  * 咨询qq
  */
 private String qq;

 /**
  * 微信
  */
 private String wechat;

 /**
  * 电话
  */
 private String phone;

 /**
  * 有效期天数
  */
 private Integer validDays;

 /**
  * 大分类名称
  */
 private String mtName;

 /**
  * 小分类名称
  */
 private String stName;

}
