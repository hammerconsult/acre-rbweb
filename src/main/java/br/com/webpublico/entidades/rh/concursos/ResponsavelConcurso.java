package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by venom on 27/10/14.
 */
@Entity
@Audited
public class ResponsavelConcurso extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Concurso concurso;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Servidor")
    private ContratoFP contratoFP;

    public ResponsavelConcurso() {
        super();
    }

    public ResponsavelConcurso(Concurso concurso, ContratoFP contratoFP) {
        this.criadoEm = System.nanoTime();
        this.concurso = concurso;
        this.contratoFP = contratoFP;
        addConcursoList();
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
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    private void addConcursoList() {
        if (this.concurso.getResponsaveis().isEmpty()) {
            this.concurso.adicionaResponsavel(this);
        } else {
            boolean contains = false;
            for (ResponsavelConcurso rc : this.concurso.getResponsaveis()) {
                if (rc.equalsByAttributes(this)) {
                    contains = true;
                }
            }
            if (!contains) {
                this.concurso.adicionaResponsavel(this);
            }
        }
    }

    public boolean equalsByAttributes(ResponsavelConcurso rc) {
        return this.concurso.equals(rc.concurso) && this.contratoFP.equals(rc.contratoFP);
    }
}
