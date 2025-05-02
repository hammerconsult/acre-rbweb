package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoTrabalhadorEsocial implements EnumComDescricao {
    SERVIDOR("Servidor"),
    TRABALHADOR_SEM_VINCULO("Trabalhador Sem Vínculo/Estátutário"),
    PRESTADOR_SERVICO("Prestador de Serviço");

    private String descricao;

    TipoTrabalhadorEsocial(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
