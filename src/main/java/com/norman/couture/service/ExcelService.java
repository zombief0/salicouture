package com.norman.couture.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelService {
    void lireFichierExcel(MultipartFile file, boolean ancienneCommande) throws IOException;
}
