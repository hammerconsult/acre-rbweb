package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by venom on 05/11/14.
 */
@Entity
@Audited
public class LocalPagamento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Local")
    private LocalConcurso localConcurso;
    @ManyToOne
    private Concurso concurso;
    @Transient
    private Long criadoEm;

    public LocalPagamento() {
        this.criadoEm = System.nanoTime();
    }

    public LocalPagamento(LocalConcurso localConcurso, Concurso concurso) {
        this.criadoEm = System.nanoTime();
        this.localConcurso = localConcurso;
        setConcurso(concurso);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalConcurso getLocalConcurso() {
        return localConcurso;
    }

    public void setLocalConcurso(LocalConcurso localConcurso) {
        this.localConcurso = localConcurso;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
