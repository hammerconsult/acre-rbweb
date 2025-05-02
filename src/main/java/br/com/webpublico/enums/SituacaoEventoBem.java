package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoEventoBemDTO;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 08/05/14
 * Time: 10:04
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoEventoBem {

    CONCLUIDO("Concluído", SituacaoEventoBemDTO.CONCLUIDO),
    AGUARDANDO_EFETIVACAO("Aguardando Efetivação", SituacaoEventoBemDTO.AGUARDANDO_EFETIVACAO),
    AGUARDANDO_APROVACAO("Aguardando Aprovação", SituacaoEventoBemDTO.AGUARDANDO_APROVACAO),
    AGUARDANDO_PARECER("Aguardando Parecer", SituacaoEventoBemDTO.AGUARDANDO_PARECER),
    RECUSADO("Recusado", SituacaoEventoBemDTO.RECUSADO),
    FINALIZADO("Finalizado", SituacaoEventoBemDTO.FINALIZADO),
    BAIXADO("Baixado", SituacaoEventoBemDTO.BAIXADO),
    EM_ELABORACAO("Em Elaboração", SituacaoEventoBemDTO.EM_ELABORACAO),
    EM_MANUTENCAO("Em Manutenção", SituacaoEventoBemDTO.EM_MANUTENCAO),
    BLOQUEADO("Bloqueado", SituacaoEventoBemDTO.BLOQUEADO),
    ESTORNADO("Estornado", SituacaoEventoBemDTO.ESTORNADO),
    ESTORNO_AQUISICAO("Estorno da Aquisição", SituacaoEventoBemDTO.ESTORNO_AQUISICAO),
    AGUARDANDO_LIQUIDACAO("Aguardando Liquidação", SituacaoEventoBemDTO.AGUARDANDO_LIQUIDACAO),
    AGUARDANDO_DEVOLUCAO("Aguardando Devolução", SituacaoEventoBemDTO.AGUARDANDO_DEVOLUCAO),
    ALIENADO("Alienado", SituacaoEventoBemDTO.ALIENADO);

    private String descricao;
    private SituacaoEventoBemDTO toDto;

    SituacaoEventoBem(String descricao, SituacaoEventoBemDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoEventoBemDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return this.descricao;
    }

    public static final String[] situacoesQueNaoPermitemReducaoDoValorBem = {
        SituacaoEventoBem.BAIXADO.name(),
        SituacaoEventoBem.BLOQUEADO.name(),
        SituacaoEventoBem.ESTORNO_AQUISICAO.name(),
        SituacaoEventoBem.ESTORNADO.name()
    };

    public static final String[] situacoesEventoBloqueio = {
        SituacaoEventoBem.EM_ELABORACAO.name(),
        SituacaoEventoBem.AGUARDANDO_EFETIVACAO.name(),
        SituacaoEventoBem.AGUARDANDO_LIQUIDACAO.name(),
        SituacaoEventoBem.AGUARDANDO_DEVOLUCAO.name(),
        SituacaoEventoBem.ALIENADO.name(),
        SituacaoEventoBem.RECUSADO.name(),
        SituacaoEventoBem.EM_MANUTENCAO.name()
    };

    public static final SituacaoEventoBem[] situacoesQuePermitemOperacoesEMovimentacoes = {
        SituacaoEventoBem.RECUSADO,
        SituacaoEventoBem.FINALIZADO
    };

    public static SituacaoEventoBem situacaoEventoBemParaProcessosParalelos(SituacaoEventoBem situacaoAtual) {
        for (SituacaoEventoBem situacaoParaMovimentacao : situacoesQuePermitemOperacoesEMovimentacoes) {
            if (situacaoParaMovimentacao.equals(situacaoAtual)) {
                return SituacaoEventoBem.FINALIZADO;
            }
        }
        return SituacaoEventoBem.CONCLUIDO;
    }
}
