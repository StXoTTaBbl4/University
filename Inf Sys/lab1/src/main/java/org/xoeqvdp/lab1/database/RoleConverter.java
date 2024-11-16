package org.xoeqvdp.lab1.database;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.xoeqvdp.lab1.model.Roles;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Roles, String> {

    @Override
    public String convertToDatabaseColumn(Roles role) {
        return role != null ? role.name() : null;
    }

    @Override
    public Roles convertToEntityAttribute(String dbData) {
        return dbData != null ? Roles.valueOf(dbData) : null;
    }
}
