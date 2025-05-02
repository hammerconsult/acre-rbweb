/*
 * Codigo gerado automaticamente em Wed Aug 24 16:52:39 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RecursoDoVinculoFP;
import br.com.webpublico.entidades.RecursoFP;
import br.com.webpublico.entidades.VinculoFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Stateless
public class RecursoDoVinculoFPFacade extends AbstractFacade<RecursoDoVinculoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RecursoDoVinculoFPFacade() {
        super(RecursoDoVinculoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(RecursoDoVinculoFP entity) {
        entity = getRecursoDoVinculoFPComHistorico(entity);
        super.salvar(entity);
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFPComHistorico(RecursoDoVinculoFP recursoDoVinculoFP) {
        RecursoDoVinculoFP recursoDoVinculoFPAtualmentePersistido = getRecursoDoVinculoFPAtualmentePersistido(recursoDoVinculoFP);
        recursoDoVinculoFP.criarOrAtualizarAndAssociarHistorico(recursoDoVinculoFPAtualmentePersistido);
        return recursoDoVinculoFP;
    }

    private RecursoDoVinculoFP getRecursoDoVinculoFPAtualmentePersistido(RecursoDoVinculoFP recursoDoVinculoFP) {
        if (recursoDoVinculoFP.getId() != null) {
            return recuperar(recursoDoVinculoFP.getId());
        }
        return recursoDoVinculoFP;
    }

    public RecursoDoVinculoFP recuperarRecursoDoVinculo(VinculoFP vinculo) {

        String hql = "from RecursoDoVinculoFP re where to_date(:parametroDataReferencia) between to_char(re.inicioVigencia,'dd/MM/yyyy') and coalesce(to_char(re.finalVigencia,'dd/MM/yyyy'),:parametroDataReferencia) and re.vinculoFP = :vinculo";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroDataReferencia", dataAtualFormataDDMMYYYY(new Date()));
        q.setParameter("vinculo", vinculo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty())
            return null;
        return (RecursoDoVinculoFP) q.getSingleResult();
    }

    public List<RecursoDoVinculoFP> recuperarRecursosDoVinculo(VinculoFP vinculo) {

        String hql = "from RecursoDoVinculoFP re where re.vinculoFP = :vinculo order by re.inicioVigencia desc";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("vinculo", vinculo);

        return q.getResultList();
    }

    public List<RecursoDoVinculoFP> recuperarRecursosDoVinculo(VinculoFP vinculo, Date dataVigente) {

        String hql = "from RecursoDoVinculoFP re where re.vinculoFP = :vinculo and :data between re.inicioVigencia and coalesce(re.finalVigencia,:data)";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("vinculo", vinculo);
        q.setParameter("data", dataVigente);

        return q.getResultList();
    }

    private String dataAtualFormataDDMMYYYY(Date data) {
        SimpleDateFormat formata = new SimpleDateFormat("dd/MM/yyyy");
        return formata.format(data);
    }

    public RecursoDoVinculoFP ultimoRecursoDoVinculo(VinculoFP vinculoFP) {
        try {
            String hql = "from RecursoDoVinculoFP re " +
                " where re.vinculoFP = :vinculoFP " +
                " order by re.finalVigencia desc, id desc";
            Query q = em.createQuery(hql);
            q.setParameter("vinculoFP", vinculoFP);

            List<RecursoDoVinculoFP> lista = q.getResultList();

            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }

            return null;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<RecursoDoVinculoFP> recuperarRecursosDoVinculoByVinculo(VinculoFP vinculo, Date dataVigente) {

        String hql = "from RecursoDoVinculoFP re where re.vinculoFP.id = :vinculo and :data between re.inicioVigencia and coalesce(re.finalVigencia,:data)";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("vinculo", vinculo.getId());
        q.setParameter("data", dataVigente);

        return q.getResultList();
    }


    public List<RecursoDoVinculoFP> buscarRecursosDoVinculoPorRecurso(RecursoFP recursoFP, Date dataVigente) {

        String hql = "from RecursoDoVinculoFP re where re.recursoFP.id = :recurso and :data between re.inicioVigencia and coalesce(re.finalVigencia,:data)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("recurso", recursoFP.getId());
        q.setParameter("data", dataVigente);
        return q.getResultList();
    }

    public RecursoFP buscarRecursoDoVinculoPorVinculoAndReferencia(VinculoFP vinculoFP, Date dataVigente) {

        String hql = "select new RecursoFP(re.recursoFP.id, re.recursoFP.codigo, re.recursoFP.descricao) from RecursoDoVinculoFP re where re.vinculoFP.id = :vinculo and :data between re.inicioVigencia and coalesce(re.finalVigencia,:data)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("vinculo", vinculoFP.getId());
        q.setParameter("data", dataVigente);
        if (q.getResultList().isEmpty()) {
            RecursoDoVinculoFP recursoDoVinculoFP = recuperarRecursosDoVinculoByVinculoUltimoVigente(vinculoFP);
            return recursoDoVinculoFP != null ? recursoDoVinculoFP.getRecursoFP(): null;
        }
        return (RecursoFP) q.getResultList().get(0);
    }

    public RecursoFP buscarRecursosFPByVinculoUltimoVigente(VinculoFP vinculo) {
        String hql = "select new RecursoFP(re.recursoFP.id, re.recursoFP.codigo, re.recursoFP.descricao) from RecursoDoVinculoFP re where re.vinculoFP.id = :vinculo order by re.inicioVigencia desc, re.finalVigencia desc";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("vinculo", vinculo.getId());
        q.setMaxResults(1);

        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (RecursoFP) q.getResultList().get(0);
    }

    public RecursoDoVinculoFP recuperarRecursosDoVinculoByVinculoUltimoVigente(VinculoFP vinculo) {
        String hql = "from RecursoDoVinculoFP re where re.vinculoFP.id = :vinculo order by re.inicioVigencia desc, re.finalVigencia desc";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("vinculo", vinculo.getId());
        q.setMaxResults(1);

        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (RecursoDoVinculoFP) q.getResultList().get(0);
    }

    public RecursoDoVinculoFP recuperarRecursosDoVinculoByVinculoOrderByID(VinculoFP vinculo) {
        String hql = "from RecursoDoVinculoFP re where re.vinculoFP.id = :vinculo order by re.id desc";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("vinculo", vinculo.getId());
        q.setMaxResults(1);

        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (RecursoDoVinculoFP) resultList.get(0);
    }
}
