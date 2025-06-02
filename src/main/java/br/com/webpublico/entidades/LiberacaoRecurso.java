/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
public class LiberacaoRecurso extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Número")
    private Long numeroLiberacao;
    @Etiqueta("Data")
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataLiberacao;
    @Etiqueta("Valor Liberado (R$)")
    @Tabelavel
    private BigDecimal valorLiberado;
    @Etiqueta("Valor a Liberar (R$)")
    @Tabelavel
    private BigDecimal valorLiberar;

    @Etiqueta("Valor de Juros a Liberar (R$)")
    @Tabelavel
    private BigDecimal valorJurosLiberar;

    @Etiqueta("Valor de Outros Encargos a Liberar (R$)")
    @Tabelavel
    private BigDecimal valorOutrosEncargosLiberar;
    @Etiqueta("Dívida Pública")
    @ManyToOne
    private DividaPublica dividaPublica;

    public LiberacaoRecurso() {
        valorLiberado = BigDecimal.ZERO;
        valorLiberar = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroLiberacao() {
        return numeroLiberacao;
    }

    public void setNumeroLiberacao(Long numeroLiberacao) {
        this.numeroLiberacao = numeroLiberacao;
    }

    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }

    public BigDecimal getValorLiberado() {
        return valorLiberado;
    }

    public void setValorLiberado(BigDecimal valorLiberado) {
        this.valorLiberado = valorLiberado;
    }

    public BigDecimal getValorLiberar() {
        return valorLiberar;
    }

    public void setValorLiberar(BigDecimal valorLiberar) {
        this.valorLiberar = valorLiberar;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ParcelaDividaPublica[ id=" + id + " ]";
    }

    public BigDecimal getValorJurosLiberar() {
        return valorJurosLiberar;
    }

    public void setValorJurosLiberar(BigDecimal valorJurosLiberar) {
        this.valorJurosLiberar = valorJurosLiberar;
    }

    public BigDecimal getValorOutrosEncargosLiberar() {
        return valorOutrosEncargosLiberar;
    }

    public void setValorOutrosEncargosLiberar(BigDecimal valorOutrosEncargosLiberar) {
        this.valorOutrosEncargosLiberar = valorOutrosEncargosLiberar;
    }
}
