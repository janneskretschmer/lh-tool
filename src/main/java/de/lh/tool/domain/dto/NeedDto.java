package de.lh.tool.domain.dto;

import java.util.Date;
import java.util.Collection

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.model.HelperType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeedDto {

	private Long id;

	private Long projectId;

	private String projectName;

	private Date date;

	private Integer quantity;

	private HelperType helperType;
	
	private Integer appliedCount;
	
	private Integer approvedCount;
  
  private Collection<NeedUserDto> users;

}
