package com.example.algeriandocumentreader.services;

// NfcDocumentReader.java - Responsible for reading document details from NFC

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.algeriandocumentreader.models.AdditionalPersonDetails;
import com.example.algeriandocumentreader.models.DocumentDetails;
import com.example.algeriandocumentreader.models.EDocument;
import com.example.algeriandocumentreader.models.PersonDetails;
import com.example.algeriandocumentreader.utils.Image;
import com.example.algeriandocumentreader.utils.ImageUtil;

import net.sf.scuba.smartcards.CardFileInputStream;
import net.sf.scuba.smartcards.CardServiceException;

import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CardSecurityFile;
import org.jmrtd.lds.DisplayedImageInfo;
import org.jmrtd.lds.LDSElement;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.PACEInfo;
import org.jmrtd.lds.SecurityInfo;
import org.jmrtd.lds.icao.COMFile;
import org.jmrtd.lds.icao.DG11File;
import org.jmrtd.lds.icao.DG12File;
import org.jmrtd.lds.icao.DG15File;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.icao.DG7File;
import org.jmrtd.lds.icao.MRZInfo;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NfcDocumentReader {

    private final PassportService passportService;
    private final BACKeySpec bacKey;
    private final EDocument eDocument;
    private final PersonDetails personDetails;
    private final AdditionalPersonDetails additionalPersonDetails;
    private final DocumentDetails documentDetails;
    private  Map<Integer, Object> dataGroupFiles = new HashMap<>();
    public NfcDocumentReader(PassportService passportService, BACKeySpec bacKey) {
        this.passportService = passportService;
        this.bacKey = bacKey;
        this.eDocument = new EDocument();
        this.personDetails = new PersonDetails();
        this.additionalPersonDetails = new AdditionalPersonDetails();
        this.documentDetails = new DocumentDetails();
    }

    public EDocument readDocument() throws Exception {
        passportService.open();

        performPACE();

        performBAC();

        List<Integer> dataGroupFilesIntegers = readComFile();
        dataGroupFiles=readDataGroupFiles(dataGroupFilesIntegers);
        readPersonalDetails();
        readFaceImage();
        readFingerprintImage();
        readSignaturePicture();
        readAdditionalDetails();
        readDocumentDetails();
        readPublicKey();
        eDocument.setPersonDetails(personDetails);
        eDocument.setAdditionalPersonDetails(additionalPersonDetails);
        eDocument.setDocumentDetails(documentDetails);
        return eDocument;
    }

    private void performPACE() throws Exception {
        try {
            CardSecurityFile cardSecurityFile = new CardSecurityFile(passportService.getInputStream(PassportService.EF_CARD_SECURITY,PassportService.DEFAULT_MAX_BLOCKSIZE));
            for (SecurityInfo securityInfo : cardSecurityFile.getSecurityInfos()) {
                if (securityInfo instanceof PACEInfo) {
                    PACEInfo paceInfo = (PACEInfo) securityInfo;
                    passportService.doPACE(bacKey, paceInfo.getObjectIdentifier(),
                            PACEInfo.toParameterSpec(paceInfo.getParameterId()), null);

                }
            }
        } catch (Exception e) {
            Log.w("NFC", "PACE failed", e);
            passportService.sendSelectApplet(false);
        }
    }

    private void performBAC() throws Exception {
        try {
            passportService.getInputStream(PassportService.EF_COM).read();
        } catch (Exception e) {
            passportService.doBAC(bacKey);
        }
    }
    private List<Integer> readComFile() throws Exception {
        CardFileInputStream comIn = passportService.getInputStream(PassportService.EF_COM,PassportService.DEFAULT_MAX_BLOCKSIZE);
        COMFile comFile = new COMFile(comIn);
        return LDSFileUtil.getDataGroupNumbers(comFile);
    }

    public Map<Integer ,Object> readDataGroupFiles(List<Integer> dataGroupNumbers) throws Exception {
        Map<Integer,Object> map = new HashMap<>();
        dataGroupNumbers.forEach(integer -> {
            short fid = LDSFileUtil.lookupFIDByDataGroupNumber(integer);
            try {
                map.put(integer, LDSFileUtil.getLDSFile(fid, passportService.getInputStream(fid,PassportService.DEFAULT_MAX_BLOCKSIZE)));
            } catch (IOException | CardServiceException e) {
                Log.e("NFC", "Error reading data group file number "+integer );
            }
        });
        return map;
    }

    private void readPersonalDetails() throws Exception {

        DG1File dg1File = (DG1File) dataGroupFiles.get(1);
        MRZInfo mrzInfo = dg1File.getMRZInfo();

        personDetails.setName(mrzInfo.getSecondaryIdentifier().replace("<", " ").trim());
        personDetails.setSurname(mrzInfo.getPrimaryIdentifier().replace("<", " ").trim());
        personDetails.setPersonalNumber(mrzInfo.getPersonalNumber());
        personDetails.setGender(mrzInfo.getGender().toString());
        personDetails.setBirthDate(mrzInfo.getDateOfBirth());
        personDetails.setExpiryDate(mrzInfo.getDateOfExpiry());
        personDetails.setSerialNumber(mrzInfo.getDocumentNumber());
        personDetails.setNationality(mrzInfo.getNationality());
        personDetails.setIssuerAuthority(mrzInfo.getIssuingState());
        eDocument.setDocType(mrzInfo.getDocumentCode());
    }

    private void readFaceImage() throws Exception {

        DG2File dg2File = (DG2File) dataGroupFiles.get(2);

        List<FaceInfo> faceInfos = dg2File.getFaceInfos();
        if (!faceInfos.isEmpty()) {
            FaceImageInfo faceImageInfo = faceInfos.iterator().next().getFaceImageInfos().iterator().next();
            InputStream imageInputStream = faceImageInfo.getImageInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream);
            personDetails.setFaceImageBase64(ImageUtil.encodeBitmapToBase64(bitmap));
        }
    }

    private void readFingerprintImage() throws Exception {

        // Set fingerprint details as necessary

    }

    private void readSignaturePicture() throws Exception {
        // Similar approach for portrait image
        DG7File dg7File = (DG7File) dataGroupFiles.get(7);
        List<DisplayedImageInfo> signatureImageInfos = dg7File.getImages();
        if (!signatureImageInfos.isEmpty()) {
            DisplayedImageInfo displayedImageInfo = signatureImageInfos.iterator().next();
            Bitmap bitmap = BitmapFactory.decodeStream(displayedImageInfo.getImageInputStream());
            personDetails.setSignatureBase64(ImageUtil.encodeBitmapToBase64(bitmap));
        }
    }

    private void readAdditionalDetails() throws Exception {

        DG11File dg11File = (DG11File) dataGroupFiles.get(11);
        if (dg11File.getLength() > 0) {
            // Set other additional details as necessary
            additionalPersonDetails.setPlaceOfBirth(dg11File.getPlaceOfBirth());
            if (dg11File.getNameOfHolder().contains("<<")&& eDocument.getDocType().equals("ID")) {
                String arabicName = dg11File.getNameOfHolder().split("<<")[1].trim();
                additionalPersonDetails.setArabicName(new String(arabicName.getBytes(StandardCharsets.UTF_8)));
                String surname = dg11File.getOtherNames().iterator().next().split("<<")[1].trim();
                additionalPersonDetails.setArabicSurname(new String(surname.getBytes(StandardCharsets.UTF_8)));
            }
            personDetails.setPersonalNumber(dg11File.getPersonalNumber());


        }
    }
    private void readDocumentDetails() throws Exception {

        DG12File dg12File = (DG12File) dataGroupFiles.get(12);
        if (dg12File.getLength() > 0) {
            // Set document details as necessary
            documentDetails.setIssuingAuthority(dg12File.getIssuingAuthority());
            documentDetails.setDocumentType(eDocument.getDocType());
            documentDetails.setDocumentNumber(personDetails.getSerialNumber());
            documentDetails.setDocumentDateOfIssue(dg12File.getDateOfIssue());
            documentDetails.setDocumentDateOfExpiry(dg12File.getEndorsementsAndObservations());
            Image image = ImageUtil.getImage(dg12File.getImageOfFront());
            documentDetails.setDocumentFrontImageBase64(image.getBase64Image());

        }
    }

    private void readPublicKey() throws Exception {

        DG15File dg15File = (DG15File) dataGroupFiles.get(15);
        eDocument.setDocPublicKey(dg15File.getPublicKey());
    }
}

