package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PPRA;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by carlos on 11/08/15.
 */
@Stateless
public class PPRAFacade extends AbstractFacade<PPRA> {

    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private RiscoFacade riscoFacade;
    @EJB
    private FatorDeRiscoFacade fatorDeRiscoFacade;
    @EJB
    private EquipamentoEPCFacade equipamentoEPCFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public PPRAFacade(){
        super(PPRA.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(PPRA entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(PPRA entity) {
        super.salvarNovo(entity);
    }

    @Override
    public PPRA recuperar(Object id) {
        PPRA ppra = super.recuperar(id);
        if (ppra != null) {
            ppra.getIdentificacaoRiscos().size();
            ppra.getReconhecimentoRiscos().size();
            ppra.getAvaliacaoQuantitativaPPRAs().size();
            ppra.getMedidaDeControlePPRAs().size();
            ppra.getProgramaPPRAs().size();
        }
        return ppra;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public RiscoFacade getRiscoFacade() {
        return riscoFacade;
    }

    public FatorDeRiscoFacade getFatorDeRiscoFacade() {
        return fatorDeRiscoFacade;
    }

    public EquipamentoEPCFacade getEquipamentoEPCFacade() {
        return equipamentoEPCFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }
}
