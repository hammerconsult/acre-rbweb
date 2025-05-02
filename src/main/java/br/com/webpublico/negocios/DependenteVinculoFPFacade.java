/*
 * Codigo gerado automaticamente em Thu Aug 04 15:51:37 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Dependente;
import br.com.webpublico.entidades.DependenteVinculoFP;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.TipoDependente;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
public class DependenteVinculoFPFacade extends AbstractFacade<DependenteVinculoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DependenteVinculoFPFacade() {
        super(DependenteVinculoFP.class);
    }

    /**
     * Método retorna um dependentevinculofp, buscando
     * se o responsável selecionado é um dependente ainda
     * vigente
     *
     * @param dependente
     * @return
     */
    public DependenteVinculoFP recuperaDependenteVigentePorResponsavel(Dependente dependente) {
        try {
            Query q = em.createQuery(" select dependenteVinculoFP from DependenteVinculoFP dependenteVinculoFP "
                    + " inner join dependenteVinculoFP.dependente dependente "
                    + " where dependente.dependente = :dependente "
                    + " and dependenteVinculoFP.finalVigencia is null ");
            q.setParameter("dependente", dependente.getResponsavel());
            q.setMaxResults(1);
            return (DependenteVinculoFP) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public DependenteVinculoFP recuperaDependenteVigentePorDependente(PessoaFisica dependente) {
        try {
            Query q = em.createQuery(" select dependenteVinculoFP from DependenteVinculoFP dependenteVinculoFP "
                    + " inner join dependenteVinculoFP.dependente dependente "
                    + " where dependente.dependente = :dependente "
                    + " and :dataVigencia >= dependenteVinculoFP.inicioVigencia and "
                    + " :dataVigencia <= coalesce(dependenteVinculoFP.finalVigencia,:dataVigencia)");
            q.setParameter("dependente", dependente);
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
            q.setMaxResults(1);
            return (DependenteVinculoFP) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean hasDependenteIRRF(Dependente dependente) {
        try {
            Query q = em.createQuery(" select dependenteVinculoFP from DependenteVinculoFP dependenteVinculoFP "
                + " inner join dependenteVinculoFP.dependente dependente "
                + " where dependente = :dependente "
                + " and :dataVigencia >= dependenteVinculoFP.inicioVigencia and "
                + " :dataVigencia <= coalesce(dependenteVinculoFP.finalVigencia,:dataVigencia)" +
                "  and dependenteVinculoFP.tipoDependente.codigo in (:tipos)");
            q.setParameter("dependente", dependente);
            q.setParameter("tipos", Lists.newArrayList(2,4,10,3));
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
            return !q.getResultList().isEmpty();
        } catch (Exception e) {
            return null;
        }
    }

    public List<DependenteVinculoFP> recuperaDependentesVigentePorTipo(Date dataVigencia, TipoDependente tipoDependente) {
        try {
            String hql = "select distinct dependenteVinculoFP from DependenteVinculoFP dependenteVinculoFP "+
                    " where :dataVigencia between dependenteVinculoFP.inicioVigencia and coalesce(dependenteVinculoFP.finalVigencia, :dataVigencia)" +
                    " and dependenteVinculoFP.tipoDependente = :tipoDependente";
            Query q = em.createQuery(hql);
            q.setParameter("dataVigencia", dataVigencia);
            q.setParameter("tipoDependente", tipoDependente);

            List<DependenteVinculoFP> lista = q.getResultList();

            Collections.sort(lista);
            return lista;
        } catch (Exception e) {
            return null;
        }
    }
}
