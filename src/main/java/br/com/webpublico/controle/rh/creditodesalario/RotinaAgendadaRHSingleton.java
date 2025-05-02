package br.com.webpublico.controle.rh.creditodesalario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
public class RotinaAgendadaRHSingleton {
    private static final Logger logger = LoggerFactory.getLogger(RotinaAgendadaRHSingleton.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Schedule(hour = "10/3")
    public void removerFichaFinanceiraFPSemItemFicha() {
        logger.info("Removendo fichas financeiras órfãs");
        em.createNativeQuery("delete from FICHAFINANCEIRAFP where id not in(select FICHAFINANCEIRAFP_ID from ITEMFICHAFINANCEIRAFP ) and id not in(select FICHAFINANCEIRAFP_ID from EMPENHOFICHAFINANCEIRAFP) ").executeUpdate();
    }
}
