package com.xuecheng.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/25 15:32
 */
@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter getCorsFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //添加可以跨域的http方法 比如：GET POST;,（多个方法中用逗号分割）,*表示所有
        corsConfiguration.addAllowedMethod("*");
        //添加可以跨域的请求  *表示所有，也可也具体指定 http://localhost:5050 表示只有5050端口可以跨域
        corsConfiguration.addAllowedOrigin("*");
        //所有头信息全部放行
        corsConfiguration.addAllowedHeader("*");
        //允许跨域发送cookie
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
