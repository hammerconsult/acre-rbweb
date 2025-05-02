package br.com.webpublico.entidadesauxiliares;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("retorno-integracaos")
public class IntegracaoSit {

    @XStreamAlias("retorno-integracao")
    List<CadastroImobiliarioIntegracaoSit> cadastrosImobiliarioIntegracaoSit;

    public List<CadastroImobiliarioIntegracaoSit> getCadastrosImobiliarioIntegracaoSit() {
        return cadastrosImobiliarioIntegracaoSit;
    }

    public void setCadastrosImobiliarioIntegracaoSit(List<CadastroImobiliarioIntegracaoSit> cadastrosImobiliarioIntegracaoSit) {
        this.cadastrosImobiliarioIntegracaoSit = cadastrosImobiliarioIntegracaoSit;
    }
}
