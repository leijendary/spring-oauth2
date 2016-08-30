package lejendary.oauth2.config;

import lejendary.oauth2.security.AjaxLogoutSuccessHandler;
import lejendary.oauth2.security.Http401UnauthorizedEntryPoint;
import lejendary.oauth2.service.ClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 7:20 PM
 */

@Configuration
public class OAuth2ServerConfiguration {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        private final Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint;
        private final AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

        @Inject
        public ResourceServerConfiguration(AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler, Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint) {
            this.ajaxLogoutSuccessHandler = ajaxLogoutSuccessHandler;
            this.http401UnauthorizedEntryPoint = http401UnauthorizedEntryPoint;
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .exceptionHandling()
                    .authenticationEntryPoint(http401UnauthorizedEntryPoint)
                    .and()
                    .logout()
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                    .and()
                    .csrf()
                    .disable()
                    .headers()
                    .frameOptions().disable()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        private final DataSource dataSource;
        private final AuthenticationManager authenticationManager;
        private final ClientService clientService;

        @Inject
        public AuthorizationServerConfiguration(DataSource dataSource, AuthenticationManager authenticationManager, ClientService clientService) {
            this.dataSource = dataSource;
            this.authenticationManager = authenticationManager;
            this.clientService = clientService;
        }

        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(dataSource);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                    .tokenStore(tokenStore())
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer authServer) throws Exception {
            authServer
                    .allowFormAuthenticationForClients()
                    .checkTokenAccess("permitAll()");
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .withClientDetails(clientService);
        }
    }

}
