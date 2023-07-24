package com.example.tripgen;

import android.util.Log;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
public class OpenAiApi {
    OpenAiService service = new OpenAiService("");

    public CompletableFuture<List<String>> getRecommendedPlaces(String placeName) {
        return CompletableFuture.supplyAsync(() -> {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "You are a helpful assistant."));
            messages.add(new ChatMessage("user", "Please provide a list of 10 interesting places to visit in " + placeName + " separated by commas."));

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .n(1)
                    .maxTokens(1000)
                    .logitBias(new HashMap<>())
                    .build();

            String result = service.createChatCompletion(chatCompletionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            Log.d("OpenAiApi", " Answer: " + result);
            return extractPlaces(result);
        });
    }


    public CompletableFuture<String> getPlaceDetail(String placeName) {
        return CompletableFuture.supplyAsync(() -> {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "You are a helpful assistant."));
            messages.add(new ChatMessage("system", "If user asks irrelevant question(Not related with a history of a place or details about a place), say: I am not allowed to answer this question"));
            messages.add(new ChatMessage("user", placeName));

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .n(1)
                    .maxTokens(1000)
                    .logitBias(new HashMap<>())
                    .build();

            String result = service.createChatCompletion(chatCompletionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            Log.d("OpenAiApi", " Answer: " + result);
            return result;
        });
    }


    public List<String> extractPlaces(String chatOutput) {
        List<String> places = new ArrayList<>();
        String[] lines = chatOutput.split("\n");

        for (String line : lines) {
            String placeWithNumber = line.trim();

            // Make sure the line starts with a number and a dot, and then has some text
            if (placeWithNumber.matches("\\d+\\. .*")) {
                String place = placeWithNumber.substring(placeWithNumber.indexOf(" ") + 1);
                places.add(place);
            }
        }

        return places;
    }

}
