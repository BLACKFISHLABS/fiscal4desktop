package io.github.blackfishlabs.fiscal4desktop;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackageClasses = FiscalApplication.class)
@EnableJpaRepositories(basePackages = {"io.github.blackfishlabs.fiscal4desktop.domain.repository"})
@EntityScan(basePackages = {"io.github.blackfishlabs.fiscal4desktop.domain.model"})
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.description}")
    private String appDescription;
    @Value("${spring.application.version}")
    private String appVersion;

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(getApiInfo());
    }

    private Info getApiInfo() {
        Contact contact = new Contact().name("Jeferson Cruz").url("http://blackfishlabs.github.io").email("dev.blackfishlabs@gmail.com");
        return new Info()
                .title("Fiscal Api")
                .description("Api de integração do Forza com outros sistemas")
                .version("1.0.0")
                .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0"))
                .contact(contact);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
