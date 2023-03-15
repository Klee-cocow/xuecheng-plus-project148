package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/3/8 21:09
 */
@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
       @Override
       public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient() {
          @Override
          public String upload(MultipartFile filedata,String folder, String objectName) throws IOException{

           log.debug("远程调用上传文件的接口发生熔断:{}",throwable.toString(),throwable);
           return null;
          }
        };
       }
}
