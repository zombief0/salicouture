package com.norman.couture.service.excel;

import com.norman.couture.entities.enums.Echeance;
import com.norman.couture.entities.enums.Sexe;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public record ExcelImportRow(String noms,
                             String prenoms,
                             Sexe sexe,
                             String telephone,
                             String email,
                             String anniversaire,
                             LocalDate dateCommande,
                             Echeance echeance,
                             LocalDate dateRetrait,
                             Long coutTotal,
                             Long avance,
                             Long reste,
                             String notes,
                             List<MesureImportValue> mesures) {

    public String nomsUpper() {
        return noms.toUpperCase(Locale.ROOT);
    }

    public String prenomsUpper() {
        return prenoms.toUpperCase(Locale.ROOT);
    }

    public String cacheKey() {
        return nomsUpper() + "|" + prenomsUpper();
    }
}
