/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@Audited
@Entity
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Documento Fiscal da Liquidação")
public class DoctoFiscalLiquidacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo do Documento Fiscal")
    private TipoDocumentoFiscal tipoDocumentoFiscal;

    @Pesquisavel
    @Etiqueta("Chave de Acesso")
    private String chaveDeAcesso;

    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data de Emissão")
    private Date dataDocto;

    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data do Atesto")
    @Obrigatorio
    private Date dataAtesto;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Número")
    private String numero;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Série")
    private String serie;

    @Pesquisavel
    @ManyToOne
    @Tabelavel
    @Etiqueta("UF")
    private UF uf;

    @Obrigatorio
    @Monetario
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Monetario
    @Etiqueta("Saldo")
    private BigDecimal saldo;

    @Obrigatorio
    @Monetario
    @Etiqueta("Total")
    private BigDecimal total;

    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;

    @Invisivel
    @OneToMany(mappedBy = "doctoFiscalLiquidacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDoctoFiscalLiquidacao> listaItemDoctoFiscalLiquidacao;

    public DoctoFiscalLiquidacao() {
        valor = new BigDecimal(BigInteger.ZERO);
        total = new BigDecimal(BigInteger.ZERO);
        listaItemDoctoFiscalLiquidacao = new ArrayList<>();
    }

    public BigDecimal getSaldo() {
        if (saldo == null) {
            return BigDecimal.ZERO;
        }
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataDocto() {
        return dataDocto;
    }

    public void setDataDocto(Date dataDocto) {
        this.dataDocto = dataDocto;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoDocumentoFiscal getTipoDocumentoFiscal() {
        return tipoDocumentoFiscal;
    }

    public void setTipoDocumentoFiscal(TipoDocumentoFiscal tipoDocumentoFiscal) {
        this.tipoDocumentoFiscal = tipoDocumentoFiscal;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public List<ItemDoctoFiscalLiquidacao> getListaItemDoctoFiscalLiquidacao() {
        return listaItemDoctoFiscalLiquidacao;
    }

    public void setListaItemDoctoFiscalLiquidacao(List<ItemDoctoFiscalLiquidacao> listaItemDoctoFiscalLiquidacao) {
        this.listaItemDoctoFiscalLiquidacao = listaItemDoctoFiscalLiquidacao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public String getChaveDeAcesso() {
        return chaveDeAcesso;
    }

    public void setChaveDeAcesso(String chaveDeAcesso) {
        this.chaveDeAcesso = chaveDeAcesso;
    }

    public Date getDataAtesto() {
        return dataAtesto;
    }

    public void setDataAtesto(Date dataAtesto) {
        this.dataAtesto = dataAtesto;
    }

    @Override
    public String toString() {
        return "Nº " + numero + " - Série: " + (serie != null ? serie : "") + " - UF: " + (uf != null ? uf : "") + " Tipo: " + tipoDocumentoFiscal + " (Valor: " + Util.formataValor(total) + ")";
    }
}
