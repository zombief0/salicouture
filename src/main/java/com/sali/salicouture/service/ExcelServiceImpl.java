package com.sali.salicouture.service;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.entities.Commande;
import com.sali.salicouture.entities.Mesure;
import com.sali.salicouture.entities.enums.Echeance;
import com.sali.salicouture.entities.enums.Sexe;
import com.sali.salicouture.entities.enums.TypeMesure;
import com.sali.salicouture.entities.enums.TypeVetement;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    private final ClientService clientService;
    private final CommandeService commandeService;
    private final MesureService mesureService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void lireFichierExcel(MultipartFile file) throws IOException {
        Path repTemp = Files.createTempDirectory("salicouture_");// créer un repertoire temporaire  préfixé par scoma
        File fichTemp = repTemp.resolve(Objects.requireNonNull(file.getOriginalFilename())).toFile();//on recopie le nom du fichier source dans le repertoir temporaire

        file.transferTo(fichTemp);
        Workbook workbook = WorkbookFactory.create(fichTemp);

        Iterator<Sheet> sheetIterator = workbook.sheetIterator();


        while (sheetIterator.hasNext()) {

            Sheet sheet = sheetIterator.next();

            DataFormatter dataFormatter = new DataFormatter();

            Iterator<Row> rowIterator = sheet.rowIterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int numRow = row.getRowNum();
                Client client = null;
                if (numRow > 2 && !dataFormatter.formatCellValue(row.getCell(0)).trim().equals("")) {
                    String noms, prenoms,
                            telephone, email, anniversaire, dateCommande,
                            dateRetrait, coutTotal, avance, reste, notesDescription, mesureEChemise,
                            mesureLMChemise, mesurePChemise, mesureVChemise, mesureTMChemise,
                            mesureABChemise, mesure2PChemise, mesureColChemise, mesureLChemise,
                            mesureEVeste, mesureLMVeste, mesurePVeste, mesureVVeste,
                            mesureTMVeste, mesureABVeste, mesureCAVeste, mesureCDVeste, mesureLVeste,
                            mesureTPantalon, mesureBPantalon, mesureCPantalon, mesureLPantalon, mesurePPantalon,
                            mesure2BPantalon, mesuremPantalon, echeance, sexe;

                    noms = dataFormatter.formatCellValue(row.getCell(0)).trim();
                    prenoms = dataFormatter.formatCellValue(row.getCell(1)).trim();
                    sexe = dataFormatter.formatCellValue(row.getCell(2)).trim();
                    telephone = dataFormatter.formatCellValue(row.getCell(3)).trim();
                    email = dataFormatter.formatCellValue(row.getCell(4)).trim();
                    anniversaire = dataFormatter.formatCellValue(row.getCell(5)).trim();
                    dateCommande = dataFormatter.formatCellValue(row.getCell(6)).trim();
                    echeance = dataFormatter.formatCellValue(row.getCell(7)).trim();
                    dateRetrait = dataFormatter.formatCellValue(row.getCell(8)).trim();
                    coutTotal = dataFormatter.formatCellValue(row.getCell(9)).trim();
                    avance = dataFormatter.formatCellValue(row.getCell(10)).trim();
                    reste = dataFormatter.formatCellValue(row.getCell(11)).trim();
                    notesDescription = dataFormatter.formatCellValue(row.getCell(12)).trim();
                    mesureEChemise = dataFormatter.formatCellValue(row.getCell(13)).trim();
                    mesureLMChemise = dataFormatter.formatCellValue(row.getCell(14)).trim();
                    mesurePChemise = dataFormatter.formatCellValue(row.getCell(15)).trim();
                    mesureVChemise = dataFormatter.formatCellValue(row.getCell(16)).trim();
                    mesureTMChemise = dataFormatter.formatCellValue(row.getCell(17)).trim();
                    mesureABChemise = dataFormatter.formatCellValue(row.getCell(18)).trim();
                    mesure2PChemise = dataFormatter.formatCellValue(row.getCell(19)).trim();
                    mesureColChemise = dataFormatter.formatCellValue(row.getCell(20)).trim();
                    mesureLChemise = dataFormatter.formatCellValue(row.getCell(21)).trim();
                    mesureEVeste = dataFormatter.formatCellValue(row.getCell(22)).trim();
                    mesureLMVeste = dataFormatter.formatCellValue(row.getCell(23)).trim();
                    mesurePVeste = dataFormatter.formatCellValue(row.getCell(24)).trim();
                    mesureVVeste = dataFormatter.formatCellValue(row.getCell(25)).trim();
                    mesureTMVeste = dataFormatter.formatCellValue(row.getCell(26)).trim();
                    mesureABVeste = dataFormatter.formatCellValue(row.getCell(27)).trim();
                    mesureCAVeste = dataFormatter.formatCellValue(row.getCell(28)).trim();
                    mesureCDVeste = dataFormatter.formatCellValue(row.getCell(29)).trim();
                    mesureLVeste = dataFormatter.formatCellValue(row.getCell(30)).trim();
                    mesureTPantalon = dataFormatter.formatCellValue(row.getCell(31)).trim();
                    mesureBPantalon = dataFormatter.formatCellValue(row.getCell(32)).trim();
                    mesureCPantalon = dataFormatter.formatCellValue(row.getCell(33)).trim();
                    mesureLPantalon = dataFormatter.formatCellValue(row.getCell(34)).trim();
                    mesurePPantalon = dataFormatter.formatCellValue(row.getCell(35)).trim();
                    mesure2BPantalon = dataFormatter.formatCellValue(row.getCell(36)).trim();
                    mesuremPantalon = dataFormatter.formatCellValue(row.getCell(37)).trim();

                    // Client
                    if (!noms.trim().isEmpty()) {
                        client = new Client();
                        client.setNoms(noms.trim().toUpperCase(Locale.ROOT));
                        client.setPrenoms(prenoms.trim().toUpperCase(Locale.ROOT));
                        client.setAnniversaire(anniversaire.trim());
                        client.setEmail(email.trim());
                        client.setTelephone(telephone.trim());
                        client.setExistMesureStandard(false);
                        sexe = sexe.trim();
                        if (!sexe.equals("")) {
                            if (sexe.toUpperCase(Locale.ROOT).equals("M")) {
                                client.setSexe(Sexe.MASCULIN);
                            } else {
                                client.setSexe(Sexe.FEMININ);
                            }
                        }
                        client = clientService.saveClientExcel(client);
                    }

                    // Commandes
                    Commande commande = new Commande();
                    if (!dateCommande.trim().isEmpty()) {
                        commande.setDateCommande(extractDate(dateCommande));

                    }

                    echeance = echeance.trim();
                    if (echeance.trim().isEmpty()) {
                        commande.setEcheance(Echeance.HNONE);
                    } else {
                        switch (echeance.toLowerCase(Locale.ROOT)) {
                            case "24h":
                                commande.setEcheance(Echeance.H24);
                                break;
                            case "48h":
                                commande.setEcheance(Echeance.H48);
                                break;
                            case "72h":
                                commande.setEcheance(Echeance.H72);
                                break;
                            default:
                                commande.setEcheance(Echeance.HNONE);
                                break;
                        }
                    }

                    if (!dateRetrait.trim().isEmpty()) {
                        commande.setDateRetrait(extractDate(dateRetrait));
                    }

                    if (!coutTotal.trim().isEmpty()) {
                        commande.setCoutTotal(Long.valueOf(coutTotal));
                    }

                    if (!avance.trim().isEmpty()) {
                        commande.setAvance(Long.valueOf(avance));
                    }
                    if (!reste.trim().isEmpty()) {
                        commande.setReste(Long.valueOf(reste));
                    }

                    commande.setNotes(notesDescription.trim());
                    commande = commandeService.saveCommandeExcel(commande, client);


                    List<Mesure> listeMesures = new ArrayList<>();
                    // CHEMISE
                    addMesure(mesureEChemise, TypeMesure.E, TypeVetement.CHEMISE, commande, listeMesures);
                    addMesure(mesureLMChemise, TypeMesure.LM, TypeVetement.CHEMISE, commande, listeMesures);
                    addMesure(mesurePChemise, TypeMesure.P, TypeVetement.CHEMISE, commande, listeMesures);
                    addMesure(mesureVChemise, TypeMesure.V, TypeVetement.CHEMISE, commande, listeMesures);
                    addMesure(mesureTMChemise, TypeMesure.TM, TypeVetement.CHEMISE, commande, listeMesures);
                    addMesure(mesureABChemise, TypeMesure.AB, TypeVetement.CHEMISE, commande, listeMesures);
                    addMesure(mesure2PChemise, TypeMesure.P, TypeVetement.CHEMISE, commande, listeMesures);
                    addMesure(mesureColChemise, TypeMesure.COL, TypeVetement.CHEMISE, commande, listeMesures);
                    addMesure(mesureLChemise, TypeMesure.L, TypeVetement.CHEMISE, commande, listeMesures);

                    // VESTES
                    addMesure(mesureEVeste, TypeMesure.E, TypeVetement.VESTE, commande, listeMesures);
                    addMesure(mesureLMVeste, TypeMesure.LM, TypeVetement.VESTE, commande, listeMesures);
                    addMesure(mesurePVeste, TypeMesure.P, TypeVetement.VESTE, commande, listeMesures);
                    addMesure(mesureVVeste, TypeMesure.V, TypeVetement.VESTE, commande, listeMesures);
                    addMesure(mesureTMVeste, TypeMesure.TM, TypeVetement.VESTE, commande, listeMesures);
                    addMesure(mesureABVeste, TypeMesure.AB, TypeVetement.VESTE, commande, listeMesures);
                    addMesure(mesureCAVeste, TypeMesure.CA, TypeVetement.VESTE, commande, listeMesures);
                    addMesure(mesureCDVeste, TypeMesure.CD, TypeVetement.VESTE, commande, listeMesures);
                    addMesure(mesureLVeste, TypeMesure.L, TypeVetement.VESTE, commande, listeMesures);

                    //PANTALON
                    addMesure(mesureTPantalon, TypeMesure.T, TypeVetement.PANTALON, commande, listeMesures);
                    addMesure(mesureBPantalon, TypeMesure.B, TypeVetement.PANTALON, commande, listeMesures);
                    addMesure(mesureCPantalon, TypeMesure.C, TypeVetement.PANTALON, commande, listeMesures);
                    addMesure(mesureLPantalon, TypeMesure.L, TypeVetement.PANTALON, commande, listeMesures);
                    addMesure(mesurePPantalon, TypeMesure.P, TypeVetement.PANTALON, commande, listeMesures);
                    addMesure(mesure2BPantalon, TypeMesure.B, TypeVetement.PANTALON, commande, listeMesures);
                    addMesure(mesuremPantalon, TypeMesure.m, TypeVetement.PANTALON, commande, listeMesures);
                    mesureService.saveMesures(listeMesures, client);
                }
            }
        }
    }

    private void addMesure(String mesureValueString,
                           TypeMesure typeMesure,
                           TypeVetement typeVetement,
                           Commande commande,
                           List<Mesure> mesures) {
        if (!mesureValueString.trim().isEmpty()) {
            Mesure mesure = new Mesure();
            mesure.setTypeMesure(typeMesure);
            mesure.setTypeVetement(typeVetement);
            mesure.setCommande(commande);
            mesure.setValeur(Double.parseDouble(mesureValueString.replace(",", ".")));
            mesures.add(mesure);
        }
    }

    private LocalDate extractDate(String date) {
        String[] dateData = date.split("/");
        LocalDate localDate;
        if (dateData[2].length() == 2) {
            localDate = LocalDate.of(Integer.parseInt("20" + dateData[2]),
                    Integer.parseInt(dateData[0]), Integer.parseInt(dateData[1]));
        } else {
            localDate = LocalDate.parse(date, formatter);
        }
        return localDate;
    }


}
