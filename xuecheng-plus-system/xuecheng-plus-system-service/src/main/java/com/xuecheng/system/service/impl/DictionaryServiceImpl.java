package com.xuecheng.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.system.mapper.DictionaryMapper;
import com.xuecheng.system.model.po.Dictionary;
import com.xuecheng.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public List<Dictionary> queryAll() {
        //未使用 redis  0.54 ms


        Long dictionary = redisTemplate.opsForList().size("dictionary");

        if(dictionary!= null && dictionary > 0){

            List<Dictionary> list = redisTemplate.opsForList().range("dictionary", 0, dictionary);

            return list;
        }
        Random random = new Random();
        int min = random.nextInt(20);
        List<Dictionary> list = this.list();
        redisTemplate.opsForList().rightPushAll("dictionary",list);
        redisTemplate.expire("dictionary",min, TimeUnit.MINUTES);

        return list;
    }

    @Override
    public Dictionary getByCode(String code) {


        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getCode, code);

        Dictionary dictionary = this.getOne(queryWrapper);


        return dictionary;
    }
}
