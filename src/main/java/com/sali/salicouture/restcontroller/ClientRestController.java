package com.sali.salicouture.restcontroller;

import com.sali.salicouture.entities.Client;
import com.sali.salicouture.service.ClientService;
import com.sali.salicouture.service.dto.MessageResponse;
import com.sali.salicouture.service.dto.client.SaveClientDto;
import com.sali.salicouture.service.dto.enums.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientRestController {
    private final ClientService clientService;
    private final ControllerTools controllerTools;

    @GetMapping
    public List<Client> lister() {
        return clientService.lister();
    }

    @PostMapping
    public ResponseEntity<MessageResponse> ajouter(@RequestBody @Valid SaveClientDto saveClient){
        Message createMessage = clientService.enregistrer(saveClient);
        return controllerTools.getResponse(createMessage, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid SaveClientDto saveClient) {
        Message createMessage = clientService.update(saveClient, id);
        return controllerTools.getResponse(createMessage, HttpStatus.OK);
    }

}
