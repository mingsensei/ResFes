<%@ page import="org.example.rf.model.Exam" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Result</title>
    <link rel="stylesheet" href="css/result.css">
    <link rel="stylesheet" href="css/home-button.css"> <!-- Link CSS riêng cho button -->
    <style>
        .level-display {
            position: absolute;
            top: 10px;
            right: 10px;
            background-color: rgba(255, 255, 255, 0.8);
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 14px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Result</h1>
    <%
        Exam exam = (Exam) request.getAttribute("exam");
        Integer questionCount = (Integer) request.getAttribute("questionCount");
        Integer currentLevel = (Integer) request.getAttribute("currentLevel");
    %>

    <% if (currentLevel != null) { %>
    <div class="level-display">
        Level: <%= currentLevel %>
    </div>
    <% } %>

    <% if (exam != null) { %>
    <p>Score: <%= exam.getScore() %></p>
    <p>Number of Questions: <%= questionCount %></p>
    <% } else { %>
    <p>Exam not found.</p>
    <% } %>

    <!-- Nút Back to Home -->
    <button class="home-button" onclick="location.href='<%= request.getContextPath() %>/'">Back to Home</button>
</div>
</body>
</html>