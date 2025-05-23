/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.PeriodoAquisitivoExcluido;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.portal.ProgramacaoFeriasPortal;
import br.com.webpublico.entidades.rh.portal.RHProgramacaoFeriasPortal;
import br.com.webpublico.entidadesauxiliares.ReferenciaFPFuncao;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.exception.rh.SemBasePeriodoAquisitivoException;
import br.com.webpublico.exception.rh.SemConfiguracaoDeFaltaInjustificada;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.portal.ProgramacaoFeriasPortalFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.ws.model.WSConcessaoFerias;
import br.com.webpublico.ws.model.WSPeriodoAquisitivo;
import br.com.webpublico.ws.model.WSProgramacaoFerias;
import br.com.webpublico.ws.model.WSVinculoFP;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Interval;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author peixe
 */
@Stateless
public class PeriodoAquisitivoFLFacade extends AbstractFacade<PeriodoAquisitivoFL> {

    private static final int SEIS_MESES = 180; //TODO trocar para ler a configuracao de ConfiguracaoRH
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacadeOLD;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private BaseCargoFacade baseCargoFacade;
    @EJB
    private PenalidadeFPFacade penalidadeFPFacade;
    @EJB
    private BasePeriodoAquisitivoFacade basePeriodoAquisitivoFacade;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private SugestaoFeriasFacade sugestaoFeriasFacade;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private ConfiguracaoFaltasInjustificadasFacade configuracaoFaltasInjustificadasFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ProgramacaoFeriasPortalFacade programacaoFeriasPortalFacade;
    @EJB
    private ParametroLicencaPremioFacade parametroLicencaPremioFacade;
    @EJB
    private ReintegracaoFacade reintegracaoFacade;

    public PeriodoAquisitivoFLFacade() {
        super(PeriodoAquisitivoFL.class);
    }

