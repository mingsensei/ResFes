package org.example.rf.dao;

import org.example.rf.model.Level;
import org.example.rf.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LevelDAO {

    // ====== SQL ======
    private static final String SELECT_ALL = "SELECT * FROM levels";
    private static final String SELECT_BY_ID = "SELECT * FROM levels WHERE id = ?";
    private static final String SELECT_BY_STUDENT_AND_CHAPTER = "SELECT * FROM levels WHERE student_id = ? AND chapter_id = ?";
    private static final String INSERT = "INSERT INTO levels (id, student_id, chapter_id, level, current_exp, required_exp) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE levels SET level = ?, current_exp = ?, required_exp = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM levels WHERE id = ?";

    // ====== LẤY TOÀN BỘ ======
    public List<Level> getAllLevels() {
        List<Level> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                list.add(extractLevel(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== LẤY THEO ID ======
    public Level getLevelById(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractLevel(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ====== LẤY THEO STUDENT & CHAPTER ======
    public Level getLevelByStudentAndChapter(String studentId, String chapterId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_STUDENT_AND_CHAPTER)) {

            stmt.setString(1, studentId);
            stmt.setString(2, chapterId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractLevel(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ====== THÊM ======
    public boolean insertLevel(Level level) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {

            stmt.setString(1, level.getId());
            stmt.setString(2, level.getStudentId());
            stmt.setString(3, level.getChapterId());
            stmt.setInt(4, level.getLevel());
            stmt.setInt(5, level.getCurrentExp());
            stmt.setInt(6, level.getRequiredExp());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== CẬP NHẬT ======
    public boolean updateLevel(Level level) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            stmt.setInt(1, level.getLevel());
            stmt.setInt(2, level.getCurrentExp());
            stmt.setInt(3, level.getRequiredExp());
            stmt.setString(4, level.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== XOÁ ======
    public boolean deleteLevel(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== PARSE RESULTSET => LEVEL ======
    private Level extractLevel(ResultSet rs) throws SQLException {
        return new Level(
                rs.getString("id"),
                rs.getString("student_id"),
                rs.getString("chapter_id"),
                rs.getInt("level"),
                rs.getInt("current_exp"),
                rs.getInt("required_exp")
        );
    }
}
