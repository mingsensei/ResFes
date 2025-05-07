package org.example.rf.dao;

import org.example.rf.model.Exam;
import org.example.rf.model.Question;
import org.example.rf.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    private static final String SQL_SELECT_ALL = "SELECT * FROM exams";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM exams WHERE id = ?";
    private static final String SQL_SELECT_BY_STUDENT_ID = "SELECT * FROM exams WHERE student_id = ?";
    private static final String SQL_INSERT = "INSERT INTO exams (id, student_id, chapter_id, score, submitted_at) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_SCORE_TIME = "UPDATE exams SET score = ?, submitted_at = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM exams WHERE id = ?";
    private static final String SQL_INCREMENT_SCORE = "UPDATE exams SET score = score + 1 WHERE id = ?";
    private static final String SQL_UPDATE_SCORE_ONLY = "UPDATE exams SET score = ? WHERE id = ?";

    private final QuestionDAO questionDAO = new QuestionDAO();

    // Lấy toàn bộ bài thi
    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    // Lấy bài thi theo ID
    public Exam getExamById(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExam(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy các bài thi theo mã sinh viên
    public List<Exam> getExamsByStudentId(String studentId) {
        List<Exam> exams = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_STUDENT_ID)) {

            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    exams.add(mapResultSetToExam(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    // Thêm mới bài thi
    public boolean insertExam(Exam exam) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, exam.getId());
            stmt.setString(2, exam.getStudentId());
            stmt.setString(3, exam.getChapterId());
            stmt.setInt(4, exam.getScore());
            stmt.setTimestamp(5, Timestamp.valueOf(exam.getSubmittedAt()));
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật điểm và thời gian nộp
    public boolean updateExamScoreAndTime(String examId, int score, LocalDateTime submittedAt) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_SCORE_TIME)) {

            stmt.setInt(1, score);
            stmt.setTimestamp(2, Timestamp.valueOf(submittedAt));
            stmt.setString(3, examId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tăng điểm thêm 1
    public boolean incrementScore(String examId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INCREMENT_SCORE)) {

            stmt.setString(1, examId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tính kết quả bài thi dựa vào đáp án
    public void calculateExamResult(String examId) {
        List<Question> questions = questionDAO.getQuestionsByExamId(examId);
        long correctCount = questions.stream()
                .filter(q -> q.getStudentAnswer() != null && q.getStudentAnswer().equals(q.getCorrectOption()))
                .count();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_SCORE_ONLY)) {

            stmt.setInt(1, (int) correctCount);
            stmt.setString(2, examId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xoá bài thi
    public boolean deleteExam(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Chuyển đổi từ ResultSet thành Exam object
    private Exam mapResultSetToExam(ResultSet rs) throws SQLException {
        return new Exam(
                rs.getString("id"),
                rs.getString("student_id"),
                rs.getString("chapter_id"),
                rs.getInt("score"),
                rs.getTimestamp("submitted_at").toLocalDateTime()
        );
    }

    public int countQuestionsByExamId(String examId) {
        String sql = "SELECT COUNT(*) FROM questions WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, examId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
