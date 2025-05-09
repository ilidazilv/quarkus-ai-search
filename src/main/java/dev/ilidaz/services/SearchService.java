package dev.ilidaz.services;

import dev.ilidaz.clients.GraphQLApi;
import dev.ilidaz.clients.dtos.BrokerPropertyDto;
import dev.ilidaz.clients.dtos.PropertyFilterDto;
import dev.ilidaz.dtos.SearchResponseDto;
import dev.ilidaz.entities.Property;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static dev.ilidaz.services.JsonTextProcessor.findUuidsInString;

@ApplicationScoped
public class SearchService {
    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    GraphQLApi graphQLApi;

    @Inject
    BotService bot;

    @Inject
    JsonTextProcessor jsonTextProcessor;

    public List<Property> find(String search) {
        Embedding embedding = embeddingModel.embed(search).content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(embedding)
                .minScore(0.8)
                .maxResults(5)
                .build();

        return embeddingStore.search(request).matches().stream().map(m -> {
            String id = m.embedded().metadata().getString("id");
            return (Property) Property.findById(id);
        }).toList();
    }

    public SearchResponseDto chat(String message) {
        String res = bot.chat(message);
        List<UUID> ids = findUuidsInString(res);
//        Boolean isProcessed = false;
//        Integer tries = 0;
//
//        while (Boolean.FALSE.equals(isProcessed)) {
//            try {
//                List<JsonObject> jsonObjects = jsonTextProcessor.extractJsonObjects(res);
//
//                ids = jsonObjects.stream().map(i -> UUID.fromString(i.getString("id"))).toList();
//
//                isProcessed = Boolean.TRUE;
//            } catch (Exception e) {
//                Log.error("Wrong response from LLM: " + e.getMessage());
//                res = bot.chat(message);
//
//                if (tries++ > 3) {
//                    isProcessed = Boolean.TRUE;
//                }
//            }
//        }


        if (!ids.isEmpty()) {
            List<BrokerPropertyDto> properties = graphQLApi.properties(new PropertyFilterDto(ids.stream().map(UUID::toString).toList()));

            for (BrokerPropertyDto property : properties) {
                res = res.replace("{\"id\": \"%s\"}".formatted(property.getId()), property.getSerialId());
            }

            return new SearchResponseDto(res, properties, null);
        }

        return new SearchResponseDto(res, null, null);
    }
}
