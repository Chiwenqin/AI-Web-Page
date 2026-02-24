package com.ruoyi.biz.controller;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.ruoyi.common.config.OpenAIConfig;
import com.ruoyi.common.utils.ai.AIMessage;
import com.ruoyi.common.utils.ai.AIRole;
import com.ruoyi.common.utils.ai.OpenAI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @PostMapping("/chat/stream")
    public ResponseEntity<StreamingResponseBody> streamChat(@RequestBody List<AIMessage> historyMessages) {
        // 1. 创建客户端
        OpenAIClient client = OpenAI.chatClient(
                OpenAIConfig.getApiKey(),
                OpenAIConfig.getBaseUrl(),
                OpenAIConfig.getProxy() != null ? OpenAIConfig.getProxy().toJavaProxy() : null
        );

        // 2. 构建消息列表
        // 如果 historyMessages 为空，可以手动添加第一个 System 消息
        if (historyMessages.isEmpty()) {
            historyMessages.add(new AIMessage(AIRole.SYSTEM, "你是一个有帮助的助手。"));
        }

        // 3. 直接将前端传来的历史记录列表传给工具类
        ChatCompletionCreateParams params = OpenAI.chatParams(
                OpenAIConfig.getModel(),
                historyMessages
        );
        // 4. 流式响应
        return OpenAI.chatStream(client, params, new OpenAI.StreamContentListener() {
            @Override
            public void onContent(String content) {
                // 每收到一段增量内容时调用
                System.out.print(content);
            }

            @Override
            public void onComplete(String fullContent) {
                // 全部内容流式结束后调用
                System.out.println("\n完整响应：" + fullContent);
            }
        });
    }
}