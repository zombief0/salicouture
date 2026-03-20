package com.norman.couture.restcontroller;

import com.norman.couture.exception.GlobalExceptionHandler;
import com.norman.couture.security.DetailsUtilisateurService;
import com.norman.couture.security.JwtAuthorizationFilter;
import com.norman.couture.security.SecurityConfiguration;
import com.norman.couture.security.component.JwtService;
import com.norman.couture.service.ExcelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExcelRestController.class)
@Import({SecurityConfiguration.class, JwtAuthorizationFilter.class, GlobalExceptionHandler.class})
class ExcelRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExcelService excelService;

    @MockitoBean
    private DetailsUtilisateurService detailsUtilisateurService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void uploadEndpoints_shouldRejectUnauthenticatedRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "clients.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy".getBytes()
        );

        mockMvc.perform(multipart("/api/excel").file(file))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void loadExcelDataOld_shouldReturnOk_whenImportSucceeds() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "clients.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy".getBytes()
        );

        mockMvc.perform(multipart("/api/excel").file(file).with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("UPLOAD_SUCCES"));

        verify(excelService).lireFichierExcel(any(), eq(true));
    }

    @Test
    void loadExcelDateNew_shouldReturnOk_whenImportSucceeds() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "clients.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy".getBytes()
        );

        mockMvc.perform(multipart("/api/excel/new").file(file).with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("UPLOAD_SUCCES"));

        verify(excelService).lireFichierExcel(any(), eq(false));
    }

    @Test
    void loadExcelDataOld_shouldReturnInternalServerError_whenIOExceptionOccurs() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "clients.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy".getBytes()
        );

        doThrow(new IOException("broken file")).when(excelService).lireFichierExcel(any(), eq(true));

        mockMvc.perform(multipart("/api/excel").file(file).with(user("admin")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("broken file"));
    }

    @Test
    void loadExcelDataOld_shouldReturnBadRequest_whenMultipartFileMissing() throws Exception {
        mockMvc.perform(multipart("/api/excel").with(user("admin")))
                .andExpect(status().isBadRequest());
    }
}
