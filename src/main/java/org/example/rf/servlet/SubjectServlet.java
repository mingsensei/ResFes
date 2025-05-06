package org.example.rf.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.rf.model.Subject;
import org.example.rf.model.User;
import org.example.rf.service.SubjectService;

import java.io.IOException;
import java.util.List;

@WebServlet("/subject")
public class SubjectServlet extends HttpServlet {
    private SubjectService subjectService;

    @Override
    public void init() {
        subjectService = new SubjectService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Subject> subjectList = subjectService.getAllSubjects();
        request.setAttribute("subjectList", subjectList);
        request.getRequestDispatcher("subject.jsp").forward(request, response);
    }
}
