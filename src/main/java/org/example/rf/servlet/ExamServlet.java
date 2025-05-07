package org.example.rf.servlet;

import org.example.rf.dao.LevelDAO;
import org.example.rf.dao.MaterialDAO;
import org.example.rf.dao.QuestionDAO;
import org.example.rf.dao.ExamDAO;
import org.example.rf.model.Level;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/exam")
public class ExamServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ExamServlet.class); // Logger
    private static final String PYTHON_API_URL = "http://localhost:8002/generate";
    private static final String PYTHON_THETA_API_URL = "http://localhost:8003/calculate_theta"; // New URL
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();
    private final ExamDAO examDAO = new ExamDAO();
    private final LevelDAO levelDAO = new LevelDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String chapterId = request.getParameter("chapterId");
        Integer numQuestionsParam = tryParseInt(request.getParameter("numQuestions"));
        String examId = (String) request.getSession().getAttribute("examId");

        logger.info("doGet called with chapterId={}, numQuestions={}, examId={}", chapterId, numQuestionsParam, examId); // Log
        if (chapterId == null || chapterId.isEmpty() || (numQuestionsParam == null && examId == null)) {
            response.getWriter().println("Chapter ID or number of questions is invalid.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Invalid request: Chapter ID or number of questions is invalid.");  //Log
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
                    logger.error("Vector DB path not found for chapter: {}", chapterId); //Log
                    return;
                }

                User user = (User) request.getSession().getAttribute("user");
                if (user == null) {
                    response.getWriter().println("User not logged in or session expired.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    logger.warn("User not logged in or session expired."); //Log
                    return;
                }
                String userId = user.getId();

                Level level = levelDAO.getLevelByStudentAndChapter(userId, chapterId);
                int difficulty = 1;
                if (level != null) {
                    difficulty = level.getLevel();
                }
                request.getSession().setAttribute("currentLevel", difficulty);
                logger.info("Starting new exam. User ID: {}, Chapter ID: {}, Initial difficulty: {}, Number of questions: {}", userId, chapterId, difficulty, numQuestions); // Log
                // 1. Gọi API Python sinh câu hỏi
                String questionsJson = callPythonAPI(vectorDbPath, difficulty, numQuestions);

                if (!questionsJson.isEmpty()) {
                    List<Question> questions = parseQuestionsFromJson(questionsJson);

                    // 2. Tạo examId
                    examId = UUID.randomUUID().toString();

                    // 3. Tạo Exam object
                    Exam exam = new Exam();
                    exam.setId(examId);
                    exam.setStudentId(userId);
                    exam.setChapterId(chapterId);
                    exam.setScore(0);
                    exam.setSubmittedAt(LocalDateTime.now());
                    // 4. Lưu Exam và Questions vào DB
                    boolean examInserted = examDAO.insertExam(exam);
                    if (!examInserted) {
                        response.getWriter().println("Failed to persist Exam to the database.");
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        logger.error("Failed to persist Exam to the database."); //Log
                        return;
                    }
                    logger.info("Exam created with ID: {}", examId);  //Log
                    for (Question q : questions) {
                        q.setExamId(examId);
                        questionDAO.insertQuestion(q);
                        logger.debug("Question inserted: {}", q.getId()); //Log
                    }

                    // 5. Lưu examId và số câu hỏi vào session
                    request.getSession().setAttribute("examId", examId);
                    request.getSession().setAttribute("numQuestions", numQuestions);
                    request.getSession().setAttribute("chapterId", chapterId);
                    request.getSession().setAttribute("initialNumQuestions", numQuestions);
                    logger.info("Exam {} started for user {}", examId, userId);  //Log

                    // 6. Redirect tới câu hỏi đầu tiên
                    response.sendRedirect(request.getContextPath() + "/question?examId=" + examId);
                } else {
                    response.getWriter().println("Failed to retrieve questions from Python API.");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    logger.error("Failed to retrieve questions from Python API.");  //Log
                }
            } else {
                // Nếu đã có examId, tiếp tục làm bài
                logger.info("Exam {} resumed.", examId);   //Log
                response.sendRedirect(request.getContextPath() + "/question?examId=" + examId);
            }
        } catch (Exception e) {
            response.getWriter().println("An error occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("An error occurred: {}", e.getMessage(), e);  //Log
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String examId = request.getParameter("examId");
        String action = request.getParameter("action");
        logger.info("doPost called with examId={}, action={}", examId, action); // Log

        if (examId == null || examId.isEmpty()) {
            response.getWriter().println("Exam ID is missing.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Exam ID is missing.");// Log
            return;
        }

        if ("continue".equals(action)) {
            // Xử lý thêm câu hỏi mới hoặc kết thúc
            String continueChoice = request.getParameter("continueChoice");
            logger.info("Continue choice: {}", continueChoice); // Log

            if ("yes".equals(continueChoice)) {
                String additionalQuestionsStr = request.getParameter("additionalQuestions");
                int additionalQuestions;
                try {
                    additionalQuestions = Integer.parseInt(additionalQuestionsStr);
                    if (additionalQuestions <= 0) {
                        response.getWriter().println("Số lượng câu hỏi phải lớn hơn 0.");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        logger.warn("Invalid additionalQuestions value: {}", additionalQuestionsStr); //Log
                        return;
                    }
                } catch (NumberFormatException e) {
                    response.getWriter().println("Số lượng câu hỏi không hợp lệ.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    logger.warn("Invalid additionalQuestions format: {}", additionalQuestionsStr, e); //Log
                    return;
                }

                Integer numQuestions = (Integer) request.getSession().getAttribute("numQuestions");
                String chapterId = (String) request.getSession().getAttribute("chapterId");

                try {
                    String vectorDbPath = materialDAO.getVectorDbPathFromChapterId(chapterId);
                    if (vectorDbPath == null || vectorDbPath.isEmpty()) {
                        response.getWriter().println("Vector DB path not found for chapter: " + chapterId);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        logger.error("Vector DB path not found for chapter: {}", chapterId); //Log
                        return;
                    }

                    User user = (User) request.getSession().getAttribute("user");
                    if (user == null) {
                        response.getWriter().println("User not logged in or session expired.");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        logger.warn("User not logged in or session expired.");  //Log
                        return;
                    }
                    String userId = user.getId();

                    // Tính toán level trước khi gọi API tạo câu hỏi
                    List<Question> answeredQuestions = questionDAO.getAnsweredQuestionsByExamId(examId);
                    JSONArray questionsData = new JSONArray();
                    for (Question question : answeredQuestions) {
                        JSONObject questionData = new JSONObject();
                        questionData.put("difficulty", question.getDifficulty());
                        questionData.put("isCorrect", question.getStudentAnswer() != null && question.getStudentAnswer().equals(question.getCorrectOption()));
                        questionsData.put(questionData);
                        logger.debug("Question data for theta calculation: {}", questionData.toString());
                    }

                    String thetaJson;
                    try {
                        thetaJson = callPythonAPIForTheta(questionsData.toString());
                        logger.info("Theta API response: {}", thetaJson);  //Log
                    } catch (IOException e) {
                        response.getWriter().println("Lỗi khi gọi API Python tính theta: " + e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        logger.error("Lỗi khi gọi API Python tính theta: {}", e.getMessage(), e); //Log
                        return;
                    }

                    JSONObject thetaResponse = new JSONObject(thetaJson);
                    int levelValue = thetaResponse.getInt("level");
                    logger.info("New level calculated: {}", levelValue);  //Log

                    // Lưu level vào DB (giống như khi kết thúc bài kiểm tra)
                    Level existingLevel = levelDAO.getLevelByStudentAndChapter(userId, chapterId);
                    if (existingLevel != null) {
                        existingLevel.setLevel(levelValue);
                        levelDAO.updateLevel(existingLevel);
                        logger.info("Level updated for user {} and chapter {}: New level = {}", userId, chapterId, levelValue);  //Log
                    } else {
                        Level level = new Level();
                        level.setId(UUID.randomUUID().toString());
                        level.setStudentId(userId);
                        level.setChapterId(chapterId);
                        level.setLevel(levelValue);
                        levelDAO.insertLevel(level);
                        logger.info("New level created for user {} and chapter {}: Level = {}", userId, chapterId, levelValue); //Log
                    }

                    request.getSession().setAttribute("currentLevel", levelValue);
                    // Gọi API Python lấy thêm câu hỏi, sử dụng level mới tính toán được
                    String questionsJson = callPythonAPI(vectorDbPath, levelValue, additionalQuestions);
                    logger.info("Generating {} new questions with difficulty {}", additionalQuestions, levelValue); //Log

                    if (!questionsJson.isEmpty()) {
                        List<Question> questions = parseQuestionsFromJson(questionsJson);

                        // Lưu vào DB
                        for (Question question : questions) {
                            question.setExamId(examId);
                            questionDAO.insertQuestion(question);
                            logger.debug("Question inserted: {}", question.getId()); // Log
                        }

                        // Cập nhật lại numQuestions trong session
                        request.getSession().setAttribute("numQuestions", numQuestions + additionalQuestions);
                        logger.info("Number of questions updated in session: {}", numQuestions + additionalQuestions);  //Log

                        // Chuyển hướng về câu hỏi tiếp theo chưa trả lời
                        response.sendRedirect(request.getContextPath() + "/question?examId=" + examId);
                    } else {
                        response.getWriter().println("Không thể lấy câu hỏi từ API Python.");
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        logger.error("Không thể lấy câu hỏi từ API Python."); //Log
                    }
                } catch (Exception e) {
                    response.getWriter().println("Đã xảy ra lỗi: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    logger.error("Đã xảy ra lỗi: {}", e.getMessage(), e); // Log
                    e.printStackTrace();
                }
            } else if ("no".equals(continueChoice)) {
                // Kết thúc bài kiểm tra

                // 1. Lấy danh sách câu hỏi đã trả lời
                List<Question> answeredQuestions = questionDAO.getAnsweredQuestionsByExamId(examId);
                logger.info("Exam {} completed. {} questions answered.", examId, answeredQuestions.size());  //Log

                // 2. Chuẩn bị dữ liệu cho Python API (ví dụ: JSON)
                JSONArray questionsData = new JSONArray();
                for (Question question : answeredQuestions) {
                    JSONObject questionData = new JSONObject();
                    questionData.put("difficulty", question.getDifficulty()); // hoặc sử dụng thuộc tính a, b, c tương ứng
                    questionData.put("isCorrect", question.getStudentAnswer() != null && question.getStudentAnswer().equals(question.getCorrectOption())); // true hoặc false
                    questionsData.put(questionData);
                    logger.debug("Question data for theta calculation: {}", questionData.toString());  // Log
                }

                // 3. Gọi API Python và nhận về theta/level
                String thetaJson;
                try {
                    thetaJson = callPythonAPIForTheta(questionsData.toString()); // Sử dụng hàm mới
                    logger.info("Theta API response: {}", thetaJson); //Log
                } catch (IOException e) {
                    response.getWriter().println("Lỗi khi gọi API Python tính theta: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    logger.error("Lỗi khi gọi API Python tính theta: {}", e.getMessage(), e);  //Log
                    return; // Dừng xử lý nếu có lỗi
                }

                JSONObject thetaResponse = new JSONObject(thetaJson);
                int levelValue = thetaResponse.getInt("level");
                logger.info("Final level calculated: {}", levelValue); // Log

                // 4. Lưu level vào DB
                User user = (User) request.getSession().getAttribute("user");
                String userId = user.getId();
                String chapterId = (String) request.getSession().getAttribute("chapterId");


                Level existingLevel = levelDAO.getLevelByStudentAndChapter(userId, chapterId);
                if (existingLevel != null) {
                    existingLevel.setLevel(levelValue);
                    levelDAO.updateLevel(existingLevel); // Cập nhật level nếu đã tồn tại
                    logger.info("Level updated for user {} and chapter {}: New level = {}", userId, chapterId, levelValue);  //Log
                } else {
                    Level level = new Level();
                    level.setId(UUID.randomUUID().toString()); // Thêm dòng này
                    level.setStudentId(userId);
                    level.setChapterId(chapterId);
                    level.setLevel(levelValue);
                    levelDAO.insertLevel(level); // Thêm mới level nếu chưa tồn tại
                    logger.info("New level created for user {} and chapter {}: Level = {}", userId, chapterId, levelValue); //Log
                }

                // Chuyển hướng đến trang kết quả
                response.sendRedirect(request.getContextPath() + "/result?examId=" + examId);
            } else {
                response.getWriter().println("Lựa chọn không hợp lệ.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                logger.warn("Invalid continue choice: {}", continueChoice);  //Log
            }
        } else {
            // Xử lý nộp đáp án một câu hỏi
            String questionId = request.getParameter("questionId");
            String studentAnswer = request.getParameter("answer");
            logger.info("Answer submitted for question {}: Answer = {}", questionId, studentAnswer);  //Log

            if (questionId == null || questionId.isEmpty()) {
                response.getWriter().println("Question ID is missing.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                logger.warn("Question ID is missing.");  //Log
                return;
            }

            Question question = questionDAO.getQuestionById(questionId);
            if (question == null) {
                response.getWriter().println("Không tìm thấy câu hỏi.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                logger.warn("Question not found: {}", questionId);  //Log
                return;
            }

            question.setStudentAnswer(studentAnswer);
            questionDAO.updateQuestionWithAnswer(question);
            logger.debug("Question {} updated with answer: {}", questionId, studentAnswer); //Log

            boolean isCorrect = studentAnswer != null && studentAnswer.equals(question.getCorrectOption());
            if (isCorrect) {
                examDAO.incrementScore(examId);
                logger.info("Correct answer for question {}. Score incremented.", questionId);   //Log
            } else {
                logger.info("Incorrect answer for question {}.", questionId);  //Log
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

    private String callPythonAPI(String vectorDbPath, int difficulty, int numQuestions) throws IOException {
        URL url = new URL(PYTHON_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Fix the parameter name to match what the Python API expects
        String postData = "vector_db_path=" + vectorDbPath + "&difficulty=" + difficulty + "&num_questions=" + numQuestions;

        logger.info("Calling Python API to generate questions: URL={}, vectorDbPath={}, difficulty={}, num_questions={}",
                PYTHON_API_URL, vectorDbPath, difficulty, numQuestions);

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
            logger.info("Python API response received, length: {}", response.toString().length());
            logger.debug("Python API response first 200 chars: {}",
                    response.toString().length() > 200 ? response.toString().substring(0, 200) : response.toString());
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    private List<Question> parseQuestionsFromJson(String questionsJson) {
        List<Question> questions = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(questionsJson);
            logger.info("Parsing questions from JSON. Number of questions: {}", jsonArray.length());  //Log
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
                question.setDifficulty(difficulty);
                questions.add(question);
                logger.debug("Question parsed from JSON: {}", question.getId());  //Log
            }
        } catch (JSONException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            logger.error("Error parsing JSON: {}", e.getMessage(), e);   //Log
            e.printStackTrace();
            return new ArrayList<>();
        }
        return questions;
    }

    private String callPythonAPIForTheta(String questionsData) throws IOException {
        URL url = new URL(PYTHON_THETA_API_URL); // Use the correct URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json"); // Thay đổi content type
        logger.info("Calling Python API for theta calculation: URL={}, data={}", PYTHON_THETA_API_URL, questionsData);   //Log

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = questionsData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            logger.info("Python Theta API response: {}", response.toString());  //Log
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    private Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}