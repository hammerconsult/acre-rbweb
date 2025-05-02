package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.EquipamentoFacade;
import br.com.webpublico.negocios.ReservaObjetoFrotaFacade;
import br.com.webpublico.negocios.VeiculoFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Wellington on 05/02/2016.
 */
@Service
@Transactional
public class ServiceNotificacaoFrotas {

    private static final Logger logger = LoggerFactory.getLogger(ServiceNotificacaoFrotas.class.getName());
    @PersistenceContext
    protected transient EntityManager em;
    public VeiculoFacade veiculoFacade;
    public EquipamentoFacade equipamentoFacade;
    public ReservaObjetoFrotaFacade reservaObjetoFrotaFacade;

    @PostConstruct
    private void init() {
        try {
            veiculoFacade = (VeiculoFacade) new InitialContext().lookup("java:module/VeiculoFacade");
            equipamentoFacade = (EquipamentoFacade) new InitialContext().lookup("java:module/EquipamentoFacade");
            reservaObjetoFrotaFacade = (ReservaObjetoFrotaFacade) new InitialContext().lookup("java:module/ReservaObjetoFrotaFacade");
        } catch (NamingException e) {
            logger.debug(e.getMessage());
        }
    }

    public void lancarNotificacoesDoFrotas() {
        veiculoFacade.lancarNotificacoesDeVeiculosComRevisaoAVencer();
        equipamentoFacade.lancarNotificacoesDeEquipamentosComRevisaoAVencer();
        reservaObjetoFrotaFacade.lancarNotificacoesDeReservasFuturas();
    }
}
