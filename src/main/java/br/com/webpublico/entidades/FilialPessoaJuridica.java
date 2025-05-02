package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Fabio on 09/10/2018.
 */
@Entity
@Audited
@Table(name = "FILIALPJ")
public class FilialPessoaJuridica extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaJuridica pessoaJuridica;
    @ManyToOne
    private Pessoa filial;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public Pessoa getFilial() {
        return filial;
    }

    public void setFilial(Pessoa filial) {
        this.filial = filial;
    }
}
