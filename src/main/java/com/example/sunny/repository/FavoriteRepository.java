package com.example.sunny.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sunny.entity.Favorite;
import com.example.sunny.entity.Item;
import com.example.sunny.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	// そのユーザーのお気に入り一覧
	List<Favorite> findByUserOrderByCreatedAtDesc(User user);

	// 既にお気に入り登録されているかチェック用
	boolean existsByUserAndItem(User user, Item item);

	// 削除用
	void deleteByUserAndItem(User user, Item item);
}
