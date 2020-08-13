package de.lh.tool.domain.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RentalDto {
	private Long id;
	private Long itemId;
	private Long userId;
	private Long storeKeeperId;
	private LocalDateTime start;
	private LocalDateTime end;
}
