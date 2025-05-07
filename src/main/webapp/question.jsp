<%@ page import="org.example.rf.model.Question" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Question question = (Question) request.getAttribute("question");
    String examId = (String) request.getAttribute("examId");
    Integer currentLevel = (Integer) request.getAttribute("currentLevel");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Câu hỏi trắc nghiệm</title>
    <link rel="stylesheet" href="css/question.css">
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

<% if (currentLevel != null) { %>
<div class="level-display">
    Level: <%= currentLevel %>
</div>
<% } %>

<% if (question != null) { %>
<h2>Câu hỏi:</h2>
<form action="exam" method="post">
    <input type="hidden" name="examId" value="<%= examId %>">
    <input type="hidden" name="questionId" value="<%= question.getId() %>">

    <p><b><%= question.getContent() %></b></p>
    <ul>
        <li><label><input type="radio" name="answer" value="A" required> A: <%= question.getOptionA() %></label></li>
        <li><label><input type="radio" name="answer" value="B"> B: <%= question.getOptionB() %></label></li>
        <li><label><input type="radio" name="answer" value="C"> C: <%= question.getOptionC() %></label></li>
        <li><label><input type="radio" name="answer" value="D"> D: <%= question.getOptionD() %></label></li>
    </ul>
    <input type="submit" value="Nộp đáp án">
</form>
<% } else { %>
<p>Không còn câu hỏi nào chưa trả lời.</p>
<% } %>
</body>
</html>