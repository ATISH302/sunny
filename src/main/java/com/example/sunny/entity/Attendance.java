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
@Table(name = "attendance")
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private LocalDate workDate;

	private LocalDateTime clockIn;
	private LocalDateTime clockOut;

	// ★ 休憩開始・終了（これが必要！）
	private LocalDateTime breakStart;
	private LocalDateTime breakEnd;

	public Attendance() {
	}

	public Attendance(User user, LocalDate workDate) {
		this.user = user;
		this.workDate = workDate;
		this.clockIn = LocalDateTime.now();
	}

	// ===== Getter / Setter =====
	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDate getWorkDate() {
		return workDate;
	}

	public void setWorkDate(LocalDate workDate) {
		this.workDate = workDate;
	}

	public LocalDateTime getClockIn() {
		return clockIn;
	}

	public void setClockIn(LocalDateTime clockIn) {
		this.clockIn = clockIn;
	}

	public LocalDateTime getClockOut() {
		return clockOut;
	}

	public void setClockOut(LocalDateTime clockOut) {
		this.clockOut = clockOut;
	}

	public LocalDateTime getBreakStart() {
		return breakStart;
	}

	public void setBreakStart(LocalDateTime breakStart) {
		this.breakStart = breakStart;
	}

	public LocalDateTime getBreakEnd() {
		return breakEnd;
	}

	public void setBreakEnd(LocalDateTime breakEnd) {
		this.breakEnd = breakEnd;
	}
}
