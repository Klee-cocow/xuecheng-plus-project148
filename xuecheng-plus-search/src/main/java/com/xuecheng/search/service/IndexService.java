package com.xuecheng.search.service;


/***
 * @description 课程索引service
 * @param null
 * @return
 * @author 咏鹅
 * @date 2023/5/8 20:26
*/
public interface IndexService {

    /**
     * @param indexName 索引名称
     * @param id 主键
     * @param object 索引对象
     * @return Boolean true表示成功,false失败
     * @description 添加索引
     * @author 咏鹅
     * @date 2023/5/8 20:26
     */


    public Boolean addCourseIndex(String indexName,String id,Object object);


    /**
     * @description 更新索引
     * @param indexName 索引名称
     * @param id 主键
     * @param object 索引对象
     * @return Boolean true表示成功,false失败
     * @author 咏鹅
     * @date 2023/5/8 20:26
     */
    public Boolean updateCourseIndex(String indexName,String id,Object object);

    /**
     * @description 删除索引
     * @param indexName 索引名称
     * @param id  主键
     * @return java.lang.Boolean
     * @author 咏鹅
     * @date 2023/5/8 20:26
     */
    public Boolean deleteCourseIndex(String indexName,String id);

}
