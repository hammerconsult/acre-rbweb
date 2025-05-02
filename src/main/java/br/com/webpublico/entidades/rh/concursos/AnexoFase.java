package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by venom on 21/11/14.
 */
@Entity
@Audited
public class AnexoFase extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private FaseConcurso faseConcurso;
    @Obrigatorio
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;

    public AnexoFase() {
        super();
    }

    public AnexoFase(FaseConcurso faseConcurso, Arquivo arquivo) {
        this.criadoEm = System.nanoTime();
        this.arquivo = arquivo;
        this.faseConcurso = faseConcurso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FaseConcurso getFaseConcurso() {
        return faseConcurso;
    }

    public void setFaseConcurso(FaseConcurso concurso) {
        this.faseConcurso = concurso;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}
