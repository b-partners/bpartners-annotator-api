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
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@Slf4j
public class SecurityConf extends WebSecurityConfigurerAdapter {

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

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http.exceptionHandling()
        .authenticationEntryPoint(
            // note(spring-exception)
            // https://stackoverflow.com/questions/59417122/how-to-handle-usernamenotfoundexception-spring-security
            // issues like when a user tries to access a resource
            // without appropriate authentication elements
            (req, res, e) ->
                exceptionResolver.resolveException(req, res, null, forbiddenWithRemoteInfo(e, req)))
        .accessDeniedHandler(
            // note(spring-exception): issues like when a user not having required roles
            (req, res, e) ->
                exceptionResolver.resolveException(req, res, null, forbiddenWithRemoteInfo(e, req)))

        // authenticate
        .and()
        .authenticationProvider(authProvider)
        .addFilterBefore(
            bearerFilter(
                new NegatedRequestMatcher(
                    new OrRequestMatcher(
                        new AntPathRequestMatcher("/**", OPTIONS.toString()),
                        new AntPathRequestMatcher("/ping", GET.name()),
                        new AntPathRequestMatcher("/dummy-table", GET.name()),
                        new AntPathRequestMatcher("/uuid-created", GET.name())))),
            AnonymousAuthenticationFilter.class)
        .anonymous()

        // authorize
        .and()
        .authorizeRequests()
        .antMatchers(OPTIONS, "/**")
        .permitAll()
        .antMatchers(GET, "/ping")
        .permitAll()
        .antMatchers("/dummy-table")
        .permitAll()
        .antMatchers("/uuid-created")
        .permitAll()
        .antMatchers("/whoami")
        .hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .antMatchers(PUT, "/users")
        .hasRole(ADMIN.getRole())
        .antMatchers(GET, "/users")
        .hasRole(ADMIN.getRole())
        .antMatchers(GET, "/jobs")
        .hasRole(ADMIN.getRole())
        .antMatchers(GET, "/jobs/*")
        .hasRole(ADMIN.getRole())
        .antMatchers(PUT, "/jobs/*")
        .hasRole(ADMIN.getRole())
        .antMatchers(GET, "/jobs/*/tasks")
        .hasRole(ADMIN.getRole())
        .antMatchers(GET, "/jobs/*/tasks/*")
        .hasRole(ADMIN.getRole())
        .antMatchers(GET, "/teams")
        .hasRole(ADMIN.getRole())
        .antMatchers(POST, "/teams")
        .hasRole(ADMIN.getRole())
        .requestMatchers(new SelfTeamMatcher(GET, "/teams/*/jobs", resourceProvider))
        .hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .requestMatchers(new SelfTeamMatcher(GET, "/teams/*/jobs/*", resourceProvider))
        .hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .requestMatchers(new SelfTeamMatcher(GET, "/teams/*/jobs/*/task", resourceProvider))
        .hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .requestMatchers(new SelfTeamMatcher(PUT, "/teams/*/jobs/*/tasks/*", resourceProvider))
        .hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .requestMatchers(new SelfUserMatcher(PUT, "/users/*/tasks/*/annotations", resourceProvider))
        .hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .antMatchers("/**")
        .denyAll()

        // disable superfluous protections
        // Eg if all clients are non-browser then no csrf
        // https://docs.spring.io/spring-security/site/docs/3.2.0.CI-SNAPSHOT/reference/html/csrf.html,
        // Sec 13.3
        .and()
        .csrf()
        .disable() // NOSONAR
        .formLogin()
        .disable()
        .logout()
        .disable();
    // formatter:on
  }

  private Exception forbiddenWithRemoteInfo(Exception e, HttpServletRequest req) {
    log.info(
        String.format(
            "Access is denied for remote caller: address=%s, host=%s, port=%s",
            req.getRemoteAddr(), req.getRemoteHost(), req.getRemotePort()));
    return new ForbiddenException(e.getMessage());
  }

  private HeaderAuthFilter bearerFilter(RequestMatcher requestMatcher) throws Exception {
    HeaderAuthFilter bearerFilter = new HeaderAuthFilter(requestMatcher, AUTHORIZATION_HEADER);
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
