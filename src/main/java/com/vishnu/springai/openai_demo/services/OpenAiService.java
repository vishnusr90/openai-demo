package com.vishnu.springai.openai_demo.services;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vishnu.springai.openai_demo.text.prompttemplate.CountryCuisines;

@Service
public class OpenAiService {

    private final ChatClient chatClient;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private VectorStore vectorStore;

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
        Prompt prompt = promptTemplate
                .create(Map.of("city", city, "month", month, "language", language, "budget", budget));
        return chatClient
                .prompt(prompt)
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

    public float[] embed(String text) {
        return embeddingModel.embed(text);
    }

    public List<Document> searchJobs(String query) {
        // return vectorStore.similaritySearch(query);
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .topK(3)
                .build());
    }

    public double findSimilarity(String text1, String text2) {
        List<float[]> response = embeddingModel
                .embed(List.of(text1, text2));
        // invoke cosine similarity
        return cosineSimilarity(response.get(0), response.get(1));
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0 || normB == 0) {
            return 0.0; // Avoid division by zero
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
