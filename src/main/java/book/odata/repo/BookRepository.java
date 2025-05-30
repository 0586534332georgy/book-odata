package book.odata.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import book.odata.api.BookGenreEnum;
import book.odata.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
	
	List<Book> findByCredential_BookGenre(BookGenreEnum bookGenre);

	List<Book> findByCredential_BookGenreAndCredential_PagesAmountBetween(BookGenreEnum bookGenre, int min, int max);

	List<Book> findByCredential_BookGenreAndCredential_PagesAmountBetweenOrderByCredential_PagesAmountAsc(BookGenreEnum bookGenre,
			int min, int max);

	Optional<Book> findByTitle(String title);

}
