package book.odata.odata;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
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
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import book.odata.dto.BookCredentialsDto;
import book.odata.dto.BookDto;
import book.odata.dto.BookReservedDto;
import book.odata.odata.service.ODataBookService;

@Component
public class BookEntityCollectionProcessor implements EntityCollectionProcessor {

    @Autowired
    private ODataBookService odataBookService;

    private OData odata;
    private ServiceMetadata serviceMetadata;

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    @Override
    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {

        // Retrieve the requested EntitySet from the uriInfo
        List<UriResource> resourceParts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourceParts.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        // Fetch the data from our data source
        EntityCollection entityCollection = getData(edmEntitySet);

        // Serialize
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
        SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entityCollection, opts);
        InputStream serializedContent = serializerResult.getContent();

        // Configure the response object
        response.setContent(serializedContent);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    private EntityCollection getData(EdmEntitySet edmEntitySet) {
        EntityCollection entityCollection = new EntityCollection();

        String entitySetName = edmEntitySet.getName();

        if (BookEdmProvider.ES_BOOKS_NAME.equals(entitySetName)) {
            List<BookDto> books = odataBookService.getBooks(null, null, null, null);
            for (BookDto book : books) {
                entityCollection.getEntities().add(createBookEntity(book));
            }
        } else if (BookEdmProvider.ES_BOOK_CREDENTIALS_NAME.equals(entitySetName)) {
            // Для примера, получаем все Fantasy книги
            List<BookCredentialsDto> books = odataBookService.getBookCredentials(book.odata.api.BookGenreEnum.Fantasy, null, null);
            for (BookCredentialsDto book : books) {
                entityCollection.getEntities().add(createBookCredentialsEntity(book));
            }
        } else if (BookEdmProvider.ES_BOOK_RESERVED_NAME.equals(entitySetName)) {
            List<BookReservedDto> books = odataBookService.getReservedBooks();
            for (BookReservedDto book : books) {
                entityCollection.getEntities().add(createBookReservedEntity(book));
            }
        }

        return entityCollection;
    }

    private Entity createBookEntity(BookDto book) {
        Entity entity = new Entity()
                .addProperty(new Property(null, "title", ValueType.PRIMITIVE, book.getTitle()))
                .addProperty(new Property(null, "authorSurname", ValueType.PRIMITIVE, book.getAuthorSurname()))
                .addProperty(new Property(null, "authorName", ValueType.PRIMITIVE, book.getAuthorName()))
                .addProperty(new Property(null, "bookGenre", ValueType.PRIMITIVE, 
                    book.getBookGenre() != null ? book.getBookGenre().toString() : null));

//        entity.setId(createId("Books", book.getTitle()));
        return entity;
    }

    private Entity createBookCredentialsEntity(BookCredentialsDto book) {
        Entity entity = new Entity()
                .addProperty(new Property(null, "id", ValueType.PRIMITIVE, book.getId()))
                .addProperty(new Property(null, "authorSurname", ValueType.PRIMITIVE, book.getAuthorSurname()))
                .addProperty(new Property(null, "authorName", ValueType.PRIMITIVE, book.getAuthorName()))
                .addProperty(new Property(null, "title", ValueType.PRIMITIVE, book.getTitle()))
                .addProperty(new Property(null, "bookGenre", ValueType.PRIMITIVE, 
                    book.getBookGenre() != null ? book.getBookGenre().toString() : null))
                .addProperty(new Property(null, "pagesAmount", ValueType.PRIMITIVE, book.getPagesAmount()));

//        entity.setId(createId("BookCredentials", book.getId()));
        return entity;
    }

    private Entity createBookReservedEntity(BookReservedDto book) {
        Entity entity = new Entity()
                .addProperty(new Property(null, "title", ValueType.PRIMITIVE, book.getTitle()))
                .addProperty(new Property(null, "authorSurname", ValueType.PRIMITIVE, book.getAuthorSurname()))
                .addProperty(new Property(null, "authorName", ValueType.PRIMITIVE, book.getAuthorName()))
                .addProperty(new Property(null, "bookGenre", ValueType.PRIMITIVE, 
                    book.getBookGenre() != null ? book.getBookGenre().toString() : null))
                .addProperty(new Property(null, "reservedDate", ValueType.PRIMITIVE, book.getReservedDate()));

//        entity.setId(createId("BookReserved", book.getId()));
//        entity.setId(createId("BookReserved", System.currentTimeMillis()));
        return entity;
    }

    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
        }
    }

    public static class ODataRuntimeException extends RuntimeException {
        public ODataRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}