/*
 * Codigo gerado automaticamente em Wed Jun 29 14:21:41 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovo;
import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.ServidorPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.entidades.rh.esocial.*;
import br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha;
import br.com.webpublico.entidadesauxiliares.ResultadoCalculoRetroativoFP;
import br.com.webpublico.entidadesauxiliares.rh.CedenciaPortal;
import br.com.webpublico.entidadesauxiliares.rh.calculo.MemoriaCalculoRHDTO;
import br.com.webpublico.entidadesauxiliares.rh.creditosalario.VinculoCreditoSalario;
import br.com.webpublico.entidadesauxiliares.rh.esocial.VinculoS1200VO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoCalculo;
import br.com.webpublico.enums.rh.TipoNaturezaRefenciaCalculo;
import br.com.webpublico.enums.rh.esocial.*;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.rh.CalculoBloqueadoException;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.interfaces.FolhaCalculavel;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.script.ItemErroScript;
import br.com.webpublico.singletons.SingletonFolhaDePagamento;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static br.com.webpublico.util.DataUtil.dateTimeToLocalDateTime;
import static br.com.webpublico.util.UtilRH.definirUltimaHoraDoDia;

@Stateless
public class FolhaDePagamentoFacade extends AbstractFacade<FolhaDePagamento> {

    public static final int TODOS_MESES = 12;
    protected static final Logger logger = LoggerFactory.getLogger(FolhaDePagamentoFacade.class);
    private static final Integer AFASTAMENTO_COM_ONUS = 14;
    private static final Integer AFASTAMENTO_SEM_ONUS = 15;
    private final boolean MOSTRAR_ESTATISTICAS = false;
    private final boolean MOSTRAR_LOGS_ECONSIG = true;
    private final boolean LOG2 = false;
    private final int TAMANHO_MAXIMO_IN = 1000;
    private final boolean SOMENTE_CEDER_SERVIDOR_PARA_UNIDADE_EXTERNA = true;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private br.com.webpublico.negocios.MiddleRHFacade middleRH;
    @EJB
    private LoteProcessamentoFacade loteProcessamentoFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private BloqueioEventoFPFacade bloqueioEventoFPFacade;
    @EJB
    private MotivoRejeicaoFacade motivoRejeicaoFacade;
    @EJB
    private BloqueioBeneficioFacade bloqueioBeneficioFacade;
    @EJB
    private SingletonFolhaDePagamento singletonFolhaDePagamento;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeAtual;

    public FolhaDePagamentoFacade() {
        super(FolhaDePagamento.class);

    }

    public static int getAno(Date dataAno) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAno);
        return c.get(Calendar.YEAR);
    }

    public static int getMes(Date dataAno) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAno);
        return c.get(Calendar.MONTH) + 1;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    private static BigDecimal definirValorDaMargemConsignavel(Double base, double margem) {
        BigDecimal valor = BigDecimal.ZERO;
        if (isCheckForRounding(BigDecimal.valueOf(base * margem))) {
            valor = BigDecimal.valueOf(base * margem).setScale(2, RoundingMode.HALF_UP);
        } else {
            valor = BigDecimal.valueOf(base * margem);
        }
        return valor;
    }

    private static boolean isCheckForRounding(BigDecimal value) {
        try {
            return value.remainder(BigDecimal.ONE).toPlainString().length() >= 6;
        } catch (Exception e) {
            logger.debug("Erro ao tentar checar se o número deve ser arredondado. ", e);
            return false;
        }
    }

    private static Map<LancamentoFP, BigDecimal> sortByComparator(Map<LancamentoFP, BigDecimal> unsortMap) {

        // Convert Map to List
        List<Map.Entry<LancamentoFP, BigDecimal>> list =
            new LinkedList<>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<LancamentoFP, BigDecimal>>() {
            @Override
            public int compare(Map.Entry<LancamentoFP, BigDecimal> o1,
                               Map.Entry<LancamentoFP, BigDecimal> o2) {
                EventoFP eventoFP1 = o1.getKey().getEventoFP();
                EventoFP eventoFP2 = o2.getKey().getEventoFP();
                return ordernarLancamentosParaDescontoConsignavel(eventoFP1, eventoFP2);
            }
        });

        // Convert sorted map back to a Map
        Map<LancamentoFP, BigDecimal> sortedMap = new LinkedHashMap<>();
        for (Iterator<Map.Entry<LancamentoFP, BigDecimal>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<LancamentoFP, BigDecimal> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;

    }

    @Override
    public void salvarNovo(FolhaDePagamento folhaDePagamento) {
        if (folhaDePagamento.getVersao() == null) {
            Integer versao = obterVersaoFolhaDePagamento(folhaDePagamento.getCompetenciaFP(), folhaDePagamento.getTipoFolhaDePagamento());
            folhaDePagamento.setVersao(versao);
        }
        super.salvarNovo(folhaDePagamento);
    }

    public FolhaDePagamentoSimulacao salvarNovaSimulacao(FolhaDePagamentoSimulacao folhaDePagamento) {
        return em.merge(folhaDePagamento);
    }

    public FolhaCalculavel salvarNovaFolha(FolhaCalculavel folhaDePagamento) {
        return em.merge(folhaDePagamento);
    }

    @Override
    public void salvar(FolhaDePagamento folhaDePagamento) {
        try {
            getEntityManager().merge(folhaDePagamento);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }


    public void salvar(FolhaCalculavel folhaDePagamento) {
        try {
            getEntityManager().merge(folhaDePagamento);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    public FolhaDePagamento buscarFolhaPorMesAnoAndUnidade(FolhaDePagamento folha) {
        Query q = em.createQuery("select f from FolhaDePagamento f left join f.detalhesCalculoRHList list left join list.itensDetalhesErrosCalculos iten where f.mes = :mes and f.ano = :ano and f.tipoFolhaDePagamento =:tipoFolha");
        q.setParameter("ano", folha.getAno());
        q.setParameter("mes", folha.getMes());
        q.setParameter("tipoFolha", folha.getTipoFolhaDePagamento());
        q.setMaxResults(1);
        try {
            return (FolhaDePagamento) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FolhaDePagamento buscarFolhaPorMesAnoAndUnidade(FolhaDePagamento folha, UnidadeOrganizacional unidade) {
        Query q = em.createQuery("select f from FolhaDePagamento f left join f.detalhesCalculoRHList detalhes left join detalhes.itensDetalhesErrosCalculos iten where f.mes = :mes and f.ano = :ano and f.unidadeOrganizacional = :unidade and f.tipoFolhaDePagamento =:tipoFolha" +
            " and f.competenciaFP.statusCompetencia = :status and f.efetivadaEm is null ");
        q.setParameter("ano", folha.getAno());
        q.setParameter("mes", folha.getMes());
        q.setParameter("tipoFolha", folha.getTipoFolhaDePagamento());
        q.setParameter("status", StatusCompetencia.ABERTA);
        q.setParameter("unidade", unidade);
        q.setMaxResults(1);
        try {
            FolhaDePagamento fo = (FolhaDePagamento) q.getResultList().get(0);
            return fo;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FolhaDePagamento> recuperarFolhaAberta() {
        Query q = em.createQuery("from FolhaDePagamento f where f.efetivadaEm is null");
        return q.getResultList();
    }


    public List<Afastamento> buscarAfastamentosSemRetorno(VinculoFP vinculoFP) {
        return funcoesFolhaFacade.getAfastamentoFacade().listaAfastamentoSemRetorno(vinculoFP);
    }

    public List<FolhaDePagamento> recuperarFolhaPorTipo(FolhaDePagamento folha, UnidadeOrganizacional unidade) {
        Query q = em.createQuery("from FolhaDePagamento f where f.tipoFolhaDePagamento =:tipoFolha and f.mes = :mes and f.ano = :ano and f.unidadeOrganizacional = :unidade");
        q.setParameter("ano", folha.getAno());
        q.setParameter("mes", folha.getMes());
        q.setParameter("unidade", unidade);
        q.setParameter("tipoFolha", folha.getTipoFolhaDePagamento());

        return q.getResultList();
    }

    public List<ContratoFP> recuperarContratosSemEnquadramento() {
        Query q = em.createQuery(
            "select c from ContratoFP c "
                + " where coalesce(c.finalVigencia,:dataHoje) >= :dataHoje "
                + " and c not in ("
                + "     select contratoFP from CedenciaContratoFP cedencia"
                + "         inner join cedencia.contratoFP contratoFP "
                + "         where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia "
                + "         and :dataHoje >= cedencia.dataCessao "
                + "         and :dataHoje <= coalesce(cedencia.dataRetorno,:dataHoje))"
                + " and c not in ("
                + "     select contratoFP from EnquadramentoFuncional enquadramento "
                + "         inner join enquadramento.contratoServidor contratoFP "
                + "         where coalesce(contratoFP.finalVigencia,:dataHoje) >= :dataHoje) "
                + " order by c.matriculaFP.matricula");

        q.setParameter("dataHoje", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        return q.getResultList();
    }

    public List<VinculoFP> recuperarTodosVinculos() {
        //O HQL traz os vínculos que não sejam contratos com cedência externa sem onus
        Query q = em.createQuery("select v from VinculoFP v "
            + " where coalesce(v.finalVigencia,:dataHoje) >= :dataHoje "
            + " and v not in(select contratoFP from CedenciaContratoFP cedencia"
            + " inner join cedencia.contratoFP contratoFP "
            + " where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia "
            + " and :dataHoje >= cedencia.dataCessao and "
            + " :dataHoje <= coalesce(cedencia.dataRetorno,:dataHoje) )"
            + " order by v.id");
        q.setParameter("dataHoje", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        //TODO RETIRAR ESTE MAX RESULTS
//        q.setMaxResults(300);
        return q.getResultList();
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<VinculoFP> recuperarTodasMatriculas(Mes mes, Integer ano) {
        Query q = em.createQuery("select v "
            + "  from VinculoFP v, RecursoDoVinculoFP recurso " +
            "   "
            + "  join v.matriculaFP m"
            + " where to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(v.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(v.finalVigencia,:dataHoje),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recurso.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recurso.finalVigencia,:dataHoje),'mm/yyyy'),'mm/yyyy') " +
            " and m.matricula is not null and m.pessoa is not null and v.inicioVigencia is not null and v.id = recurso.vinculoFP.id "
            + " and :data between v.inicioVigencia and coalesce(v.finalVigencia,:data) and v.id not in (select contratoFP.id "
            + "                  from CedenciaContratoFP cedencia"
            + "                   inner join cedencia.contratoFP contratoFP "
            + "                 where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia "
            + "   and trunc(:dataInicial) between trunc(cedencia.dataCessao) and trunc(coalesce(cedencia.dataRetorno, trunc(:dataInicial))) "
            + "     and trunc(:dataFinal) between trunc(cedencia.dataCessao) and trunc(coalesce(cedencia.dataRetorno, trunc(:dataFinal)))) "


            + "   and v not in(select contratoFP"
            + "                  from RetornoCedenciaContratoFP retorno"
            + "                 inner join retorno.cedenciaContratoFP cedencia "
            + "                   inner join cedencia.contratoFP contratoFP "
            + "                 where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia "
            + "                   and retorno.oficioRetorno = 0 "
            + "                   and trunc(:dataInicial) between trunc(cedencia.dataCessao) and trunc(coalesce(retorno.dataRetorno, trunc(:dataInicial))) "
            + "                   and trunc(:dataFinal) between trunc(cedencia.dataCessao) and trunc(coalesce(retorno.dataRetorno, trunc(:dataFinal)))) "
            + "     and v.id not in (select e.id from Estagiario e) order by v.inicioVigencia");
        q.setParameter("dataHoje", Util.criaDataPrimeiroDiaMesJoda(mes.getNumeroMes(), ano).toDate());
        q.setParameter("data", UtilRH.getDataOperacao(), TemporalType.DATE);
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        q.setParameter("dataInicial", Util.criaDataPrimeiroDiaMesJoda(mes.getNumeroMes(), ano).toDate());
        q.setParameter("dataFinal", DataUtil.criarDataUltimoDiaMes(mes.getNumeroMes(), ano).toDate());

        List<VinculoFP> vinculoFPs = q.getResultList();
        int contatorContrato = 0;
        int contatorAposentado = 0;
        int contatorPensionista = 0;
        int contatorBeneficiario = 0;

        for (VinculoFP v : vinculoFPs) {
            if (v instanceof ContratoFP) {
                contatorContrato++;
            }
            if (v instanceof Aposentadoria) {
                contatorAposentado++;
            }
            if (v instanceof Pensionista) {
                contatorPensionista++;
            }
            if (v instanceof Beneficiario) {
                contatorBeneficiario++;
            }
        }
        logger.debug("Total de Contratos para a folha: " + contatorContrato);
        logger.debug("Total de Aposentados para a folha: " + contatorAposentado);
        logger.debug("Total de Pensionistas para a folha: " + contatorPensionista);
        logger.debug("Total de Pensão Judicial para a folha: " + contatorBeneficiario);

        return vinculoFPs;
    }

    public Map<Long, Long> buscarVinculosComFontePagadoras(Mes mes, Integer ano) {
        Query q = em.createNativeQuery("select distinct v.id, vw.ENTIDADE_ID " +
            "from VinculoFP v " +
            "inner join VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = v.UNIDADEORGANIZACIONAL_ID " +
            "where to_date(to_char(:dataHoje, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(v.inicioVigencia, 'mm/yyyy'), 'mm/yyyy') and " +
            "              to_date(to_char(coalesce(v.finalVigencia, :dataHoje), 'mm/yyyy'), 'mm/yyyy') " +
            " and " +
            "    to_date(to_char(:dataHoje, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(vw.inicioVigencia, 'mm/yyyy'), 'mm/yyyy') and " +
            "        to_date(to_char(coalesce(vw.FIMVIGENCIA, :dataHoje), 'mm/yyyy'), 'mm/yyyy') ");
        q.setParameter("dataHoje", Util.criaDataPrimeiroDiaMesJoda(mes.getNumeroMes(), ano).toDate());
        Map<Long, Long> vinculoEntidade = Maps.newHashMap();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            vinculoEntidade.put(((BigDecimal) obj[0]).longValue(), obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
        }
        return vinculoEntidade;
    }

    public List<BigDecimal> buscarIdFontePagadora(VinculoFP vinculoFP, Date dataOperacao) {
        String sql = " select vw.entidade_id " +
            " from vwhierarquiaadministrativa vw " +
            " where to_date(to_char(:dataOperacao,'mm/yyyy'), 'mm/yyyy') between to_date(to_char(:inicioVigencia, 'mm/yyyy'), 'mm/yyyy') and " +
            "        to_date(to_char(:finalVigencia, 'mm/yyyy'), 'mm/yyyy')  " +
            " and " +
            "    to_date(to_char(:dataOperacao,'mm/yyyy'), 'mm/yyyy') between to_date(to_char(vw.inicioVigencia, 'mm/yyyy'), 'mm/yyyy') and " +
            "        to_date(to_char(coalesce(vw.fimvigencia, :dataOperacao), 'mm/yyyy'), 'mm/yyyy') " +
            " and vw.subordinada_id = :unidadeOrganizacionalId ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidadeOrganizacionalId", vinculoFP.getUnidadeOrganizacional().getId());
        q.setParameter("inicioVigencia", vinculoFP.getInicioVigencia());
        q.setParameter("finalVigencia", vinculoFP.getFinalVigencia() != null ? vinculoFP.getFinalVigencia() : dataOperacao);
        q.setParameter("dataOperacao", dataOperacao);
        return q.getResultList();
    }

    public List<VinculoFP> recuperarVinculosPorMatricula(MatriculaFP matricula) {
        //O HQL traz os vínculos que não sejam contratos com cedência externa sem onus
        Query q = em.createQuery("select v "
            + "  from VinculoFP v "
            + " where "
            + " :dataHoje between v.inicioVigencia and coalesce(v.finalVigencia,:dataHoje) "
            + " and v not in(select contratoFP"
            + "                  from CedenciaContratoFP cedencia"
            + "                 inner join cedencia.contratoFP contratoFP "
            + "                 where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia "
            + "                   and :dataHoje >= cedencia.dataCessao "
            + "                   and :dataHoje <= coalesce(cedencia.dataRetorno,:dataHoje))"
            + "   and v.matriculaFP = :matricula"
            + " order by v.id");
        q.setParameter("dataHoje", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        q.setParameter("matricula", matricula);
        q.setMaxResults(20);
        return q.getResultList();
    }

    /**
     * Método que retorna os contratos que tiveram rescisão no mes de calculo da
     * folha utilizado para o calculo geral da folha de rescisão.
     *
     * @param matricula
     * @param folha
     * @return
     */
    public List<VinculoFP> recuperarVinculosRescisos(MatriculaFP matricula, FolhaDePagamento folha) {

        String hql = "select obj from ExoneracaoRescisao recisao "
            + " inner join recisao.vinculoFP obj "
            + " where ((extract(month from recisao.dataRescisao)) = :mes) and "
            + " ((extract(year from recisao.dataRescisao)) = :ano) and obj.matriculaFP = :matricula";
        Query q = em.createQuery(hql);
        q.setParameter("mes", folha.getMes().getNumeroMes());
        q.setParameter("ano", folha.getAno());
        q.setParameter("matricula", matricula);
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<VinculoFP> recuperarVinculosPorUnidade(HierarquiaOrganizacional unidade) {
        List<VinculoFP> vinculoFPs = new ArrayList<>();
        //O HQL traz os vínculos que não sejam contratos com cedência externa sem onus
        Query q = em.createQuery("select v from VinculoFP v where v.unidadeOrganizacional = :unidade and "
            + " coalesce(v.finalVigencia,:dataHoje) >= :dataHoje "
            + " and v not in(select contratoFP from CedenciaContratoFP cedencia"
            + " inner join cedencia.contratoFP contratoFP "
            + " where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia "
            + " and :dataHoje >= cedencia.dataCessao and "
            + " :dataHoje <= coalesce(cedencia.dataRetorno,:dataHoje) )");
//        q.setMaxResults(50);
        q.setParameter("unidade", unidade.getSubordinada());
        q.setParameter("dataHoje", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        vinculoFPs.addAll(q.getResultList());
//        for (Object o : q.getResultList()) {
//            vinculoFPs.add((VinculoFP) o);
//        }
        for (HierarquiaOrganizacional u : hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipoAdm(unidade)) {
            q = em.createQuery("select v from VinculoFP v where v.unidadeOrganizacional = :unidade and coalesce(v.finalVigencia,:dataHoje) >= :dataHoje "
                + " and v not in(select contratoFP from CedenciaContratoFP cedencia"
                + " inner join cedencia.contratoFP contratoFP "
                + " where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia "
                + " and :dataHoje >= cedencia.dataCessao and "
                + " :dataHoje <= coalesce(cedencia.dataRetorno,:dataHoje) )");
//            q.setMaxResults(50);
            q.setParameter("unidade", u.getSubordinada());
            q.setParameter("dataHoje", Util.getDataHoraMinutoSegundoZerado(new Date()));
            q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);

            vinculoFPs.addAll(q.getResultList());
//            for (Object obj : q.getResultList()) {
//                vinculoFPs.add((VinculoFP) obj);
//            }
        }
        return vinculoFPs;
    }

    public void addListaErros(List<ItemErroScript> itemErroScripts, ItemErroScript item) {
        if (item != null) {
            itemErroScripts.add(item);
        }
    }

    //    public void calculoFolhaIndividual(final List<VinculoFP> vinculosFP, final FolhaDePagamento folha, ConfiguracaoContextoJS configuracaoContextoJS, List<ItemErroScript> itemErroScripts) {
//        List<EventoFP> eventosAutomaticos = eventoFPFacade.listaEventosAtivosPorTipoExecucao(TipoExecucaoEP.FOLHA, true);
//        funcoesFolhaFacade.setFolha(folha);
//        for (VinculoFP vinculo : vinculosFP) {
//            FichaFinanceiraFP ficha = new FichaFinanceiraFP();
//            ficha.setFolhaDePagamento(folha);
//            final RecursoDoVinculoFP recursoDoVinculo = recursoDoVinculoFPFacade.recuperarRecursoDoVinculo(vinculo);
//            if (recursoDoVinculo != null) {
//                ficha.setRecursoFP(recursoDoVinculo.getRecursoFP());
//            }
//
//            ficha.setVinculoFP(vinculo);
//            em.persist(ficha);
//            //Eventos automaticos...
//            geraItem(eventosAutomaticos, configuracaoContextoJS, vinculo, ficha, itemErroScripts);
//
//            //Eventos com lancamentos...
//
//            List<EventoFP> lancadosMesAtual = eventoFPFacade.listaEventosLancados(vinculo, configuracaoContextoJS.getMes(), configuracaoContextoJS.getAno());
//
//            geraItem(lancadosMesAtual, configuracaoContextoJS, vinculo, ficha, itemErroScripts);
//            //eventos lançados retroativos
//            List<EventoFP> eventosLancados = new ArrayList<EventoFP>();
//            eventosLancados = recuperaEventosLancadosRetroativos(vinculo);
//            if (eventosLancados != null) {
//                ResultadoCalculoRetroativoFP res = this.calculoRetroativoFacade.verifica(vinculo);
//                if (res != null && res.temCalculoRetroativo()) {
//                    calculaRetroativoLancamento(res, eventosLancados, configuracaoContextoJS, vinculo, ficha, itemErroScripts);
//                }
//
//            }
//        }
//    }
    public List<VinculoFP> recuperaContratosExonerados(FolhaDePagamento folha) {

//PODE RETORNAR CONTRATO.. E OS OUTROS VINCULOS?
        String hql = "select obj from ExoneracaoRescisao recisao "
            + " inner join recisao.vinculoFP obj "
            + " where ((extract(month from recisao.dataRescisao)) = :mes) and "
            + " ((extract(year from recisao.dataRescisao)) = :ano) "
            + " and obj not in(select vin from FichaFinanceiraFP ficha "
            + " inner join ficha.folhaDePagamento folha "
            + " inner join ficha.vinculoFP vin "
            + " where folha.efetivadaEm != null and folha.tipoFolhaDePagamento =:tipoFolha) ";


        Query q = em.createQuery(hql);
        q.setParameter("tipoFolha", TipoFolhaDePagamento.RESCISAO);
        q.setParameter("mes", folha.getMes().getNumeroMes());
        q.setParameter("ano", folha.getAno());


        return q.getResultList();
    }

    public List<VinculoFP> recuperaVinculosPorTipoFolhaMesEAno(Mes mes, Integer ano, TipoFolhaDePagamento tipo) {
        String hql = "select vin from FichaFinanceiraFP ficha "
            + " inner join ficha.folhaDePagamento folha "
            + " inner join ficha.vinculoFP vin "
            + " where folha.mes = :mes and folha.ano = :ano and folha.tipoFolhaDePagamento =:tipoFolha ";
        Query q = em.createQuery(hql);
        q.setParameter("tipoFolha", tipo);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);


        return q.getResultList();
    }

    public List<Long> buscarIdsFichas(RegistroEventoEsocial registroEventoEsocial, Long vinculoFP, List<String> tipoFolha) {
        String sql = " select distinct ficha.id" +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " and folha.ANO = :ano and folha.mes = :mes ";
        if (tipoFolha != null) {
            sql += " and folha.tipoFolhaDePagamento in :tipoFolha ";
        }
        sql += " and vinculo.id = :idVinculo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        if (tipoFolha != null) {
            q.setParameter("tipoFolha", tipoFolha);
        }
        q.setParameter("idVinculo", vinculoFP);
        List resultList = q.getResultList();
        List<Long> ids = Lists.newArrayList();
        for (Object o : resultList) {
            ids.add(((BigDecimal) o).longValue());
        }
        return ids;
    }


    public List<Long> buscarIdsFichasS1210(RegistroEventoEsocial registroEventoEsocial, Long idPessoa, List<Long> idsUnidade) {
        String sql = " select distinct ficha.id" +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join MATRICULAFP mat on vinculo.MATRICULAFP_ID = mat.ID " +
            " inner join PESSOAFISICA pf on mat.PESSOA_ID = pf.ID " +
            " where folha.ANO = :ano and folha.mes = :mes " +
            " and pf.ID = :idPessoa " +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("unidades", idsUnidade);

        List resultList = q.getResultList();
        List<Long> ids = Lists.newArrayList();
        for (Object o : resultList) {
            ids.add(((BigDecimal) o).longValue());
        }
        return ids;
    }

    public List<Long> buscarIdsFichasMensalOrDecimo(RegistroEventoEsocial registroEventoEsocial, Long idPessoa, List<Long> idsUnidade, VinculoFP vinculoFP, TipoRegimePrevidenciario tipoRegime) {
        String sql = " select distinct ficha.id" +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join MATRICULAFP mat on vinculo.MATRICULAFP_ID = mat.ID " +
            " inner join PESSOAFISICA pf on mat.PESSOA_ID = pf.ID " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = vinculo.id " +
            " inner join tipoprevidenciafp tiporegime on previdencia.tipoprevidenciafp_id = tiporegime.id " +
            " where folha.ANO = :ano " +
            " and pf.ID = :idPessoa " +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades) " +
            " and tiporegime.tiporegimeprevidenciario = :tipoRegimePrevidencia " +
            " and to_date(folha.MES + 1 || '/' || folha.ANO, 'MM/yyyy') between trunc(to_date(to_char(previdencia.iniciovigencia, 'MM/yyyy'), 'MM/yyyy')) " + " and coalesce(trunc(to_date(to_char(previdencia.FINALVIGENCIA, 'MM/yyyy'), 'MM/yyyy')), to_date(folha.MES + 1 || '/' || folha.ANO, 'MM/yyyy'))";
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            sql += " and folha.mes = :mes " +
                " and folha.tipoFolhaDePagamento <> :tipoFolhaDecimo ";
        } else {
            sql += " and folha.tipoFolhaDePagamento = :tipoFolhaDecimo ";
        }
        if (vinculoFP != null) {
            sql += " and vinculo.ID = :vinculo ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("unidades", idsUnidade);
        q.setParameter("tipoFolhaDecimo", TipoFolhaDePagamento.SALARIO_13.name());
        q.setParameter("tipoRegimePrevidencia", tipoRegime.name());
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        }
        if (vinculoFP != null) {
            q.setParameter("vinculo", vinculoFP.getId());
        }
        List resultList = q.getResultList();
        List<Long> ids = Lists.newArrayList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object o : resultList) {
                ids.add(((BigDecimal) o).longValue());
            }
        }
        return ids;
    }

    public List<Long> buscarIdsFichasMensalOrDecimoS1200(RegistroEventoEsocial registroEventoEsocial, Long idPessoa, List<Long> idsUnidade, VinculoFP vinculoFP, TipoRegimePrevidenciario tipoRegime) {
        String sql = " select distinct ficha.id                   idFicha, " +
            "                folha.tipoFolhaDePagamento           tipoFolha, " +
            "                tiporegimejuridico.codigo            tipoRegime" +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join MATRICULAFP mat on vinculo.MATRICULAFP_ID = mat.ID " +
            " inner join PESSOAFISICA pf on mat.PESSOA_ID = pf.ID " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = vinculo.id " +
            " inner join tipoprevidenciafp tiporegime on previdencia.tipoprevidenciafp_id = tiporegime.id " +
            " left join contratofp contrato on vinculo.id = contrato.id " +
            " left join tipoRegime tiporegimejuridico on contrato.tipoRegime_id = tiporegimejuridico.id " +
            " where folha.ANO = :ano " +
            " and pf.ID = :idPessoa " +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades) " +
            " and tiporegime.tiporegimeprevidenciario = :tipoRegimePrevidencia " +
            " and to_date(folha.MES + 1 || '/' || folha.ANO, 'MM/yyyy') between trunc(to_date(to_char(previdencia.iniciovigencia, 'MM/yyyy'), 'MM/yyyy')) " + " and coalesce(trunc(to_date(to_char(previdencia.FINALVIGENCIA, 'MM/yyyy'), 'MM/yyyy')), to_date(folha.MES + 1 || '/' || folha.ANO, 'MM/yyyy'))" +
            "  and folha.efetivadaEm is not null ";
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            sql += " and folha.mes = :mes " +
                " and folha.tipoFolhaDePagamento <> :tipoFolhaDecimo ";
        } else {
            sql += " and folha.tipoFolhaDePagamento = :tipoFolhaDecimo ";
        }
        if (vinculoFP != null) {
            sql += " and vinculo.ID = :vinculo ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("unidades", idsUnidade);
        q.setParameter("tipoFolhaDecimo", TipoFolhaDePagamento.SALARIO_13.name());
        q.setParameter("tipoRegimePrevidencia", tipoRegime.name());
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        }
        if (vinculoFP != null) {
            q.setParameter("vinculo", vinculoFP.getId());
        }

        List resultList = q.getResultList();
        List<Long> ids = Lists.newArrayList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                VinculoS1200VO vinculo = new VinculoS1200VO();
                vinculo.setIdFicha(Long.parseLong(obj[0].toString()));
                vinculo.setTipoFolhaDePagamento((String) obj[1]);
                vinculo.setTipoRegimeJuridico(obj[2] != null ? Long.parseLong(obj[2].toString()) : null);

                if (TipoRegime.CELETISTA.equals(vinculo.getTipoRegimeJuridico())) {
                    if (vinculo.getTipoFolhaDePagamento() == null || !TipoFolhaDePagamento.RESCISAO.equals(TipoFolhaDePagamento.valueOf(vinculo.getTipoFolhaDePagamento()))) {
                        ids.add(vinculo.getIdFicha());
                    }
                } else {
                    ids.add(vinculo.getIdFicha());
                }
            }
        }
        return ids;
    }

    public List<VinculoFPEventoEsocial> buscarVinculosPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidades(RegistroEventoEsocial registroEventoEsocial, List<Long> idsUnidade,
                                                                                                      VinculoFP vinculoFP, List<String> tipoFolha, boolean semRescisao,
                                                                                                      boolean somenteNaoEnviados, TipoArquivoESocial tipoArquivoESocial,
                                                                                                      TipoRegimePrevidenciario tipoRegime, PessoaFisica pessoaVinculo) {
        String sql = " select distinct vinculo.id idVinculo, pf.NOME, matricula.MATRICULA || '/' || vinculo.NUMERO matricula, pf.id idPessoa" +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.ID " +
            " inner join PESSOAFISICA pf on matricula.PESSOA_ID = pf.ID " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = vinculo.id " +
            " inner join tipoprevidenciafp tiporegime on previdencia.tipoprevidenciafp_id = tiporegime.id " +
            " inner join CategoriaTrabalhador categoria on vinculo.categoriaTrabalhador_id = categoria.id" +
            " where tiporegime.tiporegimeprevidenciario = :tipoRegimePrevidencia " +
            " and to_date(:dataOperacao, 'MM/yyyy') between trunc(to_date(to_char(previdencia.iniciovigencia, 'MM/yyyy'), 'MM/yyyy')) " +
            " and coalesce(trunc(to_date(to_char(previdencia.FINALVIGENCIA, 'MM/yyyy'), 'MM/yyyy')), to_date(:dataOperacao, 'MM/yyyy'))" +
            " and folha.ANO = :ano and folha.mes = :mes ";
        if (tipoFolha != null) {
            sql += " and folha.tipoFolhaDePagamento in :tipoFolha ";
        }
        sql += " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        if (semRescisao) {
            sql += " and not exists(select vinc.id from vinculofp vinc  " +
                "                           where vinc.id = vinculo.id          " +
                "                           and (vinc.finalvigencia is not null and (extract(month from vinc.finalvigencia) - 1) = :mes) " +
                "                           and (vinc.finalvigencia is not null and extract(year from vinc.finalvigencia) = :ano))       ";
        }
        if (vinculoFP != null) {
            sql += " and vinculo.id = :idVinculo";
        }
        if (pessoaVinculo != null) {
            sql += " and pf.ID = :idPessoa";
        }
        if (somenteNaoEnviados) {
            List<NamespacesXmlEsocial> namespaces = NamespacesXmlEsocial.getNamespacesPorEvento(tipoArquivoESocial);
            sql += "  and not exists(select * " +
                "                 from REGISTROESOCIAL registro " +
                "                 where registro.IDESOCIAL like to_char('%' || vinculo.id || '%') " +
                "                   and registro.TIPOARQUIVOESOCIAL = :evento " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "                   and  EXTRACTVALUE(xmltype(XML), '";
            sql += namespaces.get(0).getCaminhoCPF();
            sql += "' , (case ";
            for (NamespacesXmlEsocial n : namespaces) {
                sql += " when xml like '%" + n.name().substring(6) + "%' then '" + n.getDescricao() + "'";
            }
            sql += " end)) like pf.cpf " +
                "    and xml like :mesAno )";
        }

        sql += " order by pf.NOME, matricula";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoRegimePrevidencia", tipoRegime.name());
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        if (tipoFolha != null) {
            q.setParameter("tipoFolha", tipoFolha);
        }
        q.setParameter("unidades", idsUnidade);
        q.setParameter("dataOperacao", DataUtil.criarDataPrimeiroDiaMes(registroEventoEsocial.getMes().getNumeroMes(), registroEventoEsocial.getExercicio().getAno()).format(DateTimeFormatter.ofPattern("MM/yyyy")));
        if (vinculoFP != null) {
            q.setParameter("idVinculo", vinculoFP.getId());
        }
        if (pessoaVinculo != null) {
            q.setParameter("idPessoa", pessoaVinculo.getId());
        }
        if (somenteNaoEnviados) {
            q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("evento", tipoArquivoESocial.name());
            String mesAno = registroEventoEsocial.getExercicio().getAno().toString().concat("-").concat(registroEventoEsocial.getMes().getNumeroMesString());
            q.setParameter("mesAno", "%<perApur>" + mesAno + "%");
        }

        List resultList = q.getResultList();

        if (!resultList.isEmpty()) {
            List<VinculoFPEventoEsocial> item = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) resultList) {
                boolean podeAdicionar = true;
                VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
                vinculoFPEventoEsocial.setIdVinculo(Long.parseLong(obj[0].toString()));
                vinculoFPEventoEsocial.setNome((String) obj[1]);
                vinculoFPEventoEsocial.setMatricula((String) obj[2]);
                vinculoFPEventoEsocial.setIdPessoa(Long.parseLong(obj[3].toString()));
                vinculoFPEventoEsocial.setRegistroEventoEsocial(registroEventoEsocial);
                if (TipoClasseESocial.S1210.equals(registroEventoEsocial.getTipoClasseESocial())) {
                    for (VinculoFPEventoEsocial v : item) {
                        if (v.getIdPessoa().equals(vinculoFPEventoEsocial.getIdPessoa())) {
                            podeAdicionar = false;
                        }
                    }
                }
                if (podeAdicionar) {
                    item.add(vinculoFPEventoEsocial);
                }
            }
            return item;
        }
        return null;
    }

    public List<VinculoFPEventoEsocial> buscarVinculosTipoPrevidenciaRppsSemRescisaoNoMesSelecionado(RegistroEventoEsocial selecionado, List<Long> idsUnidade,
                                                                                                     VinculoFP vinculoFP, Date dataOperacao,
                                                                                                     TipoArquivoESocial tipoArquivoESocial,
                                                                                                     boolean somenteNaoEnviados) {
        String sql = " select distinct vinculo.id, pf.nome, matricula.matricula || '/' || vinculo.numero, ficha.id as idFicha" +
            " from fichafinanceirafp ficha                                                                " +
            " inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id                   " +
            " inner join vinculofp vinculo on ficha.vinculofp_id = vinculo.id                             " +
            " inner join matriculafp matricula on vinculo.matriculafp_id = matricula.id                   " +
            " inner join pessoafisica pf on matricula.pessoa_id = pf.id                                   " +
            " inner join itemfichafinanceirafp item on ficha.id = item.fichafinanceirafp_id               " +
            " inner join competenciafp competencia on folha.competenciafp_id = competencia.id             " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = vinculo.id       " +
            " inner join tipoprevidenciafp tiporegime on previdencia.tipoprevidenciafp_id = tiporegime.id " +
            " inner join categoriatrabalhador categoria on vinculo.categoriatrabalhador_id = categoria.id " +
            " where tiporegime.tiporegimeprevidenciario = :tipoRegimePrevidencia                          " +
            " and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(previdencia.iniciovigencia)          " +
            "    and coalesce(trunc(previdencia.finalvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))     " +
            " and folha.ano = :ano and folha.mes = :mes and folha.tipofolhadepagamento = :tipoFolha       " +
            " and vinculo.unidadeorganizacional_id in :unidades " +
            " and not exists(select vinc.id from vinculofp vinc " +
            "               where vinc.id = vinculo.id          " +
            "               and (vinc.finalvigencia is not null and (extract(month from vinc.finalvigencia) - 1) = :mes) " +
            "               and (vinc.finalvigencia is not null and extract(year from vinc.finalvigencia) = :ano))       " +
            " and (categoria.tipo = :tipoCategoria3 or categoria.codigo = :categoria4) " +
            (vinculoFP != null ? " and vinculo.id = :idVinculo " : "") ;

        if (somenteNaoEnviados) {
            sql += "  and not exists(select * " +
                "                 from REGISTROESOCIAL registro " +
                "                 where registro.IDESOCIAL like to_char('%' || vinculo.id || '%')" +
                "                   and registro.IDESOCIAL like '%' || folha.mes || '%' " +
                "                   and registro.IDESOCIAL like '%' || folha.ano || '%' " +
                "                   and registro.TIPOARQUIVOESOCIAL = :evento " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    )";
        }
        sql +=  " order by pf.nome ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoRegimePrevidencia", TipoRegimePrevidenciario.RPPS.name());
        q.setParameter("ano", selecionado.getExercicio().getAno());
        q.setParameter("mes", selecionado.getMes().getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", selecionado.getTipoFolhaDePagamento().name());
        q.setParameter("unidades", idsUnidade);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipoCategoria3", TipoGrupoCategoriaTrabalhador.AGENTE_PUBLICO.name());
        q.setParameter("categoria4", "410");
        if (somenteNaoEnviados) {
            q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("evento", tipoArquivoESocial.name());
        }
        if (vinculoFP != null) {
            q.setParameter("idVinculo", vinculoFP.getId());
        }

        List<Object[]> resultado = q.getResultList();
        List<VinculoFPEventoEsocial> vinculosESocial = Lists.newArrayList();

        for (Object[] obj : resultado) {
            VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
            vinculoFPEventoEsocial.setIdVinculo(Long.parseLong(obj[0].toString()));
            vinculoFPEventoEsocial.setNome((String) obj[1]);
            vinculoFPEventoEsocial.setMatricula((String) obj[2]);
            vinculoFPEventoEsocial.setIdFichaFinanceira(Long.parseLong(obj[3].toString()));

            vinculosESocial.add(vinculoFPEventoEsocial);
        }
        return !vinculosESocial.isEmpty() ? vinculosESocial : null;
    }

    public List<VinculoFPEventoEsocial> buscarVinculosPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidadesEventoS1210(RegistroEventoEsocial registroEventoEsocial, List<Long> idsUnidade,
                                                                                                                 VinculoFP vinculoFP, TipoArquivoESocial tipoArquivoESocial,
                                                                                                                 PessoaFisica pessoaVinculo, ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial,
                                                                                                                 TipoRegimePrevidenciario tipoRegimePrevidenciario) {
        String sql = " select distinct vinculo.id idVinculo, " +
            "   pf.NOME, " +
            "   matricula.MATRICULA || '/' || vinculo.NUMERO matricula, " +
            "   pf.id idPessoa, " +
            "   pf.cpf cpf " +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.ID " +
            " inner join PESSOAFISICA pf on matricula.PESSOA_ID = pf.ID ";
        if (tipoRegimePrevidenciario != null) {
            sql += "  inner join  contratofp on vinculo.ID = CONTRATOFP.ID\n" +
                "     inner join PrevidenciaVinculoFP previdencia on CONTRATOFP.ID = previdencia.CONTRATOFP_ID and folha.EFETIVADAEM between previdencia.INICIOVIGENCIA and coalesce(previdencia.FINALVIGENCIA, folha.EFETIVADAEM)\n" +
                "     inner join TipoPrevidenciaFP tipo on previdencia.TIPOPREVIDENCIAFP_ID = tipo.ID ";
        }
        sql += " where  folha.ANO = :ano and folha.mes = :mes " +
            "    and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades) ";
        if (vinculoFP != null) {
            sql += " and vinculo.id = :idVinculo";
        }
        if (pessoaVinculo != null) {
            sql += " and pf.ID = :idPessoa";
        }
        if (tipoRegimePrevidenciario != null) {
            sql += " and tipo.TIPOREGIMEPREVIDENCIARIO = :regimePrevidenciario ";
        }
        sql += " order by pf.NOME, matricula";
        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        q.setParameter("unidades", idsUnidade);

        if (vinculoFP != null) {
            q.setParameter("idVinculo", vinculoFP.getId());
        }
        if (pessoaVinculo != null) {
            q.setParameter("idPessoa", pessoaVinculo.getId());
        }

        if (tipoRegimePrevidenciario != null) {
            q.setParameter("regimePrevidenciario", tipoRegimePrevidenciario.name());
        }

        List resultList = q.getResultList();

        if (!resultList.isEmpty()) {
            List<VinculoFPEventoEsocial> item = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) resultList) {
                boolean podeAdicionar = true;
                VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
                vinculoFPEventoEsocial.setIdVinculo(Long.parseLong(obj[0].toString()));
                vinculoFPEventoEsocial.setNome((String) obj[1]);
                vinculoFPEventoEsocial.setMatricula((String) obj[2]);
                vinculoFPEventoEsocial.setIdPessoa(Long.parseLong(obj[3].toString()));
                vinculoFPEventoEsocial.setCpf((String) obj[4]);
                vinculoFPEventoEsocial.setRegistroEventoEsocial(registroEventoEsocial);
                for (VinculoFPEventoEsocial v : item) {
                    if (v.getIdPessoa().equals(vinculoFPEventoEsocial.getIdPessoa())) {
                        podeAdicionar = false;
                    }
                }
                if (podeAdicionar) {
                    item.add(vinculoFPEventoEsocial);
                }
            }
            return item;
        }
        return null;
    }

    public List<VinculoFPEventoEsocial> buscarVinculosPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidadesS1200(RegistroEventoEsocial registroEventoEsocial, List<Long> idsUnidade, boolean semRescisao,
                                                                                                           TipoRegimePrevidenciario tipoRegime, PessoaFisica pessoaVinculo, ConfiguracaoEmpregadorESocial config) {

        Integer mes = registroEventoEsocial.getMes() != null ? registroEventoEsocial.getMes().getNumeroMes() : DataUtil.dateToLocalDate(new Date()).getMonthValue();
        Date dataReferencia = DataUtil.montaData(1, mes, registroEventoEsocial.getExercicio().getAno()).getTime();
        ContratoFP contratoFP = null;
        if (pessoaVinculo != null) {
            contratoFP = contratoFPFacade.buscarContratoFPPorPessoa(pessoaVinculo.getId(), dataReferencia);
        }

        String sql = " select distinct vinculo.id idVinculo, " +
            " pf.NOME, " +
            " matricula.MATRICULA || '/' || vinculo.NUMERO matricula, " +
            " pf.id idPessoa, " +
            " pf.cpf cpf " +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.ID " +
            " inner join PESSOAFISICA pf on matricula.PESSOA_ID = pf.ID " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = vinculo.id " +
            " inner join tipoprevidenciafp tiporegime on previdencia.tipoprevidenciafp_id = tiporegime.id " +
            " inner join CategoriaTrabalhador categoria on vinculo.categoriaTrabalhador_id = categoria.id" +
            " where tiporegime.tiporegimeprevidenciario = :tipoRegimePrevidencia " +
            " and to_date(folha.MES + 1 || '/' || folha.ANO, 'MM/yyyy') between trunc(to_date(to_char(previdencia.iniciovigencia, 'MM/yyyy'), 'MM/yyyy')) " +
            " and coalesce(trunc(to_date(to_char(previdencia.FINALVIGENCIA, 'MM/yyyy'), 'MM/yyyy')), to_date(folha.MES + 1 || '/' || folha.ANO, 'MM/yyyy'))" +
            " and folha.ANO = :ano and folha.efetivadaEm is not null";

        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            sql += " and folha.mes = :mes " +
                " and folha.tipoFolhaDePagamento <> :tipoFolhaDecimo ";
        } else {
            sql += " and folha.tipoFolhaDePagamento = :tipoFolhaDecimo ";
        }
        if (contratoFP != null && contratoFP.isTipoRegimeCeletista()) {
            sql += " and folha.tipoFolhaDePagamento <> :tipoFolhaRescisao";
        }

        sql += " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        if (pessoaVinculo != null) {
            sql += " and pf.ID = :idPessoa";
        }
        sql += " order by pf.NOME, matricula";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoRegimePrevidencia", tipoRegime.name());
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("tipoFolhaDecimo", TipoFolhaDePagamento.SALARIO_13.name());
        q.setParameter("unidades", idsUnidade);
        if (pessoaVinculo != null) {
            q.setParameter("idPessoa", pessoaVinculo.getId());
        }
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        }

        // caso seja regime juridico celetista nao informar a folha de rescisao no s1200
        if (contratoFP != null && contratoFP.isTipoRegimeCeletista()) {
            q.setParameter("tipoFolhaRescisao", TipoFolhaDePagamento.RESCISAO.name());
        }

        List resultList = q.getResultList();

        if (!resultList.isEmpty()) {
            List<VinculoFPEventoEsocial> item = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) resultList) {
                boolean podeAdicionar = true;
                VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
                vinculoFPEventoEsocial.setIdVinculo(Long.parseLong(obj[0].toString()));
                vinculoFPEventoEsocial.setNome((String) obj[1]);
                vinculoFPEventoEsocial.setMatricula((String) obj[2]);
                vinculoFPEventoEsocial.setIdPessoa(Long.parseLong(obj[3].toString()));
                vinculoFPEventoEsocial.setCpf((String) obj[4]);
                vinculoFPEventoEsocial.setRegistroEventoEsocial(registroEventoEsocial);
                for (VinculoFPEventoEsocial v : item) {
                    if (v.getIdPessoa().equals(vinculoFPEventoEsocial.getIdPessoa())) {
                        podeAdicionar = false;
                    }
                }
                if (podeAdicionar) {
                    item.add(vinculoFPEventoEsocial);
                }
            }
            return item;
        }
        return null;
    }

    public List<String> recuperarCPFXmlsEventosEnviadosPorMesAnoETipo(RegistroEventoEsocial registroEventoEsocial, TipoArquivoESocial tipoArquivoESocial, ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial) {
        List<NamespacesXmlEsocial> namespaces = NamespacesXmlEsocial.getNamespacesPorEvento(tipoArquivoESocial);

        String sql = "select EXTRACTVALUE(xmltype(XML), '";
        sql += namespaces.get(0).getCaminhoCPF();
        sql += "' , (case ";
        for (NamespacesXmlEsocial n : namespaces) {
            sql += " when xml like '%" + n.name().substring(6) + "%' then '" + n.getDescricao() + "'";
        }
        sql += " end)) as CPF " +
            " from REGISTROESOCIAL registro " +
            " where registro.TIPOARQUIVOESOCIAL = :evento " +
            "  and registro.SITUACAO in (:situacoes) " +
            "  and xml like :mesAno " +
            "  and registro.EMPREGADOR_ID = :empregador ";
        Query q = em.createNativeQuery(sql);

        q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
        q.setParameter("evento", tipoArquivoESocial.name());
        String mesAno = registroEventoEsocial.getExercicio().getAno().toString();
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha()) || TipoClasseESocial.S1210.equals(registroEventoEsocial.getTipoClasseESocial())) {
            mesAno = mesAno.concat("-").concat(registroEventoEsocial.getMes().getNumeroMesString());
        }
        q.setParameter("mesAno", "%<perApur>" + mesAno + "</perApur>%");
        q.setParameter("empregador", configuracaoEmpregadorESocial.getId());

        List<String> retorno = q.getResultList();
        if (retorno != null && !retorno.isEmpty()) {
            return retorno;
        }
        return Lists.newArrayList();
    }

    public List<RegistroESocial> recuperarRegistroEsocialAnoMesEmpregador(TipoArquivoESocial tipoArquivo, Integer mes,
                                                                          Integer ano,
                                                                          ConfiguracaoEmpregadorESocial config) {
        String sql = "select * " +
            " from REGISTROESOCIAL  " +
            " where TIPOARQUIVOESOCIAL = :tipoArquivo and (SITUACAO = :sucesso or SITUACAO = :advertencia) and" +
            " mesapuracao = :mes and anoApuracao = :ano and empregador_id = :idEmpregador";
        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("tipoArquivo", tipoArquivo.name());
        q.setParameter("sucesso", SituacaoESocial.PROCESSADO_COM_SUCESSO.name());
        q.setParameter("advertencia", SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name());
        q.setParameter("idEmpregador", config.getId());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    public List<RegistroESocial> recuperarRegistroEsocialAnoEmpregador(TipoArquivoESocial tipoArquivo,
                                                                       Integer ano,
                                                                       ConfiguracaoEmpregadorESocial config) {
        String sql = "select * " +
            " from REGISTROESOCIAL  " +
            " where TIPOARQUIVOESOCIAL = :tipoArquivo and (SITUACAO = :sucesso or SITUACAO = :advertencia) " +
            " and anoApuracao = :ano and empregador_id = :idEmpregador";
        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("tipoArquivo", tipoArquivo.name());
        q.setParameter("sucesso", SituacaoESocial.PROCESSADO_COM_SUCESSO.name());
        q.setParameter("advertencia", SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name());
        q.setParameter("idEmpregador", config.getId());
        q.setParameter("ano", ano);
        return q.getResultList();
    }


    public List<VinculoFPEventoEsocial> buscarAposentadosPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidades(RegistroEventoEsocial registroEventoEsocial,
                                                                                                         List<Long> idsUnidade,
                                                                                                         boolean isSomenteNaoEnviados) {
        String sql = " select distinct vinculo.id, pf.NOME, matricula.MATRICULA || '/' || vinculo.NUMERO, ficha.id as idFicha" +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.ID " +
            " inner join PESSOAFISICA pf on matricula.PESSOA_ID = pf.ID " +
            " inner join aposentadoria on aposentadoria.id = vinculo.id" +
            " where folha.ANO = :ano " +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        if (registroEventoEsocial.getPessoaFisica() != null) {
            sql += " and pf.id = :idPF";
        }
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            sql += " and folha.mes = :mes " +
                " and folha.tipoFolhaDePagamento <> :tipoFolhaDecimo ";
        } else {
            sql += " and folha.tipoFolhaDePagamento = :tipoFolhaDecimo ";
        }
        if (isSomenteNaoEnviados) {
            sql += "  and not exists(select * " +
                "                 from REGISTROESOCIAL registro " +
                "                 where registro.IDESOCIAL like to_char('%' || :idEsocial || vinculo.id || '%')" +
                "                   and registro.TIPOARQUIVOESOCIAL = :evento " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    )";
        }
        sql += " order by pf.NOME";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("tipoFolhaDecimo", TipoFolhaDePagamento.SALARIO_13.name());
        q.setParameter("unidades", idsUnidade);
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        }
        if (isSomenteNaoEnviados) {
            q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("evento", TipoArquivoESocial.S1207.name());
            q.setParameter("idEsocial", registroEventoEsocial.getExercicio().getAno().toString().concat(registroEventoEsocial.getMes().getNumeroMes().toString()));
        }
        if (registroEventoEsocial.getPessoaFisica() != null) {
            q.setParameter("idPF", registroEventoEsocial.getPessoaFisica().getId());
        }
        List<Object[]> resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            List<VinculoFPEventoEsocial> item = Lists.newArrayList();
            for (Object[] obj : resultList) {
                VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
                vinculoFPEventoEsocial.setIdVinculo(Long.parseLong(obj[0].toString()));
                vinculoFPEventoEsocial.setNome((String) obj[1]);
                vinculoFPEventoEsocial.setMatricula((String) obj[2]);
                vinculoFPEventoEsocial.setIdFichaFinanceira(Long.parseLong(obj[3].toString()));
                vinculoFPEventoEsocial.setRegistroEventoEsocial(registroEventoEsocial);
                item.add(vinculoFPEventoEsocial);
            }
            return item;
        }
        return null;
    }

    public List<VinculoFPEventoEsocial> buscarAposentadosPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidades(RegistroEventoEsocial registroEventoEsocial, List<Long> idsUnidade,
                                                                                                      VinculoFP vinculoFP) {
        String sql = " select distinct vinculo.id, pf.NOME, matricula.MATRICULA || '/' || vinculo.NUMERO, ficha.id as idFicha" +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.ID " +
            " inner join PESSOAFISICA pf on matricula.PESSOA_ID = pf.ID " +
            " inner join aposentadoria on aposentadoria.id = vinculo.id" +
            " where folha.ANO = :ano and folha.mes = :mes and folha.tipoFolhaDePagamento = :tipoFolha" +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)" ;
        if (vinculoFP != null) {
            sql += " and vinculo.id = :idVinculo";
        }
        sql += " order by pf.NOME";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", registroEventoEsocial.getTipoFolhaDePagamento().name());
        q.setParameter("unidades", idsUnidade);
        if (vinculoFP != null) {
            q.setParameter("idVinculo", vinculoFP.getId());
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            List<VinculoFPEventoEsocial> item = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) resultList) {
                VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
                vinculoFPEventoEsocial.setIdVinculo(Long.parseLong(obj[0].toString()));
                vinculoFPEventoEsocial.setNome((String) obj[1]);
                vinculoFPEventoEsocial.setMatricula((String) obj[2]);
                vinculoFPEventoEsocial.setIdFichaFinanceira(Long.parseLong(obj[3].toString()));
                vinculoFPEventoEsocial.setRegistroEventoEsocial(registroEventoEsocial);
                item.add(vinculoFPEventoEsocial);
            }
            return item;
        }
        return null;
    }

    public List<VinculoFPEventoEsocial> buscarVinculosTipoPrevidenciaRppsSemRescisaoNoMesSelecionado(RegistroEventoEsocial registroEventoEsocial, List<Long> idsUnidade,
                                                                                                     PessoaFisica pessoaVinculo, TipoArquivoESocial tipoArquivoESocial,
                                                                                                     ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial) {
        String sql = " select distinct vinculo.id, " +
            " pf.nome, " +
            " matricula.matricula || '/' || vinculo.numero, " +
            " pf.id as idPessoa, " +
            " pf.cpf cpf " +
            " from fichafinanceirafp ficha                                                                " +
            " inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id                   " +
            " inner join vinculofp vinculo on ficha.vinculofp_id = vinculo.id                             " +
            " inner join matriculafp matricula on vinculo.matriculafp_id = matricula.id                   " +
            " inner join pessoafisica pf on matricula.pessoa_id = pf.id                                   " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = vinculo.id       " +
            " inner join tipoprevidenciafp tiporegime on previdencia.tipoprevidenciafp_id = tiporegime.id " +
            " inner join categoriatrabalhador categoria on vinculo.categoriatrabalhador_id = categoria.id " +
            " where tiporegime.tiporegimeprevidenciario = :tipoRegimePrevidencia                          " +
            " and to_date(folha.MES + 1 || '/' || folha.ANO, 'MM/yyyy') between trunc(to_date(to_char(previdencia.iniciovigencia, 'MM/yyyy'), 'MM/yyyy')) " +
            " and coalesce(trunc(to_date(to_char(previdencia.FINALVIGENCIA, 'MM/yyyy'), 'MM/yyyy')), to_date(folha.MES + 1 || '/' || folha.ANO, 'MM/yyyy'))" +
            " and folha.ano = :ano                              " +
            " and vinculo.unidadeorganizacional_id in (:unidades) " +
            " and not exists(select vinc.id from vinculofp vinc " +
            "               where vinc.id = vinculo.id          " +
            "               and (vinc.finalvigencia is not null and (extract(month from vinc.finalvigencia) - 1) = folha.MES) " +
            "               and (vinc.finalvigencia is not null and extract(year from vinc.finalvigencia) = :ano))       " +
            " and (categoria.tipo = :tipoCategoria3 or categoria.codigo = :categoria4) " +
            (pessoaVinculo != null ? " and pf.id = :idPessoaVinculo " : "");
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            sql += " and folha.mes = :mes " +
                " and folha.tipoFolhaDePagamento <> :tipoFolhaDecimo ";
        } else {
            sql += " and folha.tipoFolhaDePagamento = :tipoFolhaDecimo ";
        }
        sql += " order by pf.nome ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoRegimePrevidencia", TipoRegimePrevidenciario.RPPS.name());
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("unidades", idsUnidade);
        q.setParameter("tipoCategoria3", TipoGrupoCategoriaTrabalhador.AGENTE_PUBLICO.name());
        q.setParameter("categoria4", "410");
        q.setParameter("tipoFolhaDecimo", TipoFolhaDePagamento.SALARIO_13.name());
        if (pessoaVinculo != null) {
            q.setParameter("idPessoaVinculo", pessoaVinculo.getId());
        }
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        }

        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            List<String> cpf = Lists.newArrayList();
            List<VinculoFPEventoEsocial> item = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) resultList) {
                boolean podeAdicionar = true;
                VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
                vinculoFPEventoEsocial.setIdVinculo(Long.parseLong(obj[0].toString()));
                vinculoFPEventoEsocial.setNome((String) obj[1]);
                vinculoFPEventoEsocial.setMatricula((String) obj[2]);
                vinculoFPEventoEsocial.setIdPessoa(Long.parseLong(obj[3].toString()));
                vinculoFPEventoEsocial.setRegistroEventoEsocial(registroEventoEsocial);
                for (VinculoFPEventoEsocial v : item) {
                    if (v.getIdPessoa().equals(vinculoFPEventoEsocial.getIdPessoa())) {
                        podeAdicionar = false;
                    }
                }
                if (podeAdicionar) {
                    item.add(vinculoFPEventoEsocial);
                }
            }
            return item;
        }
        return null;
    }

    public List<VinculoFPEventoEsocial> buscarVinculosComSindicatoPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidades(RegistroEventoEsocial registroEventoEsocial, List<Long> idsUnidade,
                                                                                                                  VinculoFP vinculoFP) {
        String sql = " select distinct vinculo.id, pf.NOME, matricula.MATRICULA || '/' || vinculo.NUMERO, item.VALOR as valor" +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.ID " +
            " inner join PESSOAFISICA pf on matricula.PESSOA_ID = pf.ID " +
            " inner join SindicatoVinculoFP sindicatovinculo on sindicatovinculo.VINCULOFP_ID = vinculo.ID " +
            " inner join sindicato on sindicatovinculo.SINDICATO_ID = sindicato.ID " +
            " inner join eventofp eventosindical on sindicato.EVENTOFP_ID = eventosindical.id " +
            " where folha.ANO = :ano and folha.mes = :mes and folha.tipoFolhaDePagamento = :tipoFolha " +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades) " +
            " and item.EVENTOFP_ID = eventosindical.id";
        if (vinculoFP != null) {
            sql += " and vinculo.id = :idVinculo";
        }
        sql += " order by pf.NOME";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", registroEventoEsocial.getTipoFolhaDePagamento().name());
        q.setParameter("unidades", idsUnidade);
        if (vinculoFP != null) {
            q.setParameter("idVinculo", vinculoFP.getId());
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            List<VinculoFPEventoEsocial> item = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) resultList) {
                VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
                vinculoFPEventoEsocial.setIdVinculo(Long.parseLong(obj[0].toString()));
                vinculoFPEventoEsocial.setNome((String) obj[1]);
                vinculoFPEventoEsocial.setMatricula((String) obj[2]);
                vinculoFPEventoEsocial.setValorEventoFP((BigDecimal) obj[3]);
                vinculoFPEventoEsocial.setRegistroEventoEsocial(registroEventoEsocial);
                item.add(vinculoFPEventoEsocial);
            }
            return item;
        }
        return null;
    }

    public List<VinculoFPEventoEsocial> buscarVinculosPorMesAnoAndUnidades(RegistroEventoEsocial selecionado, List<Long> idsUnidade,
                                                                           VinculoFP vinculoFP, List<String> modalidades, boolean filtrarModalidade) {

        String sql = " select distinct vinculo.id, pf.nome, matricula.matricula || '/' || vinculo.numero " +
            " from fichafinanceirafp ficha                                                                   " +
            " inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id                      " +
            " inner join vinculofp vinculo on ficha.vinculofp_id = vinculo.id                                " +
            " inner join contratofp contrato on vinculo.id = contrato.id                                     " +
            " inner join matriculafp matricula on vinculo.matriculafp_id = matricula.id                      " +
            " inner join pessoafisica pf on matricula.pessoa_id = pf.id                                      " +
            " inner join itemfichafinanceirafp item on ficha.id = item.fichafinanceirafp_id                  " +
            " inner join competenciafp competencia on folha.competenciafp_id = competencia.id                " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = vinculo.id          " +
            " inner join modalidadecontratofp modalidade on modalidade.id = contrato.modalidadecontratofp_id " +
            " where folha.ano = :ano and folha.mes = :mes and folha.tipofolhadepagamento = :tipoFolha        " +
            " and vinculo.unidadeorganizacional_id in :unidades " +
            " and modalidade.codigo " + (filtrarModalidade ? " in " : " not in ") + " :modalidades " +
            (filtrarModalidade ? " and (vinculo.finalvigencia is not null and extract(year from vinculo.finalvigencia) = :ano " +
                "           and extract(month from vinculo.finalvigencia) = (:mes + 1)) " : "") +
            (vinculoFP != null ? " and vinculo.id = :idVinculo " : "") + " order by pf.nome ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", selecionado.getExercicio().getAno());
        q.setParameter("mes", selecionado.getMes().getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", selecionado.getTipoFolhaDePagamento().name());
        q.setParameter("unidades", idsUnidade);
        q.setParameter("modalidades", modalidades);
        if (vinculoFP != null) {
            q.setParameter("idVinculo", vinculoFP.getId());
        }

        List<Object[]> resultado = q.getResultList();
        List<VinculoFPEventoEsocial> vinculosESocial = Lists.newArrayList();

        for (Object[] obj : resultado) {
            VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
            vinculoFPEventoEsocial.setRegistroEventoEsocial(selecionado);
            vinculoFPEventoEsocial.setIdVinculo(Long.parseLong(obj[0].toString()));
            vinculoFPEventoEsocial.setNome((String) obj[1]);
            vinculoFPEventoEsocial.setMatricula((String) obj[2]);
            vinculosESocial.add(vinculoFPEventoEsocial);
        }
        return !vinculosESocial.isEmpty() ? vinculosESocial : null;
    }

    public List<VinculoFP> buscarVinculosPorTipoFolhaMesEAnoAndFolhaEfetivada(Mes mes, Integer ano, TipoFolhaDePagamento tipo, VinculoFP vinculoFP) {
        String hql = "select distinct  vin from FichaFinanceiraFP ficha "
            + " inner join ficha.folhaDePagamento folha "
            + " inner join ficha.vinculoFP vin "
            + " inner join ficha.itemFichaFinanceiraFP item "
            + " inner join folha.competenciaFP comp "
            + " where folha.mes = :mes and folha.ano = :ano and folha.tipoFolhaDePagamento = :tipoFolha "
            + "   and folha.efetivadaEm is not null";
        if (vinculoFP != null) {
            hql += " and ficha.vinculoFP.id = :id ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("tipoFolha", tipo);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        if (vinculoFP != null) {
            q.setParameter("id", vinculoFP.getId());
        }
        return q.getResultList();
    }

    //    public void gerarFolha(final List<VinculoFP> vinculosFP, final FolhaDePagamento folha, ConfiguracaoContextoJS configuracaoContextoJS, List<ItemErroScript> itemErroScripts) {
//
//        folha.setCalculadaEm(new Date());
//        //System.out.println("ORDINAL : " + folha.getMes().ordinal());
//        //System.out.println("ORDINAL MES: " + folha.getMes().getDescricao());
//        List<EventoFP> eventosAutomaticos = eventoFPFacade.listaEventosAtivosPorTipoExecucao(TipoExecucaoEP.FOLHA, true);
//        funcoesFolhaFacade.setFolha(folha);
//        em.persist(folha);
//        Integer count = vinculosFP.size();
//        for (VinculoFP vinculo : vinculosFP) {
//            count--;
////            //System.out.println("Quantidade de Processados: " + count);
//            FichaFinanceiraFP ficha = new FichaFinanceiraFP();
//            ficha.setFolhaDePagamento(folha);
//            final RecursoDoVinculoFP recursoDoVinculo = recursoDoVinculoFPFacade.recuperarRecursoDoVinculo(vinculo);
//            if (recursoDoVinculo != null) {
//                ficha.setRecursoFP(recursoDoVinculo.getRecursoFP());
//            }
//
//            ficha.setVinculoFP(vinculo);
//            //em.persist(ficha);
//            //Eventos automaticos...
////            logger.debug("Executando eventos automaticos");
//
//            List<EventoFP> eventosParaCalculo = new ArrayList<>(eventosAutomaticos);
//
//            List<EventoFP> lancadosMesAtual = eventoFPFacade.listaEventosLancados(vinculo, configuracaoContextoJS.getMes(), configuracaoContextoJS.getAno());
//            geraItemLancados(lancadosMesAtual, configuracaoContextoJS, vinculo, ficha, itemErroScripts, eventosParaCalculo);
//
//            geraItem(eventosParaCalculo, configuracaoContextoJS, vinculo, ficha, itemErroScripts);
//            //Eventos com lancamentos...
//
//
//            List<EventoFP> eventosLancados = new ArrayList<EventoFP>();
//            eventosLancados = recuperaEventosLancadosRetroativos(vinculo);
//            if (eventosLancados != null) {
//                ResultadoCalculoRetroativoFP res = this.calculoRetroativoFacade.verifica(vinculo);
//                if (res != null && res.temCalculoRetroativo()) {
//                    calculaRetroativoLancamento(res, eventosLancados, configuracaoContextoJS, vinculo, ficha, itemErroScripts);
//                }
//
//            }
//        }
//
//        if (itemErroScripts.isEmpty()) {
//            //em.merge(folha);
//        } else {
//            for (ItemErroScript item : itemErroScripts) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ops", "Ocorregam erros ao tenter gerar a folha...Evento: " + item.getNomeFunction() + "  Código do erro: " + item.getMensagemException()));
//            }
//        }
//    }
//    private BigDecimal criaValorParaItemFicha(Double valor) {
//        if (valor != null) {
//            return new BigDecimal(valor);
//        }
//        return null;
//    }
//    private void geraItem(List<EventoFP> eventos, ConfiguracaoContextoJS configuracaoContextoJS, VinculoFP vinculo, FichaFinanceiraFP ficha, List<ItemErroScript> itemErroScripts) {
//
//        ResultadoCalculoRetroativoFP res = this.calculoRetroativoFacade.verifica(vinculo);
//        if (res != null && res.temCalculoRetroativo()) {
//            calculaValoresRetroativos(res, vinculo, configuracaoContextoJS, ficha);
//        }
//
//        for (EventoFP evento : eventos) {
//            Resultado<Boolean> resultadoRegra = configuracaoContextoJS.getExecutaScriptEventoFP().executaRegra(evento, vinculo);
//            if (resultadoRegra.naoTemErro()) {
////                logger.log(Level.INFO, "Regra de Evento: [{0}], n\u00e3o tem erro.", evento.getDescricao());
//                if (resultadoRegra.getValor()) {
////                    logger.log(Level.INFO, "Regra de Evento: [{0}], deve ser executada.", evento.getDescricao());
//                    Resultado<Double> resultadoFormula = configuracaoContextoJS.getExecutaScriptEventoFP().executaFormula(evento, vinculo);
//                    Resultado<Double> resultadoValorIntegral = configuracaoContextoJS.getExecutaScriptEventoFP().executaValorIntegral(evento, vinculo);
//                    Resultado<Double> resultadoReferencia = configuracaoContextoJS.getExecutaScriptEventoFP().executaReferencia(evento, vinculo);
//                    Resultado<Double> resultadoValorBaseDeCalculo = configuracaoContextoJS.getExecutaScriptEventoFP().executaValorBaseDeCalculo(evento, vinculo);
//                    if (resultadoFormula.naoTemErro() && resultadoReferencia.naoTemErro() && resultadoValorIntegral.naoTemErro()) {
////                        logger.log(Level.INFO, "Regra de Evento: [{0}], n\u00e3o possui nenhum erro.", evento.getDescricao());
//                        ItemFichaFinanceiraFP itemFicha = new ItemFichaFinanceiraFP();
//                        itemFicha.setTipoCalculoFP(TipoCalculoFP.NORMAL);
//                        itemFicha.setEventoFP(evento);
//                        itemFicha.setFichaFinanceiraFP(ficha);
////                        logger.log(Level.INFO, "Evento: {0}", evento.getCodigo());
//
//                        final Double valorFormula = resultadoFormula.getValor();
////                        logger.log(Level.INFO, "Valor Formula: {0}", valorFormula);
//                        itemFicha.setAno(ficha.getFolhaDePagamento().getAno());
//                        itemFicha.setMes(ficha.getFolhaDePagamento().getMes().ordinal() + 1);
//                        itemFicha.valorDouble(valorFormula);
//                        itemFicha.setValorIntegral(criaValorParaItemFicha(resultadoValorIntegral.getValor()));
//
//                        final Double valorRef = resultadoReferencia.getValor();
////                        logger.log(Level.INFO, "Valor Referencia: {0}", valorRef);
//
//                        itemFicha.setValorReferencia(criaValorParaItemFicha(valorRef));
//                        itemFicha.setUnidadeReferencia(evento.getUnidadeReferencia());
//
//                        final Double valorBaseDeCalculo = resultadoValorBaseDeCalculo.getValor();
////                        logger.log(Level.INFO, "Valor Base de Calculo: {0}", valorBaseDeCalculo);
//                        itemFicha.setValorBaseDeCalculo(criaValorParaItemFicha(valorBaseDeCalculo));
//
//                        if (itemErroScripts.isEmpty()) {
////                            logger.logp(Level.INFO, FolhaDePagamentoFacade.class.getName(), "gerarFichaFinanceira", "Persistindo item: [" + itemFicha + "]");
//                            em.persist(itemFicha);
//                        }
//                    } else {
//
//                        logger.log(Level.INFO, "Regra de Evento: [{0}], com erro em algum script.", evento.getDescricao());
//                        logger.log(Level.INFO, "gerarFichaFinanceira", "Tem erro na formula:" + resultadoFormula.temErro());
//                        logger.log(Level.INFO, "Tem erro no valor integral:{0}", resultadoValorIntegral.temErro());
//                        logger.log(Level.INFO, "Tem erro no valor referencia:{0}", resultadoReferencia.temErro());
//                        logger.log(Level.INFO, "Tem erro no valor base de calculo:{0}", resultadoValorBaseDeCalculo.temErro());
//
//                        Resultado[] resultados = {resultadoFormula, resultadoValorIntegral, resultadoReferencia};
//                        for (Resultado r : resultados) {
//                            addListaErros(itemErroScripts, r.getItemErroScript());
//                        }
//                    }
//                }
//            } else {
//                addListaErros(itemErroScripts, resultadoRegra.getItemErroScript());
//            }
//
//        }
//    }
//    private void geraItemLancados(
//            List<EventoFP> eventosLancadosMesAtual,
//            ConfiguracaoContextoJS configuracaoContextoJS,
//            VinculoFP vinculo,
//            FichaFinanceiraFP ficha,
//            List<ItemErroScript> itemErroScripts,
//            List<EventoFP> eventosParaCalculo) {
//
//        ResultadoCalculoRetroativoFP res = this.calculoRetroativoFacade.verifica(vinculo);
//        if (res != null && res.temCalculoRetroativo()) {
//            calculaValoresRetroativos(res, vinculo, configuracaoContextoJS, ficha);
//        }
//
//        for (EventoFP evento : eventosLancadosMesAtual) {
//            eventosParaCalculo.remove(evento);
//            Resultado<Double> resultadoFormula = configuracaoContextoJS.getExecutaScriptEventoFP().executaFormula(evento, vinculo);
//            Resultado<Double> resultadoValorIntegral = configuracaoContextoJS.getExecutaScriptEventoFP().executaValorIntegral(evento, vinculo);
//            Resultado<Double> resultadoReferencia = configuracaoContextoJS.getExecutaScriptEventoFP().executaReferencia(evento, vinculo);
//            Resultado<Double> resultadoValorBaseDeCalculo = configuracaoContextoJS.getExecutaScriptEventoFP().executaValorBaseDeCalculo(evento, vinculo);
//            if (resultadoFormula.naoTemErro() && resultadoReferencia.naoTemErro() && resultadoValorIntegral.naoTemErro()) {
////                        logger.log(Level.INFO, "Regra de Evento: [{0}], n\u00e3o possui nenhum erro.", evento.getDescricao());
//                ItemFichaFinanceiraFP itemFicha = new ItemFichaFinanceiraFP();
//                itemFicha.setTipoCalculoFP(TipoCalculoFP.NORMAL);
//                itemFicha.setEventoFP(evento);
//                itemFicha.setFichaFinanceiraFP(ficha);
////                        logger.log(Level.INFO, "Evento: {0}", evento.getCodigo());
//
//                final Double valorFormula = resultadoFormula.getValor();
////                        logger.log(Level.INFO, "Valor Formula: {0}", valorFormula);
//                itemFicha.setAno(ficha.getFolhaDePagamento().getAno());
//                itemFicha.setMes(ficha.getFolhaDePagamento().getMes().ordinal() + 1);
//                itemFicha.valorDouble(valorFormula);
//                itemFicha.setValorIntegral(criaValorParaItemFicha(resultadoValorIntegral.getValor()));
//
//                final Double valorRef = resultadoReferencia.getValor();
////                        logger.log(Level.INFO, "Valor Referencia: {0}", valorRef);
//
//                itemFicha.setValorReferencia(criaValorParaItemFicha(valorRef));
//                itemFicha.setUnidadeReferencia(evento.getUnidadeReferencia());
//
//                final Double valorBaseDeCalculo = resultadoValorBaseDeCalculo.getValor();
////                        logger.log(Level.INFO, "Valor Base de Calculo: {0}", valorBaseDeCalculo);
//                itemFicha.setValorBaseDeCalculo(criaValorParaItemFicha(valorBaseDeCalculo));
//
//                if (itemErroScripts.isEmpty()) {
////                            logger.logp(Level.INFO, FolhaDePagamentoFacade.class.getName(), "gerarFichaFinanceira", "Persistindo item: [" + itemFicha + "]");
//                    em.persist(itemFicha);
//                }
//            } else {
//
//                logger.log(Level.INFO, "Regra de Evento: [{0}], com erro em algum script.", evento.getDescricao());
//                logger.log(Level.INFO, "gerarFichaFinanceira", "Tem erro na formula:" + resultadoFormula.temErro());
//                logger.log(Level.INFO, "Tem erro no valor integral:{0}", resultadoValorIntegral.temErro());
//                logger.log(Level.INFO, "Tem erro no valor referencia:{0}", resultadoReferencia.temErro());
//                logger.log(Level.INFO, "Tem erro no valor base de calculo:{0}", resultadoValorBaseDeCalculo.temErro());
//
//                Resultado[] resultados = {resultadoFormula, resultadoValorIntegral, resultadoReferencia};
//                for (Resultado r : resultados) {
//                    addListaErros(itemErroScripts, r.getItemErroScript());
//                }
//            }
//
//
//
//        }
//    }
//    private void calculaValoresRetroativos(ResultadoCalculoRetroativoFP res, VinculoFP vinculo, ConfiguracaoContextoJS configuracaoContextoJS, FichaFinanceiraFP fichaAtual) {
//        Calendar dataInicial = Calendar.getInstance();
//        Calendar dataAtual = Calendar.getInstance();
//        dataAtual.setTime(new Date());
//        dataInicial.setTime(res.getDataInicialCalculoRetroativo());
//        Integer ano = getAno(res.getDataInicialCalculoRetroativo());
//        Integer anoAtutal = getAno(new Date());
//        Integer mes = getMes(res.getDataInicialCalculoRetroativo());
//        Integer mesAtual = fichaAtual.getFolhaDePagamento().getMes().ordinal();
//        Map<Long, BigDecimal> mapAcumuladoRetroativo = new HashMap<Long, BigDecimal>();
//        while (mes < mesAtual && ano < anoAtutal) {
//            if (mes == 12) {
//                ano++;
//                mes = 0;
//            }
//            mes++;
//            FichaFinanceiraFP fichaRecuperada = recuperaFichaFinanceira(mes, ano, vinculo);
////            logger.log(Level.INFO, "ficha recuperada: {0}", fichaRecuperada);
//            BigDecimal acumulado = new BigDecimal(BigInteger.ZERO);
//            if (fichaRecuperada != null) {
//                for (ItemFichaFinanceiraFP ifp : fichaRecuperada.getItemFichaFinanceiraFP()) {
//                    if (ifp.getTipoCalculoFP().equals(TipoCalculoFP.NORMAL) && ifp.getEventoFP().getTipoEventoFP().equals(TipoEventoFP.VANTAGEM)) {
//
//                        configuracaoContextoJS.updateMes(mes);
//                        configuracaoContextoJS.updateAno(ano);
//
//                        Resultado<Boolean> resultadoRegra = configuracaoContextoJS.getExecutaScriptEventoFP().executaRegra(ifp.getEventoFP(), vinculo);
//                        if (resultadoRegra.naoTemErro()) {
//                            if (resultadoRegra.getValor()) {
//                                Resultado<Double> resultadoFormula = configuracaoContextoJS.getExecutaScriptEventoFP().executaFormula(ifp.getEventoFP(), vinculo);
//                                if (resultadoFormula.naoTemErro()) {
//                                    //System.out.println("Valor Evento Antigo: " + configuracaoContextoJS.getExecutaScriptEventoFP().executaFormula(ifp.getEventoFP(), vinculo).getValor());
//                                    BigDecimal valorAntigo = new BigDecimal(configuracaoContextoJS.getExecutaScriptEventoFP().executaFormula(ifp.getEventoFP(), vinculo).getValor());
//                                    logger.log(Level.INFO, "valor Antigo: {0}", valorAntigo);
//                                    logger.log(Level.INFO, "valor ifp: {0}", ifp.getValor());
//                                    BigDecimal valor = valorAntigo.subtract(ifp.getValor());//valor subtraido
//                                    logger.log(Level.INFO, "valor: {0}", valor);
//                                    int v = valor.intValue();
//                                    if (v > 0) {
//                                        ItemFichaFinanceiraFP novoItemRetroativo = new ItemFichaFinanceiraFP();
//                                        novoItemRetroativo.setValor(valor);
//                                        acumulado = acumulado.add(valor);
//
//                                        novoItemRetroativo.setFichaFinanceiraFP(fichaAtual);
//                                        novoItemRetroativo.setValorBaseDeCalculo(BigDecimal.ZERO);
//                                        novoItemRetroativo.setValorIntegral(BigDecimal.ZERO);
//                                        novoItemRetroativo.setValorReferencia(BigDecimal.ZERO);
//                                        novoItemRetroativo.setTipoCalculoFP(TipoCalculoFP.RETROATIVO);
//                                        novoItemRetroativo.setEventoFP(ifp.getEventoFP());
//                                        novoItemRetroativo.setAno(ano);
//                                        novoItemRetroativo.setMes(mes);
//                                        em.persist(novoItemRetroativo);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            logger.log(Level.INFO, "id vinculo: {0}", vinculo.getId());
//            logger.log(Level.INFO, "acumulado: {0}", acumulado);
//
//            mapAcumuladoRetroativo.put(vinculo.getId(), acumulado);
//        }
//        configuracaoContextoJS.updateMes(mesAtual);
//        configuracaoContextoJS.putMap(mapAcumuladoRetroativo);
//    }
//    public void calculaRetroativoLancamento(ResultadoCalculoRetroativoFP res, List<EventoFP> eventos, ConfiguracaoContextoJS configuracaoContextoJS, VinculoFP vinculo, FichaFinanceiraFP ficha, List<ItemErroScript> itemErroScripts) {
//        Calendar dataInicial = Calendar.getInstance();
//
//        dataInicial.setTime(res.getDataInicialCalculoRetroativo());
//
//        dataInicial.setTime(res.getDataInicialCalculoRetroativo());
//
//        Date dataMaxima = lancamentoFPFacade.getDataFinalLancamento(vinculo, dataInicial.get(Calendar.MONTH), dataInicial.get(Calendar.YEAR));
//        Map<Long, BigDecimal> mapAcumuladoRetroativo = new HashMap<Long, BigDecimal>();
//        if (dataMaxima != null && dataInicial != null) {
//            if (dataMaxima.after(new Date())) {
//                dataMaxima = new Date();
//            }
//            Calendar c = Calendar.getInstance();
//            c.setTime(dataMaxima);
//            c.add(Calendar.MONTH, -1);
//            while (dataInicial.getTime().before(c.getTime())) {
//
//                BigDecimal acumulado = new BigDecimal(BigInteger.ZERO);
//                for (EventoFP evento : eventos) {
//
//                    FichaFinanceiraFP fichaRecuperada = recuperaFichaFinanceira(dataInicial.get(Calendar.MONTH), dataInicial.get(Calendar.YEAR), vinculo, evento);
//
//                    if (fichaRecuperada == null) {
////
//
//                        if (acumulado.compareTo(BigDecimal.ZERO) == 0) {
//
//                            configuracaoContextoJS.getEngine().put(NomeObjetosConstantesScriptFP.NOME_OBJETO_ANO_CALCULO, dataInicial.get(Calendar.YEAR));
//                            configuracaoContextoJS.getEngine().put(NomeObjetosConstantesScriptFP.NOME_OBJETO_MES_CALCULO, dataInicial.get(Calendar.MONTH));
//
//                            Resultado<Boolean> resultadoRegra = configuracaoContextoJS.getExecutaScriptEventoFP().executaRegra(evento, vinculo);
//                            if (resultadoRegra.getValor() != null && resultadoRegra.naoTemErro()) {
//                                if (resultadoRegra.getValor()) {
//                                    Resultado<Double> resultadoFormula = configuracaoContextoJS.getExecutaScriptEventoFP().executaFormula(evento, vinculo);
//                                    Resultado<Double> resultadoValorIntegral = configuracaoContextoJS.getExecutaScriptEventoFP().executaValorIntegral(evento, vinculo);
//                                    Resultado<Double> resultadoReferencia = configuracaoContextoJS.getExecutaScriptEventoFP().executaReferencia(evento, vinculo);
//                                    Resultado<Double> resultadoBaseCalculo = configuracaoContextoJS.getExecutaScriptEventoFP().executaValorBaseDeCalculo(evento, vinculo);
//                                    if (resultadoFormula.naoTemErro()) {
////                                    //System.out.println("Valor Evento Antigo: " + configuracaoContextoJS.getExecutaScriptEventoFP().executaFormula(evento, vinculo).getValor());
//                                        ItemFichaFinanceiraFP novoItemRetroativo = new ItemFichaFinanceiraFP();
//                                        novoItemRetroativo.setValor(new BigDecimal(resultadoFormula.getValor()));
//                                        acumulado = acumulado.add(new BigDecimal(resultadoFormula.getValor()));
//                                        novoItemRetroativo.setFichaFinanceiraFP(ficha);
//                                        novoItemRetroativo.setValorBaseDeCalculo(new BigDecimal(resultadoBaseCalculo.getValor()));
//                                        novoItemRetroativo.setValorIntegral(new BigDecimal(resultadoValorIntegral.getValor()));
//                                        novoItemRetroativo.setValorReferencia(new BigDecimal(resultadoReferencia.getValor()));
//                                        novoItemRetroativo.setTipoCalculoFP(TipoCalculoFP.RETROATIVO);
//                                        novoItemRetroativo.setEventoFP(evento);
//                                        novoItemRetroativo.setAno(dataInicial.get(Calendar.YEAR));
//                                        novoItemRetroativo.setMes(dataInicial.get(Calendar.MONTH) + 1);
//                                        em.persist(novoItemRetroativo);
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                }
//                mapAcumuladoRetroativo.put(vinculo.getId(), acumulado);
//                configuracaoContextoJS.putMap(mapAcumuladoRetroativo);
//                dataInicial.add(Calendar.MONTH, +1);
//            }
//        }
//    }
    public List<FichaFinanceiraFP> recuperaFichaPorContrato(String matricula, String contrato) {
        String sql = "";
        sql += " SELECT ficha.id, ficha.folhadepagamento_id, ficha.recursofp_id, ficha.vinculofp_id FROM fichafinanceirafp ficha ";
        sql += " INNER JOIN vinculofp vinculo ON ficha.vinculofp_id = vinculo.id ";
        sql += " INNER JOIN matriculafp matricula ON vinculo.matriculafp_id = matricula.id ";
        sql += " WHERE matricula.matricula =:mat AND vinculo.numero =:cont ";
        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        q.setParameter("mat", matricula);
        q.setParameter("cont", contrato);

        return q.getResultList();
    }

    public FolhaDePagamento recuperarAlternativo(Object id) {
        FolhaDePagamento fdp = em.find(FolhaDePagamento.class, id);
        if (fdp.getDetalhesCalculoRHList() != null) {
            for (DetalhesCalculoRH detalhe : fdp.getDetalhesCalculoRHList()) {
                detalhe.getItensDetalhesErrosCalculos().size();
            }
        }
        return fdp;
    }

    public FolhaDePagamento recuperarSimples(Object id) {
        FolhaDePagamento fdp = em.find(FolhaDePagamento.class, id);
        return fdp;
    }

    public CompetenciaFP verificaCompetenciaAberta(FolhaDePagamento folha) {
        Query q = em.createQuery("from CompetenciaFP c where c.exercicio.ano = :exer and c.statusCompetencia = :status "
            + " and c.tipoFolhaDePagamento = :tipo and c.mes = :mes");
        q.setParameter("exer", folha.getAno());
        q.setParameter("status", StatusCompetencia.ABERTA);
        q.setParameter("tipo", folha.getTipoFolhaDePagamento());
        q.setParameter("mes", folha.getMes());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (CompetenciaFP) q.getSingleResult();
    }

    public CompetenciaFP verificaCompetenciaAbertaSimulacao(FolhaCalculavel folha) {
        Query q = em.createQuery("from CompetenciaFP c where c.exercicio.ano = :exer and c.statusCompetencia = :status "
            + " and c.tipoFolhaDePagamento = :tipo and c.mes = :mes");
        q.setParameter("exer", folha.getAno());
        q.setParameter("status", StatusCompetencia.ABERTA);
        q.setParameter("tipo", folha.getTipoFolhaDePagamento());
        q.setParameter("mes", folha.getMes());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (CompetenciaFP) q.getSingleResult();
    }

    public CompetenciaFP verificaCompetenciaEfetivada(FolhaCalculavel folha) {
        Query q = em.createQuery("from CompetenciaFP c where c.exercicio.ano = :exer and c.statusCompetencia = :status "
            + " and c.tipoFolhaDePagamento = :tipo and c.mes = :mes");
        q.setParameter("exer", folha.getAno());
        q.setParameter("status", StatusCompetencia.EFETIVADA);
        q.setParameter("tipo", folha.getTipoFolhaDePagamento());
        q.setParameter("mes", folha.getMes());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }

        return (CompetenciaFP) q.getSingleResult();
    }

    public List<FolhaDePagamento> recuperaFolhaPelaCompetencia(CompetenciaFP competencia) {
        Query q = em.createQuery("from FolhaDePagamento folha "
            + "where folha.competenciaFP = :competencia");

        q.setParameter("competencia", competencia);
        return q.getResultList();
    }

    public Mes getMes(Integer i) {
        for (Mes m : Mes.values()) {
            if (m.ordinal() == i) {
                return m;
            }
        }
        return null;
    }

    public boolean existeFolhaPorContratoData(ContratoFP contrato, Date data) {
        if (contrato.getId() == null) {
            return false;
        } else {
            Query query = em.createQuery("from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha "
                + " where ficha.vinculoFP = :vinculo and "
                + " folha.calculadaEm <= :dataVigencia");
            query.setParameter("dataVigencia", data);
            query.setParameter("vinculo", contrato);
            return !query.getResultList().isEmpty();
        }
    }

    public boolean existeFolhaProcessadaParaContratoDepoisDe(ContratoFP contrato, Date data) {
        if (contrato == null || contrato.getId() == null) {
            return false;
        }
        String sql = "  select folha.id from folhadepagamento folha " +
            " inner join fichafinanceirafp ficha on folha.id = ficha.folhadepagamento_id " +
            " where ficha.vinculofp_id = :vinculo " +
            " and folha.calculadaem  >= to_date(:dataVigencia, 'dd/MM/yyyy')";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(data));
        q.setParameter("vinculo", contrato.getId());
        return !q.getResultList().isEmpty();
    }

    public Date buscarUltimaDataFolhaProcessadaPorCargo(ContratoFP contrato) {
        Query query = em.createQuery("select max(folha.calculadaEm) from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha "
            + " where ficha.vinculoFP = :vinculo ");
        query.setParameter("vinculo", contrato);
        if (!query.getResultList().isEmpty())
            return (Date) query.getResultList().get(0);
        return null;
    }

    public boolean existeFolhaProcessadaParaContrato(ContratoFP contrato) {
        if (contrato.getId() == null) {
            return false;
        } else {
            Query query = em.createQuery("select folha.id from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha "
                + " where ficha.vinculoFP = :vinculo ");
            query.setParameter("vinculo", contrato);
            System.out.println(query.getResultList().size());
            return !query.getResultList().isEmpty();
        }
    }

    public boolean buscarFolhaProcessadaPorContratoAndStatusCompetencia(ContratoFP contrato, StatusCompetencia status) {
        if (contrato.getId() == null) {
            return false;
        } else {
            Query query = em.createQuery("select folha.id from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha "
                + " where ficha.vinculoFP = :vinculo and folha.competenciaFP.statusCompetencia = :status");
            query.setParameter("vinculo", contrato);
            query.setParameter("status", status);
            return !query.getResultList().isEmpty();
        }
    }

    public FichaFinanceiraFP recuperaFichaFinanceira(Integer mes, Integer ano, VinculoFP vinculo) {//Melhorar metodo....
        Query q = em.createQuery("select ficha from FolhaDePagamento folha "
            + "inner join folha.fichaFinanceiraFPs ficha "
            + "inner join ficha.itemFichaFinanceiraFP item "
            + "where (extract(month from folha.efetivadaEm)) = :param and (extract(year from folha.efetivadaEm)) = :ano "
            + "and ficha.vinculoFP = :vinculo ");
        q.setParameter("param", mes);
        q.setParameter("ano", ano);
        q.setParameter("vinculo", vinculo);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (FichaFinanceiraFP) q.getSingleResult();

    }

    /**
     * Metodo usado para recuparar todas as fichas de uma Matricula
     * Obs: No caso de multiplos vinculos, recupera a ficha dos 2 contratos vigentes.
     */
    public List<FichaFinanceiraFP> retornaFichaFinanceiraPorMatricula(Mes mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, VinculoFP vinculo) {//Melhorar metodo....
        Query q = em.createQuery("select ficha from FolhaDePagamento folha"
            + " inner join folha.fichaFinanceiraFPs ficha"
            + "  "
            + " where ficha.vinculoFP.matriculaFP.pessoa = :vinculo"
            + "   and folha.ano = :ano"
            + "   and folha.mes = :mes"
            + "   and folha.tipoFolhaDePagamento in :tipo and folha.efetivadaEm is null");
        q.setParameter("vinculo", vinculo.getMatriculaFP().getPessoa());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("tipo", getTipoCorreto(tipoFolhaDePagamento));
        return q.getResultList();

    }

    private List<TipoFolhaDePagamento> getTipoCorreto(TipoFolhaDePagamento tipo) {
        List<TipoFolhaDePagamento> tipos = new ArrayList<>();
//        if (tipo.equals(TipoFolhaDePagamento.NORMAL)) {
//            tipos.add(TipoFolhaDePagamento.NORMAL);
//            tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
//        }
        if (tipo.equals(TipoFolhaDePagamento.RESCISAO) || tipo.equals(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR)) {
            tipos.add(TipoFolhaDePagamento.NORMAL);
            tipos.add(TipoFolhaDePagamento.RESCISAO);
            tipos.add(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
            tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
        }
//        if (tipo.equals(TipoFolhaDePagamento.SALARIO_13) || tipo.equals(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO)) {
//            tipos.add(TipoFolhaDePagamento.NORMAL);
//            tipos.add(TipoFolhaDePagamento.SALARIO_13);
//            tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
//            tipos.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
//        }
        if (tipos.isEmpty()) {
            tipos.add(tipo);
        }
        return tipos;
    }

    public List<FichaFinanceiraFP> retornaFichaFinanceiraPorVinculo(Mes mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, VinculoFP vinculo) {//Melhorar metodo....
        Query q = em.createQuery("select ficha from FolhaDePagamento folha"
            + " inner join folha.fichaFinanceiraFPs ficha"
            + "  "
            + " where ficha.vinculoFP = :vinculo"
            + "   and folha.ano = :ano"
            + "   and folha.mes = :mes"
            + "   and folha.tipoFolhaDePagamento in :tipo and folha.efetivadaEm is null");
        q.setParameter("vinculo", vinculo);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("tipo", getTipoCorreto(tipoFolhaDePagamento));
        return q.getResultList();

    }

    public FichaFinanceiraFP recuperaFichaFinanceira(Integer mes, Integer ano, VinculoFP vinculo, EventoFP evento) {//Melhorar metodo....
        Query q = em.createQuery("select ficha from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha inner join ficha.itemFichaFinanceiraFP item "
            + "where (extract(month from folha.efetivadaEm)) = :param and (extract(year from folha.efetivadaEm)) = :ano "
            + "and ficha.vinculoFP = :vinculo and item.eventoFP = :evento");
        mes = mes++;
        q.setParameter("param", mes);
        q.setParameter("ano", ano);
        q.setParameter("vinculo", vinculo);
        q.setParameter("evento", evento);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (FichaFinanceiraFP) q.getSingleResult();

    }

    public BigDecimal buscarValorLiquido(VinculoFP contrato, Integer mes, Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append(sql);
        sql.append(" select coalesce((SELECT sum(itensFichaRendimentos.valor) from itemfichafinanceirafp itensFichaRendimentos ");
        sql.append(" inner join eventofp eventoRendimento on eventorendimento.id = itensFichaRendimentos.eventofp_id");
        sql.append(" where itensFichaRendimentos.tipoeventofp = 'VANTAGEM' and itensFichaRendimentos.fichafinanceirafp_id = ficha.id ),0) - ");
        sql.append(" coalesce((SELECT sum(itensFichaDescontos.valor) from itemfichafinanceirafp itensFichaDescontos ");
        sql.append(" inner join eventofp eventoDesconto on eventoDesconto.id = itensFichaDescontos.eventofp_id ");
        sql.append(" where itensfichaDescontos.fichafinanceirafp_id = ficha.id and itensFichaDescontos.tipoeventofp = 'DESCONTO' ),0) as valorLiquido ");
        sql.append(" from fichafinanceirafp ficha ");
        sql.append(" inner join vinculofp on vinculofp.id = ficha.vinculofp_id ");
        sql.append(" inner join folhadepagamento on folhadepagamento.id = ficha.folhadepagamento_id ");
        sql.append("  ");
        sql.append(" where folhadepagamento.mes = :MES and folhadepagamento.ano = :ANO and VINCULOFP.ID = :VINCULO ");
        sql.append(" ");

        Query q = em.createNativeQuery(sql.toString());
        q.setMaxResults(1);
        q.setParameter("VINCULO", contrato.getId());
        q.setParameter("MES", mes);
        q.setParameter("ANO", ano);


        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getSingleResult();
        }

    }

    public FolhaDePagamento recuperaFolhaCalculadaPorData(Date data) {
        try {
            Query q = em.createQuery(" from FolhaDePagamento folha where folha.calculadaEm >= :data ");
            q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(data));

            return (FolhaDePagamento) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<EventoFP> recuperaEventosLancadosRetroativos(VinculoFP vinculoFP) {
//        Date hoje = new Date();
//        Date menorData = hoje;
        try {
            Date dataUltimaFolha = recuperaDataUltimaFolha();
            if (dataUltimaFolha != null) {
                Date dataPenultimaFolha = recuperaDataPenultimaFolha(dataUltimaFolha);
                if (dataPenultimaFolha != null && dataUltimaFolha != null) {
                    Date ultimoDiaDoMes = Util.recuperaDataUltimoDiaDoMes(dataUltimaFolha);
                    return caracterizaEventosRetroativosLancamento(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<EventoFP> caracterizaEventosRetroativosLancamento(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
        String hql = " select cfl.eventoFP "
            + " from LancamentoFP cfl "
            + " where cfl.eventoFP.ativo is true and ((cfl.mesAnoInicial < cfl.dataCadastro and cfl.vinculoFP.id = :vinculoFP "
            + " and cfl.dataCadastro > :penultimaFolha "
            + " and cfl.dataCadastro <= :ultimaFolha)"
            + " or "
            + "(cfl.dataCadastro > :ultimaFolha "
            + "and cfl.dataCadastro < cfl.mesAnoInicial "
            + "and cfl.mesAnoInicial < :ultimoDiaDoMes))";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return q.getResultList();
    }

    public Date recuperaDataUltimaFolha() {//suspeito de pegar um folha que está sendo calculada no momento
        String sql = "select max(fp.calculadaem) from folhadepagamento fp";
        Query q = getEntityManager().createNativeQuery(sql);
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date recuperaDataUltimaFolhaEfetivadaEm(TipoFolhaDePagamento tipo) {
        String sql = "select max(fp.efetivadaEm) FROM FolhaDePagamento fp where fp.tipoFolhaDePagamento = :tipo ";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("tipo", tipo);
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date recuperaDataUltimaFolhaCalculadaEm(TipoFolhaDePagamento tipo) {
        String sql = "select max(fp.calculadaEm) FROM FolhaDePagamento fp where fp.tipoFolhaDePagamento = :tipo ";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("tipo", tipo);
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date recuperaDataUltimaFolhaConf() {
        String sql = "select max(fp.calculadaem) from folhadepagamento fp where fp.efetivadaem is not null";
        Query q = getEntityManager().createNativeQuery(sql);
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date recuperaDataPenultimaFolha(Date ultimaFolha) {
        String sql = "select max(fp.calculadaem) "
            + "from folhadepagamento fp "
            + "where fp.calculadaem < :ultimaFolha";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("ultimaFolha", ultimaFolha);
        try {

            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public BigDecimal recuperaValorTotalDoEventoNaFolhaPorUnidade(MesCalendarioPagamento mes, Integer ano, UnidadeOrganizacional unidadeOrganizacional, EventoFP evento) {
        Query q = em.createQuery(" select sum(coalesce(item.valor,0)) from ItemFichaFinanceiraFP item"
            + " inner join item.fichaFinanceiraFP ficha "
            + " inner join ficha.folhaDePagamento folha "
            + " where folha.ano = :ano and folha.mes = :mes "
            + " and folha.unidadeOrganizacional = :unidade and item.eventoFP = :eventoFP ");

        q.setParameter("eventoFP", evento);
        q.setParameter("unidade", unidadeOrganizacional);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);


        if (q.getResultList().isEmpty()) {
            return new BigDecimal(BigInteger.ZERO);
        }

        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal recuperaValorTotalDoEventoNaFicha(MesCalendarioPagamento mes, Integer ano, ContratoFP contratoFP, UnidadeOrganizacional unidadeOrganizacional, EventoFP evento) {
        Query q = em.createQuery(" select sum(coalesce(item.valor,0)) from ItemFichaFinanceiraFP item"
            + " inner join item.fichaFinanceiraFP ficha "
            + " inner join ficha.folhaDePagamento folha "
            + " where folha.ano = :ano and folha.mes = :mes "
            + " and ficha.vinculoFP.id = :contratoFP "
            + " and folha.unidadeOrganizacional = :unidade "
            + " and item.eventoFP = :eventoFP ");

        q.setParameter("eventoFP", evento);
        q.setParameter("contratoFP", contratoFP.getId());
        q.setParameter("unidade", unidadeOrganizacional);
        q.setParameter("mes", Mes.valueOf(mes.name()));
        q.setParameter("ano", ano);
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        if (resultado == null) {
            resultado = BigDecimal.ZERO;
        }

        return resultado;
    }

    @Asynchronous
    public void inserirLogServidoresDuplicados(FolhaCalculavel folhaCalculando, List<VinculoFP> vinculos, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        if (folhaCalculando instanceof FolhaDePagamento) {
            for (VinculoFP vinculo : vinculos) {
                ItemErroDuplicidade item = new ItemErroDuplicidade();
                item.setFolhaDePagamento((FolhaDePagamento) folhaCalculando);
                item.setVinculoFP(vinculo);
                item.setTipoCalculo(detalheProcessamentoFolha.getTipoCalculo());
                item.setObservacao("Servidor duplicado");
                item.setCriadoEm(new Date());
                em.persist(item);
            }
        }
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void iniciarProcessamento(FiltroFolhaDePagamento filtroFolhaDePagamento, FolhaCalculavel folhaCalculando, List<VinculoFP> vinculos, DetalheProcessamentoFolha detalheProcessamentoFolha, Date dataOperacao, UsuarioSistema usuarioCorrente, Map<VinculoFP, FilaProcessamentoFolha> filas, AssistenteBarraProgresso assistente) {
        iniciarProcessamentoInterno(filtroFolhaDePagamento, folhaCalculando, vinculos, detalheProcessamentoFolha, dataOperacao, usuarioCorrente, filas, assistente);
    }

    public void iniciarProcessamentoInterno(FiltroFolhaDePagamento filtroFolhaDePagamento, FolhaCalculavel folhaCalculando, List<VinculoFP> vinculos, DetalheProcessamentoFolha detalheProcessamentoFolha, Date dataOperacao, UsuarioSistema usuarioCorrente, Map<VinculoFP, FilaProcessamentoFolha> filas, AssistenteBarraProgresso assistente) {
        try {
            long time = System.currentTimeMillis();
            Map<Long, Long> idsFontesPagadoras = Maps.newHashMap();
            detalheProcessamentoFolha.getDetalhesCalculoRH().setDescricao("Coletando dados...");
            detalheProcessamentoFolha.setAno(folhaCalculando.getAno());

            detalheProcessamentoFolha.setMes(folhaCalculando.getMes().getNumeroMes());
            detalheProcessamentoFolha.setConfiguracaoFP(configuracaoRHFacade.buscarConfiguracaoFPVigente(dataOperacao));

            Set<VinculoFP> listaSet = new LinkedHashSet<>();
            for (VinculoFP v : vinculos) {
                listaSet.add(v);
            }
            vinculos = new LinkedList<>();
            detalheProcessamentoFolha.getDetalhesCalculoRH().setDescricao("Descobrindo múltiplos vinculos...");

            Map<VinculoFP, Long> mapFontePagadora = buscarFontesPagadorasPorVinculo(listaSet, folhaCalculando.getMes(), folhaCalculando.getAno(), idsFontesPagadoras);
            Set<VinculoFP> listraOutrosVinculos = buscarOutrosVinculos(listaSet, folhaCalculando, detalheProcessamentoFolha);
            for (VinculoFP v : listaSet) {
                checarMultiploVinculo(v, folhaCalculando, detalheProcessamentoFolha.getDataReferencia().toDate(), listaSet, mapFontePagadora, listraOutrosVinculos, idsFontesPagadoras);
                vinculos.add(v);
            }

            if (folhaCalculando.getId() != null && !isSimulacao(folhaCalculando)) {
                FolhaCalculavel folhaRecuperada = recuperarAlternativo(folhaCalculando.getId());
                folhaRecuperada.setLoteProcessamento(folhaCalculando.getLoteProcessamento());
                folhaRecuperada.setCalculadaEm(SistemaFacade.getDataCorrente());
                folhaRecuperada.setVersao(folhaCalculando.getVersao());
                folhaRecuperada.setImprimeLogEmArquivo(folhaCalculando.isImprimeLogEmArquivo());
                folhaRecuperada.setGravarMemoriaCalculo(folhaCalculando.isGravarMemoriaCalculo());
                folhaRecuperada.setProcessarCalculoTransient(folhaCalculando.isProcessarCalculoTransient());
                if (filtroFolhaDePagamento != null) {
                    filtroFolhaDePagamento.setFolhaDePagamento((FolhaDePagamento) folhaRecuperada);
                    folhaRecuperada.getFiltros().add(filtroFolhaDePagamento);
                }
                folhaCalculando = folhaRecuperada;
            }
            if (!isSimulacao(folhaCalculando)) {
                detalheProcessamentoFolha.getDetalhesCalculoRH().setFolhaDePagamento((FolhaDePagamento) folhaCalculando);
            }
            folhaCalculando.setDetalhesCalculoRHAtual(detalheProcessamentoFolha.getDetalhesCalculoRH());
            int total = 0;
            detalheProcessamentoFolha.limpa();
            detalheProcessamentoFolha.bloqueiaCalculo();

            total = vinculos.size();
            assistente.setTotal(total);
            detalheProcessamentoFolha.setTotalCadastros(Double.parseDouble(total + ""));
            salvar(folhaCalculando);
            logger.error("Até carregar os dados: " + (System.currentTimeMillis() - time + " ms "));
            detalheProcessamentoFolha.getDetalhesCalculoRH().setDescricao("Carregando bloqueios de calculo...");
            preCarregarCache(detalheProcessamentoFolha, vinculos, folhaCalculando);
            if (vinculos.size() > 200) {
                List<VinculoFP> multiplos = verificarEPegarMultiplosVinculos(vinculos, detalheProcessamentoFolha.getDataReferencia().toDate(), mapFontePagadora);
                for (Iterator it = vinculos.iterator(); it.hasNext(); ) {
                    VinculoFP v = (VinculoFP) it.next();
                    if (multiplos.contains(v)) {
                        it.remove();
                    }
                }

                ordenarPorMatriculaVinculos(vinculos);
                ordenarPorMatriculaVinculos(multiplos);

                List<List<VinculoFP>> listas = particionarEmCinco(vinculos);

                List<VinculoFP> lista1 = listas.get(0);
                List<VinculoFP> lista2 = listas.get(1);
                List<VinculoFP> lista3 = listas.get(2);
                List<VinculoFP> lista4 = listas.get(3);
                List<VinculoFP> lista5 = listas.get(4);
                List<VinculoFP> novaLista = new ArrayList<>();
                novaLista.addAll(lista5);
                novaLista.addAll(multiplos);
                logger.error("Liberando Future para Cálculo.");

                logger.error("sistema user " + usuarioCorrente);
                logger.error("data " + new Date());
                Hibernate.initialize(folhaCalculando.getFiltros());
                detalheProcessamentoFolha.setTarefa1(middleRH.calculoGeralNovo(folhaCalculando, lista1, detalheProcessamentoFolha, usuarioCorrente, filas, assistente, filtroFolhaDePagamento));
                detalheProcessamentoFolha.setTarefa2(middleRH.calculoGeralNovo(folhaCalculando, lista2, detalheProcessamentoFolha, usuarioCorrente, filas, assistente, filtroFolhaDePagamento));
                detalheProcessamentoFolha.setTarefa3(middleRH.calculoGeralNovo(folhaCalculando, lista3, detalheProcessamentoFolha, usuarioCorrente, filas, assistente, filtroFolhaDePagamento));
                detalheProcessamentoFolha.setTarefa4(middleRH.calculoGeralNovo(folhaCalculando, lista4, detalheProcessamentoFolha, usuarioCorrente, filas, assistente, filtroFolhaDePagamento));
                detalheProcessamentoFolha.setTarefa5(middleRH.calculoGeralNovo(folhaCalculando, novaLista, detalheProcessamentoFolha, usuarioCorrente, filas, assistente, filtroFolhaDePagamento));
            } else {
                ordenarPorMatriculaVinculos(vinculos);
                Hibernate.initialize(folhaCalculando.getFiltros());
                detalheProcessamentoFolha.setTarefa1(middleRH.calculoGeralNovo(folhaCalculando, vinculos, detalheProcessamentoFolha, usuarioCorrente, filas, assistente, filtroFolhaDePagamento));
            }

        } catch (Exception e) {
            singletonFolhaDePagamento.setCalculandoFolha(false);
            assistente.setExecutando(false);
            e.printStackTrace();
        } finally {
            detalheProcessamentoFolha.getDetalhesCalculoRH().setDescricao("Processando...");
            logger.debug("A folha foi enviada para processamento, paralelo...");
        }
    }

    private Set<VinculoFP> buscarOutrosVinculos(Set<VinculoFP> listaSet, FolhaCalculavel folhaCalculando, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        Set<VinculoFP> outros = new LinkedHashSet<>();
        if (TipoCalculo.TODOS.equals(detalheProcessamentoFolha.getTipoCalculo())) {
            return outros;
        }
        for (VinculoFP vinculoFP : listaSet) {
            Long id = vinculoFPFacade.recuperarMultiplosVinculosParaFolhaSQL(vinculoFP, folhaCalculando.getMes(), folhaCalculando.getAno(), detalheProcessamentoFolha.getDataReferencia().toDate());
            if (id != null) {
                VinculoFP outroVinculo = vinculoFPFacade.recuperarSimples(id);
                outros.add(outroVinculo);
            }
        }
        return outros;
    }

    private Map<VinculoFP, Long> buscarFontesPagadorasPorVinculo(Set<VinculoFP> listaSet, Mes mes, Integer ano, Map<Long, Long> idsFontesPagadoras) {
        if (idsFontesPagadoras.isEmpty()) {
            idsFontesPagadoras = buscarVinculosComFontePagadoras(mes, ano);
        }

        Map<VinculoFP, Long> mapFontePagadora = Maps.newHashMap();
        Map<Long, Long> tempCache = Maps.newHashMap();
        for (VinculoFP vinculoFP : listaSet) {
            try {
                if (vinculoFP.getUnidadeOrganizacional() != null) {
                    if (idsFontesPagadoras.containsKey(vinculoFP.getId()) && idsFontesPagadoras.get(vinculoFP.getId()) != null) {
                        mapFontePagadora.put(vinculoFP, idsFontesPagadoras.get(vinculoFP.getId()));
                        continue;
                    }
                    if (tempCache.containsKey(vinculoFP.getUnidadeOrganizacional().getId())) {
                        mapFontePagadora.put(vinculoFP, tempCache.get(vinculoFP.getUnidadeOrganizacional().getId()));
                    } else {
                        Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(vinculoFP.getUnidadeOrganizacional(), new Date());
                        if (entidade != null) {
                            mapFontePagadora.put(vinculoFP, entidade.getId());
                            tempCache.put(vinculoFP.getUnidadeOrganizacional().getId(), entidade.getId());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Método: buscarFontesPagadorasPorVinculo Erro, não foi possivel determinar a entidade da unidade " + vinculoFP + " Unidade: " + vinculoFP.getUnidadeOrganizacional());
            }
        }
        return mapFontePagadora;
    }

    private boolean considerarMultiploVinculo(VinculoFP v) {
        List<VinculoFP> vinculos = recuperarMultiplosVinculos(v);
        for (VinculoFP vinculo : vinculos) {
            if (vinculo.getDescontarIRRFMultiploVinculo() != null && vinculo.getDescontarIRRFMultiploVinculo()) {
                return false;
            }
        }
        return true;
    }

    private List<VinculoFP> recuperarMultiplosVinculos(VinculoFP v) {
        String hql = "select  v from VinculoFP v where v.matriculaFP.pessoa.id = :id";
        Query q = em.createQuery(hql);
        q.setParameter("id", v.getMatriculaFP().getPessoa().getId());
        return q.getResultList();
    }

    private boolean isSimulacao(FolhaCalculavel folhaCalculando) {
        return folhaCalculando instanceof FolhaDePagamentoSimulacao;
    }

    private void ordenarPorMatriculaVinculos(List<VinculoFP> vinculos) {
        Collections.sort(vinculos, new Comparator<VinculoFP>() {
            @Override
            public int compare(VinculoFP o1, VinculoFP o2) {
                int comparator = o1.getMatriculaFP().getMatricula().compareTo(o2.getMatriculaFP().getMatricula());
                if (comparator == 0) {
                    comparator = o1.getDescontarIrMultiploVinculo().compareTo(o2.getDescontarIrMultiploVinculo());
                }
                if (comparator == 0) {
                    try {
                        return o1.getNumero().compareTo(o2.getNumero());
                    } catch (Exception e) {
                        return 1;
                    }
                }
                return comparator;
            }
        });
    }

    private boolean vinculoTemMultiploVinculo(VinculoFP v) {
        return em.createNativeQuery("select  v.id from vinculofp v " +
            "inner join matriculafp mat on mat.id = v.matriculafp_id " +
            " where mat.pessoa_id = :id ").setParameter("id", v.getMatriculaFP().getPessoa().getId()).getResultList().size() > 1;
    }

    private boolean vinculoTemMultiploVinculoHi(VinculoFP v) {
        return em.createQuery("select  v.id from VinculoFP v where v.matriculaFP.pessoa.id = :id ").setParameter("id", v.getMatriculaFP().getPessoa().getId()).getResultList().size() > 1;
    }

    private void preCarregarCache(DetalheProcessamentoFolha detalheProcessamentoFolha, List<VinculoFP> vinculos, FolhaCalculavel folha) {
        preCarregaDadosBloqueioVerba(detalheProcessamentoFolha, vinculos);//precarregar dados...
        preCarregaDadosBloqueioBenficio(detalheProcessamentoFolha, vinculos);//precarregar dados...
        preCarregaDadosMotivosRejeicao(detalheProcessamentoFolha);//precarregar dados...
        preCarregarServidoresComFolhaEfetivada(detalheProcessamentoFolha, vinculos, folha);
        //preCarregarOrgaoServidor(detalheProcessamentoFolha, vinculos, folha);
        preCarregarEmpregadores(detalheProcessamentoFolha);
    }

    private void preCarregarOrgaoServidor(DetalheProcessamentoFolha detalheProcessamentoFolha, List<VinculoFP> vinculos, FolhaCalculavel folha) {
        Map<EntidadePagavelRH, String> vinculosOrgao = Maps.newHashMap();
        for (VinculoFP vinculo : vinculos) {
            String orgaoServidor = funcoesFolhaFacade.getOrgaoServidor(vinculo, detalheProcessamentoFolha.getDataReferencia().toDate());
            if(orgaoServidor == null || orgaoServidor.isEmpty()){
                orgaoServidor = funcoesFolhaFacade.getOrgaoServidorUltimoVigente(vinculo, detalheProcessamentoFolha.getDataReferencia().toDate());
            }
            vinculosOrgao.put(vinculo, orgaoServidor);
        }
        detalheProcessamentoFolha.setVinculoOrgao(vinculosOrgao);
    }

    private void preCarregarEmpregadores(DetalheProcessamentoFolha detalheProcessamentoFolha) {
        Map<ConfiguracaoEmpregadorESocial, List<HierarquiaOrganizacional>> empregadores = Maps.newLinkedHashMap();
        for (ConfiguracaoEmpregadorESocial empregadorESocial : configuracaoEmpregadorESocialFacade.lista()) {
            ConfiguracaoEmpregadorESocial empregador = configuracaoEmpregadorESocialFacade.recuperar(empregadorESocial.getId());
            empregadores.put(empregador, getHierarquiasDoEmpregador(empregadorESocial, detalheProcessamentoFolha.getDataReferencia().toDate()));
        }
        detalheProcessamentoFolha.setEmpregadores(empregadores);
    }

    public List<HierarquiaOrganizacional> getHierarquiasDoEmpregador(ConfiguracaoEmpregadorESocial empregadorESocial, Date dataReferencia) {
        Set<HierarquiaOrganizacional> hierarquiaOrganizacionals = Sets.newHashSet();
        if (empregadorESocial.getItemConfiguracaoEmpregadorESocial() != null) {
            for (ItemConfiguracaoEmpregadorESocial item : empregadorESocial.getItemConfiguracaoEmpregadorESocial()) {
                HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacadeAtual.getHierarquiaOrganizacionalVigentePorUnidade(item.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, dataReferencia);
                hierarquiaOrganizacionals.add(hierarquia);
            }
        }
        return Lists.newArrayList(hierarquiaOrganizacionals);
    }

    private void preCarregarServidoresComFolhaEfetivada(DetalheProcessamentoFolha detalheProcessamentoFolha, List<VinculoFP> vinculos, FolhaCalculavel folha) {
        logger.debug("Colocando em Cache servidores com folha efetivada.");
        List<VinculoFP> vinculosFolhaEvetivada = new ArrayList<>();
        if (vinculos.size() == 1) {
            vinculosFolhaEvetivada = buscarVinculosPorTipoFolhaMesEAnoAndFolhaEfetivada(folha.getMes(), folha.getAno(), folha.getTipoFolhaDePagamento(), vinculos.get(0));
        } else {
            vinculosFolhaEvetivada = buscarVinculosPorTipoFolhaMesEAnoAndFolhaEfetivada(folha.getMes(), folha.getAno(), folha.getTipoFolhaDePagamento(), null);
        }
        for (VinculoFP vinculoFP : vinculosFolhaEvetivada) {
            if (!detalheProcessamentoFolha.getServidoresComFolhaEfetivada().containsKey(vinculoFP.getId())) {
                detalheProcessamentoFolha.getServidoresComFolhaEfetivada().put(vinculoFP.getId(), true);
            }
        }
    }


    private void checarMultiploVinculo(VinculoFP v, FolhaCalculavel folha, Date data, Set<VinculoFP> lista, Map<VinculoFP, Long> mapFontePagadora, Set<VinculoFP> listraOutrosVinculos, Map<Long, Long> idsFontesPagadoras) {
        VinculoFP vinculo = null;

        if (lista.size() == 1) {
            Long id = vinculoFPFacade.recuperarMultiplosVinculosParaFolhaSQL(v, Mes.getMesToInt(new DateTime(data).getMonthOfYear()), new DateTime(data).getYear(), data);
            if (id != null) {
                vinculo = vinculoFPFacade.recuperarSimples(id);
            }
        } else {
            HashSet<VinculoFP> vinculoFPS = Sets.newHashSet(lista);
            vinculoFPS.addAll(listraOutrosVinculos);
            vinculo = getOutroVinculo(v, vinculoFPS, mapFontePagadora);
        }

        if (vinculo == null) {
            v.setPrimeiroContrato(true);
            return;
        }
        mapFontePagadora.putAll(buscarFontesPagadorasPorVinculo(Sets.newHashSet(vinculo), folha.getMes(), folha.getAno(), idsFontesPagadoras));
        definirAtributosVinculo(v, folha);
        definirAtributosVinculo(vinculo, folha);
        verificarEDefinirMultiploVinculoConformeSistemaLegado(v, vinculo, mapFontePagadora);

    }

    private VinculoFP getOutroVinculo(VinculoFP v, Set<VinculoFP> lista, Map<VinculoFP, Long> mapFontePagadora) {
        for (VinculoFP vinculoFP : lista) {
            Long fonteVinculo1 = mapFontePagadora.get(vinculoFP);
            Long fonteVinculo2 = mapFontePagadora.get(v);
            if (fonteVinculo1 == null || fonteVinculo2 == null) {
                fonteVinculo1 = 0l;
                fonteVinculo2 = 0l;
            }
            if (!vinculoFP.getId().equals(v.getId()) && fonteVinculo1.equals(fonteVinculo2) && vinculoFP.getMatriculaFP().getPessoa().getId().equals(v.getMatriculaFP().getPessoa().getId())) {
                return vinculoFP;
            }
        }
        return null;
    }

    private void definirAtributosVinculo(VinculoFP vinculo, FolhaCalculavel folha) {
        vinculo.setAno(folha.getAno());
        vinculo.setMes(folha.getMes().getNumeroMes());
        vinculo.setFolha(folha);
    }

    @Deprecated
    private void verificarEDefinirMultiploVinculo(VinculoFP v, VinculoFP vinculo) {
        //TODO 2 próximos ifs de teste de pensionsita com vinculo vigente na prefeitura(revisar função de verificação de multiplo vinculo)
        if ((v instanceof Pensionista) && (vinculo instanceof ContratoFP)) {
            v.setPrimeiroContrato(true);
            return;
        }
        if ((v instanceof ContratoFP) && (vinculo instanceof Pensionista)) {
            v.setPrimeiroContrato(false);
            return;
        }
        if (Integer.parseInt(vinculo.getNumero()) > Integer.parseInt(v.getNumero())) {
            v.setPrimeiroContrato(true);
        }
        if ((vinculo instanceof Aposentadoria) && v instanceof Pensionista) {
            v.setPrimeiroContrato(false);
            return;
        }
        if ((vinculo instanceof ContratoFP) && v instanceof Pensionista) {
            v.setPrimeiroContrato(false);
            return;
        }
        if ((v instanceof ContratoFP) && vinculo instanceof Pensionista) {
            v.setPrimeiroContrato(true);
            return;
        }
        if (vinculo instanceof ContratoFP && v instanceof ContratoFP) {
            if (((ContratoFP) vinculo).getModalidadeContratoFP().getCodigo() == 1L && ((ContratoFP) v).getModalidadeContratoFP().getCodigo() != 1L) {
                v.setPrimeiroContrato(false);
                return;
            }
            if (((ContratoFP) vinculo).getModalidadeContratoFP().getCodigo() == 1L && ((ContratoFP) v).getModalidadeContratoFP().getCodigo() == 1L) {
                if (v.getInicioVigencia().before(vinculo.getInicioVigencia())) {
                    v.setPrimeiroContrato(true);
                    return;
                } else {
                    v.setPrimeiroContrato(false);
                    return;
                }
            }
        }
        v.setPrimeiroContrato(false);
    }

    private void verificarEDefinirMultiploVinculoConformeSistemaLegado(VinculoFP atual, VinculoFP outro, Map<VinculoFP, Long> mapFontePagadora) {
        Long fonteVinculo1 = mapFontePagadora.get(atual);
        Long fonteVinculo2 = mapFontePagadora.get(outro);
        if (fonteVinculo1 == null || fonteVinculo2 == null) {
            fonteVinculo1 = 0l;
            fonteVinculo2 = 0l;
        }
        boolean temDiasTrabalhados = temDiasTrabalhados(outro);
        if ((atual.getDescontarIrMultiploVinculo() || outro.getDescontarIrMultiploVinculo()) && fonteVinculo1.equals(fonteVinculo2)) {
            atual.setPrimeiroContrato(!atual.getDescontarIrMultiploVinculo());
            return;
        }
        if (atual.getMatriculaFP().getMatricula().equals(outro.getMatriculaFP().getMatricula()) && fonteVinculo1.equals(fonteVinculo2)) {
            if (Integer.valueOf(atual.getNumero()) > Integer.valueOf(outro.getNumero())) {
                if (!temDiasTrabalhados) {
                    atual.setPrimeiroContrato(true);
                } else {
                    atual.setPrimeiroContrato(false);
                }
            } else {
                atual.setPrimeiroContrato(true);
            }
        } else if (Integer.valueOf(atual.getMatriculaFP().getMatricula()) > Integer.valueOf(outro.getMatriculaFP().getMatricula()) && fonteVinculo1.equals(fonteVinculo2)) {
            if (!temDiasTrabalhados) {
                atual.setPrimeiroContrato(true);
            } else {
                atual.setPrimeiroContrato(false);
            }
        } else {
            atual.setPrimeiroContrato(true);
        }
    }

    public boolean temDiasTrabalhados(VinculoFP vinculo) {
        return funcoesFolhaFacade.diasTrabalhados(vinculo, vinculo.getMes(), vinculo.getAno(), TipoDiasTrabalhados.NORMAL, null, null, null) > 0;
    }

    private List<List<VinculoFP>> particionarEmCinco(List<VinculoFP> ids) {
        List<List<VinculoFP>> retorno = new ArrayList<>();
        int parte = ids.size() / 5;
        retorno.add(ids.subList(0, parte));
        retorno.add(ids.subList(parte, parte * 2));
        retorno.add(ids.subList(parte * 2, parte * 3));
        retorno.add(ids.subList(parte * 3, parte * 4));
        retorno.add(ids.subList(parte * 4, ids.size()));
        return retorno;
    }


    private void processarLancamentosTransients(VinculoFP vinculo, FolhaDePagamento f, DetalheProcessamentoFolha detalheProcessamentoFolha, FolhaDePagamentoNovoCalculador contextTransient) {
        List<LancamentoFP> lista = lancamentoFPFacade.buscarLancamentosPorTipo(vinculo, f.getAno(), f.getMes().getNumeroMes(), f.getTipoFolhaDePagamento(), detalheProcessamentoFolha.getItemCalendarioFP().getDataUltimoDiaProcessamento(), false);
        contextTransient.preencheLacamentosCache(lista);

        for (LancamentoFP lancamento : lista) {
            EventoFP eventoFP = lancamento.getEventoFP();
            if (contextTransient.isServidorEVerbaBloqueado(vinculo, lancamento.getEventoFP())) {
                continue;
            }

            contextTransient.setEventoCalculandoAgora(eventoFP);
            contextTransient.setLancamentoFP(lancamento);
            if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                BigDecimal valorProporcionalizado = proporcionalizarValorParaLancamentos(contextTransient, lancamento.getQuantificacao(), lancamento, vinculo);
                if (contextTransient.getEventoValorFormula().containsKey(eventoFP) && !TipoFolhaDePagamento.isFolhaRescisao(f)) {
                    valorProporcionalizado = valorProporcionalizado.add(contextTransient.getEventoValorFormula().get(eventoFP));
                }
                contextTransient.getEventoValorFormula().put(eventoFP, valorProporcionalizado);
                contextTransient.getEventoValorIntegral().put(eventoFP, lancamento.getQuantificacao());

            }
        }
        for (LancamentoFP lancamento : lista) {
            EventoFP eventoFP = lancamento.getEventoFP();
            if (contextTransient.isServidorEVerbaBloqueado(vinculo, lancamento.getEventoFP())) {
                continue;
            }
            if (!lancamento.getProporcionalizar() && (contextTransient.isNaoPagaEventoEmFerias(eventoFP) || contextTransient.isNaoPagaEventoEmLicencaPremio(eventoFP))) {
                continue;
            }

            if (TipoLancamentoFP.isReferencia(lancamento.getTipoLancamentoFP())) {
                contextTransient.setEventoCalculandoAgora(eventoFP);
                contextTransient.setLancamentoFP(lancamento);
                contextTransient.getEventoValorReferencia().put(eventoFP, lancamento.getQuantificacao());
                if (!contextTransient.getEventoValorFormula().containsKey(eventoFP) || !contextTransient.getEventoValorIntegral().containsKey(eventoFP)) { // já calculou?
                    if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.REFERENCIA) && lancamento.getBaseFP() != null) {
                        calculaBaseSobreParcentualDeLancamentofp(contextTransient, lancamento);
                    } else {
                        contextTransient.avaliaFormulaDoEventoFP(eventoFP);                // necessário verificar, pois o cálculo de eventos lançados por referência
                        contextTransient.avaliaValorIntegralDoEventoFP(eventoFP);
                    }
                    if (lancamento.getEventoFP().getProporcionalizaDiasTrab() && !contextTransient.isNaoPagaEventoEmFerias(eventoFP) && !contextTransient.isNaoPagaEventoEmLicencaPremio(eventoFP)) {                                          // pode gerar o cálculo de outros eventos
                        BigDecimal valor = contextTransient.getEventoValorFormula().get(eventoFP);
                        contextTransient.getEventoValorFormula().remove(eventoFP);
                        contextTransient.getEventoValorFormula().put(eventoFP, proporcionalizarValorParaLancamentos(contextTransient, valor, lancamento, vinculo));
                    }
                }
            }
        }
    }

    private FolhaDePagamentoNovoCalculador processarFichaContextoTransient(VinculoFP vinculo, FolhaDePagamento f, DetalheProcessamentoFolha detalheProcessamentoFolha, FichaFinanceiraFP fichaFinanceiraFP, FolhaDePagamentoNovoCalculador contextoPersistente, List<EventoFP> eventosAutomaticos, Double diasTrabalhados) {
        FolhaDePagamento folhaClone = (FolhaDePagamento) Util.clonarObjeto(f);
        folhaClone.setId(null);
        folhaClone.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);

        FichaFinanceiraFP fichaClone = (FichaFinanceiraFP) Util.clonarObjeto(fichaFinanceiraFP);
        fichaClone.setId(null);
        fichaClone.setFolhaDePagamento(folhaClone);

        FolhaDePagamentoNovoCalculador contextTransient = new FolhaDePagamentoNovoCalculador(vinculo, folhaClone, TipoCalculoFP.NORMAL, funcoesFolhaFacade, detalheProcessamentoFolha, this);
        contextTransient.getValorMetodo().put("diasTrabalhados", diasTrabalhados);
        processarLancamentosTransients(vinculo, folhaClone, detalheProcessamentoFolha, contextTransient);
        calcularEventosAutomaticos(contextTransient, vinculo, eventosAutomaticos);
        return contextTransient;
    }

    public void calculoFolhaIndividualNovo(VinculoFP vinculoFP, FolhaCalculavel f, boolean mostra, DetalheProcessamentoFolha detalheProcessamentoFolha,
                                           List<EventoFP> eventosAutomaticos, UsuarioSistema usuario) {

        VinculoFP vinculo = vinculoFP;

        logger.debug("Vinculo: " + vinculo);

        FichaFinanceiraFP fichaFinanceiraFP = null;
        FichaFinanceiraFPSimulacao fichaSimulacao = null;
        if (!isSimulacao(f)) {
            fichaFinanceiraFP = criarFichaFinanceira(f, vinculo, usuario);
            definirRecursoFPAndUnidadeAdministrativa(fichaFinanceiraFP, detalheProcessamentoFolha);
            fichaFinanceiraFPFacade.salvarNovo(fichaFinanceiraFP);
        } else {
            fichaSimulacao = criarFichaSimulacao(f, vinculo);
            definirRecursoFPAndUnidadeAdministrativaSimulacao(fichaSimulacao, detalheProcessamentoFolha);
        }
        defineParametros(f, detalheProcessamentoFolha, vinculo);
        FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador = new FolhaDePagamentoNovoCalculador(vinculo, f, TipoCalculoFP.NORMAL, funcoesFolhaFacade, detalheProcessamentoFolha, this);

        try {
            checarRestricoesVinculo(vinculo, folhaDePagamentoNovoCalculador, detalheProcessamentoFolha);
        } catch (Exception e) {
            criaObservacaoCalculo(vinculoFP, detalheProcessamentoFolha, e.getMessage());
            logger.debug(e.getMessage());
            return;
        }
        if (detalheProcessamentoFolha.getQuantidadeMesesRetroativos() > 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.set(Calendar.YEAR, f.getAno());
            c.set(Calendar.MONTH, f.getMes().ordinal());
            c.set(Calendar.DAY_OF_MONTH, 1);
            logger.debug("Quantidade meses retroativos: " + detalheProcessamentoFolha.getQuantidadeMesesRetroativos());
            c.add(Calendar.MONTH, -detalheProcessamentoFolha.getQuantidadeMesesRetroativos());
            ResultadoCalculoRetroativoFP res = new ResultadoCalculoRetroativoFP(new Date());
            res.setDataInicialCalculoRetroativo(c.getTime());
            if (res != null && res.temCalculoRetroativo()) {

                calcularRetroativosNovoMetodo(res, fichaFinanceiraFP, detalheProcessamentoFolha);
            }
        }
        defineParametros(f, detalheProcessamentoFolha, vinculo);
        if (!TipoFolhaDePagamento.NORMAL.equals(f.getTipoFolhaDePagamento()) && f instanceof FolhaDePagamento && f.isProcessarCalculoTransient()) {
            FolhaDePagamentoNovoCalculador contextoTransient = processarFichaContextoTransient(vinculo, (FolhaDePagamento) f, detalheProcessamentoFolha, fichaFinanceiraFP, folhaDePagamentoNovoCalculador, eventosAutomaticos, TipoFolhaDePagamento.isFolhaRescisao(f) ? folhaDePagamentoNovoCalculador.diasTrabalhados() : new Double(Util.getDiasMes(f.getMes().getNumeroMes(), f.getAno())));
            folhaDePagamentoNovoCalculador.setContextoTransient(contextoTransient);
            logger.debug("Fim da execução do contexto transient...");
        }

        detalheProcessamentoFolha.setCalculandoRetroativo(false);
        vinculo.setCalculandoRetroativo(false);

        List<EventoTotalItemFicha> itemPagosNaFolhaNormal = new ArrayList<>();
        if (TipoFolhaDePagamento.isFolhaRescisao(f)) {
            //        //Para folhas rescisão, caso o servidor seja exonerado depois do fechamento da folha.. deve devolver o valor dos dias pagos e não trabalhados.
            if (folhaDePagamentoNovoCalculador.temEventoOutraFolhaNoMesmoMesParaRescisao()) {
                recuperarEColocarNaFolhaDeRecisaoEventosDaFolhaNormal(folhaDePagamentoNovoCalculador, vinculo, detalheProcessamentoFolha, itemPagosNaFolhaNormal);
                folhaDePagamentoNovoCalculador.setItemPagosNaFolhaNormal(itemPagosNaFolhaNormal);
            }
        }
        if (TipoFolhaDePagamento.isFolhaComplementar(f.getTipoFolhaDePagamento())) {
            if (folhaDePagamentoNovoCalculador.hasEventoOutraFolhaNoMesmoMesFolhaNormalComSaldo()) {
                vinculo.setPrimeiroContrato(false);
            }
        }
        if (TipoFolhaDePagamento.isFolha13Salario(f.getTipoFolhaDePagamento())) {
            folhaDePagamentoNovoCalculador.getValorMetodo().put("diasTrabalhados", folhaDePagamentoNovoCalculador.obterDiasDoMes());
        }


        List<LancamentoFP> lista = lancamentoFPFacade.buscarLancamentosPorTipo(vinculo, f.getAno(), f.getMes().getNumeroMes(), f.getTipoFolhaDePagamento(), detalheProcessamentoFolha.getItemCalendarioFP().getDataUltimoDiaProcessamento(), false);
        removerVerbasBloqueadasNaoLancadas(lista, detalheProcessamentoFolha, f.getTipoFolhaDePagamento());
        lista.addAll(criarLancamentosPara13Salario(vinculo, f, lista));
        folhaDePagamentoNovoCalculador.preencheLacamentosCache(lista);
        Map<LancamentoFP, BigDecimal> lancamentosConsignados = new LinkedHashMap<>();
        Map<LancamentoFP, BigDecimal> lancamentos = new LinkedHashMap<>();

        for (LancamentoFP lancamento : lista) {
            EventoFP eventoFP = lancamento.getEventoFP();
            if (folhaDePagamentoNovoCalculador.isServidorEVerbaBloqueado(vinculo, lancamento.getEventoFP())) {
                continue;
            }
            if (!lancamento.getProporcionalizar() && (folhaDePagamentoNovoCalculador.isNaoPagaEventoEmFerias(eventoFP) || folhaDePagamentoNovoCalculador.isNaoPagaEventoEmLicencaPremio(eventoFP))) {
                continue;
            }

            if (!lancamento.getTipoFolhaDePagamento().equals(folhaDePagamentoNovoCalculador.getFolhaDePagamento().getTipoFolhaDePagamento())) {
                continue;
            }

            processaMotivoRejeicaoParaLancamento(lancamento);
            folhaDePagamentoNovoCalculador.setEventoCalculandoAgora(eventoFP);
            folhaDePagamentoNovoCalculador.setLancamentoFP(lancamento);
            if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                if (LOG2) {
                    logger.debug("iniciando o calculo do lançamento valor " + lancamento.getEventoFP());
                }
                if (folhaDePagamentoNovoCalculador.isCalculo13(eventoFP)) {
                    //no caso dos eventos lançados com verba fixa
                    if (folhaDePagamentoNovoCalculador.isUltimoAcumulado(eventoFP)) {
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, new BigDecimal(folhaDePagamentoNovoCalculador.recuperarValor13ProporcionalUltimoMes(eventoFP) + ""));
                        processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                        putLancamentosCalculados(lancamentosConsignados, lancamentos, lancamento, folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));
                    } else if (folhaDePagamentoNovoCalculador.isValorAno(eventoFP)) {
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, new BigDecimal(folhaDePagamentoNovoCalculador.recuperarValor13ProporcionalMediaAno(eventoFP) + ""));
                        processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                        putLancamentosCalculados(lancamentosConsignados, lancamentos, lancamento, folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));
                    } else if (folhaDePagamentoNovoCalculador.isHorasAno(eventoFP)) {
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, proporcionalizarValorParaLancamentos(folhaDePagamentoNovoCalculador, lancamento.getQuantificacao(), lancamento, vinculo));
                        processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                        putLancamentosCalculados(lancamentosConsignados, lancamentos, lancamento, folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));
                    } else if (folhaDePagamentoNovoCalculador.isLerFormula(eventoFP)) {
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, proporcionalizarValorParaLancamentos(folhaDePagamentoNovoCalculador, lancamento.getQuantificacao(), lancamento, vinculo));
                        processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                        putLancamentosCalculados(lancamentosConsignados, lancamentos, lancamento, folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));
                    } else continue;


                } else {
//                    if (paraFolhaDeRescisaoVerificaSeFoiPagoNaFolha(vinculo, lancamento.getEventoFP(), detalheProcessamentoFolha)) {
//                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, BigDecimal.ZERO);
//                        continue;
//                    }

                    BigDecimal valorProporcionalizado = proporcionalizarValorParaLancamentos(folhaDePagamentoNovoCalculador, lancamento.getQuantificacao(), lancamento, vinculo);
                    if (folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(eventoFP) && !TipoFolhaDePagamento.isFolhaRescisao(f)) {
                        valorProporcionalizado = valorProporcionalizado.add(folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));
                    }

                    folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, valorProporcionalizado);
                    folhaDePagamentoNovoCalculador.getEventoValorIntegral().put(eventoFP, lancamento.getQuantificacao());

                    if (lancamento.getValorDaBase() != null) {
                        folhaDePagamentoNovoCalculador.getEventoValorBase().put(eventoFP, lancamento.getValorDaBase());
                    }

                    if (LOG2) {
                        logger.debug("valor do lançamento valor " + eventoFP + " : " + folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));
                    }
                    processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                    putLancamentosCalculados(lancamentosConsignados, lancamentos, lancamento, folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));
                }
            }
        }
        if (MOSTRAR_ESTATISTICAS) {
            //System.out.println((System.currentTimeMillis() - tempo) + "ms para calcular lancamentos do tipo valor");
        }
        // Necessário calcular eventos lançados por valor separado dos lançados por referência, pois
        // o cálculo de lançamentos por referência pode gerar o cálculo de eventos lançados por valor

        for (LancamentoFP lancamento : lista) {
            EventoFP eventoFP = lancamento.getEventoFP();
            if (folhaDePagamentoNovoCalculador.isServidorEVerbaBloqueado(vinculo, lancamento.getEventoFP())) {
                continue;
            }
            if (!lancamento.getProporcionalizar() && (folhaDePagamentoNovoCalculador.isNaoPagaEventoEmFerias(eventoFP) || folhaDePagamentoNovoCalculador.isNaoPagaEventoEmLicencaPremio(eventoFP))) {
                continue;
            }
            processaMotivoRejeicaoParaLancamento(lancamento
            );
            if (TipoLancamentoFP.isReferencia(lancamento.getTipoLancamentoFP())) {
                if (LOG2) {
                    logger.debug("iniciando o calculo do lançamento referencia " + lancamento.getEventoFP());
                }

                folhaDePagamentoNovoCalculador.setEventoCalculandoAgora(eventoFP);
                folhaDePagamentoNovoCalculador.setLancamentoFP(lancamento);
                if (folhaDePagamentoNovoCalculador.isCalculo13(eventoFP)) {
                    if (folhaDePagamentoNovoCalculador.isUltimoAcumulado(eventoFP)) {
                        folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(eventoFP, new BigDecimal(folhaDePagamentoNovoCalculador.quantidadeMesesTrabalhadosAno() + ""));
                        processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                        putLancamentosCalculados(lancamentosConsignados, lancamentos, lancamento, folhaDePagamentoNovoCalculador.getEventoValorReferencia().get(eventoFP));
                    } else if (folhaDePagamentoNovoCalculador.isValorAno(eventoFP)) {
                        folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(eventoFP, new BigDecimal(TODOS_MESES + ""));
                        processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                        putLancamentosCalculados(lancamentosConsignados, lancamentos, lancamento, folhaDePagamentoNovoCalculador.getEventoValorReferencia().get(eventoFP));
                    } else if (folhaDePagamentoNovoCalculador.isHorasAno(eventoFP)) {
                        //TODO proporcionalizar???
                        if (lancamento.getValorDaBase() != null) {
                            folhaDePagamentoNovoCalculador.getEventoValorBase().put(eventoFP, lancamento.getValorDaBase());
                        }
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, lancamento.getQuantificacao());
                        processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                        putLancamentosCalculados(lancamentosConsignados, lancamentos, lancamento, folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));
                    } else continue;
                } else {
                    //TODO proporcionalizar???
                    processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                    folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(eventoFP, lancamento.getQuantificacao());
                }
//                if (paraFolhaDeRescisaoVerificaSeFoiPagoNaFolha(vinculo, lancamento.getEventoFP(), detalheProcessamentoFolha)) {
//                    folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, BigDecimal.ZERO);
//                    continue;
//                }

                BigDecimal valorCalculado = BigDecimal.ZERO;
                if (temMaisDeUmLancamentoComoMesmoEvento(lista, eventoFP) && folhaDePagamentoNovoCalculador.getLancamentosReferenciaJaCalculados().containsKey(eventoFP.getCodigo())) {
                    valorCalculado = folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP);
                    folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(eventoFP);
                    folhaDePagamentoNovoCalculador.limparCacheValoresMetodos();
                }
                if (!folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(eventoFP)) { // já calculou?
                    if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.REFERENCIA) && lancamento.getBaseFP() != null) {
                        calculaBaseSobreParcentualDeLancamentofp(folhaDePagamentoNovoCalculador, lancamento);
                    } else {
                        folhaDePagamentoNovoCalculador.avaliaFormulaDoEventoFP(eventoFP);                // necessário verificar, pois o cálculo de eventos lançados por referência
                        folhaDePagamentoNovoCalculador.avaliaValorIntegralDoEventoFP(eventoFP);
                    }
                    if (lancamento.getEventoFP().getProporcionalizaDiasTrab() && !folhaDePagamentoNovoCalculador.isNaoPagaEventoEmFerias(eventoFP) && !folhaDePagamentoNovoCalculador.isNaoPagaEventoEmLicencaPremio(eventoFP)) {                                          // pode gerar o cálculo de outros eventos
                        BigDecimal valor = folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP);
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(eventoFP);
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, proporcionalizarValorParaLancamentos(folhaDePagamentoNovoCalculador, valor, lancamento, vinculo));
                        if (LOG2) {
                            logger.debug("valor do lançamento referencia " + eventoFP + " : " + folhaDePagamentoNovoCalculador.getEventoValorFormula().get(lancamento.getEventoFP()));
                        }
                    }
                    processarLancamentoFolhaComplementar(folhaDePagamentoNovoCalculador, lancamento, vinculo);
                    putLancamentosCalculados(lancamentosConsignados, lancamentos, lancamento, folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));

                    List<LancamentoFP> lancamentosJaCalculados = Lists.newArrayList();
                    if (folhaDePagamentoNovoCalculador.getLancamentosReferenciaJaCalculados().containsKey(eventoFP.getCodigo())) {
                        lancamentosJaCalculados = folhaDePagamentoNovoCalculador.getLancamentosReferenciaJaCalculados().get(eventoFP.getCodigo());
                    }
                    lancamentosJaCalculados.add(lancamento);
                    folhaDePagamentoNovoCalculador.getLancamentosReferenciaJaCalculados().put(eventoFP.getCodigo(), lancamentosJaCalculados);
                    BigDecimal valorAtual = folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP);
                    folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, valorAtual.add(valorCalculado));
                }
            }
        }

        if (MOSTRAR_ESTATISTICAS) {
            //System.out.println((System.currentTimeMillis() - tempo) + "ms para calcular lancamentos do tipo referencia");
        }


        if (temPensaoAlimenticia(folhaDePagamentoNovoCalculador, detalheProcessamentoFolha.getEventosPensaoAlimenticia())) {
            for (EventoFP eventoFP : detalheProcessamentoFolha.getEventosPensaoAlimenticia()) {
                folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, BigDecimal.ZERO);
            }
        }

        calcularEventosAutomaticos(folhaDePagamentoNovoCalculador, vinculo, eventosAutomaticos);

        if (temPensaoAlimenticia(folhaDePagamentoNovoCalculador, detalheProcessamentoFolha.getEventosPensaoAlimenticia())) {

            reloadObjectosPensaoAlimenticia(detalheProcessamentoFolha, folhaDePagamentoNovoCalculador);
            calcularEventosAutomaticos(folhaDePagamentoNovoCalculador, vinculo, detalheProcessamentoFolha.getEventosPensaoAlimenticia());
            folhaDePagamentoNovoCalculador.zerarBasesIRRF();
            if (!hasPensaoAlimenticiaSobreLiquido(detalheProcessamentoFolha, vinculo, folhaDePagamentoNovoCalculador)) {
                folhaDePagamentoNovoCalculador.zeraValoresBaseImpostoDeRenda();
                calcularEventosAutomaticos(folhaDePagamentoNovoCalculador, vinculo, detalheProcessamentoFolha.getEventosImpostoDeRenda());
            }

            if (hasPensaoAlimenticiaSemIRFicticioNaoBaseSobreLiquido(detalheProcessamentoFolha, folhaDePagamentoNovoCalculador)) {
                reloadObjectosPensaoAlimenticia(detalheProcessamentoFolha, folhaDePagamentoNovoCalculador);
                calcularEventosAutomaticos(folhaDePagamentoNovoCalculador, vinculo, detalheProcessamentoFolha.getEventosPensaoAlimenticia());
            }

        }

        if (TipoFolhaDePagamento.isFolhaComplementar(f.getTipoFolhaDePagamento())) {

            List<ItemFichaFinanceiraFP> itensFolhaNormal = fichaFinanceiraFPFacade.totalItemFichaFinanceiraFP(vinculo, vinculo.getMes(), vinculo.getAno(), TipoFolhaDePagamento.NORMAL, false);
            logger.debug("" + itensFolhaNormal);
            if (!isCalculaFolhaComplementarSemFolhaNormal(f.getTipoFolhaDePagamento()) && itensFolhaNormal.isEmpty()) {
                folhaDePagamentoNovoCalculador.addItensDetalhesErro(vinculo, "Atençao, servidor sem folha normal calculada.");
                folhaDePagamentoNovoCalculador.addItensDetalhesErro(vinculo, "Atenção, não é possível calcular folha complementar para um servidor sem folha normal.");
                return;
            }
        }


        if (TipoFolhaDePagamento.isFolhaRescisao(f)) {
            forcarReleituraDasFormulas(folhaDePagamentoNovoCalculador);
            removerEventosJaPagosNaFolhaNormalParaRescisao(folhaDePagamentoNovoCalculador, itemPagosNaFolhaNormal);

            //Para folhas rescisão, caso o servidor seja exonerado depois do fechamento da folha.. deve devolver o valor dos dias pagos e não trabalhados.
            compararFolhaRescisoriaComACompetenciaAtual(f, detalheProcessamentoFolha, eventosAutomaticos, vinculo,
                fichaFinanceiraFP, folhaDePagamentoNovoCalculador);
        }

        if (TipoFolhaDePagamento.isFolhaComplementar(f.getTipoFolhaDePagamento())) {
            removerEventosJaPagosNaFolhaNormalParaRescisao(folhaDePagamentoNovoCalculador, itemPagosNaFolhaNormal);
        }
        if (!isSimulacao(f)) {
            fichaFinanceiraFP.setDiasTrabalhados(BigDecimal.valueOf(folhaDePagamentoNovoCalculador.diasTrabalhados()));
        }


        Map<LancamentoFP, BigDecimal> treeMapLancamentos = sortByComparator(lancamentosConsignados);


        BigDecimal valorVantagem = BigDecimal.ZERO;
        BigDecimal valorDesconto = BigDecimal.ZERO;
        for (Map.Entry<EventoFP, BigDecimal> eventosCalculados : folhaDePagamentoNovoCalculador.getEventoValorFormula().entrySet()) {
            EventoFP e = eventosCalculados.getKey();
            TipoEventoFP key = folhaDePagamentoNovoCalculador.getEventoEstornoValor().containsKey(e) ? folhaDePagamentoNovoCalculador.getEventoEstornoValor().get(e) : e.getTipoEventoFP();
            BigDecimal valor = eventosCalculados.getValue();
            if (key.equals(TipoEventoFP.VANTAGEM)) {
                valorVantagem = valorVantagem.add(valor);
            } else if (key.equals(TipoEventoFP.DESCONTO)) {
                valorDesconto = valorDesconto.add(valor);
            }
        }
        valorDesconto = valorDesconto.add(fichaFinanceiraFPFacade.recuperaValorTotalPorTipoEvento(vinculo.getIdCalculo(), detalheProcessamentoFolha.getMes(), detalheProcessamentoFolha.getAno(), vinculo.getFolha().getTipoFolhaDePagamento(), TipoEventoFP.DESCONTO));
        valorVantagem = valorVantagem.add(fichaFinanceiraFPFacade.recuperaValorTotalPorTipoEvento(vinculo.getIdCalculo(), detalheProcessamentoFolha.getMes(), detalheProcessamentoFolha.getAno(), vinculo.getFolha().getTipoFolhaDePagamento(), TipoEventoFP.VANTAGEM));
        BigDecimal liquido = valorVantagem.subtract(valorDesconto);

        if (liquido.compareTo(BigDecimal.ZERO) < 0) {
            for (Map.Entry<LancamentoFP, BigDecimal> keySet : treeMapLancamentos.entrySet()) {
                folhaDePagamentoNovoCalculador.getEventoValorFormula().put(keySet.getKey().getEventoFP(), BigDecimal.ZERO);
            }
            treeMapLancamentos = new TreeMap<>(new LinkedHashMap<LancamentoFP, BigDecimal>());
        }

        if (!treeMapLancamentos.isEmpty()) {
            processarConsignados(f, detalheProcessamentoFolha, vinculo, folhaDePagamentoNovoCalculador, lancamentosConsignados, treeMapLancamentos);

        }

        if (MOSTRAR_ESTATISTICAS) {
            //System.out.println((System.currentTimeMillis() - tempo) + "ms para verfificar limite de margem");
        }
//        if (TipoFolhaDePagamento.isFolhaRescisao(f)) {
//            removerEventosJaPagosNaFolhaNormalParaRescisao(folhaDePagamentoNovoCalculador, itemPagosNaFolhaNormal);
//        }

        //Eventos ADT FOPAG E DEV FOPAG;
        verificarServidorComLiquidoNegativo(vinculo, folhaDePagamentoNovoCalculador, detalheProcessamentoFolha);
        verificarEventosFolhaDeAdiantamento13Salario(folhaDePagamentoNovoCalculador, detalheProcessamentoFolha, f);

        try {

            for (EventoFP eventoFP : folhaDePagamentoNovoCalculador.getEventoValorFormula().keySet()) {
                verificarLancamentoRejeitadoSemEstorno(vinculo, folhaDePagamentoNovoCalculador, treeMapLancamentos, eventoFP);
                if (folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP).compareTo(BigDecimal.ZERO) > 0 && !folhaDePagamentoNovoCalculador.getEventoEstornoValor().containsKey(eventoFP)) {
                    if (folhaDePagamentoNovoCalculador.avaliarRegrasPrimarias(eventoFP)) {
                        ItemFichaFinanceiraFP item = new ItemFichaFinanceiraFP();
                        item.setMes(f.getMes().getNumeroMes());
                        item.setAno(f.getAno());
                        item.setEventoFP(eventoFP);
                        item.setTipoEventoFP(folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().containsKey(eventoFP) ? folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().get(eventoFP) : eventoFP.getTipoEventoFP());
                        item.setFichaFinanceiraFP(fichaFinanceiraFP);
                        item.setTipoCalculoFP(TipoCalculoFP.NORMAL);
                        item.setUnidadeReferencia(eventoFP.getUnidadeReferencia());
                        item.setValor(new BigDecimal(getValorNormalizado(folhaDePagamentoNovoCalculador.avaliaFormulaDoEventoFP(eventoFP)) + ""));
                        item.setValorBaseDeCalculo(getValorNormalizado(folhaDePagamentoNovoCalculador.avaliaValorBaseDoEventoFP(eventoFP)));
                        item.setValorIntegral(folhaDePagamentoNovoCalculador.avaliaValorIntegralDoEventoFP(eventoFP));
                        item.setValorReferencia(folhaDePagamentoNovoCalculador.avaliaReferenciaDoEventoFP(eventoFP));

                        if (!isSimulacao(f)) {
                            String idVinculo = "V" + item.getFichaFinanceiraFP().getVinculoFP().getId();
                            String idFicha = "F" + fichaFinanceiraFP.getId();
                            String codigoEvento = "E" + eventoFP.getCodigo();
                            String tipoFolha = "F" + fichaFinanceiraFP.getFolhaDePagamento().getTipoFolhaDePagamento().name();
                            String ano = "ANO" + item.getAno();
                            String mes = "MES" + item.getMes();
                            String calculo = "CALCULO" + item.getTipoCalculoFP().name();

                            item.setSequencial(idVinculo.concat(idFicha).concat(codigoEvento).concat(tipoFolha).concat(ano).concat(mes).concat(calculo));

                            fichaFinanceiraFPFacade.salvarNovoItemFichaFinanceira(item);
                            fichaFinanceiraFP.getItemFichaFinanceiraFP().add(item);
                        } else {
                            criarItemFichaSimulacao(fichaSimulacao, item);
                        }
                        salvarMemoriaCalculo(folhaDePagamentoNovoCalculador, item);
                    }
                }
            }
        } catch (ConcurrentModificationException cme) {
            logger.error("ConcurrentModificationException ao criar ficha financeira. ", cme.getMessage());
            logger.error("Erro ao tentar criar ficha financeira. ", cme);
            criaObservacaoCalculo(vinculoFP, detalheProcessamentoFolha, cme.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao salvar ficha financeira: ", e);
            criaObservacaoCalculo(vinculoFP, detalheProcessamentoFolha, e.getMessage());
        }
        if (MOSTRAR_ESTATISTICAS) {
            //System.out.println((System.currentTimeMillis() - tempo) + "ms para salvar ficha financeira");
        }
    }

    /**
     * Método utilizado para realizar a comparacao de um competencia atual com o valor calculado no banco de dados
     * Ex: servidor teve direito a reajuste de salario com retroacao e esta sendo exonerado.
     * tarefa: 341367
     * chamado: 1756669
     *
     * @param folha
     * @param detalheProcessamentoFolha
     * @param eventosAutomaticos
     * @param vinculo
     * @param fichaFinanceiraFP
     * @param folhaDePagamentoNovoCalculador
     */
    private void compararFolhaRescisoriaComACompetenciaAtual(FolhaCalculavel folha, DetalheProcessamentoFolha detalheProcessamentoFolha,
                                                             List<EventoFP> eventosAutomaticos, VinculoFP vinculo,
                                                             FichaFinanceiraFP fichaFinanceiraFP,
                                                             FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador) {
        List<EventoTotalItemFicha> itensFicha = fichaFinanceiraFPFacade.buscarTotalEventosItemFichaFinanceira(vinculo, folha.getAno(), folha.getMes().getNumeroMes(), TipoFolhaDePagamento.NORMAL);

        FolhaDePagamentoNovoCalculador contextoTransient = processarFichaContextoTransient(vinculo, (FolhaDePagamento) folha, detalheProcessamentoFolha, fichaFinanceiraFP, folhaDePagamentoNovoCalculador, eventosAutomaticos, folhaDePagamentoNovoCalculador.diasTrabalhados());
        folhaDePagamentoNovoCalculador.setContextoTransient(contextoTransient);

        for (EventoTotalItemFicha eventoTotalItemFicha : itensFicha) {
            for (Map.Entry<EventoFP, BigDecimal> eventosCalculados : folhaDePagamentoNovoCalculador.getContextoTransient().getEventoValorFormula().entrySet()) {
                EventoFP key = eventosCalculados.getKey();
                if (key.getId().equals(eventoTotalItemFicha.getEvento().getId())) {
                    BigDecimal valorRecuperado = eventoTotalItemFicha.getTotal();
                    BigDecimal valorCalculado = eventosCalculados.getValue();
                    if (eventosCalculados.getKey().getArredondarValor() && !contextoTransient.getLancamentos().containsKey(key)) {
                        valorCalculado = valorCalculado.setScale(2, BigDecimal.ROUND_HALF_UP);
                    } else {
                        valorCalculado = UtilRH.truncate(String.valueOf(valorCalculado));
                    }
                    BigDecimal diferencaValor = getDiferencaValor(valorCalculado, valorRecuperado);


                    BigDecimal referenciaRecuperada = eventoTotalItemFicha.getReferencia();
                    BigDecimal referenciaCalculada = folhaDePagamentoNovoCalculador.getContextoTransient().getEventoValorReferencia().get(key);
                    BigDecimal diferencaReferencia = getDiferencaValor(referenciaCalculada, referenciaRecuperada);

                    if (diferencaValor.compareTo(BigDecimal.ZERO) != 0) {
                        if (diferencaValor.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal bigDecimal = folhaDePagamentoNovoCalculador.getEventoValorFormula().get(key);
                            if (bigDecimal != null) {
                                folhaDePagamentoNovoCalculador.getEventoValorFormula().put(key, diferencaValor.abs());
                            }
                        } else {
                            if (!TipoEventoFP.INFORMATIVO.equals(key.getTipoEventoFP()) && !TipoEventoFP.INFORMATIVO_DEDUTORA.equals(key.getTipoEventoFP())) {
                                folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().put(key, getTipoEventoCalculoRetroativo(key, diferencaValor));
                                folhaDePagamentoNovoCalculador.getEventoValorFormula().put(key, diferencaValor.abs());
                            } else {
                                folhaDePagamentoNovoCalculador.getEventoValorFormula().put(key, diferencaValor);
                            }
                        }
                        if (diferencaReferencia != null) {
                            folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(key, diferencaReferencia.abs());
                        }

                        BigDecimal valorBaseTransiente = folhaDePagamentoNovoCalculador.getContextoTransient().getEventoValorBase().get(key);
                        if (valorBaseTransiente != null) {
                            folhaDePagamentoNovoCalculador.getEventoValorBase().put(eventoTotalItemFicha.getEvento(), valorBaseTransiente);
                        }
                    }
                }
            }
        }
    }

    public void compararEventoFolhaRescisoriaComACompetenciaAtualSemAlterarCache(FolhaCalculavel folha, VinculoFP vinculo,
                                                                                 FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador,
                                                                                 EventoFP eventoFP, Map<EventoFP, BigDecimal> mapValor,
                                                                                 Map<EventoFP, TipoEventoFP> mapTipoEventoCalculado) {
        List<EventoTotalItemFicha> itensFicha = fichaFinanceiraFPFacade.buscarTotalEventoItemFichaFinanceira(vinculo, folha.getAno(), folha.getMes().getNumeroMes(), TipoFolhaDePagamento.NORMAL, eventoFP);

        for (EventoTotalItemFicha eventoTotalItemFicha : itensFicha) {
            for (Map.Entry<EventoFP, BigDecimal> eventosCalculados : folhaDePagamentoNovoCalculador.getContextoTransient().getEventoValorFormula().entrySet()) {
                EventoFP key = eventosCalculados.getKey();
                if (key.getId().equals(eventoTotalItemFicha.getEvento().getId()) && key.getId().equals(eventoFP.getId())) {
                    BigDecimal valorRecuperado = eventoTotalItemFicha.getTotal();
                    BigDecimal valorCalculado = eventosCalculados.getValue();
                    if (eventosCalculados.getKey().getArredondarValor() && !folhaDePagamentoNovoCalculador.getContextoTransient().getLancamentos().containsKey(key)) {
                        valorCalculado = valorCalculado.setScale(2, BigDecimal.ROUND_HALF_UP);
                    } else {
                        valorCalculado = UtilRH.truncate(String.valueOf(valorCalculado));
                    }
                    BigDecimal diferencaValor = getDiferencaValor(valorCalculado, valorRecuperado);

                    if (diferencaValor.compareTo(BigDecimal.ZERO) != 0) {
                        if (diferencaValor.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal bigDecimal = mapValor.get(eventoTotalItemFicha.getEvento());
                            if (bigDecimal != null) {
                                mapValor.put(eventoTotalItemFicha.getEvento(), diferencaValor.add(bigDecimal));
                            }
                            mapTipoEventoCalculado.put(key, TipoEventoFP.VANTAGEM);
                        } else {
                            if (!TipoEventoFP.INFORMATIVO.equals(key.getTipoEventoFP()) && !TipoEventoFP.INFORMATIVO_DEDUTORA.equals(key.getTipoEventoFP())) {
                                mapValor.put(eventoTotalItemFicha.getEvento(), diferencaValor);
                                mapTipoEventoCalculado.put(key, TipoEventoFP.DESCONTO);
                            }
                        }
                    }
                }
            }
        }
    }

    private BigDecimal getDiferencaValor(BigDecimal valorCalculado, BigDecimal valorRecuperado) {
        if (valorRecuperado != null && valorCalculado != null) {
            return valorCalculado.subtract(valorRecuperado);
        }
        return null;
    }

    private void salvarMemoriaCalculo(FolhaDePagamentoNovoCalculador calculador, ItemFichaFinanceiraFP item) {
        if (calculador.getMemoriaCalculoEventosBases() != null) {
            List<MemoriaCalculoRHDTO> memoriasPorEvento = buscarMemoriasDeCalculoPorKey(calculador.getMemoriaCalculoEventosBases(), item.getEventoFP());

            for (MemoriaCalculoRHDTO memoriaCalculoRHDTO : memoriasPorEvento) {
                MemoriaCalculoRH memoria = new MemoriaCalculoRH(item, memoriaCalculoRHDTO);
                em.persist(memoria);
            }
        }
    }

    private List<MemoriaCalculoRHDTO> buscarMemoriasDeCalculoPorKey(Map<String, List<MemoriaCalculoRHDTO>> memoriaCalculoEventosBases, EventoFP eventoFP) {
        List<MemoriaCalculoRHDTO> memoriasPorEvento = Lists.newLinkedList();
        for (Map.Entry<String, List<MemoriaCalculoRHDTO>> memoriasMap : memoriaCalculoEventosBases.entrySet()) {
            if (eventoFP.getBaseFormulaCalculo().contains(memoriasMap.getKey())) {
                return memoriasMap.getValue();
            }
        }
        return memoriasPorEvento;
    }

    private void verificarLancamentoRejeitadoSemEstorno(VinculoFP vinculo, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, Map<LancamentoFP, BigDecimal> treeMapLancamentos, EventoFP eventoFP) {
        for (Map.Entry<LancamentoFP, BigDecimal> lancamentoFPBigDecimalEntry : treeMapLancamentos.entrySet()) {
            LancamentoFP lanc = lancamentoFPBigDecimalEntry.getKey();
            if (eventoFP.getId().equals(lanc.getEventoFP().getId())) {
                if (folhaDePagamentoNovoCalculador.getEventoValorFormula().get(lanc.getEventoFP()).compareTo(BigDecimal.ZERO) > 0 && lanc.getMotivoRejeicao() != null) {
                    logger.info("ATENÇÃO CARACTERIZOU REJEIÇÃO SEM ESTORNO DE VALOR::: " + vinculo + " " + lanc);
                    folhaDePagamentoNovoCalculador.getEventoValorFormula().put(lanc.getEventoFP(), BigDecimal.ZERO);
                }
                break;
            }
        }
    }

    private boolean hasPensaoAlimenticiaSobreLiquido(DetalheProcessamentoFolha detalheProcessamentoFolha, VinculoFP vinculo, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador) {
        for (EventoFP eventoFP : detalheProcessamentoFolha.getEventosPensaoAlimenticia()) {
            try {
                if (folhaDePagamentoNovoCalculador.temPensaoAlimenticia(eventoFP.getCodigo())) {
                    TipoValorPensaoAlimenticia tipoValor = folhaDePagamentoNovoCalculador.recuperaTipoValorPensaoAlimenticia(eventoFP.getCodigo());
                    if (tipoValor != null && !TipoValorPensaoAlimenticia.BASE_SOBRE_LIQUIDO.equals(tipoValor)) {
                        return false;
                    }
                }
            } catch (Exception e) {
                logger.error("Erro ao tentar identificar pensão alimentícia", e);
            }
        }
        return true;
    }

    private boolean hasPensaoAlimenticiaSemIRFicticioNaoBaseSobreLiquido(DetalheProcessamentoFolha detalheProcessamentoFolha, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador) {
        for (EventoFP eventoFP : detalheProcessamentoFolha.getEventosPensaoAlimenticia()) {
            try {
                if (folhaDePagamentoNovoCalculador.temPensaoAlimenticia(eventoFP.getCodigo()) && !folhaDePagamentoNovoCalculador.temPensaoAlimenticiaEIrrfFicticio(eventoFP.getCodigo())) {
                    TipoValorPensaoAlimenticia tipoValor = folhaDePagamentoNovoCalculador.recuperaTipoValorPensaoAlimenticia(eventoFP.getCodigo());
                    if (tipoValor != null && !TipoValorPensaoAlimenticia.BASE_SOBRE_LIQUIDO.equals(tipoValor)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                logger.error("Erro ao tentar identificar pensão alimentícia", e);
            }
        }
        return false;
    }

    private void processarConsignados(FolhaCalculavel f, DetalheProcessamentoFolha detalheProcessamentoFolha, VinculoFP vinculo, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, Map<LancamentoFP, BigDecimal> lancamentosConsignados, Map<LancamentoFP, BigDecimal> treeMapLancamentos) {
        if (MOSTRAR_LOGS_ECONSIG) {
            logger.debug("treeMap: " + treeMapLancamentos);
            logger.debug("lancamentos calculados: " + lancamentosConsignados);
        }

        BigDecimal valorRestanteMargem35 = BigDecimal.ZERO;
        try {
            double baseMargemConsignavel = folhaDePagamentoNovoCalculador.calculaBase(detalheProcessamentoFolha.getConfiguracaoFP().getBaseMargemConsignavel().getCodigo());
            logger.debug("base 1071 margem consig : " + baseMargemConsignavel);
            Double valorBase1071 = baseMargemConsignavel;

            valorRestanteMargem35 = definirValorDaMargemConsignavel(valorBase1071, TipoDeConsignacao.getPercentualConsigancao().doubleValue() / 100);
            if (MOSTRAR_LOGS_ECONSIG) {
                logger.debug("Valores  35%: " + valorRestanteMargem35);
            }
        } catch (Exception e) {
            folhaDePagamentoNovoCalculador.addItensDetalhesErro(vinculo, "erro ao calcular base 1071, erro: " + e.getMessage());
            logger.error("Erro: ", e);
        }

        List<LancamentoFP> consignados = new LinkedList<>();
        consignados.addAll(lancamentoFPFacade.listaLancamentosEmprestimos(vinculo, f.getAno(), f.getMes().getNumeroMes()));
        consignados.addAll(lancamentoFPFacade.listaLancamentosConvenio(vinculo, f.getAno(), f.getMes().getNumeroMes()));
        consignados.addAll(lancamentoFPFacade.listaLancamentosCartao(vinculo, f.getAno(), f.getMes().getNumeroMes()));
        consignados.addAll(lancamentoFPFacade.buscarLancamentosPorConsignacao(vinculo, f.getAno(), f.getMes().getNumeroMes(), TipoDeConsignacao.CONSIGNADO));

        Collections.sort(consignados, new Comparator<LancamentoFP>() {
            @Override
            public int compare(LancamentoFP o1, LancamentoFP o2) {
                EventoFP eventoFP1 = o1.getEventoFP();
                EventoFP eventoFP2 = o2.getEventoFP();
                return ordernarLancamentosParaDescontoConsignavel(eventoFP1, eventoFP2);
            }
        });

        if (MOSTRAR_LOGS_ECONSIG) {
            logger.debug("consignados: " + consignados);
        }

        double valorTotalADescontarConsignaveis = 0;
        for (Map.Entry<LancamentoFP, BigDecimal> keySet : treeMapLancamentos.entrySet()) {
            valorTotalADescontarConsignaveis = valorTotalADescontarConsignaveis + keySet.getValue().doubleValue();
        }
        treeMapLancamentos = sortByComparator(treeMapLancamentos);
        for (Map.Entry<LancamentoFP, BigDecimal> keySet : treeMapLancamentos.entrySet()) {
            Double valorLancamento = keySet.getValue().doubleValue();
            LancamentoFP lancamentoFP = keySet.getKey();
            valorRestanteMargem35 = valorRestanteMargem35.setScale(2, RoundingMode.HALF_UP);

            for (LancamentoFP lancamentoConsignado : consignados) {
                if (lancamentoFP.equals(lancamentoConsignado)) {
                    if (valorLancamento >= 1 && valorLancamento <= valorRestanteMargem35.doubleValue()) {
                        valorRestanteMargem35 = valorRestanteMargem35.subtract(BigDecimal.valueOf(valorLancamento));
                        if (MOSTRAR_LOGS_ECONSIG) {
                            logger.debug("valor restante: " + valorRestanteMargem35);
                        }
                    } else if (lancamentoFP.getEventoFP().getTipoDeConsignacao() != null && valorLancamento < 1) {
                        logger.debug("Valor de desconto inferior a R$1,00 (um real): " + valorRestanteMargem35 + " valor verba: " + valorLancamento);
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(lancamentoFP.getEventoFP(), BigDecimal.ZERO);
                        defineMotivoRejeicaoPorCodigo(lancamentoFP, detalheProcessamentoFolha.getMotivosRejeicao(), 14);
                        lancamentoFPFacade.salvarSemHistorico(lancamentoFP);
                    } else {
                        logger.debug("limite 30% estourado valor: " + valorRestanteMargem35 + " valor verba: " + valorLancamento);
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(lancamentoFP.getEventoFP(), BigDecimal.ZERO);
                        descobreMotivoRejeicaoPorPercentual(lancamentoFP, detalheProcessamentoFolha.getMotivosRejeicao(), pegarPercentual(valorRestanteMargem35.doubleValue(), valorLancamento), lancamentoConsignado.getEventoFP().getTipoDeConsignacao());
                        lancamentoFPFacade.salvarSemHistorico(lancamentoFP);
                    }
                }
            }
        }
    }

    private static int ordernarLancamentosParaDescontoConsignavel(EventoFP eventoFP1, EventoFP eventoFP2) {
        int retorno = 0;
        if (eventoFP1.getTipoClassificacaoConsignacao() == null) {
            return (eventoFP2.getTipoClassificacaoConsignacao() == null) ? (eventoFP1.getOrdemProcessamento()).compareTo(eventoFP2.getOrdemProcessamento()) : 0;
        }
        if (eventoFP2.getTipoClassificacaoConsignacao() == null) {
            return -1;
        }

        if (eventoFP1.getTipoClassificacaoConsignacao() != null && eventoFP2.getTipoClassificacaoConsignacao() != null) {
            retorno = eventoFP1.getTipoClassificacaoConsignacao().compareTo(eventoFP2.getTipoClassificacaoConsignacao());
        }
        if (eventoFP1.getOrdemProcessamento() != null && eventoFP2.getOrdemProcessamento() != null) {
            if (retorno == 0) {
                retorno = (eventoFP1.getOrdemProcessamento()).compareTo(eventoFP2.getOrdemProcessamento());
            }
        }
        return retorno;
    }

    private void removerVerbasBloqueadasNaoLancadas(List<LancamentoFP> lista, DetalheProcessamentoFolha detalheProcessamentoFolha, TipoFolhaDePagamento tipoFolha) {
        if (TipoFolhaDePagamento.isFolhaComplementar(tipoFolha)) {
            for (LancamentoFP lancamentoFP : lista) {
                if (detalheProcessamentoFolha.getEventosBloqueados().contains(lancamentoFP.getEventoFP())) {
                    detalheProcessamentoFolha.getEventosBloqueados().remove(lancamentoFP.getEventoFP());
                }
            }
            List<EventoFP> encargos = Lists.newArrayList();
            for (EventoFP eventoFP : detalheProcessamentoFolha.getEventosBloqueados()) {
                if (IdentificacaoEventoFP.INSS.equals(eventoFP.getIdentificacaoEventoFP()) ||
                    IdentificacaoEventoFP.IMPOSTO_RENDA.equals(eventoFP.getIdentificacaoEventoFP()) ||
                    IdentificacaoEventoFP.RPPS.equals(eventoFP.getIdentificacaoEventoFP()) ||
                    IdentificacaoEventoFP.FGTS.equals(eventoFP.getIdentificacaoEventoFP())) {
                    encargos.add(eventoFP);
                }
            }
            detalheProcessamentoFolha.getEventosBloqueados().removeAll(encargos);
            detalheProcessamentoFolha.getEventosBloqueados().addAll(detalheProcessamentoFolha.getEventosBloqueadosPorTela());
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    private List<LancamentoFP> criarLancamentosPara13Salario(VinculoFP vinculo, FolhaCalculavel f, List<LancamentoFP> lista) {
        if (TipoFolhaDePagamento.isFolha13Salario(f.getTipoFolhaDePagamento())) {
            List<LancamentoFP> lancamentos = Lists.newArrayList();
            Set<EventoFP> eventoParaCalculoDecimo = Sets.newHashSet(fichaFinanceiraFPFacade.buscarVerbasComLancamentosAnuais(vinculo, f.getAno()));
            for (EventoFP eventoFP : eventoParaCalculoDecimo) {
                LancamentoFP l = new LancamentoFP();
                l.setEventoFP(eventoFP);
                l.setQuantificacao(BigDecimal.ZERO);
                l.setVinculoFP(vinculo);
                l.setMesAnoInicial(DataUtil.primeiroDiaMes(f.getMes().getNumeroMes(), f.getAno()));
                l.setMesAnoFinal(DataUtil.ultimoDiaDoMes(f.getMes()));
                l.setTipoLancamentoFP(TipoLancamentoFP.VALOR);
                l.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
                l.setProporcionalizar(true);
                l.setObservacao("NAO GRAVAR");
                if (!naoContemNoCalculoAtual(eventoFP, lista)) {
                    lancamentos.add(l);
                }
            }
            return lancamentos;
        }
        return Lists.newArrayList();
    }

    private boolean naoContemNoCalculoAtual(EventoFP eventoFP, List<LancamentoFP> lista) {
        for (LancamentoFP lancamentoFP : lista) {
            if (lancamentoFP.getEventoFP().equals(eventoFP)) {
                return true;
            }
        }
        return false;
    }

    private void reloadObjectosPensaoAlimenticia(DetalheProcessamentoFolha detalheProcessamentoFolha, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador) {
        List<EventoFP> eventos = Lists.newArrayList();

        eventos.addAll(detalheProcessamentoFolha.getEventosPensaoAlimenticia());
        for (EventoFP eventoFP : eventos) {
            if (folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(eventoFP)) {
                folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(eventoFP);
            }
            if (folhaDePagamentoNovoCalculador.getEventoValorBase().containsKey(eventoFP)) {
                folhaDePagamentoNovoCalculador.getEventoValorBase().remove(eventoFP);
            }
        }

        Iterator<Map.Entry<String, Object>> iterator = folhaDePagamentoNovoCalculador.getValorMetodo().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entrada = iterator.next();
            if (entrada.getKey().contains("obterValorPensaoAlimenticia")) {
                iterator.remove();
            }
        }

        for (String codigoPensaoAlimenticia : folhaDePagamentoNovoCalculador.getDetalheProcessamentoFolha().getCodigosBasesPensaoAlimenticia()) {
            if (folhaDePagamentoNovoCalculador.getBaseValor().containsKey(codigoPensaoAlimenticia)) {
                folhaDePagamentoNovoCalculador.getBaseValor().remove(codigoPensaoAlimenticia);
            }
        }
    }

    private void verificarEventosFolhaDeAdiantamento13Salario(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, DetalheProcessamentoFolha detalheProcessamentoFolha, FolhaCalculavel f) {
        if (TipoFolhaDePagamento.isFolhaAdiantamento13Salario(f)) {
            for (Iterator<Map.Entry<EventoFP, BigDecimal>> it = folhaDePagamentoNovoCalculador.getEventoValorFormula().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<EventoFP, BigDecimal> entry = it.next();
                if (!TipoEventoFP.INFORMATIVO.equals(entry.getKey().getTipoEventoFP())) {
                    if (!entry.getKey().getCodigo().equals(detalheProcessamentoFolha.getEventoFPAdiantamento13Salario().getCodigo())) {
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(entry.getKey(), BigDecimal.ZERO);
                    }
                }
            }
        }
    }

    private FichaFinanceiraFPSimulacao criarFichaSimulacao(FolhaCalculavel f, VinculoFP vinculo) {
        FichaFinanceiraFPSimulacao fichaFinanceiraFP = new FichaFinanceiraFPSimulacao();
        fichaFinanceiraFP.setFolhaDePagamentoSimulacao((FolhaDePagamentoSimulacao) f);
        fichaFinanceiraFP.setVinculoFP(vinculo);
        fichaFinanceiraFP.setItemFichaFinanceiraFPSimulacoes(new ArrayList<ItemFichaFinanceiraFPSimulacao>());

        return em.merge(fichaFinanceiraFP);

    }

    private void criarItemFichaSimulacao(FichaFinanceiraFPSimulacao ficha, ItemFichaFinanceiraFP item) {
        ItemFichaFinanceiraFPSimulacao simulacao = new ItemFichaFinanceiraFPSimulacao();
        simulacao.setMes(item.getMes());
        simulacao.setAno(item.getAno());
        simulacao.setValor(item.getValor());
        simulacao.setTipoCalculoFP(item.getTipoCalculoFP());
        simulacao.setEventoFP(item.getEventoFP());
        simulacao.setQuantidade(item.getQuantidade());
        simulacao.setTipoEventoFP(item.getTipoEventoFP());
        simulacao.setValorIntegral(item.getValorIntegral());
        simulacao.setValorBaseDeCalculo(item.getValorBaseDeCalculo());
        simulacao.setValorReferencia(item.getValorReferencia());
        simulacao.setFichaFinanceiraFPSimulacao(ficha);
        ficha.getItemFichaFinanceiraFPSimulacoes().add(simulacao);
        em.persist(simulacao);

    }

    private FichaFinanceiraFP criarFichaFinanceira(FolhaCalculavel folhaCalculavel, VinculoFP vinculo, UsuarioSistema usuarioSistema) {
        FichaFinanceiraFP fichaFinanceiraFP = new FichaFinanceiraFP();
        fichaFinanceiraFP.setCreditoSalarioPago(false);
        fichaFinanceiraFP.setFolhaDePagamento((FolhaDePagamento) folhaCalculavel);
        fichaFinanceiraFP.setVinculoFP(vinculo);
        fichaFinanceiraFP.setItemFichaFinanceiraFP(new ArrayList<ItemFichaFinanceiraFP>());
        fichaFinanceiraFP.setAutenticidade(gerarAutenticidade(vinculo));
        fichaFinanceiraFP.setUsuarioSistema(usuarioSistema);
        fichaFinanceiraFP.setDataCriacao(new Date());

        vinculo.setFicha(fichaFinanceiraFP);
        return fichaFinanceiraFP;
    }

    private void definirRecursoFPAndUnidadeAdministrativa(FichaFinanceiraFP fichaFinanceiraFP, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        fichaFinanceiraFP.setUnidadeOrganizacional(definirLotacaoFuncional(fichaFinanceiraFP.getVinculoFP(), detalheProcessamentoFolha));
        fichaFinanceiraFP.setRecursoFP(definirRecursoFP(fichaFinanceiraFP.getVinculoFP(), detalheProcessamentoFolha));

    }

    private void definirRecursoFPAndUnidadeAdministrativaSimulacao(FichaFinanceiraFPSimulacao fichaFinanceiraFP, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        fichaFinanceiraFP.setUnidadeOrganizacional(definirLotacaoFuncional(fichaFinanceiraFP.getVinculoFP(), detalheProcessamentoFolha));
        fichaFinanceiraFP.setRecursoFP(definirRecursoFP(fichaFinanceiraFP.getVinculoFP(), detalheProcessamentoFolha));
    }

    private RecursoFP definirRecursoFP(VinculoFP vinculoFP, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        RecursoFP recursoFP = recursoDoVinculoFPFacade.buscarRecursosFPByVinculoUltimoVigente(vinculoFP);
        if (recursoFP == null) {
            criaObservacaoCalculo(vinculoFP, detalheProcessamentoFolha, "Servidor não possui Recurso FP(Lotação Pagamento) Vigente.");
        }
        return recursoFP;
    }

    private UnidadeOrganizacional definirLotacaoFuncional(VinculoFP vinculoFP, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(vinculoFP);
        if (lotacaoFuncional == null) {
            criaObservacaoCalculo(vinculoFP, detalheProcessamentoFolha, "Servidor não possui Lotação Funcional Vigente.");
        }
        if (lotacaoFuncional != null) {
            return lotacaoFuncional.getUnidadeOrganizacional();
        }
        return null;
    }

    private boolean temPensaoAlimenticia(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, List<EventoFP> eventosPensaoAlimenticia) {
        boolean temPensao = false;
        for (EventoFP eventoFP : eventosPensaoAlimenticia) {
            try {
                temPensao = folhaDePagamentoNovoCalculador.temPensaoAlimenticia(eventoFP.getCodigo());
                if (temPensao) return temPensao;
            } catch (Exception e) {
                logger.error("Erro ao tentar identificar pensão alimentícia", e);
            }
        }
        return temPensao;
    }


    private void forcarReleituraDasFormulas(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador) {
        for (EventoFP eventoFP : folhaDePagamentoNovoCalculador.getEventoValorFormula().keySet()) {
            if (folhaDePagamentoNovoCalculador.avaliaRegra(eventoFP)) {
                folhaDePagamentoNovoCalculador.avaliaFormulaDoEventoFP(eventoFP);
                folhaDePagamentoNovoCalculador.avaliaReferenciaDoEventoFP(eventoFP);
            }
        }
    }

    private boolean isCalculaFolhaComplementarSemFolhaNormal(TipoFolhaDePagamento tipoFolhaDePagamento) {
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigenteDataAtual();
        return configuracaoRH.getFolhaComplementarSemNormal() && TipoFolhaDePagamento.COMPLEMENTAR.equals(tipoFolhaDePagamento);
    }

    private void checarRestricoesVinculo(VinculoFP vinculo, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, DetalheProcessamentoFolha detalhes) throws Exception {
        /*YearMonth mesAnoInicio = YearMonth.fromDateFields(vinculo.getInicioVigencia());
        DateTime mesAnoInicioDate = new DateTime(vinculo.getInicioVigencia());
        YearMonth mesAnoCalculo = folhaDePagamentoNovoCalculador.getMesAnoCalculo();
        if(mesAnoInicio.equals(mesAnoCalculo)){
            if(getDiasEntreDateEFinalMes(mesAnoInicioDate) <= 14){
                throw new DiasTrabalhadosException("O servidor não possui mais de 14 dias trabalhados");
            }
        }*/


        if (!isCalculaFolhaComplementarSemFolhaNormal(folhaDePagamentoNovoCalculador.getFolhaDePagamento().getTipoFolhaDePagamento())) {
            if (jaTemCalculoComFolhaEfetivadaParaMesAndAno(vinculo, detalhes) && !folhaDePagamentoNovoCalculador.getFolhaDePagamento().isSimulacao()) {
                if (TipoFolhaDePagamento.isFolhaRescisao(folhaDePagamentoNovoCalculador.getFolhaDePagamento())) {
                    criaObservacaoCalculo(vinculo, detalhes, "O servidor " + vinculo + " já tem um calculo em uma folha efetivada, porém será processado pois está numa folha rescisória.");
                } else {
                    throw new CalculoBloqueadoException("O servidor " + vinculo + " já tem um calculo em uma folha efetivada.");
                }
            }
        }
        if (TipoFolhaDePagamento.isFolhaRescisao(folhaDePagamentoNovoCalculador.getFolhaDePagamento()) && propostaConcessaoDiariaFacade.hasDiariasAComprovarPorPessoa(vinculo.getMatriculaFP().getPessoa())) {
            throw new CalculoBloqueadoException("O servidor " + vinculo + " possui Diária a Comprovar.");
        }
    }

    public boolean jaTemCalculoComFolhaEfetivadaParaMesAndAno(VinculoFP vinculoFP, DetalheProcessamentoFolha detalhes) {
        return detalhes.getServidoresComFolhaEfetivada().containsKey(vinculoFP.getId());
    }

    public int getDiasEntreDateEFinalMes(DateTime inicioVigencia) {
        return DataUtil.diasEntre(inicioVigencia, inicioVigencia.withDayOfMonth(inicioVigencia.monthOfYear().getMaximumValue()));
    }

    public int getDiasEntreDateEInicioMes(DateTime finalVigencia) {
        return DataUtil.diasEntre(finalVigencia.withDayOfMonth(finalVigencia.monthOfYear().getMinimumValue()), finalVigencia);
    }

    private BigDecimal processarLancamentoFolhaComplementar(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, LancamentoFP lancamento, VinculoFP vinculo) {
        if (TipoFolhaDePagamento.isFolhaComplementar(vinculo.getFolha().getTipoFolhaDePagamento())) {
            EventoTotalItemFicha item = fichaFinanceiraFPFacade.totalEventosItemFichaFinanceiraByEvento(vinculo, vinculo.getMes(), vinculo.getAno(), lancamento.getEventoFP().getCodigo());
            if (item != null && item.getTotal() != null) {
                BigDecimal valorCalculado = folhaDePagamentoNovoCalculador.getEventoValorFormula().get(lancamento.getEventoFP());
                return valorCalculado;
            }
        }
        return BigDecimal.ZERO;
    }

    private void calcularDiferencaEntreCalculoAtualEFolhaNoBanco(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, VinculoFP vinculo, List<ItemFichaFinanceiraFP> itensFolhaNormal, Map<LancamentoFP, BigDecimal> lancamentoCalculados) {
        List<ItemFichaFinanceiraFP> itensCalculando = convertCalculadorEmItensFicha(folhaDePagamentoNovoCalculador, vinculo);
        logger.debug("itens convertidos: " + itensCalculando);
        for (Iterator iteratorCalculado = itensFolhaNormal.iterator(); iteratorCalculado.hasNext(); ) {
            ItemFichaFinanceiraFP itemBanco = (ItemFichaFinanceiraFP) iteratorCalculado.next();
            for (Iterator iteratorCalculando = itensCalculando.iterator(); iteratorCalculando.hasNext(); ) {
                ItemFichaFinanceiraFP itemMemoria = (ItemFichaFinanceiraFP) iteratorCalculando.next();
                if (itemBanco.getEventoFP().equals(itemMemoria.getEventoFP())) {
                    verificarDiferencaEntreFichasEGravar(folhaDePagamentoNovoCalculador, itemBanco, itemMemoria, lancamentoCalculados);
                }
            }
        }
    }

    private void verificarDiferencaEntreFichasEGravar(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, ItemFichaFinanceiraFP itemBanco, ItemFichaFinanceiraFP itemMemoria, Map<LancamentoFP, BigDecimal> lancamentoCalculados) {
        if (TipoEventoFP.DESCONTO.equals(itemBanco.getTipoEventoFP()) || TipoEventoFP.INFORMATIVO.equals(itemBanco.getTipoEventoFP())) {
            return;
        }
        BigDecimal valorBanco = itemBanco.getValor();
        BigDecimal valorMemoria = itemMemoria.getValor();
        BigDecimal resultado = valorBanco;
        logger.debug("Resultado: " + resultado);
        if (resultado.compareTo(BigDecimal.ZERO) != 0) {
//            if (!contemLancamentoParaOEvento(lancamentoCalculados, itemMemoria)) {
            TipoEventoFP tipoEventoFP = TipoEventoFP.getTipoEventoFPPorValor(itemMemoria.getEventoFP().getTipoEventoFP(), resultado);
            folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(itemMemoria.getEventoFP());
            folhaDePagamentoNovoCalculador.getEventoValorFormula().put(itemMemoria.getEventoFP(), resultado.abs());
            if (folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().containsKey(itemMemoria.getEventoFP())) {
                folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().remove(itemMemoria.getEventoFP());
                folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().put(itemMemoria.getEventoFP(), tipoEventoFP);
            } else {
                folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().put(itemMemoria.getEventoFP(), tipoEventoFP);
            }
//            }
        } else {
            LancamentoFP l = null;
            for (Map.Entry<LancamentoFP, BigDecimal> lancamentosCalculado : lancamentoCalculados.entrySet()) {
                if (lancamentosCalculado.getKey().getEventoFP().equals(itemMemoria.getEventoFP())) {
                    l = lancamentosCalculado.getKey();
                }
            }
            if (l != null && lancamentoCalculados.containsKey(l)) {
                lancamentoCalculados.remove(l);
            }
        }
    }

    public boolean contemLancamentoParaOEvento(Map<LancamentoFP, BigDecimal> lancamentoCalculados, ItemFichaFinanceiraFP itemMemoria) {
        for (Map.Entry<LancamentoFP, BigDecimal> lancamentosCalculado : lancamentoCalculados.entrySet()) {
            if (lancamentosCalculado.getKey().getEventoFP().equals(itemMemoria.getEventoFP())) {
                return true;
            }
        }
        return false;
    }

    private boolean podeGerarItemFicha(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, EventoFP e) {
        return folhaDePagamentoNovoCalculador.getEventoValorFormula().get(e).compareTo(BigDecimal.ZERO) != 0 && !folhaDePagamentoNovoCalculador.getEventoEstornoValor().containsKey(e);
    }

    private List<ItemFichaFinanceiraFP> convertCalculadorEmItensFicha(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, VinculoFP vinculo) {
        List<ItemFichaFinanceiraFP> itensCalculando = new ArrayList<>();
        for (EventoFP eventoFP : folhaDePagamentoNovoCalculador.getEventoValorFormula().keySet()) {
            if (podeGerarItemFicha(folhaDePagamentoNovoCalculador, eventoFP)) {
                ItemFichaFinanceiraFP item = new ItemFichaFinanceiraFP();
                item.setMes(vinculo.getMes());
                item.setAno(vinculo.getAno());
                item.setEventoFP(eventoFP);
                item.setTipoEventoFP(folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().containsKey(eventoFP) ? folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().get(eventoFP) : eventoFP.getTipoEventoFP());
                item.setFichaFinanceiraFP(null);
                item.setTipoCalculoFP(TipoCalculoFP.NORMAL);
                item.setUnidadeReferencia(eventoFP.getUnidadeReferencia());
                item.setValor(new BigDecimal(getValorNormalizado(folhaDePagamentoNovoCalculador.avaliaFormulaDoEventoFP(eventoFP)) + ""));
                item.setValorBaseDeCalculo(getValorNormalizado(folhaDePagamentoNovoCalculador.avaliaValorBaseDoEventoFP(eventoFP)));
                item.setValorIntegral(folhaDePagamentoNovoCalculador.avaliaValorIntegralDoEventoFP(eventoFP));
                item.setValorReferencia(folhaDePagamentoNovoCalculador.avaliaReferenciaDoEventoFP(eventoFP));
                item.setSequencial(item.getSequencial().concat(item.getFichaFinanceiraFP().getVinculoFP().getId().toString()).
                    concat(item.getFichaFinanceiraFP().getFolhaDePagamento().getTipoFolhaDePagamento().name()).
                    concat(item.getFichaFinanceiraFP().getFolhaDePagamento().getVersao().toString()));

                itensCalculando.add(item);
            }
        }
        return itensCalculando;
    }

    private void calcularEventosAutomaticos(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, VinculoFP vinculo, List<EventoFP> eventosAutomaticos) {
        if (eventosAutomaticos == null) {
            eventosAutomaticos = eventoFPFacade.listaEventosAtivosPorTipoExecucao(TipoExecucaoEP.FOLHA, true);
        }
        /*if(isCalculo13Salario(folhaDePagamentoNovoCalculador.getFolhaDePagamento())){
            eventosAutomaticos.addAll(eventoFPFacade.listaEventosAtivosPara13PorTipoExecucao(TipoExecucaoEP.FOLHA));
        }*/
        for (EventoFP eventoFP : eventosAutomaticos) {
            if (folhaDePagamentoNovoCalculador.isServidorEVerbaBloqueado(vinculo, eventoFP)) {
                continue;
            }

            folhaDePagamentoNovoCalculador.setEventoCalculandoAgora(eventoFP);
            if (folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(eventoFP)) { //já calculou?
                continue;
            }
            if (folhaDePagamentoNovoCalculador.avaliaRegra(eventoFP) || isCalculo13Salario(folhaDePagamentoNovoCalculador, eventoFP)) {
                folhaDePagamentoNovoCalculador.avaliaFormulaDoEventoFP(eventoFP);
                folhaDePagamentoNovoCalculador.avaliaValorIntegralDoEventoFP(eventoFP);
                folhaDePagamentoNovoCalculador.avaliaValorBaseDoEventoFP(eventoFP);
            }
        }
    }

    private boolean isCalculo13Salario(FolhaCalculavel folhaDePagamento) {
        return TipoFolhaDePagamento.isFolha13Salario(folhaDePagamento);
    }

    private boolean isCalculo13Salario(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, EventoFP eventoFP) {
        if (TipoCalculo13.LER_FORMULA.equals(eventoFP.getTipoCalculo13()) && TipoFolhaDePagamento.isFolha13Salario(folhaDePagamentoNovoCalculador.getFolhaDePagamento())) {
            return folhaDePagamentoNovoCalculador.avaliaRegra(eventoFP);
        }
        return TipoFolhaDePagamento.isFolha13Salario(folhaDePagamentoNovoCalculador.getFolhaDePagamento());
    }

    private double pegarPercentual(Double margem, Double valorLancamento) {
        BigDecimal v = BigDecimal.valueOf(margem - valorLancamento);
        return ((v.abs().doubleValue() * 100) / margem);
    }

    private void descobreMotivoRejeicaoPorPercentual(LancamentoFP
                                                         lancamentoFP, Map<Integer, MotivoRejeicao> motivosRejeicao, double percentual, TipoDeConsignacao tipo) {
        if (percentual >= 0 && percentual < 10) {
            defineMotivoRejeicaoPorCodigo(lancamentoFP, motivosRejeicao, 10);
            return;
        }
        if (percentual >= 10 && percentual < 20) {
            defineMotivoRejeicaoPorCodigo(lancamentoFP, motivosRejeicao, 9);
            return;
        }
        if (percentual >= 20 && percentual < 30) {
            defineMotivoRejeicaoPorCodigo(lancamentoFP, motivosRejeicao, 8);
            return;
        }
        if (percentual >= 30 && percentual < 70) {
            defineMotivoRejeicaoPorCodigo(lancamentoFP, motivosRejeicao, 12);
            return;
        }
        if (percentual >= 70) {
            defineMotivoRejeicaoPorCodigo(lancamentoFP, motivosRejeicao, 13);
            return;
        }
        Integer codigo = getCodigoMotivoRejeicaoPorTipoDeConsignacao(tipo);
        defineMotivoRejeicaoPorCodigo(lancamentoFP, motivosRejeicao, codigo);
    }

    private Integer getCodigoMotivoRejeicaoPorTipoDeConsignacao(TipoDeConsignacao tipo) {
        if (tipo.equals(TipoDeConsignacao.CARTAO)) return 10;
        if (tipo.equals(TipoDeConsignacao.CONVENIO)) return 9;
        if (tipo.equals(TipoDeConsignacao.EMPRESTIMO)) return 8;
        return 11;
    }

    private void defineMotivoRejeicaoPorCodigo(LancamentoFP
                                                   lancamentoFP, Map<Integer, MotivoRejeicao> motivosRejeicao, int codigo) {
        lancamentoFP.setMotivoRejeicao(motivosRejeicao.get(codigo));
    }

//    public boolean temEventosPagosEmOutraFolhaNoMesmoMes(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, VinculoFP vinculo, DetalheProcessamentoFolha detalheProcessamentoFolha) {
//        return TipoFolhaDePagamento.isFolhaRescisao(detalheProcessamentoFolha.getDetalhesCalculoRH().getFolhaDePagamento()) && !eventosPagosEmOutraFolhaNoMesmoMes(folhaDePagamentoNovoCalculador, vinculo, detalheProcessamentoFolha).isEmpty();
//    }
//
//
//    public List<EventoTotalItemFicha> eventosPagosEmOutraFolhaNoMesmoMes(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, VinculoFP vinculo, DetalheProcessamentoFolha detalheProcessamentoFolha) {
//        List<EventoTotalItemFicha> eventos = fichaFinanceiraFPFacade.buscarTotalEventosItemFichaFinanceira(vinculo, detalheProcessamentoFolha.getAno(), detalheProcessamentoFolha.getMes());
//        return eventos;
//    }

    public void removerEventosJaPagosNaFolhaNormalParaRescisao(FolhaDePagamentoNovoCalculador
                                                                   folhaDePagamentoNovoCalculador, List<EventoTotalItemFicha> itemPagosNaFolhaNormal) {
        for (EventoTotalItemFicha eventoTotalItemFicha : itemPagosNaFolhaNormal) {
            if (folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(eventoTotalItemFicha.getEvento())) {
                if (folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoTotalItemFicha.getEvento()).setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(eventoTotalItemFicha.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP)) == 0
                    || folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoTotalItemFicha.getEvento()).setScale(2, BigDecimal.ROUND_DOWN).compareTo(eventoTotalItemFicha.getTotal().setScale(2, BigDecimal.ROUND_DOWN)) == 0) {
                    folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoTotalItemFicha.getEvento(), BigDecimal.ZERO);
                }
            }
        }

    }

    public void removerEventoJaPagoNaFolhaNormalParaRescisaoSemAlterarCache(List<EventoTotalItemFicha> itemPagosNaFolhaNormal, Map<EventoFP, BigDecimal> mapValor) {
        for (EventoTotalItemFicha eventoTotalItemFicha : itemPagosNaFolhaNormal) {
            if (mapValor.containsKey(eventoTotalItemFicha.getEvento())) {
                if (mapValor.get(eventoTotalItemFicha.getEvento()).setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(eventoTotalItemFicha.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP)) == 0
                    || mapValor.get(eventoTotalItemFicha.getEvento()).setScale(2, BigDecimal.ROUND_DOWN).compareTo(eventoTotalItemFicha.getTotal().setScale(2, BigDecimal.ROUND_DOWN)) == 0) {
                    mapValor.put(eventoTotalItemFicha.getEvento(), BigDecimal.ZERO);
                }
            }
        }
    }

    private void recuperarEColocarNaFolhaDeRecisaoEventosDaFolhaNormal(FolhaDePagamentoNovoCalculador
                                                                           folhaDePagamentoNovoCalculador, VinculoFP vinculo, DetalheProcessamentoFolha
                                                                           detalheProcessamentoFolha, List<EventoTotalItemFicha> itens) {
        List<EventoTotalItemFicha> eventos = fichaFinanceiraFPFacade.buscarTotalEventosItemFichaFinanceira(vinculo, detalheProcessamentoFolha.getAno(), detalheProcessamentoFolha.getMes());
        if (!eventos.isEmpty()) {
            logger.debug("adicionado eventos da folha normal à folha recisão...");
            for (EventoTotalItemFicha evento : eventos) {
                if (evento.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM) || evento.getTipoEventoFP().equals(TipoEventoFP.DESCONTO)) {
                    if (!folhaDePagamentoNovoCalculador.isServidorEVerbaBloqueado(vinculo, evento.getEvento())) {
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(evento.getEvento(), evento.getTotal());
                        folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(evento.getEvento(), evento.getReferencia());
                        folhaDePagamentoNovoCalculador.getEventoValorIntegral().put(evento.getEvento(), evento.getValorIntegral());
                        folhaDePagamentoNovoCalculador.getEventoValorBase().put(evento.getEvento(), evento.getValorBase());
                        itens.add(evento);
                    }
                }
            }

        }
    }

    private BigDecimal getValorNormalizado(BigDecimal valor) {
        try {
            return truncate(valor + "");
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorArredondado(BigDecimal valor) {
        BigDecimal valor3Casas = new BigDecimal(valor + "").setScale(3, RoundingMode.HALF_DOWN);
        return new BigDecimal(valor3Casas.setScale(2, RoundingMode.DOWN) + "");
    }

    public BigDecimal truncate(String s) {
        int l = s.length();
        StringBuffer sb = new StringBuffer();
        if (l != 0 && l > 3) {
            for (int i = 0; i < l; i++) {
                if (s.charAt(i) == '.' && (l - i - 1) > 2) {
                    sb.append(s.charAt(i));
                    sb.append(s.charAt(++i));
                    sb.append(s.charAt(++i));
                    break;
                } else {
                    sb.append(s.charAt(i));
                }
            }
            String s1 = sb.toString();
            double d1 = Double.parseDouble(s1);
            return BigDecimal.valueOf(d1);
        } else {
            return new BigDecimal(s);
        }
    }

    public boolean paraFolhaDeRescisaoVerificaSeFoiPagoNaFolha(VinculoFP vinculo, EventoFP
        evento, DetalheProcessamentoFolha detalhes) {
        if (!detalhes.getDetalhesCalculoRH().getFolhaDePagamento().getTipoFolhaDePagamento().equals(TipoFolhaDePagamento.RESCISAO)) {
            return false;
        }
        ItemFichaFinanceiraFP item = fichaFinanceiraFPFacade.recuperaValorItemFichaFinanceiraFPPorEventoETiposDeFolha(vinculo.getId(), detalhes.getMes(), detalhes.getAno(), evento, TipoFolhaDePagamento.getTiposFolhaDePagamentoParaRescisao());
        if (item == null) {
            return false;
        } else {
            return true;
        }
    }

    private void calculaBaseSobreParcentualDeLancamentofp(FolhaDePagamentoNovoCalculador
                                                              folhaDePagamentoNovoCalculador, LancamentoFP lancamento) {
        try {
            String codigoBase = lancamento.getBaseFP().getCodigo();
            Double valor = folhaDePagamentoNovoCalculador.calculaBase(codigoBase);
            BigDecimal valorBase = BigDecimal.ZERO;
            if (valor != null && valor.compareTo(0.0) != 0) {
                valorBase = BigDecimal.valueOf(valor);
                BigDecimal resultado = valorBase.multiply(lancamento.getQuantificacao()).divide(new BigDecimal("100"));

                Boolean naoPagaEventoEmFerias = folhaDePagamentoNovoCalculador.isNaoPagaEventoEmFerias(lancamento.getEventoFP());
                Boolean naoPagaEventoEmLicencaPremio = folhaDePagamentoNovoCalculador.isNaoPagaEventoEmLicencaPremio(lancamento.getEventoFP());
                if (lancamento.getProporcionalizar() && (naoPagaEventoEmFerias || naoPagaEventoEmLicencaPremio)) {
                    double diasFeriasELicenca = 0d;
                    LocalDate dataReferencia = funcoesFolhaFacade.getDataReferenciaDateTime(folhaDePagamentoNovoCalculador.getEp());
                    LocalDate dataInicial = dataReferencia.withDayOfMonth(1);
                    LocalDate dataFinal = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());

                    if (dataInicial.isBefore(DataUtil.dateToLocalDate(lancamento.getInicioVigencia()))) {
                        dataInicial = DataUtil.dateToLocalDate(lancamento.getInicioVigencia());
                    }
                    if (dataFinal.isAfter(DataUtil.dateToLocalDate(lancamento.getFinalVigencia()))) {
                        dataFinal = DataUtil.dateToLocalDate(lancamento.getFinalVigencia());
                    }

                    if (naoPagaEventoEmFerias) {
                        diasFeriasELicenca += funcoesFolhaFacade.quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(folhaDePagamentoNovoCalculador.getEp(), TipoPeriodoAquisitivo.FERIAS, dataInicial, dataFinal);
                    }
                    if (naoPagaEventoEmLicencaPremio) {
                        diasFeriasELicenca += funcoesFolhaFacade.quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(folhaDePagamentoNovoCalculador.getEp(), TipoPeriodoAquisitivo.LICENCA, dataInicial, dataFinal);
                    }

                    Double diasTrabalhadosPeloLancamento = funcoesFolhaFacade.diasTrabalhados(folhaDePagamentoNovoCalculador.getEp(), folhaDePagamentoNovoCalculador.getMes(), folhaDePagamentoNovoCalculador.getAno(), TipoDiasTrabalhados.NORMAL, null, dataInicial, dataFinal);
                    double dias = diasTrabalhadosPeloLancamento - diasFeriasELicenca;
                    BigDecimal resultadoProporcionalizado = BigDecimal.ZERO;
                    if (dias > 0) {
                        resultadoProporcionalizado = new BigDecimal(resultado.doubleValue() / folhaDePagamentoNovoCalculador.obterDiasDoMes().intValue() * dias);
                    }
                    folhaDePagamentoNovoCalculador.getEventoValorFormula().put(lancamento.getEventoFP(), resultadoProporcionalizado);
                } else {
                    folhaDePagamentoNovoCalculador.getEventoValorFormula().put(lancamento.getEventoFP(), resultado);
                }
                folhaDePagamentoNovoCalculador.getEventoValorBase().put(lancamento.getEventoFP(), valorBase);
                folhaDePagamentoNovoCalculador.getEventoValorIntegral().put(lancamento.getEventoFP(), resultado);
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void processarMargensConsignaveis(FolhaDePagamentoNovoCalculador
                                                  folhaDePagamentoNovoCalculador, Map<LancamentoFP, BigDecimal> treeMapLancamentos, List<LancamentoFP> convenios, List<LancamentoFP> emprestimos, List<LancamentoFP> cartao, BigDecimal
                                                  valorMargem5, BigDecimal valorMargem10, BigDecimal valorMargem20, BigDecimal valorMargem30) {

    }

    private void processaMotivoRejeicaoParaLancamento(LancamentoFP lancamento) {
        if (lancamento.getMotivoRejeicao() != null) {
            lancamento.setMotivoRejeicao(null);
            lancamentoFPFacade.salvarSemHistorico(lancamento);
        }
    }

    public void realizarEstornoEPropocionalizacaoDaVerbaSemAlterarCache(VinculoFP vinculo, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador,
                                                                        EventoFP eventoFP, Map<EventoFP, BigDecimal> mapValor,
                                                                        Map<EventoFP, TipoEventoFP> mapTipoEventoCalculado) {
        List<ItemFichaFinanceiraFP> itensCalculadosNaFolhaNormal = fichaFinanceiraFPFacade.totalItemFichaFinanceiraFPPorEvento(vinculo, vinculo.getMes(), vinculo.getAno(), TipoFolhaDePagamento.NORMAL, eventoFP);
        for (ItemFichaFinanceiraFP item : itensCalculadosNaFolhaNormal) {
            if (item.getEventoFP().getId().equals(eventoFP.getId())) {
                if (item.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM) || item.getEventoFP().getTipoDeConsignacao() != null) {
                    calcularNovoValorItemFichaFinanceiraDoEstornoSemAlterarCache(item, folhaDePagamentoNovoCalculador, mapValor, mapTipoEventoCalculado);
                }
            }
        }
    }

    private void calcularNovoValorItemFichaFinanceiraDoEstornoSemAlterarCache(ItemFichaFinanceiraFP item, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador,
                                                                              Map<EventoFP, BigDecimal> mapValor, Map<EventoFP, TipoEventoFP> mapTipoEventoCalculado) {
        int diasTrabalhados = folhaDePagamentoNovoCalculador.diasDeDevolucao();
        Double diasDoMes = folhaDePagamentoNovoCalculador.obterDiasDoMes();
        if (diasTrabalhados == 0) {
            return;
        }

        EventoFP evento = item.getEventoFP();
        if (item.getEventoFP().getTipoDeConsignacao() != null && mapValor.containsKey(item.getEventoFP())) {
            mapValor.remove(item.getEventoFP());
            return;
        }
        BigDecimal proporcionalizado = proporcionalizarDiasTrabalhados(item.getValor(), diasTrabalhados, diasDoMes.intValue());
        if (mapValor.containsKey(item.getEventoFP())) {
            mapValor.put(evento, proporcionalizado);
            mapTipoEventoCalculado.put(evento, TipoEventoFP.DESCONTO);
        }
    }

    private void realizarEstornoEPropocionalizacaoDasVerbas(VinculoFP vinculo, FolhaDePagamentoNovoCalculador
        folhaDePagamentoNovoCalculador) {
        List<ItemFichaFinanceiraFP> itensCalculadosNaFolhaNormal = fichaFinanceiraFPFacade.totalItemFichaFinanceiraFP(vinculo, vinculo.getMes(), vinculo.getAno(), TipoFolhaDePagamento.NORMAL, true);
        for (ItemFichaFinanceiraFP item : itensCalculadosNaFolhaNormal) {
            if (item.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM) || item.getEventoFP().getTipoDeConsignacao() != null) {
                calcularNovoValorItemFichaFinanceiraDoEstorno(item, folhaDePagamentoNovoCalculador);
            }
        }
    }

    private void calcularNovoValorItemFichaFinanceiraDoEstorno(ItemFichaFinanceiraFP
                                                                   item, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador) {
        int diasTrabalhados = folhaDePagamentoNovoCalculador.diasDeDevolucao();
        Double diasDoMes = folhaDePagamentoNovoCalculador.obterDiasDoMes();
        if (diasTrabalhados == 0) {
            return;
        }

        EventoFP evento = item.getEventoFP();
        if (item.getEventoFP().getTipoDeConsignacao() != null && folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(item.getEventoFP())) {
            folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(item.getEventoFP());
            return;
        }
        BigDecimal proporcionalizado = proporcionalizarDiasTrabalhados(item.getValor(), diasTrabalhados, diasDoMes.intValue());
        if (folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(item.getEventoFP())) {
            folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(item.getEventoFP());
            folhaDePagamentoNovoCalculador.getEventoValorFormula().put(evento, proporcionalizado);
            folhaDePagamentoNovoCalculador.getTipoEventoTipoEventoFP().put(item.getEventoFP(), TipoEventoFP.DESCONTO);

            if (item.getEventoFP().getReferencia().contains("diasTrabalhados")) {
                folhaDePagamentoNovoCalculador.getEventoValorReferencia().remove(item.getEventoFP());
                folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(evento, BigDecimal.valueOf(diasTrabalhados));
            }
        }
        logger.debug(":::" + evento);

//        ItemFichaFinanceiraFP novoItem = new ItemFichaFinanceiraFP();
//        novoItem.setAno(vinculo.getAno());
//        novoItem.setMes(vinculo.getMes());
//        novoItem.setFichaFinanceiraFP(novaFicha);
//        novoItem.setTipoCalculoFP(TipoCalculoFP.NORMAL);
//        novoItem.setTipoEventoFP(TipoEventoFP.DESCONTO);
//        novoItem.setValorBaseDeCalculo(item.getValorBaseDeCalculo());
//        if (item.getEventoFP().getReferencia().contains("diasTrabalhados")) {
//            novoItem.setValorReferencia(new BigDecimal(diasTrabalhados));
//        } else {
//            novoItem.setValorReferencia(item.getValorReferencia());
//        }
//        novoItem.setValor(valorEstornoFolhaRescicao(diasTrabalhados, diasDoMes, item.getEventoFP(), item.getValor(), item.getValorReferencia(), item.getValorBaseDeCalculo()));
//        novoItem.setEventoFP(item.getEventoFP());
//        novoItem.setUnidadeReferencia(item.getEventoFP().getUnidadeReferencia());
//        novaFicha.getItemFichaFinanceiraFP().add(novoItem);
////        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(item.getEventoFP(), new BigDecimal("0"));
//        folhaDePagamentoNovoCalculador.getEventoEstornoValor().put(item.getEventoFP(), TipoEventoFP.VANTAGEM);
//        fichaFinanceiraFPFacade.salvarNovoItemFichaFinanceira(novoItem);
//        em.persist(novaFicha);
    }

    private BigDecimal valorEstornoFolhaRescicao(double diasTrabalhados, double diasDoMes, EventoFP
        eventoFP, BigDecimal valor, BigDecimal referencia, BigDecimal base) {
        if (eventoFP.getTipoLancamentoFP() != null && eventoFP.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
            Double valorEstorno = (valor.doubleValue() / diasDoMes) * diasTrabalhados;
            return new BigDecimal(valorEstorno);
        } else {  //TODO se caso a referencia for em horas é possível que calcule errado... anotado para observações.
            double referenciaPropocionalizada = ((referencia.doubleValue() / diasDoMes) * diasTrabalhados);
            Double valorEstorno = base.doubleValue() * referenciaPropocionalizada / 100;
            return new BigDecimal(valorEstorno);
        }
    }

    private void defineParametros(FolhaCalculavel f, DetalheProcessamentoFolha detalheProcessamentoFolha, VinculoFP
        vinculo) {
        vinculo.setAno(f.getAno());
        vinculo.setMes(f.getMes().getNumeroMes());
        detalheProcessamentoFolha.setAno(f.getAno());
        detalheProcessamentoFolha.setMes(f.getMes().getNumeroMes());
        vinculo.setFolha(f);
    }

    private void verificarServidorComLiquidoNegativo(EntidadePagavelRH vinculo, FolhaDePagamentoNovoCalculador
        folhaDePagamentoNovoCalculador, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        EventoFP eventoAdtFogap = eventoFPFacade.recuperaEvento(216 + "", TipoExecucaoEP.FOLHA); //TODO fixo evento adt fopag
        EventoFP eventoDevFogap = eventoFPFacade.recuperaEvento(623 + "", TipoExecucaoEP.FOLHA);//TODO fixo evento dev fopag
        if (!folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(eventoDevFogap)) {
            ItemFichaFinanceiraFP itemFichaFinanceiraFPAdtFopag = fichaFinanceiraFPFacade.recuperaValorItemFichaFinanceiraFPPorEvento(vinculo.getIdCalculo(), detalheProcessamentoFolha.getMes(), detalheProcessamentoFolha.getAno(), vinculo.getFolha().getTipoFolhaDePagamento(), eventoAdtFogap);
//        ItemFichaFinanceiraFP itemFichaFinanceiraFPDevFopag = fichaFinanceiraFPFacade.recuperaValorItemFichaFinanceiraFPPorEvento(vinculo.getIdCalculo(), detalheProcessamentoFolha.getMes()-2, detalheProcessamentoFolha.getAno(), vinculo.getFolha().getTipoFolhaDePagamento(), eventoDevFogap);
            if (itemFichaFinanceiraFPAdtFopag.getValor().compareTo(BigDecimal.ZERO) > 0) {
                //existe valor acumuladado;
                folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoDevFogap, itemFichaFinanceiraFPAdtFopag.getValor());
            }
        }
        BigDecimal valorVantagem = BigDecimal.ZERO;
        BigDecimal valorDesconto = BigDecimal.ZERO;
        for (Map.Entry<EventoFP, BigDecimal> eventosCalculados : folhaDePagamentoNovoCalculador.getEventoValorFormula().entrySet()) {
            EventoFP key = eventosCalculados.getKey();
            BigDecimal valor = eventosCalculados.getValue();
            if (key.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM)) {
                valorVantagem = valorVantagem.add(valor);
            } else if (key.getTipoEventoFP().equals(TipoEventoFP.DESCONTO)) {
                valorDesconto = valorDesconto.add(valor);
            }
        }
        //recuperar os valores retroativos!!!!!
        valorDesconto = valorDesconto.add(fichaFinanceiraFPFacade.buscarValorTotalPorTipoEventoAndTipoCalculo(vinculo.getIdCalculo(), detalheProcessamentoFolha.getMes(), detalheProcessamentoFolha.getAno(), vinculo.getFolha().getTipoFolhaDePagamento(), TipoEventoFP.DESCONTO, TipoCalculoFP.RETROATIVO));
        valorVantagem = valorVantagem.add(fichaFinanceiraFPFacade.buscarValorTotalPorTipoEventoAndTipoCalculo(vinculo.getIdCalculo(), detalheProcessamentoFolha.getMes(), detalheProcessamentoFolha.getAno(), vinculo.getFolha().getTipoFolhaDePagamento(), TipoEventoFP.VANTAGEM, TipoCalculoFP.RETROATIVO));
        BigDecimal liquido = valorVantagem.subtract(valorDesconto);
        if (liquido.compareTo(BigDecimal.ZERO) < 0) {
            logger.debug("valor negativo para " + vinculo + " - valor: " + liquido);

            //TODO valor negativo, recuperar verba ad fopag e garantir o previdencia do servidor;
            liquido = liquido.add(getValoresEmCache(folhaDePagamentoNovoCalculador, eventoAdtFogap));
            liquido = liquido.setScale(2, RoundingMode.HALF_UP);
            folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoAdtFogap, liquido.abs());
        }
    }

    private BigDecimal getValoresEmCache(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, EventoFP
        eventoAdtFogap) {
        if (folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(eventoAdtFogap)) {
            return folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoAdtFogap).negate();
        }
        return BigDecimal.ZERO;
    }

    private void verificarMargemConsignavel(FolhaDePagamentoNovoCalculador
                                                folhaDePagamentoNovoCalculador, List<LancamentoFP> lancamentoFPList, LancamentoFP lancamentoFP, BigDecimal
                                                valorMargem, Double valorVerba, DetalheProcessamentoFolha detalheProcessamentoFolha, Integer codigoRejeito) {
        for (LancamentoFP lancamento : lancamentoFPList) {
            if (lancamento.equals(lancamentoFP)) {
                if (valorVerba < valorMargem.doubleValue()) {
                    valorMargem = valorMargem.subtract(new BigDecimal(valorVerba));
                    if (MOSTRAR_LOGS_ECONSIG) {
                        logger.debug("valor restante: " + valorMargem);
                    }
                } else {
                    logger.debug("limite de margem estourado valor: " + valorMargem + " valor verba: " + valorVerba);
                    folhaDePagamentoNovoCalculador.getEventoValorFormula().put(lancamentoFP.getEventoFP(), BigDecimal.ZERO);
                    lancamentoFP.setMotivoRejeicao(detalheProcessamentoFolha.getMotivosRejeicao().get(codigoRejeito));
                    lancamentoFPFacade.salvarSemHistorico(lancamentoFP);
                }
            }
        }
    }

    private void preCarregaDadosMotivosRejeicao(DetalheProcessamentoFolha detalheProcessamentoFolha) {
        for (MotivoRejeicao m : motivoRejeicaoFacade.lista()) {
            detalheProcessamentoFolha.getMotivosRejeicao().put(m.getCodigo(), m);
        }
    }

    private List<VinculoFP> verificarEPegarMultiplosVinculos(List<VinculoFP> vinculos, Date data, Map<VinculoFP, Long> mapFontePagadora) {
        logger.debug("iniciando a busca por multiplos vinculos...");
        List<VinculoFP> vinculoMultiplosVinculos = new ArrayList<>();
        for (VinculoFP vf : vinculos) {
            if (getOutroVinculo(vf, Sets.newHashSet(vinculos), mapFontePagadora) != null) {
                logger.debug("multiplo vinculo encontrado: " + vf);
                vinculoMultiplosVinculos.add(vf);
            }
        }
        logger.debug("finalizado a busca por multiplos vinculos. Total encontrado com multiplos vinculo: " + vinculoMultiplosVinculos.size());
        return vinculoMultiplosVinculos;  //To change body of created methods use File | Settings | File Templates.
    }

    private void putLancamentosCalculados
        (Map<LancamentoFP, BigDecimal> lancamentosCalculados, Map<LancamentoFP, BigDecimal> lancamentos, LancamentoFP
            lancamento, BigDecimal valor) {
        lancamentos.put(lancamento, valor);
        if (lancamento.getEventoFP().getTipoDeConsignacao() != null) {
            lancamentosCalculados.put(lancamento, valor);
        }
    }

    private void criaObservacaoCalculo(VinculoFP vinculoFP, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        ItensDetalhesErrosCalculo it = new ItensDetalhesErrosCalculo();
        it.setVinculoFP(vinculoFP);
        it.setDetalhesCalculoRH(detalheProcessamentoFolha.getDetalhesCalculoRH());
        it.setErros("servidor sem dias trabalhados, " + vinculoFP);
        detalheProcessamentoFolha.getDetalhesCalculoRH().getItensDetalhesErrosCalculos().add(it);
        logger.debug("servidor sem dias trabalhados");
    }

    private void criaObservacaoCalculo(VinculoFP vinculoFP, DetalheProcessamentoFolha
        detalheProcessamentoFolha, String mensagem) {
        List<Afastamento> afastamentos = buscarAfastamentosSemRetorno(vinculoFP);

        if (!afastamentos.isEmpty()) {
            List<ItensDetalhesErrosCalculo> mensagensServidorSemDiasTrabalhados = Lists.newArrayList();
            List<ItensDetalhesErrosCalculo> mensagensServidorSemRetornoInformado = Lists.newArrayList();
            for (Afastamento a : afastamentos) {
                ItensDetalhesErrosCalculo it = new ItensDetalhesErrosCalculo();
                it.setVinculoFP(vinculoFP);
                it.setDetalhesCalculoRH(detalheProcessamentoFolha.getDetalhesCalculoRH());
                if (a.getTipoAfastamento().getDescontarDiasTrabalhados()) {
                    Date dataCalculo = DataUtil.ultimoDiaMes(Mes.getMesToInt(detalheProcessamentoFolha.getMes()));
                    if (DataUtil.getDataDiaAnterior(dataCalculo).before(a.getTermino())) {
                        mensagem = "Servidor sem dias trabalhados";
                        it.setErros(mensagem + ", " + vinculoFP);
                        mensagensServidorSemDiasTrabalhados.add(it);
                    } else {
                        mensagem = "Servidor com afastamento sem retorno informado";
                        it.setErros(mensagem + ", " + vinculoFP + " - " + DataUtil.getDataFormatada(a.getInicio(), "dd/MM/yyyy")
                            + " até " + DataUtil.getDataFormatada(a.getTermino(), "dd/MM/yyyy"));
                        mensagensServidorSemRetornoInformado.add(it);
                    }
                }
                logger.debug(mensagem);
            }
            detalheProcessamentoFolha.getDetalhesCalculoRH().getItensDetalhesErrosCalculos().addAll(mensagensServidorSemDiasTrabalhados);
            detalheProcessamentoFolha.getDetalhesCalculoRH().getItensDetalhesErrosCalculos().addAll(mensagensServidorSemRetornoInformado);
        } else {
            ItensDetalhesErrosCalculo it = new ItensDetalhesErrosCalculo();
            it.setVinculoFP(vinculoFP);
            it.setDetalhesCalculoRH(detalheProcessamentoFolha.getDetalhesCalculoRH());
            it.setErros(mensagem + ", " + vinculoFP);
            detalheProcessamentoFolha.getDetalhesCalculoRH().getItensDetalhesErrosCalculos().add(it);
            logger.debug(mensagem);
        }

    }

    public void preCarregaDadosBloqueioVerba(DetalheProcessamentoFolha detalhe, List<VinculoFP> vinculos) {
        Map<String, List<BloqueioEventoFP>> bloqueios = new LinkedHashMap<>();
        if (vinculos.size() == 1) {
            bloqueios = bloqueioEventoFPFacade.getVinculosEVerbasBloqueadosPorVinculo(vinculos.get(0));
        } else {
            long tempo = System.currentTimeMillis();
            bloqueios = bloqueioEventoFPFacade.getVinculosEVerbasBloqueados();
            logger.debug("tempo bloqueio verba: " + (System.currentTimeMillis() - tempo) / 1000 + "s");
        }
        detalhe.setBloqueioVerbaMap(bloqueios);
    }

    public void preCarregaDadosBloqueioBenficio(DetalheProcessamentoFolha detalhe, List<VinculoFP> vinculos) {
        Map<String, List<BloqueioBeneficio>> bloqueios = new LinkedHashMap<>();
        if (vinculos.size() == 1) {
            bloqueios = bloqueioBeneficioFacade.getVinculosBloqueadosPorBeneficioPorVinculo(vinculos.get(0),
                detalhe.getQuantidadeMesesRetroativos(), detalhe.getItemCalendarioFP().getDataUltimoDiaProcessamento());
        } else {
            long tempo = System.currentTimeMillis();
            bloqueios.putAll(bloqueioBeneficioFacade.findAllVinculosBloqueadosPorBeneficio(detalhe.getQuantidadeMesesRetroativos(), detalhe.getItemCalendarioFP().getDataUltimoDiaProcessamento()));
            logger.debug("tempo bloqueio beneficio: " + (System.currentTimeMillis() - tempo) / 1000 + "s");
        }
        detalhe.setBloqueioBeneficioMap(bloqueios);
    }

    public BigDecimal proporcionalizarValorParaLancamentos(FolhaDePagamentoNovoCalculador calculador, BigDecimal
        valor, LancamentoFP lancamento, VinculoFP vinculo) {

        Boolean naoPagaEventoEmFerias = calculador.isNaoPagaEventoEmFerias(lancamento.getEventoFP());
        Boolean naoPagaEventoEmLicencaPremio = calculador.isNaoPagaEventoEmLicencaPremio(lancamento.getEventoFP());
        if (lancamento.getProporcionalizar() && (naoPagaEventoEmFerias || naoPagaEventoEmLicencaPremio)) {
            double diasFeriasELicenca = 0d;
            LocalDate dataReferencia = funcoesFolhaFacade.getDataReferenciaDateTime(calculador.getEp());
            LocalDate dataInicial = dataReferencia.withDayOfMonth(1);
            LocalDate dataFinal = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());

            if (dataInicial.isBefore(DataUtil.dateToLocalDate(lancamento.getInicioVigencia()))) {
                dataInicial = DataUtil.dateToLocalDate(lancamento.getInicioVigencia());
            }
            if (dataFinal.isAfter(DataUtil.dateToLocalDate(lancamento.getFinalVigencia()))) {
                dataFinal = DataUtil.dateToLocalDate(lancamento.getFinalVigencia());
            }

            if (naoPagaEventoEmFerias) {
                diasFeriasELicenca += funcoesFolhaFacade.quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(calculador.getEp(), TipoPeriodoAquisitivo.FERIAS, dataInicial, dataFinal);
            }
            if (naoPagaEventoEmLicencaPremio) {
                diasFeriasELicenca += funcoesFolhaFacade.quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(calculador.getEp(), TipoPeriodoAquisitivo.LICENCA, dataInicial, dataFinal);
            }

            Double diasTrabalhadosPeloLancamento = funcoesFolhaFacade.diasTrabalhados(calculador.getEp(), calculador.getMes(), calculador.getAno(), TipoDiasTrabalhados.NORMAL, null, dataInicial, dataFinal);
            double dias = diasTrabalhadosPeloLancamento - diasFeriasELicenca;
            if (dias > 0) {
                return new BigDecimal(valor.doubleValue() / calculador.obterDiasDoMes().intValue() * dias);
            }
            return BigDecimal.ZERO;
        }

        if (isPodeProporcionalizar(lancamento)) {
            if (valor == null) {
                return BigDecimal.ZERO;
            }
            int diasTrabalhados = calculador.diasTrabalhados().intValue();
            int diasDoMes = calculador.obterDiasDoMes().intValue();
            int diasLancados = entreDiasLancamento(vinculo, lancamento.getMesAnoInicial(), lancamento.getMesAnoFinal());
            /*if (TipoLancamentoFP.isReferencia(lancamento.getTipoLancamentoFP()) && diasTrabalhados != diasDoMes) {
                return valor;
            }*/
            return proporcionalizarDiasTrabalhados(valor, diasTrabalhados, diasLancados, diasDoMes);
        } else {
            return valor;
        }
    }

    public boolean isPodeProporcionalizar(LancamentoFP l) {
        if (l.getTipoImportacao() != null && l.getTipoImportacao().equals(TipoImportacao.ECONSIG)) return false;
        if (!l.getProporcionalizar()) {
            return false;
        }
        return l.getEventoFP().getProporcionalizaDiasTrab();
    }


    @Deprecated
    private void calcularValorAposentadoriaParidade(VinculoFP vinculoFP, FolhaDePagamentoNovoCalculador calculador) {
        for (ItemAposentadoria item : aposentadoriaFacade.getItemAposentadoriaEventoFPByAposetado(vinculoFP)) {
            if (item.getEventoFP().getVerbaFixa() != null && item.getEventoFP().getVerbaFixa()) {
                int diasDoMes = calculador.obterDiasDoMes().intValue();
                Double valor = item.getValor().doubleValue() / diasDoMes;
                valor = valor * calculador.diasTrabalhados();
                calculador.getEventoValorFormula().put(item.getEventoFP(), new BigDecimal(valor));
            }
        }
    }

    private int entreDiasLancamento(VinculoFP vinculoFP, Date ini, Date fim) {
        DateTime dataInicial = new DateTime(ini);
        DateTime dataFinal = new DateTime(fim);
        DateTime primeiroDiaDoMesDoCalculo = vinculoFP.getPrimeiroDiaDoMesCalculo();
        primeiroDiaDoMesDoCalculo = DataUtil.zerarHorasMinutosSegundos(primeiroDiaDoMesDoCalculo);
        DateTime ultimoDiaDoMesDoCalculo = vinculoFP.getUltimoDiaDoMesCalculo();
        ultimoDiaDoMesDoCalculo = definirUltimaHoraDoDia(ultimoDiaDoMesDoCalculo);
        if (dataInicial.isBefore(primeiroDiaDoMesDoCalculo)) {
            dataInicial = primeiroDiaDoMesDoCalculo;
        }
        if (dataFinal.isAfter(ultimoDiaDoMesDoCalculo)) {
            dataFinal = ultimoDiaDoMesDoCalculo;
        }
        return Days.daysBetween(dataInicial, dataFinal).getDays() + 1;
    }

    private BigDecimal proporcionalizarDiasTrabalhados(BigDecimal valor, int diasTrabalhados, int diasLancados,
                                                       int diasDoMes) {
//        BigDecimal bd = valor.divide(new BigDecimal(diasDoMes), RoundingMode.HALF_EVEN);
        Double val = valor.doubleValue();

        if (diasTrabalhados == 0) {
            return BigDecimal.ZERO;
        }
        if (diasLancados > diasTrabalhados) {
            diasLancados = diasTrabalhados;
        }
//        bd = bd.multiply(new BigDecimal(diasTrabalhados));
//        bd = bd.divide(new BigDecimal(diasTrabalhados), RoundingMode.HALF_UP);
//        bd = bd.multiply(new BigDecimal(diasLancados));
        val = val / diasDoMes;
        val = val * diasLancados;
        return new BigDecimal(val + "");
    }

    private BigDecimal proporcionalizarDiasTrabalhados(BigDecimal valor, int diasTrabalhados, int diasDoMes) {
        Double val = valor.doubleValue();
        val = val / diasDoMes * diasTrabalhados;
        return new BigDecimal(val + "");
    }


    /**
     * Novo método de calculo retroativo, em teste.
     * obs: não foi alterado nada o método antigo
     *
     * @param res
     * @param fichaAtual
     * @param detalheProcessamentoFolha
     */
    private void calcularRetroativosNovoMetodo(ResultadoCalculoRetroativoFP res, FichaFinanceiraFP
        fichaAtual, DetalheProcessamentoFolha detalheProcessamentoFolha) {
        VinculoFP vinculo = fichaAtual.getVinculoFP();
        boolean mostrarMensagens = false;
        detalheProcessamentoFolha.setCalculandoRetroativo(true);
        vinculo.setCalculandoRetroativo(true);

        Calendar dataInicial = Calendar.getInstance();
        Calendar dataAtual = Calendar.getInstance();
        DateTime data = new DateTime().withYear(fichaAtual.getFolhaDePagamento().getAno()).withMonthOfYear(fichaAtual.getFolhaDePagamento().getMes().getNumeroMes()).withDayOfMonth(1);
        dataAtual.setTime(data.toDate());
        dataInicial.setTime(data.minusMonths(detalheProcessamentoFolha.getQuantidadeMesesRetroativos()).toDate());

        if (res.getDataInicialCalculoRetroativo().after(dataInicial.getTime())) {
            dataInicial.setTime(res.getDataInicialCalculoRetroativo());
        }

        while (dataInicial.getTime().before(dataAtual.getTime())) {

            //mes normal janeiro = 1
            Integer mes = (dataInicial.get(Calendar.MONTH) + 1);
            Integer ano = dataInicial.get(Calendar.YEAR);

            //define a data para as funcoes da folha
            vinculo.setAno(ano);
            vinculo.setMes(mes);
            vinculo.setFolha(fichaAtual.getFolhaDePagamento());
            //para o mes atual da folha de pagamento não calcula por aqui!
            if (fichaAtual.getFolhaDePagamento().getAno().equals(ano) && fichaAtual.getFolhaDePagamento().getMes().getNumeroMes() == mes) {
                break;
            }
            EventoFP eventoFerias = eventoFPFacade.recuperaEventoEstornoParaFerias();
            boolean temFerias = false;
            boolean temFeriasRecuperado = false;
            //recuperar todos os eventos agrupando valor.. caso naquele mes tenha mais de um evento igual calculado.
            List<EventoTotalItemFicha> itensFicha = fichaFinanceiraFPFacade.buscarTotalEventosItemFichaFinanceira(vinculo, ano, mes, fichaAtual.getFolhaDePagamento().getTipoFolhaDePagamento());

            for (EventoTotalItemFicha eventoSoma : itensFicha) {
                if (eventoFerias.equals(eventoSoma.getEvento())) {
                    temFeriasRecuperado = true;
                    break;
                }
            }

            //Ficha calculada na época;
            FolhaDePagamento novaFolha = getNovaFolha(fichaAtual, mes, ano);

            if (temFeriasRecuperado && funcoesFolhaFacade.houveAlteracaoProgramacaoFerias(vinculo, dateTimeToLocalDateTime(detalheProcessamentoFolha.getDataReferencia()).toLocalDate())) {
                vinculo.setHouveEstornoFerias(false);
            }
            Map.Entry<FichaFinanceiraFP, FolhaDePagamentoNovoCalculador> entry = calculoFolhaIndividualNovoParaRetroacao(vinculo, novaFolha, detalheProcessamentoFolha, mes, ano);
            FichaFinanceiraFP fichaCalculadaEpoca = entry.getKey();
            FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador = entry.getValue();

            if (mostrarMensagens) {
                for (ItemFichaFinanceiraFP item : fichaCalculadaEpoca.getItemFichaFinanceiraFP()) {

                    logger.debug("calculado época: " + item.toString());
                    if (item.getEventoFP().equals(eventoFerias)) {
                        temFerias = true;
                    }
                }
            }

            if (vinculo.isHouveEstornoFerias() && !temFerias) {
                ItemFichaFinanceiraFP it = new ItemFichaFinanceiraFP();
                it.setEventoFP(eventoFerias);
                it.setAno(ano);
                it.setMes(mes);
                it.setTipoEventoFP(TipoEventoFP.DESCONTO);
                it.setTipoCalculoFP(TipoCalculoFP.NORMAL);
                it.setValor(BigDecimal.ZERO);
                it.setFichaFinanceiraFP(fichaCalculadaEpoca);

                String idVinculo = "V" + it.getFichaFinanceiraFP().getVinculoFP().getId();
                String idFicha = "F" + fichaCalculadaEpoca.getId();
                String codigoEvento = "E" + eventoFerias.getCodigo();
                String tipoFolha = "F" + fichaCalculadaEpoca.getFolhaDePagamento().getTipoFolhaDePagamento().name();
                String anoFicha = "ANO" + it.getAno();
                String mesFicha = "MES" + it.getMes();
                String calculo = "CALCULO" + it.getTipoCalculoFP().name();

                it.setSequencial(idVinculo.concat(idFicha).concat(codigoEvento).concat(tipoFolha).concat(anoFicha).concat(mesFicha).concat(calculo));

                fichaCalculadaEpoca.getItemFichaFinanceiraFP().add(it);
            }
            List<ItemFichaFinanceiraFP> novosItensASalvar = new ArrayList<>();
            for (Iterator itRecuperado = itensFicha.iterator(); itRecuperado.hasNext(); ) {
                EventoTotalItemFicha eventoSomaRecuperado = (EventoTotalItemFicha) itRecuperado.next();
                for (Iterator itCalculado = fichaCalculadaEpoca.getItemFichaFinanceiraFP().iterator(); itCalculado.hasNext(); ) {

                    ItemFichaFinanceiraFP itemFichaCalculado = (ItemFichaFinanceiraFP) itCalculado.next();

                    if (eventoSomaRecuperado.getEvento().equals(itemFichaCalculado.getEventoFP())) {
                        BigDecimal valorRecuperado = eventoSomaRecuperado.getTotal();
                        BigDecimal valorCalculado = itemFichaCalculado.getValor();
                        BigDecimal resultado = valorCalculado.subtract(valorRecuperado);
                        TipoEventoFP tipoEventoFP = getTipoEventoCalculoRetroativo(itemFichaCalculado.getEventoFP(), resultado);
                        //Testes
                        BigDecimal valorRecuperadoTeste = valorRecuperado.setScale(5, RoundingMode.HALF_EVEN);
                        BigDecimal valorCalculadoTeste = valorCalculado.setScale(5, RoundingMode.HALF_EVEN);
                        BigDecimal resultadoValorTeste = valorCalculadoTeste.subtract(valorRecuperadoTeste);

                        if (itemFichaCalculado.getEventoFP().equals(eventoFerias) && vinculo.isHouveEstornoFerias()) {
                            logger.debug("estorno detectado");
                            resultadoValorTeste = eventoSomaRecuperado.getTotal();
                            defineValoresFeriasEstorno(eventoSomaRecuperado, itemFichaCalculado);
                        }

                        BigDecimal valorDiferenca = resultadoValorTeste.setScale(1, RoundingMode.HALF_EVEN);
                        if (valorDiferenca.compareTo(BigDecimal.ZERO) != 0) {
                            if (folhaDePagamentoNovoCalculador.isServidorEVerbaBloqueado(vinculo, itemFichaCalculado.getEventoFP())) {
                                itRecuperado.remove();
                                itCalculado.remove();
                                continue;
                            }
                            ItemFichaFinanceiraFP itemASalvar = new ItemFichaFinanceiraFP();
                            logger.debug("item a salvar: " + itemFichaCalculado);
                            itemASalvar.setAno(ano);
                            itemASalvar.setMes(mes);
                            itemASalvar.setTipoEventoFP(TipoEventoFP.INFORMATIVO.equals(itemFichaCalculado.getTipoEventoFP()) ? TipoEventoFP.INFORMATIVO : tipoEventoFP);
                            itemASalvar.setEventoFP(itemFichaCalculado.getEventoFP());
                            itemASalvar.setFichaFinanceiraFP(fichaAtual);
                            itemASalvar.setTipoCalculoFP(TipoCalculoFP.RETROATIVO);

                            String idVinculo = "V" + itemASalvar.getFichaFinanceiraFP().getVinculoFP().getId();
                            String idFicha = "F" + fichaAtual.getId();
                            String codigoEvento = "E" + itemFichaCalculado.getEventoFP().getCodigo();
                            String tipoFolha = "F" + fichaAtual.getFolhaDePagamento().getTipoFolhaDePagamento().name();
                            String anoFicha = "ANO" + itemASalvar.getAno();
                            String mesFicha = "MES" + itemASalvar.getMes();
                            String calculo = "CALCULO" + itemASalvar.getTipoCalculoFP().name();

                            itemASalvar.setSequencial(idVinculo.concat(idFicha).concat(codigoEvento).concat(tipoFolha).concat(anoFicha).concat(mesFicha).concat(calculo));

                            if (TipoNaturezaRefenciaCalculo.DIAS.equals(itemFichaCalculado.getEventoFP().getNaturezaRefenciaCalculo())) {
                                BigDecimal referencia = eventoSomaRecuperado.getReferencia().subtract(itemFichaCalculado.getValorReferencia());
                                itemASalvar.setValorReferencia(referencia.abs());
                            } else {
                                itemASalvar.setValorReferencia(itemFichaCalculado.getValorReferencia());
                            }
                            itemASalvar.setUnidadeReferencia(itemFichaCalculado.getUnidadeReferencia());
                            itemASalvar.setValorBaseDeCalculo(itemFichaCalculado.getValorBaseDeCalculo());
                            if (TipoEventoFP.INFORMATIVO.equals(itemFichaCalculado.getTipoEventoFP()) && TipoEventoFP.DESCONTO.equals(tipoEventoFP)) {
                                itemASalvar.setValor(getValorNormalizado(resultadoValorTeste.abs()).negate());
                            } else {
                                itemASalvar.setValor(getValorNormalizado(resultadoValorTeste.abs()));
                            }
                            novosItensASalvar.add(itemASalvar);
                            em.persist(itemASalvar);
                            salvarMemoriaCalculo(folhaDePagamentoNovoCalculador, itemASalvar);

                        }
                        itRecuperado.remove();
                        itCalculado.remove();
                    }
                }
            }
            if (mostrarMensagens) {
                logger.debug("----------------- Itens com diferença de valor inicio----------------------");
                for (ItemFichaFinanceiraFP item : novosItensASalvar) {
                    logger.debug(item.toString());
                }
                logger.debug("----------------- Itens com diferença de valor fim----------------------");
                logger.debug("----------------- Itens restantes da dos recuperados inicio----------------------");
                for (EventoTotalItemFicha item : itensFicha) {
                    logger.debug(item.toString());
                }
                logger.debug("----------------- Itens restantes da dos recuperados fim ----------------------");
                logger.debug("----------------- Itens restantes dos calculados inicio ----------------------");
            }
            for (ItemFichaFinanceiraFP item : fichaCalculadaEpoca.getItemFichaFinanceiraFP()) {
                if (item.getValor().floatValue() >= 0.01) {
                    if (folhaDePagamentoNovoCalculador.isServidorEVerbaBloqueado(vinculo, item.getEventoFP())) {
                        continue;
                    }
                    ItemFichaFinanceiraFP itemASalvar = new ItemFichaFinanceiraFP();
                    itemASalvar.setAno(ano);
                    itemASalvar.setMes(mes);
                    itemASalvar.setEventoFP(item.getEventoFP());
                    itemASalvar.setTipoEventoFP(item.getEventoFP().getTipoEventoFP());
                    itemASalvar.setFichaFinanceiraFP(fichaAtual);
                    itemASalvar.setTipoCalculoFP(TipoCalculoFP.RETROATIVO);
                    itemASalvar.setValorReferencia(item.getValorReferencia());
                    itemASalvar.setUnidadeReferencia(item.getUnidadeReferencia());
                    itemASalvar.setValorBaseDeCalculo(item.getValorBaseDeCalculo());
                    itemASalvar.setValor(getValorNormalizado(item.getValor()));

                    String idVinculo = "V" + itemASalvar.getFichaFinanceiraFP().getVinculoFP().getId();
                    String idFicha = "F" + fichaAtual.getId();
                    String codigoEvento = "E" + item.getEventoFP().getCodigo();
                    String tipoFolha = "F" + fichaAtual.getFolhaDePagamento().getTipoFolhaDePagamento().name();
                    String anoFicha = "ANO" + itemASalvar.getAno();
                    String mesFicha = "MES" + itemASalvar.getMes();
                    String calculo = "CALCULO" + itemASalvar.getTipoCalculoFP().name();

                    itemASalvar.setSequencial(idVinculo.concat(idFicha).concat(codigoEvento).concat(tipoFolha).concat(anoFicha).concat(mesFicha).concat(calculo));

                    novosItensASalvar.add(itemASalvar);
                    em.persist(itemASalvar);
                    salvarMemoriaCalculo(folhaDePagamentoNovoCalculador, itemASalvar);
                }
            }
            if (!TipoFolhaDePagamento.isFolhaRescisao(novaFolha.getTipoFolhaDePagamento()) || !TipoFolhaDePagamento.isFolhaComplementar(novaFolha.getTipoFolhaDePagamento())) {
                for (EventoTotalItemFicha item : itensFicha) {
                    if (item.getEvento().getCalculoRetroativo() && item.getTotal().floatValue() >= 0.01) {
                        if (folhaDePagamentoNovoCalculador.isServidorEVerbaBloqueado(vinculo, item.getEvento())) {
                            continue;
                        }
                        logger.debug("item a salvar: " + item);
                        ItemFichaFinanceiraFP itemASalvar = new ItemFichaFinanceiraFP();
                        itemASalvar.setAno(ano);
                        itemASalvar.setMes(mes);
                        itemASalvar.setEventoFP(item.getEvento());
                        itemASalvar.setTipoEventoFP(item.getTipoEventoFP().equals(TipoEventoFP.INFORMATIVO) ? TipoEventoFP.INFORMATIVO : (item.getTipoEventoFP().equals(TipoEventoFP.DESCONTO) ? TipoEventoFP.VANTAGEM : TipoEventoFP.DESCONTO));
                        itemASalvar.setFichaFinanceiraFP(fichaAtual);
                        itemASalvar.setTipoCalculoFP(TipoCalculoFP.RETROATIVO);
                        itemASalvar.setValorReferencia(item.getReferencia());
                        itemASalvar.setUnidadeReferencia(item.getEvento().getUnidadeReferencia());
                        itemASalvar.setValorBaseDeCalculo(item.getValorBase());
                        if (item.getTipoEventoFP().equals(TipoEventoFP.INFORMATIVO)) {
                            itemASalvar.setValor(getValorNormalizado(item.getTotal()).negate());
                        } else {
                            itemASalvar.setValor(getValorNormalizado(item.getTotal()));
                        }

                        String idVinculo = "V" + itemASalvar.getFichaFinanceiraFP().getVinculoFP().getId();
                        String idFicha = "F" + fichaAtual.getId();
                        String codigoEvento = "E" + itemASalvar.getEventoFP().getCodigo();
                        String tipoFolha = "F" + fichaAtual.getFolhaDePagamento().getTipoFolhaDePagamento().name();
                        String anoFicha = "ANO" + itemASalvar.getAno();
                        String mesFicha = "MES" + itemASalvar.getMes();
                        String calculo = "CALCULO" + itemASalvar.getTipoCalculoFP().name();

                        itemASalvar.setSequencial(idVinculo.concat(idFicha).concat(codigoEvento).concat(tipoFolha).concat(anoFicha).concat(mesFicha).concat(calculo));

                        novosItensASalvar.add(itemASalvar);
                        em.persist(itemASalvar);
                        salvarMemoriaCalculo(folhaDePagamentoNovoCalculador, itemASalvar);
                    }
                }
            }
            if (mostrarMensagens) {
                logger.debug("----------------- Itens restantes dos calculados fim----------------------");
            }

            dataInicial.add(Calendar.MONTH, 1);
        }
        //TODO remover iteração
//        for (ItemFichaFinanceiraFP itemFicha : listaCalculadosWebpublico){
//            //System.out.println(itemFicha);
//        }
    }

    private void defineValoresFeriasEstorno(EventoTotalItemFicha eventoSomaRecuperado, ItemFichaFinanceiraFP
        itemFichaCalculado) {
        itemFichaCalculado.setValor(eventoSomaRecuperado.getTotal().abs());
        itemFichaCalculado.setValorReferencia(eventoSomaRecuperado.getReferencia());
        itemFichaCalculado.setValorBaseDeCalculo(eventoSomaRecuperado.getValorBase());

        itemFichaCalculado.setTipoEventoFP(TipoEventoFP.DESCONTO);
    }

    public TipoEventoFP getTipoEventoCalculoRetroativo(EventoFP eventoFP, BigDecimal resultado) {
        if (TipoEventoFP.VANTAGEM.equals(eventoFP.getTipoEventoFP())) {
            if (resultado.compareTo(BigDecimal.ZERO) > 0) {
                return TipoEventoFP.VANTAGEM;
            } else {
                return TipoEventoFP.DESCONTO;
            }
        }
        if (TipoEventoFP.DESCONTO.equals(eventoFP.getTipoEventoFP())) {
            if (resultado.compareTo(BigDecimal.ZERO) > 0) {
                return TipoEventoFP.DESCONTO;
            } else {
                return TipoEventoFP.VANTAGEM;
            }
        }
        if (TipoEventoFP.INFORMATIVO.equals(eventoFP.getTipoEventoFP())) {
            if (resultado.compareTo(BigDecimal.ZERO) > 0) {
                return TipoEventoFP.VANTAGEM;
            } else {
                return TipoEventoFP.DESCONTO;
            }
        }
        return eventoFP.getTipoEventoFP();
    }

    //       public List<EventoTotalItemFicha> transformaFichaFinanceiraEmTotalEventos(FichaFinanceiraFP fichaFinanceiraFP){
//           List<EventoTotalItemFicha> eventos = new ArrayList<>();
//
//       }
    public FolhaDePagamento getNovaFolha(FichaFinanceiraFP ficha, Integer mes, Integer ano) {
        FolhaDePagamento nova = new FolhaDePagamento();
        nova.setAno(ano);
        nova.setMes(Mes.getMesToInt(mes));
        nova.setTipoFolhaDePagamento(ficha.getFolhaDePagamento().getTipoFolhaDePagamento());
        nova.setImprimeLogEmArquivo(ficha.getFolhaDePagamento().isImprimeLogEmArquivo());
        nova.setGravarMemoriaCalculo(ficha.getFolhaDePagamento().isGravarMemoriaCalculo());
        nova.setVersao(ficha.getFolhaDePagamento().getVersao());
        nova.setGravarMemoriaCalculo(ficha.getFolhaDePagamento().isGravarMemoriaCalculo());
        nova.setProcessarCalculoTransient(ficha.getFolhaDePagamento().isProcessarCalculoTransient());
        return nova;
    }

    public Map.Entry<FichaFinanceiraFP, FolhaDePagamentoNovoCalculador> calculoFolhaIndividualNovoParaRetroacao
        (VinculoFP vinculoFP, FolhaDePagamento folhaDePagamento, DetalheProcessamentoFolha
            detalheProcessamentoFolha, Integer mes, Integer ano) {
        Map<FichaFinanceiraFP, FolhaDePagamentoNovoCalculador> map = new HashMap<>();
        VinculoFP vinculo = vinculoFP;
        //System.out.println("Vinculo: " + vinculo);
        FichaFinanceiraFP fichaFinanceiraFP = new FichaFinanceiraFP();
        fichaFinanceiraFP.setCreditoSalarioPago(false);
        fichaFinanceiraFP.setVinculoFP(vinculo);
        fichaFinanceiraFP.setItemFichaFinanceiraFP(new ArrayList<ItemFichaFinanceiraFP>());
        fichaFinanceiraFP.setFolhaDePagamento(folhaDePagamento);
        fichaFinanceiraFP.setAutenticidade(gerarAutenticidade(vinculo));

        FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador = new FolhaDePagamentoNovoCalculador(vinculoFP, folhaDePagamento, TipoCalculoFP.RETROATIVO, funcoesFolhaFacade, detalheProcessamentoFolha, this);
        if (!folhaDePagamento.getTipoFolhaDePagamento().equals(TipoFolhaDePagamento.RESCISAO) && folhaDePagamentoNovoCalculador.diasTrabalhados() <= 0) {
            map.put(fichaFinanceiraFP, folhaDePagamentoNovoCalculador);
            return new AbstractMap.SimpleEntry<>(fichaFinanceiraFP, folhaDePagamentoNovoCalculador);
        }

        List<LancamentoFP> lista = lancamentoFPFacade.buscarLancamentosPorTipo(vinculo, ano, mes, folhaDePagamento.getTipoFolhaDePagamento(), detalheProcessamentoFolha.getItemCalendarioFP().getDataUltimoDiaProcessamento(), false);
        folhaDePagamentoNovoCalculador.preencheLacamentosCache(lista);
        for (LancamentoFP lancamento : lista) {
            EventoFP eventoFP = lancamento.getEventoFP();
            if (folhaDePagamentoNovoCalculador.isServidorEVerbaBloqueado(vinculo, lancamento.getEventoFP())) {
                continue;
            }
            if (!lancamento.getProporcionalizar() && (folhaDePagamentoNovoCalculador.isNaoPagaEventoEmFerias(eventoFP) || folhaDePagamentoNovoCalculador.isNaoPagaEventoEmLicencaPremio(eventoFP))) {
                continue;
            }
            folhaDePagamentoNovoCalculador.setEventoCalculandoAgora(eventoFP);
            folhaDePagamentoNovoCalculador.setLancamentoFP(lancamento);
            if (lancamento.getEventoFP().getCalculoRetroativo()) {
                if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                    if (folhaDePagamentoNovoCalculador.isCalculo13(eventoFP)) {
                        //no caso dos eventos lançados com verba fixa
                        if (folhaDePagamentoNovoCalculador.isUltimoAcumulado(eventoFP)) {
                            folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, new BigDecimal(folhaDePagamentoNovoCalculador.recuperarValor13ProporcionalUltimoMes(eventoFP) + ""));
                        } else if (folhaDePagamentoNovoCalculador.isValorAno(eventoFP)) {
                            folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, new BigDecimal(folhaDePagamentoNovoCalculador.recuperarValor13ProporcionalMediaAno(eventoFP) + ""));
                            if (lancamento.getValorDaBase() != null) {
                                folhaDePagamentoNovoCalculador.getEventoValorBase().put(eventoFP, lancamento.getValorDaBase());
                            }
                        } else if (folhaDePagamentoNovoCalculador.isHorasAno(eventoFP)) {
                            folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, proporcionalizarValorParaLancamentos(folhaDePagamentoNovoCalculador, lancamento.getQuantificacao(), lancamento, vinculo));
                        } else continue;


                    } else {
                        BigDecimal valorPropocionalizado = proporcionalizarValorParaLancamentos(folhaDePagamentoNovoCalculador, lancamento.getQuantificacao(), lancamento, vinculo);
                        //Caso tenha 2 lançamentos com mesmo evento vigentes para o servidor
                        if (folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(eventoFP)) {
                            valorPropocionalizado = valorPropocionalizado.add(folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP));
                        }
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, valorPropocionalizado);
                        folhaDePagamentoNovoCalculador.getEventoValorIntegral().put(eventoFP, lancamento.getQuantificacao());
                        if (lancamento.getValorDaBase() != null) {
                            folhaDePagamentoNovoCalculador.getEventoValorBase().put(eventoFP, lancamento.getValorDaBase());
                        }
                    }
                }
            }
        }

        // Necessário calcular eventos lançados por valor separado dos lançados por referência, pois
        // o cálculo de lançamentos por referência pode gerar o cálculo de eventos lançados por valor
        for (LancamentoFP lancamento : lista) {
            if (folhaDePagamentoNovoCalculador.isServidorEVerbaBloqueado(vinculo, lancamento.getEventoFP())) {
                continue;
            }
            if (!lancamento.getProporcionalizar() && (folhaDePagamentoNovoCalculador.isNaoPagaEventoEmFerias(lancamento.getEventoFP()) || folhaDePagamentoNovoCalculador.isNaoPagaEventoEmLicencaPremio(lancamento.getEventoFP()))) {
                continue;
            }
            if (lancamento.getEventoFP().getCalculoRetroativo()) {
                if (TipoLancamentoFP.isReferencia(lancamento.getTipoLancamentoFP())) {
                    EventoFP eventoFP = lancamento.getEventoFP();
                    folhaDePagamentoNovoCalculador.setEventoCalculandoAgora(eventoFP);
                    folhaDePagamentoNovoCalculador.setLancamentoFP(lancamento);
                    if (folhaDePagamentoNovoCalculador.isCalculo13(eventoFP)) {
                        if (folhaDePagamentoNovoCalculador.isUltimoAcumulado(eventoFP)) {
                            folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(eventoFP, new BigDecimal(folhaDePagamentoNovoCalculador.quantidadeMesesTrabalhadosAno()));
                        } else if (folhaDePagamentoNovoCalculador.isValorAno(eventoFP)) {
                            folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(eventoFP, new BigDecimal(TODOS_MESES));
                        } else if (folhaDePagamentoNovoCalculador.isHorasAno(eventoFP)) {
                            folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, lancamento.getQuantificacao());
                        } else continue;
                    } else {
                        folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(eventoFP, lancamento.getQuantificacao());
                    }

                    BigDecimal valorCalculado = BigDecimal.ZERO;
                    if (temMaisDeUmLancamentoComoMesmoEvento(lista, eventoFP) && folhaDePagamentoNovoCalculador.getLancamentosReferenciaJaCalculados().containsKey(eventoFP.getCodigo())) {
                        valorCalculado = folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP);
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(eventoFP);
                        folhaDePagamentoNovoCalculador.limparCacheValoresMetodos();
                    }

                    if (!folhaDePagamentoNovoCalculador.getEventoValorFormula().containsKey(eventoFP)) { // já calculou?
                        if (lancamento.getQuantificacao() != null) {
                            folhaDePagamentoNovoCalculador.getValorMetodo().put("recuperaQuantificacaoLancamentoTipoReferencia".concat(eventoFP.getCodigo()), lancamento.getQuantificacao().doubleValue());
                        }
                        if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.REFERENCIA) && lancamento.getBaseFP() != null) {
                            calculaBaseSobreParcentualDeLancamentofp(folhaDePagamentoNovoCalculador, lancamento);
                        } else {
                            folhaDePagamentoNovoCalculador.avaliaFormulaDoEventoFP(eventoFP);                // necessário verificar, pois o cálculo de eventos lançados por referência
                            folhaDePagamentoNovoCalculador.avaliaValorIntegralDoEventoFP(eventoFP);
                        }                // necessário verificar, pois o cálculo de eventos lançados por referência

                        if (lancamento.getEventoFP().getProporcionalizaDiasTrab() && !folhaDePagamentoNovoCalculador.isNaoPagaEventoEmFerias(eventoFP) && !folhaDePagamentoNovoCalculador.isNaoPagaEventoEmLicencaPremio(eventoFP)) {                                          // pode gerar o cálculo de outros eventos
                            BigDecimal valor = folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP);
                            folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(eventoFP);
                            folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, proporcionalizarValorParaLancamentos(folhaDePagamentoNovoCalculador, valor, lancamento, vinculo));
                        }

                        List<LancamentoFP> lancamentosJaCalculados = Lists.newArrayList();
                        if (folhaDePagamentoNovoCalculador.getLancamentosReferenciaJaCalculados().containsKey(eventoFP.getCodigo())) {
                            lancamentosJaCalculados = folhaDePagamentoNovoCalculador.getLancamentosReferenciaJaCalculados().get(eventoFP.getCodigo());
                        }
                        lancamentosJaCalculados.add(lancamento);
                        folhaDePagamentoNovoCalculador.getLancamentosReferenciaJaCalculados().put(eventoFP.getCodigo(), lancamentosJaCalculados);
                        BigDecimal valorAtual = folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP);
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(eventoFP);
                        folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, valorAtual.add(valorCalculado));
                    }
                }
            }
        }
        List<EventoFP> eventosAutomaticos = Lists.newArrayList();
        if (detalheProcessamentoFolha.getEventosAutomaticos() != null && !detalheProcessamentoFolha.getEventosAutomaticos().isEmpty()) {
            eventosAutomaticos = detalheProcessamentoFolha.getEventosAutomaticos();
        } else {
            eventosAutomaticos = eventoFPFacade.listaEventosAtivosPorTipoExecucao(TipoExecucaoEP.FOLHA, true);
            detalheProcessamentoFolha.setEventosAutomaticos(eventosAutomaticos);
        }

        calcularEventosAutomaticos(folhaDePagamentoNovoCalculador, vinculo, eventosAutomaticos);

        for (EventoFP eventoFP : folhaDePagamentoNovoCalculador.getEventoValorFormula().keySet()) {
            if (!folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP).equals(BigDecimal.ZERO)) {
                ItemFichaFinanceiraFP item = new ItemFichaFinanceiraFP();
                item.setMes(mes);
                item.setAno(ano);
                item.setEventoFP(eventoFP);
                item.setFichaFinanceiraFP(fichaFinanceiraFP);
                item.setTipoCalculoFP(TipoCalculoFP.RETROATIVO);
                item.setUnidadeReferencia(eventoFP.getUnidadeReferencia());
                item.setTipoEventoFP(eventoFP.getTipoEventoFP());
                item.setValor(folhaDePagamentoNovoCalculador.avaliaFormulaDoEventoFP(eventoFP));
                item.setValorBaseDeCalculo(folhaDePagamentoNovoCalculador.avaliaValorBaseDoEventoFP(eventoFP));
                item.setValorIntegral(folhaDePagamentoNovoCalculador.avaliaValorIntegralDoEventoFP(eventoFP));
                item.setValorReferencia(folhaDePagamentoNovoCalculador.avaliaReferenciaDoEventoFP(eventoFP));

                String idVinculo = "V" + item.getFichaFinanceiraFP().getVinculoFP().getId();
                String idFicha = "F" + fichaFinanceiraFP.getId();
                String codigoEvento = "E" + item.getEventoFP().getCodigo();
                String tipoFolha = "F" + fichaFinanceiraFP.getFolhaDePagamento().getTipoFolhaDePagamento().name();
                String anoFicha = "ANO" + item.getAno();
                String mesFicha = "MES" + item.getMes();
                String calculo = "CALCULO" + item.getTipoCalculoFP().name();

                item.setSequencial(idVinculo.concat(idFicha).concat(codigoEvento).concat(tipoFolha).concat(anoFicha).concat(mesFicha).concat(calculo));
                fichaFinanceiraFP.getItemFichaFinanceiraFP().add(item);
            }
        }
        return new AbstractMap.SimpleEntry<>(fichaFinanceiraFP, folhaDePagamentoNovoCalculador);
    }

    public boolean temMaisDeUmLancamentoComoMesmoEvento(List<LancamentoFP> list, EventoFP e) {
        int contador = 0;
        for (LancamentoFP l : list) {
            if (l.getEventoFP().equals(e)) {
                contador++;
            }
        }
        return contador > 1;
    }

    private void calculaSegundoLancamento(VinculoFP vinculo, FolhaDePagamentoNovoCalculador
        folhaDePagamentoNovoCalculador, LancamentoFP lancamento, EventoFP eventoFP) {
        BigDecimal valorLancamentoJaCalculado = folhaDePagamentoNovoCalculador.getEventoValorFormula().get(eventoFP);
        double valorSegundoLancamento = 0;
        try {
            folhaDePagamentoNovoCalculador.getEventoValorReferencia().remove(eventoFP);
            folhaDePagamentoNovoCalculador.getEventoValorReferencia().put(eventoFP, lancamento.getQuantificacao());
            BigDecimal base = folhaDePagamentoNovoCalculador.avaliaValorBaseDoEventoFP(eventoFP);
            valorSegundoLancamento = base.doubleValue() * (lancamento.getQuantificacao().doubleValue() / 100);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lancamento.getEventoFP().getProporcionalizaDiasTrab()) {                                          // pode gerar o cálculo de outros eventos

            folhaDePagamentoNovoCalculador.getEventoValorFormula().remove(eventoFP);
            BigDecimal valorLancamento2 = proporcionalizarValorParaLancamentos(folhaDePagamentoNovoCalculador, BigDecimal.valueOf(valorSegundoLancamento), lancamento, vinculo);
            valorLancamentoJaCalculado = valorLancamentoJaCalculado.add(valorLancamento2);
            folhaDePagamentoNovoCalculador.getEventoValorFormula().put(eventoFP, valorLancamentoJaCalculado);
        }
    }

    private DateTime getDataCalculo(Integer dia, Integer mes, Integer ano) {
        DateTime dataCalculo = new DateTime();
        dataCalculo = dataCalculo.withMonthOfYear(mes);
        dataCalculo = dataCalculo.withYear(ano);
        if (dia == null) {
            dataCalculo = dataCalculo.withDayOfMonth(dataCalculo.dayOfMonth().getMaximumValue());
        } else {
            dataCalculo = dataCalculo.withDayOfMonth(dia);
        }
        return dataCalculo;
    }

    @Override
    public List<FolhaDePagamento> lista() {
        return em.createQuery("from FolhaDePagamento folha order by folha.ano desc, folha.mes desc").getResultList();
    }

    public Integer obterVersaoFolhaDePagamento(CompetenciaFP competenciaFP, TipoFolhaDePagamento
        tipoFolhaDePagamento) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MAX(VERSAO)");
        sql.append("  FROM FOLHADEPAGAMENTO");
        sql.append(" WHERE TIPOFOLHADEPAGAMENTO = :tipoFolhaDePagamento");
        sql.append("   AND COMPETENCIAFP_ID = :competencia");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("tipoFolhaDePagamento", tipoFolhaDePagamento.name());
        q.setParameter("competencia", competenciaFP.getId());
        Integer versao = 1;
        try {
            Object result = q.getSingleResult();
            if (result != null) {
                versao = ((BigDecimal) result).intValue() + 1;
            }
        } catch (ExcecaoNegocioGenerica ex) {
            logger.debug("Error : {} ", ex);
            logger.debug("O sistema não pode determinar a versão da folha de pagamento! Retornando valor padrão 1. Competencia {} e Tipo de Folha {}.", competenciaFP, tipoFolhaDePagamento);
            return versao;
        }
        return versao;
    }

    public FolhaDePagamento efetivarFolhaDePagamento(FolhaDePagamento folhaDePagamento, Date dataEfetivacao) {
        folhaDePagamento.setEfetivadaEm(dataEfetivacao);
        if (configuracaoRHFacade.recuperarConfiguracaoRHVigente().getExibirFolhaPortalAoEfetivar()) {
            folhaDePagamento.setExibirPortal(Boolean.TRUE);
            folhaDePagamento.setDataPortal(sistemaFacade.getDataOperacao());
        }
        return salvarComNovaTransacao(folhaDePagamento);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FolhaDePagamento salvarComNovaTransacao(FolhaDePagamento folhaDePagamento) {
        folhaDePagamento = em.merge(folhaDePagamento);
        em.flush();
        return folhaDePagamento;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso recuperarInformacoesPortalTransparencia(AssistenteBarraProgresso assistente,
                                                                            FolhaDePagamento folhaDePagamento) {
        logger.info("recuperarInformacoesPortalTransparencia");
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente(assistente.getDataAtual());
        if (configuracaoRH.getIntegrarPortalTransparencia() != null
            && configuracaoRH.getIntegrarPortalTransparencia()
            && configuracaoRH.getVencimentoBasePortal() != null
        ) {
            Exercicio exercicioPorAno = exercicioFacade.getExercicioPorAno(folhaDePagamento.getAno());
            PortalTransparenciaNovo portalTransparenciaNovo = new PortalTransparenciaNovo(folhaDePagamento.getEfetivadaEm(), exercicioPorAno);
            assistente.setSelecionado(recuperarDadosPortalTransparencia(portalTransparenciaNovo, folhaDePagamento));
        }
        return assistente;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso salvarServidores(AssistenteBarraProgresso assistente) {
        logger.info("salvarServidores");
        assistente.setDescricaoProcesso("Salvando Servidores no Portal da Transparência...");
        if (assistente.getSelecionado() != null) {
            List<ServidorPortal> servidoresPortal = (List<ServidorPortal>) assistente.getSelecionado();
            if (servidoresPortal != null && !servidoresPortal.isEmpty()) {
                assistente.setTotal(servidoresPortal.size());
                for (ServidorPortal servidor : servidoresPortal) {
                    em.merge(servidor);
                    assistente.conta();
                }
            }
        }
        return assistente;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<List<String>> removerServidoresPortal(List<ServidorPortal> servidores, AssistenteBarraProgresso assistente, Date data) {
        assistente.setDescricaoProcesso("Removendo Servidores no Portal da Transparência...");
        List<String> retorno = Lists.newArrayList();
        if (servidores != null && !servidores.isEmpty()) {
            assistente.setTotal(servidores.size());
            for (ServidorPortal servidor : servidores) {
                try {
                    ServidorPortal servidorPortalNaoEnvido = recuperarServidorPortalNaoEnviado(servidor.getVinculo(), servidor.getMes(), servidor.getExercicio().getAno(), servidor.getTipoFolhaDePagamento());
                    if (servidorPortalNaoEnvido != null) {
                        removerEntidadePortalTransparencia(servidorPortalNaoEnvido);
                    } else {
                        String retornoPortal = servidor.getVinculo().getMatriculaFP().getMatricula() + "/" + servidor.getVinculo().getNumero() + " - " + servidor.getVinculo().getMatriculaFP().getPessoa().getNome() + ": ";
                        retornoPortal += portalTransparenciaNovoFacade.removerServidor(servidor, data);
                        retorno.add(retornoPortal);
                    }
                } finally {
                    assistente.conta();
                }
            }
        }
        return new AsyncResult<>(retorno);
    }

    public void efetivarFolhaDePagamentoPorCompetencia(CompetenciaFP competenciaFP, Date dataEfetivacao) {
        List<FolhaDePagamento> folhas = buscarFolhasPorCompetencia(competenciaFP);
        for (FolhaDePagamento folha : folhas) {
            if (!folha.folhaEfetivada()) {
                efetivarFolhaDePagamento(folha, dataEfetivacao);
            }
        }
    }

    public List<FolhaDePagamento> buscarFolhasPorCompetencia(CompetenciaFP competenciaFP) {
        Query q = em.createQuery("select folha from FolhaDePagamento folha where folha.competenciaFP = :competencia ");
        q.setParameter("competencia", competenciaFP);
        return q.getResultList();
    }


    public FolhaDePagamento getFolhaPorTipo(TipoFolhaDePagamento tipo) {
        Query q = em.createQuery("from FolhaDePagamento f where f.tipoFolhaDePagamento = :tipoFolha and f.mes = :mes and f.ano = :ano and f.efetivadaEm is null");

        q.setParameter("ano", getAno(new Date()));
        q.setParameter("mes", Mes.getMesToInt(getMes(new Date())));
        q.setParameter("tipoFolha", tipo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        FolhaDePagamento folha = (FolhaDePagamento) q.getSingleResult();
        if (folha.getCompetenciaFP() != null && folha.getCompetenciaFP().getStatusCompetencia().equals(StatusCompetencia.ABERTA)) {
            return folha;
        } else {
            return null;
        }
    }

    public List<VinculoFP> findVinculosByLote(LoteProcessamento loteProcessamento) {
        LoteProcessamento lote = loteProcessamentoFacade.recuperar(loteProcessamento.getId());
        String consulta = loteProcessamentoFacade.gerarQueryConsulta(lote);
        List<Long> idsVinculo = vinculoFPFacade.findVinculosByConsultDinamicaSql(consulta);
        List<VinculoFP> vinculosRecuperados = Lists.newArrayList();
        for (Long id : idsVinculo) {
            vinculosRecuperados.add(vinculoFPFacade.recuperar(id));
        }
        return vinculosRecuperados;
    }

    public List<FolhaDePagamento> recuperaFolhaPorCompetenciaEStatus(CompetenciaFP competencia) {
        String hql = "from FolhaDePagamento folha where folha.competenciaFP = :competencia " +
            " and folha.competenciaFP.statusCompetencia = :statusCompetencia";
        Query q = em.createQuery(hql);

        q.setParameter("competencia", competencia);
        q.setParameter("statusCompetencia", competencia.getStatusCompetencia());
        return q.getResultList();
    }

    public FolhaDePagamento recuperarUltimaFolhaDePagamentoNormalEfetivada() {
        String hql = "select fp" +
            "       from FolhaDePagamento fp" +
            "      where efetivadaEm is not null" +
            "        and fp.tipoFolhaDePagamento = :tipoFolha" +
            "      order by efetivadaEm desc";
        Query q = em.createQuery(hql);
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL);
        q.setMaxResults(1);
        try {
            return (FolhaDePagamento) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> recuperarVersao(Mes mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento) {
        Query q = em.createQuery("select versao from FolhaDePagamento where ano = :ano and mes = :mes and tipoFolhaDePagamento = :tipo order by versao");
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("tipo", tipoFolhaDePagamento);
        return q.getResultList();
    }

    public List<Integer> recuperarVersaoWithIntevaloMes(Mes mes, Integer ano, TipoFolhaDePagamento
        tipoFolhaDePagamento, Mes mesFinal, VinculoFP vinculo) {
        Query q = em.createQuery("select distinct folha.versao from FolhaDePagamento folha " +
            " inner join folha.fichaFinanceiraFPs ficha " +
            "where folha.ano = :ano " +
            "and folha.mes between :mes " +
            "and :mesFinal " +
            "and folha.tipoFolhaDePagamento = :tipo " +
            "and ficha.vinculoFP.id = :vinculo " +
            "order by folha.versao");
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("mesFinal", mesFinal);
        q.setParameter("tipo", tipoFolhaDePagamento);
        q.setParameter("vinculo", vinculo.getId());
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso remover(FolhaDePagamento entity, AssistenteBarraProgresso assistenteBarraProgressoExclusao) {
        Long folhaId = entity.getId();
        excluirDependencias(folhaId);
        super.remover(entity);
        assistenteBarraProgressoExclusao.conta();
        return assistenteBarraProgressoExclusao;
    }

    private void excluirDependencias(Long folhaId) {
        List<String> queries = Arrays.asList(
            "DELETE FROM ItensDetalhesErrosCalculo WHERE detalhescalculorh_id IN (SELECT id FROM DetalhesCalculoRH WHERE folhadepagamento_id = :folhaId)",
            "DELETE FROM DetalhesCalculoRH WHERE folhadepagamento_id = :folhaId",
            "DELETE FROM ItemErroDuplicidade WHERE folhaDePagamento_id = :folhaId",
            "DELETE FROM FilaProcessamentoFolha WHERE folhaDePagamento_id = :folhaId",
            "DELETE FROM fichafinanceirafp WHERE folhadepagamento_id = :folhaId",
            "DELETE FROM filtrofolhadepagamento WHERE folhadepagamento_id = :folhaId"
        );

        for (String query : queries) {
            em.createNativeQuery(query)
                .setParameter("folhaId", folhaId)
                .executeUpdate();
        }
    }

    @Override
    public List<FolhaDePagamento> listaDecrescente() {
        String hql = "from FolhaDePagamento f order by ano desc, mes desc";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public List<FolhaDePagamento> getFolhasDePagamentoEfetivadasNoMesDoTipo(Mes mes, Integer ano, TipoFolhaDePagamento tfp, boolean somenteEfetivadas) {
        String hql = "select fp" +
            "       from FolhaDePagamento fp" +
            "       where fp.mes = :mes" +
            "      and fp.ano = :ano" +
            "      and fp.tipoFolhaDePagamento = :tfp" +
            (somenteEfetivadas ? " and fp.efetivadaEm is not null " : "") +
            " order by fp.versao";
        Query q = em.createQuery(hql);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("tfp", tfp);
        return q.getResultList();
    }

    public List<FolhaDePagamento> getFolhasDePagamentoEfetivadasNoMesDoTipo(Mes mes, Integer ano, TipoFolhaDePagamento tfp) {
        return getFolhasDePagamentoEfetivadasNoMesDoTipo(mes, ano, tfp, true);
    }

    public List<FolhaDePagamento> getFolhasDePagamentoEfetivadasDoMes(Mes mes, Integer ano) {
        List<FolhaDePagamento> folhas = new ArrayList<>();
        for (TipoFolhaDePagamento tfp : TipoFolhaDePagamento.getFolhasPorPrioridadeDeUso()) {
            folhas.addAll(getFolhasDePagamentoEfetivadasNoMesDoTipo(mes, ano, tfp));
        }

        return folhas;
    }

    public List<FolhaDePagamento> getFolhasDePagamentoEfetivadasDoMesSemDecimoTerceiro(Mes mes, Integer ano) {
        List<FolhaDePagamento> folhas = new ArrayList<>();
        for (TipoFolhaDePagamento tfp : TipoFolhaDePagamento.getFolhasPorPrioridadeDeUsoSemDecimoTerceiro()) {
            folhas.addAll(getFolhasDePagamentoEfetivadasNoMesDoTipo(mes, ano, tfp));
        }

        return folhas;
    }

    public List<FolhaDePagamento> getFolhasDePagamentoEfetivadasDoMesDecimoTerceiro(Mes mes, Integer ano) {
        List<FolhaDePagamento> folhas = new ArrayList<>();
        for (TipoFolhaDePagamento tfp : TipoFolhaDePagamento.getFolhasDePagamentoDecimoTerceiro()) {
            folhas.addAll(getFolhasDePagamentoEfetivadasNoMesDoTipo(mes, ano, tfp));
        }
        return folhas;
    }

    public List<ServidorPortal> recuperarDadosPortalTransparencia(PortalTransparenciaNovo selecionado,
                                                                  FolhaDePagamento folhaDePagamento) {
        HashMap<Long, ServidorPortal> servidorPortalPorVinculoId = new HashMap<>();
        Integer ano = selecionado.getExercicio().getAno();
        int mes = DataUtil.getMes(selecionado.getDataOperacao());
        int numeroMesIniciandoEmZero = Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero();
        if (folhaDePagamento != null) {
            numeroMesIniciandoEmZero = folhaDePagamento.getMes().getNumeroMesIniciandoEmZero();
            ano = folhaDePagamento.getAno();
        }
        recuperarFolhaNormal(ano, numeroMesIniciandoEmZero, selecionado, folhaDePagamento)
            .forEach(servidorPortal -> servidorPortalPorVinculoId.put(servidorPortal.getVinculo().getId(), servidorPortal));
        buscarEstagiarios(ano, numeroMesIniciandoEmZero, selecionado, folhaDePagamento)
            .forEach(servidorPortal -> servidorPortalPorVinculoId.put(servidorPortal.getVinculo().getId(), servidorPortal));
        List<ServidorPortal> cedidos = buscarCedidos(ano, numeroMesIniciandoEmZero, selecionado, folhaDePagamento);
        adicionarCedidosOuAtualizarServidorPortalExistente(cedidos, servidorPortalPorVinculoId);
        List<ServidorPortal> retorno = Lists.newArrayList(servidorPortalPorVinculoId.values());
        corrigirMesServidores(folhaDePagamento, retorno);
        return retorno;
    }

    private static void adicionarCedidosOuAtualizarServidorPortalExistente(List<ServidorPortal> cedidos, HashMap<Long, ServidorPortal> servidorPortalPorVinculoId) {
        for (ServidorPortal cedido : cedidos) {
            if (servidorPortalPorVinculoId.containsKey(cedido.getVinculo().getId())) {
                ServidorPortal servidorPortalExistente = servidorPortalPorVinculoId.get(cedido.getVinculo().getId());
                servidorPortalExistente.setTipoCedenciaContratoFP(cedido.getTipoCedenciaContratoFP());
                servidorPortalPorVinculoId.put(cedido.getVinculo().getId(), servidorPortalExistente);
            } else {
                servidorPortalPorVinculoId.put(cedido.getVinculo().getId(), cedido);
            }
        }
    }

    private void corrigirMesServidores(FolhaDePagamento folhaDePagamento, List<ServidorPortal> retorno) {
        if (folhaDePagamento != null) {
            for (ServidorPortal servidorPortal : retorno) {
                servidorPortal.setMes(folhaDePagamento.getMes().getNumeroMes());
            }
        }
    }

    public List<ServidorPortal> recuperarFolhaNormal(Integer ano,
                                                     int numeroMesIniciandoEmZero,
                                                     PortalTransparenciaNovo portalTransparenciaNovo,
                                                     FolhaDePagamento folhaDePagamento) {
        String sql = "  select vinculo,  " +
            "  tipo,  " +
            "  lotacaoFuncional,  " +
            "  lotacaoRecurso, " +
            "  coalesce(sum(vencimentoBase),0) as vencimentoBase,  " +
            "  coalesce(sum(valorBruto),0) -coalesce(sum(vencimentoBase),0) as outrasVerbas,  " +
            "  coalesce(sum(valorBruto),0) as valorBruto,  " +
            "  coalesce(sum(valorDesconto),0) as valorDesconto, " +
            "  coalesce(sum(coalesce(valorBruto,0)) - sum(coalesce(valorDesconto,0)),0) as totalLiquido, " +
            "  coalesce(sum(valorIr),0) as impostoRenda, " +
            "  coalesce(sum(valorPrevidencia),0) as previdencia, " +
            "  coalesce(sum(valorAbateTeto),0) as abateTeto, " +
            "  coalesce(sum(valorDesconto),0) - coalesce(sum(valorIr),0) -  coalesce(sum(valorPrevidencia),0) -coalesce(sum(valorAbateTeto),0) as outrosDescontos " +
            "  from ( " +
            "  select  " +
            "  v.id as vinculo,  " +
            "  folha.TIPOFOLHADEPAGAMENTO as tipo," +
            "   uo.id  as lotacaofuncional,  " +
            "  recfp.id as lotacaoRecurso,  " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join itembasefp itemBase on itemBase.eventofp_id = item.eventofp_id " +
            "    inner join basefp b on b.id = itemBase.BASEFP_ID " +
            "  where ficha.vinculofp_id = v.id and itemFicha.id = item.id and item.tipoeventofp = 'VANTAGEM' and b.codigo = :verbaVencimentoBase) as vencimentoBase,  " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'VANTAGEM') as valorBruto,  " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "  where ficha.vinculofp_id = v.id and  item.id = itemFicha.id  and item.tipoeventofp = 'DESCONTO') as valorDesconto, " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join eventofp e on e.id = item.EVENTOFP_ID " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'DESCONTO' and e.IDENTIFICACAOEVENTOFP = 'IMPOSTO_RENDA') as valorIr,  " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join eventofp e on e.id = item.EVENTOFP_ID " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'DESCONTO' and e.IDENTIFICACAOEVENTOFP in ( 'RPPS', 'INSS')) as valorPrevidencia,  " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join eventofp e on e.id = item.EVENTOFP_ID " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'DESCONTO' and e.CODIGO in (635)) as valorAbateTeto " +
            "   " +
            "  " +
            "from vinculofp v  " +
            "inner join matriculafp mat on mat.id = v.matriculafp_id  " +
            "  inner join pessoafisica pf on pf.id = mat.pessoa_id " +
            "  inner join FichaFinanceiraFP ff on ff.vinculofp_id = v.id " +
            "  inner join folhadepagamento folha on folha.id  = ff.folhadepagamento_id " +
            "  inner join itemFichaFinanceiraFP itemFicha on itemFicha.FICHAFINANCEIRAFP_ID = ff.id " +
            "  inner join unidadeorganizacional uo on (uo.id = ff.UNIDADEORGANIZACIONAL_ID)  " +
            "  inner join recursofp recfp on (recfp.id = ff.RECURSOFP_ID)  " +
            "  inner join HierarquiaOrganizacional ho on (uo.ID = ho.SUBORDINADA_ID)  " +
            "    where folha.mes = :mesFolha " +
            "      and folha.ano = :exercicio " +
            "      and to_date(to_char(folha.EFETIVADAEM,'dd/MM/yyyy'), 'dd/MM/yyyy') BETWEEN ho.INICIOVIGENCIA and coalesce(ho.fimVIGENCIA, to_date(to_char(folha.EFETIVADAEM,'dd/MM/yyyy'), 'dd/MM/yyyy')) " +
            "      and ho.TIPOHIERARQUIAORGANIZACIONAL = :administrativa " +
            "      and v.id not in(select cedencia.contratofp_id from CEDENCIACONTRATOFP cedencia  where current_date between dataCessao and dataRetorno)  ";
        if (folhaDePagamento != null) {
            sql += " and folha.id = :folha ";
        }
        sql += "      group by v.id,  uo.id, folha.TIPOFOLHADEPAGAMENTO, itemFicha.id, recfp.id) dados   " +
            "      group by vinculo, tipo, lotacaoFuncional, lotacaoRecurso  ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("exercicio", ano);
        consulta.setParameter("mesFolha", numeroMesIniciandoEmZero);
        consulta.setParameter("verbaVencimentoBase", configuracaoRHFacade.recuperarConfiguracaoRHVigente(portalTransparenciaNovo.getDataOperacao()).getVencimentoBasePortal());
        consulta.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        if (folhaDePagamento != null) {
            consulta.setParameter("folha", folhaDePagamento.getId());
        }
        List<ServidorPortal> retorno = Lists.newArrayList();
        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            ServidorPortal servidorPortal = criarServidorPortal(objeto, numeroMesIniciandoEmZero, portalTransparenciaNovo);
            retorno.add(servidorPortal);
        }
        return retorno;
    }

    public Boolean folhasNaoExibidasPortalServidor(Integer ano, int numeroMesIniciandoEmZero) {
        String sql = " select * " +
            " from FOLHADEPAGAMENTO folha " +
            "  where folha.mes = :mesFolha " +
            "   and folha.ano = :exercicio " +
            "   and folha.EXIBIRPORTAL = 0 " +
            "   and folha.EFETIVADAEM is not null ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("exercicio", ano);
        consulta.setParameter("mesFolha", numeroMesIniciandoEmZero);
        List resultList = consulta.getResultList();
        return resultList != null && !resultList.isEmpty();
    }

    private List<CedenciaPortal> buscarCedenciasPortal(ServidorPortal servidorPortal) {
        String sql = "select  " +
            " c.datacessao as inicio, " +
            " c.dataretorno as termino, " +
            " pj.nomefantasia as orgao_receptor, " +
            " e.nome as esfera, " +
            " c.finalidadecedenciacontratofp as finalidade, " +
            " c.tipocontratocedenciafp as tipo_cedencia, " +
            " c.id as id_externo " +
            " from cedenciacontratofp c " +
            " inner join unidadeexterna u on u.id = c.cessionario_id " +
            " inner join pessoajuridica pj on pj.id = u.pessoajuridica_id " +
            " inner join esferagoverno e on e.id = u.esferagoverno_id " +
            " where c.contratofp_id = :vinculoFPId " +
            " and to_date(:dataCompetencia, 'dd/MM/yyyy') between trunc(c.datacessao, 'mm') " +
            "                                  and coalesce(c.dataretorno, to_date(:dataCompetencia, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataCompetencia", DataUtil.getDataFormatada(Util.criaDataPrimeiroDiaDoMesFP(servidorPortal.getMes(), servidorPortal.getExercicio().getAno())));
        q.setParameter("vinculoFPId", servidorPortal.getVinculo().getId());
        List<CedenciaPortal> retorno = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (resultado == null) {
            return retorno;
        }
        for (Object[] o : resultado) {
            CedenciaPortal cedenciaPortal = new CedenciaPortal();
            cedenciaPortal.setInicio(o[0] != null ? new java.sql.Date(((Date) o[0]).getTime()) : null);
            cedenciaPortal.setTermino(o[1] != null ? new java.sql.Date(((Date) o[1]).getTime()) : null);
            cedenciaPortal.setOrgaoReceptor(o[2] != null ? (String) o[2] : "");
            cedenciaPortal.setEsfera(o[3] != null ? (String) o[3] : "");
            cedenciaPortal.setFinalidade(o[4] != null ? FinalidadeCedenciaContratoFP.valueOf((String) o[4]).name() : null);
            cedenciaPortal.setTipoCedencia(o[5] != null ? TipoCedenciaContratoFP.valueOf((String) o[5]).name() : null);
            cedenciaPortal.setIdExterno(((BigDecimal) o[6]).longValue());
            retorno.add(cedenciaPortal);
        }
        return retorno;
    }

    private List<CedenciaPortal> buscarCedenciasPorAfastamentoPortal(ServidorPortal servidorPortal) {
        String sql = "select  " +
            " a.inicio as inicio, " +
            " a.termino as termino, " +
            " '' as orgao_receptor, " +
            " '' as esfera, " +
            " '' as finalidade, " +
            " t.codigo as tipo_cedencia, " +
            " a.id as id_externo " +
            " from afastamento a " +
            " inner join tipoafastamento t on t.id = a.tipoafastamento_id " +
            " where a.contratofp_id = :vinculoFPId " +
            " and t.codigo in :codigos " +
            " and to_date(:dataCompetencia, 'dd/MM/yyyy') between trunc(a.inicio, 'mm')  " +
            "                                 and coalesce(a.termino, to_date(:dataCompetencia, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataCompetencia", DataUtil.getDataFormatada(Util.criaDataPrimeiroDiaDoMesFP(servidorPortal.getMes(), servidorPortal.getExercicio().getAno())));
        q.setParameter("vinculoFPId", servidorPortal.getVinculo().getId());
        q.setParameter("codigos", Lists.newArrayList(AFASTAMENTO_COM_ONUS, AFASTAMENTO_SEM_ONUS));
        List<CedenciaPortal> retorno = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (resultado == null) {
            return retorno;
        }
        for (Object[] o : resultado) {
            CedenciaPortal cedenciaPortal = new CedenciaPortal();
            cedenciaPortal.setInicio(o[0] != null ? new java.sql.Date(((Date) o[0]).getTime()) : null);
            cedenciaPortal.setTermino(o[1] != null ? new java.sql.Date(((Date) o[1]).getTime()) : null);
            cedenciaPortal.setEsfera("");
            cedenciaPortal.setOrgaoReceptor("");
            cedenciaPortal.setFinalidade(null);
            cedenciaPortal.setTipoCedencia(
                o[5] != null ? (AFASTAMENTO_COM_ONUS.equals(((BigDecimal) o[5]).intValue())
                    ? TipoCedenciaContratoFP.COM_ONUS.name()
                    : TipoCedenciaContratoFP.SEM_ONUS.name()) : null);
            cedenciaPortal.setIdExterno(((BigDecimal) o[6]).longValue());
            retorno.add(cedenciaPortal);
        }
        return retorno;
    }

    public List<CedenciaPortal> buscarCedencias(ServidorPortal servidorPortal) {
        if (servidorPortal.getTipoCedenciaContratoFP() == null) {
            return Lists.newArrayList();
        }
        List<CedenciaPortal> cedencias = Lists.newArrayList();
        cedencias.addAll(buscarCedenciasPortal(servidorPortal));
        cedencias.addAll(buscarCedenciasPorAfastamentoPortal(servidorPortal)
            .stream()
            .filter(cedencia -> cedencias.stream()
                .noneMatch(cedenciaExistente -> cedenciaExistente.getInicio().equals(cedencia.getInicio())))
            .collect(Collectors.toList()));
        return cedencias;
    }

    private List<ServidorPortal> buscarCedidos(Integer ano,
                                               int mes,
                                               PortalTransparenciaNovo selecionado,
                                               FolhaDePagamento folhaDePagamento) {
        List<ServidorPortal> retorno = Lists.newArrayList();
        List cedidos = buscarCedidos(ano, mes, folhaDePagamento, selecionado, SOMENTE_CEDER_SERVIDOR_PARA_UNIDADE_EXTERNA);
        for (Object o : cedidos) {
            Object[] objeto = (Object[]) o;
            ServidorPortal servidorPortal = criarServidorPortal(objeto, mes, selecionado);
            servidorPortal.setTipoCedenciaContratoFP(TipoCedenciaContratoFP.COM_ONUS);
            retorno.add(servidorPortal);
        }

        List porAfastamento = buscarCedidosPorAfastamento(ano, mes);

        for (Object o : porAfastamento) {
            Object[] objeto = (Object[]) o;
            ServidorPortal servidorPortal = criarServidorPortal(objeto, mes, selecionado);
            Integer codigoAfastamento = ((BigDecimal) objeto[14]).intValue();
            if (AFASTAMENTO_COM_ONUS.equals(codigoAfastamento)) {
                servidorPortal.setTipoCedenciaContratoFP(TipoCedenciaContratoFP.COM_ONUS);
            } else {
                servidorPortal.setTipoCedenciaContratoFP(TipoCedenciaContratoFP.SEM_ONUS);
            }
            retorno.add(servidorPortal);
        }

        List semOnus = buscarCedidosSemOnus(ano, mes, SOMENTE_CEDER_SERVIDOR_PARA_UNIDADE_EXTERNA);
        for (Object o : semOnus) {
            Object[] objeto = (Object[]) o;
            ServidorPortal servidorPortal = criarServidorPortal(objeto, mes, selecionado);
            servidorPortal.setTipoCedenciaContratoFP(TipoCedenciaContratoFP.SEM_ONUS);
            retorno.add(servidorPortal);
        }

        return retorno;
    }

    private List buscarCedidosPorAfastamento(Integer ano, int mes) {
        String sql = "select  " +
            "        v.id as vinculo,    " +
            "        '" + TipoFolhaDePagamento.NORMAL.name() + "' as tipo,    " +
            "        uo.id  as lotacaofuncional,    " +
            "        recfp.id as lotacaoRecurso,    " +
            "        sum(0) as vencimentoBase,    " +
            "        sum(0) as outrasVerbas,    " +
            "        sum(0) as valorBruto,    " +
            "        sum(0) as valorDesconto,   " +
            "        sum(0) as totalLiquido,   " +
            "        sum(0) as impostoRenda,   " +
            "        sum(0) as previdencia,   " +
            "        sum(0) as abateTeto,   " +
            "        sum(0) as outrosDescontos,   " +
            "       tipo.descricao as tipoAfastamento,  " +
            "       tipo.codigo as codigo " +
            "  from vinculofp v   " +
            "  inner join matriculafp mat on mat.id = v.matriculafp_id    " +
            "    inner join pessoafisica pf on pf.id = mat.pessoa_id   " +
            "    inner join unidadeorganizacional uo on (uo.id = v.UNIDADEORGANIZACIONAL_ID)    " +
            "    inner join HierarquiaOrganizacional ho on (ho.subordinada_id = uo.id )    " +
            "    inner join RECURSODOVINCULOFP recv on recv.VINCULOFP_ID = v.id  " +
            "    inner join recursofp recfp on (recfp.id = recv.RECURSOFP_ID)    " +
            "    inner join afastamento afast on afast.CONTRATOFP_ID = v.id  " +
            "    inner join TIPOAFASTAMENTO tipo on tipo.id = afast.TIPOAFASTAMENTO_ID " +
            "      where    " +
            "      tipo.codigo in (:tipos) " +
            "        AND to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy') between afast.inicio and coalesce(afast.TERMINO,to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy'))  " +
            "        and to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy') BETWEEN ho.INICIOVIGENCIA and coalesce(ho.fimVIGENCIA, to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy'))  " +
            "        and to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy') BETWEEN recv.INICIOVIGENCIA and coalesce(recv.finalVIGENCIA, to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy')) " +
            "        and ho.TIPOHIERARQUIAORGANIZACIONAL = :administrativa " +
            "        group by v.id,  uo.id, recfp.id, tipo.descricao, tipo.codigo ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("tipos", buscarTipos());
        consulta.setParameter("dataParam", DataUtil.getDataFormatada(Util.criaDataPrimeiroDiaDoMesFP(mes, ano)));
        consulta.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        return consulta.getResultList();
    }

    private List<Integer> buscarTipos() {
        List<Integer> codigos = Lists.newLinkedList();
        codigos.add(AFASTAMENTO_COM_ONUS);
        codigos.add(AFASTAMENTO_SEM_ONUS);
        return codigos;
    }


    public ServidorPortal criarServidorPortal(Object[] objeto, int mes, PortalTransparenciaNovo selecionado) {
        VinculoFP vinculoFP = em.find(VinculoFP.class, ((BigDecimal) objeto[0]).longValue());
        TipoFolhaDePagamento tipoFolhaDePagamento = TipoFolhaDePagamento.valueOf((String) objeto[1]);
        UnidadeOrganizacional lotacao = em.find(UnidadeOrganizacional.class, ((BigDecimal) objeto[2]).longValue());
        RecursoFP lotacaoRecurso = em.find(RecursoFP.class, ((BigDecimal) objeto[3]).longValue());
        BigDecimal vencimentoBase = (BigDecimal) objeto[4];
        BigDecimal outrasVerbas = (BigDecimal) objeto[5];
        BigDecimal bruto = (BigDecimal) objeto[6];
        BigDecimal descontos = (BigDecimal) objeto[7];
        BigDecimal liquido = (BigDecimal) objeto[8];
        BigDecimal impostoRenda = (BigDecimal) objeto[9];
        BigDecimal previdencia = (BigDecimal) objeto[10];
        BigDecimal abateTeto = (BigDecimal) objeto[11];
        BigDecimal outrosDescontos = (BigDecimal) objeto[12];
        ServidorPortal servidorPortal = new ServidorPortal(vinculoFP, tipoFolhaDePagamento, lotacao, lotacaoRecurso, vencimentoBase, outrasVerbas, bruto, descontos, liquido, mes, selecionado.getExercicio(), impostoRenda, previdencia, abateTeto, outrosDescontos);
        return servidorPortal;
    }

    // cedente_id == NULL -> cedido = 1 (/cedencia/novo) -> Ceder Servidor para Unidade Externa (somenteCederServidorParaUnidadeExterna = true)
    // cedente_id != NULL -> cedido = 2 (/cedencia/novo) -> Registrar Funcionário Cedido para a Prefeitura
    private List buscarCedidos(Integer ano,
                               int numeroMesIniciandoEmZero,
                               FolhaDePagamento folhaDePagamento,
                               PortalTransparenciaNovo selecionado,
                               boolean somenteCederServidorParaUnidadeExterna) {
        String sql = "  select vinculo,  " +
            "  tipo,  " +
            "  lotacaoFuncional,  " +
            "  lotacaoRecurso, " +
            "  coalesce(sum(vencimentoBase),0) as vencimentoBase,  " +
            "  coalesce(sum(valorBruto),0) -coalesce(sum(vencimentoBase),0) as outrasVerbas,  " +
            "  coalesce(sum(valorBruto),0) as valorBruto,  " +
            "  coalesce(sum(valorDesconto),0) as valorDesconto, " +
            "  coalesce(sum(coalesce(valorBruto,0)) - sum(coalesce(valorDesconto,0)),0) as totalLiquido, " +
            "  coalesce(sum(valorIr),0) as impostoRenda, " +
            "  coalesce(sum(valorPrevidencia),0) as previdencia, " +
            "  coalesce(sum(valorAbateTeto),0) as abateTeto, " +
            "  coalesce(sum(valorDesconto),0) - coalesce(sum(valorIr),0) -  coalesce(sum(valorPrevidencia),0) -coalesce(sum(valorAbateTeto),0) as outrosDescontos " +
            "  from ( " +
            "  select  " +
            "  v.id as vinculo,  " +
            "  folha.TIPOFOLHADEPAGAMENTO as tipo,  " +
            "   uo.id  as lotacaofuncional,  " +
            "  recfp.id as lotacaoRecurso,  " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join itembasefp itemBase on itemBase.eventofp_id = item.eventofp_id " +
            "    inner join basefp b on b.id = itemBase.BASEFP_ID " +
            "  where ficha.vinculofp_id = v.id and itemFicha.id = item.id and item.tipoeventofp = 'VANTAGEM' and b.codigo = :verbaVencimentoBase) as vencimentoBase,  " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'VANTAGEM') as valorBruto,  " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "  where ficha.vinculofp_id = v.id and  item.id = itemFicha.id  and item.tipoeventofp = 'DESCONTO') as valorDesconto, " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join eventofp e on e.id = item.EVENTOFP_ID " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'DESCONTO' and e.IDENTIFICACAOEVENTOFP = 'IMPOSTO_RENDA') as valorIr,  " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join eventofp e on e.id = item.EVENTOFP_ID " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'DESCONTO' and e.IDENTIFICACAOEVENTOFP in ( 'RPPS', 'INSS')) as valorPrevidencia,  " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join eventofp e on e.id = item.EVENTOFP_ID " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'DESCONTO' and e.CODIGO in (635)) as valorAbateTeto " +
            "   " +
            "  " +
            "from vinculofp v  " +
            "inner join matriculafp mat on mat.id = v.matriculafp_id  " +
            "  inner join pessoafisica pf on pf.id = mat.pessoa_id " +
            "  inner join FichaFinanceiraFP ff on ff.vinculofp_id = v.id " +
            "  inner join folhadepagamento folha on folha.id  = ff.folhadepagamento_id " +
            "  inner join itemFichaFinanceiraFP itemFicha on itemFicha.FICHAFINANCEIRAFP_ID = ff.id " +
            "  inner join unidadeorganizacional uo on (uo.id = ff.UNIDADEORGANIZACIONAL_ID)  " +
            "  inner join recursofp recfp on (recfp.id = ff.RECURSOFP_ID)  " +
            "  inner join HierarquiaOrganizacional ho on (ho.subordinada_id = uo.id )  " +
            "  inner join cedenciacontratofp ced on ced.contratofp_id = v.id " +
            "    where folha.mes = :mesFolha " +
            "      and folha.ano = :exercicio " +
            "      and ho.TIPOHIERARQUIAORGANIZACIONAL = :administrativa " +
            "      and to_date(to_char(folha.EFETIVADAEM,'dd/MM/yyyy'), 'dd/MM/yyyy') BETWEEN ho.INICIOVIGENCIA and coalesce(ho.fimVIGENCIA, to_date(to_char(folha.EFETIVADAEM,'dd/MM/yyyy'), 'dd/MM/yyyy')) " +
            "      and folha.efetivadaEm between ced.dataCessao and coalesce(ced.dataRetorno, to_date(to_char(folha.EFETIVADAEM,'dd/MM/yyyy'), 'dd/MM/yyyy')) ";
        sql += somenteCederServidorParaUnidadeExterna ? " and ced.cedente_id is null " : "";
        sql += folhaDePagamento != null ? " and folha.id = :folha" : "";

        sql += "      group by v.id, folha.TIPOFOLHADEPAGAMENTO, uo.id, itemFicha.id, recfp.id) dados   " +
            "      group by vinculo, tipo, lotacaoFuncional, lotacaoRecurso  ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("exercicio", ano);
        consulta.setParameter("mesFolha", numeroMesIniciandoEmZero);
        consulta.setParameter("verbaVencimentoBase", configuracaoRHFacade.recuperarConfiguracaoRHVigente(selecionado.getDataOperacao()).getVencimentoBasePortal());
        consulta.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        if (folhaDePagamento != null) {
            consulta.setParameter("folha", folhaDePagamento.getId());
        }
        return consulta.getResultList();
    }

    // cedente_id == NULL -> cedido = 1 (/cedencia/novo) -> Ceder Servidor para Unidade Externa (somenteCederServidorParaUnidadeExterna = true)
    // cedente_id != NULL -> cedido = 2 (/cedencia/novo) -> Registrar Funcionário Cedido para a Prefeitura
    private List buscarCedidosSemOnus(Integer ano, int numeroMesIniciandoEmZero, boolean somenteCederServidorParaUnidadeExterna) {
        String sql = " select  " +
            "              v.id as vinculo,   " +
            "        '" + TipoFolhaDePagamento.NORMAL.name() + "' as tipo,    " +
            "              uo.id  as lotacaofuncional,   " +
            "              recfp.id as lotacaoRecurso,   " +
            "              sum(0) as vencimentoBase,   " +
            "              sum(0) as outrasVerbas,   " +
            "              sum(0) as valorBruto,   " +
            "              sum(0) as valorDesconto,  " +
            "              sum(0) as totalLiquido,  " +
            "              sum(0) as impostoRenda,  " +
            "              sum(0) as previdencia,  " +
            "              sum(0) as abateTeto,  " +
            "              sum(0) as outrosDescontos,  " +
            "             cedencia.TIPOCONTRATOCEDENCIAFP as tipoCedencia " +
            "                 " +
            "            from vinculofp v  " +
            "            inner join matriculafp mat on mat.id = v.matriculafp_id   " +
            "              inner join pessoafisica pf on pf.id = mat.pessoa_id  " +
            "              inner join unidadeorganizacional uo on (uo.id = v.UNIDADEORGANIZACIONAL_ID)   " +
            "              inner join VWHIERARQUIAADMINISTRATIVA ho on (ho.subordinada_id = uo.id )   " +
            "              inner join RECURSODOVINCULOFP recv on recv.VINCULOFP_ID = v.id " +
            "              inner join recursofp recfp on (recfp.id = recv.RECURSOFP_ID)   " +
            "              inner join CEDENCIACONTRATOFP cedencia on cedencia.CONTRATOFP_ID = v.id " +
            "                where   " +
            "                cedencia.TIPOCONTRATOCEDENCIAFP = :sem_onus  " +
            "                  AND to_date(:dataParam,'dd/MM/yyyy') between cedencia.dataCessao and coalesce(cedencia.dataRetorno,to_date(:dataParam, 'dd/MM/yyyy')) " +
            "                  and to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy') BETWEEN ho.INICIOVIGENCIA and coalesce(ho.fimVIGENCIA, to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy')) " +
            "                  and to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy') BETWEEN recv.INICIOVIGENCIA and coalesce(recv.finalVIGENCIA, to_date(to_char(to_date(:dataParam,'dd/MM/yyyy'),'dd/MM/yyyy'), 'dd/MM/yyyy')) " +
            "                  and current_date between cedencia.dataCessao and cedencia.dataRetorno ";
        sql += somenteCederServidorParaUnidadeExterna ? " and cedencia.cedente_id is null " : "";
        sql += "            group by v.id,  uo.id, recfp.id, cedencia.TIPOCONTRATOCEDENCIAFP ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("sem_onus", TipoCedenciaContratoFP.SEM_ONUS.name());
        consulta.setParameter("dataParam", DataUtil.getDataFormatada(Util.criaDataPrimeiroDiaDoMesFP(numeroMesIniciandoEmZero, ano)));
        List resultList = consulta.getResultList();
        return resultList;
    }

    private List<ServidorPortal> buscarEstagiarios(Integer ano,
                                                   int numeroMesIniciandoEmZero,
                                                   PortalTransparenciaNovo portalTransparenciaNovo,
                                                   FolhaDePagamento folhaDePagamento) {
        String sql = "  select vinculo,  " +
            "  tipo,  " +
            "  lotacaoFuncional,  " +
            "  lotacaoRecurso, " +
            "  coalesce(sum(vencimentoBase),0) as vencimentoBase,  " +
            "  coalesce(sum(valorBruto),0) -coalesce(sum(vencimentoBase),0) as outrasVerbas,  " +
            "  coalesce(sum(valorBruto),0) as valorBruto,  " +
            "  coalesce(sum(valorDesconto),0) as valorDesconto, " +
            "  coalesce(sum(coalesce(valorBruto,0)) - sum(coalesce(valorDesconto,0)),0) as totalLiquido, " +
            "  coalesce(sum(valorIr),0) as impostoRenda, " +
            "  coalesce(sum(valorPrevidencia),0) as previdencia, " +
            "  coalesce(sum(valorAbateTeto),0) as abateTeto, " +
            "  coalesce(sum(valorDesconto),0) - coalesce(sum(valorIr),0) -  coalesce(sum(valorPrevidencia),0) -coalesce(sum(valorAbateTeto),0) as outrosDescontos " +
            "  from ( " +
            "  select  " +
            "  v.id as vinculo,  " +
            "  folha.TIPOFOLHADEPAGAMENTO as tipo,  " +
            "   uo.id  as lotacaofuncional,  " +
            "  recfp.id as lotacaoRecurso,  " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join itembasefp itemBase on itemBase.eventofp_id = item.eventofp_id " +
            "    inner join basefp b on b.id = itemBase.BASEFP_ID " +
            "  where ficha.vinculofp_id = v.id and itemFicha.id = item.id and item.tipoeventofp = 'VANTAGEM' and b.codigo = :verbaVencimentoBase) as vencimentoBase,  " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'VANTAGEM') as valorBruto,  " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "  where ficha.vinculofp_id = v.id and  item.id = itemFicha.id  and item.tipoeventofp = 'DESCONTO') as valorDesconto, " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join eventofp e on e.id = item.EVENTOFP_ID " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'DESCONTO' and e.IDENTIFICACAOEVENTOFP = 'IMPOSTO_RENDA') as valorIr,  " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join eventofp e on e.id = item.EVENTOFP_ID " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'DESCONTO' and e.IDENTIFICACAOEVENTOFP in ( 'RPPS', 'INSS')) as valorPrevidencia,  " +
            "   " +
            "  (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "    inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "    inner join eventofp e on e.id = item.EVENTOFP_ID " +
            "  where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'DESCONTO' and e.CODIGO in (635)) as valorAbateTeto " +
            "   " +
            "  " +
            " from vinculofp v  inner join contratofp c on c.id = v.id inner join ESTAGIARIO e on e.id = c.id " +
            "  inner join matriculafp mat on mat.id = v.matriculafp_id  " +
            "  inner join pessoafisica pf on pf.id = mat.pessoa_id " +
            "  inner join FichaFinanceiraFP ff on ff.vinculofp_id = v.id " +
            "  inner join folhadepagamento folha on folha.id  = ff.folhadepagamento_id " +
            "  inner join itemFichaFinanceiraFP itemFicha on itemFicha.FICHAFINANCEIRAFP_ID = ff.id " +
            "  inner join unidadeorganizacional uo on (uo.id = ff.UNIDADEORGANIZACIONAL_ID)  " +
            "  inner join recursofp recfp on (recfp.id = ff.RECURSOFP_ID)  " +
            "  inner join HierarquiaOrganizacional ho on (ho.subordinada_id = uo.id )  " +
            "    where folha.mes = :mesFolha " +
            "      and folha.ano = :exercicio " +
            "      and ho.TIPOHIERARQUIAORGANIZACIONAL = :administrativa " +
            "      and to_date(to_char(folha.EFETIVADAEM,'dd/MM/yyyy'), 'dd/MM/yyyy') BETWEEN ho.INICIOVIGENCIA and coalesce(ho.fimVIGENCIA, to_date(to_char(folha.EFETIVADAEM,'dd/MM/yyyy'), 'dd/MM/yyyy')) ";
        if (folhaDePagamento != null) {
            sql += " and folha.id = :folha ";
        }
        sql += "      group by v.id,  folha.TIPOFOLHADEPAGAMENTO,uo.id, itemFicha.id, recfp.id) dados   " +
            "      group by vinculo,tipo, lotacaoFuncional, lotacaoRecurso  ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("exercicio", ano);
        consulta.setParameter("mesFolha", numeroMesIniciandoEmZero);
        consulta.setParameter("verbaVencimentoBase", configuracaoRHFacade.recuperarConfiguracaoRHVigente(portalTransparenciaNovo.getDataOperacao()));
        consulta.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        if (folhaDePagamento != null) {
            consulta.setParameter("folha", folhaDePagamento.getId());
        }
        List resultList = consulta.getResultList();
        if (folhaDePagamento == null) {
            resultList.addAll(buscarEstagiariosSemFolha(ano, numeroMesIniciandoEmZero));
        }
        List<ServidorPortal> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            ServidorPortal servidorPortal = criarServidorPortal(objeto, numeroMesIniciandoEmZero, portalTransparenciaNovo);
            retorno.add(servidorPortal);
        }
        return retorno;
    }

    private List buscarEstagiariosSemFolha(Integer ano, int numeroMesIniciandoEmZero) {
        String sql =
            "   select   " +
                "    v.id as vinculo,   " +
                "        '" + TipoFolhaDePagamento.NORMAL.name() + "' as tipo,    " +
                "     uo.id  as lotacaofuncional,   " +
                "    recfp.id as lotacaoRecurso,   " +
                "    0 as vencimentoBase,   " +
                "    0 as outrasVerbas,   " +
                "    0 as valorBruto,   " +
                "    0 as valorDesconto,  " +
                "    0 as totalLiquido,  " +
                "    0 as impostoRenda,  " +
                "    0 as previdencia,  " +
                "    0 as abateTeto,  " +
                "    0 as outrosDescontos  " +
                " from vinculofp v  inner join contratofp c on c.id = v.id inner join ESTAGIARIO e on e.id = c.id   " +
                "    inner join matriculafp mat on mat.id = v.matriculafp_id   " +
                "    inner join pessoafisica pf on pf.id = mat.pessoa_id  " +
                "    inner join unidadeorganizacional uo on (uo.id = v.UNIDADEORGANIZACIONAL_ID)   " +
                "    inner join recursodovinculofp recv on recv.vinculofp_id = v.id " +
                "    inner join recursofp recfp on (recfp.id = recv.RECURSOFP_ID)   " +
                "    inner join VWHIERARQUIAADMINISTRATIVA ho on (ho.subordinada_id = uo.id )   " +
                "      where  " +
                "        to_date(:dataParam,'dd/MM/yyyy')  BETWEEN ho.INICIOVIGENCIA and coalesce(ho.fimVIGENCIA, to_date(:dataParam,'dd/MM/yyyy'))  " +
                "        and to_date(:dataParam,'dd/MM/yyyy') BETWEEN v.INICIOVIGENCIA and coalesce(v.finalVIGENCIA, to_date(:dataParam,'dd/MM/yyyy'))  " +
                "        and to_date(:dataParam,'dd/MM/yyyy') BETWEEN recv.INICIOVIGENCIA and coalesce(recv.finalVIGENCIA, to_date(:dataParam,'dd/MM/yyyy'))  ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataParam", DataUtil.getDataFormatada(Util.criaDataPrimeiroDiaDoMesFP(numeroMesIniciandoEmZero, ano)));
        return consulta.getResultList();
    }

    public List<FolhaDePagamento> buscarFolhasPorMesAnoTipoAndVersao(Mes mes, Integer ano, TipoFolhaDePagamento
        tipoFolhaDePagamento, Integer versao) {

        String hql = "select fp" +
            "       from FolhaDePagamento fp" +
            "            inner join fp.competenciaFP c" +
            "      where fp.mes = :mes" +
            "            and fp.ano = :ano" +
            "            and fp.tipoFolhaDePagamento = :tipo" +
            "            and fp.versao = :versao " +
            " order by fp.versao";
        Query q = em.createQuery(hql);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("tipo", tipoFolhaDePagamento);
        q.setParameter("versao", versao);
        return q.getResultList();
    }

    private String gerarAutenticidade(VinculoFP vinculo) {
        if (vinculo != null) {
            Date dataGeracaoChave = new Date();
            String assinatura = dataGeracaoChave.getTime() + vinculo.getNumero() + StringUtil.retornaApenasNumeros(vinculo.getMatriculaFP().getMatricula());
            return GeradorChaveAutenticacao.geraChaveDeAutenticacao(assinatura, GeradorChaveAutenticacao.TipoAutenticacao.CONTRA_CHEQUE);
        }
        return "";
    }

    public boolean isServidorPrevidenciaPorTipoRegime(Long idVinculo, Integer mes, Integer ano, TipoRegimePrevidenciario tipoRegime) {
        String sql = " select vinculo.* from vinculofp vinculo " +
            " inner join previdenciavinculofp prev on prev.contratofp_id = vinculo.id " +
            " inner join tipoprevidenciafp tipo on prev.tipoprevidenciafp_id = tipo.id " +
            " where tipo.tiporegimeprevidenciario = :tipoRegime " +
            " and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(prev.iniciovigencia) " +
            " and coalesce(trunc(prev.finalvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "and vinculo.id = :idVinculo";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoRegime", tipoRegime.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(Util.criaDataUltimoDiaDoMesFP(mes, ano)));
        q.setParameter("idVinculo", idVinculo);

        return !q.getResultList().isEmpty();
    }

    public List<FichaFinanceiraFP> buscarFichasPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidades(Mes mes, Integer ano, VinculoFP vinculoFP, List<String> tipoFolha) {
        String sql = " select distinct ficha.* " +
            " from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.ID " +
            " inner join PESSOAFISICA pf on matricula.PESSOA_ID = pf.ID " +
            " where folha.ANO = :ano and folha.mes = :mes and folha.tipoFolhaDePagamento in :tipoFolha" +
            " and vinculo.id = :idVinculo";

        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", tipoFolha);
        q.setParameter("idVinculo", vinculoFP.getId());

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }


    public List<FolhaDePagamento> buscarFolhaPorTipoMesAndExercicio(TipoFolhaDePagamento tipo, Mes mes, Exercicio exercicio) {
        Query q = em.createQuery("from FolhaDePagamento f where f.tipoFolhaDePagamento =:tipoFolha " +
            "and f.mes = :mes and f.ano = :ano order by f.versao");
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("mes", mes);
        q.setParameter("tipoFolha", tipo);

        return q.getResultList();
    }

    public List<Object[]> buscarVinculosParaCreditoSalario(CreditoSalario creditoSalario, Boolean omitirMatricula) {
        List<Long> gruposIds = creditoSalario.getGrupos().stream().map(GrupoRecursoFP::getId).collect(Collectors.toList());

        List<List<Long>> listaIds = Lists.partition(gruposIds, TAMANHO_MAXIMO_IN);
        List<String> condicoes = Lists.newArrayList();
        for (int index = 0; index < listaIds.size(); index++) {
            condicoes.add("grupo.id in :gruposIds" + index);
        }
        if (creditoSalario.getVinculoFPS() != null && !creditoSalario.getVinculoFPS().isEmpty()) {
            condicoes.add(" vfp.id " + (omitirMatricula ? " not " : " ") + " in :listaIdVinculos ");
        }

        String condicaoComOr = String.join(" or ", condicoes);

        StringBuilder sb = new StringBuilder();
        sb.append("select vfp.id, mfp.matricula, pf.nome, sum(case when (iff.tipoEventoFP = :tipo_vantagem) then coalesce(iff.valor, 0) else coalesce(-iff.valor, 0) end) as valor,");
        sb.append(" grupo.nome as nomeGrupo, rec.id as recursofFPId, ff.unidadeOrganizacional_Id as unidadeId, grupo.id as grupoId");
        sb.append(" from FichaFinanceiraFP ff ");
        sb.append(" inner join folhadepagamento fp on fp.id = ff.folhadepagamento_id ");
        sb.append(" inner join itemfichafinanceirafp iff on iff.fichafinanceirafp_id = ff.id ");
        sb.append(" inner join recursofp rec on ff.recursofp_id = rec.id ");
        sb.append(" inner join AgrupamentoRecursoFP agr on agr.recursofp_id = rec.id  ");
        sb.append(" inner join GrupoRecursoFP grupo on agr.grupoRecursofp_ID = grupo.id  ");
        sb.append(" inner join vinculofp vfp on vfp.id = ff.vinculofp_id ");
        sb.append(" inner join matriculafp mfp on mfp.id = vfp.matriculafp_id ");
        sb.append(" inner join pessoafisica pf on pf.id = mfp.pessoa_id ");
        sb.append(" where fp.id = :folha_id and iff.tipoEventoFP in('VANTAGEM', 'DESCONTO')");
        sb.append(" and coalesce(ff.creditoSalarioPago, 0) = 0 ");
        sb.append(" and  valor > 0 ");
        if (!condicoes.isEmpty()) {
            sb.append(" and (" + condicaoComOr + ") ");
        }
        sb.append(" group by vfp.id, mfp.matricula, pf.nome, grupo.nome,  rec.id, ff.unidadeOrganizacional_Id, grupo.id  ");
        sb.append(" order by pf.nome ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("folha_id", creditoSalario.getFolhaDePagamento().getId());
        q.setParameter("tipo_vantagem", TipoEventoFP.VANTAGEM.name());
        if (creditoSalario.getVinculoFPS() != null && !creditoSalario.getVinculoFPS().isEmpty()) {
            q.setParameter("listaIdVinculos", creditoSalario.getVinculoFPS().stream().map(VinculoFP::getId).collect(Collectors.toList()));
        }
        if (!gruposIds.isEmpty()) {
            for (int index = 0; index < listaIds.size(); index++) {
                q.setParameter("gruposIds" + index, listaIds.get(index));
            }
        }
        return q.getResultList();

    }

    private String buscarListaIdVinculos(CreditoSalario creditoSalario) {
        String codigos = "";
        for (VinculoFP cod : creditoSalario.getVinculoFPS()) {
            codigos += "" + cod.getId() + ",";
        }
        codigos = codigos.substring(0, codigos.length() - 1);
        return codigos;
    }

    public List<Object[]> buscarPensionistasParaCreditoSalario(CreditoSalario creditoSalario, Boolean omitirMatricula) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select vfp.numero, mfp.matricula, vfp.id as contratoId , ");
            sb.append(" grupo.nome as nomeGrupo , rec.id as recursofFPId, ff.unidadeOrganizacional_Id as unidadeId, grupo.id as idGrupo ");
            sb.append(" from FichaFinanceiraFP ff ");
            sb.append(" inner join folhadepagamento fp on fp.id = ff.folhadepagamento_id ");
            sb.append(" inner join itemfichafinanceirafp iff on iff.fichafinanceirafp_id = ff.id ");
            sb.append(" inner join recursofp rec on ff.recursofp_id = rec.id ");
            sb.append(" inner join AgrupamentoRecursoFP agr on agr.recursofp_id = rec.id  ");
            sb.append(" inner join GrupoRecursoFP grupo on agr.grupoRecursofp_ID = grupo.id  ");
            sb.append(" inner join vinculofp vfp on vfp.id = ff.vinculofp_id ");
            sb.append(" inner join matriculafp mfp on mfp.id = vfp.matriculafp_id ");
            sb.append(" where fp.id = :folha_id ");
            sb.append(" and coalesce(ff.creditoSalarioPago, 0 ) = 0 ");
            sb.append(" and vfp.id in (select pensao.vinculofp_id from pensaoalimenticia pensao  ");
            sb.append(" inner join BeneficioPensaoAlimenticia bpa on pensao.id = bpa.PensaoAlimenticia_id  ");
            sb.append(" where to_date(:dataReferencia, 'dd/MM/yyyy') between bpa.inicioVigencia and coalesce(bpa.finalVigencia, to_date(:dataReferencia, 'dd/MM/yyyy')) ");
            sb.append(" and pensao.vinculofp_id = vfp.id) ");
            sb.append(creditoSalario.getVinculoFPS() != null && !creditoSalario.getVinculoFPS().isEmpty() ? " and vfp.id " + (omitirMatricula ? " not " : " ") + " in (" + buscarListaIdVinculos(creditoSalario) + ") " : "");
            sb.append(" group by vfp.numero, mfp.matricula, vfp.id, grupo.nome ,  rec.id, ff.unidadeOrganizacional_Id, grupo.id ");

            Query q = em.createNativeQuery(sb.toString());
            q.setParameter("folha_id", creditoSalario.getFolhaDePagamento().getId());
            q.setParameter("dataReferencia", DataUtil.getDataFormatada(creditoSalario.getDataCredito()));
            return q.getResultList();
        } catch (NullPointerException npe) {
            return new ArrayList<>();
        }
    }

    public List<VinculoCreditoSalario> buscarPensionistaParaCreditoSalario(CreditoSalario creditoSalario, Boolean omitirMatricula) {
        List<VinculoCreditoSalario> retorno = new ArrayList<>();
        for (Object[] obj : buscarPensionistasParaCreditoSalario(creditoSalario, omitirMatricula)) {
            VinculoFP vinculo = vinculoFPFacade.recuperar((((BigDecimal) obj[2]).longValue()));
            List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticias = pensaoAlimenticiaFacade.buscarBeneficiarioPensaoAlimenticiaVigentePorVinculo(vinculo, creditoSalario.getCompetenciaFP());
            filtrarSomenteBeneficiarioManuais(creditoSalario, beneficioPensaoAlimenticias);
            for (BeneficioPensaoAlimenticia beneficioPensaoAlimenticia : beneficioPensaoAlimenticias) {
                PessoaFisica pessoaFisica = pessoaFisicaFacade.buscarPessoaFisicaDoBeneficio(vinculo, beneficioPensaoAlimenticia, creditoSalario);
                if (pessoaFisica != null) {
                    MatriculaFP matriculaFP = new MatriculaFP((String) obj[1], pessoaFisica);
                    VinculoFP vinculoFP = new VinculoFP(matriculaFP, (String) obj[0]);
                    VinculoCreditoSalario vinculoCreditoSalario = new VinculoCreditoSalario();
                    vinculoCreditoSalario.setIdVinculo(((BigDecimal) obj[2]).longValue());
                    vinculoCreditoSalario.setMatricula((String) obj[1]);
                    vinculoCreditoSalario.setPessoa(pessoaFisica);
                    vinculoCreditoSalario.setValor(buscarValorDaPensao(vinculo, beneficioPensaoAlimenticia, creditoSalario));
                    vinculoCreditoSalario.setGrupo((String) obj[3]);
                    vinculoCreditoSalario.setRecursoFPId(((BigDecimal) obj[4]).longValue());
                    vinculoCreditoSalario.setUnidadeId(((BigDecimal) obj[5]).longValue());
                    vinculoCreditoSalario.setVinculoFP(vinculoFP);
                    vinculoCreditoSalario.setGrupoId(((BigDecimal) obj[6]).longValue());
                    if (beneficioPensaoAlimenticia.getContaCorrenteBancaria() != null) {
                        vinculoCreditoSalario.setConta(beneficioPensaoAlimenticia.getContaCorrenteBancaria());
                    } else {
                        vinculoCreditoSalario.setConta(pessoaFisicaFacade.buscarContaCorrenteDaPessoa(vinculo, beneficioPensaoAlimenticia.getPessoaFisicaBeneficiario(), creditoSalario));
                    }
                    retorno.add(vinculoCreditoSalario);
                }
            }
        }
        ordenarVinculoPensao(retorno);
        return retorno;
    }

    private void filtrarSomenteBeneficiarioManuais(CreditoSalario creditoSalario, List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticia) {
        if (!creditoSalario.isTipoArquivoServidor() && isPossuiBeneficiarios(creditoSalario)) {
            for (Iterator<BeneficioPensaoAlimenticia> iterator = beneficioPensaoAlimenticia.iterator(); iterator.hasNext(); ) {
                BeneficioPensaoAlimenticia objeto = iterator.next();

                if (naoPossuiNaLista(creditoSalario, objeto)) {
                    iterator.remove();
                }
            }
        }
    }

    private boolean naoPossuiNaLista(CreditoSalario selecionado, BeneficioPensaoAlimenticia v) {
        if (selecionado.getBeneficiarios() != null) {
            for (BeneficioPensaoAlimenticia beneficioPensaoAlimenticia : selecionado.getBeneficiarios()) {
                if (beneficioPensaoAlimenticia.getId().equals(v.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isPossuiBeneficiarios(CreditoSalario selecionado) {
        return selecionado.getBeneficiarios() != null && selecionado.getBeneficiarios().length > 0;
    }

    public static void ordenarVinculoPensao(List<VinculoCreditoSalario> vinculos) {
        Collections.sort(vinculos, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                VinculoCreditoSalario v1 = (VinculoCreditoSalario) o1;
                VinculoCreditoSalario v2 = (VinculoCreditoSalario) o2;
                return v1.getPessoa().getNome().compareTo(v2.getPessoa().getNome());
            }
        });
    }

    private BigDecimal buscarValorDaPensao(VinculoFP vinculo, BeneficioPensaoAlimenticia beneficioPensaoAlimenticia, CreditoSalario creditoSalario) {
        BigDecimal valorPensao;
        valorPensao = fichaFinanceiraFPFacade.buscarItemFichaFinanceiraFPPorVinculoFPEventoAndFolha(vinculo, beneficioPensaoAlimenticia.getEventoFP(), creditoSalario.getFolhaDePagamento());
        if (valorPensao == null) {
            valorPensao = BigDecimal.ZERO;
        }
        return valorPensao;
    }

    public List<Object[]> buscarFolhasParaComparacao(Integer ano1, Integer mes1, TipoFolhaDePagamento tipoFolhaDePagamento1, Integer ano2, Integer mes2, TipoFolhaDePagamento tipoFolhaDePagamento2, String filtroRecursoFP, Boolean semRetroacao, Boolean exclusivoFolha1, HierarquiaOrganizacional orgao) {
        String sql = "select vinculos.vinculo_id vinculoFP, " +
            "       (select coalesce(sum(item.valor), 0) " +
            "        from itemfichafinanceirafp item " +
            "                 inner join FICHAFINANCEIRAFP ficha on item.FICHAFINANCEIRAFP_ID = ficha.ID " +
            "                 inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                 inner join vinculofp v on ficha.VINCULOFP_ID = v.ID " +
            "                 inner join recursofp rec on ficha.RECURSOFP_ID = rec.ID " +
            "        where v.id = vinculos.vinculo_id " +
            "          and folha.ANO = :ano1 " +
            "          and folha.MES = :mes1 " +
            "          and folha.TIPOFOLHADEPAGAMENTO = :tipoFolha1 " +
            "          and item.TIPOEVENTOFP = :vantagem ";
        sql += filtroRecursoFP;
        if (semRetroacao) {
            sql += " and item.TIPOCALCULOFP = :normal ";
        }
        sql += ") as totalVantagem1, " +
            "       (select coalesce(sum(item.valor), 0) " +
            "        from itemfichafinanceirafp item " +
            "                 inner join FICHAFINANCEIRAFP ficha on item.FICHAFINANCEIRAFP_ID = ficha.ID " +
            "                 inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                 inner join vinculofp v on ficha.VINCULOFP_ID = v.ID " +
            "                 inner join recursofp rec on ficha.RECURSOFP_ID = rec.ID " +
            "        where v.id = vinculos.vinculo_id " +
            "          and folha.ANO = :ano1 " +
            "          and folha.MES = :mes1 " +
            "          and folha.TIPOFOLHADEPAGAMENTO = :tipoFolha1 " +
            "          and item.TIPOEVENTOFP = :desconto ";
        sql += filtroRecursoFP;
        if (semRetroacao) {
            sql += " and item.TIPOCALCULOFP = :normal ";
        }
        sql += ")    totalDesconto1, " +
            "       (select coalesce(sum(item.valor), 0) " +
            "        from itemfichafinanceirafp item " +
            "                 inner join FICHAFINANCEIRAFP ficha on item.FICHAFINANCEIRAFP_ID = ficha.ID " +
            "                 inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                 inner join vinculofp v on ficha.VINCULOFP_ID = v.ID " +
            "                 inner join recursofp rec on ficha.RECURSOFP_ID = rec.ID " +
            "        where v.id = vinculos.vinculo_id " +
            "          and folha.ANO = :ano2 " +
            "          and folha.MES = :mes2 " +
            "          and folha.TIPOFOLHADEPAGAMENTO = :tipoFolha2 " +
            "          and item.TIPOEVENTOFP = :vantagem";
        sql += filtroRecursoFP;
        if (semRetroacao) {
            sql += " and item.TIPOCALCULOFP = :normal ";
        }
        sql += ") as totalVantagem2, " +
            "       (select coalesce(sum(item.valor), 0) " +
            "        from itemfichafinanceirafp item " +
            "                 inner join FICHAFINANCEIRAFP ficha on item.FICHAFINANCEIRAFP_ID = ficha.ID " +
            "                 inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                 inner join vinculofp v on ficha.VINCULOFP_ID = v.ID " +
            "                 inner join recursofp rec on ficha.RECURSOFP_ID = rec.ID " +
            "        where v.id = vinculos.vinculo_id " +
            "          and folha.ANO = :ano2 " +
            "          and folha.MES = :mes2 " +
            "          and folha.TIPOFOLHADEPAGAMENTO = :tipoFolha2 " +
            "          and item.TIPOEVENTOFP = :desconto ";
        sql += filtroRecursoFP;
        if (semRetroacao) {
            sql += " and item.TIPOCALCULOFP = :normal ";
        }
        sql += ")    totalDesconto2 " +
            " from (select distinct v.id vinculo_id " +
            "      from vinculofp v " +
            "               inner join matriculafp mat on v.MATRICULAFP_ID = mat.ID " +
            "               inner join PESSOAFISICA pf on mat.PESSOA_ID = pf.ID " +
            "               inner join FICHAFINANCEIRAFP ficha on v.ID = ficha.VINCULOFP_ID " +
            "               inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "               inner join recursofp rec on ficha.RECURSOFP_ID = rec.ID ";
        String condicao = " where ";
        if (orgao != null) {
            sql += " inner join LOTACAOFUNCIONAL lot on v.ID = lot.VINCULOFP_ID " +
                " inner join VWHIERARQUIAADMINISTRATIVA vw on lot.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID ";
            condicao += " (";
        }
        sql += condicao + "(folha.ano = :ano1 and folha.mes = :mes1 and folha.TIPOFOLHADEPAGAMENTO  = :tipoFolha1 ";
        sql += filtroRecursoFP + ") ";
        if (exclusivoFolha1) {
            sql += " and v.id not in (select distinct vinculo.id " +
                "                         from FICHAFINANCEIRAFP fichaFinanceira " +
                "                                  inner join FOLHADEPAGAMENTO folhaPagamento on fichaFinanceira.FOLHADEPAGAMENTO_ID = folhaPagamento.ID " +
                "                                  inner join vinculofp vinculo on fichaFinanceira.VINCULOFP_ID = vinculo.ID " +
                "                         where folhaPagamento.ano = :ano2 and folhaPagamento.mes = :mes2 and folhaPagamento.TIPOFOLHADEPAGAMENTO = :tipoFolha2 ";
            sql += filtroRecursoFP + ")";
        } else {
            sql += "        or  (folha.ano = :ano2 and folha.mes = :mes2 and folha.TIPOFOLHADEPAGAMENTO  = :tipoFolha2 ";
            sql += filtroRecursoFP + ")";
        }
        if (orgao != null) {
            sql += ") and vw.codigo like :unidade " +
                " and :dataAtual between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, :dataAtual) " +
                " and folha.CALCULADAEM between lot.INICIOVIGENCIA and coalesce(lot.FINALVIGENCIA,folha.CALCULADAEM) ";
        }
        sql += "      order by v.id) vinculos ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("ano1", ano1);
        consulta.setParameter("mes1", Mes.getMesToInt(mes1).getNumeroMesIniciandoEmZero());
        consulta.setParameter("tipoFolha1", tipoFolhaDePagamento1.name());
        consulta.setParameter("ano2", ano2);
        consulta.setParameter("mes2", Mes.getMesToInt(mes2).getNumeroMesIniciandoEmZero());
        consulta.setParameter("tipoFolha2", tipoFolhaDePagamento2.name());
        consulta.setParameter("vantagem", TipoEventoFP.VANTAGEM.name());
        consulta.setParameter("desconto", TipoEventoFP.DESCONTO.name());
        if (semRetroacao) {
            consulta.setParameter("normal", TipoCalculoFP.NORMAL.name());
        }
        if (orgao != null) {
            consulta.setParameter("unidade", orgao.getCodigoSemZerosFinais() + "%");
            consulta.setParameter("dataAtual", new Date());
        }
        List<Object[]> retorno = consulta.getResultList();
        return retorno;
    }

    public ServidorPortal recuperarServidorPortalNaoEnviado(VinculoFP vinculo, Integer mes, Integer ano, TipoFolhaDePagamento tipo) {
        String hql = " select serv from ServidorPortal serv " +
            "   inner join serv.vinculo v " +
            "   inner join serv.exercicio ex " +
            "   inner join v.matriculaFP mat " +
            "     where mat.matricula = :matricula " +
            "       and serv.mes = :mes " +
            "       and ex.ano = :exercicio " +
            "       and serv.tipoFolhaDePagamento = :tipo " +
            "       and serv.situacao = :situacao " +
            "     order by serv.id desc ";
        Query q = em.createQuery(hql);
        q.setParameter("matricula", vinculo.getMatriculaFP().getMatricula());
        q.setParameter("mes", mes);
        q.setParameter("exercicio", ano);
        q.setParameter("tipo", tipo);
        q.setParameter("situacao", PortalTransparenciaSituacao.NAO_PUBLICADO);
        q.setMaxResults(1);
        try {
            return (ServidorPortal) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void removerEntidadePortalTransparencia(ServidorPortal servidorPortal) {
        String sql = " delete EntidadePortalTransparencia " +
            " where id = :idServidor " +
            "   and tipo = :tipo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idServidor", servidorPortal.getId());
        q.setParameter("tipo", TipoObjetoPortalTransparencia.SERVIDOR.name());
        q.executeUpdate();
        removerServidorPortal(servidorPortal);
    }

    public void removerServidorPortal(ServidorPortal servidorPortal) {
        String sql = " delete servidorPortal " +
            " where id = :idServidor ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idServidor", servidorPortal.getId());
        q.executeUpdate();
    }
}
