package xyz.jiang.cloud.mall.cartorder.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.jiang.cloud.mall.categoryproduct.model.pojo.Product;

/**
 * @Program: cloud-mall
 * @Classname ProductFeignClient
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-11-30
 **/
@FeignClient(value = "cloud-mall-category-product")
public interface ProductFeignClient {
    @GetMapping("/product/detailForFeign")
    Product detailForFeign(@RequestParam Integer id);

    @PostMapping("/product/updateStock")
    void updateStock(@RequestParam Integer productId, @RequestParam Integer stock);
}
