/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author major
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Dívida Passiva")

public class LancContabilManual implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel

    private Date dataLancamento;
    @Obrigatorio
    @Tabelavel
    @Monetario
    private BigDecimal valorLancamento;

    @Tabelavel
    private String historico;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    private EventoContabil eventoContabil;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private ParametroEvento parametroEvento;

    public LancContabilManual() {
        valorLancamento = BigDecimal.ZERO;
    }

    public LancContabilManual(BigDecimal valorLancamento, Date dataLancamento, String numero, String historico, EventoContabil eventoContabil, UnidadeOrganizacional unidadeOrganizacional, ParametroEvento parametroEvento) {
        this.valorLancamento = valorLancamento;
        this.dataLancamento = dataLancamento;
        this.numero = numero;
        this.historico = historico;
        this.eventoContabil = eventoContabil;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.parametroEvento = parametroEvento;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getValorLancamento() {
        return valorLancamento;
    }

    public void setValorLancamento(BigDecimal valorLancamento) {
        this.valorLancamento = valorLancamento;
    }

    public ParametroEvento getParametroEvento() {
        return parametroEvento;
    }

    public void setParametroEvento(ParametroEvento parametroEvento) {
        this.parametroEvento = parametroEvento;
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
        if (!(object instanceof LancContabilManual)) {
            return false;
        }
        LancContabilManual other = (LancContabilManual) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.LacamentoContabilManual[ id=" + id + " ]";
    }
}
