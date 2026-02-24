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

const messages = ref([]); // 消息历史
const input = ref('');
const loading = ref(false);


const sendMessage = async () => {
    if (!input.value.trim() || loading.value) return;

    const userText = input.value;
    input.value = '';
    loading.value = true;

    // 1. 将用户的当前输入加入本地历史
    messages.value.push({ role: 'user', content: userText });

    // 2. 准备发送给后端的 Payload（包含之前的上下文）
    // 注意：role 必须和后端 AIRole 对应（'user', 'assistant', 'system'）
    const payload = messages.value.map(m => ({
        role: m.role.toUpperCase(), // 匹配后端的 AIRole 枚举（如果是用枚举的话）
        message: m.content
    }));

    // 3. 占位 AI 回复
    const aiMessage = { role: 'assistant', content: '' };
    messages.value.push(aiMessage);

    try {
        const response = await fetch(import.meta.env.VITE_APP_BASE_API + '/api/ai/chat/stream', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken() 
            },
            // 发送整个列表而不是单条消息
            body: JSON.stringify(payload) 
        });

        const reader = response.body.getReader();
        const decoder = new TextDecoder('utf-8');

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;
            const chunk = decoder.decode(value, { stream: true });
            
            // 实时更新 AI 的回复
            aiMessage.content += chunk;
        }
    } catch (error) {
        console.error('Chat Error:', error);
        aiMessage.content += '\n[连接中断]';
    } finally {
        loading.value = false;
    }
};
</script>
