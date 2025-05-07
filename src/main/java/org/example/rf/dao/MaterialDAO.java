package org.example.rf.dao;

import org.example.rf.model.Material;
import org.example.rf.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    // ====== CÂU LỆNH SQL ======
    private static final String SELECT_ALL = "SELECT * FROM materials";
    private static final String SELECT_BY_ID = "SELECT * FROM materials WHERE id = ?";
    private static final String SELECT_BY_CHAPTER_ID = "SELECT * FROM materials WHERE chapter_id = ?";
    private static final String INSERT = "INSERT INTO materials (id, title, pdfPath, chapter_id, type, vectorDbPath) VALUES (?, ?, ?, ?, ?, ?)"; // Thêm vectorDbPath
    private static final String UPDATE = "UPDATE materials SET title = ?, pdfPath = ?, chapter_id = ?, type = ?, vectorDbPath = ? WHERE id = ?"; // Thêm vectorDbPath
    private static final String DELETE = "DELETE FROM materials WHERE id = ?";

    // ====== LẤY TOÀN BỘ MATERIAL ======
    public List<Material> getAllMaterials() {
        List<Material> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                list.add(extractMaterial(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== LẤY THEO ID ======
    public Material getMaterialById(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractMaterial(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ====== LẤY THEO CHAPTER ======
    public List<Material> getMaterialsByChapterId(String chapterId) {
        List<Material> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CHAPTER_ID)) {

            stmt.setString(1, chapterId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractMaterial(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== THÊM MỚI ======
    public boolean insertMaterial(Material material) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {

            stmt.setString(1, material.getMaterialId());
            stmt.setString(2, material.getTitle());
            stmt.setString(3, material.getPdfPath());
            stmt.setString(4, material.getChapterId());
            stmt.setString(5, material.getType());
            stmt.setString(6, material.getVectorDbPath()); // Thêm vectorDbPath

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== CẬP NHẬT ======
    public boolean updateMaterial(Material material) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            stmt.setString(1, material.getTitle());
            stmt.setString(2, material.getPdfPath());
            stmt.setString(3, material.getChapterId());
            stmt.setString(4, material.getType());
            stmt.setString(5, material.getVectorDbPath()); // Thêm vectorDbPath
            stmt.setString(6, material.getMaterialId());


            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== XOÁ ======
    public boolean deleteMaterial(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== PARSE MATERIAL ======
    private Material extractMaterial(ResultSet rs) throws SQLException {
        return new Material(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("pdfPath"),  // Sửa lại thành pdfPath
                rs.getString("chapter_id"),
                rs.getString("type"),
                rs.getString("vectorDbPath") // Thêm vectorDbPath
        );
    }

    public String getVectorDbPathFromChapterId(String chapterId) {
        String sql = "SELECT m.vectorDbPath FROM materials m JOIN chapters c ON m.chapter_id = c.id WHERE c.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, chapterId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String vectorDbPath = rs.getString("vectorDbPath");
                System.out.println("DEBUG: vectorDbPath found = " + vectorDbPath);
                return vectorDbPath;
            } else {
                System.out.println("DEBUG: No vectorDbPath found for chapterId = " + chapterId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}