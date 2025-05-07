package org.example.rf.servlet;

import org.example.rf.dao.ExamDAO;
import org.example.rf.model.Exam;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;


@WebServlet("/result")
public class ResultServlet extends HttpServlet {

    private final ExamDAO examDAO = new ExamDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String examId = request.getParameter("examId");

        if (examId == null || examId.isEmpty()) {
            response.getWriter().println("Exam ID is missing.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Exam exam = examDAO.getExamById(examId);

        if (exam == null) {
            response.getWriter().println("Exam not found.");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int questionCount = examDAO.countQuestionsByExamId(examId);
        request.setAttribute("exam", exam);
        request.setAttribute("questionCount", questionCount);
        // Clear session
        HttpSession session = request.getSession();
        session.removeAttribute("examId");
        session.removeAttribute("numQuestions");
        session.removeAttribute("chapterId");
        session.removeAttribute("initialNumQuestions");

        request.getRequestDispatcher("result.jsp").forward(request, response);
    }

}