package com.example.sunny.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.sunny.entity.User;
import com.example.sunny.entity.UserStatus;
import com.example.sunny.form.UserForm;
import com.example.sunny.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class RegisterController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// =====================
	// 新規会員登録画面（GET）
	// =====================
	@GetMapping("/register")
	public String showRegister(Model model) {
		// ★これが無いと、Thymeleaf が userForm を見つけられず落ちる
		model.addAttribute("userForm", new UserForm());
		return "register";
	}

	// =====================
	// 新規会員登録処理（POST）
	// =====================
	@PostMapping("/register")
	@Transactional
	public String doRegister(
			@Valid UserForm userForm,
			BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			// バリデーションエラー時：同じ画面に戻す（userForm を保持）
			model.addAttribute("userForm", userForm);
			return "register";
		}

		// email重複チェック（任意だけど実用）
		if (userRepository.findByEmail(userForm.getEmail()).isPresent()) {
			model.addAttribute("userForm", userForm);
			model.addAttribute("errorMessage", "このメールアドレスは既に登録されています。");
			return "register";
		}

		// 登録
		User user = new User();
		user.setName(userForm.getName());
		user.setEmail(userForm.getEmail());
		user.setPassword(passwordEncoder.encode(userForm.getPassword()));

		// ここはあなたのUser設計に合わせて：
		// roleが String なら "CUSTOMER"、enumなら適宜変更
		user.setRole("CUSTOMER");
		user.setStatus(UserStatus.ACTIVE);

		userRepository.save(user);

		// 登録後はログインへ
		return "redirect:/login";
	}
}
