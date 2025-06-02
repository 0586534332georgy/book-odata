package book.odata.odata;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.processor.ActionPrimitiveProcessor;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.springframework.stereotype.Component;

import book.odata.service.BookService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookActionProcessor implements ActionPrimitiveProcessor {

    private final BookService bookService;
    private OData odata;
    private ServiceMetadata serviceMetadata;

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }
    
    @Override
    public void processActionPrimitive(ODataRequest request, ODataResponse response,
                                        UriInfo uriInfo, ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException, DeserializerException, SerializerException {

        UriResource resource = uriInfo.getUriResourceParts().get(0);

        if (resource instanceof UriResourceAction actionResource) {
            String actionName = actionResource.getAction().getName();

            Map<String, Parameter> params = odata.createDeserializer(requestFormat)
                    .actionParameters(request.getBody(), actionResource.getAction())
                    .getActionParameters();

            String title = Optional.ofNullable(params.get("title"))
                    .map(Parameter::getValue)
                    .map(Object::toString)
                    .orElseThrow(() -> new ODataApplicationException("Missing 'title' parameter",
                            HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH));

            int result = switch (actionName) {
                case "ReserveBook" -> bookService.setBookReserved(title);
                case "FreeBook" -> bookService.setBookFree(title);
                default -> throw new ODataApplicationException("Unknown action: " + actionName,
                        HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
            };

			String json = "{\"value\":" + result + "}";
			response.setContent(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        } else {
            throw new ODataApplicationException("Expected action resource",
                    HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
        }
    }
   
}