package lejendary.oauth2.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;
import lejendary.oauth2.util.AppConstants;
import lejendary.oauth2.web.filter.CachingHttpHeadersFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.*;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 6:42 PM
 */

@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter implements ServletContextInitializer, EmbeddedServletContainerCustomizer {

    private final Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    private final Environment environment;
    private final OAuth2Properties oAuth2Properties;
    private final MetricRegistry metricRegistry;

    @Autowired
    public WebConfiguration(OAuth2Properties oAuth2Properties, Environment environment, MetricRegistry metricRegistry) {
        this.oAuth2Properties = oAuth2Properties;
        this.environment = environment;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(false);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {

    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (environment.getActiveProfiles().length != 0) {
            log.info("Web application configuration, using profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        }
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        initMetrics(servletContext, dispatcherTypes);
        if (environment.acceptsProfiles(AppConstants.SPRING_PROFILE_PRODUCTION)) {
            initCachingHttpHeadersFilter(servletContext, dispatcherTypes);
        }
        log.info("Web application fully configured");
    }

    /**
     * Initializes the caching HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(ServletContext servletContext,
                                              EnumSet<DispatcherType> dispatcherTypes) {
        log.debug("Registering Caching HTTP Headers Filter");
        FilterRegistration.Dynamic cachingHttpHeadersFilter =
                servletContext.addFilter("cachingHttpHeadersFilter",
                        new CachingHttpHeadersFilter(oAuth2Properties));

        cachingHttpHeadersFilter.addMappingForUrlPatterns(dispatcherTypes, true, "/css/**");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(dispatcherTypes, true, "/app/**");
        cachingHttpHeadersFilter.setAsyncSupported(true);
    }

    /**
     * Initializes Metrics.
     */
    private void initMetrics(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Initializing Metrics registries");
        servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE,
                metricRegistry);
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY,
                metricRegistry);

        log.debug("Registering Metrics Filter");
        FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter",
                new InstrumentedFilter());

        metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
        metricsFilter.setAsyncSupported(true);

        log.debug("Registering Metrics Servlet");
        ServletRegistration.Dynamic metricsAdminServlet =
                servletContext.addServlet("metricsServlet", new MetricsServlet());

        metricsAdminServlet.addMapping("/management/oauth2/metrics/*");
        metricsAdminServlet.setAsyncSupported(true);
        metricsAdminServlet.setLoadOnStartup(2);
    }

    @Bean
    @ConditionalOnProperty(name = "oauth2.cors.allowed-origins")
    public CorsFilter corsFilter() {
        log.debug("Registering CORS filter");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = oAuth2Properties.getCors();
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/oauth/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public EmbeddedServletContainerCustomizer notFoundCustomizer() {
        return new NotFoundIndexTemplate();
    }

    private static class NotFoundIndexTemplate implements EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/"));
        }
    }
}
