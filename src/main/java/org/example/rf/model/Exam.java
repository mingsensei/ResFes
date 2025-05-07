package org.example.rf.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Exam {
    private String id;
    private String studentId;
    private String chapterId;
    private int score;
    private LocalDateTime submittedAt;

    public Exam() {}

    public Exam(String id, String studentId, String chapterId, int score, LocalDateTime submittedAt) {
        this.id = id;
        this.studentId = studentId;
        this.chapterId = chapterId;
        this.score = score;
        this.submittedAt = submittedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getChapterId() { return chapterId; } // ✅ renamed getter
    public void setChapterId(String chapterId) { this.chapterId = chapterId; } // ✅ renamed setter

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

}
