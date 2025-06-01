package book.odata.controller;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
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

@RestController
@RequestMapping("/odata")
@RequiredArgsConstructor
public class ODataController {

    final private BookEdmProvider edmProvider;

    final private BookEntityProcessor entityProcessor;

    final private BookEntityCollectionProcessor collectionProcessor;
        

    @RequestMapping(value = "/**")
    public void process(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Create OData Handler and configure it with EDM provider
            OData odata = OData.newInstance();
            ServiceMetadata edm = odata.createServiceMetadata(edmProvider, new ArrayList<>());
            ODataHttpHandler handler = odata.createHandler(edm);

            // Register processors
            handler.register(entityProcessor);
            handler.register(collectionProcessor);

            // Let the handler do the work
            handler.process(new HttpServletRequestWrapper(request) {
                // Spring's HttpServletRequest doesn't behave exactly the same as the default servlet API
                // implementation with regard to URL processing. This fixes that.
                @Override
                public String getServletPath() {
                    return "/odata";
                }
            }, response);
        } catch (RuntimeException e) {
            throw new RuntimeException("OData processing failed", e);
        }
    }
}

