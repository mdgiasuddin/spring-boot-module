package com.example.springneo4j.service;

import com.example.springneo4j.dto.request.BookRequest;
import com.example.springneo4j.entity.BookNode;
import com.example.springneo4j.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public BookNode createBook(BookRequest request) {
        BookNode book = new BookNode();
        book.setName(request.name());
        book.setAuthor(request.author());
        return bookRepository.save(book);
    }

    public List<BookNode> getAllBooks() {
        return bookRepository.findAll();
    }

    public BookNode getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }
}
