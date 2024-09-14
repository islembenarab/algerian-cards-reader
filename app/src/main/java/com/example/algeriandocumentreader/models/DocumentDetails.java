package com.example.algeriandocumentreader.models;

import android.graphics.Bitmap;

import com.example.algeriandocumentreader.utils.ImageUtil;

import java.io.Serializable;

public class DocumentDetails implements Serializable {
    private String issuingAuthority;
    private String documentType;
    private String documentNumber;
    private String documentDateOfIssue;
    private String documentDateOfExpiry;
    private Bitmap documentFrontImage;
    private String documentFrontImageBase64;

    public void setIssuingAuthority(String issuingAuthority) {

        this.issuingAuthority = issuingAuthority;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setDocumentType(String documentType) {
        if (documentType.equals("P")) {
            documentType = "Passport";
        } else if (documentType.equals("ID")) {
            documentType = "ID Card";
        }
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentDateOfIssue(String documentDateOfIssue) {
        this.documentDateOfIssue = documentDateOfIssue;
    }

    public String getDocumentDateOfIssue() {
        return documentDateOfIssue;
    }

    public void setDocumentDateOfExpiry(String documentDateOfExpiry) {
        this.documentDateOfExpiry = documentDateOfExpiry;
    }

    public String getDocumentDateOfExpiry() {
        return documentDateOfExpiry;
    }

    public void setDocumentFrontImage(Bitmap documentFrontImage) {
        this.documentFrontImage = documentFrontImage;
    }

    public Bitmap getDocumentFrontImage() {
        return ImageUtil.decodeBase64ToBitmap(documentFrontImageBase64);
    }

    public String getDocumentFrontImageBase64() {
        return documentFrontImageBase64;
    }

    public void setDocumentFrontImageBase64(String documentFrontImageBase64) {
        this.documentFrontImageBase64 = documentFrontImageBase64;
    }
}
