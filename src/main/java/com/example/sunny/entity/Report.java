package com.example.sunny.entity;

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
@Table(name = "reports")
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 通報対象のレビュー
	@ManyToOne
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;

	// 通報したユーザー
	@ManyToOne
	@JoinColumn(name = "reported_by_id", nullable = false)
	private User reportedBy;

	// 通報理由（今は未使用でもOK）
	@Column(length = 1000)
	private String reason;

	// 通報日時
	@Column(nullable = false)
	private LocalDateTime createdAt;

	// 対応ステータス（NEW / DONE など）
	@Column(nullable = false)
	private String status;

	// ===== getter / setter =====

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public User getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(User reportedBy) {
		this.reportedBy = reportedBy;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
