package com.ruoyi.biz.controller;

import com.ruoyi.biz.domain.AiChatMessage;
import com.ruoyi.biz.domain.AiChatSession;
import com.ruoyi.biz.service.IAiChatMessageService;
import com.ruoyi.biz.service.IAiChatSessionService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 助手会话与消息（持久化）接口
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController extends BaseController {

    private final IAiChatSessionService sessionService;
    private final IAiChatMessageService messageService;

    /**
     * 当前用户的会话列表，按更新时间倒序
     */
    @GetMapping("/sessions")
    public AjaxResult listSessions() {
        try {
            Long userId = SecurityUtils.getUserId();
            List<AiChatSession> list = sessionService.listByUserId(userId);
            return success(list);
        } catch (DataAccessException e) {
            log.error("AI会话列表查询失败，请确认已执行 sql/ai_chat.sql 建表", e);
            throw new ServiceException("AI会话服务未就绪，请联系管理员执行 sql/ai_chat.sql 建表后再试");
        }
    }

    /**
     * 某会话的消息列表
     */
    @GetMapping("/sessions/{id}/messages")
    public AjaxResult listMessages(@PathVariable Long id) {
        List<AiChatMessage> list = messageService.listBySessionId(id);
        return success(list);
    }

    /**
     * 创建会话
     * body 可选: { "title": "新对话" }
     */
    @PostMapping("/sessions")
    public AjaxResult createSession(@RequestBody(required = false) CreateSessionBody body) {
        try {
            String title = (body != null && body.getTitle() != null) ? body.getTitle() : "新对话";
            AiChatSession session = sessionService.createSession(title);
            return success(session);
        } catch (DataAccessException e) {
            log.error("AI会话创建失败，请确认已执行 sql/ai_chat.sql 建表", e);
            throw new ServiceException("AI会话服务未就绪，请联系管理员执行 sql/ai_chat.sql 建表后再试");
        }
    }

    /**
     * 更新会话标题
     * body: { "title": "xxx" }
     */
    @PutMapping("/sessions/{id}")
    public AjaxResult updateSession(@PathVariable Long id, @RequestBody UpdateSessionBody body) {
        if (body == null || body.getTitle() == null) return success();
        sessionService.updateTitle(id, body.getTitle());
        return success();
    }

    /**
     * 删除会话（及该会话下所有消息，校验归属）
     * 同时提供 DELETE 与 POST，避免部分代理/网关不支持 DELETE 导致 500
     */
    @DeleteMapping("/sessions/{id}")
    public AjaxResult deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return success();
    }

    /** 删除会话（POST 方式，用于不支持 DELETE 的代理环境） */
    @PostMapping("/sessions/{id}/delete")
    public AjaxResult deleteSessionPost(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return success();
    }

    /**
     * 批量保存消息
     * body: [ { "role": "USER", "content": "..." }, { "role": "ASSISTANT", "content": "..." } ]
     */
    @PostMapping("/sessions/{id}/messages")
    public AjaxResult saveMessages(@PathVariable Long id, @RequestBody List<MessageItem> items) {
        if (items == null || items.isEmpty()) return success();
        List<AiChatMessage> messages = items.stream().map(item -> {
            AiChatMessage m = new AiChatMessage();
            m.setRole(item.getRole());
            m.setContent(item.getContent() != null ? item.getContent() : "");
            return m;
        }).collect(Collectors.toList());
        messageService.saveMessages(id, messages);
        return success();
    }

    @Data
    public static class CreateSessionBody {
        private String title;
    }

    @Data
    public static class UpdateSessionBody {
        private String title;
    }

    @Data
    public static class MessageItem {
        private String role;
        private String content;
    }
}
