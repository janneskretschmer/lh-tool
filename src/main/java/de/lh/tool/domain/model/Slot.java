package de.lh.tool.domain.model;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@Table(name = "slot")
public class Slot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(insertable = true, name = "store_id", updatable = true)
	private Store store;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "description", length = 1000, nullable = true)
	private String description;

	@Column(name = "width", nullable = true)
	private Float width;

	@Column(name = "height", nullable = true)
	private Float height;

	@Column(name = "depth", nullable = true)
	private Float depth;

	@Column(name = "outside", nullable = false)
	private Boolean outside;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "slot")
	private Collection<Item> items;
}
