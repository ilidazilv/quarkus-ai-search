package dev.ilidaz.clients.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.graphql.Name;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Name("Property")
public class BrokerPropertyDto implements Serializable {
    private String id;
    private String description;
    private String title;
    private String serialId;
    private List<FileInfoDto> media;
}
