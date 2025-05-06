package org.example.rf.service;

import org.example.rf.dao.SubjectDAO;
import org.example.rf.model.Subject;

import java.util.List;

public class SubjectService {
    private SubjectDAO subjectDAO;

    public SubjectService() {
        this.subjectDAO = new SubjectDAO();
    }

    public List<Subject> getAllSubjects() {
        return subjectDAO.getAllSubjects();
    }

    public Subject getSubjectById(String id) {
        return subjectDAO.getSubjectById(id);
    }

    // Nếu muốn có logic xử lý thêm sau này (filter, sort, cache...)
    // bạn có thể mở rộng ngay tại đây
}
