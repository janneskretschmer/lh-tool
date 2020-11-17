package de.lh.tool.domain.model;

import java.time.LocalDateTime;

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
@Table(name = "item_history")
public class ItemHistory implements Identifiable<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(insertable = true, name = "item_id", updatable = true)
	private Item item;

	@ManyToOne
	@JoinColumn(insertable = true, name = "user_id", updatable = true)
	private User user;

	@Column(nullable = false)
	private LocalDateTime timestamp;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = 20, nullable = false)
	private HistoryType type;

	@Column(name = "data", length = 1000, nullable = true)
	private String data;
}
