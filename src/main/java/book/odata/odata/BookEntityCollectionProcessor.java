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
import org.apache.olingo.server.api.uri.queryoption.*;
import org.apache.olingo.server.core.uri.queryoption.FilterOptionImpl;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.*;

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
    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) 
            throws ODataApplicationException, SerializerException {
        
        try {
            UriResourceEntitySet entitySet = (UriResourceEntitySet) uriInfo.getUriResourceParts().get(0);
            List<Book> result = bookService.getAll();

            // Применяем фильтрацию
            if (uriInfo.getFilterOption() != null) {
                result = applyFilter(result, uriInfo.getFilterOption());
            }

            // Применяем сортировку
            if (uriInfo.getOrderByOption() != null) {
                result = Book.sortBooks(result, uriInfo.getOrderByOption().getOrders());
            }

            // Применяем пагинацию
            if (uriInfo.getSkipOption() != null) {
                int skip = uriInfo.getSkipOption().getValue();
                result = result.stream().skip(skip).toList();
            }

            if (uriInfo.getTopOption() != null) {
                int top = uriInfo.getTopOption().getValue();
                result = result.stream().limit(top).toList();
            }

            // Создаем коллекцию сущностей
            EntityCollection entityCollection = new EntityCollection();
            
            // Определяем необходимые расширения
            Set<String> expandedProperties = getExpandedProperties(uriInfo.getExpandOption());
            
            for (Book book : result) {
                Entity entity = BookEntityMapper.toEntity(book, entitySet.getEntityType(), odata, expandedProperties);
                entityCollection.getEntities().add(entity);
            }

            // Сериализация
            UriResourceEntitySet uriEntitySet = (UriResourceEntitySet) uriInfo.getUriResourceParts().get(0);
            EdmEntitySet edmEntitySet = uriEntitySet.getEntitySet();

            EntityCollectionSerializerOptions.Builder optionsBuilder = EntityCollectionSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet).build());

            if (uriInfo.getSelectOption() != null) {
                optionsBuilder.select(uriInfo.getSelectOption());
            }
            
            if (uriInfo.getExpandOption() != null) {
                optionsBuilder.expand(uriInfo.getExpandOption());
            }

            EntityCollectionSerializerOptions opts = optionsBuilder.build();

            SerializerResult serializerResult = odata
                .createSerializer(responseFormat)
                .entityCollection(serviceMetadata, edmEntitySet.getEntityType(), entityCollection, opts);

            response.setContent(serializerResult.getContent());
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

        } catch (URISyntaxException e) {
            throw new ODataApplicationException("Invalid URI", 
                HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH, e);
        } catch (Exception e) {
            throw new ODataApplicationException("Error processing request: " + e.getMessage(), 
                HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH, e);
        }
    }

    private List<Book> applyFilter(List<Book> books, FilterOption filterOption) throws ODataApplicationException {
        if (filterOption instanceof FilterOptionImpl filterImpl) {
            String filterExpression = filterImpl.getText();
            
            return books.stream().filter(book -> {
                try {
                    return evaluateFilterExpression(book, filterExpression);
                } catch (Exception e) {
                    return true; // В случае ошибки включаем элемент
                }
            }).toList();
        }
        
        return books;
    }

    private boolean evaluateFilterExpression(Book book, String expression) {
        // Упрощенная реализация для основных фильтров
        expression = expression.toLowerCase().trim();
        
        // Обработка фильтров с навигационными свойствами
        if (expression.contains("status/reservedstatus eq")) {
            String reservedStr = extractStringValue(expression, "status/reservedstatus eq");
            boolean reserved = Boolean.parseBoolean(reservedStr);
            return book.getStatus() != null && 
                   book.getStatus().getReservedStatus() != null &&
                   book.getStatus().getReservedStatus() == reserved;
        }
        
        if (expression.contains("credential/bookgenre eq")) {
            String genre = extractStringValue(expression, "credential/bookgenre eq");
            return book.getCredential() != null && 
                   book.getCredential().getBookGenre() != null &&
                   book.getCredential().getBookGenre().toString().equalsIgnoreCase(genre);
        }
        
        if (expression.contains("credential/pagesamount ge")) {
            Integer minPages = extractIntValue(expression, "credential/pagesamount ge");
            return book.getCredential() != null && 
                   book.getCredential().getPagesAmount() != null &&
                   book.getCredential().getPagesAmount() >= minPages;
        }
        
        if (expression.contains("credential/pagesamount le")) {
            Integer maxPages = extractIntValue(expression, "credential/pagesamount le");
            return book.getCredential() != null && 
                   book.getCredential().getPagesAmount() != null &&
                   book.getCredential().getPagesAmount() <= maxPages;
        }
        
        // Обработка прямых свойств (для обратной совместимости)
        if (expression.contains("bookgenre eq")) {
            String genre = extractStringValue(expression, "bookgenre eq");
            return book.getCredential() != null && 
                   book.getCredential().getBookGenre() != null &&
                   book.getCredential().getBookGenre().toString().equalsIgnoreCase(genre);
        }
        
        if (expression.contains("pagesamount ge")) {
            Integer minPages = extractIntValue(expression, "pagesamount ge");
            return book.getCredential() != null && 
                   book.getCredential().getPagesAmount() != null &&
                   book.getCredential().getPagesAmount() >= minPages;
        }
        
        if (expression.contains("pagesamount le")) {
            Integer maxPages = extractIntValue(expression, "pagesamount le");
            return book.getCredential() != null && 
                   book.getCredential().getPagesAmount() != null &&
                   book.getCredential().getPagesAmount() <= maxPages;
        }
        
        if (expression.contains("reservedstatus eq")) {
            String reservedStr = extractStringValue(expression, "reservedstatus eq");
            boolean reserved = Boolean.parseBoolean(reservedStr);
            return book.getStatus() != null && 
                   book.getStatus().getReservedStatus() != null &&
                   book.getStatus().getReservedStatus() == reserved;
        }
        
        if (expression.contains("title eq")) {
            String title = extractStringValue(expression, "title eq");
            return book.getTitle() != null && book.getTitle().equalsIgnoreCase(title);
        }
        
        return true;
    }

    private String extractStringValue(String expression, String operator) {
        int index = expression.indexOf(operator);
        if (index == -1) return "";
        
        String value = expression.substring(index + operator.length()).trim();
        // Убираем кавычки если есть
        if (value.startsWith("'") && value.endsWith("'")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    private Integer extractIntValue(String expression, String operator) {
        try {
            String valueStr = extractStringValue(expression, operator);
            return Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Set<String> getExpandedProperties(ExpandOption expandOption) {
        Set<String> expandedProperties = new HashSet<>();
        
        if (expandOption != null) {
            expandOption.getExpandItems().forEach(item -> {
                if (item.getResourcePath() != null && !item.getResourcePath().getUriResourceParts().isEmpty()) {
                    String propertyName = item.getResourcePath().getUriResourceParts().get(0).getSegmentValue();
                    expandedProperties.add(propertyName.toLowerCase());
                }
            });
        }
        
        return expandedProperties;
    }
}