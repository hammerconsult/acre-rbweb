/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.LayoutArquivoBordero;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@Entity
@Audited
@Etiqueta("Arquivo - OBN600")
public class ArquivoRemessaBanco implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private String numero;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Banco")
    private Banco banco;
    @OneToMany(mappedBy = "arquivoRemessaBanco", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoRemBancoBordero> arquivoRemBancoBorderos;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data de Geração")
    @Tabelavel
    @Pesquisavel
    private Date dataGeracao;
    @Etiqueta("Layout do Arquivo")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private LayoutArquivoBordero layoutArquivoBordero;
    @Tabelavel
    @Etiqueta("Qtde Ordens Bancárias")
    private Long qntdDocumento;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor Total dos Documentos (R$)")
    private BigDecimal valorTotalDoc;
    @Etiqueta("Valor Total dos Borderos(R$)")
    private BigDecimal valorTotalBor;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Transmitido")
    private Boolean transmitido;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Contrato")
    @ManyToOne
    private ContratoObn contratoObn;
    @Transient
    private Boolean gerarArquivoTeste;
    @Transient
    private Boolean gerarLinhaTipoUm;

    public ArquivoRemessaBanco() {
        arquivoRemBancoBorderos = new ArrayList<>();
        valorTotalBor = BigDecimal.ZERO;
        valorTotalDoc = BigDecimal.ZERO;
        transmitido = Boolean.FALSE;
        gerarArquivoTeste = Boolean.FALSE;
        qntdDocumento = 0l;
        gerarLinhaTipoUm = Boolean.FALSE;
    }

    public ArquivoRemessaBanco(String numero, Banco banco, List<ArquivoRemBancoBordero> arquivoRemBancoBorderos, Date dataGeracao, Long qntdDocumento, BigDecimal valorTotalDoc, BigDecimal valorTotalBor, LayoutArquivoBordero layoutArquivoBordero) {
        this.numero = numero;
        this.banco = banco;
        this.arquivoRemBancoBorderos = arquivoRemBancoBorderos;
        this.dataGeracao = dataGeracao;
        this.qntdDocumento = qntdDocumento;
        this.valorTotalDoc = valorTotalDoc;
        this.valorTotalBor = valorTotalBor;
        this.layoutArquivoBordero = layoutArquivoBordero;
    }

    public List<ArquivoRemBancoBordero> getArquivoRemBancoBorderos() {
        return arquivoRemBancoBorderos;
    }

    public void setArquivoRemBancoBorderos(List<ArquivoRemBancoBordero> arquivoRemBancoBorderos) {
        this.arquivoRemBancoBorderos = arquivoRemBancoBorderos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Long getQntdDocumento() {
        return qntdDocumento;
    }

    public void setQntdDocumento(Long qntdDocumento) {
        this.qntdDocumento = qntdDocumento;
    }

    public BigDecimal getValorTotalDoc() {
        return valorTotalDoc;
    }

    public void setValorTotalDoc(BigDecimal valorTotalDoc) {
        this.valorTotalDoc = valorTotalDoc;
    }

    public BigDecimal getValorTotalBor() {
        return valorTotalBor;
    }

    public void setValorTotalBor(BigDecimal valorTotalBor) {
        this.valorTotalBor = valorTotalBor;
    }

    public LayoutArquivoBordero getLayoutArquivoBordero() {
        return layoutArquivoBordero;
    }

    public void setLayoutArquivoBordero(LayoutArquivoBordero layoutArquivoBordero) {
        this.layoutArquivoBordero = layoutArquivoBordero;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getTransmitido() {
        return transmitido;
    }

    public void setTransmitido(Boolean transmitido) {
        this.transmitido = transmitido;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArquivoRemessaBanco)) {
            return false;
        }
        ArquivoRemessaBanco other = (ArquivoRemessaBanco) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ArquivoRemessa[ id=" + id + " ]";
    }

    public BigDecimal getValor() {
        BigDecimal soma = BigDecimal.ZERO;
        for (ArquivoRemBancoBordero arquivoRemBancoBordero : arquivoRemBancoBorderos) {
            soma = soma.add(arquivoRemBancoBordero.getBordero().getValor());
        }
        return soma;
    }

    public Boolean getGerarArquivoTeste() {
        return gerarArquivoTeste;
    }

    public void setGerarArquivoTeste(Boolean gerarArquivoTeste) {
        this.gerarArquivoTeste = gerarArquivoTeste;
    }

    public ContratoObn getContratoObn() {
        return contratoObn;
    }

    public void setContratoObn(ContratoObn contratoObn) {
        this.contratoObn = contratoObn;
    }

    public Boolean getGerarLinhaTipoUm() {
        return gerarLinhaTipoUm == null ? Boolean.FALSE : gerarLinhaTipoUm;
    }

    public void setGerarLinhaTipoUm(Boolean gerarLinhaTipoUm) {
        this.gerarLinhaTipoUm = gerarLinhaTipoUm;
    }
}
