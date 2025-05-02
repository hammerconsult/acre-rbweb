package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoAjusteContrato implements EnumComDescricao {

    CONTRATO("Dados Gerais de Contrato"),
    ADITIVO("Dados Gerais de Aditivo"),
    APOSTILAMENTO("Dados Gerais de Apostilamento"),
    SUBSTITUICAO_OBJETO_COMPRA("Substituição do Objeto de Compra"),
    CONTROLE_EXECUCAO("Controle de Execução"),
    OPERACAO_ADITIVO("Operações do Aditivo");

    private String descricao;

    TipoAjusteContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isContrato() {
        return TipoAjusteContrato.CONTRATO.equals(this);
    }

    public boolean isAditivo() {
        return TipoAjusteContrato.ADITIVO.equals(this);
    }

    public boolean isApostilamento() {
        return TipoAjusteContrato.APOSTILAMENTO.equals(this);
    }

    public boolean isSubstituicaoObjetoCompra() {
        return TipoAjusteContrato.SUBSTITUICAO_OBJETO_COMPRA.equals(this);
    }

    public boolean isControleExecucao() {
        return TipoAjusteContrato.CONTROLE_EXECUCAO.equals(this);
    }

    public boolean isOperacaoAditivo() { return TipoAjusteContrato.OPERACAO_ADITIVO.equals(this); }

    public String getTitulo() {
        String titulo = descricao;
        if (isContrato()) {
            titulo = "Contrato";
        } else if (isApostilamento()) {
            titulo = "Apostilamento";
        } else if (isAditivo()) {
            titulo = "Aditivo";
        }
        return titulo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
