package org.example.rf.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/logout")
public class    LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy session hiện tại
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate(); // Hủy session
        }

        // Chuyển hướng về trang đăng nhập
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
