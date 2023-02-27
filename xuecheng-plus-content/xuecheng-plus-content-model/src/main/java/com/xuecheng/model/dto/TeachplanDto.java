package com.xuecheng.model.dto;

import com.xuecheng.model.po.Teachplan;
import com.xuecheng.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/26 21:02
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {

      TeachplanMedia teachplanMedia;
      List<TeachplanDto> teachPlanTreeNodes;
}
