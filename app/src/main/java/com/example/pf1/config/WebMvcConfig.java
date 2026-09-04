package com.example.pf1.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.media.location}")
    private String mediaLocation;

    @Value("${app.media.url-path}")
    private String mediaUrlPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /media/** へのリクエストをローカルの media/ フォルダから配信する
        Path mediaPath = Paths.get(mediaLocation).toAbsolutePath();
        registry.addResourceHandler(mediaUrlPath + "**")
                .addResourceLocations("file:" + mediaPath + "/");
    }
}