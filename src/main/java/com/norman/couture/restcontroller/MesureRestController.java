package com.norman.couture.restcontroller;

import com.norman.couture.service.MesureService;
import com.norman.couture.service.dto.mesure.MesureResponseDto;
import com.norman.couture.service.dto.mesure.SaveMesureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/mesure/{id}")
@RequiredArgsConstructor
@Slf4j
public class MesureRestController {
    private final MesureService mesureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveMesure(@PathVariable(name = "id") Long idCommande,
                           @RequestBody @Valid SaveMesureDto saveMesureDto) {
        log.info("API POST /api/mesure/{} appelée", idCommande);
        mesureService.ajouter(saveMesureDto, idCommande);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMesure(@PathVariable(name = "id") Long idMesure,
                             @RequestBody @Valid SaveMesureDto saveMesure) {
        log.info("API PUT /api/mesure/{} appelée", idMesure);
        mesureService.update(saveMesure, idMesure);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMesure(@PathVariable(name = "id") Long idMesure) {
        log.info("API DELETE /api/mesure/{} appelée", idMesure);
        mesureService.delete(idMesure);
    }

    @GetMapping
    public List<MesureResponseDto> listerMesureDuClient(@PathVariable(name = "id") Long idClient) {
        log.debug("API GET /api/mesure/{} appelée", idClient);
        return mesureService.listerMesuresStandards(idClient);
    }
}
