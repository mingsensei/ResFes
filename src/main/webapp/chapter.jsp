<%@ page import="org.example.rf.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Chương học</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/header.css" />
  <link rel="stylesheet" href="chapter.css" />
</head>
<body>

<!-- Header -->
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

<!-- Body: danh sách chương học -->
<main>
  <h1 class="subject-heading">Danh sách Chương học</h1>
  <div class="chapter-list">
    <c:forEach var="chapter" items="${chapterList}">
      <div class="chapter-box">
        <div class="chapter-info">
          <h3>${chapter.title}</h3>
          <p>Thứ tự: ${chapter.orderIndex}</p>
        </div>
        <a href="material.jsp?chapterId=${chapter.id}" class="btn-learn">Xem tài liệu</a>
      </div>
    </c:forEach>
  </div>
</main>

<footer>
  <p>&copy; 2025 EduPlatform. All rights reserved.</p>
</footer>

</body>
</html>
