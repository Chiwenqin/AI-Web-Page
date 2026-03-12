package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.AiChatMessage;
import com.ruoyi.biz.mapper.AiChatMessageMapper;
import com.ruoyi.biz.service.IAiChatMessageService;
import com.ruoyi.biz.service.IAiChatSessionService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 助手消息 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AiChatMessageServiceImpl extends ServiceImpl<AiChatMessageMapper, AiChatMessage> implements IAiChatMessageService {

    private final IAiChatSessionService sessionService;

    @Override
    public List<AiChatMessage> listBySessionId(Long sessionId) {
        Long userId = SecurityUtils.getUserId();
        if (!sessionService.isSessionOwnedByUser(sessionId, userId)) {
            throw new ServiceException("会话不存在或无权查看");
        }
        LambdaQueryWrapper<AiChatMessage> q = new LambdaQueryWrapper<>();
        q.eq(AiChatMessage::getSessionId, sessionId)
            .orderByAsc(AiChatMessage::getCreateTime);
        return list(q);
    }

    @Override
    public void saveMessages(Long sessionId, List<AiChatMessage> messages) {
        if (sessionId == null || messages == null || messages.isEmpty()) return;
        Long userId = SecurityUtils.getUserId();
        if (!sessionService.isSessionOwnedByUser(sessionId, userId)) {
            throw new ServiceException("会话不存在或无权操作");
        }
        for (AiChatMessage msg : messages) {
            msg.setSessionId(sessionId);
            msg.setId(null);
        }
        saveBatch(messages);
    }
}
