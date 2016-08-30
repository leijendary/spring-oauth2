package lejendary.oauth2.config;


import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.*;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import fr.ippon.spark.metrics.SparkReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class MetricsConfiguration extends MetricsConfigurerAdapter {

    private static final String PROP_METRIC_REG_JVM_MEMORY = "jvm.memory";
    private static final String PROP_METRIC_REG_JVM_GARBAGE = "jvm.garbage";
    private static final String PROP_METRIC_REG_JVM_THREADS = "jvm.threads";
    private static final String PROP_METRIC_REG_JVM_FILES = "jvm.files";
    private static final String PROP_METRIC_REG_JVM_BUFFERS = "jvm.buffers";

    private final Logger log = LoggerFactory.getLogger(MetricsConfiguration.class);
    private MetricRegistry metricRegistry = new MetricRegistry();
    private HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
    private final OAuth2Properties oAuth2Properties;

    @Inject
    public MetricsConfiguration(OAuth2Properties oAuth2Properties) {
        this.oAuth2Properties = oAuth2Properties;
    }

    @Override
    @Bean
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    @Override
    @Bean
    public HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    @PostConstruct
    public void init() {
        log.debug("Registering JVM gauges");
        metricRegistry.register(PROP_METRIC_REG_JVM_MEMORY, new MemoryUsageGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_THREADS, new ThreadStatesGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_FILES, new FileDescriptorRatioGauge());
        metricRegistry.register(PROP_METRIC_REG_JVM_BUFFERS, new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        if (oAuth2Properties.getMetrics().getJmx().isEnabled()) {
            log.debug("Initializing Metrics JMX reporting");
            JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
            jmxReporter.start();
        }

        if (oAuth2Properties.getMetrics().getLogs().isEnabled()) {
            log.info("Initializing Metrics Log reporting");
            final Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry)
                    .outputTo(LoggerFactory.getLogger("metrics"))
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build();
            reporter.start(oAuth2Properties.getMetrics().getLogs().getReportFrequency(), TimeUnit.SECONDS);
        }
    }

    @Configuration
    @ConditionalOnClass(Graphite.class)
    public static class GraphiteRegistry {

        private final Logger log = LoggerFactory.getLogger(GraphiteRegistry.class);
        private final MetricRegistry metricRegistry;
        private final OAuth2Properties oAuth2Properties;

        @Inject
        public GraphiteRegistry(OAuth2Properties oAuth2Properties, MetricRegistry metricRegistry) {
            this.oAuth2Properties = oAuth2Properties;
            this.metricRegistry = metricRegistry;
        }

        @PostConstruct
        private void init() {
            if (oAuth2Properties.getMetrics().getGraphite().isEnabled()) {
                log.info("Initializing Metrics Graphite reporting");
                String graphiteHost = oAuth2Properties.getMetrics().getGraphite().getHost();
                Integer graphitePort = oAuth2Properties.getMetrics().getGraphite().getPort();
                String graphitePrefix = oAuth2Properties.getMetrics().getGraphite().getPrefix();
                Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
                GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .prefixedWith(graphitePrefix)
                        .build(graphite);
                graphiteReporter.start(1, TimeUnit.MINUTES);
            }
        }
    }

    @Configuration
    @ConditionalOnClass(SparkReporter.class)
    public static class SparkRegistry {

        private final Logger log = LoggerFactory.getLogger(SparkRegistry.class);
        private final MetricRegistry metricRegistry;
        private final OAuth2Properties oAuth2Properties;

        @Inject
        public SparkRegistry(MetricRegistry metricRegistry, OAuth2Properties oAuth2Properties) {
            this.metricRegistry = metricRegistry;
            this.oAuth2Properties = oAuth2Properties;
        }

        @PostConstruct
        private void init() {
            if (oAuth2Properties.getMetrics().getSpark().isEnabled()) {
                log.info("Initializing Metrics Spark reporting");
                String sparkHost = oAuth2Properties.getMetrics().getSpark().getHost();
                Integer sparkPort = oAuth2Properties.getMetrics().getSpark().getPort();
                SparkReporter sparkReporter = SparkReporter.forRegistry(metricRegistry)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .build(sparkHost, sparkPort);
                sparkReporter.start(1, TimeUnit.MINUTES);
            }
        }
    }

}
