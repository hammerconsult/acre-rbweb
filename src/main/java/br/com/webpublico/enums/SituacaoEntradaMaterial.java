package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.SituacaoEntradaMaterialDTO;

public enum SituacaoEntradaMaterial implements EnumComDescricao {

    EM_ELABORACAO("Em Elaboração", SituacaoEntradaMaterialDTO.EM_ELABORACAO),
    CONCLUIDA("Concluída", SituacaoEntradaMaterialDTO.CONCLUIDA),
    ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO("Atesto Provisório - Aguardando Liquidação", SituacaoEntradaMaterialDTO.ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO),
    ATESTO_PROVISORIO_COM_PENDENCIA("Atesto Provisório - Com Pendência", SituacaoEntradaMaterialDTO.ATESTO_PROVISORIO_COM_PENDENCIA),
    ATESTO_DEFINITIVO_ESTORNADO("Atesto Definitivo - Estornado", SituacaoEntradaMaterialDTO.ATESTO_DEFINITIVO_ESTORNADO),
    ATESTO_DEFINITIVO_LIQUIDADO("Atesto Definitivo - Liquidado", SituacaoEntradaMaterialDTO.ATESTO_DEFINITIVO_LIQUIDADO);
    private String descricao;
    private SituacaoEntradaMaterialDTO toDto;

    SituacaoEntradaMaterial(String descricao, SituacaoEntradaMaterialDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public SituacaoEntradaMaterialDTO getToDto() {
        return toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isEmElaboracao() {
        return SituacaoEntradaMaterial.EM_ELABORACAO.equals(this);
    }

    public boolean isAtestoDefinitivo() {
        return SituacaoEntradaMaterial.ATESTO_DEFINITIVO_LIQUIDADO.equals(this);
    }

    public boolean isAtestoDefinitivoEstornado() {
        return SituacaoEntradaMaterial.ATESTO_DEFINITIVO_ESTORNADO.equals(this);
    }

    public boolean isAtestoAguardandoLiquidacao() {
        return SituacaoEntradaMaterial.ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO.equals(this);
    }

    public boolean isConcluida() {
        return SituacaoEntradaMaterial.CONCLUIDA.equals(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
