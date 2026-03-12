package com.campus.market.controller;

import com.campus.market.common.Result;
import com.campus.market.dto.ChatMessageDTO;
import com.campus.market.service.ChatService;
import com.campus.market.vo.ChatMessageVO;
import com.campus.market.vo.ChatSessionVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    /**
     * 创建或获取聊天会话
     */
    @PostMapping("/session")
    public Result<Long> createOrGetSession(@RequestAttribute("userId") Long userId,
                                           @RequestParam Long otherUserId,
                                           @RequestParam(required = false) Long goodsId) {
        Long sessionId = chatService.createOrGetSession(userId, otherUserId, goodsId);
        return Result.success(sessionId);
    }

    /**
     * 发送消息
     */
    @PostMapping("/message")
    public Result<Void> sendMessage(@RequestAttribute("userId") Long userId,
                                    @Valid @RequestBody ChatMessageDTO messageDTO) {
        chatService.sendMessage(userId, messageDTO.getSessionId(), messageDTO.getContent());
        return Result.success();
    }

    /**
     * 获取我的会话列表
     */
    @GetMapping("/sessions")
    public Result<List<ChatSessionVO>> getMySessions(@RequestAttribute("userId") Long userId) {
        List<ChatSessionVO> sessions = chatService.getMySessions(userId);
        return Result.success(sessions);
    }

    /**
     * 获取会话消息列表
     */
    @GetMapping("/session/{sessionId}/messages")
    public Result<List<ChatMessageVO>> getSessionMessages(@RequestAttribute("userId") Long userId,
                                                          @PathVariable Long sessionId) {
        List<ChatMessageVO> messages = chatService.getSessionMessages(userId, sessionId);
        return Result.success(messages);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/session/{sessionId}/read")
    public Result<Void> markMessagesAsRead(@RequestAttribute("userId") Long userId,
                                           @PathVariable Long sessionId) {
        chatService.markMessagesAsRead(userId, sessionId);
        return Result.success();
    }

    /**
     * 获取未读消息总数
     */
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(@RequestAttribute("userId") Long userId) {
        Integer count = chatService.getUnreadCount(userId);
        return Result.success(count);
    }
}
