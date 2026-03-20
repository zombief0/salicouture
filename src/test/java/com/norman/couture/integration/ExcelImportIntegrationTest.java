package com.norman.couture.integration;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import com.norman.couture.service.ClientService;
import com.norman.couture.service.CommandeService;
import com.norman.couture.service.ExcelService;
import com.norman.couture.service.MesureService;
import com.norman.couture.service.excel.ExcelImportRowParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ExcelService.class, ExcelImportRowParser.class})
class ExcelImportIntegrationTest {

    @Autowired
    private ExcelService excelService;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private CommandeService commandeService;

    @MockitoBean
    private MesureService mesureService;

    @Test
    void lireFichierExcel_shouldImportUsingRealWorkbook() throws Exception {
        Path excelPath = Path.of("fichier-excel", "fichierExcel.xlsx");
        assertTrue(Files.exists(excelPath), "fichierExcel.xlsx must exist at fichier-excel/fichierExcel.xlsx");

        byte[] content = Files.readAllBytes(excelPath);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fichierExcel.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content
        );

        AtomicLong clientIds = new AtomicLong(1);
        AtomicLong commandeIds = new AtomicLong(100);

        when(clientService.saveClientExcel(any(Client.class))).thenAnswer(invocation -> {
            Client c = invocation.getArgument(0);
            c.setId(clientIds.getAndIncrement());
            return c;
        });

        when(commandeService.saveCommandeExcel(any(Commande.class), any(Client.class))).thenAnswer(invocation -> {
            Commande commande = invocation.getArgument(0);
            commande.setId(commandeIds.getAndIncrement());
            return commande;
        });


        excelService.lireFichierExcel(file, false);

        verify(clientService, atLeastOnce()).saveClientExcel(any(Client.class));
        verify(commandeService, atLeastOnce()).saveCommandeExcel(any(Commande.class), any(Client.class));
        verify(mesureService, atLeastOnce()).saveMesures(any(), any(Client.class));
    }
}
