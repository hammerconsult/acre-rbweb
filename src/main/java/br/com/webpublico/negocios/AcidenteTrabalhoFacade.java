package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AcidenteTrabalho;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * Created by israeleriston on 11/11/15.
 */
@Stateless
public class AcidenteTrabalhoFacade extends AbstractFacade<AcidenteTrabalho> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private CIDFacade cidFacade;
    @EJB
    private MedicoFacade medicoFacade;

    public AcidenteTrabalhoFacade() {
        super(AcidenteTrabalho.class);
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public CIDFacade getCidFacade() {
        return cidFacade;
    }

    public MedicoFacade getMedicoFacade() {
        return medicoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AcidenteTrabalho recuperar(Object id) {
        AcidenteTrabalho acidenteTrabalho = em.find(AcidenteTrabalho.class, id);
        return acidenteTrabalho;
    }

}
