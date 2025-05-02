/*
 * Codigo gerado automaticamente em Thu Mar 31 17:13:44 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Indicador;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class IndicadorFacade extends AbstractFacade<Indicador> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;

    public IndicadorFacade() {
        super(Indicador.class);
    }

    public List<Indicador> listaFiltrandoIndicador(String parte) {
        String sql = "SELECT I.* FROM INDICADOR I WHERE (LOWER(I.NOME) LIKE :parte)";
        Query q = em.createNativeQuery(sql, Indicador.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    @Override
    public Indicador recuperar(Object id) {
        Indicador i = em.find(Indicador.class, id);
        i.getValorIndicadores().size();
        return i;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public UnidadeMedidaFacade getUnidadeMedidaFacade() {
        return unidadeMedidaFacade;
    }
}
