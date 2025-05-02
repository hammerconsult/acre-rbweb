package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.SituacaoMovimentoAdministrativoDTO;

public enum SituacaoMovimentoAdministrativo implements EnumComDescricao {

    AGUARDANDO_EFETIVACAO("Aguardando Efetivação", SituacaoMovimentoAdministrativoDTO.AGUARDANDO_EFETIVACAO),
    RECUSADO("Recusado", SituacaoMovimentoAdministrativoDTO.RECUSADO),
    FINALIZADO("Finalizado", SituacaoMovimentoAdministrativoDTO.FINALIZADO),
    EM_ELABORACAO("Em Elaboração", SituacaoMovimentoAdministrativoDTO.EM_ELABORACAO),
    ESTORNADO("Estornado", SituacaoMovimentoAdministrativoDTO.ESTORNADO);
    private String descricao;
    private SituacaoMovimentoAdministrativoDTO dto;

    SituacaoMovimentoAdministrativo(String descricao, SituacaoMovimentoAdministrativoDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isEmElaboracao() {
        return EM_ELABORACAO.equals(this);
    }

    public boolean isRecusado() {
        return RECUSADO.equals(this);
    }

    public boolean isFinalizado() {
        return FINALIZADO.equals(this);
    }

    public boolean isAguardandoEfetivacao() {
        return AGUARDANDO_EFETIVACAO.equals(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
