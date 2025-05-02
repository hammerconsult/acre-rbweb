package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AuxilioFuneral;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
@Stateless
public class AuxilioFuneralFacade extends AbstractFacade<AuxilioFuneral> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProcedenciaFacade procedenciaFacade;
    @EJB
    private FunerariaFacade funerariaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;

    public AuxilioFuneralFacade() {
        super(AuxilioFuneral.class);
    }

    @Override
    public AuxilioFuneral recuperar(Object id) {
        AuxilioFuneral auxilioFuneral = super.recuperar(id);
        inicializarDependencias(auxilioFuneral);
        return auxilioFuneral;
    }

    private void inicializarDependencias(AuxilioFuneral auxilioFuneral) {
        Hibernate.initialize(auxilioFuneral.getFamiliares());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProcedenciaFacade getProcedenciaFacade() {
        return procedenciaFacade;
    }

    public FunerariaFacade getFunerariaFacade() {
        return funerariaFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public AuxilioFuneral salvarRetornando(AuxilioFuneral selecionado) {
        selecionado = em.merge(selecionado);
        inicializarDependencias(selecionado);
        return selecionado;
    }
}
