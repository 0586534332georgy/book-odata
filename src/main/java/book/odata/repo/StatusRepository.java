package book.odata.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import book.odata.entity.BookStatus;

public interface StatusRepository extends JpaRepository<BookStatus, Integer> {

	List<BookStatus> findByReservedStatusTrue();

	List<BookStatus> findByReservedStatusFalse();	

}