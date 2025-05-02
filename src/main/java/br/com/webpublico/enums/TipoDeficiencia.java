package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum TipoDeficiencia {

    NAO_PORTADOR("0", "Não Portador de Deficiência"),
    REABILITADO("6", "Reabilitado/Readaptado"),
    FISICA("1", "Física"),
    AUDITIVA("2", "Auditiva"),
    VISUAL("3", "Visual"),
    MENTAL("4", "Mental"),
    INTELECTUAL("7", "Intelectual "),
    PSICOSSOCIAL("8", "Psicossocial"),
    MULTIPLA("5", "Múltipla"),
    OUTROS("9", "Outros");

    private String codigo;
    private String descricao;

    private TipoDeficiencia(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
