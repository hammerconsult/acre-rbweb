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
public class AtoLegalLiberacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AtoLegal atoLegal;
    @ManyToOne
    private LiberacaoFonteDespesaOrc liberacaoFonteDespesaOrc;

    public AtoLegalLiberacao() {
    }

    public AtoLegalLiberacao(AtoLegal atoLegal, LiberacaoFonteDespesaOrc liberacaoFonteDespesaOrc) {
        this.atoLegal = atoLegal;
        this.liberacaoFonteDespesaOrc = liberacaoFonteDespesaOrc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public LiberacaoFonteDespesaOrc getLiberacaoFonteDespesaOrc() {
        return liberacaoFonteDespesaOrc;
    }

    public void setLiberacaoFonteDespesaOrc(LiberacaoFonteDespesaOrc liberacaoFonteDespesaOrc) {
        this.liberacaoFonteDespesaOrc = liberacaoFonteDespesaOrc;
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
        if (!(object instanceof AtoLegalLiberacao)) {
            return false;
        }
        AtoLegalLiberacao other = (AtoLegalLiberacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Ato Legal: " + atoLegal + " - Liberação: " + liberacaoFonteDespesaOrc;
    }
}
