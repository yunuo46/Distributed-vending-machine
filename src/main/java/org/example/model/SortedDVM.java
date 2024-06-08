package org.example.model;

import org.example.model.dto.ClosestDVMDto;
import org.example.model.dto.DVMDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SortedDVM {
    private Connection connection;

    public SortedDVM(Connection connection) {
        this.connection = connection;
    }

    public boolean removeNearestDVM(String id, int x, int y) {
        String sql = "DELETE FROM sorteddvm WHERE id = ? AND x = ? AND y = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setInt(2, x);
            pstmt.setInt(3, y);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addSortedDVM(String dvmId, int dvmX, int dvmY, String dvmItemCode, float dist) {
        String sql = "INSERT INTO sorteddvm (id, x, y, item_code, distance) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, dvmId);
            pstmt.setInt(2, dvmX);
            pstmt.setInt(3, dvmY);
            pstmt.setInt(4, Integer.parseInt(dvmItemCode));
            pstmt.setFloat(5, dist);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ClosestDVMDto getNearestDVM(String item_code) {
        String sql = "SELECT id, x, y FROM sorteddvm WHERE item_code = ? ORDER BY distance ASC LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, item_code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String id = rs.getString("id");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                removeNearestDVM(id, x, y);
                ClosestDVMDto result = new ClosestDVMDto(id, x, y);
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
