package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.AiChatMessage;

import java.util.List;

/**
 * AI 助手消息 Service 接口
 */
public interface IAiChatMessageService extends IService<AiChatMessage> {

    /**
     * 某会话的消息列表，按创建时间升序（校验会话归属当前用户）
     */
    List<AiChatMessage> listBySessionId(Long sessionId);

    /**
     * 批量保存消息（校验 session 归属当前用户）
     */
    void saveMessages(Long sessionId, List<AiChatMessage> messages);
}
