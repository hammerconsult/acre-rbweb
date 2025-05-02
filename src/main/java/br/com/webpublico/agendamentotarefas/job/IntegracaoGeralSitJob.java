package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.entidades.ConfiguracaoWebService;
import br.com.webpublico.entidadesauxiliares.AssistenteIntegracaoSit;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.util.Util;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class IntegracaoGeralSitJob extends WPJob {


    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void execute() {
        try {
            CadastroImobiliarioFacade cadastroImobiliarioFacade = (CadastroImobiliarioFacade) Util.getFacadeViaLookup("java:module/CadastroImobiliarioFacade");
            LeitorWsConfig leitorWsConfig = (LeitorWsConfig) Util.getFacadeViaLookup("java:module/LeitorWsConfig");
            ConfiguracaoWebService configuracaoPorTipoDaKeyCorrente = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.REDESIM);
            if (configuracaoPorTipoDaKeyCorrente != null) {
                DateTime hoje = new DateTime();
                DateTime ontem = hoje.minusMonths(3);
                List<String> inscricoes = cadastroImobiliarioFacade.buscarInscricoesCadastraisNaoAtualiazadasDoPeriodoAteHoje(ontem.toDate());
                AssistenteIntegracaoSit assistenteSit = new AssistenteIntegracaoSit(inscricoes);
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
