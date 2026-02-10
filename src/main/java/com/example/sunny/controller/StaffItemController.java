package com.example.sunny.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sunny.entity.Item;
import com.example.sunny.form.ItemForm;
import com.example.sunny.repository.ItemRepository;
import com.example.sunny.service.UploadService;

import jakarta.validation.Valid;

// =====================
// スタッフ機能：商品管理（登録・編集・一覧・削除）
// =====================

@Controller
@RequestMapping("/staff/items")
public class StaffItemController {

	private final ItemRepository itemRepository;
	private final UploadService uploadService;

	public StaffItemController(ItemRepository itemRepository, UploadService uploadService) {
		this.itemRepository = itemRepository;
		this.uploadService = uploadService;
	}

	// =====================
	// スタッフ：商品一覧表示
	// =====================
	@GetMapping
	public String staffItems(Model model) {
		List<Item> items = itemRepository.findAll();
		model.addAttribute("items", items);
		return "staff_items";
	}

	// =====================
	// スタッフ：商品新規登録画面
	// =====================
	@GetMapping("/new")
	public String newItem(Model model) {
		model.addAttribute("itemForm", new ItemForm());
		model.addAttribute("items", itemRepository.findAll());
		return "staff_item_new";
	}

	// =====================
	// スタッフ：商品新規登録処理
	// =====================
	@PostMapping
	public String create(
			@Valid @ModelAttribute("itemForm") ItemForm form,
			BindingResult result,
			Model model,
			RedirectAttributes ra) {

		if (result.hasErrors()) {
			model.addAttribute("items", itemRepository.findAll());
			return "staff_item_new";
		}

		Item item = new Item();
		item.setName(form.getName());
		item.setDescription(form.getDescription());
		item.setPrice(form.getPrice());
		item.setStock(form.getStock());
		item.setStatus(form.getStatus());

		String savedPath = uploadService.save(form.getImageFile());
		if (savedPath != null) {
			item.setImageUrl(savedPath);
		}

		itemRepository.save(item);

		ra.addFlashAttribute("successMessage", "商品を登録しました。");
		return "redirect:/staff/items";
	}

	// =====================
	// スタッフ：商品編集画面
	// =====================
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable Long id, Model model) {

		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("商品が見つかりません id=" + id));

		ItemForm form = new ItemForm();
		form.setId(item.getId());
		form.setName(item.getName());
		form.setDescription(item.getDescription());
		form.setPrice(item.getPrice());
		form.setStock(item.getStock());
		form.setStatus(item.getStatus());
		form.setImageUrl(item.getImageUrl());

		model.addAttribute("itemId", item.getId());
		model.addAttribute("itemForm", form);

		return "staff_item_edit";
	}

	// =====================
	// スタッフ：商品編集更新処理
	// =====================
	@PostMapping("/{id}/edit")
	public String update(
			@PathVariable Long id,
			@Valid @ModelAttribute("itemForm") ItemForm form,
			BindingResult result,
			Model model,
			RedirectAttributes ra) {

		if (result.hasErrors()) {
			model.addAttribute("itemId", id);
			return "staff_item_edit";
		}

		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("商品が見つかりません id=" + id));

		item.setName(form.getName());
		item.setDescription(form.getDescription());
		item.setPrice(form.getPrice());
		item.setStock(form.getStock());
		item.setStatus(form.getStatus());

		String savedPath = uploadService.save(form.getImageFile());
		if (savedPath != null) {
			item.setImageUrl(savedPath);
		} else {
			item.setImageUrl(form.getImageUrl());
		}

		itemRepository.save(item);

		ra.addFlashAttribute("successMessage", "商品ID " + id + " を更新しました。");
		return "redirect:/staff/items";
	}

	// =====================
	// スタッフ：商品削除処理
	// =====================
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes ra) {

		if (!itemRepository.existsById(id)) {
			ra.addFlashAttribute("errorMessage", "商品が見つかりませんでした。（ID: " + id + "）");
			return "redirect:/staff/items";
		}

		itemRepository.deleteById(id);
		ra.addFlashAttribute("successMessage", "商品ID " + id + " を削除しました。");

		return "redirect:/staff/items";
	}
}
