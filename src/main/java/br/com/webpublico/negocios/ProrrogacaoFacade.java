package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.LancamentoTercoFeriasAut;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.administracaodepagamento.LancamentoTercoFeriasAutFacade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.Map;

/**
 * Created by Buzatto on 19/07/2016.
 */
@Stateless
public class ProrrogacaoFacade extends AbstractFacade<Prorrogacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private LancamentoTercoFeriasAutFacade lancamentoTercoFeriasAutFacade;

    public ProrrogacaoFacade() {
        super(Prorrogacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void salvarNovo(Prorrogacao entity, Map<PeriodoAquisitivoFL, LancamentoTercoFeriasAut> periodosParaExclusao, Boolean temFolhaEfetivadaTercoOuCargoSemBasePA) {
        VinculoFP vinculoFP = entity.getProvimentoFP().getVinculoFP();
        atribuirFinalVigenciaContratoFP(vinculoFP, entity.getProvimentoFP().getDataProvimento());
        validarDataProrrogadoAte(entity.getProrrogadoAte(), vinculoFP.getFinalVigencia());
        entity.getProvimentoFP().setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(vinculoFP));

        ContratoFP contrato = vinculoFP.getContratoFP();
        contratoFPFacade.processarHistoricoContratoFP(contrato);
        contrato.setFinalVigencia(entity.getProrrogadoAte());
        entity.getProvimentoFP().setVinculoFP(em.merge(contrato));

        super.salvarNovo(entity);
        if (!temFolhaEfetivadaTercoOuCargoSemBasePA) {
            corrigirPeriodosAquisitivos(periodosParaExclusao, (ContratoFP) entity.getProvimentoFP().getVinculoFP());
        }
    }

    public void salvar(Prorrogacao entity, Map<PeriodoAquisitivoFL, LancamentoTercoFeriasAut> periodosParaExclusao, Boolean temFolhaEfetivadaTercoOuCargoSemBasePA) {
        VinculoFP vinculoFP = entity.getProvimentoFP().getVinculoFP();
        VinculoFPHist historico = vinculoFP.getVinculoFPHist();
        if (historico != null && historico.getFinalVigencia() != null) {
            validarDataProrrogadoAte(entity.getProrrogadoAte(), historico.getFinalVigencia());
        } else {
            validarDataProrrogadoAte(entity.getProrrogadoAte(), entity.getProvimentoFP().getDataProvimento());
        }
        vinculoFP.setFinalVigencia(entity.getProrrogadoAte());
        entity.getProvimentoFP().setVinculoFP(em.merge(vinculoFP));
        super.salvar(entity);
        if (!temFolhaEfetivadaTercoOuCargoSemBasePA) {
            corrigirPeriodosAquisitivos(periodosParaExclusao, (ContratoFP) entity.getProvimentoFP().getVinculoFP());
        }
    }

    public void validarDataProrrogadoAte(Date prorrogadoAte, Date dataValidacao) {
        ValidacaoException ve = new ValidacaoException();
        if (prorrogadoAte.before(dataValidacao) || prorrogadoAte.equals(dataValidacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de prorrogação deve ser posterior a data <b>" + DataUtil.getDataFormatada(dataValidacao) + "</b>");
            throw ve;
        }
    }

    public void remover(Prorrogacao entity, Map<PeriodoAquisitivoFL, LancamentoTercoFeriasAut> periodosParaExclusao, Boolean temFolhaEfetivadaTercoOuCargoSemBasePA) {
        try {
            ContratoFP contratoFP = entity.getProvimentoFP().getVinculoFP().getContratoFP();
            VinculoFPHist historico = contratoFP.getVinculoFPHist();
            contratoFP.setFinalVigencia(historico.getFinalVigencia());
            contratoFP = em.merge(contratoFP);
            if (!temFolhaEfetivadaTercoOuCargoSemBasePA) {
                corrigirPeriodosAquisitivos(periodosParaExclusao, contratoFP);
            }
        } catch (Exception ex) {
            logger.error("Erro ao reverter o final de vigência pegando do histórico.{}", ex);
        }
        super.remover(entity);
    }

    private void corrigirPeriodosAquisitivos(Map<PeriodoAquisitivoFL, LancamentoTercoFeriasAut> periodosParaExclusao, ContratoFP contratoFP) {
        contratoFP = contratoFPFacade.recuperarSomentePeriodosAquisitivos(contratoFP.getId());
        for (Map.Entry<PeriodoAquisitivoFL, LancamentoTercoFeriasAut> mapPA : periodosParaExclusao.entrySet()) {
            PeriodoAquisitivoFL pa = mapPA.getKey();
            LancamentoTercoFeriasAut terco = mapPA.getValue();
            if (terco != null) {
                terco = lancamentoTercoFeriasAutFacade.recuperar(terco.getId());
                lancamentoTercoFeriasAutFacade.remover(terco);
            }
            pa = periodoAquisitivoFLFacade.recuperar(pa.getId());
            contratoFP.getPeriodosAquisitivosFLs().remove(pa);
            periodoAquisitivoFLFacade.remover(pa);
        }
        periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contratoFP, sistemaFacade.getDataOperacao(), null);
    }

    public void atribuirFinalVigenciaContratoFP(VinculoFP vinculoFP, Date dataProvimento) {
        if (!vinculoFP.temFinalVigencia()) {
            vinculoFP.setFinalVigencia(dataProvimento);
        }
    }
}
