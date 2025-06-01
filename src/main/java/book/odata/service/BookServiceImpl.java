package book.odata.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import book.odata.entity.Book;
import book.odata.entity.BookStatus;
import book.odata.repo.BookRepository;
import book.odata.repo.CredentialRepository;
import book.odata.repo.StatusRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	final private BookRepository bookRepo;
	
	final private CredentialRepository credentialRepo;

	final private StatusRepository statusRepo;
	
	@Override
	public List<Book> getAll() {
		return bookRepo.findAll();
	}  
	
	public Book findById(int id) {
		return bookRepo.findById(id).orElseThrow(() -> new RuntimeException("id not found"));
	}



    @Override
    public int setBookReserved(String title) {
    	int res = 0;
        Optional<Book> bookOpt = bookRepo.findByTitle(title);
        if (bookOpt.isPresent()) {
            BookStatus status = bookOpt.get().getStatus();
            if (!status.getReservedStatus()) {
                status.setReservedStatus(true);
                status.setReservedDate(java.time.LocalDate.now());
                statusRepo.save(status);
                res = 1;
            }
        }
        return res;
    }

    @Override
    public int setBookFree(String title) {
    	int res = 0;
        Optional<Book> bookOpt = bookRepo.findByTitle(title);
        if (bookOpt.isPresent()) {
            BookStatus status = bookOpt.get().getStatus();
            if (status.getReservedStatus()) {
                status.setReservedStatus(false);
                status.setReservedDate(null);
                statusRepo.save(status);
                res = 1;
            }
        }
        return res;
    }


}