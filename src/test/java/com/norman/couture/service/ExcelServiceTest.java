package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import com.norman.couture.entities.enums.Echeance;
import com.norman.couture.entities.enums.Sexe;
import com.norman.couture.entities.enums.TypeMesure;
import com.norman.couture.entities.enums.TypeVetement;
import com.norman.couture.service.excel.ExcelImportRow;
import com.norman.couture.service.excel.ExcelImportRowParser;
import com.norman.couture.service.excel.MesureImportValue;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcelServiceTest {

    @Mock
    private ClientService clientService;

    @Mock
    private CommandeService commandeService;

    @Mock
    private MesureService mesureService;

    @Mock
    private ExcelImportRowParser excelImportRowParser;

    @InjectMocks
    private ExcelService excelService;

    @Test
    void lireFichierExcel_shouldThrowWhenFileIsNull() {
        assertThrows(IllegalArgumentException.class, () -> excelService.lireFichierExcel(null, true));
    }

    @Test
    void lireFichierExcel_shouldProcessParsedRows() throws IOException {
        MockMultipartFile file = workbookWithOneDataRow();
        ExcelImportRow row = sampleRow("Ndiaye", "Awa");

        Client savedClient = new Client();
        savedClient.setId(1L);
        Commande savedCommande = new Commande();
        savedCommande.setId(100L);

        when(excelImportRowParser.parse(any(), any(), any())).thenReturn(Optional.of(row));
        when(clientService.saveClientExcel(any(Client.class))).thenReturn(savedClient);
        when(commandeService.saveCommandeExcel(any(Commande.class), any(Client.class))).thenReturn(savedCommande);

        excelService.lireFichierExcel(file, true);

        verify(clientService, times(1)).saveClientExcel(any(Client.class));
        verify(commandeService, times(1)).saveCommandeExcel(any(Commande.class), any(Client.class));
        verify(mesureService, times(1)).saveMesures(any(), any(Client.class));
    }

    @Test
    void lireFichierExcel_shouldReuseCachedClientForSameName() throws IOException {
        MockMultipartFile file = workbookWithTwoDataRows();
        ExcelImportRow row = sampleRow("Ndiaye", "Awa");

        Client savedClient = new Client();
        savedClient.setId(1L);

        when(excelImportRowParser.parse(any(), any(), any())).thenReturn(Optional.of(row));
        when(clientService.saveClientExcel(any(Client.class))).thenReturn(savedClient);
        when(commandeService.saveCommandeExcel(any(Commande.class), any(Client.class))).thenAnswer(invocation -> {
            Commande commande = invocation.getArgument(0);
            commande.setId(100L);
            return commande;
        });

        excelService.lireFichierExcel(file, false);

        verify(clientService, times(1)).saveClientExcel(any(Client.class));
        verify(commandeService, times(2)).saveCommandeExcel(any(Commande.class), any(Client.class));
    }

    private ExcelImportRow sampleRow(String noms, String prenoms) {
        return new ExcelImportRow(
                noms,
                prenoms,
                Sexe.FEMININ,
                "770000000",
                "awa@example.com",
                "1990-01-01",
                LocalDate.of(2026, 1, 10),
                Echeance.H48,
                LocalDate.of(2026, 1, 12),
                15000L,
                10000L,
                5000L,
                "notes",
                List.of(new MesureImportValue(TypeMesure.COL, TypeVetement.CHEMISE, 39.0))
        );
    }

    private MockMultipartFile workbookWithOneDataRow() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("sheet");
        sheet.createRow(3).createCell(0).setCellValue("line-1");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new MockMultipartFile("file", "file.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());
    }

    private MockMultipartFile workbookWithTwoDataRows() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("sheet");
        sheet.createRow(3).createCell(0).setCellValue("line-1");
        sheet.createRow(4).createCell(0).setCellValue("line-2");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new MockMultipartFile("file", "file.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());
    }
}
