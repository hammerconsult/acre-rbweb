package br.com.webpublico.entidades.administrativo.obra;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by zaca on 28/04/17.
 */
@Entity
@Audited
public class ResponsavelTecnico extends SuperEntidade  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private PessoaFisica pessoaFisica;

    public ResponsavelTecnico(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public ResponsavelTecnico() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }
}
