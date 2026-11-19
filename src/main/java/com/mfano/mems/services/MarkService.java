package com.mfano.mems.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.models.Marks;
import com.mfano.mems.repositories.MarkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarkRepository markRepository;
    private final AuditService auditService;

    public List<Marks> findByStudentAndExam(Long student, Long exam) {
        return markRepository.findByStudentAndExam(student, exam);
    }

    public List<Marks> findByExamAndSubject(Long exam, Long subject) {
        return markRepository.findByExamAndSubject(exam, subject);
    }

    public Marks findByStudentAndExamAndSubject(Long student, Long exam, Long subject) {
        return markRepository.findByStudentAndExamAndSubject(student, exam, subject);
    }

    public void save(Long auth, Marks entity, RedirectAttributes red) {
        if (entity != null) {
            markRepository.save(entity);
            auditService.record(auth, "RECORD_MARKS", "SUCCESS", "Recorded marks");
        }
    }

}
