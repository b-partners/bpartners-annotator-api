package api.bpartners.annotator.endpoint.rest.security;

import static api.bpartners.annotator.endpoint.rest.security.model.Role.ADMIN;
import static api.bpartners.annotator.endpoint.rest.security.model.Role.ANNOTATOR;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import api.bpartners.annotator.endpoint.rest.security.matcher.SelfTeamMatcher;
import api.bpartners.annotator.endpoint.rest.security.matcher.SelfUserMatcher;
import api.bpartners.annotator.model.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@Slf4j
@EnableWebSecurity
public class SecurityConf {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  private final AuthProvider authProvider;
  private final HandlerExceptionResolver exceptionResolver;
  private final AuthenticatedResourceProvider resourceProvider;

  public SecurityConf(
      AuthProvider authProvider,
      // InternalToExternalErrorHandler behind
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver,
      AuthenticatedResourceProvider resourceProvider) {
    this.authProvider = authProvider;
    this.exceptionResolver = exceptionResolver;
    this.resourceProvider = resourceProvider;
  }

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http.exceptionHandling(
            (exceptionHandler) ->
                exceptionHandler
                    .authenticationEntryPoint(
                        // note(spring-exception)
                        // https://stackoverflow.com/questions/59417122/how-to-handle-usernamenotfoundexception-spring-security
                        // issues like when a user tries to access a resource
                        // without appropriate authentication elements
                        (req, res, e) ->
                            exceptionResolver.resolveException(
                                req, res, null, forbiddenWithRemoteInfo(e, req)))
                    .accessDeniedHandler(
                        // note(spring-exception): issues like when a user not having required roles
                        (req, res, e) ->
                            exceptionResolver.resolveException(
                                req, res, null, forbiddenWithRemoteInfo(e, req))))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // authenticate
        .authenticationProvider(authProvider)
        .addFilterBefore(
            bearerFilter(
                new NegatedRequestMatcher(
                    new OrRequestMatcher(
                        new AntPathRequestMatcher("/**", OPTIONS.toString()),
                        new AntPathRequestMatcher("/ping", GET.name()),
                        new AntPathRequestMatcher("/health/bucket", GET.name()),
                        new AntPathRequestMatcher("/health/db", GET.name()),
                        new AntPathRequestMatcher("/health/email", GET.name()),
                        new AntPathRequestMatcher("/health/event", GET.name())))),
            AnonymousAuthenticationFilter.class)
        .authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers(OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(GET, "/ping")
                    .permitAll()
                    .requestMatchers("/health/bucket")
                    .permitAll()
                    .requestMatchers("/health/db")
                    .permitAll()
                    .requestMatchers("/health/email")
                    .permitAll()
                    .requestMatchers("/health/event")
                    .permitAll()
                    .requestMatchers("/whoami")
                    .hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
                    .requestMatchers(POST, "/users")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/users")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs/*")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(PUT, "/jobs/*")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs/*/annotationStatistics")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs/*/export")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs/*/tasks")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(PUT, "/jobs/*/tasks")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs/*/tasks/*")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs/*/tasks/*/annotations")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs/*/tasks/*/annotations/*")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs/*/tasks/*/annotations/*/reviews")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(PUT, "/jobs/*/tasks/*/annotations/*/reviews/*")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/jobs/*/tasks/*/annotations/*/reviews/*")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(GET, "/teams")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(POST, "/teams")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(new SelfTeamMatcher(GET, "/teams/*/jobs", resourceProvider))
                    .hasRole(ANNOTATOR.getRole())
                    .requestMatchers(GET, "/teams/*/jobs")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(new SelfTeamMatcher(GET, "/teams/*/jobs/*", resourceProvider))
                    .hasRole(ANNOTATOR.getRole())
                    .requestMatchers(GET, "/teams/*/jobs/*")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(
                        new SelfTeamMatcher(GET, "/teams/*/jobs/*/task", resourceProvider))
                    .hasRole(ANNOTATOR.getRole())
                    // .requestMatchers(GET, "/teams/*/jobs/*/task")
                    // .hasRole(ADMIN.getRole())
                    .requestMatchers(
                        new SelfTeamMatcher(PUT, "/teams/*/jobs/*/tasks/*", resourceProvider))
                    .hasRole(ANNOTATOR.getRole())
                    .requestMatchers(
                        new SelfUserMatcher(PUT, "/users/*/tasks/*/annotations", resourceProvider))
                    .hasRole(ANNOTATOR.getRole())
                    .requestMatchers(
                        new SelfUserMatcher(GET, "/users/*/tasks/*/annotations", resourceProvider))
                    .hasRole(ANNOTATOR.getRole())
                    .requestMatchers(GET, "/users/*/tasks/*/annotations")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(
                        new SelfUserMatcher(
                            GET, "/users/*/tasks/*/annotations/*", resourceProvider))
                    .hasRole(ANNOTATOR.getRole())
                    .requestMatchers(GET, "/users/*/tasks/*/annotations/*")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(
                        new SelfUserMatcher(
                            GET, "/users/*/tasks/*/annotations/*/reviews", resourceProvider))
                    .hasRole(ANNOTATOR.getRole())
                    .requestMatchers(GET, "/users/*/tasks/*/annotations/*/reviews")
                    .hasRole(ADMIN.getRole())
                    .requestMatchers(
                        new SelfUserMatcher(
                            GET, "/users/*/tasks/*/annotations/*/reviews/*", resourceProvider))
                    .hasRole(ANNOTATOR.getRole())
                    .requestMatchers(GET, "/users/*/tasks/*/annotations/*/reviews/*")
                    .hasRole(ADMIN.getRole())
                    // .requestMatchers(PUT, "/users/*/tasks/*/annotations/*")
                    // .hasRole(ADMIN.getRole())
                    .requestMatchers("/**")
                    .denyAll())
        // disable superfluous protections
        // Eg if all clients are non-browser then no csrf
        // https://docs.spring.io/spring-security/site/docs/3.2.0.CI-SNAPSHOT/reference/html/csrf.html,
        // Sec 13.3
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable);
    // formatter:on
    return http.build();
  }

  private Exception forbiddenWithRemoteInfo(Exception e, HttpServletRequest req) {
    log.info(
        String.format(
            "Access is denied for remote caller: address=%s, host=%s, port=%s",
            req.getRemoteAddr(), req.getRemoteHost(), req.getRemotePort()));
    return new ForbiddenException(e.getMessage());
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(authProvider);
  }

  private BearerAuthFilter bearerFilter(RequestMatcher requestMatcher) throws Exception {
    BearerAuthFilter bearerFilter = new BearerAuthFilter(requestMatcher, AUTHORIZATION_HEADER);
    bearerFilter.setAuthenticationManager(authenticationManager());
    bearerFilter.setAuthenticationSuccessHandler(
        (httpServletRequest, httpServletResponse, authentication) -> {});
    bearerFilter.setAuthenticationFailureHandler(
        (req, res, e) ->
            // note(spring-exception)
            // issues like when a user is not found(i.e. UsernameNotFoundException)
            // or other exceptions thrown inside authentication provider.
            // In fact, this handles other authentication exceptions that are
            // not handled by AccessDeniedException and AuthenticationEntryPoint
            exceptionResolver.resolveException(req, res, null, forbiddenWithRemoteInfo(e, req)));
    return bearerFilter;
  }
}
