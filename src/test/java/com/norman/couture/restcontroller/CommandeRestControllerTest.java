package com.norman.couture.restcontroller;

import com.norman.couture.entities.enums.Echeance;
import com.norman.couture.entities.enums.TypeVetement;
import com.norman.couture.exception.ClientNotFoundException;
import com.norman.couture.exception.CommandeNotFoundException;
import com.norman.couture.exception.GlobalExceptionHandler;
import com.norman.couture.exception.MesureStandardNotFoundException;
import com.norman.couture.security.DetailsUtilisateurService;
import com.norman.couture.security.JwtAuthorizationFilter;
import com.norman.couture.security.SecurityConfiguration;
import com.norman.couture.security.component.JwtService;
import com.norman.couture.service.CommandeService;
import com.norman.couture.service.dto.commande.CommandesClientDto;
import com.norman.couture.service.dto.commande.CommandeResponseDto;
import com.norman.couture.service.dto.commande.SaveCommandeDto;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommandeRestController.class)
@Import({SecurityConfiguration.class, JwtAuthorizationFilter.class, GlobalExceptionHandler.class})
class CommandeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private CommandeService commandeService;

    @MockitoBean
    private DetailsUtilisateurService detailsUtilisateurService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void protectedEndpoints_shouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/commande"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getById_shouldReturnCommande_whenAuthenticated() throws Exception {
        CommandeResponseDto dto = new CommandeResponseDto();
        dto.setId(1L);
        dto.setClientId(3L);
        dto.setEcheance(Echeance.H48);
        dto.setLivrer(false);

        when(commandeService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/commande/{id}", 1L).with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value(3))
                .andExpect(jsonPath("$.echeance").value("H48"));
    }

    @Test
    void getById_shouldReturnNotFound_whenCommandeDoesNotExist() throws Exception {
        when(commandeService.getById(77L)).thenThrow(new CommandeNotFoundException());

        mockMvc.perform(get("/api/commande/{id}", 77L).with(user("admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande introuvable"));
    }

    @Test
    void lister_shouldReturnAllCommandes() throws Exception {
        CommandeResponseDto dto = new CommandeResponseDto();
        dto.setId(4L);
        dto.setEcheance(Echeance.H24);

        when(commandeService.listerAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/commande").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].echeance").value("H24"));
    }

    @Test
    void listerByClient_shouldReturnPayload_whenClientExists() throws Exception {
        CommandeResponseDto commande = new CommandeResponseDto();
        commande.setId(9L);
        CommandesClientDto dto = new CommandesClientDto();
        dto.setCommandes(List.of(commande));

        when(commandeService.listerByClient(8L)).thenReturn(dto);

        mockMvc.perform(get("/api/commande/by-client/{idClient}", 8L).with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commandes[0].id").value(9));
    }

    @Test
    void listerByClient_shouldReturnNotFound_whenClientDoesNotExist() throws Exception {
        when(commandeService.listerByClient(111L)).thenThrow(new ClientNotFoundException());

        mockMvc.perform(get("/api/commande/by-client/{idClient}", 111L).with(user("admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client introuvable"));
    }

    @Test
    void saveCommande_shouldReturnCreated_whenPayloadValid() throws Exception {
        doNothing().when(commandeService).createCommande(any(SaveCommandeDto.class), eq(5L));

        mockMvc.perform(post("/api/commande/{idClient}", 5L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validCommande())))
                .andExpect(status().isCreated());

        verify(commandeService).createCommande(any(SaveCommandeDto.class), eq(5L));
    }

    @Test
    void saveCommande_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        SaveCommandeDto invalid = new SaveCommandeDto();

        mockMvc.perform(post("/api/commande/{idClient}", 5L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCommande_shouldReturnNotFound_whenClientMissing() throws Exception {
        doThrow(new ClientNotFoundException()).when(commandeService).createCommande(any(SaveCommandeDto.class), eq(5L));

        mockMvc.perform(post("/api/commande/{idClient}", 5L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validCommande())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client introuvable"));
    }

    @Test
    void saveCommande_shouldReturnUnprocessableEntity_whenMesureStandardMissing() throws Exception {
        doThrow(new MesureStandardNotFoundException(TypeVetement.CHEMISE))
                .when(commandeService).createCommande(any(SaveCommandeDto.class), eq(5L));

        mockMvc.perform(post("/api/commande/{idClient}", 5L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validCommande())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Mesure standard introuvable pour le type : CHEMISE"));
    }

    @Test
    void updateCommande_shouldReturnNoContent_whenPayloadValid() throws Exception {
        doNothing().when(commandeService).update(any(SaveCommandeDto.class), eq(10L));

        mockMvc.perform(put("/api/commande/{idCommande}", 10L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validCommande())))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateCommande_shouldReturnNotFound_whenCommandeMissing() throws Exception {
        doThrow(new CommandeNotFoundException()).when(commandeService).update(any(SaveCommandeDto.class), eq(10L));

        mockMvc.perform(put("/api/commande/{idCommande}", 10L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validCommande())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande introuvable"));
    }

    @Test
    void livrerNonLivrer_shouldReturnNoContent_whenCommandeExists() throws Exception {
        doNothing().when(commandeService).livrerNonLivrer(10L);

        mockMvc.perform(patch("/api/commande/livrer-non-livrer/{idCommande}", 10L).with(user("admin")))
                .andExpect(status().isNoContent());

        verify(commandeService).livrerNonLivrer(10L);
    }

    @Test
    void livrerNonLivrer_shouldReturnNotFound_whenCommandeMissing() throws Exception {
        doThrow(new CommandeNotFoundException()).when(commandeService).livrerNonLivrer(10L);

        mockMvc.perform(patch("/api/commande/livrer-non-livrer/{idCommande}", 10L).with(user("admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande introuvable"));
    }

    private SaveCommandeDto validCommande() {
        SaveCommandeDto dto = new SaveCommandeDto();
        dto.setEcheance(Echeance.H48);
        dto.setNotes("retouche col");
        dto.setCoutTotal(15000L);
        dto.setAvance(10000L);
        dto.setReste(5000L);
        dto.setUseMesureStandardChemise(true);
        return dto;
    }
}
