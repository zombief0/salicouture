package com.norman.couture.service.excel;

import com.norman.couture.entities.enums.Echeance;
import com.norman.couture.entities.enums.Sexe;
import com.norman.couture.entities.enums.TypeMesure;
import com.norman.couture.entities.enums.TypeVetement;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class ExcelImportRowParser {
    private static final DateTimeFormatter DATE_FORMATTER_4_DIGITS = DateTimeFormatter.ofPattern("d/M/uuuu");
    private static final DateTimeFormatter DATE_FORMATTER_2_DIGITS = DateTimeFormatter.ofPattern("d/M/uu");

    private static final List<MesureCellSpec> MESURE_CELL_SPECS = List.of(
            new MesureCellSpec(13, TypeMesure.E, TypeVetement.CHEMISE),
            new MesureCellSpec(14, TypeMesure.LM, TypeVetement.CHEMISE),
            new MesureCellSpec(15, TypeMesure.P, TypeVetement.CHEMISE),
            new MesureCellSpec(16, TypeMesure.V, TypeVetement.CHEMISE),
            new MesureCellSpec(17, TypeMesure.TM, TypeVetement.CHEMISE),
            new MesureCellSpec(18, TypeMesure.AB, TypeVetement.CHEMISE),
            new MesureCellSpec(19, TypeMesure.P, TypeVetement.CHEMISE),
            new MesureCellSpec(20, TypeMesure.COL, TypeVetement.CHEMISE),
            new MesureCellSpec(21, TypeMesure.L, TypeVetement.CHEMISE),

            new MesureCellSpec(22, TypeMesure.E, TypeVetement.VESTE),
            new MesureCellSpec(23, TypeMesure.LM, TypeVetement.VESTE),
            new MesureCellSpec(24, TypeMesure.P, TypeVetement.VESTE),
            new MesureCellSpec(25, TypeMesure.V, TypeVetement.VESTE),
            new MesureCellSpec(26, TypeMesure.TM, TypeVetement.VESTE),
            new MesureCellSpec(27, TypeMesure.AB, TypeVetement.VESTE),
            new MesureCellSpec(28, TypeMesure.CA, TypeVetement.VESTE),
            new MesureCellSpec(29, TypeMesure.CD, TypeVetement.VESTE),
            new MesureCellSpec(30, TypeMesure.L, TypeVetement.VESTE),

            new MesureCellSpec(31, TypeMesure.T, TypeVetement.PANTALON),
            new MesureCellSpec(32, TypeMesure.B, TypeVetement.PANTALON),
            new MesureCellSpec(33, TypeMesure.C, TypeVetement.PANTALON),
            new MesureCellSpec(34, TypeMesure.L, TypeVetement.PANTALON),
            new MesureCellSpec(35, TypeMesure.P, TypeVetement.PANTALON),
            new MesureCellSpec(36, TypeMesure.B, TypeVetement.PANTALON),
            new MesureCellSpec(37, TypeMesure.m, TypeVetement.PANTALON)
    );

    public Optional<ExcelImportRow> parse(Row row, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator) {
        String noms = getCellTrim(row, 0, dataFormatter, formulaEvaluator);
        if (noms.isEmpty()) {
            return Optional.empty();
        }

        String prenoms = getCellTrim(row, 1, dataFormatter, formulaEvaluator);
        String sexe = getCellTrim(row, 2, dataFormatter, formulaEvaluator);
        String telephone = getCellTrim(row, 3, dataFormatter, formulaEvaluator);
        String email = getCellTrim(row, 4, dataFormatter, formulaEvaluator);
        String anniversaire = getCellTrim(row, 5, dataFormatter, formulaEvaluator);

        String dateCommandeRaw = getCellTrim(row, 6, dataFormatter, formulaEvaluator);
        LocalDate dateCommande = dateCommandeRaw.isEmpty() ? null : extractDate(dateCommandeRaw);

        String echeanceRaw = getCellTrim(row, 7, dataFormatter, formulaEvaluator);
        Echeance echeance = parseEcheance(echeanceRaw);

        String dateRetraitRaw = getCellTrim(row, 8, dataFormatter, formulaEvaluator);
        LocalDate dateRetrait = dateRetraitRaw.isEmpty() ? null : extractDate(dateRetraitRaw);

        Long coutTotal = parseLongValue(getCellTrim(row, 9, dataFormatter, formulaEvaluator));
        Long avance = parseLongValue(getCellTrim(row, 10, dataFormatter, formulaEvaluator));
        Long reste = parseLongValue(getCellTrim(row, 11, dataFormatter, formulaEvaluator));
        String notes = getCellTrim(row, 12, dataFormatter, formulaEvaluator);

        List<MesureImportValue> mesures = new ArrayList<>();
        for (MesureCellSpec mesureCellSpec : MESURE_CELL_SPECS) {
            String mesureValue = getCellTrim(row, mesureCellSpec.index(), dataFormatter, formulaEvaluator);
            if (!mesureValue.isEmpty()) {
                mesures.add(new MesureImportValue(
                        mesureCellSpec.typeMesure(),
                        mesureCellSpec.typeVetement(),
                        Double.parseDouble(mesureValue.replace(",", "."))
                ));
            }
        }

        return Optional.of(new ExcelImportRow(
                noms,
                prenoms,
                parseSexe(sexe),
                telephone,
                email,
                anniversaire,
                dateCommande,
                echeance,
                dateRetrait,
                coutTotal,
                avance,
                reste,
                notes,
                mesures
        ));
    }

    private String getCellTrim(Row row, int index, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return "";
        }
        return dataFormatter.formatCellValue(cell, formulaEvaluator).trim();
    }

    private LocalDate extractDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMATTER_4_DIGITS);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(date, DATE_FORMATTER_2_DIGITS);
            } catch (DateTimeParseException ignored2) {
                return LocalDate.parse(date);
            }
        }
    }

    private Echeance parseEcheance(String echeance) {
        if (echeance.isEmpty()) {
            return Echeance.HNONE;
        }
        switch (echeance.toLowerCase(Locale.ROOT)) {
            case "24h":
                return Echeance.H24;
            case "48h":
                return Echeance.H48;
            case "72h":
                return Echeance.H72;
            default:
                return Echeance.HNONE;
        }
    }

    private Long parseLongValue(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        String normalizedValue = rawValue
                .replace("\u00A0", "")
                .replace(" ", "")
                .replace(",", ".");
        return Math.round(Double.parseDouble(normalizedValue));
    }

    private Sexe parseSexe(String sexe) {
        if (sexe == null || sexe.isBlank()) {
            return null;
        }
        return "M".equalsIgnoreCase(sexe) ? Sexe.MASCULIN : Sexe.FEMININ;
    }

    private record MesureCellSpec(int index, TypeMesure typeMesure, TypeVetement typeVetement) {
    }
}
