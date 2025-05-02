/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "ReservaDeDotacao")
public class ReservaDotacaoReservaFonte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ReservaDotacao reservaDotacao;
    @ManyToOne
    private ReservaFonteDespesaOrc reservaFonteDespesaOrc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservaDotacao getReservaDotacao() {
        return reservaDotacao;
    }

    public void setReservaDotacao(ReservaDotacao reservaDotacao) {
        this.reservaDotacao = reservaDotacao;
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
        if (!(object instanceof ReservaDotacaoReservaFonte)) {
            return false;
        }
        ReservaDotacaoReservaFonte other = (ReservaDotacaoReservaFonte) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ReservaDotacaoReservaFonte[ id=" + id + " ]";
    }
}
