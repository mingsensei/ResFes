package org.example.rf.model;

public class Material {
    private String id;
    private String title;
    private String content;
    private String chapterId;
    private String type; // PDF, VIDEO, LINK

    public Material() {}

    public Material(String id, String title, String content, String chapterId, String type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.chapterId = chapterId;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getChapterId() { return chapterId; }
    public void setChapterId(String chapterId) { this.chapterId = chapterId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
