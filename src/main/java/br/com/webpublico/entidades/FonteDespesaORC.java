/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "ReservaDeDotacao")
@Etiqueta("Fonte de Despesa Orçamentária")
public class FonteDespesaORC implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta(value = "Despesa")
    @Tabelavel
    private DespesaORC despesaORC;
    @ManyToOne
    @Etiqueta(value = "Fonte")
    @Tabelavel
    @Pesquisavel
    private ProvisaoPPAFonte provisaoPPAFonte;
    @ManyToOne
    @Etiqueta(value = "Grupo Orçamentário")
    @Tabelavel
    @Pesquisavel
    private GrupoOrcamentario grupoOrcamentario;

    public FonteDespesaORC() {
    }

    public FonteDespesaORC(DespesaORC despesaORC, ProvisaoPPAFonte provisaoPPAFonte, GrupoOrcamentario grupoOrcamentario) {
        this.despesaORC = despesaORC;
        this.provisaoPPAFonte = provisaoPPAFonte;
        this.grupoOrcamentario = grupoOrcamentario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public ProvisaoPPAFonte getProvisaoPPAFonte() {
        return provisaoPPAFonte;
    }

    public void setProvisaoPPAFonte(ProvisaoPPAFonte provisaoPPAFonte) {
        this.provisaoPPAFonte = provisaoPPAFonte;
    }

    public GrupoOrcamentario getGrupoOrcamentario() {
        return grupoOrcamentario;
    }

    public void setGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario) {
        this.grupoOrcamentario = grupoOrcamentario;
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
        if (!(object instanceof FonteDespesaORC)) {
            return false;
        }
        FonteDespesaORC other = (FonteDespesaORC) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (despesaORC != null && provisaoPPAFonte != null) {
            return this.despesaORC.toString() + " - " + this.provisaoPPAFonte.getDestinacaoDeRecursos() + " - " + getDescricaoFonteDeRecurso();
        } else {
            return "";
        }
    }

    public String getDescricaoContaDespesa() {
        try {
            return getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDescricaoFonteDeRecurso() {
        try {
            return getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().toString();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public String getDescricaoContaDeDestinacao() {
        try {
            return getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().toString();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public String getFuncionalProgramatica() {
        try {
            return getDespesaORC().getFuncionalProgramatica();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return this.getDespesaORC().getTipoContaDespesa();
    }

    public Conta getContaDeDespesa() {
        try {
            return this.getDespesaORC().getContaDeDespesa();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar a despesa orçamentária da fonte de recurso.");
        }
    }
}
