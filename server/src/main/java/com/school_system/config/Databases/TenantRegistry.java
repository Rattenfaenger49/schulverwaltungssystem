package com.school_system.config.Databases;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TenantRegistry {
    private static final  Set<String> tenantNames = Collections.synchronizedSet(new HashSet<>());

    // Method to add a tenant name to the registry
    public static void addTenant(String tenantName) {
        tenantNames.add(tenantName);
    }

    // Method to check if a tenant exists in the registry
    public static boolean containsTenant(String tenantName) {
        return tenantNames.contains(tenantName);
    }


    // Method to retrieve all tenant IDs
    public static Set<String> getAllTenantIds() {
        return new HashSet<>(tenantNames);
    }
    // Optional: Method to remove a tenant from the registry
    public static void removeTenant(String tenantName) {
        tenantNames.remove(tenantName);
    }
}
