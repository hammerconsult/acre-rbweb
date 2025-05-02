/*
 * Codigo gerado automaticamente em Wed Nov 23 09:59:01 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrauDeParentesco;
import br.com.webpublico.entidades.GrauParentTipoDepend;
import br.com.webpublico.entidades.TipoDependente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoDependenteFacade extends AbstractFacade<TipoDependente> {

    private static final Logger logger = LoggerFactory.getLogger(TipoDependenteFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrauDeParentescoFacade grauDeParentescoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public TipoDependenteFacade() {
        super(TipoDependente.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrauDeParentescoFacade getGrauDeParentescoFacade() {
        return grauDeParentescoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public TipoDependente recuperar(Object id) {
        TipoDependente tipo = em.find(TipoDependente.class, id);
        tipo.getGrauParentTipoDepends().size();
        forcarRegistroInicialNaAuditoria(tipo);
        return tipo;
    }

    public TipoDependente recuperarSemDependencias(Object id) {
        return em.find(TipoDependente.class, id);
    }

    public void salvar(TipoDependente entity, List<GrauParentTipoDepend> listaExcluidos) {
        try {
            for (GrauParentTipoDepend g : listaExcluidos) {
                if (g.getId() != null) {
                    g = em.find(GrauParentTipoDepend.class, g.getId());
                    em.remove(g);
                }
            }
            getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    public TipoDependente recuperarTipoDependentePorCodigoEGrau(String codigoTipo, GrauDeParentesco grauDeParentesco) {
        Query q = this.em.createQuery("select tipo from TipoDependente tipo join tipo.grauParentTipoDepends grautipo where grautipo.grauDeParentesco = :grau and tipo.codigo = :codigo");
        q.setParameter("grau", grauDeParentesco);
        q.setParameter("codigo", codigoTipo);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (TipoDependente) q.getResultList().get(0);
        }
        return null;
    }

    public List<TipoDependente> completaTipoDependente(String filtro) {
        Query q = this.em.createQuery("from TipoDependente tipo where tipo.codigo like :filtro or tipo.descricao like :filtro");
        q.setParameter("filtro", "%" + filtro.trim().toUpperCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public TipoDependente recuperarTipoDependentePorCodigo(String codigoTipo) {
        Query q = this.em.createQuery("from TipoDependente tipo where tipo.codigo = :codigo");
        q.setParameter("codigo", codigoTipo);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (TipoDependente) q.getResultList().get(0);
        }
        return null;
    }



}
