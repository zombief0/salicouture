package com.norman.couture.restcontroller;

import com.norman.couture.entities.Client;
import com.norman.couture.service.ClientService;
import com.norman.couture.service.dto.MessageResponse;
import com.norman.couture.service.dto.client.SaveClientDto;
import com.norman.couture.service.dto.enums.Message;
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
