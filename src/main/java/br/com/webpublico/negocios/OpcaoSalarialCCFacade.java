/*
 * Codigo gerado automaticamente em Wed Oct 05 15:42:34 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.OpcaoSalarialCC;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class OpcaoSalarialCCFacade extends AbstractFacade<OpcaoSalarialCC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OpcaoSalarialCCFacade() {
        super(OpcaoSalarialCC.class);
    }

    public String buscaNovoCodigo() {
        String sql = "SELECT max(cast(codigo AS INTEGER))+1 FROM OpcaoSalarialCC";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        return q.getSingleResult().toString();

    }

    public Boolean existeOpcaoSalarialCC(OpcaoSalarialCC opcao) {
        Query q = em.createQuery(" from OpcaoSalarialCC opcao where trim(opcao.codigo) = :codigo ");
        q.setParameter("codigo", opcao.getCodigo().trim());
        q.setMaxResults(1);

        List<OpcaoSalarialCC> lista = new ArrayList<OpcaoSalarialCC>();
        lista = q.getResultList();

        if (!lista.isEmpty()) {
            return !lista.contains(opcao);
        } else {
            return false;
        }
    }

    @Override
    public List<OpcaoSalarialCC> lista() {
        List<OpcaoSalarialCC> toReturn = new ArrayList<OpcaoSalarialCC>();
        Query q = em.createQuery("select opcao from OpcaoSalarialCC opcao");
        toReturn = q.getResultList();
        return toReturn;
    }

    public List<OpcaoSalarialCC> listaAtivos() {
        List<OpcaoSalarialCC> toReturn = new ArrayList<OpcaoSalarialCC>();
        Query q = em.createQuery("select opcao from OpcaoSalarialCC opcao where opcao.ativo = true order by codigo");
        toReturn = q.getResultList();
        return toReturn;
    }

    public OpcaoSalarialCC recuperarPorCodigo(String codigo) {
        List<OpcaoSalarialCC> toReturn = new ArrayList<OpcaoSalarialCC>();
        Query q = em.createQuery("select opcao from OpcaoSalarialCC opcao where opcao.codigo = :codigo ");
        q.setParameter("codigo", codigo);
        toReturn = q.getResultList();
        if (toReturn.isEmpty()) {
            return null;
        }
        return toReturn.get(0);
    }
}
