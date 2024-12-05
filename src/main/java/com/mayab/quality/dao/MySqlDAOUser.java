package com.mayab.quality.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mayab.quality.model.User;

public class MySqlDAOUser implements IDAOUser {
	
	public Connection getConnectionMySQL() {

		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Quality", "root", "123456");
		} catch (Exception e) {
			System.out.println(e);
		}
		return conn;
	}

	@Override
	public int save(User user) {
	    Connection connection = getConnectionMySQL();
	    int result = -1;

	    if (user.getPassword().length() < 8 || user.getPassword().length() > 20) {
	        return -1;
	    }

	    try {
	        PreparedStatement checkEmailStmt = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?");
	        checkEmailStmt.setString(1, user.getEmail());
	        ResultSet rs = checkEmailStmt.executeQuery();

	        if (rs.next() && rs.getInt(1) > 0) {
	            System.out.println("Error: The email is already registered.");
	            return -1;
	        }

	        PreparedStatement preparedStatement = connection.prepareStatement(
	            "INSERT INTO users(name, email, password, isLogged) VALUES(?,?,?,?)", 
	            Statement.RETURN_GENERATED_KEYS);

	        preparedStatement.setString(1, user.getName());
	        preparedStatement.setString(2, user.getEmail());
	        preparedStatement.setString(3, user.getPassword());
	        preparedStatement.setBoolean(4, user.isLogged());

	        if (preparedStatement.executeUpdate() >= 1) {
	            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    result = generatedKeys.getInt(1);
	                }
	            }
	        }

	        connection.close();
	        preparedStatement.close();

	    } catch (Exception e) {
	        System.out.println(e);
	    }
	    return result;
	}

	
	@Override
	public User updateUser(User userNew) {
	    Connection connection = getConnectionMySQL();
	    User result = null;

	    try {
	        PreparedStatement preparedStatement;
	        preparedStatement = connection.prepareStatement("UPDATE users SET name = ?, password = ? WHERE id = ?");

	        preparedStatement.setString(1, userNew.getName());
	        preparedStatement.setString(2, userNew.getPassword());
	        preparedStatement.setInt(3, userNew.getId());

	        if (preparedStatement.executeUpdate() > 0) {
	            result = userNew;
	        }

	        connection.close();
	        preparedStatement.close();

	    } catch (Exception e) {
	        System.out.println(e);
	    }

	    return result;
	}
	
	@Override
	public boolean deleteById(int id) {
		Connection connection = getConnectionMySQL();
		boolean result = false;

		try {
			PreparedStatement preparedStatement;
			preparedStatement = connection.prepareStatement("DELETE from users WHERE id = ?");

			preparedStatement.setInt(1, id);


			if (preparedStatement.executeUpdate() >= 1) {
				result = true;
			}
			System.out.println("\n");
			System.out.println("User delted succesfully");
			System.out.println(">> Return: " + result + "\n");

			connection.close();
			preparedStatement.close();

		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

	@Override
	public List<User> findAll() {
		Connection connection = getConnectionMySQL();
		  PreparedStatement preparedStatement;
		  ResultSet rs;

		  User retrieved = null;

		  List<User> listaAlumnos = new ArrayList<User>();
		  
		  try {
		   preparedStatement = connection.prepareStatement("SELECT * from users");
		   rs = preparedStatement.executeQuery();

		   while (rs.next()) {

			   int id = rs.getInt(1);
			   String name = rs.getString(2);
			   String email = rs.getString(3);
			   String password = rs.getString(4);
			   boolean log = rs.getBoolean(5);		 
			   retrieved = new User(name, email,password);
			   retrieved.setId(id);
			   retrieved.setLogged(log);
			   listaAlumnos.add(retrieved);
		   }
		   
			   connection.close();
			   rs.close();
			   preparedStatement.close();
	
			  } catch (Exception e) {
			   System.out.println(e);
			  }
			  return listaAlumnos;
	}
	
	@Override
	public User findByEmail(String email) {
	    Connection connection = getConnectionMySQL();
	    PreparedStatement preparedStatement;
	    ResultSet rs;

	    User result = null;

	    try {
	        preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
	        preparedStatement.setString(1, email);
	        rs = preparedStatement.executeQuery();

	        if (rs.next()) {
	            int id = rs.getInt("id");
	            String name = rs.getString("name");
	            String mail = rs.getString("email");
	            String password = rs.getString("password");
	            boolean isLogged = rs.getBoolean("isLogged");

	            result = new User(name, mail, password);
	            result.setId(id);
	            result.setLogged(isLogged);
	        } else {
	            System.out.println("No user found with the provided email.");
	        }

	        connection.close();
	        rs.close();
	        preparedStatement.close();

	    } catch (Exception e) {
	        System.out.println(e);
	    }

	    return result;
	}

	@Override
	public User findById(int id) {
		Connection connection = getConnectionMySQL();
		PreparedStatement preparedStatement;
		ResultSet rs;
		
		User result = null;

		try {
			preparedStatement = connection.prepareStatement("SELECT * from users WHERE id = ?");
			preparedStatement.setInt(1, id);
			rs = preparedStatement.executeQuery();
			rs.next();

			String username  = rs.getString(2);
			String email = rs.getString(3);
			String password = rs.getString(4);
			boolean isLogged = rs.getBoolean(5);

			result = new User(username, password, email);
			result.setId(id);
			result.setLogged(isLogged);

			System.out.println("\n");
			System.out.println("---User---");
			System.out.println("ID: " + result.getId());
			System.out.println("Name: " + result.getName());
			System.out.println("Email: " + result.getEmail());
			System.out.println("isLogged: " + result.isLogged() + "\n");
			
			connection.close();
			rs.close();
			preparedStatement.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		
		return result;
	}

	@Override
	public User findByName(String name) {
	    Connection connection = getConnectionMySQL();
	    PreparedStatement preparedStatement;
	    ResultSet rs;

	    User result = null;

	    try {
	        preparedStatement = connection.prepareStatement("SELECT * from users WHERE name = ?");
	        preparedStatement.setString(1, name);
	        rs = preparedStatement.executeQuery();

	        if (rs.next()) {
	            int id = rs.getInt("id");
	            String username = rs.getString("name");
	            String email = rs.getString("email");
	            String password = rs.getString("password");
	            boolean isLogged = rs.getBoolean("isLogged");

	            result = new User(username, password, email);
	            result.setId(id);
	            result.setLogged(isLogged);

	            System.out.println("\n---User---");
	            System.out.println("ID: " + result.getId());
	            System.out.println("Name: " + result.getName());
	            System.out.println("Email: " + result.getEmail());
	            System.out.println("isLogged: " + result.isLogged() + "\n");
	        }

	        connection.close();
	        rs.close();
	        preparedStatement.close();

	    } catch (Exception e) {
	        System.out.println(e);
	    }

	    return result;
	}
	
	public void clearTable() {
		Connection connection = getConnectionMySQL();
		if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("TRUNCATE TABLE users");
                System.out.println("Table truncated after tests.");
            } catch (Exception e) {
                System.out.println("Error truncating table: " + e.getMessage());
            }
        }
	}
}