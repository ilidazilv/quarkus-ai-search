package dev.ilidaz.resources;

import dev.ilidaz.dtos.PropertyDto;
import dev.ilidaz.services.ImportService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;

@GraphQLApi
public class ImportResource {
    @Inject
    ImportService importService;

    @Mutation
    public boolean importProperty(PropertyDto data) {
        return importService.importProperty(data);
    }
}
