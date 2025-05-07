<%@ page import="java.util.List" %>
<%@ page import="org.example.rf.model.Question" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Exam</title>
    <link rel="stylesheet" href="css/exam.css">
</head>
<body>
<h1>Exam Questions</h1>

<%
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    if (questions != null && !questions.isEmpty()) {
%>

<form action="submitExam" method="post"> <!-- Thay URL nếu cần -->
    <c:forEach var="question" items="${questions}" varStatus="loop">
        <div>
            <h2>Question ${loop.index + 1}: ${question.content}</h2>
            <input type="hidden" name="questionId_${loop.index}" value="${question.id}">
            <ul>
                <li><label><input type="radio" name="answer_${loop.index}" value="A"> A: ${question.optionA}</label></li>
                <li><label><input type="radio" name="answer_${loop.index}" value="B"> B: ${question.optionB}</label></li>
                <li><label><input type="radio" name="answer_${loop.index}" value="C"> C: ${question.optionC}</label></li>
                <li><label><input type="radio" name="answer_${loop.index}" value="D"> D: ${question.optionD}</label></li>
            </ul>
        </div>
    </c:forEach>
    <input type="submit" value="Submit Exam">
</form>

<%
} else {
%>
<p>No questions available.</p>
<%
    }
%>
</body>
</html>
