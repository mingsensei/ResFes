package org.example.rf.servlet;

import org.example.rf.dao.UserDAO;
import org.example.rf.model.User;
import org.example.rf.util.HashPassword;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String hashedPassword = HashPassword.hashPassword(password);

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole(User.Role.STUDENT);

        boolean success = userDAO.insertUser(user);
        String referer = request.getHeader("referer");

        if (success) {
            response.sendRedirect(referer != null ? referer : "login.jsp");
        } else {
            request.setAttribute("error", "Đăng ký thất bại!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}
