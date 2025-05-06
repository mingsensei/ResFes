package org.example.rf.servlet;

import org.example.rf.dao.MaterialDAO;
import org.example.rf.dao.QuestionDAO;
import org.example.rf.dao.ExamDAO;
import org.example.rf.model.Question;
import org.example.rf.model.Exam;
import org.example.rf.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@WebServlet("/exam")
public class ExamServlet extends HttpServlet {

    private static final String PYTHON_API_URL = "http://localhost:8002/generate";
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();
    private final ExamDAO examDAO = new ExamDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String chapterId = request.getParameter("chapterId");
        Integer numQuestionsParam = tryParseInt(request.getParameter("numQuestions"));
        String examId = (String) request.getSession().getAttribute("examId");

        if (chapterId == null || chapterId.isEmpty() || (numQuestionsParam == null && examId == null)) {
            response.getWriter().println("Chapter ID or number of questions is invalid.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            // Nếu chưa có examId trong session, tạo mới exam
            if (examId == null || examId.isEmpty()) {
                int numQuestions = numQuestionsParam;

                String vectorDbPath = materialDAO.getVectorDbPathFromChapterId(chapterId);
                if (vectorDbPath == null || vectorDbPath.isEmpty()) {
                    response.getWriter().println("Vector DB path not found for chapter: " + chapterId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                // 1. Gọi API Python sinh câu hỏi
                String questionsJson = callPythonAPI(vectorDbPath, 2, numQuestions);

                if (questionsJson != null && !questionsJson.isEmpty()) {
                    List<Question> questions = parseQuestionsFromJson(questionsJson);

                    // 2. Tạo examId
                    examId = UUID.randomUUID().toString();

                    // 3. Lấy userId từ session
                    User user = (User) request.getSession().getAttribute("user");
                    if (user == null) {
                        response.getWriter().println("User not logged in or session expired.");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    String userId = user.getId();

                    // 4. Tạo Exam object
                    Exam exam = new Exam();
                    exam.setId(examId);
                    exam.setStudentId(userId);
                    exam.setChapterId(chapterId);
                    exam.setScore(0);
                    exam.setSubmittedAt(LocalDateTime.now());
                    exam.setQuestions(new ArrayList<>(questions));

                    // 5. Lưu Exam và Questions vào DB
                    boolean examInserted = examDAO.insertExam(exam);
                    if (!examInserted) {
                        response.getWriter().println("Failed to persist Exam to the database.");
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return;
                    }
                    for (Question q : questions) {
                        q.setExamId(examId);
                        questionDAO.insertQuestion(q);
                    }

                    // 6. Lưu examId và số câu hỏi vào session
                    request.getSession().setAttribute("examId", examId);
                    request.getSession().setAttribute("numQuestions", numQuestions);
                    request.getSession().setAttribute("chapterId", chapterId);
                    request.getSession().setAttribute("initialNumQuestions", numQuestions);

                    // 7. Redirect tới câu hỏi đầu tiên (không cần index)
                    response.sendRedirect(request.getContextPath() + "/question?examId=" + examId);
                } else {
                    response.getWriter().println("Failed to retrieve questions from Python API.");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                // Nếu đã có examId, tiếp tục làm bài
                response.sendRedirect(request.getContextPath() + "/question?examId=" + examId);
            }
        } catch (Exception e) {
            response.getWriter().println("An error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String examId = request.getParameter("examId");
        String action = request.getParameter("action");

        if (examId == null || examId.isEmpty()) {
            response.getWriter().println("Exam ID is missing.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if ("continue".equals(action)) {
            // Xử lý thêm câu hỏi mới hoặc kết thúc
            String continueChoice = request.getParameter("continueChoice");

            if ("yes".equals(continueChoice)) {
                String additionalQuestionsStr = request.getParameter("additionalQuestions");
                int additionalQuestions;
                try {
                    additionalQuestions = Integer.parseInt(additionalQuestionsStr);
                    if (additionalQuestions <= 0) {
                        response.getWriter().println("Số lượng câu hỏi phải lớn hơn 0.");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                } catch (NumberFormatException e) {
                    response.getWriter().println("Số lượng câu hỏi không hợp lệ.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                Integer numQuestions = (Integer) request.getSession().getAttribute("numQuestions");
                String chapterId = (String) request.getSession().getAttribute("chapterId");

                try {
                    String vectorDbPath = materialDAO.getVectorDbPathFromChapterId(chapterId);
                    if (vectorDbPath == null || vectorDbPath.isEmpty()) {
                        response.getWriter().println("Vector DB path not found for chapter: " + chapterId);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    // Gọi API Python lấy thêm câu hỏi
                    String questionsJson = callPythonAPI(vectorDbPath, 2, additionalQuestions);

                    if (questionsJson != null && !questionsJson.isEmpty()) {
                        List<Question> questions = parseQuestionsFromJson(questionsJson);

                        // Lưu vào DB
                        for (Question question : questions) {
                            question.setExamId(examId);
                            questionDAO.insertQuestion(question);
                        }

                        // Cập nhật lại numQuestions trong session
                        request.getSession().setAttribute("numQuestions", numQuestions + additionalQuestions);

                        // Chuyển hướng về câu hỏi tiếp theo chưa trả lời
                        response.sendRedirect(request.getContextPath() + "/question?examId=" + examId);
                    } else {
                        response.getWriter().println("Không thể lấy câu hỏi từ API Python.");
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                } catch (Exception e) {
                    response.getWriter().println("Đã xảy ra lỗi: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    e.printStackTrace();
                }
            } else if ("no".equals(continueChoice)) {
                // Kết thúc bài kiểm tra
                response.sendRedirect(request.getContextPath() + "/result?examId=" + examId);
            } else {
                response.getWriter().println("Lựa chọn không hợp lệ.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            // Xử lý nộp đáp án một câu hỏi
            String questionId = request.getParameter("questionId");
            String studentAnswer = request.getParameter("answer");

            if (questionId == null || questionId.isEmpty()) {
                response.getWriter().println("Question ID is missing.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Question question = questionDAO.getQuestionById(questionId);
            if (question == null) {
                response.getWriter().println("Không tìm thấy câu hỏi.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            question.setStudentAnswer(studentAnswer);
            questionDAO.updateQuestionWithAnswer(question);

            boolean isCorrect = studentAnswer != null && studentAnswer.equals(question.getCorrectOption());
            if (isCorrect) {
                examDAO.incrementScore(examId);
            }

            // Kiểm tra còn câu chưa trả lời không
            List<Question> unansweredQuestions = questionDAO.getUnansweredQuestionsByExamId(examId);
            if (!unansweredQuestions.isEmpty()) {
                // Còn câu chưa trả lời, chuyển tiếp
                response.sendRedirect(request.getContextPath() + "/question?examId=" + examId);
            } else {
                // Hết, hỏi tiếp tục hay không
                response.sendRedirect(request.getContextPath() + "/continueExam.jsp?examId=" + examId);
            }
        }
    }

    // --- Các hàm phụ trợ giữ nguyên ---

    private String callPythonAPI(String vectorDbPath, int difficulty, int numQuestions) throws IOException {
        URL url = new URL(PYTHON_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String postData = "vector_db_path=" + vectorDbPath + "&difficulty=" + difficulty + "&numQuestions=" + numQuestions;

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    private List<Question> parseQuestionsFromJson(String questionsJson) {
        List<Question> questions = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(questionsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String questionText = jsonObject.getString("question");
                int difficulty = jsonObject.getInt("difficulty");

                JSONArray choicesArray = jsonObject.getJSONArray("choices");
                String optionA = choicesArray.getString(0);
                String optionB = choicesArray.getString(1);
                String optionC = choicesArray.getString(2);
                String optionD = choicesArray.getString(3);

                String correctAnswer = jsonObject.getString("correct_answer");
                String explanation = jsonObject.getString("explanation");

                String correctOption = null;
                for (int j = 0; j < choicesArray.length(); j++) {
                    if (choicesArray.getString(j).equals(correctAnswer)) {
                        correctOption = switch (j) {
                            case 0 -> "A";
                            case 1 -> "B";
                            case 2 -> "C";
                            case 3 -> "D";
                            default -> null;
                        };
                        break;
                    }
                }

                if (correctOption == null) {
                    System.err.println("Correct answer not found in choices for question: " + questionText);
                    continue;
                }

                Question question = new Question();
                question.setId(UUID.randomUUID().toString());
                question.setContent(questionText);
                question.setOptionA(optionA);
                question.setOptionB(optionB);
                question.setOptionC(optionC);
                question.setOptionD(optionD);
                question.setCorrectOption(correctOption);
                question.setExplain(explanation);

                questions.add(question);
            }
        } catch (JSONException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        return questions;
    }

    private Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}
