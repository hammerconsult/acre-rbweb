package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ParametroETRAceite extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametroETR parametroETR;
    @Obrigatorio
    @Etiqueta("Unidade Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroETR getParametroETR() {
        return parametroETR;
    }

    public void setParametroETR(ParametroETR parametroETR) {
        this.parametroETR = parametroETR;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        if (this.hierarquiaOrganizacional != null) {
            this.unidadeOrganizacional = this.hierarquiaOrganizacional.getSubordinada();
        }
    }
}
