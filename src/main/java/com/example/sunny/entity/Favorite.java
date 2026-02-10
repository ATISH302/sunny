package com.example.sunny.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Favorite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// どのユーザーのお気に入りか
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// どの商品をお気に入りにしたか
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	// 登録日時（なくても良いけど、あった方が発表でカッコいい）
	private LocalDateTime createdAt;

	// getter / setter

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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
