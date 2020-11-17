package de.lh.tool.domain.model;

import java.util.List;

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

import de.lh.tool.domain.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "item")
@EqualsAndHashCode
public class Item implements Identifiable<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(insertable = true, name = "slot_id", updatable = true)
	private Slot slot;

	@Column(name = "identifier", length = 100, nullable = false)
	private String identifier;

	@Column(name = "has_barcode", nullable = false)
	private Boolean hasBarcode;

	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Column(name = "description", length = 4000, nullable = true)
	private String description;

	@Column(name = "quantity", nullable = false)
	private Double quantity;

	@Column(name = "unit", length = 50, nullable = false)
	private String unit;

	// TODO: Default in DB
	@Column(name = "outside_qualified", nullable = false)
	private Boolean outsideQualified;

	@Column(name = "consumable", nullable = false)
	private Boolean consumable;

	@Column(name = "broken", nullable = false)
	private Boolean broken;

	@OneToOne
	@JoinColumn(insertable = true, name = "technical_crew_id", updatable = true)
	private TechnicalCrew technicalCrew;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "item")
	@EqualsAndHashCode.Exclude
	private List<ItemNote> itemNotes;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "item")
	@EqualsAndHashCode.Exclude
	private List<ItemHistory> history;

}
