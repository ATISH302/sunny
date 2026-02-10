package com.example.sunny.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sunny.entity.User;
import com.example.sunny.entity.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// ログイン用：名前でユーザーを探す
	Optional<User> findByName(String name);

	// メールでユーザーを探す
	Optional<User> findByEmail(String email);

	// 新規登録チェック用（既に使われてるか）
	boolean existsByName(String name);

	boolean existsByEmail(String email);

	// 管理画面用（一覧・絞り込み用）
	List<User> findAllByOrderByIdDesc();

	List<User> findByStatusOrderByIdDesc(UserStatus status);

	List<User> findByRoleOrderByIdDesc(String role);
}
