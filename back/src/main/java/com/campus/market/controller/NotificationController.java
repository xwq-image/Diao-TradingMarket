package com.campus.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.Result;
import com.campus.market.entity.Notification;
import com.campus.market.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @GetMapping("/my")
    public Result<Page<Notification>> getMyNotifications(@RequestAttribute("userId") Long userId,
                                                          @RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        Page<Notification> notifications = notificationService.getMyNotifications(userId, page, size);
        return Result.success(notifications);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@RequestAttribute("userId") Long userId,
                                   @PathVariable Long id) {
        notificationService.markAsRead(userId, id);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestAttribute("userId") Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(@RequestAttribute("userId") Long userId) {
        Integer count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }
}
