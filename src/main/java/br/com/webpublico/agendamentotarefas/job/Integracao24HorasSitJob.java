package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.ConfiguracaoWebService;
import br.com.webpublico.entidadesauxiliares.AssistenteIntegracaoSit;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.util.Util;
import org.joda.time.DateTime;

public class Integracao24HorasSitJob extends WPJob {

    @Override
    public void execute() {
        try {
            ConfiguracaoTributarioFacade configuracaoTributarioFacade = (ConfiguracaoTributarioFacade) Util.getFacadeViaLookup("java:module/ConfiguracaoTributarioFacade");
            LeitorWsConfig leitorWsConfig = (LeitorWsConfig) Util.getFacadeViaLookup("java:module/LeitorWsConfig");
            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            ConfiguracaoWebService configuracaoPorTipoDaKeyCorrente = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.REDESIM);
            if (configuracaoPorTipoDaKeyCorrente != null) {
                DateTime hoje = new DateTime();
                DateTime ontem = hoje.minusHours(configuracaoTributario.getIntervaloSit());
                AssistenteIntegracaoSit assistenteSit = new AssistenteIntegracaoSit(hoje.toDate(), ontem.toDate());
                assistenteSit.integrar(configuracaoPorTipoDaKeyCorrente.getUrl());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Integração com o sistema SIT";
    }

}
