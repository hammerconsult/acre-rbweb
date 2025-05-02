package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 05/06/2018.
 */
@Entity
@Audited
@Table(name = "ITEMEMPREGADORESOCIAL")
public class ItemConfiguracaoEmpregadorESocial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @ManyToOne
    private ConfiguracaoEmpregadorESocial configEmpregadorEsocial;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoEmpregadorESocial getConfigEmpregadorEsocial() {
        return configEmpregadorEsocial;
    }

    public void setConfigEmpregadorEsocial(ConfiguracaoEmpregadorESocial configEmpregadorEsocial) {
        this.configEmpregadorEsocial = configEmpregadorEsocial;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }
}
