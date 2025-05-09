package dev.ilidaz.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;

@RegisterAiService(retrievalAugmentor = PropertyRetrievalAugmentorSupplier.class) // no need to declare a retrieval augmentor here, it is automatically generated and discovered
@SessionScoped
public interface BotService {

    @SystemMessage("""
            You are an AI named Bob helping to find ideal properties, which you have in a database.
            Your response must be polite, use the same language as the question, and be relevant to the question.
            You should always provide ID of the property, so client can find it through the search.
            Always, an answer in language, which is the same as the question.

            When you don't know, respond that you don't know the answer and the team will contact the customer directly.
            
            Please, format ids from additional contents in json format: {"id": "UUID from additional content"}, so developer can find it in the database.
            Here's an example how it'll look like in a text "ID - 54d5dbc8-f2d1-49a5-985a-bde311a438bd"
            """)
    String chat(@UserMessage String question);
}
