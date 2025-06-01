package book.odata.odata;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import book.odata.dto.BookCredentialsDto;
import book.odata.dto.BookDto;
import book.odata.entity.Book;
import book.odata.service.BookService;

@Component
public class BookEntityProcessor implements EntityProcessor {

    @Autowired
    private BookService bookService;

    private OData odata;
    private ServiceMetadata serviceMetadata;

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    @Override
    public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {

        // Retrieve the requested EntitySet from the uriInfo
        List<UriResource> resourceParts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourceParts.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        // Extract the key from the request
        List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
        Entity entity = getData(edmEntitySet, keyPredicates);

        if (entity == null) {
            throw new ODataApplicationException("Entity not found", HttpStatusCode.NOT_FOUND.getStatusCode(),
                    Locale.ENGLISH);
        }

        // Serialize
        ODataSerializer serializer = odata.createSerializer(responseFormat);
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

        EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();
        SerializerResult serializerResult = serializer.entity(serviceMetadata, edmEntityType, entity, options);
        InputStream serializedContent = serializerResult.getContent();

        // Configure the response object
        response.setContent(serializedContent);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    private Entity getData(EdmEntitySet edmEntitySet, List<UriParameter> keyPredicates) {
        String entitySetName = edmEntitySet.getName();
        
        if (BookEdmProvider.ES_BOOKS_NAME.equals(entitySetName)) {
            String title = getKeyValue(keyPredicates, "title");
            List<Book> books = bookService.getAll();
            for (Book book : books) {
                if (book.getTitle().equals(title)) {
                    return createBookEntity(book);
                }
            }

        
        return null;
    }

    private String getKeyValue(List<UriParameter> keyPredicates, String keyName) {
        for (UriParameter keyPredicate : keyPredicates) {
            if (keyPredicate.getName().equals(keyName)) {
                return keyPredicate.getText().replace("'", ""); // Remove quotes for string values
            }
        }
        return null;
    }

    private Entity createBookEntity(Book book) {
        Entity entity = new Entity()
        		.addProperty(new Property(null, "id", ValueType.PRIMITIVE, book.getId()))
                .addProperty(new Property(null, "title", ValueType.PRIMITIVE, book.getTitle()))
                .addProperty(new Property(null, "authorSurname", ValueType.PRIMITIVE, book.getAuthorSurname()))
                .addProperty(new Property(null, "authorName", ValueType.PRIMITIVE, book.getAuthorName()))
                .addProperty(new Property(null, "bookGenre", ValueType.PRIMITIVE, 
                		book.getCredential() != null ? book.getCredential().getBookGenre().toString() : null));
        
        return entity;
    }

   

    @Override
    public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
            ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
        // Implementation for POST requests - creating new entities
        throw new ODataApplicationException("Create operation is not supported yet", 
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
    }

    @Override
    public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
            ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
        // Implementation for PUT/PATCH requests - updating entities
        throw new ODataApplicationException("Update operation is not supported yet", 
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
    }

    @Override
    public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
            throws ODataApplicationException, ODataLibraryException {
        // Implementation for DELETE requests
        throw new ODataApplicationException("Delete operation is not supported yet", 
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
    }
    
}