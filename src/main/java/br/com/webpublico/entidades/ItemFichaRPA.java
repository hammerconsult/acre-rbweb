/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author peixe
 */
@Entity

@Audited
@Etiqueta("Item Ficha RPA")
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemFichaRPA extends SuperEntidade implements Comparable<ItemFichaRPA> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Ficha RPA")
    @Obrigatorio
    @ManyToOne
    private FichaRPA fichaRPA;
    @Tabelavel
    @Etiqueta("Evento FP")
    @Obrigatorio
    @ManyToOne
    private EventoFP eventoFP;
    @Monetario
    @Tabelavel
    @Etiqueta("Valor")
    @Obrigatorio
    private BigDecimal valor;
    @Monetario
    private BigDecimal valorReferencia;
    @Monetario
    private BigDecimal valorBaseDeCalculo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorBaseDeCalculo() {
        return valorBaseDeCalculo;
    }

    public void setValorBaseDeCalculo(BigDecimal valorBaseDeCalculo) {
        this.valorBaseDeCalculo = valorBaseDeCalculo;
    }

    public BigDecimal getValorReferencia() {
        return valorReferencia;
    }

    public void setValorReferencia(BigDecimal valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public FichaRPA getFichaRPA() {
        return fichaRPA;
    }

    public void setFichaRPA(FichaRPA fichaRPA) {
        this.fichaRPA = fichaRPA;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valorLiquido) {
        this.valor = valorLiquido;
    }

    @Override
    public String toString() {
        return eventoFP + " - Valor: " + valor;
    }

    @Override
    public int compareTo(ItemFichaRPA o) {
        try {
            return ComparisonChain.start()
                .compare(this.eventoFP.getOrdenacaoTipoEventoFP(), o.eventoFP.getOrdenacaoTipoEventoFP())
                .compare(this.eventoFP.getCodigo(), o.eventoFP.getCodigo())
                .result();
        } catch (NullPointerException e) {
            return ComparisonChain.start()
                .compare(this.eventoFP.getCodigo(), o.eventoFP.getCodigo())
                .result();
        }
    }
}
