<%@ page import="org.example.rf.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Trang Chủ Học Tập</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/index.css" />
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/header.css" />
</head>
<body>
<header>
    <nav class="navbar">
        <div class="logo">EduPlatform</div>
        <ul class="nav-links">
            <li><a href="<%= request.getContextPath() %>/material">Thêm tài liệu</a></li> <!-- MỤC MỚI -->
            <li><a href="subject.jsp">Môn học</a></li>
            <li><a href="#">Kiểm tra</a></li>
            <li><a href="#">AI Agent</a></li>
            <li>
                <%
                    User user = (User) session.getAttribute("user");
                %>
                <%
                    if (user != null) {
                %>
                <a>Xin chào, <%= user.getName()%></a>
                <%
                } else {
                %>
                <a href="<%= request.getContextPath() %>/login">Đăng nhập</a>
                <%
                    }
                %>
            </li>
        </ul>
    </nav>
</header>
<main>
    <section class="hero">
        <div class="hero-content">
            <h1>Chào mừng đến với nền tảng học tập thông minh</h1>
            <p>Khám phá tài liệu, luyện tập kiểm tra và sử dụng AI để học hiệu quả hơn!</p>
            <a href="<%= request.getContextPath() %>/subject" class="btn-primary">Bắt đầu học</a>
        </div>
    </section>
</main>


<footer>
    <p>&copy; 2025 EduPlatform. All rights reserved.</p>
</footer>
</body>
</html>
