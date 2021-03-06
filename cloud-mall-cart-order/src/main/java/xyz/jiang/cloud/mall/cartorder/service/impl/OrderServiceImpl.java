package xyz.jiang.cloud.mall.cartorder.service.impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.jiang.cloud.mall.cartorder.feign.ProductFeignClient;
import xyz.jiang.cloud.mall.cartorder.feign.UserFeignClient;
import xyz.jiang.cloud.mall.cartorder.model.VO.CartVO;
import xyz.jiang.cloud.mall.cartorder.model.VO.OrderItemVO;
import xyz.jiang.cloud.mall.cartorder.model.VO.OrderVO;
import xyz.jiang.cloud.mall.cartorder.model.dao.CartMapper;
import xyz.jiang.cloud.mall.cartorder.model.dao.OrderItemMapper;
import xyz.jiang.cloud.mall.cartorder.model.dao.OrderMapper;
import xyz.jiang.cloud.mall.cartorder.model.pojo.Order;
import xyz.jiang.cloud.mall.cartorder.model.pojo.OrderItem;
import xyz.jiang.cloud.mall.cartorder.model.request.CreateOrderReq;
import xyz.jiang.cloud.mall.cartorder.service.CartService;
import xyz.jiang.cloud.mall.cartorder.service.OrderService;
import xyz.jiang.cloud.mall.cartorder.utils.OrderCodeFactory;
import xyz.jiang.cloud.mall.categoryproduct.common.ProductConstant;
import xyz.jiang.cloud.mall.categoryproduct.model.pojo.Product;
import xyz.jiang.cloud.mall.common.common.Constant;
import xyz.jiang.cloud.mall.common.exception.MallException;
import xyz.jiang.cloud.mall.common.exception.MallExceptionEnum;
import xyz.jiang.cloud.mall.common.utils.QRCodeGenerator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Program: mall
 * @Classname OrderServiceImpl
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-10-15
 **/

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private CartService cartService;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private UserFeignClient userFeignClient;

    @Value("${file.upload.ip}")
    String ip;

    @Value("${file.upload.port}")
    String port;

    @Value("${file.upload.dir}")
    String FILE_UPLOAD_DIR;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq) {

        //拿到用户ID
        Integer userId = userFeignClient.getUser().getId();
        //从购物车查找已经勾选的商品
        List<CartVO> cartVOList = cartService.list(userId);
        ArrayList<CartVO> cartVOListTemp = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;
        //如果购物车勾选为空，报错
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new MallException(MallExceptionEnum.CART_EMPTY);
        }
        //判断商品是否存在、上下架状态、库存
        validSaleStatusAndStock(cartVOList);
        //把购物车对象转化为订单item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        //扣库存
        for (OrderItem orderItem : orderItemList) {
            Product product = productFeignClient.detailForFeign(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new MallException(MallExceptionEnum.NOT_ENOUGH);
            }

            product.setStock(stock);
            productFeignClient.updateStock(product.getId(),product.getStock());
        }

        //把购物车中的已勾选的商品删除
        cleanCart(cartVOList);
        //生成订单
        Order order = new Order();
        //生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        //插入到order表中
        orderMapper.insertSelective(order);
        //循环保存每个商品到order_item表
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        //把结果返回
        return orderNo;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();

        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {

        for (CartVO cartVO : cartVOList) {
            Product product = productFeignClient.detailForFeign(cartVO.getProductId());
            //判断商品是否存在，商品是否上架
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new MallException(MallExceptionEnum.NOT_SALE);
            }
            //判断商品库存
            if (cartVO.getQuantity() > product.getStock()) {
                throw new MallException(MallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    @Override
    public OrderVO detail(String orderNo){
       Order order =  orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }

        //订单存在，判断订单所属用户
        Integer userId = userFeignClient.getUser().getId();
        if (!order.getUserId().equals(userId)) {
            throw new MallException(MallExceptionEnum.NO_YOUR_ORDER);
        }

        OrderVO orderVO = getOrderVO(order);

        return orderVO;

    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }

        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize){
        Integer userId = userFeignClient.getUser().getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);

        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;

    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }

        return orderVOList;
    }

    @Override
    public void cancle(String orderNu) {
        Order order = orderMapper.selectByOrderNo(orderNu);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public String qrcode(String orderNo) throws IOException, WriterException {
        String address = ip +":"+ port;
        String payUrl = "http://" + address + "/cart-order/pay?orderNo=" + orderNo;
        QRCodeGenerator.generateQRCodeImage(payUrl,350,350, FILE_UPLOAD_DIR+orderNo+".png");

        String pngAddress = "http://" + address + "/cart-order/images/" + orderNo + ".png";
        return pngAddress;
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;

    }

    @Override
    public void pay(String orderNo) {
        Order order =  orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.NOT_PAID.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }

    }

    @Override
    public void delivered(String orderNo) {
        Order order =  orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.PAID.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }

    }



    @Override
    public void finish(String orderNo) {
        Order order =  orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        //如果是普通用户，就要校验订单的所属
        if (userFeignClient.getUser().getRole().equals(1) && !order.getUserId().equals(userFeignClient.getUser().getId())) {
            throw new MallException(MallExceptionEnum.NO_YOUR_ORDER);

        }

        //发货后可以完结订单
        if (order.getOrderStatus() == Constant.OrderStatusEnum.DELIVERED.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }

    }




}
