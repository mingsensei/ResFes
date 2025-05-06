<%@ page import="org.example.rf.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Môn học</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/header.css" />
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/subject.css" />
</head>
<body>

<!-- Header dùng chung -->
<header>
    <nav class="navbar">
        <div class="logo">EduPlatform</div>
        <ul class="nav-links">
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

<!-- Môn học -->
<main>
    <h1 class="subject-heading">Danh sách Môn học</h1>
    <div class="subject-container">
        <c:forEach var="subject" items="${subjectList}">
            <div class="subject-card">
                <div class="subject-title">${subject.name}</div>
                <div class="subject-desc">${subject.description}</div>
                <a href="chapter.jsp?subjectId=${subject.id}" class="btn-learn">Vào học</a>
            </div>
        </c:forEach>
    </div>
</main>

<footer>
    <p>&copy; 2025 EduPlatform. All rights reserved.</p>
</footer>

</body>
</html>
