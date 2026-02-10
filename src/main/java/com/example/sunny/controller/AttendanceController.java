package com.example.sunny.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sunny.entity.Attendance;
import com.example.sunny.entity.User;
import com.example.sunny.repository.AttendanceRepository;
import com.example.sunny.repository.UserRepository;

/*
 * =====================
 * スタッフ勤怠管理コントローラー
 * =====================
 *
 * このControllerは、スタッフ用の勤怠管理機能を担当する。
 * 主に以下の操作を提供する：
 *
 * ・出勤打刻
 * ・退勤打刻
 * ・休憩開始
 * ・休憩終了
 *
 * URLの先頭が /staff で始まるため、
 * SecurityConfig により STAFF または ADMIN 権限のユーザーのみが利用可能。
 */

@Controller
@RequestMapping("/staff")
public class AttendanceController {

	private final AttendanceRepository attendanceRepository;
	private final UserRepository userRepository;

	public AttendanceController(AttendanceRepository attendanceRepository, UserRepository userRepository) {
		this.attendanceRepository = attendanceRepository;
		this.userRepository = userRepository;
	}

	/*
	 * =====================
	 * 共通処理：ログイン中ユーザーの取得
	 * =====================
	 *
	 * Spring Security から渡される Authentication には
	 * ログインID（email）が入っている。
	 *
	 * 1. email で users テーブルを検索
	 * 2. 見つからなければ name でも検索
	 * 3. ログインユーザーの User エンティティを返す
	 *
	 * 各勤怠処理で毎回同じ取得処理を書くのを防ぐため、
	 * 共通メソッドとして切り出している。
	 */
	private User getLoginUser(Authentication authentication) {
		String username = authentication.getName(); // 通常は email

		return userRepository.findByEmail(username)
				.orElseGet(() -> userRepository.findByName(username)
						.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません name=" + username)));
	}

	/*
	 * =====================
	 * 勤怠画面表示
	 * =====================
	 *
	 * 本日の勤怠データを取得し、画面に表示する。
	 *
	 * 処理の流れ：
	 * 1. ログイン中のユーザーを取得
	 * 2. 今日の日付を取得
	 * 3. 「ユーザー＋日付」で勤怠データを検索
	 * 4. あれば画面に渡す（なければ null）
	 */
	@GetMapping("/attendance")
	public String showAttendance(Authentication authentication, Model model) {

		User user = getLoginUser(authentication);

		LocalDate today = LocalDate.now();
		Attendance todayAttendance = attendanceRepository
				.findByUserAndWorkDate(user, today)
				.orElse(null);

		model.addAttribute("attendance", todayAttendance);
		return "staff_attendance";
	}

	/*
	 * =====================
	 * 出勤打刻
	 * =====================
	 *
	 * 本日の勤怠レコードが存在しなければ新規作成する。
	 * すでに存在する場合は何もしない。
	 */
	@PostMapping("/attendance/clockin")
	public String clockIn(Authentication authentication) {

		User user = getLoginUser(authentication);
		LocalDate today = LocalDate.now();

		attendanceRepository.findByUserAndWorkDate(user, today)
				.orElseGet(() -> attendanceRepository.save(new Attendance(user, today)));

		return "redirect:/staff/attendance";
	}

	/*
	 * =====================
	 * 退勤打刻
	 * =====================
	 *
	 * 本日の勤怠データが存在する場合のみ退勤時刻を記録する。
	 * 出勤していない場合はエラーとする。
	 */
	@PostMapping("/attendance/clockout")
	public String clockOut(Authentication authentication) {

		User user = getLoginUser(authentication);
		LocalDate today = LocalDate.now();

		Attendance att = attendanceRepository
				.findByUserAndWorkDate(user, today)
				.orElseThrow(() -> new IllegalArgumentException("本日の出勤がまだありません"));

		att.setClockOut(LocalDateTime.now());
		attendanceRepository.save(att);

		return "redirect:/staff/attendance";
	}

	/*
	 * =====================
	 * 休憩開始
	 * =====================
	 *
	 * ・退勤後は休憩開始できない
	 * ・すでに休憩中の場合は何もしない
	 */
	@PostMapping("/attendance/breakstart")
	public String breakStart(Authentication authentication) {

		User user = getLoginUser(authentication);
		LocalDate today = LocalDate.now();

		Attendance att = attendanceRepository
				.findByUserAndWorkDate(user, today)
				.orElseThrow(() -> new IllegalArgumentException("本日の出勤がまだありません"));

		if (att.getClockOut() != null) {
			return "redirect:/staff/attendance";
		}

		if (att.getBreakStart() == null) {
			att.setBreakStart(LocalDateTime.now());
			att.setBreakEnd(null); // 新しい休憩のため初期化
			attendanceRepository.save(att);
		}

		return "redirect:/staff/attendance";
	}

	/*
	 * =====================
	 * 休憩終了
	 * =====================
	 *
	 * ・休憩開始していない場合は何もしない
	 * ・すでに休憩終了済みの場合も何もしない
	 */
	@PostMapping("/attendance/breakend")
	public String breakEnd(Authentication authentication) {

		User user = getLoginUser(authentication);
		LocalDate today = LocalDate.now();

		Attendance att = attendanceRepository
				.findByUserAndWorkDate(user, today)
				.orElseThrow(() -> new IllegalArgumentException("本日の出勤がまだありません"));

		if (att.getBreakStart() == null) {
			return "redirect:/staff/attendance";
		}

		if (att.getBreakEnd() == null) {
			att.setBreakEnd(LocalDateTime.now());
			attendanceRepository.save(att);
		}

		return "redirect:/staff/attendance";
	}
}
