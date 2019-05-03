package com.mma.domain;

import java.util.Date;

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

import com.mma.common.enums.UserEnums.Status;

@Entity
@Table(name = "user", schema = "heroku_538c076711f1b9b")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private UserRole role;

	@ManyToOne
	@JoinColumn(name = "unit_id")
	private Unit unit;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;

	@Column(name = "name")
	private String name;

	@Column(name = "email")
	private String email;

	@Column(name = "email_token")
	private String emailToken;

	@Column(name = "email_time_requested")
	private Date emailTimeRequested;

	@Column(name = "email_time_confirmed")
	private Date emailTimeConfirmed;

	@Column(name = "password")
	private String password;

	@Column(name = "time_added") 
	private Date timeAdded;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailToken() {
		return emailToken;
	}

	public void setEmailToken(String emailToken) {
		this.emailToken = emailToken;
	}

	public Date getEmailTimeRequested() {
		return emailTimeRequested;
	}

	public void setEmailTimeRequested(Date emailTimeRequested) {
		this.emailTimeRequested = emailTimeRequested;
	}

	public Date getEmailTimeConfirmed() {
		return emailTimeConfirmed;
	}

	public void setEmailTimeConfirmed(Date emailTimeConfirmed) {
		this.emailTimeConfirmed = emailTimeConfirmed;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getTimeAdded() {
		return timeAdded;
	}

	public void setTimeAdded(Date timeAdded) {
		this.timeAdded = timeAdded;
	}

}

