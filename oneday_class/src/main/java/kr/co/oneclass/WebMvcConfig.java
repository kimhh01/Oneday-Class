package kr.co.oneclass;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // DB의 /upload/... 요청을 실제 서버/로컬 디렉토리 경로로 연결
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///C:/upload/", "classpath:/static/upload/"); 
                // actual local path where your image files exist
    }
}