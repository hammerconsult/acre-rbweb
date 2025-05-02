package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoColetaLixo implements EnumComDescricao {

    ALTERNADA("Alternada", "coleta_lixo_alternado"),
    DIARIA("Diária", "coleta_lixo_diario");

    private String descricao;
    private String identificacao;

    TipoColetaLixo(String descricao, String identificacao) {
        this.descricao = descricao;
        this.identificacao = identificacao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getIdentificacao() {
        return identificacao;
    }
}
