package com.todo.chrono.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleWorkspaceMember {
    OWNER,
    MEMBER;

    @JsonCreator
    public static RoleWorkspaceMember from(String value) {
        try {
            return RoleWorkspaceMember.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + value);
        }
    }
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
