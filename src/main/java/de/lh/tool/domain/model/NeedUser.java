package de.lh.tool.domain.model;

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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "need_user")
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class NeedUser implements Identifiable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(insertable = true, name = "need_id", updatable = false, nullable = false)
	@NonNull
	private Need need;

	@ManyToOne
	@JoinColumn(insertable = true, name = "user_id", updatable = false, nullable = false)
	@NonNull
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", length = 10, nullable = false)
	@NonNull
	private NeedUserState state;
}
