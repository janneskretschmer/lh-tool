package de.lh.tool.domain.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeedDto implements Identifiable<Long> {

	private Long id;

	private Long projectHelperTypeId;

	// FUTURE use new time api
	private Date date;

	private Integer quantity;

}
