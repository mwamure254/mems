package com.mfano.mems.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.models.Student;
import com.mfano.mems.models.Transfer;
import com.mfano.mems.repositories.StudentRepository;
import com.mfano.mems.repositories.TransferRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final AuditService auditService;
    private final StudentService studentService;
    private final StudentRepository studentRepository;

    @Transactional
    public void saveIn(Long branch, Transfer entity, RedirectAttributes red) {
        if (entity != null) {
            entity.setActive(true);
            transferRepository.save(entity);
            auditService.record(branch, "CREATE_TRANSFER", "SUCCESS", "Created tranfer: " + entity.getType());
            red.addFlashAttribute("message", "Trasfer created successfully!");

        } else {
            red.addFlashAttribute("error", "Invalid transfer details.");
        }
    }

    @Transactional
    public void saveOut(Long branch, Transfer entity, RedirectAttributes red) {
        if (entity != null) {
            Student existing = studentService.findById(entity.getStudent());
            existing.setActive(false);
            existing.setStatus("Transferred");
            studentRepository.save(existing);
            auditService.record(branch, "TRANSFER_STUDENT", "SUCCESS", "Transferred student");

            entity.setActive(true);
            transferRepository.save(entity);
            auditService.record(branch, "CREATE_TRANSFER", "SUCCESS", "Created tranfer: " + entity.getType());
            red.addFlashAttribute("message", "Trasfer created successfully!");

        } else {
            red.addFlashAttribute("error", "Invalid transfer details.");
        }
    }

    public List<Transfer> findByBranch(Long id) {
        return transferRepository.findByBranch(id);
    }

    public List<Transfer> findByBranchAndType(Long id, String filter) {
        return transferRepository.findByBranchAndType(id,filter);
    }

}
