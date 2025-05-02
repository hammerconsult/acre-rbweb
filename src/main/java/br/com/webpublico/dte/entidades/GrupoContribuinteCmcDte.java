package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class GrupoContribuinteCmcDte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private GrupoContribuinteDte grupo;

    @ManyToOne
    private CadastroEconomico cadastroEconomico;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoContribuinteDte getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoContribuinteDte grupo) {
        this.grupo = grupo;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

}
