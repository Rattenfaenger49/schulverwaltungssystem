package com.school_system.config;


import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FontManager {

    private  final FontConfig fontConfig;

    public  PdfFont getRegularFont() {
        return createFont(fontConfig.getPoppinsRegular());
    }

    public  PdfFont getMediumFont() {
        return createFont(fontConfig.getPoppinsMedium());
    }

    public  PdfFont getThinFont() {
        return createFont(fontConfig.getPoppinsThin());
    }

    public  PdfFont getBoldFont() {
        return createFont(fontConfig.getPoppinsBold());
    }

    private  PdfFont createFont(String fontPath) {
        try {
            return PdfFontFactory.createFont(fontPath, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create font from path: " + fontPath, e);
        }
    }
}
