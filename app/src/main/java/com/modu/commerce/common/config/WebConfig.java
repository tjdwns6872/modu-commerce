package com.modu.commerce.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.modu.commerce.common.web.TraceIdFilter;

@Configuration
public class WebConfig {
    
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> bean = new FilterRegistrationBean<>(new TraceIdFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
