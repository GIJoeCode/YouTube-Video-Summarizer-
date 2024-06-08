package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatGPTSummarizer {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions"; // Correct endpoint for chat models
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String summarize(String content) throws IOException {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("OpenAI API key is missing. Please set the environment variable 'OPENAI_API_KEY'.");
        }

        // Enhancing the prompt to focus on concise summarization
        String prompt = "Summarize the following content into a concise paragraph focusing on key points:\n\n" + content;

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "gpt-3.5-turbo");
        payload.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(payload),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code " + response.code() + " with body " + response.body().string());
            }
            Map<?, ?> responseBody = objectMapper.readValue(response.body().byteStream(), Map.class);
            List<?> choices = (List<?>) responseBody.get("choices");
            if (!choices.isEmpty()) {
                Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) choice.get("message");
                return message != null ? (String) message.get("content") : null;
            }
            throw new IOException("No choices found in the response");
        }
    }
}