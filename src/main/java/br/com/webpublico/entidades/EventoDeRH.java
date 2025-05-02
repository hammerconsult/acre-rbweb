package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by AndreGustavo on 23/09/2014.
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Evento de RH")
public abstract class EventoDeRH extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Nome")
    private String nome;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Promotor do Evento")
    private Pessoa promotorEvento;
    @Etiqueta("Local do Evento")
    @ManyToOne
    private PessoaJuridica pessoaJuridica;

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

    public Pessoa getPromotorEvento() {
        return promotorEvento;
    }

    public void setPromotorEvento(Pessoa promotorEvento) {
        this.promotorEvento = promotorEvento;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }
}
