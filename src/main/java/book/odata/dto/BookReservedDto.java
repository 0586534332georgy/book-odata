package book.odata.dto;

import java.time.LocalDate;

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
}
