<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List, org.example.rf.model.Subject" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Upload PDF Material</title>
    <style>
        /* Giữ nguyên style cũ */
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .container {
            width: 600px;
            margin: 0 auto;
            border: 1px solid #ddd;
            padding: 20px;
            border-radius: 5px;
        }
        h1 {
            text-align: center;
            color: #333;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        input[type="file"] {
            padding: 8px 0;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        button:hover {
            background-color: #45a049;
        }
        .error {
            color: red;
            font-weight: bold;
            margin-bottom: 15px;
        }
        .success {
            color: green;
            font-weight: bold;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Upload PDF Material</h1>

    <form action="<%= request.getContextPath() %>/material" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <label for="title">Tiêu đề:</label>
            <input type="text" id="title" name="title" required>
        </div>

        <div class="form-group">
            <label for="subjectId">Môn học:</label>
            <select id="subjectId" name="subjectId" required>
                <option value="">-- Chọn môn học --</option>
                <%
                    List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
                    if (subjects != null) {
                        for (Subject subject : subjects) {
                %>
                <option value="<%= subject.getId() %>"><%= subject.getName() %></option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <div class="form-group">
            <label for="chapterId">Chương:</label>
            <select id="chapterId" name="chapterId" required>
                <option value="">-- Chọn chương --</option>
            </select>
        </div>

        <div class="form-group">
            <label for="pdfFile">Chọn file PDF:</label>
            <input type="file" id="pdfFile" name="pdfFile" accept=".pdf" required>
        </div>

        <div class="form-group">
            <button type="submit">Tải lên</button>
        </div>
    </form>
</div>

<script>
    document.getElementById('subjectId').addEventListener('change', function () {
        const subjectId = this.value;
        const chapterSelect = document.getElementById('chapterId');

        chapterSelect.innerHTML = '<option value="">-- Đang tải chương --</option>';

        if (!subjectId) {
            chapterSelect.innerHTML = '<option value="">-- Chọn chương --</option>';
            return;
        }

        fetch('<%=request.getContextPath()%>/chapters-by-subject?subjectId=' + encodeURIComponent(subjectId))
            .then(response => {
                if (!response.ok) {
                    throw new Error('Lỗi khi tải chương');
                }
                return response.json();
            })
            .then(data => {
                chapterSelect.innerHTML = '<option value="">-- Chọn chương --</option>';
                data.forEach(chapter => {
                    const option = document.createElement('option');
                    option.value = chapter.id;
                    option.textContent = chapter.title;
                    chapterSelect.appendChild(option);
                });
            })
            .catch(error => {
                chapterSelect.innerHTML = '<option value="">-- Lỗi tải chương --</option>';
                console.error(error);
            });
    });
</script>
</body>
</html>
