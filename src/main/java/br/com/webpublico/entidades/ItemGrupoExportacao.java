/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemGrupoExportacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private OperacaoFormula operacaoFormula;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Evento Folha de Pagamento")
    @ManyToOne
    private EventoFP eventoFP;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Base Folha de Pagamento")
    @ManyToOne
    private BaseFP baseFP;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Grupo de Exportação")
    @ManyToOne
    private GrupoExportacao grupoExportacao;

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

    public GrupoExportacao getGrupoExportacao() {
        return grupoExportacao;
    }

    public void setGrupoExportacao(GrupoExportacao grupoExportacao) {
        this.grupoExportacao = grupoExportacao;
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ItemGrupoExportacao)) {
            return false;
        }
        ItemGrupoExportacao other = (ItemGrupoExportacao) object;
        if (this.id != null && other.id != null && !this.id.equals(other.id)) {
            return false;
        }
        if ((this.id == null || other.id == null) && !this.eventoFP.equals(other.eventoFP)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (eventoFP != null) {
            return eventoFP.toString();
        } else if (baseFP != null) {
            return baseFP.toString();
        }
        return "";
    }
}
