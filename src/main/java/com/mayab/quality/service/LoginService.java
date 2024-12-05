package com.mayab.quality.service;

import com.mayab.quality.dao.IDAOUser;
import com.mayab.quality.model.User;

public class LoginService {
	IDAOUser dao;
	
	public LoginService(IDAOUser d) {
		dao = d;
	}
	
	public boolean login(String email, String pass) {
		User u = dao.findByEmail(email);
		if(u != null) {
			if(u.getPassword() == pass) {
				u.setLogged(true);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
}
