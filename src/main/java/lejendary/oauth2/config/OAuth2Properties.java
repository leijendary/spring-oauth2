package lejendary.oauth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 2:02 PM
 */

@ConfigurationProperties(prefix = "oauth2", ignoreUnknownFields = false)
@Data
public class OAuth2Properties {

    private final Http http = new Http();
    private final Cache cache = new Cache();
    private final Security security = new Security();
    private final Async async = new Async();
    private final CorsConfiguration cors = new CorsConfiguration();
    private final Metrics metrics = new Metrics();
    private final Logging logging = new Logging();

    @Data
    public static class Http {
        private final Cache cache = new Cache();

        @Data
        public static class Cache {
            private int timeToLiveInDays = 1461;
        }
    }

    @Data
    public final class Cache {
        private int timeToLiveSeconds = 3600;
        private final Ehcache ehcache = new Ehcache();

        @Data
        public final class Ehcache {
            private String maxBytesLocalHeap = "16M";
        }
    }

    @Data
    public final class Security {
        private final OAuth oauth = new OAuth();

        @Data
        public final class OAuth {
            private String clientId;
            private String secret;
            private int tokenValidityInSeconds = 1800;
        }
    }

    @Data
    public static class Async {
        private int corePoolSize = 2;
        private int maxPoolSize = 50;
        private int queueCapacity = 10000;
    }

    @Data
    public static class Metrics {
        private final Jmx jmx = new Jmx();
        private final Spark spark = new Spark();
        private final Graphite graphite = new Graphite();
        private final Logs logs = new Logs();

        @Data
        public static class Jmx {
            private boolean enabled = true;
        }

        @Data
        public static class Spark {
            private boolean enabled = false;
            private String host = "localhost";
            private int port = 9999;
        }

        @Data
        public static class Graphite {
            private boolean enabled = false;
            private String host = "localhost";
            private int port = 2003;
            private String prefix = "OAuth2Application";
        }

        @Data
        public static class Logs {
            private boolean enabled = false;
            private long reportFrequency = 60;
        }
    }

    @Data
    public static class Logging {
        private final Logstash logstash = new Logstash();

        @Data
        public static class Logstash {
            private boolean enabled = false;
            private String host = "localhost";
            private int port = 5000;
            private int queueSize = 512;
        }

    }

}
