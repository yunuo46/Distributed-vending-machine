package org.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Card {
    private Connection connection;

    public Card(Connection connection) {
        this.connection = connection;
    }

    public boolean checkCardData(String card_id, int price) {
        String sql = "SELECT id, balance FROM card WHERE id = ?";
        boolean success = false;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, card_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int balance = rs.getInt("balance");
                if (balance >= price) {
                    reduceBalance(card_id, price);
                    success = true;
                } else {
                    System.out.println("Insufficient balance.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    private void reduceBalance(String card_id, int price) {
        String sql = "UPDATE card SET balance = balance - ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, price);
            pstmt.setString(2, card_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refundCardData(String card_id, int price) {
        String sql = "UPDATE card SET balance = balance + ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, price);
            pstmt.setString(2, card_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
