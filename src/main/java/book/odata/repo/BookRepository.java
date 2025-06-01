package book.odata.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import book.odata.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
	
	Optional<Book> findByTitle(String title);

}
