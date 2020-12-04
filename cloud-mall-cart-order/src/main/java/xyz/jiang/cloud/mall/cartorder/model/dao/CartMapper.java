package xyz.jiang.cloud.mall.cartorder.model.dao;

import org.apache.ibatis.annotations.Param;
import xyz.jiang.cloud.mall.cartorder.model.VO.CartVO;
import xyz.jiang.cloud.mall.cartorder.model.pojo.Cart;


import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<CartVO> selectList(@Param("userId") Integer userId);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    Integer selectOrNot(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("selected") Integer integer);


}