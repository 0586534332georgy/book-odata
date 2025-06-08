package book.odata.config;

import javax.persistence.EntityManagerFactory;

import org.apache.olingo.odata2.core.servlet.ODataServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ODataServletConfig {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public ServletRegistrationBean<ODataServlet> odataServlet() {
        System.out.println("Creating OData servlet, EMF is null: " + (entityManagerFactory == null));        
        
        ODataServiceFactory.setEntityManagerFactory(entityManagerFactory);
        
        ServletRegistrationBean<ODataServlet> srb = new ServletRegistrationBean<>(new ODataServlet(), "/odata.svc/*");
        srb.setLoadOnStartup(1);
        
        srb.addInitParameter("org.apache.olingo.odata2.service.factory", 
                            "book.odata.config.ODataServiceFactory");
        
        return srb;
    }
}