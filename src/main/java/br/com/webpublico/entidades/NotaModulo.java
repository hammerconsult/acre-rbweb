package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by carlos on 20/08/15.
 */
@Entity
@Audited
@Etiqueta("Nota no Módulo")
public class NotaModulo extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Inscrição")
    private InscricaoCapacitacao inscricaoCapacitacao;
    @ManyToOne
    @Etiqueta("Módulo de Capacitação")
    private ModuloCapacitacao moduloCapacitacao;
    @Etiqueta("Nota")
    private Double nota;

    public NotaModulo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InscricaoCapacitacao getInscricaoCapacitacao() {
        return inscricaoCapacitacao;
    }

    public void setInscricaoCapacitacao(InscricaoCapacitacao inscricaoCapacitacao) {
        this.inscricaoCapacitacao = inscricaoCapacitacao;
    }

    public ModuloCapacitacao getModuloCapacitacao() {
        return moduloCapacitacao;
    }

    public void setModuloCapacitacao(ModuloCapacitacao moduloCapacitacao) {
        this.moduloCapacitacao = moduloCapacitacao;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    @Override
    public String toString() {
        return moduloCapacitacao.getNomeModulo();
    }
}
