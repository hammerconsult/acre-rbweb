package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametrosOtt;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author octavio
 */
@Stateless
public class ParametrosOttFacade extends AbstractFacade<ParametrosOtt> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private TributoFacade tributoFacade;

    public ParametrosOttFacade() {
        super(ParametrosOtt.class);
    }

    @Override
    public ParametrosOtt recuperar(Object id) {
        ParametrosOtt parametrosOtt = super.recuperar(id);
        if (parametrosOtt != null) {
            Hibernate.initialize(parametrosOtt.getDocumentosCredenciamento());
            Hibernate.initialize(parametrosOtt.getDocumentosCondutor());
            Hibernate.initialize(parametrosOtt.getDocumentosVeiculo());
            Hibernate.initialize(parametrosOtt.getCnaes());
            Hibernate.initialize(parametrosOtt.getVencimentos());
            if (parametrosOtt.getDetentorArquivoComposicao() != null
                && parametrosOtt.getDetentorArquivoComposicao().getArquivoComposicao() != null) {
                Hibernate.initialize(parametrosOtt.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes());
            }
        }
        return parametrosOtt;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public void setDividaFacade(DividaFacade dividaFacade) {
        this.dividaFacade = dividaFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public void setTributoFacade(TributoFacade tributoFacade) {
        this.tributoFacade = tributoFacade;
    }

    public Boolean isTemParametro() {
        Query q = em.createQuery(" from ParametrosOtt");

        return q.getResultList().isEmpty();
    }

    public ParametrosOtt retornarParametroOtt() {
        Query q = em.createQuery(" select parametro from ParametrosOtt parametro ");
        if (!q.getResultList().isEmpty()) {
            ParametrosOtt parametrosOtt = (ParametrosOtt) q.getResultList().get(0);
            return recuperar(parametrosOtt.getId());
        }
        return null;
    }
}
