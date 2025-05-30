<%@ page import="org.example.rf.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Lịch sử làm bài</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/header.css"/>
</head>
<body>
<main>
    <h1 class="attempt-heading">Lịch sử làm bài</h1>
    <div class="attempt-container">
        <c:forEach var="attempt" items="${examList}">
            <div class="attempt-card">
                <div class="attempt-title">${attempt.score}</div>
                <div class="attempt-desc">${attempt.id}</div>
                <a href="attempt?examId=${attempt.id}" class="btn-learn">Xem chi tiết</a>
            </div>
        </c:forEach>
    </div>
</main>
</body>
</html>
