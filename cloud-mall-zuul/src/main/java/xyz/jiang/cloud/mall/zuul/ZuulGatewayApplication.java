package xyz.jiang.cloud.mall.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Program: cloud-mall
 * @Classname ZuulGatewayApplication
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-11-22
 **/
@EnableEurekaClient
@EnableFeignClients
@EnableZuulProxy
@EnableRedisHttpSession
@SpringBootApplication
public class ZuulGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}
