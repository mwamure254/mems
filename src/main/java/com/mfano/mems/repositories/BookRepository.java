package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Book;

public interface BookRepository extends JpaRepository<Book , Long>{
    
}
