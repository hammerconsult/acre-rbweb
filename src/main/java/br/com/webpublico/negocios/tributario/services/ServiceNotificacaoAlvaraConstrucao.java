package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.AlvaraConstrucaoFacade;
import br.com.webpublico.negocios.IntegracaoRedeSimFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Service
@Transactional
public class ServiceNotificacaoAlvaraConstrucao {

    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;

    @PostConstruct
    private void init() {
        try {
            alvaraConstrucaoFacade = (AlvaraConstrucaoFacade) new InitialContext().lookup("java:module/AlvaraConstrucaoFacade");
        } catch (NamingException e) {
        }
    }

    public void lancar() {
        alvaraConstrucaoFacade.lancarNotificacoesVencidos();
        alvaraConstrucaoFacade.lancarNotificacoesAVencer();
    }

}
