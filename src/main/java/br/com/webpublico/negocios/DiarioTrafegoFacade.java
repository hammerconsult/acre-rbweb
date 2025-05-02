package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DiarioTrafego;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 16/10/14
 * Time: 08:25
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class DiarioTrafegoFacade extends AbstractFacade<DiarioTrafego> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DiarioTrafegoFacade() {
        super(DiarioTrafego.class);
    }

    @Override
    public DiarioTrafego recuperar(Object id) {
        DiarioTrafego diarioTrafego = super.recuperar(id);
        diarioTrafego.getItensDiarioTrafego().size();
        if (diarioTrafego.getDetentorArquivoComposicao() != null &&
            diarioTrafego.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            diarioTrafego.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return diarioTrafego;
    }


    public boolean existeDiarioTrafego(DiarioTrafego diarioTrafego) {
        String hql = " select dt from DiarioTrafego dt " +
            " where dt.ano = :ano " +
            "   and dt.mes = :mes " +
            "   and dt.veiculo = :veiculo ";
        if (diarioTrafego.getId() != null) {
            hql += " and dt.id <> :id ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("ano", diarioTrafego.getAno());
        q.setParameter("mes", diarioTrafego.getMes());
        q.setParameter("veiculo", diarioTrafego.getVeiculo());
        if (diarioTrafego.getId() != null) {
            q.setParameter("id", diarioTrafego.getId());
        }
        return q.getResultList() != null && q.getResultList().size() > 0;
    }

    public DiarioTrafego salvarRetornando(DiarioTrafego entity) {
        return em.merge(entity);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
