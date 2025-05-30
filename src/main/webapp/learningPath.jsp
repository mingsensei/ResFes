<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lộ trình học tập</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/learningPath.css">
</head>
<body>

<header>
    <h1>Lộ trình học tập cá nhân</h1>
</header>

<main>
    <!-- Phần nhập yêu cầu từ người dùng -->
    <section class="user-goals">
        <h2>Nhập thông tin để cá nhân hóa lộ trình</h2>
        <form method="post" action="${pageContext.request.contextPath}/learningPath">
            <div>
                <label for="targetScore">🎯 Mục tiêu điểm số:</label>
                <input type="number" id="targetScore" name="targetScore" required min="0" max="100" placeholder="VD: 85">
            </div>
            <div>
                <label for="upcomingExam">📆 Kỳ thi sắp tới:</label>
                <input type="text" id="upcomingExam" name="upcomingExam" required placeholder="VD: Thi học kỳ 2, 2025">
            </div>
            <div>
                <label for="careerGoal">💼 Mục tiêu nghề nghiệp:</label>
                <input type="text" id="careerGoal" name="careerGoal" required placeholder="VD: Backend Developer">
            </div>
            <div>
                <button type="submit">Tạo lộ trình học</button>
            </div>
        </form>
    </section>

    <!-- Nếu đã có lộ trình học, hiển thị -->
    <section class="path-result">
        <c:if test="${not empty learningPathList}">
            <h2>Lộ trình học đề xuất</h2>
            <ul>
                <c:forEach var="item" items="${learningPathList}">
                    <li>
                        <strong>${item.chapterTitle}</strong> - Độ ưu tiên: ${item.priority} - Số câu cần luyện: ${item.recommendedQuestions}
                    </li>
                </c:forEach>
            </ul>
        </c:if>
    </section>
</main>

<footer>
    <p>© 2025 EduPlatform. All rights reserved.</p>
</footer>

</body>
</html>
