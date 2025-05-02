package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.enums.TipoDePagamentoBaixa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Arrecadacao")
@Audited
@Entity
@Etiqueta(value = "Lote de Baixas")
public class LoteBaixa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel(ordemApresentacao = 1)
    @Pesquisavel
    @Etiqueta("Lote")
    private String codigoLote;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Banco")
    private Banco banco;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel(ordemApresentacao = 5)
    @Pesquisavel
    @Etiqueta("Data de Pagamento")
    private Date dataPagamento;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("C/C")
    private SubConta subConta;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel(ordemApresentacao = 6)
    @Pesquisavel
    @Etiqueta("Data de Movimento")
    private Date dataFinanciamento;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ordemApresentacao = 7)
    @Pesquisavel
    @Etiqueta("Tipo de Pagamento")
    private TipoDePagamentoBaixa tipoDePagamentoBaixa;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ordemApresentacao = 8)
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoLoteBaixa situacaoLoteBaixa;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loteBaixa", orphanRemoval = true)
    private List<ItemLoteBaixa> itemLoteBaixa;
    @Transient
    private Long criadoEm;
    @OneToMany(mappedBy = "loteBaixa")
    private List<LoteBaixaDoArquivo> arquivosLoteBaixa;
    @Tabelavel(TIPOCAMPO = Tabelavel.TIPOCAMPO.NUMERO, ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA, ordemApresentacao = 7)
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;
    private Integer quantidadeParcelas;
    @OneToMany(mappedBy = "loteBaixa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InconsistenciaArrecadacao> inconsistencias;
    @Transient
    @Etiqueta(value = "Banco e Conta Bancária")
    @Tabelavel(ordemApresentacao = 3)
    private String bancoConta;
    @OneToMany(mappedBy = "loteBaixa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteBaixaIntegracao> integracoes;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ordemApresentacao = 4)
    @Pesquisavel
    @Etiqueta("Forma de Pagamento")
    private FormaPagamento formaPagamento;
    @Tabelavel(ordemApresentacao = 2)
    @Etiqueta("Arquivo")
    @Transient
    private String codigoArquivo;
    private Boolean integraContasAcrecimos;
    private Boolean integracaoArrecadacao;
    private Boolean integracaoEstorno;

    public LoteBaixa() {
        inconsistencias = Lists.newLinkedList();
        integracoes = Lists.newLinkedList();
        dataFinanciamento = new Date();
        dataPagamento = new Date();
        itemLoteBaixa = Lists.newLinkedList();
        this.criadoEm = System.nanoTime();
        valorTotal = BigDecimal.ZERO;
        quantidadeParcelas = 0;
        integraContasAcrecimos = true;
        integracaoArrecadacao = false;
        integracaoEstorno = false;
    }

    public LoteBaixa(Long id, String codigoLote, Date dataPagamento, Date dataFinanciamento, TipoDePagamentoBaixa tipoDePagamentoBaixa, SituacaoLoteBaixa situacaoLoteBaixa, BigDecimal valorTotal, SubConta subConta, FormaPagamento formaPagamento, String codigoArquivo) {
        this.id = id;
        this.codigoLote = codigoLote;
        this.dataPagamento = dataPagamento;
        this.subConta = subConta;
        this.dataFinanciamento = dataFinanciamento;
        this.tipoDePagamentoBaixa = tipoDePagamentoBaixa;
        this.situacaoLoteBaixa = situacaoLoteBaixa;
        this.valorTotal = valorTotal;
        bancoConta = montabancoConta();
        this.formaPagamento = formaPagamento;
        this.codigoArquivo = codigoArquivo;
    }

    public String montabancoConta() {
        return montabancoConta(true);
    }

    public String montabancoConta(boolean html) {
        StringBuilder sb = new StringBuilder("");
        if (subConta != null) {
            if (subConta.getContaBancariaEntidade() != null) {
                if (subConta.getContaBancariaEntidade().getAgencia() != null) {
                    if (subConta.getContaBancariaEntidade().getAgencia().getBanco() != null) {
                        sb.append(subConta.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco()).append(" - ");
                        sb.append(subConta.getContaBancariaEntidade().getAgencia().getBanco().getDescricao()).append(" - ");
                    }
                    if (html) sb.append("<b>");
                    sb.append("Agência: ");
                    if (html) sb.append("</b>");
                    sb.append(subConta.getContaBancariaEntidade().getAgencia().getNumeroAgencia()).append("-");
                    sb.append(subConta.getContaBancariaEntidade().getAgencia().getDigitoVerificador()).append(" - ");
                    sb.append(subConta.getContaBancariaEntidade().getAgencia().getNomeAgencia()).append(" - ");
                }
                if (html) sb.append("<b>");
                sb.append("Conta Bancária: ");
                if (html) sb.append("</b>");
                sb.append(subConta.getContaBancariaEntidade().getNumeroConta()).append(" - ");
                sb.append(subConta.getContaBancariaEntidade().getDigitoVerificador()).append(" - ");
                sb.append(subConta.getContaBancariaEntidade().getNomeConta()).append(" - ");
            }
            if (html) sb.append("<b>");
            sb.append("Sub-Conta: ");
            if (html) sb.append("</b>");
            sb.append(subConta.getCodigo());
        }
        return sb.toString().replace("null", " ");
    }

    public String montaBanco() {
        StringBuilder sb = new StringBuilder("");
        if (subConta != null) {
            if (subConta.getContaBancariaEntidade() != null) {
                if (subConta.getContaBancariaEntidade().getAgencia() != null) {
                    if (subConta.getContaBancariaEntidade().getAgencia().getBanco() != null) {
                        sb.append(subConta.getContaBancariaEntidade().getAgencia().getBanco().getDescricao());
                    }
                }
            }
        }
        return sb.toString().replace("null", " ");
    }

    public String getBancoConta() {
        if (bancoConta == null || bancoConta.equals("")) {
            bancoConta = montabancoConta();
        }
        return bancoConta;
    }

    public void setBancoConta(String bancoConta) {
        this.bancoConta = bancoConta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public Date getDataFinanciamento() {
        return dataFinanciamento;
    }

    public void setDataFinanciamento(Date dataFinanciamento) {
        this.dataFinanciamento = dataFinanciamento;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public List<ItemLoteBaixa> getItemLoteBaixa() {
        return itemLoteBaixa;
    }

    public void setItemLoteBaixa(List<ItemLoteBaixa> itemLoteBaixa) {
        this.itemLoteBaixa = itemLoteBaixa;
    }

    public SituacaoLoteBaixa getSituacaoLoteBaixa() {
        return situacaoLoteBaixa;
    }

    public void setSituacaoLoteBaixa(SituacaoLoteBaixa situacaoLoteBaixa) {
        this.situacaoLoteBaixa = situacaoLoteBaixa;
    }

    public TipoDePagamentoBaixa getTipoDePagamentoBaixa() {
        return tipoDePagamentoBaixa;
    }

    public void setTipoDePagamentoBaixa(TipoDePagamentoBaixa tipoDePagamentoBaixa) {
        this.tipoDePagamentoBaixa = tipoDePagamentoBaixa;
    }

    public List<LoteBaixaDoArquivo> getArquivosLoteBaixa() {
        if(arquivosLoteBaixa == null) {
            arquivosLoteBaixa = Lists.newArrayList();
        }
        return arquivosLoteBaixa;
    }

    public void setArquivosLoteBaixa(List<LoteBaixaDoArquivo> arquivosLoteBaixa) {
        this.arquivosLoteBaixa = arquivosLoteBaixa;
    }

    public BigDecimal getValorTotal() {
        return valorTotal != null ? valorTotal : BigDecimal.ZERO;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas != null ? quantidadeParcelas : 0;
    }

    public void setQuantidadeParcelas(Integer quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public List<InconsistenciaArrecadacao> getInconsistencias() {
        return inconsistencias;
    }

    public void setInconsistencias(List<InconsistenciaArrecadacao> inconsistencias) {
        this.inconsistencias = inconsistencias;
    }

    public void addInconsistencia(InconsistenciaArrecadacao inconsistencia) {
        this.inconsistencias.add(inconsistencia);
    }

    public boolean isConsistente() {
        return inconsistencias.isEmpty();
    }

    public List<LoteBaixaIntegracao> getIntegracoes() {
        return integracoes;
    }

    public void setIntegracoes(List<LoteBaixaIntegracao> integracoes) {
        this.integracoes = integracoes;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public Boolean getIntegraContasAcrecimos() {
        return integraContasAcrecimos != null ? integraContasAcrecimos : false;
    }

    public void setIntegraContasAcrecimos(Boolean integraContasAcrecimos) {
        this.integraContasAcrecimos = integraContasAcrecimos;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {

        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return codigoLote;
    }


    public String toStringAutoComplete() {
        return codigoLote + " - " + DataUtil.getDataFormatada(dataPagamento) + " - " + situacaoLoteBaixa.descricao;
    }

    public String getCodigoArquivo() {
        return codigoArquivo;
    }

    public void setCodigoArquivo(String codigoArquivo) {
        this.codigoArquivo = codigoArquivo;
    }

    public static enum FormaPagamento implements EnumComDescricao {
        NORMAL("Normal"), CARTAO_CREDITO("Cartão de Crédito"), CARTAO_DEBITO("Cartão de Débito");
        private String descricao;

        private FormaPagamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public boolean isAberto() {
        return SituacaoLoteBaixa.EM_ABERTO.equals(this.getSituacaoLoteBaixa());
    }

    public boolean isEstornado() {
        return SituacaoLoteBaixa.ESTORNADO.equals(this.getSituacaoLoteBaixa());
    }

    public Boolean getIntegracaoArrecadacao() {
        return integracaoArrecadacao;
    }

    public void setIntegracaoArrecadacao(Boolean integracaoArrecadacao) {
        this.integracaoArrecadacao = integracaoArrecadacao;
    }

    public Boolean getIntegracaoEstorno() {
        return integracaoEstorno;
    }

    public void setIntegracaoEstorno(Boolean integracaoEstorno) {
        this.integracaoEstorno = integracaoEstorno;
    }
}
