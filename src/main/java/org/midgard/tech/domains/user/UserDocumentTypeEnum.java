package org.midgard.tech.domains.user;

import lombok.Getter;

@Getter
public enum UserDocumentTypeEnum {

    _0_CEDULA_DE_CIUDADANIA("CEDULA_DE_CIUDADANIA"),
    _1_TARJETA_DE_IDENTIDAD("TARJETA_DE_IDENTIDAD"),
    _2_CEDULA_DE_EXTRANJERIA("CEDULA_DE_EXTRANJERIA"),
    _3_PERMISO_ESPECIAL_DE_PERMANENCIA("PERMISO_ESPECIAL_DE_PERMANENCIA"),
    _4_PERMISO_DE_PROTECCION_TEMPORAL("PERMISO_DE_PROTECCION_TEMPORAL");

    private final String value;

    UserDocumentTypeEnum(String value) { this.value = value; }
}
