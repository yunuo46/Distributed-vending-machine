package org.example.model;

import org.example.model.dto.ClosestDVMDto;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SortedDVMTest {
    private Connection connection;
    private SortedDVM sortedDVM;

    @BeforeAll
    public void setup() throws SQLException {
        String jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String username = "sa";
        String password = "";

        connection = DriverManager.getConnection(jdbcUrl, username, password);
        System.out.println("H2 Database connected!");

        try (Statement stmt = connection.createStatement()) {
            String createTableSql = "CREATE TABLE sorteddvm (" +
                    "id VARCHAR(255) PRIMARY KEY," +
                    "x INT NOT NULL," +
                    "y INT NOT NULL," +
                    "item_code VARCHAR(2) NOT NULL," +
                    "distance FLOAT NOT NULL)";
            stmt.execute(createTableSql);
        }

        sortedDVM = new SortedDVM(connection);
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
            String insertDataSql = "INSERT INTO sorteddvm (id, x, y, item_code, distance) VALUES " +
                    "('dvm1', 1, 1, '01', 10.0)," +
                    "('dvm2', 2, 2, '01', 20.0)," +
                    "('dvm3', 3, 3, '02', 15.0)";
            stmt.execute(insertDataSql);
        }
    }

    @AfterEach
    public void cleanupEach() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String deleteDataSql = "DELETE FROM sorteddvm";
            stmt.execute(deleteDataSql);
        }
    }

    @Test
    public void testAddSortedDVM() throws SQLException {
        sortedDVM.addSortedDVM("dvm4", 4, 4, "03", 5.0f);

        String sql = "SELECT COUNT(*) FROM sorteddvm WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "dvm4");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(1, count, "DVM should be present in the database");
        }
    }

    @Test
    public void testRemoveNearestDVM() throws SQLException {
        boolean result = sortedDVM.removeNearestDVM("dvm1", 1, 1);
        assertTrue(result, "DVM should be removed successfully");

        String sql = "SELECT COUNT(*) FROM sorteddvm WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "dvm1");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(0, count, "DVM should not be present in the database");
        }
    }

    @Test
    public void testGetNearestDVM() {
        ClosestDVMDto closestDVM = sortedDVM.getNearestDVM("01");
        assertNotNull(closestDVM, "Closest DVM should be found");
        assertEquals("dvm1", closestDVM.getId());
        assertEquals(1, closestDVM.getX());
        assertEquals(1, closestDVM.getY());

        String sql = "SELECT COUNT(*) FROM sorteddvm WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "dvm1");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(0, count, "DVM should not be present in the database after retrieval");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRemoveRemainSortedDVM() throws SQLException {
        sortedDVM.removeRemainSortedDVM("01");

        String sql = "SELECT COUNT(*) FROM sorteddvm WHERE item_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "01");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(2, count, "All DVMs with item_code 'item1' should be removed from the database");
        }
    }
}
