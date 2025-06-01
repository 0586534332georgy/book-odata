package book.odata.odata;

import java.util.Arrays;
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

    // Actions
    public static final String ACTION_RESERVE_BOOK = "ReserveBook";
    public static final String ACTION_FREE_BOOK = "FreeBook";
    public static final FullQualifiedName ACTION_RESERVE_BOOK_FQN = new FullQualifiedName(NAMESPACE, ACTION_RESERVE_BOOK);
    public static final FullQualifiedName ACTION_FREE_BOOK_FQN = new FullQualifiedName(NAMESPACE, ACTION_FREE_BOOK);

    @Override
    public List<CsdlSchema> getSchemas() {
        return List.of(
            new CsdlSchema()
                .setNamespace(NAMESPACE)
                .setEntityTypes(List.of(getEntityType(ET_BOOK_FQN), getEntityType(ET_CREDENTIAL_FQN), getEntityType(ET_STATUS_FQN)))
                .setActions(Arrays.asList(getAction(ACTION_RESERVE_BOOK_FQN), getAction(ACTION_FREE_BOOK_FQN)))
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
                    new CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()).setNullable(false),
                    new CsdlProperty().setName("authorSurname").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setMaxLength(255),
                    new CsdlProperty().setName("authorName").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setMaxLength(255),
                    new CsdlProperty().setName("title").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setMaxLength(500)
                ))
                .setNavigationProperties(List.of(
                    new CsdlNavigationProperty().setName("credential").setType(ET_CREDENTIAL_FQN).setNullable(true).setPartner("book"),
                    new CsdlNavigationProperty().setName("status").setType(ET_STATUS_FQN).setNullable(true).setPartner("book")
                ));
        }

        if (fqn.equals(ET_CREDENTIAL_FQN)) {
            return new CsdlEntityType()
                .setName(ET_CREDENTIAL_NAME)
                .setKey(List.of(new CsdlPropertyRef().setName("id")))
                .setProperties(List.of(
                    new CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()).setNullable(false),
                    new CsdlProperty().setName("bookGenre").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setMaxLength(50),
                    new CsdlProperty().setName("pagesAmount").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName())
                ))
                .setNavigationProperties(List.of(
                    new CsdlNavigationProperty().setName("book").setType(ET_BOOK_FQN).setNullable(false).setPartner("credential")
                ));
        }

        if (fqn.equals(ET_STATUS_FQN)) {
            return new CsdlEntityType()
                .setName(ET_STATUS_NAME)
                .setKey(List.of(new CsdlPropertyRef().setName("id")))
                .setProperties(List.of(
                    new CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()).setNullable(false),
                    new CsdlProperty().setName("reservedStatus").setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName()),
                    new CsdlProperty().setName("reservedDate").setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName()),
                    new CsdlProperty().setName("conditionStatus").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setMaxLength(50)
                ))
                .setNavigationProperties(List.of(
                    new CsdlNavigationProperty().setName("book").setType(ET_BOOK_FQN).setNullable(false).setPartner("status")
                ));
        }

        return null;
    }

//    @Override
    public CsdlAction getAction(FullQualifiedName actionName) {
        if (actionName.equals(ACTION_RESERVE_BOOK_FQN)) {
            return new CsdlAction()
                .setName(ACTION_RESERVE_BOOK)
                .setParameters(List.of(
                    new CsdlParameter().setName("title").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false)
                ))
                .setReturnType(new CsdlReturnType().setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()));
        }
        
        if (actionName.equals(ACTION_FREE_BOOK_FQN)) {
            return new CsdlAction()
                .setName(ACTION_FREE_BOOK)
                .setParameters(List.of(
                    new CsdlParameter().setName("title").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false)
                ))
                .setReturnType(new CsdlReturnType().setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()));
        }
        
        return null;
    }

    @Override
    public List<CsdlAction> getActions(FullQualifiedName actionName) {
        if (actionName.equals(ACTION_RESERVE_BOOK_FQN)) {
            return List.of(getAction(ACTION_RESERVE_BOOK_FQN));
        }
        if (actionName.equals(ACTION_FREE_BOOK_FQN)) {
            return List.of(getAction(ACTION_FREE_BOOK_FQN));
        }
        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName container, String entitySetName) {
        if (container.equals(CONTAINER)) {
            return switch (entitySetName) {
                case ES_BOOKS_NAME -> new CsdlEntitySet()
                    .setName(ES_BOOKS_NAME)
                    .setType(ET_BOOK_FQN)
                    .setNavigationPropertyBindings(List.of(
                        new CsdlNavigationPropertyBinding().setPath("credential").setTarget(ES_CREDENTIALS_NAME),
                        new CsdlNavigationPropertyBinding().setPath("status").setTarget(ES_STATUSES_NAME)
                    ));
                case ES_CREDENTIALS_NAME -> new CsdlEntitySet()
                    .setName(ES_CREDENTIALS_NAME)
                    .setType(ET_CREDENTIAL_FQN)
                    .setNavigationPropertyBindings(List.of(
                        new CsdlNavigationPropertyBinding().setPath("book").setTarget(ES_BOOKS_NAME)
                    ));
                case ES_STATUSES_NAME -> new CsdlEntitySet()
                    .setName(ES_STATUSES_NAME)
                    .setType(ET_STATUS_FQN)
                    .setNavigationPropertyBindings(List.of(
                        new CsdlNavigationPropertyBinding().setPath("book").setTarget(ES_BOOKS_NAME)
                    ));
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
            ))
            .setActionImports(List.of(
                new CsdlActionImport().setName(ACTION_RESERVE_BOOK).setAction(ACTION_RESERVE_BOOK_FQN),
                new CsdlActionImport().setName(ACTION_FREE_BOOK).setAction(ACTION_FREE_BOOK_FQN)
            ));
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName name) {
        if (name == null || name.equals(CONTAINER)) {
            return new CsdlEntityContainerInfo().setContainerName(CONTAINER);
        }
        return null;
    }
}