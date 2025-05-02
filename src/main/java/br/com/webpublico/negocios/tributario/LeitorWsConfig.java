package br.com.webpublico.negocios.tributario;


import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.ConfiguracaoWebService;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class LeitorWsConfig {

    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoWebService getConfiguracaoPorTipoDaKeyCorrente(TipoWebService tipo) {
        return  getConfiguracaoPorTipoDaKeyCorrente(tipo, sistemaFacade.getUsuarioBancoDeDados());
    }
    public ConfiguracaoWebService getConfiguracaoPorTipoDaKeyCorrente(TipoWebService tipo, String chave) {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        for (ConfiguracaoWebService config : configuracaoTributario.getItemConfiguracaoWebService()) {
            if (config.getTipo().equals(tipo) && config.getChave().equals(chave)) {
                return config;
            }
        }
        return null;
    }

}
