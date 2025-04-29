// File: dao/SubjectDAO.java
package org.example.rf.dao;

import org.example.rf.model.Subject;
import org.example.rf.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    private static final String SELECT_ALL = "SELECT * FROM subjects";
    private static final String SELECT_BY_ID = "SELECT * FROM subjects WHERE id = ?";
    private static final String INSERT = "INSERT INTO subjects (id, name, description) VALUES (?, ?, ?)";
    private static final String DELETE = "DELETE FROM subjects WHERE id = ?";
    private static final String UPDATE = "UPDATE subjects SET name = ?, description = ? WHERE id = ?";

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                list.add(extractSubjectFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Subject getSubjectById(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractSubjectFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertSubject(Subject subject) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {

            stmt.setString(1, subject.getId());
            stmt.setString(2, subject.getName());
            stmt.setString(3, subject.getDescription());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSubject(Subject subject) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getDescription());
            stmt.setString(3, subject.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteSubject(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Subject extractSubjectFromResultSet(ResultSet rs) throws SQLException {
        return new Subject(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }
}
