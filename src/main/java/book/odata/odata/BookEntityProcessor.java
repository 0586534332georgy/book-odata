package book.odata.odata;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.springframework.stereotype.Component;

import book.odata.entity.Book;
import book.odata.service.BookService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookEntityProcessor implements EntityProcessor {

	private final BookService bookService;
	private OData odata;
	private ServiceMetadata serviceMetadata;

	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}

	@Override
	public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
			throws ODataApplicationException, SerializerException {
		
		List<UriResource> resourceParts = uriInfo.getUriResourceParts();
		
		// Получаем основную сущность (Books)
		UriResourceEntitySet entitySet = (UriResourceEntitySet) resourceParts.get(0);
		int id = Integer.parseInt(entitySet.getKeyPredicates().get(0).getText());
		Book book = bookService.findById(id);
		
		if (book == null) {
			throw new ODataApplicationException("Book not found", 
				HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
		}

		// Проверяем, есть ли навигационное свойство
		if (resourceParts.size() > 1 && resourceParts.get(1) instanceof UriResourceNavigation) {
			UriResourceNavigation navigationResource = (UriResourceNavigation) resourceParts.get(1);
			String navigationProperty = navigationResource.getProperty().getName();
			
			handleNavigationProperty(book, navigationProperty, response, responseFormat, uriInfo);
		} else {
			// Обычный запрос к сущности
			handleEntityRequest(book, entitySet, response, responseFormat, uriInfo);
		}
	}

	private void handleNavigationProperty(Book book, String navigationProperty, ODataResponse response, 
			ContentType responseFormat, UriInfo uriInfo) throws ODataApplicationException, SerializerException {
		
		Entity entity = null;
		EdmEntitySet edmEntitySet = null;
		
		try {
			switch (navigationProperty.toLowerCase()) {
				case "credential":
					if (book.getCredential() == null) {
						throw new ODataApplicationException("Credential not found for this book", 
							HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
					}
					entity = BookEntityMapper.toCredentialEntity(book.getCredential(), odata);
					edmEntitySet = serviceMetadata.getEdm().getEntityContainer()
						.getEntitySet(BookEdmProvider.ES_CREDENTIALS_NAME);
					break;
					
				case "status":
					if (book.getStatus() == null) {
						throw new ODataApplicationException("Status not found for this book", 
							HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
					}
					entity = BookEntityMapper.toStatusEntity(book.getStatus(), odata);
					edmEntitySet = serviceMetadata.getEdm().getEntityContainer()
						.getEntitySet(BookEdmProvider.ES_STATUSES_NAME);
					break;
					
				default:
					throw new ODataApplicationException("Unknown navigation property: " + navigationProperty, 
						HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
			}
			
			ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build();
			EntitySerializerOptions opts = EntitySerializerOptions.with().contextURL(contextUrl).build();

			SerializerResult serializerResult = odata
				.createSerializer(responseFormat)
				.entity(serviceMetadata, edmEntitySet.getEntityType(), entity, opts);

			response.setContent(serializerResult.getContent());
			response.setStatusCode(HttpStatusCode.OK.getStatusCode());
			response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
			
		} catch (URISyntaxException e) {
			throw new ODataApplicationException("Invalid URI for navigation property",
				HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
		}
	}

	private void handleEntityRequest(Book book, UriResourceEntitySet entitySet, ODataResponse response, 
			ContentType responseFormat, UriInfo uriInfo) throws ODataApplicationException, SerializerException {
		
		// Определяем, какие свойства нужно расширить
		Set<String> expandedProperties = getExpandedProperties(uriInfo);
		
		Entity entity;
		try {
			entity = BookEntityMapper.toEntity(book, entitySet.getEntityType(), odata, expandedProperties);
		} catch (URISyntaxException e) {
			throw new ODataApplicationException("Invalid URI for entity ID",
				HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
		}

		EdmEntitySet edmEntitySet = entitySet.getEntitySet();
		
		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build();
		EntitySerializerOptions.Builder optsBuilder = EntitySerializerOptions.with().contextURL(contextUrl);
		
		// Добавляем опции select и expand если есть
		if (uriInfo.getSelectOption() != null) {
			optsBuilder.select(uriInfo.getSelectOption());
		}
		if (uriInfo.getExpandOption() != null) {
			optsBuilder.expand(uriInfo.getExpandOption());
		}
		
		EntitySerializerOptions opts = optsBuilder.build();

		SerializerResult serializerResult = odata
			.createSerializer(responseFormat)
			.entity(serviceMetadata, edmEntitySet.getEntityType(), entity, opts);

		response.setContent(serializerResult.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

	private Set<String> getExpandedProperties(UriInfo uriInfo) {
		Set<String> expandedProperties = new HashSet<>();
		
		if (uriInfo.getExpandOption() != null) {
			uriInfo.getExpandOption().getExpandItems().forEach(item -> {
				if (item.getResourcePath() != null && !item.getResourcePath().getUriResourceParts().isEmpty()) {
					String propertyName = item.getResourcePath().getUriResourceParts().get(0).getSegmentValue();
					expandedProperties.add(propertyName.toLowerCase());
				}
			});
		}
		
		return expandedProperties;
	}

	@Override
	public void createEntity(ODataRequest r, ODataResponse s, UriInfo u, ContentType requestFormat,
			ContentType responseFormat) {
	}

	@Override
	public void updateEntity(ODataRequest r, ODataResponse s, UriInfo u, ContentType requestFormat,
			ContentType responseFormat) {
	}

	@Override
	public void deleteEntity(ODataRequest r, ODataResponse s, UriInfo u) {
	}	
	
}