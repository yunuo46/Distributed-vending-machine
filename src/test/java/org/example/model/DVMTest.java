package org.example.model;

import org.example.model.dto.DVMDto;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DVMTest {
    private Connection connection;
    private DVM dvm;

    @BeforeAll
    public void setup() throws SQLException {
        String jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String username = "sa";
        String password = "";

        connection = DriverManager.getConnection(jdbcUrl, username, password);
        System.out.println("H2 Database connected!");

        try (Statement stmt = connection.createStatement()) {
            String createTableSql = "CREATE TABLE dvm (" +
                    "id VARCHAR(255) PRIMARY KEY," +
                    "ip VARCHAR(255) NOT NULL," +
                    "port VARCHAR(255) NOT NULL)";
            stmt.execute(createTableSql);
        }

        dvm = new DVM(connection);
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
            String insertDataSql = "INSERT INTO dvm (id, ip, port) VALUES " +
                    "('dvm1', '192.168.1.1', '8080')," +
                    "('dvm2', '192.168.1.2', '8081')";
            stmt.execute(insertDataSql);
        }
    }

    @AfterEach
    public void cleanupEach() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String deleteDataSql = "DELETE FROM dvm";
            stmt.execute(deleteDataSql);
        }
    }

    @Test
    public void testAddDVM() throws SQLException {
        boolean result = dvm.addDVM("dvm3", "192.168.1.3", "8082");
        assertTrue(result, "DVM should be added successfully");

        String sql = "SELECT COUNT(*) FROM dvm WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "dvm3");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(1, count, "DVM should be present in the database");
        }
    }

    @Test
    public void testRemoveDVM() throws SQLException {
        boolean result = dvm.removeDVM("dvm1");
        assertTrue(result, "DVM should be removed successfully");

        String sql = "SELECT COUNT(*) FROM dvm WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "dvm1");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(0, count, "DVM should not be present in the database");
        }
    }

    @Test
    public void testGetAllDVM() {
        List<DVMDto> dvmList = dvm.getAllDVM();
        assertEquals(2, dvmList.size(), "There should be 2 DVMs in the database");

        DVMDto dvm1 = dvmList.get(0);
        assertEquals("dvm1", dvm1.getId());
        assertEquals("192.168.1.1", dvm1.getIp());
        assertEquals("8080", dvm1.getPort());

        DVMDto dvm2 = dvmList.get(1);
        assertEquals("dvm2", dvm2.getId());
        assertEquals("192.168.1.2", dvm2.getIp());
        assertEquals("8081", dvm2.getPort());
    }

    @Test
    public void testGetIpPort() {
        DVMDto dvmDto = dvm.getIpPort("dvm1");
        assertNotNull(dvmDto, "DVM should be found");
        assertEquals("dvm1", dvmDto.getId());
        assertEquals("192.168.1.1", dvmDto.getIp());
        assertEquals("8080", dvmDto.getPort());

        DVMDto nonExistentDvmDto = dvm.getIpPort("dvm3");
        assertNull(nonExistentDvmDto, "DVM should not be found");
    }
}
