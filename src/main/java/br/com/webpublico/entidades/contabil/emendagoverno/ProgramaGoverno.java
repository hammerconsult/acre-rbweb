package br.com.webpublico.entidades.contabil.emendagoverno;

import br.com.webpublico.entidades.EsferaGoverno;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class ProgramaGoverno extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Esfera do Governo")
    private EsferaGoverno esferaGoverno;

    public ProgramaGoverno() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public EsferaGoverno getEsferaGoverno() {
        return esferaGoverno;
    }

    public void setEsferaGoverno(EsferaGoverno esferaGoverno) {
        this.esferaGoverno = esferaGoverno;
    }

    @Override
    public String toString() {
        return nome + " - " + esferaGoverno.getNome();
    }
}
