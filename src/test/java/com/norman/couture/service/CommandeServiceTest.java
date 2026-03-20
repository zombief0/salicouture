package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.Commande;
import com.norman.couture.entities.Mesure;
import com.norman.couture.entities.enums.Echeance;
import com.norman.couture.entities.enums.TypeVetement;
import com.norman.couture.exception.ClientNotFoundException;
import com.norman.couture.exception.CommandeNotFoundException;
import com.norman.couture.exception.MesureStandardNotFoundException;
import com.norman.couture.repositories.ClientRepository;
import com.norman.couture.repositories.CommandeRepository;
import com.norman.couture.repositories.MesureRepository;
import com.norman.couture.service.dto.commande.CommandesClientDto;
import com.norman.couture.service.dto.commande.SaveCommandeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private MesureRepository mesureRepository;

    @InjectMocks
    private CommandeService commandeService;

    @Test
    void createCommande_shouldThrowWhenClientMissing() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> commandeService.createCommande(validSaveCommande(), 1L));
    }

    @Test
    void createCommande_shouldThrowWhenStandardChemiseMissing() {
        Client client = new Client();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mesureRepository.existsByClientAndTypeVetement(client, TypeVetement.CHEMISE)).thenReturn(false);

        SaveCommandeDto dto = validSaveCommande();
        dto.setUseMesureStandardChemise(true);

        assertThrows(MesureStandardNotFoundException.class, () -> commandeService.createCommande(dto, 1L));
        verify(commandeRepository, never()).save(any(Commande.class));
    }

    @Test
    void createCommande_shouldSaveCommande_whenInputIsValid() {
        Client client = new Client();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(invocation -> invocation.getArgument(0));

        commandeService.createCommande(validSaveCommande(), 1L);

        verify(commandeRepository).save(any(Commande.class));
    }

    @Test
    void update_shouldThrowWhenCommandeMissing() {
        when(commandeRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(CommandeNotFoundException.class, () -> commandeService.update(validSaveCommande(), 5L));
    }

    @Test
    void update_shouldDeleteStandardMesuresWhenFlagDisabled() {
        Commande commande = new Commande();
        Client client = new Client();
        commande.setClient(client);
        commande.setUseMesureStandardChemise(true);

        SaveCommandeDto dto = validSaveCommande();
        dto.setUseMesureStandardChemise(false);

        when(commandeRepository.findById(6L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(invocation -> invocation.getArgument(0));

        commandeService.update(dto, 6L);

        verify(mesureRepository).deleteAllByCommandeAndClientIsNullAndTypeVetement(eq(commande), eq(TypeVetement.CHEMISE));
    }

    @Test
    void listerAll_shouldReturnDtos() {
        Commande commande = new Commande();
        commande.setId(8L);
        commande.setClient(new Client());
        when(commandeRepository.findAll(any(Sort.class))).thenReturn(List.of(commande));

        assertEquals(1, commandeService.listerAll().size());
    }

    @Test
    void listerByClient_shouldThrowWhenClientMissing() {
        when(commandeRepository.findAllByClient_IdOrderByDateRetraitDesc(2L)).thenReturn(List.of());
        when(clientRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> commandeService.listerByClient(2L));
    }

    @Test
    void listerByClient_shouldReturnPayloadWhenClientExists() {
        Client client = new Client();
        client.setId(2L);
        client.setNoms("NDIAYE");
        Commande commande = new Commande();
        commande.setId(100L);
        commande.setClient(client);

        when(commandeRepository.findAllByClient_IdOrderByDateRetraitDesc(2L)).thenReturn(List.of(commande));
        when(clientRepository.findById(2L)).thenReturn(Optional.of(client));

        CommandesClientDto result = commandeService.listerByClient(2L);

        assertEquals(1, result.getCommandes().size());
        assertEquals(2L, result.getClient().getId());
    }

    @Test
    void livrerNonLivrer_shouldToggleWhenCommandeExists() {
        Commande commande = new Commande();
        commande.setLivrer(false);
        when(commandeRepository.findById(9L)).thenReturn(Optional.of(commande));

        commandeService.livrerNonLivrer(9L);

        assertTrue(commande.isLivrer());
        verify(commandeRepository).save(commande);
    }

    @Test
    void getById_shouldThrowWhenCommandeMissing() {
        when(commandeRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(CommandeNotFoundException.class, () -> commandeService.getById(50L));
    }

    @Test
    void createCommande_shouldThrowIllegalArgumentExceptionWhenDtoNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> commandeService.createCommande(null, 1L));
        assertTrue(ex.getMessage().contains("saveCommandeDto"));
    }

    @Test
    void createCommande_shouldCloneAndSaveMeasuresWhenUsingStandard() {
        Client client = new Client();
        client.setId(1L);

        Mesure mesure = new Mesure();
        mesure.setTypeVetement(TypeVetement.CHEMISE);
        mesure.setValeur(30.0);

        SaveCommandeDto dto = validSaveCommande();
        dto.setUseMesureStandardChemise(true);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mesureRepository.existsByClientAndTypeVetement(client, TypeVetement.CHEMISE)).thenReturn(true);
        when(mesureRepository.findAllByClientAndTypeVetement(client, TypeVetement.CHEMISE)).thenReturn(List.of(mesure));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(invocation -> invocation.getArgument(0));

        commandeService.createCommande(dto, 1L);

        verify(mesureRepository).save(any(Mesure.class));
    }

    private SaveCommandeDto validSaveCommande() {
        SaveCommandeDto dto = new SaveCommandeDto();
        dto.setEcheance(Echeance.H24);
        dto.setCoutTotal(10000L);
        dto.setAvance(5000L);
        dto.setReste(5000L);
        dto.setNotes("notes");
        return dto;
    }
}
