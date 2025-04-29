package org.example.rf.model;

public class Chapter {
    private String id;
    private String title;
    private String subjectId;
    private int orderIndex;

    public Chapter() {}

    public Chapter(String id, String title, String subjectId, int orderIndex) {
        this.id = id;
        this.title = title;
        this.subjectId = subjectId;
        this.orderIndex = orderIndex;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
}
