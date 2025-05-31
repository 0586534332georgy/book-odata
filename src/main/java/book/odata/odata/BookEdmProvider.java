package book.odata.odata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.springframework.stereotype.Component;

@Component
public class BookEdmProvider extends CsdlAbstractEdmProvider {

    // Service Namespace
    public static final String NAMESPACE = "BookLibrary";

    // EDM Container
    public static final String CONTAINER_NAME = "Container";
    public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    // Entity Types Names
    public static final String ET_BOOK_NAME = "Book";
    public static final FullQualifiedName ET_BOOK_FQN = new FullQualifiedName(NAMESPACE, ET_BOOK_NAME);

    public static final String ET_BOOK_CREDENTIALS_NAME = "BookCredentials";
    public static final FullQualifiedName ET_BOOK_CREDENTIALS_FQN = new FullQualifiedName(NAMESPACE, ET_BOOK_CREDENTIALS_NAME);

    public static final String ET_BOOK_RESERVED_NAME = "BookReserved";
    public static final FullQualifiedName ET_BOOK_RESERVED_FQN = new FullQualifiedName(NAMESPACE, ET_BOOK_RESERVED_NAME);

    // Entity Set Names
    public static final String ES_BOOKS_NAME = "Books";
    public static final String ES_BOOK_CREDENTIALS_NAME = "BookCredentials";
    public static final String ES_BOOK_RESERVED_NAME = "BookReserved";

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
        if (entityTypeName.equals(ET_BOOK_FQN)) {
            return getBookEntityType();
        } else if (entityTypeName.equals(ET_BOOK_CREDENTIALS_FQN)) {
            return getBookCredentialsEntityType();
        } else if (entityTypeName.equals(ET_BOOK_RESERVED_FQN)) {
            return getBookReservedEntityType();
        }
        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {
        if (entityContainer.equals(CONTAINER)) {
            if (entitySetName.equals(ES_BOOKS_NAME)) {
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ES_BOOKS_NAME);
                entitySet.setType(ET_BOOK_FQN);
                return entitySet;
            } else if (entitySetName.equals(ES_BOOK_CREDENTIALS_NAME)) {
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ES_BOOK_CREDENTIALS_NAME);
                entitySet.setType(ET_BOOK_CREDENTIALS_FQN);
                return entitySet;
            } else if (entitySetName.equals(ES_BOOK_RESERVED_NAME)) {
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ES_BOOK_RESERVED_NAME);
                entitySet.setType(ET_BOOK_RESERVED_FQN);
                return entitySet;
            }
        }
        return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() {
        // create EntitySets
        List<CsdlEntitySet> entitySets = new ArrayList<>();
        entitySets.add(getEntitySet(CONTAINER, ES_BOOKS_NAME));
        entitySets.add(getEntitySet(CONTAINER, ES_BOOK_CREDENTIALS_NAME));
        entitySets.add(getEntitySet(CONTAINER, ES_BOOK_RESERVED_NAME));

        // create EntityContainer
        CsdlEntityContainer entityContainer = new CsdlEntityContainer();
        entityContainer.setName(CONTAINER_NAME);
        entityContainer.setEntitySets(entitySets);

        return entityContainer;
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {
        if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(CONTAINER);
            return entityContainerInfo;
        }
        return null;
    }

    @Override
    public List<CsdlSchema> getSchemas() {
        // create Schema
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);

        // add EntityTypes
        List<CsdlEntityType> entityTypes = new ArrayList<>();
        entityTypes.add(getEntityType(ET_BOOK_FQN));
        entityTypes.add(getEntityType(ET_BOOK_CREDENTIALS_FQN));
        entityTypes.add(getEntityType(ET_BOOK_RESERVED_FQN));
        schema.setEntityTypes(entityTypes);

        // add EntityContainer
        schema.setEntityContainer(getEntityContainer());

        // finally
        List<CsdlSchema> schemas = new ArrayList<>();
        schemas.add(schema);

        return schemas;
    }

    private CsdlEntityType getBookEntityType() {
        // create EntityType properties
        CsdlProperty title = new CsdlProperty().setName("title")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty authorSurname = new CsdlProperty().setName("authorSurname")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty authorName = new CsdlProperty().setName("authorName")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty bookGenre = new CsdlProperty().setName("bookGenre")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

        // configure EntityType
        CsdlEntityType entityType = new CsdlEntityType();
        entityType.setName(ET_BOOK_NAME);
        entityType.setProperties(Arrays.asList(title, authorSurname, authorName, bookGenre));
        entityType.setKey(Collections.singletonList(new CsdlPropertyRef().setName("title")));

        return entityType;
    }

    private CsdlEntityType getBookCredentialsEntityType() {
        CsdlProperty id = new CsdlProperty().setName("id")
                .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
        CsdlProperty authorSurname = new CsdlProperty().setName("authorSurname")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty authorName = new CsdlProperty().setName("authorName")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty title = new CsdlProperty().setName("title")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty bookGenre = new CsdlProperty().setName("bookGenre")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty pagesAmount = new CsdlProperty().setName("pagesAmount")
                .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());

        CsdlEntityType entityType = new CsdlEntityType();
        entityType.setName(ET_BOOK_CREDENTIALS_NAME);
        entityType.setProperties(Arrays.asList(id, authorSurname, authorName, title, bookGenre, pagesAmount));
        entityType.setKey(Collections.singletonList(new CsdlPropertyRef().setName("id")));

        return entityType;
    }

    private CsdlEntityType getBookReservedEntityType() {
        CsdlProperty title = new CsdlProperty().setName("title")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty authorSurname = new CsdlProperty().setName("authorSurname")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty authorName = new CsdlProperty().setName("authorName")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty bookGenre = new CsdlProperty().setName("bookGenre")
                .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        CsdlProperty reservedDate = new CsdlProperty().setName("reservedDate")
                .setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName());

        CsdlEntityType entityType = new CsdlEntityType();
        entityType.setName(ET_BOOK_RESERVED_NAME);
        entityType.setProperties(Arrays.asList(title, authorSurname, authorName, bookGenre, reservedDate));
        entityType.setKey(Collections.singletonList(new CsdlPropertyRef().setName("title")));

        return entityType;
    }
}