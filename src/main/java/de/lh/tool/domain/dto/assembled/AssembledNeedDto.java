package de.lh.tool.domain.dto.assembled;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.model.NeedUserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssembledNeedDto {

	private Long id;

	private Long projectHelperTypeId;

	private Date date;

	private Integer quantity;

	private NeedUserState state;

	private List<AssembledNeedUserDto> users;

}
