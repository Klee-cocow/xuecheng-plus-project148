package com.xuecheng.content.feignclient;

import com.xuecheng.model.po.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/10 21:25
 */
@FeignClient(value = "search",fallbackFactory = SearchServiceClientFallbackFactory.class)
public interface SearchServiceClient {
 @PostMapping("/search/index/course")
 Boolean add(@RequestBody CourseIndex courseIndex);
}

