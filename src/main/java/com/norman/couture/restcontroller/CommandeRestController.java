package com.norman.couture.restcontroller;

import com.norman.couture.service.CommandeService;
import com.norman.couture.service.dto.commande.CommandesClientDto;
import com.norman.couture.service.dto.commande.CommandeResponseDto;
import com.norman.couture.service.dto.commande.SaveCommandeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/commande")
@RequiredArgsConstructor
@Slf4j
public class CommandeRestController {
    private final CommandeService commandeService;

    @GetMapping("/{id}")
    public CommandeResponseDto getById(@PathVariable Long id) {
        log.debug("API GET /api/commande/{} appelée", id);
        return commandeService.getById(id);
    }

    @GetMapping
    public List<CommandeResponseDto> lister() {
        log.debug("API GET /api/commande appelée");
        return commandeService.listerAll();
    }

    @GetMapping("/by-client/{idClient}")
    public CommandesClientDto listerByClient(@PathVariable Long idClient) {
        log.debug("API GET /api/commande/by-client/{} appelée", idClient);
        return commandeService.listerByClient(idClient);
    }

    @PostMapping("/{idClient}")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveCommande(@RequestBody @Valid SaveCommandeDto saveCommandeDto, @PathVariable Long idClient) {
        log.info("API POST /api/commande/{} appelée", idClient);
        commandeService.createCommande(saveCommandeDto, idClient);
    }

    @PutMapping("/{idCommande}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCommande(@RequestBody @Valid SaveCommandeDto saveCommande, @PathVariable Long idCommande) {
        log.info("API PUT /api/commande/{} appelée", idCommande);
        commandeService.update(saveCommande, idCommande);
    }

    @PatchMapping("/livrer-non-livrer/{idCommande}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void livrerNonLivrer(@PathVariable Long idCommande) {
        log.info("API PATCH /api/commande/livrer-non-livrer/{} appelée", idCommande);
        commandeService.livrerNonLivrer(idCommande);
    }
}
