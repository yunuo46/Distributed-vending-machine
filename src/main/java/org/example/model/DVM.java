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

    public List<DVMDto> getAllDVM() {
        String sql = "SELECT id, ip, port FROM dvm";
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
