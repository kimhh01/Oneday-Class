package kr.co.oneclass.author.common;

import java.nio.file.Paths;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class AuthorUploadWebConfig implements WebMvcConfigurer {

    private final String uploadLocation;

    public AuthorUploadWebConfig(
            @Value("${oneday.author.upload-root:uploads/author}") String uploadRootPath) {
        String location = Paths.get(uploadRootPath)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();
        this.uploadLocation = location.endsWith("/") ? location : location + "/";
    }

    // 파일당 5MB, 상세 2/2 단계의 갤러리 최대 9장 요청을 수용한다
    @Bean
    public MultipartConfigElement authorMultipartConfig() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(5));
        factory.setMaxRequestSize(DataSize.ofMegabytes(46));
        return factory.createMultipartConfig();
    }

    // LocalFileStorageService 가 저장한 외부 파일을 웹 경로로 제공한다
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(LocalFileStorageService.WEB_PREFIX + "**")
                .addResourceLocations(uploadLocation);
    }
}
