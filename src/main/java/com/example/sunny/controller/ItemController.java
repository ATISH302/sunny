package com.example.sunny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.sunny.entity.Item;
import com.example.sunny.repository.ItemRepository;

@Controller
public class ItemController {

	private final ItemRepository itemRepository;

	public ItemController(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	// 商品詳細ページ
	@GetMapping("/items/{id}")
	public String showItemDetail(@PathVariable Long id, Model model) {

		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("商品が見つかりません id=" + id));

		model.addAttribute("item", item);
		return "item_detail"; // → item_detail.html を表示
	}
}
