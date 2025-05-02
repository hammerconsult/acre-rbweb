package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.nfse.domain.CampanhaNfse;
import br.com.webpublico.nfse.facades.CampanhaNfseFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.List;

@Service
@Transactional
public class NotaPremiadaService implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(NotaPremiadaService.class);
    private CampanhaNfseFacade campanhaNfseFacade;

    @PostConstruct
    private void init() {
        try {
            campanhaNfseFacade = (CampanhaNfseFacade) new InitialContext().lookup("java:module/CampanhaNfseFacade");
        } catch (NamingException e) {
            logger.error("Erro ao iniciar service integração rede sim {}", e);
        }
    }

    public void gerarCupons() {
        List<CampanhaNfse> campanhas = campanhaNfseFacade.buscarCampanhasAbertas();
        if (campanhas != null) {
            for (CampanhaNfse campanha : campanhas) {
                campanhaNfseFacade.gerarCuponsEletronicos(campanha, new AssistenteBarraProgresso());
            }
        }
    }
}
