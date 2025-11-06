package com.iona.ionaapi.config;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class MistralChatModelConfig {

    @Value("${mistral.api.key}")
    private String apiKey="12222222222222222222222";

    @Bean
    public ChatModel mistralAiChatModelConfig() {
        return MistralAiChatModel.builder()
                .apiKey(apiKey)
//                .modelName(MistralAiChatModelName.MISTRAL_MEDIUM_LATEST) //pas mal
                .modelName(MistralAiChatModelName.MISTRAL_SMALL_LATEST)
                .safePrompt(true)
                .responseFormat(ResponseFormat.JSON)
                .temperature((double) 0)
                .logRequests(true)
                .logResponses(true)
                .build();
    }


}
