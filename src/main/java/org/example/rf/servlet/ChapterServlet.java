package org.example.rf.servlet;



import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.example.rf.model.Chapter;
import org.example.rf.model.User;
import org.example.rf.service.ChapterService;

import java.io.IOException;
import java.util.List;

@WebServlet("/chapter")
public class ChapterServlet extends HttpServlet {
    private ChapterService chapterService;

    @Override
    public void init() {
        chapterService = new ChapterService();
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


        String subjectId = request.getParameter("subjectId");

        if (subjectId != null && !subjectId.isEmpty()) {
            List<Chapter> chapterList = chapterService.getChaptersBySubjectId(subjectId);
            request.setAttribute("chapterList", chapterList);
            request.getRequestDispatcher("chapter.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu subjectId trong URL.");
        }
    }
}

