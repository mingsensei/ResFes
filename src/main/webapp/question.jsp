<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.rf.model.Question" %>
<%
    Question question = (Question) request.getAttribute("question");
    String examId = (String) request.getAttribute("examId");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Câu hỏi trắc nghiệm</title>
    <link rel="stylesheet" href="css/question.css">
</head>
<body>
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
