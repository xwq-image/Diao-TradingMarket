package com.campus.market.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.market.dto.OrderCreateDTO;
import com.campus.market.entity.Goods;
import com.campus.market.entity.Order;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.OrderMapper;
import com.campus.market.service.GoodsService;
import com.campus.market.service.NotificationService;
import com.campus.market.service.OrderService;
import com.campus.market.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private GoodsService goodsService;

    @Resource
    private NotificationService notificationService;
    
    @Resource
    private WalletService walletService;

    @Override
    @Transactional
    public void createOrder(Long userId, OrderCreateDTO orderCreateDTO) {
        Goods goods = goodsService.getById(orderCreateDTO.getGoodsId());
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        
        if (!"published".equals(goods.getStatus())) {
            throw new BusinessException("商品不可购买");
        }
        
        if (goods.getUserId().equals(userId)) {
            throw new BusinessException("不能购买自己的商品");
        }
        
        // 创建商品快照
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("title", goods.getTitle());
        snapshot.put("price", goods.getCurrentPrice());
        snapshot.put("mainImage", goods.getMainImage());
        snapshot.put("description", goods.getDescription());
        
        Order order = new Order();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setGoodsId(goods.getId());
        order.setBuyerId(userId);
        order.setSellerId(goods.getUserId());
        order.setPrice(goods.getCurrentPrice());
        order.setStatus("PENDING"); // 待确认
        order.setGoodsSnapshot(JSONUtil.toJsonStr(snapshot));
        
        this.save(order);
        
        // 发送通知给卖家
        notificationService.createNotification(
            goods.getUserId(),
            "order",
            "新订单通知",
            "您有一个新的购买请求，请及时处理",
            order.getId()
        );
    }

    @Override
    public Page<Order> getMyBuyOrders(Long userId, Integer page, Integer size) {
        Page<Order> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getBuyerId, userId)
                .orderByDesc(Order::getCreateTime);
        return this.page(pageObj, wrapper);
    }

    @Override
    public Page<Order> getMySellOrders(Long userId, Integer page, Integer size) {
        Page<Order> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getSellerId, userId)
                .orderByDesc(Order::getCreateTime);
        return this.page(pageObj, wrapper);
    }

    @Override
    @Transactional
    public void confirmOrder(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getSellerId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 更新订单状态为待付款
        order.setStatus("CONFIRMED");
        order.setConfirmTime(LocalDateTime.now());
        this.updateById(order);
        
        // 通知买家付款
        notificationService.createNotification(
            order.getBuyerId(),
            "order",
            "订单确认通知",
            "卖家已确认订单，请及时付款",
            orderId
        );
    }

    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId, String password) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 支付
        walletService.pay(userId, orderId, order.getPrice(), password);
        
        // 更新订单状态为待发货
        order.setStatus("PAID");
        order.setPaymentTime(LocalDateTime.now());
        this.updateById(order);
        
        // 通知卖家发货
        notificationService.createNotification(
            order.getSellerId(),
            "order",
            "订单支付通知",
            "买家已付款，请及时发货",
            orderId
        );
    }

    @Override
    @Transactional
    public void shipOrder(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getSellerId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        if (!"PAID".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 更新订单状态为待收货
        order.setStatus("SHIPPED");
        order.setShipTime(LocalDateTime.now());
        this.updateById(order);
        
        // 通知买家确认收货
        notificationService.createNotification(
            order.getBuyerId(),
            "order",
            "订单发货通知",
            "卖家已发货，请注意查收",
            orderId
        );
    }

    @Override
    @Transactional
    public void completeOrder(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        if (!"SHIPPED".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 卖家收款
        walletService.income(order.getSellerId(), orderId, order.getPrice());
        
        // 更新订单状态为已完成
        order.setStatus("COMPLETED");
        order.setCompleteTime(LocalDateTime.now());
        this.updateById(order);
        
        // 更新商品状态为已售出
        Goods goods = goodsService.getById(order.getGoodsId());
        if (goods != null) {
            goods.setStatus("sold");
            goodsService.updateById(goods);
        }
        
        // 通知卖家
        notificationService.createNotification(
            order.getSellerId(),
            "order",
            "订单完成通知",
            "买家已确认收货，交易完成",
            orderId
        );
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        if ("COMPLETED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许取消");
        }
        
        // 如果已付款，退款给买家
        if ("PAID".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus())) {
            walletService.refund(order.getBuyerId(), orderId, order.getPrice());
        }
        
        // 更新订单状态为已取消
        order.setStatus("CANCELLED");
        this.updateById(order);
        
        // 通知对方
        Long notifyUserId = order.getBuyerId().equals(userId) ? order.getSellerId() : order.getBuyerId();
        notificationService.createNotification(
            notifyUserId,
            "order",
            "订单取消通知",
            "订单已被取消",
            orderId
        );
    }
}
