package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.service.CourseCategoryService;
import com.xuecheng.model.dto.CourseCategoryTreeDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 咏鹅
 * @description 针对表【course_category(课程分类)】的数据库操作Service实现
 * @createDate 2023-02-25 16:25:56
 */
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Resource
    private CourseCategoryMapper courseCategoryMapper;
    
    //TODO 可与用树结构去定义数据
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //查出节点下的子节点
        List<CourseCategoryTreeDto> categoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = new ArrayList<>();
        
        //方便找字节点的父节点定义一个map
        HashMap<String,CourseCategoryTreeDto> nodeMap = new HashMap<>();

        categoryTreeDtos.stream().forEach(item->{
            nodeMap.put(item.getId(),item);
            if(item.getParentid().equals(id)){
                courseCategoryTreeDtos.add(item);
            }
            
            //找到当前数据的父节点
            String parentid = item.getParentid();
            //找到该节点的父节点对象
            CourseCategoryTreeDto parentNode = nodeMap.get(parentid);
            if(parentNode != null){
                List childrenTreenodes = parentNode.getChildrenTreeNodes();
                if(childrenTreenodes ==null){
                    parentNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //找到子节点，放到它的父节点的childrenTreeNodes 属性中
                parentNode.getChildrenTreeNodes().add(item);
            }
        });

        return courseCategoryTreeDtos;
    }
}




