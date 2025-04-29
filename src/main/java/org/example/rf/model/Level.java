package org.example.rf.model;

public class Level {
    private String id;
    private String studentId;
    private String subjectId;
    private int level;
    private int currentExp;
    private int requiredExp;

    public Level() {}

    public Level(String id, String studentId, String subjectId, int level, int currentExp, int requiredExp) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.level = level;
        this.currentExp = currentExp;
        this.requiredExp = requiredExp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getCurrentExp() { return currentExp; }
    public void setCurrentExp(int currentExp) { this.currentExp = currentExp; }
    public int getRequiredExp() { return requiredExp; }
    public void setRequiredExp(int requiredExp) { this.requiredExp = requiredExp; }
}