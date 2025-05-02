package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.ClassificacaoContaAuxiliarDTO;

public enum ClassificacaoContaAuxiliar implements EnumComDescricao {

    SISTEMA(1, "Sistema", ClassificacaoContaAuxiliarDTO.SISTEMA),
    SICONFI(2, "SICONFI", ClassificacaoContaAuxiliarDTO.SICONFI);
    private int codigo;
    private String descricao;
    private ClassificacaoContaAuxiliarDTO toDto;

    ClassificacaoContaAuxiliar(int codigo, String descricao, ClassificacaoContaAuxiliarDTO toDto) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public int getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public ClassificacaoContaAuxiliarDTO getToDto() {
        return toDto;
    }

    public String getCodigoDescricao() {
        return codigo + " - " + descricao;
    }

}
