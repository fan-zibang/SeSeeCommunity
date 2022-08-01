package com.fanzibang.community.config;

import com.fanzibang.community.component.*;
import com.fanzibang.community.pojo.Resource;
import com.fanzibang.community.service.DynamicAuthenticationService;
import com.fanzibang.community.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @Autowired
    private ResourceService resourceService;

    @Autowired(required = false)
    private DynamicAuthenticationService dynamicAuthenticationService;

    @Autowired
    private DynamicAuthenticationFilter dynamicAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        // 对白名单的 url 直接放行
        for (String url : ignoreUrlsConfig.getUrls()) {
            registry.antMatchers(url).permitAll();
        }
        // 允许跨域的请求
        registry.antMatchers(HttpMethod.OPTIONS).permitAll();
        // 其他任何的请求都需要认证
        registry.and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                // 关闭跨站请求保护
                .csrf().disable()
                // 不使用session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 添加自定义未授权和未登录结果返回
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                // 自定义权限拦截器jwt filter
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // Security 底层会默认拦截 /logout 请求，进行退出处理
                // 此处赋予它一个根本不存在的退出路径，使得程序能够执行到我们自己编写的退出代码
                .logout().logoutUrl("/securitylogout")
                // 允许跨域
                .and().cors();
        // 添加动态校验过滤器
        registry.and().addFilterBefore(dynamicAuthenticationFilter, FilterSecurityInterceptor.class);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DynamicAccessDecisionManager dynamicAccessDecisionManager() {
        return new DynamicAccessDecisionManager();
    }

    @Bean
    public DynamicAuthenticationMetadataSource dynamicAuthenticationMetadataSource() {
        return new DynamicAuthenticationMetadataSource();
    }

    @Bean
    public DynamicAuthenticationService dynamicAuthenticationService() {
        return new DynamicAuthenticationService() {
            public Map<String, ConfigAttribute> loadDataSource() {
                Map<String, ConfigAttribute> map = new ConcurrentHashMap<>();
                List<Resource> resourceList = resourceService.getResourceList();
                for (Resource resource : resourceList) {
                    map.put(resource.getUrl(), new org.springframework.security.access.SecurityConfig(resource.getId() + ":" + resource.getName()));
                }
                return map;
            }
        };
    }

}
