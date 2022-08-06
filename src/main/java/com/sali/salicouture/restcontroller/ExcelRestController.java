package com.sali.salicouture.restcontroller;

import com.sali.salicouture.service.ExcelService;
import com.sali.salicouture.service.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
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
public class ExcelRestController {
    private final ExcelService excelService;

    @PostMapping
    public ResponseEntity<?> loadExcelDataOld(@RequestPart MultipartFile file) {
        try {
            excelService.lireFichierExcel(file, true);
            return ResponseEntity.ok(new MessageResponse("UPLOAD_SUCCES"));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> loadExcelDateNew(@RequestPart MultipartFile file) {
        try {
            excelService.lireFichierExcel(file, false);
            return ResponseEntity.ok(new MessageResponse("UPLOAD_SUCCES"));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(e.getMessage()));
        }
    }
}
