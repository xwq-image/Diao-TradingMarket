package com.campus.market.service;

import com.campus.market.vo.ChatMessageVO;
import com.campus.market.vo.ChatSessionVO;
import java.util.List;

public interface ChatService {
    // 创建或获取聊天会话
    Long createOrGetSession(Long userId, Long otherUserId, Long goodsId);
    
    // 发送消息
    void sendMessage(Long userId, Long sessionId, String content);
    
    // 获取会话列表
    List<ChatSessionVO> getMySessions(Long userId);
    
    // 获取会话消息列表
    List<ChatMessageVO> getSessionMessages(Long userId, Long sessionId);
    
    // 标记消息为已读
    void markMessagesAsRead(Long userId, Long sessionId);
    
    // 获取未读消息总数
    Integer getUnreadCount(Long userId);
}
