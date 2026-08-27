package kr.co.oneclass.author.common;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class AuthorUploadWebConfig {

    // 파일당 5MB, 상세 2/2 단계의 갤러리 최대 9장 요청을 수용한다
    @Bean
    public MultipartConfigElement authorMultipartConfig() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(5));
        factory.setMaxRequestSize(DataSize.ofMegabytes(46));
        return factory.createMultipartConfig();
    }

}
