package book.odata.odata;

import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
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
		UriResourceEntitySet entitySet = (UriResourceEntitySet) uriInfo.getUriResourceParts().get(0);
		int id = Integer.parseInt(entitySet.getKeyPredicates().get(0).getText());

		Book book = bookService.findById(id);
		Entity entity;
		try {
			entity = BookEntityMapper.toEntity(book, entitySet.getEntityType(), odata);
		} catch (URISyntaxException e) {
			throw new ODataApplicationException("Invalid URI for entity ID",
	                HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
		}

		SerializerResult serializerResult = odata.createSerializer(responseFormat).entity(serviceMetadata,
				entitySet.getEntityType(), entity, EntitySerializerOptions.with().build());

		response.setContent(serializerResult.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
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

	public void process(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, SerializerException, DeserializerException {

		UriResource resource = uriInfo.getUriResourceParts().get(0);

		if (resource instanceof UriResourceAction actionResource) {
			String actionName = actionResource.getAction().getName();

			Map<String, Parameter> params = OData.newInstance().createDeserializer(requestFormat)
					.actionParameters(request.getBody(), actionResource.getAction()).getActionParameters();

			String title = Optional.ofNullable(params.get("title")).map(Parameter::getValue).map(Object::toString)
					.orElseThrow(() -> new ODataApplicationException("Missing 'title'", 400, Locale.ENGLISH));

			int result = switch (actionName) {
			case "ReserveBook" -> bookService.setBookReserved(title);
			case "FreeBook" -> bookService.setBookFree(title);
			default -> throw new ODataApplicationException("Unknown action", 400, Locale.ENGLISH);
			};

			Entity responseEntity = new Entity();
			responseEntity.addProperty(new Property(null, "result", ValueType.PRIMITIVE, result));

			SerializerResult serialized = OData.newInstance().createSerializer(responseFormat).entity(null, null,
					responseEntity, EntitySerializerOptions.with().build());

			response.setContent(serialized.getContent());
			response.setStatusCode(200);
			response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
		}
	}

}