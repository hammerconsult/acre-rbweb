/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemFaixaReferenciaFP extends SuperEntidade implements Serializable, Comparable<ItemFaixaReferenciaFP> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Faixa Referência FP")
    @Obrigatorio
    @ManyToOne
    private FaixaReferenciaFP faixaReferenciaFP;
    @Tabelavel
    @Etiqueta("Referência Ate")
    private BigDecimal referenciaAte;
    @Tabelavel
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRegistro;
    private BigDecimal percentual;

    public ItemFaixaReferenciaFP() {
        this.dataRegistro = new Date();
        percentual = new BigDecimal(BigInteger.ZERO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public FaixaReferenciaFP getFaixaReferenciaFP() {
        return faixaReferenciaFP;
    }

    public void setFaixaReferenciaFP(FaixaReferenciaFP faixaReferenciaFP) {
        this.faixaReferenciaFP = faixaReferenciaFP;
    }

    public BigDecimal getReferenciaAte() {
        return referenciaAte;
    }

    public void setReferenciaAte(BigDecimal referenciaAte) {
        this.referenciaAte = referenciaAte;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        return "Referência Ate : " + referenciaAte + " Valor : " + valor;
    }

    @Override
    public int compareTo(ItemFaixaReferenciaFP outro) {
        return dataRegistro.compareTo(outro.getDataRegistro());
    }
}
