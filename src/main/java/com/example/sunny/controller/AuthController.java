package com.example.sunny.controller;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.sunny.entity.User;
import com.example.sunny.form.UserRegisterForm;
import com.example.sunny.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class AuthController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthController(UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// 🔹 ログイン画面表示
	@GetMapping("/login")
	public String showLogin() {
		return "login"; // login.html
	}

	// 🔹 新規登録フォーム表示
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("userForm", new UserRegisterForm());
		return "register"; // register.html
	}

	// 🔹 新規登録の処理
	@PostMapping("/register")
	public String register(
			@ModelAttribute("userForm") @Valid UserRegisterForm form,
			BindingResult bindingResult) {

		// 名前がかぶっていないかチェック
		if (userRepository.findByName(form.getName()) != null) {
			bindingResult.rejectValue("name", "duplicate", "そのユーザー名は既に使われています");
		}

		// メールがかぶっていないかチェック
		if (userRepository.findByEmail(form.getEmail()) != null) {
			bindingResult.rejectValue("email", "duplicate", "そのメールアドレスは既に登録されています");
		}

		if (bindingResult.hasErrors()) {
			return "register";
		}

		// エンティティに詰める
		User user = new User();
		user.setName(form.getName());
		user.setEmail(form.getEmail());
		user.setPassword(passwordEncoder.encode(form.getPassword())); // ← ここで暗号化
		user.setRole("CUSTOMER"); // 一般ユーザー固定
		user.setEnabled(true);
		user.setCreatedAt(LocalDateTime.now());

		userRepository.save(user);

		// 登録成功 → ログイン画面へ
		return "redirect:/login?registered=true";
	}
}
