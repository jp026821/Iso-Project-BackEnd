package com.example.loginframe.Service;

import com.example.loginframe.Entity.AuditDetails;
import com.example.loginframe.Entity.IsoStandard;
import com.example.loginframe.Repository.AuditDetailsRepository;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    @Autowired
    private AuditDetailsRepository auditDetailsRepository;

    public byte[] generateCertificate(Long auditId) throws IOException {

        // ── Fetch audit from DB by auditId ──
        AuditDetails audit = auditDetailsRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit not found for id: " + auditId));

        // ── Extract all required parameters from the audit entity ──
        String companyName  = (audit.getProfile() != null && audit.getProfile().getLoginEmail() != null)
                ? audit.getProfile().getLoginEmail()
                : "N/A";

        String isoStandard  = (audit.getIsoStandards() != null && !audit.getIsoStandards().isEmpty())
                ? audit.getIsoStandards().stream()
                .map(IsoStandard::getIsoCode)
                .collect(Collectors.joining(", "))
                : "N/A";

        String issueDate = (audit.getUpdateTime() != null)
                ? audit.getUpdateTime().toString()
                : "N/A";

        String expiryDate   = (audit.getPreferredDate() != null)
                ? audit.getPreferredDate().plusYears(3).toString()   // 3-year validity; adjust as needed
                : "N/A";

        String auditorName  = (audit.getAssignedAuditor() != null)
                ? audit.getAssignedAuditor()
                : "N/A";

        // ── PDF generation (unchanged logic) ──
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4.rotate());

        PdfFont titleFont  = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont boldFont   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        DeviceRgb darkBlue  = new DeviceRgb(0, 51, 102);
        DeviceRgb gold      = new DeviceRgb(184, 148, 50);
        DeviceRgb lightGray = new DeviceRgb(245, 245, 245);

        Table borderTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(gold, 5))
                .setBackgroundColor(lightGray)
                .setPadding(24);

        Cell card = new Cell().setBorder(Border.NO_BORDER).setPadding(12);

        card.add(new Paragraph("AUDIT MANAGEMENT SYSTEM")
                .setFont(boldFont).setFontSize(11)
                .setFontColor(darkBlue)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(2));

        card.add(new Paragraph("CERTIFICATE OF CONFORMITY")
                .setFont(titleFont).setFontSize(30)
                .setFontColor(darkBlue)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(4));

        card.add(divider(gold));

        card.add(new Paragraph("This is to certify that")
                .setFont(normalFont).setFontSize(13)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(6));

        card.add(new Paragraph(companyName)
                .setFont(titleFont).setFontSize(24)
                .setFontColor(darkBlue)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(6));

        card.add(new Paragraph("has been assessed and certified to comply with the requirements of")
                .setFont(normalFont).setFontSize(13)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(6));

        card.add(new Paragraph(isoStandard)
                .setFont(titleFont).setFontSize(22)
                .setFontColor(gold)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(16));

        card.add(divider(gold));

        Table details = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .useAllAvailableWidth()
                .setBorder(Border.NO_BORDER)
                .setMarginTop(10);

        details.addCell(detailCell("Issue Date",   issueDate,   boldFont, normalFont, darkBlue));
        details.addCell(detailCell("Expiry Date",  expiryDate,  boldFont, normalFont, darkBlue));
        details.addCell(detailCell("Certified By", auditorName, boldFont, normalFont, darkBlue));
        card.add(details);

        card.add(new Paragraph(
                "\nThis certificate is issued under the authority of the Audit Management System " +
                        "and is valid for the period stated above.")
                .setFont(normalFont).setFontSize(9)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(14));

        borderTable.addCell(card);
        document.add(borderTable);
        document.close();

        return baos.toByteArray();
    }

    private Paragraph divider(DeviceRgb color) {
        return new Paragraph(
                "──────────────────────────────────────────────────────────────────────")
                .setFontColor(color)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
    }

    private Cell detailCell(String label, String value,
                            PdfFont boldFont, PdfFont normalFont,
                            DeviceRgb darkBlue) {
        Cell cell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);

        cell.add(new Paragraph(label)
                .setFont(boldFont).setFontSize(10)
                .setFontColor(darkBlue));

        cell.add(new Paragraph(value)
                .setFont(normalFont).setFontSize(12)
                .setFontColor(ColorConstants.BLACK));

        return cell;
    }
}