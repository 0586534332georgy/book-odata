package book.odata.odata;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;

import book.odata.dto.BookReservedDto;

public interface BooksHandler {

	public static List<BookReservedDto> sortBooks(List<BookReservedDto> books, List<OrderByItem> orderByItems) {
		return books.stream().sorted((book1, book2) -> {
			for (OrderByItem orderByItem : orderByItems) {
				String propertyName = ((UriResourcePrimitiveProperty) orderByItem.getExpression()).getProperty()
						.getName();

				int comparison = compareBooksByProperty(book1, book2, propertyName);

				if (orderByItem.isDescending()) {
					comparison = -comparison;
				}

				if (comparison != 0) {
					return comparison;
				}
			}
			return 0;
		}).collect(Collectors.toList());
	}

	public static int compareBooksByProperty(BookReservedDto book1, BookReservedDto book2, String propertyName) {
		switch (propertyName.toLowerCase()) {
		case "title":
			return book1.getTitle().compareTo(book2.getTitle());
		case "authorsurname":
			return book1.getAuthorSurname().compareTo(book2.getAuthorSurname());
		case "authorname":
			return book1.getAuthorName().compareTo(book2.getAuthorName());
		case "bookgenre":
			String genre1 = book1.getBookGenre() != null ? book1.getBookGenre().toString() : "";
			String genre2 = book2.getBookGenre() != null ? book2.getBookGenre().toString() : "";
			return genre1.compareTo(genre2);
		default:
			return 0;
		}
	}

}
