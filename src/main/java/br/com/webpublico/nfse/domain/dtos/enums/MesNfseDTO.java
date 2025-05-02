package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum MesNfseDTO implements NfseEnumDTO {
    JANEIRO("Janeiro", 1),
    FEVEREITO("Fevereiro", 2),
    MARCO("Março", 3),
    ABRIL("Abril", 4),
    MAIO("Maio", 5),
    JUNHO("Junho", 6),
    JULHO("Julho", 7),
    AGOSTO("Agosto", 8),
    SETEMBRO("Setembro", 9),
    OUTUBRO("Outubro", 10),
    NOVEMBRO("Novembro", 11),
    DEZEMBRO("Dezembro", 12);

    private String descricao;
    private Integer mes;

    MesNfseDTO(String descricao, Integer mes) {
        this.descricao = descricao;
        this.mes = mes;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getMes() {
        return mes;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
