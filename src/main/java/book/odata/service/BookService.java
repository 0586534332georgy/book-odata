package book.odata.service;

import java.util.List;

import book.odata.entity.Book;


public interface BookService {

	public List<Book> getAll();

	public int setBookFree(String title);

	public int setBookReserved(String title);

}