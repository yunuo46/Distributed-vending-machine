package org.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PrepaymentState {
    private Connection connection;

    public PrepaymentState(Connection connection) {
        this.connection = connection;
    }

    public void storePrePayment(int item_code, int item_num, String cert_code) {
        String insertSql = "INSERT INTO prepayment (item_code, item_num, cert_code) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setInt(1, item_code);
            insertStmt.setInt(2, item_num);
            insertStmt.setString(3, cert_code);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> checkCode(String cert_code) {
        String sql = "SELECT item_code, item_num FROM prepayment WHERE cert_code = ?";
        Map<String, Integer> result = new HashMap<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cert_code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int item_code = rs.getInt("item_code");
                int item_num = rs.getInt("item_num");
                result.put("item_code", item_code);
                result.put("item_num", item_num);
                disposeCode(cert_code);
            } else {
                result.put("item_code", 0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void disposeCode(String cert_code) {
        String deleteSql = "DELETE FROM prepayment WHERE cert_code = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
            deleteStmt.setString(1, cert_code);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
