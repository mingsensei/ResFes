package org.example.rf.dao;

import org.example.rf.model.Exam;
import org.example.rf.model.Question;
import org.example.rf.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    // ====== SQL ======
    private static final String SELECT_ALL = "SELECT * FROM exams";
    private static final String SELECT_BY_ID = "SELECT * FROM exams WHERE id = ?";
    private static final String SELECT_BY_STUDENT_ID = "SELECT * FROM exams WHERE student_id = ?";
    private static final String INSERT = "INSERT INTO exams (id, student_id, chapter_id, score, submitted_at) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM exams WHERE id = ?";
    private static final String UPDATE_SCORE = "UPDATE exams SET score = ?, submitted_at = ? WHERE id = ?";

    private QuestionDAO questionDAO = new QuestionDAO();

    // ====== LẤY TOÀN BỘ EXAM ======
    public List<Exam> getAllExams() {
        List<Exam> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                list.add(extractExam(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== LẤY THEO ID ======
    public Exam getExamById(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractExam(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ====== LẤY THEO STUDENT ======
    public List<Exam> getExamsByStudentId(String studentId) {
        List<Exam> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_STUDENT_ID)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractExam(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== THÊM MỚI EXAM ======
    public boolean insertExam(Exam exam) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {

            stmt.setString(1, exam.getId());
            stmt.setString(2, exam.getStudentId());
            stmt.setString(3, exam.getChapterId()); // ✅ chapter_id
            stmt.setInt(4, exam.getScore());
            stmt.setTimestamp(5, Timestamp.valueOf(exam.getSubmittedAt()));

            boolean inserted = stmt.executeUpdate() > 0;

            if (inserted && exam.getQuestions() != null) {
                for (Question q : exam.getQuestions()) {
                    q.setExamId(exam.getId());
                    questionDAO.insertQuestion(q);
                }
            }

            return inserted;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== CẬP NHẬT ĐIỂM ======
    public boolean updateExamScoreAndTime(String examId, int score, LocalDateTime submittedAt) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SCORE)) {

            stmt.setInt(1, score);
            stmt.setTimestamp(2, Timestamp.valueOf(submittedAt));
            stmt.setString(3, examId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== XOÁ EXAM ======
    public boolean deleteExam(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== PARSE EXAM ======
    private Exam extractExam(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        return new Exam(
                id,
                rs.getString("student_id"),
                rs.getString("chapter_id"), // ✅ đổi từ subject_id sang chapter_id
                rs.getInt("score"),
                rs.getTimestamp("submitted_at").toLocalDateTime(),
                new ArrayList<>(questionDAO.getQuestionsByExamId(id))
        );
    }
}
