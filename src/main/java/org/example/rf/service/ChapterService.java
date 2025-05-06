package org.example.rf.service;

import org.example.rf.dao.ChapterDAO;
import org.example.rf.model.Chapter;

import java.util.List;

public class ChapterService {
    private ChapterDAO chapterDAO = new ChapterDAO();

    public List<Chapter> getChaptersBySubjectId(String subjectId) {
        return chapterDAO.getChaptersBySubjectId(subjectId);
    }
}
