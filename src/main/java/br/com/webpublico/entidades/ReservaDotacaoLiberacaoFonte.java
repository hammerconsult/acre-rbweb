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
@Table(name = "RESERVADOTACAOLIBFONTE")
public class ReservaDotacaoLiberacaoFonte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ReservaDotacao reservaDotacao;
    @ManyToOne
    private LiberacaoFonteDespesaOrc liberacaoFonteDespesaOrc;

    public ReservaDotacaoLiberacaoFonte() {
    }

    public ReservaDotacaoLiberacaoFonte(ReservaDotacao reservaDotacao, LiberacaoFonteDespesaOrc liberacaoFonteDespesaOrc) {
        this.reservaDotacao = reservaDotacao;
        this.liberacaoFonteDespesaOrc = liberacaoFonteDespesaOrc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LiberacaoFonteDespesaOrc getLiberacaoFonteDespesaOrc() {
        return liberacaoFonteDespesaOrc;
    }

    public void setLiberacaoFonteDespesaOrc(LiberacaoFonteDespesaOrc liberacaoFonteDespesaOrc) {
        this.liberacaoFonteDespesaOrc = liberacaoFonteDespesaOrc;
    }

    public ReservaDotacao getReservaDotacao() {
        return reservaDotacao;
    }

    public void setReservaDotacao(ReservaDotacao reservaDotacao) {
        this.reservaDotacao = reservaDotacao;
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
        if (!(object instanceof ReservaDotacaoLiberacaoFonte)) {
            return false;
        }
        ReservaDotacaoLiberacaoFonte other = (ReservaDotacaoLiberacaoFonte) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ReservaDotacaoLiberacaoFonte[ id=" + id + " ]";
    }
}
