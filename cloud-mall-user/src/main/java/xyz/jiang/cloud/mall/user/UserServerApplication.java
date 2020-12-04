package xyz.jiang.cloud.mall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Program: cloud-mall
 * @Classname UserServerApplication
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-11-22
 **/
@EnableRedisHttpSession
@EnableSwagger2
@EnableEurekaClient
//@EnableFeignClients
@SpringBootApplication
@MapperScan(basePackages = "xyz.jiang.cloud.mall.user.model.dao")
public class UserServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServerApplication.class, args);
    }
}
