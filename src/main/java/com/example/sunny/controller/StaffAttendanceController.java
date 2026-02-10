package com.example.sunny.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sunny.entity.Attendance;
import com.example.sunny.repository.AttendanceRepository;

// =====================
// スタッフ/管理者：勤怠管理（一覧）表示
// =====================
//
// このControllerは、スタッフ側で「勤怠一覧」を確認するための機能。
// URL：/staff/attendance/manage
//
// 特徴：
// ・?date=YYYY-MM-DD を付けると指定日で絞り込みできる
// ・date未指定なら「今日の日付」で表示する
// ・勤怠データを出勤時間順で並べて一覧表示する
//
// 例：
// /staff/attendance/manage?date=2026-01-16
//

@Controller
public class StaffAttendanceController {

	private final AttendanceRepository attendanceRepository;

	public StaffAttendanceController(AttendanceRepository attendanceRepository) {
		this.attendanceRepository = attendanceRepository;
	}

	/**
	 * ======================
	 * スタッフ/管理者：勤怠管理（一覧）
	 * ======================
	 *
	 * 処理の流れ：
	 * 1) date パラメータを受け取る（未指定なら null）
	 * 2) date があればその日付、なければ今日の日付を targetDate にする
	 * 3) targetDate の勤怠一覧を DB から取得する
	 * 4) 画面で表示するために model に入れる
	 * 5) staff_attendance_manage.html に渡して表示する
	 *
	 */
	@GetMapping("/staff/attendance/manage")
	public String staffAttendanceManage(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			Model model) {

		// date が指定されていればその日、なければ今日
		LocalDate targetDate = (date != null) ? date : LocalDate.now();

		// 指定日の勤怠を出勤時間順に取得（早い順）
		List<Attendance> attendances = attendanceRepository.findByWorkDateOrderByClockInAsc(targetDate);

		// 画面側で「現在選択中の日付」を表示するために渡す
		model.addAttribute("selectedDate", targetDate);

		// 勤怠一覧を渡す
		model.addAttribute("attendances", attendances);

		// 表示画面
		return "staff_attendance_manage";
	}
}
