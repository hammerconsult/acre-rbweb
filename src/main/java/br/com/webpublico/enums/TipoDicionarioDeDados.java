package br.com.webpublico.enums;

public enum TipoDicionarioDeDados {
    CALAMIDADE_PUBLICA_CONTRATOS("Calamidade Pública - Contratos"),
    CALAMIDADE_PUBLICA_RECURSOS_RECEBIDOS("Calamidade Pública - Recursos Recebidos"),
    CALAMIDADE_PUBLICA_ATOS_LEGAIS("Calamidade Pública - Atos Legais"),
    CALAMIDADE_PUBLICA_BENS_SERVICOS_RECEBIDOS("Calamidade Pública - Bens e Serviços Recebidos"),
    CALAMIDADE_PUBLICA_BENS_DOADO("Calamidade Pública - Bens Doados");

    private String descricao;

    TipoDicionarioDeDados(String descricao) {
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
