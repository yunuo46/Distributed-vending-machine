package org.example.model;

import org.example.model.dto.ClosestDVMDto;
import org.example.model.dto.DVMDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DVM {
    private Connection connection;

    public DVM(Connection connection) {
        this.connection = connection;
    }

    public boolean addDVM(String id, String ip, String port) {
        String sql = "INSERT INTO dvm (id, ip, port) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, ip);
            pstmt.setString(3, port);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeDVM(String id) {
        String sql = "DELETE FROM dvm WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeNearestDVM(String id, int x, int y) {
        String sql = "DELETE FROM dvm WHERE id = ? AND x = ? AND y = ?";
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

    public List<DVMDto> getAllDVM() {
        String sql = "SELECT id, ip, port FROM dvm WHERE item_code IS NULL";
        List<DVMDto> dvmList = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String ip = rs.getString("ip");
                String port = rs.getString("port");
                dvmList.add(new DVMDto(id, ip, port));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dvmList;
    }

    public void addSortedDVM(String dvmId, int dvmX, int dvmY, String dvmItemCode, float dist) {
        String sql = "INSERT INTO dvm (id, x, y, item_code, distance) VALUES (?, ?, ?, ?, ?)";
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

    public ClosestDVMDto getNearestDVM(int item_code) {
        String sql = "SELECT id, x, y FROM dvm WHERE item_code = ? ORDER BY distance ASC LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item_code);
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

    public DVMDto getIpPort(String id) {
        String sql = "SELECT ip, port FROM dvm WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String ip = rs.getString("ip");
                String port = rs.getString("port");
                return new DVMDto(id, ip, port);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
