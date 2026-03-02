package com.example.loginframe.Service;

import com.example.loginframe.Configrantion.DocumentConfig;
import com.example.loginframe.Entity.AuditDetails;
import com.example.loginframe.Entity.Documents;
import com.example.loginframe.Repository.AuditDetailsRepository;
import com.example.loginframe.Repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentConfig documentConfig;

    @Autowired
    private AuditDetailsRepository auditDetailsRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Documents uploadFile(MultipartFile file, Long auditId) throws IOException {


        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "application/pdf");

        if (!allowedTypes.contains(file.getContentType())) {
            throw new RuntimeException("Invalid file type!");
        }

        String originalFileName = new File(file.getOriginalFilename()).getName();

        // 3️⃣ Generate unique filename
        String uniqueFileName = UUID.randomUUID().toString()
                + "_" + originalFileName;

        // 4️⃣ Get upload directory
        String uploadDir = documentConfig.getUploadDir();

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 5️⃣ Save file to disk
        String fullPath = uploadDir + File.separator + uniqueFileName;
        file.transferTo(new File(fullPath));


        Documents document = new Documents();
        document.setFileName(uniqueFileName);
        document.setOriginalFileName(originalFileName);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setUploadedAt(LocalDateTime.now());

        AuditDetails auditDetails = auditDetailsRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit not found"));

        document.setAuditDetails(auditDetails);

        return documentRepository.save(document);
    }
}