package xyz.jiang.cloud.mall.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Program: cloud-mall
 * @Classname EurekaServerApplication
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-11-22
 **/

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
