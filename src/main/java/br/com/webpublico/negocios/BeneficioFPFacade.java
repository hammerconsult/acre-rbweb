/*
 * Codigo gerado automaticamente em Wed Aug 24 09:28:31 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BeneficioFP;
import br.com.webpublico.entidades.MatriculaFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class BeneficioFPFacade extends AbstractFacade<BeneficioFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BeneficioFPFacade() {
        super(BeneficioFP.class);
    }

    public String retornaCodigo(MatriculaFP matricula) {
        String num;
        String sql = " SELECT max(cast(COALESCE(b.numero,'0') AS INTEGER)) "
                + " FROM VinculoFP b WHERE b.matriculaFP_id = :matricula ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("matricula", matricula.getId());
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            Long l = 1l;
            if (q.getSingleResult() != null) {
                l += Long.parseLong(q.getSingleResult().toString());
            }

            return String.valueOf(l);
        }

        return "1";


        //        String num;
//        String hql = "select a from VinculoFP a "
//                + "where a.numero = (select max(b.numero) from VinculoFP b where b.matriculaFP = :matricula)";
//        Query query = em.createQuery(hql);
//        query.setMaxResults(1);
//        query.setParameter("matricula", matricula);
//        if (!query.getResultList().isEmpty()) {
//            Long l = Long.parseLong(((BeneficioFP) query.getSingleResult()).getNumero()) + 1;
//            num = String.valueOf(l);
//        } else {
//            return "1";
//        }
//        return num;
    }

    public List<BeneficioFP> listaFiltrandoBeneficioFP(String s, String... atributos) {
        String hql = " select bfp from BeneficioFP bfp "
                + "inner join bfp.unidadeOrganizacional uo "
                + "inner join bfp.matriculaFP mfp          "
                + "inner join bfp.matriculaFP.pessoa p     "
                + "inner join bfp.matriculaFP.unidadeMatriculado um "
                + "where (lower(uo.descricao) like :parametro "
                + "or lower(mfp.matricula) like :parametro "
                + "or lower(p.nome) like :parametro "
                + "or lower(um.descricao) like :parametro) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);

        return q.getResultList();
    }

    @Override
    public BeneficioFP recuperar(Object id) {
        BeneficioFP bfp = em.find(BeneficioFP.class, id);
        bfp.getValoresBeneficiosFPs().size();
        return bfp;
    }

    public boolean verificaSeExite(String numero, MatriculaFP matricula) {
        Query q = em.createQuery("from BeneficioFP b where b.numero=:numero and b.matriculaFP = :matricula");
        q.setParameter("numero", numero);
        q.setParameter("matricula", matricula);
        return q.getResultList().isEmpty();
    }

    /**
     * Método utilizando para editar, casa o codigo ja esteja em utilização não salva.
     *
     * @param beneficio
     * @return
     */
    public boolean verificaCodigoEditar(BeneficioFP beneficio) {
        Query q = em.createQuery("from BeneficioFP e where (e.numero = :numero and e = :beneficio)");
        q.setParameter("beneficio", beneficio);
        q.setParameter("numero", beneficio.getNumero());
        return !q.getResultList().isEmpty();
    }
}
