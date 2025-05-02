package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;
public enum OrigemProcessoLicenciamentoAmbiental implements EnumComDescricao {

    CAC("CAC"),
    REDE_SIM("Rede SIM"),
    RBDOC("RBDOC"),
    EMAIL_INSTITUCIONAL("E-mail Institucional"),
    VIA_BIANCA("Via Bianca"),
    RECEPCAO("Recepção"),
    ONLINE("On-line");
    private String descricao;

    OrigemProcessoLicenciamentoAmbiental(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
