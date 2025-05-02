package br.com.webpublico.enums.rh;

public enum StatusIntegracaoRBPonto {
    INTEGRADO_COM_SUCESSO("Integrado com Sucesso"),
    INTEGRADO_COM_ERRO("Integrado com Erro");

    private String descricao;

    StatusIntegracaoRBPonto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
