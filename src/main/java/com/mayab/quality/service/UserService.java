package com.mayab.quality.service;

import java.util.List;

import com.mayab.quality.dao.IDAOUser;
import com.mayab.quality.model.User;

public class UserService {

    private IDAOUser dao;

    public UserService(IDAOUser dao) {
        this.dao = dao;
    }

    public User createUser(String name, String email, String password) {
        if (password.length() < 8 || password.length() > 16) {
            return null;
        }

        User existingUser = dao.findByEmail(email);
        if (existingUser != null) {
            return null;
        }

        User newUser = new User(name, email, password);
        int id = dao.save(newUser);
        newUser.setId(id);
        return newUser;
    }

    public List<User> findAllUsers() {
        return dao.findAll();
    }

    public User findUserByEmail(String email) {
        return dao.findByEmail(email);
    }

    public User findUserById(int id) {
        return dao.findById(id);
    }

    public User updateUser(User user) {
        User existingUser = dao.findById(user.getId());
        if (existingUser == null) {
            return null;
        }

        existingUser.setName(user.getName());
        existingUser.setPassword(user.getPassword());
        return dao.updateUser(existingUser);
    }

    public boolean deleteUser(int id) {
        return dao.deleteById(id);
    }
}
