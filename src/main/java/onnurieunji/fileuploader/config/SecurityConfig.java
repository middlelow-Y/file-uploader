package onnurieunji.fileuploader.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 스프링 시큐리티 룰을 무시하는 URL 규칙
        web.ignoring()
                .antMatchers("/js/**","/css/**","/files/**","/download/**","html/**");
    }
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        http
////                .csrf().disable();
//    }
}
