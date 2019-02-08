package de.lh.tool.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "need")
public class Need {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(insertable = true, name = "project_id", updatable = true)
	private Project project;

	@Column(name = "date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date date;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "helper_type", length = 20, nullable = false)
	private HelperType helperType;
}
