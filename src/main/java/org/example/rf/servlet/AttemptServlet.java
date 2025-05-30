package org.example.rf.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.rf.dao.ExamDAO;
import org.example.rf.model.Exam;
import org.example.rf.model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/attempt")
public class AttemptServlet extends HttpServlet {
    private final ExamDAO examDAO = new ExamDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String chapterId = request.getParameter("chapterId");
        User user = (User)request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
        }
        assert user != null;
        String userId = user.getId();

        List<Exam> examList = examDAO.getExamsByChapterIdAndStudentId(chapterId, userId);
        request.setAttribute("examList", examList);
        request.getRequestDispatcher("attempt.jsp").forward(request, response);
    }
}
