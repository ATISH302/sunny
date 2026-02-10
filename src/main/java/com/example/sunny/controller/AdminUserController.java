package com.example.sunny.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sunny.entity.User;
import com.example.sunny.entity.UserStatus;
import com.example.sunny.repository.UserRepository;

// =====================
// 管理者専用：ユーザー管理機能
// =====================
//
// この Controller は、管理者（ADMIN）のみが利用できる
// ユーザー管理機能を提供する。
//
// 主な役割：
// ・ユーザー一覧の表示
// ・ユーザーのステータス変更（ACTIVE / SUSPENDED / BANNED）
//
// @PreAuthorize("hasRole('ADMIN')") により、
// ADMIN 権限を持つユーザーだけがアクセス可能。
// 不正アクセスは Spring Security により自動的にブロックされる。
//

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

	private final UserRepository userRepository;

	// UserRepository を DI（依存性注入）
	public AdminUserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// =====================
	// 管理者：ユーザー一覧表示
	// =====================
	//
	// 機能概要：
	// ・登録されている全ユーザーを一覧表示する
	// ・ユーザーIDの降順で表示（新しいユーザーが上）
	// ・ステータス変更用の select（ACTIVE / SUSPENDED / BANNED）も画面に渡す
	//
	// 使用画面：
	// ・admin_users.html
	//
	@GetMapping("/users")
	public String users(Model model) {

		// 全ユーザーをID降順で取得
		List<User> users = userRepository.findAllByOrderByIdDesc();

		// ユーザー一覧を画面に渡す
		model.addAttribute("users", users);

		// UserStatus（enum）を渡し、selectボックスで使用
		model.addAttribute("statuses", UserStatus.values());

		return "admin_users";
	}

	// =====================
	// 管理者：ユーザーステータス変更
	// =====================
	//
	// 機能概要：
	// ・指定されたユーザーのステータスを変更する
	// ・ACTIVE / SUSPENDED / BANNED を切り替え可能
	// ・変更後はユーザー一覧画面へリダイレクト
	//
	// 想定用途：
	// ・規約違反ユーザーの停止（SUSPENDED）
	// ・悪質ユーザーの永久BAN（BANNED）
	//
	@PostMapping("/users/{id}/status")
	public String changeStatus(
			@PathVariable Long id,
			@RequestParam("status") UserStatus status,
			RedirectAttributes ra) {

		// 対象ユーザーを取得
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません id=" + id));

		// ★★★ ：ADMIN は変更不可 ★★★
		if ("ADMIN".equals(user.getRole())) {
			ra.addFlashAttribute(
					"message",
					"管理者ユーザーのステータスは変更できません。");
			return "redirect:/admin/users";
		}
		// ステータスを更新
		user.setStatus(status);

		// enabled と連動させたい場合は以下を使用（任意）
		// user.setEnabled(status == UserStatus.ACTIVE);

		// DBに保存
		userRepository.save(user);

		// 完了メッセージをリダイレクト先へ渡す
		ra.addFlashAttribute(
				"message",
				"ユーザーID " + id + " のステータスを " + status + " に変更しました");

		return "redirect:/admin/users";
	}
}
