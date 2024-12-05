package com.mayab.quality.dao;

import java.util.List;

import com.mayab.quality.model.User;

public interface IDAOUser {
	int save(User u);
	User findByEmail(String email);
	User findByName(String name);
	User findById(int id);
	List<User> findAll();
	User updateUser(User u);
	boolean deleteById(int id);
}