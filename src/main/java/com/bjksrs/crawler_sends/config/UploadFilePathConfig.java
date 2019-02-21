package com.bjksrs.crawler_sends.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class UploadFilePathConfig extends WebMvcConfigurerAdapter {


    @Value("${file.uploadFolder}")
    private String uploadFolder;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String staticAccessPath = "/api/file/**";
        if(!uploadFolder.endsWith("/")){
            uploadFolder += "/";
        }
        registry.addResourceHandler(staticAccessPath).addResourceLocations("file:" + uploadFolder);
    }
}

