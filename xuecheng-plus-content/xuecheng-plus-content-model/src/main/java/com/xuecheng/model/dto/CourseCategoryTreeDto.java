package com.xuecheng.model.dto;

import com.xuecheng.model.po.CourseCategory;
import lombok.Data;

import java.util.List;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/25 16:34
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory {
    List childrenTreeNodes;
}
