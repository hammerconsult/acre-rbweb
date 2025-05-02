/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.geradores.GrupoDiagrama;
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
@GrupoDiagrama(nome = "PPA")
public class LiberacaoFonteDespesaOrc implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data da Liberação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLiberacao;
    @Obrigatorio
    @Tabelavel
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    private OrigemReservaFonte origemReservaFonte;
    @ManyToOne
    private ReservaFonteDespesaOrc reservaFonteDespesaOrc;

    public LiberacaoFonteDespesaOrc() {
    }

    public LiberacaoFonteDespesaOrc(Date dataLiberacao, BigDecimal valor, OrigemReservaFonte origemReservaFonte, ReservaFonteDespesaOrc reservaFonteDespesaOrc) {
        this.dataLiberacao = dataLiberacao;
        this.valor = valor;
        this.origemReservaFonte = origemReservaFonte;
        this.reservaFonteDespesaOrc = reservaFonteDespesaOrc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }

    public OrigemReservaFonte getOrigemReservaFonte() {
        return origemReservaFonte;
    }

    public void setOrigemReservaFonte(OrigemReservaFonte origemReservaFonte) {
        this.origemReservaFonte = origemReservaFonte;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ReservaFonteDespesaOrc getReservaFonteDespesaOrc() {
        return reservaFonteDespesaOrc;
    }

    public void setReservaFonteDespesaOrc(ReservaFonteDespesaOrc reservaFonteDespesaOrc) {
        this.reservaFonteDespesaOrc = reservaFonteDespesaOrc;
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
        if (!(object instanceof LiberacaoFonteDespesaOrc)) {
            return false;
        }
        LiberacaoFonteDespesaOrc other = (LiberacaoFonteDespesaOrc) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data: " + dataLiberacao + " - " + valor;
    }
}
