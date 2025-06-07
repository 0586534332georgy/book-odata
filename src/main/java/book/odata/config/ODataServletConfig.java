package book.odata.config;

import org.apache.olingo.odata2.core.servlet.ODataServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ODataServletConfig {

    @Bean
    public ServletRegistrationBean<ODataServlet> odataServlet() {
        ServletRegistrationBean<ODataServlet> srb = new ServletRegistrationBean<>(new ODataServlet(), "/odata.svc/*");
        srb.setLoadOnStartup(1);         

        return srb;
    }
}