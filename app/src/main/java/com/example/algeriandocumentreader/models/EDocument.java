package com.example.algeriandocumentreader.models;

import android.util.Log;

import com.example.algeriandocumentreader.utils.ImageUtil;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class EDocument implements Serializable {

    private String docType;
    private PersonDetails personDetails;
    private AdditionalPersonDetails additionalPersonDetails;
    private PublicKey docPublicKey;
    private DocumentDetails documentDetails;

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public PersonDetails getPersonDetails() {
        return personDetails;
    }

    public void setPersonDetails(PersonDetails personDetails) {
        this.personDetails = personDetails;
    }

    public AdditionalPersonDetails getAdditionalPersonDetails() {
        return additionalPersonDetails;
    }

    public void setAdditionalPersonDetails(AdditionalPersonDetails additionalPersonDetails) {
        this.additionalPersonDetails = additionalPersonDetails;
    }

    public PublicKey getDocPublicKey() {
        return docPublicKey;
    }

    public void setDocPublicKey(PublicKey docPublicKey) {
        this.docPublicKey = docPublicKey;
    }

    public void setDocumentDetails(DocumentDetails documentDetails) {
        this.documentDetails = documentDetails;
    }

    public DocumentDetails getDocumentDetails() {
        return documentDetails;
    }

    public Map<String, Object> getDocumentDetailsMap() {
        Map<String, Object> dataMustShow = new LinkedHashMap<>();
        if (this.personDetails.getFaceImageBase64() != null || this.documentDetails.getDocumentFrontImageBase64() != null) {
            if (this.personDetails.getFaceImageBase64() != null)
                dataMustShow.put("Face Image", this.personDetails.getFaceImage());
            else
                dataMustShow.put("Face Image", this.documentDetails.getDocumentFrontImage());
        }
        dataMustShow.put("Full Name", this.personDetails.getName() + " " + this.personDetails.getSurname());
        //arabic Full name if exists
        if (Objects.equals(this.docType, "ID")) {
            dataMustShow.put("الاسم الكامل", this.additionalPersonDetails.getArabicName() + " " + this.additionalPersonDetails.getArabicSurname());
        }
        dataMustShow.put("Document Number", this.personDetails.getSerialNumber());
        dataMustShow.put("Document type", this.documentDetails.getDocumentType());
        dataMustShow.put("Nationality", this.personDetails.getNationality());
        dataMustShow.put("National Number", this.personDetails.getPersonalNumber());
        dataMustShow.put("Birth Date", this.personDetails.getBirthDate());
        dataMustShow.put("Birth Place", this.additionalPersonDetails.getPlaceOfBirth().iterator().next());
        dataMustShow.put("Issue Date Document", this.documentDetails.getDocumentDateOfIssue().replace("<<", ""));
        dataMustShow.put("Expiry Date Document", this.documentDetails.getDocumentDateOfExpiry());
        dataMustShow.put("Issuing Authority", this.documentDetails.getIssuingAuthority());
        dataMustShow.put("Signature", this.personDetails.getSignature());
        return dataMustShow;
    }
}
