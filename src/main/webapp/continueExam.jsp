<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tiếp tục bài kiểm tra?</title>
    <link rel="stylesheet" href="css/continueExam.css">
</head>
<body>
<h1>Bạn muốn tiếp tục với nhiều câu hỏi hơn?</h1>
<form action="exam" method="post">
    <input type="hidden" name="examId" value="<%= request.getParameter("examId") %>">
    <input type="hidden" name="action" value="continue">
    <label>
        <input type="radio" name="continueChoice" value="yes"> Có, cho tôi thêm câu hỏi:
        <input type="number" name="additionalQuestions" value="10">
    </label><br>
    <label>
        <input type="radio" name="continueChoice" value="no"> Không, tôi muốn kết thúc bài kiểm tra.
    </label><br>
    <input type="submit" value="Xác nhận">
</form>
</body>
</html>