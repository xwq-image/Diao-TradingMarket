package com.campus.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.market.dto.MessageDTO;
import com.campus.market.entity.Goods;
import com.campus.market.entity.Message;
import com.campus.market.entity.User;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.MessageMapper;
import com.campus.market.service.GoodsService;
import com.campus.market.service.MessageService;
import com.campus.market.service.NotificationService;
import com.campus.market.service.UserService;
import com.campus.market.vo.MessageVO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private UserService userService;
    
    @Resource
    private GoodsService goodsService;
    
    @Resource
    private NotificationService notificationService;

    @Override
    public void sendMessage(Long userId, MessageDTO messageDTO) {
        Message message = new Message();
        message.setGoodsId(messageDTO.getGoodsId());
        message.setUserId(userId);
        message.setReplyToId(messageDTO.getReplyToId());
        message.setContent(messageDTO.getContent());
        message.setIsRead(0);
        
        this.save(message);
        
        // 获取商品信息
        Goods goods = goodsService.getById(messageDTO.getGoodsId());
        if (goods != null && !goods.getUserId().equals(userId)) {
            // 如果留言者不是商品发布者，给商品发布者发送通知
            User commenter = userService.getById(userId);
            String commenterName = commenter != null ? commenter.getNickname() : "用户";
            
            notificationService.createNotification(
                goods.getUserId(),
                "message",
                "收到新留言",
                String.format("用户\"%s\"在您的商品\"%s\"下留言了", commenterName, goods.getTitle()),
                goods.getId()
            );
        }
    }

    @Override
    public List<MessageVO> getGoodsMessages(Long goodsId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getGoodsId, goodsId)
                .orderByAsc(Message::getCreateTime);
        
        List<Message> messages = this.list(wrapper);
        List<MessageVO> result = new ArrayList<>();
        
        for (Message message : messages) {
            MessageVO vo = new MessageVO();
            vo.setId(message.getId());
            vo.setGoodsId(message.getGoodsId());
            vo.setUserId(message.getUserId());
            vo.setReplyToId(message.getReplyToId());
            vo.setContent(message.getContent());
            vo.setIsRead(message.getIsRead());
            vo.setCreateTime(message.getCreateTime());
            
            // 获取用户信息
            User user = userService.getById(message.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname());
                vo.setUserAvatar(user.getAvatar());
            }
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public List<MessageVO> getMyMessages(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
                .orderByDesc(Message::getCreateTime);
        
        List<Message> messages = this.list(wrapper);
        List<MessageVO> result = new ArrayList<>();
        
        for (Message message : messages) {
            MessageVO vo = new MessageVO();
            vo.setId(message.getId());
            vo.setGoodsId(message.getGoodsId());
            vo.setUserId(message.getUserId());
            vo.setReplyToId(message.getReplyToId());
            vo.setContent(message.getContent());
            vo.setIsRead(message.getIsRead());
            vo.setCreateTime(message.getCreateTime());
            
            User user = userService.getById(message.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname());
                vo.setUserAvatar(user.getAvatar());
            }
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public void markAsRead(Long userId, Long messageId) {
        Message message = this.getById(messageId);
        if (message == null) {
            throw new BusinessException("留言不存在");
        }
        
        message.setIsRead(1);
        this.updateById(message);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
                .eq(Message::getIsRead, 0);
        return Math.toIntExact(this.count(wrapper));
    }
}
