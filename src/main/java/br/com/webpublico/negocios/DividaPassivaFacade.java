/*
 * Codigo gerado automaticamente em Thu Jul 12 14:10:35 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DividaPassiva;
import br.com.webpublico.entidades.LancContabilManual;
import br.com.webpublico.entidades.ProvAtuarialMatematica;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class DividaPassivaFacade extends AbstractFacade<DividaPassiva> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFacade contratoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DividaPassivaFacade() {
        super(DividaPassiva.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public BigDecimal retornaUltimoNumeroLancamentoManual() {
        String sql = "SELECT CON.NUMERO FROM LANCCONTABILMANUAL CON "
                + "ORDER BY CON.NUMERO DESC";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        List<BigDecimal> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            return resultado.get(0);
        }
        return BigDecimal.ZERO;
    }

    public List<LancContabilManual> listaDivida() {
        String sql = " SELECT * "
                + " FROM LANCCONTABILMANUAL LANC"
                + " INNER JOIN DIVIDAPASSIVA div ON lanc.id = div.id"
                + " ORDER BY lanc.numero DESC";
        Query q = em.createNativeQuery(sql, DividaPassiva.class);
        return q.getResultList();
    }

    public LancContabilManual recuperaDividaPassiva(Object id) {
        DividaPassiva dp = em.find(DividaPassiva.class, id);
        return dp;
    }

    public List<LancContabilManual> listaTodasProvAtuariaisMatematicas() {
        String sql = "SELECT lcm.*, pam.tipopassivoatuarial_id FROM lanccontabilmanual lcm "
                + "INNER JOIN provatuarialmatematica pam ON lcm.id = pam.id";
        Query q = em.createNativeQuery(sql, ProvAtuarialMatematica.class);
        return q.getResultList();
    }
}
