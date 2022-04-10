package com.sali.salicouture.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
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

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Commande> commandes = new ArrayList<>();
}
