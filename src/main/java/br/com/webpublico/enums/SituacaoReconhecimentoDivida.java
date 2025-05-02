package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.SituacaoReconhecimentoDividaDTO;

public enum SituacaoReconhecimentoDivida implements EnumComDescricao {
    EM_ELABORACAO("Em Elaboração", SituacaoReconhecimentoDividaDTO.EM_ELABORACAO),
    AGUARDANDO_APROVACAO("Aguardando Aprovação", SituacaoReconhecimentoDividaDTO.AGUARDANDO_APROVACAO),
    APROVADO("Aprovado", SituacaoReconhecimentoDividaDTO.APROVADO),
    REJEITADO("Rejeitado", SituacaoReconhecimentoDividaDTO.REJEITADO);

    private String descricao;
    private SituacaoReconhecimentoDividaDTO toDto;

    SituacaoReconhecimentoDivida(String descricao, SituacaoReconhecimentoDividaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public boolean isEmElaboracao() {
        return EM_ELABORACAO.equals(this);
    }

    public boolean isRejeitado() {
        return REJEITADO.equals(this);
    }

    public boolean isAprovado() {
        return APROVADO.equals(this);
    }

    public boolean isAguardandoAprovacao() {
        return AGUARDANDO_APROVACAO.equals(this);
    }

    public SituacaoReconhecimentoDividaDTO getToDto() {
        return toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
