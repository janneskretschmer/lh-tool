package de.lh.tool.domain.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User implements UserDetails {
	private static final long serialVersionUID = 2931297692792293149L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "first_name", length = 50, nullable = false)
	private String firstName;

	@Column(name = "last_name", length = 50, nullable = false)
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", length = 6, nullable = false)
	private Gender gender;

	@Column(name = "password_hash", length = 60)
	private String passwordHash;

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

	@OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true, mappedBy = "user")
	private PasswordChangeToken passwordChangeToken;

	public enum Gender {
		MALE, FEMALE;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<>();
	}

	/**
	 * alias for getPasswordHash()
	 */
	@Override
	public String getPassword() {
		return passwordHash;
	}

	/**
	 * alias for getEmail()
	 */
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
