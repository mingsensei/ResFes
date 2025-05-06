package org.example.rf.model;

public class Question {
    private String id;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private String studentAnswer;
    private String examId;
    private String explain; // ✅ Thêm thuộc tính giải thích

    public Question() {}

    public Question(String id, String content, String optionA, String optionB, String optionC,
                    String optionD, String correctOption, String studentAnswer, String examId, String explain) {
        this.id = id;
        this.content = content;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.studentAnswer = studentAnswer;
        this.examId = examId;
        this.explain = explain; // ✅ Khởi tạo thuộc tính mới
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

    public String getStudentAnswer() { return studentAnswer; }
    public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getExplain() { return explain; } // ✅ Getter
    public void setExplain(String explain) { this.explain = explain; } // ✅ Setter
}
