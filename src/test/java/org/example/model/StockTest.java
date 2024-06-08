package org.example.model;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StockTest {
    private Connection connection;
    private Stock stock;

    @BeforeAll
    public void setup() throws SQLException {
        String jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String username = "sa";
        String password = "";

        connection = DriverManager.getConnection(jdbcUrl, username, password);
        System.out.println("H2 Database connected!");

        try (Statement stmt = connection.createStatement()) {
            String createTableSql = "CREATE TABLE stock (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "item_code VARCHAR(2) NOT NULL," +
                    "item_num INT NOT NULL," +
                    "item_cost INT NOT NULL)";
            stmt.execute(createTableSql);
        }

        stock = new Stock(connection);
    }

    @AfterAll
    public void teardown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("H2 Database connection closed!");
        }
    }

    @Test
    public void testAddOrEditStock() {
        stock.editStock("01", 50);
        int item_num = stock.checkStock("01");
        assertEquals(50, item_num, "Item number should be 50 after adding stock");
    }

    @Test
    public void testSaleStock() {
        stock.editStock("01", 50);
        stock.saleStock("01", 10);
        int item_num = stock.checkStock("01");
        assertEquals(40, item_num, "Item number should be 40 after selling 10 items");
    }

    @Test
    public void testEditStock() {
        stock.editStock("01", 100);
        int item_num = stock.checkStock("01");
        assertEquals(100, item_num, "Item number should be 100 after editing stock");
    }
}