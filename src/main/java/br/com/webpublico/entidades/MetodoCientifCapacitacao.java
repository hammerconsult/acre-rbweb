package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by carlos on 26/05/15.
 */
@Entity
@Audited
@Etiqueta("Método Científico Capacitação")
public class MetodoCientifCapacitacao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Capacitacao capacitacao;
    @ManyToOne
    private MetodoCientifico metodoCientifico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Capacitacao getCapacitacao() {
        return capacitacao;
    }

    public void setCapacitacao(Capacitacao capacitacao) {
        this.capacitacao = capacitacao;
    }

    public MetodoCientifico getMetodoCientifico() {
        return metodoCientifico;
    }

    public void setMetodoCientifico(MetodoCientifico metodoCientifico) {
        this.metodoCientifico = metodoCientifico;
    }
}
