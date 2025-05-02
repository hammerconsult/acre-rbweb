/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.webreportdto.dto.administrativo.ModalidadeLicitacaoDTO;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
public enum ModalidadeLicitacao {
    // Utilizado no portal da transparencia e consulta do gráfico 'Licitações por modalidade' da tela de resumo-geral do portal da transparencia /portal/transparencia/pagina/listar/,
    // quando alterado/adicionado, necessário dar manutenção.
    CONVITE("Convite", ModalidadeLicitacaoDTO.CONVITE, ModoSolicitacaoCompra.LICITACAO),
    TOMADA_PRECO("Tomada de Preço", ModalidadeLicitacaoDTO.TOMADA_PRECO, ModoSolicitacaoCompra.LICITACAO),
    CONCORRENCIA("Concorrência", ModalidadeLicitacaoDTO.CONCORRENCIA, ModoSolicitacaoCompra.LICITACAO),
    CONCURSO("Concurso", ModalidadeLicitacaoDTO.CONCURSO, ModoSolicitacaoCompra.LICITACAO, 3),
    PREGAO("Pregão", ModalidadeLicitacaoDTO.PREGAO, ModoSolicitacaoCompra.LICITACAO),
    DISPENSA_LICITACAO("Dispensa de Licitação", ModalidadeLicitacaoDTO.DISPENSA_LICITACAO, ModoSolicitacaoCompra.DISPENSA_INEXIGIBILIDADE, 8),
    INEXIGIBILIDADE("Inexigibilidade", ModalidadeLicitacaoDTO.INEXIGIBILIDADE, ModoSolicitacaoCompra.DISPENSA_INEXIGIBILIDADE, 9),
    RDC("Regime Diferenciado de Contratações – RDC", ModalidadeLicitacaoDTO.RDC, ModoSolicitacaoCompra.LICITACAO),
    LEILAO("Leilão", ModalidadeLicitacaoDTO.LEILAO, ModoSolicitacaoCompra.LICITACAO),
    MANIFESTACAO_INTERESSE("Manifestação de Interesse", ModalidadeLicitacaoDTO.MANIFESTACAO_INTERESSE, ModoSolicitacaoCompra.PROCEDIMENTO_AUXILIARES, 10),
    PRE_QUALIFICACAO("Pré-Qualificação", ModalidadeLicitacaoDTO.PRE_QUALIFICACAO, ModoSolicitacaoCompra.PROCEDIMENTO_AUXILIARES, 11),
    CREDENCIAMENTO("Credenciamento", ModalidadeLicitacaoDTO.CREDENCIAMENTO, ModoSolicitacaoCompra.PROCEDIMENTO_AUXILIARES, 12),
    DIALOGO_COMPETITIVO("Diálogo Competitivo", ModalidadeLicitacaoDTO.DIALOGO_COMPETITIVO, ModoSolicitacaoCompra.LICITACAO, 2);

    private String descricao;
    private ModalidadeLicitacaoDTO toDto;
    private ModoSolicitacaoCompra modoSolicitacaoCompra;
    private Integer codigoPncp;

    ModalidadeLicitacao(String descricao, ModalidadeLicitacaoDTO toDto, ModoSolicitacaoCompra modoSolicitacaoCompra) {
        this.descricao = descricao;
        this.toDto = toDto;
        this.modoSolicitacaoCompra = modoSolicitacaoCompra;
    }

    ModalidadeLicitacao(String descricao, ModalidadeLicitacaoDTO toDto, ModoSolicitacaoCompra modoSolicitacaoCompra, Integer codigoPncp) {
        this.descricao = descricao;
        this.toDto = toDto;
        this.modoSolicitacaoCompra = modoSolicitacaoCompra;
        this.codigoPncp = codigoPncp;
    }

    public String getDescricao() {
        return descricao;
    }

    public ModalidadeLicitacaoDTO getToDto() {
        return toDto;
    }

    public ModoSolicitacaoCompra getModoSolicitacaoCompra() {
        return modoSolicitacaoCompra;
    }

    public Integer getCodigoPncp() {
        return codigoPncp;
    }

