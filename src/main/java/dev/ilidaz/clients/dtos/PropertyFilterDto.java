package dev.ilidaz.clients.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.graphql.Name;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Name("PropertyInputFilters")
public class PropertyFilterDto {
    private List<String> ids;
}
