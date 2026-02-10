package com.example.sunny.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sunny.entity.Report;
import com.example.sunny.repository.ReportRepository;

// ==============================
// スタッフ専用：レビュー通報管理機能
// ==============================
//
// この Controller は、レビューに対する「通報」を
// スタッフが確認・対応するための管理機能を提供する。
//
// 主な役割：
// ・通報一覧の表示
// ・通報詳細の確認
// ・対応済み（DONE）への変更
// ・対応状況を NEW に戻す
//
// 想定利用者：
// ・スタッフ / 管理者
//
// 通報は Review に紐づいており、
// 同一レビューに対する通報数も確認できるようにしている。
//

@Controller
@RequestMapping("/staff")
public class StaffReportController {

	private final ReportRepository reportRepository;

	// ReportRepository を DI（依存性注入）
	public StaffReportController(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}

	// ==============================
	// 通報一覧画面
	// ==============================
	//
	// 機能概要：
	// ・すべての通報を新しい順に取得
	// ・同じレビューに対する通報件数を集計
	// ・通報一覧画面に表示
	//
	// 使用画面：
	// ・staff_reports.html
	//
	@GetMapping("/reports")
	public String showReports(Model model) {

		// 通報を作成日時の降順で取得
		List<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc();

		// reviewId → 通報数 を保持する Map
		Map<Long, Long> reportCounts = new HashMap<>();

		for (Report rep : reports) {

			// ★重要：review が null の場合に備えた null チェック
			// データ不整合や削除済みレビュー対策
			if (rep.getReview() == null || rep.getReview().getId() == null) {
				continue;
			}

			Long reviewId = rep.getReview().getId();

			// 同一レビューに対する通報数をカウント
			reportCounts.computeIfAbsent(
					reviewId,
					id -> reportRepository.countByReviewId(id));
		}

		// 画面に渡す
		model.addAttribute("reports", reports);
		model.addAttribute("reportCounts", reportCounts);

		return "staff_reports";
	}

	// ==============================
	// 通報詳細画面
	// ==============================
	//
	// 機能概要：
	// ・指定された通報IDの詳細を表示
	// ・同じレビューに対する通報総数も表示
	//
	// 使用画面：
	// ・staff_report_detail.html
	//
	@GetMapping("/reports/{id}")
	public String showReportDetail(@PathVariable Long id, Model model) {

		// 通報を取得
		Report report = reportRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("通報が見つかりません id=" + id));

		long sameReviewCount = 0;

		// review が存在する場合のみ通報数をカウント
		if (report.getReview() != null && report.getReview().getId() != null) {
			sameReviewCount = reportRepository.countByReviewId(report.getReview().getId());
		}

		model.addAttribute("report", report);
		model.addAttribute("sameReviewCount", sameReviewCount);

		return "staff_report_detail";
	}

	// ==============================
	// 通報を「対応済み（DONE）」にする
	// ==============================
	//
	// 機能概要：
	// ・通報のステータスを DONE に変更
	// ・対応完了として管理
	//
	@PostMapping("/reports/{id}/done")
	public String markDone(@PathVariable Long id) {

		Report report = reportRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("通報が見つかりません id=" + id));

		report.setStatus("DONE");
		reportRepository.save(report);

		return "redirect:/staff/reports";
	}

	// ==============================
	// 通報を「未対応（NEW）」に戻す
	// ==============================
	//
	// 機能概要：
	// ・誤って DONE にした通報を再対応状態に戻す
	//
	@PostMapping("/reports/{id}/reopen")
	public String markNew(@PathVariable Long id) {

		Report report = reportRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("通報が見つかりません id=" + id));

		report.setStatus("NEW");
		reportRepository.save(report);

		return "redirect:/staff/reports";
	}
}
