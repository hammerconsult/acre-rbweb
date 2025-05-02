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
@GrupoDiagrama(nome = "PPA")
public class AnulacaoReserva implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AnulacaoORC anulacaoORC;
    @ManyToOne
    private ReservaFonteDespesaOrc reservaFonteDespesaOrc;

    public AnulacaoReserva() {
    }

    public AnulacaoReserva(AnulacaoORC anulacaoORC, ReservaFonteDespesaOrc reservaFonteDespesaOrc) {
        this.anulacaoORC = anulacaoORC;
        this.reservaFonteDespesaOrc = reservaFonteDespesaOrc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnulacaoORC getAnulacaoORC() {
        return anulacaoORC;
    }

    public void setAnulacaoORC(AnulacaoORC anulacaoORC) {
        this.anulacaoORC = anulacaoORC;
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
        if (!(object instanceof AnulacaoReserva)) {
            return false;
        }
        AnulacaoReserva other = (AnulacaoReserva) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Anulação: " + anulacaoORC + " - " + "Reserva: " + reservaFonteDespesaOrc;
    }
}
