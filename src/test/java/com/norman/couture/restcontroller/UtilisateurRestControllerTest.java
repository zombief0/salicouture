package com.norman.couture.restcontroller;

import com.norman.couture.exception.GlobalExceptionHandler;
import com.norman.couture.security.AuthResponse;
import com.norman.couture.security.DetailsUtilisateurService;
import com.norman.couture.security.JwtAuthorizationFilter;
import com.norman.couture.security.SecurityConfiguration;
import com.norman.couture.security.component.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UtilisateurRestController.class)
@Import({SecurityConfiguration.class, JwtAuthorizationFilter.class, GlobalExceptionHandler.class})
class UtilisateurRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private DetailsUtilisateurService detailsUtilisateurService;

    @Test
    void login_shouldSucceedWithoutPriorAuthentication() throws Exception {
        Authentication authentication = mock(Authentication.class);
        AuthResponse authResponse = new AuthResponse("jwt-token", 3600, "ROLE_ADMIN", "admin");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn(authResponse);

        mockMvc.perform(post("/api/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validLoginPayload())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.login").value("admin"));

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_shouldReturnBadRequest_whenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/api/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnClientOrServerError_whenCredentialsAreInvalid() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validLoginPayload())))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertThat(status).isIn(401, 403, 500);
                });
    }

    private Object validLoginPayload() {
        return new Object() {
            public final String login = "admin";
            public final String password = "password";
        };
    }
}
