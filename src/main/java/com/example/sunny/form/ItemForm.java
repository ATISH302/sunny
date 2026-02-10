package com.example.sunny.form;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ItemForm {

	private Long id; // 編集時に使う（必要なら）

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotNull
	@Min(0)
	private BigDecimal price;

	@NotNull
	@Min(0)
	private Integer stock;

	@NotBlank
	private String status;

	// ★追加：アップロードされた画像ファイル
	private MultipartFile imageFile;

	// ★追加：既存画像を保持する（編集で画像を変えない時に必要）
	private String imageUrl;

	public ItemForm() {
		this.status = "PUBLIC";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
