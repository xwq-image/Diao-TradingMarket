package com.campus.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.market.entity.Notification;

public interface NotificationService extends IService<Notification> {
    void createNotification(Long userId, String type, String title, String content, Long relatedId);
    Page<Notification> getMyNotifications(Long userId, Integer page, Integer size);
    void markAsRead(Long userId, Long notificationId);
    void markAllAsRead(Long userId);
    Integer getUnreadCount(Long userId);
}
