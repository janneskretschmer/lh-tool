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

import de.lh.tool.domain.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "store_project")
public class StoreProject implements Identifiable<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(insertable = true, name = "store_id", updatable = false, nullable = false)
	private Store store;

	@ManyToOne
	@JoinColumn(insertable = true, name = "project_id", updatable = false, nullable = false)
	private Project project;

	// FUTURE rename to startDate
	@Column(name = "start", nullable = false)
	private LocalDate start;

	// FUTURE rename to endDate
	@Column(name = "end", nullable = false)
	private LocalDate end;
}
