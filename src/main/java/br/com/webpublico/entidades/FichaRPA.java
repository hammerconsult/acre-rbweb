/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Ficha RPA")
public class FichaRPA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Prestador de Serviços")
    @Obrigatorio
    @ManyToOne
    private PrestadorServicos prestadorServicos;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @OneToMany(mappedBy = "fichaRPA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemFichaRPA> ItemFichaRPAs;
    @Tabelavel
    @Etiqueta("Valor")
    @Obrigatorio
    @Monetario
    /*
     * Valor Integral combinado.
     */
    private BigDecimal valor;
    @Tabelavel
    @Etiqueta("Gerado em")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date geradoEm;
    /*
     * Valor liquido a ser pago ao Prestador
     *
     */
    @Monetario
    private BigDecimal valorLiquido;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataServico;
    @Pesquisavel
    @Etiqueta("Tomador")
    @Obrigatorio
    @Tabelavel
    @ManyToOne
    private TomadorDeServico tomador;
    @Pesquisavel
    @Etiqueta("Número")
    @Obrigatorio
    @Tabelavel
    private String numero;
    @Etiqueta("Cobrar ISS")
    private Boolean cobrarISS;
    @Etiqueta("Percentual ISS")
    private BigDecimal percentualISS;
    @ManyToOne
    private EconomicoCNAE economicoCNAE;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TomadorDeServico getTomador() {
        return tomador;
    }

    public void setTomador(TomadorDeServico tomador) {
        this.tomador = tomador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemFichaRPA> getItemFichaRPAs() {
        return ItemFichaRPAs;
    }

    public void setItemFichaRPAs(List<ItemFichaRPA> ItemFichaRPAs) {
        this.ItemFichaRPAs = ItemFichaRPAs;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(Date geradoEm) {
        this.geradoEm = geradoEm;
    }

    public PrestadorServicos getPrestadorServicos() {
        return prestadorServicos;
    }

    public void setPrestadorServicos(PrestadorServicos prestadorServicos) {
        this.prestadorServicos = prestadorServicos;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataServico() {
        return dataServico;
    }

    public void setDataServico(Date dataServico) {
        this.dataServico = dataServico;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public Boolean getCobrarISS() {
        return cobrarISS;
    }

    public void setCobrarISS(Boolean cobrarISS) {
        this.cobrarISS = cobrarISS;
    }

    public BigDecimal getPercentualISS() {
        return percentualISS;
    }

    public void setPercentualISS(BigDecimal percentualISS) {
        this.percentualISS = percentualISS;
    }

    public EconomicoCNAE getEconomicoCNAE() {
        return economicoCNAE;
    }

    public void setEconomicoCNAE(EconomicoCNAE economicoCNAE) {
        this.economicoCNAE = economicoCNAE;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FichaRPA)) {
            return false;
        }
        FichaRPA other = (FichaRPA) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return prestadorServicos.getPrestador().getNome() + " - " + descricao;
    }
}
