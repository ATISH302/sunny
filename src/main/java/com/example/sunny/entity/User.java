package com.example.sunny.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 名前（表示名）
	@Column(nullable = false)
	private String name;

	// メール（ログインID）
	@Column(nullable = false, unique = true)
	private String email;

	// パスワード
	@Column(nullable = false)
	private String password;

	// USER / STAFF / ADMIN
	@Column(nullable = false)
	private String role;

	// 利用可能フラグ（Spring Securityの enabled 判定に使える）
	@Column(nullable = false)
	private boolean enabled = true;

	// ★ ステータス（BAN / 停止）
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status = UserStatus.ACTIVE;

	// 作成日時
	@Column(nullable = false)
	private LocalDateTime createdAt;

	// ===== コンストラクタ =====
	public User() {
	}

	public User(String name, String email, String password, String role) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.enabled = true;
		this.status = UserStatus.ACTIVE;
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		// 念のため（DBでnullにならないように）
		if (this.status == null)
			this.status = UserStatus.ACTIVE;
	}

	// ===== Getter / Setter =====
	public Long getId() {
		return id;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
