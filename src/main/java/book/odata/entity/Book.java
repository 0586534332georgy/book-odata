package book.odata.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
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

}
