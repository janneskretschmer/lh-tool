package de.lh.tool.domain.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "project")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", length = 150, nullable = false)
	private String name;

	@Column(name = "start_date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "end_date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date endDate;

	@ManyToMany()
	@JoinTable(name = "project_user", inverseJoinColumns = @JoinColumn(name = "user_id"), joinColumns = @JoinColumn(name = "project_id"))
	private Collection<User> users;

}
