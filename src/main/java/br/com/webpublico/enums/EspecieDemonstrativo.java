package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.EspecieDemonstrativoDTO;

public enum EspecieDemonstrativo {
    PREVISAO_INICIAL("Previsão Inicial", EspecieDemonstrativoDTO.PREVISAO_INICIAL),
    PREVISAO_ADICIONAL("Previsão Adicional", EspecieDemonstrativoDTO.PREVISAO_ADICIONAL),
    PREVISAO_ATUALIZADA("Previsão Atualizada", EspecieDemonstrativoDTO.PREVISAO_ATUALIZADA),
    REALIZADA("Realizada", EspecieDemonstrativoDTO.REALIZADA),
    A_REALIZAR("A Realizar", EspecieDemonstrativoDTO.A_REALIZAR);

    private String descricao;
    private EspecieDemonstrativoDTO toDto;

    EspecieDemonstrativo(String descricao, EspecieDemonstrativoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public EspecieDemonstrativoDTO getToDto() {
        return toDto;
    }

    public boolean isPrevisaoInicial() {
        return PREVISAO_INICIAL.equals(this);
    }

    public boolean isPrevisaoAdicional() {
        return PREVISAO_ADICIONAL.equals(this);
    }

    public boolean isPrevisaoAtualizada() {
        return PREVISAO_ATUALIZADA.equals(this);
    }

    public boolean isRealizada() {
        return REALIZADA.equals(this);
    }

    public boolean isARealizar() {
        return A_REALIZAR.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
