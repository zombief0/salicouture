package com.norman.couture.restcontroller;

import com.norman.couture.entities.enums.TypeMesure;
import com.norman.couture.entities.enums.TypeVetement;
import com.norman.couture.exception.ClientNotFoundException;
import com.norman.couture.exception.CommandeNotFoundException;
import com.norman.couture.exception.GlobalExceptionHandler;
import com.norman.couture.exception.MesureNotFoundException;
import com.norman.couture.exception.MesureStandardNotFoundException;
import com.norman.couture.security.DetailsUtilisateurService;
import com.norman.couture.security.JwtAuthorizationFilter;
import com.norman.couture.security.SecurityConfiguration;
import com.norman.couture.security.component.JwtService;
import com.norman.couture.service.MesureService;
import com.norman.couture.service.dto.mesure.MesureResponseDto;
import com.norman.couture.service.dto.mesure.SaveMesureDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MesureRestController.class)
@Import({SecurityConfiguration.class, JwtAuthorizationFilter.class, GlobalExceptionHandler.class})
class MesureRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private MesureService mesureService;

    @MockitoBean
    private DetailsUtilisateurService detailsUtilisateurService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void protectedEndpoints_shouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/mesure/{id}", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void listerMesureDuClient_shouldReturnMeasures_whenAuthenticated() throws Exception {
        MesureResponseDto mesure = new MesureResponseDto();
        mesure.setId(12L);
        mesure.setTypeVetement(TypeVetement.CHEMISE);
        mesure.setTypeMesure(TypeMesure.COL);
        mesure.setValeur(38.5);

        when(mesureService.listerMesuresStandards(3L)).thenReturn(List.of(mesure));

        mockMvc.perform(get("/api/mesure/{id}", 3L).with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(12))
                .andExpect(jsonPath("$[0].typeVetement").value("CHEMISE"))
                .andExpect(jsonPath("$[0].typeMesure").value("COL"));
    }

    @Test
    void listerMesureDuClient_shouldReturnNotFound_whenClientMissing() throws Exception {
        when(mesureService.listerMesuresStandards(77L)).thenThrow(new ClientNotFoundException());

        mockMvc.perform(get("/api/mesure/{id}", 77L).with(user("admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client introuvable"));
    }

    @Test
    void saveMesure_shouldReturnCreated_whenPayloadValid() throws Exception {
        mockMvc.perform(post("/api/mesure/{id}", 5L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validMesure())))
                .andExpect(status().isCreated());

        verify(mesureService).ajouter(any(SaveMesureDto.class), eq(5L));
    }

    @Test
    void saveMesure_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        SaveMesureDto invalid = new SaveMesureDto();

        mockMvc.perform(post("/api/mesure/{id}", 5L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveMesure_shouldReturnNotFound_whenCommandeMissing() throws Exception {
        doThrow(new CommandeNotFoundException()).when(mesureService).ajouter(any(SaveMesureDto.class), eq(5L));

        mockMvc.perform(post("/api/mesure/{id}", 5L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validMesure())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Commande introuvable"));
    }

    @Test
    void saveMesure_shouldReturnUnprocessableEntity_whenMesureStandardMissing() throws Exception {
        doThrow(new MesureStandardNotFoundException(TypeVetement.PANTALON))
                .when(mesureService).ajouter(any(SaveMesureDto.class), eq(5L));

        mockMvc.perform(post("/api/mesure/{id}", 5L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validMesure())))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("Mesure standard introuvable pour le type : PANTALON"));
    }

    @Test
    void updateMesure_shouldReturnNoContent_whenPayloadValid() throws Exception {
        mockMvc.perform(put("/api/mesure/{id}", 9L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validMesure())))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateMesure_shouldReturnNotFound_whenMesureMissing() throws Exception {
        doThrow(new MesureNotFoundException()).when(mesureService).update(any(SaveMesureDto.class), eq(9L));

        mockMvc.perform(put("/api/mesure/{id}", 9L)
                        .with(user("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validMesure())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Mesure introuvable"));
    }

    @Test
    void deleteMesure_shouldReturnNoContent_whenMesureExists() throws Exception {
        mockMvc.perform(delete("/api/mesure/{id}", 9L).with(user("admin")))
                .andExpect(status().isNoContent());

        verify(mesureService).delete(9L);
    }

    @Test
    void deleteMesure_shouldReturnNotFound_whenMesureMissing() throws Exception {
        doThrow(new MesureNotFoundException()).when(mesureService).delete(9L);

        mockMvc.perform(delete("/api/mesure/{id}", 9L).with(user("admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Mesure introuvable"));
    }

    private SaveMesureDto validMesure() {
        SaveMesureDto dto = new SaveMesureDto();
        dto.setTypeVetement(TypeVetement.CHEMISE);
        dto.setTypeMesure(TypeMesure.COL);
        dto.setValeur(38.5);
        return dto;
    }
}
