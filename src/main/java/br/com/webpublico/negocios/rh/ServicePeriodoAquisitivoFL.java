package br.com.webpublico.negocios.rh;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.negocios.PeriodoAquisitivoFLFacade;
import br.com.webpublico.negocios.SistemaFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Service
public class ServicePeriodoAquisitivoFL {
    public static final Logger logger = LoggerFactory.getLogger(ServicePeriodoAquisitivoFL.class);

    @PersistenceContext
    protected transient EntityManager em;
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;

    @PostConstruct
    private void init() {
        try {
            periodoAquisitivoFLFacade = (PeriodoAquisitivoFLFacade) new InitialContext().lookup("java:module/PeriodoAquisitivoFLFacade");
        } catch (NamingException e) {
            logger.error(e.getExplanation());
        }
    }

    public void lancarPeriodosAquisitivos() {
        Date dataAtual = SistemaFacade.getDataCorrente();
        for (ContratoFP contrato : recuperaPeriodoAquisitivoPorMesAno()) {
            try {
                logger.info("Servidor: " + contrato.toString());
                periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contrato, dataAtual, TipoPeriodoAquisitivo.FERIAS);
            } catch (Exception ex) {
                logger.error("Erro ao gerar período aquisitivo automaticamente {}", ex);
            }
        }

    }

    public List<ContratoFP> recuperaPeriodoAquisitivoPorMesAno() {
        Query q = em.createQuery(" from ContratoFP c " +
            " where trunc(:data) between trunc(c.inicioVigencia) and trunc(coalesce(c.finalVigencia, :data)) " +
            " and not exists (select 1 from PeriodoAquisitivoFL periodo " +
            "                   inner join periodo.baseCargo baseCargo " +
            "                   inner join baseCargo.basePeriodoAquisitivo basePeriodo" +
            "                   where periodo.contratoFP = c and trunc(:data) between trunc(periodo.inicioVigencia) and trunc(coalesce(periodo.finalVigencia, :data))" +
            "                           and basePeriodo.tipoPeriodoAquisitivo = :ferias) " +
            " order by c.id");
        q.setParameter("data", SistemaFacade.getDataCorrente());
        q.setParameter("ferias", TipoPeriodoAquisitivo.FERIAS);
        return q.getResultList();
    }
}
