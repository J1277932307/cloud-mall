package xyz.jiang.cloud.mall.cartorder.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.jiang.cloud.mall.user.model.pojo.User;

@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {
    @GetMapping("/getUser")
    User getUser();
}
