package dev.ilidaz.clients;

import dev.ilidaz.clients.dtos.BrokerPropertyDto;
import dev.ilidaz.clients.dtos.PropertyFilterDto;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLClientApi(configKey = "graphql-api")
public interface GraphQLApi {
    @Query
    List<BrokerPropertyDto> properties(PropertyFilterDto filters);
}
