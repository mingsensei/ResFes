<%@ page import="org.example.rf.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Chương học</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/header.css" />
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/chapter.css" />
</head>
<body>

<!-- Header -->
<header>
  <nav class="navbar">
    <div class="logo">EduPlatform</div>
    <ul class="nav-links">
      <li><a href="<%= request.getContextPath() %>/material">Thêm tài liệu</a></li>
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
        <a>Xin chào, <%= user.getName() %></a>
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

<!-- Nội dung chính -->
<main>
  <h1 class="subject-heading">Danh sách Chương học</h1>

  <div class="learning-path-button">
    <a href="<%= request.getContextPath() %>/learningPath?subjectId=${subjectId}" class="btn-learning-path">Xem lộ trình học toàn môn</a>
  </div>

  <!-- Danh sách chương học -->
  <div class="chapter-container">
    <c:forEach var="chapter" items="${chapterList}">
      <div class="chapter-card">
        <div class="chapter-title">${chapter.title}</div>
        <div class="chapter-actions">
          <a href="material?chapterId=${chapter.id}" class="btn-learn">Xem tài liệu</a>
          <a href="attempt?chapterId=${chapter.id}" class="btn-test">Bài đã làm</a>
          <a href="#" onclick="showQuestionDialog('${chapter.id}')" class="btn-test">Làm kiểm tra</a>
        </div>
      </div>
    </c:forEach>
  </div>
</main>

<!-- Script bắt đầu kiểm tra -->
<script>
  function showQuestionDialog(chapterId) {
    let numQuestions = prompt("Vui lòng nhập số lượng câu hỏi bạn muốn:");
    if (numQuestions != null && numQuestions.trim() !== "") {
      window.location.href = '<%=request.getContextPath()%>/exam?chapterId=' + chapterId + '&numQuestions=' + numQuestions;
    }
  }
</script>

<footer>
  <p>© 2025 EduPlatform. All rights reserved.</p>
</footer>

</body>
</html>