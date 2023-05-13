package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/8 15:08
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {


    @Resource
    private CoursePublishService coursePublishService;

    @Resource
    private CoursePublishMapper coursePublishMapper;




    ////课程发布任务执行入口，由xxl-job调度
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception{
        // 分片序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片数量
        int shardTotal = XxlJobHelper.getShardTotal();
        process(shardIndex,shardTotal,"course_publish",30,60);
    }

    @Override
    public boolean execute(MqMessage mqMessage) {

        //从mqMessage 拿到课程id
        Long courseId = Long.parseLong(mqMessage.getBusinessKey1());
        
        //课程静态化上传到minio
        generateCourseHtml(mqMessage,courseId);
        
        //向elasticsearch写索引数据
        saveCourseIndex(mqMessage,courseId);



        //返回true表示任务完成
        return true;
    }

    private void saveCourseIndex(MqMessage mqMessage, Long courseId) {
        //任务id
        Long id = mqMessage.getId();
        //作消息幂等性处理
        //如果该阶段任务完成了不再处理直接返回
        int stageTwo = this.getMqMessageService().getStageTwo(id);//第二阶段的状态
        if(stageTwo>0){
            log.debug("当前阶段是创建课程索引,已经完成不再处理,任务信息:{}",mqMessage);
            return ;
        }

        //调用service创建索引
        coursePublishService.saveCourseIndex(courseId);


        //给该阶段任务打上完成标记
        this.getMqMessageService().completedStageTwo(id);//完成第二阶段的任务
    }

    private void generateCourseHtml(MqMessage mqMessage, Long courseId) {
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        //做任务幂等性处理
        //查询数据库取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if(stageOne>0){
            log.debug("课程静态化任务完成，无需处理");
            return;
        }
        //开始进行课程静态化生成html文件
        File file = coursePublishService.generateCourseHtml(courseId);
        if(file == null){
            XueChengException.cast("生成的静态页面为空");
        }
        //将html上传到minio
        coursePublishService.uploadCourseHtml(courseId,file);

        //任务处理完成写任务状态为完成
        mqMessageService.completedStageOne(taskId);
    }


}
