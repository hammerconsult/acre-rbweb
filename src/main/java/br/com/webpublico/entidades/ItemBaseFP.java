/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoValor;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemBaseFP extends SuperEntidade implements Serializable, Comparable<ItemBaseFP> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Base Folha de Pagamento")
    @ManyToOne
    private BaseFP baseFP;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Evento Folha de Pagamento")
    @ManyToOne
    @OrderBy("codigo")
    private EventoFP eventoFP;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Operação Fórmula")
    @Enumerated(EnumType.STRING)
    private OperacaoFormula operacaoFormula;
    @Enumerated(EnumType.STRING)
    private TipoValor tipoValor;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private Boolean somaValorRetroativo;

    public Boolean getSomaValorRetroativo() {
        return somaValorRetroativo == null ? false : somaValorRetroativo;
    }

    public void setSomaValorRetroativo(Boolean somaValorRetroativo) {
        this.somaValorRetroativo = somaValorRetroativo;
    }

    public TipoValor getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(TipoValor tipoValor) {
        this.tipoValor = tipoValor;
    }

    public ItemBaseFP() {
        dataRegistro = new Date();
    }

    public ItemBaseFP(BaseFP baseFP, EventoFP eventoFP, OperacaoFormula operacaoFormula, TipoValor tipoValor, Date dataRegistro) {
        this.baseFP = baseFP;
        this.eventoFP = eventoFP;
        this.operacaoFormula = operacaoFormula;
        this.tipoValor = tipoValor;
        this.dataRegistro = dataRegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        return baseFP.toString() + " - " + eventoFP.toString() + " - " + operacaoFormula;
    }

    public int compareTo(ItemBaseFP item) {
        try {
            return this.getEventoFP().getCodigoInt().compareTo(item.getEventoFP().getCodigoInt());
        } catch (Exception ex) {
            return 0;
        }
    }
}
