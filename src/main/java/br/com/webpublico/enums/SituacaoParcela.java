package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.tributario.consultadebitos.dtos.DTOConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.EnumConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.SituacaoParcelaDTO;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public enum SituacaoParcela implements EnumConsultaDebitos, EnumComDescricao {

    EM_ABERTO("Em Aberto", true, 1, SituacaoParcelaDTO.EM_ABERTO),
    AGUARDANDO("Aguardando", true, 2, SituacaoParcelaDTO.AGUARDANDO),
    AGUARDANDO_REVISAO("Aguardando Revisão", true, 3, SituacaoParcelaDTO.AGUARDANDO_REVISAO),
    ANISTIA("Anistiado", true, 4, SituacaoParcelaDTO.ANISTIA),
    BAIXADO("Baixado", true, 5, SituacaoParcelaDTO.BAIXADO),
    CANCELADA_INSCRICAO_DIVIDA_ATIVA("Cancelada na Inscrição em Dívida Ativa", true, 6, SituacaoParcelaDTO.CANCELADA_INSCRICAO_DIVIDA_ATIVA),
    BAIXADO_OUTRA_OPCAO("Cancelado por Outra Opção de Pagamento", true, 7, SituacaoParcelaDTO.BAIXADO_OUTRA_OPCAO),
    CANCELADO_RECALCULO("Cancelado por Recálculo", true, 8, SituacaoParcelaDTO.CANCELADO_RECALCULO),
    CANCELADO_ISENCAO("Cancelado por Isenção", true, 9, SituacaoParcelaDTO.CANCELADO_ISENCAO),
    CANCELAMENTO("Cancelado", true, 10, SituacaoParcelaDTO.CANCELAMENTO),
    COMPENSACAO("Compensado", true, 11, SituacaoParcelaDTO.COMPENSACAO),
    DACAO("Dação", true, 12, SituacaoParcelaDTO.DACAO),
    DECADENCIA("Decadência", true, 13, SituacaoParcelaDTO.DECADENCIA),
    DESISTENCIA("Desistência", true, 14, SituacaoParcelaDTO.DESISTENCIA),
    DIVIDA_ATIVA_EM_REVISAO("Dívida Ativa em Revisão", true, 15, SituacaoParcelaDTO.DIVIDA_ATIVA_EM_REVISAO),
    ESTORNADO("Estornado", true, 16, SituacaoParcelaDTO.ESTORNADO),
    INSCRITA_EM_DIVIDA_ATIVA("Inscrito em Dívida Ativa", true, 17, SituacaoParcelaDTO.INSCRITA_EM_DIVIDA_ATIVA),
    ISENTO("Isento", true, 18, SituacaoParcelaDTO.ISENTO),
    PAGO("Pago", true, 19, SituacaoParcelaDTO.PAGO),
    PAGO_PARCELAMENTO("Pago por Parcelamento", true, 20, SituacaoParcelaDTO.PAGO_PARCELAMENTO),
    PAGO_REFIS("Pago por Refis", true, 21, SituacaoParcelaDTO.PAGO_REFIS),
    PAGO_SUBVENCAO("Pago por Subvenção", true, 22, SituacaoParcelaDTO.PAGO_SUBVENCAO),
    AGUARDANDO_PAGAMENTO_SUBVENCAO("Aguardando Pagamento por Subvenção", true, 23, SituacaoParcelaDTO.AGUARDANDO_PAGAMENTO_SUBVENCAO),
    PARCELADO("Parcelado", true, 24, SituacaoParcelaDTO.PARCELADO),
    PARCELAMENTO_CANCELADO("Parcelamento Cancelado", true, 25, SituacaoParcelaDTO.PARCELAMENTO_CANCELADO),
    PRESCRICAO("Prescrita", true, 26, SituacaoParcelaDTO.PRESCRICAO),
    REFIS("Refis", true, 27, SituacaoParcelaDTO.REFIS),
    REMISSAO("Remissão", true, 28, SituacaoParcelaDTO.REMISSAO),
    RETIDO_NA_FONTE("Retido na Fonte ", true, 29, SituacaoParcelaDTO.RETIDO_NA_FONTE),
    REVISADO("Revisado", true, 30, SituacaoParcelaDTO.REVISADO),
    SEM_MOVIMENTO("Sem Movimento de ISS", true, 31, SituacaoParcelaDTO.SEM_MOVIMENTO),
    SUBSTITUIDA_POR_COMPENSACAO("Substituída por compensação", true, 32, SituacaoParcelaDTO.SUBSTITUIDA_POR_COMPENSACAO),
    SUBSTITUIDO("Substituído", true, 33, SituacaoParcelaDTO.SUBSTITUIDO),
    SUSPENSAO("Suspenso", true, 34, SituacaoParcelaDTO.SUSPENSAO),
    SUSPENSAO_LEI("Suspenso por Lei", true, 35, SituacaoParcelaDTO.SUSPENSAO_LEI),
    ISOLAMENTO("Parcela Isolada", false, 36, SituacaoParcelaDTO.ISOLAMENTO),
    SUBSTITUIDA_POR_CANCELAMENTO_PARCELAMENTO("Substituída por Cancelamento do Parcelamento", false, 37, SituacaoParcelaDTO.SUBSTITUIDA_POR_CANCELAMENTO_PARCELAMENTO),
    RESTITUICAO("Restituída", true, 38, SituacaoParcelaDTO.RESTITUICAO),
    AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL("Aguardando Pagamento por Bloqueio Judicial", true, 39, SituacaoParcelaDTO.AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL),
    PAGO_BLOQUEIO_JUDICIAL("Pago por Bloqueio Judicial", true, 40, SituacaoParcelaDTO.PAGO_BLOQUEIO_JUDICIAL),
    IMUNE("Imune", true, 41, SituacaoParcelaDTO.IMUNE);

    private String descricao;
    private boolean visivel;
    private Integer codigo;
    private SituacaoParcelaDTO dto;


    private SituacaoParcela(String descricao, boolean visivel, Integer codigo, SituacaoParcelaDTO dto) {
        this.descricao = descricao;
        this.visivel = visivel;
        this.codigo = codigo;
        this.dto = dto;
    }

    public static List<String> getsituacoesPago() {
        List<String> situacoes = new ArrayList<>();
        for (SituacaoParcela situacaoParcela : SituacaoParcela.values()) {
            if (situacaoParcela.isPago()) {
                situacoes.add(situacaoParcela.name());
            }

        }
        return situacoes;
    }

    public static List<SituacaoParcela> getValues() {
        List<SituacaoParcela> lista = Lists.newArrayList();
        for (SituacaoParcela situacaoParcela : SituacaoParcela.values()) {
            if (situacaoParcela.isVisivel()) {
                lista.add(situacaoParcela);
            }
        }
        return lista;
    }

    public static List<SituacaoParcela> getSituacoesPagas() {
        List<SituacaoParcela> lista = Lists.newArrayList();
        lista.add(BAIXADO);
        lista.add(PAGO);
        lista.add(PAGO_PARCELAMENTO);
        lista.add(PAGO_REFIS);
        lista.add(PAGO_SUBVENCAO);
        lista.add(BAIXADO_OUTRA_OPCAO);
        lista.add(PAGO_BLOQUEIO_JUDICIAL);
        return lista;
    }

    public static List<SituacaoParcela> getSituacoesParaExportacaoDebitosGeraisArquivoBI() {
        List<SituacaoParcela> lista = Lists.newArrayList();
        lista.add(EM_ABERTO);
        lista.add(PRESCRICAO);
        lista.add(CANCELAMENTO);
        lista.add(BAIXADO);
        lista.add(SUSPENSAO);
        lista.add(COMPENSACAO);
        lista.add(ISENTO);
        lista.add(PAGO);
        lista.add(PAGO_PARCELAMENTO);
        lista.add(PAGO_SUBVENCAO);
        lista.add(PAGO_BLOQUEIO_JUDICIAL);
        lista.add(RESTITUICAO);
        return lista;
    }

    public static List<SituacaoParcela> getSituacaoCancelamento() {
        return Lists.newArrayList(ANISTIA, CANCELADA_INSCRICAO_DIVIDA_ATIVA, BAIXADO_OUTRA_OPCAO, CANCELADO_RECALCULO,
            CANCELADO_ISENCAO, CANCELAMENTO, DACAO, DECADENCIA, DESISTENCIA, DIVIDA_ATIVA_EM_REVISAO, ESTORNADO, ISENTO,
            AGUARDANDO_PAGAMENTO_SUBVENCAO, PRESCRICAO, REFIS, REMISSAO, RETIDO_NA_FONTE, REVISADO,
            SUBSTITUIDA_POR_COMPENSACAO, SUBSTITUIDO, SUSPENSAO, SUSPENSAO_LEI, ISOLAMENTO);
    }

    public static String montarClausulaInSituacoesPagas() {
        StringBuilder clausulaIn = new StringBuilder();
        String juncao = "";
        for (SituacaoParcela situacaoParcela : SituacaoParcela.values()) {
            if (situacaoParcela.isPago()) {
                clausulaIn.append(juncao).append("'").append(situacaoParcela.name()).append("'");
                juncao = ", ";
            }
        }

        return clausulaIn.toString();
    }

    public static SituacaoParcela fromDto(SituacaoParcelaDTO dto) {
        for (SituacaoParcela value : values()) {
            if (value.toDto().equals(dto)) {
                return value;
            }
        }
        return null;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public boolean isSoPago() {
        return this.equals(PAGO);
    }

    public boolean isPago() {
        return this.equals(BAIXADO) || this.equals(PAGO) || this.equals(PAGO_PARCELAMENTO)
            || this.equals(PAGO_REFIS) || this.equals(PAGO_SUBVENCAO) || this.equals(BAIXADO_OUTRA_OPCAO)
            || this.equals(PAGO_BLOQUEIO_JUDICIAL);
    }

    public boolean isInscritoDa() {
        return this.equals(INSCRITA_EM_DIVIDA_ATIVA);
    }

    public boolean isCancelado() {
        return this.equals(CANCELADA_INSCRICAO_DIVIDA_ATIVA) || this.equals(CANCELADO_RECALCULO)
            || this.equals(CANCELADO_ISENCAO) || this.equals(CANCELAMENTO);
    }

    public boolean isCompensado() {
        return this.equals(COMPENSACAO) || this.equals(SUBSTITUIDA_POR_COMPENSACAO);
    }

    public boolean isAguardandoPagamentoSubvencao() {
        return this.equals(AGUARDANDO_PAGAMENTO_SUBVENCAO);
    }

    public boolean isParcelado() {
        return this.equals(PARCELADO);
    }

    public boolean isParcelamentoCancelado() {
        return this.equals(PARCELAMENTO_CANCELADO);
    }

    public boolean isParcelaBloqueioJudicial() {
        return this.equals(AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL) || this.equals(PAGO_BLOQUEIO_JUDICIAL);
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public DTOConsultaDebitos toDto() {
        return dto;
    }

    public boolean equals(SituacaoParcelaDTO dto) {
        return this.equals(SituacaoParcela.fromDto(dto));
    }

}
