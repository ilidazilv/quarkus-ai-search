package dev.ilidaz.dtos;

import dev.ilidaz.clients.dtos.BrokerPropertyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDto {
    private String message;
    private List<BrokerPropertyDto> properties;
    private String messageId;
}
