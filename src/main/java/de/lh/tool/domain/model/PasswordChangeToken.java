package de.lh.tool.domain.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "password_change_token")
public class PasswordChangeToken {

	public static final int TOKEN_LENGTH = 128;
	public static final int MIN_PASSWORD_LENGTH = 6;
	public static final int TOKEN_VALIDITY_IN_DAYS = 14;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(insertable = true, name = "user_id", unique = true, updatable = true)
	private User user;

	@Column(name = "token", length = TOKEN_LENGTH, nullable = false)
	private String token;

	@Column(name = "updated")
	@UpdateTimestamp
	private Calendar updated;

}