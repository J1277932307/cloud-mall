package xyz.jiang.cloud.mall.cartorder.service;

import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import xyz.jiang.cloud.mall.cartorder.model.VO.OrderVO;
import xyz.jiang.cloud.mall.cartorder.model.request.CreateOrderReq;

import java.io.IOException;

//订单service
public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancle(String orderNu);

    String qrcode(String orderNo) throws IOException, WriterException;

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void pay(String orderNo);

    void delivered(String orderNo);

    void finish(String orderNo);
}
