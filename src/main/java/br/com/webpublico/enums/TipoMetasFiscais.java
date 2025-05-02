package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoMetasFiscaisDTO;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 30/09/13
 * Time: 10:33
 * To change this template use File | Settings | File Templates.
 */
public enum TipoMetasFiscais {

    PREVISTO("Previsto", TipoMetasFiscaisDTO.PREVISTO),
    REALIZADO("Realizado", TipoMetasFiscaisDTO.REALIZADO),
    CORRENTE("Corrente", TipoMetasFiscaisDTO.CORRENTE);

    private String descricao;
    private TipoMetasFiscaisDTO toDto;

    TipoMetasFiscais(String descricao, TipoMetasFiscaisDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoMetasFiscaisDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

