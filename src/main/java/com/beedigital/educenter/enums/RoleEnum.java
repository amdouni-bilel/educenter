package com.beedigital.educenter.enums;

public enum RoleEnum {

    SUPER_ADMIN("SUPER_ADMIN", "Administrateur Système", 1),
    REGISTRAR("REGISTRAR", "Agent de Scolarité", 2),
    TEACHER("TEACHER", "Enseignant", 3),
    STUDENT("STUDENT", "Étudiant", 4),
    PARENT("PARENT", "Parent", 5);
    private final String code;
    private final String label;
    private final int niveau;


    RoleEnum(String code, String label, int niveau) {
        this.code = code;
        this.label = label;
        this.niveau = niveau;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public int getNiveau() {
        return niveau;
    }

    /**
     * Récupère le rôle à partir de son code
     * @param code Code du rôle
     * @return RoleEnum correspondant ou null
     */
    public static RoleEnum getbycode(String code) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }
}
