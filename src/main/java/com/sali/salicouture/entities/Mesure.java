package com.sali.salicouture.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sali.salicouture.entities.enums.TypeMesure;
import com.sali.salicouture.entities.enums.TypeVetement;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Mesure extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private TypeVetement typeVetement;

    @Enumerated(EnumType.STRING)
    private TypeMesure typeMesure;
    private Double valeur;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Commande commande;

    @JsonIgnore
    @ManyToOne
    private Client client;

    public Mesure copy(){
        Mesure mesure = new Mesure();
        mesure.setTypeMesure(this.typeMesure);
        mesure.setValeur(this.valeur);
        mesure.setTypeVetement(this.typeVetement);
        return mesure;
    }
}
