package book.odata.entity;

import java.util.Comparator;
import java.util.List;

import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "book_library")
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_book")
	private Integer id;

	@Column(name = "author_surname")
	private String authorSurname;

	@Column(name = "author_name")
	private String authorName;

	@Column(name = "bookname")
	private String title;

	@OneToOne(mappedBy = "book")
	@JsonManagedReference
	private BookCredential credential;

	@OneToOne(mappedBy = "book")
	@JsonManagedReference
	private BookStatus status;

	public static List<Book> sortBooks(List<Book> books, List<OrderByItem> orderByItems) {
		return books.stream().sorted(getComparator(orderByItems)).toList();
	}

	public static Comparator<Book> getComparator(List<OrderByItem> orderByItems) {
        Comparator<Book> finalComparator = (a, b) -> 0;

        for (OrderByItem item : orderByItems) {
            String prop = ((Member) item.getExpression())
            	    .getResourcePath()
            	    .getUriResourceParts()
            	    .get(0)
            	    .getSegmentValue()
            	    .toLowerCase();

            Comparator<Book> current = switch (prop) {
                case "title" 		-> Comparator.comparing(
                							book -> stripLeadingArticle(book.getTitle()),
                							Comparator.nullsLast(String::compareToIgnoreCase)
                							);
                case "authorsurname"-> Comparator.comparing(
                							Book::getAuthorSurname,
											Comparator.nullsLast(String::compareToIgnoreCase)
											);
                case "authorname" 	-> Comparator.comparing(
                							Book::getAuthorName,
											Comparator.nullsLast(String::compareToIgnoreCase)
											);               
                case "bookgenre"    -> Comparator.comparing(
                							book -> book.getCredential() != null ? 
                								book.getCredential().getBookGenre().toString() : null,
											Comparator.nullsLast(String::compareToIgnoreCase)
											);
                case "pagesamount"	-> Comparator.comparing(
                							book -> book.getCredential() != null ? 
                								book.getCredential().getPagesAmount() : null,
                							Comparator.nullsLast(Integer::compareTo)
                							);
                default             -> throw new IllegalArgumentException("Unknown property: " + prop);
            };

            if (item.isDescending()) {
                current = current.reversed();
            }

            finalComparator = finalComparator.thenComparing(current);
        }

        return finalComparator;

    }

	private static String stripLeadingArticle(String s) {
		if (s == null)
			return "";
		return s.replaceFirst("(?i)^(a |an |the )", "").trim();
	}

}
