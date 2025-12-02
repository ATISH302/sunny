package com.example.sunny.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.sunny.entity.Item;
import com.example.sunny.repository.ItemRepository;

@Controller
public class HomeController {

	private final ItemRepository itemRepository;

	public HomeController(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@GetMapping("/")
	public String home(Model model) {
		// 公開状態の商品だけ取得（PUBLIC）
		List<Item> items = itemRepository.findByStatus("PUBLIC");
		model.addAttribute("items", items);
		return "home";
	}
}
