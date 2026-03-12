package com.campus.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.Result;
import com.campus.market.dto.OrderCreateDTO;
import com.campus.market.entity.Order;
import com.campus.market.service.OrderService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Void> createOrder(@RequestAttribute("userId") Long userId,
                                    @Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        orderService.createOrder(userId, orderCreateDTO);
        return Result.success();
    }

    @GetMapping("/buy")
    public Result<Page<Order>> getMyBuyOrders(@RequestAttribute("userId") Long userId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        Page<Order> orders = orderService.getMyBuyOrders(userId, page, size);
        return Result.success(orders);
    }

    @GetMapping("/sell")
    public Result<Page<Order>> getMySellOrders(@RequestAttribute("userId") Long userId,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size) {
        Page<Order> orders = orderService.getMySellOrders(userId, page, size);
        return Result.success(orders);
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@RequestAttribute("userId") Long userId,
                                     @PathVariable Long id) {
        orderService.confirmOrder(userId, id);
        return Result.success();
    }
    
    @PutMapping("/{id}/pay")
    public Result<Void> payOrder(@RequestAttribute("userId") Long userId,
                                 @PathVariable Long id,
                                 @RequestParam String password) {
        orderService.payOrder(userId, id, password);
        return Result.success();
    }
    
    @PutMapping("/{id}/ship")
    public Result<Void> shipOrder(@RequestAttribute("userId") Long userId,
                                  @PathVariable Long id) {
        orderService.shipOrder(userId, id);
        return Result.success();
    }
    
    @PutMapping("/{id}/complete")
    public Result<Void> completeOrder(@RequestAttribute("userId") Long userId,
                                      @PathVariable Long id) {
        orderService.completeOrder(userId, id);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@RequestAttribute("userId") Long userId,
                                    @PathVariable Long id) {
        orderService.cancelOrder(userId, id);
        return Result.success();
    }
}
