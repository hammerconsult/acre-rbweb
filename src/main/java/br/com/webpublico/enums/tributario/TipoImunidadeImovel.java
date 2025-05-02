package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoImunidadeImovel implements EnumComDescricao {
    IMOVEIS_COM_IMUNIDADE("Imóveis Com Imunidade"),
    IMOVEIS_SEM_IMUNIDADE("Imóveis Sem Imunidade");

    String descricao;

    TipoImunidadeImovel(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
