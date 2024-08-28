package com.school_system.classes;


import java.math.BigDecimal;

public record BillItem(String description, double amount, BigDecimal cost) {

}

