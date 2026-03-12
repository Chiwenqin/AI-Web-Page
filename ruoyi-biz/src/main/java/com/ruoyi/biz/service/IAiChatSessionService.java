package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.AiChatSession;

import java.util.List;

/**
 * AI 助手会话 Service 接口
 */
public interface IAiChatSessionService extends IService<AiChatSession> {

    /**
     * 当前用户的会话列表，按更新时间倒序
     */
    List<AiChatSession> listByUserId(Long userId);

    /**
     * 创建会话（当前用户）
     */
    AiChatSession createSession(String title);

    /**
     * 更新会话标题（校验归属）
     */
    void updateTitle(Long sessionId, String title);

    /**
     * 校验会话属于当前用户
     */
    boolean isSessionOwnedByUser(Long sessionId, Long userId);

    /**
     * 删除会话（及该会话下所有消息，校验归属）
     */
    void deleteSession(Long sessionId);
}
