package org.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stock {
    private Connection connection;

    public Stock(Connection connection) {
        this.connection = connection;
    }

    public int checkStock(int item_code) {
        String sql = "SELECT item_num FROM stock WHERE item_code = ?";
        int check_num = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item_code);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                check_num = rs.getInt("item_num");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check_num;
    }

    public void saleStock(int item_code, int item_num) {
        String sql = "UPDATE stock SET item_num = item_num - ? WHERE item_code = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item_num);
            pstmt.setInt(2, item_code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editStock(int item_code, int item_num) {
        try {
            // 먼저 해당 item_code가 존재하는지 확인
            String checkSql = "SELECT COUNT(*) FROM stock WHERE item_code = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, item_code);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // item_code가 존재하면 업데이트
                    String updateSql = "UPDATE stock SET item_num = ?, item_cost = ? WHERE item_code = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, item_num);
                        updateStmt.setInt(2, 100);
                        updateStmt.setInt(3, item_code);
                        updateStmt.executeUpdate();

                    }
                } else {
                    // item_code가 존재하지 않으면 삽입
                    String insertSql = "INSERT INTO stock (item_code, item_num, item_cost) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, item_code);
                        insertStmt.setInt(2, item_num);
                        insertStmt.setInt(3, 100);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int checkPrice(int item_code) {
        String sql = "SELECT item_cost FROM stock WHERE item_code = ?";
        int check_cost = 0;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item_code);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                check_cost = rs.getInt("item_cost");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check_cost;
    }
}
