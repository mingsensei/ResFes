package org.example.rf.dao;

import org.example.rf.model.Chapter;
import org.example.rf.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChapterDAO {

    // ====== KHAI BÁO CÂU LỆNH SQL ======
    private static final String SELECT_ALL = "SELECT * FROM chapters";
    private static final String SELECT_BY_ID = "SELECT * FROM chapters WHERE id = ?";
    private static final String SELECT_BY_SUBJECT_ID = "SELECT * FROM chapters WHERE subject_id = ? ORDER BY order_index ASC";
    private static final String INSERT = "INSERT INTO chapters (id, title, subject_id, order_index) VALUES (?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM chapters WHERE id = ?";
    private static final String UPDATE = "UPDATE chapters SET title = ?, subject_id = ?, order_index = ? WHERE id = ?";

    // ====== LẤY TẤT CẢ CHƯƠNG ======
    public List<Chapter> getAllChapters() {
        List<Chapter> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                list.add(extractChapter(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== LẤY CHƯƠNG THEO ID ======
    public Chapter getChapterById(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractChapter(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ====== LẤY CHƯƠNG THEO MÔN HỌC ======
    public List<Chapter> getChaptersBySubjectId(String subjectId) {
        List<Chapter> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_SUBJECT_ID)) {

            stmt.setString(1, subjectId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(extractChapter(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== THÊM CHƯƠNG ======
    public boolean insertChapter(Chapter chapter) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {

            stmt.setString(1, chapter.getId());
            stmt.setString(2, chapter.getTitle());
            stmt.setString(3, chapter.getSubjectId());
            stmt.setInt(4, chapter.getOrderIndex());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== CẬP NHẬT CHƯƠNG ======
    public boolean updateChapter(Chapter chapter) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            stmt.setString(1, chapter.getTitle());
            stmt.setString(2, chapter.getSubjectId());
            stmt.setInt(3, chapter.getOrderIndex());
            stmt.setString(4, chapter.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== XOÁ CHƯƠNG ======
    public boolean deleteChapter(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== PARSE RESULTSET => CHAPTER ======
    private Chapter extractChapter(ResultSet rs) throws SQLException {
        return new Chapter(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("subject_id"),
                rs.getInt("order_index")
        );
    }
}
