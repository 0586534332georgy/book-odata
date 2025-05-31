package book.odata.dto;

import java.util.Comparator;
import java.util.List;

import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.springframework.util.comparator.Comparators;

import book.odata.api.BookGenreEnum;
import book.odata.entity.Book;
import book.odata.entity.BookCredential;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class BookDto {
	
    private String title;
    private String authorSurname;
    private String authorName;
    private BookGenreEnum bookGenre;

    public static BookDto build(Book book) {
        BookDto dto = new BookDto();
        dto.setTitle(book.getTitle());
        dto.setAuthorSurname(book.getAuthorSurname());
        dto.setAuthorName(book.getAuthorName());
        
        BookCredential credential = book.getCredential();
        if (credential != null) {
            dto.setBookGenre(credential.getBookGenre());
        }

        return dto;
    }
    
    public static List<BookDto> sortBooks(List<BookDto> books, List<OrderByItem> orderByItems) {
        return books.stream().sorted(getComparator(orderByItems)).toList();    	
    }

    public static Comparator<BookDto> getComparator(List<OrderByItem> orderByItems) {
        Comparator<BookDto> finalComparator = (a, b) -> 0;

        for (OrderByItem item : orderByItems) {
            String prop = ((Member) item.getExpression())
            	    .getResourcePath()
            	    .getUriResourceParts()
            	    .get(0)
            	    .getSegmentValue()
            	    .toLowerCase();

            Comparator<BookDto> current = switch (prop) {
                case "title"        -> Comparator.comparing(dto -> stripLeadingArticle(dto.getTitle()));
                case "authorsurname"-> Comparator.comparing(BookDto::getAuthorSurname);
                case "authorname"   -> Comparator.comparing(BookDto::getAuthorName);
                case "bookgenre"    -> Comparator.comparing(dto -> dto.getBookGenre().toString());
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
        if (s == null) return "";
        return s.replaceFirst("(?i)^(a |an |the )", "").trim();
    }
}