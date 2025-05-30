package book.odata.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import book.odata.entity.BookCredential;

public interface CredentialRepository extends JpaRepository<BookCredential, Integer> {


}
