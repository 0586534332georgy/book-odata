package book.odata.odata;

import book.odata.entity.Book;
import book.odata.entity.BookCredential;
import book.odata.entity.BookStatus;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.OData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class BookEntityMapper {

    public static Entity toEntity(Book book, EdmEntityType edmEntityType, OData odata) throws URISyntaxException {
        return toEntity(book, edmEntityType, odata, Set.of());
    }

    public static Entity toEntity(Book book, EdmEntityType edmEntityType, OData odata, Set<String> expandedProperties) throws URISyntaxException {
        Entity entity = new Entity();

        // Основные свойства Book
        entity.addProperty(new Property(null, "id", ValueType.PRIMITIVE, book.getId()));
        entity.addProperty(new Property(null, "title", ValueType.PRIMITIVE, book.getTitle()));
        entity.addProperty(new Property(null, "authorName", ValueType.PRIMITIVE, book.getAuthorName()));
        entity.addProperty(new Property(null, "authorSurname", ValueType.PRIMITIVE, book.getAuthorSurname()));

        // Навигационные свойства для Credential
        if (expandedProperties.contains("credential")) {
            if (book.getCredential() != null) {
                // Полное расширение - включаем данные
                Entity credentialEntity = toCredentialEntity(book.getCredential(), odata);
                Link credentialLink = new Link();
                credentialLink.setTitle("credential");
                credentialLink.setInlineEntity(credentialEntity);
                entity.getNavigationLinks().add(credentialLink);
            } else {
                // Явно указываем null для расширенного свойства
                Link credentialLink = new Link();
                credentialLink.setTitle("credential");
                credentialLink.setInlineEntity(null);
                entity.getNavigationLinks().add(credentialLink);
            }
        } else if (book.getCredential() != null) {
            // Только ссылка, если не расширяем
            Link credentialLink = new Link();
            credentialLink.setTitle("credential");
            credentialLink.setHref("BookCredentials(" + book.getCredential().getId() + ")");
            entity.getNavigationLinks().add(credentialLink);
        }

        // Навигационные свойства для Status
        if (expandedProperties.contains("status")) {
            if (book.getStatus() != null) {
                // Полное расширение - включаем данные
                Entity statusEntity = toStatusEntity(book.getStatus(), odata);
                Link statusLink = new Link();
                statusLink.setTitle("status");
                statusLink.setInlineEntity(statusEntity);
                entity.getNavigationLinks().add(statusLink);
            } else {
                // Явно указываем null для расширенного свойства
                Link statusLink = new Link();
                statusLink.setTitle("status");
                statusLink.setInlineEntity(null);
                entity.getNavigationLinks().add(statusLink);
            }
        } else if (book.getStatus() != null) {
            // Только ссылка, если не расширяем
            Link statusLink = new Link();
            statusLink.setTitle("status");
            statusLink.setHref("BookStatuses(" + book.getStatus().getId() + ")");
            entity.getNavigationLinks().add(statusLink);
        }

        entity.setId(new URI("Books(" + book.getId() + ")"));
        return entity;
    }

    public static Entity toCredentialEntity(BookCredential credential, OData odata) throws URISyntaxException {
        Entity entity = new Entity();
        
        entity.addProperty(new Property(null, "id", ValueType.PRIMITIVE, credential.getId()));
        entity.addProperty(new Property(null, "bookGenre", ValueType.PRIMITIVE, 
            credential.getBookGenre() != null ? credential.getBookGenre().toString() : null));
        entity.addProperty(new Property(null, "pagesAmount", ValueType.PRIMITIVE, credential.getPagesAmount()));

        // Обратная ссылка на книгу
        if (credential.getBook() != null) {
            Link bookLink = new Link();
            bookLink.setTitle("book");
            bookLink.setHref("Books(" + credential.getBook().getId() + ")");
            entity.getNavigationLinks().add(bookLink);
        }

        entity.setId(new URI("BookCredentials(" + credential.getId() + ")"));
        return entity;
    }

    public static Entity toStatusEntity(BookStatus status, OData odata) throws URISyntaxException {
        Entity entity = new Entity();
        
        entity.addProperty(new Property(null, "id", ValueType.PRIMITIVE, status.getId()));
        entity.addProperty(new Property(null, "reservedStatus", ValueType.PRIMITIVE, status.getReservedStatus()));
        entity.addProperty(new Property(null, "reservedDate", ValueType.PRIMITIVE, status.getReservedDate()));
        entity.addProperty(new Property(null, "conditionStatus", ValueType.PRIMITIVE, 
            status.getConditionStatus() != null ? status.getConditionStatus().toString() : null));

        // Обратная ссылка на книгу
        if (status.getBook() != null) {
            Link bookLink = new Link();
            bookLink.setTitle("book");
            bookLink.setHref("Books(" + status.getBook().getId() + ")");
            entity.getNavigationLinks().add(bookLink);
        }

        entity.setId(new URI("BookStatuses(" + status.getId() + ")"));
        return entity;
    }
}