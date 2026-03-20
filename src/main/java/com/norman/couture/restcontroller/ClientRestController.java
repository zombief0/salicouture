package com.norman.couture.restcontroller;

import com.norman.couture.service.ClientService;
import com.norman.couture.service.dto.client.ClientResponseDto;
import com.norman.couture.service.dto.client.SaveClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Slf4j
public class ClientRestController {
    private final ClientService clientService;

    @GetMapping
    public List<ClientResponseDto> lister() {
        log.debug("API GET /api/client appelée");
        return clientService.lister();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void ajouter(@RequestBody @Valid SaveClientDto saveClient) {
        log.info("API POST /api/client appelée");
        clientService.enregistrer(saveClient);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody @Valid SaveClientDto saveClient) {
        log.info("API PUT /api/client/{} appelée", id);
        clientService.update(saveClient, id);
    }
}
