package com.sali.salicouture.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelService {
    void lireFichierExcel(MultipartFile file) throws IOException;
}
