package book.odata.dto;

import java.time.LocalDate;
import java.util.Comparator;

import book.odata.api.BookGenreEnum;
import book.odata.entity.Book;
import book.odata.entity.BookCredential;
import book.odata.entity.BookStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookReservedDto {

    private String title;
    private String authorSurname;
    private String authorName;
    private BookGenreEnum bookGenre;
    private LocalDate reservedDate;

    public static BookReservedDto build(Book book) {
        BookReservedDto dto = new BookReservedDto();
        
        dto.setTitle(book.getTitle());
        dto.setAuthorSurname(book.getAuthorSurname());
        dto.setAuthorName(book.getAuthorName());
        
        BookCredential credential = book.getCredential();
        if (credential != null) {
            dto.setBookGenre(credential.getBookGenre());
        }

        BookStatus status = book.getStatus();
        if (status != null) {
            dto.setReservedDate(status.getReservedDate());
        }

        return dto;
    }
    
    public static Comparator<BookReservedDto> getComparator(String propertyName, boolean descending) {
        Comparator<BookReservedDto> comparator;
        
        switch (propertyName.toLowerCase()) {
            case "title":
                comparator = Comparator.comparing(BookReservedDto::getTitle);
                break;
            case "authorsurname":
                comparator = Comparator.comparing(BookReservedDto::getAuthorSurname);
                break;
            case "authorname":
                comparator = Comparator.comparing(BookReservedDto::getAuthorName);
                break;
            case "bookgenre":
                comparator = Comparator.comparing(dto -> 
                    dto.getBookGenre() != null ? dto.getBookGenre().toString() : "");
                break;
            default:
                return (o1, o2) -> 0;
        }
        
        return descending ? comparator.reversed() : comparator;
    }
}
