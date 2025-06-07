package book.odata.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import book.odata.api.BookGenreEnum;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "book_credential")
public class BookCredential {
	@Id
	@Column(name = "id_book")
	private Integer id;	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "book_genre")
	private BookGenreEnum bookGenre; 
	
	@Column(name = "pages_amount")
	private Integer pagesAmount;
	
	@OneToOne
	@JoinColumn(name = "id_book")
	@JsonBackReference
	private Book book;
	
}
