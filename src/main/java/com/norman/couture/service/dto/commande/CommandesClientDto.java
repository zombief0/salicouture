package com.norman.couture.service.dto.commande;

import com.norman.couture.service.dto.client.ClientResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommandesClientDto {
    private List<CommandeResponseDto> commandes = new ArrayList<>();
    private ClientResponseDto client;
}
