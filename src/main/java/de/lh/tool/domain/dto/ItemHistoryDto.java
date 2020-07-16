package de.lh.tool.domain.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.model.HistoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemHistoryDto {
	private Long id;
	private Long itemId;
	private Long userId;
	private LocalDateTime timestamp;
	private HistoryType type;
	private String data;
}
