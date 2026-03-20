package com.norman.couture.service;

import com.norman.couture.entities.Client;
import com.norman.couture.entities.enums.Sexe;
import com.norman.couture.exception.ClientAlreadyExistsException;
import com.norman.couture.exception.ClientNotFoundException;
import com.norman.couture.repositories.ClientRepository;
import com.norman.couture.service.dto.client.ClientResponseDto;
import com.norman.couture.service.dto.client.SaveClientDto;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void enregistrer_shouldThrowWhenDtoIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> clientService.enregistrer(null));
        assertTrue(ex.getMessage().contains("saveClientDto"));
    }

    @Test
    void enregistrer_shouldThrowWhenClientAlreadyExists() {
        SaveClientDto dto = validSaveDto();
        when(clientRepository.existsByNomsIgnoreCaseAndPrenomsIgnoreCase("ndiaye", "awa")).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class, () -> clientService.enregistrer(dto));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void enregistrer_shouldSaveUppercaseNames() {
        SaveClientDto dto = validSaveDto();
        Client saved = new Client();
        saved.setId(1L);

        when(clientRepository.existsByNomsIgnoreCaseAndPrenomsIgnoreCase(anyString(), anyString())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(saved);

        clientService.enregistrer(dto);

        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void saveClientExcel_shouldReturnExistingClientWhenFound() {
        Client incoming = new Client();
        incoming.setNoms("NDIAYE");
        incoming.setPrenoms("AWA");

        Client existing = new Client();
        existing.setId(10L);

        when(clientRepository.findByNomsIgnoreCaseAndPrenomsIgnoreCase("NDIAYE", "AWA")).thenReturn(existing);

        Client result = clientService.saveClientExcel(incoming);

        assertEquals(10L, result.getId());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void update_shouldThrowWhenClientMissing() {
        when(clientRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.update(validSaveDto(), 20L));
    }

    @Test
    void update_shouldThrowWhenDuplicateBelongsToAnotherClient() {
        Client current = new Client();
        current.setId(1L);
        Client duplicate = new Client();
        duplicate.setId(2L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(current));
        when(clientRepository.findByNomsIgnoreCaseAndPrenomsIgnoreCase(anyString(), anyString())).thenReturn(duplicate);

        assertThrows(ClientAlreadyExistsException.class, () -> clientService.update(validSaveDto(), 1L));
    }

    @Test
    void lister_shouldMapEntitiesToDtos() {
        Client c = new Client();
        c.setId(3L);
        c.setNoms("NDIAYE");
        c.setPrenoms("AWA");
        c.setSexe(Sexe.FEMININ);

        when(clientRepository.findAll(any(Sort.class))).thenReturn(List.of(c));

        List<ClientResponseDto> result = clientService.lister();

        assertEquals(1, result.size());
        assertEquals(3L, result.getFirst().getId());
        assertEquals("NDIAYE", result.getFirst().getNoms());
        assertEquals(Sexe.FEMININ, result.getFirst().getSexe());
    }

    private SaveClientDto validSaveDto() {
        SaveClientDto dto = new SaveClientDto();
        dto.setNoms("ndiaye");
        dto.setPrenoms("awa");
        dto.setTelephone("770000000");
        dto.setEmail("awa@example.com");
        dto.setAnniversaire("1990-01-01");
        dto.setSexe(Sexe.FEMININ);
        return dto;
    }
}
