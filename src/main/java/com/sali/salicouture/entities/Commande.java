package com.sali.salicouture.entities;

import com.sali.salicouture.entities.enums.Echeance;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Commande extends BaseEntity{
    private LocalDate dateCommande;
    private LocalDate dateRetrait;
    private Long coutTotal;
    private Long avance;
    private Long reste;
    @Column(columnDefinition = "TEXT")
    private String notes;
    @Enumerated(EnumType.STRING)
    private Echeance echeance;
    private boolean useMesureStandardVeste;
    private boolean useMesureStandardPantalon;
    private boolean useMesureStandardChemise;
    private boolean livrer;

    @ManyToOne(optional = false)
    private Client client;

    @OneToMany(mappedBy = "commande")
    private List<Mesure> mesures = new ArrayList<>();
}
