package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.ItemReprocessamentoLancamentoTercoFeriasAutomatico;
import br.com.webpublico.entidades.rh.administracaodepagamento.LancamentoTercoFeriasAut;
import br.com.webpublico.entidades.rh.administracaodepagamento.ReprocessamentoLancamentoTercoFeriasAutomatico;
import br.com.webpublico.entidadesauxiliares.rh.LancamentoTercoFeriasAutVO;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.enums.rh.administracaopagamento.IdentificadorLancamentoTerco;
import br.com.webpublico.enums.rh.administracaopagamento.StatusReprocessamentoLancamentoTercoFeriasAutomatico;
import br.com.webpublico.enums.rh.administracaopagamento.TipoTercoFeriasAutomatico;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PeriodoAquisitivoFLFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.ZoneId;
import java.util.List;

@Stateless
public class ReprocessamentoLancamentoTercoFeriasAutomaticoFacade extends AbstractFacade<ReprocessamentoLancamentoTercoFeriasAutomatico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private ServiceLancamentoTercoFeriasAut serviceLancamentoTercoFeriasAut;
    @EJB
    private LancamentoTercoFeriasAutFacade lancamentoTercoFeriasAutFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;


    public ReprocessamentoLancamentoTercoFeriasAutomaticoFacade() {
        super(ReprocessamentoLancamentoTercoFeriasAutomatico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ReprocessamentoLancamentoTercoFeriasAutomatico recuperar(Object id) {
        ReprocessamentoLancamentoTercoFeriasAutomatico p = em.find(ReprocessamentoLancamentoTercoFeriasAutomatico.class, id);
        Hibernate.initialize(p.getItensReprocessamento());
        return p;
    }

    public void salvar(ReprocessamentoLancamentoTercoFeriasAutomatico entity, List<LancamentoTercoFeriasAutVO> lancamentos) {
        for (LancamentoTercoFeriasAutVO lancamento : lancamentos) {
            if (lancamento.getStatus() != null && lancamento.getStatus().getSeraIncluido()) {
                LancamentoTercoFeriasAut terco = new LancamentoTercoFeriasAut(lancamento.getPeriodoAquisitivoFL(), lancamento.getSaldoPeriodoAquisitivo(), lancamento.getMes(), lancamento.getAno(), lancamento.getContratoFP(), IdentificadorLancamentoTerco.REPROCESSADO);
                ItemReprocessamentoLancamentoTercoFeriasAutomatico item = new ItemReprocessamentoLancamentoTercoFeriasAutomatico(entity, terco);
                entity.getItensReprocessamento().add(item);
            }
        }
        super.salvar(entity);
    }

    public ServiceLancamentoTercoFeriasAut getServiceLancamentoTercoFeriasAut() {
        if (serviceLancamentoTercoFeriasAut == null) {
            serviceLancamentoTercoFeriasAut = (ServiceLancamentoTercoFeriasAut) Util.getSpringBeanPeloNome("serviceLancamentoTercoFeriasAut");
        }
        return serviceLancamentoTercoFeriasAut;
    }

    public List<LancamentoTercoFeriasAutVO> buscarServidoresParaLancamentoTerco(ReprocessamentoLancamentoTercoFeriasAutomatico reprocessamento, ContratoFP contratoFPFiltro, HierarquiaOrganizacional hierarquiaOrganizacional) {
        List<LancamentoTercoFeriasAutVO> lancamentos = Lists.newArrayList();
        if (TipoTercoFeriasAutomatico.FINAL_PERIODO_AQUISITIVO.equals(reprocessamento.getCriterioBusca())) {
            for (PeriodoAquisitivoFL periodo : getServiceLancamentoTercoFeriasAut().recuperaPeriodoAquisitivoPorMesAno(DataUtil.primeiroDiaMes(reprocessamento.getMesVerificacao(), reprocessamento.getAnoVerificacao()), Boolean.FALSE, contratoFPFiltro, hierarquiaOrganizacional)) {
                int saldoDias = periodo.getSaldo() != null ? periodo.getSaldo() : 0;
                LancamentoTercoFeriasAut lancamentoTercoFeriasAut = lancamentoTercoFeriasAutFacade.recuperaLancamentoTercoFeriasAutPorPeriodoAquisitivo(periodo);
                if (lancamentoTercoFeriasAut != null) {
                    lancamentos.add(new LancamentoTercoFeriasAutVO(saldoDias, reprocessamento.getMesLancamento(), reprocessamento.getAnoLancamento(), periodo.getContratoFP(), periodo, StatusReprocessamentoLancamentoTercoFeriasAutomatico.LANCAMENTO_JA_REALIZADO));
                } else {
                    for (ConcessaoFeriasLicenca concessao : getServiceLancamentoTercoFeriasAut().recuperaConsessaoParaPeriodoAquisitivo(periodo)) {
                        if (reprocessamento.getAnoVerificacao() < concessao.getAno() || (reprocessamento.getMesVerificacao() <= concessao.getMes() && reprocessamento.getAnoVerificacao() == concessao.getAno())) {
                            java.time.LocalDate dataInicial = java.time.Instant.ofEpochMilli(concessao.getInicioVigencia().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                            java.time.LocalDate dataFinal = java.time.Instant.ofEpochMilli(concessao.getFimVigencia().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                            saldoDias = saldoDias + DataUtil.diasEntre(dataInicial, dataFinal);
                        }
                    }
                    if (saldoDias > 0) {
                        lancamentos.add(new LancamentoTercoFeriasAutVO(saldoDias, reprocessamento.getMesLancamento(), reprocessamento.getAnoLancamento(), periodo.getContratoFP(), periodo, StatusReprocessamentoLancamentoTercoFeriasAutomatico.PRONTO_INCLUSAO));
                    } else {
                        lancamentos.add(new LancamentoTercoFeriasAutVO(saldoDias, reprocessamento.getMesLancamento(), reprocessamento.getAnoLancamento(), periodo.getContratoFP(), periodo, StatusReprocessamentoLancamentoTercoFeriasAutomatico.CONCEDIDO));
                    }
                }
            }
        } else if (TipoTercoFeriasAutomatico.ANIVERSARIO_CONTRATO.equals(reprocessamento.getCriterioBusca())) {
            List<ContratoFP> contratos = Lists.newArrayList();
            if (contratoFPFiltro != null) {
                contratos.add(contratoFPFiltro);
            } else {
                List<ContratoFP> contratosRecuperados = getServiceLancamentoTercoFeriasAut().buscarContratosParaPreenchimentoPorAniversarioContrato(DataUtil.dateToLocalDate(DataUtil.primeiroDiaMes(reprocessamento.getMesVerificacao(), reprocessamento.getAnoVerificacao())),hierarquiaOrganizacional);
                if (!contratosRecuperados.isEmpty()) {
                    contratos.addAll(contratosRecuperados);
                }
            }

            for (ContratoFP contratoFP : contratos) {
                BasePeriodoAquisitivo basePA = getServiceLancamentoTercoFeriasAut().buscarBasePeriodoAquisitivoPorContrato(contratoFP, DataUtil.primeiroDiaMes(reprocessamento.getMesVerificacao(), reprocessamento.getAnoVerificacao()));
                Integer saldo = basePA != null ? basePA.getDiasDeDireito() : 0;
                StatusReprocessamentoLancamentoTercoFeriasAutomatico status = buscarStatusReprocessamentoTercoFeriasAutoParaAniversarioContrato(reprocessamento, contratoFP);
                lancamentos.add(new LancamentoTercoFeriasAutVO(saldo, reprocessamento.getMesLancamento(), reprocessamento.getAnoLancamento(), contratoFP, null, status));
            }
        }
        return lancamentos;
    }

    private StatusReprocessamentoLancamentoTercoFeriasAutomatico buscarStatusReprocessamentoTercoFeriasAutoParaAniversarioContrato(ReprocessamentoLancamentoTercoFeriasAutomatico reprocessamento, ContratoFP contratoFP) {
        StatusReprocessamentoLancamentoTercoFeriasAutomatico status = StatusReprocessamentoLancamentoTercoFeriasAutomatico.PRONTO_INCLUSAO;
        LancamentoTercoFeriasAut lancamento = lancamentoTercoFeriasAutFacade.recuperaLancamentosTercoFeriasAutPorContratoMesAno(contratoFP, reprocessamento.getMesLancamento(), reprocessamento.getAnoLancamento());
        PeriodoAquisitivoFL periodo = periodoAquisitivoFLFacade.buscarPeriodoAquisitivoVigentePorContratoETipo(contratoFP, DataUtil.primeiroDiaMes(reprocessamento.getMesVerificacao(), reprocessamento.getAnoVerificacao()), TipoPeriodoAquisitivo.FERIAS);
        if (lancamento != null) {
            status = StatusReprocessamentoLancamentoTercoFeriasAutomatico.LANCAMENTO_EXISTENTE_NA_COMPETENCIA;
        } else if (periodo != null) {
            LancamentoTercoFeriasAut lancamentoTercoFeriasAut = lancamentoTercoFeriasAutFacade.recuperaLancamentoTercoFeriasAutPorPeriodoAquisitivo(periodo);
            if (lancamentoTercoFeriasAut != null) {
                if (lancamentoTercoFeriasAut.getAno().equals(reprocessamento.getAnoLancamento()) && lancamentoTercoFeriasAut.getMes().equals(reprocessamento.getMesLancamento())) {
                    status = StatusReprocessamentoLancamentoTercoFeriasAutomatico.PA_VIGENTE_COM_LANCAMENTO_NA_COMPETENCIA;
                } else {
                    status = StatusReprocessamentoLancamentoTercoFeriasAutomatico.PA_VIGENTE_COM_LANCAMENTO;
                }
            } else {
                int saldoDias = periodo.getSaldo() != null ? periodo.getSaldo() : 0;
                for (ConcessaoFeriasLicenca concessao : getServiceLancamentoTercoFeriasAut().recuperaConsessaoParaPeriodoAquisitivo(periodo)) {
                    if (reprocessamento.getAnoVerificacao() < concessao.getAno() || (reprocessamento.getMesVerificacao() <= concessao.getMes() && reprocessamento.getAnoVerificacao() == concessao.getAno())) {
                        java.time.LocalDate dataInicial = java.time.Instant.ofEpochMilli(concessao.getInicioVigencia().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                        java.time.LocalDate dataFinal = java.time.Instant.ofEpochMilli(concessao.getFimVigencia().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                        saldoDias = saldoDias + DataUtil.diasEntre(dataInicial, dataFinal);
                    }
                }
                if (saldoDias <= 0) {
                    status = StatusReprocessamentoLancamentoTercoFeriasAutomatico.PA_VIGENTE_CONCEDIDO;
                }
            }
        }
        return status;
    }

    public ItemReprocessamentoLancamentoTercoFeriasAutomatico recuperaItemReprocessamentoPorLancamentoTercoFerias(LancamentoTercoFeriasAut lancamento) {
        Query q = em.createQuery("from ItemReprocessamentoLancamentoTercoFeriasAutomatico item " +
            " where item.lancamentoTercoFeriasAut.id = :lancamento ");
        q.setParameter("lancamento", lancamento.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (ItemReprocessamentoLancamentoTercoFeriasAutomatico) resultList.get(0);
        }
        return null;
    }
}
