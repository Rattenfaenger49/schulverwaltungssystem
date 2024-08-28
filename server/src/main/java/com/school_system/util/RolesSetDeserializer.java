package com.school_system.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.school_system.entity.security.Permission;
import com.school_system.entity.security.Role;
import com.school_system.enums.authentication.RoleName;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RolesSetDeserializer extends StdDeserializer<Set<Role>> {

    public RolesSetDeserializer() {
        this(null);
    }

    public RolesSetDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Set<Role> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        Set<Role> roles = new HashSet<>();
        if (node.isArray()) {
            for (JsonNode roleNode : node) {
                Role role = new Role();

                role.setId(roleNode.get("id").asLong());
                role.setName(RoleName.valueOf(roleNode.get("name").asText().toUpperCase()));

                JsonNode permissionsNode = roleNode.get("permissions");
                if (permissionsNode != null && permissionsNode.isArray()) {
                    Set<Permission> permissions = new HashSet<>();
                    for (JsonNode permissionNode : permissionsNode) {
                        Permission permission = new Permission();
                        permission.setId(permissionNode.get("id").asLong());
                        permission.setName(permissionNode.get("name").asText());
                        permission.setRequiresVerification(permissionNode.get("requiresVerification").asBoolean());
                        permissions.add(permission);
                    }
                    role.setPermissions(permissions);


                }
                roles.add(role);

            }
        }

        return roles;
    }
}




