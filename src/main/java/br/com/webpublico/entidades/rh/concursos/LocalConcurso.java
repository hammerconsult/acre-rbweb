package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.enums.TipoLocalConcurso;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by venom on 28/10/14.
 */
@Entity
@Audited
@Etiqueta(value = "Local Concurso")
@GrupoDiagrama(nome = "Concursos")
public class LocalConcurso extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Concurso concurso;

    @Etiqueta(value = "Código")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoLocalConcurso tipoLocalConcurso;

    @ManyToOne(cascade = CascadeType.ALL)
    private EnderecoCorreio enderecoCorreio;

    @Etiqueta(value = "Telefone")
    @Pesquisavel
    @Tabelavel
    private String telefone;

    public LocalConcurso() {
        super();
        setEnderecoCorreio(new EnderecoCorreio());
    }

    public LocalConcurso(Concurso concurso) {
        setConcurso(concurso);
        setEnderecoCorreio(new EnderecoCorreio());
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

    public TipoLocalConcurso getTipoLocalConcurso() {
        return tipoLocalConcurso;
    }

    public void setTipoLocalConcurso(TipoLocalConcurso tipoLocalConcurso) {
        this.tipoLocalConcurso = tipoLocalConcurso;
    }

    public EnderecoCorreio getEnderecoCorreio() {
        return enderecoCorreio;
    }

    public void setEnderecoCorreio(EnderecoCorreio enderecoCorreio) {
        this.enderecoCorreio = enderecoCorreio;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
