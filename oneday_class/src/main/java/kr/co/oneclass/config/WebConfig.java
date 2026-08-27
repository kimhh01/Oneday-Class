package kr.co.oneclass.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 실제 업로드 파일 저장 경로와 동일한 file.upload-dir 사용
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        registry.addResourceHandler("/upload/**")   // ← 컨트롤러가 쓰는 경로(단수)로 통일
                .addResourceLocations("file:" + uploadDir);

        // 1. 신규 규격 단수형 (/upload/inquiry/...)
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadDir);

        // 2. 기존 DB 데이터 호환용 복수형 (/uploads/inquiry/...)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir);

        // 3. static/images 매핑
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}