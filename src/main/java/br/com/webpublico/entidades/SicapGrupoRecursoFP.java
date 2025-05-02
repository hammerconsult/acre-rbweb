package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @Author peixe on 20/09/2016  14:05.
 */
@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Audited
@Etiqueta(value = "Sicap")
public class SicapGrupoRecursoFP extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Sicap sicap;
    @ManyToOne
    private GrupoRecursoFP grupoRecursoFP;

    public SicapGrupoRecursoFP() {
    }

    public SicapGrupoRecursoFP(Sicap sicap, GrupoRecursoFP grupoRecursoFP) {
        this.sicap = sicap;
        this.grupoRecursoFP = grupoRecursoFP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sicap getSicap() {
        return sicap;
    }

    public void setSicap(Sicap sicap) {
        this.sicap = sicap;
    }

    public GrupoRecursoFP getGrupoRecursoFP() {
        return grupoRecursoFP;
    }

    public void setGrupoRecursoFP(GrupoRecursoFP grupoRecursoFP) {
        this.grupoRecursoFP = grupoRecursoFP;
    }
}
