package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by venom on 21/11/14.
 */
@Entity
@Audited
public class AnexoConcurso extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private Concurso concurso;
    @Obrigatorio
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;

    public AnexoConcurso() {
        super();
    }

    public AnexoConcurso(Concurso concurso, Arquivo arquivo) {
        this.criadoEm = System.nanoTime();
        setConcurso(concurso);
        this.arquivo = arquivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
        this.concurso.adicionaAnexo(this);
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}
