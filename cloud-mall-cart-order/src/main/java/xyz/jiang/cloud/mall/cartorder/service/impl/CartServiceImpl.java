package xyz.jiang.cloud.mall.cartorder.service.impl;

import org.springframework.stereotype.Service;
import xyz.jiang.cloud.mall.cartorder.feign.ProductFeignClient;
import xyz.jiang.cloud.mall.cartorder.model.VO.CartVO;
import xyz.jiang.cloud.mall.cartorder.model.dao.CartMapper;
import xyz.jiang.cloud.mall.cartorder.model.pojo.Cart;
import xyz.jiang.cloud.mall.cartorder.service.CartService;
import xyz.jiang.cloud.mall.categoryproduct.model.pojo.Product;
import xyz.jiang.cloud.mall.common.common.Constant;
import xyz.jiang.cloud.mall.common.exception.MallException;
import xyz.jiang.cloud.mall.common.exception.MallExceptionEnum;


import javax.annotation.Resource;
import java.util.List;

/**
 * @Program: mall
 * @Classname CartServiceImpl
 * @Description: 购物车Service实现类
 * @Author: JiangKan
 * @Create: 2020-10-13
 **/

@Service
public class CartServiceImpl implements CartService {

    @Resource
    ProductFeignClient productFeignClient;

    @Resource
    CartMapper cartMapper;

    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOS = cartMapper.selectList(userId);
        for (CartVO cartVO : cartVOS) {
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }

        return cartVOS;
    }
    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里，需要新增一个记录
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        } else {
            //这个商品在购物车中已经有了，则数量相加
            count = cart.getQuantity() + count;
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }

        return this.list(userId);
    }

    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //更新时如果购物车为空，则报错
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);

        } else {
            //这个商品在购物车中已经有了，则数量更新
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }

        return this.list(userId);
    }

    @Override
    public List<CartVO> delete(Integer userId, Integer productId) {

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //更新时如果购物车为空，则报错
            throw new MallException(MallExceptionEnum.DELETE_FAILED);

        } else {
            //这个商品在购物车中已经有了，则可以删除
            cartMapper.deleteByPrimaryKey(cart.getId());
        }

        return this.list(userId);
    }
    //验证
    private void validProduct(Integer productId, Integer count){
        Product product = productFeignClient.detailForFeign(productId);
        //判断商品是否存在，商品是否上架
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new MallException(MallExceptionEnum.NOT_SALE);
        }
        //判断商品库存
        if (count > product.getStock()) {
            throw new MallException(MallExceptionEnum.NOT_ENOUGH);
        }
    }

    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //更新时如果购物车为空，则报错
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);

        } else {
            //这个商品在购物车中已经有了，则可以选中/不选中
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }


    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected) {
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }


}
