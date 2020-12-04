package xyz.jiang.cloud.mall.cartorder.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.jiang.cloud.mall.cartorder.feign.UserFeignClient;
import xyz.jiang.cloud.mall.cartorder.model.VO.CartVO;
import xyz.jiang.cloud.mall.cartorder.service.CartService;
import xyz.jiang.cloud.mall.common.common.ApiRestResponse;


import javax.annotation.Resource;
import java.util.List;

/**
 * @Program: mall
 * @Classname CartController
 * @Description: 购物车Controller
 * @Author: JiangKan
 * @Create: 2020-10-12
 **/
@RestController
@RequestMapping("/cart")
public class CartController {

    @Resource
    CartService cartService;

    @Resource
    UserFeignClient userFeignClient;

    @ApiOperation("购物车列表")
    @GetMapping("/list")
    public ApiRestResponse list() {
        //内部获取用户ID，防止横向越权
        List<CartVO> cartVOS = cartService.list(userFeignClient.getUser().getId());
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVO> cartVOS = cartService.add(userFeignClient.getUser().getId(), productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("更新购物车")
    @PostMapping("/update")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVO> cartVOS = cartService.update(userFeignClient.getUser().getId(), productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("更新购物车")
    @PostMapping("/delete")
    public ApiRestResponse delete(@RequestParam Integer productId) {
        List<CartVO> cartVOS = cartService.delete(userFeignClient.getUser().getId(), productId);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("选中购物车或不选中某商品")
    @PostMapping("/select")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected) {
        List<CartVO> cartVOS = cartService.selectOrNot(userFeignClient.getUser().getId(), productId,selected);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("全选中购物车中的商品或全不选")
    @PostMapping("/select`All")
    public ApiRestResponse selectAll(@RequestParam Integer selected) {
        List<CartVO> cartVOS = cartService.selectAllOrNot(userFeignClient.getUser().getId(),selected);
        return ApiRestResponse.success(cartVOS);
    }
}
