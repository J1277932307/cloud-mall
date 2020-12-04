package xyz.jiang.cloud.mall.categoryproduct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Program: cloud-mall
 * @Classname CategoryProductAplication
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-11-29
 **/
@EnableEurekaClient
@SpringBootApplication
@EnableRedisHttpSession
@EnableFeignClients
@MapperScan(basePackages = "xyz.jiang.cloud.mall.categoryproduct.model.dao")
public class CategoryProductAplication {
    public static void main(String[] args) {
        SpringApplication.run(CategoryProductAplication.class, args);
    }
}
