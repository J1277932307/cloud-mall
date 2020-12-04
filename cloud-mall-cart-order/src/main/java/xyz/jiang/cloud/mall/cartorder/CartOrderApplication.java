package xyz.jiang.cloud.mall.cartorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Program: cloud-mall
 * @Classname CartOrderApplication
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-11-30
 **/

@EnableEurekaClient
@EnableRedisHttpSession
@SpringBootApplication
@EnableFeignClients
@MapperScan(basePackages = "xyz.jiang.cloud.mall.cartorder.model.dao")
public class CartOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartOrderApplication.class, args);
    }
}
