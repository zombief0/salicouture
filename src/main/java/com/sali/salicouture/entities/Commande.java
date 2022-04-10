package com.sali.salicouture.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    @ManyToOne(optional = false)
    private Client client;

    @OneToMany(mappedBy = "commande")
    private List<Mesure> mesures = new ArrayList<>();
}
