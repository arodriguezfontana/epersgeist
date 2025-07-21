package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.EpersgeistApp;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EpersgeistApp.class);
    }

}