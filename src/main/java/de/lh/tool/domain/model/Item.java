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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "item")
public class Item {
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

	private Double width;
	private Double height;
	private Double depth;

	@Column(name = "outside_qualified", nullable = false)
	private Boolean outsideQualified;

	@Column(name = "consumable", nullable = false)
	private Boolean consumable;

	@Column(name = "broken", nullable = false)
	private Boolean broken;

	@Column(name = "picture_url", length = 255)
	private String pictureUrl;

	@OneToOne
	@JoinColumn(insertable = true, name = "technical_crew_id", updatable = true)
	private TechnicalCrew technicalCrew;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "item")
	private Collection<ItemNote> itemNotes;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "item")
	private Collection<ItemHistory> history;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "item_tag", inverseJoinColumns = @JoinColumn(name = "tag_id"), joinColumns = @JoinColumn(name = "item_id"))
	private Collection<ItemTag> tags;

}
