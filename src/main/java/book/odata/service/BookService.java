package book.odata.service;

import java.util.List;

import book.odata.api.BookGenreEnum;
import book.odata.dto.BookCredentialsDto;
import book.odata.dto.BookDto;
import book.odata.dto.BookReservedDto;
import book.odata.entity.Book;


public interface BookService {

	public List<Book> getAll();

	public List<BookDto> getAll2();

	public List<BookCredentialsDto> getBooksByGenre(BookGenreEnum bookGenre);

	public List<BookCredentialsDto> getBooksByGenreAndPages(BookGenreEnum bookGenre, int min, int max);

	public List<BookCredentialsDto> getBooksByGenreAndPagesOrdered(BookGenreEnum bookGenre, int min, int max);

	public List<BookDto> getFreeBooks();

	public List<BookReservedDto> getReservedBooks();

	public int setBookFree(String title);

	public int setBookReserved(String title);

}