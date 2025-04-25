package dev.ilidaz.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link dev.ilidaz.entities.Property}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDto implements Serializable {
    private String id;
    private String title;
    private String description;
    private String singleLine;
}