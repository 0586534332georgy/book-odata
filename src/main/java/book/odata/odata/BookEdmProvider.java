package book.odata.odata;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.springframework.stereotype.Component;

@Component
public class BookEdmProvider extends CsdlAbstractEdmProvider {
    // Namespace
    public static final String NAMESPACE = "book.odata";
    
    // EDM Container
    public static final String CONTAINER_NAME = "Container";
    public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    // Entity types
    public static final String ET_BOOK_NAME = "Book";
    public static final String ET_CREDENTIAL_NAME = "BookCredential";
    public static final String ET_STATUS_NAME = "BookStatus";

    public static final FullQualifiedName ET_BOOK_FQN = new FullQualifiedName(NAMESPACE, ET_BOOK_NAME);
    public static final FullQualifiedName ET_CREDENTIAL_FQN = new FullQualifiedName(NAMESPACE, ET_CREDENTIAL_NAME);
    public static final FullQualifiedName ET_STATUS_FQN = new FullQualifiedName(NAMESPACE, ET_STATUS_NAME);

    // Entity sets
    public static final String ES_BOOKS_NAME = "Books";
    public static final String ES_CREDENTIALS_NAME = "BookCredentials";
    public static final String ES_STATUSES_NAME = "BookStatuses";

    @Override
    public List<CsdlSchema> getSchemas() {
        return List.of(
            new CsdlSchema()
                .setNamespace(NAMESPACE)
                .setEntityTypes(List.of(getEntityType(ET_BOOK_FQN), getEntityType(ET_CREDENTIAL_FQN), getEntityType(ET_STATUS_FQN)))
                .setEntityContainer(getEntityContainer())
        );
    }

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName fqn) {
        if (fqn.equals(ET_BOOK_FQN)) {
            return new CsdlEntityType()
                .setName(ET_BOOK_NAME)
                .setKey(List.of(new CsdlPropertyRef().setName("id")))
                .setProperties(List.of(
                    new CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()),
                    new CsdlProperty().setName("authorSurname").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                    new CsdlProperty().setName("authorName").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                    new CsdlProperty().setName("title").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                ))
                .setNavigationProperties(List.of(
                    new CsdlNavigationProperty().setName("Credential").setType(ET_CREDENTIAL_FQN).setNullable(true).setPartner("Book"),
                    new CsdlNavigationProperty().setName("Status").setType(ET_STATUS_FQN).setNullable(true).setPartner("Book")
                ));
        }

        if (fqn.equals(ET_CREDENTIAL_FQN)) {
            return new CsdlEntityType()
                .setName(ET_CREDENTIAL_NAME)
                .setKey(List.of(new CsdlPropertyRef().setName("id")))
                .setProperties(List.of(
                    new CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()),
                    new CsdlProperty().setName("bookGenre").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                    new CsdlProperty().setName("pagesAmount").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName())
                ))
                .setNavigationProperties(List.of(
                    new CsdlNavigationProperty().setName("Book").setType(ET_BOOK_FQN).setNullable(false).setPartner("Credential")
                ));
        }

        if (fqn.equals(ET_STATUS_FQN)) {
            return new CsdlEntityType()
                .setName(ET_STATUS_NAME)
                .setKey(List.of(new CsdlPropertyRef().setName("id")))
                .setProperties(List.of(
                    new CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()),
                    new CsdlProperty().setName("reservedStatus").setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName()),
                    new CsdlProperty().setName("reservedDate").setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName()),
                    new CsdlProperty().setName("conditionStatus").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                ))
                .setNavigationProperties(List.of(
                    new CsdlNavigationProperty().setName("Book").setType(ET_BOOK_FQN).setNullable(false).setPartner("Status")
                ));
        }

        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName container, String entitySetName) {
        if (container.equals(CONTAINER)) {
            return switch (entitySetName) {
                case ES_BOOKS_NAME -> new CsdlEntitySet().setName(ES_BOOKS_NAME).setType(ET_BOOK_FQN);
                case ES_CREDENTIALS_NAME -> new CsdlEntitySet().setName(ES_CREDENTIALS_NAME).setType(ET_CREDENTIAL_FQN);
                case ES_STATUSES_NAME -> new CsdlEntitySet().setName(ES_STATUSES_NAME).setType(ET_STATUS_FQN);
                default -> null;
            };
        }
        return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() {
        return new CsdlEntityContainer()
            .setName(CONTAINER_NAME)
            .setEntitySets(List.of(
                getEntitySet(CONTAINER, ES_BOOKS_NAME),
                getEntitySet(CONTAINER, ES_CREDENTIALS_NAME),
                getEntitySet(CONTAINER, ES_STATUSES_NAME)
            ));
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName name) {
        if (name == null || name.equals(CONTAINER)) {
            return new CsdlEntityContainerInfo().setContainerName(new FullQualifiedName(NAMESPACE, "Container"));
        }
        return null;
    }

   
}