package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Porcentagem;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Ajuste Contrato - Dados Cadastrais")
@Table(name = "AJUSTECONTRATODADOSCAD")
public class AjusteContratoDadosCadastrais extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Ajuste Contrato")
    private AjusteContrato ajusteContrato;

    @ManyToOne
    @Etiqueta("Alteração Contratual")
    private AlteracaoContratual alteracaoContratual;

    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Ajuste")
    private TipoAjusteDadosCadastrais tipoAjuste;

    @Etiqueta("Número")
    private String numeroTermo;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Aprovação")
    private Date dataAprovacao;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Deferimento")
    private Date dataDeferimento;

    @Etiqueta("Inicío de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Etiqueta("Término de Vigência")
    @Temporal(TemporalType.DATE)
    private Date terminoVigencia;

    @Etiqueta("Término de Vigência Atual")
    @Temporal(TemporalType.DATE)
    private Date terminoVigenciaAtual;

    @Etiqueta("Inicío de Execução")
    @Temporal(TemporalType.DATE)
    private Date inicioExecucao;

    @Etiqueta("Término de Execução")
    @Temporal(TemporalType.DATE)
    private Date terminoExecucao;

    @Etiqueta("Término de Execução Atual")
    @Temporal(TemporalType.DATE)
    private Date terminoExecucaoAtual;

    @Etiqueta("Data de Assinatura")
    @Temporal(TemporalType.DATE)
    private Date dataAssinatura;

    @Length(maximo = 3000)
    @Etiqueta("Objeto")
    private String objeto;

    @Length(maximo = 3000)
    @Etiqueta("Observação ou Justificativa")
    private String observacao;

    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoContrato situacaoContrato;

    @Etiqueta("Data de Lancamento")
    private Date dataLancamento;

    @ManyToOne
    @Etiqueta("Item Contrato")
    private ItemContrato itemContrato;

    @ManyToOne
    @Etiqueta("Objeto Compra")
    private ObjetoCompra objetoCompra;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Controle")
    private TipoControleLicitacao tipoControle;

    @Length(maximo = 3000)
    @Etiqueta("Descrição Complementar")
    private String descricaoComplementar;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @ManyToOne
    @Etiqueta("Responsável Unidade Administrativa")
    private ContratoFP responsavelPrefeitura;

    @ManyToOne
    @Etiqueta("Responsável Fornecedor")
    private PessoaFisica responsavelEmpresa;

    @ManyToOne
    @Etiqueta("Movimento Alteração Contratual")
    private MovimentoAlteracaoContratual movimentoAlteracaoCont;

    @Etiqueta("Operação")
    @Enumerated(EnumType.STRING)
    private OperacaoMovimentoAlteracaoContratual operacao;


    public AjusteContratoDadosCadastrais() {
        tipoAjuste = TipoAjusteDadosCadastrais.ALTERACAO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoAjusteDadosCadastrais getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(TipoAjusteDadosCadastrais tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public String getNumeroTermo() {
        return numeroTermo;
    }

    public void setNumeroTermo(String numero) {
        this.numeroTermo = numero;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public Date getDataDeferimento() {
        return dataDeferimento;
    }

    public void setDataDeferimento(Date dataDeferimento) {
        this.dataDeferimento = dataDeferimento;
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

    public Date getTerminoVigenciaAtual() {
        return terminoVigenciaAtual;
    }

    public void setTerminoVigenciaAtual(Date terminoVigenciaAtual) {
        this.terminoVigenciaAtual = terminoVigenciaAtual;
    }

    public Date getTerminoExecucaoAtual() {
        return terminoExecucaoAtual;
    }

    public void setTerminoExecucaoAtual(Date terminoExecucaoAtual) {
        this.terminoExecucaoAtual = terminoExecucaoAtual;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public SituacaoContrato getSituacaoContrato() {
        return situacaoContrato;
    }

    public void setSituacaoContrato(SituacaoContrato situacaoContrato) {
        this.situacaoContrato = situacaoContrato;
    }

    public AjusteContrato getAjusteContrato() {
        return ajusteContrato;
    }

    public void setAjusteContrato(AjusteContrato ajusteContrato) {
        this.ajusteContrato = ajusteContrato;
    }

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public ContratoFP getResponsavelPrefeitura() {
        return responsavelPrefeitura;
    }

    public void setResponsavelPrefeitura(ContratoFP responsavelPrefeitura) {
        this.responsavelPrefeitura = responsavelPrefeitura;
    }

    public PessoaFisica getResponsavelEmpresa() {
        return responsavelEmpresa;
    }

    public void setResponsavelEmpresa(PessoaFisica responsavelEmpresa) {
        this.responsavelEmpresa = responsavelEmpresa;
    }

    public MovimentoAlteracaoContratual getMovimentoAlteracaoCont() {
        return movimentoAlteracaoCont;
    }

    public void setMovimentoAlteracaoCont(MovimentoAlteracaoContratual movimentoAlteracaoCont) {
        this.movimentoAlteracaoCont = movimentoAlteracaoCont;
    }

    public OperacaoMovimentoAlteracaoContratual getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoMovimentoAlteracaoContratual operacao) {
        this.operacao = operacao;
    }


    public boolean hasCamposAlteracaoNulos() {
        if (ajusteContrato.isAjusteContrato()) {
            return Util.isStringNulaOuVazia(getNumeroTermo())
                    && Util.isNull(getExercicio())
                    && Util.isNull(getDataLancamento())
                    && Util.isNull(getDataAprovacao())
                    && Util.isNull(getDataDeferimento())
                    && Util.isNull(getDataAssinatura())
                    && Util.isNull(getInicioVigencia())
                    && Util.isNull(getTerminoVigencia())
                    && Util.isNull(getTerminoVigenciaAtual())
                    && Util.isNull(getInicioExecucao())
                    && Util.isNull(getTerminoExecucao())
                    && Util.isNull(getTerminoExecucaoAtual())
                    && Util.isStringNulaOuVazia(getObjeto())
                    && Util.isNull(getResponsavelPrefeitura())
                    && Util.isNull(getResponsavelEmpresa())
                    && Util.isNull(getSituacaoContrato());
        } else if (ajusteContrato.isAjusteApostilamento()) {
            return Util.isStringNulaOuVazia(getNumeroTermo())
                    && Util.isNull(getExercicio())
                    && Util.isNull(getDataLancamento())
                    && Util.isNull(getDataAprovacao())
                    && Util.isNull(getDataDeferimento())
                    && Util.isNull(getDataAssinatura());
        }
        return Util.isStringNulaOuVazia(getNumeroTermo())
                && Util.isNull(getExercicio())
                && Util.isNull(getDataLancamento())
                && Util.isNull(getDataAprovacao())
                && Util.isNull(getDataDeferimento())
                && Util.isNull(getDataAssinatura())
                && Util.isStringNulaOuVazia(getObservacao());
    }

    public String getNumeroAnoTermo() {
        try {
            if (!Util.isStringNulaOuVazia(numeroTermo)) {
                return numeroTermo + "/" + exercicio.getAno();
            }
            return "";
        } catch (NullPointerException e) {
            return numeroTermo + "/" + ajusteContrato.getContrato().getExercicioContrato().getAno();
        }
    }
}
