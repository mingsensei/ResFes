package org.example.rf.dao;

import org.example.rf.model.Question;
import org.example.rf.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    // ====== SQL ======
    private static final String SELECT_ALL = "SELECT * FROM questions";
    private static final String SELECT_BY_ID = "SELECT * FROM questions WHERE id = ?";
    private static final String SELECT_BY_EXAM_ID = "SELECT * FROM questions WHERE exam_id = ?";

    private static final String INSERT = "INSERT INTO questions (id, content, option_a, option_b, option_c, option_d, correct_option, student_answer, exam_id, explain) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String DELETE = "DELETE FROM questions WHERE id = ?";

    private static final String UPDATE = "UPDATE questions SET content = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_option = ?, student_answer = ?, exam_id = ?, explain = ? WHERE id = ?";

    // ====== LẤY TOÀN BỘ ======
    public List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                list.add(extractQuestion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== LẤY THEO ID ======
    public Question getQuestionById(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractQuestion(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ====== LẤY THEO EXAM ======
    public List<Question> getQuestionsByExamId(String examId) {
        List<Question> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_EXAM_ID)) {

            stmt.setString(1, examId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractQuestion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== THÊM ======
    public boolean insertQuestion(Question question) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {

            stmt.setString(1, question.getId());
            stmt.setString(2, question.getContent());
            stmt.setString(3, question.getOptionA());
            stmt.setString(4, question.getOptionB());
            stmt.setString(5, question.getOptionC());
            stmt.setString(6, question.getOptionD());
            stmt.setString(7, question.getCorrectOption());
            stmt.setString(8, question.getStudentAnswer());
            stmt.setString(9, question.getExamId());
            stmt.setString(10, question.getExplain()); // ✅ thêm phần explain

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== CẬP NHẬT ======
    public boolean updateQuestion(Question question) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            stmt.setString(1, question.getContent());
            stmt.setString(2, question.getOptionA());
            stmt.setString(3, question.getOptionB());
            stmt.setString(4, question.getOptionC());
            stmt.setString(5, question.getOptionD());
            stmt.setString(6, question.getCorrectOption());
            stmt.setString(7, question.getStudentAnswer());
            stmt.setString(8, question.getExamId());
            stmt.setString(9, question.getExplain()); // ✅ cập nhật explain
            stmt.setString(10, question.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== XOÁ ======
    public boolean deleteQuestion(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== PARSE RESULTSET => QUESTION ======
    private Question extractQuestion(ResultSet rs) throws SQLException {
        return new Question(
                rs.getString("id"),
                rs.getString("content"),
                rs.getString("option_a"),
                rs.getString("option_b"),
                rs.getString("option_c"),
                rs.getString("option_d"),
                rs.getString("correct_option"),
                rs.getString("student_answer"),
                rs.getString("exam_id"),
                rs.getString("explain") // ✅ lấy explain từ DB
        );
    }
}