    public ConfiguracaoRHFacade getConfiguracaoRHFacade() {
        return configuracaoRHFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConcessaoFeriasLicencaFacade getConcessaoFeriasLicencaFacade() {
        return concessaoFeriasLicencaFacade;
    }

    @Override
    public PeriodoAquisitivoFL recuperar(Object id) {
        PeriodoAquisitivoFL periodo = super.recuperar(id);
        if (periodo.temSugestaoFerias()) {
            periodo.setSugestaoFerias(sugestaoFeriasFacade.recuperar(periodo.getSugestaoFerias().getId()));
        }
        return periodo;
    }

    @Override
    public void remover(PeriodoAquisitivoFL entity) {
        if (entity.temSugestaoFerias()) {
            SugestaoFerias sf = sugestaoFeriasFacade.recuperar(entity.getSugestaoFerias().getId());
            sugestaoFeriasFacade.remover(sf);
        } else {
            super.remover(entity);
        }
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodosPorContratoTipoLicencaPremio(ContratoFP contrato) {
        Query q = em.createQuery("select p from PeriodoAquisitivoFL p "
            + "inner join p.baseCargo base "
            + "inner join base.basePeriodoAquisitivo bpa "
            + "where p.contratoFP = :contrato and bpa.tipoPeriodoAquisitivo = :tipo "
            + "order by p.inicioVigencia");
        q.setParameter("contrato", contrato);
        q.setParameter("tipo", TipoPeriodoAquisitivo.LICENCA);
        return q.getResultList();
    }

    @Deprecated
    //modificado para pegar mes e ano, visto que o na geração estava duplicando registro.
    //fax variação de 2 dias mais e 2 dias a menos por causa da migração.
    public Boolean existePeriodoAquisitivoNovo(PeriodoAquisitivoFL periodo) {
        Query q = em.createQuery(" from PeriodoAquisitivoFL periodo where " +
            "(periodo.inicioVigencia = :inicio or (periodo.inicioVigencia) = (:inicio+1) or (periodo.inicioVigencia) = (:inicio+2) or (periodo.inicioVigencia) = (:inicio-1) or (periodo.inicioVigencia) = (:inicio-2) ) " +
            " and (periodo.finalVigencia = :final or (periodo.finalVigencia) = (:final+1) or (periodo.finalVigencia) = (:final+2) or (periodo.finalVigencia) = (:final-1) or (periodo.finalVigencia) = (:final-2))" +
            "and periodo.baseCargo = :baseCargo "
            + " and periodo.contratoFP = :contratoFP ");

        q.setParameter("contratoFP", periodo.getContratoFP());
        q.setParameter("baseCargo", periodo.getBaseCargo());
        q.setParameter("final", periodo.getFinalVigencia());
        q.setParameter("inicio", periodo.getInicioVigencia());

        return !q.getResultList().isEmpty();
    }

    /**
     * Método adiciona 10 dias no inicio da vigencia para verificar se tem algum periodo entre a data.
     *
     * @param periodo
     * @return
     */
    public Boolean existePeriodoAquisitivoNovoByData(PeriodoAquisitivoFL periodo) {
        Query q = em.createQuery(" from PeriodoAquisitivoFL periodo where (" +
            " :inicio between  periodo.inicioVigencia and periodo.finalVigencia " +
            " and :fim between periodo.inicioVigencia and periodo.finalVigencia)" +
            "and periodo.baseCargo = :baseCargo "
            + " and periodo.contratoFP = :contratoFP ");

        DateTime dt = new DateTime(periodo.getInicioVigencia());
        dt = dt.plusDays(10);
        q.setParameter("contratoFP", periodo.getContratoFP());
        q.setParameter("baseCargo", periodo.getBaseCargo());
        q.setParameter("inicio", dt.toDate());
        q.setParameter("fim", periodo.getFinalVigencia());

        return !q.getResultList().isEmpty();
    }

    //Criado pq havia diferença nos dados migrados, gerando assim registros no mesmo periodo aquisitivo
    public Boolean existePeriodoAquisitivoParcial(PeriodoAquisitivoFL periodo) {
        Query q = em.createQuery(" from PeriodoAquisitivoFL periodo where ((:inicio between periodo.inicioVigencia and periodo.finalVigencia) "
            + " or :final between periodo.inicioVigencia and periodo.finalVigencia) and periodo.baseCargo = :baseCargo "
            + " and periodo.contratoFP = :contratoFP ");

        q.setParameter("contratoFP", periodo.getContratoFP());
        q.setParameter("baseCargo", periodo.getBaseCargo());
        q.setParameter("final", periodo.getFinalVigencia());
        q.setParameter("inicio", periodo.getInicioVigencia());

        return !q.getResultList().isEmpty();
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodoAquisitivoPorHierarquiaOrganizacional(HierarquiaOrganizacional hierarquia) {
        List<PeriodoAquisitivoFL> periodoAquisitivoFLs = new ArrayList<>();

        Query q = em.createQuery(" select distinct periodo from PeriodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contrato "
            + " where contrato in ("
            + " select contratoLotacao from LotacaoFuncional lotacao "
            + " inner join lotacao.vinculoFP contratoLotacao "
            + " where :dataAtual >= lotacao.inicioVigencia "
            + " and :dataAtual <= coalesce(lotacao.finalVigencia,:dataAtual) "
            + " and lotacao.unidadeOrganizacional in (:unidades)) "
            + " and coalesce(periodo.saldoDeDireito,0) > 0 "
            + " and periodo.inicioVigencia <= :dataAtual and (:dataAtual >= contrato.inicioVigencia "
            + " and :dataAtual <= coalesce(contrato.finalVigencia,:dataAtual))");
        q.setParameter("dataAtual", Util.getDataHoraMinutoSegundoZerado(new Date()));

        List<HierarquiaOrganizacional> listaHierarquia = new ArrayList<>();
        q.setParameter("unidades", hierarquiaOrganizacionalFacadeOLD.recuperaUnidadesOrganizacionaisFilhas(listaHierarquia, hierarquia));
        return q.getResultList();
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodoAquisitivoPorContratoFP(ContratoFP contrato) {
        Query q = em.createQuery(" select distinct periodo from PeriodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contrato "
//                + " where coalesce(periodo.saldoDeDireito,0) > 0 "
            + " where periodo.inicioVigencia <= :dataAtual "
            + " and contrato = :parametroContrato and :dataAtual >= contrato.inicioVigencia "
            + " and :dataAtual <= coalesce(contrato.finalVigencia,:dataAtual)"
            + "order by periodo.inicioVigencia");
        q.setParameter("parametroContrato", contrato);
        q.setParameter("dataAtual", Util.getDataHoraMinutoSegundoZerado(new Date()));
        return q.getResultList();
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodoAquisitivoPorContratoFPPorTipoPeriodoAquisitivo(ContratoFP contrato, TipoPeriodoAquisitivo tipo) {
        Query q = em.createQuery(" select distinct periodo from PeriodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contrato inner join periodo.baseCargo baseCargo " +
            " inner join baseCargo.basePeriodoAquisitivo base"
            + " where base.tipoPeriodoAquisitivo = :tipo and periodo.inicioVigencia <= :dataAtual "
            + " and contrato = :parametroContrato and :dataAtual >= contrato.inicioVigencia "
            + " and :dataAtual <= coalesce(contrato.finalVigencia,:dataAtual)"
            + "order by periodo.inicioVigencia desc");
        q.setParameter("parametroContrato", contrato);
        q.setParameter("tipo", tipo);
        q.setParameter("dataAtual", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }


    public List<PeriodoAquisitivoFL> recuperaPeriodoAquisitivoPorHierarquiaOrganizacionalEContratoFP(HierarquiaOrganizacional hierarquia, ContratoFP contratoFP) {
        Query q = em.createQuery(" select distinct periodo from PeriodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contrato "
            + " where contrato = :parametroContrato and contrato in ("
            + " select contratoLotacao from LotacaoFuncional lotacao "
            + " inner join lotacao.vinculoFP contratoLotacao "
            + " where :dataAtual >= lotacao.inicioVigencia "
            + " and :dataAtual <= coalesce(lotacao.finalVigencia,:dataAtual) "
            + " and lotacao.unidadeOrganizacional in (:unidades)) "
            + " and coalesce(periodo.saldoDeDireito,0) > 0 "
            + " and periodo.inicioVigencia <= :dataAtual and (:dataAtual >= contrato.inicioVigencia "
            + " and :dataAtual <= coalesce(contrato.finalVigencia,:dataAtual))");
        q.setParameter("dataAtual", Util.getDataHoraMinutoSegundoZerado(new Date()));
        List<HierarquiaOrganizacional> listaHierarquia = new ArrayList<>();
        q.setParameter("unidades", hierarquiaOrganizacionalFacadeOLD.recuperaUnidadesOrganizacionaisFilhas(listaHierarquia, hierarquia));
        q.setParameter("parametroContrato", contratoFP);
        return q.getResultList();
    }

    private Calendar getValidadeDoPeriodoAquisitivo(Date dataBase, BasePeriodoAquisitivo bpa) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataBase);
        c.add(Calendar.MONTH, bpa.getDuracao());
        c.add(Calendar.DAY_OF_YEAR, -1);
        return c;
    }

    public Date adicionarDiasDaBaseNaDataInicial(Date dataInicial, BasePeriodoAquisitivo basePeriodoAquisitivo) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataInicial);
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) + basePeriodoAquisitivo.getDuracao());
        dataInicial = c.getTime();
        return dataInicial;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public ContratoFP gerarPeriodosAquisitivos(ContratoFP contratoFP, Date dataOperacao, TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        TipoPeriodoAquisitivo[] tipos = tipoPeriodoAquisitivo != null ? new TipoPeriodoAquisitivo[]{tipoPeriodoAquisitivo} : TipoPeriodoAquisitivo.values();
        contratoFP = contratoFPFacade.recuperarSomentePeriodosAquisitivos(contratoFP.getId());
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente(dataOperacao);
        List<ContratoFP> todosContratos = Lists.newArrayList();
        Integer diferencaDiasContrato = null;
        if (configuracaoRH != null) {
            diferencaDiasContrato = configuracaoRH.getDiferencaDiasContratoPA();
        }
        if (diferencaDiasContrato != null) {
            todosContratos = contratoFPFacade.recuperaTodosContratosPorMatriculaFP(contratoFP.getMatriculaFP());
        }
        deletarPeriodosInconsistentes(contratoFP);
        contratoFP.setPeriodosAquisitivosFLs(contratoFP.getPeriodosAquisitivosFLs() == null ? new ArrayList<PeriodoAquisitivoFL>() : contratoFP.getPeriodosAquisitivosFLs());
        Date dataCorrente = contratoFP.getFinalVigencia() != null ? contratoFP.getFinalVigencia() : dataOperacao;
        dataCorrente = dataCorrente.compareTo(dataOperacao) > 0 ? dataOperacao : dataCorrente;
        dataCorrente = new DateTime(dataCorrente).plusMonths(6).toDate();
        verificarBasesPeriodoAquisitivoVinculadasWithCargoByContratoFP(contratoFP, dataOperacao);

        logger.debug(" data corrente {} ", dataCorrente);
        Reintegracao reintegracao = reintegracaoFacade.recuperarUltimaReintegracao(contratoFP);

        for (TipoPeriodoAquisitivo tipo : tipos) {
            logger.debug(" tipo periodo {} ", tipo);
            Date dataInicial = buscarFinalVigenciaUltimoPeriodoAquisitivoByTipo(contratoFP, tipo);

            dataInicial = getDataInicialGeracaoPeriodoAquisitivo(contratoFP, diferencaDiasContrato, todosContratos, reintegracao, dataInicial, tipo);

            logger.debug(" data inicial {} ", dataInicial);
            if (validarServidorTemDireitoFeriasAndLicencaPremio(tipo, contratoFP)) {
                continue;
            }
            if (validarServidorComFinalDeVigenciaAndPeriodoAcimaDoFinalVigencia(contratoFP, tipo, dataInicial)) {
                continue;
            }
            while (dataInicial.before(dataCorrente)) {
                BaseCargo baseCargo = baseCargoFacade.buscarBaseCargosPorTipoPeriodoAquisitivoAndCargoAndDataReferencia(tipo, contratoFP.getCargo(), dataInicial);
                logger.debug(" base cargo {} ", baseCargo);
                if (baseCargo == null) {
                    break;
                }

                BasePeriodoAquisitivo basePeriodoAquisitivo = selecionarBasePeriodoAquisitivo(contratoFP, baseCargo, tipo);
                logger.debug(" base cargo {} data {}", basePeriodoAquisitivo, dataInicial);
                Date fimDoPeriodo = getValidadeDoPeriodoAquisitivo(dataInicial, basePeriodoAquisitivo).getTime();
                logger.debug("fim do periodo {}", fimDoPeriodo);

                CalculoPeriodoAquisitivo periodoExcludente = checarPeriodosExcludentes(basePeriodoAquisitivo, contratoFP.getCargo(), new CalculoPeriodoAquisitivo(contratoFP, tipo, dataInicial, fimDoPeriodo));

                CalculoPeriodoAquisitivo calculo = checarAfastamentosNoPeriodoAquisitivo(new CalculoPeriodoAquisitivo(contratoFP, tipo, dataInicial, fimDoPeriodo), dataOperacao);

                if (periodoExcludente.isPerdePeriodoReiniciandoContagem()) {
                    dataInicial = baseCargo.getCargo().getPeriodoAquisitivoExcluido().getFinalVigenciaJoda().toDate();/*.plusDays(1).toDate()*/
                    continue;
                }

                if (calculo.isPerdePeriodoReiniciandoContagem()) {
                    dataInicial = calculo.getFinalVigenciaJoda().plusDays(1).toDate();
                    continue;
                }

                if (calculo.alterarDataFimDoPeriodoAquisitivo) {
                    fimDoPeriodo = calculo.getFinalVigencia();
                }

                Integer duracao = DataUtil.diferencaDiasInteira(dataInicial, fimDoPeriodo);
                logger.debug("fim do periodo reajustado {}", fimDoPeriodo);
                PeriodoAquisitivoFL periodo = new PeriodoAquisitivoFL(duracao, baseCargo, dataInicial, fimDoPeriodo, contratoFP, basePeriodoAquisitivo.getDiasDeDireito());

                if (calculo.isPeriodoAquisitivoCancelado()) {
                    periodo.setStatus(StatusPeriodoAquisitivo.SEM_DIREITO);
                    contratoFP.setPeriodosAquisitivosFLs(Util.adicionarObjetoEmLista(contratoFP.getPeriodosAquisitivosFLs(), periodo));
                }

                periodo.setQuantidadeDias(periodo.getQuantidadeDias() + 1);

                if (!jaPossuiPeriodoLancado(contratoFP.getPeriodosAquisitivosFLs(), periodo)) {
                    logger.debug("Já possui periodo... {}", periodo);
                    if (temPeriodosAquisitivosMaisRecentes(periodo)) {
                        periodo.setStatus(StatusPeriodoAquisitivo.NAO_CONCEDIDO);
                        periodo.setSaldo(basePeriodoAquisitivo.getDiasDeDireito());
                        periodo.setObservacao("Período Aquisitivo Antigo");
                    }
                    periodo = atribuirDiasDeDireitoDoServidor(periodo, dataOperacao);
                    contratoFP.setPeriodosAquisitivosFLs(Util.adicionarObjetoEmLista(contratoFP.getPeriodosAquisitivosFLs(), periodo));
                }

                if (calculo.alterarDataFimDoPeriodoAquisitivo) {
                    dataInicial = DataUtil.adicionaDias(calculo.getFinalVigencia(), 1);
                } else {
                    dataInicial = adicionarDiasDaBaseNaDataInicial(dataInicial, basePeriodoAquisitivo);
                }


                if (isRescisaoAndUltimoPeriodo(contratoFP, dataInicial, dataCorrente)) {
                    excluirPeriodosAquisitivosSemConcessao(contratoFP, tipo, contratoFP.getFinalVigencia() != null ? contratoFP.getFinalVigencia() : dataInicial, periodo);
                    Integer ultimaDuracao = DataUtil.diferencaDiasInteira(periodo.getInicioVigencia(), fimDoPeriodo);
                    periodo.setFinalVigencia(contratoFP.getFinalVigencia());

                    PeriodoAquisitivoFL ultimoPeriodo = periodo;

                    Integer diasSaldo = quantidadeSaldoProporcialUltimoPeriodo(basePeriodoAquisitivo, (ultimaDuracao / Double.parseDouble(basePeriodoAquisitivo.getDiasDeDireito() + "")));

                    ultimoPeriodo.setQuantidadeDias(periodo.getQuantidadeDias() + 1);
                    ultimoPeriodo.setSaldoDeDireito(diasSaldo);

                    ultimoPeriodo = atribuirDiasDeDireitoDoServidor(periodo, dataOperacao);

                    adicionarNoContrato(contratoFP, ultimoPeriodo);
                    dataInicial = adicionarDiasDaBaseNaDataInicial(dataInicial, basePeriodoAquisitivo);
                }

            }
        }

        em.merge(contratoFP);
        return contratoFP;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void deletarPeriodosInconsistentes(ContratoFP contratoFP) {
        buscarPeriodosAquisitivosInconsistentes(contratoFP);
    }

    private Date getDataInicialGeracaoPeriodoAquisitivo(ContratoFP contratoFP, Integer diferencaDiasContrato, List<ContratoFP> todosContratos,
                                                        Reintegracao reintegracao, Date dataInicial, TipoPeriodoAquisitivo tipo) {
        //~data inicial da reintegração
        if (reintegracao != null) {
            if (dataInicial == null) {
                dataInicial = reintegracao.getDataReintegracao();
            } else if (reintegracao.getDataReintegracao().after(dataInicial)) {
                dataInicial = reintegracao.getDataReintegracao();
            }
        } else {
            //data inicial do contrato anterior(exonerado)
            if (diferencaDiasContrato != null && todosContratos.size() > 1) {
                Integer diferencaDias = null;
                for (ContratoFP contratoRecuperado : todosContratos) {
                    if (contratoRecuperado.getFinalVigencia() != null) {
                        diferencaDias = DataUtil.diferencaDiasInteira(contratoRecuperado.getFinalVigencia(), contratoFP.getInicioVigencia());
                    }
                    if (diferencaDias != null && diferencaDias <= diferencaDiasContrato) {
                        Date dataUltimoPeriodoAquisitivoContratoAnterior = buscarFinalVigenciaUltimoPeriodoAquisitivoByTipo(contratoRecuperado, tipo);
                        dataInicial = dataUltimoPeriodoAquisitivoContratoAnterior != null ? dataUltimoPeriodoAquisitivoContratoAnterior : contratoRecuperado.getInicioVigencia();
                        break;
                    }
                }
            }

            if (dataInicial != null) {
                dataInicial = DataUtil.adicionaDias(dataInicial, 1);
            } else {
                if (contratoFP.getAlteracaoVinculo() != null) {
                    dataInicial = contratoFP.getAlteracaoVinculo();
                } else {
                    dataInicial = TipoPeriodoAquisitivo.FERIAS.name().equals(tipo) ? contratoFP.getInicioVigencia() :contratoFP.getDataExercicio();
                }
            }
        }
        return dataInicial;
    }

    private boolean validarServidorComFinalDeVigenciaAndPeriodoAcimaDoFinalVigencia(ContratoFP contratoFP, TipoPeriodoAquisitivo tipo, Date dataInicial) {
        if (contratoFP.getFinalVigencia() == null) {
            return false;
        }
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        int retVal = dateTimeComparator.compare(dataInicial, contratoFP.getFinalVigencia());
        if (retVal >= 0) {
            return true;
        }
        return false;
    }

    private boolean validarServidorTemDireitoFeriasAndLicencaPremio(TipoPeriodoAquisitivo tipo, ContratoFP contratoFP) {
        if (TipoPeriodoAquisitivo.FERIAS.equals(tipo)) {
            return false;
        }
        if (TipoPeriodoAquisitivo.LICENCA.equals(tipo) && !ModalidadeContratoFP.CONCURSADOS.equals(contratoFP.getModalidadeContratoFP().getCodigo())) {
            logger.debug("Não é permitida gerar periodo aquisitivo de licença prêmio para servidor que não sejam concursados: " + contratoFP);
            return true;
        }
        return false;
    }

    private boolean jaPossuiPeriodoLancado(List<PeriodoAquisitivoFL> periodosAquisitivosFLs, PeriodoAquisitivoFL periodo) {
        try {
            for (PeriodoAquisitivoFL periodosAquisitivosFL : periodosAquisitivosFLs) {
                if (periodosAquisitivosFL.getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo().equals(periodo.getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo())) {
                    if (new Interval(periodosAquisitivosFL.getInicioVigenciaJoda(), periodosAquisitivosFL.getFinalVigenciaJoda()).contains(periodo.getInicioVigenciaJoda()) ||
                        new Interval(periodosAquisitivosFL.getInicioVigenciaJoda(), periodosAquisitivosFL.getFinalVigenciaJoda()).contains(periodo.getFinalVigenciaJoda())) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("problema para verificar periodo lancado " + e.getMessage());
            return false;
        }
    }

    private CalculoPeriodoAquisitivo checarPeriodosExcludentes(BasePeriodoAquisitivo basePeriodoAquisitivo, Cargo cargo, CalculoPeriodoAquisitivo calculo) {
        PeriodoAquisitivoExcluido excluido = cargo.getPeriodoAquisitivoExcluido();
        if (excluido != null && basePeriodoAquisitivo.getTipoPeriodoAquisitivo().equals(excluido.getTipoPeriodoAquisitivo())) {
            if (excluido.getInicioVigencia() != null && excluido.getFinalVigencia() != null) {
                if (new Interval(excluido.getInicioVigenciaJoda(), excluido.getFinalVigenciaJoda()).contains(calculo.getInicioVigenciaJoda()) || new Interval(excluido.getInicioVigenciaJoda(), excluido.getFinalVigenciaJoda()).contains(calculo.getFinalVigenciaJoda())) {
                    calculo.setPerdePeriodoReiniciandoContagem(true);
                    return calculo;
                }
            }
        }
        return calculo;
    }

    private BasePeriodoAquisitivo selecionarBasePeriodoAquisitivo(ContratoFP contratoFP, BaseCargo baseCargo, TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        BasePeriodoAquisitivo base = basePeriodoAquisitivoFacade.buscarBasePeriodoAquisitivoPorContratoFPAndCargoAndTipo(contratoFP, tipoPeriodoAquisitivo);
        return base == null ? baseCargo.getBasePeriodoAquisitivo() : base;
    }

    public Date buscarFinalVigenciaUltimoPeriodoAquisitivoByTipo(ContratoFP contrato, TipoPeriodoAquisitivo tipo) {
        String sql = " SELECT DISTINCT  max(periodo.FINALVIGENCIA) " +
            " FROM CONTRATOFP contrato   " +
            "   INNER JOIN VINCULOFP vinculo ON contrato.ID = vinculo.ID  " +
            "   INNER JOIN matriculafp matricula ON vinculo.MATRICULAFP_ID = matricula.ID  " +
            "   INNER JOIN PESSOAFISICA pf ON matricula.PESSOA_ID = pf.ID  " +
            "   INNER JOIN PERIODOAQUISITIVOFL periodo ON contrato.ID = periodo.CONTRATOFP_ID " +
            "   INNER JOIN BASECARGO bcargo on periodo.BASECARGO_ID = bcargo.ID " +
            "   INNER JOIN BASEPERIODOAQUISITIVO baseperiodo on bcargo.BASEPERIODOAQUISITIVO_ID = baseperiodo.ID " +
            "   WHERE contrato.id = :contratoID AND baseperiodo.TIPOPERIODOAQUISITIVO = :tipoPeriodo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoID", contrato.getId());
        q.setParameter("tipoPeriodo", tipo.name());
        q.setMaxResults(1);
        return q.getResultList().isEmpty() ? null : (Date) q.getSingleResult();
    }

    private void verificarBasesPeriodoAquisitivoVinculadasWithCargoByContratoFP(ContratoFP contratoFP, Date dataOperacao) {
        List<BaseCargo> bases = Lists.newArrayList();
        if (contratoFP.getCargo() != null) {
            bases = baseCargoFacade.buscarBasesDoCargo(contratoFP.getCargo(), dataOperacao);
        } else {
            throw new SemBasePeriodoAquisitivoException("Não foi localizado cargo para o servidor(a): " + contratoFP.getMatriculaFP().getMatricula() + "/" + contratoFP.getNumero() + " - " + contratoFP.getMatriculaFP().getPessoa().getNome() + ".");
        }
        if (bases == null || bases.isEmpty()) {
            throw new SemBasePeriodoAquisitivoException("Não foram localizadas bases de períodos aquisitivos vinculadas ao cargo " + contratoFP.getCargo() + " em " + Util.formatterDiaMesAno.format(dataOperacao) + "");
        }
    }

    private void adicionarNoContrato(ContratoFP contratoFP, PeriodoAquisitivoFL ultimoPeriodo) {
        if (!contratoFP.getPeriodosAquisitivosFLs().contains(ultimoPeriodo)) {
            contratoFP.setPeriodosAquisitivosFLs(Util.adicionarObjetoEmLista(contratoFP.getPeriodosAquisitivosFLs(), ultimoPeriodo));
        }
    }

    private boolean isRescisaoAndUltimoPeriodo(ContratoFP contratoFP, Date dataInicial, Date dataCorrente) {
        if (contratoFP.getFinalVigencia() != null) {
            return dataInicial.after(contratoFP.getFinalVigencia());
        }
        return false;
    }

    private CalculoPeriodoAquisitivo checarFaltasNoPeriodoAquisitivo(CalculoPeriodoAquisitivo calculo, ContratoFP contratoFP, Date dataOperacao) {
        double totalFaltas = faltasFacade.recuperaDiasDeFaltasPorPeriodo(contratoFP.getIdCalculo(), calculo.inicioVigencia, calculo.finalVigencia);
        if (totalFaltas == 0) {
            return calculo;
        }
        ParametroLicencaPremio parametro = parametroLicencaPremioFacade.recuperarVigente(dataOperacao);
        if (parametro != null && parametro.getReferenciaFP() != null) {
            FolhaDePagamento fp = new FolhaDePagamento();
            fp.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
            contratoFP.setFolha(fp);
            contratoFP.setMes(DataUtil.getMes(calculo.inicioVigencia));
            contratoFP.setAno(DataUtil.getAno(calculo.finalVigencia));

            ReferenciaFPFuncao faixa = funcoesFolhaFacade.obterReferenciaFaixaFP(contratoFP, parametro.getReferenciaFP().getCodigo(), totalFaltas, 0, 0);
            if (faixa != null) {
                if (faixa.getReferenciaAte().compareTo(new BigDecimal("16")) >= 0) {
                    calculo.setPeriodoAquisitivoCancelado(true);
                } else {
                    Integer diasParaDescontar = faixa.getValor().intValue();
                    calculo.setFinalVigencia(calculo.getFinalVigenciaJoda().plusDays(diasParaDescontar).toDate());
                    calculo.setAlterarDataFimDoPeriodoAquisitivo(true);
                }
            }
        }
        return calculo;
    }

    private CalculoPeriodoAquisitivo checarAfastamentosNoPeriodoAquisitivo(CalculoPeriodoAquisitivo calculo, Date dataOperacao) {
        if (TipoPeriodoAquisitivo.LICENCA.equals(calculo.tipo)) {
            List<PenalidadeFP> penalidades = penalidadeFPFacade.buscarPenalidadePorContrato(calculo.getContratoFP());
            for (PenalidadeFP penalidade : penalidades) {
                if (penalidadeContainsNoPeriodo(calculo, penalidade)) {
                    if (penalidade.getTipoPenalidadeFP().getReiniciarContagem()) {
                        calculo.setPerdePeriodoReiniciandoContagem(true);
                        calculo.setFinalVigencia(penalidade.getFinalVigencia());
                        break;
                    }
                }
            }
        }

        Integer diasAfastado = 0;
        Integer diasCarencia = 0;
        Integer percaPeriodoAquisitivo = 0;
        Integer total = 0;

        List<Afastamento> afastamentos = afastamentoFacade.buscarAfastamentosWithAlongamentoPeriodoAquisitivoByContratoFP(calculo.getContratoFP(),
            calculo.getInicioVigencia(),
            dataOperacao);


        if (!afastamentos.isEmpty()) {
            for (Afastamento afastamento : afastamentos) {

                if (containsNoPeriodo(calculo, afastamento)) {

                    diasAfastado = tratarAtributoNuloWithZero(DataUtil.diasEntreDate(afastamento.getInicio(), afastamento.getTermino()));

                    diasCarencia = tratarAtributoNuloWithZero(afastamento.getTipoAfastamento().getCarenciaAlongamento());

                    percaPeriodoAquisitivo = tratarAtributoNuloWithZero(afastamento.getTipoAfastamento().getMaximoPerdaPeriodoAquisitivo());

                    total = diasAfastado - diasCarencia;

                    if (TipoPeriodoAquisitivo.LICENCA.equals(calculo.tipo)) {
                        if (afastamento.getTipoAfastamento().getReiniciarContagem()) {
                            calculo.setPerdePeriodoReiniciandoContagem(true);
                            calculo.setFinalVigencia(afastamento.getTermino());
                            break;
                        }

                        if (afastamento.getTipoAfastamento().getAlongarPeriodoAquisitivo() && diasAfastado.compareTo(diasCarencia) > 0) {
                            calculo.setFinalVigencia(DataUtil.adicionaDias(calculo.getFinalVigencia(), total));
                            calculo.setAlterarDataFimDoPeriodoAquisitivo(true);
                        }
                        continue;
                    }

                    if (afastamento.getTipoAfastamento().getNaoGerarPeriodoAquisitivo()) {
                        calculo.setPerdePeriodoReiniciandoContagem(true);
                        calculo.setFinalVigencia(afastamento.getTermino());
                        break;
                    }

                    if (afastamento.getTipoAfastamento().getPerderPAFerias() && total.compareTo(percaPeriodoAquisitivo) > 0) {
                        calculo.setPeriodoAquisitivoCancelado(true);
                        break;

                    }
                    if (afastamento.getTipoAfastamento().getAlongarPeriodoAquisitivo() && diasAfastado.compareTo(diasCarencia) > 0) {
                        calculo.setFinalVigencia(DataUtil.adicionaDias(calculo.getFinalVigencia(), total));
                        calculo.setAlterarDataFimDoPeriodoAquisitivo(true);
                    }
                }

            }
        }

        return calculo;
    }


    private boolean containsNoPeriodo(CalculoPeriodoAquisitivo calculo, Afastamento afastamento) {
        return DataUtil.isBetween(afastamento.getInicio(), afastamento.getTermino(), calculo.getInicioVigencia())
            || DataUtil.isBetween(afastamento.getInicio(), afastamento.getTermino(), calculo.getFinalVigencia())
            || DataUtil.isBetween(calculo.getInicioVigencia(), calculo.getFinalVigencia(), afastamento.getInicio())
            || DataUtil.isBetween(calculo.getInicioVigencia(), calculo.getFinalVigencia(), afastamento.getTermino());
    }

    private boolean penalidadeContainsNoPeriodo(CalculoPeriodoAquisitivo calculo, PenalidadeFP penalidade) {
        return DataUtil.isBetween(penalidade.getInicioVigencia(), penalidade.getFinalVigencia(), calculo.getInicioVigencia())
            || DataUtil.isBetween(penalidade.getInicioVigencia(), penalidade.getFinalVigencia(), calculo.getFinalVigencia())
            || DataUtil.isBetween(calculo.getInicioVigencia(), calculo.getFinalVigencia(), penalidade.getInicioVigencia())
            || DataUtil.isBetween(calculo.getInicioVigencia(), calculo.getFinalVigencia(), penalidade.getFinalVigencia());
    }

    private Integer tratarAtributoNuloWithZero(Integer objeto) {
        return (objeto != null) ? objeto : 0;
    }

    private void excluirPeriodosAquisitivosSemConcessao(ContratoFP contratoFP, TipoPeriodoAquisitivo tipo, Date dataInicial, PeriodoAquisitivoFL periodo) {
        List<PeriodoAquisitivoFL> periodosAquisitivos = buscarPeriodosPorContratoSemConcessaoPorDataAndTipo(contratoFP, dataInicial, tipo);
        for (Iterator<PeriodoAquisitivoFL> p = periodosAquisitivos.iterator(); p.hasNext(); ) {
            PeriodoAquisitivoFL periodosAquisitivo = p.next();
            if (checarSePeriodoAquisitivoPodeSerExcluido(periodosAquisitivo)) {
                if (!periodo.equals(periodosAquisitivo)) {
                    if (contratoFP.getPeriodosAquisitivosFLs().contains(periodosAquisitivo)) {
                        contratoFP.getPeriodosAquisitivosFLs().remove(periodosAquisitivo);
                    }
                    if (periodosAquisitivo.getId() != null) {
                        remover(periodosAquisitivo);
                    }
                }
            }
        }
    }

    private boolean checarSePeriodoAquisitivoPodeSerExcluido(PeriodoAquisitivoFL periodosAquisitivo) {
        if (TipoPeriodoAquisitivo.FERIAS.equals(periodosAquisitivo.getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo()) && StatusPeriodoAquisitivo.NAO_CONCEDIDO.equals(periodosAquisitivo.getStatus())) {
            return sugestaoFeriasFacade.findSugestoesPorPeriodoAquisitivoFl(periodosAquisitivo).isEmpty();
        }
        return false;
    }

    //preparado para calcular em dias
    private Integer quantidadeSaldoProporcialUltimoPeriodo(BasePeriodoAquisitivo base, Double meses) {
        if (base.getTipoPeriodoAquisitivo().equals(TipoPeriodoAquisitivo.FERIAS) && meses > 0.5) {
            Double total = (meses / base.getDuracao()) * 30;
            return total.intValue();
        }
        return 0;
    }

    public List<PeriodoAquisitivoFL> recuperarPeriodosAquisitivosComFeriasAprovadas(ContratoFP c) {
        String hql = "select p from PeriodoAquisitivoFL p" +
            "   inner join p.sugestaoFerias sf" +
            "   inner join p.contratoFP c" +
            "   inner join sf.listAprovacaoFerias af" +
            "        where af.aprovado is true" +
            "          and c = :contrato" +
            "          and p.status not in (:status, :statusSemDireito) " +
            "     order by p.inicioVigencia asc, p.finalVigencia";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", c);
        q.setParameter("status", StatusPeriodoAquisitivo.CONCEDIDO);
        q.setParameter("statusSemDireito", StatusPeriodoAquisitivo.SEM_DIREITO);
        return (List<PeriodoAquisitivoFL>) q.getResultList();
    }

    public boolean temPeriodosAquisitivosMaisRecentes(PeriodoAquisitivoFL periodo) {
        Query q = em.createQuery("select periodo from PeriodoAquisitivoFL  periodo" +
            "             inner join periodo.baseCargo.basePeriodoAquisitivo base" +
            "                  where periodo.contratoFP = :contrato " +
            "                    and periodo.inicioVigencia > :inicioVigencia" +
            "                    and periodo.status = :status" +
            "                    and base.tipoPeriodoAquisitivo = :tipoPeriodo");
        q.setParameter("contrato", periodo.getContratoFP());
        q.setParameter("inicioVigencia", periodo.getInicioVigencia());
        q.setParameter("status", StatusPeriodoAquisitivo.CONCEDIDO);
        q.setParameter("tipoPeriodo", periodo.getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo());
        return !q.getResultList().isEmpty();
    }

    public List<PeriodoAquisitivoFL> buscarPeriodosAquisitivos(ContratoFP contrato, TipoPeriodoAquisitivo tipoPeriodoAquisitivo, StatusPeriodoAquisitivo... statusPeriodoAquisitivo) {
        Query q = em.createQuery("select p from PeriodoAquisitivoFL p "
            + "inner join p.baseCargo base "
            + "inner join base.basePeriodoAquisitivo bpa "
            + "where p.contratoFP = :contrato "
            + " and p.status in(:status) "
            + " and bpa.tipoPeriodoAquisitivo = :tipo "
            + " order by p.inicioVigencia");
        q.setParameter("contrato", contrato);
        q.setParameter("tipo", tipoPeriodoAquisitivo);
        q.setParameter("status", Arrays.asList(statusPeriodoAquisitivo));
        return q.getResultList();
    }

    public List<PeriodoAquisitivoFL> recuperarPeriodosAquisitivos(ContratoFP contrato, TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        Query q = em.createQuery("select p from PeriodoAquisitivoFL p "
            + "inner join p.baseCargo base "
            + "inner join base.basePeriodoAquisitivo bpa "
            + "where p.contratoFP = :contrato "
            + " and bpa.tipoPeriodoAquisitivo = :tipo "
            + " order by p.inicioVigencia desc");
        q.setParameter("contrato", contrato);
        q.setParameter("tipo", tipoPeriodoAquisitivo);
        return q.getResultList();
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodosPorContrato(ContratoFP contrato) {
        Query q = em.createQuery("from PeriodoAquisitivoFL p where p.contratoFP = :contrato order by p.inicioVigencia desc");
        q.setParameter("contrato", contrato);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public void buscarPeriodosAquisitivosInconsistentes(ContratoFP contrato) {
        Query q = em.createNativeQuery("delete from PeriodoAquisitivoFL p where p.contratoFP_id = :contrato and p.status = :status and p.inicioVigencia > p.finalVigencia");
        q.setParameter("contrato", contrato.getId());
        q.setParameter("status", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
        q.executeUpdate();
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodosPorContratoSemConcessao(VinculoFP contrato) {
        Query q = em.createQuery("select p from PeriodoAquisitivoFL p where p.contratoFP.id = :contrato" +
            " and p not in (select c.periodoAquisitivoFL from ConcessaoFeriasLicenca  c) order by p.inicioVigencia desc");
        q.setParameter("contrato", contrato.getId());
        return q.getResultList();
    }

    public List<PeriodoAquisitivoFL> buscarPeriodosPorContratoSemConcessaoPorDataAndTipo(VinculoFP contrato, Date dataInicial, TipoPeriodoAquisitivo tipo) {
        Query q = em.createQuery("select p from PeriodoAquisitivoFL p where p.contratoFP.id = :contrato and p.finalVigencia >= :data " +
            " and p.baseCargo.basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipo " +
            " and p not in (select c.periodoAquisitivoFL from ConcessaoFeriasLicenca  c) order by p.inicioVigencia desc");
        q.setParameter("contrato", contrato.getId());
        q.setParameter("data", dataInicial);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<WSPeriodoAquisitivo> recuperarPeriodoAquisitivoPorVinculoPortal(WSVinculoFP wsVinculoFP) {
        List<WSPeriodoAquisitivo> wsPeriodoAquisitivos = new LinkedList<>();
        wsPeriodoAquisitivos.addAll(carregaPeriodoAquisitivosPortal(wsVinculoFP.getId()));
        return wsPeriodoAquisitivos;
    }

    public List<WSPeriodoAquisitivo> recuperarPeriodoAquisitivoPorVinculoPortalSemConcessao(WSVinculoFP wsVinculoFP) {
        List<WSPeriodoAquisitivo> wsPeriodoAquisitivos = new LinkedList<>();
        wsPeriodoAquisitivos.addAll(carregarPeriodoAquisitivosPortalSemConcessao(wsVinculoFP.getId()));
        return wsPeriodoAquisitivos;
    }

    private Collection<? extends WSPeriodoAquisitivo> carregarPeriodoAquisitivosPortalSemConcessao(Long vinculoId) {
        Query q = em.createQuery("select p from PeriodoAquisitivoFL p "
            + " inner join p.baseCargo base "
            + " inner join base.basePeriodoAquisitivo bpa "
            + " where p.contratoFP.id = :id and p.status = :status and bpa.tipoPeriodoAquisitivo = :tipo "
            + " order by p.inicioVigencia");
        q.setParameter("id", vinculoId);
        q.setParameter("status", StatusPeriodoAquisitivo.NAO_CONCEDIDO);
        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS);
        List<PeriodoAquisitivoFL> periodoAquisitivoFLs = q.getResultList();
        List<WSPeriodoAquisitivo> wsPeriodoAquisitivos = Lists.newLinkedList();
        if (periodoAquisitivoFLs != null && !periodoAquisitivoFLs.isEmpty()) {
            for (PeriodoAquisitivoFL periodoAquisitivoFL : periodoAquisitivoFLs) {
                WSPeriodoAquisitivo ws = WSPeriodoAquisitivo.convertPeriodoAquisitivoFLTOWsPeriodoAquisitivo(periodoAquisitivoFL);
                List<ProgramacaoFeriasPortal> programacaoFeriasPortals = programacaoFeriasPortalFacade.buscarProgramacoesPorPeriodoAquisitivo(periodoAquisitivoFL);
                for (ProgramacaoFeriasPortal programacaoFeriasPortal : programacaoFeriasPortals) {
                    ws.setProgramacaoFeriasPortalortal(new RHProgramacaoFeriasPortal(programacaoFeriasPortal));
                }
                wsPeriodoAquisitivos.add(ws);
            }
        }
        return wsPeriodoAquisitivos;
    }


    private Collection<? extends WSPeriodoAquisitivo> carregaPeriodoAquisitivosPortal(Long vinculoId) {
        Query q = em.createQuery("select p from PeriodoAquisitivoFL p "
            + " inner join p.baseCargo base "
            + " inner join base.basePeriodoAquisitivo bpa "
            + " where p.contratoFP.id = :id"
            + " order by p.inicioVigencia");
        q.setParameter("id", vinculoId);
        List<PeriodoAquisitivoFL> periodoAquisitivoFLs = q.getResultList();
        List<WSPeriodoAquisitivo> wsPeriodoAquisitivos = Lists.newLinkedList();
        if (periodoAquisitivoFLs != null && !periodoAquisitivoFLs.isEmpty()) {
            for (PeriodoAquisitivoFL periodoAquisitivoFL : periodoAquisitivoFLs) {
                WSPeriodoAquisitivo ws = WSPeriodoAquisitivo.convertPeriodoAquisitivoFLTOWsPeriodoAquisitivo(periodoAquisitivoFL);
                ws.setWsConcessaoFerias(WSConcessaoFerias.convertConcessaoToWSConcessaoFeriasList(concessaoFeriasLicencaFacade.listaConcessaoFeriasLicencaPorPeriodoAquisitivo(periodoAquisitivoFL)));
                ws.setWsProgramacaoFerias(WSProgramacaoFerias.convertSugestaoFeriasToWsSugestaoFerias(sugestaoFeriasFacade.findSugestoesPorPeriodoAquisitivoFl(periodoAquisitivoFL)));
                wsPeriodoAquisitivos.add(ws);
            }
        }
        return wsPeriodoAquisitivos;
    }

    public PeriodoAquisitivoFL atribuirDiasDeDireitoDoServidor(PeriodoAquisitivoFL periodo, Date dataOperacao) {
        if (podeDescontarFaltasInjustificadas(periodo, dataOperacao)) {
            return getPeriodoAquisitivoFLComDescontoNoSaldoDeDireito(periodo, dataOperacao);
        }
        return getPeriodoAquisitivoFLSemDescontoNoSaldoDeDireito(periodo);
    }

    private boolean podeDescontarFaltasInjustificadas(PeriodoAquisitivoFL periodo, Date dataOperacao) {
        if (periodo.getBaseCargo().getBasePeriodoAquisitivo().isTipoLicenca()) {
            return false;
        }
        try {
            if (naoTemConfiguracaoFaltasInjustificadasParaOrgao(getHierarquiaOrganizacionalPorPeriodoAquisitivo(periodo, dataOperacao), dataOperacao)) {
                return false;
            }
        } catch (Exception e) {
            logger.error("não foi possível executar naoTemConfiguracaoFaltasInjustificadasParaOrgao ", e);
        }
        return true;
    }

    private boolean naoTemConfiguracaoFaltasInjustificadasParaOrgao(HierarquiaOrganizacional hoPartindoDoPeriodoAquisitivo, Date dataOperacao) {
        return !temConfiguracaoFaltasInjustificadasParaOrgao(hoPartindoDoPeriodoAquisitivo, dataOperacao);
    }

    private boolean temConfiguracaoFaltasInjustificadasParaOrgao(HierarquiaOrganizacional hoPartindoDoPeriodoAquisitivo, Date dataOperacao) {
        return getCodigosHODasConfiguracoesVigentes(dataOperacao).contains(getCodigoHOPartindoDoPeriodoAquisitivo(hoPartindoDoPeriodoAquisitivo));
    }

    private List<String> getCodigosHODasConfiguracoesVigentes(Date dataOperacao) {
        List<String> codigosHODasConfiguracoesVigentes = new ArrayList<>();
        for (ConfiguracaoFaltasInjustificadas configuracao : configuracaoFaltasInjustificadasFacade.buscarConfiguracoesFaltasInjustificadasVigentes(dataOperacao)) {
            if (configuracao.getUnidadeOrganizacional() == null) {
                throw new SemConfiguracaoDeFaltaInjustificada("O cadastro de configurações de faltas está incompleto sendo necessário informar o órgão da configuração vigente.");
            }
            codigosHODasConfiguracoesVigentes.add(hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(configuracao.getUnidadeOrganizacional().getId()).getCodigoDo2NivelDeHierarquia());
        }
        return codigosHODasConfiguracoesVigentes;
    }

    private String getCodigoHOPartindoDoPeriodoAquisitivo(HierarquiaOrganizacional hoPartindoDoPeriodoAquisitivo) {
        return hoPartindoDoPeriodoAquisitivo.getCodigoDo2NivelDeHierarquia();
    }

    private HierarquiaOrganizacional getHierarquiaOrganizacionalPorPeriodoAquisitivo(PeriodoAquisitivoFL periodo, Date dataOperacao) {
        return hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(
            getUnidadeOrganizacionalPorLotacaoFuncional(getLotacaoFuncionalVigentePorContratoFP(getContratoFPPorPeriodoAquisitivo(periodo), dataOperacao)).getId());
    }

    private UnidadeOrganizacional getUnidadeOrganizacionalPorLotacaoFuncional(LotacaoFuncional lotacaoFuncionalVigentePorContratoFP) {
        return lotacaoFuncionalVigentePorContratoFP.getUnidadeOrganizacional();
    }

    private LotacaoFuncional getLotacaoFuncionalVigentePorContratoFP(ContratoFP contratoFP, Date dataOperacao) {
        LotacaoFuncional lotacao = lotacaoFuncionalFacade.recuperarLotacaoFuncionalVigentePorContratoFP(contratoFP, dataOperacao);
        if (lotacao == null) return lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(contratoFP);
        return lotacao;
    }

    private ContratoFP getContratoFPPorPeriodoAquisitivo(PeriodoAquisitivoFL periodo) {
        return periodo.getContratoFP();
    }

    private PeriodoAquisitivoFL getPeriodoAquisitivoFLComDescontoNoSaldoDeDireito(PeriodoAquisitivoFL periodo, Date dataOperacao) {
        double totalFaltas = faltasFacade.recuperaDiasDeFaltasPorPeriodo(periodo.getContratoFP().getIdCalculo(), periodo.getInicioVigencia(), periodo.getFinalVigencia());

        ConfiguracaoFaltasInjustificadas config = getConfiguracaoFaltasInjustificadasVigenteAndPeriodoAquisitivo(periodo, dataOperacao);
        if (config != null) {
            FolhaDePagamento fp = new FolhaDePagamento();
            fp.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
            periodo.getContratoFP().setFolha(fp);
            periodo.getContratoFP().setMes(DataUtil.getMes(periodo.getInicioVigencia()));
            periodo.getContratoFP().setAno(DataUtil.getAno(periodo.getInicioVigencia()));

            ReferenciaFPFuncao faixa = funcoesFolhaFacade.obterReferenciaFaixaFP(periodo.getContratoFP(), config.getReferenciaFP().getCodigo(), totalFaltas, 0, 0);
            if (faixa != null) {
                Integer diasParaDescontar = faixa.getValor().intValue();
                periodo.setSaldoDeDireito(periodo.getSaldo() - diasParaDescontar);
                periodo.setSaldoDeDireito(periodo.getSaldoDeDireito() < 0 ? 0 : periodo.getSaldoDeDireito());
            }
        }

        periodo.getContratoFP().setPeriodosAquisitivosFLs(Util.adicionarObjetoEmLista(periodo.getContratoFP().getPeriodosAquisitivosFLs(), periodo));
        return periodo;
    }

    public ConfiguracaoFaltasInjustificadas getConfiguracaoFaltasInjustificadasVigenteAndPeriodoAquisitivo(PeriodoAquisitivoFL periodo, Date dataOperacao) {
        try {
            return configuracaoFaltasInjustificadasFacade.buscarConfiguracaoFaltasInjustificadasPorOrgaoAndDataOperacao(
                getCodigoDo2NivelDoOrgao(getHierarquiaOrganizacionalPorPeriodoAquisitivo(periodo, dataOperacao)), dataOperacao);
        } catch (Exception e) {
            logger.error("Não foi possível recuperar a configuração de desconto de falta", e);
            return null;
        }
    }

    private String getCodigoDo2NivelDoOrgao(HierarquiaOrganizacional orgao) {
        return orgao.getCodigoDo2NivelDeHierarquia();
    }

    private PeriodoAquisitivoFL getPeriodoAquisitivoFLSemDescontoNoSaldoDeDireito(PeriodoAquisitivoFL periodo) {
        periodo.setSaldoDeDireito(periodo.getSaldo());
        return periodo;
    }

    public PeriodoAquisitivoFL buscarPeriodoAquisitivoFLPorContratoFPAndFinalVigencia(ContratoFP contratoFP, Date finalVigencia) {
        try {
            String sql = "select p.* from periodoaquisitivofl p where p.contratofp_id = :idContrato and p.finalVigencia = :data";
            Query q = em.createNativeQuery(sql, PeriodoAquisitivoFL.class);
            q.setParameter("idContrato", contratoFP.getId());
            q.setParameter("data", finalVigencia);
            q.setMaxResults(1);
            return (PeriodoAquisitivoFL) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodosNaoConcedidosDepoisDaDataReferenciaPorContrato(ContratoFP contrato, Date dataReferencia) {
        String hql = "from PeriodoAquisitivoFL p " +
            " where p.contratoFP = :contrato " +
            " and p.status = :statusPA ";
        if (dataReferencia != null) {
            hql += " and p.inicioVigencia > :dataReferencia ";
        }
        hql += " order by p.inicioVigencia desc";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", contrato);
        q.setParameter("statusPA", StatusPeriodoAquisitivo.NAO_CONCEDIDO);
        if (dataReferencia != null) {
            q.setParameter("dataReferencia", dataReferencia);
        }
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public PeriodoAquisitivoFL recuperaUltimoPeriodoConcedidoPorContrato(ContratoFP contrato) {
        Query q = em.createQuery("from PeriodoAquisitivoFL p " +
            " where p.contratoFP = :contrato " +
            " and p.status in (:statusPAConcedido, :statusPAParcial)" +
            " order by p.finalVigencia desc");
        q.setParameter("contrato", contrato);
        q.setParameter("statusPAConcedido", StatusPeriodoAquisitivo.CONCEDIDO);
        q.setParameter("statusPAParcial", StatusPeriodoAquisitivo.PARCIAL);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (PeriodoAquisitivoFL) resultList.get(0);
        }
        return null;
    }

    public PeriodoAquisitivoFL recuperaUltimoPeriodoConcedidoPorContratoETipoPA(ContratoFP contrato, TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        String sql = "select p.* from PeriodoAquisitivoFL p " +
            " inner join BaseCargo b on b.id = p.basecargo_id  " +
            " inner join BasePeriodoAquisitivo bpa on bpa.id = b.baseperiodoaquisitivo_id " +
            " where p.contratofp_id = :contrato " +
            " and p.status in (:statusPAConcedido, :statusPAParcial)" +
            " and bpa.tipoperiodoaquisitivo = :tipoPeriodoAquisitivo " +
            " order by p.finalVigencia desc ";
        Query q = em.createNativeQuery(sql, PeriodoAquisitivoFL.class);
        q.setParameter("contrato", contrato);
        q.setParameter("statusPAConcedido", StatusPeriodoAquisitivo.CONCEDIDO.name());
        q.setParameter("statusPAParcial", StatusPeriodoAquisitivo.PARCIAL.name());
        q.setParameter("tipoPeriodoAquisitivo", tipoPeriodoAquisitivo.name());

        List<PeriodoAquisitivoFL> resultList = q.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodosNaoConcedidosPorContratoETipoPA(ContratoFP contrato, Date dataReferencia, TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        String sql = "select p.* from PeriodoAquisitivoFL p " +
            " inner join BaseCargo b on b.id = p.basecargo_id  " +
            " inner join BasePeriodoAquisitivo bpa on bpa.id = b.baseperiodoaquisitivo_id " +
            " where p.contratofp_id = :contrato " +
            " and p.status = :statusPANaoConcedido " +
            " and bpa.tipoperiodoaquisitivo = :tipoPeriodoAquisitivo ";
        if (dataReferencia != null) {
            sql += " and :dataReferencia < p.iniciovigencia ";
        }
        sql += " order by p.finalVigencia desc ";
        Query q = em.createNativeQuery(sql, PeriodoAquisitivoFL.class);
        q.setParameter("contrato", contrato);
        q.setParameter("statusPANaoConcedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
        q.setParameter("tipoPeriodoAquisitivo", tipoPeriodoAquisitivo.name());
        if (dataReferencia != null) {
            q.setParameter("dataReferencia", dataReferencia);
        }
        List<PeriodoAquisitivoFL> resultList = q.getResultList();
        return resultList.isEmpty() ? null : resultList;
    }


    public PeriodoAquisitivoFL recuperaUltimoPANaoCondedidoComTercoAutomaticoPorContrato(ContratoFP contrato) {
        String sql = "select p.* from PeriodoAquisitivoFL p " +
            "   inner join LancamentoTercoFeriasAut ltf on ltf.periodoaquisitivofl_id = p.id " +
            " where p.contratofp_id = :contrato " +
            " and p.status = :statusPANaoConcedido ";
        sql += " order by p.finalVigencia desc ";
        Query q = em.createNativeQuery(sql, PeriodoAquisitivoFL.class);
        q.setParameter("contrato", contrato);
        q.setParameter("statusPANaoConcedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
        List<PeriodoAquisitivoFL> resultList = q.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);

    }

    public PeriodoAquisitivoFL buscarPeriodoAquisitivoVigentePorContratoETipo(ContratoFP contratoFP, Date data, TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        Query q = em.createQuery(" select pa from PeriodoAquisitivoFL pa" +
            " inner join pa.baseCargo baseCargo " +
            " inner join baseCargo.basePeriodoAquisitivo basePA " +
            " where pa.contratoFP.id = :idContrato " +
            " and basePA.tipoPeriodoAquisitivo = :tipoPA " +
            " and :data between pa.inicioVigencia and coalesce(pa.finalVigencia, :data) ");
        q.setParameter("data", data);
        q.setParameter("idContrato", contratoFP.getId());
        q.setParameter("tipoPA", tipoPeriodoAquisitivo);
        List resultList = q.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return (PeriodoAquisitivoFL) resultList.get(0);
        }
        return null;
    }

    public class CalculoPeriodoAquisitivo {
        private ContratoFP contratoFP;
        private Date inicioVigencia;
        private Date finalVigencia;
        private TipoPeriodoAquisitivo tipo;
        private boolean alterarDataFimDoPeriodoAquisitivo;
        private boolean periodoAquisitivoCancelado;
        private boolean perdePeriodoReiniciandoContagem;

        public CalculoPeriodoAquisitivo(ContratoFP contratoFP, TipoPeriodoAquisitivo tipo, Date inicioVigencia, Date finalVigencia) {
            this.contratoFP = contratoFP;
            this.inicioVigencia = inicioVigencia;
            this.finalVigencia = finalVigencia;
            this.tipo = tipo;
        }

        public TipoPeriodoAquisitivo getTipo() {
            return tipo;
        }

        public void setTipo(TipoPeriodoAquisitivo tipo) {
            this.tipo = tipo;
        }

        public boolean isPeriodoAquisitivoCancelado() {
            return periodoAquisitivoCancelado;
        }

        public void setPeriodoAquisitivoCancelado(boolean periodoAquisitivoCancelado) {
            this.periodoAquisitivoCancelado = periodoAquisitivoCancelado;
        }

        public boolean isAlterarDataFimDoPeriodoAquisitivo() {
            return alterarDataFimDoPeriodoAquisitivo;
        }

        public void setAlterarDataFimDoPeriodoAquisitivo(boolean alterarDataFimDoPeriodoAquisitivo) {
            this.alterarDataFimDoPeriodoAquisitivo = alterarDataFimDoPeriodoAquisitivo;
        }

        public boolean isPerdePeriodoReiniciandoContagem() {
            return perdePeriodoReiniciandoContagem;
        }

        public void setPerdePeriodoReiniciandoContagem(boolean perdePeriodoReiniciandoContagem) {
            this.perdePeriodoReiniciandoContagem = perdePeriodoReiniciandoContagem;
        }

        public DateTime getFinalVigenciaJoda() {
            return new DateTime(finalVigencia);
        }

        public DateTime getInicioVigenciaJoda() {
            return new DateTime(inicioVigencia);
        }

        public Date getFinalVigencia() {
            return finalVigencia;
        }

        public void setFinalVigencia(Date finalVigencia) {
            this.finalVigencia = finalVigencia;
        }

        public Date getInicioVigencia() {
            return inicioVigencia;
        }

        public void setInicioVigencia(Date inicioVigencia) {
            this.inicioVigencia = inicioVigencia;
        }

        public ContratoFP getContratoFP() {
            return contratoFP;
        }

        public void setContratoFP(ContratoFP contratoFP) {
            this.contratoFP = contratoFP;
        }
    }

}
