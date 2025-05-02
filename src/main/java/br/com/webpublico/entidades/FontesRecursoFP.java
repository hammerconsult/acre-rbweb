/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@Entity

@Audited
public class FontesRecursoFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RecursoFP recursoFP;
    @ManyToOne
    private FonteDespesaORC fonteDespesaORC;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fontesRecursoFP", orphanRemoval = true)
    private List<FonteEventoFP> fonteEventoFPs;
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Inicio da Vigência")
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Transient
    private Date dataRegistro;
    @Transient
    private Operacoes operacao;

    public FontesRecursoFP() {
        fonteEventoFPs = new ArrayList<FonteEventoFP>();
        dataRegistro = new Date();
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public List<FonteEventoFP> getFonteEventoFPs() {
        return fonteEventoFPs;
    }

    public void setFonteEventoFPs(List<FonteEventoFP> fonteEventoFPs) {
        this.fonteEventoFPs = fonteEventoFPs;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public boolean isOperacaoEditar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
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
        if (!(object instanceof FontesRecursoFP)) {
            return false;
        }
        FontesRecursoFP other = (FontesRecursoFP) object;
        if (id != other.id && (id == null || !id.equals(other.id)) || dataRegistro != other.dataRegistro && (dataRegistro == null || !dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return fonteDespesaORC + "";
    }
}
