package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ObjetoLicitatorioContrato;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by mga on 26/02/2018.
 */
@Entity
@Audited
@Etiqueta("Contratos Vigentes")
public class ContratosVigente extends SuperEntidade implements ObjetoLicitatorioContrato {

    private static final Logger logger = LoggerFactory.getLogger(ContratosVigente.class);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Cotação")
    private Cotacao cotacao;

    @Transient
    @Etiqueta("Tipo de Contrato")
    @Obrigatorio
    private TipoContrato tipoContrato;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Modalidade")
    private ModalidadeLicitacao modalidade;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número da Licitação")
    private Integer numeroLicitacao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Exercício da Licitação")
    private Exercicio exercicioLicitacao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Subtipo de Objeto de Compra")
    private SubTipoObjetoCompra subTipoObjetoCompra;

    @Etiqueta("Tipo de Avaliação")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoAvaliacaoLicitacao tipoAvaliacao;

    @Etiqueta("Tipo de Apuração")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoApuracaoLicitacao tipoApuracao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Natureza do Procedimento")
    private TipoNaturezaDoProcedimentoLicitacao tipoNaturezaProcedimento;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Fornecedor Vencedor")
    private Pessoa fornecedor;

    @ManyToOne
    @Etiqueta("Convênio de Receita")
    private ConvenioReceita convenioReceita;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Regime de Execução")
    private RegimeDeExecucao regimeDeExecucao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situcação")
    private SituacaoContratoVigente situacao;

    public ContratosVigente() {
        situacao = SituacaoContratoVigente.EM_ELABORACAO;
    }

    public RegimeDeExecucao getRegimeDeExecucao() {
        return regimeDeExecucao;
    }

    public void setRegimeDeExecucao(RegimeDeExecucao regimeDeExecucao) {
        this.regimeDeExecucao = regimeDeExecucao;
    }

    public ContratosVigente(Contrato contrato) {
        this.contrato = contrato;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Cotacao getCotacao() {
        return cotacao;
    }

    public void setCotacao(Cotacao cotacao) {
        this.cotacao = cotacao;
    }

    public ModalidadeLicitacao getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeLicitacao modalidade) {
        this.modalidade = modalidade;
    }

    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        return subTipoObjetoCompra;
    }

    public void setSubTipoObjetoCompra(SubTipoObjetoCompra subTipoObjetoCompra) {
        this.subTipoObjetoCompra = subTipoObjetoCompra;
    }

    public TipoAvaliacaoLicitacao getTipoAvaliacao() {
        return tipoAvaliacao;
    }

    public void setTipoAvaliacao(TipoAvaliacaoLicitacao tipoAvaliacao) {
        this.tipoAvaliacao = tipoAvaliacao;
    }

    public TipoApuracaoLicitacao getTipoApuracao() {
        return tipoApuracao;
    }

    public void setTipoApuracao(TipoApuracaoLicitacao tipoApuracao) {
        this.tipoApuracao = tipoApuracao;
    }

    public TipoNaturezaDoProcedimentoLicitacao getTipoNaturezaProcedimento() {
        return tipoNaturezaProcedimento;
    }

    public void setTipoNaturezaProcedimento(TipoNaturezaDoProcedimentoLicitacao tipoNaturezaProcedimento) {
        this.tipoNaturezaProcedimento = tipoNaturezaProcedimento;
    }

    public Integer getNumeroLicitacao() {
        return numeroLicitacao;
    }

    public void setNumeroLicitacao(Integer numeroLicitacao) {
        this.numeroLicitacao = numeroLicitacao;
    }

    public Exercicio getExercicioLicitacao() {
        return exercicioLicitacao;
    }

    public void setExercicioLicitacao(Exercicio exercicioLicitacao) {
        this.exercicioLicitacao = exercicioLicitacao;
    }

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public SituacaoContratoVigente getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoContratoVigente situacao) {
        this.situacao = situacao;
    }

    public Boolean isNaturezaRegistroDePrecos() {
        try {
            return TipoNaturezaDoProcedimentoLicitacao.getTiposDeNaturezaDeRegistroDePreco().contains(getTipoNaturezaProcedimento());
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }

    @Override
    public boolean isRegistroDePrecos() {
        return isNaturezaRegistroDePrecos();
    }

    @Override
    public void transferirObjetoLicitatorioParaContrato() {
        try {
            return;
        } catch (NullPointerException npe) {
            logger.debug(npe.getMessage());
        }
    }

    @Override
    public String getLocalDeEntrega() {
        return "";
    }

    @Override
    public ProcessoDeCompra getProcessoDeCompra() {
        return null;
    }

    @Override
    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return null;
    }

    @Override
    public boolean isObras() {
        return getTipoSolicitacao().isObraServicoEngenharia();
    }

    public void definirSubTipoObjetoCompra() {
        if (getTipoSolicitacao().isCompraServico()) {
            setSubTipoObjetoCompra(SubTipoObjetoCompra.NAO_APLICAVEL);
        } else {
            setSubTipoObjetoCompra(null);
        }
    }

    public TipoSolicitacao getTipoSolicitacao() {
        if (getContrato().isObrasEngenharia()) {
            return TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA;
        } else {
            return TipoSolicitacao.COMPRA_SERVICO;
        }
    }

    public enum SituacaoContratoVigente {
        EM_ELABORACAO("Em Elaboração"),
        CONCLUIDO("Concluído");
        private String descricao;

        SituacaoContratoVigente(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public boolean isConcluido() {
        return this.situacao != null && SituacaoContratoVigente.CONCLUIDO.equals(this.situacao);
    }

    public boolean isEmElaboracao() {
        return this.situacao != null && SituacaoContratoVigente.EM_ELABORACAO.equals(this.situacao);
    }
}
