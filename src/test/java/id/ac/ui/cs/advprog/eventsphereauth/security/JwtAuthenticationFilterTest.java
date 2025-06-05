package id.ac.ui.cs.advprog.eventsphereauth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testDoFilterInternalNoAuthHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, securityContext);
    }

    @Test
    void testDoFilterInternalNonBearerAuthHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic somecredentials");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, securityContext);
    }

    @Test
    void testDoFilterInternalValidJwtNotAuthenticated() throws ServletException, IOException {
        String jwt = "valid.jwt.token";
        String userEmail = "test@example.com";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(userEmail);

        when(securityContext.getAuthentication()).thenReturn(null);

        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, times(1)).getHeader("Authorization");
        verify(jwtService, times(1)).extractUsername(jwt);
        verify(securityContext, times(1)).getAuthentication();
        verify(userDetailsService, times(1)).loadUserByUsername(userEmail);
        verify(jwtService, times(1)).isTokenValid(jwt, userDetails);

        verify(securityContext, times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testDoFilterInternalInvalidJwt() throws ServletException, IOException {
        String jwt = "invalid.jwt.token";
        String userEmail = "test@example.com";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(userEmail);

        when(securityContext.getAuthentication()).thenReturn(null);

        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, times(1)).getHeader("Authorization");
        verify(jwtService, times(1)).extractUsername(jwt);
        verify(securityContext, times(1)).getAuthentication();
        verify(userDetailsService, times(1)).loadUserByUsername(userEmail);
        verify(jwtService, times(1)).isTokenValid(jwt, userDetails);

        verify(securityContext, never()).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testDoFilterInternalJwtWithNullUsername() throws ServletException, IOException {
        String jwt = "token.with.no.username";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, times(1)).getHeader("Authorization");
        verify(jwtService, times(1)).extractUsername(jwt);
        verifyNoMoreInteractions(jwtService, userDetailsService, securityContext);
    }
}
