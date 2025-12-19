package com.gdg.slbackend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 인터셉터 등록
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // registry.addInterceptor(new LoggingInterceptor())
        //         .addPathPatterns("/**")
        //         .excludePathPatterns("/swagger-ui/**");
    }

    /**
     * ArgumentResolver 등록
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // resolvers.add(new CurrentUserArgumentResolver());
    }
}
