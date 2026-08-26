package kr.co.oneclass.author.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class AuthorAccessWebConfig implements WebMvcConfigurer {

    private final AuthorAccessInterceptor authorAccessInterceptor;

    public AuthorAccessWebConfig(AuthorAccessInterceptor authorAccessInterceptor) {
        this.authorAccessInterceptor = authorAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorAccessInterceptor)
                .addPathPatterns("/author/**")
                .excludePathPatterns(
                        "/author/access",
						"/author/access/profile",
                        "/author/start",
                        "/author/css/**",
                        "/author/js/**",
                        "/author/images/**");
    }
}
