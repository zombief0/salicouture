package com.norman.couture.restcontroller;

import com.norman.couture.service.ExcelService;
import com.norman.couture.service.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/excel")
@Slf4j
public class ExcelRestController {
    private final ExcelService excelService;

    @PostMapping
    public ResponseEntity<?> loadExcelDataOld(@RequestPart MultipartFile file) {
        try {
            log.info("API POST /api/excel appelée: fileName={}", file.getOriginalFilename());
            excelService.lireFichierExcel(file, true);
            log.info("Import Excel ancien format terminé: fileName={}", file.getOriginalFilename());
            return ResponseEntity.ok(new MessageResponse("UPLOAD_SUCCES"));
        } catch (IOException e) {
            log.error("Erreur IO pendant import Excel ancien format: fileName={}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> loadExcelDateNew(@RequestPart MultipartFile file) {
        try {
            log.info("API POST /api/excel/new appelée: fileName={}", file.getOriginalFilename());
            excelService.lireFichierExcel(file, false);
            log.info("Import Excel nouveau format terminé: fileName={}", file.getOriginalFilename());
            return ResponseEntity.ok(new MessageResponse("UPLOAD_SUCCES"));
        } catch (IOException e) {
            log.error("Erreur IO pendant import Excel nouveau format: fileName={}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(e.getMessage()));
        }
    }
}
