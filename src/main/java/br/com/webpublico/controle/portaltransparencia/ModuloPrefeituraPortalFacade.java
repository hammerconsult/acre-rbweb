package br.com.webpublico.controle.portaltransparencia;

import br.com.webpublico.controle.portaltransparencia.entidades.ModuloPrefeituraPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.PrefeituraPortal;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

@Stateless
public class ModuloPrefeituraPortalFacade extends AbstractFacade<ModuloPrefeituraPortal> implements Serializable {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public ModuloPrefeituraPortalFacade() {
        super(ModuloPrefeituraPortal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ModuloPrefeituraPortal> buscar(String parte, PrefeituraPortal prefeitura) {
        String sql = " select p.* from ModuloPrefeituraPortal p " + " where lower(p.modulo) like :parte ";
        if (prefeitura != null) {
            sql += " and p.prefeitura_id = :prefeitura";
        }
        Query q = em.createNativeQuery(sql, ModuloPrefeituraPortal.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        if (prefeitura != null) {
            q.setParameter("prefeitura", prefeitura.getId());
        }
        q.setMaxResults(10);
        return q.getResultList();
    }
}
