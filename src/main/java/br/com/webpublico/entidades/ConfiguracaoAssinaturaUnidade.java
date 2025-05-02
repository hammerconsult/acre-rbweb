package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "CONFIGASSINATURAUNIDADE")
public class ConfiguracaoAssinaturaUnidade extends SuperEntidade implements EntidadeWebPublico {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoAssinatura configuracaoAssinatura;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ConfiguracaoAssinaturaUnidade() {
    }

    public ConfiguracaoAssinaturaUnidade(ConfiguracaoAssinatura configuracaoAssinatura) {
        this.configuracaoAssinatura = configuracaoAssinatura;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoAssinatura getConfiguracaoAssinatura() {
        return configuracaoAssinatura;
    }

    public void setConfiguracaoAssinatura(ConfiguracaoAssinatura configuracaoAssinatura) {
        this.configuracaoAssinatura = configuracaoAssinatura;
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
    }

    @Override
    public String toString() {
        return unidadeOrganizacional.toString();
    }
}
