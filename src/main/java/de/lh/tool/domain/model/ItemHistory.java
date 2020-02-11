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
@Table(name = "item_history")
public class ItemHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(insertable = true, name = "item_id", updatable = true)
	private Item item;

	@ManyToOne
	@JoinColumn(insertable = true, name = "user_id", updatable = true)
	private User user;

	@Column(name = "timestamp", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date timestamp;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = 20, nullable = false)
	private HistoryType type;

	@Column(name = "data", length = 1000, nullable = true)
	private String data;
}
