package org.example.model;

import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CardTest {
    private Connection connection;
    private Card card;

    @BeforeAll
    public void setup() throws SQLException {
        String jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String username = "sa";
        String password = "";

        connection = DriverManager.getConnection(jdbcUrl, username, password);
        System.out.println("H2 Database connected!");

        try (Statement stmt = connection.createStatement()) {
            String createTableSql = "CREATE TABLE card (" +
                    "id VARCHAR(255) PRIMARY KEY," +
                    "balance INT NOT NULL)";
            stmt.execute(createTableSql);
        }

        card = new Card(connection);
    }

    @AfterAll
    public void teardown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("H2 Database connection closed!");
        }
    }

    @BeforeEach
    public void setupEach() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String insertDataSql = "INSERT INTO card (id, balance) VALUES " +
                    "('card1', 1000)," +
                    "('card2', 500)";
            stmt.execute(insertDataSql);
        }
    }

    @AfterEach
    public void cleanupEach() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String deleteDataSql = "DELETE FROM card";
            stmt.execute(deleteDataSql);
        }
    }

    @Test
    public void testCheckCardDataWithSufficientBalance() throws SQLException {
        boolean result = card.checkCardData("card1", 500);
        assertTrue(result, "The transaction should succeed with sufficient balance");

        String sql = "SELECT balance FROM card WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "card1");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int balance = rs.getInt("balance");
            assertEquals(500, balance, "Balance should be reduced by the price");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCheckCardDataWithInsufficientBalance()  throws SQLException {
        boolean result = card.checkCardData("card2", 600);
        assertFalse(result, "The transaction should fail with insufficient balance");

        String sql = "SELECT balance FROM card WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "card2");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int balance = rs.getInt("balance");
            assertEquals(500, balance, "Balance should remain the same with insufficient balance");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCheckCardDataCardNotFound() {
        boolean result = card.checkCardData("card3", 100);
        assertFalse(result, "The transaction should fail when card is not found");
    }
}
