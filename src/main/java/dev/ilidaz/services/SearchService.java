package dev.ilidaz.services;

import dev.ilidaz.entities.Property;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class SearchService {
    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    EmbeddingModel embeddingModel;

    public List<Property> find(String search) {
        Embedding embedding = embeddingModel.embed(search).content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(embedding)
                .minScore(0.9)
                .maxResults(10)
                .build();

        return embeddingStore.search(request).matches().stream().map(m -> {
            String id = m.embedded().metadata().getString("id");
            return (Property) Property.findById(id);
        }).toList();
    }
}
