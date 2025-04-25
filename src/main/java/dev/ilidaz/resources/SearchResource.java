package dev.ilidaz.resources;

import dev.ilidaz.entities.Property;
import dev.ilidaz.services.SearchService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
public class SearchResource {
    @Inject
    SearchService searchService;

    @Query
    public List<Property> search(String search) {
        return searchService.find(search);
    }
}
