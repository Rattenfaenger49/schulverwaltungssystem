package com.school_system.util;

import com.school_system.entity.school.Invoice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvoiceUtils {
    public static String generateInvoiceNumber(Invoice invoice) {

        try {

            String[] parts = invoice.getInvoiceNumber().split("-");
            // if the next year set the counter to 1
            if  (Integer.parseInt(parts[2]) != LocalDate.now().getYear() % 100) {
                return  String.format("%s-%05d-%02d", parts[0], 1, LocalDate.now().getYear() % 100);
            }
            return  String.format("%s-%05d-%02d", parts[0], Integer.parseInt(parts[1]) + 1, LocalDate.now().getYear() % 100);
        } catch (Exception e) {
            throw new RuntimeException("Rechnungsnummer konnte nicht erstellt werden. Bitte kontaktieren Sie uns!");
        }

    }
    public static String generateNextInvoiceNumber(Long userId, String yearMonth, Integer maxNumber) {

        // Bestimme die nächste Rechnungsnummer
        int nextNumber = (maxNumber == null) ? 1 : maxNumber + 1;

        // Formatiere die laufende Nummer mit führenden Nullen example 012
        String nextNumberStr = String.format("%03d", nextNumber);

        // Erstelle die neue Rechnungsnummer exampe 2409-23-012
        return yearMonth + "-" + userId + "-" + nextNumberStr;
    }
}
