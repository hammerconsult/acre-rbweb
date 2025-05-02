package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AcaoPrincipal;
import br.com.webpublico.entidades.ParticipanteAcaoPrincipal;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 30/06/14
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ParticipanteAcaoPrincipalFacade extends AbstractFacade<ParticipanteAcaoPrincipal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParticipanteAcaoPrincipalFacade() {
        super(ParticipanteAcaoPrincipal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ParticipanteAcaoPrincipal> recuperarParticipanteAcaoPrincipal(AcaoPrincipal ac) {
        List<ParticipanteAcaoPrincipal> listaParticipantes = new ArrayList<ParticipanteAcaoPrincipal>();
        String hql = " from ParticipanteAcaoPrincipal s where s.acaoPrincipal = :acao ";
        if (ac != null) {
            Query q = em.createQuery(hql);
            q.setParameter("acao", ac);
            listaParticipantes = q.getResultList();
        }
        return listaParticipantes;
    }
}
