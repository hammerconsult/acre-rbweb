package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SubTipoObjetoCompraDTO;

/**
 * Created by mga on 19/02/2018.
 */
public enum SubTipoObjetoCompra {

    AMPLIACAO("Ampliação", SubTipoObjetoCompraDTO.AMPLIACAO),
    CONSTRUCAO("Construção", SubTipoObjetoCompraDTO.CONSTRUCAO),
    DEMOLICAO("Demolição", SubTipoObjetoCompraDTO.DEMOLICAO),
    MANUTENCAO("Manutenção", SubTipoObjetoCompraDTO.MANUTENCAO),
    PLANTIO("Plantio", SubTipoObjetoCompraDTO.PLANTIO),
    REFORMA("Reforma", SubTipoObjetoCompraDTO.REFORMA),
    REFORMA_AMPLIACAO("Reforma e Ampliação", SubTipoObjetoCompraDTO.REFORMA_AMPLIACAO),
    SERVICO_DE_INSTALACAO("Serviço de Instalação", SubTipoObjetoCompraDTO.SERVICO_DE_INSTALACAO),
    NAO_APLICAVEL("Não Aplicável", SubTipoObjetoCompraDTO.NAO_APLICAVEL);

    private final String descricao;
    private SubTipoObjetoCompraDTO toDto;

    SubTipoObjetoCompra(String descricao, SubTipoObjetoCompraDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SubTipoObjetoCompraDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
