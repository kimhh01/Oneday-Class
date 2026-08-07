package kr.co.oneclass.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 프로젝트 루트 하위 uploads/ 폴더 경로 생성 및 매핑 (기존 유지)
        String uploadPath = System.getProperty("user.dir").replace("\\", "/") + "/uploads/";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + uploadPath);

        // 2. [추가] src/main/resources/static/images/ 폴더 매핑
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}