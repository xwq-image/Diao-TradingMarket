package com.campus.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.market.dto.OrderCreateDTO;
import com.campus.market.entity.Order;

public interface OrderService extends IService<Order> {
    void createOrder(Long userId, OrderCreateDTO orderCreateDTO);
    Page<Order> getMyBuyOrders(Long userId, Integer page, Integer size);
    Page<Order> getMySellOrders(Long userId, Integer page, Integer size);
    
    // 新的订单流程方法
    void confirmOrder(Long userId, Long orderId); // 卖家确认订单
    void payOrder(Long userId, Long orderId, String password); // 买家付款
    void shipOrder(Long userId, Long orderId); // 卖家发货
    void completeOrder(Long userId, Long orderId); // 买家确认收货
    void cancelOrder(Long userId, Long orderId); // 取消订单
}
