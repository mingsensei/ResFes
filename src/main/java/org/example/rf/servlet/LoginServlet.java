package org.example.rf.servlet;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.rf.dao.UserDAO;
import org.example.rf.model.User;
import org.example.rf.util.HashPassword;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ✅ Lưu trang trước vào session (nếu có)
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("login")) { // Tránh lưu lại trang login
            request.getSession().setAttribute("redirectUrl", referer);
        }

        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(email);

        HttpSession session = request.getSession();
        System.out.println("Login Session ID (Before Auth): " + session.getId());

        if (user != null && user.getPassword().equals(HashPassword.hashPassword(password))) {
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(60 * 60); // ✅ Giữ session 1 giờ

            // ✅ Debug: In thông tin session
            System.out.println("User logged in: " + user.getEmail());
            System.out.println("Login Session ID (After Auth): " + session.getId());
            System.out.println("Session Attributes: ");
            session.getAttributeNames().asIterator().forEachRemaining(attr ->
                    System.out.println(" - " + attr + ": " + session.getAttribute(attr))
            );

            // ✅ Chuyển hướng về trang trước (nếu có)
            String redirectUrl = (String) session.getAttribute("redirectUrl");
            if (redirectUrl != null) {
                session.removeAttribute("redirectUrl");
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/home"); // Mặc định về home
            }
        } else {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            request.setAttribute("error", "Sai email hoặc mật khẩu!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
