package id.ac.ui.cs.advprog.eventsphereauth.controller;

import id.ac.ui.cs.advprog.eventsphereauth.dto.AuthResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.LoginRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.TokenValidationResponse;
import id.ac.ui.cs.advprog.eventsphereauth.exception.AuthenticationException;
import id.ac.ui.cs.advprog.eventsphereauth.exception.UserAlreadyExistsException;
import id.ac.ui.cs.advprog.eventsphereauth.service.AuthenticationService;
import id.ac.ui.cs.advprog.eventsphereauth.service.JwtService;
import id.ac.ui.cs.advprog.eventsphereauth.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import id.ac.ui.cs.advprog.eventsphereauth.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;
    
    @MockBean
    private JwtService jwtService;
    
    @MockBean
    private UserDetailsService userDetailsService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerShouldReturnToken() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testuser",
                "test@example.com",
                "Password123!",
                "1234567890"
        );
        
        AuthResponse response = new AuthResponse("jwt-token");
        
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
    
    @Test
    public void registerShouldReturnConflictWhenUserExists() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "existingUser",
                "existing@example.com",
                "Password123!",
                "1234567890"
        );
        
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("Username already exists"));
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
    
    @Test
    public void loginShouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "Password123!");
        AuthResponse response = new AuthResponse("jwt-token");
        
        when(authenticationService.login(any(LoginRequest.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
    
    @Test
    public void loginShouldReturnUnauthorizedWhenInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "wrongPassword");
        
        when(authenticationService.login(any(LoginRequest.class)))
                .thenThrow(new AuthenticationException("Invalid username or password"));
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser
    public void validateTokenShouldReturnValidWhenTokenIsValid() throws Exception {
        String token = "valid-jwt-token";
        UserDetails userDetails = mock(UserDetails.class);
        
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        
        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
    
    @Test
    @WithMockUser
    public void validateTokenShouldReturnInvalidWhenTokenIsInvalid() throws Exception {
        String token = "invalid-jwt-token";
        UserDetails userDetails = mock(UserDetails.class);
        
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);
        
        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
}