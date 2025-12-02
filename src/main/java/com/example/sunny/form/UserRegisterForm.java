package com.example.sunny.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegisterForm {

	@NotBlank(message = "名前を入力してください")
	private String name;

	@NotBlank(message = "メールアドレスを入力してください")
	@Email(message = "メールアドレスの形式が正しくありません")
	private String email;

	@NotBlank(message = "パスワードを入力してください")
	@Size(min = 4, max = 32, message = "パスワードは4〜32文字で入力してください")
	private String password;

	// ▼ getter / setter（右クリック → Source → Generate Getters and Setters でもOK）

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
