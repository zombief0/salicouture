package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import com.norman.couture.entities.Mesure;
import com.norman.couture.service.excel.ExcelImportRow;
import com.norman.couture.service.excel.ExcelImportRowParser;
import com.norman.couture.service.excel.MesureImportValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelService {
    private final ClientService clientService;
    private final CommandeService commandeService;
    private final MesureService mesureService;
    private final ExcelImportRowParser excelImportRowParser;
    private static final int FIRST_DATA_ROW_INDEX = 3;

    public void lireFichierExcel(MultipartFile file, boolean ancienneCommande) throws IOException {
        MultipartFile validatedFile = requireNonNullParam(file, "file", "lireFichierExcel");
        log.info("Import Excel démarré: fileName={}, ancienneCommande={}", validatedFile.getOriginalFilename(), ancienneCommande);
        try (Workbook workbook = WorkbookFactory.create(validatedFile.getInputStream())) {
            DataFormatter dataFormatter = new DataFormatter();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Map<String, Client> clientCache = new HashMap<>();

            for (Sheet sheet : workbook) {
                int lastRowNum = sheet.getLastRowNum();
                for (int rowNum = FIRST_DATA_ROW_INDEX; rowNum <= lastRowNum; rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }

                    ExcelImportRow importRow = excelImportRowParser.parse(row, dataFormatter, formulaEvaluator).orElse(null);
                    if (importRow == null) {
                        continue;
                    }

                    Client client = resolveClient(clientCache, importRow);

                    Commande commande = new Commande();
                    commande.setDateCommande(importRow.dateCommande());
                    commande.setEcheance(importRow.echeance());
                    commande.setDateRetrait(importRow.dateRetrait());
                    commande.setCoutTotal(importRow.coutTotal());
                    commande.setAvance(importRow.avance());
                    commande.setReste(importRow.reste());
                    commande.setNotes(importRow.notes());
                    commande.setLivrer(ancienneCommande);
                    commande = commandeService.saveCommandeExcel(commande, client);

                    List<Mesure> listeMesures = buildMesures(importRow.mesures(), commande);

                    mesureService.saveMesures(listeMesures, client);
                    log.debug("Ligne Excel importée: rowNum={}, clientId={}, commandeId={}, mesuresCount={}", rowNum, client.getId(), commande.getId(), listeMesures.size());
                }
            }
        }
        log.info("Import Excel terminé avec succès: fileName={}", validatedFile.getOriginalFilename());
    }

    private List<Mesure> buildMesures(List<MesureImportValue> mesuresImportValues, Commande commande) {
        List<Mesure> mesures = new ArrayList<>(mesuresImportValues.size());
        for (MesureImportValue mesureImportValue : mesuresImportValues) {
            Mesure mesure = new Mesure();
            mesure.setTypeMesure(mesureImportValue.typeMesure());
            mesure.setTypeVetement(mesureImportValue.typeVetement());
            mesure.setCommande(commande);
            mesure.setValeur(mesureImportValue.valeur());
            mesures.add(mesure);
        }
        return mesures;
    }

    private Client resolveClient(Map<String, Client> clientCache, ExcelImportRow importRow) {
        String key = importRow.cacheKey();
        Client cachedClient = clientCache.get(key);
        if (cachedClient != null) {
            return cachedClient;
        }

        Client client = new Client();
        client.setNoms(importRow.nomsUpper());
        client.setPrenoms(importRow.prenomsUpper());
        client.setAnniversaire(importRow.anniversaire());
        client.setEmail(importRow.email());
        client.setTelephone(importRow.telephone());
        client.setExistMesureStandardPantalon(false);
        client.setSexe(importRow.sexe());

        Client savedClient = clientService.saveClientExcel(client);
        clientCache.put(key, savedClient);
        return savedClient;
    }

    private <T> T requireNonNullParam(T value, String paramName, String methodName) {
        if (value == null) {
            log.warn("Paramètre null détecté: service=ExcelService, methode={}, parametre={}", methodName, paramName);
            throw new IllegalArgumentException("Le paramètre '" + paramName + "' ne doit pas être null");
        }
        return value;
    }


}
