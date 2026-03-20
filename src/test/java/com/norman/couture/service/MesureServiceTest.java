package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import com.norman.couture.entities.Mesure;
import com.norman.couture.entities.enums.TypeMesure;
import com.norman.couture.entities.enums.TypeVetement;
import com.norman.couture.exception.CommandeNotFoundException;
import com.norman.couture.exception.MesureNotFoundException;
import com.norman.couture.repositories.ClientRepository;
import com.norman.couture.repositories.CommandeRepository;
import com.norman.couture.repositories.MesureRepository;
import com.norman.couture.service.dto.mesure.MesureResponseDto;
import com.norman.couture.service.dto.mesure.SaveMesureDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MesureServiceTest {

    @Mock
    private MesureRepository mesureRepository;

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private MesureService mesureService;

    @Test
    void ajouter_shouldThrowWhenCommandeMissing() {
        when(commandeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommandeNotFoundException.class, () -> mesureService.ajouter(validSaveMesure(), 1L));
    }

    @Test
    void ajouter_shouldSaveMesureWhenCommandeExists() {
        Commande commande = new Commande();
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(mesureRepository.save(any(Mesure.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mesureService.ajouter(validSaveMesure(), 1L);

        verify(mesureRepository).save(any(Mesure.class));
    }

    @Test
    void update_shouldThrowWhenMesureMissing() {
        when(mesureRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(MesureNotFoundException.class, () -> mesureService.update(validSaveMesure(), 3L));
    }

    @Test
    void delete_shouldThrowWhenMesureMissing() {
        when(mesureRepository.existsById(4L)).thenReturn(false);

        assertThrows(MesureNotFoundException.class, () -> mesureService.delete(4L));
    }

    @Test
    void delete_shouldDeleteWhenMesureExists() {
        when(mesureRepository.existsById(4L)).thenReturn(true);

        mesureService.delete(4L);

        verify(mesureRepository).deleteById(4L);
    }

    @Test
    void saveMesures_shouldSkipWhenListEmpty() {
        Client client = new Client();
        client.setId(8L);

        mesureService.saveMesures(List.of(), client);

        verify(mesureRepository, never()).saveAll(any());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void saveMesures_shouldPromoteClientStandardFlagsAndPersist() {
        Client client = new Client();
        client.setId(8L);
        client.setExistMesureStandardChemise(false);

        Commande commande = new Commande();
        Mesure mesure = new Mesure();
        mesure.setCommande(commande);
        mesure.setTypeVetement(TypeVetement.CHEMISE);
        mesure.setTypeMesure(TypeMesure.COL);
        mesure.setValeur(39.0);

        mesureService.saveMesures(List.of(mesure), client);

        verify(commandeRepository).save(commande);
        verify(mesureRepository).saveAll(any());
        verify(clientRepository).save(client);
    }

    @Test
    void listerMesuresStandards_shouldMapDtos() {
        Mesure mesure = new Mesure();
        mesure.setId(12L);
        mesure.setTypeVetement(TypeVetement.VESTE);
        mesure.setTypeMesure(TypeMesure.AB);
        mesure.setValeur(20.0);

        when(mesureRepository.findAllByClient_Id(55L)).thenReturn(List.of(mesure));

        List<MesureResponseDto> result = mesureService.listerMesuresStandards(55L);

        assertEquals(1, result.size());
        assertEquals(12L, result.getFirst().getId());
        assertEquals(TypeVetement.VESTE, result.getFirst().getTypeVetement());
    }

    private SaveMesureDto validSaveMesure() {
        SaveMesureDto dto = new SaveMesureDto();
        dto.setTypeVetement(TypeVetement.CHEMISE);
        dto.setTypeMesure(TypeMesure.COL);
        dto.setValeur(38.5);
        return dto;
    }
}
