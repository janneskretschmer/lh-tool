package de.lh.tool.domain.model;

import java.util.Date;

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
@Table(name = "item_note")
public class ItemNote {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(insertable = true, name = "item_id", updatable = true)
	private Item item;

	@OneToOne
	@JoinColumn(insertable = true, name = "user_id", updatable = true)
	private User user;

	@Column(name = "note", length = 4000, nullable = false)
	private String note;

	@Column(name = "timestamp", nullable = false)
	private Date timestamp;
}