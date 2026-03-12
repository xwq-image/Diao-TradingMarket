package com.campus.market.controller;

import com.campus.market.common.Result;
import com.campus.market.dto.MessageDTO;
import com.campus.market.service.MessageService;
import com.campus.market.vo.MessageVO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @PostMapping("/send")
    public Result<Void> sendMessage(@RequestAttribute("userId") Long userId,
                                    @Valid @RequestBody MessageDTO messageDTO) {
        messageService.sendMessage(userId, messageDTO);
        return Result.success();
    }

    @GetMapping("/goods/{goodsId}")
    public Result<List<MessageVO>> getGoodsMessages(@PathVariable Long goodsId) {
        List<MessageVO> messages = messageService.getGoodsMessages(goodsId);
        return Result.success(messages);
    }

    @GetMapping("/my")
    public Result<List<MessageVO>> getMyMessages(@RequestAttribute("userId") Long userId) {
        List<MessageVO> messages = messageService.getMyMessages(userId);
        return Result.success(messages);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@RequestAttribute("userId") Long userId,
                                   @PathVariable Long id) {
        messageService.markAsRead(userId, id);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(@RequestAttribute("userId") Long userId) {
        Integer count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }
}
