/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author peixe
 */
@Stateless
public class AlteracaoLocalTrabalhoFacade extends AbstractFacade<ContratoFP> {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private HorarioDeTrabalhoFacade horarioDeTrabalhoFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;

    public AlteracaoLocalTrabalhoFacade() {
        super(ContratoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public LotacaoFuncionalFacade getLotacaoFuncionalFacade() {
        return lotacaoFuncionalFacade;
    }

    public HorarioDeTrabalhoFacade getHorarioDeTrabalhoFacade() {
        return horarioDeTrabalhoFacade;
    }

    public RecursoDoVinculoFPFacade getRecursoDoVinculoFPFacade() {
        return recursoDoVinculoFPFacade;
    }

    public RecursoFPFacade getRecursoFPFacade() {
        return recursoFPFacade;
    }

    public ProvimentoFPFacade getProvimentoFPFacade() {
        return provimentoFPFacade;
    }

    public TipoProvimentoFacade getTipoProvimentoFacade() {
        return tipoProvimentoFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public void salvarAlteracao(ContratoFP contratoFP, AtoLegal atoLegal, ProvimentoFP provimentoFP) {
        LotacaoFuncional lotacaoFuncionalAdicionada = getLotacaoFuncionalAdicionada(contratoFP);

        if (provimentoFP == null) {
            ProvimentoFP provimentoFPTransferencia = contratoFPFacade.adicionarProvimentoNoContrato(contratoFP, UtilRH.getDataOperacao(), atoLegal, TipoProvimento.TRANSFERENCIA);
            contratoFP = provimentoFPTransferencia.getVinculoFP().getContratoFP();

            if (lotacaoFuncionalAdicionada != null) {
                LotacaoFuncional ultimaLotacaoVigentePorVinculoFP = lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(contratoFP);
                for (LotacaoFuncional lotacaoFuncional : contratoFP.getLotacaoFuncionals()) {
                    if (lotacaoFuncional.equals(ultimaLotacaoVigentePorVinculoFP)) {
                        lotacaoFuncional.setProvimentoFP(provimentoFPTransferencia);
                    }
                }
            }
        }
        validarInformacaoPeq(contratoFP);
        em.merge(contratoFP);
    }

    //caso o servidor não se enquadra nos criterios de ter TIPO de PEQ dever setar null
    private void validarInformacaoPeq(ContratoFP selecionado){
        if(selecionado.getTipoPeq() != null){
            if(!isHabilitaTipoPEQ(selecionado)){
                selecionado.setTipoPeq(null);
            }
        }
    }

    private boolean isHabilitaTipoPEQ(ContratoFP selecionado) {
        boolean retorno = false;
        if (selecionado.getUnidadeOrganizacional() != null && selecionado.getUnidadeOrganizacional().getHabilitaTipoPeq()) {
            if (selecionado.getModalidadeContratoFP() != null && ModalidadeContratoFP.CONCURSADOS.equals(selecionado.getModalidadeContratoFP().getCodigo())) {
                retorno = true;
            }
        }
        return retorno;
    }

    public LotacaoFuncional getLotacaoFuncionalAdicionada(ContratoFP contratoFP) {
        for (LotacaoFuncional lotacaoFuncional : contratoFP.getLotacaoFuncionals()) {
            if (lotacaoFuncional.getId() == null) {
                return lotacaoFuncional;
            }
        }
        return null;
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFPAdicionado(ContratoFP contratoFP) {
        for (RecursoDoVinculoFP recursoDoVinculoFP : contratoFP.getRecursosDoVinculoFP()) {
            if (!recursoDoVinculoFP.temId()) {
                return recursoDoVinculoFP;
            }
        }
        return null;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
