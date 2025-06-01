package com.vishnu.springai.openai_demo.services;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import com.vishnu.springai.openai_demo.text.prompttemplate.CountryCuisines;

@Service
public class OpenAiService {

    private final ChatClient chatClient;

    public OpenAiService(ChatClient.Builder builder) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        this.chatClient = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public ChatResponse generateResponse(String question) {
        return chatClient
                .prompt(question)
                .call()
                .chatResponse();
    }

    public String getTravelGuidance(String city, String month, String language, String budget) {
        PromptTemplate promptTemplate = new PromptTemplate("Welcome to the {city} travel guide! " +
                "If you are visiting in {month}, here's what you can do :" +
                "1. Must-visit attractions: " +
                "2. Local cuisine to try: " +
                "3. Cultural experiences: " +
                "4. Useful phrases in {language}: " +
                "5. Tips for travelling on a {budget} budget: ");
        Prompt promt = promptTemplate
                .create(Map.of("city", city, "month", month, "language", language, "budget", budget));
        return chatClient
                .prompt(promt)
                .call()
                .chatResponse().getResult().getOutput().getText();
    }

    public CountryCuisines getCuisines(String country, String numCuisines, String language) {
        PromptTemplate promptTemplate = new PromptTemplate(
                "You are a expert in traditional cuisines. "
                        + "Anser the question: What is the traditional cuisine of {country} ? "
                        + "Return a list of {numCuisines} in {language}."
                        + "Avoid giving information about ficitional palces."
                        + "IF the country is ficition al or non-existent simple return the country with out any cuisines.");
        Prompt prompt = promptTemplate
                .create(Map.of(
                        "country", country,
                        "numCuisines", numCuisines,
                        "language", language));
        return chatClient
                .prompt(prompt)
                .call()
                .entity(CountryCuisines.class);
    }
}
