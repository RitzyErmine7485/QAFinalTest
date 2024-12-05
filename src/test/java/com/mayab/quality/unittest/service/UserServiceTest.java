package com.mayab.quality.unittest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mayab.quality.dao.IDAOUser;
import com.mayab.quality.model.User;
import com.mayab.quality.service.UserService;

class UserServiceTest {

    private UserService userService;
    private IDAOUser userDaoMock;
    private Map<Integer, User> userDatabase;

    @BeforeEach
    public void setUp() {
        userDaoMock = mock(IDAOUser.class);
        userService = new UserService(userDaoMock);
        userDatabase = new HashMap<>();
    }

    // Test cases for creating a new user
    @Test
    public void testCreateUser_Success() {
        String userName = "testUser";
        String userEmail = "test@example.com";
        String userPassword = "securePass";

        when(userDaoMock.findByEmail(userEmail)).thenReturn(null);
        when(userDaoMock.save(any(User.class))).thenReturn(1);

        User createdUser = userService.createUser(userName, userEmail, userPassword);

        assertThat(createdUser, is(notNullValue()));
        assertThat(createdUser.getEmail(), is(userEmail));
    }

    @Test
    public void testCreateUser_DuplicateEmail() {
        String userName = "duplicateUser";
        String userEmail = "test@example.com";
        String userPassword = "securePass";
        when(userDaoMock.findByEmail(userEmail)).thenReturn(new User("existingUser", userEmail, userPassword));

        User createdUser = userService.createUser(userName, userEmail, userPassword);

        assertThat(createdUser, is(nullValue()));
        verify(userDaoMock, never()).save(any(User.class));
    }

    // Test cases for updating user data
    @Test
    public void testUpdateUser_ValidData() {
        User existingUser = new User("oldUser", "old@example.com", "oldPassword");
        existingUser.setId(1);
        userDatabase.put(1, existingUser);

        User updatedUser = new User("updatedUser", "old@example.com", "newPassword");
        updatedUser.setId(1);

        when(userDaoMock.findById(1)).thenReturn(existingUser);
        when(userDaoMock.updateUser(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            userDatabase.replace(user.getId(), user);
            return user;
        });

        User resultUser = userService.updateUser(updatedUser);

        assertThat(resultUser.getName(), is("updatedUser"));
        assertThat(resultUser.getPassword(), is("newPassword"));
    }

    // Test cases for deleting a user
    @Test
    public void testDeleteUser_Success() {
    	int userId = 1;
        User existingUser = new User("testUser", "test@example.com", "securePass");
        existingUser.setId(userId);
        
        when(userDaoMock.deleteById(userId)).thenReturn(true);
        
        boolean result = userService.deleteUser(userId);

        assertThat(result, is(true));
        
        User deletedUser = userDaoMock.findById(userId);
        assertThat(deletedUser, is(nullValue()));
    }

    // Test cases for finding a user by email
    @Test
    public void testFindUserByEmail_Found() {
        String userEmail = "test@example.com";
        User expectedUser = new User("foundUser", userEmail, "securePass");

        when(userDaoMock.findByEmail(userEmail)).thenReturn(expectedUser);

        User resultUser = userService.findUserByEmail(userEmail);

        assertThat(resultUser, is(notNullValue()));
        assertThat(resultUser.getEmail(), is(userEmail));
    }

    @Test
    public void testFindUserByEmail_NotFound() {
        when(userDaoMock.findByEmail("nonexistent@example.com")).thenReturn(null);

        assertThat(userService.findUserByEmail("nonexistent@example.com"), is(nullValue()));
    }

    // Test cases for finding all users
    @Test
    public void testFindAllUsers_Success() {
        List<User> allUsers = Arrays.asList(
            new User("userOne", "userone@example.com", "passwordOne"),
            new User("userTwo", "usertwo@example.com", "passwordTwo")
        );

        when(userDaoMock.findAll()).thenReturn(allUsers);

        List<User> resultUsers = userService.findAllUsers();

        assertThat(resultUsers, hasSize(2));
        assertThat(resultUsers.get(0).getName(), is("userOne"));
        assertThat(resultUsers.get(1).getName(), is("userTwo"));
    }
}
