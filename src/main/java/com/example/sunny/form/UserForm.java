package com.example.sunny.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserForm {

	@NotBlank(message = "名前は必須です")
	private String name;

	@NotBlank(message = "メールアドレスは必須です")
	@Email(message = "メール形式で入力してください")
	private String email;

	@NotBlank(message = "パスワードは必須です")
	@Size(min = 4, message = "パスワードは4文字以上にしてください")
	private String password;

	// getter / setter
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
