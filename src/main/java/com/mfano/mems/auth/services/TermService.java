package com.mfano.mems.auth.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mems.auth.models.Term;
import com.mfano.mems.auth.repositories.TermRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TermService {
    private final TermRepository termRepository;

    public List<Term> findAll() {
        return termRepository.findAll();
    }

}
