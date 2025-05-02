package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoOperacaoPagto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Parametro Integração RH/Contábil")
public class ParametroRHContabil extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private IntegracaoRHContabil integracaoRHContabil;
    /*Empenho*/
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data do Empenho")
    private Date dataEmpenho;
    @Etiqueta("Histórico")
    private String historico;

    /*Liquidação*/
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data da Liquidação")
    private Date dataLiquidacao;
    @Etiqueta("Número")
    private String numeroDocumentoLiquidacao;
    @Etiqueta("Tipo do Documento Fiscal")
    @ManyToOne
    private TipoDocumentoFiscal tipoDocumento;

    /*Pagamento*/
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de Previsão de Pagamento")
    private Date dataPrevisaoPagamento;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Finalidade do Pagamento")
    private FinalidadePagamento finalidadePagamento;
    @Etiqueta("Número do Documento")
    private String numeroDocumentoPagamento;
    @Etiqueta("Data do Documento")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataDocumento;
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Tipo de Documento")
    private TipoDocPagto tipoDocPagto;
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Tipo de Operação")
    private TipoOperacaoPagto tipoOperacaoPagto;

    /*Obrigação a Pagar*/
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data da Obrigação a Pagar")
    private Date dataObrigacao;
    @Etiqueta("Número")
    private String numeroDocumentoObrigacao;
    @Etiqueta("Tipo do Documento Fiscal Obrigação")
    @ManyToOne
    private TipoDocumentoFiscal tipoDocumentoObrigacao;
    @Etiqueta("Histórico da Obrigação a Pagar")
    private String historicoObrigacao;

    public ParametroRHContabil() {
        super();
    }

    public ParametroRHContabil(IntegracaoRHContabil selecionado) {
        this.integracaoRHContabil = selecionado;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IntegracaoRHContabil getIntegracaoRHContabil() {
        return integracaoRHContabil;
    }

    public void setIntegracaoRHContabil(IntegracaoRHContabil integracaoRHContabil) {
        this.integracaoRHContabil = integracaoRHContabil;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Date getDataLiquidacao() {
        return dataLiquidacao;
    }

    public void setDataLiquidacao(Date dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
    }

    public String getNumeroDocumentoLiquidacao() {
        return numeroDocumentoLiquidacao;
    }

    public void setNumeroDocumentoLiquidacao(String numeroDocumentoLiquidacao) {
        this.numeroDocumentoLiquidacao = numeroDocumentoLiquidacao;
    }

    public TipoDocumentoFiscal getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoFiscal tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Date getDataPrevisaoPagamento() {
        return dataPrevisaoPagamento;
    }

    public void setDataPrevisaoPagamento(Date dataPrevisaoPagamento) {
        this.dataPrevisaoPagamento = dataPrevisaoPagamento;
    }

    public FinalidadePagamento getFinalidadePagamento() {
        return finalidadePagamento;
    }

    public void setFinalidadePagamento(FinalidadePagamento finalidadePagamento) {
        this.finalidadePagamento = finalidadePagamento;
    }

    public String getNumeroDocumentoPagamento() {
        return numeroDocumentoPagamento;
    }

    public void setNumeroDocumentoPagamento(String numeroDocumentoPagamento) {
        this.numeroDocumentoPagamento = numeroDocumentoPagamento;
    }

    public Date getDataDocumento() {
        return dataDocumento;
    }

    public void setDataDocumento(Date dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    public TipoDocPagto getTipoDocPagto() {
        return tipoDocPagto;
    }

    public void setTipoDocPagto(TipoDocPagto tipoDocPagto) {
        this.tipoDocPagto = tipoDocPagto;
    }

    public TipoOperacaoPagto getTipoOperacaoPagto() {
        return tipoOperacaoPagto;
    }

    public void setTipoOperacaoPagto(TipoOperacaoPagto tipoOperacaoPagto) {
        this.tipoOperacaoPagto = tipoOperacaoPagto;
    }

    public Date getDataObrigacao() {
        return dataObrigacao;
    }

    public void setDataObrigacao(Date dataObrigacao) {
        this.dataObrigacao = dataObrigacao;
    }

    public String getNumeroDocumentoObrigacao() {
        return numeroDocumentoObrigacao;
    }

    public void setNumeroDocumentoObrigacao(String numeroDocumentoObrigacao) {
        this.numeroDocumentoObrigacao = numeroDocumentoObrigacao;
    }

    public TipoDocumentoFiscal getTipoDocumentoObrigacao() {
        return tipoDocumentoObrigacao;
    }

    public void setTipoDocumentoObrigacao(TipoDocumentoFiscal tipoDocumentoObrigacao) {
        this.tipoDocumentoObrigacao = tipoDocumentoObrigacao;
    }

    public String getHistoricoObrigacao() {
        return historicoObrigacao;
    }

    public void setHistoricoObrigacao(String historicoObrigacao) {
        this.historicoObrigacao = historicoObrigacao;
    }
}
