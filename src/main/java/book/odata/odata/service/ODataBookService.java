package book.odata.odata.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import book.odata.api.BookGenreEnum;
import book.odata.dto.BookCredentialsDto;
import book.odata.dto.BookDto;
import book.odata.dto.BookReservedDto;
import book.odata.service.BookService;

/**
 * OData специфичный сервис для обработки запросов
 */
@Service
public class ODataBookService {

    @Autowired
    private BookService bookService;

    /**
     * Получить все книги с возможностью фильтрации
     */
    public List<BookDto> getBooks(String filter, String orderBy, Integer top, Integer skip) {
        List<BookDto> books = bookService.getAll2();
        
        // Простейшая фильтрация (для полноценной реализации нужен парсер OData запросов)
        if (filter != null && !filter.isEmpty()) {
            books = books.stream()
                .filter(book -> applySimpleFilter(book, filter))
                .collect(Collectors.toList());
        }
        
        // Пагинация
        if (skip != null && skip > 0) {
            books = books.stream().skip(skip).collect(Collectors.toList());
        }
        
        if (top != null && top > 0) {
            books = books.stream().limit(top).collect(Collectors.toList());
        }
        
        return books;
    }

    /**
     * Получить книги с дополнительной информацией
     */
    public List<BookCredentialsDto> getBookCredentials(BookGenreEnum genre, Integer minPages, Integer maxPages) {
        if (genre != null && minPages != null && maxPages != null) {
            return bookService.getBooksByGenreAndPages(genre, minPages, maxPages);
        } else if (genre != null) {
            return bookService.getBooksByGenre(genre);
        }
        
        // Возвращаем пустой список или все книги
        return List.of();
    }

    /**
     * Получить зарезервированные книги
     */
    public List<BookReservedDto> getReservedBooks() {
        return bookService.getReservedBooks();
    }

    /**
     * Простая фильтрация (для демонстрации)
     */
    private boolean applySimpleFilter(BookDto book, String filter) {
        // Пример: "title eq 'Some Book'"
        if (filter.contains("title eq")) {
            String title = filter.split("'")[1];
            return book.getTitle().equals(title);
        }
        
        // Пример: "bookGenre eq 'Fantasy'"
        if (filter.contains("bookGenre eq")) {
            String genre = filter.split("'")[1];
            return book.getBookGenre() != null && book.getBookGenre().toString().equals(genre);
        }
        
        return true;
    }

    /**
     * Найти книгу по ID или названию
     */
    public BookDto findBook(String key, String keyType) {
        List<BookDto> books = bookService.getAll2();
        
        if ("title".equals(keyType)) {
            return books.stream()
                .filter(book -> book.getTitle().equals(key))
                .findFirst()
                .orElse(null);
        }
        
        return null;
    }

    /**
     * Найти книгу с дополнительной информацией по ID
     */
    public BookCredentialsDto findBookCredentials(Integer id) {
        // Получаем все книги и ищем по ID (можно оптимизировать)
        List<BookCredentialsDto> books = bookService.getBooksByGenre(BookGenreEnum.Fantasy);
        books.addAll(bookService.getBooksByGenre(BookGenreEnum.Horror));
        books.addAll(bookService.getBooksByGenre(BookGenreEnum.Drama));
        books.addAll(bookService.getBooksByGenre(BookGenreEnum.Comedy));
        books.addAll(bookService.getBooksByGenre(BookGenreEnum.NonFiction));
        
        return books.stream()
            .filter(book -> book.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}