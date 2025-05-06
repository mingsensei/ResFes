<%@ page import="org.example.rf.model.Exam" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
  <title>Result</title>
  <link rel="stylesheet" href="css/result.css">
</head>
<body>
<h1>Result</h1>

<%
  Exam exam = (Exam) request.getAttribute("exam");
%>

<% if (exam != null) { %>
<p>Score: <%= exam.getScore() %></p>
<p>Number of Questions: <%= exam.getQuestions().size() %></p>
<%-- Display detailed results, iterate through the `questions` list in the `exam` object --%>
<% } else { %>
<p>Exam not found.</p>
<% } %>
</body>
</html>