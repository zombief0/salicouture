package com.sali.salicouture.service.dto.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveClientDto {
    private String noms;
    private String prenoms;
    private String telephone;
    private String email;
    private String anniversaire;
}
