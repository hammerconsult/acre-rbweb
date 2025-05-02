package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BeneficiarioEmenda;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/06/15
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class BeneficiarioEmendaFacade extends AbstractFacade<BeneficiarioEmenda> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BeneficiarioEmendaFacade() {
        super(BeneficiarioEmenda.class);
    }

    @Override
    public BeneficiarioEmenda recuperar(Object id) {
        BeneficiarioEmenda b = em.find(BeneficiarioEmenda.class, id);
        b.getResponsavelBeneficiarioEmendas().size();
        return b;
    }

    public List<BeneficiarioEmenda> buscarBeneficiarioEmenda(String parte) {
        String sql = " select b.* from beneficiarioemenda b " +
            "           inner join pessoa p on p.id = b.pessoa_id " +
            "           inner join pessoajuridica pj on pj.id = p.id " +
            "               where (pj.cnpj like :parte or lower(pj.razaosocial) like :parte) ";
        Query q = em.createNativeQuery(sql, BeneficiarioEmenda.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }
}
