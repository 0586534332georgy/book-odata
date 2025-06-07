package book.odata.config;

import java.util.ArrayList;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import book.odata.odata.BookActionProcessor;
import book.odata.odata.BookEdmProvider;
import book.odata.odata.BookEntityCollectionProcessor;
import book.odata.odata.BookEntityProcessor;

@Configuration
public class ODataConfig {

    /**
     * CORS configuration для OData API
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Разрешить все источники (в продакшене следует ограничить)
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(false);
        
        source.registerCorsConfiguration("/odata/**", config);
        
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
    
    /**
     * Конфигурация OData Handler - создается один раз при старте приложения
     */
    @Bean
    public ODataHttpHandler oDataHttpHandler(
            BookEdmProvider edmProvider,
            BookEntityProcessor entityProcessor,
            BookEntityCollectionProcessor collectionProcessor,
            BookActionProcessor actionProcessor) {
        
        // Создаем OData instance и ServiceMetadata один раз
        OData odata = OData.newInstance();
        ServiceMetadata edm = odata.createServiceMetadata(edmProvider, new ArrayList<>());
        ODataHttpHandler handler = odata.createHandler(edm);

        // Регистрируем все процессоры один раз при создании бина
        handler.register(entityProcessor);
        handler.register(collectionProcessor);
        handler.register(actionProcessor);

        return handler;
    }
}