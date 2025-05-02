package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
public class ArquivoNFSETREDetalhe extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ArquivoNFSETRE arquivoNFSETRE;

    private String uf;

    private String cnpjDestinatario;

    private String tipoPessoaEmitente;

    private String cpfCnpjEmitente;

    private String naturezaOperacao;

    private String modeloNotaFiscal;

    @Temporal(TemporalType.DATE)
    private Date emissaoNotaFiscal;

    private String serieNotaFiscal;

    private Long numeroNotaFiscal;

    private String situacaoNotaFiscal;

    private Long numeroNotaFiscalSubs;

    private BigDecimal valorTotalNotaFiscal;

    private String chaveAcesso;

    private String urlAcessoNfse;

    public ArquivoNFSETREDetalhe() {
        super();
        this.uf = "";
        this.cnpjDestinatario = "";
        this.tipoPessoaEmitente = "";
        this.cpfCnpjEmitente = "";
        this.naturezaOperacao = "";
        this.modeloNotaFiscal = "";
        this.serieNotaFiscal = "";
        this.numeroNotaFiscal = 0L;
        this.numeroNotaFiscalSubs = 0L;
        this.valorTotalNotaFiscal = BigDecimal.ZERO;
        this.chaveAcesso = "";
        this.urlAcessoNfse = "";
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArquivoNFSETRE getArquivoNFSETRE() {
        return arquivoNFSETRE;
    }

    public void setArquivoNFSETRE(ArquivoNFSETRE arquivoNFSETRE) {
        this.arquivoNFSETRE = arquivoNFSETRE;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCnpjDestinatario() {
        return cnpjDestinatario;
    }

    public void setCnpjDestinatario(String cnpjDestinatario) {
        this.cnpjDestinatario = cnpjDestinatario;
    }

    public String getTipoPessoaEmitente() {
        return tipoPessoaEmitente;
    }

    public void setTipoPessoaEmitente(String tipoPessoaEmitente) {
        this.tipoPessoaEmitente = tipoPessoaEmitente;
    }

    public String getCpfCnpjEmitente() {
        return cpfCnpjEmitente;
    }

    public void setCpfCnpjEmitente(String cpfCnpjEmitente) {
        this.cpfCnpjEmitente = cpfCnpjEmitente;
    }

    public String getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(String naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public String getModeloNotaFiscal() {
        return modeloNotaFiscal;
    }

    public void setModeloNotaFiscal(String modeloNotaFiscal) {
        this.modeloNotaFiscal = modeloNotaFiscal;
    }

    public Date getEmissaoNotaFiscal() {
        return emissaoNotaFiscal;
    }

    public void setEmissaoNotaFiscal(Date emissaoNotaFiscal) {
        this.emissaoNotaFiscal = emissaoNotaFiscal;
    }

    public String getSerieNotaFiscal() {
        return serieNotaFiscal;
    }

    public void setSerieNotaFiscal(String serieNotaFiscal) {
        this.serieNotaFiscal = serieNotaFiscal;
    }

    public Long getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(Long numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public String getSituacaoNotaFiscal() {
        return situacaoNotaFiscal;
    }

    public void setSituacaoNotaFiscal(String situacaoNotaFiscal) {
        this.situacaoNotaFiscal = situacaoNotaFiscal;
    }

    public Long getNumeroNotaFiscalSubs() {
        return numeroNotaFiscalSubs;
    }

    public void setNumeroNotaFiscalSubs(Long numeroNotaFiscalSubs) {
        this.numeroNotaFiscalSubs = numeroNotaFiscalSubs;
    }

    public BigDecimal getValorTotalNotaFiscal() {
        return valorTotalNotaFiscal;
    }

    public void setValorTotalNotaFiscal(BigDecimal valorTotalNotaFiscal) {
        this.valorTotalNotaFiscal = valorTotalNotaFiscal;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public String getUrlAcessoNfse() {
        return urlAcessoNfse;
    }

    public void setUrlAcessoNfse(String urlAcessoNfse) {
        this.urlAcessoNfse = urlAcessoNfse;
    }
}
