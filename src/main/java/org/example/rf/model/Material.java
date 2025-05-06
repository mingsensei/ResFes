package org.example.rf.model;

public class Material {
    private String materialId;
    private String title;
    private String pdfPath; // path to the stored PDF on Java server
    private String chapterId;
    private String type;
    private String vectorDbPath; // Path to the vector DB generated

    public Material(String materialId, String title, String pdfPath, String chapterId, String type, String vectorDbPath) {
        this.materialId = materialId;
        this.title = title;
        this.pdfPath = pdfPath;
        this.chapterId = chapterId;
        this.type = type;
        this.vectorDbPath = vectorDbPath;
    }

    // Getters and setters for all fields
    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVectorDbPath() {
        return vectorDbPath;
    }

    public void setVectorDbPath(String vectorDbPath) {
        this.vectorDbPath = vectorDbPath;
    }
}