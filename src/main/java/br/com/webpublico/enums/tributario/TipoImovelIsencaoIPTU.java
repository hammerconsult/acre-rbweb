package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoImovelIsencaoIPTU implements EnumComDescricao {

    IMOVEIS_COM_ISENCAO_IPTU("Imóveis Com Isenção de IPTU"),
    IMOVEIS_SEM_ISENCAO_IPTU("Imóveis Sem Isenção de IPTU");

    String descricao;

    TipoImovelIsencaoIPTU(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