    public static Integer obterCodigoModalidade(ModalidadeLicitacao modalidadeLicitacao, TipoNaturezaDoProcedimentoLicitacao naturezaDoProcedimento) {
        if (modalidadeLicitacao.isLeilao()) {
            if (naturezaDoProcedimento != null && naturezaDoProcedimento.isEletronico()) {
                return ModalidadePresencialOuEletronico.LEILAO_ELETRONICO.getCodigoPncp();
            } else {
                return ModalidadePresencialOuEletronico.LEILAO_PRESENCIAL.getCodigoPncp();
            }
        }

        if (modalidadeLicitacao.isConcorrencia()) {
            if (naturezaDoProcedimento != null && naturezaDoProcedimento.isEletronico()) {
                return ModalidadePresencialOuEletronico.CONCORRENCIA_ELETRONICO.getCodigoPncp();
            } else {
                return ModalidadePresencialOuEletronico.CONCORRENCIA_PRESENCIAL.getCodigoPncp();
            }
        }

        if (modalidadeLicitacao.isPregao()) {
            if (naturezaDoProcedimento != null && naturezaDoProcedimento.isEletronico()) {
                return ModalidadePresencialOuEletronico.PREGAO_ELETRONICO.getCodigoPncp();
            } else {
                return ModalidadePresencialOuEletronico.PREGAO_PRESENCIAL.getCodigoPncp();
            }
        }


        if (modalidadeLicitacao.getCodigoPncp() != null) {
            return modalidadeLicitacao.getCodigoPncp();
        }

        throw new ExcecaoNegocioGenerica("Não encontrado código PNCP para Modalidade Licitação: ".concat(modalidadeLicitacao.getDescricao()));
    }

    public static String obterDescricaoModalidadePorCodigo(Integer codigoModalidade) {
        for (ModalidadeLicitacao modalidadeLicitacao : ModalidadeLicitacao.values()) {
            if (modalidadeLicitacao.getCodigoPncp() != null && modalidadeLicitacao.getCodigoPncp().equals(codigoModalidade)) {
                return modalidadeLicitacao.getDescricao();
            }
        }

        for (ModalidadePresencialOuEletronico modalidadePresencialOuEletronico : ModalidadePresencialOuEletronico.values()) {
            if (modalidadePresencialOuEletronico.getCodigoPncp().equals(codigoModalidade)) {
                return modalidadePresencialOuEletronico.getDescricao();
            }
        }

        return null;
    }

