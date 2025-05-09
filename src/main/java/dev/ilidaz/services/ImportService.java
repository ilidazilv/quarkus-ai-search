package dev.ilidaz.services;

import dev.ilidaz.dtos.PropertyDto;
import dev.ilidaz.entities.Property;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ImportService {
    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    EmbeddingModel embeddingModel;

    public void load(
//            @Observes StartupEvent event,
            @ConfigProperty(name = "properties.file") Path path
    ) throws Exception {
        if (!Files.exists(path)) {
            throw new IllegalStateException("Missing movies file: " + path);
        }

//    embeddingStore.removeAll();

        EmbeddingStoreIngestor ingester = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        List<Document> docs = new ArrayList<>();
        List<Property> properties = parsePropertiesFile(path);
        final int BATCH_SIZE = 1000;
        int processedCount = 0;

        for (Property property : properties) {
            if (Property.findById(property.getId()) != null) {
                continue;
            }

            save(property);

            Metadata metadata = Metadata.from(Map.of(
                    "id", property.id,
                    "singleLine", property.singleLine,
                    "title", property.title
            ));

            Document document = Document.from(
                    property.description + " Location - " + property.singleLine + " ID - " + property.id,
                    metadata);

            docs.add(document);
            processedCount++;

            // When we reach batch size or finish processing all properties, ingest the batch
            if (docs.size() >= BATCH_SIZE || processedCount == properties.size()) {
                Log.info("Ingesting batch of %s properties (%s of %s)...".formatted(
                        docs.size(), processedCount, properties.size()));

                ingester.ingest(docs);
                docs.clear(); // Clear the list for the next batch
            }
        }

        Log.info("Application initialized!");
    }

    public boolean importProperty(PropertyDto data) {
        Property property = Property.builder()
                .description(data.getDescription())
                .title(data.getTitle())
                .id(data.getId())
                .singleLine(data.getSingleLine())
                .build();

        EmbeddingStoreIngestor ingester = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        save(property);
        Metadata metadata = Metadata.from(Map.of("id", property.id, "singleLine", property.singleLine, "title", property.title));
        Document document = Document.from(property.description + "Location - %s".formatted(property.singleLine), metadata);

        ingester.ingest(document);

        return true;
    }

    @Transactional
    public Property save(Property property) {
        property.persist();
        return property;
    }

    private List<Property> parsePropertiesFile(Path filePath) {
        List<Property> properties = new ArrayList<>();

        try (FileReader fileReader = new FileReader(filePath.toString(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT
                     .withDelimiter(';')  // Use semicolon as delimiter
                     .withQuote('"')
                     .withIgnoreEmptyLines(true)
                     .withTrim(false))) {

            for (CSVRecord record : csvParser) {
                if (record.get(0).equals("id")) {
                    continue;
                }
                try {
                    Property property = new Property();
                    if (record.size() > 0) property.setId(record.get(0));
                    if (record.size() > 1) property.setTitle(record.get(1));
                    if (record.size() > 2) property.setDescription(record.get(2));
                    if (record.size() > 3) property.setSingleLine(record.get(3));
                    properties.add(property);
                } catch (Exception e) {
                    Log.error("Error processing record #" + record.getRecordNumber() + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            Log.error("Error parsing CSV file: " + e.getMessage());
        }

        return properties;
    }
}
