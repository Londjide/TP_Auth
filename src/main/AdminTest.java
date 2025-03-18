package main;

import static org.junit.Assert.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AdminTest {
	private static final String TEST_DB_URL = "jdbc:sqlite:test_users.db";
    private static Connection conn;

    // Helper method to establish a connection to the database
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(TEST_DB_URL);
    }

    // Set up a fresh database for each test
    @Before
    public static void setup() throws SQLException {
        conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL, password TEXT NOT NULL)");
        }
    }

    @After
    public static void tearDown() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
        }
        conn.close();
    }

    @Before
    public void cleanDatabase() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        }
    }

    // Test Create User
    @Test
    public void testCreateUser() throws SQLException {
        String email = "testuser@example.com";
        String password = "Test@12345";

        // Create a new user
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?)")) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            int rowsAffected = pstmt.executeUpdate();
            assertEquals(1, rowsAffected);

            // Verify the user is inserted
            try (PreparedStatement pstmtCheck = conn.prepareStatement("SELECT email, password FROM users WHERE email = ?")) {
                pstmtCheck.setString(1, email);
                ResultSet rs = pstmtCheck.executeQuery();
                assertTrue(rs.next());
                assertEquals(email, rs.getString("email"));
                assertEquals(password, rs.getString("password"));
            }
        }
    }

    // Test Update User
    @Test
    public void testUpdateUser() throws SQLException {
        String email = "testuser@example.com";
        String password = "Test@12345";

        // Insert a user
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?)")) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }

        // Update the user's email and password
        String newEmail = "updateduser@example.com";
        String newPassword = "Updated@12345";
        try (PreparedStatement pstmtUpdate = conn.prepareStatement("UPDATE users SET email = ?, password = ? WHERE email = ?")) {
            pstmtUpdate.setString(1, newEmail);
            pstmtUpdate.setString(2, newPassword);
            pstmtUpdate.setString(3, email);
            int rowsAffected = pstmtUpdate.executeUpdate();
            assertEquals(1, rowsAffected);
        }

        // Verify the user is updated
        try (PreparedStatement pstmtCheck = conn.prepareStatement("SELECT email, password FROM users WHERE email = ?")) {
            pstmtCheck.setString(1, newEmail);
            ResultSet rs = pstmtCheck.executeQuery();
            assertTrue(rs.next());
            assertEquals(newEmail, rs.getString("email"));
            assertEquals(newPassword, rs.getString("password"));
        }
    }

    // Test Delete User
    @Test
    public void testDeleteUser() throws SQLException {
        String email = "testuser@example.com";
        String password = "Test@12345";

        // Insert a user
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?)")) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }

        // Delete the user
        try (PreparedStatement pstmtDelete = conn.prepareStatement("DELETE FROM users WHERE email = ?")) {
            pstmtDelete.setString(1, email);
            int rowsAffected = pstmtDelete.executeUpdate();
            assertEquals(1, rowsAffected);
        }

        // Verify the user is deleted
        try (PreparedStatement pstmtCheck = conn.prepareStatement("SELECT email FROM users WHERE email = ?")) {
            pstmtCheck.setString(1, email);
            ResultSet rs = pstmtCheck.executeQuery();
            assertFalse(rs.next());
        }
    }

    // Test Read User (Find User by ID)
    @Test
    public void testReadUserById() throws SQLException {
        String email = "testuser@example.com";
        String password = "Test@12345";

        // Insert a user
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?)")) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }

        // Retrieve user by email
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT id, email, password FROM users WHERE email = ?")) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(email, rs.getString("email"));
            assertEquals(password, rs.getString("password"));
        }
    }

}
