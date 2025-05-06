package org.example.rf.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.rf.dao.ChapterDAO;
import org.example.rf.model.Chapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/chapters-by-subject")
public class ChaptersBySubjectServlet extends HttpServlet {

    private ChapterDAO chapterDAO = new ChapterDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String subjectId = request.getParameter("subjectId");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (subjectId == null || subjectId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Missing subjectId parameter\"}");
            return;
        }

        List<Chapter> chapters = chapterDAO.getChaptersBySubjectId(subjectId);

        JSONArray jsonArray = new JSONArray();
        for (Chapter chapter : chapters) {
            JSONObject obj = new JSONObject();
            obj.put("id", chapter.getId());
            obj.put("title", chapter.getTitle());
            jsonArray.put(obj);
        }

        out.write(jsonArray.toString());
        out.flush();
    }
}
