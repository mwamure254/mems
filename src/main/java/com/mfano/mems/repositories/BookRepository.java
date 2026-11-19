package com.mfano.mems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Book;
import com.mfano.mems.models.Category;

public interface BookRepository extends JpaRepository<Book , Long>{

    List<Book> findByBranch(Long b);

    List<Book> findByNameContaining(String key);

    List<Book> findByBranchAndCategory(Long id, Category cat);

    List<Book> findByBranchAndBorrowed(Long id, boolean b);

    
}
