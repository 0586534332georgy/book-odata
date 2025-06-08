package book.odata.config;

import javax.persistence.EntityManagerFactory;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.apache.olingo.odata2.jpa.processor.core.ODataJPAContextImpl;


public class ODataServiceFactory extends ODataJPAServiceFactory {

    private static final String PERSISTENCE_UNIT_NAME = "default";
    private static volatile EntityManagerFactory emf;

    public static synchronized void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        System.out.println("Setting EMF in ODataServiceFactory: " + (entityManagerFactory != null));
        ODataServiceFactory.emf = entityManagerFactory;
    }

    @Override
    public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException {
        System.out.println("ODataServiceFactory.initializeODataJPAContext() called");
        System.out.println("EMF is null: " + (emf == null));
        System.out.println("This object: " + this.getClass().getName() + "@" + Integer.toHexString(this.hashCode()));
        
        if (emf == null) {
            throw new RuntimeException("EntityManagerFactory is null in ODataServiceFactory");
        }
        
        ODataJPAContextImpl oDataJPAContext = new ODataJPAContextImpl();
        oDataJPAContext.setEntityManagerFactory(emf);
        oDataJPAContext.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        return oDataJPAContext;
    }

}
