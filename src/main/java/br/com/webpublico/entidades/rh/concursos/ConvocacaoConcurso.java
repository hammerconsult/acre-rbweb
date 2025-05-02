package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by venom on 13/11/14.
 */
@Entity
@Audited
@Etiqueta("Convocação Concurso")
public class ConvocacaoConcurso extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Etiqueta("Concurso")
    @Pesquisavel
    @Tabelavel
    private Concurso concurso;
    @OneToOne
    @Etiqueta("Cargo")
    @Pesquisavel
    @Tabelavel
    private CargoConcurso cargo;
    @Etiqueta(value = "")
    @Temporal(TemporalType.DATE)
    @Tabelavel
    private Date realizadaEm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CargoConcurso getCargo() {
        return cargo;
    }

    public void setCargo(CargoConcurso cargo) {
        this.cargo = cargo;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public Date getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(Date realizadaEm) {
        this.realizadaEm = realizadaEm;
    }
}
