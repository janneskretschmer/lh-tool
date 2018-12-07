package de.lh.tool.bean.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "first_name", length = 50, nullable = false)
	private String firstName;

	@Column(name = "last_name", length = 50, nullable = false)
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", length = 6, nullable = false)
	private Gender gender;

	@Column(name = "password_hash", length = 128)
	private String passwordHash;

	@Column(name = "password_salt", length = 32)
	private String passwordSalt;

	@Column(name = "email", length = 100, unique = true, nullable = false)
	private String email;

	@Column(name = "telephone_number", length = 30)
	private String telephoneNumber;

	@Column(name = "mobile_number", length = 30)
	private String mobileNumber;

	@Column(name = "business_number", length = 30)
	private String businessNumber;

	@Column(name = "profession", length = 250)
	private String profession;

	@Column(name = "skills", length = 4000)
	private String skills;

	public enum Gender {
		MALE, FEMALE;
	}

}
