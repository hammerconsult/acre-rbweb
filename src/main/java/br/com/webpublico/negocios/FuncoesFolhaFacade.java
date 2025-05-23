/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.previdencia.ItemPrevidenciaComplementar;
import br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha;
import br.com.webpublico.entidadesauxiliares.ReferenciaFPFuncao;
import br.com.webpublico.entidadesauxiliares.rh.VwFalta;
import br.com.webpublico.entidadesauxiliares.rh.calculo.AfastavelDTO;
import br.com.webpublico.entidadesauxiliares.rh.calculo.PeriodoAquisitivoFLDTO;
import br.com.webpublico.entidadesauxiliares.rh.calculo.ValorEventoPeriodoVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoAvisoPrevio;
import br.com.webpublico.enums.rh.TipoFuncaoGratificada;
import br.com.webpublico.enums.rh.TipoPeq;
import br.com.webpublico.enums.rh.administracaopagamento.TipoFiltroValorPensionista;
import br.com.webpublico.exception.FuncoesFolhaFacadeException;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.interfaces.ValidadorVigenciaFolha;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ObjetoData;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.DescricaoMetodo;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.webpublico.util.DataUtil.*;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

/**
 * @author andre
 */
@Stateless
public class FuncoesFolhaFacade extends AbstractFacade<FolhaDePagamento> {

    private static final Logger logger = LoggerFactory.getLogger(FuncoesFolhaFacade.class);
    private static int MESES_ANO = 12;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    //@EJB
    //private CalculoRetroativoFacade calculoRetroativoFacade;
//    private FolhaDePagamento folha;
//    private Integer ano;
//    private Integer mes;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private RetornoCedenciaContratoFPFacade retornoCedenciaContratoFPFacade;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private BloqueioEventoFPFacade bloqueioEventoFPFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private CalendarioFPFacade calendarioFPFacade;
    @EJB
    private ConfiguracaoFaltasInjustificadasFacade configuracaoFaltasInjustificadasFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    @EJB
    private PrevidenciaVinculoFPFacade previdenciaVinculoFPFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;

    public FuncoesFolhaFacade() {
        super(FolhaDePagamento.class);
    }

    public java.time.LocalDate getPrimeiroDiaDoMesCalculo(EntidadePagavelRH ep) {
        java.time.LocalDate primeiroDiaDoMesDoCalculo = LocalDate.now();
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withDayOfMonth(1);
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withMonth(ep.getMes());
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withYear(ep.getAno());
        return primeiroDiaDoMesDoCalculo;
    }

    public java.time.LocalDate getUltimoDiaDoMesCalculo(EntidadePagavelRH ep) {
        java.time.LocalDate ultimoDiaDoMesDeCalculo = LocalDate.now();
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withMonth(ep.getMes());
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withYear(ep.getAno());
        return ultimoDiaDoMesDeCalculo.with(TemporalAdjusters.lastDayOfMonth());
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LocalDate getDataReferencia(EntidadePagavelRH ep) {
        return getDataReferenciaDateTime(ep);
    }

    public LocalDate getDataReferenciaDateTime(EntidadePagavelRH ep) {
        LocalDate dataReferencia = LocalDate.now();
        dataReferencia = dataReferencia.withYear(ep.getAno());
        dataReferencia = dataReferencia.withMonth(Mes.getMesToInt(ep.getMes()).getNumeroMes());
        dataReferencia = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());
        if (ep.getFolha().getTipoFolhaDePagamento().equals(TipoFolhaDePagamento.RESCISAO)) {
            if (ep instanceof VinculoFP) {
                VinculoFP v = (VinculoFP) ep;
                if (v.getFinalVigencia() != null && dataReferencia.isAfter(dateToLocalDate(v.getFinalVigencia()))) {
                    LocalDate referencia = dateToLocalDate(v.getFinalVigencia());
                    return referencia;
                }
            }
        }
        return dataReferencia;
    }


    @DescricaoMetodo("salarioBase(ep)")
    public Double salarioBase(EntidadePagavelRH ep) {
        if (ep instanceof Aposentadoria) {
            return salarioBaseAposentado(ep);
        }
        if (ep instanceof Pensionista) {
            return salarioBasePensionista(ep);
        } else {
            LocalDate refe = getDataReferencia(ep);
            boolean houveAcesso = false;
//            if (ep instanceof ContratoFP) {
//                String codigo = recuperarOpcaoSalarialCodigo(ep);
//                if (((ContratoFP) ep).getTipoRegime().getCodigo() == 2 && codigo.equals("8")) {
//                    if (temCargoConfianca(ep, ep.getMes(), ep.getAno())) {
//                        CargoConfianca c = obterCargoConfianca(ep);
//                        if ((c.getOpcaoSalarialCC() != null && (c.getOpcaoSalarialCC().getPercentual() == null || c.getOpcaoSalarialCC().getPercentual() == 0))) {
//                            LocalDate LocalDate = getDateCargoConfianca(ep);
//                            refe = LocalDate.minusDays(1).toDate();
//                            houveAcesso = true;
//                        }
//                    }
//                }
//            }
            if (ep.getFolha().isSimulacao()) {
                Double valor = salarioBaseContratoServidorProporcionalizadoSimulacao(ep, getDataReferencia(ep), refe, houveAcesso);
                if (valor != null && valor.compareTo(Double.valueOf(0)) > 0) {
                    return valor;
                }
            }
            return salarioBaseContratoServidorProporcionalizado(ep, getDataReferencia(ep), refe, houveAcesso);
        }

    }

    private LocalDate getDataReferenciaCorretaParaQuemTemAcessoCC(EntidadePagavelRH ep) {
        CargoConfianca c = obterCargoConfianca(ep);
        if ((c.getOpcaoSalarialCC() != null && c.getOpcaoSalarialCC().getPercentual() == null || c.getOpcaoSalarialCC().getPercentual() == 0)) {
            LocalDate retorno = getDataParaAcesso(ep, dateToLocalDate(c.getInicioVigencia()));
            return retorno;
        }
        return null;
    }

    private LocalDate getDateCargoConfianca(EntidadePagavelRH ep) {
        CargoConfianca c = obterCargoConfianca(ep);
        if ((c != null && c.getOpcaoSalarialCC() != null && c.getOpcaoSalarialCC().getPercentual() == null || c.getOpcaoSalarialCC().getPercentual() == 0)) {
//            LocalDate retorno = getDataParaAcesso(ep, new LocalDate(c.getInicioVigencia()));
            CargoConfianca cc1 = obterCargoConfiancaPorData(ep, dateToLocalDate(c.getInicioVigencia()).minusDays(1));
            if (cc1 != null) {
//                CargoConfianca cc2 = obterCargoConfiancaPorData(ep, new LocalDate(cc1.getInicioVigencia()).minusDays(1).toDate());
//                if (cc2 != null) {
//                    CargoConfianca cc3 = obterCargoConfiancaPorData(ep, new LocalDate(cc2.getInicioVigencia()).minusDays(1).toDate());
//                    if (cc3 != null) {
//                        return new LocalDate(cc3.getFinalVigencia());
//                    }else {
//                        return new LocalDate(cc2.getFinalVigencia());
//                    }
//                }else {
                return dateToLocalDate(cc1.getFinalVigencia());
//                }
            } else {
                return dateToLocalDate(c.getInicioVigencia());
            }
        }
        return null;
    }

    private LocalDate getDataParaAcesso(EntidadePagavelRH ep, LocalDate vigencia) {
        CargoConfianca c = obterCargoConfiancaPorData(ep, vigencia.minusDays(1));
        if (c != null) {
            return getDataParaAcesso(ep, dateToLocalDate(c.getFinalVigencia() != null ? c.getFinalVigencia() : c.getInicioVigencia()));
        } else {
            return vigencia;
        }
    }


    public Double salarioBaseContratoServidorProporcionalizadoSimulacao(EntidadePagavelRH ep, LocalDate dataReferenciapcs, LocalDate dataReferencia, boolean houveAcessoAnterior) {
        String hql = "";

        hql = "select new EnquadramentoFuncional(ef.inicioVigencia,ef.finalVigencia,pcs.vencimentoBase) from EnquadramentoFuncional ef, EnquadramentoPCSSimulacao pcs" +
            " where ef.contratoServidor.id = :parametro and pcs.categoriaPCS = ef.categoriaPCS and pcs.progressaoPCS = ef.progressaoPCS" +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(ef.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(ef.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy')  " +
            " and :dataVigenciaPcs between pcs.inicioVigencia and coalesce(pcs.finalVigencia,:dataVigenciaPcs) order by ef.inicioVigencia ";

        double salarioBase = 0;
        try {
            Query q = em.createQuery(hql);
            q.setParameter("parametro", ep.getIdCalculo());
            q.setParameter("dataVigencia", localDateToDate(dataReferencia));
            q.setParameter("dataVigenciaPcs", localDateToDate(dataReferenciapcs));
            List<ValidadorVigenciaFolha> resultList = new LinkedList<>();
            resultList = q.getResultList();
            if (resultList.isEmpty() || (resultList.get(0) == null)) {
                return 0.0;
            }
            if (isSomenteUmEnquadramento(houveAcessoAnterior, resultList)) {
                return resultList.get(0).getValor().doubleValue();
            } else {
                salarioBase = realizarMediaSalarial(ep, dataReferencia, resultList);
            }
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método salarioBase", re);
        }
        return salarioBase;
    }

    public Double salarioBaseContratoServidorProporcionalizado(EntidadePagavelRH ep, LocalDate dataReferenciapcs, LocalDate dataReferencia, boolean houveAcessoAnterior) {
        String hql = "select new EnquadramentoFuncional(ef.inicioVigencia,ef.finalVigencia,pcs.vencimentoBase) from EnquadramentoFuncional ef, EnquadramentoPCS pcs" +
            " where ef.contratoServidor.id = :parametro and pcs.categoriaPCS = ef.categoriaPCS and pcs.progressaoPCS = ef.progressaoPCS" +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(ef.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(ef.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy')  " +
            " and :dataVigenciaPcs between pcs.inicioVigencia and coalesce(pcs.finalVigencia,:dataVigenciaPcs) order by ef.inicioVigencia ";


        double salarioBase = 0;
        try {
            Query q = em.createQuery(hql);
            q.setParameter("parametro", ep.getIdCalculo());
            q.setParameter("dataVigencia", localDateToDate(dataReferencia));
            q.setParameter("dataVigenciaPcs", localDateToDate(dataReferenciapcs));
            List<ValidadorVigenciaFolha> resultList = new LinkedList<>();
            resultList = q.getResultList();
            if (resultList.isEmpty() || (resultList.get(0) == null)) {
                return 0.0;
            }
            if (isSomenteUmEnquadramento(houveAcessoAnterior, resultList)) {
                return resultList.get(0).getValor().doubleValue();
            } else {
                salarioBase = realizarMediaSalarial(ep, dataReferencia, resultList);
            }
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método salarioBase", re);
        }
        return salarioBase;
    }

    private boolean isSomenteUmEnquadramento(boolean houveAcessoAnterior, List<ValidadorVigenciaFolha> resultList) {
        if (resultList.size() == 1 || houveAcessoAnterior) return true;
        if (resultList.size() > 2) return true;
        int contadorFinalVigenciaAberta = 0;

        for (ValidadorVigenciaFolha enquadramentoFuncional : resultList) {
            if (enquadramentoFuncional.getFimVigencia() == null) {
                contadorFinalVigenciaAberta++;
            }
        }
        if (contadorFinalVigenciaAberta == resultList.size()) return true;
        return false;
    }

    private double realizarMediaSalarial(EntidadePagavelRH ep, LocalDate dataReferencia, List<ValidadorVigenciaFolha> resultList) {
        BigDecimal valor = BigDecimal.ZERO;
        for (Iterator it = resultList.iterator(); it.hasNext(); ) {
            ValidadorVigenciaFolha vig = (ValidadorVigenciaFolha) it.next();
            if (vig.getInicioVigencia().equals(vig.getInicioVigencia()) && vig.getInicioVigencia().equals(vig.getFimVigencia())) {
                it.remove();
            }
        }
        for (ValidadorVigenciaFolha enquadramentoFuncional : resultList) {
            LocalDate refe = dataReferencia;
            DataVigencia dataVigencia = equalizarDatasDentroDoMes(dateToLocalDate(enquadramentoFuncional.getInicioVigencia()), (enquadramentoFuncional.getFimVigencia() != null ? dateToLocalDate(enquadramentoFuncional.getFimVigencia()) : null), refe);
            logger.debug("Vigência: " + dataVigencia);
            logger.debug("total de dias do enquadramento:" + diasEntre(dataVigencia.getInicioVigencia(), dataVigencia.getFinalVigencia()));
            int diasNoEnquadramento = diasEntre(dataVigencia.getInicioVigencia(), dataVigencia.getFinalVigencia());
            valor = valor.add(BigDecimal.valueOf(enquadramentoFuncional.getValor().doubleValue() / refe.lengthOfMonth() * diasNoEnquadramento));
        }
        logger.debug("media salarial:  " + valor);
        return valor.doubleValue();
    }


    private int totalDiasNoMes(EntidadePagavelRH ep, LocalDate dataReferencia, List<ValidadorVigenciaFolha> resultList) {
        BigDecimal valor = BigDecimal.ZERO;
        for (Iterator it = resultList.iterator(); it.hasNext(); ) {
            ValidadorVigenciaFolha vig = (ValidadorVigenciaFolha) it.next();
            for (ValidadorVigenciaFolha validadorVigenciaFolha : resultList) {
                if (validadorVigenciaFolha.getInicioVigencia().equals(vig.getInicioVigencia()) && vig.getInicioVigencia().equals(vig.getFimVigencia())) {
                    it.remove();
                }
            }
        }

        for (ValidadorVigenciaFolha enquadramentoFuncional : resultList) {
            LocalDate refe = dataReferencia;
            DataVigencia dataVigencia = equalizarDatasDentroDoMes(dateToLocalDate(enquadramentoFuncional.getInicioVigencia()), dateToLocalDate(enquadramentoFuncional.getFimVigencia()), refe);
            logger.debug("Vigência: " + dataVigencia);
            logger.debug("total de dias do enquadramento:" + diasEntre(dataVigencia.getInicioVigencia(), dataVigencia.getFinalVigencia()));
            int diasNoEnquadramento = diasEntre(dataVigencia.getInicioVigencia(), dataVigencia.getFinalVigencia());
            valor = valor.add(BigDecimal.valueOf(enquadramentoFuncional.getValor().doubleValue() / refe.lengthOfMonth() * diasNoEnquadramento));
        }
        logger.debug("media salarial:  " + valor);
        return valor.intValue();
    }


    private DataVigencia equalizarDatasDentroDoMes(LocalDate inicioVigencia, LocalDate finalVigencia, LocalDate dataReferencia) {
        LocalDate inicio = inicioVigencia;
        LocalDate fim = finalVigencia == null ? dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth()) : finalVigencia;
        LocalDate inicioAux = inicio;
        LocalDate fimAux = fim;
        LocalDate referencia = dataReferencia;
        if (DataUtil.isEqualYearAndMonth(inicio, referencia) && DataUtil.isEqualYearAndMonth(fim, referencia)) {
            return new DataVigencia(inicio, fim, referencia);
        }
        if (DataUtil.isEqualYearAndMonth(inicio, referencia)) {
            fimAux = inicio.withDayOfMonth(inicio.lengthOfMonth());
        }
        if (DataUtil.isEqualYearAndMonth(fim, referencia)) {
            inicioAux = fim.withDayOfMonth(1);
        }
        if (inicio.isBefore(referencia.withDayOfMonth(1))) {
            inicioAux = referencia.withDayOfMonth(1);
        }
        if (fim.isAfter(referencia.withDayOfMonth(referencia.lengthOfMonth()))) {
            fimAux = referencia.withDayOfMonth(referencia.lengthOfMonth());
        }
        return new DataVigencia(inicioAux, fimAux, referencia);
    }


    public Double salarioBaseContratoServidor(EntidadePagavelRH ep, LocalDate dataReferenciapcs, LocalDate dataReferencia) {
        String sql = " SELECT coalesce(c.vencimentoBase,0) FROM EnquadramentoFuncional a "
            + " INNER JOIN VinculoFP b ON a.contratoservidor_id = b.id "
            + " INNER JOIN EnquadramentoPCS c ON a.categoriaPCS_id = c.categoriaPCS_id"
            + " AND a.progressaoPCS_id = c.progressaoPCS_id "
            + " WHERE b.id = :parametro "
            + " AND :dataVigencia between a.inicioVigencia AND coalesce(a.finalVigencia,:dataVigencia) "
            + " AND :dataVigenciaPcs between c.inicioVigencia AND coalesce(c.finalVigencia,:dataVigenciaPcs) ";
        double salarioBase = 0;
        try {
            Query q = em.createNativeQuery(sql);
            q.setParameter("parametro", ep.getIdCalculo());
            q.setParameter("dataVigencia", localDateToDate(dataReferencia));
            q.setParameter("dataVigenciaPcs", localDateToDate(dataReferenciapcs));
            q.setMaxResults(1);
            List resultList = q.getResultList();
            if (resultList.isEmpty() || (resultList.get(0) == null)) {
                return 0.0;
            }
            salarioBase = ((BigDecimal) resultList.get(0)).doubleValue();
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método salarioBase", re);
        }
        ////System.out.println("Salario Base: " + salarioBase);
        return salarioBase;
    }

    public Double salarioBaseAposentado(EntidadePagavelRH ep) {
        String sql = " SELECT coalesce(c.vencimentoBase,0) FROM EnquadramentoFuncional a "
            + " INNER JOIN VinculoFP b ON a.contratoservidor_id = b.id "
            + " INNER JOIN EnquadramentoPCS c ON a.categoriaPCS_id = c.categoriaPCS_id"
            + " AND a.progressaoPCS_id = c.progressaoPCS_id "
            + " WHERE b.id = :parametro "
            + " AND :dataVigencia between c.inicioVigencia AND coalesce(c.finalVigencia,:dataVigencia)"
            + " AND (:dataVigencia between a.inicioVigencia AND coalesce(a.finalVigencia,:dataVigencia)"
            + " or a.inicioVigencia =(select max(enq.inicioVigencia) from EnquadramentoFuncional enq where enq.contratoservidor_id = b.id))" +
            "   order by a.inicioVigencia desc ";
        double salarioBase = 0;
        try {
            Aposentadoria ap = (Aposentadoria) ep;
            Query q = em.createNativeQuery(sql);
            q.setParameter("parametro", ap.getContratoFP().getIdCalculo());
            q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
            q.setMaxResults(1);
            List resultList = q.getResultList();
            if (resultList.isEmpty() || (resultList.get(0) == null)) {
                return 0.0;
            }
            salarioBase = ((BigDecimal) resultList.get(0)).doubleValue();
            return salarioBase;
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método salarioBase", re);
        }
    }

    public Double salarioBasePensionista(EntidadePagavelRH ep) {
        Double valor = valorCargoComissaoInstituidor(ep);
        if (valor > 0) {
            return valor;
        }
        String sql = " SELECT coalesce(c.vencimentoBase,0) FROM EnquadramentoFuncional a "
            + " INNER JOIN VinculoFP b ON a.contratoservidor_id = b.id "
            + " INNER JOIN EnquadramentoPCS c ON a.categoriaPCS_id = c.categoriaPCS_id"
            + " AND a.progressaoPCS_id = c.progressaoPCS_id "
            + " WHERE b.id = :parametro "
            + " AND :dataVigencia between c.inicioVigencia AND coalesce(c.finalVigencia,:dataVigencia) " +
            "   order by a.inicioVigencia desc ";
        double salarioBase = 0;
        try {
            Pensionista p = (Pensionista) ep;
            Query q = em.createNativeQuery(sql);
            q.setParameter("parametro", p.getPensao().getContratoFP().getIdCalculo());
            q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
            q.setMaxResults(1);
            List resultList = q.getResultList();
            if (resultList.isEmpty() || (resultList.get(0) == null)) {
                return 0.0;
            }
            salarioBase = ((BigDecimal) resultList.get(0)).doubleValue();
            //System.out.println("Salario Base: " + salarioBase);
            return salarioBase;
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método salarioBase", re);
        }
    }


    public Double valorTotalEventoRetroativos(String codigo, EntidadePagavelRH ep, Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(case when(itemFicha.tipoEventoFP = 'VANTAGEM') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append(" inner join itemFicha.fichaFinanceiraFP ficha");
        sql.append(" inner join ficha.folhaDePagamento folha");
        sql.append(" inner join itemFicha.eventoFP evento");
        sql.append(" where ");
        sql.append("   itemFicha.ano = :ano");
        sql.append("   and itemFicha.mes = :mes");
        sql.append("   and evento.codigo = :codigo");
        sql.append("   and ficha.vinculoFP = :vinculo");
        sql.append("   and itemFicha.tipoCalculoFP = :tipoCalculo and itemFicha.tipoEventoFP in ('VANTAGEM','DESCONTO')");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMes());
        q.setParameter("codigo", codigo);
        q.setParameter("vinculo", ep);
        q.setParameter("tipoCalculo", TipoCalculoFP.RETROATIVO);
        Object result = q.getSingleResult();

        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }

    @DescricaoMetodo("obterModalidadeContratoFP(ep)")
    public ModalidadeContratoFP obterModalidadeContratoFP(EntidadePagavelRH ep) {

        StringBuilder sbHql = new StringBuilder();

        sbHql.append("select mfp from ContratoFP c ");
        sbHql.append("inner join c.modalidadeContratoFP mfp ");
        sbHql.append("where c.id = :idParam");
        ModalidadeContratoFP obterModalidadeContratoFP;
        try {
            VinculoFP vinculo = (VinculoFP) ep;
            Query q = em.createQuery(sbHql.toString());
            q.setParameter("idParam", vinculo.getId());
            //logger.debug("Codigo: {0}", ep.getIdCalculo());
            //logger.debug("Entidade Pagavel: {0}", ep);
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return new ModalidadeContratoFP(-1L);
            }
            obterModalidadeContratoFP = (ModalidadeContratoFP) resultList.get(0);
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método obterModalidadeContratoFP", e);
        }
        return obterModalidadeContratoFP;
    }

    @DescricaoMetodo("obterTipoRegime(ep)")
    public TipoRegime obterTipoRegime(EntidadePagavelRH ep) {
        StringBuilder sbHql = new StringBuilder();
        sbHql.append("select tipoRegime from ContratoFP contratoFP ");
        sbHql.append("inner join contratoFP.tipoRegime tipoRegime ");
        sbHql.append("where contratoFP.id = :vinculo");
        TipoRegime obterTipoRegime;
        try {
            Query q = em.createQuery(sbHql.toString());
            q.setParameter("vinculo", ep.getIdCalculo());
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return new TipoRegime();
            }
            obterTipoRegime = (TipoRegime) resultList.get(0);
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método obterTipoRegimo", re);
        }


        return obterTipoRegime;
    }

    @DescricaoMetodo("obterDiasDoMes(mes, ano)")
    public Integer obterDiasDoMes(int numeroMes, int ano) {
        return Util.getDiasMes(numeroMes, ano);
    }

    @DescricaoMetodo("obterModalidadeBeneficioFP(ep)")
    public ModalidadeBeneficioFP obterModalidadeBeneficioFP(EntidadePagavelRH ep) {
        StringBuilder sbHql = new StringBuilder();
        sbHql.append("select modalidadeBeneficioFP from ModalidadeBeneficioFP modalidadeBeneficioFP ");
        sbHql.append("inner join modalidadeBeneficioFP.beneficios beneficio ");
        sbHql.append("where beneficio.id = :idParam");
        ModalidadeBeneficioFP obterModalideBeneficio;
        try {
            Query q = em.createQuery(sbHql.toString());
            q.setParameter("idParam", ep.getIdCalculo());
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return null;
            }
            obterModalideBeneficio = (ModalidadeBeneficioFP) resultList.get(0);
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método obterModalideBeneficioFP", re);
        }
        return obterModalideBeneficio;
    }

    @DescricaoMetodo("obterValorBeneficioFP(ep, mes, ano)")
    public Double obterValorBeneficioFP(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo) {
        StringBuilder sbHql = new StringBuilder();
        sbHql.append("select valorBeneficioFP.valor from ValorBeneficioFP valorBeneficioFP ");
        sbHql.append("where valorBeneficioFP.beneficioFP.contratoFP.id = :idParam ");
        sbHql.append("and valorBeneficioFP.finalVigencia >= :dataParam");
        BigDecimal obterValorBeneficio = BigDecimal.ZERO;

        try {
            Query q = em.createQuery(sbHql.toString());
            q.setParameter("idParam", ep.getIdCalculo());
            q.setParameter("dataParam", localDateToDate(getDataReferencia(ep)));
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return null;
            }
            obterValorBeneficio = (BigDecimal) resultList.get(0);
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método obterValorBeneficio", re);
        }
        return obterValorBeneficio.doubleValue();

    }

    public boolean isFolhaRescisao(EntidadePagavelRH ep) {
        return ep.getFolha().getTipoFolhaDePagamento().equals(TipoFolhaDePagamento.RESCISAO);
    }

    public boolean caracterizaDevolucaoDeValor(EntidadePagavelRH ep, Double diasMes) {
        BigDecimal diasTrabalhados = buscarDiasTrabalhadadosDaFichaFinanceira((VinculoFP) ep, ep.getMes(), ep.getAno(), TipoFolhaDePagamento.NORMAL, diasMes);
        BigDecimal diasDoMes = BigDecimal.valueOf(diasMes);
        return isFolhaRescisao(ep) && isFolhaNormalEfetivadaNoMesmoMes(ep) && exoneradoNoUltimoDiaDoMes(ep) && diasTrabalhados.compareTo(diasDoMes) == 0;
    }

    public boolean exoneradoNoUltimoDiaDoMes(EntidadePagavelRH ep) {
        VinculoFP v = (VinculoFP) ep;
        LocalDate finalVigencia = v.getFinalVigencia() != null ? dateToLocalDate(v.getFinalVigencia()) : null;
        LocalDate dataMes = LocalDate.now().withMonth(ep.getMes()).withYear(ep.getAno()).withDayOfMonth(1);
        dataMes = dataMes.withDayOfMonth(dataMes.lengthOfMonth());
        if (finalVigencia == null) return false;
        if (dataMes.getDayOfMonth() == finalVigencia.getDayOfMonth()) {
            return false;
        }
        return true;
    }

    private boolean isFolhaNormalEfetivadaNoMesmoMes(EntidadePagavelRH ep) {
//        Date calculada = folhaDePagamentoFacade.recuperaDataUltimaFolhaCalculadaEm(TipoFolhaDePagamento.NORMAL);
        ItemCalendarioFP itemCalendarioFP = calendarioFPFacade.buscarDataCalculoCalendarioFPPorFolha(ep.getFolha());
//        LocalDate calculadaEm = calculada != null ? new LocalDate(calculada) : null;
        VinculoFP v = (VinculoFP) ep;
        LocalDate finalVigencia = v.getFinalVigencia() != null ? dateToLocalDate(v.getFinalVigencia()) : null;
        LocalDate dataUltimoDiaProcessamento = itemCalendarioFP.getDataUltimoDiaProcessamento() != null ? dateToLocalDate(itemCalendarioFP.getDataUltimoDiaProcessamento()) : null;

        if (finalVigencia != null && dataUltimoDiaProcessamento != null) {
            if (finalVigencia.getDayOfMonth() == dataUltimoDiaProcessamento.withDayOfMonth(dataUltimoDiaProcessamento.lengthOfMonth()).getDayOfMonth()) {
                return false;
            }
        }
//        calculadaEm = dataCalendario;
        if (finalVigencia != null && (dataUltimoDiaProcessamento.getMonthValue() == finalVigencia.getMonthValue() && dataUltimoDiaProcessamento.getYear() == finalVigencia.getYear())) {
            if (!fichaFinanceiraFPFacade.totalItemFichaFinanceiraFP(ep, ep.getMes(), ep.getAno(), TipoFolhaDePagamento.NORMAL, false).isEmpty())
                return true;
        }

        return false;
    }

    public BigDecimal buscarDiasTrabalhadadosDaFichaFinanceira(VinculoFP vinculoFP, int mes, int ano, TipoFolhaDePagamento tipo, Double diasMes) {
        Query q = this.em.createNativeQuery(
            "select ficha.diasTrabalhados " +
                "  from FichaFinanceiraFP ficha inner join FolhaDePagamento folha on folha.id = ficha.folhadepagamento_id " +
                " where ficha.vinculoFP_id = :vinculo " +
                "   and folha.mes = :mes " +
                "   and folha.ano = :ano " +
                "   and folha.tipoFolhaDePagamento = :tipoFolha");
        q.setParameter("vinculo", vinculoFP);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("ano", ano);
        q.setParameter("tipoFolha", tipo.name());
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.valueOf(diasMes);
    }


    public boolean temEventosPagosEmOutraFolhaNoMesmoMesParaRescisao(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, EntidadePagavelRH vinculo, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        return TipoFolhaDePagamento.isFolhaRescisao(detalheProcessamentoFolha.getDetalhesCalculoRH().getFolhaDePagamento()) && !eventosPagosEmOutraFolhaNoMesmoMes((VinculoFP) vinculo).isEmpty();
    }


    public List<EventoTotalItemFicha> eventosPagosEmOutraFolhaNoMesmoMes(EntidadePagavelRH vinculo) {
        List<EventoTotalItemFicha> eventos = fichaFinanceiraFPFacade.buscarTotalEventosItemFichaFinanceiraParaRetroativo(vinculo, vinculo.getAno(), vinculo.getMes());
        return eventos;
    }

    public boolean eventosPagosEmOutraFolhaNoMesmoMesComSaldo(EntidadePagavelRH vinculo) {
        List<EventoTotalItemFicha> eventos = fichaFinanceiraFPFacade.buscarTotalEventosItemFichaFinanceiraParaRetroativoAndNormal(vinculo, vinculo.getAno(), vinculo.getMes());
        BigDecimal soma = BigDecimal.ZERO;
        BigDecimal subtracao = BigDecimal.ZERO;

        for (EventoTotalItemFicha evento : eventos) {
            if (TipoEventoFP.VANTAGEM.equals(evento.getTipoEventoFP())) {
                soma = soma.add(evento.getTotal());
            }
            if (TipoEventoFP.DESCONTO.equals(evento.getTipoEventoFP())) {
                subtracao = subtracao.add(evento.getTotal());
            }
        }

        BigDecimal liquido = soma.subtract(subtracao);

        return liquido.compareTo(BigDecimal.ZERO) == 0;
    }

    public Integer diasDeDevolucao(EntidadePagavelRH ep, Double diasDoMes) {
        if (caracterizaDevolucaoDeValor(ep, diasDoMes)) {
            logger.debug("caracterizou devolução de dias trabalhados.. (rescisão depois do calculo da folha e antes do final do mês) ");
            LocalDate inicioVigencia = dateToLocalDate(((VinculoFP) ep).getFinalVigencia());
            LocalDate finalVigencia = getDataReferenciaDateTime(ep).withDayOfMonth(getDataReferenciaDateTime(ep).lengthOfMonth());
//Não precisa somar mais 1 dia, visto que no ultimo dia de trabalho ele trabalhou.. ex: final vigencia dia 25 ultimo dia do mes= 28 conta-se 26,27,28
            return daysBetween(inicioVigencia, finalVigencia);
        }
        return 0;
    }

    public Double diasTrabalhados(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo, TipoDiasTrabalhados tipo, String tipoFuncao, LocalDate dataCorteInicial, LocalDate dataCorteFinal) {
        Integer somaDia = 1;
        VinculoFP vinculo = (VinculoFP) ep;
        LocalDate inicioVigencia = null;
        Date dataInicioEfeitosFinaceiros = buscarDataProvimentoReintegracao(ep);
        if (dataInicioEfeitosFinaceiros != null) {
            inicioVigencia = dateToLocalDate(dataInicioEfeitosFinaceiros);
        } else {
            inicioVigencia = dateToLocalDate(vinculo.getInicioVigencia());
        }
        LocalDate finalVigencia = vinculo.getFinalVigencia() == null ? null : dateToLocalDate(vinculo.getFinalVigencia());
        if (tipo.equals(TipoDiasTrabalhados.FG)) {
            List<ValidadorVigenciaFolha> fgs = Lists.newArrayList();
            if (TipoFuncaoGratificada.GESTOR_RECURSOS.name().equals(tipoFuncao) || TipoFuncaoGratificada.CONTROLE_INTERNO.name().equals(tipoFuncao)) {
                TipoFuncaoGratificada tipoFuncaoGratificada = TipoFuncaoGratificada.valueOf(tipoFuncao);
                ValidadorVigenciaFolha validadorVigenciaFolha = funcaoGratificadaFacade.buscarFuncaoGratificadaVigente(tipoFuncaoGratificada, ep.getContratoFP(), localDateToDate(getDataReferencia(ep)));
                fgs.add(validadorVigenciaFolha);
            } else {
                fgs.addAll(obterFuncaoGratificada(ep, tipoFuncao));
            }
            if (fgs == null || (fgs != null && fgs.isEmpty())) {
                return 0.0;
            } else {
                DataVigencia vigencia = getVigenciaCorreta(fgs, ep);

                inicioVigencia = vigencia.getInicioVigencia();
                finalVigencia = vigencia.getFinalVigencia();
            }
        }
        if (tipo.equals(TipoDiasTrabalhados.CC)) {
            List<ValidadorVigenciaFolha> cc = cargosConfianca(ep);
            if (cc == null || (cc != null && cc.isEmpty())) {
                return 0.0;
            } else {
                DataVigencia vigencia = getVigenciaCorreta(cc, ep);
                inicioVigencia = vigencia.getInicioVigencia();
                finalVigencia = vigencia.getFinalVigencia();
            }
        }
        if (tipo.equals(TipoDiasTrabalhados.AS)) {
            List<ValidadorVigenciaFolha> cc = acessoSubsidio(ep);
            if (cc == null || (cc != null && cc.isEmpty())) {
                return 0.0;
            } else {
                DataVigencia vigencia = getVigenciaCorreta(cc, ep);
                inicioVigencia = vigencia.getInicioVigencia();
                finalVigencia = vigencia.getFinalVigencia();
            }
        }

        LocalDate varIniMes = LocalDate.now();
        LocalDate varFimMes = LocalDate.now();
        if (dataCorteInicial != null) {
            varIniMes = dataCorteInicial;
        } else {
            varIniMes = dateToLocalDate(Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mesDeCalculo).ordinal(), anoDeCalculo));
        }
        if (dataCorteFinal != null) {
            varFimMes = dataCorteFinal;
        } else {
            varFimMes = varIniMes.withDayOfMonth(varIniMes.lengthOfMonth());
        }

        LocalDate dataIni = varIniMes;
        LocalDate dataInicioVigencia = inicioVigencia;

        if (dataInicioVigencia.isAfter(varFimMes)) {
            return 0.0;
        }

        if (dataIni.isBefore(dataInicioVigencia)) {
            varIniMes = inicioVigencia;
        }

        Date dataRetornoCedencia = retornoCedenciaContratoFPFacade.recuperaDataRetorno(vinculo, getDataReferencia(ep));
        if (dataRetornoCedencia != null) {
            LocalDate dataRetorno = dateToLocalDate(dataRetornoCedencia);
            if (dataIni.isBefore(dataRetorno)) {
                varIniMes = dataRetorno;
                somaDia = 0;
            }
        }

        LocalDate dataFim = varFimMes;
//        Date finalVigencia = vinculo.getFinalVigencia();
        if (finalVigencia != null) {
            LocalDate dataFinalVigencia = finalVigencia;
            if (dataFim.isAfter(dataFinalVigencia)) {
                varFimMes = finalVigencia;
            }
        }

        Date finalVigenciaDataCedencia = cedenciaContratoFPFacade.recuperaDataCessao(vinculo, getDataReferencia(ep));
        if (finalVigenciaDataCedencia != null) {
            LocalDate dataFinalVigencia = dateToLocalDate(finalVigenciaDataCedencia);
            if (dataFim.isAfter(dataFinalVigencia)) {
                varFimMes = dataFinalVigencia;
                somaDia = 0;
            }
        }
        LocalDate varBeginMes = varIniMes;
        LocalDate varEndMes = varFimMes;

        if (varBeginMes.isAfter(varEndMes) && !varBeginMes.isEqual(varEndMes)) {
            return 0.0;
        }
        Integer totalDiasTrabalhados = varFimMes.getDayOfMonth() - varIniMes.getDayOfMonth() + somaDia;
        Double diasDeDireitoNoAfastamento = 0.0;
        Period p = null;
        for (Afastamento a : afastamentoFacade.listaAfastamentoVigenteMes(vinculo, anoDeCalculo, Mes.getMesToInt(mesDeCalculo))) {
            if (a.getTipoAfastamento().getClasseAfastamento().equals(ClasseAfastamento.FALTA_JUSTIFICADA)) {
                continue;
            }
            LocalDate varInicio = dateToLocalDate(a.getInicio());
            LocalDate varFim = dateToLocalDate(a.getTermino());


            if (a.getTipoAfastamento().getProcessaNormal() && !a.getTipoAfastamento().getNaoCalcularFichaSemRetorno()) {
                continue;
            }

            if (a.getTipoAfastamento().getDescontarDiasTrabalhados() != null && a.getTipoAfastamento().getDescontarDiasTrabalhados()) {

                if (a.getCarencia() != null) {
                    varInicio = varInicio.plusDays(a.getCarencia());
                }

                if (varInicio.isBefore(varIniMes)) {
                    varInicio = varIniMes;
                }

                if (varFim.isAfter(varFimMes)) {
                    varFim = varFimMes;
                }

//                if (a.getTipoAfastamento().getReferenciaFP() != null) {
//                    Integer totalDiasAfastados;
//                    List<Afastamento> afastamentos = afastamentoFacade.recuperarTodosAfastamentos(vinculo);
//
//                    Afastamento primeiroAfastamento = new Afastamento(buscarPrimeiroAfastamentoPeloTipoConsecutivo(a, afastamentos).getInicio(), a.getTermino());
//
//                    LocalDate dataReferencia;
//                    if (afastamentos.get(0) != null && afastamentos.get(0).getRetornoInformado()) {
//                        dataReferencia = dateToLocalDate(afastamentos.get(0).getTermino());
//                    } else {
//                        dataReferencia = getDataReferenciaDateTime(ep);
//                    }
//                    totalDiasAfastados = diasEntre(dateToLocalDate(primeiroAfastamento.getInicio()), dataReferencia);
//
//                    ReferenciaFPFuncao f = getTotalDiasAfastados(ep, totalDiasAfastados, a.getTipoAfastamento().getReferenciaFP());
//                    diasDeDireitoNoAfastamento = f.getValor();
//
//                    logger.debug("Caracterizou a lei de afastamento para maternidade ou saúde :" + diasDeDireitoNoAfastamento);
//                    if (diasDeDireitoNoAfastamento == 30) {
//                        return Double.parseDouble(getDataReferenciaDateTime(ep).lengthOfMonth() + "");
//                    }
//                    return diasDeDireitoNoAfastamento;
//                }

                if (!a.getRetornoInformado()) {
                    if (varFim.isBefore(varFimMes)) {
                        varFim = varFimMes;
                    }
                }

                if (dataInferior(varInicio, varFim)) {
                    continue;
                }


                totalDiasTrabalhados = totalDiasTrabalhados - (varFim.getDayOfMonth() - (varInicio.getDayOfMonth()) + 1);
            } else if (!a.getRetornoInformado()) {
                if (a.getCarencia() != null && !a.getTipoAfastamento().getNaoCalcularFichaSemRetorno()) {
                    varInicio.plusDays(a.getCarencia());
                }

                if (varInicio.isBefore(varIniMes)) {
                    varInicio = varIniMes;
                }
                if (varFim.isAfter(varFimMes)) {
                    varFim = varFimMes;
                }
                if (!a.getRetornoInformado()) {
                    if (varFim.isBefore(varFimMes)) {
                        varFim = varFimMes;
                    }
                }
                if (dataInferior(varInicio, varFim)) {
                    continue;
                }
                totalDiasTrabalhados = totalDiasTrabalhados - (varFim.getDayOfMonth() - (varInicio.getDayOfMonth()) + 1);
            }

        }

        if (totalDiasTrabalhados.intValue() < 0) {
            totalDiasTrabalhados = 0;
        }
        if (diasDeDireitoNoAfastamento > 0) {
            totalDiasTrabalhados = obterDiasDoMes(ep.getMes(), ep.getAno());
            if (p != null && p.getMonths() >= 3) {
                logger.debug("Caracterizou a lei de afastamento para maternidade ou saúde :" + diasDeDireitoNoAfastamento);
                if (diasDeDireitoNoAfastamento == 30) {
                    diasDeDireitoNoAfastamento = Double.parseDouble(getDataReferenciaDateTime(ep).lengthOfMonth() + "");
                }
                totalDiasTrabalhados = diasDeDireitoNoAfastamento.intValue();
            }
        }
        return totalDiasTrabalhados.doubleValue();
    }

    public Date buscarDataProvimentoReintegracao(EntidadePagavelRH ep) {
        String hql = " select reintegra.inicioEfeitosFinanceiros "
            + "          from Reintegracao reintegra "
            + "           where reintegra.contratofp_id = :vinculo ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("vinculo", ep.getIdCalculo());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (Date) resultList.get(0);
    }

    private Afastamento buscarPrimeiroAfastamentoPeloTipoConsecutivo(Afastamento afastamentoIteracao, List<Afastamento> afastamentos) {
        Afastamento afastamentoRecuperado = new Afastamento();
        for (Afastamento afastamento : afastamentos) {
            if (afastamento.getTipoAfastamento() == afastamentoIteracao.getTipoAfastamento()) {
                afastamentoRecuperado = afastamento;
            } else if (afastamentoRecuperado.getId() != null) {
                break;
            }
        }
        return afastamentoRecuperado;

    }

    public boolean dataInferior(LocalDate varInicio, LocalDate varFim) {
        LocalDate dtInicio = varInicio;
        LocalDate dtFim = varFim;
        if (!dtInicio.isEqual(dtFim)) {
            if (varInicio.isAfter(varFim)) {
                return true;
            }
        }
        return false;
    }

    private DataVigencia getVigenciaCorreta(List<ValidadorVigenciaFolha> vigencias, EntidadePagavelRH ep) {
        LocalDate inicioVigencia;
        LocalDate finalVigencia;
        DataVigencia dataVigencia = new DataVigencia();
        if (vigencias.size() == 1) {
            ValidadorVigenciaFolha fg = vigencias.get(0);
            dataVigencia.setInicioVigencia(dateToLocalDate(fg.getInicioVigencia()));
            dataVigencia.setFinalVigencia(fg.getFimVigencia() != null ? dateToLocalDate(fg.getFimVigencia()) : null);
            return dataVigencia;
        }

        for (ValidadorVigenciaFolha vigencia : vigencias) {
            inicioVigencia = vigencia.getInicioVigencia() != null ? dateToLocalDate(vigencia.getInicioVigencia()) : null;
            finalVigencia = vigencia.getFimVigencia() != null ? dateToLocalDate(vigencia.getFimVigencia()) : null;
            if (dataVigencia.getInicioVigencia() == null) {
                dataVigencia.setInicioVigencia(inicioVigencia);
            } else {
                dataVigencia.setInicioVigencia(dataVigencia.getInicioVigencia().minusDays(inicioVigencia.getDayOfYear()));
            }
            if (dataVigencia.getFinalVigencia() == null) {
                dataVigencia.setFinalVigencia(finalVigencia);
            } else {
                dataVigencia.setFinalVigencia(finalVigencia != null ? dataVigencia.getFinalVigencia().plusDays(finalVigencia.getDayOfYear()) : null);
            }
        }

        return dataVigencia;
    }

    public ReferenciaFPFuncao getTotalDiasAfastados(EntidadePagavelRH ep, Integer totalDias, ReferenciaFP ref) {
        ReferenciaFPFuncao item = obterReferenciaFaixaFP(ep, ref.getCodigo(), totalDias.doubleValue(), ep.getMes(), ep.getAno());
        return item;
    }

    @DescricaoMetodo("obterItensBaseFP(String[codigo])")
    public List<ItemBaseFP> obterItensBaseFP(String codigo) {
        String hql = " select item from ItemBaseFP item "
            + " inner join item.baseFP base "
            + " inner join fetch item.eventoFP e "
            + " where lower(base.codigo) = :codigo order by e.ordemProcessamento";
        List<ItemBaseFP> obterItens = Lists.newArrayList();
        try {
            Query q = em.createQuery(hql.toString());
            q.setParameter("codigo", codigo.toLowerCase().trim());
            q.setHint("org.hibernate.cacheable", Boolean.TRUE);
            obterItens = q.getResultList();
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método obterItensBaseFP", re);
        }
        return obterItens;
    }

    public List<EventoFP> obterEventosItensBaseFP(String codigo) {
        List<EventoFP> eventos = Lists.newArrayList();
        for (ItemBaseFP itemBaseFP : obterItensBaseFP(codigo)) {
            eventos.add(itemBaseFP.getEventoFP());
        }
        return eventos;
    }


    public FiltroBaseFP getFiltroBasePorCodigo(String codigo) {
        String hql = " select base.filtroBaseFP from BaseFP base "
            + " where lower(base.codigo) = :codigo ";
        try {
            Query q = em.createQuery(hql.toString());
            q.setParameter("codigo", codigo.toLowerCase().trim());
            if (!q.getResultList().isEmpty()) {
                return (FiltroBaseFP) q.getResultList().get(0);
            }
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método getFiltroBasePorCodigo", re);
        }
        return null;
    }


    @DescricaoMetodo("obterTipoPrevidenciaFP(ep, mes, ano)")
    public String obterTipoPrevidenciaFP(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo) {
        if (ep instanceof Aposentadoria) {
            return getTipoPrevidenciaAposentadoria(ep);
        }
        return getTipoPrevidenciaContratoFP(ep);
    }

    private String getTipoPrevidenciaContratoFP(EntidadePagavelRH ep) {
        String hql = " select tipo.codigo from PrevidenciaVinculoFP previdencia  "
            + " inner join previdencia.tipoPrevidenciaFP tipo "
            + " inner join previdencia.contratoFP contrato  "
            + " where contrato.id = :parametro "
            + " and :dataVigencia >= previdencia.inicioVigencia and "
            + " :dataVigencia <= coalesce(previdencia.finalVigencia,:dataVigencia) ";
        String obterTipoPrevidencia = "";
        try {
            Query q = em.createQuery(hql);
            q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
            q.setParameter("parametro", ep.getIdCalculo());
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return buscarUltimoTipoPrevidencia(ep);
            }
            Long resultado = (Long) resultList.get(0);
            obterTipoPrevidencia = resultado + "";

        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método obterTipoPrevidenciaFP", re);
        }
        return obterTipoPrevidencia;
    }

    private String buscarUltimoTipoPrevidencia(EntidadePagavelRH ep) {
        String hql = " select tipo.codigo from PrevidenciaVinculoFP previdencia  "
            + " inner join previdencia.tipoPrevidenciaFP tipo "
            + " inner join previdencia.contratoFP contrato  "
            + " where contrato.id = :parametro "
            + " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between previdencia.inicioVigencia AND coalesce(previdencia.finalVigencia,to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy')) "
            + " order by previdencia.inicioVigencia desc";
        String obterTipoPrevidencia = "";
        try {
            Query q = em.createQuery(hql);
            q.setParameter("parametro", ep.getIdCalculo());
            q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return null;
            }
            Long resultado = (Long) resultList.get(0);
            obterTipoPrevidencia = resultado + "";

        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método obterTipoPrevidenciaFP", re);
        }
        return obterTipoPrevidencia;
    }

    private String getTipoPrevidenciaAposentadoria(EntidadePagavelRH ep) {
        String hql = " select tipo.codigo from PrevidenciaVinculoFP previdencia  "
            + " inner join previdencia.tipoPrevidenciaFP tipo "
            + " inner join previdencia.contratoFP contrato  "
            + " where contrato.id = :parametro "
            + "  order by previdencia.inicioVigencia desc ";
        String obterTipoPrevidencia = "";
        try {
            Query q = em.createQuery(hql);
            q.setParameter("parametro", ep.getIdCalculo());
            q.setMaxResults(1);
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return null;
            }
            Long resultado = (Long) resultList.get(0);
            obterTipoPrevidencia = resultado + "";

        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método obterTipoPrevidenciaFP", re);
        }
        return obterTipoPrevidencia;
    }

    /**
     * Metodo para retornar ReferenciaFPFuncao(entidade auxiliar)
     *
     * @param codigo
     * @param valor  if (referencia for do tipo Faixa retorna valor e percentual)
     *               if (referencia for do tipo Valor ou Percentual retornar somente valor)
     * @return obterValorRefencia;
     */
    @DescricaoMetodo("obterReferenciaFaixaFP(String[codigo],Double[valor], mes, ano)")
    public ReferenciaFPFuncao obterReferenciaFaixaFP(EntidadePagavelRH ep, String codigo, Double valor, Integer mesDeCalculo, Integer anoDeCalculo) {
        ReferenciaFPFuncao obterValorReferencia = new ReferenciaFPFuncao();
        obterValorReferencia.setPercentual(0.0);
        obterValorReferencia.setValor(0.0);
        try {
            Query query = em.createQuery(" select item from ItemFaixaReferenciaFP item  "
                + " inner join item.faixaReferenciaFP faixa "
                + " where faixa.referenciaFP.codigo = :codigo "
                + " and faixa.referenciaFP.tipoReferenciaFP = :tipoReferenciaFP and "
                + " :dataVigencia >= faixa.inicioVigencia and "
                + " :dataVigencia <= coalesce(faixa.finalVigencia,:dataVigencia)"
                + " order by item.referenciaAte asc ");

            query.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
            query.setParameter("codigo", codigo);
            query.setParameter("tipoReferenciaFP", TipoReferenciaFP.FAIXA);

            if (valor.isInfinite() || valor.isNaN()) {
                valor = 0.0;
            }

            for (ItemFaixaReferenciaFP item : (List<ItemFaixaReferenciaFP>) query.getResultList()) {
                obterValorReferencia.setValor(item.getValor().doubleValue());
                obterValorReferencia.setTipoReferenciaFP(item.getFaixaReferenciaFP().getReferenciaFP().getTipoReferenciaFP());
                obterValorReferencia.setPercentual(item.getPercentual() == null ? 0.0 : item.getPercentual().doubleValue());
                obterValorReferencia.setReferenciaAte(item.getReferenciaAte() == null ? BigDecimal.ZERO : item.getReferenciaAte());

                if (item.getReferenciaAte().compareTo(new BigDecimal(valor)) >= 0) {
                    break;
                }
            }

            if (obterValorReferencia.getReferenciaAte() != null && obterValorReferencia.getReferenciaAte().compareTo(new BigDecimal(valor)) < 0) {
                obterValorReferencia.setPercentual(0.0);
                obterValorReferencia.setValor(0.0);
            }

            return obterValorReferencia;
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método obterReferenciaFaixaFP", re);
        }
        //return obterValorReferencia;
    }

    @DescricaoMetodo("obterReferenciaValorFP(String[codigo], mes, ano)")
    public ReferenciaFPFuncao obterReferenciaValorFP(EntidadePagavelRH ep, String codigo, Integer mesDeCalculo, Integer anoDeCalculo) {
        ReferenciaFPFuncao obterValorReferencia = referenciaFPFacade.obterReferenciaValorFP(ep, codigo, localDateToDate(getDataReferencia(ep)));
        return obterValorReferencia;
    }

    @DescricaoMetodo("contaLancamento(ep, codigoEvento, mes, ano)")
    public int contaLancamento(EntidadePagavelRH ep, String codigo, Integer mes, Integer ano) {
        //logger.debug("Iniciando a contagem de lançamentos.");
        Query q = em.createQuery("select count(*) from LancamentoFP lancamento "
            + "where lancamento.eventoFP.codigo = :codigoEvento "
            + "and lancamento.vinculoFP.id = :vinculo "
            //                + " and ((extract(month from :dataParam)) >= (extract(month from lancamento.mesAnoInicial))"
            //                + " and  (extract(year from :dataParam)) >= (extract(year from (lancamento.mesAnoInicial))))"
            //                + " and ((extract(month from :dataParam)) <= (extract(month from lancamento.mesAnoFinal)) "
            //                + " and (extract(year from :dataParam)) <=  (extract(year from (coalesce(lancamento.mesAnoFinal, :dataParam)))))");
            + " and :dataParam >= lancamento.mesAnoInicial "
            + " and :dataParam <= coalesce(lancamento.mesAnoFinal,:dataParam) ");
        Integer contaLancamentos = 0;
        Date dataCalculo = Util.criaDataPrimeiroDiaDoMesFP(mes, ano);
        ////System.out.println("MES: " + mes);
        ////System.out.println("ANO: " + ano);
        //logger.debug("Data de c\u00e1lculo: {0}", Util.criaDataPrimeiroDiaDoMesFP(mes, ano));
        //logger.debug("Ep: {0}", ep);
        //logger.debug("Evento: {0}", codigo);

        try {
            q.setParameter("vinculo", ep.getIdCalculo());
            q.setParameter("codigoEvento", codigo);
//            q.setParameter("dataAtual", dataHoje);
            q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(mes, ano));
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0;
            }
            contaLancamentos = ((Long) resultList.get(0)).intValue();
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método contaLancamento", re);
        }
        ////System.out.println("Lancamentos: " + contaLancamentos);
        return contaLancamentos;
    }

    @DescricaoMetodo("recuperaQuantificacaoLancamentoTipoReferencia(ep, codigoEvento, mes, ano)")
    public Double recuperaQuantificacaoLancamentoTipoReferencia(EntidadePagavelRH ep, String codigo, Integer mesDeCalculo, Integer anoDeCalculo) {

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);

        Query q = em.createQuery("select lancamento.quantificacao from LancamentoFP lancamento "
            + "where lancamento.eventoFP.codigo = :evento "
            + " and lancamento.vinculoFP.id = :vinculo "
            + " and lancamento.tipoLancamentoFP <> :tipoLancamentoFP "
            //                + " and :dataAtual between lancamento.mesAnoInicial and coalesce(lancamento.mesAnoFinal, :dataParam)");
            //                + " and :dataAtual between lancamento.mesAnoInicial and coalesce(lancamento.mesAnoFinal, :dataParam)");
            //                + " and ((extract(month from :dataParam)) >= (extract(month from lancamento.mesAnoInicial)) and  (extract(year from :dataParam)) >= (extract(year from (lancamento.mesAnoInicial))))"
            //                + " and ((extract(month from :dataParam)) <= (extract(month from lancamento.mesAnoFinal)) and (extract(year from :dataParam)) <=  (extract(year from (coalesce(lancamento.mesAnoFinal, :dataParam)))))");
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') order by lancamento.dataCadastro desc ");
        Double quantificacao = 0.0;

        try {
            VinculoFP vinculo = (VinculoFP) ep;
            q.setParameter("vinculo", vinculo.getId());
            q.setParameter("evento", codigo);
            q.setParameter("tipoLancamentoFP", TipoLancamentoFP.VALOR); // tudo que não for valor é considerado referencia, por enquanto
            q.setParameter("dataParam", localDateToDate(getDataReferencia(ep)));
            q.setMaxResults(1);
            List resultList = q.getResultList();
//            q.setParameter("dataAtual", calendar.getTime());
            if (resultList.isEmpty()) {
                quantificacao = 0.0;
            } else {
                quantificacao = new BigDecimal(resultList.get(0) + "").doubleValue();
            }
        } catch (RuntimeException re) {
            re.printStackTrace();
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método recuperaQuantificacaoLancamentoTipoReferencia", re);
        }
        return quantificacao;

    }

    @DescricaoMetodo("recuperaQuantificacaoLancamentoTipoReferenciaExcetoLancamentosCalculados(ep, codigoEvento, lancamentosJaCalculados)")
    public Double recuperaQuantificacaoLancamentoTipoReferenciaExcetoLancamentosCalculados(EntidadePagavelRH ep, String codigo, List<LancamentoFP> lancamentosJaCalculados) {

        String hql = "select lancamento.quantificacao from LancamentoFP lancamento "
            + "where lancamento.eventoFP.codigo = :evento "
            + " and lancamento.vinculoFP.id = :vinculo "
            + " and lancamento.tipoLancamentoFP <> :tipoLancamentoFP "
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') ";
        if (lancamentosJaCalculados != null && !lancamentosJaCalculados.isEmpty()) {
            hql += " and lancamento not in (:lancamentos)";
        }
        hql += "   order by lancamento.dataCadastro ";
        Query q = em.createQuery(hql);
        Double quantificacao = 0.0;

        try {
            VinculoFP vinculo = (VinculoFP) ep;
            q.setParameter("vinculo", vinculo.getId());
            q.setParameter("evento", codigo);
            q.setParameter("tipoLancamentoFP", TipoLancamentoFP.VALOR); // tudo que não for valor é considerado referencia, por enquanto
            q.setParameter("dataParam", localDateToDate(getDataReferencia(ep)));
            if (lancamentosJaCalculados != null && !lancamentosJaCalculados.isEmpty()) {
                q.setParameter("lancamentos", lancamentosJaCalculados);
            }
            q.setMaxResults(1);
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                quantificacao = 0.0;
            } else {
                quantificacao = new BigDecimal(resultList.get(0) + "").doubleValue();
            }
        } catch (RuntimeException re) {
            re.printStackTrace();
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método recuperaQuantificacaoLancamentoTipoReferenciaExcetoLancamentosCalculados", re);
        }
        return quantificacao;

    }

    @DescricaoMetodo("truncarResultado(base, percentual)")
    public BigDecimal truncarResultado(BigDecimal base, BigDecimal percentual) {
        BigDecimal retorno = (percentual.divide(new BigDecimal(100))).multiply(base);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return new BigDecimal(decimalFormat.format(retorno).replace(",", "."));
    }

    @DescricaoMetodo("recuperaQuantificacaoLancamentoTipoValor(ep, codigoEvento, mes, ano)")
    public Double recuperaQuantificacaoLancamentoTipoValor(EntidadePagavelRH ep, String codigo, Integer mesDeCalculo, Integer anoDeCalculo) {
        Query q = em.createQuery("select lancamento.quantificacao from LancamentoFP lancamento where "
            + " lancamento.eventoFP.codigo = :evento and lancamento.vinculoFP.id = :vinculo "
            + " and lancamento.tipoLancamentoFP = :tipoLancamentoFP "
            //                + " and :dataAtual between lancamento.mesAnoInicial and coalesce(lancamento.mesAnoFinal, :dataParam)");
            //                + " and ((extract(month from :dataParam)) >= (extract(month from lancamento.mesAnoInicial)) and  (extract(year from :dataParam)) >= (extract(year from (lancamento.mesAnoInicial))))"
            //                + " and ((extract(month from :dataParam)) <= (extract(month from lancamento.mesAnoFinal)) and (extract(year from :dataParam)) <=  (extract(year from (coalesce(lancamento.mesAnoFinal, :dataParam)))))");
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') order by lancamento.dataCadastro desc ");
        Double quantificacao = 0.0;
        try {
            VinculoFP vinculo = (VinculoFP) ep;
            q.setParameter("vinculo", vinculo.getId());
            q.setParameter("evento", codigo);
            q.setParameter("tipoLancamentoFP", TipoLancamentoFP.VALOR);
            q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(mesDeCalculo, anoDeCalculo));
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                quantificacao = 0.0;
            } else {
                quantificacao = new BigDecimal(resultList.get(0) + "").doubleValue();
            }
        } catch (RuntimeException ex) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método recuperaQuantificacaoLancamentoTipoValor", ex);
        }
        return quantificacao;
    }

    @DescricaoMetodo("obterHoraMensal(ep)")
    public Double obterHoraMensal(EntidadePagavelRH ep) {
        Query q = em.createQuery("select sum(coalesce(jornada.horasMensal,0)) from VinculoFP vinculo inner join vinculo.jornadaDeTrabalho jornada where vinculo.id = :vinculo");
        Double obterHora = 0.0;
        try {
            q.setParameter("vinculo", ep.getIdCalculo());
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0.0;
            }
            Long valor = (Long) resultList.get(0);
            if (valor == null) {
                return 0.0;
            }
            if (valor == 0) {
                return 0.0;
            }
            obterHora = valor.doubleValue();
        } catch (RuntimeException ee) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método obterHoraMensal", ee);
        }
        return obterHora;
    }

    @DescricaoMetodo("temSindicato(Long[codigo], ep)")
    public boolean temSindicato(EntidadePagavelRH ep, Long... codigo) {
        boolean temSindicatoCargo = temSindicatoCargo(ep, codigo);
        if (!temSindicatoCargo) {
            Query q = em.createQuery(" select sindicato from SindicatoVinculoFP sindicatoVinculoFP "
                + " inner join sindicatoVinculoFP.sindicato sindicato "
                + " where sindicatoVinculoFP.vinculoFP.id = :vinculo and sindicato.mesDataBase = :mes" +
                " and sindicato.codigo in :codigos and :dataReferencia between  sindicatoVinculoFP.inicioVigencia and coalesce(sindicatoVinculoFP.finalVigencia,:dataReferencia) ");
            q.setParameter("codigos", Arrays.asList(codigo));
            q.setParameter("dataReferencia", localDateToDate(getDataReferencia(ep)));
            q.setParameter("mes", getDataReferencia(ep).getMonthValue());
            q.setParameter("vinculo", ep.getIdCalculo());
            return !q.getResultList().isEmpty();
        }
        return temSindicatoCargo;
    }

    public boolean temSindicatoCargo(EntidadePagavelRH ep, Long... codigo) {
        if (ep.getCargo() == null) {
            return false;
        }
        String sql = "SELECT sind.* " +
            "FROM ItemCargoSindicato item " +
            "INNER JOIN CARGO ON cargo.ID = item.CARGO_ID " +
            "INNER JOIN SINDICATO sind ON sind.id = item.SINDICATO_ID " +
            "WHERE sind.CODIGO in :codigos " +
            "AND :dataReferencia BETWEEN item.DATAINICIO AND coalesce(item.DATAFIM,:dataReferencia) " +
            "AND sind.MESDATABASE = :mes " +
            "AND CARGO.ID = :cargo";
        Query q = em.createNativeQuery(sql, Sindicato.class);
        q.setParameter("codigos", Arrays.asList(codigo));
        q.setParameter("dataReferencia", localDateToDate(getDataReferencia(ep)));
        q.setParameter("mes", getDataReferencia(ep).getMonthValue());
        q.setParameter("cargo", ep.getCargo().getId());
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("obterNumeroDependentes(String [codigo], ep)")
    public int obterNumeroDependentes(String codigo, EntidadePagavelRH ep) {
        String sql = " SELECT COUNT(DEPENDENTESVINCULOFP.ID) FROM DEPENDENTE DEPENDENTE  "
            + "INNER JOIN PESSOAFISICA DEPENDENTEFISICA ON DEPENDENTE.DEPENDENTE_ID = DEPENDENTEFISICA.ID  "
            + "INNER JOIN DEPENDENTEVINCULOFP DEPENDENTESVINCULOFP ON DEPENDENTE.ID = DEPENDENTESVINCULOFP.DEPENDENTE_ID "
            + "INNER JOIN TIPODEPENDENTE TIPODEPENDENTE ON DEPENDENTESVINCULOFP.TIPODEPENDENTE_ID = TIPODEPENDENTE.ID "
            + "WHERE dependente.responsavel_id = :responsavel  "
            + "AND TIPODEPENDENTE.CODIGO = :codigo "
//              + "AND :data >= DEPENDENTESVINCULOFP.INICIOVIGENCIA AND :data <= coalesce(DEPENDENTESVINCULOFP.FINALVIGENCIA,:data) " +
            + "  and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between DEPENDENTESVINCULOFP.INICIOVIGENCIA AND coalesce(DEPENDENTESVINCULOFP.FINALVIGENCIA,to_date(to_char(:data,'mm/yyyy'),'mm/yyyy'))"
            + "AND  FLOOR (FLOOR (MONTHS_BETWEEN (:data, DEPENDENTEFISICA.DATANASCIMENTO)) / 12) <= TIPODEPENDENTE.IDADEMAXIMA ";


        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        q.setParameter("responsavel", ep.getMatriculaFP().getPessoa().getId());
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return 0;
        }
        return ((BigDecimal) resultList.get(0)).intValue();

    }

    public int obterIdadeServidor(EntidadePagavelRH ep) {
        Query q = em.createNativeQuery("select coalesce(FLOOR (FLOOR (MONTHS_BETWEEN (:dataReferencia, p.dataNascimento)) / 12),0) from VinculoFP v inner join matriculafp mat on mat.id = v.matriculafp_id\n" +
            "inner join pessoafisica p on p.id = mat.pessoa_id where v.id = :id");
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("dataReferencia", localDateToDate(getDataReferencia(ep)));
        q.setMaxResults(1);
        BigDecimal idade = (BigDecimal) q.getResultList().get(0);
        return idade.intValue();
    }

    @DescricaoMetodo("temFeriasConcedidas(ep)")
    public boolean temFeriasConcedidas(EntidadePagavelRH ep) {
        return quantidadeDiasFeriasConcedidas(ep) > 0;
    }

    @DescricaoMetodo("estaEmFeriasEsteMes(ep)")
    public boolean estaEmFeriasEsteMes(EntidadePagavelRH ep, Boolean verificarNoMesAnterior) {
        Query q = em.createQuery(" select concessao from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contratofp "
            + " where contratofp.id = :parametro and "
            + " concessao.mes = :mes and concessao.ano = :ano " +
            "");
        LocalDate data = getDataReferenciaDateTime(ep);
        if (verificarNoMesAnterior) {
            data = data.minusMonths(1);
        }
        q.setParameter("parametro", ep.getIdCalculo());
        q.setParameter("mes", data.getMonthValue());
        q.setParameter("ano", data.getYear());
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("estaEmFeriasOuLicencaEsteMes(ep)")
    public boolean estaEmFeriasOuLicencaEsteMes(EntidadePagavelRH ep, TipoPeriodoAquisitivo tipoPa) {
        Query q = em.createQuery(" select concessao from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contratofp "
            + " inner join periodo.baseCargo basec "
            + " inner join basec.basePeriodoAquisitivo basePA "
            + " where contratofp.id = :parametro "
            + " and to_date(to_char(to_date(:data, 'dd/MM/yyyy'), 'mm/yyyy'),'mm/yyyy') between "
            + "         to_date(to_char(concessao.dataInicial, 'mm/yyyy'), 'mm/yyyy') and "
            + "         to_date(to_char(coalesce(concessao.dataFinal, to_date(:data, 'dd/MM/yyyy')), 'mm/yyyy'), 'mm/yyyy')"
            + " and basePA.tipoPeriodoAquisitivo = :tipoPA ");
        LocalDate data = getDataReferenciaDateTime(ep);
        q.setParameter("parametro", ep.getIdCalculo());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoPA", tipoPa);
        return !q.getResultList().isEmpty();
    }


    public void temProgramacaoFeriasAprovada(EntidadePagavelRH ep) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        Query q = em.createQuery(" select distinct sugestao from SugestaoFerias sugestao "
            + " inner join sugestao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contratofp "
            + " inner join sugestao.listAprovacaoFerias aprovacao "
            + " where contratofp.id = :parametro and "
            + " sugestao.dataInicio >= :primeiroDiaConcessao and sugestao.dataInicio <= :primeiroDiaProxMesConcessao "
            + " and aprovacao.aprovado = 1 ");
    }

    @SuppressWarnings("JpaQueryApiInspection")
    @DescricaoMetodo("quantidadeDiasFeriasConcedidas(ep)")
    public Double quantidadeDiasFeriasConcedidas(EntidadePagavelRH ep) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        Query q = em.createQuery(" select concessao from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contratofp "
            + " inner join periodo.baseCargo.basePeriodoAquisitivo base "
            + " where concessao.mes is not null and concessao.ano is not null "
            + " and contratofp.id = :parametro and "
            + " base.tipoPeriodoAquisitivo = :tipo and "
            //+ " concessao.dataInicial >= :primeiroDiaConcessao and concessao.dataInicial <= :primeiroDiaProxMesConcessao");
            + "  concessao.mes = :mes and concessao.ano = :ano ");
        Date primeiroDiaConcessao = null;
        Date primeiroDiaProxMesConcessao = null;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, ep.getMes());
        c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
        primeiroDiaConcessao = Util.getDataHoraMinutoSegundoZerado(Util.criaDataPrimeiroDiaDoMesFP(ep.getMes(), ep.getAno()));

        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        primeiroDiaProxMesConcessao = c.getTime();

        q.setParameter("parametro", vinculoFP.getIdCalculo());
        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS);
        if (vinculoFP.isHouveEstornoFerias()) {
            //q.setParameter("primeiroDiaConcessao", getPrimeiroDiaDoMesCalculo(ep).toDate());
            //q.setParameter("primeiroDiaProxMesConcessao", getUltimoDiaDoMesCalculo(ep).toDate());
            q.setParameter("mes", getPrimeiroDiaDoMesCalculo(ep).getMonthValue());
            q.setParameter("ano", getPrimeiroDiaDoMesCalculo(ep).getYear());
        } else {
            //q.setParameter("primeiroDiaConcessao", primeiroDiaConcessao);
            //q.setParameter("primeiroDiaProxMesConcessao", primeiroDiaProxMesConcessao);
            q.setParameter("mes", ep.getMes());
            q.setParameter("ano", ep.getAno());
        }
        Integer qtdDias = 0;

        for (ConcessaoFeriasLicenca concessao : (List<ConcessaoFeriasLicenca>) q.getResultList()) {
            qtdDias = qtdDias + DataUtil.diferencaDiasInteira(concessao.getDataInicial(), concessao.getDataFinal()) + 1;
        }
        return qtdDias.doubleValue();
    }

    @DescricaoMetodo("quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(ep)")
    public Double quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(EntidadePagavelRH ep, TipoPeriodoAquisitivo tipoPa, LocalDate dataCorteInicial, LocalDate dataCorteFinal) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        Query q = em.createQuery(" select concessao from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contratofp "
            + " inner join periodo.baseCargo.basePeriodoAquisitivo base "
            + " where contratofp.id = :parametro and "
            + " base.tipoPeriodoAquisitivo = :tipo and "
            + " to_date(to_char(to_date(:data, 'dd/MM/yyyy'), 'mm/yyyy'),'mm/yyyy') between "
            + "         to_date(to_char(concessao.dataInicial, 'mm/yyyy'), 'mm/yyyy') and "
            + "         to_date(to_char(coalesce(concessao.dataFinal, to_date(:data, 'dd/MM/yyyy')), 'mm/yyyy'), 'mm/yyyy')");

        q.setParameter("parametro", vinculoFP.getIdCalculo());
        q.setParameter("tipo", tipoPa);
        LocalDate dataReferencia = getDataReferenciaDateTime(ep);
        q.setParameter("data", DataUtil.getDataFormatada(dataReferencia));
        Integer qtdDias = 0;
        LocalDate dataPrimeiroDia;
        LocalDate dataUltimoDia;
        if (dataCorteInicial != null) {
            dataPrimeiroDia = dataCorteInicial;
        } else {
            dataPrimeiroDia = dataReferencia.withDayOfMonth(1);
        }
        if (dataCorteFinal != null) {
            dataUltimoDia = dataCorteFinal;
        } else {
            dataUltimoDia = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());
        }

        for (ConcessaoFeriasLicenca concessao : (List<ConcessaoFeriasLicenca>) q.getResultList()) {
            LocalDate inicioConcessao = DataUtil.dateToLocalDate(concessao.getDataInicial());
            LocalDate finalConcessao = DataUtil.dateToLocalDate(concessao.getDataFinal());

            if (inicioConcessao.isBefore(dataPrimeiroDia)) {
                inicioConcessao = dataPrimeiroDia;
            }
            if (finalConcessao == null || finalConcessao.isAfter(dataUltimoDia)) {
                finalConcessao = dataUltimoDia;
            }
            qtdDias += DataUtil.diasEntre(inicioConcessao, finalConcessao);
        }
        return qtdDias.doubleValue();
    }

    @DescricaoMetodo("quantidadeDiasFeriasConcedidasNaoLancadasAutomaticamente(ep)")
    public Double quantidadeDiasFeriasConcedidasNaoLancadasAutomaticamente(EntidadePagavelRH ep) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        Query q = em.createQuery(" select concessao from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contratofp "
            + " inner join periodo.baseCargo.basePeriodoAquisitivo base "
            + " where contratofp.id = :parametro and "
            + " base.tipoPeriodoAquisitivo = :tipo and "
            + "  concessao.mes = :mes and concessao.ano = :ano "
            + " and periodo not in (select lanc.periodoAquisitivoFL from LancamentoTercoFeriasAut lanc where lanc.periodoAquisitivoFL = periodo) ");
        q.setParameter("parametro", vinculoFP.getIdCalculo());
        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS);
        if (vinculoFP.isHouveEstornoFerias()) {
            q.setParameter("mes", getPrimeiroDiaDoMesCalculo(ep).getMonthValue());
            q.setParameter("ano", getPrimeiroDiaDoMesCalculo(ep).getYear());
        } else {
            q.setParameter("mes", ep.getMes());
            q.setParameter("ano", ep.getAno());
        }
        Integer qtdDias = 0;

        for (ConcessaoFeriasLicenca concessao : (List<ConcessaoFeriasLicenca>) q.getResultList()) {
            qtdDias = qtdDias + DataUtil.diferencaDiasInteira(concessao.getDataInicial(), concessao.getDataFinal()) + 1;
        }
        return qtdDias.doubleValue();
    }

    @DescricaoMetodo("quantidadeDiasFeriasDobradas(ep)")
    public Double quantidadeDiasFeriasDobradas(EntidadePagavelRH ep) {
        LocalDate dataReferenciaEp = getDataReferencia(ep);
        String sql = " select sum(pa.saldo) " +
            " from periodoaquisitivofl pa " +
            "         inner join baseCargo bc on pa.basecargo_id = bc.id " +
            "         inner join basePeriodoAquisitivo bp on bc.baseperiodoaquisitivo_id = bp.id " +
            " where pa.contratofp_id = :contrato " +
            "  and bp.tipoperiodoaquisitivo = :tipoPA " +
            "  and pa.status in (:statusNaoConcedido, :statusParcial) " +
            "  and add_months(pa.finalvigencia, bp.duracao) < :dataCompetencia ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("tipoPA", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("statusNaoConcedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
        q.setParameter("statusParcial", StatusPeriodoAquisitivo.PARCIAL.name());
        q.setParameter("dataCompetencia", ep.getFinalVigencia() != null && dateToLocalDate(ep.getFinalVigencia()).isBefore(dataReferenciaEp) ? ep.getFinalVigencia() : dataReferenciaEp);
        Object result = q.getSingleResult();
        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }

    @DescricaoMetodo("quantidadeDiasFeriasVencidas(ep)")
    public Double quantidadeDiasFeriasVencidas(EntidadePagavelRH ep) {
        LocalDate dataReferenciaEp = getDataReferencia(ep);
        String sql = " select sum(pa.saldo) " +
            " from periodoaquisitivofl pa " +
            "         inner join baseCargo bc on pa.basecargo_id = bc.id " +
            "         inner join basePeriodoAquisitivo bp on bc.baseperiodoaquisitivo_id = bp.id " +
            " where pa.contratofp_id = :contrato " +
            "  and bp.tipoperiodoaquisitivo = :tipoPA " +
            "  and pa.status in (:statusNaoConcedido, :statusParcial) " +
            "  and trunc(pa.finalvigencia) < :dataCompetencia ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("tipoPA", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("statusNaoConcedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
        q.setParameter("statusParcial", StatusPeriodoAquisitivo.PARCIAL.name());
        q.setParameter("dataCompetencia", ep.getFinalVigencia() != null && dateToLocalDate(ep.getFinalVigencia()).isBefore(dataReferenciaEp) ? ep.getFinalVigencia() : dataReferenciaEp);
        Object result = q.getSingleResult();
        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }

    @DescricaoMetodo("quantidadeDiasFeriasProporcional(ep)")
    public Double quantidadeDiasFeriasProporcional(EntidadePagavelRH ep) {
        LocalDate dataReferenciaEp = getDataReferencia(ep);
        String sql = " select pa.* " +
            "from periodoaquisitivofl pa " +
            "         inner join baseCargo bc on pa.basecargo_id = bc.id " +
            "         inner join basePeriodoAquisitivo bp on bc.baseperiodoaquisitivo_id = bp.id " +
            " where pa.contratofp_id = :contrato " +
            "  and bp.tipoperiodoaquisitivo = :tipoPA " +
            "  and pa.status in (:statusNaoConcedido, :statusParcial) " +
            "  and :dataCompetencia between pa.iniciovigencia and coalesce(pa.finalvigencia, :dataCompetencia) " +
            " order by pa.iniciovigencia desc";
        Query q = em.createNativeQuery(sql, PeriodoAquisitivoFL.class);
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("tipoPA", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("statusNaoConcedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
        q.setParameter("statusParcial", StatusPeriodoAquisitivo.PARCIAL.name());
        q.setParameter("dataCompetencia", ep.getFinalVigencia() != null && dateToLocalDate(ep.getFinalVigencia()).isBefore(dataReferenciaEp) ? ep.getFinalVigencia() : dataReferenciaEp);
        List result = q.getResultList();
        if (result == null || result.isEmpty()) {
            return 0.0;
        }

        PeriodoAquisitivoFL periodo = (PeriodoAquisitivoFL) result.get(0);
        LocalDate dataReferencia = ep.getFinalVigencia() != null && dateToLocalDate(ep.getFinalVigencia()).isBefore(dataReferenciaEp) ? dateToLocalDate(ep.getFinalVigencia()) : dataReferenciaEp;

        LocalDate inicioPeriodo = dateToLocalDate(periodo.getInicioVigencia());
        LocalDate fim = dataReferencia.isBefore(dateToLocalDate(periodo.getFinalVigencia())) ? dataReferencia : dateToLocalDate(periodo.getFinalVigencia());
        List<LocalDate> datas = getDatasEmLista(inicioPeriodo, fim);
        int acumulado = 0;
        int saldo = 0;
        for (LocalDate refe : datas) {
            int dias = diasEntreDatas(refe, inicioPeriodo, fim);

            if (dias >= 15) {
                ++saldo;
            } else {
                acumulado += dias;
            }
            if (acumulado >= 15) {
                saldo++;
            }
        }
        BigDecimal duracaoPA = BigDecimal.valueOf(periodo.getBaseCargo().getBasePeriodoAquisitivo().getDuracao());
        BigDecimal diasDeSaldo = BigDecimal.valueOf(periodo.getSaldo());
        BigDecimal resultado = (diasDeSaldo.divide(duracaoPA, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(saldo));
        return resultado.doubleValue();
    }

    @DescricaoMetodo("quantidadePeriodoAquisitivoFeriasDobradas(ep)")
    public Integer quantidadePeriodoAquisitivoFeriasDobradas(EntidadePagavelRH ep) {
        LocalDate dataReferenciaEp = getDataReferencia(ep);
        String sql = " select count(pa.id) " +
            " from periodoaquisitivofl pa " +
            "         inner join baseCargo bc on pa.basecargo_id = bc.id " +
            "         inner join basePeriodoAquisitivo bp on bc.baseperiodoaquisitivo_id = bp.id " +
            " where pa.contratofp_id = :contrato " +
            "  and bp.tipoperiodoaquisitivo = :tipoPA " +
            "  and pa.status in (:statusNaoConcedido, :statusParcial) " +
            "  and add_months(pa.finalvigencia, bp.duracao) < :dataCompetencia ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("tipoPA", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("statusNaoConcedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
        q.setParameter("statusParcial", StatusPeriodoAquisitivo.PARCIAL.name());
        q.setParameter("dataCompetencia", ep.getFinalVigencia() != null && dateToLocalDate(ep.getFinalVigencia()).isBefore(dataReferenciaEp) ? ep.getFinalVigencia() : dataReferenciaEp);
        Object result = q.getSingleResult();
        if (result == null) {
            return 0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.intValue();
    }

    @DescricaoMetodo("quantidadePeriodoAquisitivoFeriasVencidas(ep)")
    public Integer quantidadePeriodoAquisitivoFeriasVencidas(EntidadePagavelRH ep) {
        LocalDate dataReferenciaEp = getDataReferencia(ep);
        String sql = " select count(pa.id) " +
            " from periodoaquisitivofl pa " +
            "         inner join baseCargo bc on pa.basecargo_id = bc.id " +
            "         inner join basePeriodoAquisitivo bp on bc.baseperiodoaquisitivo_id = bp.id " +
            " where pa.contratofp_id = :contrato " +
            "  and bp.tipoperiodoaquisitivo = :tipoPA " +
            "  and pa.status in (:statusNaoConcedido, :statusParcial) " +
            "  and trunc(pa.finalvigencia) < :dataCompetencia ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("tipoPA", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("statusNaoConcedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
        q.setParameter("statusParcial", StatusPeriodoAquisitivo.PARCIAL.name());
        q.setParameter("dataCompetencia", ep.getFinalVigencia() != null && dateToLocalDate(ep.getFinalVigencia()).isBefore(dataReferenciaEp) ? ep.getFinalVigencia() : dataReferenciaEp);
        Object result = q.getSingleResult();
        if (result == null) {
            return 0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.intValue();
    }

    /**
     * Método utilizado para pegar a quantidade de dias de férias que o serivdor tem direito desontando as faltas injustificadas do periodo.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Double totalDiasDireitoFerias(EntidadePagavelRH ep) {
        VinculoFP vinculo = (VinculoFP) ep;
        double totalFaltas = 0;
        double mesesSemConcessao = verificarEPegarMesesEmPeriodoAquisitivoSemConcessao(ep);
        LocalDate inicio = LocalDate.now();
        LocalDate fim = LocalDate.now();
        PeriodoAquisitivoFL p = null;
        if (mesesSemConcessao > 0) {
            p = getUltimoPeriodoAquitivo(ep);
            inicio = dateToLocalDate(p.getInicioVigencia());
            fim = vinculo.getFinalVigencia() != null ? dateToLocalDate(vinculo.getFinalVigencia()) : getDataReferencia(vinculo);
            totalFaltas += faltasFacade.recuperaDiasDeFaltasPorPeriodo(ep.getIdCalculo(), localDateToDate(inicio), localDateToDate(fim));
        } else {
            List<PeriodoAquisitivoFL> periodos = getPeriodosAquisitivosNaoConcedidoEParcial(ep);
            if (!periodos.isEmpty()) {
                Collections.sort(periodos, PeriodoAquisitivoFL.Comparators.INICIOVIGENCIADESC);
                p = periodos.get(0);
            }

            if (p != null) {
                totalFaltas += faltasFacade.recuperaDiasDeFaltasPorPeriodo(ep.getIdCalculo(), p.getInicioVigencia(), p.getFinalVigencia());
            }
        }

        ReferenciaFPFuncao faixa = null;
        if (ep instanceof ContratoFP) {
            ConfiguracaoFaltasInjustificadas config = periodoAquisitivoFLFacade.getConfiguracaoFaltasInjustificadasVigenteAndPeriodoAquisitivo(p, localDateToDate(getDataReferencia(ep)));
            if (config != null) {
                faixa = obterReferenciaFaixaFP(ep, config.getReferenciaFP().getCodigo(), totalFaltas, 0, 0);
            }
        }
        if (faixa != null) {
            logger.debug("total de dias de direito férias: " + faixa.getValor());
            Double dias = proporcionalizarFeriasProporcional(p, faixa, ep);
            return dias;
        }
        return p.getSaldoDeDireito().doubleValue();
    }

    private double proporcionalizarFeriasProporcional(PeriodoAquisitivoFL p, ReferenciaFPFuncao faixa, EntidadePagavelRH ep) {
        Double mesesEntre = totalDiasDireitoProporcional(ep);
        try {
            Double valorDesconto = faixa.getValor() != null ? faixa.getValor() : 0.0;
            return ((p.getSaldoDeDireito() - valorDesconto) / 12) * mesesEntre;
        } catch (Exception e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método proporcionalizarFeriasProporcional, erro: ", e);
        }
    }

    @DescricaoMetodo("quantidadeLicencaPremio(ep)")
    public Double quantidadeLicencaPremioConcedido(EntidadePagavelRH ep) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        Query q = em.createQuery(" select concessao from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contratofp "
            + " where contratofp.id = :parametro and "
            //+ " concessao.dataInicial >= :primeiroDiaConcessao and concessao.dataInicial <= :primeiroDiaProxMesConcessao");
            + "  concessao.mes = :mes and concessao.ano = :ano and periodo.baseCargo.basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipo");
        Date primeiroDiaConcessao = null;
        Date primeiroDiaProxMesConcessao = null;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, ep.getMes());
        c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
        primeiroDiaConcessao = Util.getDataHoraMinutoSegundoZerado(Util.criaDataPrimeiroDiaDoMesFP(ep.getMes(), ep.getAno()));

        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        primeiroDiaProxMesConcessao = c.getTime();

        q.setParameter("parametro", vinculoFP.getIdCalculo());
        q.setParameter("tipo", TipoPeriodoAquisitivo.LICENCA);
        if (vinculoFP.isHouveEstornoFerias()) {
            q.setParameter("mes", getPrimeiroDiaDoMesCalculo(ep).getMonthValue());
            q.setParameter("ano", getPrimeiroDiaDoMesCalculo(ep).getYear());
        } else {

            q.setParameter("mes", ep.getMes());
            q.setParameter("ano", ep.getAno());
        }
        List<ConcessaoFeriasLicenca> lista = new ArrayList<>();
        Integer qtdDias = 0;

        for (ConcessaoFeriasLicenca concessao : (List<ConcessaoFeriasLicenca>) q.getResultList()) {
            qtdDias = qtdDias + DataUtil.diferencaDiasInteira(concessao.getDataInicial(), concessao.getDataFinal()) + 1;
        }
        return qtdDias.doubleValue();
    }

    public boolean houveAlteracaoProgramacaoFerias(EntidadePagavelRH ep, LocalDate dataMaximaDoMesCorrenteCalculo) {
        try {


            VinculoFP vinculoFP = (VinculoFP) ep;
            Query q = em.createQuery(" select sugestao from SugestaoFerias sugestao "
                + " inner join sugestao.periodoAquisitivoFL periodo "
                + " inner join periodo.contratoFP contratofp "
                + " inner join sugestao.listAprovacaoFerias ap "
                + " where contratofp.id = :parametro and "
                + " ap.dataAprovacao between :efetivadaEm and :ultimoDiaMesCalculo and ap.aprovado is true ");
//        Date primeiroDiaConcessao = null;
//        Date primeiroDiaProxMesConcessao = null;

            Date dataUltimaFolhaEfetivada = folhaDePagamentoFacade.recuperaDataUltimaFolhaEfetivadaEm(ep.getFolha().getTipoFolhaDePagamento());


//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.MONTH, getMes());
//        c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
//        primeiroDiaConcessao = Util.getDataHoraMinutoSegundoZerado(Util.criaDataPrimeiroDiaDoMesFP(getMes(), getAno()));
//
//        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
//        primeiroDiaProxMesConcessao = c.getTime();

            q.setParameter("parametro", vinculoFP.getIdCalculo());
            q.setParameter("efetivadaEm", dataUltimaFolhaEfetivada);
            q.setParameter("ultimoDiaMesCalculo", localDateToDate(dataMaximaDoMesCorrenteCalculo));
//        List<ConcessaoFeriasLicenca> lista = new ArrayList<ConcessaoFeriasLicenca>();
            Integer qtdDias = 0;

            if (!q.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception ex) {
            throw new FuncoesFolhaFacadeException("erro ao executar método houveAlteracaoProgramacaoFerias ", ex);
        }
        return false;

    }

    @DescricaoMetodo("temAbonoPecuniarioFerias(ep)")
    public boolean temAbonoPecuniarioFerias(EntidadePagavelRH ep) {
        return quantidadeDiasAbonoPecuniarioFerias(ep) > 0;
    }

    @DescricaoMetodo("quantidadeDiasAbonoPecuniarioFerias(ep)")
    public Double quantidadeDiasAbonoPecuniarioFerias(EntidadePagavelRH ep) {
        Integer qtdDias = 0;
        try {


            VinculoFP vinculoFP = (VinculoFP) ep;
            Query q = em.createQuery(" select concessao from ConcessaoFeriasLicenca concessao "
                + " inner join concessao.periodoAquisitivoFL periodo "
                + " inner join periodo.contratoFP contratofp "
                + " where contratofp.id = :parametro and "
                //+ " concessao.dataInicial >= :primeiroDiaMesConcessao and concessao.dataInicial < :primeiroDiaProxMesConcessao"
                + "  concessao.mes = :mes and concessao.ano = :ano ");
            Date primeiroDiaMesConcessao = null;
            Date primeiroDiaProxMesConcessao = null;

            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, ep.getMes());
            c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
            primeiroDiaMesConcessao = Util.getDataHoraMinutoSegundoZerado(Util.criaDataPrimeiroDiaDoMesFP(ep.getMes(), ep.getAno()));

            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            primeiroDiaProxMesConcessao = c.getTime();

            q.setParameter("parametro", vinculoFP.getIdCalculo());
            if (vinculoFP.isHouveEstornoFerias()) {
                //q.setParameter("primeiroDiaConcessao", getPrimeiroDiaDoMesCalculo(ep).toDate());
                //q.setParameter("primeiroDiaProxMesConcessao", getUltimoDiaDoMesCalculo(ep).toDate());
                q.setParameter("mes", getPrimeiroDiaDoMesCalculo(ep).getMonthValue());
                q.setParameter("ano", getPrimeiroDiaDoMesCalculo(ep).getYear());
            } else {
                //q.setParameter("primeiroDiaConcessao", primeiroDiaConcessao);
                //q.setParameter("primeiroDiaProxMesConcessao", primeiroDiaProxMesConcessao);
                q.setParameter("mes", ep.getMes());
                q.setParameter("ano", ep.getAno());
            }
            List<ConcessaoFeriasLicenca> lista = q.getResultList();
            qtdDias = 0;

            for (ConcessaoFeriasLicenca concessao : lista) {
                qtdDias = qtdDias + (concessao.getDiasAbonoPecuniario() == null ? 0 : concessao.getDiasAbonoPecuniario());
            }
        } catch (Exception e) {
            throw new FuncoesFolhaFacadeException("erro ao executar método quantidadeDiasAbonoPecuniarioFerias ", e);
        }
        return qtdDias.doubleValue();
    }

    @DescricaoMetodo("quantidadeDiasFeriasGozadasNoMes(ep)")
    public Double quantidadeDiasFeriasGozadasNoMes(EntidadePagavelRH ep) {
        List<ConcessaoFeriasLicenca> concessaoFerias = getConcessaoFeriasDias(ep);
        Integer qtdDias = 0;

        for (ConcessaoFeriasLicenca concessao : concessaoFerias) {
            java.time.LocalDate inicioMes = getPrimeiroDiaDoMesCalculo(ep);
            java.time.LocalDate fimMes = getUltimoDiaDoMesCalculo(ep);
            java.time.LocalDate inicio = inicioMes;
            java.time.LocalDate fim = fimMes;

            java.time.LocalDate inicioConcessao = DataUtil.dateToLocalDate(concessao.getDataInicial());
            java.time.LocalDate finalConcessao = concessao.getDataFinal() != null ? DataUtil.dateToLocalDate(concessao.getDataFinal()) : null;

            if (inicioMes.isBefore(inicioConcessao)) {
                inicio = inicioConcessao;
            }
            if (finalConcessao != null && fimMes.isAfter(finalConcessao)) {
                fim = finalConcessao;
            }
            qtdDias += DataUtil.diasEntre(inicio, fim);
        }
        return qtdDias.doubleValue();
    }

    private List<ConcessaoFeriasLicenca> getConcessaoFeriasDias(EntidadePagavelRH ep) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        String sql = "select c.* " +
            " from concessaoferiaslicenca c " +
            "  inner join periodoaquisitivofl p on c.periodoaquisitivofl_id = p.id " +
            "  inner join BASECARGO baseC on p.BASECARGO_ID = baseC.ID " +
            "  inner join BASEPERIODOAQUISITIVO basePa on baseC.BASEPERIODOAQUISITIVO_ID = basePa.ID " +
            "where p.contratofp_id = :idcontrato  " +
            "      and basePa.TIPOPERIODOAQUISITIVO = :tipoPA " +
            "      and (to_date(:iniciomes, 'dd/mm/yyyy') between c.datainicial and c.datafinal or " +
            "       to_date(:finalmes, 'dd/mm/yyyy') between c.datainicial and c.datafinal or " +
            "       c.datainicial between to_date(:iniciomes, 'dd/mm/yyyy') and to_date(:finalmes, 'dd/mm/yyyy') or " +
            "       c.DATAFINAL between to_date(:iniciomes, 'dd/mm/yyyy') and to_date(:finalmes, 'dd/mm/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConcessaoFeriasLicenca.class);
        q.setParameter("idcontrato", vinculoFP.getIdCalculo());
        q.setParameter("iniciomes", DataUtil.getDataFormatada(getPrimeiroDiaDoMesCalculo(ep)));
        q.setParameter("finalmes", DataUtil.getDataFormatada(getUltimoDiaDoMesCalculo(ep)));
        q.setParameter("tipoPA", TipoPeriodoAquisitivo.FERIAS.name());

        return q.getResultList();
    }

    public Date getDataProximoMes(Date valorData) {
        Calendar c = Calendar.getInstance();
        c.setTime(valorData);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH, 1);
        Date d = getDataUltimoFachamento(c);
        if (d != null) {
            return d;
        }
        return new Date();
    }

    public MesCalendarioPagamento getMesCalendario(Calendar calendar) {
        for (MesCalendarioPagamento m : MesCalendarioPagamento.values()) {
            if (m.ordinal() == calendar.get(Calendar.MONTH)) {
                return m;
            }
        }
        return null;
    }

    public Date getDataUltimoFachamento(Calendar c) {
        Query q = em.createQuery("select c.ultimoDiaProcessamento from ItemCalendarioFP c inner join c.calendarioFP calendario"
            + " where calendario.ano = :ano and c.mesCalendarioPagamento = :mes");
        q.setParameter("ano", c.get(Calendar.YEAR));
        ////System.out.println("MES: " + getMesCalendario(c).toString());
        q.setParameter("mes", getMesCalendario(c));
        Integer valor = (Integer) q.getSingleResult();
        if (valor != null) {
            c.set(Calendar.DAY_OF_MONTH, valor);
            return c.getTime();
        }
        return null;
    }

    @DescricaoMetodo("recuperarPercentualPorTipoNaturezaAtividade(ep, mes, ano, codigoNaturezaAtividade)")
    public Double recuperarPercentualPorTipoNaturezaAtividade(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo, Integer codigoNatureza) {
        Double retorno = 0.0;
        StringBuilder builder = new StringBuilder();
        builder.append(" select valorUo.VALOR from CONTRATOFP contrato inner join LOTACAOFUNCIONAL lotacao on contrato.ID = lotacao.VINCULOFP_ID");
        builder.append(" inner join UNIDADEORGANIZACIONAL unidade on unidade.ID = lotacao.UNIDADEORGANIZACIONAL_ID");
        builder.append(" inner join VALORUNIDADEORGANIZACIONAL valorUo on valorUo.UNIDADEORGANIZACIONAL_ID = unidade.ID");
        builder.append(" inner join LAUDO laudo on valorUo.LAUDO_ID = laudo.ID");
        builder.append(" where contrato.ID = :vinculo and valorUo.TIPONATUREZAATIVIDADEFP = :tipoNatureza");
        builder.append(" and :dataVigencia >= laudo.INICIOVIGENCIA and :dataVigencia <= ");
        builder.append(" coalesce(laudo.FIMVIGENCIA,:dataVigencia)");

        Query q = em.createNativeQuery(builder.toString());
        try {
            q.setParameter("tipoNatureza", TipoNaturezaAtividadeFP.find(codigoNatureza).toString());
            q.setParameter("vinculo", ep.getIdCalculo());
            q.setParameter("dataVigencia", Util.criaDataPrimeiroDiaDoMes(mesDeCalculo, anoDeCalculo));
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return retorno;
            }
            return (Double) resultList.get(0);
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método recuperarPercentualPorTipoNaturezaAtividade", re);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @DescricaoMetodo
    public BigDecimal retornaMenorSalario() {
        String hql = "";

        hql = "select min(enquadramento.vencimentoBase)  from EnquadramentoPCS enquadramento"
            + " inner join enquadramento.progressaoPCS progressao"
            + " inner join progressao.planoCargosSalarios pcs"
            + " where pcs.tipoPCS = 'QUADRO_EFETIVO'";

        Query q = em.createQuery(hql);
        return (BigDecimal) q.getSingleResult();
    }

    @DescricaoMetodo("recuperaValorItemSindicato(ep, codigo)")
    public BigDecimal recuperaValorItemSindicato(EntidadePagavelRH ep, Integer codigo) {
        StringBuilder builder = new StringBuilder();
        builder.append(" select coalesce(itemSindicato.valor,0) from ITEMSINDICATO itemSindicato ");
        builder.append(" inner join SINDICATO sindicato on itemSindicato.sindicato_id = sindicato.id ");
        builder.append(" inner join SINDICATOVINCULOFP sindicatoVinculoFP on sindicato.id = sindicatoVinculoFP.id ");
        builder.append(" inner join CONTRATOFP contratoFP on sindicatoVinculoFP.vinculofp_id = contratoFP.id ");
        builder.append(" where contratoFP.id = :contrato and itemSindicato.tipoItemSindicato = :tipoItemSindicato ");
        builder.append(" and :dataVigencia >= itemSindicato.inicioVigencia and :dataVigencia <= coalesce(itemSindicato.finalVigencia,:dataVigencia)");
        Query q = em.createNativeQuery(builder.toString());
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("tipoItemSindicato", TipoItemSindicato.find(codigo).toString());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return new BigDecimal(BigInteger.ZERO);
        }

        return new BigDecimal(resultList.get(0) + "");
    }

    @DescricaoMetodo("recuperaValorItemAssociacao(ep, codigo)")
    public BigDecimal recuperaValorItemAssociacao(EntidadePagavelRH ep, Integer codigo) {
        StringBuilder builder = new StringBuilder();
        builder.append(" select coalesce(itemValorAssociacao.valor,0) from ITEMVALORASSOCIACAO itemValorAssociacao ");
        builder.append(" inner join ASSOCIACAO associacao on itemValorAssociacao.associacao_id = associacao.id ");
        builder.append(" inner join ASSOCIACAOVINCULOFP associacaoVinculoFP on associacao.id = associacaoVinculoFP.id ");
        builder.append(" inner join CONTRATOFP contratoFP on associacaoVinculoFP.vinculofp_id = contratoFP.id ");
        builder.append(" where contratoFP.id = :contrato and itemValorAssociacao.tipoAssociacao = :tipoAssociacao ");
        builder.append(" and :dataVigencia >= itemValorAssociacao.inicioVigencia and :dataVigencia <= coalesce(itemValorAssociacao.finalVigencia,:dataVigencia)");
        Query q = em.createNativeQuery(builder.toString());
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("tipoAssociacao", TipoAssociacao.find(codigo).toString());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return new BigDecimal(BigInteger.ZERO);
        }

        return (BigDecimal) resultList.get(0);
    }

    @DescricaoMetodo("identificaAposentado(ep)")
    public boolean identificaAposentado(EntidadePagavelRH ep) {
        StringBuilder builder = new StringBuilder();
        builder.append(" from Aposentadoria aposentadoria ");
        builder.append(" where aposentadoria.id = :parametroEP ");
        builder.append(" and :dataVigencia >= aposentadoria.inicioVigencia ");
        builder.append(" and :dataVigencia <= coalesce(aposentadoria.finalVigencia,:dataVigencia) ");
        Query q = em.createQuery(builder.toString());

        Aposentadoria a = null;
        if (ep instanceof Aposentadoria) {
            a = (Aposentadoria) ep;
            q.setParameter("parametroEP", a.getId());
        } else {
            q.setParameter("parametroEP", ep.getIdCalculo());
        }
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }


    @DescricaoMetodo("obterCodigoSituacaoFuncional(ep)")
    public Long obterCodigoSituacaoFuncional(EntidadePagavelRH ep) {
        String sql = " select situacaofuncional.codigo " +
            " from CONTRATOFP contrato " +
            "         inner join vinculofp vinculo on contrato.id = vinculo.ID " +
            "         inner join SituacaoContratoFP situacao on situacao.CONTRATOFP_ID = contrato.ID " +
            "         inner join SITUACAOFUNCIONAL situacaofuncional on situacao.SITUACAOFUNCIONAL_ID = situacaofuncional.ID " +
            " where contrato.id = :idContrato  " +
            " and :dataVigencia between situacao.INICIOVIGENCIA and coalesce (situacao.FINALVIGENCIA, :dataVigencia) " +
            " order by situacao.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        q.setParameter("idContrato", ep.getId());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return Long.parseLong(resultList.get(0).toString());
        }
        return null;
    }

    @DescricaoMetodo("estaAfastadoPorTipos(ep, Integer[codigos])")
    public boolean estaAfastadoPorTipos(EntidadePagavelRH ep, Integer... codigos) {
        String hql = " select afas from Afastamento afas " +
            " inner join afas.contratoFP c " +
            " inner join afas.tipoAfastamento tipo " +
            " where c.id = :idContrato and :dataVigencia between afas.inicio and coalesce(afas.termino, :dataVigencia) ";
        if (codigos != null && codigos.length > 0) {
            hql += " and tipo.codigo in :codigos ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("idContrato", ep.getId());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        if (codigos != null && codigos.length > 0) {
            q.setParameter("codigos", Arrays.asList(codigos));
        }
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("estaAfastado(ep)")
    public boolean estaAfastado(EntidadePagavelRH ep) {
        String hql = " select afas from Afastamento afas " +
            " inner join afas.contratoFP c " +
            " inner join afas.tipoAfastamento tipo " +
            " where c.id = :idContrato and :dataVigencia between afas.inicio and coalesce(afas.termino, :dataVigencia) ";
        Query q = em.createQuery(hql);
        q.setParameter("idContrato", ep.getId());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("estaCedidoPorTipo(ep, tipo)")
    public boolean estaCedidoPorTipo(EntidadePagavelRH ep, String tipo) {
        if ("CONUS".equalsIgnoreCase(tipo) || "SONUS".equalsIgnoreCase(tipo)) {
            String hql = " select cedencia from CedenciaContratoFP cedencia " +
                " inner join cedencia.contratoFP c " +
                " where c.id = :idContrato and :dataVigencia between cedencia.dataCessao and coalesce(cedencia.dataRetorno, :dataVigencia) " +
                " and cedencia.tipoContratoCedenciaFP = :tipo ";
            Query q = em.createQuery(hql);
            q.setParameter("idContrato", ep.getId());
            q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
            q.setParameter("tipo", "CONUS".equalsIgnoreCase(tipo) ? TipoCedenciaContratoFP.COM_ONUS : TipoCedenciaContratoFP.SEM_ONUS);
            return !q.getResultList().isEmpty();
        }
        return false;
    }

    @DescricaoMetodo("estaCedido(ep)")
    public boolean estaCedido(EntidadePagavelRH ep) {
        String hql = " select cedencia from CedenciaContratoFP cedencia " +
            " inner join cedencia.contratoFP c " +
            " where c.id = :idContrato and :dataVigencia between cedencia.dataCessao and coalesce(cedencia.dataRetorno, :dataVigencia) ";
        Query q = em.createQuery(hql);
        q.setParameter("idContrato", ep.getId());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("paga13FeriasAut(ep)")
    public boolean paga13FeriasAut(EntidadePagavelRH ep) {
        String hql = " from ConfiguracaoRH config " +
            " where config.tipoTercoFeriasAutomatico is not null " +
            "   and :dataVigencia between config.inicioVigencia and coalesce(config.finalVigencia, :dataVigencia) ";
        Query q = em.createQuery(hql);
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("diasSaldo13FeriasAut(ep)")
    public Double diasSaldo13FeriasAut(EntidadePagavelRH ep) {
        String hql = " select lanc.saldoPeriodoAquisitivo from LancamentoTercoFeriasAut lanc " +
            " where lanc.contratoFP = :contrato " +
            "   and lanc.mes = :mes and lanc.ano = :ano";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("contrato", ep.getContratoFP());
        q.setParameter("mes", getDataReferencia(ep).getMonthValue());
        q.setParameter("ano", getDataReferencia(ep).getYear());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return Double.valueOf((Integer) resultList.get(0));
        }
        return 0.0;
    }

    @DescricaoMetodo("quantidadeDiasDireitoBasePA(ep, tipo)")
    public Double quantidadeDiasDireitoBasePA(EntidadePagavelRH ep, String tipo) {
        String hql = " select basePA.diasDeDireito from ContratoFPCargo cargoContrato " +
            " inner join cargoContrato.contratoFP cont " +
            " inner join cargoContrato.basePeriodoAquisitivo basePA " +
            " where cont = :contrato " +
            " and basePA.tipoPeriodoAquisitivo = :tipoPA " +
            " and :dataVigencia between cargoContrato.inicioVigencia and coalesce(cargoContrato.fimVigencia, :dataVigencia) " +
            " order by cargoContrato.id desc";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("contrato", ep.getContratoFP());
        q.setParameter("tipoPA", "LICPREMIO".equalsIgnoreCase(tipo.trim()) ? TipoPeriodoAquisitivo.LICENCA : TipoPeriodoAquisitivo.FERIAS);
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return Double.valueOf((Integer) resultList.get(0));
        }
        return quantidadeDiasDireitoBasePADoCargo(ep, tipo);
    }


    public Double quantidadeDiasDireitoBasePADoCargo(EntidadePagavelRH ep, String tipo) {
        String hql = " select basePA.diasDeDireito from ContratoFP cont " +
            " inner join cont.cargo carg " +
            " inner join carg.baseCargos baseC " +
            " inner join baseC.basePeriodoAquisitivo basePA " +
            " where cont = :contrato " +
            "   and basePA.tipoPeriodoAquisitivo = :tipoPA " +
            "   and :dataVigencia between baseC.inicioVigencia and coalesce(baseC.finalVigencia, :dataVigencia)" +
            " order by baseC.id desc";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("contrato", ep.getContratoFP());
        q.setParameter("tipoPA", "LICPREMIO".equalsIgnoreCase(tipo.trim()) ? TipoPeriodoAquisitivo.LICENCA : TipoPeriodoAquisitivo.FERIAS);
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return Double.valueOf((Integer) resultList.get(0));
        }
        return 0.0;
    }

    @DescricaoMetodo("tipoPagamento13FeriasAutomatico(ep)")
    public Integer tipoPagamento13FeriasAutomatico(EntidadePagavelRH ep) {
        String hql = " from ConfiguracaoRH config " +
            " where config.tipoTercoFeriasAutomatico is not null " +
            "   and :dataVigencia between config.inicioVigencia and coalesce(config.finalVigencia, :dataVigencia) ";
        Query q = em.createQuery(hql);
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return ((ConfiguracaoRH) resultList.get(0)).getTipoTercoFeriasAutomatico().getCodigo();
        }
        return 0;
    }

    public boolean identificaTipoPeq(EntidadePagavelRH ep, String tipoPEQ) {
        String sql = "select TIPOPEQ from CONTRATOFP where id = :idContrato and tipopeq = :tipoPEQ";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        q.setParameter("idContrato", ep.getId());
        q.setParameter("tipoPEQ", TipoPeq.valueOf(tipoPEQ).name());
        return !q.getResultList().isEmpty();
    }

    public Integer quantidadeTipoPeq(EntidadePagavelRH ep, String tipoPEQ) {
        String sql = "select count(distinct contrato.id) " +
            " from CONTRATOFP contrato " +
            "         inner join MODALIDADECONTRATOFP modalidade on contrato.MODALIDADECONTRATOFP_ID = modalidade.id " +
            "         inner join SITUACAOCONTRATOFP situacaocontrato on situacaocontrato.CONTRATOFP_ID = contrato.ID " +
            "         inner join SITUACAOFUNCIONAL situacao on situacaocontrato.SITUACAOFUNCIONAL_ID = situacao.ID " +
            " where TIPOPEQ =  :tipoPEQ " +
            "  and modalidade.CODIGO = :modalidadeContrato " +
            "  and (:dataVigencia between situacaocontrato.INICIOVIGENCIA and coalesce(situacaocontrato.FINALVIGENCIA, :dataVigencia) " +
            "    and (situacao.codigo = 2 or situacao.codigo = 1))";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        q.setParameter("tipoPEQ", TipoPeq.valueOf(tipoPEQ).name());
        q.setParameter("modalidadeContrato", ModalidadeContratoFP.CONCURSADOS);
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            BigDecimal bg = (BigDecimal) resultList.get(0);
            return bg.intValue();
        }
        return 0;
    }

    @DescricaoMetodo("obterValorAposentadoria(ep)")
    public Double obterValorAposentadoria(EntidadePagavelRH ep, String[] eventos) {
        StringBuilder builder = new StringBuilder();
//        builder.append(" select sum(coalesce(itemAposentadoria.valor,0)) from ITEMAPOSENTADORIA itemAposentadoria ");
//        builder.append(" inner join APOSENTADORIA aposentadoria on itemAposentadoria.aposentadoria_id = aposentadoria.id ");
//        builder.append(" inner join VINCULOFP vinculoFP on vinculoFP.id = aposentadoria.id ");
//        builder.append(" where aposentadoria.id = :parametroEP ");
//        builder.append(" and :dataVigencia >= vinculoFP.inicioVigencia and :dataVigencia <= coalesce(vinculoFP.finalVigencia,:dataVigencia) ");
//        builder.append(" and :dataVigencia between vinculoFP.inicioVigencia and coalesce(vinculoFP.finalVigencia,:dataVigencia) ");
        //    builder.append(" and :dataVigencia  between itemAposentadoria.inicioVigencia and coalesce(itemAposentadoria.finalVigencia,:dataVigencia)");

        builder.append("select sum(coalesce(item.valor,0)) from ItemAposentadoria item inner join item.aposentadoria apo" +
            " where apo.id = :parametroEP" +
            " and  item.eventoFP.codigo in (:eventos) " +
            " and trunc(:dataVigencia) between trunc(item.inicioVigencia) and trunc(coalesce(item.finalVigencia,:dataVigencia))" +
            " and :dataVigencia between apo.inicioVigencia and coalesce(apo.finalVigencia,:dataVigencia) ");
        Query q = em.createQuery(builder.toString());
        VinculoFP v = (VinculoFP) ep;
        q.setParameter("parametroEP", v.getId());
        q.setParameter("eventos", Arrays.asList(eventos));
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return 0.0;
        }

        BigDecimal valor = (BigDecimal) q.getSingleResult();
        if (valor == null) return 0.0;

        return valor.doubleValue();
    }

    public boolean podeCalcularParaAposentado(EntidadePagavelRH ep, String codigo) {
        if (!(ep instanceof Aposentadoria)) {
            return false;
        }
        String hql = "select itens from Aposentadoria apo inner join apo.itemAposentadorias itens" +
            " where apo.id = :id" +
            " and :data between apo.inicioVigencia and coalesce(apo.finalVigencia,:data)" +
            " and trunc(:data) between trunc(itens.inicioVigencia) and trunc(coalesce(itens.finalVigencia,:data)) " +
            " and itens.eventoFP.codigo = :codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("id", ((VinculoFP) ep).getId());
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        q.setParameter("codigo", codigo.trim());
        List<ItemAposentadoria> retorno = q.getResultList();
        return !retorno.isEmpty();
    }

    public boolean podeCalcularParaPensionista(EntidadePagavelRH ep, String codigo) {
        if (!(ep instanceof Pensionista)) {
            return false;
        }
        String hql = "select itens from Pensionista pe inner join pe.itensValorPensionista  itens " +
            " where pe.id = :id" +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pe.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pe.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(itens.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(itens.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy')  " +
            " and itens.eventoFP.codigo = :codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("id", ((VinculoFP) ep).getId());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        q.setParameter("codigo", codigo.trim());
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("temFuncaoGratificada(ep)")
    public boolean temFuncaoGratificada(EntidadePagavelRH ep) {
        try {
            return obterValorFuncaoGratificada(ep) > 0;
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método temFuncaoGratificada", e);
        }
    }


    @DescricaoMetodo("obterValorFuncaoGratificada(ep)")
    public Double obterValorFuncaoGratificada(EntidadePagavelRH ep) {
        BigDecimal retorno = BigDecimal.ZERO;

        String hql = "select new FuncaoGratificada(fg.inicioVigencia, fg.finalVigencia, coalesce(pcs.vencimentoBase,0)) from FuncaoGratificada  fg" +
            ", EnquadramentoPCS  pcs join fg.enquadramentoFGs efgs" +
            " where efgs.categoriaPCS = pcs.categoriaPCS and  efgs.progressaoPCS = pcs.progressaoPCS" +
            " and fg.contratoFP.id = :id " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(fg.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(fg.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(efgs.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(efgs.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(:dataString,'dd/MM/yyyy') between pcs.inicioVigencia and coalesce(pcs.finalVigencia,:dataVigencia) order by fg.inicioVigencia ";
        Query q = em.createQuery(hql);
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        q.setParameter("dataString", getDataFormatada(localDateToDate(getDataReferencia(ep))));
        try {
            List<ValidadorVigenciaFolha> resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0.0;
            }
            if (resultList.size() == 1) {
                retorno = resultList.get(0).getValor();
            } else {
                retorno = BigDecimal.valueOf(realizarMediaSalarial(ep, getDataReferencia(ep), resultList));
            }
            return retorno.doubleValue();
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método obterValorFuncaoGratificada", e);
        }

    }

    public Double obterValorFuncaoGratificadaPorTipo(EntidadePagavelRH ep, String tipoFuncao) {
        BigDecimal retorno = BigDecimal.ZERO;

        String hql = "select new FuncaoGratificada(fg.inicioVigencia, fg.finalVigencia, coalesce(pcs.vencimentoBase,0)) from FuncaoGratificada  fg" +
            ", EnquadramentoPCS  pcs join fg.enquadramentoFGs efgs" +
            " where efgs.categoriaPCS = pcs.categoriaPCS and  efgs.progressaoPCS = pcs.progressaoPCS" +
            " and fg.contratoFP.id = :id " +
            " and fg.tipoFuncaoGratificada = :tipoFuncao " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(fg.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(fg.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(efgs.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(efgs.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(:dataString,'dd/MM/yyyy') between pcs.inicioVigencia and coalesce(pcs.finalVigencia,:dataVigencia) order by fg.inicioVigencia ";
        Query q = em.createQuery(hql);
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        q.setParameter("dataString", getDataFormatada(localDateToDate(getDataReferencia(ep))));
        q.setParameter("tipoFuncao", TipoFuncaoGratificada.valueOf(tipoFuncao));
        try {
            List<ValidadorVigenciaFolha> resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0.0;
            }
            if (resultList.size() == 1) {
                retorno = resultList.get(0).getValor();
            } else {
                retorno = BigDecimal.valueOf(realizarMediaSalarial(ep, getDataReferencia(ep), resultList));
            }
            return retorno.doubleValue();
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método obterValorFuncaoGratificada", e);
        }

    }

    public List<ValidadorVigenciaFolha> obterFuncaoGratificada(EntidadePagavelRH ep, String tipoFuncao) {
        String hql = "select fg from FuncaoGratificada  fg" +
            ", EnquadramentoPCS  pcs join fg.enquadramentoFGs efgs" +
            " where efgs.categoriaPCS = pcs.categoriaPCS and  efgs.progressaoPCS = pcs.progressaoPCS" +
            " and fg.contratoFP.id = :id " +
            " and fg.tipoFuncaoGratificada = :tipoFuncao " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(fg.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(fg.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(efgs.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(efgs.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(:dataString,'dd/MM/yyyy') between pcs.inicioVigencia and coalesce(pcs.finalVigencia,:dataVigencia) order by fg.inicioVigencia ";
        Query q = em.createQuery(hql);
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        q.setParameter("dataString", Util.dateToString(localDateToDate(getDataReferencia(ep))));
        if (tipoFuncao == null) {
            q.setParameter("tipoFuncao", TipoFuncaoGratificada.NORMAL);
        } else {
            q.setParameter("tipoFuncao", TipoFuncaoGratificada.valueOf(tipoFuncao));
        }

        try {
            List resultList = q.getResultList();
            if (resultList == null || resultList.isEmpty()) {
                return Lists.newArrayList();
            }
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método obterValorFuncaoGratificada", e);
        }
    }

    @DescricaoMetodo("identificaPensionista(ep)")
    public boolean identificaPensionista(EntidadePagavelRH ep) {
        if (!(ep instanceof Pensionista)) {
            return false;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(" select pensionista.id from ItemValorPensionista item ");
        builder.append(" inner join item.pensionista pensionista ");
        builder.append(" where pensionista.id = :parametroEP ");
        builder.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(item.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(item.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");
        builder.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pensionista.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pensionista.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy')  ");
        Query q = em.createQuery(builder.toString());
        q.setParameter("parametroEP", ((Pensionista) ep).getId());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
//        logger.debug("pensionista :" + !q.getResultList().isEmpty());
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("obterValorPensionista(ep)")
    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public Double obterValorPensionista(EntidadePagavelRH ep, String codigo, Boolean considerarValorParaBaseCalculo) {

        if (ep instanceof Pensionista) {
            StringBuilder builder = new StringBuilder();
            builder.append(" select item from ItemValorPensionista item ");
            builder.append(" inner join item.pensionista pensionista ");
            builder.append(" where pensionista.id = :parametroEP ");
            builder.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(item.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(item.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");
            builder.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pensionista.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pensionista.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy')  ");
            if (codigo != null) {
                builder.append(" and item.eventoFP.codigo = :codigo ");
            }
            builder.append(" order by item.inicioVigencia desc ");
            Query q = em.createQuery(builder.toString());
            q.setParameter("parametroEP", ((Pensionista) ep).getId());
            q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
            if (codigo != null) {
                q.setParameter("codigo", codigo);
            }
            List<ValidadorVigenciaFolha> resultList = new LinkedList<>();
            resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0.0;
            } else {
                verificarFinalVigenciaVinculo(ep, resultList);
                if (considerarValorParaBaseCalculo) {
                    ValidadorVigenciaFolha validadorVigenciaFolha = resultList.get(0);
                    if (validadorVigenciaFolha.getFimVigencia() != null) validadorVigenciaFolha.setFimVigencia(null);
                }
                return realizarMediaSalarial(ep, getDataReferencia(ep), resultList);
            }
        } else {
            return 0.0;
        }
    }

    @DescricaoMetodo("obterValorPensionista(ep)")
    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public Double obterValorPensionista(EntidadePagavelRH ep, Boolean considerarValorParaBaseCalculo, String modo, String... codigos) {

        if (ep instanceof Pensionista) {
            StringBuilder builder = new StringBuilder();
            builder.append(" select item from ItemValorPensionista item ");
            builder.append(" inner join item.pensionista pensionista ");
            builder.append(" where pensionista.id = :parametroEP ");
            if (TipoFiltroValorPensionista.INICIO.name().equalsIgnoreCase(modo.trim()) || TipoFiltroValorPensionista.FINAL.name().equalsIgnoreCase(modo.trim()) || TipoFiltroValorPensionista.CALCULO.name().equalsIgnoreCase(modo.trim())) {
                builder.append(" and trunc(:dataVigencia) between trunc(item.inicioVigencia) and trunc(coalesce(item.finalVigencia,:dataVigencia)) ");
                builder.append(" and trunc(:dataVigencia) between trunc(pensionista.inicioVigencia) and trunc(coalesce(pensionista.finalVigencia,:dataVigencia)) ");
            } else {
                builder.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(item.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(item.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");
                builder.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pensionista.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pensionista.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy')  ");
            }
            if (codigos != null && !Arrays.asList(codigos).contains("TODAS")) {
                builder.append(" and item.eventoFP.codigo in (:codigos) ");
            }
            builder.append(" order by item.inicioVigencia desc ");
            Query q = em.createQuery(builder.toString());
            q.setParameter("parametroEP", ((Pensionista) ep).getId());
            if (TipoFiltroValorPensionista.INICIO.name().equalsIgnoreCase(modo.trim())) {
                q.setParameter("dataVigencia", DataUtil.primeiroDiaMes(getDataReferencia(ep)));
            } else if (TipoFiltroValorPensionista.FINAL.name().equalsIgnoreCase(modo.trim())) {
                q.setParameter("dataVigencia", DataUtil.ultimoDiaMes(getDataReferencia(ep)));
            } else if (TipoFiltroValorPensionista.CALCULO.name().equalsIgnoreCase(modo.trim())) {
                q.setParameter("dataVigencia", SistemaFacade.getDataCorrente());
            } else {
                q.setParameter("dataVigencia", getDataReferencia(ep));
            }
            if (codigos != null && !Arrays.asList(codigos).contains("TODAS")) {
                q.setParameter("codigos", Arrays.asList(codigos));
            }
            List<ValidadorVigenciaFolha> resultList = new LinkedList<>();
            resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0.0;
            } else {
                verificarFinalVigenciaVinculo(ep, resultList);
                if (considerarValorParaBaseCalculo) {
                    ValidadorVigenciaFolha validadorVigenciaFolha = resultList.get(0);
                    if (validadorVigenciaFolha.getFimVigencia() != null) validadorVigenciaFolha.setFimVigencia(null);
                }
                return realizarMediaSalarial(ep, getDataReferencia(ep), resultList);
            }
        } else {
            return 0.0;
        }
    }


    @DescricaoMetodo("obterReferenciaPensionista(ep, mes, ano, codigoEvento)")
    public Double obterReferenciaPensionista(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo, String codigoEvento) {
        VinculoFP vinculo = (VinculoFP) ep;
        List<ItemValorPensionista> itensValorPensionista = obterItensValorPensionista(ep, codigoEvento);
        LocalDate inicioVigencia = null;
        LocalDate finalVigencia = null;
        Integer retorno = 0;

        for (ItemValorPensionista itemValorPensionista : itensValorPensionista) {
            inicioVigencia = dateToLocalDate(itemValorPensionista.getInicioVigencia());
            if (itemValorPensionista != null && itemValorPensionista.getFinalVigencia() != null) {
                if (vinculo.getFinalVigencia() != null && itemValorPensionista.getFinalVigencia().after(vinculo.getFinalVigencia())) {
                    finalVigencia = dateToLocalDate(vinculo.getFinalVigencia());
                } else {
                    finalVigencia = dateToLocalDate(itemValorPensionista.getFinalVigencia());
                }
            } else {
                finalVigencia = vinculo.getFinalVigencia() == null ? null : dateToLocalDate(vinculo.getFinalVigencia());
            }

            LocalDate varIniMes = dateToLocalDate(Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mesDeCalculo).ordinal(), anoDeCalculo));
            LocalDate varFimMes = varIniMes.withDayOfMonth(varIniMes.lengthOfMonth());


            LocalDate dataIni = varIniMes;
            LocalDate dataInicioVigencia = inicioVigencia;

            if (dataInicioVigencia.isAfter(varFimMes)) {
                return 0.0;
            }

            if (dataIni.isBefore(dataInicioVigencia)) {
                varIniMes = inicioVigencia;
            }

            LocalDate dataFim = varFimMes;
            if (finalVigencia != null) {
                LocalDate dataFinalVigencia = finalVigencia;
                if (dataFim.isAfter(dataFinalVigencia)) {
                    varFimMes = finalVigencia;
                }
            }

            LocalDate varBeginMes = varIniMes;
            LocalDate varEndMes = varFimMes;

            if (varBeginMes.isAfter(varEndMes) && !varBeginMes.isEqual(varEndMes)) {
                return 0.0;
            }
            Integer totalDias = varFimMes.getDayOfMonth() - varIniMes.getDayOfMonth() + 1;

            if (totalDias.intValue() < 0) {
                totalDias = 0;
            }
            retorno += totalDias;
        }
        return retorno.doubleValue();
    }

    public List<ItemValorPensionista> obterItensValorPensionista(EntidadePagavelRH ep, String codigoEvento) {
        String hql = "select itens from Pensionista pe inner join pe.itensValorPensionista  itens " +
            " inner join itens.eventoFP e " +
            " where pe.id = :id " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(itens.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(itens.finalVigencia, :dataVigencia),'mm/yyyy'),'mm/yyyy')  " +
            " and e.codigo = :codigo " +
            " order by itens.id desc ";
        Query q = em.createQuery(hql);
        q.setParameter("id", ((VinculoFP) ep).getId());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        q.setParameter("codigo", codigoEvento);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }


    private void verificarFinalVigenciaVinculo(EntidadePagavelRH ep, List<ValidadorVigenciaFolha> resultList) {
        if (!resultList.isEmpty()) {
            ValidadorVigenciaFolha ultimoRegistro = resultList.get(0);
            Date finalVigencia = ep.getFinalVigencia();
            if (finalVigencia != null && ultimoRegistro.getFimVigencia() != null && finalVigencia.before(ultimoRegistro.getFimVigencia())) {
                ultimoRegistro.setFimVigencia(finalVigencia);
            }
            if (finalVigencia != null && ultimoRegistro.getFimVigencia() == null) {
                ultimoRegistro.setFimVigencia(finalVigencia);
            }
        }
    }

    @DescricaoMetodo("calculaBaseInstituidorPensaoPrev(ep)")
    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public Double calculaBaseInstituidorPensaoPrev(EntidadePagavelRH ep) {

        String hql = " select distinct pensionista from ItemValorPensionista item " +
            " inner join item.pensionista pensionista " +
            " inner join pensionista.pensao pensao " +
            " where pensao.id = :parametroEP " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(item.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(item.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pensionista.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pensionista.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ";

        Query q = em.createQuery(hql);
        q.setParameter("parametroEP", ((Pensionista) ep).getPensao().getId());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        List<EntidadePagavelRH> pensionistas = Lists.newArrayList();
        Double valor = 0.0;

        pensionistas = q.getResultList();
        if (pensionistas != null && !pensionistas.isEmpty()) {
            for (EntidadePagavelRH pensionista : pensionistas) {
                pensionista.setAno(ep.getAno());
                pensionista.setMes(ep.getMes());
                pensionista.setFolha(ep.getFolha());
                valor += obterValorPensionista(pensionista, null, true);
            }
        }
        return valor;
    }

    @DescricaoMetodo("temLocalInsalubre(ep)")
    public boolean temLocalInsalubre(EntidadePagavelRH ep) {
        try {
            return obterValorLocalInsalubre(ep) > 0.0;
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método temLocalInsalubre", e);
        }
    }

    @DescricaoMetodo("obterValorLocalInsalubre(ep)")
    public Double obterValorLocalInsalubre(EntidadePagavelRH ep) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select valorUnidade.VALOR from HORARIOCONTRATOFP horario ");
        sb.append(" inner join LOTACAOFUNCIONAL lotacao on lotacao.HORARIOCONTRATOFP_ID = horario.ID ");
        sb.append(" inner join VALORUNIDADEORGANIZACIONAL valorUnidade on valorUnidade.UNIDADEORGANIZACIONAL_ID = lotacao.UNIDADEORGANIZACIONAL_ID  ");
        sb.append(" inner join ITEMLAUDO itemLaudo on itemLaudo.ID = valorUnidade.ITEMLAUDO_ID ");
        sb.append(" inner join LAUDO laudo on laudo.ID = itemLaudo.LAUDO_ID ");
        sb.append(" where lotacao.VINCULOFP_ID = :ep ");
        sb.append(" and valorUnidade.TIPONATUREZAATIVIDADEFP = :tipo");
        sb.append(" and :dataVigencia >= laudo.inicioVigencia and :dataVigencia <= coalesce(laudo.fimVigencia,:dataVigencia) ");
        sb.append(" and :dataVigencia >= horario.inicioVigencia and :dataVigencia <= coalesce(horario.finalVigencia,:dataVigencia) ");
        sb.append(" and :dataVigencia >= lotacao.inicioVigencia and :dataVigencia <= coalesce(lotacao.finalVigencia,:dataVigencia) ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("tipo", TipoNaturezaAtividadeFP.find(2).toString());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        try {
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0.0;
            }
            BigDecimal retorno = (BigDecimal) resultList.get(0);
            return retorno.doubleValue();
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método obterValorLocalInsalubre", e);
        }
    }

    @DescricaoMetodo("temLocalPenoso(ep)")
    public boolean temLocalPenoso(EntidadePagavelRH ep) {
        try {
            return obterValorLocalPenosidade(ep) > 0.0;
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método temLocalPenoso", e);
        }
    }

    @DescricaoMetodo("obterValorLocalPenosidade(ep)")
    public Double obterValorLocalPenosidade(EntidadePagavelRH ep) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select valorUnidade.VALOR from HORARIOCONTRATOFP horario ");
        sb.append(" inner join LOTACAOFUNCIONAL lotacao on lotacao.HORARIOCONTRATOFP_ID = horario.ID ");
        sb.append(" inner join VALORUNIDADEORGANIZACIONAL valorUnidade on valorUnidade.UNIDADEORGANIZACIONAL_ID = lotacao.UNIDADEORGANIZACIONAL_ID  ");
        sb.append(" inner join ITEMLAUDO itemLaudo on itemLaudo.ID = valorUnidade.ITEMLAUDO_ID ");
        sb.append(" inner join LAUDO laudo on laudo.ID = itemLaudo.LAUDO_ID ");
        sb.append(" where lotacao.VINCULOFP_ID = :ep ");
        sb.append(" and valorUnidade.TIPONATUREZAATIVIDADEFP = :tipo");
        sb.append(" and :dataVigencia >= laudo.inicioVigencia and :dataVigencia <= coalesce(laudo.fimVigencia,:dataVigencia) ");
        sb.append(" and :dataVigencia >= horario.inicioVigencia and :dataVigencia <= coalesce(horario.finalVigencia,:dataVigencia) ");
        sb.append(" and :dataVigencia >= lotacao.inicioVigencia and :dataVigencia <= coalesce(lotacao.finalVigencia,:dataVigencia) ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("tipo", TipoNaturezaAtividadeFP.find(1).toString());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        try {
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0.0;
            }

            BigDecimal retorno = (BigDecimal) resultList.get(0);
            return retorno.doubleValue();
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método obterValorLocalPenosidade", e);
        }
    }

    @DescricaoMetodo("temLocalPericuloso(ep)")
    public boolean temLocalPericuloso(EntidadePagavelRH ep) {
        try {
            return obterValorLocalPericuloso(ep) > 0.0;
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método temLocalPericuloso", e);
        }
    }

    @DescricaoMetodo("obterValorLocalPericuloso(ep)")
    public Double obterValorLocalPericuloso(EntidadePagavelRH ep) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select valorUnidade.VALOR from HORARIOCONTRATOFP horario ");
        sb.append(" inner join LOTACAOFUNCIONAL lotacao on lotacao.HORARIOCONTRATOFP_ID = horario.ID ");
        sb.append(" inner join VALORUNIDADEORGANIZACIONAL valorUnidade on valorUnidade.UNIDADEORGANIZACIONAL_ID = lotacao.UNIDADEORGANIZACIONAL_ID  ");
        sb.append(" inner join ITEMLAUDO itemLaudo on itemLaudo.ID = valorUnidade.ITEMLAUDO_ID ");
        sb.append(" inner join LAUDO laudo on laudo.ID = itemLaudo.LAUDO_ID ");
        sb.append(" where lotacao.VINCULOFP_ID = :ep ");
        sb.append(" and valorUnidade.TIPONATUREZAATIVIDADEFP = :tipo");
        sb.append(" and :dataVigencia >= laudo.inicioVigencia and :dataVigencia <= coalesce(laudo.fimVigencia,:dataVigencia) ");
        sb.append(" and :dataVigencia >= horario.inicioVigencia and :dataVigencia <= coalesce(horario.finalVigencia,:dataVigencia) ");
        sb.append(" and :dataVigencia >= lotacao.inicioVigencia and :dataVigencia <= coalesce(lotacao.finalVigencia,:dataVigencia) ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("tipo", TipoNaturezaAtividadeFP.find(3).toString());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        try {
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0.0;
            }

            BigDecimal retorno = (BigDecimal) resultList.get(0);
            return retorno.doubleValue();
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("Erro ao executar método obterValorLocalPericuloso", e);
        }
    }

    @DescricaoMetodo("temPensaoJudicial(ep)")
    public boolean temPensaoJudicial(EntidadePagavelRH ep) {
        Query q = em.createQuery("select beneficiario.id from Beneficiario beneficiario inner join beneficiario.itensBeneficiarios item "
            + "  where beneficiario.id = :ep "
            + " and :dataVigencia between item.inicioVigencia and coalesce(item.finalVigencia,:dataVigencia)");
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("obterParametroPensaoJudicial(ep)")
    public Double obterParametroPensaoJudicial(EntidadePagavelRH ep) {
        Query q = em.createQuery("select parametro.codigo from Beneficiario beneficiario "
            + " inner join beneficiario.itensBeneficiarios item "
            + " inner join item.parametroCalcIndenizacao parametro "
            + " where beneficiario.id = :ep "
            + " and :dataVigencia between  item.inicioVigencia and coalesce(item.finalVigencia,:dataVigencia) ");
        //System.out.println(ep.getIdCalculo());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));

        try {
            if (q.getResultList().isEmpty()) {
                return 0.0;
            }

            String retorno = (String) q.getResultList().get(0);
            return Double.valueOf(retorno);
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("erro ao executar método obterParametroPensaoJudicial", e);
        }
    }

    @DescricaoMetodo("temPensaoAlimenticia(ep)")
    public boolean temPensaoAlimenticia(EntidadePagavelRH ep, String codigoEvento) {
        StringBuilder queryString = new StringBuilder().append(" select v.valor from PensaoAlimenticia p ")
            .append(" inner join p.beneficiosPensaoAlimenticia b ")
            .append(" inner join b.valoresPensaoAlimenticia v ")
            .append(" where p.vinculoFP.id = :ep and b.eventoFP.codigo = :evento ")
            .append(" and :dataVigencia >= v.inicioVigencia ")
            .append(" and :dataVigencia <= coalesce(v.finalVigencia,:dataVigencia) ");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getId());
        q.setParameter("evento", codigoEvento);
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("temPensaoAlimenticiaEIrrfFicticio(ep)")
    public boolean temPensaoAlimenticiaEIrrfFicticio(EntidadePagavelRH ep, String codigoEvento) {
        StringBuilder queryString = new StringBuilder()
            .append(" select v.valor from PensaoAlimenticia p ")
            .append(" inner join p.beneficiosPensaoAlimenticia b ")
            .append(" inner join b.valoresPensaoAlimenticia v ")
            .append(" where p.vinculoFP.id = :ep and b.eventoFP.codigo = :evento ")
            .append(" and v.irrfFicticio is true ")
            .append(" and :dataVigencia >= b.inicioVigencia ")
            .append(" and :dataVigencia <= coalesce(b.finalVigencia,:dataVigencia) ");
        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getId());
        q.setParameter("evento", codigoEvento);
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }

//    @DescricaoMetodo("recuperaTipoFolha()")
//    public String recuperaTipoFolha() {
//        return folha.getTipoFolhaDePagamento().name();
//    }

    @DescricaoMetodo("obterValorPensaoAlimenticia(ep)")
    public Double obterValorPensaoAlimenticia(EntidadePagavelRH ep, EventoFP eventoFP) {
        if (!(ep instanceof PensaoAlimenticia)) {
            return 0.0;
        }
        StringBuilder queryString = new StringBuilder().append(" select coalesce(sum(v.valor),0) from PensaoAlimenticia p ");
        queryString.append(" inner join p.beneficiosPensaoAlimenticia b ");
        queryString.append(" inner join b.valoresPensaoAlimenticia v ");
        queryString.append(" where p.vinculoFP.id = :ep and b.eventoFP = :evento");
        queryString.append(" and :dataVigencia >= v.inicioVigencia ");
        queryString.append(" and :dataVigencia <= coalesce(v.finalVigencia,:dataVigencia) ");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getId());
        q.setParameter("evento", eventoFP);
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        try {
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return 0.0;
            }
            BigDecimal retorno = (BigDecimal) resultList.get(0);
            return retorno.doubleValue();
        } catch (RuntimeException e) {
            throw new FuncoesFolhaFacadeException("erro ao executar método obterValorPensaoAlimenticia", e);
        }

    }

    @DescricaoMetodo("temOpcaoValeTransporte(ep)")
    public boolean temOpcaoValeTransporte(EntidadePagavelRH ep) {
        StringBuilder queryString = new StringBuilder().append(" select vale.id from OpcaoValeTransporteFP vale ");
        queryString.append(" inner join vale.contratoFP contrato ");
        queryString.append(" where contrato.id = :ep ");
        queryString.append(" and vale.optante is true  ");
        queryString.append(" and :dataVigencia >= vale.inicioVigencia ");
        queryString.append(" and :dataVigencia <= coalesce(vale.finalVigencia,:dataVigencia) ");
        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        TipoAfastamento tipo = getCalcularValeTransporteParaEsseAfastamento(ep);
        if (tipo == null) {
            return !q.getResultList().isEmpty();
        } else
            return !q.getResultList().isEmpty() && (tipo.getCalcularValeTransporte());
    }

    @DescricaoMetodo("quantidadeValeTransporte(ep)")
    public int quantidadeValeTransporte(EntidadePagavelRH ep) {
        StringBuilder queryString = new StringBuilder().append(" select vale from OpcaoValeTransporteFP vale ");
        queryString.append(" inner join vale.contratoFP contrato ");
        queryString.append(" where contrato.id = :ep ");
        queryString.append(" and vale.optante = true  ");
        queryString.append(" and :dataVigencia >= vale.inicioVigencia ");
        queryString.append(" and :dataVigencia <= coalesce(vale.finalVigencia,:dataVigencia) ");
        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));

        if (!q.getResultList().isEmpty()) {
            OpcaoValeTransporteFP vale = (OpcaoValeTransporteFP) q.getResultList().get(0);
            if (vale.getQuantidade() == null) {
                logger.debug("quantidade do vale transporte é nulo ");
                return 0;
            }
            return (vale.getQuantidade() + (vale.getComplementoQuantidade() == null ? 0 : vale.getComplementoQuantidade()));
        }
        return 0;
    }

    public TipoAfastamento getCalcularValeTransporteParaEsseAfastamento(EntidadePagavelRH ep) {
        Query q = em.createQuery("select tipo from Afastamento afa inner join afa.tipoAfastamento tipo " +
            " where :data between afa.inicio and coalesce(afa.termino,:data) and afa.contratoFP.id= :ep");
        q.setMaxResults(1);
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        q.setParameter("ep", ep.getIdCalculo());
        if (q.getResultList().isEmpty()) return null;
        return (TipoAfastamento) q.getResultList().get(0);
    }

    @DescricaoMetodo("recuperaTipoReajusteAposentadoria(ep)")
    public String recuperaTipoReajusteAposentadoria(EntidadePagavelRH ep) {
        try {
            StringBuilder queryString = new StringBuilder().append(" from Aposentadoria aposentadoria ");
            queryString.append(" where aposentadoria.id = :ep ");
            queryString.append(" and :dataVigencia >= aposentadoria.inicioVigencia ");
            queryString.append(" and :dataVigencia <= coalesce(aposentadoria.finalVigencia,:dataVigencia) ");
            Query q = em.createQuery(queryString.toString());
            q.setMaxResults(1);
            Aposentadoria a = null;
            if (ep instanceof Aposentadoria) {
                a = (Aposentadoria) ep;
                q.setParameter("ep", a.getId());
            } else {
                q.setParameter("ep", ep.getIdCalculo());
            }

            q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
            if (q.getResultList().isEmpty()) {
                return null;
            }
            Aposentadoria aposentadoria = (Aposentadoria) q.getSingleResult();
            return aposentadoria.getTipoReajusteAposentadoria().name();
        } catch (Exception e) {
            logger.debug("Nenhum valor encontratdo para recuperaTipoReajusteAposentadoria" + ep + ", erro: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    //    @DescricaoMetodo("recuperaTipoValorPensaoAlimenticia(pensaoAlimenticia)")
    public TipoValorPensaoAlimenticia recuperaTipoValorPensaoAlimenticia(EntidadePagavelRH ep, String codigoEvento) {
        Query q = em.createQuery("select beneficio.tipoValorPensaoAlimenticia "
            + " from PensaoAlimenticia pensaoAlimenticia"
            + " inner join pensaoAlimenticia.beneficiosPensaoAlimenticia beneficio"
            + " where pensaoAlimenticia.vinculoFP.id = :p and beneficio.eventoFP.codigo = :evento" +
            " and :data between beneficio.inicioVigencia and coalesce(beneficio.finalVigencia, :data) ");
        q.setParameter("p", ep.getId());
        q.setParameter("evento", codigoEvento);
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (TipoValorPensaoAlimenticia) q.getSingleResult();
    }

    public boolean temFaltas(EntidadePagavelRH ep, String codigo, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        return recuperaNumeroFaltas(ep, codigo, detalheProcessamentoFolha) > 0;
    }

    public double recuperaNumeroFaltas(EntidadePagavelRH ep, String codigo, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        Integer totalFaltas = 0;

        String hql = " from Faltas faltas "
            + " where faltas.contratoFP.id = :ep "
            + " and faltas.tipoFalta = :tipo ";

        hql += " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(faltas.inicio,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(faltas.fim,:dataVigencia),'mm/yyyy'),'mm/yyyy') ";

        java.time.LocalDate dataParaFalta = getPrimeiroDiaDoMesCalculo(ep);
        Query q = em.createQuery(hql);
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("tipo", TipoFalta.FALTA_INJUSTIFICADA);
        q.setParameter("dataVigencia", localDateToDate(dataParaFalta));

        LocalDate primeiroDiaDoMes = dataParaFalta;
        LocalDate ultimoDiaDoMes = dataParaFalta.withDayOfMonth(dataParaFalta.lengthOfMonth());

        for (Faltas falta : (List<Faltas>) q.getResultList()) {
            LocalDate inicioFalta = dateToLocalDate(falta.getInicio());
            LocalDate fimFalta = falta.getFim() != null ? dateToLocalDate(falta.getFim()) : ultimoDiaDoMes;

            if (inicioFalta.isBefore(primeiroDiaDoMes)) {
                inicioFalta = primeiroDiaDoMes;
            }

            if (fimFalta.isAfter(ultimoDiaDoMes)) {
                fimFalta = ultimoDiaDoMes;
            }

            // Só considera se início for antes ou igual ao fim
            if (!inicioFalta.isAfter(fimFalta)) {
                long diasNoMes = ChronoUnit.DAYS.between(inicioFalta, fimFalta) + 1;
                totalFaltas += (int) diasNoMes;
                logger.debug("Período ajustado: {} até {} = {} dia(s)", inicioFalta, fimFalta, diasNoMes);
            } else {
                logger.debug("Falta ignorada (fora do mês): início={} fim={}", inicioFalta, fimFalta);
            }
        }
        return totalFaltas.doubleValue();
    }

    @DescricaoMetodo("temFaltasNovo(ep)")
    public boolean temFaltasNovo(EntidadePagavelRH ep) {
        return recuperaNumeroFaltasNovo(ep) > 0;
    }

    @DescricaoMetodo("recuperaNumeroFaltasNovo(ep)")
    public Double recuperaNumeroFaltasNovo(EntidadePagavelRH ep) {
        Integer totalFaltas = 0;
        Date dataInicio = folhaDePagamentoFacade.recuperaDataUltimaFolhaConf();
        if (dataInicio == null) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.set(Calendar.DAY_OF_MONTH, -30);
            dataInicio = c.getTime();
        }
        Date dataFim = new Date();
        StringBuilder sb = new StringBuilder();
        sb.append(" from Faltas faltas where faltas.dataCadastro between :dataInicio and :dataFim");
        sb.append(" and faltas.contratoFP.id = :ep ");
        sb.append(" and faltas.tipoFalta = :tipo ");
        Query q = em.createQuery(sb.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("tipo", TipoFalta.FALTA_INJUSTIFICADA);
        q.setParameter("dataInicio", dataInicio);
        q.setParameter("dataFim", dataFim);
        for (Faltas falta : (List<Faltas>) q.getResultList()) {
            Calendar cInicio = Calendar.getInstance();
            Calendar cFim = Calendar.getInstance();
            Date dataHoje = Util.getDataHoraMinutoSegundoZerado(new Date());
            if (falta.getInicio().before(dataHoje) && falta.getFim().after(dataHoje)) {
                cInicio.setTime(falta.getInicio());
                cFim.setTime(dataHoje);
            } else if (dataInicio != null && falta.getInicio().before(dataInicio) && falta.getFim().after(dataInicio)) {
                cInicio.setTime(dataInicio);
                cFim.setTime(falta.getFim());
            } else {
                cInicio.setTime(falta.getInicio());
                cFim.setTime(falta.getFim());
            }
            totalFaltas += (cFim.get(Calendar.DAY_OF_MONTH) - cInicio.get(Calendar.DAY_OF_MONTH));
        }
        return totalFaltas.doubleValue();
    }

    @DescricaoMetodo("recuperarUltimaDataFalta(ep, codigo)")
    public Date recuperarUltimaDataFalta(EntidadePagavelRH ep, String codigo) {
        Query q = em.createQuery(" select max(folha.calculadaEm) from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha inner join ficha.itemFichaFinanceiraFP item "
            + " where item.eventoFP.codigo = :codigo and ficha.vinculoFP = :ep");
        q.setParameter("codigo", codigo);
        q.setParameter("ep", ep);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (Date) resultList.get(0);
    }

    @Deprecated
    @DescricaoMetodo("recuperarPercentualOpcaoSalarial(codigo)")
    public Double recuperarPercentualOpcaoSalarial(String codigo) {
        Query q = em.createQuery("from OpcaoSalarialCC opcSal where opcSal.codigo = :codigo");
        q.setParameter("codigo", codigo);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return 0.0;
        }
        return ((OpcaoSalarialCC) resultList.get(0)).getPercentual();
    }

    public Double recuperarPercentualOpcaoSalarial(EntidadePagavelRH ep) {
        Double valor = 0.0;
        Query query = em.createQuery("select cc from CargoConfianca cc where cc.contratoFP.id = :id " +
            " and :data between cc.inicioVigencia and coalesce(cc.finalVigencia,:data) ");
        query.setParameter("id", ep.getIdCalculo());
        query.setParameter("data", localDateToDate(getDataReferencia(ep)));
        if (!query.getResultList().isEmpty()) {
            CargoConfianca cc = (CargoConfianca) query.getResultList().get(0);
            if (cc.getOpcaoSalarialCC() != null && cc.getOpcaoSalarialCC().getPercentual() != null) {
                return cc.getOpcaoSalarialCC().getPercentual() != null && cc.getOpcaoSalarialCC().getPercentual() != 0 ? (cc.getOpcaoSalarialCC().getPercentual()) : 100;
            }
            valor = cc.getPercentual() != null ? cc.getPercentual() : 100;
            return valor == 0 ? 100 : valor;
        }

        Query q = em.createQuery("select coalesce(opcSal.percentual,0) from OpcaoSalarialVinculoFP opcSal where opcSal.vinculoFP.id = :id" +
            " and :data between opcSal.inicioVigencia and coalesce(opcSal.finalVigencia,:data) ");
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            valor = (Double) resultList.get(0);
        }
        return valor;
    }

    public String recuperarOpcaoSalarialCodigo(EntidadePagavelRH ep) {
        ModalidadeContratoFP modalidade = obterModalidadeContratoFP(ep);
        if ((modalidade != null && modalidade.getCodigo() != null) && modalidade.getCodigo() == 2) {
            return !findCodigoOpcaoSalarialNomeacaoCC(ep)
                .equals("0") ? findCodigoOpcaoSalarialNomeacaoCC(ep) : (!findCodigoOpcaoSalarialAcesso(ep)
                .equals("0") ? findCodigoOpcaoSalarialAcesso(ep) : "0");
        }
        return findCodigoOpcaoSalarialAcesso(ep);
    }

    public String findCodigoOpcaoSalarialNomeacaoCC(EntidadePagavelRH ep) {
        Query query = em.createQuery("select opcSal.codigo from OpcaoSalarialVinculoFP opv inner join opv.opcaoSalarialCC opcSal where " +
            " :dataReferencia between opv.inicioVigencia and coalesce(opv.finalVigencia,:dataReferencia) and opv.vinculoFP.id = :id ");
        query.setParameter("id", ep.getIdCalculo());
        query.setParameter("dataReferencia", localDateToDate(getDataReferencia(ep)));
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return "0";
        }
        return (String) resultList.get(0);
    }

    public String findCodigoOpcaoSalarialAcesso(EntidadePagavelRH ep) {
        Query q = em.createQuery("select opcSal.codigo from CargoConfianca cc inner join cc.opcaoSalarialCC opcSal inner join cc.enquadramentoCCs enq where " +
            "  to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between " +
            " to_date(to_char(cc.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(coalesce(cc.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') " +
            " and cc.inicioVigencia = (select max(cargoConfianca.inicioVigencia) from CargoConfianca cargoConfianca where cargoConfianca.contratoFP = cc.contratoFP " +
            " and  " +
            "  to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between " +
            " to_date(to_char(cargoConfianca.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(coalesce(cargoConfianca.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy')) " +
            " and cc.contratoFP.id = :id and" +
            "  to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between " +
            " to_date(to_char(enq.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(coalesce(enq.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("dataReferencia", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return "0";
        }
        return (String) resultList.get(0);
    }

    @DescricaoMetodo("tempoServicoSextaParte(ep,codigoEvento)")
    public Double tempoServicoSextaParte(EntidadePagavelRH ep, String codigoEvento) {
        VinculoFP vinculo = ((VinculoFP) ep);
        Date dataHoje = localDateToDate(getDataReferencia(ep));
        Date dataUltimoCalculo = recuperarUltimaDataEventoCalculado(ep, codigoEvento);
        if (dataUltimoCalculo != null) {
            return 25.0;
        }
        TempoServico tempo;
        String hql;

        hql = " from AverbacaoTempoServico tempo "
            + " where tempo.contratoFP.id = :ep ";

        if (dataUltimoCalculo != null) {
            hql += " and tempo.dataCadastro >= :dataUltimoCalculo ";
        }

        Query q = em.createQuery(hql);
        q.setParameter("ep", vinculo.getId());

        if (dataUltimoCalculo != null) {
            q.setParameter("dataUltimoCalculo", dataUltimoCalculo);
        }

        List resultList = q.getResultList();

        if (resultList.isEmpty()) {
            tempo = new TempoServico(0, 0, 0);
        } else {
            //tempo de serviço averbado
            List<AverbacaoTempoServico> averbacaoTempoServico = resultList;
            Integer ano = 0;
            Integer mes = 0;
            Integer dias = 0;
            for (AverbacaoTempoServico averb : averbacaoTempoServico) {
                ano += averb.getAnos() == null ? 0 : averb.getAnos();
                mes += averb.getMeses() == null ? 0 : averb.getMeses();
                dias += averb.getDias() == null ? 0 : averb.getDias();
            }
            tempo = new TempoServico(ano, mes, dias);
        }

        //tempo de serviço do contrato
        Date finalVigencia;
        if (vinculo.getFinalVigencia() != null) {
            finalVigencia = vinculo.getFinalVigencia();
        } else {
            finalVigencia = localDateToDate(getDataReferencia(ep));
        }

        //caso a data do ultimo cálculo da sexta parte, já tenha sido feita
        //e o contrato continua em aberto, a vigencia comeca a contar a partir
        //da data do ultimo cálculo
        ObjetoData tempoContrato;
        if (dataUltimoCalculo == null) {
            tempoContrato = DataUtil.getAnosMesesDias(vinculo.getInicioVigencia(), finalVigencia);
        } else {
            tempoContrato = DataUtil.getAnosMesesDias(dataUltimoCalculo, finalVigencia);
        }

        tempo.setAno(tempo.getAno() + tempoContrato.getAnos());
        tempo.setMes(tempo.getMes() + tempoContrato.getMeses());
        tempo.setDias(tempo.getDias() + tempoContrato.getDias());

        //Faltas
        hql = " from Faltas f "
            + " where f.contratoFP.id = :contrato "
            + " and f.tipoFalta = :tipo";

        if (dataUltimoCalculo != null) {
            hql += " and f.dataCadastro >= :dataUltimoCalculo ";
        }

        q = em.createQuery(hql);
        q.setParameter("contrato", vinculo.getId());
        q.setParameter("tipo", TipoFalta.FALTA_INJUSTIFICADA);
        if (dataUltimoCalculo != null) {
            q.setParameter("dataUltimoCalculo", dataUltimoCalculo);
        }

        for (Faltas falta : (List<Faltas>) q.getResultList()) {
            Calendar cInicio = Calendar.getInstance();
            Calendar cFim = Calendar.getInstance();

            if (falta.getInicio().before(dataHoje) && falta.getFim().after(dataHoje)) {
                cInicio.setTime(falta.getInicio());
                cFim.setTime(dataHoje);
            } else {
                cInicio.setTime(falta.getInicio());
                cFim.setTime(falta.getFim());
            }
            tempoContrato = DataUtil.getAnosMesesDias(cInicio.getTime(), cFim.getTime());
            tempo.setAno(tempo.getAno() - tempoContrato.getAnos());
            tempo.setMes(tempo.getMes() - tempoContrato.getMeses());
            tempo.setDias(tempo.getDias() - tempoContrato.getDias());
        }

        //Afastamentos
        hql = "select a from Afastamento a "
            + " inner join a.tipoAfastamento tipo "
            + " where a.contratoFP.id = :vinculo and "
            + " tipo.classeAfastamento = :classe ";

        if (dataUltimoCalculo != null) {
            hql += " and a.dataCadastro >= :dataUltimoCalculo ";
        }

        q = em.createQuery(hql);
        q.setParameter("vinculo", vinculo.getId());
        q.setParameter("classe", ClasseAfastamento.FALTA_INJUSTIFICADA);
        if (dataUltimoCalculo != null) {
            q.setParameter("dataUltimoCalculo", dataUltimoCalculo);
        }
        for (Afastamento a : (List<Afastamento>) q.getResultList()) {
            tempoContrato = DataUtil.getAnosMesesDias(a.getInicio(), a.getTermino());

            tempo.setAno(tempo.getAno() - tempoContrato.getAnos());
            tempo.setMes(tempo.getMes() - tempoContrato.getMeses());
            tempo.setDias(tempo.getDias() - tempoContrato.getDias());
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, tempo.getAno());
        c.set(Calendar.MONTH, tempo.getMes());
        c.set(Calendar.DAY_OF_YEAR, tempo.getDias());

        return new Double(c.get(Calendar.YEAR));
    }

    public void salvarDetalhes(DetalhesCalculoRH detalhesCalculoRHAtual) {
        try {
            getEntityManager().merge(detalhesCalculoRHAtual);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    @DescricaoMetodo("recuperarUltimaDataEventoCalculado(ep, codigo)")
    public Date recuperarUltimaDataEventoCalculado(EntidadePagavelRH ep, String codigo) {
        Query q = em.createQuery(" select max(folha.calculadaEm) from FolhaDePagamento folha "
            + " inner join folha.fichaFinanceiraFPs ficha "
            + " inner join ficha.itemFichaFinanceiraFP item "
            + " where item.eventoFP.codigo = :codigo and ficha.vinculoFP = :ep");
        q.setParameter("codigo", codigo);
        q.setParameter("ep", ep);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (Date) resultList.get(0);
    }

    public Mes buscarUltimoMesCalculado(EntidadePagavelRH ep, Integer ano, String codigo) {
        Query q = em.createQuery(" select max(folha.mes) from FolhaDePagamento folha "
            + " inner join folha.fichaFinanceiraFPs ficha "
            + " inner join ficha.itemFichaFinanceiraFP item "
            + " where item.eventoFP.codigo = :codigo " +
            "   and ficha.vinculoFP = :ep" +
            "   and folha.ano = :ano ");
        q.setParameter("codigo", codigo);
        q.setParameter("ep", ep);
        q.setParameter("ano", ano);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (Mes) resultList.get(0);
    }


    public Mes buscarUltimoMesCalculadoPorTipoFolha(EntidadePagavelRH ep, Integer ano, TipoFolhaDePagamento tipo) {
        Query q = em.createQuery(" select max(folha.mes) from FolhaDePagamento folha "
            + " inner join folha.fichaFinanceiraFPs ficha "
            + " inner join ficha.itemFichaFinanceiraFP item "
            + " where folha.tipoFolhaDePagamento = :tipo " +
            "   and ficha.vinculoFP = :ep" +
            "   and folha.ano = :ano ");
        q.setParameter("tipo", tipo);
        q.setParameter("ep", ep);
        q.setParameter("ano", ano);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (Mes) resultList.get(0);
    }

    @DescricaoMetodo("recuperarPercentualIRRF(ep)")
    public Double recuperarPercentualIRRF(EntidadePagavelRH ep) {
        Query q = em.createQuery(" select classificacao.baseIrrf from PrestadorServicos prestador "
            + " inner join prestador.classificacaoCredor classificacao "
            + " where prestador = :ep ");
        q.setParameter("ep", ep);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return new Double(0);
        } else {
            return ((BigDecimal) resultList.get(0)).doubleValue();
        }
    }

    @DescricaoMetodo("quantidadeMesesTrabalhadosAno(ep, mes, ano)")
    public Double quantidadeMesesTrabalhadosAno(EntidadePagavelRH ep, boolean isDecimoProporcional, boolean considerarFinalAno) {
        Integer retorno = 0;


        VinculoFP vinculo = (VinculoFP) ep;
        LocalDate ini = dateToLocalDate(vinculo.getInicioVigencia());
        LocalDate dataFolha = null;

        dataFolha = dateToLocalDate(DataUtil.getPrimeiroDiaAno(ep.getAno()));


        ini = dateToLocalDate(vinculo.getInicioVigencia());
        if (dataFolha != null && dataFolha.isAfter(ini)) {
            ini = dataFolha;
        }
        LocalDate fim = vinculo.getFinalVigencia() != null ? dateToLocalDate(vinculo.getFinalVigencia()) : getDataReferencia(ep);
        if (vinculo.getFinalVigencia() == null) {
            fim = fim.withDayOfMonth(fim.lengthOfMonth());
        }
        if (fim.isAfter(dateToLocalDate(DataUtil.getUltimoDiaAno(ep.getAno())))) {
            LocalDate dataReferenciaDateTime = getDataReferenciaDateTime(ep);
            fim = considerarFinalAno ? dateToLocalDate(DataUtil.getUltimoDiaAno(ep.getAno())) : fim.isAfter(dataReferenciaDateTime) ? dataReferenciaDateTime.withDayOfMonth(dataReferenciaDateTime.lengthOfMonth()) : fim.withDayOfMonth(fim.lengthOfMonth());
        }
        List<LocalDate> datas = getDatasEmLista(ini, fim);
        List<Afastamento> afastamentos = afastamentoFacade.buscarAfastamentosDescontarDiasTrabalhados(vinculo, ini, fim);

        for (LocalDate refe : datas) {
            int diasAfastados = 0;
            int totalDias = 0;
            for (Afastamento afastamento : afastamentos) {
                diasAfastados += getDiasAfastados(refe, afastamento, fim);
            }
            AfastavelDTO faltas = buscarFaltas(vinculo, refe);
            diasAfastados += faltas.getDias();
            AfastavelDTO cedencias = buscarCedencias(vinculo, refe);
            diasAfastados += cedencias.getDias();

            if (diasAfastados > refe.lengthOfMonth()) {
                diasAfastados = refe.lengthOfMonth();
            }
            totalDias = diasEntreDatas(refe, ini, fim);

            totalDias = totalDias - diasAfastados;
            if (totalDias >= 15) {
                if (podeContarAvo(datas, ini, ep, isDecimoProporcional)) {
                    retorno++;
                }
            }
        }
        if (retorno > 12) {
            retorno = 12;
        }

        return retorno.doubleValue();
    }

    private AfastavelDTO buscarCedencias(VinculoFP vinculo, LocalDate refe) {
        Date inicio = Util.criaDataPrimeiroDiaDoMesFP(refe.getMonthValue() - 1, refe.getYear());
        Date fim = Util.recuperaDataUltimoDiaDoMes(inicio);
        List<CedenciaContratoFP> cedencias = cedenciaContratoFPFacade.buscarCedenciaPorPeriodoAndTipo(vinculo.getId(), inicio, fim);
        AfastavelDTO dto = new AfastavelDTO();
        dto.setTipo(AfastavelDTO.Tipo.DIAS);
        for (CedenciaContratoFP cedencia : cedencias) {
            int qtdDiasFaltas = DataUtil.diasEntreDate(cedencia.getDataCessao(), cedencia.getDataRetorno());
            dto.setDias(dto.getDias() + qtdDiasFaltas);
        }
        return dto;
    }

    private AfastavelDTO buscarFaltas(VinculoFP vinculo, LocalDate refe) {
        Date inicio = Util.criaDataPrimeiroDiaDoMesFP(refe.getMonthValue() - 1, refe.getYear());
        Date fim = Util.recuperaDataUltimoDiaDoMes(inicio);
        List<Faltas> faltas = faltasFacade.buscarFaltasPorPeriodo(vinculo.getId(), inicio, fim);
        AfastavelDTO dto = new AfastavelDTO();
        dto.setTipo(AfastavelDTO.Tipo.DIAS);
        for (Faltas falta : faltas) {
            JustificativaFaltas justificativaFaltas = faltasFacade.buscarJustificativaPorFalta(falta.getId());
            int qtdDiasJustificativas = 0;
            if (justificativaFaltas != null) {
                qtdDiasJustificativas = DataUtil.diasEntreDate(justificativaFaltas.getInicioVigencia(), justificativaFaltas.getFinalVigencia());
            }
            int qtdDiasFaltas = DataUtil.diasEntreDate(falta.getInicio(), falta.getFim()) - qtdDiasJustificativas;
            dto.setDias(dto.getDias() + qtdDiasFaltas);
        }
        return dto;
    }


    private boolean podeContarAvo(List<LocalDate> datas, LocalDate ini, EntidadePagavelRH ep, boolean isDecimoProporcional) {
        if (!isDecimoProporcional) {
            return true;
        }
        if (TipoFolhaDePagamento.isFolhaRescisao(ep.getFolha().getTipoFolhaDePagamento())) {
            return true;
        }
        return datas.size() > 1 && ini.getMonthValue() != MESES_ANO && ini.getMonthValue() != ep.getMes() && ini.getYear() != ep.getAno();
    }


    @DescricaoMetodo("quantidadeMesesTrabalhadosPorAno(ep, mes, ano)")
    public Double quantidadeMesesTrabalhadosPorAno(EntidadePagavelRH ep) {
        Integer retorno = 0;


        LocalDate ini = null;
        LocalDate fim = null;

        VinculoFP vinculo = (VinculoFP) ep;
        LocalDate dataReferencia = getDataReferenciaDateTime(ep);

        ini = dateToLocalDate(vinculo.getInicioVigencia());
        if (ini.getYear() < dataReferencia.getYear()) {
            ini = dataReferencia.withMonth(1).withDayOfMonth(1);
        }
        if (vinculo.getFinalVigencia() == null || dateToLocalDate(vinculo.getFinalVigencia()).isAfter(dataReferencia.withMonth(12).withDayOfMonth(31))) {
            fim = dataReferencia.withMonth(12).withDayOfMonth(31);
        } else {
            fim = vinculo.getFinalVigencia() != null ? dateToLocalDate(vinculo.getFinalVigencia()) : LocalDate.now();
        }

        List<LocalDate> datas = getDatasEmLista(ini, fim);
        List<Afastamento> afastamentos = afastamentoFacade.buscarAfastamentosDescontarDiasTrabalhados(vinculo, ini, fim);
        for (LocalDate refe : datas) {
            int diasAfastados = 0;
            int totalDias = 0;
            for (Afastamento afastamento : afastamentos) {
                diasAfastados += getDiasAfastados(refe, afastamento, fim);
            }
            if (diasAfastados > refe.lengthOfMonth()) {
                diasAfastados = refe.lengthOfMonth();
            }
            totalDias = diasEntreDatas(refe, ini, fim);


            totalDias = totalDias - diasAfastados;
            if (totalDias >= 15 && diasAfastados < 15) {
                retorno++;
            }

        }
        if (retorno > dataReferencia.lengthOfMonth()) {
            retorno = dataReferencia.lengthOfMonth();
        }
        return retorno.doubleValue();
    }

    @DescricaoMetodo("quantidadeMesesAfastadosPorAno(ep, '3','5'...)")
    public Double quantidadeMesesAfastadosNoAno(EntidadePagavelRH ep, String codigos) {
        List<Integer> afastamentosCodigos = Lists.newArrayList();
        String listaCodigos[] = codigos.split(",");
        Arrays.stream(listaCodigos).forEach(a -> afastamentosCodigos.add(Integer.valueOf(!"".equals(a) ? a : "-1")));
        Integer retorno = 0;

        VinculoFP vinculo = (VinculoFP) ep;
        LocalDate dataReferencia = getDataReferenciaDateTime(ep);
        LocalDate ini = dateToLocalDate(vinculo.getInicioVigencia());
        LocalDate fim = null;

        if (ini.getYear() < dataReferencia.getYear()) {
            ini = dataReferencia.withMonth(1).withDayOfMonth(1);
        }
        LocalDate ultimoDiaAno = dataReferencia;
        ultimoDiaAno = ultimoDiaAno.withMonth(12);
        ultimoDiaAno = ultimoDiaAno.withDayOfMonth(dataReferencia.lengthOfMonth());
        if (vinculo.getFinalVigencia() == null || vinculo.getFinalVigencia().after(DataUtil.localDateToDate(ultimoDiaAno))) {
            fim = ultimoDiaAno;
        } else {
            fim = vinculo.getFinalVigencia() != null ? dateToLocalDate(vinculo.getFinalVigencia()) : LocalDate.now();
        }

        List<Afastamento> afastamentos = afastamentoFacade.buscarAfastamentosDescontarDiasTrabalhadosPorCodigo(vinculo, DataUtil.localDateToDate(ini), DataUtil.localDateToDate(fim), afastamentosCodigos);
        int diasAfastados = 0;
        for (Afastamento afastamento : afastamentos) {

            if (dateToLocalDate(afastamento.getInicio()).isAfter(ini)) {
                ini = dateToLocalDate(afastamento.getInicio());
            }

            List<LocalDate> datas = getDatasEmLista(ini, fim);
            for (LocalDate refe : datas) {
                diasAfastados = getDiasAfastados(refe, afastamento, fim);
                if (diasAfastados >= 15) {
                    retorno++;
                }
            }
        }

        if (retorno > 12) {
            retorno = 12;
        }
        return retorno.doubleValue();
    }

    public List<Mes> quantidadeMesesTrabalhadosPorAnoDescontandoFaltaAndAfastamento(EntidadePagavelRH ep) {
        List<Mes> mesesAfastados = Lists.newArrayList();


        LocalDate ini = null;
        LocalDate fim = null;

        VinculoFP vinculo = (VinculoFP) ep;
        LocalDate dataReferencia = getDataReferenciaDateTime(ep);

        ini = dateToLocalDate(vinculo.getInicioVigencia());
        if (ini.getYear() < dataReferencia.getYear()) {
            ini = dataReferencia.withMonth(1).withDayOfMonth(1);
        }
        if (vinculo.getFinalVigencia() == null || dateToLocalDate(vinculo.getFinalVigencia()).isAfter(dataReferencia.withMonth(12).withDayOfMonth(31))) {
            fim = dataReferencia.withMonth(12).withDayOfMonth(31);
        } else {
            fim = vinculo.getFinalVigencia() != null ? dateToLocalDate(vinculo.getFinalVigencia()) : LocalDate.now();
        }

        List<LocalDate> datas = getDatasEmLista(ini, fim);
        List<Afastamento> afastamentos = afastamentoFacade.buscarAfastamentosDescontarDiasTrabalhados(vinculo, ini, fim);
        List<VwFalta> faltas = Lists.newArrayList();
        if (vinculo instanceof ContratoFP) {
            faltas = faltasFacade.buscarFaltasPorContratoAndPeriodo((ContratoFP) vinculo, ini, fim);
        }
        for (LocalDate refe : datas) {
            int diasAfastados = 0;
            for (Afastamento afastamento : afastamentos) {
                diasAfastados += getDiasAfastados(refe, afastamento, fim);
            }
            for (VwFalta vwFalta : faltas) {
                if (TipoFalta.FALTA_INJUSTIFICADA.equals(vwFalta.getTipoFalta())) {
                    Integer dias = getDiasDeFaltas(refe, vwFalta);
                    diasAfastados += dias;
                }
            }
            if (diasAfastados > refe.lengthOfMonth()) {
                diasAfastados = refe.lengthOfMonth();
            }

            if (diasAfastados > 15) {
                mesesAfastados.add(Mes.getMesToInt(refe.getMonthValue()));
            }

        }
        return mesesAfastados;
    }

    /**
     * Método utilizando para verificar efetivamente os meses trabalhados no ano,
     * diferente dos outros que completem os meses até o final do ano,
     * esse método não considera até o final do ano e sim até o mes atual.
     * Desconta afastamentos e faltas ijustificados
     */
    public Integer getQuantidadeMesesTrabalhadosPorAno(EntidadePagavelRH ep) {
        Integer retorno = 0;


        LocalDate ini = null;
        LocalDate fim = null;

        VinculoFP vinculo = (VinculoFP) ep;
        LocalDate dataReferencia = getDataReferenciaDateTime(ep);

        ini = dateToLocalDate(vinculo.getInicioVigencia());
        if (ini.getYear() < dataReferencia.getYear()) {
            ini = dataReferencia.withMonth(1).withDayOfMonth(1);
        }
        if (vinculo.getFinalVigencia() == null || dateToLocalDate(vinculo.getFinalVigencia()).isAfter(dataReferencia.withMonth(12).withDayOfMonth(31))) {
            fim = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());
        } else {
            fim = vinculo.getFinalVigencia() != null ? dateToLocalDate(vinculo.getFinalVigencia()) : LocalDate.now();
        }

        List<LocalDate> datas = getDatasEmLista(ini, fim);
        List<Afastamento> afastamentos = afastamentoFacade.buscarAfastamentosVigentesPorPeriodoAndDescontarTempoServicoEfetivo(vinculo, ini, fim);
        List<VwFalta> faltas = Lists.newArrayList();
        if (vinculo instanceof ContratoFP) {
            faltas = faltasFacade.buscarFaltasPorContratoAndPeriodo((ContratoFP) vinculo, ini, fim);
        }
        for (LocalDate refe : datas) {
            int diasAfastados = 0;
            Integer totalDias = 0;
            for (Afastamento afastamento : afastamentos) {
                diasAfastados += getDiasAfastados(refe, afastamento, fim);
            }
            for (VwFalta vwFalta : faltas) {
                if (TipoFalta.FALTA_INJUSTIFICADA.equals(vwFalta.getTipoFalta())) {
                    Integer dias = getDiasDeFaltas(refe, vwFalta);
                    diasAfastados += dias;
                }
            }
            if (diasAfastados > refe.lengthOfMonth()) {
                diasAfastados = refe.lengthOfMonth();
            }

            totalDias = diasEntreDatas(refe, ini, fim);

            totalDias = totalDias - diasAfastados;
            if (totalDias >= 15) {
                retorno++;
            }
        }
        return retorno;
    }


    public Integer quantidadeOcorrenciaEventoByAno(EntidadePagavelRH ep, EventoFP eventoFP) {
        Integer retorno = 0;
        List<Mes> mesesTrabalhados = quantidadeOcorrenciaEventoByAnoMes(ep, eventoFP);
        List<Mes> mesesAfastados = quantidadeMesesTrabalhadosPorAnoDescontandoFaltaAndAfastamento(ep);
        retorno = verificarMesesTrabalhadosXMesesAfastados(mesesTrabalhados, mesesAfastados);
        retorno = retorno > MESES_ANO ? MESES_ANO : retorno;
        return retorno;
    }

    private Integer verificarMesesTrabalhadosXMesesAfastados(List<Mes> mesesTrabalhados, List<Mes> mesesAfastados) {
        for (Iterator it = mesesTrabalhados.iterator(); it.hasNext(); ) {
            Mes mesTrabalhado = (Mes) it.next();
            if (mesesAfastados.contains(mesTrabalhado)) {
                it.remove();
            }
        }
        return mesesTrabalhados.size();
    }

    public List<Mes> quantidadeOcorrenciaEventoByAnoMes(EntidadePagavelRH ep, EventoFP eventoFP) {
        List<Mes> resultados = Lists.newArrayList();

        LocalDate dRerefencia = getDataReferencia(ep);
        String hql = "      select distinct itemFicha.mes" +
            "        from FichaFinanceiraFP ficha" +
            "  inner join ficha.vinculoFP v " +
            "  inner join ficha.folhaDePagamento folha" +
            "  inner join ficha.itemFichaFinanceiraFP itemFicha" +
            "  inner join itemFicha.eventoFP evento" +
            "       where folha.ano = :ano" +
            "         and itemFicha.ano = :ano" +
            "         and itemFicha.tipoCalculoFP = :tipoCalculo " +
            "         and v = :ep" +
            "         and folha.tipoFolhaDePagamento = :tipoFolha";
        if (eventoFP != null) {
            hql += "         and evento = :ev ";
        }
        hql += "         and itemFicha.tipoEventoFP = :tipoVantagem";

        Query q = em.createQuery(hql.toString());
        q.setParameter("ep", ep);
        if (eventoFP != null) {
            q.setParameter("ev", eventoFP);
        }
        q.setParameter("ano", dRerefencia.getYear());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL);
        q.setParameter("tipoCalculo", TipoCalculoFP.NORMAL);
        q.setParameter("tipoVantagem", TipoEventoFP.VANTAGEM);

        try {
            List<Integer> meses = q.getResultList();
            for (Integer mes : meses) {
                resultados.add(Mes.getMesToInt(mes));
            }

            VinculoFP v = (VinculoFP) ep;
            LocalDate LocalDate = dateToLocalDate(v.getInicioVigencia());
            if (LocalDate.getYear() == dRerefencia.getYear() && LocalDate.getDayOfMonth() > 15) {
                if (!resultados.isEmpty()) {
                    resultados.remove(0);
                }
            }
            return resultados;
        } catch (NoResultException nre) {
            return resultados;
        }
    }

    @DescricaoMetodo("temCargoConfianca(ep, mes, ano)")
    public boolean temCargoConfianca(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo) {
        StringBuilder queryString = new StringBuilder("");
        queryString.append(" select cargo from CargoConfianca cargo ");
        queryString.append(" where cargo.contratoFP.id = :ep  ");
        queryString.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between ");
        queryString.append(" to_date(to_char(cargo.inicioVigencia,'mm/yyyy'),'mm/yyyy') ");
        queryString.append(" and to_date(to_char(coalesce(cargo.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("temAcessoSubsidio(ep, mes, ano)")
    public boolean temAcessoSubsidio(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo) {
        StringBuilder queryString = new StringBuilder("");
        queryString.append(" select cargo from AcessoSubsidio cargo ");
        queryString.append(" where cargo.contratoFP.id = :ep  ");
        queryString.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between ");
        queryString.append(" to_date(to_char(cargo.inicioVigencia,'mm/yyyy'),'mm/yyyy') ");
        queryString.append(" and to_date(to_char(coalesce(cargo.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");
        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("codigoBaseFPCargoConfianca(ep, mes, ano)")
    public String codigoBaseFPCargoConfianca(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo) {
        StringBuilder queryString = new StringBuilder("");
        queryString.append(" select base.codigo from CargoConfianca cargo ");
        queryString.append(" join cargo.baseFP base ");
        queryString.append(" where cargo.contratoFP.id = :ep  ");
        queryString.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between ");
        queryString.append(" to_date(to_char(cargo.inicioVigencia,'mm/yyyy'),'mm/yyyy') ");
        queryString.append(" and to_date(to_char(coalesce(cargo.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();

        if (!resultList.isEmpty()) {
            return (String) resultList.get(0);
        }
        return null;
    }

    @DescricaoMetodo("codigoBaseFPSubsidio(ep, mes, ano)")
    public String codigoBaseFPSubsidio(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo) {
        StringBuilder queryString = new StringBuilder("");
        queryString.append(" select base.codigo from AcessoSubsidio cargo ");
        queryString.append(" join cargo.baseFP base ");
        queryString.append(" where cargo.contratoFP.id = :ep  ");
        queryString.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between ");
        queryString.append(" to_date(to_char(cargo.inicioVigencia,'mm/yyyy'),'mm/yyyy') ");
        queryString.append(" and to_date(to_char(coalesce(cargo.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();

        if (!resultList.isEmpty()) {
            return (String) resultList.get(0);
        }
        return null;
    }
//    @DescricaoMetodo("recuperaPercentualIRRF(ep)")
//    public Double recuperarPercentualIRRF(EntidadePagavelRH ep){
//        Query q = em.createQuery(" select classificacao.baseIrrf from PrestadorServicos prestador "
//                + " inner join prestador.classificacaoCredor classificacao "
//                + " where prestador = :ep ");
//        q.setParameter("ep", ep);
//        q.setMaxResults(1);
//        if (q.getResultList().isEmpty()){
//            return new Double(0);
//        } else {
//            return (Double) q.getSingleResult();
//        }
//    }

    public List<ValidadorVigenciaFolha> cargosConfianca(EntidadePagavelRH ep) {
        StringBuilder queryString = new StringBuilder("");
        queryString.append(" select cargo from CargoConfianca cargo ");
        queryString.append(" where cargo.contratoFP.id = :ep  ");
        queryString.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between ");
        queryString.append(" to_date(to_char(cargo.inicioVigencia,'mm/yyyy'),'mm/yyyy') ");
        queryString.append(" and to_date(to_char(coalesce(cargo.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') order by cargo.inicioVigencia asc");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return q.getResultList();
    }

    public List<ValidadorVigenciaFolha> acessoSubsidio(EntidadePagavelRH ep) {
        StringBuilder queryString = new StringBuilder("");
        queryString.append(" select cargo from AcessoSubsidio cargo ");
        queryString.append(" where cargo.contratoFP.id = :ep  ");
        queryString.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between ");
        queryString.append(" to_date(to_char(cargo.inicioVigencia,'mm/yyyy'),'mm/yyyy') ");
        queryString.append(" and to_date(to_char(coalesce(cargo.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') order by cargo.inicioVigencia asc");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        return q.getResultList();
    }

    public CargoConfianca obterCargoConfianca(EntidadePagavelRH ep) {
        StringBuilder queryString = new StringBuilder("");
        queryString.append(" select cargo from CargoConfianca cargo ");
        queryString.append(" where cargo.contratoFP.id = :ep  ");
        queryString.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between ");
        queryString.append(" to_date(to_char(cargo.inicioVigencia,'mm/yyyy'),'mm/yyyy') ");
        queryString.append(" and to_date(to_char(coalesce(cargo.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') order by cargo.inicioVigencia asc");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        if (!q.getResultList().isEmpty()) {
            return (CargoConfianca) q.getResultList().get(0);
        }
        return null;
    }

    public CargoConfianca obterCargoConfiancaPorData(EntidadePagavelRH ep, LocalDate referencia) {
        StringBuilder queryString = new StringBuilder("");
        queryString.append(" select cargo from CargoConfianca cargo ");
        queryString.append(" where cargo.contratoFP.id = :ep  ");
        queryString.append(" and :dataVigencia between ");
        queryString.append(" cargo.inicioVigencia ");
        queryString.append(" and coalesce(cargo.finalVigencia,:dataVigencia) order by cargo.inicioVigencia asc");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(referencia));
        if (!q.getResultList().isEmpty()) {
            return (CargoConfianca) q.getResultList().get(0);
        }
        return null;
    }

    @DescricaoMetodo("diasUteisMes(mes, ano)")
    public Double diasUteisMes(EntidadePagavelRH ep, Integer mesDeCalculo, Integer anoDeCalculo) {
        Calendar c = Calendar.getInstance();
        LocalDate dateTime = getDataReferencia(ep);
        dateTime = dateTime.withDayOfMonth(1);
        c.setTime(localDateToDate(dateTime));

        int totalDias = 0;
        List<Date> listaDiasNaoUteis = new ArrayList<>();

        for (int i = 0; i < c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            c.set(Calendar.DAY_OF_MONTH, i + 1);
            if (!((c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) /* ||  (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)*/)) {
                totalDias++;
            } else {
                listaDiasNaoUteis.add(c.getTime());
            }
        }

        Query q = em.createQuery(" from Feriado f where extract(year from f.dataFeriado) = :ano"
            + " and extract(month from f.dataFeriado) = :mes "
            + " and f.dataFeriado not in (:listaDiasNaoUteis)");
        q.setParameter("mes", dateTime.getMonthValue());
        q.setParameter("ano", dateTime.getYear());
        q.setParameter("listaDiasNaoUteis", listaDiasNaoUteis);

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            totalDias -= resultList.size();
        }

        return Double.parseDouble(totalDias + "");
    }

    @DescricaoMetodo("diasDomingosFeriados()")
    public Double diasDomingosFeriados(EntidadePagavelRH ep) {
        Calendar c = Calendar.getInstance();
        LocalDate dateTime = getDataReferencia(ep);
        dateTime = dateTime.withDayOfMonth(1);
        c.setTime(localDateToDate(dateTime));

        int totalDias = 0;
        List<Date> listaDiasNaoUteis = new ArrayList<>();

        for (int i = 0; i < c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            c.set(Calendar.DAY_OF_MONTH, i + 1);
            if (((c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) /* ||  (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)*/)) {
                totalDias++;
            } else {
                listaDiasNaoUteis.add(c.getTime());
            }
        }

        Query q = em.createQuery(" from Feriado f where extract(year from f.dataFeriado) = :ano"
            + " and extract(month from f.dataFeriado) = :mes "
            + " and f.dataFeriado in (:listaDiasNaoUteis)");
        q.setParameter("mes", dateTime.getMonthValue());
        q.setParameter("ano", dateTime.getYear());
        q.setParameter("listaDiasNaoUteis", listaDiasNaoUteis);

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            totalDias += resultList.size();
        }

        return Double.parseDouble(totalDias + "");
    }

    @DescricaoMetodo("valorCargoComissaoPCS(ep, mes, ano)")
    public Double valorCargoComissaoPCS(EntidadePagavelRH ep, Integer mes, Integer ano) {
        if (ep instanceof Pensionista) {
            return valorCargoComissaoInstituidor(ep);
        }
        return valorCargoComissao(ep);
    }

    @DescricaoMetodo("valorSubsidioPCS(ep, mes, ano)")
    public Double valorSubsidioPCS(EntidadePagavelRH ep, Integer mes, Integer ano) {
        if (ep instanceof Pensionista) {
            return valorAcessoSubsidioInstituidor(ep);
        }
        return valorAcessoSubsidio(ep);
    }

    private Double valorCargoComissao(EntidadePagavelRH ep) {
        StringBuilder sb = new StringBuilder();
        sb.append("select cargo.inicioVigencia, cargo.finalVigencia, pcs.vencimentoBase ");
        sb.append("  from CARGOCONFIANCA cargo");
        sb.append(" inner join ENQUADRAMENTOCC enquadramento on (enquadramento.CARGOCONFIANCA_ID = cargo.ID)");
        sb.append(" inner join VinculoFP vinculo on (vinculo.id = cargo.contratoFP_id)");
        sb.append(" inner join CATEGORIAPCS cat on (cat.ID = enquadramento.CATEGORIAPCS_ID)");
        sb.append(" inner join PROGRESSAOPCS prog on (prog.id  = enquadramento.PROGRESSAOPCS_ID)");
        sb.append(" inner join ENQUADRAMENTOPCS pcs on (pcs.CATEGORIAPCS_ID = cat.ID and pcs.PROGRESSAOPCS_ID = prog.ID)");
        sb.append(" where vinculo.id = :ep");
        sb.append("   and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cargo.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cargo.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append("   and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(enquadramento.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(enquadramento.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");

        sb.append("   and :dataVigencia >= pcs.inicioVigencia ");
        sb.append("   and :dataVigencia <= coalesce(pcs.finalVigencia,:dataVigencia) " +
            "and :dataVigencia between pcs.inicioVigencia and coalesce(pcs.finalVigencia,:dataVigencia)");

        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        List<ValidadorVigenciaFolha> resultList = new LinkedList<>();

        List<Object[]> retorno = q.getResultList();
        for (Object[] o : retorno) {
            EnquadramentoCC cc = new EnquadramentoCC();
            cc.setInicioVigencia((Date) o[0]);
            cc.setFinalVigencia((Date) o[1]);
            cc.setVencimentoBase((BigDecimal) o[2]);
            resultList.add(cc);
        }

        double salarioBase = 0;
        if (resultList.isEmpty() || (resultList.get(0) == null)) {
            return 0.0;
        }
        if (isSomenteUmEnquadramento(false, resultList)) {
            return resultList.get(0).getValor().doubleValue();
        } else {
            salarioBase = realizarMediaSalarial(ep, getDataReferencia(ep), resultList);
        }
        logger.debug("valor cargo comissao: " + salarioBase);
        return salarioBase;
    }

    private Double valorAcessoSubsidio(EntidadePagavelRH ep) {
        StringBuilder sb = new StringBuilder();
        sb.append("select coalesce(pcs.VENCIMENTOBASE,0)");
        sb.append("  from ACESSOSUBSIDIO cargo");
        sb.append(" inner join ENQUADRAMENTOCC enquadramento on (enquadramento.ACESSOSUBSIDIO_ID = cargo.ID)");
        sb.append(" inner join VinculoFP vinculo on (vinculo.id = cargo.contratoFP_id)");
        sb.append(" inner join CATEGORIAPCS cat on (cat.ID = enquadramento.CATEGORIAPCS_ID)");
        sb.append(" inner join PROGRESSAOPCS prog on (prog.id  = enquadramento.PROGRESSAOPCS_ID)");
        sb.append(" inner join ENQUADRAMENTOPCS pcs on (pcs.CATEGORIAPCS_ID = cat.ID and pcs.PROGRESSAOPCS_ID = prog.ID)");
        sb.append(" where vinculo.id = :ep");
        sb.append("   and cargo.inicioVigencia = (select max(c.inicioVigencia) from ACESSOSUBSIDIO c where c.contratoFP_id = cargo.contratoFP_id and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(c.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(c.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ) ");
        sb.append("   and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cargo.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cargo.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");

        sb.append("   and enquadramento.inicioVigencia = (select max(c.inicioVigencia) from EnquadramentoCC c where c.ACESSOSUBSIDIO_ID = cargo.id and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(c.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(c.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ) ");
        sb.append("   and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(enquadramento.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(enquadramento.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') ");

        sb.append("   and :dataVigencia >= pcs.inicioVigencia ");
        sb.append("   and :dataVigencia <= coalesce(pcs.finalVigencia,:dataVigencia) " +
            "and :dataVigencia between pcs.inicioVigencia and coalesce(pcs.finalVigencia,:dataVigencia)");

        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return 0.0;
        }
        BigDecimal d = (BigDecimal) resultList.get(0);
        logger.debug("valor cargo comissao: " + d.doubleValue());
        return d.doubleValue();
    }

    private Double valorCargoComissaoInstituidor(EntidadePagavelRH ep) {
        StringBuilder sb = new StringBuilder();
        sb.append("select coalesce(pcs.VENCIMENTOBASE,0)");
        sb.append("  from CARGOCONFIANCA cargo");
        sb.append(" inner join ENQUADRAMENTOCC enquadramento on (enquadramento.CARGOCONFIANCA_ID = cargo.ID)");
        sb.append(" inner join VinculoFP vinculo on (vinculo.id = cargo.contratoFP_id)");
        sb.append(" inner join CATEGORIAPCS cat on (cat.ID = enquadramento.CATEGORIAPCS_ID)");
        sb.append(" inner join PROGRESSAOPCS prog on (prog.id  = enquadramento.PROGRESSAOPCS_ID)");
        sb.append(" inner join ENQUADRAMENTOPCS pcs on (pcs.CATEGORIAPCS_ID = cat.ID and pcs.PROGRESSAOPCS_ID = prog.ID)");
        sb.append(" where vinculo.id = :ep ");
        sb.append("  and cargo.iniciovigencia = (select max(c.iniciovigencia) from CargoConfianca c where c.contratofp_id = vinculo.id) ");
        sb.append("  and enquadramento.iniciovigencia = (select max(enq.iniciovigencia) from enquadramentocc enq where enq.cargoConfianca_id = cargo.id ) ");
        sb.append("   ");
        sb.append("   ");
        sb.append(" and pcs.inicioVigencia = (select max(e.inicioVigencia) from EnquadramentoPCS e where e.categoriapcs_id = cat.id and e.progressaopcs_id = prog.id) order by cargo.finalVigencia desc ");

        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("ep", ((Pensionista) ep).getPensao().getContratoFP().getId());
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return 0.0;
        }
        BigDecimal d = (BigDecimal) resultList.get(0);
        return d.doubleValue();
    }

    private Double valorAcessoSubsidioInstituidor(EntidadePagavelRH ep) {
        StringBuilder sb = new StringBuilder();
        sb.append("select coalesce(pcs.VENCIMENTOBASE,0)");
        sb.append("  from ACESSOSUBSIDIO cargo");
        sb.append(" inner join ENQUADRAMENTOCC enquadramento on (enquadramento.ACESSOSUBSIDIO_ID = cargo.ID)");
        sb.append(" inner join VinculoFP vinculo on (vinculo.id = cargo.contratoFP_id)");
        sb.append(" inner join CATEGORIAPCS cat on (cat.ID = enquadramento.CATEGORIAPCS_ID)");
        sb.append(" inner join PROGRESSAOPCS prog on (prog.id  = enquadramento.PROGRESSAOPCS_ID)");
        sb.append(" inner join ENQUADRAMENTOPCS pcs on (pcs.CATEGORIAPCS_ID = cat.ID and pcs.PROGRESSAOPCS_ID = prog.ID)");
        sb.append(" where vinculo.id = :ep ");
        sb.append("  and cargo.iniciovigencia = (select max(c.iniciovigencia) from ACESSOSUBSIDIO c where c.contratofp_id = vinculo.id) ");
        sb.append("  and enquadramento.iniciovigencia = (select max(enq.iniciovigencia) from enquadramentocc enq where enq.ACESSOSUBSIDIO_ID = cargo.id ) ");
        sb.append("   ");
        sb.append("   ");
        sb.append(" and pcs.inicioVigencia = (select max(e.inicioVigencia) from EnquadramentoPCS e where e.categoriapcs_id = cat.id and e.progressaopcs_id = prog.id) order by cargo.finalVigencia desc ");

        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("ep", ((Pensionista) ep).getPensao().getContratoFP().getId());
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return 0.0;
        }
        BigDecimal d = (BigDecimal) resultList.get(0);
        return d.doubleValue();
    }

    @DescricaoMetodo("optanteFGTS(ep)")
    public boolean optanteFGTS(EntidadePagavelRH ep) {
        Query q = em.createQuery(" from ContratoFP contrato where contrato.id = :parametro ");
        q.setMaxResults(1);
        q.setParameter("parametro", ep.getIdCalculo());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            ContratoFP c = ((ContratoFP) resultList.get(0));
            if (c.getFgts() == null) {

                return false;
            }
            return c.getFgts();
        }

        return false;
    }

    public EventoFP recuperaEvento(String codigo, TipoExecucaoEP tipo) {
        Query q = em.createQuery("select e from EventoFP e where e.codigo = :codigo and e.tipoExecucaoEP = :tipo");
        q.setParameter("codigo", codigo);
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        EventoFP e = (EventoFP) resultList.get(0);
        Hibernate.initialize(e.getTiposFolha());
        return e;
    }

    @DescricaoMetodo("valorTotalEventoOutrosVinculos(codigo, ep, ano, mes)")
    public Double valorTotalEventoOutrosVinculos(String codigo, EntidadePagavelRH ep, Integer ano, Integer mes, Boolean validarPrimeiroContrato, TipoFolhaDePagamento tipoFolhaEmCalculo, String... tiposFolha) {
        if (validarPrimeiroContrato && ep.getPrimeiroContrato()) {
            return 0.0;
        }
        StringBuilder sql = new StringBuilder();

        sql.append("select sum(coalesce(itemFicha.valor, 0))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join EventoFP evento on (itemFicha.eventoFP_ID = evento.ID)");
        sql.append("  join fichaFinanceiraFP ficha on (itemFicha.fichaFinanceiraFP_ID = ficha.ID)");
        sql.append("  join FolhaDePagamento folha on (ficha.folhaDePagamento_ID = folha.ID)");
        sql.append("  join VinculoFP vinculo on (ficha.vinculoFP_ID = vinculo.ID)");
        sql.append("  join MatriculaFP mat on (mat.ID = vinculo.matriculafp_id)");
        sql.append(" where folha.ano = :ano");
        sql.append("   and folha.mes = :mes");
        sql.append("   and evento.codigo = :evento");
        sql.append("   and mat.pessoa_ID = :pessoaID");
        sql.append("   and vinculo.ID <> :vinculo");
        if (tiposFolha != null) {
            sql.append("   and folha.tipofolhadepagamento in (:tiposFolha) ");
        }

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).ordinal());
        q.setParameter("evento", codigo);
        q.setParameter("pessoaID", ep.getMatriculaFP().getPessoa().getId());
        q.setParameter("vinculo", ep.getIdCalculo());
        if (tiposFolha != null) {
            q.setParameter("tiposFolha", Arrays.asList(tiposFolha));
        }
        Object result = q.getSingleResult();

        BigDecimal d = (BigDecimal) result;
        if (result == null) {
            if (!validarPrimeiroContrato || TipoFolhaDePagamento.isFolhaComplementar(ep.getFolha().getTipoFolhaDePagamento())) {
                return valorTotalEventoMesmoVinculo(codigo, ep, ano, mes, tipoFolhaEmCalculo, tiposFolha).doubleValue();
            }
            return 0.0;
        }

        if (!validarPrimeiroContrato || TipoFolhaDePagamento.isFolhaComplementar(ep.getFolha().getTipoFolhaDePagamento())) {
            d = d.add(valorTotalEventoMesmoVinculo(codigo, ep, ano, mes, tipoFolhaEmCalculo, tiposFolha));
        }
        return d.doubleValue();
    }

    @DescricaoMetodo("valorTotalEventoMesmoVinculo(codigo, ep, ano, mes)")
    public BigDecimal valorTotalEventoMesmoVinculo(String codigo, EntidadePagavelRH ep, Integer ano, Integer mes, TipoFolhaDePagamento tipoFolhaEmCalculo, String... tiposFolha) {
        StringBuilder sql = new StringBuilder();

        sql.append("select sum(coalesce(itemFicha.valor, 0))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join EventoFP evento on (itemFicha.eventoFP_ID = evento.ID)");
        sql.append("  join fichaFinanceiraFP ficha on (itemFicha.fichaFinanceiraFP_ID = ficha.ID)");
        sql.append("  join FolhaDePagamento folha on (ficha.folhaDePagamento_ID = folha.ID)");
        sql.append("  join VinculoFP vinculo on (ficha.vinculoFP_ID = vinculo.ID)");
        sql.append("  join MatriculaFP mat on (mat.ID = vinculo.matriculafp_id)");
        sql.append(" where folha.ano = :ano");
        sql.append("   and folha.mes = :mes");
        sql.append("   and evento.codigo = :evento");
        sql.append("   and vinculo.ID = :vinculo");
        sql.append("   and folha.tipofolhadepagamento <> :tipoFolhaEmCalculo ");
        if (tiposFolha != null) {
            sql.append("   and folha.tipofolhadepagamento in (:tiposFolha) ");
        }

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).ordinal());
        q.setParameter("evento", codigo);
        q.setParameter("vinculo", ep.getIdCalculo());
        q.setParameter("tipoFolhaEmCalculo", tipoFolhaEmCalculo.name());
        if (tiposFolha != null) {
            q.setParameter("tiposFolha", Arrays.asList(tiposFolha));
        }
        Object result = q.getSingleResult();

        if (result == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal d = (BigDecimal) result;
        return d;
    }

    public TipoAvisoPrevio recuperarTipoAvisoPrevio(EntidadePagavelRH ep) {
        StringBuilder sql = new StringBuilder();

        sql.append("select ex.tipoAvisoPrevio from ExoneracaoRescisao ex ");
        sql.append(" where ex.vinculoFP.id = :vinculo ");


        Query q = em.createQuery(sql.toString());

        q.setParameter("vinculo", ep.getIdCalculo());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        TipoAvisoPrevio result = (TipoAvisoPrevio) q.getSingleResult();
        return result;
    }

    @DescricaoMetodo("valorTotalBaseOutrosVinculos(codigo, ep, ano, mes)")
    public Double valorTotalBaseOutrosVinculos(String codigo, EntidadePagavelRH ep, Integer ano, Integer mes, Boolean validarPrimeiroContrato, TipoFolhaDePagamento tipoFolha) {
        if (validarPrimeiroContrato && ep.getPrimeiroContrato()) {
            return 0.0;
        }
        StringBuilder sql = new StringBuilder();

        sql.append("select sum(case when(itemBase.operacaoFormula = 'ADICAO') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join FichaFinanceiraFP ficha on (itemFicha.fichaFinanceiraFP_ID = ficha.ID)");
        sql.append("  join FolhaDePagamento folha on (ficha.folhaDePagamento_ID = folha.ID)");
        sql.append("  join VinculoFP vinculo on (ficha.vinculoFP_ID = vinculo.ID)");
        sql.append("  join MatriculaFP mat on (mat.ID = vinculo.matriculafp_id)");
        sql.append("  join ItemBaseFP itemBase on (itemBase.eventoFP_ID = itemFicha.eventoFP_ID)");
        sql.append("  join BaseFP base on (itemBase.baseFP_ID = base.ID)");
        sql.append(" where folha.ano = :ano");
        sql.append("   and folha.mes = :mes");
        sql.append("   and base.codigo = :codigo");
        sql.append("   and mat.pessoa_ID = :pessoaID");
        sql.append("   and vinculo.ID <> :vinculo");
        if (TipoFolhaDePagamento.isFolha13Salario(ep.getFolha().getTipoFolhaDePagamento())) {
            sql.append("   and folha.tipoFolhaDePagamento =:tipo");
        }

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).ordinal());
        q.setParameter("codigo", codigo);
        q.setParameter("pessoaID", ep.getMatriculaFP().getPessoa().getId());
        q.setParameter("vinculo", ep.getIdCalculo());
        if (TipoFolhaDePagamento.isFolha13Salario(ep.getFolha().getTipoFolhaDePagamento())) {
            q.setParameter("tipo", ep.getFolha().getTipoFolhaDePagamento().name());
        }
        Object result = q.getSingleResult();
        BigDecimal d = (BigDecimal) result;
        if (result == null) {
            if (!validarPrimeiroContrato || TipoFolhaDePagamento.isFolhaComplementar(ep.getFolha().getTipoFolhaDePagamento())) {
                return valorTotalBaseOutrosVinculosFolhaNormal(codigo, ep, ano, mes, tipoFolha).doubleValue();
            }
            return 0.0;
        }


        if (!validarPrimeiroContrato || TipoFolhaDePagamento.isFolhaComplementar(ep.getFolha().getTipoFolhaDePagamento())) {
            d = d.add(valorTotalBaseOutrosVinculosFolhaNormal(codigo, ep, ano, mes, tipoFolha));
        }
        return d.doubleValue();
    }


    @DescricaoMetodo("valorTotalBaseOutrosVinculos(codigo, ep, ano, mes)")
    public BigDecimal valorTotalBaseOutrosVinculosFolhaNormal(String codigo, EntidadePagavelRH ep, Integer ano, Integer mes, TipoFolhaDePagamento tipoFolha) {
        StringBuilder sql = new StringBuilder();

        sql.append("select sum(case when(itemBase.operacaoFormula = 'ADICAO' and itemFicha.tipoEventoFP = 'VANTAGEM') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join FichaFinanceiraFP ficha on (itemFicha.fichaFinanceiraFP_ID = ficha.ID)");
        sql.append("  join FolhaDePagamento folha on (ficha.folhaDePagamento_ID = folha.ID)");
        sql.append("  join VinculoFP vinculo on (ficha.vinculoFP_ID = vinculo.ID)");
        sql.append("  join MatriculaFP mat on (mat.ID = vinculo.matriculafp_id)");
        sql.append("  join ItemBaseFP itemBase on (itemBase.eventoFP_ID = itemFicha.eventoFP_ID)");
        sql.append("  join BaseFP base on (itemBase.baseFP_ID = base.ID)");
        sql.append(" where folha.ano = :ano");
        sql.append("   and folha.mes = :mes");
        sql.append("   and base.codigo = :codigo");
        sql.append("   and vinculo.ID = :vinculo");
        sql.append("   and folha.tipofolhadepagamento <> :tipoFolha");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).ordinal());
        q.setParameter("codigo", codigo);
        q.setParameter("vinculo", ep.getIdCalculo());
        q.setParameter("tipoFolha", tipoFolha.name());
        Object result = q.getSingleResult();

        if (result == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal d = (BigDecimal) result;
        if (d == null) {
            return BigDecimal.ZERO;
        }
        return d;
    }


    //    public Double valorTotalBaseRetroativos(String codigo, EntidadePagavelRH ep, Integer ano, Integer mes, List<EventoFP> eventosExcluidos) {
    public Double valorTotalBaseRetroativos(String codigo, EntidadePagavelRH ep, Integer ano, Integer mes, List<EventoFP> eventoFiltrados) {
        StringBuilder sql = new StringBuilder();
//        sql.append("select sum(case when(itemBase.operacaoFormula = 'ADICAO') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("select sum(case when(itemFicha.tipoEventoFP = 'VANTAGEM') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha, ItemBaseFP itemBase");
        sql.append(" inner join itemFicha.fichaFinanceiraFP ficha");
        sql.append(" inner join ficha.folhaDePagamento folha");
        sql.append(" inner join itemFicha.eventoFP evento");
        sql.append(" inner join itemBase.baseFP base");
        sql.append(" where evento = itemBase.eventoFP");
        sql.append("   and itemBase.somaValorRetroativo is true ");
        sql.append("   and folha.ano = :ano");
        sql.append("   and folha.mes = :mes");
        sql.append("   and base.codigo = :codigo");
        sql.append("   and ficha.vinculoFP = :vinculo");
        if (eventoFiltrados != null) {
            sql.append("   and evento in :filtrado ");
        }
        sql.append("   and itemFicha.tipoCalculoFP = :tipoCalculo and itemFicha.tipoEventoFP in ('VANTAGEM','DESCONTO')");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("codigo", codigo);
        if (eventoFiltrados != null) {
            q.setParameter("filtrado", eventoFiltrados);
        }

        q.setParameter("vinculo", ep);
        q.setParameter("tipoCalculo", TipoCalculoFP.RETROATIVO);
        Object result = q.getSingleResult();

        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }


    public String recuperarBaseSextaParte(EntidadePagavelRH ep) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select basefp.CODIGO ");
        sql.append(" from sextaparte ");
        sql.append(" inner join basefp on sextaparte.BASEFP_ID = basefp.id");
        sql.append(" where sextaparte.VINCULOFP_ID = :idVinculo and sextaparte.temDireito = :temDireito");
        sql.append(" and :data between sextaparte.inicioVigencia and coalesce(sextaparte.fimVigencia,:data)");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idVinculo", ep.getId());
        q.setParameter("temDireito", Boolean.TRUE);
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        try {
            if (!q.getResultList().isEmpty()) {
                return (String) q.getResultList().get(0);
            }
        } catch (Exception e) {
            logger.error("Erro ", e);
        }
        return null;
    }


    public Double valorTotalBaseParaRescisao(String codigo, EntidadePagavelRH ep, Integer ano, Integer mes, List<EventoFP> eventosExcluidos) {
        StringBuilder sql = new StringBuilder();
//        sql.append("select sum(case when(itemBase.operacaoFormula = 'ADICAO') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("select sum(case when(itemFicha.tipoEventoFP = 'VANTAGEM') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha, ItemBaseFP itemBase");
        sql.append(" inner join itemFicha.fichaFinanceiraFP ficha");
        sql.append(" inner join ficha.folhaDePagamento folha");
        sql.append(" inner join itemFicha.eventoFP evento");
        sql.append(" inner join itemBase.baseFP base");
        sql.append(" where evento = itemBase.eventoFP");
        sql.append("   and itemFicha.ano = :ano");
        sql.append("   and itemFicha.mes = :mes");
        sql.append("   and base.codigo = :codigo");
        sql.append("   and ficha.vinculoFP = :vinculo");
        sql.append("   and evento not in :itens");
        sql.append("   and itemFicha.tipoCalculoFP = :tipoCalculo and itemFicha.tipoEventoFP in ('VANTAGEM','DESCONTO')");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMes());
        q.setParameter("codigo", codigo);
        q.setParameter("vinculo", ep);
        q.setParameter("itens", eventosExcluidos);
        q.setParameter("tipoCalculo", TipoCalculoFP.NORMAL);
        Object result = q.getSingleResult();

        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }

    public Double valorTotalBaseRetroativosPorEvento(String codigo, EventoFP eventoFP, EntidadePagavelRH ep, Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
//        sql.append("select sum(case when(itemBase.operacaoFormula = 'ADICAO') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("select sum(case when(itemFicha.tipoEventoFP = 'VANTAGEM') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha, ItemBaseFP itemBase");
        sql.append(" inner join itemFicha.fichaFinanceiraFP ficha");
        sql.append(" inner join ficha.folhaDePagamento folha");
        sql.append(" inner join itemFicha.eventoFP evento");
        sql.append(" inner join itemBase.baseFP base");
        sql.append(" where evento = itemBase.eventoFP and evento = :evento");
        sql.append("   and folha.ano = :ano");
        sql.append("   and folha.mes = :mes");
        sql.append("   and base.codigo = :codigo");
        sql.append("   and ficha.vinculoFP = :vinculo");
        sql.append("   and itemFicha.tipoCalculoFP = :tipoCalculo and itemFicha.tipoEventoFP in ('VANTAGEM','DESCONTO')");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("codigo", codigo);
        q.setParameter("vinculo", ep);
        q.setParameter("evento", eventoFP);
        q.setParameter("tipoCalculo", TipoCalculoFP.RETROATIVO);
        Object result = q.getSingleResult();

        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }

    public Double valorTotalBaseRetroativosPorEventos(String codigo, List<EventoFP> eventoFPs, EntidadePagavelRH ep, Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
//        sql.append("select sum(case when(itemBase.operacaoFormula = 'ADICAO') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("select sum(case when(itemFicha.tipoEventoFP = 'VANTAGEM') then itemFicha.valor else -itemFicha.valor end)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha, ItemBaseFP itemBase");
        sql.append(" inner join itemFicha.fichaFinanceiraFP ficha");
        sql.append(" inner join ficha.folhaDePagamento folha");
        sql.append(" inner join itemFicha.eventoFP evento");
        sql.append(" inner join itemBase.baseFP base");
        sql.append(" where evento = itemBase.eventoFP and evento in :eventos");
        sql.append("   and folha.ano = :ano");
        sql.append("   and folha.mes = :mes");
        sql.append("   and base.codigo = :codigo");
        sql.append("   and ficha.vinculoFP = :vinculo");
        sql.append("   and itemFicha.tipoCalculoFP = :tipoCalculo and itemFicha.tipoEventoFP in ('VANTAGEM','DESCONTO')");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("codigo", codigo);
        q.setParameter("vinculo", ep);
        q.setParameter("eventos", eventoFPs);
        q.setParameter("tipoCalculo", TipoCalculoFP.RETROATIVO);
        Object result = q.getSingleResult();

        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }

    public boolean isLancamentosValidoPorTipo(EntidadePagavelRH vinculo, EventoFP evento, int ano, int mes, TipoFolhaDePagamento tipo, Date dataCalculo) { //data do calculo para caso o lançamento tenha sido lançado depois do ultimo dia de processamento da folha, não calcular :D
        Query q = em.createQuery("from LancamentoFP lancamento "
            + " where lancamento.vinculoFP.id = :vinculo and lancamento.tipoFolhaDePagamento = :tipo and lancamento.eventoFP.id = :eventoID "
            + " and (to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy')) and coalesce(lancamento.dataCadastro,:dataCalculo) <= :dataCalculo order by cast(lancamento.eventoFP.codigo as integer)");
        q.setParameter("vinculo", vinculo.getIdCalculo());
        q.setParameter("dataCalculo", dataCalculo);
        q.setParameter("tipo", tipo);
        q.setParameter("eventoID", evento.getId());
        q.setParameter("dataParam", dataCalculo);
        return !q.getResultList().isEmpty();
    }

    public LancamentoFP getLancamentosValidoPorTipo(EntidadePagavelRH vinculo, String codigo, int ano, int mes, TipoFolhaDePagamento tipo, LocalDate dataCalculo) { //data do calculo para caso o lançamento tenha sido lançado depois do ultimo dia de processamento da folha, não calcular :D
        EventoFP e = eventoFPFacade.recuperaEvento(codigo, TipoExecucaoEP.FOLHA);
        return getLancamentosValidoPorTipo(vinculo, e, ano, mes, tipo, dataCalculo);
    }

    public LancamentoFP getLancamentosValidoPorTipo(EntidadePagavelRH vinculo, EventoFP evento, int ano, int mes, TipoFolhaDePagamento tipo, LocalDate dataCalculo) { //data do calculo para caso o lançamento tenha sido lançado depois do ultimo dia de processamento da folha, não calcular :D
        String hql = "select lancamento from LancamentoFP lancamento "
            + " where lancamento.vinculoFP.id = :vinculo and lancamento.eventoFP.id = :eventoID "
            + " and (to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy')) and coalesce(lancamento.dataCadastro,:dataCalculo) <= :dataCalculo order by cast(lancamento.eventoFP.codigo as integer)";
        if (tipo.equals(TipoFolhaDePagamento.SALARIO_13)) {
            hql += " and lancamento.tipoFolhaDePagamento = :tipo ";
        }

        Query q = em.createQuery(hql);
        q.setParameter("vinculo", vinculo.getIdCalculo());
        q.setParameter("dataCalculo", dataCalculo);
        if (tipo.equals(TipoFolhaDePagamento.SALARIO_13)) {
            q.setParameter("tipo", tipo);
        }
        q.setParameter("eventoID", evento.getId());
        q.setParameter("dataParam", DataUtil.criarDataComMesEAno(mes, ano).toDate());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (LancamentoFP) q.getResultList().get(0);
    }

    @DescricaoMetodo("somaEventosPeriodoAquisitivoFeriasConcedidas(ep, mes, ano, tiposFolha, eventos)")
    public Double somaEventosPeriodoAquisitivoFeriasConcedidas(EntidadePagavelRH ep, Integer mes, Integer ano, List<TipoFolhaDePagamento> tiposFolha, String... eventos) {
        Double soma = new Double(0);
        List<String> listaEventos = Arrays.asList(eventos);
        VinculoFP vinculo = (VinculoFP) ep;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);

        Query q = em.createQuery(" select p from ConcessaoFeriasLicenca c"
            + " inner join c.periodoAquisitivoFL p"
            + " inner join p.contratoFP contrato"
            + " inner join p.baseCargo bc"
            + " inner join bc.basePeriodoAquisitivo bp"
            + " where contrato.id = :ep"
            + " and c.mes = :mes and c.ano = :ano "
            + " and bp.tipoPeriodoAquisitivo = :tipo ");
        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS);
        q.setParameter("ep", ep.getIdCalculo());

        if (vinculo.isHouveEstornoFerias()) {
            q.setParameter("mes", getPrimeiroDiaDoMesCalculo(ep).getMonthValue());
            q.setParameter("ano", getPrimeiroDiaDoMesCalculo(ep).getYear());
        } else {
            q.setParameter("mes", ep.getMes());
            q.setParameter("ano", ep.getAno());
        }


        List<PeriodoAquisitivoFL> listaPeriodoAquisitivoFLs = q.getResultList();

        if (listaPeriodoAquisitivoFLs != null) {
            for (PeriodoAquisitivoFL p : listaPeriodoAquisitivoFLs) {
                LocalDate proximaData = dateToLocalDate(p.getFinalVigencia());
                proximaData = proximaData.plusMonths(1);
                LocalDate dataInicio = dateToLocalDate(p.getInicioVigencia());
                LocalDate dataFinal = dateToLocalDate(p.getFinalVigencia());
                LocalDate inicioPeriodo = dateToLocalDate(p.getInicioVigencia());
                LocalDate finalPeriodo = dateToLocalDate(p.getFinalVigencia());

                while (dataInicio.isBefore(proximaData)) {
                    String hql = " select sum(coalesce((case when item.tipoEventoFP = evento.tipoEventoFP then item.valor else -item.valor end),0)) from ItemFichaFinanceiraFP item "
                        + " inner join item.fichaFinanceiraFP ficha"
                        + " inner join ficha.folhaDePagamento folha "
                        + " inner join item.eventoFP evento"
                        + " where folha.mes = :mes and folha.ano = :ano"
                        + " and ficha.vinculoFP.id = :ep "
                        + " and evento.codigo in :eventos";
                    if (tiposFolha != null) {
                        hql += "  and folha.tipoFolhaDePagamento in (:tiposFolha)";
                    }
                    Query q2 = em.createQuery(hql);
                    q2.setParameter("eventos", listaEventos);
                    q2.setParameter("ep", ep.getIdCalculo());
                    q2.setParameter("ano", dataInicio.getYear());
                    q2.setParameter("mes", Mes.getMesToInt(dataInicio.getMonthValue()));
                    if (tiposFolha != null) {
                        q2.setParameter("tiposFolha", tiposFolha);
                    }
                    q2.setMaxResults(1);

                    Object resultado = q2.getSingleResult();

                    if (resultado != null) {
                        double valor = ((BigDecimal) resultado).doubleValue();
                        int diasPeriodoArquisitivo = 0;
                        if (dataInicio.getMonthValue() == inicioPeriodo.getMonthValue() && dataInicio.getYear() == inicioPeriodo.getYear() || dataFinal.getMonthValue() == finalPeriodo.getMonthValue() && dataFinal.getYear() == finalPeriodo.getYear()) {
                            if (dataInicio.getMonthValue() == inicioPeriodo.getMonthValue() && dataInicio.getYear() == inicioPeriodo.getYear()) {
                                diasPeriodoArquisitivo = (dataInicio.lengthOfMonth() - dataInicio.getDayOfMonth()) + 1;
                            } else {
                                diasPeriodoArquisitivo = (dataInicio.lengthOfMonth() - dataInicio.withDayOfMonth(1).getDayOfMonth()) + 1;
                            }
                            if (dataInicio.getMonthValue() == finalPeriodo.getMonthValue() && dataInicio.getYear() == finalPeriodo.getYear()) {
                                diasPeriodoArquisitivo = (dataFinal.getDayOfMonth());
                            }
                        } else {
                            diasPeriodoArquisitivo = (dataInicio.lengthOfMonth() - dataInicio.withDayOfMonth(1).getDayOfMonth()) + 1;
                        }
                        int diasDoMes = DataUtil.diasEntre(dataInicio.withDayOfMonth(1), dataInicio.withDayOfMonth(dataInicio.lengthOfMonth()));

                        valor = getReferenciaPropocionalizada(diasDoMes, valor, diasPeriodoArquisitivo);
                        soma += valor;

                    }
                    dataInicio = dataInicio.plusMonths(1);
                }
            }
        }
        if (soma < 0) {
            return (double) 0;
        }
        return soma;
    }

    @DescricaoMetodo("somaEventosPeriodoAquisitivoTercoAutomatico(ep, mes, ano, tiposFolha, eventos)")
    public Double somaEventosPeriodoAquisitivoTercoAutomatico(EntidadePagavelRH ep, Integer mes, Integer ano, List<TipoFolhaDePagamento> tiposFolha, String... eventos) {
        Double soma = new Double(0);
        List<String> listaEventos = Arrays.asList(eventos);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);

        Query q = em.createQuery(" select p from LancamentoTercoFeriasAut l"
            + " inner join l.periodoAquisitivoFL p "
            + " where p.contratoFP.id = :ep "
            + " and l.mes = :mes "
            + " and l.ano = :ano ");
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("mes", ep.getMes());
        q.setParameter("ano", ep.getAno());


        List<PeriodoAquisitivoFL> listaPeriodoAquisitivoFLs = q.getResultList();

        if (listaPeriodoAquisitivoFLs != null) {
            for (PeriodoAquisitivoFL p : listaPeriodoAquisitivoFLs) {
                LocalDate proximaData = dateToLocalDate(p.getFinalVigencia());
                proximaData = proximaData.plusMonths(1);
                LocalDate dataInicio = dateToLocalDate(p.getInicioVigencia());
                LocalDate dataFinal = dateToLocalDate(p.getFinalVigencia());
                LocalDate inicioPeriodo = dateToLocalDate(p.getInicioVigencia());
                LocalDate finalPeriodo = dateToLocalDate(p.getFinalVigencia());

                while (dataInicio.isBefore(proximaData)) {
                    String hql = " select sum(coalesce((case when item.tipoEventoFP = evento.tipoEventoFP then item.valor else -item.valor end),0)) from ItemFichaFinanceiraFP item "
                        + " inner join item.fichaFinanceiraFP ficha"
                        + " inner join ficha.folhaDePagamento folha "
                        + " inner join item.eventoFP evento"
                        + " where folha.mes = :mes and folha.ano = :ano"
                        + " and ficha.vinculoFP.id = :ep "
                        + " and evento.codigo in :eventos";
                    if (tiposFolha != null) {
                        hql += "  and folha.tipoFolhaDePagamento in (:tiposFolha)";
                    }
                    Query q2 = em.createQuery(hql);
                    q2.setParameter("eventos", listaEventos);
                    q2.setParameter("ep", ep.getIdCalculo());
                    q2.setParameter("ano", dataInicio.getYear());
                    q2.setParameter("mes", Mes.getMesToInt(dataInicio.getMonthValue()));
                    if (tiposFolha != null) {
                        q2.setParameter("tiposFolha", tiposFolha);
                    }
                    q2.setMaxResults(1);

                    Object resultado = q2.getSingleResult();

                    if (resultado != null) {
                        double valor = ((BigDecimal) resultado).doubleValue();
                        int diasPeriodoArquisitivo = 0;
                        if (dataInicio.getMonthValue() == inicioPeriodo.getMonthValue() && dataInicio.getYear() == inicioPeriodo.getYear() || dataFinal.getMonthValue() == finalPeriodo.getMonthValue() && dataFinal.getYear() == finalPeriodo.getYear()) {
                            if (dataInicio.getMonthValue() == inicioPeriodo.getMonthValue() && dataInicio.getYear() == inicioPeriodo.getYear()) {
                                diasPeriodoArquisitivo = (dataInicio.lengthOfMonth() - dataInicio.getDayOfMonth()) + 1;
                            } else {
                                diasPeriodoArquisitivo = (dataInicio.lengthOfMonth() - dataInicio.withDayOfMonth(1).getDayOfMonth()) + 1;
                            }
                            if (dataInicio.getMonthValue() == finalPeriodo.getMonthValue() && dataInicio.getYear() == finalPeriodo.getYear()) {
                                diasPeriodoArquisitivo = (dataFinal.getDayOfMonth());
                            }
                        } else {
                            diasPeriodoArquisitivo = (dataInicio.lengthOfMonth() - dataInicio.withDayOfMonth(1).getDayOfMonth()) + 1;
                        }
                        int diasDoMes = DataUtil.diasEntre(dataInicio.withDayOfMonth(1), dataInicio.withDayOfMonth(dataInicio.lengthOfMonth()));

                        valor = getReferenciaPropocionalizada(diasDoMes, valor, diasPeriodoArquisitivo);
                        soma += valor;

                    }
                    dataInicio = dataInicio.plusMonths(1);
                }
            }
        }
        if (soma < 0) {
            return (double) 0;
        }
        return soma;
    }

    @DescricaoMetodo("quantidadeOcorrenciaEventosNoPeriodoAquisitoFeriasConcedida(ep, mes, ano,eventos)")
    public Double quantidadeOcorrenciaEventosNoPeriodoAquisitoFeriasConcedida(EntidadePagavelRH ep, Integer mes, Integer ano, FolhaDePagamentoNovoCalculador calculador, String... eventos) {
        Double soma = new Double(0);
        List<String> listaEventos = Arrays.asList(eventos);
        VinculoFP vinculo = (VinculoFP) ep;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);

        Query q = em.createQuery(" select p from ConcessaoFeriasLicenca c"
            + " inner join c.periodoAquisitivoFL p"
            + " inner join p.contratoFP contrato"
            + " inner join p.baseCargo bc"
            + " inner join bc.basePeriodoAquisitivo bp"
            + " where contrato.id = :ep"
            + " and c.mes = :mes and c.ano = :ano "
            + " and bp.tipoPeriodoAquisitivo = :tipo ");
        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS);
        q.setParameter("ep", ep.getIdCalculo());

        if (vinculo.isHouveEstornoFerias()) {
            q.setParameter("mes", getPrimeiroDiaDoMesCalculo(ep).getMonthValue());
            q.setParameter("ano", getPrimeiroDiaDoMesCalculo(ep).getYear());
        } else {
            q.setParameter("mes", ep.getMes());
            q.setParameter("ano", ep.getAno());
        }


        List<PeriodoAquisitivoFL> listaPeriodoAquisitivoFLs = q.getResultList();
        if (listaPeriodoAquisitivoFLs == null || listaPeriodoAquisitivoFLs != null && listaPeriodoAquisitivoFLs.isEmpty()) {
            listaPeriodoAquisitivoFLs = Lists.newArrayList();
            PeriodoAquisitivoFL ultimoPeriodoAquitivo = getUltimoPeriodoAquitivo(ep);
            if (ultimoPeriodoAquitivo != null) {
                listaPeriodoAquisitivoFLs.add(ultimoPeriodoAquitivo);
            }
        }
        if (listaPeriodoAquisitivoFLs != null) {
            for (PeriodoAquisitivoFL p : listaPeriodoAquisitivoFLs) {
                LocalDate proximaData = dateToLocalDate(p.getFinalVigencia());
                LocalDate inicioPeriodo = dateToLocalDate(p.getInicioVigencia());
                LocalDate finalPeriodo = dateToLocalDate(p.getFinalVigencia());
                List<LocalDate> datas = getDatasEmLista(inicioPeriodo, finalPeriodo);
                if (datas != null && !datas.isEmpty()) {
                    soma = soma + datas.size();
                }
            }
        }
        return soma;
    }

    @DescricaoMetodo("somaReferenciaEventosNoPeriodoAquisitoFeriasConcedida(ep, mes, ano, tiposFolha, eventos)")
    public Double somaReferenciaEventosNoPeriodoAquisitoFeriasConcedida(EntidadePagavelRH ep, Integer mes, Integer ano, FolhaDePagamentoNovoCalculador calculador, String[] tiposFolha, String... eventos) {
        Double soma = new Double(0);
        List<String> listaEventos = Arrays.asList(eventos);
        VinculoFP vinculo = (VinculoFP) ep;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);

        Query q = em.createNativeQuery(" select p.id, p.INICIOVIGENCIA, p.FINALVIGENCIA " +
            " from ConcessaoFeriasLicenca c " +
            "         inner join periodoAquisitivoFL p on c.PERIODOAQUISITIVOFL_ID = p.ID " +
            "         inner join contratoFP contrato on p.CONTRATOFP_ID = contrato.ID " +
            "         inner join baseCargo bc on p.BASECARGO_ID = bc.ID " +
            "         inner join basePeriodoAquisitivo bp on bc.BASEPERIODOAQUISITIVO_ID = bp.ID " +
            " where p.CONTRATOFP_ID = :ep "
            + " and c.mes = :mes and c.ano = :ano "
            + " and bp.tipoPeriodoAquisitivo = :tipo ");
        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("ep", ep.getIdCalculo());

        if (vinculo.isHouveEstornoFerias()) {
            q.setParameter("mes", getPrimeiroDiaDoMesCalculo(ep).getMonthValue());
            q.setParameter("ano", getPrimeiroDiaDoMesCalculo(ep).getYear());
        } else {
            q.setParameter("mes", ep.getMes());
            q.setParameter("ano", ep.getAno());
        }

        List<PeriodoAquisitivoFLDTO> listaPeriodoAquisitivoFLs = Lists.newArrayList();
        List<Object[]> periodos = q.getResultList();
        for (Object[] periodo : periodos) {
            PeriodoAquisitivoFLDTO dto = new PeriodoAquisitivoFLDTO();
            dto.setId(((BigDecimal) periodo[0]).longValue());
            dto.setInicioVigencia((Date) periodo[1]);
            dto.setFinalVigencia((Date) periodo[2]);
            listaPeriodoAquisitivoFLs.add(dto);
        }

        if (listaPeriodoAquisitivoFLs != null) {
            for (PeriodoAquisitivoFLDTO p : listaPeriodoAquisitivoFLs) {
                LocalDate proximaData = dateToLocalDate(p.getFinalVigencia());
                proximaData = proximaData.plusMonths(1);
                LocalDate dataInicio = dateToLocalDate(p.getInicioVigencia());
                LocalDate dataFinal = dateToLocalDate(p.getFinalVigencia());
                LocalDate inicioPeriodo = dateToLocalDate(p.getInicioVigencia());
                LocalDate finalPeriodo = dateToLocalDate(p.getFinalVigencia());
                List<LocalDate> datas = getDatasEmLista(inicioPeriodo, finalPeriodo);
                for (LocalDate data : datas) {
                    int diasDoMes = daysBetween(dataInicio.withDayOfMonth(1), dataInicio.withDayOfMonth(dataInicio.lengthOfMonth())) + 1;

                    Query q2 = em.createNativeQuery(" select iff.valorReferencia        as valorReferencia, " +
                        "       iff.MES                    as mesItem, " +
                        "       iff.ano                    as anoItem, " +
                        "       evento.codigo              as codigoEvento, " +
                        "       evento.tipoeventofp        as tipoEveto, " +
                        "       iff.tipoeventofp           as tipoEventoFicha, " +
                        "       folha.tipofolhadepagamento as tipoFolha " +
                        " from ItemFichaFinanceiraFP iff " +
                        " inner join fichafinanceirafp ff on iff.FICHAFINANCEIRAFP_ID = ff.id " +
                        " inner join folhadepagamento folha on ff.FOLHADEPAGAMENTO_ID = folha.id " +
                        " inner join eventofp evento on iff.EVENTOFP_ID = evento.id " +
                        " where ff.VINCULOFP_ID = :ep " +
                        " and folha.mes = :mes " +
                        " and folha.ano = :ano " +
                        " and folha.TIPOFOLHADEPAGAMENTO in (:tiposFolha) " +
                        " and evento.codigo in :eventos ");
                    q2.setParameter("eventos", listaEventos);
                    q2.setParameter("ep", ep.getIdCalculo());
                    q2.setParameter("tiposFolha", tiposFolha != null ? Arrays.asList(tiposFolha) : Lists.newArrayList(TipoFolhaDePagamento.NORMAL.name()));
                    q2.setParameter("ano", data.getYear());
                    q2.setParameter("mes", data.getMonthValue() - 1);
                    List<Object[]> resultado = (List<Object[]>) q2.getResultList();
                    if (temMesDoPeriodoNoCalculoAtual(calculador, dataInicio) && (resultado == null || resultado.isEmpty())) {
                        int diasPeriodoAquisi = diasDoPeriodoAquisitivo(vinculo, p.getInicioVigencia(), dataInicio, dataFinal, inicioPeriodo, finalPeriodo, diasDoMes);
                        double refe = 0;
                        for (String evento : eventos) {
                            try {
                                refe += calculador.avaliaReferencia(evento);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        double val = refe;
                        if (diasDoMes != diasPeriodoAquisi) {
                            val = getReferenciaPropocionalizada(diasDoMes, refe, diasPeriodoAquisi);
                        }
                        soma += val;

                    }
                    if (resultado != null && !resultado.isEmpty()) {
                        List<ValorEventoPeriodoVO> valores = Lists.newArrayList();
                        BigDecimal somaValores = BigDecimal.ZERO;
                        for (Object[] obj : resultado) {
                            ValorEventoPeriodoVO valor = new ValorEventoPeriodoVO();
                            valor.setValorReferencia(obj[0] != null ? ((BigDecimal) obj[0]) : BigDecimal.ZERO);
                            valor.setMesItem(obj[1] != null ? ((BigDecimal) obj[1]).intValue() : null);
                            valor.setAnoItem(obj[2] != null ? ((BigDecimal) obj[2]).intValue() : null);
                            valor.setCodigoEvento((String) obj[3]);
                            valor.setTipoEvento((String) obj[4]);
                            valor.setTipoEventoFicha((String) obj[5]);
                            valor.setTipoFolhaDePagamento((String) obj[6]);
                            valores.add(valor);
                        }

                        for (ValorEventoPeriodoVO v : valores) {
                            if (data.getMonthValue() == v.getMesItem() && data.getYear() == v.getAnoItem()) {
                                somaValores = somaValores.add(v.getValorReferencia());
                            } else {
                                BigDecimal valorReferenciaOrigemEvento = fichaFinanceiraFPFacade.buscarValorReferenciaPorMesAnoTipoFolhaEventoVinculo(v.getMesItem(), v.getAnoItem(), v.getTipoFolhaDePagamento(), ep.getIdCalculo(), v.getCodigoEvento(), (data.getMonthValue() - 1), data.getYear());
                                if (valorReferenciaOrigemEvento == null) {
                                    somaValores = somaValores.add(v.getValorReferencia());
                                } else {
                                    if (v.getValorReferencia().equals(valorReferenciaOrigemEvento) && !v.getTipoEvento().equals(v.getTipoEventoFicha())) {
                                        somaValores = somaValores.add(valorReferenciaOrigemEvento.negate());
                                    } else {
                                        somaValores = somaValores.add(v.getValorReferencia().subtract(valorReferenciaOrigemEvento));
                                    }
                                }
                            }
                        }
                        double valor = somaValores.doubleValue();

                        int diasPeriodoArquisitivo = 0;
                        diasDoMes = daysBetween(dataInicio.withDayOfMonth(1), dataInicio.withDayOfMonth(dataInicio.lengthOfMonth())) + 1;


                        diasPeriodoArquisitivo = diasDoPeriodoAquisitivo(vinculo, p.getInicioVigencia(), dataInicio, dataFinal, inicioPeriodo, finalPeriodo, diasDoMes);
                        valor = getReferenciaPropocionalizada(diasDoMes, valor, diasPeriodoArquisitivo);
                        soma += valor;

                    }
                    dataInicio = dataInicio.plusMonths(1);
                }
            }
        }
        if (soma < 0) {
            return (double) 0;
        }
        return soma;
    }


    @DescricaoMetodo("somaReferenciaEventosNoPeriodoAquisitoTercoAutomatico(ep, mes, ano, tiposFolha, eventos)")
    public Double somaReferenciaEventosNoPeriodoAquisitoTercoAutomatico(EntidadePagavelRH ep, Integer mes, Integer ano, FolhaDePagamentoNovoCalculador calculador, String[] tiposFolha, String... eventos) {
        Double soma = new Double(0);
        List<String> listaEventos = Arrays.asList(eventos);
        VinculoFP vinculo = (VinculoFP) ep;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);

        Query q = em.createNativeQuery(" select pa.id, pa.INICIOVIGENCIA, pa.FINALVIGENCIA " +
            " from lancamentotercoferiasaut lancamento " +
            "         inner join periodoaquisitivofl pa on lancamento.periodoaquisitivofl_id = pa.id " +
            " where lancamento.ano = :ano " +
            "  and lancamento.mes = :mes " +
            "  and pa.contratofp_id = :ep ");
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("mes", ep.getMes());
        q.setParameter("ano", ep.getAno());

        List<PeriodoAquisitivoFLDTO> listaPeriodoAquisitivoFLs = Lists.newArrayList();
        List<Object[]> periodos = q.getResultList();
        for (Object[] periodo : periodos) {
            PeriodoAquisitivoFLDTO dto = new PeriodoAquisitivoFLDTO();
            dto.setId(((BigDecimal) periodo[0]).longValue());
            dto.setInicioVigencia((Date) periodo[1]);
            dto.setFinalVigencia((Date) periodo[2]);
            listaPeriodoAquisitivoFLs.add(dto);
        }

        if (listaPeriodoAquisitivoFLs != null) {
            for (PeriodoAquisitivoFLDTO p : listaPeriodoAquisitivoFLs) {
                LocalDate dataInicio = dateToLocalDate(p.getInicioVigencia());
                LocalDate dataFinal = dateToLocalDate(p.getFinalVigencia());
                LocalDate inicioPeriodo = dateToLocalDate(p.getInicioVigencia());
                LocalDate finalPeriodo = dateToLocalDate(p.getFinalVigencia());
                List<LocalDate> datas = getDatasEmLista(inicioPeriodo, finalPeriodo);
                for (LocalDate data : datas) {
                    int diasDoMes = daysBetween(dataInicio.withDayOfMonth(1), dataInicio.withDayOfMonth(dataInicio.lengthOfMonth())) + 1;

                    Query q2 = em.createNativeQuery(" select iff.valorReferencia        as valorReferencia, " +
                        "       iff.MES                    as mesItem, " +
                        "       iff.ano                    as anoItem, " +
                        "       evento.codigo              as codigoEvento, " +
                        "       evento.tipoeventofp        as tipoEveto, " +
                        "       iff.tipoeventofp           as tipoEventoFicha, " +
                        "       folha.tipofolhadepagamento as tipoFolha " +
                        " from ItemFichaFinanceiraFP iff " +
                        " inner join fichafinanceirafp ff on iff.FICHAFINANCEIRAFP_ID = ff.id " +
                        " inner join folhadepagamento folha on ff.FOLHADEPAGAMENTO_ID = folha.id " +
                        " inner join eventofp evento on iff.EVENTOFP_ID = evento.id " +
                        " where ff.VINCULOFP_ID = :ep " +
                        " and folha.mes = :mes " +
                        " and folha.ano = :ano " +
                        " and folha.TIPOFOLHADEPAGAMENTO in (:tiposFolha) " +
                        " and evento.codigo in :eventos ");
                    q2.setParameter("eventos", listaEventos);
                    q2.setParameter("ep", ep.getIdCalculo());
                    q2.setParameter("tiposFolha", tiposFolha != null ? Arrays.asList(tiposFolha) : Lists.newArrayList(TipoFolhaDePagamento.NORMAL.name()));
                    q2.setParameter("ano", data.getYear());
                    q2.setParameter("mes", data.getMonthValue() - 1);
                    List<Object[]> resultado = (List<Object[]>) q2.getResultList();
                    if (temMesDoPeriodoNoCalculoAtual(calculador, dataInicio) && (resultado == null || resultado.isEmpty())) {
                        int diasPeriodoAquisi = diasDoPeriodoAquisitivo(vinculo, p.getInicioVigencia(), dataInicio, dataFinal, inicioPeriodo, finalPeriodo, diasDoMes);
                        double refe = 0;
                        for (String evento : eventos) {
                            try {
                                refe += calculador.avaliaReferencia(evento);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        double val = refe;
                        if (diasDoMes != diasPeriodoAquisi) {
                            val = getReferenciaPropocionalizada(diasDoMes, refe, diasPeriodoAquisi);
                        }
                        soma += val;

                    }
                    if (resultado != null && !resultado.isEmpty()) {
                        List<ValorEventoPeriodoVO> valores = Lists.newArrayList();
                        BigDecimal somaValores = BigDecimal.ZERO;
                        for (Object[] obj : resultado) {
                            ValorEventoPeriodoVO valor = new ValorEventoPeriodoVO();
                            valor.setValorReferencia(obj[0] != null ? ((BigDecimal) obj[0]) : BigDecimal.ZERO);
                            valor.setMesItem(obj[1] != null ? ((BigDecimal) obj[1]).intValue() : null);
                            valor.setAnoItem(obj[2] != null ? ((BigDecimal) obj[2]).intValue() : null);
                            valor.setCodigoEvento((String) obj[3]);
                            valor.setTipoEvento((String) obj[4]);
                            valor.setTipoEventoFicha((String) obj[5]);
                            valor.setTipoFolhaDePagamento((String) obj[6]);
                            valores.add(valor);
                        }

                        for (ValorEventoPeriodoVO v : valores) {
                            if (data.getMonthValue() == v.getMesItem() && data.getYear() == v.getAnoItem()) {
                                somaValores = somaValores.add(v.getValorReferencia());
                            } else {
                                BigDecimal valorReferenciaOrigemEvento = fichaFinanceiraFPFacade.buscarValorReferenciaPorMesAnoTipoFolhaEventoVinculo(v.getMesItem(), v.getAnoItem(), v.getTipoFolhaDePagamento(), ep.getIdCalculo(), v.getCodigoEvento(), (data.getMonthValue() - 1), data.getYear());
                                if (valorReferenciaOrigemEvento == null) {
                                    somaValores = somaValores.add(v.getValorReferencia());
                                } else {
                                    if (v.getValorReferencia().equals(valorReferenciaOrigemEvento) && !v.getTipoEvento().equals(v.getTipoEventoFicha())) {
                                        somaValores = somaValores.add(valorReferenciaOrigemEvento.negate());
                                    } else {
                                        somaValores = somaValores.add(v.getValorReferencia().subtract(valorReferenciaOrigemEvento));
                                    }
                                }
                            }
                        }
                        double valor = somaValores.doubleValue();

                        int diasPeriodoArquisitivo = 0;
                        diasDoMes = daysBetween(dataInicio.withDayOfMonth(1), dataInicio.withDayOfMonth(dataInicio.lengthOfMonth())) + 1;


                        diasPeriodoArquisitivo = diasDoPeriodoAquisitivo(vinculo, p.getInicioVigencia(), dataInicio, dataFinal, inicioPeriodo, finalPeriodo, diasDoMes);
                        valor = getReferenciaPropocionalizada(diasDoMes, valor, diasPeriodoArquisitivo);
                        soma += valor;

                    }
                    dataInicio = dataInicio.plusMonths(1);
                }
            }
        }
        if (soma < 0) {
            return (double) 0;
        }
        return soma;
    }

    public Double somarReferenciaHorasPagasNoAno(EntidadePagavelRH ep, Integer mes, Integer ano, String... eventos) {
        Double soma = new Double(0);
        List<String> listaEventos = Arrays.asList(eventos);
        VinculoFP vinculo = (VinculoFP) ep;

        Query q2 = em.createQuery(" select sum(coalesce(item.valorReferencia,0)) from ItemFichaFinanceiraFP item "
            + " inner join item.fichaFinanceiraFP ficha"
            + " inner join ficha.folhaDePagamento folha "
            + " inner join item.eventoFP evento"
            + " where folha.ano = :ano"
            + " and folha.tipoFolhaDePagamento = :tipo "
            + " and ficha.vinculoFP.id = :ep "
            + " and evento.codigo in :eventos ");
        q2.setParameter("eventos", listaEventos);
        q2.setParameter("ep", ep.getIdCalculo());
        q2.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
        q2.setParameter("ano", ep.getAno());
        q2.setMaxResults(1);
        Object resultado = q2.getSingleResult();
        if (resultado != null) {
            double valor = ((BigDecimal) resultado).doubleValue();
            soma += valor;
        }
        return soma;
    }


    @DescricaoMetodo("buscarQuantidadeLancamentoEventosNoPeriodoAquisitoFerias(ep, mes, ano,eventos)")
    public Double buscarQuantidadeLancamentoEventosNoPeriodoAquisitoFerias(EntidadePagavelRH ep, Integer mes, Integer ano, FolhaDePagamentoNovoCalculador calculador, String... eventos) {
        Long quantidadeLancamento = 0L;
        List<String> listaEventos = Arrays.asList(eventos);
        VinculoFP vinculo = (VinculoFP) ep;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);

        Query q = em.createQuery(" select p from ConcessaoFeriasLicenca c"
            + " inner join c.periodoAquisitivoFL p"
            + " inner join p.contratoFP contrato"
            + " inner join p.baseCargo bc"
            + " inner join bc.basePeriodoAquisitivo bp"
            + " where contrato.id = :ep"
            //   + " and c.dataInicial >= :primeiroDiaConcessao and c.dataInicial <= :primeiroDiaProxMesConcessao "
            + " and c.mes = :mes and c.ano = :ano "
            + " and bp.tipoPeriodoAquisitivo = :tipo ");
        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS);
        q.setParameter("ep", ep.getIdCalculo());

        if (vinculo.isHouveEstornoFerias()) {
            q.setParameter("mes", getPrimeiroDiaDoMesCalculo(ep).getMonthValue());
            q.setParameter("ano", getPrimeiroDiaDoMesCalculo(ep).getYear());
        } else {
            q.setParameter("mes", ep.getMes());
            q.setParameter("ano", ep.getAno());
        }


        List<PeriodoAquisitivoFL> listaPeriodoAquisitivoFLs = q.getResultList();
        if (listaPeriodoAquisitivoFLs == null || listaPeriodoAquisitivoFLs != null && listaPeriodoAquisitivoFLs.isEmpty()) {
            listaPeriodoAquisitivoFLs = Lists.newArrayList();
            PeriodoAquisitivoFL ultimoPeriodoAquitivo = getUltimoPeriodoAquitivo(ep);
            if (ultimoPeriodoAquitivo != null) {
                listaPeriodoAquisitivoFLs.add(ultimoPeriodoAquitivo);
            }
        }
        if (listaPeriodoAquisitivoFLs != null) {
            for (PeriodoAquisitivoFL p : listaPeriodoAquisitivoFLs) {
                LocalDate inicioPeriodo = dateToLocalDate(p.getInicioVigencia());
                LocalDate finalPeriodo = dateToLocalDate(p.getFinalVigencia());
                List<LocalDate> datas = getDatasEmLista(inicioPeriodo, finalPeriodo);
                for (LocalDate data : datas) {
                    Query q2 = em.createQuery(" select count(item.id) from ItemFichaFinanceiraFP item "
                        + " inner join item.fichaFinanceiraFP ficha"
                        + " inner join ficha.folhaDePagamento folha "
                        + " inner join item.eventoFP evento"
                        + " where item.mes = :mes and item.ano = :ano"
                        + " and folha.tipoFolhaDePagamento = :tipo "
                        + " and ficha.vinculoFP.id = :ep "
                        + " and evento.codigo in :eventos ");
                    q2.setParameter("eventos", listaEventos);
                    q2.setParameter("ep", ep.getIdCalculo());
                    q2.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
                    q2.setParameter("ano", data.getYear());
                    q2.setParameter("mes", data.getMonthValue());
                    q2.setMaxResults(1);
                    if (!q2.getResultList().isEmpty()) {
                        quantidadeLancamento += (Long) q2.getSingleResult();
                    }
                }
            }
        }
        return quantidadeLancamento.doubleValue();
    }


    public Double somarValorHorasPagasNoAno(EntidadePagavelRH ep, Integer mes, Integer ano, String... eventos) {
        Double soma = new Double(0);
        List<String> listaEventos = Arrays.asList(eventos);
        VinculoFP vinculo = (VinculoFP) ep;

        Query q2 = em.createQuery(" select sum(coalesce(item.valor,0)) from ItemFichaFinanceiraFP item "
            + " inner join item.fichaFinanceiraFP ficha"
            + " inner join ficha.folhaDePagamento folha "
            + " inner join item.eventoFP evento"
            + " where folha.ano = :ano"
            + " and folha.tipoFolhaDePagamento = :tipo "
            + " and ficha.vinculoFP.id = :ep "
            + " and evento.codigo in :eventos ");
        q2.setParameter("eventos", listaEventos);
        q2.setParameter("ep", ep.getIdCalculo());
        q2.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
        q2.setParameter("ano", ep.getAno());
        q2.setMaxResults(1);
        Object resultado = q2.getSingleResult();
        if (resultado != null) {
            double valor = ((BigDecimal) resultado).doubleValue();
            soma += valor;
        }
        return soma;
    }

    @DescricaoMetodo("somaReferenciaEventosNoPeriodoAquisitoFeriasConcedida(ep, mes, ano,eventos)")
    public Double somaReferenciaEventosNoPeriodoAquisitoComProporcionalizacaoDiasLancamentoFP(EntidadePagavelRH ep, Integer mes, Integer ano, FolhaDePagamentoNovoCalculador calculador, String... eventos) {
        Double soma = new Double(0);
        List<String> listaEventos = Arrays.asList(eventos);
        VinculoFP vinculo = (VinculoFP) ep;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);

        Query q = em.createQuery(" select p from ConcessaoFeriasLicenca c"
            + " inner join c.periodoAquisitivoFL p"
            + " inner join p.contratoFP contrato"
            + " inner join p.baseCargo bc"
            + " inner join bc.basePeriodoAquisitivo bp"
            + " where contrato.id = :ep"
            //   + " and c.dataInicial >= :primeiroDiaConcessao and c.dataInicial <= :primeiroDiaProxMesConcessao "
            + " and c.mes = :mes and c.ano = :ano "
            + " and bp.tipoPeriodoAquisitivo = :tipo ");
        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS);
        q.setParameter("ep", ep.getIdCalculo());

        if (vinculo.isHouveEstornoFerias()) {
            //q.setParameter("primeiroDiaConcessao", getPrimeiroDiaDoMesCalculo(ep).toDate());
            //q.setParameter("primeiroDiaProxMesConcessao", getUltimoDiaDoMesCalculo(ep).toDate());
            q.setParameter("mes", getPrimeiroDiaDoMesCalculo(ep).getMonthValue());
            q.setParameter("ano", getPrimeiroDiaDoMesCalculo(ep).getYear());
        } else {
            //q.setParameter("primeiroDiaConcessao", primeiroDiaConcessao);
            //q.setParameter("primeiroDiaProxMesConcessao", primeiroDiaProxMesConcessao);
            q.setParameter("mes", ep.getMes());
            q.setParameter("ano", ep.getAno());
        }


        List<PeriodoAquisitivoFL> listaPeriodoAquisitivoFLs = q.getResultList();
        if (listaPeriodoAquisitivoFLs != null) {
            for (PeriodoAquisitivoFL p : listaPeriodoAquisitivoFLs) {
                LocalDate proximaData = dateToLocalDate(p.getFinalVigencia());
                proximaData = proximaData.plusMonths(1);
                LocalDate dataInicio = dateToLocalDate(p.getInicioVigencia());
                LocalDate dataFinal = dateToLocalDate(p.getFinalVigencia());
                LocalDate inicioPeriodo = dateToLocalDate(p.getInicioVigencia());
                LocalDate finalPeriodo = dateToLocalDate(p.getFinalVigencia());
                int meses = monthsBetween(inicioPeriodo, finalPeriodo) + 1;
                List<LocalDate> datas = getDatasEmLista(inicioPeriodo, finalPeriodo);
                for (LocalDate data : datas) {

                    for (String evento : eventos) {

                        int diasDoMes = daysBetween(dataInicio.withDayOfMonth(1), dataInicio.withDayOfMonth(dataInicio.lengthOfMonth())) + 1;


                        Query q2 = em.createQuery(" select sum(coalesce(item.valorReferencia,0)) from ItemFichaFinanceiraFP item "
                            + " inner join item.fichaFinanceiraFP ficha"
                            + " inner join ficha.folhaDePagamento folha "
                            + " inner join item.eventoFP evento"
                            + " where item.mes = :mes and item.ano = :ano"
                            + " and folha.tipoFolhaDePagamento = :tipo "
                            + " and ficha.vinculoFP.id = :ep "
                            + " and evento.codigo like :eventos ");
                        q2.setParameter("eventos", evento);
                        q2.setParameter("ep", ep.getIdCalculo());
                        q2.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
                        q2.setParameter("ano", data.getYear());
                        q2.setParameter("mes", data.getMonthValue());
                        q2.setMaxResults(1);
                        Object resultado = q2.getSingleResult();
                        if (temMesDoPeriodoNoCalculoAtual(calculador, dataInicio) && resultado == null) {
                            int diasPeriodoAquisi = diasDoPeriodoAquisitivo(vinculo, p, dataInicio, dataFinal, inicioPeriodo, finalPeriodo, diasDoMes);
                            double refe = 0;

                            try {
                                refe += calculador.avaliaReferencia(evento);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            double val = refe;
                            if (diasDoMes != diasPeriodoAquisi) {
                                val = getReferenciaPropocionalizada(diasDoMes, refe, diasPeriodoAquisi);
                            }
                            soma += val;

                        }
                        if (resultado != null) {
                            double valor = ((BigDecimal) resultado).doubleValue();
                            logger.debug("REFE: " + getDataReferencia(ep));
                            EventoFP eventoFP = eventoFPFacade.recuperaEvento(evento, TipoExecucaoEP.FOLHA);
                            LancamentoFP l = getLancamentosValidoPorTipo(ep, eventoFP, data.getYear(), data.getMonthValue(), calculador.getFolhaDePagamento().getTipoFolhaDePagamento(), getDataReferencia(ep));
                            diasDoMes = daysBetween(dataInicio.withDayOfMonth(1), dataInicio.withDayOfMonth(dataInicio.lengthOfMonth())) + 1;
                            int diasPeriodoArquisitivo = 0;
                            if (l != null) {
                                valor = l.getQuantificacao().doubleValue();
                                int dias = diasEntreDatas(data, dateToLocalDate(l.getMesAnoInicial()), dateToLocalDate(l.getMesAnoFinal()));
                                logger.debug("Dias: " + dias);


                                System.out.println("lancamento: " + l + " datas: " + l.getMesAnoInicial() + l.getMesAnoFinal() + " ID: " + ep.getIdCalculo());


                                if (eventoFP.getProporcionalizaDiasTrab()) {
                                    logger.debug("valor antes: " + valor);
                                    valor = getReferenciaPropocionalizada(diasDoMes, valor, dias);
                                    logger.debug("valor depois: " + valor);
                                }
                            }

                            diasPeriodoArquisitivo = diasDoPeriodoAquisitivo(vinculo, p, dataInicio, dataFinal, inicioPeriodo, finalPeriodo, diasDoMes);

//                        Interval i = new Interval(inicioPeriodo,finalPeriodo);
//                        logger.debug("dataFicha: "+ dataFicha);
//                        logger.debug("contem a data: "+i.contains(dataFicha));
//                        if(!i.contains(dataFicha)){
//                            valor = 0;
//                        }

//                        logger.debug("Total: " + valor + " data inicio: " + dataInicio);
//                        logger.debug("data final : " + finalPeriodo);
//                        logger.debug("dias periodo aquisitivo " + diasPeriodoArquisitivo);

                            valor = getReferenciaPropocionalizada(diasDoMes, valor, diasPeriodoArquisitivo);
                            soma += valor;

                        }
//                    logger.debug("data inicio: " + dataInicio);
                        dataInicio = dataInicio.plusMonths(1);
                    }
                }

            }
        }
        return soma;
    }

    private double getReferenciaPropocionalizada(int diasDoMes, double valor, int diasPeriodoArquisitivo) {
        if (diasDoMes != diasPeriodoArquisitivo) {
            BigDecimal v = BigDecimal.valueOf((valor / diasDoMes) * diasPeriodoArquisitivo);
//            valor = v.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
            valor = v.doubleValue();
        }
        return valor;
    }

    private boolean temMesDoPeriodoNoCalculoAtual(FolhaDePagamentoNovoCalculador calculador, LocalDate data) {
        return calculador.getEp().getMes() == data.getMonthValue() && calculador.getEp().getAno() == data.getYear();
    }

    private int diasDoPeriodoAquisitivo(VinculoFP vinculo, PeriodoAquisitivoFL p, LocalDate dataInicio, LocalDate dataFinal, LocalDate inicioPeriodo, LocalDate finalPeriodo, int diasDoMes) {
        return diasDoPeriodoAquisitivo(vinculo, p.getInicioVigencia(), dataInicio, dataFinal, inicioPeriodo, finalPeriodo, diasDoMes);
    }

    private int diasDoPeriodoAquisitivo(VinculoFP vinculo, Date inicioVigenciaPeriodoAquisitivo, LocalDate dataInicio, LocalDate dataFinal, LocalDate inicioPeriodo, LocalDate finalPeriodo, int diasDoMes) {
        int diasPeriodoArquisitivo;
        if (dataInicio.getMonthValue() == inicioPeriodo.getMonthValue() && dataInicio.getYear() == inicioPeriodo.getYear() || dataFinal.getMonthValue() == finalPeriodo.getMonthValue() && dataFinal.getYear() == finalPeriodo.getYear()) {
            if (dataInicio.getMonthValue() == inicioPeriodo.getMonthValue() && dataInicio.getYear() == inicioPeriodo.getYear()) {
                diasPeriodoArquisitivo = (dataInicio.lengthOfMonth() - dataInicio.getDayOfMonth()) + 1;
                if (vinculo.getInicioVigencia().equals(inicioVigenciaPeriodoAquisitivo)) {
                    diasPeriodoArquisitivo = diasDoMes;
                }
            } else {
                diasPeriodoArquisitivo = (dataInicio.lengthOfMonth() - dataInicio.withDayOfMonth(1).getDayOfMonth()) + 1;
            }
            if (dataInicio.getMonthValue() == finalPeriodo.getMonthValue() && dataInicio.getYear() == finalPeriodo.getYear()) {
                diasPeriodoArquisitivo = (dataFinal.getDayOfMonth());
            }
        } else {
            diasPeriodoArquisitivo = (dataInicio.lengthOfMonth() - dataInicio.withDayOfMonth(1).getDayOfMonth()) + 1;
        }
        return diasPeriodoArquisitivo;
    }


    private List<LocalDate> getDatasEmLista(LocalDate inicioPeriodo, LocalDate finalPeriodo) {

        List<LocalDate> datas = new LinkedList<>();
        int meses = ((Long) MONTHS.between(inicioPeriodo, finalPeriodo)).intValue() + 1;
        datas.add(inicioPeriodo);

        for (int i = 1; i <= meses; i++) {
            LocalDate data = inicioPeriodo.plusMonths(i);
            if (data.withDayOfMonth(1).isBefore(finalPeriodo.withDayOfMonth(1)) || (data.getYear() == finalPeriodo.getYear() && data.getMonthValue() == finalPeriodo.getMonthValue())) {
                datas.add(data);
            }
        }
        return datas;
    }

    public Double diasDeAfastamento(EntidadePagavelRH ep, Integer mes, Integer ano, Boolean considerarCarencia, String codigos) {
        String arrayString[] = codigos.split(",");

        LocalDate inicioMes = DataUtil.criarDataPrimeiroDiaMes(mes, ano);
        LocalDate finalMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        String sql = " select distinct afastamento.* " +
            " from Afastamento afastamento " +
            "         inner join tipoAfastamento tipo on afastamento.tipoafastamento_id = tipo.id " +
            " where afastamento.contratofp_id = :ep " +
            "  and (((:inicio between afastamento.inicio and coalesce(afastamento.termino, :inicio)) " +
            "    or (:termino between afastamento.inicio and coalesce(afastamento.termino, :termino)) " +
            "    or (afastamento.inicio between :inicio and :termino) " +
            "    or (afastamento.termino between :inicio and :termino)) or " +
            "       (afastamento.inicio < :termino and afastamento.retornoInformado = 0)) " +
            "    and tipo.codigo in (:listaCodigos)";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("listaCodigos", Arrays.asList(arrayString));
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("inicio", DataUtil.localDateToDate(inicioMes));
        q.setParameter("termino", DataUtil.localDateToDate(finalMes));

        List<Afastamento> afastamentos = q.getResultList();
        Integer dias = 0;
        Integer diasDoMes = inicioMes.lengthOfMonth();

        if (afastamentos != null) {
            for (Afastamento f : afastamentos) {
                LocalDate inicioAfastamento = DataUtil.dateToLocalDate(f.getInicio());
                LocalDate terminoAfastamento = DataUtil.dateToLocalDate(f.getTermino());

                if (considerarCarencia && f.getCarencia() != null) {
                    inicioAfastamento = inicioAfastamento.plusDays(f.getCarencia());
                    if (inicioAfastamento.isAfter(terminoAfastamento)) {
                        continue;
                    }
                }

                if (inicioAfastamento.isAfter(finalMes) || (f.isRetornoInformado() && terminoAfastamento.isBefore(inicioMes))) {
                    continue;
                }

                if (inicioAfastamento.isBefore(inicioMes)) {
                    inicioAfastamento = inicioMes;
                }

                if (terminoAfastamento.isAfter(finalMes) || !f.isRetornoInformado()) {
                    terminoAfastamento = finalMes;
                }

                dias += DataUtil.diasEntre(inicioAfastamento, terminoAfastamento);
            }
        }

        if (dias > diasDoMes) {
            return diasDoMes.doubleValue();
        }
        return dias.doubleValue();
    }

    public Double diasDeAfastamentoAteCompAtual(EntidadePagavelRH ep, Integer mes, Integer ano, String codigo, Boolean considerarCarencia) {

        LocalDate dataReferencia = DataUtil.criarDataPrimeiroDiaMes(mes, ano);
        dataReferencia = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());

        String sql = " select afastamento.* " +
            "from afastamento " +
            "         inner join tipoAfastamento tipo on afastamento.tipoafastamento_id = tipo.id " +
            " where afastamento.contratofp_id = :ep " +
            " and tipo.codigo = :codigo" +
            "  and ( " +
            "    to_date(to_char(to_date(:dataReferencia, 'dd/MM/yyyy'), 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(afastamento.inicio, 'mm/yyyy'), 'mm/yyyy') " +
            "        and to_date(to_char(coalesce(afastamento.termino, to_date(:dataReferencia, 'dd/MM/yyyy')), 'mm/yyyy'), 'mm/yyyy') or " +
            "    (afastamento.inicio <= to_date(:dataReferencia, 'dd/MM/yyyy') and afastamento.retornoinformado = 0)) ";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("codigo", codigo);
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));

        List<Afastamento> afastamentos = q.getResultList();
        Integer dias = 0;

        if (afastamentos != null) {
            Map<Long, Afastamento> mapAfastamentos = new HashMap<>();
            afastamentos.forEach(afastamento -> {
                mapAfastamentos.put(afastamento.getId(), afastamento);
            });

            for (Afastamento f : mapAfastamentos.values()) {
                Integer carencia = 0;
                LocalDate inicio = DataUtil.dateToLocalDate(f.getInicio());
                LocalDate termino = DataUtil.dateToLocalDate(f.getTermino());
                Afastamento primeiroAfastamentoProrrogacao = buscarPrimeiroAfastamentoProrrogacao(f, Lists.newArrayList(mapAfastamentos.keySet()));
                if (primeiroAfastamentoProrrogacao != null) {
                    inicio = DataUtil.dateToLocalDate(primeiroAfastamentoProrrogacao.getInicio());
                    if (considerarCarencia && primeiroAfastamentoProrrogacao.getCarencia() != null) {
                        carencia = primeiroAfastamentoProrrogacao.getCarencia();
                    }
                }

                if (considerarCarencia && f.getCarencia() != null) {
                    carencia += f.getCarencia();
                }

                if (carencia > 0) {
                    inicio = inicio.plusDays(carencia);
                    if (inicio.isAfter(dataReferencia)) {
                        continue;
                    }
                }

                if (dataReferencia.isBefore(termino) ||
                    ((primeiroAfastamentoProrrogacao != null && !primeiroAfastamentoProrrogacao.isRetornoInformado())) || !f.isRetornoInformado()) {
                    termino = dataReferencia;
                }
                dias += DataUtil.diasEntre(inicio, termino);

                int intervaloDias = f.getTipoAfastamento().getIntervaloProrrogacaoDias();
                Date dataIntervalo = DataUtil.removerDias(f.getInicio(), intervaloDias);
                List<Afastamento> afastamentosAnteriores = AfastamentosAnteriores(ep.getIdCalculo(), dataIntervalo, f.getInicio(), codigo);
                for (Afastamento afastamento : afastamentosAnteriores) {
                    if (!afastamento.equals(primeiroAfastamentoProrrogacao)) {
                        dias += DataUtil.diasEntreDate(afastamento.getInicio(), afastamento.getTermino());
                    }
                }
            }
        }
        return dias.doubleValue();
    }

    public List<Afastamento> AfastamentosAnteriores(Long IdCalculo, Date dataIntervalo, Date inicioAfastamentoReferencia, String codigo){

        String sql = "  select afastamento.* from afastamento  " +
            " inner join tipoAfastamento tipo on afastamento.tipoafastamento_id = tipo.id  " +
            " where afastamento.contratofp_id = :ep " +
            " and tipo.codigo = :codigo" +
            " and afastamento.termino >= :dataIntervalo and afastamento.inicio < :inicioAfastamentoReferencia order by afastamento.termino desc ";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("codigo", codigo);
        q.setParameter("ep", IdCalculo);
        q.setParameter("dataIntervalo", dataIntervalo);
        q.setParameter("inicioAfastamentoReferencia", inicioAfastamentoReferencia);
        return q.getResultList();
    }

    public Afastamento buscarPrimeiroAfastamentoProrrogacao(Afastamento afastamentoReferencia, List<Long> idAfastamentosNaoRecuperar) {

        String sql = " select afastamento.* " +
            " from afastamento " +
            "         inner join tipoafastamento tipo on afastamento.tipoafastamento_id = tipo.ID " +
            " where tipo.codigo = :tipoAfastamento " +
            "  and afastamento.contratofp_id = :ep " +
            "  and trunc(afastamento.termino) = to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "  and afastamento.id not in (:idsNaoRecuperar) ";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("ep", afastamentoReferencia.getContratoFP().getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(DataUtil.dateToLocalDate(afastamentoReferencia.getInicio()).minusDays(1)));
        q.setParameter("tipoAfastamento", afastamentoReferencia.getTipoAfastamento().getCodigo());
        q.setParameter("idsNaoRecuperar", idAfastamentosNaoRecuperar);

        List<Afastamento> afastamentos = q.getResultList();
        Afastamento afastamento = null;

        if (afastamentos != null) {
            for (Afastamento f : afastamentos) {
                Afastamento afastamentoProrrogacao = buscarPrimeiroAfastamentoProrrogacao(f, Lists.newArrayList(f.getId()));
                if (afastamentoProrrogacao != null) {
                    afastamento = afastamentoProrrogacao;
                } else {
                    afastamento = f;
                }
            }
        }
        return afastamento;
    }

    public Double totalHorasExtras(EntidadePagavelRH ep, Integer ano, Integer mes, TipoHoraExtra tipoHoraExtra, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(totalHoras)");
        sql.append("  from HoraExtra");
        sql.append(" where contratoFP.id = :contrato");
        sql.append("   and ano = :ano");
        sql.append("   and mes = :mes");
        sql.append("   and tipoHoraExtra = :tipoHoraExtra and dataCadastro <= :data");
        Query q = em.createQuery(sql.toString());
        LocalDate dt = getDataReferencia(ep);
        q.setParameter("ano", dt.getYear());
        q.setParameter("mes", dt.getMonthValue());
        q.setParameter("data", detalheProcessamentoFolha.getItemCalendarioFP().getDataUltimoDiaProcessamento());
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("tipoHoraExtra", tipoHoraExtra);
        Object result = q.getSingleResult();

        if (result == null) {
            return 0d;
        }

        Long d = (Long) result;
        return d.doubleValue();
    }

    public boolean isentoDePrevidencia(EntidadePagavelRH ep, Date data) {
        if (ep instanceof Aposentadoria) {
            Query q = em.createQuery("select invalidez from Aposentadoria apo inner join apo.invalidezAposentados invalidez " +
                " where apo.id = :ep and :data between  invalidez.inicioVigencia  and coalesce(invalidez.finalVigencia,:data) and " +
                " :data between apo.inicioVigencia and coalesce(apo.finalVigencia,:data) ");
            q.setParameter("ep", ((Aposentadoria) ep).getId());
            q.setParameter("data", localDateToDate(getDataReferencia(ep)));
            q.setMaxResults(1);
            if (q.getResultList().isEmpty()) {
                return false;
            }
            InvalidezAposentado invalidezAposentado = (InvalidezAposentado) q.getResultList().get(0);
            return invalidezAposentado.getIsentoPrevidencia() != null ? invalidezAposentado.getIsentoPrevidencia() : false;
        }
        if (ep instanceof ContratoFP) {
            String tipo = obterTipoPrevidenciaFP(ep, ep.getMes(), ep.getAno());
            return TipoPrevidenciaFP.isIsento(tipo);
        }
        if (ep instanceof Pensionista) {
            Query q = em.createQuery("select invalidez from Pensionista pen inner join pen.listaInvalidezPensao invalidez " +
                " where pen.id = :ep and :data between  invalidez.inicioVigencia  and coalesce(invalidez.finalVigencia,:data) and " +
                " :data between pen.inicioVigencia and coalesce(pen.finalVigencia,:data) ");
            q.setParameter("ep", ((Pensionista) ep).getId());
            q.setParameter("data", localDateToDate(getDataReferencia(ep)));
            q.setMaxResults(1);
            if (q.getResultList().isEmpty()) {
                return false;
            }
            InvalidezPensao invalidezPensionista = (InvalidezPensao) q.getResultList().get(0);
            return invalidezPensionista.getIsentoPrevidencia();
        }
        return false;
    }

    public boolean invalidezPensionsita(EntidadePagavelRH ep) {
        if (ep instanceof Pensionista) {
            Query q = em.createQuery("select invalidez from Pensionista pen inner join pen.listaInvalidezPensao invalidez " +
                " where pen.id = :ep and :data between  invalidez.inicioVigencia  and coalesce(invalidez.finalVigencia,:data) and " +
                " :data between pen.inicioVigencia and coalesce(pen.finalVigencia,:data) ");
            q.setParameter("ep", ((Pensionista) ep).getId());
            q.setParameter("data", localDateToDate(getDataReferencia(ep)));
            q.setMaxResults(1);
            if (!q.getResultList().isEmpty()) {
                return true;
            }
//            InvalidezPensao invalidezAposentado = (InvalidezPensao) q.getResultList().get(0);
//            return invalidezAposentado.getIsentoIRRF() != null ? invalidezAposentado.getIsentoPrevidencia() : false;
        }
        return false;
    }

    public boolean isentoDeIrrf(EntidadePagavelRH ep) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        if (ep instanceof ContratoFP) {
            return ((ContratoFP) ep).getIsentoIR();
        }
        if (ep instanceof Aposentadoria) {
            Query q = em.createQuery("select invalidez from Aposentadoria apo inner join apo.invalidezAposentados invalidez " +
                " where apo.id = :ep and :data between  invalidez.inicioVigencia  and coalesce(invalidez.finalVigencia,:data) and " +
                " :data between apo.inicioVigencia and coalesce(apo.finalVigencia,:data) ");
            q.setParameter("ep", ((Aposentadoria) ep).getId());
            q.setParameter("data", localDateToDate(getDataReferencia(ep)));
            q.setMaxResults(1);
            if (q.getResultList().isEmpty()) {
                return false;
            }
            InvalidezAposentado invalidezAposentado = (InvalidezAposentado) q.getResultList().get(0);
            return invalidezAposentado.getIsentoIRRF() != null ? invalidezAposentado.getIsentoIRRF() : false;
        }
        if (ep instanceof Pensionista) {
            Query q = em.createQuery("select invalidez from Pensionista pen inner join pen.listaInvalidezPensao invalidez " +
                " where pen.id = :ep and :data between  invalidez.inicioVigencia  and coalesce(invalidez.finalVigencia,:data) and " +
                " :data between pen.inicioVigencia and coalesce(pen.finalVigencia,:data) ");
            q.setParameter("ep", ((Pensionista) ep).getId());
            q.setParameter("data", localDateToDate(getDataReferencia(ep)));
            q.setMaxResults(1);
            if (q.getResultList().isEmpty()) {
                return false;
            }
            InvalidezPensao invalidezPensao = (InvalidezPensao) q.getResultList().get(0);
            return invalidezPensao.getIsentoIRRF() != null ? invalidezPensao.getIsentoIRRF() : false;
        }
        return false;
    }

    public boolean aposentadoInvalido(EntidadePagavelRH ep) {
        if (ep instanceof Aposentadoria) {
            Query q = em.createQuery("select invalidez from Aposentadoria apo inner join apo.invalidezAposentados invalidez " +
                " where apo.id = :ep and :data between  invalidez.inicioVigencia  and coalesce(invalidez.finalVigencia,:data) and " +
                " :data between apo.inicioVigencia and coalesce(apo.finalVigencia,:data) ");
            q.setParameter("ep", ((Aposentadoria) ep).getId());
            q.setParameter("data", localDateToDate(getDataReferencia(ep)));
            q.setMaxResults(1);
            return !q.getResultList().isEmpty();
        }
        return false;
    }

    @DescricaoMetodo("obterCargo(codigo)")
    public boolean obterCargo(EntidadePagavelRH ep, String codigo) {
        if ((ep instanceof Aposentadoria)) {
            Aposentadoria apo = (Aposentadoria) ep;
            return apo.getContratoFP().getCargo() != null && apo.getContratoFP().getCargo().getCodigoDoCargo().equals(codigo.trim());
        }
        if ((ep instanceof Pensionista)) {
            Pensionista pen = (Pensionista) ep;
            return pen.getPensao().getContratoFP().getCargo() != null && pen.getPensao().getContratoFP().getCargo().getCodigoDoCargo().equals(codigo.trim());
        }
        if ((ep instanceof Beneficiario)) {
            Beneficiario ben = (Beneficiario) ep;
            return false;
//            return ben.getContratoFP().getCargo() != null && ben.getContratoFP().getCargo().getCodigoDoCargo().equals(codigo.trim());
        }
        if (ep instanceof ContratoFP) {
            ContratoFP contrato = (ContratoFP) ep;
            if (contrato.getCargo() != null) {
                return contrato.getCargo().getCodigoDoCargo().trim().equals(codigo.trim());
            }
        }
        return false;
    }

    @DescricaoMetodo("obterCargoComissao(codigo)")
    public String obterCargoComissao(EntidadePagavelRH ep) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select cc.cargo from CargoConfianca cc where cc.contratoFP.id = :id");
        sb.append(" and :dataVigencia >= cc.inicioVigencia ");
        sb.append(" and :dataVigencia <= coalesce(cc.finalVigencia,:dataVigencia)");
        Query q = em.createQuery(sb.toString());
        q.setParameter("id", ep.getIdCalculo());
        Calendar dataAtual = Calendar.getInstance();
        dataAtual.setTime(new Date());
        dataAtual.set(Calendar.YEAR, ep.getAno());
        dataAtual.set(Calendar.MONTH, ep.getMes());
        q.setParameter("dataVigencia", dataAtual.getTime());

        if (q.getResultList().isEmpty()) {
            return "";
        }
        if (q.getResultList().size() > 1) {
            throw new FuncoesFolhaFacadeException("Foram encontrados mais de um Cargo Confiança vigente para o mesmo servidor.", null);
        }
        Cargo c = (Cargo) q.getResultList().get(0);
        return c.getCodigoDoCargo();
    }

    public TipoPensao recuperaTipoPensao(EntidadePagavelRH ep) {
        StringBuilder builder = new StringBuilder();
        builder.append(" select pensionista.tipoPensao from Pensionista pensionista ");
        builder.append(" where pensionista.id = :parametroEP ");
        builder.append(" and :dataVigencia >= pensionista.inicioVigencia and :dataVigencia <= coalesce(pensionista.finalVigencia,:dataVigencia) ");
        Query q = em.createQuery(builder.toString());
        q.setParameter("parametroEP", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        try {
            TipoPensao tipo = (TipoPensao) q.getSingleResult();
            return tipo;
        } catch (NoResultException nr) {
            //System.out.println("Erro ao executar metodo recuperaTipoPensao, nenhum registro encontrado para a query." + nr);
            return null;
        }
    }

    public Double recuperaValorPensao(EntidadePagavelRH ep) {
        StringBuilder builder = new StringBuilder();
        builder.append(" select coalesce(pensionista.pensao.remuneracaoInstituidor,0) from Pensionista pensionista ");
        builder.append(" where pensionista.id = :parametroEP ");
        builder.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pensionista.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pensionista.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy')  ");
        Query q = em.createQuery(builder.toString());
        q.setParameter("parametroEP", ep.getIdCalculo());
        q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
        try {
            BigDecimal valor = (BigDecimal) q.getSingleResult();
            return valor.doubleValue();
        } catch (NoResultException nr) {
            logger.debug("Erro ao executar metodo recuperaValorPensao, nenhum registro encontrado para a query." + nr);
            return null;
        } catch (Exception nre) {
            nre.printStackTrace();
            logger.debug("Erro ao executar metodo recuperaValorPensao, nenhum registro encontrado para a query." + nre);
            return null;
        }
    }

    public Integer recuperaNumeroCotas(EntidadePagavelRH ep) {
        if (ep instanceof Pensionista) {
            StringBuilder builder = new StringBuilder();
            builder.append(" select count(pensionista.id) from Pensionista pensionista ");
            builder.append(" where pensionista.pensao.id = :parametroEP ");
            builder.append(" and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pensionista.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pensionista.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy')  ");
            Query q = em.createQuery(builder.toString());
            q.setParameter("parametroEP", ((Pensionista) ep).getPensao().getId());
            q.setParameter("dataVigencia", localDateToDate(getDataReferencia(ep)));
            try {
                Long valor = (Long) q.getSingleResult();
                return valor.intValue();
            } catch (NoResultException nr) {
                logger.debug("Erro ao executar metodo recuperaNumeroCotas, nenhum registro encontrado para a query." + nr);
                return null;
            }
        } else
            return 0;
    }

    public Integer recuperaMotivoExoneracao(EntidadePagavelRH ep) {
        StringBuilder builder = new StringBuilder();
        builder.append(" select exoneracao.motivoExoneracaoRescisao.codigo from ExoneracaoRescisao exoneracao  ");
        builder.append(" where exoneracao.vinculoFP.id = :parametroEP ");
        Query q = em.createQuery(builder.toString());
        q.setParameter("parametroEP", ep.getIdCalculo());
        try {
            Integer valor = (Integer) q.getSingleResult();
            return valor;
        } catch (NoResultException nr) {
            throw new FuncoesFolhaFacadeException("Não foi possível encontrar motivo da exoneração para " + ep, nr);
        } catch (RuntimeException nr) {
            throw new FuncoesFolhaFacadeException("Erro inesperado ao executar recuperaMotivoExoneracao" + ep, nr);
        }
    }

    public Long recuperaCategoriaSefip(EntidadePagavelRH ep) {
        StringBuilder builder = new StringBuilder();
        builder.append(" select vinculo.categoriaSEFIP.codigo from VinculoFP vinculo ");
        builder.append(" where vinculo.id = :parametroEP ");
        Query q = em.createQuery(builder.toString());
        q.setParameter("parametroEP", ep.getIdCalculo());
        try {
            Long valor = (Long) q.getSingleResult();
            return valor;
        } catch (NoResultException nr) {
            throw new FuncoesFolhaFacadeException("Não foi possível encontrar a categoria SEFIP para " + ep, nr);
        } catch (RuntimeException nr) {
            throw new FuncoesFolhaFacadeException("Erro inesperado ao executar recuperaCategoriaSefip" + ep, nr);
        }
    }

    public Integer recuperaCategoriaeSocial(EntidadePagavelRH ep) {
        String sql = "";
        if (ep instanceof PrestadorServicos) {
            sql = "select cat.codigo " +
                " from PrestadorServicos prest  " +
                "       inner join prest.categoriaTrabalhador cat  " +
                " where prest.id = :parametroEP";
        } else {
            sql = "select cat.codigo " +
                "from VinculoFP v " +
                "         inner join v.categoriaTrabalhador cat " +
                "where v.id = :parametroEP";
        }
        Query q = em.createQuery(sql);
        q.setParameter("parametroEP", ep.getId());
        try {
            if (!q.getResultList().isEmpty()) {
                Integer valor = (Integer) q.getSingleResult();
                return valor;
            }
            return null;
        } catch (NoResultException nr) {
            throw new FuncoesFolhaFacadeException("Não foi possível encontrar a categoria do e-social para " + ep, nr);
        } catch (RuntimeException nr) {
            throw new FuncoesFolhaFacadeException("Erro inesperado ao executar recuperaCategoriaeSocial" + ep, nr);
        }
    }

    public Double totalHorasExtrasAnual(EntidadePagavelRH ep, Integer ano, Integer mes, String[] argumentos) {
        List<EventoFP> eventos = new ArrayList<>();
        for (String s : argumentos) {
            Query q = em.createQuery("from EventoFP where codigo = :codigo");
            q.setParameter("codigo", s);
            List<EventoFP> events = q.getResultList();

            if (events != null) {
                eventos.addAll(events);
            }
        }

        StringBuilder query = new StringBuilder();
        query.append("select sum(item.valorReferencia) from ItemFichaFinanceiraFP item inner join item.fichaFinanceiraFP ficha inner join ficha.folhaDePagamento folha");
        query.append(" inner join item.eventoFP evento ");
        query.append(" where evento in (:eventos) and folha.ano = :ano and ficha.vinculoFP.id = :contrato ");
        query.append(" and folha.efetivadaEm is not null ");

        Query q = em.createQuery(query.toString());
        q.setParameter("ano", getAnoCalculoHorasExtras(ep));
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("eventos", eventos);

        Object result = q.getSingleResult();

        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }

    private Integer getAnoCalculoHorasExtras(EntidadePagavelRH ep) {
        VinculoFP v = (VinculoFP) ep;
        Calendar c = Calendar.getInstance();
        if (v.getFinalVigencia() != null) {
            c.setTime(v.getFinalVigencia());
            return c.get(Calendar.YEAR);
        } else {
            return ep.getAno();
        }
    }

    public Double totalDiasDireitoProporcional(EntidadePagavelRH ep) {
        if (true) {
//            return verificarEPegarMesesEmPeriodoAquisitivoSemConcessao((VinculoFP) ep);
            return totalMesesIndenizacaoFerias((VinculoFP) ep);
        }


        double saldo = 0;
        StringBuilder query = new StringBuilder();
        query.append(" select periodo from PeriodoAquisitivoFL periodo ");
        query.append(" where periodo.contratoFP.id = :contrato and periodo.baseCargo.basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipoPeriodo ");
        query.append(" and (periodo.status = :parcial or periodo.status = :nao_concedido) order by periodo.finalVigencia desc ");

        Query q = em.createQuery(query.toString());
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("parcial", StatusPeriodoAquisitivo.PARCIAL);
        q.setParameter("nao_concedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO);
        q.setParameter("tipoPeriodo", TipoPeriodoAquisitivo.FERIAS);
        if (!q.getResultList().isEmpty()) {

            PeriodoAquisitivoFL periodo = (PeriodoAquisitivoFL) q.getResultList().get(0);

//            for (PeriodoAquisitivoFL periodo : periodos) {
            Date finalVigencia = ((VinculoFP) ep).getFinalVigencia();
            LocalDate inicio;
            if (finalVigencia != null && periodo.getFinalVigencia().before(finalVigencia)) {
                inicio = dateToLocalDate(periodo.getFinalVigencia()).plusDays(1);
            } else {
                inicio = dateToLocalDate(periodo.getInicioVigencia());
            }
            LocalDate fim = dateToLocalDate(((VinculoFP) ep).getFinalVigencia() != null ? ((VinculoFP) ep).getFinalVigencia() : periodo.getFinalVigencia());
            while (inicio.isBefore(fim)) {
                Integer days = daysBetween(inicio, fim);
                if (days >= 15) {
                    inicio = inicio.plusMonths(1);
                    ++saldo;
                } else {
                    break;
                }
            }

//                if (periodo.getSaldoDeDireito() <= periodo.getBaseCargo().getBasePeriodoAquisitivo().getSaldoDeDireito()) {
//                saldo += Months.monthsBetween(new LocalDate(periodo.getInicioVigencia()), ).getMonths();
//                }
//            }
        }

        return saldo;
    }

    public List<PeriodoAquisitivoFL> getPeriodosAquisitivosNaoConcedidoEParcial(EntidadePagavelRH ep) {
        StringBuilder query = new StringBuilder();
        query.append(" select periodo from PeriodoAquisitivoFL periodo ");
        query.append(" where periodo.contratoFP.id = :contrato and periodo.baseCargo.basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipoPeriodo ");
        //query.append(" and periodo in (select c.periodoAquisitivoFL from ConcessaoFeriasLicenca c) ");
        query.append(" and (periodo.status = :parcial or periodo.status = :nao_concedido) order by periodo.finalVigencia desc ");

        Query q = em.createQuery(query.toString());
        q.setParameter("contrato", ep.getIdCalculo());
        q.setParameter("parcial", StatusPeriodoAquisitivo.PARCIAL);
        q.setParameter("nao_concedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO);
        q.setParameter("tipoPeriodo", TipoPeriodoAquisitivo.FERIAS);
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Double totalMesesIndenizacaoFerias(EntidadePagavelRH ep) {
        VinculoFP vinculo = (VinculoFP) ep;
        double saldo = 0;
        saldo += verificarEPegarMesesEmPeriodoAquisitivoSemConcessao(vinculo);

        List<PeriodoAquisitivoFL> periodos = getPeriodosAquisitivosNaoConcedidoEParcial(ep);
        if (periodos != null && !periodos.isEmpty()) {

            //List<PeriodoAquisitivoFL> periodos = q.getResultList();

            for (PeriodoAquisitivoFL periodo : periodos) {

                LocalDate inicio = dateToLocalDate(periodo.getInicioVigencia());
                LocalDate inicioPeriodo = dateToLocalDate(periodo.getInicioVigencia());
                LocalDate fim = ((VinculoFP) ep).getFinalVigencia() != null && dateToLocalDate(((VinculoFP) ep).getFinalVigencia()).isBefore(dateToLocalDate(periodo.getFinalVigencia())) ? dateToLocalDate(((VinculoFP) ep).getFinalVigencia()) : dateToLocalDate(periodo.getFinalVigencia());
                List<Afastamento> afastamentos = afastamentoFacade.recuperaAfastamentosDescontosTempoEfetivoNaVigencia(vinculo, inicio, fim);

//                if (finalVigencia != null && periodo.getFinalVigencia().before(finalVigencia)) {
//                    fim = new LocalDate(periodo.getFinalVigencia());
//                } else {
//                    fim = new LocalDate(periodo.getFinalVigencia());
//                }
//                fim = new LocalDate(((VinculoFP) ep).getFinalVigencia() != null ? ((VinculoFP) ep).getFinalVigencia() : periodo.getFinalVigencia());
//                fim = new LocalDate(periodo.getFinalVigencia());
                List<LocalDate> datas = getDatasEmLista(inicioPeriodo, fim);
                int acumulado = 0;
                for (LocalDate refe : datas) {
                    int dias = diasEntreDatas(refe, inicioPeriodo, fim);
                    int diasAfastados = 0;
                    for (Afastamento afastamento : afastamentos) {
                        diasAfastados += getDiasAfastados(inicio, afastamento, fim);
                    }
                    inicio = inicio.plusMonths(1);
                    if (dias >= 15) {
                        ++saldo;
                    } else {
                        acumulado += dias;
                    }
                    if (acumulado >= 15) {
                        saldo++;
                    }
                }

//                if (periodo.getSaldoDeDireito() <= periodo.getBaseCargo().getBasePeriodoAquisitivo().getSaldoDeDireito()) {
//                saldo += Months.monthsBetween(new LocalDate(periodo.getInicioVigencia()), ).getMonths();
//                }
            }
        }

        return saldo;
    }

    public PeriodoAquisitivoFL getUltimoPeriodoAquitivo(EntidadePagavelRH ep) {
        StringBuilder query = new StringBuilder();
        query.append(" select periodo from PeriodoAquisitivoFL periodo ");
        query.append(" where periodo.contratoFP.id = :contrato and periodo.baseCargo.basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipoPeriodo ");
//        query.append(" and periodo.status = :concedido order by periodo.inicioVigencia desc ");
        query.append(" order by periodo.inicioVigencia desc ");
        Query q = em.createQuery(query.toString());
        q.setParameter("contrato", ep.getIdCalculo());
//        q.setParameter("concedido", StatusPeriodoAquisitivo.CONCEDIDO);
        q.setParameter("tipoPeriodo", TipoPeriodoAquisitivo.FERIAS);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (PeriodoAquisitivoFL) q.getResultList().get(0);
    }

    public double verificarEPegarMesesEmPeriodoAquisitivoSemConcessao(EntidadePagavelRH ep) {
        VinculoFP vinculo = (VinculoFP) ep;
        double saldo = 0;
        PeriodoAquisitivoFL p = getUltimoPeriodoAquitivo(vinculo);
        if (p == null) {
            return 0;
        }
        LocalDate inicio = dateToLocalDate(p.getFinalVigencia()).plusDays(1);
        LocalDate inicioPeriodo = dateToLocalDate(p.getFinalVigencia()).plusDays(1);
        LocalDate fim = vinculo.getFinalVigencia() != null ? dateToLocalDate(vinculo.getFinalVigencia()) : getDataReferencia(vinculo);
        List<Afastamento> afastamentos = afastamentoFacade.recuperaAfastamentosDescontosTempoEfetivoNaVigencia(vinculo, inicio, fim);
        //TODO calcular as datas do periodo e dentro de cada mes verificar se tem afastamento para descontar
        int meses = 0;
        while (inicio.isBefore(fim)) {
            int dias = diasEntreDatas(inicio, inicioPeriodo, fim);
            int diasAfastados = 0;
            for (Afastamento afastamento : afastamentos) {
                diasAfastados += getDiasAfastados(inicio, afastamento, fim);
            }
            dias = dias - diasAfastados;
            inicio = inicio.plusMonths(1);
            if (dias >= 15) {
                ++saldo;
                meses++;
                if (meses == 12) {
                    logger.debug("12 meses");
                }
            }
            //else {
//                break;
//            }
        }
        logger.debug("total de meses: " + saldo);
        return saldo;
    }

    private int getDiasAfastados(LocalDate inicio, Afastamento afastamento, LocalDate dataFimAfastamentoSemRetorno) {
        LocalDate inicioAfastamento = dateToLocalDate(afastamento.getInicio());
        if (afastamento.getCarencia() != null) {
            inicioAfastamento = inicioAfastamento.plusDays(afastamento.getCarencia());
        }
        Boolean retornoInformado = afastamento.getRetornoInformado();
        LocalDate terminoAfastamento = retornoInformado ? dateToLocalDate(afastamento.getTermino()) : dataFimAfastamentoSemRetorno;
        return calculaDiasAfastados(inicio, inicioAfastamento, terminoAfastamento);
    }

    private int getDiasDeFaltas(LocalDate inicio, VwFalta afastamento) {
        LocalDate inicioAfastamento = dateToLocalDate(afastamento.getInicio());
        LocalDate terminoAfastamento = dateToLocalDate(afastamento.getFim());
        return calculaDiasAfastados(inicio, inicioAfastamento, terminoAfastamento);
    }

    private java.time.Period getDiasAfastadosPeriodo(LocalDate inicio, Afastamento afastamento) {
        LocalDate inicioAfastamento = dateToLocalDate(afastamento.getInicio());
        LocalDate terminoAfastamento = dateToLocalDate(afastamento.getTermino());
        return calculaPeriodoDiasAfastados(inicio, inicioAfastamento, terminoAfastamento);
    }

    private int getDiasAfastadosCompleto(LocalDate inicio, Afastamento afastamento) {
        LocalDate inicioAfastamento = dateToLocalDate(afastamento.getInicio());
        LocalDate terminoAfastamento = dateToLocalDate(afastamento.getTermino());
        return calculaDiasAfastadosCompleto(inicio, inicioAfastamento, terminoAfastamento);
    }

    private int calculaDiasAfastados(LocalDate inicio, LocalDate inicioAfastamento, LocalDate terminoAfastamento) {
        int diasAfastados = 0;

        LocalDate start = inicioAfastamento.withDayOfMonth(1);
        LocalDate end = terminoAfastamento.withDayOfMonth(terminoAfastamento.lengthOfMonth());
        if (start.isAfter(end)) {
            return 0;
        }

        if (isWithinRange(inicio, start, end)) {
            return diasEntreDatas(inicio, inicioAfastamento, terminoAfastamento);
        }
        return 0;
    }

    private java.time.Period calculaPeriodoDiasAfastados(LocalDate inicio, LocalDate inicioAfastamento, LocalDate terminoAfastamento) {
        if (isWithinRange(inicio, inicioAfastamento.withDayOfMonth(1), terminoAfastamento.withDayOfMonth(terminoAfastamento.lengthOfMonth()))) {
            return diasEntreDatasPeriod(inicio, inicioAfastamento, terminoAfastamento);
        }
        return null;
    }

    private int calculaDiasAfastadosCompleto(LocalDate inicio, LocalDate inicioAfastamento, LocalDate terminoAfastamento) {
        int diasAfastados = 0;

        if (isWithinRange(inicio, inicioAfastamento.withDayOfMonth(1), terminoAfastamento.withDayOfMonth(terminoAfastamento.lengthOfMonth()))) {
            return diasEntreDatasCompleto(inicio, inicioAfastamento, terminoAfastamento);
        }
        return 0;
    }

    public int diasEntreDatas(LocalDate refe, LocalDate inicio, LocalDate fim) {
        int dias = 0;
        boolean estaNoInicioDoAfastamento = refe.getMonthValue() == inicio.getMonthValue() && refe.getYear() == inicio.getYear();
        boolean estaNoTerminoDoAfastamento = refe.getMonthValue() == fim.getMonthValue() && refe.getYear() == fim.getYear();
        if (estaNoInicioDoAfastamento || estaNoTerminoDoAfastamento) {
            if (estaNoInicioDoAfastamento) {
                dias = diasEntre(inicio, inicio.withDayOfMonth(inicio.lengthOfMonth()));
            }
            if (estaNoTerminoDoAfastamento) {
                dias = diasEntre(fim.withDayOfMonth(1), fim);
            }
            if (estaNoInicioDoAfastamento && estaNoTerminoDoAfastamento) {
                dias = diasEntre(inicio, fim);
            }
        } else {
            dias = diasEntre(refe.withDayOfMonth(1), refe.withDayOfMonth(refe.lengthOfMonth()));
        }
        return dias;
    }

    public java.time.Period diasEntreDatasPeriod(LocalDate refe, LocalDate inicio, LocalDate fim) {
        java.time.Period p = null;
        boolean estaNoInicioDoAfastamento = refe.getMonthValue() == inicio.getMonthValue() && refe.getYear() == inicio.getYear();
        boolean estaNoTerminoDoAfastamento = refe.getMonthValue() == fim.getMonthValue() && refe.getYear() == fim.getYear();
        if (estaNoInicioDoAfastamento || estaNoTerminoDoAfastamento) {
            if (estaNoInicioDoAfastamento) {
                p = java.time.Period.between(inicio, inicio.withDayOfMonth(inicio.lengthOfMonth()));
            }
            if (estaNoTerminoDoAfastamento) {

                p = java.time.Period.between(fim.withDayOfMonth(1), fim);
            }
            if (estaNoInicioDoAfastamento && estaNoTerminoDoAfastamento) {

                p = java.time.Period.between(inicio, fim);
            }
        } else {
            p = java.time.Period.between(inicio, refe.withDayOfMonth(refe.lengthOfMonth()));
        }
        return p;
    }

    public int diasEntreDatasCompleto(LocalDate refe, LocalDate inicio, LocalDate fim) {
        int dias = 0;
        boolean estaNoInicioDoAfastamento = refe.getMonthValue() == inicio.getMonthValue() && refe.getYear() == inicio.getYear();
        boolean estaNoTerminoDoAfastamento = refe.getMonthValue() == fim.getMonthValue() && refe.getYear() == fim.getYear();
        if (estaNoInicioDoAfastamento || estaNoTerminoDoAfastamento) {
            if (estaNoInicioDoAfastamento) {
                dias = diasEntre(inicio, refe);
            }
            if (estaNoTerminoDoAfastamento) {
                dias = diasEntre(refe, fim);
            }
            if (estaNoInicioDoAfastamento && estaNoTerminoDoAfastamento) {
                dias = diasEntre(inicio, fim);
            }
        } else {
            dias = diasEntre(inicio, refe);
        }
        logger.debug("dias na refe: " + refe + " dias : " + dias);
        return dias;
    }

    public Integer diasEntre(LocalDate begin, LocalDate end) {
        return DataUtil.daysBetween(begin, end) + 1;
    }

    private boolean temPeriodoAquisitivoFeriasNaExoneracao(VinculoFP vinculoFP) {
        StringBuilder query = new StringBuilder();
        query.append(" select periodo from PeriodoAquisitivoFL periodo ");
        query.append(" where periodo.contratoFP.id = :contrato and periodo.baseCargo.basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipoPeriodo ");
        query.append(" and :finalVigencia between periodo.inicioVigencia and perido.finalVigencia ");

        Query q = em.createQuery(query.toString());
        q.setParameter("contrato", vinculoFP.getIdCalculo());
        q.setParameter("finalVigencia", dateToLocalDate(vinculoFP.getFinalVigencia()).minusDays(1));
        q.setParameter("tipoPeriodo", TipoPeriodoAquisitivo.FERIAS);
        return !q.getResultList().isEmpty();
    }

    public BigDecimal valorTotalFaltasDescontadas(EntidadePagavelRH ep, String codigoEvento, FolhaDePagamentoNovoCalculador calculador) {
        String hql = "select justificativa from JustificativaFaltas justificativa where justificativa.faltas.contratoFP.id = :ep and extract(month from justificativa.dataParaCalculo) = :mes and extract(year  from justificativa.dataParaCalculo) = :ano";
        Query q = em.createQuery(hql);
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("mes", ep.getMes());
        q.setParameter("ano", ep.getAno());
        BigDecimal totalRestituicao = BigDecimal.ZERO;
        for (JustificativaFaltas justificativa : (List<JustificativaFaltas>) q.getResultList()) {
            LocalDate dataFalta = dateToLocalDate(justificativa.getFaltas().getInicio());
            LocalDate dt = dateToLocalDate(justificativa.getInicioVigencia());
            LocalDate dtFim = dateToLocalDate(justificativa.getFinalVigencia());
            List<EventoTotalItemFicha> totalFichaEvento = fichaFinanceiraFPFacade.totalEventosItemFichaFinanceiraPorEvento(ep, dataFalta.getYear(), dataFalta.getMonthValue(), codigoEvento);

            EventoFP eventoFPFalta = eventoFPFacade.recuperaEvento(codigoEvento, TipoExecucaoEP.FOLHA);
            BigDecimal valorFaltaNoContexto = BigDecimal.ZERO;
            if (calculador.avaliaRegraDoEventoFP(eventoFPFalta)) {
                calculador.avaliaFormulaDoEventoFP(eventoFPFalta);
            }

            if (calculador.getEventoValorFormula().containsKey(eventoFPFalta)) {
                valorFaltaNoContexto = calculador.getEventoValorFormula().get(eventoFPFalta);
            }

            if (totalFichaEvento.isEmpty()) {
                EventoFP eventoFP = eventoFPFacade.recuperaEvento(codigoEvento, TipoExecucaoEP.FOLHA);
                BigDecimal referenciaFalta = calculador.avaliaReferenciaDoEventoFP(eventoFP);
                BigDecimal bigDecimal = calculador.avaliaFormulaDoEventoFP(eventoFP);
                totalFichaEvento.add(new EventoTotalItemFicha(eventoFP, bigDecimal, referenciaFalta));
            }

            if (!totalFichaEvento.isEmpty()) {
                EventoTotalItemFicha item = totalFichaEvento.get(0);
                BigDecimal base = item.getTotal();
                if (valorFaltaNoContexto.compareTo(base) > 0) {
                    totalRestituicao = totalRestituicao.add(valorFaltaNoContexto);
                } else {
                    if (item.getReferencia() != null && item.getReferencia().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal total = base.multiply(new BigDecimal(DataUtil.daysBetween(dt, dtFim) + 1)).divide(item.getReferencia(), 2, RoundingMode.HALF_UP);
                        totalRestituicao = totalRestituicao.add(total);
                    }
                }

            }
        }
        return totalRestituicao;
    }

    public BigDecimal totalFaltasDescontadas(EntidadePagavelRH ep, String codigoEvento) {
        String hql = "select justificativa from JustificativaFaltas justificativa where justificativa.faltas.contratoFP.id = :ep and extract(month from justificativa.dataParaCalculo) = :mes and extract(year  from justificativa.dataParaCalculo) = :ano";
        Query q = em.createQuery(hql);
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("mes", ep.getMes());
        q.setParameter("ano", ep.getAno());
        for (JustificativaFaltas faltas : (List<JustificativaFaltas>) q.getResultList()) {
            LocalDate dataFalta = dateToLocalDate(faltas.getFaltas().getInicio());
            LocalDate dt = dateToLocalDate(faltas.getInicioVigencia());
            LocalDate dtFim = dateToLocalDate(faltas.getFinalVigencia());
            if (dt.getMonthValue() == dtFim.getMonthValue()) {
                //System.out.println("meses iguais");
            }
            List<EventoTotalItemFicha> totalFichaEvento = fichaFinanceiraFPFacade.totalEventosItemFichaFinanceiraPorEvento(ep, dataFalta.getYear(), dataFalta.getMonthValue(), codigoEvento);
            if (!totalFichaEvento.isEmpty()) {
                BigDecimal valor = new BigDecimal(DataUtil.daysBetween(dt, dtFim) + 1);
                return valor;
            }
        }
        return new BigDecimal("0");
    }

    public Double totalFaltasRestituidas(EntidadePagavelRH ep) {
        Query q = em.createQuery("select coalesce(sum((justificativa.finalVigencia - justificativa.inicioVigencia)+1),0) from JustificativaFaltas justificativa where justificativa.faltas.contratoFP.id = :ep and extract(month from justificativa.dataParaCalculo) = :mes and extract(year  from justificativa.dataParaCalculo) = :ano");
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("mes", ep.getMes());
        q.setParameter("ano", ep.getAno());
        return (Double) q.getSingleResult();
    }

    public ValorPensaoAlimenticia recuperarValorBasePensaoAlimenticia(EntidadePagavelRH ep, String codigoEvento) {
        Query q = em.createQuery("select valor from BeneficioPensaoAlimenticia  bene inner join bene.valoresPensaoAlimenticia valor " +
            " where bene.pensaoAlimenticia.vinculoFP.id = :ep and bene.eventoFP.codigo  = :evento" +
            " and :data between valor.inicioVigencia and coalesce(valor.finalVigencia, :data)" +
            " and :data between bene.inicioVigencia and coalesce(bene.finalVigencia, :data)");

        q.setParameter("ep", ep.getId());
        q.setParameter("evento", codigoEvento);
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        ValorPensaoAlimenticia valorPensao = (ValorPensaoAlimenticia) q.getSingleResult();
        return valorPensao;
    }

    public boolean temSextaParte(EntidadePagavelRH ep) {
        Query q = em.createQuery("from SextaParte sexta where sexta.vinculoFP.id = :ep and sexta.temDireito = true and :data between sexta.inicioVigencia and coalesce(sexta.fimVigencia,:data)");
        VinculoFP v = (VinculoFP) ep;
        q.setParameter("ep", v.getId());
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        if (!q.getResultList().isEmpty()) return true;
        else return false;
    }

    public boolean temSextaParte(EntidadePagavelRH ep, String codigo) {
        Query q = em.createQuery("from SextaParte sexta where sexta.tipoSextaParte.eventoFP.codigo = :codigo and  sexta.vinculoFP.id = :ep and sexta.temDireito = true and :data between sexta.inicioVigencia and coalesce(sexta.fimVigencia,:data)");
        VinculoFP v = (VinculoFP) ep;
        q.setParameter("ep", v.getId());
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        q.setParameter("codigo", codigo);
        if (!q.getResultList().isEmpty()) return true;
        else return false;
    }

    public EventoTotalItemFicha recuperarValorFicha(EventoFP evento, EntidadePagavelRH ep, Mes mes, Integer ano) {
        String hql = "select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento,"
            + "                                                                            sum(case when itemFicha.valorIntegral > itemFicha.valor then itemFicha.valorIntegral else itemFicha.valor end),"
            + "                                                                            sum(itemFicha.valorReferencia))"
            + "        from FichaFinanceiraFP ficha"
            + "  inner join ficha.itemFichaFinanceiraFP itemFicha"
            + "  inner join itemFicha.eventoFP evento"
            + "  inner join ficha.vinculoFP vinculo"
            + "       where ficha.folhaDePagamento.ano = :ano "
            + "         and  ficha.folhaDePagamento.mes = :mes"
            + "         and  vinculo.id = :ep "
            + "         and evento.codigo = :codigo"
            + "         and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipoFolha"
            + "         and itemFicha.tipoEventoFP = :tipoVantagem"
            + "         and itemFicha.tipoCalculoFP = :tipoCalculo"
            + " group by evento";

        Query q = em.createQuery(hql.toString());

        q.setParameter("ano", ano);
        q.setParameter("codigo", evento.getCodigo());
        q.setParameter("mes", mes);
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL);
        q.setParameter("tipoCalculo", TipoCalculoFP.NORMAL);
        q.setParameter("ep", ep.getId());
        q.setParameter("tipoVantagem", TipoEventoFP.VANTAGEM);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (EventoTotalItemFicha) q.getSingleResult();
    }

    public EventoTotalItemFicha recuperarValorFichaByAnoAndEvento(String codigo, EntidadePagavelRH ep, Integer ano, TipoFolhaDePagamento tipo) {
        StringBuilder hql = new StringBuilder();
        hql.append("select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento, sum(itemFicha.valor), sum(itemFicha.valorReferencia)) ");
        hql.append(" from FichaFinanceiraFP ficha inner join ficha.itemFichaFinanceiraFP itemFicha");
        hql.append(" inner join itemFicha.eventoFP evento");

        hql.append(" inner join ficha.vinculoFP vinculo");
        hql.append(" where ");
        hql.append(" ficha.folhaDePagamento.ano = :ano ");
        hql.append(" and itemFicha.ano = :ano ");
        hql.append(" and  vinculo.id = :ep ");
        hql.append(" and evento.codigo = :codigo ");
        if (tipo != null) {
            hql.append(" and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipoFolha ");
        }
        hql.append(" group by evento");

        Query q = em.createQuery(hql.toString());

        q.setParameter("ano", ep.getAno());
        q.setParameter("codigo", codigo);
        if (tipo != null) {
            q.setParameter("tipoFolha", tipo);
        }
        q.setParameter("ep", ep.getId());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (EventoTotalItemFicha) q.getSingleResult();
    }

    public double valorUltimosMeses(EntidadePagavelRH ep, String codigo, int quantidadeMeses) {
        LocalDate dt = getDataReferencia(ep);

        StringBuilder hql = new StringBuilder();
        hql.append("select sum(coalesce(itemFicha.valor,0)) ");
        hql.append(" from FichaFinanceiraFP ficha inner join ficha.itemFichaFinanceiraFP itemFicha");
        hql.append(" inner join itemFicha.eventoFP evento");

        hql.append(" inner join ficha.vinculoFP vinculo");
        hql.append(" where ");
        hql.append(" ficha.folhaDePagamento.ano " + montaIn12MesesAno(dt, quantidadeMeses) + " and ficha.folhaDePagamento.mes " + montaIn12MesesMeses(dt, quantidadeMeses) + " ");
        hql.append(" and  vinculo.id = :ep ");
        hql.append(" and evento.codigo = :codigo ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("codigo", codigo);
        q.setParameter("ep", ep.getIdCalculo());
        if (q.getResultList().isEmpty()) {
            return 0;
        }
        BigDecimal valor = (BigDecimal) q.getSingleResult();
        if (valor == null) return 0;
        return valor.doubleValue();
    }

    private String montaIn12MesesAno(LocalDate LocalDate, int quantidadeMeses) {
        String retorno = " in (";
        int ano1 = LocalDate.getYear();
        LocalDate = LocalDate.minusMonths(quantidadeMeses);
        for (int i = LocalDate.getYear(); i <= ano1; i++) {
            retorno += LocalDate.getYear() + ",";
            LocalDate = LocalDate.plusYears(1);
        }
        retorno = retorno.substring(0, retorno.length() - 1);
        retorno += ") ";
        return retorno;
    }

    private String montaIn12MesesMeses(LocalDate LocalDate, int quantidadeMeses) {
        String retorno = " in (";
        LocalDate = LocalDate.minusMonths(quantidadeMeses);
        Integer inicio = LocalDate.getMonthValue();
        for (int i = LocalDate.getMonthValue(); i < inicio + quantidadeMeses; i++) {
            retorno += LocalDate.getMonthValue() - 1 + ",";
            LocalDate = LocalDate.plusMonths(1);
        }
        retorno = retorno.substring(0, retorno.length() - 1);
        retorno += ") ";
        return retorno;
    }

    public int entreDiasMesCalculo(EntidadePagavelRH ep, Date ini, Date fim) {
        java.time.LocalDate dataInicial = java.time.Instant.ofEpochMilli(ini.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        java.time.LocalDate dataFinal = java.time.Instant.ofEpochMilli(fim.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        java.time.LocalDate primeiroDiaDoMesDoCalculo = getPrimeiroDiaDoMesCalculo(ep);
        java.time.LocalDate ultimoDiaDoMesDoCalculo = getUltimoDiaDoMesCalculo(ep);
        if (dataInicial.isBefore(primeiroDiaDoMesDoCalculo)) {
            dataInicial = primeiroDiaDoMesDoCalculo;
        }
        if (dataFinal.isAfter(ultimoDiaDoMesDoCalculo)) {
            dataFinal = ultimoDiaDoMesDoCalculo;
        }
        return (int) DAYS.between(dataInicial, dataFinal) + 1;
    }

    /*
    Recupera o numero de penalidades do servidor, esse valor deve ser sempre recuperado do mes anterior,
    por isso a trava para calcular sempre na retroação.
 */
    public double numeroDiasDescontoDePenalidalidade(EntidadePagavelRH ep) {
        double valor = 0;
        Query query = em.createQuery("select p from PenalidadeFP  p " +
            " where p.contratoFP.id = :ep and  "
            + " to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(p.inicioVigencia,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(p.finalVigencia,'mm/yyyy'),'mm/yyyy') ");
        query.setParameter("ep", ep.getIdCalculo());
        query.setParameter("dataParam", localDateToDate(getDataReferencia(ep)));
        if (query.getResultList().isEmpty()) {
            return 0;

        }

        List<PenalidadeFP> penalidadeFPList = query.getResultList();
        if (!ep.isCalculandoRetroativo()) {
            return valor;
        }
        for (PenalidadeFP penalidadeFP : penalidadeFPList) {
            LocalDate varInicio = dateToLocalDate(penalidadeFP.getInicioVigencia());
            LocalDate varFim = dateToLocalDate(penalidadeFP.getFinalVigencia());
            LocalDate varIniMes = LocalDate.of(ep.getAno(), Mes.getMesToInt(ep.getMes()).getNumeroMes(), 1);
            LocalDate varFimMes = varIniMes.withDayOfMonth(varIniMes.lengthOfMonth());
            if (varInicio.isBefore(varIniMes)) {
                varInicio = varIniMes;
            }
            if (varFim.isAfter(varFimMes)) {
                varFim = varFimMes;
            }
            valor += DataUtil.diasEntre(varInicio, varFim);
        }
        return valor;
    }

    public int diasSextaParte(EntidadePagavelRH ep) {
        VinculoFP v = (VinculoFP) ep;
        Query q = em.createQuery("select sextaParte from SextaParte sextaParte " +
            " where sextaParte.vinculoFP.id = :ep " +
            " and :data between sextaParte.inicioVigencia and coalesce(sextaParte.fimVigencia,:data) ");
        q.setParameter("ep", v.getId());
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        if (q.getResultList().isEmpty()) {
            return 0;
        }

        SextaParte sextaParte = (SextaParte) q.getResultList().get(0);
        int dias = entreDiasMesCalculo(ep, sextaParte.getInicioVigencia(), Date.from(getUltimoDiaDoMesCalculo(ep).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        if (dias < 0) return 0;
        return dias;

    }

    public boolean isServidorEVerbaBloqueado(EntidadePagavelRH vinculoFP, EventoFP eventoFP, Date dataReferencia) {
        return bloqueioEventoFPFacade.isBloqueado(vinculoFP, eventoFP, dataReferencia);
    }

    public boolean categoriaEvento(EntidadePagavelRH ep, String codigo) {
        if (ep instanceof Aposentadoria) {
            return temEventoCategoriaAposentado(ep, codigo);
        } else {
            return temEventoCategoriaServidor(ep, codigo);
        }

    }

    private boolean temEventoCategoriaServidor(EntidadePagavelRH ep, String codigo) {
//        Query q = em.createNativeQuery("SELECT cat.* FROM EnquadramentoFuncional a \n" +
//                "                 INNER JOIN VinculoFP b ON a.contratoservidor_id = b.id \n" +
//                "                 INNER JOIN EnquadramentoPCS c ON a.categoriaPCS_id = c.categoriaPCS_id\n" +
//                "                 AND a.progressaoPCS_id = c.progressaoPCS_id \n" +
//                "                 inner join  CategoriaEventoFP cat on cat.categoriapcs_id  = a.categoriaPCS_id\n" +
//                "                 inner join EventoFP evento on evento.id = cat.eventofp_id\n" +
//                "                 WHERE b.id = :id  and evento.codigo = :codigo \n" +
//                " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(a.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(a.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') " +
//                " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(c.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(c.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') " +
//                " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(cat.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cat.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') ");
        Query q = em.createQuery("select c from ContratoFP  contrato join  contrato.cargo cargo join cargo.cargoEventoFP c where cargo.ativo = true and :data between cargo.inicioVigencia and coalesce(cargo.finalVigencia,:data)" +
            " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(c.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(c.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') " +
            " and c.eventoFP.codigo = :codigo and contrato.id = :id ");
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("codigo", codigo);
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        if (categoriaEventoComEnquadramentoCC(ep, codigo)) {
            return true;
        }
        if (q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean temEventoCategoriaAposentado(EntidadePagavelRH ep, String codigo) {
//        Query q = em.createNativeQuery("SELECT cat.* FROM EnquadramentoFuncional a \n" +
//                "                 INNER JOIN VinculoFP b ON a.contratoservidor_id = b.id \n" +
//                "                 INNER JOIN EnquadramentoPCS c ON a.categoriaPCS_id = c.categoriaPCS_id\n" +
//                "                 AND a.progressaoPCS_id = c.progressaoPCS_id \n" +
//                "                 inner join  CategoriaEventoFP cat on cat.categoriapcs_id  = a.categoriaPCS_id\n" +
//                "                 inner join EventoFP evento on evento.id = cat.eventofp_id\n" +
//                "                 WHERE b.id = :id  and evento.codigo = :codigo \n" +
//                " and a.inicioVigencia = (select max(enq.inicioVigencia) from EnquadramentoFuncional enq where enq.contratoServidor_id = :id) " +
//                " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(c.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(c.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') " +
//                " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(cat.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cat.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') ");
        Query q = em.createQuery("select c from Aposentadoria ap join ap.contratoFP contrato join  contrato.cargo cargo join cargo.cargoEventoFP c where cargo.ativo = true and :data between cargo.inicioVigencia and coalesce(cargo.finalVigencia,:data)" +
            " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(c.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(c.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') " +
            " and c.eventoFP.codigo = :codigo and ap.id = :id ");
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("codigo", codigo);
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        if (q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean categoriaEventoComEnquadramentoCC(EntidadePagavelRH ep, String codigo) {
//        Query q = em.createNativeQuery("SELECT cat.* FROM EnquadramentoCC a inner join CargoConfianca cc on cc.id = a.cargoconfianca_id \n" +
//                "                 INNER JOIN VinculoFP b ON cc.contratofp_id = b.id \n" +
//                "                 INNER JOIN EnquadramentoPCS c ON a.categoriaPCS_id = c.categoriaPCS_id\n" +
//                "                 AND a.progressaoPCS_id = c.progressaoPCS_id \n" +
//                "                 inner join  CategoriaEventoFP cat on cat.categoriapcs_id  = a.categoriaPCS_id\n" +
//                "                 inner join EventoFP evento on evento.id = cat.eventofp_id\n" +
//                "                 WHERE b.id = :id  and evento.codigo = :codigo \n " +
//                " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(a.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(a.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') " +
//                " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(c.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(c.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') " +
//                " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(cat.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cat.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') ");
        Query q = em.createQuery("select c from CargoConfianca cargoConfianca join  cargoConfianca.contratoFP contrato join cargoConfianca.cargo cargo join cargo.cargoEventoFP c where cargo.ativo = true and :data between cargo.inicioVigencia and coalesce(cargo.finalVigencia,:data)" +
            " and :data between c.inicioVigencia and coalesce(c.finalVigencia,:data)" +
            " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(cargoConfianca.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cargoConfianca.finalVigencia,:data),'mm/yyyy'),'mm/yyyy')" +
            " and c.eventoFP.codigo = :codigo and contrato.id = :id ");
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("codigo", codigo);
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        return !q.getResultList().isEmpty();
    }

    public boolean isServidorLotadoNoOrgao(EntidadePagavelRH ep, String codigo) {
        Query q = em.createQuery("select lotacao.id from LotacaoFuncional lotacao, VwHierarquiaAdministrativa ho join lotacao.vinculoFP vinculo " +
            " where ho.subordinadaId = lotacao.unidadeOrganizacional.id and vinculo.id = :id and " +
            " :data between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,:data)" +
            " and :data between ho.inicioVigencia and coalesce(ho.fimVigencia,:data) " +
            " and substring(ho.codigo, 4,2) like :codigo");
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        q.setParameter("codigo", codigo.trim());
        return !q.getResultList().isEmpty();
    }

    @DescricaoMetodo("obterPrevidenciaComplementar(ep)")
    public ItemPrevidenciaComplementar obterPrevidenciaComplementar(EntidadePagavelRH ep) {
        Query q = em.createQuery("select item from ItemPrevidenciaComplementar item " +
            " inner join item.previdenciaComplementar previdencia " +
            " where previdencia.contratoFP.id = :id " +
            " and :data between item.inicioVigencia and coalesce(item.finalVigencia, :data) ");
        q.setParameter("id", ep.getIdCalculo());
        q.setParameter("data", localDateToDate(getDataReferencia(ep)));
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (ItemPrevidenciaComplementar) resultList.get(0);
        }
        return null;
    }

    @DescricaoMetodo("tetoRemuneratorio(ep)")
    public ReferenciaFPFuncao tetoRemuneratorio(EntidadePagavelRH ep) {
        ReferenciaFPFuncao obterValorReferencia = new ReferenciaFPFuncao();
        obterValorReferencia.setPercentual(0.0);
        obterValorReferencia.setValor(0.0);

        Query q = em.createQuery(" select valor from ContratoFP contrato " +
            " inner join contrato.cargo c " +
            " inner join c.referenciaFP ref  " +
            " inner join ref.valoresReferenciasFPs valor" +
            " where contrato.id = :ep " +
            "    and ref.tipoReferenciaFP = :tipoReferencia" +
            "    and :dataReferencia between valor.inicioVigencia and coalesce(valor.finalVigencia, :dataReferencia) ");
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("dataReferencia", localDateToDate(getDataReferencia(ep)));
        q.setParameter("tipoReferencia", TipoReferenciaFP.VALOR_VALOR);
        try {
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                return obterValorReferencia;
            }

            ValorReferenciaFP valorReferenciaFP = (ValorReferenciaFP) resultList.get(0);
            obterValorReferencia.setValor(valorReferenciaFP.getValor().doubleValue());
            obterValorReferencia.setTipoReferenciaFP(valorReferenciaFP.getReferenciaFP().getTipoReferenciaFP());
        } catch (Exception re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método tetoRemuneratorio", re);
        }
        return obterValorReferencia;
    }

    @DescricaoMetodo("enquadramentoPCCR(ep, 'PLANO','CATEGORIA','PROGRESSÃO','NÍVEL','REFERÊNCIA')")
    public Double enquadramentoPCCR(EntidadePagavelRH ep, String plano, String categoria, String progressao, String nivel, String referencia) {
        String sql = "select obj.vencimentobase " +
            " from EnquadramentoPCS obj " +
            "         inner join ProgressaoPCS progressao on obj.progressaopcs_id = progressao.id " +
            "         inner join ProgressaoPCS progressaoPai on progressao.superior_id = progressaoPai.id " +
            "         inner join CategoriaPCS categoria on obj.categoriapcs_id = categoria.id " +
            "         inner join CategoriaPCS categoriaPai on categoria.superior_id = categoriaPai.id " +
            "         inner join PlanoCargosSalarios pla on categoria.planocargossalarios_id = pla.id " +
            " where :dataReferencia between obj.iniciovigencia and coalesce(obj.finalvigencia, :dataReferencia) " +
            "  and trim(upper(pla.descricao)) = :plano " +
            "  and trim(upper(categoriaPai.descricao)) = :categoria " +
            "  and trim(upper(progressaoPai.descricao)) = :progressao " +
            "  and trim(upper(categoria.descricao)) = :nivel " +
            "  and trim(upper(progressao.descricao)) = :referencia" +
            " order by obj.iniciovigencia desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", localDateToDate(getDataReferencia(ep)));
        q.setParameter("plano", plano.toUpperCase().trim());
        q.setParameter("categoria", categoria.toUpperCase().trim());
        q.setParameter("progressao", progressao.toUpperCase().trim());
        q.setParameter("nivel", nivel.toUpperCase().trim());
        q.setParameter("referencia", referencia.toUpperCase().trim());
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return 0.0;
        }

        return ((BigDecimal) resultList.get(0)).doubleValue();
    }

    @DescricaoMetodo("progressaoServidor()")
    public String progressaoServidor(EntidadePagavelRH ep) {
        String sql = " select progressaoPai.descricao " +
            " from EnquadramentoFuncional enquadramento " +
            "         inner join vinculofp vinculo on enquadramento.contratoservidor_id = vinculo.id " +
            "         left join ProgressaoPCS progressao on enquadramento.progressaopcs_id = progressao.ID " +
            "         left join ProgressaoPCS progressaoPai on progressao.superior_id = progressaoPai.id " +
            " where vinculo.id = :ep " +
            "   and :dataReferencia between enquadramento.iniciovigencia and coalesce(enquadramento.finalvigencia, :dataReferencia) " +
            " order by enquadramento.iniciovigencia";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", localDateToDate(getDataReferencia(ep)));
        q.setParameter("ep", ep.getIdCalculo());
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return "";
        }
        return ((String) resultList.get(0));
    }

    public double recuperaQuantidadeMesesPropocionaisA13Salario(EntidadePagavelRH ep) {
        String sql = "  select months_between(to_date(:data,'mm/yyyy'),to_date(f.mes+1||'/'||f.ano,'mm/yyyy')) as meses " +
            "                 from folhadepagamento f inner join fichafinanceirafp ficha on ficha.folhadepagamento_id = f.id " +
            "                 where ficha.vinculofp_id = :id and f.tipofolhadepagamento = 'SALARIO_13' AND ROWNUM = 1 ORDER BY f.MES desc, f.ANO desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", getMesAnoFormatado(ep));
        q.setParameter("id", ep.getIdCalculo());
        if (q.getResultList().isEmpty()) {
            return 0;
        }
        BigDecimal valor = (BigDecimal) q.getResultList().get(0);
        return valor.doubleValue();
    }

    private String getMesAnoFormatado(EntidadePagavelRH ep) {
        LocalDate refe = getDataReferenciaDateTime(ep);
        return refe.getMonthValue() + "/" + refe.getYear();  //To change body of created methods use File | Settings | File Templates.
    }

    public List<EventoTotalItemFicha> recuperarValorFichaPorParametro(List<String> eventos, EntidadePagavelRH ep, LocalDate dataLimite, Integer ano, FolhaDePagamentoNovoCalculador.TipoBusca tipoBusca, List<TipoFolhaDePagamento> tipoFolhaDePagamento, Boolean limitarAno, Boolean considerarMesAtual) {
        String sql = "select evento.codigo, ";
        if (FolhaDePagamentoNovoCalculador.TipoBusca.REFERENCIA.equals(tipoBusca)) {
            sql += " coalesce(sum(itemFicha.valorReferencia), 0) as referencia ";
        } else {
            sql += " coalesce(sum(case when itemFicha.tipoeventofp = 'DESCONTO' then -itemFicha.valor else itemFicha.valor end),0) as valor ";
        }
        sql += " , itemFicha.tipoCalculoFP ";
        sql += " from FichaFinanceiraFP ficha " +
            "         inner join itemFichaFinanceiraFP itemFicha on ficha.id = itemFicha.fichafinanceirafp_id " +
            "         inner join eventoFP evento on itemFicha.eventofp_id = evento.id " +
            "         inner join vinculoFP vinculo on ficha.vinculofp_id = vinculo.id " +
            "         inner join FolhaDePagamento folha on ficha.folhadepagamento_id = folha.id " +
            " where vinculo.id = :ep ";
        if (dataLimite != null) {
            sql += " and to_date(folha.mes+1||'/'||folha.ano,'mm/yyyy') >= to_date(:dataParam,'dd/mm/yyyy') ";
        }
        if (limitarAno) {
            sql += "    and folha.ano = :ano  ";
        }

        if (!considerarMesAtual) {
            sql += " and (folha.ano = :anoCalculo and folha.mes <> :mes) ";
        }

        if (tipoFolhaDePagamento != null && !tipoFolhaDePagamento.isEmpty()) {
            List<String> listaTipoFolha = Lists.newArrayList();
            for (TipoFolhaDePagamento tipoFolha : tipoFolhaDePagamento) {
                listaTipoFolha.add(tipoFolha.name());
            }
            sql += "  and folha.tipoFolhaDePagamento in(" + getClausulaInCodigoEventoFP(listaTipoFolha) + ") ";
        }
        sql += "  and evento.codigo in (" + getClausulaInCodigoEventoFP(eventos) + ")" +
            "   group by evento.codigo, itemFicha.tipoCalculoFP ";

        Query q = (Query) em.createNativeQuery(sql);
        if (limitarAno) {
            q.setParameter("ano", ano);
        }
        if (!considerarMesAtual) {
            q.setParameter("anoCalculo", ep.getAno());
            q.setParameter("mes", Mes.getMesToInt(ep.getMes()).getNumeroMesIniciandoEmZero());
        }
        if (dataLimite != null) {
            q.setParameter("dataParam", getDataFormatada(localDateToDate(dataLimite)));
        }

        q.setParameter("ep", ep.getId());

        List<Object[]> objetos = q.getResultList();
        List<EventoTotalItemFicha> eventosFicha = Lists.newLinkedList();
        if (!objetos.isEmpty()) {
            for (Object[] objeto : objetos) {
                EventoTotalItemFicha ev = new EventoTotalItemFicha();
                String codigo = (String) objeto[0];
                ev.setEvento(eventoFPFacade.recuperaPorCodigo(codigo));
                BigDecimal decimal = (BigDecimal) objeto[1];
                BigDecimal valor = decimal;
                String tipoCalculo = (String) objeto[2];
                if (tipoCalculo != null) {
                    ev.setTipoCalculoFP(TipoCalculoFP.valueOf(tipoCalculo));
                }

                if (FolhaDePagamentoNovoCalculador.TipoBusca.REFERENCIA.equals(tipoBusca)) {
                    ev.setReferencia(valor.abs());
                } else {
                    ev.setTotal(valor.abs());
                }
                //return ev;
                eventosFicha.add(ev);
            }


        }
        return eventosFicha;
    }

    private String getClausulaInCodigoEventoFP(List<String> codigosRecurso) {
        String codigos = "";
        for (String cod : codigosRecurso) {
            codigos += "'" + cod + "',";
        }
        codigos = codigos.substring(0, codigos.length() - 1);
        return codigos;
    }

    public Integer buscarDiasDeLicencaPremio(EntidadePagavelRH ep, LocalDate dataReferencia) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        Query q = em.createQuery(" select concessao from ConcessaoFeriasLicenca concessao "
            + " inner join concessao.periodoAquisitivoFL periodo "
            + " inner join periodo.contratoFP contratofp "
            + " where contratofp.id = :parametro "
            + " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(concessao.dataInicial,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(concessao.dataFinal,:dataVigencia),'mm/yyyy'),'mm/yyyy')  " +
            " and periodo.baseCargo.basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipo");

        q.setParameter("parametro", vinculoFP.getIdCalculo());
        q.setParameter("tipo", TipoPeriodoAquisitivo.LICENCA);
        q.setParameter("dataVigencia", dataReferencia);
        List<ConcessaoFeriasLicenca> lista = new ArrayList<>();
        Integer qtdDias = 0;


        for (ConcessaoFeriasLicenca concessao : lista) {
            qtdDias = +getVigenciaDaConcessao(concessao, dataReferencia);
        }
        return qtdDias;
    }


    public boolean hasVinculoEncerrandoNoMes(EntidadePagavelRH ep) {
        String sql = "select vinculo.id from pensionista " +
            " inner join vinculofp vinculo on pensionista.id = vinculo.id " +
            " where extract(year from vinculo.FINALVIGENCIA) = :ano and extract(month from vinculo.FINALVIGENCIA) = :mes " +
            " and vinculo.id = :idVinculo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ep.getAno());
        q.setParameter("mes", ep.getMes());
        q.setParameter("idVinculo", ep.getId());
        return !q.getResultList().isEmpty();
    }

    public List<FichaFinanceiraFP> buscarFichasFinanceirasFPPorAno(Integer ano, EntidadePagavelRH ep, TipoFolhaDePagamento tipo) {
        return fichaFinanceiraFPFacade.buscarFichasFinanceirasFPPorAno(ano, (VinculoFP) ep, tipo);
    }

    public List<FichaFinanceiraFP> buscarListaFichaFinanceiraPorVinculoFPMesAnoTipoFolha(EntidadePagavelRH ep, Integer mes, Integer ano, TipoFolhaDePagamento tipo) {
        return fichaFinanceiraFPFacade.recuperaListaFichaFinanceiraPorVinculoFPMesAnoTipoFolha((VinculoFP) ep, mes, ano, tipo);
    }

    public Integer getDuracaoBasesPeriodoAquisitivo(EntidadePagavelRH ep, String tipoPeriodoAquisitivo) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        List<String> tipos = Arrays.stream(TipoPeriodoAquisitivo.values()).map(Enum::name).collect(Collectors.toList());
        if (!tipos.contains(tipoPeriodoAquisitivo)) {
            return 0;
        }
        String sql = "select basePA.*  " +
            " from baseperiodoaquisitivo basePA  " +
            "         inner join baseCargo baseC on basepa.id = basec.baseperiodoaquisitivo_id  " +
            "         inner join cargo c on basec.cargo_id = c.id  " +
            "         inner join contratofp contrato on contrato.cargo_id = c.id  " +
            " where contrato.id = :contratoId  " +
            "  and basePA.tipoperiodoaquisitivo = :tipoPeriodoAquisitivo  " +
            "  and :dataReferencia between basec.iniciovigencia and coalesce(basec.finalvigencia, :dataReferencia)";
        Query q = em.createNativeQuery(sql, BasePeriodoAquisitivo.class);
        q.setParameter("contratoId", vinculoFP.getIdCalculo());
        q.setParameter("tipoPeriodoAquisitivo", tipoPeriodoAquisitivo);
        q.setParameter("dataReferencia", getDataReferencia(ep));
        q.setMaxResults(1);

        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return ((BasePeriodoAquisitivo) resultList.get(0)).getDuracao();
        }
        return 0;
    }

    public Integer buscarDiasDireitoBasePA(EntidadePagavelRH ep, String tipoPeriodoAquisitivo) {
        VinculoFP vinculoFP = (VinculoFP) ep;
        List<String> tipos = Arrays.stream(TipoPeriodoAquisitivo.values()).map(Enum::name).collect(Collectors.toList());
        if (!tipos.contains(tipoPeriodoAquisitivo)) {
            return 0;
        }
        String sql = "select basePA.*  " +
            "from baseperiodoaquisitivo basePA  " +
            "         inner join baseCargo baseC on basepa.id = basec.baseperiodoaquisitivo_id  " +
            "         inner join cargo c on basec.cargo_id = c.id  " +
            "         inner join contratofp contrato on contrato.cargo_id = c.id  " +
            "where contrato.id = :contratoId  " +
            "  and basePA.tipoperiodoaquisitivo = :tipoPeriodoAquisitivo  " +
            "  and :dataReferencia between basec.iniciovigencia and coalesce(basec.finalvigencia, :dataReferencia)";
        Query q = em.createNativeQuery(sql, BasePeriodoAquisitivo.class);
        q.setParameter("contratoId", vinculoFP.getIdCalculo());
        q.setParameter("tipoPeriodoAquisitivo", tipoPeriodoAquisitivo);
        q.setParameter("dataReferencia", getDataReferencia(ep));
        q.setMaxResults(1);

        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return ((BasePeriodoAquisitivo) resultList.get(0)).getDiasDeDireito();
        }
        return 0;
    }

    public Double somaEventosFeriasProporcionais(EntidadePagavelRH ep, Boolean isReferencia, String[] tiposFolha, FolhaDePagamentoNovoCalculador calculador, String[] eventos) {
        VinculoFP vinculo = (VinculoFP) ep;

        String sql = " select pa.id, pa.iniciovigencia, e.datarescisao " +
            " from periodoaquisitivofl pa " +
            " inner join basecargo bc on bc.id = pa.basecargo_id " +
            " inner join baseperiodoaquisitivo bp on bp.id = bc.baseperiodoaquisitivo_id " +
            " inner join exoneracaorescisao e on e.vinculofp_id = pa.contratofp_id " +
            " where pa.contratofp_id = :contratoId " +
            " and bp.tipoperiodoaquisitivo = :tipoPeriodoAquisitivo " +
            " and pa.status in :status " +
            " and e.datarescisao between pa.iniciovigencia and pa.finalvigencia " +
            " order by pa.iniciovigencia desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoId", ep.getIdCalculo());
        q.setParameter("tipoPeriodoAquisitivo", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("status", Lists.newArrayList(StatusPeriodoAquisitivo.NAO_CONCEDIDO.name(), StatusPeriodoAquisitivo.PARCIAL.name()));
        q.setMaxResults(1);

        List<Object[]> periodos = q.getResultList();
        PeriodoAquisitivoFLDTO periodoDTO = null;
        if (periodos != null && !periodos.isEmpty()) {
            Object[] periodo = periodos.get(0);
            periodoDTO = new PeriodoAquisitivoFLDTO();
            periodoDTO.setId(((BigDecimal) periodo[0]).longValue());
            periodoDTO.setInicioVigencia((Date) periodo[1]);
            periodoDTO.setFinalVigencia((Date) periodo[2]);
        } else {
            return 0.0;
        }
        return getSomaValoresItensFichaDosPeriodosAquisitivos(vinculo, isReferencia, tiposFolha, eventos, Lists.newArrayList(periodoDTO));
    }

    public Double somaEventosFeriasVencidas(EntidadePagavelRH ep, Boolean isReferencia, String[] tiposFolha, FolhaDePagamentoNovoCalculador calculador, String[] eventos) {
        VinculoFP vinculo = (VinculoFP) ep;

        String sql = " select pa.id, pa.iniciovigencia, pa.finalvigencia \n" +
            " from periodoaquisitivofl pa \n" +
            " inner join basecargo bc on bc.id = pa.basecargo_id \n" +
            " inner join baseperiodoaquisitivo bp on bp.id = bc.baseperiodoaquisitivo_id \n" +
            " inner join exoneracaorescisao e on e.vinculofp_id = pa.contratofp_id \n" +
            " where pa.contratofp_id = :contratoId \n" +
            " and bp.tipoperiodoaquisitivo = :tipoPeriodoAquisitivo \n" +
            " AND pa.status in :status \n" +
            " and pa.finalvigencia < e.datarescisao \n" +
            " order by pa.iniciovigencia desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoId", ep.getIdCalculo());
        q.setParameter("tipoPeriodoAquisitivo", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("status", Lists.newArrayList(StatusPeriodoAquisitivo.NAO_CONCEDIDO.name(), StatusPeriodoAquisitivo.PARCIAL.name()));

        List<PeriodoAquisitivoFLDTO> listaPeriodoAquisitivoFLs = Lists.newArrayList();
        List<Object[]> periodos = q.getResultList();
        for (Object[] periodo : periodos) {
            PeriodoAquisitivoFLDTO periodoDTO = new PeriodoAquisitivoFLDTO();
            periodoDTO.setId(((BigDecimal) periodo[0]).longValue());
            periodoDTO.setInicioVigencia((Date) periodo[1]);
            periodoDTO.setFinalVigencia((Date) periodo[2]);
            listaPeriodoAquisitivoFLs.add(periodoDTO);
        }
        if (periodos == null || periodos.isEmpty()) {
            return 0.0;
        }
        return getSomaValoresItensFichaDosPeriodosAquisitivos(vinculo, isReferencia, tiposFolha, eventos, listaPeriodoAquisitivoFLs);
    }

    public Double somaEventosFeriasDobradas(EntidadePagavelRH ep, Boolean isReferencia, String[] tiposFolha, FolhaDePagamentoNovoCalculador calculador, String[] eventos) {
        VinculoFP vinculo = (VinculoFP) ep;

        String sql = " select pa.id, pa.iniciovigencia, pa.finalvigencia \n" +
            " from periodoaquisitivofl pa \n" +
            " inner join basecargo bc on bc.id = pa.basecargo_id \n" +
            " inner join baseperiodoaquisitivo bp on bp.id = bc.baseperiodoaquisitivo_id \n" +
            " inner join exoneracaorescisao e on e.vinculofp_id = pa.contratofp_id \n" +
            " where pa.contratofp_id = :contratoId \n" +
            " and bp.tipoperiodoaquisitivo = :tipoPeriodoAquisitivo \n" +
            " AND pa.status in :status \n" +
            " and add_months(pa.finalvigencia, bp.duracao) < e.datarescisao + pa.saldo \n" +
            " order by pa.iniciovigencia desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoId", ep.getIdCalculo());
        q.setParameter("tipoPeriodoAquisitivo", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("status", Lists.newArrayList(StatusPeriodoAquisitivo.NAO_CONCEDIDO.name(), StatusPeriodoAquisitivo.PARCIAL.name()));

        List<PeriodoAquisitivoFLDTO> listaPeriodoAquisitivoFLs = Lists.newArrayList();
        List<Object[]> periodos = q.getResultList();
        for (Object[] periodo : periodos) {
            PeriodoAquisitivoFLDTO periodoDTO = new PeriodoAquisitivoFLDTO();
            periodoDTO.setId(((BigDecimal) periodo[0]).longValue());
            periodoDTO.setInicioVigencia((Date) periodo[1]);
            periodoDTO.setFinalVigencia((Date) periodo[2]);
            listaPeriodoAquisitivoFLs.add(periodoDTO);
        }
        if (periodos == null || periodos.isEmpty()) {
            return 0.0;
        }
        return getSomaValoresItensFichaDosPeriodosAquisitivos(vinculo, isReferencia, tiposFolha, eventos, listaPeriodoAquisitivoFLs);
    }

    private Double getSomaValoresItensFichaDosPeriodosAquisitivos(VinculoFP vinculo, Boolean isReferencia, String[] tiposFolha, String[] eventos, List<PeriodoAquisitivoFLDTO> periodos) {
        List<String> listaEventos = Arrays.asList(eventos);
        List<String> listaTiposFolha = Arrays.asList(tiposFolha);
        Date dataInicio = periodos.stream().map(PeriodoAquisitivoFLDTO::getInicioVigencia).min(Date::compareTo).get();
        Date dataFim = periodos.stream().map(PeriodoAquisitivoFLDTO::getFinalVigencia).max(Date::compareTo).get();
        return getTotalSomaValoresDoPeriodo(dataInicio, dataFim, vinculo.getId(), isReferencia, listaTiposFolha, listaEventos)
            - getDiferencaInicioSomaValoresDoPeriodo(dataInicio, vinculo.getId(), isReferencia, listaTiposFolha, listaEventos)
            - getDiferencaFimSomaValoresDoPeriodo(dataFim, vinculo.getId(), isReferencia, listaTiposFolha, listaEventos);
    }

    private Double getDiferencaInicioSomaValoresDoPeriodo(Date dataInicio, Long contratoId, Boolean isReferencia, List<String> tipoFolhas, List<String> eventos) {
        String sql = " SELECT\n" +
            "    sum(" +
            (Boolean.TRUE.equals(isReferencia) ? "iff.valorreferencia" : "iff.valor") +
            ") \n" +
            "    * (1 - (1+trunc(last_day(to_date(:dataInicio, 'dd/MM/yyyy'))) - to_date(:dataInicio, 'dd/MM/yyyy'))\n" +
            "    / (1+trunc(last_day(to_date(:dataInicio, 'dd/MM/yyyy')))-trunc(to_date(:dataInicio, 'dd/MM/yyyy'),'MM')))\n" +
            " FROM\n" +
            "    ItemFichaFinanceiraFP iff\n" +
            " INNER JOIN fichafinanceirafp ff ON\n" +
            "    iff.FICHAFINANCEIRAFP_ID = ff.id\n" +
            " INNER JOIN folhadepagamento folha ON\n" +
            "    ff.FOLHADEPAGAMENTO_ID = folha.id\n" +
            " INNER JOIN eventofp evento ON\n" +
            "    iff.EVENTOFP_ID = evento.id\n" +
            " WHERE\n" +
            "    ff.VINCULOFP_ID = :contratoId\n" +
            "    AND folha.ano = extract(YEAR FROM to_date(:dataInicio, 'dd/MM/yyyy'))\n" +
            "    AND folha.mes = extract(month FROM to_date(:dataInicio, 'dd/MM/yyyy')) - 1\n" +
            "    AND evento.codigo IN :eventos\n" +
            "    AND folha.tipofolhadepagamento IN :tipoFolhas ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicio", DataUtil.getDataFormatada(dataInicio));
        q.setParameter("contratoId", contratoId);
        q.setParameter("eventos", eventos);
        q.setParameter("tipoFolhas", tipoFolhas);
        Object resultado = q.getSingleResult();
        if (resultado != null) {
            return ((BigDecimal) resultado).doubleValue();
        }
        return 0.0;
    }

    private Double getDiferencaFimSomaValoresDoPeriodo(Date dataFim, Long contratoId, Boolean isReferencia, List<String> tipoFolhas, List<String> eventos) {
        String sql = " SELECT\n" +
            "    sum(" +
            (Boolean.TRUE.equals(isReferencia) ? "iff.valorreferencia" : "iff.valor") +
            ") \n" +
            "    * (trunc(last_day(to_date(:dataFim, 'dd/MM/yyyy'))) - to_date(:dataFim, 'dd/MM/yyyy'))\n" +
            "    / (1+trunc(last_day(to_date(:dataFim, 'dd/MM/yyyy')))-trunc(to_date(:dataFim, 'dd/MM/yyyy'),'MM'))\n" +
            " FROM\n" +
            "    ItemFichaFinanceiraFP iff\n" +
            " INNER JOIN fichafinanceirafp ff ON\n" +
            "    iff.FICHAFINANCEIRAFP_ID = ff.id\n" +
            " INNER JOIN folhadepagamento folha ON\n" +
            "    ff.FOLHADEPAGAMENTO_ID = folha.id\n" +
            " INNER JOIN eventofp evento ON\n" +
            "    iff.EVENTOFP_ID = evento.id\n" +
            " WHERE\n" +
            "    ff.VINCULOFP_ID = :contratoId\n" +
            "    AND folha.ano = extract(YEAR FROM to_date(:dataFim, 'dd/MM/yyyy'))\n" +
            "    AND folha.mes = extract(month FROM to_date(:dataFim, 'dd/MM/yyyy')) - 1\n" +
            "    AND evento.codigo IN :eventos\n" +
            "    AND folha.tipofolhadepagamento IN :tipoFolhas ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataFim", DataUtil.getDataFormatada(dataFim));
        q.setParameter("contratoId", contratoId);
        q.setParameter("eventos", eventos);
        q.setParameter("tipoFolhas", tipoFolhas);
        Object resultado = q.getSingleResult();
        if (resultado != null) {
            return ((BigDecimal) resultado).doubleValue();
        }
        return 0.0;
    }

    private Double getTotalSomaValoresDoPeriodo(Date dataInicio, Date dataFim, Long contratoId, Boolean isReferencia, List<String> tipoFolhas, List<String> eventos) {
        String sql = "SELECT\n" +
            " sum(" +
            (Boolean.TRUE.equals(isReferencia) ? "iff.valorreferencia" : "iff.valor") +
            ")\n" +
            " FROM ItemFichaFinanceiraFP iff\n" +
            " INNER JOIN fichafinanceirafp ff ON\n" +
            " iff.FICHAFINANCEIRAFP_ID = ff.id\n" +
            " INNER JOIN folhadepagamento folha ON\n" +
            " ff.FOLHADEPAGAMENTO_ID = folha.id\n" +
            " INNER JOIN eventofp evento ON\n" +
            " iff.EVENTOFP_ID = evento.id\n" +
            " WHERE\n" +
            " ff.VINCULOFP_ID = :contratoId\n" +
            " AND to_date(folha.mes + 1 || '/' || folha.ano, 'MM/yyyy')\n" +
            "     BETWEEN trunc(:dataInicio, 'MM') \n" +
            "     AND :dataFim\n" +
            " AND evento.codigo IN :eventos\n" +
            " AND folha.tipofolhadepagamento IN :tipoFolhas";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoId", contratoId);
        q.setParameter("dataInicio", dataInicio);
        q.setParameter("dataFim", dataFim);
        q.setParameter("eventos", eventos);
        q.setParameter("tipoFolhas", tipoFolhas);
        Object resultado = q.getSingleResult();
        if (resultado != null) {
            return ((BigDecimal) resultado).doubleValue();
        }
        return 0.0;
    }

    private int getVigenciaDaConcessao(ConcessaoFeriasLicenca concessao, LocalDate referencia) {
        LocalDate inicioConcessao = dateToLocalDate(concessao.getDataInicial());
        LocalDate finalConcessao = dateToLocalDate(concessao.getDataFinal());

        return diasEntreDatas(referencia, inicioConcessao, finalConcessao);
    }

    public double buscarPercentualFuncaoGratificada(TipoFuncaoGratificada tipo, VinculoFP vinculoFP, Date dataOperacao) {
        FuncaoGratificada funcaoGratificada = funcaoGratificadaFacade.buscarFuncaoGratificadaVigente(tipo, vinculoFP, dataOperacao);
        if (funcaoGratificada != null) {
            return funcaoGratificada.getPercentual();
        }
        return 0;
    }

    public FuncaoGratificada buscarFuncaoGratificada(TipoFuncaoGratificada tipo, VinculoFP vinculoFP, Date dataOperacao) {
        return funcaoGratificadaFacade.buscarFuncaoGratificadaVigente(tipo, vinculoFP, dataOperacao);
    }

    public class TempoServico {

        private Integer ano;
        private Integer mes;
        private Integer dias;

        public TempoServico(Integer ano, Integer mes, Integer dias) {
            this.ano = ano;
            this.mes = mes;
            this.dias = dias;
        }

        public Integer getAno() {
            return ano;
        }

        public void setAno(Integer ano) {
            this.ano = ano;
        }

        public Integer getDias() {
            return dias;
        }

        public void setDias(Integer dias) {
            this.dias = dias;
        }

        public Integer getMes() {
            return mes;
        }

        public void setMes(Integer mes) {
            this.mes = mes;
        }
    }

    //    public double quantidadeMesesPeriodoAquisitivo(EntidadePagavelRH ep) {
//        String sql = " select round(months_between(p.finalvigencia,p.iniciovigencia),0) as meses " +
//                "                 from PeriodoAquisitivoFL p inner join vinculofp v  on p.contratofp_id = v.id " +
//                "                 inner join basecargo base on base.id = p.basecargo_id inner join baseperiodoaquisitivo basep on basep.id =  base.baseperiodoaquisitivo_id " +
//                "                 where (p.status = :naoconcedido or p.status = :parcial) and basep.tipoperiodoaquisitivo = :tipo and v.id = :id ";
//        Query q = em.createNativeQuery(sql);
//        q.setParameter("id", ep.getIdCalculo());
//        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS.name());
//        q.setParameter("naoconcedido", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
//        q.setParameter("parical", StatusPeriodoAquisitivo.PARCIAL.name());
//        if(q.getResultList().isEmpty()){
//            return 0;
//        }
//       return (Double) q.getResultList().get(0);
//    }
    public class DataVigencia {
        private LocalDate inicioVigencia;
        private LocalDate finalVigencia;
        private LocalDate referencia;

        public DataVigencia() {
        }

        public DataVigencia(Date inicio, Date fim, Date referencia) {
            this.inicioVigencia = dateToLocalDate(inicio);
            this.finalVigencia = dateToLocalDate(fim);
            this.referencia = dateToLocalDate(referencia);
        }

        public DataVigencia(LocalDate inicio, LocalDate fim, LocalDate referencia) {
            this.inicioVigencia = inicio;
            this.finalVigencia = fim;
            this.referencia = referencia;
        }

        @Override
        public String toString() {
            return "DataVigencia{" +
                "finalVigencia=" + finalVigencia +
                ", inicioVigencia=" + inicioVigencia +
                '}';
        }

        public LocalDate getInicioVigencia() {
            return inicioVigencia;
        }

        public void setInicioVigencia(LocalDate inicioVigencia) {
            this.inicioVigencia = inicioVigencia;
        }

        public LocalDate getFinalVigencia() {
            return finalVigencia;
        }

        public void setFinalVigencia(LocalDate finalVigencia) {
            this.finalVigencia = finalVigencia;
        }

        public LocalDate getReferencia() {
            return referencia;
        }

        public void setReferencia(LocalDate referencia) {
            this.referencia = referencia;
        }
    }

    public AfastamentoFacade getAfastamentoFacade() {
        return afastamentoFacade;
    }
}
