package com.example.sunny.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sunny.entity.Item;
import com.example.sunny.entity.Report;
import com.example.sunny.entity.Review;
import com.example.sunny.entity.User;
import com.example.sunny.repository.ItemRepository;
import com.example.sunny.repository.ReportRepository;
import com.example.sunny.repository.ReviewRepository;
import com.example.sunny.repository.UserRepository;

// =====================
// レビュー機能：投稿・編集・削除・通報
// =====================
//
// 目的：
// ・商品にレビュー（星1〜5＋コメント）を投稿できる
// ・レビューは「1商品につき1回だけ」投稿できる
// ・自分のレビューだけ編集・削除できる
// ・他人のレビューを通報できる（自分のレビューは通報不可）
// ・二重通報も防止できる
//
// ポイント：
// ・ログインユーザーは email 優先で取得（なければ name）
// ・@Transactional で DB処理をまとめて実行し安全にする
//
@Controller
public class ReviewController {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final ReportRepository reportRepository;

	public ReviewController(
			ReviewRepository reviewRepository,
			UserRepository userRepository,
			ItemRepository itemRepository,
			ReportRepository reportRepository) {
		this.reviewRepository = reviewRepository;
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
		this.reportRepository = reportRepository;
	}

	// =====================
	// ログインユーザー取得（email優先 → name）
	// =====================
	private User getLoginUser(UserDetails loginUser) {
		if (loginUser == null) {
			throw new IllegalArgumentException("ログインユーザー情報が取得できません");
		}

		String username = loginUser.getUsername(); // email のことが多い
		return userRepository.findByEmail(username)
				.orElseGet(() -> userRepository.findByName(username)
						.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません name=" + username)));
	}

	// =====================
	// レビュー投稿（1商品1回）
	// =====================
	@PostMapping("/reviews/add/{itemId}")
	@Transactional
	public String addReview(
			@PathVariable Long itemId,
			@RequestParam int rating,
			@RequestParam(required = false) String comment,
			@AuthenticationPrincipal UserDetails loginUser,
			RedirectAttributes ra) {

		// 評価値チェック
		if (rating < 1 || rating > 5) {
			ra.addFlashAttribute("reviewMessage", "評価は1〜5で入力してください");
			return "redirect:/items/" + itemId;
		}

		User user = getLoginUser(loginUser);

		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("商品が見つかりません id=" + itemId));

		// 同じ商品に二重投稿できないように制御
		if (reviewRepository.existsByUserAndItem(user, item)) {
			ra.addFlashAttribute("reviewMessage", "この商品はすでにレビュー済みです");
			return "redirect:/items/" + itemId;
		}

		// レビュー登録
		Review review = new Review();
		review.setUser(user);
		review.setItem(item);
		review.setRating(rating);
		review.setComment(comment);
		review.setCreatedAt(LocalDateTime.now());
		reviewRepository.save(review);

		ra.addFlashAttribute("reviewMessage", "レビューを投稿しました");
		return "redirect:/items/" + itemId;
	}

	// =====================
	// レビュー編集画面表示（自分のレビューのみ）
	// =====================
	@GetMapping("/reviews/edit/{reviewId}")
	public String editReviewForm(
			@PathVariable Long reviewId,
			@AuthenticationPrincipal UserDetails loginUser,
			Model model,
			RedirectAttributes ra) {

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("レビューが見つかりません id=" + reviewId));

		User user = getLoginUser(loginUser);

		// 自分のレビュー以外は編集できない
		if (!review.getUser().getId().equals(user.getId())) {
			ra.addFlashAttribute("reviewMessage", "自分のレビュー以外は編集できません");
			return "redirect:/items/" + review.getItem().getId();
		}

