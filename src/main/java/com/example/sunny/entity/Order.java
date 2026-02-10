package com.example.sunny.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ─ ユーザー ─
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// ─ 商品 ─
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	// ─ 数量 ─
	@Column(nullable = false)
	private int quantity;

	// ─ 合計金額 ─
	@Column(name = "total_price", nullable = false)
	private int totalPrice;

	// ─ ステータス（CREATED / PAID / SHIPPING / COMPLETED / CANCELED）─
	@Column(nullable = false)
	private String status;

	// ─ 作成日時 ─
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	// ─ 配送予定日 ─
	@Column(name = "delivery_date")
	private LocalDate deliveryDate;

	// ─ 追跡番号 ─
	@Column(name = "tracking_number")
	private String trackingNumber;

	// ====== getter / setter ======

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
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

	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	// ====== 画面表示用：日本語ステータス ======

	public String getStatusLabel() {
		if (status == null)
			return "";
		switch (status) {
		case "CREATED":
			return "支払い待ち";
		case "PAID":
			return "支払い済み";
		case "SHIPPING":
			return "発送準備中";
		case "DELIVERING":
			return "配達中";
		case "COMPLETED":
			return "配達完了";
		case "CANCELLED":
		case "CANCELED":
			return "キャンセル";
		default:
			return status;
		}
	}

}
