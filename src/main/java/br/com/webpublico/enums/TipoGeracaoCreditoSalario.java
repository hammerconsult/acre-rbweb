package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Criado por Mateus
 * Data: 18/05/2016.
 */
public enum TipoGeracaoCreditoSalario implements EnumComDescricao {

    SERVIDORES("Servidores", "30"),
    PENSIONISTAS("Pensionistas", "98");

    TipoGeracaoCreditoSalario(String descricao, String tipoServico) {
        this.descricao = descricao;
        this.tipoServico = tipoServico;
    }

    private String descricao;
    private String tipoServico;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoServico() {
        return tipoServico;
    }
}
