package com.campus.market.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.entity.*;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.ChatMessageMapper;
import com.campus.market.mapper.ChatSessionMapper;
import com.campus.market.mapper.GoodsMapper;
import com.campus.market.mapper.UserMapper;
import com.campus.market.service.ChatService;
import com.campus.market.vo.ChatMessageVO;
import com.campus.market.vo.ChatSessionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatSessionMapper chatSessionMapper;
    
    @Resource
    private ChatMessageMapper chatMessageMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private GoodsMapper goodsMapper;

    @Override
    @Transactional
    public Long createOrGetSession(Long userId, Long otherUserId, Long goodsId) {
        if (userId.equals(otherUserId)) {
            throw new BusinessException("不能与自己聊天");
        }
        
        // 确保 user1Id < user2Id，保证唯一性
        Long user1Id = userId < otherUserId ? userId : otherUserId;
        Long user2Id = userId < otherUserId ? otherUserId : userId;
        
        // 查找是否已存在会话
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getUser1Id, user1Id)
                .eq(ChatSession::getUser2Id, user2Id);
        
        if (goodsId != null) {
            wrapper.eq(ChatSession::getGoodsId, goodsId);
        } else {
            wrapper.isNull(ChatSession::getGoodsId);
        }
        
        ChatSession session = chatSessionMapper.selectOne(wrapper);
        
        if (session == null) {
            // 创建新会话
            session = new ChatSession();
            session.setUser1Id(user1Id);
            session.setUser2Id(user2Id);
            session.setGoodsId(goodsId);
            session.setUser1Unread(0);
            session.setUser2Unread(0);
            chatSessionMapper.insert(session);
        }
        
        return session.getId();
    }

    @Override
    @Transactional
    public void sendMessage(Long userId, Long sessionId, String content) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        
        // 确定接收者
        Long receiverId = session.getUser1Id().equals(userId) ? session.getUser2Id() : session.getUser1Id();
        
        // 创建消息
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderId(userId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(0);
        chatMessageMapper.insert(message);
        
        // 更新会话信息
        session.setLastMessage(content);
        session.setLastMessageTime(LocalDateTime.now());
        
        // 增加接收者的未读数
        if (session.getUser1Id().equals(receiverId)) {
            session.setUser1Unread(session.getUser1Unread() + 1);
        } else {
            session.setUser2Unread(session.getUser2Unread() + 1);
        }
        
        chatSessionMapper.updateById(session);
    }

    @Override
    public List<ChatSessionVO> getMySessions(Long userId) {
        // 查找用户参与的所有会话
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ChatSession::getUser1Id, userId).or().eq(ChatSession::getUser2Id, userId))
                .orderByDesc(ChatSession::getLastMessageTime);
        
        List<ChatSession> sessions = chatSessionMapper.selectList(wrapper);
        List<ChatSessionVO> result = new ArrayList<>();
        
        for (ChatSession session : sessions) {
            ChatSessionVO vo = new ChatSessionVO();
            vo.setId(session.getId());
            vo.setGoodsId(session.getGoodsId());
            vo.setLastMessage(session.getLastMessage());
            vo.setLastMessageTime(session.getLastMessageTime());
            
            // 确定对方用户ID
            Long otherUserId = session.getUser1Id().equals(userId) ? session.getUser2Id() : session.getUser1Id();
            vo.setOtherUserId(otherUserId);
            
            // 获取对方用户信息
            User otherUser = userMapper.selectById(otherUserId);
            if (otherUser != null) {
                vo.setOtherUserName(otherUser.getNickname());
                vo.setOtherUserAvatar(otherUser.getAvatar());
            }
            
            // 获取商品信息
            if (session.getGoodsId() != null) {
                Goods goods = goodsMapper.selectById(session.getGoodsId());
                if (goods != null) {
                    vo.setGoodsTitle(goods.getTitle());
                    vo.setGoodsImage(goods.getMainImage());
                }
            }
            
            // 获取未读数
            Integer unreadCount = session.getUser1Id().equals(userId) ? 
                    session.getUser1Unread() : session.getUser2Unread();
            vo.setUnreadCount(unreadCount);
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public List<ChatMessageVO> getSessionMessages(Long userId, Long sessionId) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        
        // 验证用户是否是会话参与者
        if (!session.getUser1Id().equals(userId) && !session.getUser2Id().equals(userId)) {
            throw new BusinessException("无权访问此会话");
        }
        
        // 查询消息列表
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSessionId, sessionId)
                .orderByAsc(ChatMessage::getCreateTime);
        
        List<ChatMessage> messages = chatMessageMapper.selectList(wrapper);
        List<ChatMessageVO> result = new ArrayList<>();
        
        for (ChatMessage message : messages) {
            ChatMessageVO vo = new ChatMessageVO();
            BeanUtil.copyProperties(message, vo);
            
            // 获取发送者信息
            User sender = userMapper.selectById(message.getSenderId());
            if (sender != null) {
                vo.setSenderName(sender.getNickname());
                vo.setSenderAvatar(sender.getAvatar());
            }
            
            // 标记是否是当前用户发送的
            vo.setIsMine(message.getSenderId().equals(userId));
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long userId, Long sessionId) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        
        // 标记消息为已读
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSessionId, sessionId)
                .eq(ChatMessage::getReceiverId, userId)
                .eq(ChatMessage::getIsRead, 0);
        
        List<ChatMessage> unreadMessages = chatMessageMapper.selectList(wrapper);
        for (ChatMessage message : unreadMessages) {
            message.setIsRead(1);
            chatMessageMapper.updateById(message);
        }
        
        // 重置未读数
        if (session.getUser1Id().equals(userId)) {
            session.setUser1Unread(0);
        } else {
            session.setUser2Unread(0);
        }
        chatSessionMapper.updateById(session);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ChatSession::getUser1Id, userId).or().eq(ChatSession::getUser2Id, userId));
        
        List<ChatSession> sessions = chatSessionMapper.selectList(wrapper);
        int totalUnread = 0;
        
        for (ChatSession session : sessions) {
            if (session.getUser1Id().equals(userId)) {
                totalUnread += session.getUser1Unread();
            } else {
                totalUnread += session.getUser2Unread();
            }
        }
        
        return totalUnread;
    }
}
