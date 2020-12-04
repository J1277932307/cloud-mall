package xyz.jiang.cloud.mall.cartorder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Program: cloud-mall
 * @Classname MallWebMvcConfig
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-12-03
 **/
@Configuration
public class MallWebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir}")
    private  String FILE_UPLOAD_DIR;

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + FILE_UPLOAD_DIR);
    }
}
