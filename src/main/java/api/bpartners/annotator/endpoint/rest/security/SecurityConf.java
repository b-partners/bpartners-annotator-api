package api.bpartners.annotator.endpoint.rest.security;

import api.bpartners.annotator.model.exception.ForbiddenException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static api.bpartners.annotator.endpoint.rest.security.model.Role.ADMIN;
import static api.bpartners.annotator.endpoint.rest.security.model.Role.ANNOTATOR;

@Configuration
@Slf4j
public class SecurityConf extends WebSecurityConfigurerAdapter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  private final AuthProvider authProvider;
  private final HandlerExceptionResolver exceptionResolver;

  public SecurityConf(
      AuthProvider authProvider,
      // InternalToExternalErrorHandler behind
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
    this.authProvider = authProvider;
    this.exceptionResolver = exceptionResolver;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        .exceptionHandling()
        .authenticationEntryPoint(
            // note(spring-exception)
            // https://stackoverflow.com/questions/59417122/how-to-handle-usernamenotfoundexception-spring-security
            // issues like when a user tries to access a resource
            // without appropriate authentication elements
            (req, res, e) -> exceptionResolver
                .resolveException(req, res, null, forbiddenWithRemoteInfo(e, req)))
        .accessDeniedHandler(
            // note(spring-exception): issues like when a user not having required roles
            (req, res, e) -> exceptionResolver
                .resolveException(req, res, null, forbiddenWithRemoteInfo(e, req)))

        // authenticate
        .and()
        .authenticationProvider(authProvider)
        .addFilterBefore(
            bearerFilter(new NegatedRequestMatcher(
                new OrRequestMatcher(
                    new AntPathRequestMatcher("/ping"),
                    new AntPathRequestMatcher("/dummy")
                )
            )),
            AnonymousAuthenticationFilter.class)
        .anonymous()

        // authorize
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/ping").permitAll()
        .antMatchers("/dummy").permitAll()
        .antMatchers("/jobs").hasRole(ADMIN.getRole())
        .antMatchers("/jobs/*").hasRole(ADMIN.getRole())
        .antMatchers("/jobs/*/tasks").hasRole(ADMIN.getRole())
        .antMatchers("/jobs/*/tasks/*").hasRole(ADMIN.getRole())
        .antMatchers("/teams/*/jobs").hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .antMatchers("/teams/*/jobs/*").hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .antMatchers("/teams/*/jobs/*/task").hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .antMatchers("/teams/*/jobs/*/tasks/*").hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .antMatchers("/users/*/tasks/*/annotations").hasAnyRole(ADMIN.getRole(), ANNOTATOR.getRole())
        .antMatchers("/**").denyAll()

        // disable superfluous protections
        // Eg if all clients are non-browser then no csrf
        // https://docs.spring.io/spring-security/site/docs/3.2.0.CI-SNAPSHOT/reference/html/csrf.html,
        // Sec 13.3
        .and()
        .csrf().disable() // NOSONAR
        .formLogin().disable()
        .logout().disable();
    // formatter:on
  }

  private Exception forbiddenWithRemoteInfo(Exception e, HttpServletRequest req) {
    log.info(String.format(
        "Access is denied for remote caller: address=%s, host=%s, port=%s",
        req.getRemoteAddr(), req.getRemoteHost(), req.getRemotePort()));
    return new ForbiddenException(e.getMessage());
  }

  private BearerAuthFilter bearerFilter(RequestMatcher requestMatcher) throws Exception {
    BearerAuthFilter bearerFilter = new BearerAuthFilter(requestMatcher, AUTHORIZATION_HEADER);
    bearerFilter.setAuthenticationManager(authenticationManager());
    bearerFilter.setAuthenticationSuccessHandler(
        (httpServletRequest, httpServletResponse, authentication) -> {
        });
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
