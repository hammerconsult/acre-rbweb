package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 20/01/2019.
 */
@Audited
@Entity
public class BloqueioCpfCampanhaNfse extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CampanhaNfse campanha;
    @ManyToOne
    private PessoaFisica pessoaFisica;

    public BloqueioCpfCampanhaNfse() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CampanhaNfse getCampanha() {
        return campanha;
    }

    public void setCampanha(CampanhaNfse campanha) {
        this.campanha = campanha;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }
}