		model.addAttribute("review", review);
		return "review_edit";
	}

	// =====================
	// レビュー更新（自分のレビューのみ）
	// =====================
	@PostMapping("/reviews/update/{reviewId}")
	@Transactional
	public String updateReview(
			@PathVariable Long reviewId,
			@RequestParam int rating,
			@RequestParam(required = false) String comment,
			@AuthenticationPrincipal UserDetails loginUser,
			RedirectAttributes ra) {

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("レビューが見つかりません id=" + reviewId));
		Long itemId = review.getItem().getId();

		User user = getLoginUser(loginUser);

		// 自分のレビュー以外は更新できない
		if (!review.getUser().getId().equals(user.getId())) {
			ra.addFlashAttribute("reviewMessage", "自分のレビュー以外は編集できません");
			return "redirect:/items/" + itemId;
		}

		// 評価チェック
		if (rating < 1 || rating > 5) {
			ra.addFlashAttribute("reviewMessage", "評価は1〜5で入力してください");
			return "redirect:/reviews/edit/" + reviewId;
		}

		review.setRating(rating);
		review.setComment(comment);
		reviewRepository.save(review);

		ra.addFlashAttribute("reviewMessage", "レビューを更新しました");
		return "redirect:/items/" + itemId;
	}

	// =====================
	// 自分のレビュー削除
	// =====================
	@PostMapping("/reviews/delete/{reviewId}")
	@Transactional
	public String deleteMyReview(
			@PathVariable Long reviewId,
			@AuthenticationPrincipal UserDetails loginUser,
			RedirectAttributes ra) {

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("レビューが見つかりません id=" + reviewId));
		Long itemId = review.getItem().getId();

		User user = getLoginUser(loginUser);

		// 自分のレビュー以外は削除できない
		if (!review.getUser().getId().equals(user.getId())) {
			ra.addFlashAttribute("reviewMessage", "自分のレビュー以外は削除できません");
			return "redirect:/items/" + itemId;
		}

		reviewRepository.delete(review);
		ra.addFlashAttribute("reviewMessage", "レビューを削除しました");
		return "redirect:/items/" + itemId;
	}

	// =====================
	// 通報確認ページ（GET）
	// =====================
	@GetMapping("/reviews/report/{reviewId}")
	public String showReportPage(
			@PathVariable Long reviewId,
			@AuthenticationPrincipal UserDetails loginUser,
			Model model,
			RedirectAttributes ra) {

		User user = getLoginUser(loginUser);

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("レビューが見つかりません id=" + reviewId));

		// 自分のレビューは通報できない
		if (review.getUser().getId().equals(user.getId())) {
			ra.addFlashAttribute("reviewMessage", "自分のレビューは通報できません。");
			return "redirect:/items/" + review.getItem().getId();
		}

		// 二重通報防止
		if (reportRepository.existsByReviewAndReportedBy(review, user)) {
			ra.addFlashAttribute("reviewMessage", "このレビューは既に通報済みです。");
			return "redirect:/items/" + review.getItem().getId();
		}

		model.addAttribute("review", review);
		model.addAttribute("itemId", review.getItem().getId());

		return "review_report";
	}

	// =====================
	// レビュー通報（POST）
	// =====================
	@PostMapping("/reviews/report/{reviewId}")
	@Transactional
	public String reportReview(
			@PathVariable Long reviewId,
			@RequestParam("reason") String reason,
			@AuthenticationPrincipal UserDetails loginUser,
			RedirectAttributes ra) {

		User user = getLoginUser(loginUser);

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("レビューが見つかりません id=" + reviewId));

		// 自分のレビューは通報不可
		if (review.getUser().getId().equals(user.getId())) {
			ra.addFlashAttribute("reviewMessage", "自分のレビューは通報できません。");
			return "redirect:/items/" + review.getItem().getId();
		}

		// 二重通報防止
		if (reportRepository.existsByReviewAndReportedBy(review, user)) {
			ra.addFlashAttribute("reviewMessage", "このレビューは既に通報済みです。");
			return "redirect:/items/" + review.getItem().getId();
		}

		// 通報データ作成
		Report rep = new Report();
		rep.setReview(review);
		rep.setReportedBy(user);
		rep.setReason(reason);
		rep.setCreatedAt(LocalDateTime.now());
		rep.setStatus("NEW");

		reportRepository.save(rep);

		ra.addFlashAttribute("reviewMessage", "通報を受け付けました。");
		return "redirect:/items/" + review.getItem().getId();
	}
}
