package com.sali.salicouture.service.dto.client;

import com.sali.salicouture.entities.enums.Sexe;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SaveClientDto {
    @NotEmpty
    private String noms;
    private String prenoms;
    @NotEmpty
    private String telephone;
    @Email
    private String email;
    private String anniversaire;

    @NotNull
    private Sexe sexe;
}
