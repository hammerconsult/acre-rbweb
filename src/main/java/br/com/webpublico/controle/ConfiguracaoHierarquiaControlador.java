/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoHierarquiaOrganizacionalFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * @author reidocrime
 */
@ManagedBean
@SessionScoped
public class ConfiguracaoHierarquiaControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ConfiguracaoHierarquiaOrganizacionalFacade configuracaoHierarquiaOrganizacionalFacade;
    private ConfiguracaoHierarquiaOrganizacional configuracaoHierarquiaAdministrativa;
    private ConfiguracaoHierarquiaOrganizacional configuracaoHierarquiaOrcamentaria;

    @Override
    public AbstractFacade getFacede() {
        return configuracaoHierarquiaOrganizacionalFacade;
    }

    public void novaConfiguracao() {
        this.configuracaoHierarquiaOrcamentaria = configuracaoHierarquiaOrganizacionalFacade.configuracaoVigente(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        this.configuracaoHierarquiaAdministrativa = configuracaoHierarquiaOrganizacionalFacade.configuracaoVigente(TipoHierarquiaOrganizacional.ADMINISTRATIVA);

        if (this.configuracaoHierarquiaOrcamentaria == null) {
            this.configuracaoHierarquiaOrcamentaria = new ConfiguracaoHierarquiaOrganizacional();
            this.configuracaoHierarquiaOrcamentaria.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }

        if (this.configuracaoHierarquiaAdministrativa == null) {
            this.configuracaoHierarquiaAdministrativa = new ConfiguracaoHierarquiaOrganizacional();
            this.configuracaoHierarquiaAdministrativa.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        }

    }

    public ConfiguracaoHierarquiaOrganizacional getConfiguracaoHierarquiaAdministrativa() {
        novaConfiguracao();
        return configuracaoHierarquiaAdministrativa;
    }

    public void setConfiguracaoHierarquiaAdministrativa(ConfiguracaoHierarquiaOrganizacional configuracaoHierarquiaAdministrativa) {
        this.configuracaoHierarquiaAdministrativa = configuracaoHierarquiaAdministrativa;
    }

    public ConfiguracaoHierarquiaOrganizacional getConfiguracaoHierarquiaOrcamentaria() {
        novaConfiguracao();
        return configuracaoHierarquiaOrcamentaria;
    }

    public void setConfiguracaoHierarquiaOrcamentaria(ConfiguracaoHierarquiaOrganizacional configuracaoHierarquiaOrcamentaria) {
        this.configuracaoHierarquiaOrcamentaria = configuracaoHierarquiaOrcamentaria;
    }

    public void salvarConf() {

        configuracaoHierarquiaOrganizacionalFacade.salvarNovo(configuracaoHierarquiaAdministrativa, configuracaoHierarquiaOrcamentaria);

    }
}
