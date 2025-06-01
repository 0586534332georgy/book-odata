package book.odata.odata;

import book.odata.entity.Book;
import book.odata.service.BookService;
import lombok.RequiredArgsConstructor;
import org.apache.olingo.commons.api.data.*;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.*;
import org.apache.olingo.server.api.uri.*;
import org.apache.olingo.server.api.uri.queryoption.CustomQueryOption;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookEntityCollectionProcessor implements EntityCollectionProcessor {

    private final BookService bookService;
    private OData odata;
    private ServiceMetadata serviceMetadata;

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    @Override
    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {
        UriResourceEntitySet entitySet = (UriResourceEntitySet) uriInfo.getUriResourceParts().get(0);
        List<Book> result = bookService.getAll();

        String bookGenre = null;
        Integer minPages = null;
        Integer maxPages = null;
        boolean descending = false;

        for (CustomQueryOption option : uriInfo.getCustomQueryOptions()) {
            switch (option.getName()) {
                case "bookGenre" -> bookGenre = option.getText();
                case "minPages" -> minPages = Integer.parseInt(option.getText());
                case "maxPages" -> maxPages = Integer.parseInt(option.getText());
                case "desc" -> descending = Boolean.parseBoolean(option.getText());
            }
        }

        if (bookGenre != null) {
        	final String genreFilter = bookGenre;
            result = result.stream().filter(b ->
                b.getCredential() != null &&
                genreFilter.equalsIgnoreCase(b.getCredential().getBookGenre().toString())
            ).collect(Collectors.toList());
        }
        if (minPages != null) {
        	final int minPagesFilter = minPages;
            result = result.stream().filter(b ->
                b.getCredential() != null && b.getCredential().getPagesAmount() >= minPagesFilter
            ).collect(Collectors.toList());
        }
        if (maxPages != null) {
        	final int maxPagesFilter = maxPages;
            result = result.stream().filter(b ->
                b.getCredential() != null && b.getCredential().getPagesAmount() <= maxPagesFilter
            ).collect(Collectors.toList());
        }
        if (descending) {
            result.sort((a, b) -> b.getCredential().getPagesAmount() - a.getCredential().getPagesAmount());
        }

        EntityCollection entityCollection = new EntityCollection();
        for (Book book : result) {
            try {
                Entity entity = BookEntityMapper.toEntity(book, entitySet.getEntityType(), odata);
                entityCollection.getEntities().add(entity);
            } catch (URISyntaxException e) {
                throw new ODataApplicationException("Invalid URI", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
            }
        };

        UriResourceEntitySet uriEntitySet = (UriResourceEntitySet) uriInfo.getUriResourceParts().get(0);
        
        EdmEntitySet edmEntitySet = uriEntitySet.getEntitySet();

        EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
            .build();

        SerializerResult serializerResult = odata
            .createSerializer(responseFormat)
            .entityCollection(serviceMetadata, edmEntitySet.getEntityType(), entityCollection, opts);
        	
        response.setContent(serializerResult.getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

}
