package com.paathshala.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${note.dir}")
    private String noteDir;

    @Value("${video.dir}")
    private String videoDir;

    @Value("${modelQuestion.dir}")
    private String modelQuestionDir;

    @Value("${course.dir}")
    private String courseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/note/**")
                .addResourceLocations(Paths.get(noteDir).toUri().toString());

        registry.addResourceHandler("/video/**")
                .addResourceLocations(Paths.get(videoDir).toUri().toString());

        registry.addResourceHandler("/modelQuestion/**")
                .addResourceLocations(Paths.get(modelQuestionDir).toUri().toString());

        registry.addResourceHandler("/course/**")
                .addResourceLocations(Paths.get(courseDir).toUri().toString());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/api/**")
                             .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                             .allowedHeaders("*")
                             .allowCredentials(true);
      }
}
