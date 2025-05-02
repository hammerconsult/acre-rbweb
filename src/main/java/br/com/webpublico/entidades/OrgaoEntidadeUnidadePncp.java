package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Órgão/Entidade Unidade Pncp")
public class OrgaoEntidadeUnidadePncp extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private OrgaoEntidadePncp orgaoEntidadePncp;

    private Boolean integrado;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public OrgaoEntidadeUnidadePncp() {
        integrado =false;
    }

    public OrgaoEntidadePncp getOrgaoEntidadePncp() {
        return orgaoEntidadePncp;
    }

    public void setOrgaoEntidadePncp(OrgaoEntidadePncp orgaoEntidadePncp) {
        this.orgaoEntidadePncp = orgaoEntidadePncp;
    }

    public Boolean getIntegrado() {
        return integrado;
    }

    public void setIntegrado(Boolean integrado) {
        this.integrado = integrado;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
        if (hierarquiaOrganizacional !=null){
            setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
