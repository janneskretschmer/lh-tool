package de.lh.tool.domain.model;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.lh.tool.domain.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "project_helper_type")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectHelperType implements Identifiable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(insertable = true, name = "project_id", updatable = false, nullable = false)
	private Project project;

	@ManyToOne
	@JoinColumn(insertable = true, name = "helper_type_id", updatable = false, nullable = false)
	private HelperType helperType;

	@Column(name = "weekday", nullable = false)
	private Integer weekday;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "end_time", nullable = true)
	private LocalTime endTime;
}