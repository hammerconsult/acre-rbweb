/*
 * Codigo gerado automaticamente em Mon Jan 23 15:16:49 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.GrupoCotaORC;
import br.com.webpublico.entidades.GrupoOrcamentario;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class GrupoCotaORCFacade extends AbstractFacade<GrupoCotaORC> {

    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private GrupoOrcamentarioFacade grupoOrcamentarioFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private CotaOrcamentariaFacade cotaOrcamentariaFacade;
    @EJB
    private ReprocessamentoCotaOrcamentariaFacade reprocessamentoCotaOrcamentariaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoCotaORCFacade() {
        super(GrupoCotaORC.class);
    }

    @Override
    public GrupoCotaORC recuperar(Object id) {
        GrupoCotaORC gco = em.find(GrupoCotaORC.class, id);
        gco.getCotaOrcamentaria().size();
        return gco;
    }

    public GrupoCotaORC recuperaPorGrupo(GrupoOrcamentario grupo) {
        String hql = "from GrupoCotaORC where grupoOrcamentario = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", grupo);
        return (GrupoCotaORC) q.getSingleResult();
    }

    public List<GrupoCotaORC> listaExercicio(Exercicio ex) {
        String sql = "SELECT grupoc.* FROM grupocotaorc grupoc "
                + "INNER JOIN grupoorcamentario grupo ON grupoc.grupoorcamentario_id = grupo.id "
                + "INNER JOIN exercicio ex ON grupo.exercicio_id = ex.id WHERE ex.ano = :ano";
        Query q = em.createNativeQuery(sql, GrupoCotaORC.class);
        q.setParameter("ano", ex.getAno());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<GrupoCotaORC>();
        } else {
            return q.getResultList();
        }
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public GrupoOrcamentarioFacade getGrupoOrcamentarioFacade() {
        return grupoOrcamentarioFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public CotaOrcamentariaFacade getCotaOrcamentariaFacade() {
        return cotaOrcamentariaFacade;
    }

    public ReprocessamentoCotaOrcamentariaFacade getReprocessamentoCotaOrcamentariaFacade() {
        return reprocessamentoCotaOrcamentariaFacade;
    }
}
