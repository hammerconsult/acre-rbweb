/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.base.Objects;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Terminal-2
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
public class ItemCalculoIPTU extends ItemCalculo implements Serializable, Comparable<ItemCalculoIPTU> { //renomear p/ ItemCalculoIPTU?

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private CalculoIPTU calculoIPTU;

    private BigDecimal valorReal;
    private BigDecimal valorEfetivo;
    private Boolean isento;
    private Boolean imune;
    @ManyToOne
    private EventoConfiguradoIPTU evento;
    @ManyToOne
    private Construcao construcao;

    public ItemCalculoIPTU() {
        this.isento = false;
        this.imune = false;
        this.valorReal = BigDecimal.ZERO;
        this.valorEfetivo = BigDecimal.ZERO;
        dataRegistro = new Date();
    }

    public Boolean getIsento() {
        return isento;
    }

    public void setIsento(Boolean isento) {
        this.isento = isento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoIPTU getCalculoIPTU() {
        return calculoIPTU;
    }

    public void setCalculoIPTU(CalculoIPTU calculoIPTU) {
        this.calculoIPTU = calculoIPTU;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public BigDecimal getValorReal() {
        return valorReal;
    }

    public void setValorReal(BigDecimal valor) {
        this.valorReal = valor;
    }

    public BigDecimal getValorEfetivo() {
        return valorEfetivo;
    }

    public void setValorEfetivo(BigDecimal valorEfetivo) {
        this.valorEfetivo = valorEfetivo;
    }

    public EventoConfiguradoIPTU getEvento() {
        return evento;
    }

    public void setEvento(EventoConfiguradoIPTU evento) {
        this.evento = evento;
    }

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ValorTributo[ id=" + id + " ]";
    }

    @Override
    public int compareTo(ItemCalculoIPTU o) {
        try {
            int var = this.evento.getEventoCalculo().getTipoLancamento().compareTo(o.getEvento().getEventoCalculo().getTipoLancamento());
            if (var == 0) {
                return this.evento.getEventoCalculo().getTipoLancamento().compareTo(o.getEvento().getEventoCalculo().getTipoLancamento());
            } else {
                return var;
            }

        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public Tributo getTributo() {
        return this.evento.getEventoCalculo().getTributo();
    }

    @Override
    public BigDecimal getValor() {
        return this.valorReal;
    }

    public Boolean getImune() {
        return imune;
    }

    public void setImune(Boolean imune) {
        this.imune = imune;
    }
}
