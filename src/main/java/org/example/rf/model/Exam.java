package org.example.rf.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Exam {
    private String id;
    private String studentId;
    private String subjectId;
    private int score;
    private LocalDateTime submittedAt;
    private ArrayList<Question> questions;

    public Exam() {}

    public Exam(String id, String studentId, String subjectId, int score, LocalDateTime submittedAt, ArrayList<Question> questions) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.score = score;
        this.submittedAt = submittedAt;
        this.questions = questions;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public ArrayList<Question> getQuestions() { return questions; }
    public void setQuestions(ArrayList<Question> questions) { this.questions = questions; }
}
