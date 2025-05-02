package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.ComunicaSofPlanFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional
public class ServiceIntegracaoSofPlan {

    public static final Logger logger = LoggerFactory.getLogger(ServiceIntegracaoSofPlan.class);
    @PersistenceContext
    protected transient EntityManager em;
    public ComunicaSofPlanFacade comunicaSofPlanFacade;


    @PostConstruct
    private void init() {
        try {
            comunicaSofPlanFacade = (ComunicaSofPlanFacade) new InitialContext().lookup("java:module/ComunicaSofPlanFacade");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void integraCDA() {
        comunicaSofPlanFacade.enviarTodasCdasDisponiveis();
    }


    public void geraArquivoCDA() {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Geração de arquivo para Softplan");
        assistente.setUsuarioSistema(comunicaSofPlanFacade.getSistemaFacade().getUsuarioCorrente());
        comunicaSofPlanFacade.geraArquivoCdasDisponiveis(assistente);
    }


    public void retificaCDA() {
        comunicaSofPlanFacade.retificaCDA();
    }


}
