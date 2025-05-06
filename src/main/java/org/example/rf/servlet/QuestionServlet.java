package org.example.rf.servlet;

import org.example.rf.dao.QuestionDAO;
import org.example.rf.model.Question;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/question")
public class QuestionServlet extends HttpServlet {

    private final QuestionDAO questionDAO = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String examId = request.getParameter("examId");
        if (examId == null || examId.isEmpty()) {
            response.getWriter().println("Exam ID is missing.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<Question> unansweredQuestions = questionDAO.getUnansweredQuestionsByExamId(examId);

        if (unansweredQuestions.isEmpty()) {
            // Không còn câu chưa trả lời, chuyển sang trang tiếp tục hoặc kết quả
            response.sendRedirect(request.getContextPath() + "/continueExam.jsp?examId=" + examId);
            return;
        }

        Question question = unansweredQuestions.get(0); // Lấy câu đầu tiên chưa trả lời

        request.setAttribute("question", question);
        request.setAttribute("examId", examId);
        request.getRequestDispatcher("question.jsp").forward(request, response);
    }

}