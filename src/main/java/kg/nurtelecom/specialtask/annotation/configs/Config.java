package kg.nurtelecom.specialtask.annotation.configs;

import kg.nurtelecom.specialtask.annotation.resolver.RequestsArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration class for setting up custom argument resolvers in Spring MVC.
 */
@Configuration
public class Config implements WebMvcConfigurer {

    private final ConversionService conversionService;

    /**
     * Constructor for Config class.
     *
     * @param conversionService the conversion service used for argument resolution
     */
    public Config(@Lazy ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Adds custom argument resolvers to the Spring MVC configuration.
     *
     * @param resolvers the list of argument resolvers to which the custom resolver is added
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestsArgumentResolver(conversionService));
    }
}
