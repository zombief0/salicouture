package com.norman.couture.restcontroller;

import com.norman.couture.entities.enums.Sexe;
import com.norman.couture.exception.ClientAlreadyExistsException;
import com.norman.couture.exception.ClientNotFoundException;
import com.norman.couture.exception.GlobalExceptionHandler;
import com.norman.couture.security.DetailsUtilisateurService;
import com.norman.couture.security.JwtAuthorizationFilter;
import com.norman.couture.security.SecurityConfiguration;
import com.norman.couture.security.component.JwtService;
import com.norman.couture.service.ClientService;
import com.norman.couture.service.dto.client.ClientResponseDto;
import com.norman.couture.service.dto.client.SaveClientDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientRestController.class)
@Import({SecurityConfiguration.class, JwtAuthorizationFilter.class, GlobalExceptionHandler.class})
class ClientRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private DetailsUtilisateurService detailsUtilisateurService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void lister_shouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/client"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void lister_shouldReturnClients_whenAuthenticated() throws Exception {
        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(1L);
        dto.setNoms("Ndiaye");
        dto.setPrenoms("Awa");
        dto.setTelephone("770000000");
        dto.setEmail("awa@example.com");
        dto.setSexe(Sexe.FEMININ);

        when(clientService.lister()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/client").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].noms").value("Ndiaye"))
                .andExpect(jsonPath("$[0].sexe").value("FEMININ"));
    }

    @Test
    void ajouter_shouldCreateClient_whenPayloadIsValidAndAuthenticated() throws Exception {
        SaveClientDto payload = validClient();

        mockMvc.perform(post("/api/client")
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        verify(clientService).enregistrer(any(SaveClientDto.class));
    }

    @Test
    void ajouter_shouldReturnBadRequest_whenPayloadIsInvalid() throws Exception {
        SaveClientDto invalid = new SaveClientDto();
        invalid.setPrenoms("Awa");

        mockMvc.perform(post("/api/client")
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ajouter_shouldReturnConflict_whenClientAlreadyExists() throws Exception {
        doThrow(new ClientAlreadyExistsException()).when(clientService).enregistrer(any(SaveClientDto.class));

        mockMvc.perform(post("/api/client")
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validClient())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Un client avec ce nom et prénom existe déjà"));
    }

    @Test
    void update_shouldReturnNoContent_whenPayloadIsValidAndAuthenticated() throws Exception {
        mockMvc.perform(put("/api/client/{id}", 5L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validClient())))
                .andExpect(status().isNoContent());

        verify(clientService).update(any(SaveClientDto.class), eq(5L));
    }

    @Test
    void update_shouldReturnNotFound_whenClientDoesNotExist() throws Exception {
        doThrow(new ClientNotFoundException()).when(clientService).update(any(SaveClientDto.class), eq(99L));

        mockMvc.perform(put("/api/client/{id}", 99L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validClient())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client introuvable"));
    }

    private SaveClientDto validClient() {
        SaveClientDto dto = new SaveClientDto();
        dto.setNoms("Ndiaye");
        dto.setPrenoms("Awa");
        dto.setTelephone("770000000");
        dto.setEmail("awa@example.com");
        dto.setAnniversaire("1995-01-01");
        dto.setSexe(Sexe.FEMININ);
        return dto;
    }
}
