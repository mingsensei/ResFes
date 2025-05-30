package org.example.rf.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.rf.dao.ChapterDAO;
import org.example.rf.dao.ExamDAO;
import org.example.rf.dao.QuestionDAO;
import org.example.rf.model.User;

import java.io.IOException;

@WebServlet("/learningPath")
public class LearningPathServlet extends HttpServlet {
    private final ExamDAO  examDAO = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ChapterDAO chapterDAO = new ChapterDAO();
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/learningPath.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

