package dev.ilidaz.services;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@Named("propertyRetriever")
@ApplicationScoped
public class PropertyRetrievalAugmentor implements RetrievalAugmentor {

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    EmbeddingModel embeddingModel;

    @Override
    public AugmentationResult augment(AugmentationRequest request) {
        // Create embedding for the query text
        String text = request.metadata().userMessage().singleText();
        Embedding queryEmbedding = embeddingModel.embed(text).content();

        // Use the search method available in your version
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(5)
                .minScore(0.9)
                .build();

        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        // Extract the text segments from the search result
        List<TextSegment> relevantSegments = searchResult.matches().stream()
                .map(EmbeddingMatch::embedded)
                .toList();

        // Convert TextSegments to Content objects
        List<Content> contents = relevantSegments.stream()
                .map(Content::from)
                .toList();

        // Return the original chat message and the relevant contents
        return new AugmentationResult(request.chatMessage(), contents);
    }
}