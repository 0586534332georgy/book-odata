package book.odata.odata.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import book.odata.api.BookGenreEnum;
import book.odata.entity.Book;
import book.odata.service.BookService;


@Service
public class ODataBookService {

    @Autowired
    private BookService bookService;

    public List<Book> getBooks(String filter, String orderBy, Integer top, Integer skip) {
        List<Book> books = bookService.getAll();
                
        if (filter != null && !filter.isEmpty()) {
            books = books.stream()
                .filter(book -> applyFilter(book, filter))
                .collect(Collectors.toList());
        }
        

        if (skip != null && skip > 0) {
            books = books.stream().skip(skip).collect(Collectors.toList());
        }
        
        if (top != null && top > 0) {
            books = books.stream().limit(top).collect(Collectors.toList());
        }
        
        return books;
    }


//    public List<BookCredentialsDto> getBookCredentials(BookGenreEnum genre, Integer minPages, Integer maxPages) {
//        if (genre != null && minPages != null && maxPages != null) {
//            return bookService.getBooksByGenreAndPages(genre, minPages, maxPages);
//        } else if (genre != null) {
//            return bookService.getBooksByGenre(genre);
//        }
//        
//        // Возвращаем пустой список или все книги
//        return List.of();
//    }


    private boolean applyFilter(Book book, String filter) {   
        if (filter.contains("title eq")) {
            String title = filter.split("'")[1];
            return book.getTitle().equals(title);
        }
                
        if (filter.contains("bookGenre eq")) {
            String genre = filter.split("'")[1];
            return book.getCredential() != null && book.getCredential().getBookGenre().toString().equals(genre);
        }
        
        return true;
    }


    public Book findBook(String key, String keyType) {
        List<Book> books = bookService.getAll();
        
        if ("title".equals(keyType)) {
            return books.stream()
                .filter(book -> book.getTitle().equals(key))
                .findFirst()
                .orElse(null);
        }
        
        return null;
    }

}