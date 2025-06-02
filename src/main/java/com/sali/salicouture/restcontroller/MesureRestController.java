package com.sali.salicouture.restcontroller;

import com.sali.salicouture.entities.Mesure;
import com.sali.salicouture.service.MesureService;
import com.sali.salicouture.service.dto.MessageResponse;
import com.sali.salicouture.service.dto.enums.Message;
import com.sali.salicouture.service.dto.mesure.SaveMesureDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/mesure/{id}")
@RequiredArgsConstructor
public class MesureRestController {
    private final MesureService mesureService;
    private final ControllerTools controllerTools;

    @PostMapping
    public ResponseEntity<MessageResponse> saveMesure(@PathVariable(name = "id") Long idCommande,
                                                       @RequestBody @Valid SaveMesureDto saveMesureDto) {
        Message createMessage = mesureService.ajouter(saveMesureDto, idCommande);
        return controllerTools.getResponse(createMessage, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<MessageResponse> updateMesure(@PathVariable(name = "id") Long idMesure,
                                                        @RequestBody @Valid SaveMesureDto saveMesure) {
        Message message = mesureService.update(saveMesure, idMesure);
        return controllerTools.getResponse(message, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> deleteMesure(@PathVariable(name = "id") Long idMesure) {

        Message message = mesureService.delete(idMesure);
        return controllerTools.getResponse(message, HttpStatus.OK);
    }

    @GetMapping
    public List<Mesure> listerMesureDuClient(@PathVariable(name = "id") Long idClient) {
        return mesureService.listerMesuresStandards(idClient);
    }
}
