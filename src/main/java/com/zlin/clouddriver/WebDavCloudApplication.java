package com.zlin.clouddriver;

import com.zlin.clouddriver.webdav.WebDavServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 实现webdav协议的云盘
 */
@SpringBootApplication
@EnableScheduling
public class WebDavCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebDavCloudApplication.class, args);

    }

    @Bean
    public ServletRegistrationBean<WebDavServlet> registerWebDavServlet() {
        ServletRegistrationBean<WebDavServlet> registration = new ServletRegistrationBean<>(new WebDavServlet(),
                "/webdav");
        registration.addInitParameter("listings", "true");
        return registration;
    }

//    @Bean
    public FilterRegistrationBean disableErrorFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new ErrorF());
        registration.setEnabled(true);
        return registration;
    }
}
