package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.SaldoItemContratoOrigemVO;
import br.com.webpublico.enums.FinalidadeMovimentoAlteracaoContratual;
import br.com.webpublico.enums.OperacaoMovimentoAlteracaoContratual;
import br.com.webpublico.enums.OrigemSaldoItemContrato;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Movimento Alteração Contratual")
@Table(name = "MOVIMENTOALTERACAOCONT")
public class MovimentoAlteracaoContratual extends SuperEntidade implements Comparable<MovimentoAlteracaoContratual> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Alteração Contratual")
    private AlteracaoContratual alteracaoContratual;

    @Etiqueta("Finalidade")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private FinalidadeMovimentoAlteracaoContratual finalidade;

    @Etiqueta("Início de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Etiqueta("Término da Vigência")
    @Temporal(TemporalType.DATE)
    private Date terminoVigencia;

    @Etiqueta("Inicío de Execução")
    @Temporal(TemporalType.DATE)
    private Date inicioExecucao;

    @Etiqueta("Término de Execução")
    @Temporal(TemporalType.DATE)
    private Date terminoExecucao;

    @Tabelavel
    @Etiqueta("Objeto")
    private String objeto;

    @Etiqueta("Índice")
    private String indice;

    @Etiqueta("Percentual")
    @Porcentagem
    private BigDecimal percentual;

    @Etiqueta("Valor")
    @Monetario
    private BigDecimal valor;

    @ManyToOne
    private FonteDespesaORC fonteDespesaORC;

    private Boolean gerarReserva;

    @OneToMany(mappedBy = "movimentoAlteracaoCont", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentoAlteracaoContratualItem> itens;

    @Etiqueta("Origem Supressão")
    @Enumerated(EnumType.STRING)
    private OrigemSaldoItemContrato origemSupressao;

    @Etiqueta("Movimento Origem")
    private Long idMovimentoOrigem;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Operação")
    private OperacaoMovimentoAlteracaoContratual operacao;

    @ManyToOne
    @Etiqueta("Fornecedor")
    private Pessoa fornecedor;

    @ManyToOne
    @Etiqueta("Responsável Fornecedor")
    private PessoaFisica responsavelFornecedor;

    @ManyToOne
    @Etiqueta("Unidade Admininistrativa")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Valor Variação do Contrato")
    private Boolean valorVariacaoContrato;

    @Transient
    @Etiqueta("Movimento Origem")
    private SaldoItemContratoOrigemVO movimentoOrigemExecucaoContratoVO;

    public MovimentoAlteracaoContratual() {
        itens = Lists.newArrayList();
        gerarReserva = false;
        valorVariacaoContrato = false;
        valor = BigDecimal.ZERO;
        percentual = BigDecimal.ZERO;
    }

    public List<MovimentoAlteracaoContratualItem> getItens() {
        return itens;
    }

    public void setItens(List<MovimentoAlteracaoContratualItem> itensMovimento) {
        this.itens = itensMovimento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }

    public FinalidadeMovimentoAlteracaoContratual getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(FinalidadeMovimentoAlteracaoContratual finalidade) {
        this.finalidade = finalidade;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getTerminoVigencia() {
        return terminoVigencia;
    }

    public void setTerminoVigencia(Date terminoVigencia) {
        this.terminoVigencia = terminoVigencia;
    }

    public Date getInicioExecucao() {
        return inicioExecucao;
    }

    public void setInicioExecucao(Date inicioExecucao) {
        this.inicioExecucao = inicioExecucao;
    }

    public Date getTerminoExecucao() {
        return terminoExecucao;
    }

    public void setTerminoExecucao(Date terminoExecucao) {
        this.terminoExecucao = terminoExecucao;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getIndice() {
        return indice;
    }

    public void setIndice(String indice) {
        this.indice = indice;
    }


    public boolean isTransferenciaFornecedor() {
        return operacao != null && operacao.isTransferenciaFornecedor();
    }

    public boolean isOperacoesTransferencias() {
        return operacao != null && OperacaoMovimentoAlteracaoContratual.getOperacoesTransferencia().contains(operacao);
    }

    public boolean isOperacoesValor() {
        if (alteracaoContratual.isApostilamento() && operacao != null && operacao.isReajustePreExecucao()) {
            return true;
        }
        return alteracaoContratual.isAditivo() && operacao != null && OperacaoMovimentoAlteracaoContratual.getOperacoesValor().contains(operacao);
    }

    public boolean isOperacoesPrazo() {
        return operacao != null && OperacaoMovimentoAlteracaoContratual.getOperacoesPrazo().contains(operacao);
    }

    public boolean isTransferenciaDotacao() {
        return operacao != null && operacao.isTransferenciaDotacao();
    }

    public boolean isTransferenciaUnidade() {
        return operacao != null && operacao.isTransferenciaUnidade();
    }

    public boolean isSupressao() {
        return finalidade != null && finalidade.isSupressao();
    }

    public boolean isMovimentoOrigemSupressao() {
        return isSupressao()
            && operacao != null
            && (operacao.isRedimensionamentoObjeto() || operacao.isOutro());
    }

    public boolean isApostilamentoOperacaoOutro() {
        return alteracaoContratual.isApostilamento()
            && operacao != null
            && operacao.isOutro();
    }

    public Boolean getGerarReserva() {
        return gerarReserva;
    }

    public void setGerarReserva(Boolean gerarReserva) {
        this.gerarReserva = gerarReserva;
    }

    public OrigemSaldoItemContrato getOrigemSupressao() {
        return origemSupressao;
    }

    public void setOrigemSupressao(OrigemSaldoItemContrato origemSupressao) {
        this.origemSupressao = origemSupressao;
    }

    public Long getIdMovimentoOrigem() {
        return idMovimentoOrigem;
    }

    public void setIdMovimentoOrigem(Long idMovimentoOrigem) {
        this.idMovimentoOrigem = idMovimentoOrigem;
    }

    public SaldoItemContratoOrigemVO getMovimentoOrigemExecucaoContratoVO() {
        return movimentoOrigemExecucaoContratoVO;
    }

    public void setMovimentoOrigemExecucaoContratoVO(SaldoItemContratoOrigemVO movimentoOrigemExecucaoContratoVO) {
        this.movimentoOrigemExecucaoContratoVO = movimentoOrigemExecucaoContratoVO;
    }

    public Boolean getValorVariacaoContrato() {
        return valorVariacaoContrato != null ? valorVariacaoContrato : Boolean.FALSE;
    }

    public void setValorVariacaoContrato(Boolean valorVariacaoContrato) {
        this.valorVariacaoContrato = valorVariacaoContrato;
    }

    public OperacaoMovimentoAlteracaoContratual getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoMovimentoAlteracaoContratual operacao) {
        this.operacao = operacao;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public PessoaFisica getResponsavelFornecedor() {
        return responsavelFornecedor;
    }

    public void setResponsavelFornecedor(PessoaFisica responsavelFornecedor) {
        this.responsavelFornecedor = responsavelFornecedor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public int compareTo(MovimentoAlteracaoContratual o) {
        if (getOperacao() != null && o.getOperacao() != null && getFinalidade() != null && o.getFinalidade() != null) {
            return ComparisonChain.start().compare(o.getFinalidade(), getFinalidade())
                .result();
        }
        return 0;
    }

    public BigDecimal getValorTotalItensOriginal() {
        BigDecimal total = BigDecimal.ZERO;
        for (MovimentoAlteracaoContratualItem movItem : getItens()) {
            total = total.add(movItem.getValorTotalProcesso());
        }
        return total;
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        for (MovimentoAlteracaoContratualItem movItem : getItens()) {
            total = total.add(movItem.getValorTotal());
        }
        return total;
    }

    public BigDecimal getPercentualTotalItens(BigDecimal valorTotalProcesso) {
        BigDecimal percentual = BigDecimal.ZERO;
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal valorParaPercentual = valor.divide(valorTotalProcesso, 8, RoundingMode.HALF_UP);
            if (valorParaPercentual.compareTo(BigDecimal.ZERO) > 0) {
                percentual = valorParaPercentual.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return percentual;
    }

    public String labelColunaQuantidadeAjuste() {
        String label = "Qtde ";
        if (getFinalidade() != null) {
            label += getFinalidade().isAcrescimo() ? "Acréscimo" : "Supressão";
        }
        return label;
    }

    public String labelColunaValorUnitarioAjuste() {
        String label = "Valor Unit. ";
        if (getFinalidade() != null) {
            label += getFinalidade().isAcrescimo() ? "Acréscimo" : "Supressão";
        }
        return label + " (R$)";
    }
}
