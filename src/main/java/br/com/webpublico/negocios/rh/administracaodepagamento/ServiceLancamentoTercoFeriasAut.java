package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.LancamentoTercoFeriasAut;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.enums.TipoCedenciaContratoFP;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.enums.rh.administracaopagamento.IdentificadorLancamentoTerco;
import br.com.webpublico.enums.rh.administracaopagamento.TipoTercoFeriasAutomatico;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceLancamentoTercoFeriasAut {
    public static final Logger logger = LoggerFactory.getLogger(ServiceLancamentoTercoFeriasAut.class);

    @PersistenceContext
    protected transient EntityManager em;
    private LancamentoTercoFeriasAutFacade lancamentoTercoFeriasAutFacade;
    private ConfiguracaoRHFacade configuracaoRHFacade;
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @PostConstruct
    private void init() {
        try {
            lancamentoTercoFeriasAutFacade = (LancamentoTercoFeriasAutFacade) new InitialContext().lookup("java:module/LancamentoTercoFeriasAutFacade");
            configuracaoRHFacade = (ConfiguracaoRHFacade) new InitialContext().lookup("java:module/ConfiguracaoRHFacade");
            hierarquiaOrganizacionalFacade = (HierarquiaOrganizacionalFacade) new InitialContext().lookup("java:module/HierarquiaOrganizacionalFacade");
        } catch (NamingException e) {
            logger.error(e.getExplanation());
        }
    }

    public void lancarTercoFerias() {
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente(SistemaFacade.getDataCorrente());
        int mes = DataUtil.getMes(SistemaFacade.getDataCorrente());
        int ano = DataUtil.getAno(SistemaFacade.getDataCorrente());
        if (TipoTercoFeriasAutomatico.FINAL_PERIODO_AQUISITIVO.equals(configuracaoRH.getTipoTercoFeriasAutomatico())) {
            for (PeriodoAquisitivoFL periodo : recuperaPeriodoAquisitivoPorMesAno(SistemaFacade.getDataCorrente(), Boolean.TRUE, null, null)) {
                int saldoDias = periodo.getSaldo() != null ? periodo.getSaldo() : 0;
                for (ConcessaoFeriasLicenca concessao : recuperaConsessaoParaPeriodoAquisitivo(periodo)) {
                    if (ano < concessao.getAno() || (mes <= concessao.getMes() && ano == concessao.getAno())) {
                        java.time.LocalDate dataInicial = java.time.Instant.ofEpochMilli(concessao.getInicioVigencia().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                        java.time.LocalDate dataFinal = java.time.Instant.ofEpochMilli(concessao.getFimVigencia().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                        saldoDias = saldoDias + DataUtil.diasEntre(dataInicial, dataFinal);
                    }
                }
                if (saldoDias > 0) {
                    lancamentoTercoFeriasAutFacade.salvar(new LancamentoTercoFeriasAut(periodo, saldoDias, mes, ano, periodo.getContratoFP(), IdentificadorLancamentoTerco.AUTOMATICO));
                }
            }
        } else if (TipoTercoFeriasAutomatico.ANIVERSARIO_CONTRATO.equals(configuracaoRH.getTipoTercoFeriasAutomatico())) {
            List<ContratoFP> contratos = buscarContratosParaPreenchimentoPorAniversarioContrato(DataUtil.dateToLocalDate(SistemaFacade.getDataCorrente()), null);
            for (ContratoFP contratoFP : contratos) {
                try {
                    BasePeriodoAquisitivo basePA = buscarBasePeriodoAquisitivoPorContrato(contratoFP, SistemaFacade.getDataCorrente());
                    Integer saldo = basePA != null ? basePA.getDiasDeDireito() : 0;
                    lancamentoTercoFeriasAutFacade.salvar(new LancamentoTercoFeriasAut(null, saldo, mes, ano, contratoFP, IdentificadorLancamentoTerco.AUTOMATICO));
                } catch (Exception e) {
                    logger.error("Erro ao gerar lançamento de terço de férias [{}]. {}", contratoFP, e.getMessage());
                }
            }
        }
    }

    public List<ContratoFP> buscarContratosParaPreenchimentoPorAniversarioContrato(LocalDate dataOperacao, HierarquiaOrganizacional hierarquiaOrganizacional) {
        List<ContratoFP> contratos = Lists.newArrayList();
        LocalDate inicioMes = dataOperacao.withDayOfMonth(1);
        LocalDate finalMes = dataOperacao.withDayOfMonth(dataOperacao.lengthOfMonth());
        int diasNoMes = DataUtil.diasEntre(inicioMes, finalMes);

        for (ContratoFP contratoFP : buscarServidoresComAniversarioDeContratoNaCompetencia(DataUtil.localDateToDate(dataOperacao), hierarquiaOrganizacional)) {
            int diasAfastados = 0;
            int diasCedido = 0;
            for (Afastamento afastamento : buscarAfastamentosQueNaoProcessamFolha(contratoFP)) {
                LocalDate dataCorteInicio = inicioMes;
                LocalDate dataCorteFinal = finalMes;
                LocalDate inicioAfastamento = DataUtil.dateToLocalDate(afastamento.getInicio());
                LocalDate finalAfastamento = afastamento.getTermino() != null ? DataUtil.dateToLocalDate(afastamento.getTermino()) : null;
                if (afastamento.getCarencia() != null) {
                    inicioAfastamento = inicioAfastamento.plusDays(afastamento.getCarencia());
                }
                if (inicioAfastamento.isAfter(dataCorteInicio)) {
                    dataCorteInicio = inicioAfastamento;
                }
                if (finalAfastamento != null && finalAfastamento.isBefore(dataCorteFinal) && afastamento.getRetornoInformado()) {
                    dataCorteFinal = finalAfastamento;
                }
                diasAfastados = DataUtil.diasEntre(dataCorteInicio, dataCorteFinal);
            }

            for (CedenciaContratoFP cedencia : buscarCedenciasSemOnus(contratoFP)) {
                LocalDate dataCorteInicio = inicioMes;
                LocalDate dataCorteFinal = finalMes;
                LocalDate inicioCessao = DataUtil.dateToLocalDate(cedencia.getDataCessao());

                RetornoCedenciaContratoFP retorno = buscarRetornoCedencia(cedencia);
                LocalDate finalCessao = retorno != null ? DataUtil.dateToLocalDate(retorno.getDataRetorno()) : null;

                if (inicioCessao.isAfter(dataCorteInicio)) {
                    dataCorteInicio = inicioCessao;
                }
                if (finalCessao != null && finalCessao.isBefore(dataCorteFinal)) {
                    dataCorteFinal = finalCessao;
                }
                diasCedido = DataUtil.diasEntre(dataCorteInicio, dataCorteFinal);
            }
            if ((diasCedido + diasAfastados) < diasNoMes) {
                contratos.add(contratoFP);
            }
        }
        return contratos;
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodoAquisitivoPorMesAno(Date data, Boolean filtrarApenasNaoLancados, ContratoFP contratoFP, HierarquiaOrganizacional hierarquiaOrganizacional) {
        String hql = " select periodo from PeriodoAquisitivoFL periodo " +
            "   inner join periodo.baseCargo bc " +
            "   inner join periodo.contratoFP contrato " +
            "   inner join bc.basePeriodoAquisitivo base " +
            " where to_date(to_char(periodo.finalVigencia + 1, 'mm/yyyy'), 'mm/yyyy') = to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') " +
            "   and base.tipoPeriodoAquisitivo = :tipoPeriodo " +
            "   and months_between(periodo.finalVigencia + 1, periodo.inicioVigencia) >= base.duracao " +
            "   and not exists (select lanc from LancamentoTercoFeriasAut lanc " +
            "                            where lanc.mes = extract(month from :data) " +
            "                                and lanc.ano = extract(year from :data) " +
            "                                 and lanc.contratoFP = periodo.contratoFP)";
        if (filtrarApenasNaoLancados) {
            hql += "   and periodo not in (select lanc.periodoAquisitivoFL from LancamentoTercoFeriasAut lanc" +
                "                       where lanc.periodoAquisitivoFL = periodo)";
        }
        if (contratoFP != null) {
            hql += "  and contrato = :contratofp";
        }
        if (hierarquiaOrganizacional != null) {
            hql += " and contrato in (" +
                " select contratoLotacao from LotacaoFuncional lotacao " +
                " inner join lotacao.vinculoFP contratoLotacao " +
                " inner join lotacao.unidadeOrganizacional unidadeContrato " +
                " inner join VwHierarquiaAdministrativa ho on ho.subordinadaId = unidadeContrato.id  " +
                " where :dataAtual >= lotacao.inicioVigencia " +
                " and :dataAtual <= coalesce(lotacao.finalVigencia,:dataAtual) " +
                " and ho.id in (:unidades)) ";
        }

        Query q = em.createQuery(hql);
        q.setParameter("data", data);
        q.setParameter("tipoPeriodo", TipoPeriodoAquisitivo.FERIAS);
        if (contratoFP != null) {
            q.setParameter("contratofp", contratoFP);
        }
        if (hierarquiaOrganizacional != null) {
            Date dataAtual = Util.getDataHoraMinutoSegundoZerado(new Date());
            List<HierarquiaOrganizacional> hos = hierarquiaOrganizacionalFacade.buscarHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativa(hierarquiaOrganizacional, dataAtual);
            q.setParameter("unidades", hos.stream().map(ho -> ho.getId()).collect(Collectors.toList()));
            q.setParameter("dataAtual", dataAtual);

        }
        return q.getResultList();
    }

    public List<ConcessaoFeriasLicenca> recuperaConsessaoParaPeriodoAquisitivo(PeriodoAquisitivoFL periodo) {
        Query q = em.createQuery(" from ConcessaoFeriasLicenca conc " +
            "                       where conc.periodoAquisitivoFL = :periodo " +
            "                           and conc.mes is not null " +
            "                           and conc.mes > 0 " +
            "                           and conc.mes < 13 " +
            "                           and conc.ano is not null");
        q.setParameter("periodo", periodo);
        return q.getResultList();
    }

    public List<ContratoFP> buscarServidoresComAniversarioDeContratoNaCompetencia(Date data, HierarquiaOrganizacional hierarquiaOrganizacional) {
        String hql = " select contrato from ContratoFP contrato " +
            " where :dataAtual between contrato.inicioVigencia and coalesce(contrato.finalVigencia, :dataAtual) " +
            "       and extract(month from contrato.inicioVigencia) = extract(month from :dataAtual) " +
            "       and extract(year from contrato.inicioVigencia) < extract(year from :dataAtual) " +
            "       and not exists (select lanc from LancamentoTercoFeriasAut lanc " +
            "                               where lanc.mes = extract(month from :dataAtual) " +
            "                                   and lanc.ano = extract(year from :dataAtual) " +
            "                                   and lanc.contratoFP = contrato)";
        if (hierarquiaOrganizacional != null) {
            hql += " and contrato in (" +
                " select contratoLotacao from LotacaoFuncional lotacao " +
                " inner join lotacao.vinculoFP contratoLotacao " +
                " inner join lotacao.unidadeOrganizacional unidadeContrato " +
                " inner join VwHierarquiaAdministrativa ho on ho.subordinadaId = unidadeContrato.id  " +
                " where :dataAtual >= lotacao.inicioVigencia " +
                " and :dataAtual <= coalesce(lotacao.finalVigencia,:dataAtual) " +
                " and ho.id in (:unidades)) ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("dataAtual", data);
        if (hierarquiaOrganizacional != null) {
            Date dataAtual = Util.getDataHoraMinutoSegundoZerado(new Date());
            List<HierarquiaOrganizacional> hos = hierarquiaOrganizacionalFacade.buscarHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativa(hierarquiaOrganizacional, dataAtual);
            q.setParameter("unidades", hos.stream().map(ho -> ho.getId()).collect(Collectors.toList()));
            q.setParameter("dataAtual", dataAtual);

        }
        return q.getResultList();
    }


    public BasePeriodoAquisitivo buscarBasePeriodoAquisitivoPorContrato(ContratoFP contratoFP, Date data) {
        Query q = em.createQuery(" select basePA from ContratoFPCargo cargoContrato " +
            " inner join cargoContrato.contratoFP cont " +
            " inner join cargoContrato.basePeriodoAquisitivo basePA " +
            " where cont.id = :idContrato " +
            " and basePA.tipoPeriodoAquisitivo = :tipoPA " +
            " and :data between cargoContrato.inicioVigencia and coalesce(cargoContrato.fimVigencia, :data) ");
        q.setParameter("data", data);
        q.setParameter("idContrato", contratoFP.getId());
        q.setParameter("tipoPA", TipoPeriodoAquisitivo.FERIAS);
        List resultList = q.getResultList();

        if (resultList == null || resultList.isEmpty()) {
            return buscarBasePeriodoAquisitivoPorCargo(contratoFP.getCargo());
        }
        return (BasePeriodoAquisitivo) resultList.get(0);
    }

    public BasePeriodoAquisitivo buscarBasePeriodoAquisitivoPorCargo(Cargo cargo) {
        Query q = em.createQuery(" select basePA from BaseCargo baseC " +
            " inner join baseC.cargo cargo " +
            " inner join baseC.basePeriodoAquisitivo basePA " +
            " where cargo.id = :idCargo " +
            " and basePA.tipoPeriodoAquisitivo = :tipoPA " +
            " and :data between baseC.inicioVigencia and coalesce(baseC.finalVigencia, :data) ");
        q.setParameter("data", SistemaFacade.getDataCorrente());
        q.setParameter("idCargo", cargo.getId());
        q.setParameter("tipoPA", TipoPeriodoAquisitivo.FERIAS);
        List resultList = q.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return (BasePeriodoAquisitivo) resultList.get(0);
        }
        return null;
    }

    public List<Afastamento> buscarAfastamentosQueNaoProcessamFolha(ContratoFP contratoFP) {
        Query q = em.createQuery(" select afas from Afastamento afas " +
            " inner join afas.tipoAfastamento tipo " +
            " where (tipo.processaNormal = false or tipo.processaNormal is null)" +
            "   and afas.contratoFP.id = :contratoFP " +
            "   and ((to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between to_date(to_char(afas.inicio,'mm/yyyy'),'mm/yyyy') " +
            "               and to_date(to_char(coalesce(afas.termino,:data),'mm/yyyy'),'mm/yyyy')) " +
            "           or (afas.inicio <= :data and (afas.retornoInformado = false or afas.retornoInformado is null))) ");
        q.setParameter("data", DataUtil.ultimoDiaMes(DataUtil.dateToLocalDate(SistemaFacade.getDataCorrente())));
        q.setParameter("contratoFP", contratoFP.getId());
        List resultList = q.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<CedenciaContratoFP> buscarCedenciasSemOnus(ContratoFP contratoFP) {
        Query q = em.createQuery(" select cedencia from CedenciaContratoFP cedencia  " +
            " where cedencia.tipoContratoCedenciaFP = :tipoCedencia" +
            "   and cedencia.contratoFP.id = :contratoFP " +
            "   and ((to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cedencia.dataCessao,'mm/yyyy'),'mm/yyyy') " +
            "               and to_date(to_char(coalesce(cedencia.dataRetorno,:data),'mm/yyyy'),'mm/yyyy')) " +
            "           or (cedencia.dataCessao <= :data " +
            "                   and not exists (select 1 from RetornoCedenciaContratoFP retorno " +
            "                                       where retorno.cedenciaContratoFP = cedencia))) ");
        q.setParameter("data", DataUtil.ultimoDiaMes(DataUtil.dateToLocalDate(SistemaFacade.getDataCorrente())));
        q.setParameter("contratoFP", contratoFP.getId());
        q.setParameter("tipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        List resultList = q.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public RetornoCedenciaContratoFP buscarRetornoCedencia(CedenciaContratoFP cedenciaContratoFP) {
        Query q = em.createQuery(" select retorno from RetornoCedenciaContratoFP retorno " +
            "  where retorno.cedenciaContratoFP = :cedencia ");
        q.setParameter("cedencia", cedenciaContratoFP);
        List resultList = q.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return (RetornoCedenciaContratoFP) resultList.get(0);
        }
        return null;
    }

}
