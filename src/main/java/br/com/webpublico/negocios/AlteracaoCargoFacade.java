package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AlteracaoCargo;
import br.com.webpublico.entidades.ContratoFP;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 13/07/15
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AlteracaoCargoFacade extends AbstractFacade<AlteracaoCargo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private CBOFacade cboFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;

    public AlteracaoCargoFacade() {
        super(AlteracaoCargo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AlteracaoCargo recuperar(Object id) {
        AlteracaoCargo ac = super.recuperar(id);
        if (ac.getProvimentoFP() != null) {
            ac.getProvimentoFP().setVinculoFP(contratoFPFacade.recuperarParaTelaAlteracaoDeCargo(ac.getProvimentoFP().getVinculoFP().getContratoFP().getId()));
        }
        return ac;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public TipoProvimentoFacade getTipoProvimentoFacade() {
        return tipoProvimentoFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }

    public CBOFacade getCboFacade() {
        return cboFacade;
    }

    public ProvimentoFPFacade getProvimentoFPFacade() {
        return provimentoFPFacade;
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    public CategoriaPCSFacade getCategoriaPCSFacade() {
        return categoriaPCSFacade;
    }

    public ProgressaoPCSFacade getProgressaoPCSFacade() {
        return progressaoPCSFacade;
    }

    public EnquadramentoPCSFacade getEnquadramentoPCSFacade() {
        return enquadramentoPCSFacade;
    }

    public PlanoCargosSalariosFacade getPlanoCargosSalariosFacade() {
        return planoCargosSalariosFacade;
    }

    public void salvar(AlteracaoCargo selecionado, ContratoFP contrato) {
        if (selecionado.temCargo()) {
            contrato.setCargo(selecionado.getContratoFPCargo().getCargo());
            contrato.setCbo(selecionado.getContratoFPCargo().getCbo());
            selecionado.getProvimentoFP().setCargo(selecionado.getContratoFPCargo().getCargo());
        }
        selecionado.getProvimentoFP().setVinculoFP(em.merge(contrato));
        super.salvar(selecionado);
    }

    public void excluir(AlteracaoCargo selecionado, ContratoFP contrato) {
        contrato = em.merge(contrato);
        super.remover(selecionado);
    }
}
