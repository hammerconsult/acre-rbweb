package br.com.webpublico.entidades.administrativo.patrimonio;


import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Renato Romanini on 13/06/2018.
 */
@Entity
@Audited
@Etiqueta("Unidade Controle Setorial")
public class UnidadeControleSetorial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Secretária Controle Setorial")
    @Obrigatorio
    private SecretariaControleSetorial secretariaControleSetorial;

    @ManyToOne
    @Etiqueta("Unidade Controle Setorial")
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;


    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public UnidadeControleSetorial() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public SecretariaControleSetorial getSecretariaControleSetorial() {
        return secretariaControleSetorial;
    }

    public void setSecretariaControleSetorial(SecretariaControleSetorial secretariaControleSetorial) {
        this.secretariaControleSetorial = secretariaControleSetorial;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public String toString() {
        return unidadeOrganizacional.toString();
    }
}
