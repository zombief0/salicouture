package com.sali.salicouture.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sali.salicouture.entities.enums.Sexe;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Client extends BaseEntity{
    private String noms;
    private String prenoms;
    private String telephone;
    private String email;
    private String anniversaire;
    private boolean existMesureStandardPantalon;
    private boolean existMesureStandardChemise;
    private boolean existMesureStandardVeste;
    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Commande> commandes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Mesure> mesuresStandards = new ArrayList<>();
}
