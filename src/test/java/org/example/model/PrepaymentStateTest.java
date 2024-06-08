package org.example.model;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrepaymentStateTest {
    private Connection connection;
    private PrepaymentState prepaymentState;

    @BeforeAll
    public void setup() throws SQLException {
        String jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String username = "sa";
        String password = "";

        connection = DriverManager.getConnection(jdbcUrl, username, password);
        System.out.println("H2 Database connected!");

        try (Statement stmt = connection.createStatement()) {
            String createTableSql = "CREATE TABLE prepayment (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "item_code VARCHAR(2) NOT NULL," +
                    "item_num INT NOT NULL," +
                    "cert_code VARCHAR(255) NOT NULL UNIQUE)";
            stmt.execute(createTableSql);
        }

        prepaymentState = new PrepaymentState(connection);
    }

    @AfterAll
    public void teardown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("H2 Database connection closed!");
        }
    }

    @Test
    public void testStorePrePayment() {
        prepaymentState.storePrePayment("01", 50, "cert123");
        Map<String, String> result = prepaymentState.checkCode("cert123");
        assertEquals("01", result.get("item_code"), "Item code should be 1001");
        assertEquals(50, Integer.parseInt(result.get("item_num")), "Item number should be 50");
    }

    @Test
    public void testCheckCodeAndDispose() {
        prepaymentState.storePrePayment("02", 30, "cert456");
        Map<String, String> result = prepaymentState.checkCode("cert456");
        assertEquals("02", result.get("item_code"), "Item code should be 1002");
        assertEquals(30, Integer.parseInt(result.get("item_num")), "Item number should be 30");

        Map<String, String> failedResult = prepaymentState.checkCode("cert456");
        assertEquals("0", failedResult.get("item_code"), "Item code should be 0");
    }
}
