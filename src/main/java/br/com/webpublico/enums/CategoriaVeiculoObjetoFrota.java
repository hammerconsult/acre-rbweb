package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.CategoriaDTO;

public enum CategoriaVeiculoObjetoFrota implements EnumComDescricao {

    CARRO("Carro", CategoriaDTO.CARRO),
    MOTO("Moto", CategoriaDTO.MOTO),
    CAMINHAO("Caminhão", CategoriaDTO.CAMINHAO),
    VAN("Van", CategoriaDTO.VAN),
    CAMIONETE("Camionete", CategoriaDTO.CAMIONETE),
    ONIBUS("Ônibus", CategoriaDTO.ONIBUS);

    private String descricao;
    private CategoriaDTO toDto;

    CategoriaVeiculoObjetoFrota(String descricao, CategoriaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public CategoriaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
