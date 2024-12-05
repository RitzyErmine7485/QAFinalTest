package com.mayab.quality;

import com.mayab.quality.dao.MySqlDAOUser;
import com.mayab.quality.model.User;

public class App 
{
    public static void main( String[] args )
    {
    	User u = new User("test", "email@mail.com", "securePass");
    	MySqlDAOUser dao = new MySqlDAOUser();
    	dao.save(u);
    }
}
