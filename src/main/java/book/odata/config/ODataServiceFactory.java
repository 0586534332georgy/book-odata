package book.odata.config;

import javax.persistence.EntityManagerFactory;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.core.ODataJPAContextImpl;

import org.springframework.stereotype.Component;

@Component
public class ODataServiceFactory extends ODataJPAServiceFactory {

    private static final String PERSISTENCE_UNIT_NAME = "default";
    private static EntityManagerFactory emf;

    public static void setEmf(EntityManagerFactory emfInput) {
        emf = emfInput;
    }

    @Override
    public ODataJPAContext initializeODataJPAContext() {
        ODataJPAContextImpl context = new ODataJPAContextImpl();
        context.setEntityManagerFactory(emf);
        context.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        return context;
    }

}
