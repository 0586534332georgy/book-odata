package book.odata.odata;

import book.odata.entity.Book;
import book.odata.entity.BookCredential;
import book.odata.entity.BookStatus;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.OData;

import java.net.URI;
import java.net.URISyntaxException;

public class BookEntityMapper {

    public static Entity toEntity(Book book, EdmEntityType edmEntityType, OData odata) throws URISyntaxException {
        Entity entity = new Entity();

        entity.addProperty(new Property(null, "id",         ValueType.PRIMITIVE, book.getId()));
        entity.addProperty(new Property(null, "title",      ValueType.PRIMITIVE, book.getTitle()));
        entity.addProperty(new Property(null, "authorName", ValueType.PRIMITIVE, book.getAuthorName()));
        entity.addProperty(new Property(null, "authorSurname", ValueType.PRIMITIVE, book.getAuthorSurname()));

        if (book.getCredential() != null) {
            BookCredential c = book.getCredential();
            entity.addProperty(new Property(null, "bookGenre",    ValueType.PRIMITIVE, c.getBookGenre().toString()));
            entity.addProperty(new Property(null, "pagesAmount",  ValueType.PRIMITIVE, c.getPagesAmount()));
        }

        if (book.getStatus() != null) {
            BookStatus s = book.getStatus();
            entity.addProperty(new Property(null, "reservedStatus", ValueType.PRIMITIVE, s.getReservedStatus()));
            entity.addProperty(new Property(null, "reservedDate",   ValueType.PRIMITIVE, s.getReservedDate()));
            entity.addProperty(new Property(null, "conditionStatus",ValueType.PRIMITIVE, s.getConditionStatus().toString()));
        }

        entity.setId(new URI(book.getId().toString()));
        return entity;
    }
   
}

