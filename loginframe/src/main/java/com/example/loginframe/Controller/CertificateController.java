package com.example.loginframe.Controller;

import com.example.loginframe.Service.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/audit/{auditId}/certificate")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long auditId) throws IOException {
        byte[] pdf = certificateService.generateCertificate(auditId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificate_" + auditId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
