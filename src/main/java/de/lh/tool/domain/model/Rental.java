package de.lh.tool.domain.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rental")
public class Rental {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(insertable = true, name = "item_id", updatable = true, nullable = false)
	private Item item;

	@OneToOne
	@JoinColumn(insertable = true, name = "user_id", updatable = true, nullable = false)
	private User user;
	@OneToOne
	@JoinColumn(insertable = true, name = "store_keeper_id", updatable = true, nullable = false)
	private User storeKeeper;

	@Column(name = "start", nullable = false)
	private LocalDateTime start;

	@Column(name = "end", nullable = true)
	private LocalDateTime end;
}
