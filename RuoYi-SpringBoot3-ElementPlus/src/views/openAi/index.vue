<template>
    <div class="chat-container">
        <div class="messages" ref="messagesRef">
            <div v-for="msg in messages" :key="msg.id" :class="['message', msg.role]">
                {{ msg.content }}
            </div>
        </div>
        <div class="input-area">
            <input v-model="input" @keyup.enter="sendMessage" placeholder="输入消息..." />
            <button @click="sendMessage" :disabled="loading">发送</button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { getToken } from '@/utils/auth'; // 1. 引入 RuoYi 的 Token 获取工具

const messages = ref([]);
const input = ref('');
const loading = ref(false);

const sendMessage = async () => {
    // 1. 基础校验
    if (!input.value.trim() || loading.value) return;

    const userMessage = input.value;
    input.value = '';
    loading.value = true;

    // 2. 添加用户消息到列表
    messages.value.push({ id: Date.now(), role: 'user', content: userMessage });

    // 3. 预先添加一条空的 AI 消息占位（用于实时追加内容）
    const aiMessage = { id: Date.now() + 1, role: 'assistant', content: '' };
    messages.value.push(aiMessage);

    try {
        // 4. 使用原生 fetch 发起请求
        // 注意：import.meta.env.VITE_APP_BASE_API 是 RuoYi 环境变量中配置的接口前缀（如 /dev-api）
        const response = await fetch(import.meta.env.VITE_APP_BASE_API + '/api/ai/chat/stream', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // 手动携带 Token，否则会被后端拦截器拦截 (401)
                'Authorization': 'Bearer ' + getToken() 
            },
            body: JSON.stringify({ message: userMessage }) // 根据后端 DTO 调整参数名
        });

        if (!response.ok) {
            throw new Error(response.statusText);
        }

        // 5. 获取流式读取器
        const reader = response.body.getReader();
        const decoder = new TextDecoder('utf-8');

        // 6. 循环读取流
        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            // 解码二进制数据块
            const chunk = decoder.decode(value, { stream: true });
            
            // 7. 将内容实时追加到界面
            // 注意：如果后端返回的是标准 SSE 格式 (data: xxx)，这里可能需要简单的字符串处理去除 "data:" 前缀
            // 如果后端直接返回纯文本流 (Flux<String>)，直接追加即可
            aiMessage.content += chunk;
        }

    } catch (error) {
        console.error('Chat Error:', error);
        aiMessage.content += '\n[出错了，请稍后再试]';
    } finally {
        loading.value = false;
    }
};
</script>
