package com.example.sunny.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 商品名
	@Column(nullable = false)
	private String name;

	// 説明文
	@Column(nullable = false, length = 1000)
	private String description;

	// 価格
	@Column(nullable = false)
	private BigDecimal price;

	// 在庫数
	@Column(nullable = false)
	private int stock;

	// 公開状態 （PUBLIC / HIDDEN / SOLD）
	@Column(nullable = false)
	private String status;

	// いつ登録されたか
	@Column(nullable = false)
	private LocalDateTime createdAt;

	// ★ 必須：引数なしコンストラクタ
	public Item() {
	}

	// ゲッター・セッター（必要に応じて）

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
