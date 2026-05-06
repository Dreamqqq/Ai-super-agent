package com.bailou.aiagent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 对话模型配置（使用 OpenAI 兼容的中转站 API）
 */
@Slf4j
@Configuration
public class ChatModelConfig {

    @Value("${spring.ai.chat.api-key}")
    private String apiKey;

    @Value("${spring.ai.chat.base-url}")
    private String baseUrl;

    @Value("${spring.ai.chat.model}")
    private String model;

    @Bean
    @Primary
    ChatModel dashScopeChatModel() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(60))
                .build();
        ClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);

        // === 调试用：打印发给中转站的原始请求体（含 tools 字段） ===
        // 默认 debug 级别，平时不打印；需要时把 application.yml 里
        // logging.level.com.bailou.aiagent.config.ChatModelConfig 设为 DEBUG 即可看到
        ClientHttpRequestInterceptor logInterceptor = (request, body, execution) -> {
            if (log.isDebugEnabled()) {
                String bodyStr = new String(body, StandardCharsets.UTF_8);
                log.debug("===== [AI Raw Request] =====");
                log.debug("URI: {}", request.getURI());
                log.debug("Method: {}", request.getMethod());
                log.debug("Body: {}", bodyStr);
                log.debug("Body size: {} bytes", body.length);
                log.debug("============================");
            }
            return execution.execute(request, body);
        };

        RestClient.Builder restClientBuilder = RestClient.builder()
                .requestFactory(requestFactory)
                .requestInterceptor(logInterceptor);

        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .restClientBuilder(restClientBuilder)
                .build();
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(model)
                .maxTokens(8192)
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatOptions)
                .build();
    }
}

