package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MedicaoProvisaoPPA;
import br.com.webpublico.entidades.ProvisaoPPA;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MedicaoProvisaoPPAFacade extends AbstractFacade<MedicaoProvisaoPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public MedicaoProvisaoPPAFacade() {
        super(MedicaoProvisaoPPA.class);
    }

    public List<MedicaoProvisaoPPA> recuperarMedicoes (ProvisaoPPA prov){
        String sql = " select med.* from MEDICAOPROVISAOPPA med where med.PROVISAOPPA_ID = :idProv ";
        Query q = em.createNativeQuery(sql, MedicaoProvisaoPPA.class);
        q.setParameter("idProv", prov.getId());
        if (!q.getResultList().isEmpty()){
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
