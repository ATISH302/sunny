package com.example.sunny.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sunny.entity.Attendance;
import com.example.sunny.entity.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	// 1人の「指定日」勤怠（打刻の重複防止/当日データ取得用）
	Optional<Attendance> findByUserAndWorkDate(User user, LocalDate workDate);

	// ★スタッフ勤怠管理：指定日の勤怠一覧
	List<Attendance> findByWorkDateOrderByClockInAsc(LocalDate workDate);

	// ★スタッフ勤怠管理：全件（最新順）
	List<Attendance> findAllByOrderByWorkDateDescClockInDesc();

	// （任意）個人の勤怠履歴（最新順）
	List<Attendance> findByUserOrderByWorkDateDesc(User user);
}
