package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by carlos on 27/05/15.
 */
@Audited
@Entity
@Etiqueta("Metodologia Módulo")
public class MetodologiaModulo extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ModuloCapacitacao moduloCapacitacao;
    @ManyToOne
    private Metodologia metodologia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModuloCapacitacao getModuloCapacitacao() {
        return moduloCapacitacao;
    }

    public void setModuloCapacitacao(ModuloCapacitacao moduloCapacitacao) {
        this.moduloCapacitacao = moduloCapacitacao;
    }

    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }
}
