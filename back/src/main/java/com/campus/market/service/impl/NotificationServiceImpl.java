package com.campus.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.market.entity.Notification;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.NotificationMapper;
import com.campus.market.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public void createNotification(Long userId, String type, String title, String content, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        this.save(notification);
    }

    @Override
    public Page<Notification> getMyNotifications(Long userId, Integer page, Integer size) {
        Page<Notification> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreateTime);
        return this.page(pageObj, wrapper);
    }

    @Override
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = this.getById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }
        
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        notification.setIsRead(1);
        this.updateById(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1);
        this.update(wrapper);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        return Math.toIntExact(this.count(wrapper));
    }
}
