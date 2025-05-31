package book.odata.controller;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import book.odata.odata.BookEdmProvider;
import book.odata.odata.BookEntityCollectionProcessor;

@RestController
@RequestMapping("/odata")
public class ODataController {

    @Autowired
    private BookEdmProvider edmProvider;

    @Autowired
    private BookEntityCollectionProcessor entityCollectionProcessor;

    @RequestMapping(value = "/**")
    public void process(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Create OData Handler and configure it with EDM provider
            OData odata = OData.newInstance();
            ServiceMetadata edm = odata.createServiceMetadata(edmProvider, new ArrayList<>());
            ODataHttpHandler handler = odata.createHandler(edm);

            // Register processors
            handler.register(entityCollectionProcessor);

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