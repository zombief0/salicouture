package com.sali.salicouture.restcontroller;

import com.sali.salicouture.entities.Commande;
import com.sali.salicouture.service.CommandeService;
import com.sali.salicouture.service.dto.MessageResponse;
import com.sali.salicouture.service.dto.commande.CommandesClientDto;
import com.sali.salicouture.service.dto.commande.SaveCommandeDto;
import com.sali.salicouture.service.dto.enums.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/commande")
@RequiredArgsConstructor
public class CommandeRestController {
    private final CommandeService commandeService;
    private final ControllerTools controllerTools;

    @GetMapping("/{id}")
    public Commande getById(@PathVariable Long id) {
        return commandeService.getById(id);
    }
    @GetMapping
    public List<Commande> lister() {
        return commandeService.listerAll();
    }

    @GetMapping("/by-client/{idClient}")
    public CommandesClientDto listerByClient(@PathVariable Long idClient) {
        return commandeService.listerByClient(idClient);
    }

    @PostMapping("/{idClient}")
    public ResponseEntity<MessageResponse> saveCommande(@RequestBody @Valid SaveCommandeDto saveCommandeDto, @PathVariable Long idClient) {
        Message createMessage = commandeService.createCommande(saveCommandeDto, idClient);
        return controllerTools.getResponse(createMessage, HttpStatus.CREATED);
    }

    @PutMapping("/{idCommande}")
    public ResponseEntity<MessageResponse> updateCommande(@RequestBody @Valid SaveCommandeDto saveCommande,
                                                          @PathVariable Long idCommande) {
        Message createMessage = commandeService.update(saveCommande, idCommande);
        return controllerTools.getResponse(createMessage, HttpStatus.OK);
    }

    @PatchMapping("/livrer-non-livrer/{idCommande}")
    public void livrerNonLivrer(@PathVariable Long idCommande){
        commandeService.livrerNonLivrer(idCommande);
    }
}
