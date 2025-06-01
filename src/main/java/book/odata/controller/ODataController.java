package book.odata.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import book.odata.odata.BookEdmProvider;
import book.odata.odata.BookEntityCollectionProcessor;
import book.odata.odata.BookEntityProcessor;
import book.odata.service.BookService;

@RestController
@RequestMapping("/odata")
@RequiredArgsConstructor
public class ODataController {

	private final BookService bookService;

    @RequestMapping(value = "/**")
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            OData odata = OData.newInstance();
            ServiceMetadata edm = odata.createServiceMetadata(new BookEdmProvider(), List.of());

            ODataHttpHandler handler = odata.createHandler(edm);
            handler.register(new BookEntityCollectionProcessor(bookService));
            handler.register(new BookEntityProcessor(bookService));
            handler.process(request, response);

        } catch (RuntimeException e) {
            response.setStatus(500);
        }
    }
}