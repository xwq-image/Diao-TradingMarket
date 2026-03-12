package com.campus.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.market.dto.MessageDTO;
import com.campus.market.entity.Message;
import com.campus.market.vo.MessageVO;
import java.util.List;

public interface MessageService extends IService<Message> {
    void sendMessage(Long userId, MessageDTO messageDTO);
    List<MessageVO> getGoodsMessages(Long goodsId);
    List<MessageVO> getMyMessages(Long userId);
    void markAsRead(Long userId, Long messageId);
    Integer getUnreadCount(Long userId);
}
