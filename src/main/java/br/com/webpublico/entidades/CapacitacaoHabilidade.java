package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by AndreGustavo on 27/10/2014.
 */
@Audited
@Entity
@Etiqueta("Habilidades da Capacitação")
public class CapacitacaoHabilidade extends SuperEntidade{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Capacitacao capacitacao;
    @ManyToOne
    private Habilidade habilidade;

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

    public Habilidade getHabilidade() {
        return habilidade;
    }

    public void setHabilidade(Habilidade habilidade) {
        this.habilidade = habilidade;
    }
}
