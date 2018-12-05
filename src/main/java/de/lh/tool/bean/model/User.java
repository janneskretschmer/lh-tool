package de.lh.tool.bean.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
public class User {
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	@Setter
	@Column(name = "first_name", length = 50, nullable = false)
	private String firstName;

	@Getter
	@Setter
	@Column(name = "last_name", length = 50, nullable = false)
	private String lastName;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "gender", length = 6, nullable = false)
	private Gender gender;

	@Getter
	@Setter
	@Column(name = "password_hash", length = 128)
	private String passwordHash;

	@Getter
	@Setter
	@Column(name = "password_salt", length = 32)
	private String passwordSalt;

	@Getter
	@Setter
	@Column(name = "email", length = 100, unique = true, nullable = false)
	private String email;

	@Getter
	@Setter
	@Column(name = "telephone_number", length = 30)
	private String telephoneNumber;

	@Getter
	@Setter
	@Column(name = "mobile_number", length = 30)
	private String mobileNumber;

	@Getter
	@Setter
	@Column(name = "business_number", length = 30)
	private String businessNumber;

	@Getter
	@Setter
	@Column(name = "profession", length = 250)
	private String profession;

	@Getter
	@Setter
	@Column(name = "skills", length = 4000)
	private String skills;

	public enum Gender {
		MALE, FEMALE;
	}

}
