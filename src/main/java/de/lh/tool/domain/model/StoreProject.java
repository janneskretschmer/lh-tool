package de.lh.tool.domain.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "store_project")
public class StoreProject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(insertable = true, name = "store_id", updatable = false, nullable = false)
	@NonNull
	private Store store;

	@ManyToOne
	@JoinColumn(insertable = true, name = "project_id", updatable = false, nullable = false)
	@NonNull
	private Project project;

	@Column(name = "start", nullable = false)
	private LocalDate start;

	@Column(name = "end", nullable = false)
	private LocalDate end;
}
