package com.mayab.quality.integration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.mayab.quality.service.UserService;
import com.mayab.quality.dao.MySqlDAOUser;
import com.mayab.quality.model.User;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class MySqlUserServiceTest {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        MySqlDAOUser daoUser = new MySqlDAOUser();
        userService = new UserService(daoUser);
    }

    // Create user tests
    @Test
    public void test1_CreateUser_Success() {
        User newUser = userService.createUser("newUser", "new@example.com", "newPass123");
        assertNotNull(newUser);
        assertTrue(newUser.getId() > 0);
    }

    @Test
    public void test2_CreateUser_DuplicateEmail() {
    	userService.createUser("newUser", "existing@example.com", "newPass123");
        User result = userService.createUser("anotherUser", "existing@example.com", "diffPass123");
        assertNull(result);
    }

    @Test
    public void test3_CreateUser_InvalidPassword() {
        assertNull(userService.createUser("shortUser", "shortpass@example.com", "123")); 
        assertNull(userService.createUser("longUser", "longpass@example.com", "toolongpassword123456"));
    }

    // Update user test
    @Test
    public void test4_UpdateUser() {
        User existingUser = userService.findUserById(1);
        existingUser.setName("updName");
        existingUser.setPassword("updPass123");

        User updatedUser = userService.updateUser(existingUser);
        assertNotNull(updatedUser);
        assertEquals("updName", updatedUser.getName());
        assertEquals("updPass123", updatedUser.getPassword());
    }

    // Delete user test
    @Test
    public void test5_DeleteUser() {
        boolean deleted = userService.deleteUser(1);
        assertTrue(deleted);
        assertNull(userService.findUserById(1));
    }

    // Find user tests
    @Test
    public void test6_FindAllUsers() {
        List<User> allUsers = userService.findAllUsers();
        assertFalse(allUsers.isEmpty());
    }

    @Test
    public void test7_FindUserByEmail() {
        User foundUser = userService.findUserByEmail("existing@example.com");
        assertNotNull(foundUser);
        assertEquals("existing@example.com", foundUser.getEmail());
    }
    
    @AfterAll
    public static void tearDownOnce() {
    	MySqlDAOUser daoUser = new MySqlDAOUser();
    	daoUser.clearTable();
    }
}
