package de.lh.tool.domain.model;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = "item_note")
public class ItemNote implements Comparable<ItemNote>, Identifiable<Long> {
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
	private LocalDateTime timestamp;

	@Override
	public int compareTo(ItemNote other) {
		return Optional.ofNullable(other).map(note -> -timestamp.compareTo(note.getTimestamp())).orElse(1);
	}
}