    public static List<Integer> getModalidadesPresenciais() {
        List<Integer> modalidadesPresenciais = new ArrayList<>();
        modalidadesPresenciais.add(5);
        modalidadesPresenciais.add(7);
        modalidadesPresenciais.add(13);
        return modalidadesPresenciais;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getLabelTipoModalidade() {
        String label = "";
        switch (this) {
            case PREGAO:
                label = " Tipo de Pregão";
                break;
            case CONCORRENCIA:
                label = " Tipo de Concorrência";
                break;
            case RDC:
                label = " Tipo de RDC";
                break;
            case INEXIGIBILIDADE:
                label = " Tipo de Inexibilidade";
                break;
            case CREDENCIAMENTO:
                label = " Tipo de Credenciamento";
                break;
            case LEILAO:
                label = " Tipo de Leilão";
                break;
            case DISPENSA_LICITACAO:
                label = " Natureza do Procedimento";
                break;
            default:
                return label;
        }
        return label;
    }

    public static List<ModalidadeLicitacao> getModalidadesParaTipoDoProcesso(TipoProcessoDeCompra tipoProcessoDeCompra) {
        switch (tipoProcessoDeCompra) {
            case LICITACAO:
                return getModalidadesLicitacao();
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                return getModalidadesDispensaInexibilidade();
            case CREDENCIAMENTO:
                return getModalidadesCredenciamento();
            default:
                return null;
        }
    }

    public static List<ModalidadeLicitacao> getModalidadeSolicitacaoCompra(ModoSolicitacaoCompra modo) {
        List<ModalidadeLicitacao> modalidades = Lists.newArrayList();
        switch (modo) {
            case LICITACAO:
                modalidades.addAll(getModalidadesLicitacao());
                break;
            case DISPENSA_INEXIGIBILIDADE:
                modalidades.addAll(getModalidadesDispensaInexibilidade());
                break;
            default:
                modalidades.addAll(getModalidadesProcessoAuxiliares());
                break;
        }
        return modalidades;
    }

    public static List<ModalidadeLicitacao> getModalidadesLicitacao() {
        return Arrays.asList(modalidadesLicitacao);
    }

    public static final ModalidadeLicitacao[] modalidadesLicitacao = {
        ModalidadeLicitacao.CONVITE,
        ModalidadeLicitacao.TOMADA_PRECO,
        ModalidadeLicitacao.CONCORRENCIA,
        ModalidadeLicitacao.CONCURSO,
        ModalidadeLicitacao.PREGAO,
        ModalidadeLicitacao.RDC
    };

    public static List<ModalidadeLicitacao> getModalidadesProcessoAuxiliares() {
        List<ModalidadeLicitacao> modalidades = new ArrayList<>();
        modalidades.add(ModalidadeLicitacao.MANIFESTACAO_INTERESSE);
        modalidades.add(ModalidadeLicitacao.PRE_QUALIFICACAO);
        modalidades.add(ModalidadeLicitacao.CREDENCIAMENTO);
        return modalidades;
    }


    public static List<ModalidadeLicitacao> getModalidadesDispensaInexibilidade() {
        List<ModalidadeLicitacao> modalidades = new ArrayList<>();
        modalidades.add(ModalidadeLicitacao.DISPENSA_LICITACAO);
        modalidades.add(ModalidadeLicitacao.INEXIGIBILIDADE);
        return modalidades;
    }

    public static List<ModalidadeLicitacao> getModalidadesCredenciamento() {
        List<ModalidadeLicitacao> modalidades = new ArrayList<>();
        modalidades.add(ModalidadeLicitacao.CREDENCIAMENTO);
        return modalidades;
    }

    public static List<ModalidadeLicitacao> getModalidadesRegistroPrecoExterno() {
        List<ModalidadeLicitacao> modalidades = new ArrayList<>();
        modalidades.add(ModalidadeLicitacao.PREGAO);
        modalidades.add(ModalidadeLicitacao.CONCORRENCIA);
        modalidades.add(ModalidadeLicitacao.RDC);
        return modalidades;
    }

    public boolean isPregao() {
        return PREGAO.equals(this);
    }

    public boolean isConvite() {
        return CONVITE.equals(this);
    }

    public boolean isTomadaPrecos() {
        return TOMADA_PRECO.equals(this);
    }

    public boolean isConcorrencia() {
        return CONCORRENCIA.equals(this);
    }

    public boolean isDispensaLicitacao() {
        return DISPENSA_LICITACAO.equals(this);
    }

    public boolean isInexigibilidade() {
        return INEXIGIBILIDADE.equals(this);
    }

    public boolean isCredenciamento() {
        return CREDENCIAMENTO.equals(this);
    }

    public boolean isManifestacaoInteresse() {
        return MANIFESTACAO_INTERESSE.equals(this);
    }

    public boolean isPreQualificacao() {
        return PRE_QUALIFICACAO.equals(this);
    }

    public boolean isDialogCompetitivo() {
        return DIALOGO_COMPETITIVO.equals(this);
    }

    public boolean isRDC() {
        return RDC.equals(this);
    }

    public boolean isConcurso() {
        return CONCURSO.equals(this);
    }

    public boolean isLeilao() {
        return LEILAO.equals(this);
    }

    private enum ModalidadePresencialOuEletronico {
        LEILAO_ELETRONICO("Leilão Eletrônico", 1),
        LEILAO_PRESENCIAL("Leilão Presencial", 13),
        CONCORRENCIA_ELETRONICO("Concorrência Eletrônico", 4),
        CONCORRENCIA_PRESENCIAL("Concorrência Presencial", 5),
        PREGAO_ELETRONICO("Pregão Eletrônico", 6),
        PREGAO_PRESENCIAL("Pregão Presencial", 7);

        private String descricao;
        private Integer codigoPncp;


        ModalidadePresencialOuEletronico(String descricao, Integer codigoPncp) {
            this.descricao = descricao;
            this.codigoPncp = codigoPncp;
        }

        public String getDescricao() {
            return descricao;
        }

        public Integer getCodigoPncp() {
            return codigoPncp;
        }
    }
}
