package com.bjike.goddess.datastore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;

/**
 * 扫描com.bjike.goddess.common.consumer 加入过滤器引入userToken
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.bjike.goddess.datastore.action", "com.bjike.goddess.datastore.config","com.bjike.goddess.common.consumer"})
@PropertySource(value = {"classpath:permission.properties"},encoding="utf-8")
@ImportResource({"classpath:app.xml"})
@EnableAutoConfiguration(exclude = {ValidationAutoConfiguration.class})
public class Application {


    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("800MB");
        factory.setMaxRequestSize("800MB");
        return factory.createMultipartConfig();
    }
    public static void main(String[] args) throws IOException {

        SpringApplication.run(Application.class, args);
        System.in.read();
    }

}
