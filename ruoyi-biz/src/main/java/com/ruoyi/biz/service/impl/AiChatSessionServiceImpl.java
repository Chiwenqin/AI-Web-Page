package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.AiChatSession;
import com.ruoyi.biz.mapper.AiChatSessionMapper;
import com.ruoyi.biz.service.IAiChatSessionService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 助手会话 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSession> implements IAiChatSessionService {

    @Override
    public List<AiChatSession> listByUserId(Long userId) {
        LambdaQueryWrapper<AiChatSession> q = new LambdaQueryWrapper<>();
        q.eq(AiChatSession::getUserId, userId)
            .orderByDesc(AiChatSession::getUpdateTime);
        return list(q);
    }

    @Override
    public AiChatSession createSession(String title) {
        Long userId = SecurityUtils.getUserId();
        AiChatSession session = new AiChatSession();
        session.setUserId(userId);
        session.setTitle(StringUtils.isBlank(title) ? "新对话" : title);
        save(session);
        return session;
    }

    @Override
    public void updateTitle(Long sessionId, String title) {
        if (sessionId == null || StringUtils.isBlank(title)) return;
        Long userId = SecurityUtils.getUserId();
        AiChatSession session = getById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new ServiceException("会话不存在或无权操作");
        }
        session.setTitle(title);
        updateById(session);
    }

    @Override
    public boolean isSessionOwnedByUser(Long sessionId, Long userId) {
        if (sessionId == null || userId == null) return false;
        AiChatSession session = getById(sessionId);
        return session != null && session.getUserId().equals(userId);
    }
}
