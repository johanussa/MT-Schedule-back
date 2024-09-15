package org.midgard.tech.domains.user;

import lombok.Getter;

@Getter
public enum UserRoleEnum {

    _0_ADMINISTRADOR("ADMINISTRADOR"),
    _1_INSTRUCTOR("INSTRUCTOR"),
    _2_FUNCIONARIO("FUNCIONARIO");

    private final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }
}
