package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ControleViagemSaud;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ControleViagemSaudFacade extends AbstractFacade<ControleViagemSaud> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private MotoristaSaudFacade motoristaSaudFacade;

    @EJB
    private VeiculoSaudFacade veiculoSaudFacade;

    @EJB
    private AgendamentoViagemSaudFacade agendamentoViagemSaudFacade;

    public ControleViagemSaudFacade() {
        super(ControleViagemSaud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotoristaSaudFacade getMotoristaSaudFacade() {
        return motoristaSaudFacade;
    }

    public AgendamentoViagemSaudFacade getAgendamentoViagemSaudFacade() {
        return agendamentoViagemSaudFacade;
    }

    public VeiculoSaudFacade getVeiculoSaudFacade() {
        return veiculoSaudFacade;
    }

    @Override
    public void preSave(ControleViagemSaud entidade) {
        entidade.realizarValidacoes();
    }

    public Long proximoCodigoAgendamento() {
        String sql = " SELECT COALESCE(MAX(CVS.CODIGO), 0) FROM CONTROLEVIAGEMSAUD CVS ";
        Query q = em.createNativeQuery(sql);
        List<BigDecimal> resultado = q.getResultList();
        return resultado.get(0).longValue() + 1;
    }
}
