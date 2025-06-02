package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha;
import br.com.webpublico.entidadesauxiliares.ItensResultado;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.entidadesauxiliares.ObjetoResultado;
import br.com.webpublico.entidadesauxiliares.rh.ItemDirfCorrecao;
import br.com.webpublico.entidadesauxiliares.rh.ItemResultadoLancamento;
import br.com.webpublico.entidadesauxiliares.rh.RelatorioRH;
import br.com.webpublico.entidadesauxiliares.rh.ResultadoLancamento;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.ExecutaJDBC;
import br.com.webpublico.util.RegistroDB;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 24/01/14
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ComparadorDeFollhasFacade extends AbstractFacade<FolhaDePagamento> {

    private static final Logger logger = LoggerFactory.getLogger(ComparadorDeFollhasFacade.class);
    private static final int CODIGO_RBPREV = 21;
    private static final String RBPREV = "01.21.";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private ComparadorDeFolhasFacadeAsync comparadorDeFolhasFacadeAsync;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    /**
     * Deve ser executado no construtor da subclasse para indicar qual é a
     * classe da entidade
     */
    public ComparadorDeFollhasFacade() {
        super(FolhaDePagamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ComparadorWeb recuperarCamparadorWeb(Mes mes, Integer ano) {
        if (mes == null || ano == null) return null;

        System.out.println("opa");
        Query q = em.createQuery("from ComparadorWeb where mes = :mes and ano = :ano");
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        if (q.getResultList().isEmpty()) {
            ComparadorWeb c = new ComparadorWeb();
            c.setAno(ano);
            c.setMes(mes);
            em.persist(c);
            return c;
        }
        ComparadorWeb comparadorWeb = (ComparadorWeb) q.getResultList().get(0);
        comparadorWeb.getRejeitados().size();
        return comparadorWeb;
    }

    public void salvarCamparadorWeb(ComparadorWeb comparadorWeb) {
        try {
            if (comparadorWeb.getMes() != null && comparadorWeb.getAno() != null) {
                em.merge(comparadorWeb);
            }
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    public void salvarCamparadorFolha(ComparadorFolha comparador) {
        try {
            if (comparador.getMes() != null && comparador.getAno() != null) {
                em.merge(comparador);
            }
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<ResultadoLancamento> iniciarComparacaoLancamento(ObjetoPesquisa objetoPesquisa) {
        List<ResultadoLancamento> resultados = new ArrayList<>();
        Set<RegistroDB> registrosDB = Sets.newHashSet();
        Map<VinculoFP, List<LancamentoFP>> lancamentosTurmalina = Maps.newHashMap();
        try {
            registrosDB = new HashSet<>(buscarLancamentosPorMes(objetoPesquisa));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lancamentosTurmalina = criarLancamentosTurmalina(registrosDB);
        logger.debug("foram criados os lançamentos virtual no turmalina");
        for (Map.Entry<VinculoFP, List<LancamentoFP>> lancamentos : lancamentosTurmalina.entrySet()) {
            List<LancamentoFP> lancamentosWeb = lancamentoFPFacade.listaLancamentos(lancamentos.getKey(), objetoPesquisa.getAno(), objetoPesquisa.getMes());
            if (!lancamentosWeb.isEmpty()) {
                processarLancamentos(objetoPesquisa, lancamentos.getKey(), resultados, lancamentos.getValue(), lancamentosWeb);
            } else {
                logger.debug("Nenhum lançamento encontrato no webpublico para o vinculo {}", lancamentos.getKey());
            }
        }

        return resultados;
    }

    private void processarLancamentos(ObjetoPesquisa objetoPesquisa, VinculoFP vinculoFp, List<ResultadoLancamento> resultados, List<LancamentoFP> value, List<LancamentoFP> lancamentosWeb) {
        ResultadoLancamento resultado = new ResultadoLancamento();
        List<EventoFP> removidos = Lists.newArrayList();
        for (Iterator<LancamentoFP> itTurmalina = value.iterator(); itTurmalina.hasNext(); ) {
            LancamentoFP lancamentoFPTurmalina = itTurmalina.next();
            boolean encontrado = false;
            if (isLancamentoInvalido(lancamentoFPTurmalina, objetoPesquisa)) {
                itTurmalina.remove();
                continue;
            }
            List<EventoFP> eventos = Lists.newArrayList(lancamentoFPTurmalina.getEventoFP());
            for (Iterator<LancamentoFP> itWebpublico = lancamentosWeb.iterator(); itWebpublico.hasNext(); ) {
                LancamentoFP lancamentoFPWeb = itWebpublico.next();
                if (isLancamentoInvalido(lancamentoFPWeb, objetoPesquisa)) {
                    itWebpublico.remove();
                    continue;
                }
                if (eventos.contains(lancamentoFPWeb.getEventoFP())) {
                    itWebpublico.remove();
                    encontrado = true;
                }
            }

            if (encontrado) {
                if (lancamentoFPTurmalina.getEventoFP() != null) {
                    removidos.add(lancamentoFPTurmalina.getEventoFP());
                }
                itTurmalina.remove();
            }
        }
        removerDuplicados(value, removidos);

        if (!lancamentosWeb.isEmpty()) {
            logger.debug("Sobraram lançamento do webpublico");
        }
        if (!value.isEmpty()) {
            logger.debug("Sobraram lançamento do turmalina");
        }

        if (!lancamentosWeb.isEmpty() || !value.isEmpty()) {
            resultado.setVinculoFP(vinculoFp);

            for (LancamentoFP lancamentoFP : lancamentosWeb) {
                /*logger.debug("Adicionando lançamento fp do webpublico");*/
                adicionarLancamento(resultado, true, lancamentoFP);
            }
            for (LancamentoFP lancamentoTurmalina : value) {
                /*logger.debug("Adicionando lançamento fp do turmalina");*/
                adicionarLancamento(resultado, false, lancamentoTurmalina);
            }
            if (!resultado.getItensResultados().isEmpty()) {
                resultados.add(resultado);
            }
        }
    }

    private boolean isLancamentoInvalido(LancamentoFP lancamentoFPTurmalina, ObjetoPesquisa objetoPesquisa) {
        if (objetoPesquisa != null && lancamentoFPTurmalina != null) {
            if (lancamentoFPTurmalina.getEventoFP() == null) {
                return true;
            }
            if (objetoPesquisa.getEventosFPList() != null && objetoPesquisa.getEventosFPList().contains(lancamentoFPTurmalina.getEventoFP())) {
                return true;
            }
        }
        return false;
    }

    private void removerDuplicados(List<LancamentoFP> value, List<EventoFP> removidos) {
        for (Iterator<LancamentoFP> itTurmalina = value.iterator(); itTurmalina.hasNext(); ) {
            LancamentoFP lancamentoFPTurmalina = itTurmalina.next();
            if (lancamentoFPTurmalina.getEventoFP() != null) {
                if (removidos.contains(lancamentoFPTurmalina.getEventoFP())) {
                    itTurmalina.remove();
                }
            }
        }
    }

    private void adicionarLancamento(ResultadoLancamento resultado, boolean web, LancamentoFP lancamentoFP) {
        ItemResultadoLancamento itens = criarItensResultados(lancamentoFP, web);
        resultado.getItensResultados().add(itens);

    }


    private ItemResultadoLancamento criarItensResultados(LancamentoFP lancamentoFP, boolean web) {
        ItemResultadoLancamento item = new ItemResultadoLancamento();
        if (web) {
            item.setLancamentoFPWeb(lancamentoFP);
        } else {
            item.setLancamentoFPTurmalina(lancamentoFP);
        }
        return item;
    }

    @TransactionTimeout(value = 30, unit = TimeUnit.MINUTES)
    private Map<VinculoFP, List<LancamentoFP>> criarLancamentosTurmalina(Set<RegistroDB> registrosDB) {
        Map<VinculoFP, List<LancamentoFP>> lancamentosTurmalina = Maps.newHashMap();
        int contator = 0;
        for (RegistroDB registroDB : registrosDB) {
            contator++;
            String matricula = ((BigDecimal) registroDB.getCampoByIndex(0).getValor()).toString();
            Integer contrato = ((BigDecimal) registroDB.getCampoByIndex(1).getValor()).intValue();
            String codigoEvento = ((BigDecimal) registroDB.getCampoByIndex(3).getValor()).toString();
            BigDecimal quantidade = (BigDecimal) registroDB.getCampoByIndex(4).getValor();
            BigDecimal percentual = (BigDecimal) registroDB.getCampoByIndex(5).getValor();
            BigDecimal valor = (BigDecimal) registroDB.getCampoByIndex(6).getValor();
            Date mesAnoIncial = (Date) registroDB.getCampoByIndex(7).getValor();
            Date mesAnoFinal = (Date) registroDB.getCampoByIndex(8).getValor();
            VinculoFP vinculoFP = vinculoFPFacade.recuperarVinculoPorMatriculaENumero(matricula, contrato + "");

            if (contator % 100 == 0) {
                /*logger.debug("Vinculo {}", vinculoFP);
                logger.debug("Evento {}", codigoEvento);
                logger.debug("percentual {}", percentual);
                logger.debug("Mes Ano Inicial {} e Mês Ano Final {}", mesAnoIncial, mesAnoFinal);
                logger.debug("Valor {}", valor);*/
                logger.debug("Total Processados {}", contator);
            }

            if (vinculoFP == null) {
                logger.debug("Matrícula " + matricula + " e número " + contrato + " não encontrado.");
                continue;
            }
            criarLancamentoMock(lancamentosTurmalina, vinculoFP, codigoEvento, quantidade, percentual, valor, mesAnoIncial, mesAnoFinal);
            //break;
        }
        return lancamentosTurmalina;
    }

    private void criarLancamentoMock(Map<VinculoFP, List<LancamentoFP>> lancamentosTurmalina, VinculoFP vinculoFP, String codigoEvento, BigDecimal quantidade, BigDecimal percentual, BigDecimal valor, Date mesAnoIncial, Date mesAnoFinal) {
        LancamentoFP lancamento = criarLancamento(vinculoFP, codigoEvento, quantidade, percentual, valor, mesAnoIncial, mesAnoFinal);
        if (lancamentosTurmalina.containsKey(vinculoFP)) {
            lancamentosTurmalina.get(vinculoFP).add(lancamento);
        } else {
            lancamentosTurmalina.put(vinculoFP, Lists.newArrayList(lancamento));
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private LancamentoFP criarLancamento(VinculoFP vinculoFP, String codigoEvento, BigDecimal quantidade, BigDecimal percentual, BigDecimal valor, Date mesAnoIncial, Date mesAnoFinal) {
        LancamentoFP lancamento = new LancamentoFP();
        lancamento.setVinculoFP(vinculoFP);
        lancamento.setEventoFP(eventoFPFacade.recuperaPorCodigo(codigoEvento));
        lancamento.setQuantificacao(valor == null ? percentual : valor);
        lancamento.setTipoLancamentoFP(valor == null ? TipoLancamentoFP.REFERENCIA : TipoLancamentoFP.VALOR);
        lancamento.setMesAnoInicial(mesAnoIncial);
        lancamento.setMesAnoFinal(mesAnoFinal);
        return lancamento;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<ObjetoResultado> iniciarComparacao(ObjetoPesquisa objetoPesquisa, List<VinculoFP> rejeitados) {
        String clausula = getClausulaTurma(objetoPesquisa.getEventosFPList());
        String clausulaWeb = getClausulaWeb(objetoPesquisa.getEventosFPList());
        Set<EventoFP> eventosComDiferenca = new LinkedHashSet<>();
        logger.debug("Iniciando a busca dos vinculos calculados..." + objetoPesquisa);
        List<VinculoFP> vinculos = new LinkedList<>();
        if (objetoPesquisa.getHierarquiaOrganizacional() != null) {
            vinculos = contratoFPFacade.listarVinculosPorHierarquiaSemCedenciaEstagioOuAfastamento(Lists.newArrayList(objetoPesquisa.getHierarquiaOrganizacional()), Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno(), false);
        } else if (!objetoPesquisa.getTipoFolhaDePagamentoWeb().equals(TipoFolhaDePagamento.RESCISAO)) {
            vinculos = folhaDePagamentoFacade.recuperarMatriculasSemCedenciaEstagioOuAfastamento(Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno());
        } else {
            vinculos = folhaDePagamentoFacade.recuperaVinculosPorTipoFolhaMesEAno(Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno(), objetoPesquisa.getTipoFolhaDePagamentoWeb());
        }

        logger.debug("Vinculos recu  perados com sucesso, total de " + vinculos.size());
//        List<RegistroDB> matriculasTurmalina = new ArrayList<>();
//        try {
//            matriculasTurmalina = recuperarTodasMatriculasFolhaTurmalina(objetoPesquisa, getClausulaTurma(objetoPesquisa.getEventosFPList()));
//        } catch (SQLException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        logger.debug("total de registros calculados do turmalina = " + matriculasTurmalina.size());
//        for (RegistroDB reg : matriculasTurmalina) {
//            BigDecimal matricula = (BigDecimal) reg.getCampoByIndex(0).getValor();
//            BigDecimal numero = (BigDecimal) reg.getCampoByIndex(1).getValor();
//            VinculoFP v = vinculoFPFacade.recuperarVinculoPorMatriculaENumero(matricula.intValue() + "", numero.intValue() + "");
//            if (v != null) {
//                vinculos.add(v);
//            }
//        }
        Set<VinculoFP> lista2 = new LinkedHashSet<>();
        for (VinculoFP v : vinculos) {
            if (rejeitados.contains(v)) {
                continue;
            }
            lista2.add(v);
        }
        List<ObjetoResultado> resultados = new LinkedList<>();
        int contador = 0;
        logger.debug("total de vinculos contabilizados: " + lista2.size());
        for (VinculoFP vinculo : lista2) {
            objetoPesquisa.setMatricula(Integer.parseInt(vinculo.getMatriculaFP().getMatricula()));
            objetoPesquisa.setNumero(Integer.valueOf(vinculo.getNumero()));
            List<RegistroDB> fichaFinanceiras = null;
            try {
                fichaFinanceiras = buscaFichaFinanceiraTurmalina(objetoPesquisa, clausula);
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            List<EventoTotalItemFicha> itensFichaWebPublico = new LinkedList<>();
            itensFichaWebPublico = fichaFinanceiraFPFacade.totalEventosItemFichaFinanceiraPorTipoFolhaEventos(vinculo.getId(), objetoPesquisa.getMes(), objetoPesquisa.getAno(), objetoPesquisa.getTipoFolhaDePagamentoWeb(), clausulaWeb);
            ObjetoResultado objetoResultado = new ObjetoResultado();
            objetoResultado.setVinculoFP(vinculo);
//            if (itensFichaWebPublico.isEmpty()) {
////                objetoResultado.setFichaWebNaoEncontrada(true);
//                continue;
//            }
            for (Iterator itWeb = itensFichaWebPublico.iterator(); itWeb.hasNext(); ) {
                EventoTotalItemFicha item = (EventoTotalItemFicha) itWeb.next();
                if (item.getTipoEventoFP().equals(TipoEventoFP.INFORMATIVO)) {
                    itWeb.remove();
                    continue;
                }
                for (Iterator iterator = fichaFinanceiras.iterator(); iterator.hasNext(); ) {
                    RegistroDB registro = (RegistroDB) iterator.next();
                    String codigoVerbaWeb = item.getEvento().getCodigo();
                    String codigoVerbaTumalina = ((BigDecimal) registro.getCampoByIndex(0).getValor()).toString();
                    String tipoEvento = registro.getCampoByIndex(1).getValor().toString();
                    if (codigoVerbaWeb.equals(codigoVerbaTumalina) && isTipoEventoIguais(tipoEvento, item.getTipoEventoFP())) {
                        BigDecimal valorWeb = item.getTotal().setScale(2);
                        BigDecimal valorTurmalina = new BigDecimal(registro.getCampoByIndex(2).getValor() + "");
                        BigDecimal valorAux = new BigDecimal("0.01");

//                        //System.out.println("Valores web: " + valorWeb + " valor turmalina: " + valorTurmalina);
                        if (valorWeb.compareTo(valorTurmalina) == 0 || (valorWeb.add(valorAux)).compareTo(valorTurmalina) == 0 || (valorTurmalina.add(valorAux)).compareTo(valorWeb) == 0) {
                            itWeb.remove();
                            iterator.remove();
                        } else {
//                            //System.out.println("diferença... Valores web: " + valorWeb + " valor turmalina: " + valorTurmalina);
//                            ObjetoResultado or = new ObjetoResultado();
                            adicionaDiferenca(objetoResultado, item.getEvento(), item.getTipoEventoFP(), valorWeb, valorTurmalina);
                            eventosComDiferenca.add(item.getEvento());
                            iterator.remove();
                            itWeb.remove();
                        }
                    }
                }

            }
            List<EventoFP> eventoFPs = new LinkedList<>();
            if (!fichaFinanceiras.isEmpty()) {
                for (Iterator iterator = fichaFinanceiras.iterator(); iterator.hasNext(); ) {

                    RegistroDB registro = (RegistroDB) iterator.next();
                    String codigoVerbaTumalina = ((BigDecimal) registro.getCampoByIndex(0).getValor()).toString();
                    String tipoEventoTurmalina = registro.getCampoByIndex(1).getValor() + "";
                    BigDecimal valorTurmalina = new BigDecimal(registro.getCampoByIndex(2).getValor() + "");
                    Integer codigo = Integer.valueOf(codigoVerbaTumalina);
                    try {
                        EventoFP eventoFP = eventoFPFacade.recuperaEvento(codigo + "", TipoExecucaoEP.FOLHA);
                        eventoFPs.add(eventoFP);
                        eventosComDiferenca.add(eventoFP);
                        if (eventoFP != null) {
                            adicionaDiferenca(objetoResultado, eventoFP, tipoEventoTurmalina.equals("C") ? TipoEventoFP.VANTAGEM : TipoEventoFP.DESCONTO, BigDecimal.ZERO, valorTurmalina);
                        } else {
                            //System.out.println("evento null " + codigo);
                        }
                        iterator.remove();
                    } catch (Exception np) {
                        logger.debug("evento não encontrado no webpublico... " + codigoVerbaTumalina);
                        np.printStackTrace();
                    }


                }
            }
            if (!itensFichaWebPublico.isEmpty()) {
                for (Iterator itWeb = itensFichaWebPublico.iterator(); itWeb.hasNext(); ) {
                    EventoTotalItemFicha registro = (EventoTotalItemFicha) itWeb.next();
                    if (!eventoFPs.contains(registro.getEvento())) {
                        adicionaDiferenca(objetoResultado, registro.getEvento(), registro.getTipoEventoFP(), registro.getTotal(), BigDecimal.ZERO);
                        eventosComDiferenca.add(registro.getEvento());
                        itWeb.remove();
                    }
                }
            }

            contador++;
            if (contador % 50 == 0) {
                logger.debug("total de vinculos processados: " + contador);
            }
            objetoResultado.setHierarquiaOrganizacional(lotacaoFuncionalFacade.recuperarHieraquiaPelaLotacaoFuncional(vinculo, new Date()));
            Collections.sort(objetoResultado.getItensResultados());
            resultados.add(objetoResultado);
        }
        for (Iterator it = resultados.iterator(); it.hasNext(); ) {
            ObjetoResultado or = (ObjetoResultado) it.next();
            if (or.getItensResultados().isEmpty() && !or.isFichaWebNaoEncontrada()) {
                it.remove();
            }
        }

        Map<EventoFP, Integer> totais = new LinkedHashMap<>();

        for (ObjetoResultado resultado : resultados) {
            for (ItensResultado itensResultado : resultado.getItensResultados()) {
                if (totais.containsKey(itensResultado.getEvento())) {
                    Integer total = totais.get(itensResultado.getEvento());
                    total++;
                    totais.put(itensResultado.getEvento(), total);
                } else {
                    totais.put(itensResultado.getEvento(), 1);
                }
            }
        }

        for (Map.Entry<EventoFP, Integer> entry : totais.entrySet()) {
            logger.debug(entry.getKey() + " = " + entry.getValue());
        }

        logger.debug("Total de Eventos com Diferença: " + eventosComDiferenca.size());
        logger.debug("" + eventosComDiferenca);

        logger.debug("finalizando a busca, total: " + resultados.size());
        return resultados;
    }

    private boolean isTipoEventoIguais(String tipoEvento, TipoEventoFP tipoEventoFP) {
        if ("C".equals(tipoEvento) && tipoEventoFP.equals(TipoEventoFP.VANTAGEM)) return true;
        if ("D".equals(tipoEvento) && tipoEventoFP.equals(TipoEventoFP.DESCONTO)) return true;
        return false;
    }

    private String getClausulaTurma(List<EventoFP> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            return "";
        }
        String clausula = " and itemFicha.cdgVerba in (";
        for (EventoFP eventoFP : eventos) {
            clausula += eventoFP.getCodigo() + ",";
        }
        clausula = clausula.substring(0, clausula.length() - 1);
        clausula += ") ";
        return clausula;
    }

    private String getClausulaWeb(List<EventoFP> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            return "";
        }
        String clausula = " and evento.codigo in (";
        for (EventoFP eventoFP : eventos) {
            clausula += eventoFP.getCodigo() + ",";
        }
        clausula = clausula.substring(0, clausula.length() - 1);
        clausula += ") ";
        return clausula;
    }

    private void adicionaDiferenca(ObjetoResultado objetoResultado, EventoFP evento, TipoEventoFP tipo, BigDecimal valorWeb, BigDecimal valorTurmalina) {
        ItensResultado ir = new ItensResultado();
        ir.setEvento(evento);
        ir.setValorweb(valorWeb);
        ir.setTipoEventoFP(tipo);
        ir.setOrdenacao(evento.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM) ? Integer.valueOf("1" + evento.getCodigo()) : (evento.getTipoEventoFP().equals(TipoEventoFP.DESCONTO) ? Integer.valueOf("2" + evento.getCodigo()) : Integer.valueOf("3" + evento.getCodigo())));
        ir.setValorTurma(valorTurmalina);
        objetoResultado.getItensResultados().add(ir);
    }

    public List<RegistroDB> buscaFichaFinanceiraTurmalina(ObjetoPesquisa objetoPesquisa, String clausula) throws SQLException {
        Connection con = null;
        try {
            con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            con.getClientInfo();
            List<RegistroDB> retorno = ExecutaJDBC.consulta(con
                , "SELECT DISTINCT \n" +
                    "  itemFicha.cdgVerba as codigoEvento,\n" +
                    "  verbas.creDebVBA as tipoEvento,\n" +
                    "  sum(case when itemFicha.credDebFA = 'C' then itemFicha.valorFa else  -itemFicha.valorFa end) as valor,\n" +
                    "  sum(itemFicha.VlrvbasFa) as valorBaseCalculoFP\n" +
                    " FROM\n" +
                    "    turmalina.arh203 itemFicha\n" +
                    "    inner JOIN turmalina.arh025 verbas ON\n" +
                    "      verbas.CdgVerba = itemFicha.cdgVerba  \n" +
                    "      where itemFicha.credDebFA in ('C','D') and\n" +
                    "       itemFicha.anoFa = :ano and itemFicha.mesFa = :mes and \n" +
                    "       itemFicha.nmrLnc in (" + objetoPesquisa.getTipoFolhaDePagamento() + ") and\n" +
                    "       itemFicha.cdgMatSrv = :matricula and\n" +                                             //and itemFicha.cdgVerba = 101
                    "       itemFicha.nmrcntsrv = :numero and itemFicha.cdgVerba not in ('904','905','906','907') " + clausula + " group by itemFicha.cdgVerba,verbas.creDebVBA order by itemFicha.cdgVerba ",
                objetoPesquisa.getAno(), objetoPesquisa.getMes(), objetoPesquisa.getMatricula(), objetoPesquisa.getNumero());
            if (con != null) {
                con.close();
            }
            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                con.close();
            }
            return null;
        }
    }


    public List<RegistroDB> buscarLancamentosPorMes(ObjetoPesquisa objetoPesquisa) throws SQLException {
        Connection con = null;
        try {
            con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            con.getClientInfo();
            List<RegistroDB> retorno = ExecutaJDBC.consulta(con
                , "select * from (\n" +
                    "select \n" +
                    "    lancamento.cdgMatSrv as matricula, \n" +
                    "    lancamento.NMRCNTSRV as contrato, \n" +
                    "    lancamento.NMRLNCSRV as tipoFolha, \n" +
                    "    lancamento.cdgVerba as evento, \n" +
                    "    lancamento.QTDEHORA as quantidade,\n" +
                    "    lancamento.perc as percentual,\n" +
                    "    lancamento.vlr as valor,\n" +
                    "    to_date(to_char(to_date(lancamento.dtaIniLnc,'YYYYMMDD'),'YYYYMM'), 'YYYYMM') as mesAnoInicial, \n" +
                    "    to_date(lancamento.DTAFIMLNC,'YYYYMMDD') as mesAnoFinal \n" +
                    "    \n" +
                    "from turmalina.arh206 lancamento \n" +
                    "where \n" +
/*                    " lancamento.cdgMatSrv = 2518 and lancamento.nmrCntSrv = 1 and \n" +*/
                    " :dataReferencia between substr(lancamento.DTAINILNC,0,6) and substr(lancamento.DTAFIMLNC,0,6)\n" +
                    " and substr(lancamento.dtaCad010,0,6) <= :dataReferencia " +
                    "and nullif(lancamento.dtaIniLnc, '00000000') IS NOT null and nullif(lancamento.dtafimLnc, '00000000') IS NOT null\n" +
                    "and lancamento.NMRLNCSRV = 1) dados order by matricula, contrato, evento",
                getMesAnoConcatenado(objetoPesquisa), getMesAnoConcatenado(objetoPesquisa));
            if (con != null) {
                con.close();
            }
            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                con.close();
            }
            return null;
        }
    }

    private String getMesAnoConcatenado(ObjetoPesquisa objetoPesquisa) {
        YearMonth yearMonth = new YearMonth(objetoPesquisa.getAno(), objetoPesquisa.getMes());
        return yearMonth.toString("yyyyMM");
    }


    public List<RegistroDB> buscaFichaFinanceiraTurmalinaTodosCampos(ObjetoPesquisa objetoPesquisa) throws SQLException {
        Connection con = null;
        try {
            con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            con.getClientInfo();
            List<RegistroDB> retorno = ExecutaJDBC.consulta(con
                , "SELECT itemFicha.* " +
                    " FROM\n" +
                    "    turmalina.arh203 itemFicha\n" +
                    "    inner JOIN turmalina.arh025 verbas ON\n" +
                    "      verbas.CdgVerba = itemFicha.cdgVerba  \n" +
                    "      where \n" +
                    "       itemFicha.anoFa = :ano and itemFicha.mesFa = :mes and \n" +
                    "       itemFicha.nmrLnc in (" + objetoPesquisa.getTipoFolhaDePagamento() + ") and\n" +
                    "       itemFicha.cdgMatSrv = :matricula and\n" +                                             //and itemFicha.cdgVerba = 101
                    "       itemFicha.nmrcntsrv = :numero order by itemFicha.cdgVerba ",
                objetoPesquisa.getAno(), objetoPesquisa.getMes(), objetoPesquisa.getMatricula(), objetoPesquisa.getNumero());
            if (con != null) {
                con.close();
            }
            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                con.close();
            }
            return null;
        }
    }


    public List<RegistroDB> buscarLancamentosPorDataCadastro(Date dataCadastro) {
        Connection con = null;
        try {
            con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            con.getClientInfo();
            StringBuilder consulta = getConsultaLancamento();

            List<RegistroDB> retorno = ExecutaJDBC.consulta(con, consulta.toString(), Util.dateToString(dataCadastro));
            if (con != null) {
                con.close();
            }
            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.error("erro: ", e);
                }
            }
            return null;
        }
    }


    public List<RegistroDB> buscaFichaFinanceiraTurmalinaTodosCamposPorVerbas(ObjetoPesquisa objetoPesquisa) throws SQLException {
        Connection con = null;
        try {
            con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            con.getClientInfo();
            List<RegistroDB> retorno = ExecutaJDBC.consulta(con
                , "SELECT itemFicha.* " +
                    " FROM\n" +
                    "    turmalina.arh203 itemFicha\n" +
                    "    inner JOIN turmalina.arh025 verbas ON\n" +
                    "      verbas.CdgVerba = itemFicha.cdgVerba  \n" +
                    "      where \n" +
                    "       itemFicha.anoFa = :ano and itemFicha.mesFa = :mes and \n" +
                    "       itemFicha.nmrLnc in (" + objetoPesquisa.getTipoFolhaDePagamento() + ") and\n" +
                    "       itemFicha.cdgMatSrv = :matricula \n" +                                             //and itemFicha.cdgVerba = 101
                    getVerbas(objetoPesquisa.getEventosFPList()) +
                    "      and itemFicha.nmrcntsrv = :numero order by itemFicha.cdgVerba ",
                objetoPesquisa.getAno(), objetoPesquisa.getMes(), objetoPesquisa.getMatricula(), objetoPesquisa.getNumero());
            if (con != null) {
                con.close();
            }
            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                con.close();
            }
            return null;
        }
    }

    String getVerbas(List<EventoFP> eventoFPs) {
        String eventosRetorno = " and itemFicha.cdgVerba in('";
        if (eventoFPs != null && !eventoFPs.isEmpty()) {
            for (EventoFP eventoFP : eventoFPs) {
                eventosRetorno += eventoFP.getCodigo() + ",";
            }
            eventosRetorno = eventosRetorno.substring(0, eventosRetorno.length() - 1);
            eventosRetorno += "') ";
        } else {
            return " ";
        }
        return eventosRetorno;
    }

    public List<RegistroDB> recuperarTodasMatriculasFolhaTurmalina(ObjetoPesquisa objetoPesquisa, String clausula) throws SQLException {
        Connection con = null;
        try {
            con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            con.getClientInfo();
            List<RegistroDB> retorno = ExecutaJDBC.consulta(con
                , "SELECT \n" +
                    "  itemFicha.cdgmatsrv as matricula,\n" +
                    "   itemFicha.nmrcntsrv as numero \n" +

                    " FROM\n" +
                    "    turmalina.arh203 itemFicha \n" +
                    "      where itemFicha.credDebFA in ('C','D') and\n" +
                    "       itemFicha.anoFa = :ano and itemFicha.mesFa = :mes and \n" +
                    "       itemFicha.nmrLnc = :tipo \n" +
                    //and itemFicha.cdgVerba = 101
                    "       and itemFicha.cdgVerba not in ('904','905','906','907') " + clausula + " group by itemFicha.cdgmatsrv, itemficha.nmrcntsrv ",
                objetoPesquisa.getAno(), objetoPesquisa.getMes(), objetoPesquisa.getTipoFolhaDePagamento());
            if (con != null) {
                con.close();
            }
            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                con.close();
            }
            return null;
        }
    }

    public List<RegistroDB> buscaUlimaFichaFinanceiraTurmalinaApenasCredito(ObjetoPesquisa objetoPesquisa) throws SQLException {
        Connection con = null;
        try {
            con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            con.getClientInfo();
            List<RegistroDB> retorno = ExecutaJDBC.consulta(con
                , "SELECT DISTINCT \n" +
                    "  itemFicha.cdgVerba as codigoEvento,\n" +
                    "  sum(itemFicha.valorFa) as valor,\n" +
                    "  sum(itemFicha.VlrvbasFa) as valorBaseCalculoFP\n" +
                    " FROM\n" +
                    "    turmalina.arh203 itemFicha\n" +
                    "      where itemFicha.credDebFA in ('C') and\n" +
                    "       itemFicha.anoFa = (select max(anoFa) from turmalina.arh203 where cdgmatsrv = :matricula and nmrcntsrv = :numero )" +
                    "       and itemFicha.mesFa = (select max(mesFa) from turmalina.arh203 where cdgmatsrv = :matricula and nmrcntsrv = :numero and anoFa = itemFicha.anoFa ) and \n" +
                    "       itemFicha.nmrLnc = :tipo and\n" +
                    "       itemFicha.cdgMatSrv = :matricula and\n" +                                             //and itemFicha.cdgVerba = 101
                    "       itemFicha.nmrcntsrv = :numero and itemFicha.cdgVerba not in ('904','905','906','907') group by " +
                    "       itemFicha.cdgVerba order by itemFicha.cdgVerba ",
                objetoPesquisa.getMatricula(), objetoPesquisa.getNumero(), objetoPesquisa.getMatricula(), objetoPesquisa.getNumero(), objetoPesquisa.getTipoFolhaDePagamento(), objetoPesquisa.getMatricula(), objetoPesquisa.getNumero());
            if (con != null) {
                con.close();
            }
            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                con.close();
            }
            return null;
        }
    }

    public List<RegistroDB> buscarServidoresParaSextaParte() throws SQLException {
        Connection con = null;
        try {
            con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            con.getClientInfo();
            List<RegistroDB> retorno = ExecutaJDBC.consulta(con
                , "SELECT DISTINCT \n" +
                    "  ats.cdgmatsrv as matricula,\n" +
                    "  ats.nmrcntsrv as numero," +
                    "  to_date(ats.dtaivigats,'yyyymmdd') as inicioVigencia, \n" +
                    "  case when ats.dtafvigats = '00000000' then null else to_date(ats.dtafvigats,'yyyymmdd') end as finalVigencia \n" +

                    " FROM \n" +
                    "    turmalina.ARH132 ats\n" +
                    "      where ats.usrcadats1 <> 'CONVERSAO' and ats.CODTPOATS in (2,3) \n");
            if (con != null) {
                con.close();
            }
            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                con.close();
            }
            return null;
        }
    }

    public List<RegistroDB> buscaMatriculaENumeroContratoParaRetroacaoComMaisDeUmMes(ObjetoPesquisa objetoPesquisa) throws SQLException {
        Connection con = null;
        try {
            con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            con.getClientInfo();
            List<RegistroDB> retorno = ExecutaJDBC.consulta(con
                , "SELECT DISTINCT \n" +
                    "  itemFicha.cdgmatsrv as matricula,\n" +
                    "  itemFicha.nmrcntsrv as numero  FROM\n" +
                    "    turmalina.arh203 itemFicha\n" +
                    "      where itemFicha.credDebFA in ('C') and\n" +
                    "       itemFicha.anoFa = :ano" +
                    "       and itemFicha.mesFa = :mes and \n" +
                    "       itemFicha.mesanofa = :mesanofa and\n" +
                    "       itemFicha.nmrLnc = 1 group by itemficha.cdgmatsrv, itemficha.nmrcntsrv \n", objetoPesquisa.getAno(), objetoPesquisa.getMes(), objetoPesquisa.getMesAnoFa());
            if (con != null) {
                con.close();
            }
            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                con.close();
            }
            return null;
        }
    }


    public List<ComparadorFolha> listaComparadores() {
        Query q = em.createQuery("from ComparadorFolha order by dataRegistro");
        return q.getResultList();
    }

    public Future<RelatorioRH> contabilizarVinculos(ObjetoPesquisa objetoPesquisa) {
        Future<RelatorioRH> mapFuture = comparadorDeFolhasFacadeAsync.contabilizarVinculos(objetoPesquisa);
        logger.debug("liberação assincrona concluída.");
        return mapFuture;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Map<EventoFP, Map<TipoEventoFP, BigDecimal>> classificarERecuperarValores(ObjetoPesquisa objetoPesquisa, Map<EventoFP, Map<TipoEventoFP, BigDecimal>> valores, Map<Class, List<VinculoFP>> classificados) {
        int counter = 0;
        for (Map.Entry<Class, List<VinculoFP>> vinculoClassificado : classificados.entrySet()) {
            for (VinculoFP vinculoFP : vinculoClassificado.getValue()) {
                counter++;
                if (counter % 100 == 0) {
                    logger.debug("Total Processados: {}", counter);
                }
                List<ItemFichaFinanceiraFP> itensFicha = fichaFinanceiraFPFacade.buscarItemFichaFinanceiraPorMesAnoVinculoTipoFolha(objetoPesquisa.getMes(), objetoPesquisa.getAno(), vinculoFP, objetoPesquisa.getTipoFolhaDePagamentoWeb());
                preencherMapItens(itensFicha, valores);
            }
        }
        return valores;
    }

    private Map<EventoFP, Map<TipoEventoFP, BigDecimal>> preencherMapItens(List<ItemFichaFinanceiraFP> itensFicha, Map<EventoFP, Map<TipoEventoFP, BigDecimal>> valores) {
        if (valores == null) {
            valores = Maps.newHashMap();
        }
        for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : itensFicha) {
            if (valores.containsKey(itemFichaFinanceiraFP.getEventoFP())) {
                Map<TipoEventoFP, BigDecimal> tipoEventoFPBigDecimalMap = valores.get(itemFichaFinanceiraFP.getEventoFP());
                classificarTipoEventoValor(tipoEventoFPBigDecimalMap, itemFichaFinanceiraFP);
            } else {
                Map<TipoEventoFP, BigDecimal> tipoValor = Maps.newHashMap();
                classificarTipoEventoValor(tipoValor, itemFichaFinanceiraFP);
                valores.put(itemFichaFinanceiraFP.getEventoFP(), tipoValor);
            }
        }
        return valores;
    }

    public Map<TipoEventoFP, BigDecimal> classificarTipoEventoValor(Map<TipoEventoFP, BigDecimal> tipoValor, ItemFichaFinanceiraFP itemFicha) {
        if (tipoValor.containsKey(itemFicha.getTipoEventoFP())) {
            BigDecimal valor = tipoValor.get(itemFicha.getTipoEventoFP());
            valor = valor.add(itemFicha.getValor());
            tipoValor.put(itemFicha.getTipoEventoFP(), valor);
        } else {
            tipoValor.put(itemFicha.getTipoEventoFP(), itemFicha.getValor());
        }
        return tipoValor;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Map<Class, List<VinculoFP>> classificarVinculos(List<VinculoFP> vinculos, DateTime dataReferencia, Map<Class, Map<VinculoFP, Map<EventoFP, Map<TipoEventoFP, BigDecimal>>>> vinculosValor, ObjetoPesquisa objetoPesquisa) {
        logger.debug("liberando execução assincrona.");
        Map<Class, List<VinculoFP>> vinculosClassificados = Maps.newLinkedHashMap();
        Map<Object, List<VinculoFP>> vinculosLotacao = Maps.newLinkedHashMap();
        int counter = 0;
        for (VinculoFP vinculo : vinculos) {
            counter++;
            if (counter % 100 == 0) {
                logger.debug("Total Processados: {}", counter);
            }
            if (vinculo instanceof ContratoFP) {
                adicionarClassificacao(ContratoFP.class, vinculo, vinculosClassificados);
            }
            if (vinculo instanceof Aposentadoria) {
                adicionarClassificacao(Aposentadoria.class, vinculo, vinculosClassificados);
            }
            if (vinculo instanceof Estagiario) {
                adicionarClassificacao(Estagiario.class, vinculo, vinculosClassificados);
            }
            if (vinculo instanceof Beneficiario) {
                adicionarClassificacao(Beneficiario.class, vinculo, vinculosClassificados);
            }
            if (vinculo instanceof Pensionista) {
                adicionarClassificacao(Pensionista.class, vinculo, vinculosClassificados);
            }
            //classificarLotacao(vinculo, vinculosLotacao, dataReferencia);


            if (vinculo instanceof ContratoFP) {
                adicionarClassificacaoEValor(ContratoFP.class, vinculo, vinculosValor, dataReferencia, objetoPesquisa);
            }
            if (vinculo instanceof Aposentadoria) {
                adicionarClassificacaoEValor(Aposentadoria.class, vinculo, vinculosValor, dataReferencia, objetoPesquisa);
            }
            if (vinculo instanceof Estagiario) {
                adicionarClassificacaoEValor(Estagiario.class, vinculo, vinculosValor, dataReferencia, objetoPesquisa);
            }
            if (vinculo instanceof Beneficiario) {
                adicionarClassificacaoEValor(Beneficiario.class, vinculo, vinculosValor, dataReferencia, objetoPesquisa);
            }
            if (vinculo instanceof Pensionista) {
                adicionarClassificacaoEValor(Pensionista.class, vinculo, vinculosValor, dataReferencia, objetoPesquisa);
            }
            //classificarLotacao(vinculo, vinculosLotacao, dataReferencia);
        }

        for (Map.Entry<Class, List<VinculoFP>> vinculoTipo : vinculosClassificados.entrySet()) {
            logger.debug("Total de {}  vinculos: {}", vinculoTipo.getKey(), vinculoTipo.getValue().size());
        }
        for (Map.Entry<Object, List<VinculoFP>> vinculoLotacao : vinculosLotacao.entrySet()) {
            logger.debug("Total de {}  vinculos: {}", vinculoLotacao.getKey(), vinculoLotacao.getValue().size());
        }
        return vinculosClassificados;
    }

    private void classificarLotacao(VinculoFP vinculo, Map<Object, List<VinculoFP>> vinculosLotacao, DateTime dataReferencia) {
     /*   HierarquiaOrganizacional hierarquiaOrganizacional = lotacaoFuncionalFacade.recuperarHieraquiaPelaLotacaoFuncional(vinculo, dataReferencia.toDate());
        adicionarLotacao(vinculo, vinculosLotacao, hierarquiaOrganizacional);*/
        RecursoFP recursoFP = recursoDoVinculoFPFacade.buscarRecursoDoVinculoPorVinculoAndReferencia(vinculo, dataReferencia.toDate());
        adicionarLotacao(vinculo, vinculosLotacao, recursoFP);
    }

    private void adicionarLotacao(VinculoFP vinculo, Map<Object, List<VinculoFP>> vinculosLotacao, Object hierarquiaOrganizacional) {
        if (vinculosLotacao.containsKey(hierarquiaOrganizacional)) {
            vinculosLotacao.get(hierarquiaOrganizacional).add(vinculo);
        } else {
            vinculosLotacao.put(hierarquiaOrganizacional, Lists.newArrayList(vinculo));
        }
    }

    private void adicionarClassificacao(Class classe, VinculoFP vinculo, Map<Class, List<VinculoFP>> vinculosClassificados) {
        if (vinculosClassificados.containsKey(classe)) {
            vinculosClassificados.get(classe).add(vinculo);
        } else {
            vinculosClassificados.put(classe, Lists.newArrayList(vinculo));
        }
    }

    private void adicionarClassificacaoEValor(Class classe, VinculoFP vinculo, Map<Class, Map<VinculoFP, Map<EventoFP, Map<TipoEventoFP, BigDecimal>>>> vinculosValor, DateTime dataReferencia, ObjetoPesquisa objetoPesquisa) {
        if (vinculosValor.containsKey(classe)) {
            vinculosValor.get(classe).put(vinculo, null);
        } else {
            Map<VinculoFP, Map<EventoFP, Map<TipoEventoFP, BigDecimal>>> valor = Maps.newHashMap();
            valor.put(vinculo, null);
            vinculosValor.put(classe, valor);
        }
        List<ItemFichaFinanceiraFP> itensFicha = fichaFinanceiraFPFacade.buscarItemFichaFinanceiraPorMesAnoVinculoTipoFolha(objetoPesquisa.getMes(), objetoPesquisa.getAno(), vinculo, objetoPesquisa.getTipoFolhaDePagamentoWeb());
        Map<EventoFP, Map<TipoEventoFP, BigDecimal>> eventoFPMapMap = preencherMapItens(itensFicha, vinculosValor.get(classe).get(vinculo));
        vinculosValor.get(classe).remove(vinculo);
        vinculosValor.get(classe).put(vinculo, eventoFPMapMap);
    }

    public StringBuilder getConsultaLancamento() {
        StringBuilder sb = new StringBuilder();
        sb.append("select dados.*,to_date(to_char(dataCadastro,'dd/MM/yyyy'),'dd/MM/yyyy') as cadastro from (\n");
        sb.append("with lancamentos as (SELECT DISTINCT\n");
        sb.append("  lancamento.cdgMatSrv AS matricula,\n");
        sb.append("  lancamento.nmrCntSrv AS contrato,\n");
        sb.append("  verba.cdgVerba AS codigoVerba,\n");
        sb.append("  CASE WHEN nullif(lancamento.dtaIniLnc, '00000000') IS NOT null THEN to_date(lancamento.dtaIniLnc, 'YYYYMMDD') END AS mesAnoInicial,\n");
        sb.append("  CASE WHEN nullif(lancamento.dtaFimLnc, '00000000') IS NOT null\n");
        sb.append("  THEN CASE WHEN lancamento.dtaFimLnc = '20051131' THEN to_date('20051201', 'yyyyMMdd')\n");
        sb.append("       ELSE to_date(lancamento.dtaFimLnc, 'YYYYMMDD') END\n");
        sb.append("  END AS mesAnoFinal,\n");
        sb.append("  CASE WHEN nullif(lancamento.dtaCad010, '00000000') IS null\n");
        sb.append("  THEN to_date(lancamento.dtaIniLnc, 'YYYYMMDD')\n");
        sb.append("  ELSE to_date(lancamento.dtaCad010, 'YYYYMMDD')\n");
        sb.append("  END AS dataCadastro,\n");
        sb.append("  case upper(trim(lancamento.tpoVant))\n");
        sb.append("  when 'Q' then cast(lancamento.qtdeHora as decimal(19, 2))\n");
        sb.append("  when 'H' then cast(lancamento.qtdeHora as decimal(19, 2))\n");
        sb.append("  when 'V' then cast(lancamento.vlr as decimal(19, 2))\n");
        sb.append("  when 'P' then cast(lancamento.perc as decimal(19, 2))\n");
        sb.append("  else 0\n");
        sb.append("  end as valor,\n");
        sb.append("  case upper(trim(lancamento.tpoVant))\n");
        sb.append("  when 'Q' then 'QUANTIDADE'\n");
        sb.append("  when 'H' then 'HORAS'\n");
        sb.append("  when 'V' then 'VALOR'\n");
        sb.append("  when 'P' THEN 'REFERENCIA'\n");
        sb.append("  else 'REFERENCIA'\n");
        sb.append("  end as tipoLancamentoFP,\n");
        sb.append("  CASE WHEN(replace(trim(lancamento.nmrAmpLvs), '.', '') IS NOT null) THEN trim(lancamento.nmrAmpLvs) END AS numeroAtoLegal,\n");
        sb.append("  CASE WHEN(lancamento.anoAmpLvs = 0) THEN lancamento.anoInicio ELSE lancamento.anoAmpLvs END AS anoAtoLegal,\n");
        sb.append("  CASE WHEN nullif(lancamento.dtaEmiLvs, '00000000') IS null\n");
        sb.append("  THEN CASE WHEN nullif(lancamento.dtaIniLnc, '00000000') IS NOT null THEN to_date(lancamento.dtaIniLnc, 'YYYYMMDD') END\n");
        sb.append("  ELSE to_date(lancamento.dtaEmiLvs, 'YYYYMMDD')\n");
        sb.append("  END AS dataEmissao,\n");
        sb.append("  CASE WHEN nullif(lancamento.dtaPubLvs, '00000000') IS null\n");
        sb.append("  THEN CASE WHEN nullif(lancamento.dtaIniLnc, '00000000') IS NOT null THEN to_date(lancamento.dtaIniLnc, 'YYYYMMDD') END\n");
        sb.append("  ELSE to_date(lancamento.dtaPubLvs, 'YYYYMMDD')\n");
        sb.append("  END AS dataPublicacao,\n");
        sb.append("  CASE WHEN ((trim(tipoAtoLegal.dscAmpLeg) = 'LEIS' ) OR (tipoAtoLegal.dscAmpLeg IS null))\n");
        sb.append("  THEN 'LEI'\n");
        sb.append("  ELSE tipoAtoLegal.dscAmpLeg\n");
        sb.append("  END AS Tipo,\n");
        sb.append("  verba.DscVerba AS descEvento,\n");
        sb.append("  CASE verba.creDebVba\n");
        sb.append("  WHEN 'D' THEN 'DESCONTO'\n");
        sb.append("  WHEN 'C' THEN 'VANTAGEM'\n");
        sb.append("  ELSE 'INFORMATIVO'\n");
        sb.append("  END AS tipoEvento,\n");
        sb.append("  substr(trim(lancamento.obsLvs), 1, 255) AS obsLvs,\n");
        sb.append("  lancamento.nmrLncSrv AS tipoDeFolha,\n");
        sb.append("  case when nullif(lancamento.anoInicio, 0) is not null and nullif(lancamento.mesInicio, 0) is not null then to_date(lpad(lancamento.anoInicio, 4, '0') || lpad(case when lancamento.mesInicio = 13 then 12 else lancamento.mesInicio end, 2, '0') || '01', 'yyyyMMdd') end AS mesAnoInicioCalculo,\n");
        sb.append("  case when nullif(lancamento.dtaCad010, '00000000') is not null then to_date(lancamento.dtaCad010, 'yyyyMMdd') end AS dataCadastroEConsig,\n");
        sb.append("  case when usrCad010 = 'ROTECONSIG' then 'ECONSIG' end as importado\n");
        sb.append("FROM\n");
        sb.append("  turmalina.arh206 lancamento\n");
        sb.append("  INNER JOIN turmalina.arh105 contrato ON\n");
        sb.append("    contrato.cdgMatSrv = lancamento.cdgMatSrv\n");
        sb.append("    AND contrato.nmrCntSrv = lancamento.nmrCntSrv\n");
        sb.append("  INNER JOIN turmalina.arh188 m ON\n");
        sb.append("    m.cdgMatProv = lancamento.cdgMatSrv\n");
        sb.append("    AND m.nmrCntProv = lancamento.nmrCntSrv\n");
        sb.append("  LEFT OUTER JOIN turmalina.arh023 tipoAtoLegal ON\n");
        sb.append("    tipoAtoLegal.tipAmpLeg = lancamento.espDocLvs\n");
        sb.append("  LEFT OUTER JOIN turmalina.arh025 verba ON\n");
        sb.append("    verba.cdgVerba = lancamento.cdgVerba\n");
        sb.append("WHERE\n");
        sb.append("  contrato.dtaAdmSrv > 0\n");
        sb.append("  AND m.tpoOcrProv IN (1, 11, 19, 20, 21, 22, 23, 24, 30)\n");
        sb.append("  AND verba.cdgVerba IS NOT null),\n");
        sb.append("\n");
        sb.append("    mensal as (SELECT DISTINCT\n");
        sb.append("    cdgMatSrv AS matricula,\n");
        sb.append("    nmrCntSrv AS contrato,\n");
        sb.append("    cdgVerba AS codigoVerba,\n");
        sb.append("    nmrLnc AS numeroLancamento,\n");
        sb.append("    case upper(trim(tpoVantInt))\n");
        sb.append("    when 'Q' then cast(qteHorInt as decimal(19, 2))\n");
        sb.append("    when 'H' then cast(qteHorInt as decimal(19, 2))\n");
        sb.append("    when 'V' then cast(valorInt as decimal(19, 2))\n");
        sb.append("    when 'P' then cast(percInt as decimal(19, 2))\n");
        sb.append("    end as valor,\n");
        sb.append("    to_date(mesAnoInt, 'yyyyMMdd') as mesAno\n");
        sb.append("  FROM\n");
        sb.append("    turmalina.arh205)\n");
        sb.append("SELECT * FROM (\n");
        sb.append("                SELECT DISTINCT\n");
        sb.append("                  lancamentos.matricula,\n");
        sb.append("                  lancamentos.contrato,\n");
        sb.append("                  lancamentos.codigoVerba,\n");
        sb.append("                  lancamentos.mesAnoInicial,\n");
        sb.append("                  lancamentos.mesAnoFinal,\n");
        sb.append("                  lancamentos.dataCadastro,\n");
        sb.append("                  lancamentos.valor,\n");
        sb.append("                  lancamentos.tipoLancamentoFP,\n");
        sb.append("                  lancamentos.numeroAtoLegal,\n");
        sb.append("                  lancamentos.anoAtoLegal,\n");
        sb.append("                  lancamentos.dataEmissao,\n");
        sb.append("                  lancamentos.dataPublicacao,\n");
        sb.append("                  lancamentos.tipo,\n");
        sb.append("                  lancamentos.descEvento,\n");
        sb.append("                  lancamentos.tipoEvento,\n");
        sb.append("                  lancamentos.obsLvs,\n");
        sb.append("                  lancamentos.tipoDeFolha,\n");
        sb.append("                  lancamentos.mesAnoInicioCalculo,\n");
        sb.append("                  lancamentos.dataCadastroEConsig,\n");
        sb.append("                  lancamentos.importado\n");
        sb.append("                FROM\n");
        sb.append("                  lancamentos\n");
        sb.append("      UNION ALL\n");
        sb.append("                SELECT\n");
        sb.append("                  frequencia.cdgMatSrv AS matricula,\n");
        sb.append("                  frequencia.nmrCntSrv AS contrato,\n");
        sb.append("                  tipo.cdgVerba AS codigoVerba,\n");
        sb.append("                  to_date(frequencia.dtaIniFrq, 'yyyyMMdd') AS mesAnoInicial,\n");
        sb.append("                  to_date(frequencia.dtaFinFrq, 'yyyyMMdd') AS mesAnoFinal,\n");
        sb.append("                  frequencia.dtaCad075 AS dataCadastro,\n");
        sb.append("                  frequencia.qtdFrq AS valor,\n");
        sb.append("                  'REFERENCIA' AS tipoLancamentoFP,\n");
        sb.append("                  null AS numeroAtoLegal,\n");
        sb.append("                  null AS anoAtoLegal,\n");
        sb.append("                  null AS dataEmissao,\n");
        sb.append("                  null AS dataPublicacao,\n");
        sb.append("                  null AS tipo,\n");
        sb.append("                  tipo.dscFrq AS descEvento,\n");
        sb.append("                  'VANTAGEM' AS tipoEvento,\n");
        sb.append("                  'Usuario Cadastro: ' || usrCad075 AS obsLvs,\n");
        sb.append("                  1 as tipoDeFolha,\n");
        sb.append("                  to_date(frequencia.dtaIniFrq, 'yyyyMMdd') AS mesAnoInicioCalculo,\n");
        sb.append("                  frequencia.dtaCad075 as dataCadastroEConsig,\n");
        sb.append("                  null as importado\n");
        sb.append("                FROM\n");
        sb.append("                  turmalina.arh290 frequencia\n");
        sb.append("                  INNER JOIN turmalina.arh291 tipo ON\n");
        sb.append("                    tipo.cdgFrq = frequencia.cdgFrq\n");
        sb.append("\n");
        sb.append("                WHERE frequencia.cdgFrq in (7, 8)\n");
        sb.append("                UNION ALL\n");
        sb.append("                SELECT\n");
        sb.append("                  cdgMatSrv AS matricula,\n");
        sb.append("                  nmrCntSrv AS contrato,\n");
        sb.append("                  441 AS codigoVerba,\n");
        sb.append("                  to_date(hDtaIsn, 'yyyyMMdd') AS mesAnoInicial,\n");
        sb.append("                  to_date(hDtaFimIsn, 'yyyyMMdd') AS mesAnoFinal,\n");
        sb.append("                  hDtaAlt037 AS dataCadastro,\n");
        sb.append("                  11 AS valor,\n");
        sb.append("                  'REFERENCIA' AS tipoLancamentoFP,\n");
        sb.append("                  null AS numeroAtoLegal,\n");
        sb.append("                  null AS anoAtoLegal,\n");
        sb.append("                  null AS dataEmissao,\n");
        sb.append("                  null AS dataPublicacao,\n");
        sb.append("                  null AS tipo,\n");
        sb.append("                  null AS descEvento,\n");
        sb.append("                  null AS tipoEvento,\n");
        sb.append("                  null AS obsLvs,\n");
        sb.append("                  1 AS tipoDeFolha,\n");
        sb.append("                  to_date(hDtaIsn, 'yyyyMMdd') AS mesAnoInicioCalculo,\n");
        sb.append("                  null AS dataCadastroEConsig,\n");
        sb.append("                  null AS importado\n");
        sb.append("                FROM\n");
        sb.append("                  turmalina.arh078\n");
        sb.append("                WHERE\n");
        sb.append("                  tpoIsenAbo = 'A') dados\n");
        sb.append("where \n");
        sb.append("  not exists (select * from turmalina.arh280 pensao WHERE pensao.cdgMatSrv = dados.matricula and pensao.nmrCntSrv = dados.contrato and pensao.bpeVerba = dados.codigoVerba and pensao.bpeIncPens = to_char(dados.mesAnoInicial, 'yyyymmdd'))\n")
        ;
        sb.append("\n");
        sb.append("ORDER BY\n");
        sb.append("  matricula,\n");
        sb.append("  contrato,\n");
        sb.append("  codigoVerba,\n");
        sb.append("  mesAnoInicial) dados where dados.dataCadastro >= to_date(:dataCadatro,'dd/MM/yyyy') and dados.importado IS null");
        return sb;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Map<Integer, List<LancamentoFP>> buscarLancamentosParaMigracao(Date dataRegistro) {
        try {
            Map<Integer, List<LancamentoFP>> mapLancamentos = Maps.newLinkedHashMap();
            List<LancamentoFP> lancamentos = Lists.newLinkedList();
            List<LancamentoFP> rejeitados = Lists.newLinkedList();
            List<RegistroDB> registros = buscarLancamentosPorDataCadastro(dataRegistro);
            for (RegistroDB registro : registros) {
                LancamentoFP lancamento = converterRegistroEmLancamentoFP(registro);
                if (isPodeAdicionar(lancamento)) {
                    lancamentos.add(lancamento);
                } else {
                    rejeitados.add(lancamento);
                }
            }
            mapLancamentos.put(1, lancamentos);
            mapLancamentos.put(2, rejeitados);
            return mapLancamentos;
        } catch (Exception e) {
            logger.error("Erro", e);
        }
        return null;
    }

    private boolean isPodeAdicionar(LancamentoFP lancamento) {
        if (lancamento.getVinculoFP() == null) return false;
        if (lancamento.getEventoFP() == null) return false;
        if (lancamento.getTipoFolhaDePagamento() == null) return false;
        if (lancamento.getTipoLancamentoFP() == null) return false;
        if (lancamento.getMesAnoInicial() == null) return false;
        if (lancamento.getMesAnoFinal() == null) return false;
        return true;
    }

    private LancamentoFP converterRegistroEmLancamentoFP(RegistroDB registro) throws SQLException {
        LancamentoFP l = new LancamentoFP();
        l.setVinculoFP(buscarVinculoFP((BigDecimal) registro.getCampoByIndex(0).getValor(), (BigDecimal) registro.getCampoByIndex(1).getValor()));
        l.setEventoFP(buscarEventoFP((BigDecimal) registro.getCampoByIndex(2).getValor()));
        l.setMesAnoInicial((Date) registro.getCampoByIndex(3).getValor());
        l.setMesAnoFinal((Date) registro.getCampoByIndex(4).getValor());
        l.setDataCadastro((Date) registro.getCampoByIndex(20).getValor());
        l.setQuantificacao((BigDecimal) registro.getCampoByIndex(6).getValor());
        l.setTipoLancamentoFP(converterTipoLancamento((String) registro.getCampoByIndex(7).getValor()));
        l.setTipoFolhaDePagamento(converterTipoFolha((BigDecimal) registro.getCampoByIndex(16).getValor()));
        l.setMesAnoInicioCalculo(l.getMesAnoInicial());
        return l;
    }


    private TipoFolhaDePagamento converterTipoFolha(BigDecimal tipo) {
        if (BigDecimal.valueOf(1).compareTo(tipo) == 0) {
            return TipoFolhaDePagamento.NORMAL;
        }
        logger.debug("putss tem outros tipos de folha além da normal! {}", tipo);
        return TipoFolhaDePagamento.COMPLEMENTAR;
    }

    private TipoLancamentoFP converterTipoLancamento(String valor) {
        return TipoLancamentoFP.valueOf(valor);
    }

    private EventoFP buscarEventoFP(BigDecimal codigo) {
        EventoFP eventoFP = eventoFPFacade.recuperaPorCodigo(codigo + "");
        return eventoFP;
    }

    private VinculoFP buscarVinculoFP(BigDecimal matricula, BigDecimal numero) {
        VinculoFP vinculoFP = vinculoFPFacade.recuperarVinculoPorMatriculaENumero(matricula + "", numero + "");
        if (vinculoFP == null) {
            logger.debug("Ops, Matricula {} e Contrato não encontrado {}", matricula, numero);
        }
        return vinculoFP;
    }

    public void definirRecursoFPEmFichaFinanceira() {
        comparadorDeFolhasFacadeAsync.definirRecursoFPEmFichaFinaceiraVazias();
    }

    public void definirLotacaoEmFichaFinanceira() {
        comparadorDeFolhasFacadeAsync.definirLotacaoFuncionalEmFichaFinaceiraVazias();
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<ItemDirfCorrecao> buscarContratosAposentadosNoRbprev() {
        List<ItemDirfCorrecao> itemsDirf = Lists.newLinkedList();
        List<Aposentadoria> aposentadorias = aposentadoriaFacade.lista();
        int contador = 0;
        for (Aposentadoria aposentadoria : aposentadorias) {
            contador++;
            ItemDirfCorrecao item = new ItemDirfCorrecao();
            item.setVinculoFP(aposentadoria.getContratoFP());
            List<LotacaoFuncional> lotacoes = lotacaoFuncionalFacade.recuperaLotacaoFuncionalPorContratoFP(aposentadoria.getContratoFP());
            if (lotacoes != null && !lotacoes.isEmpty()) {
                preencherItemLotacao(item, aposentadoria.getContratoFP(), lotacoes);
            }
            List<RecursoDoVinculoFP> recursos = recursoDoVinculoFPFacade.recuperarRecursosDoVinculo(aposentadoria.getContratoFP());
            if (recursos != null && !recursos.isEmpty()) {
                preencherRecuros(item, aposentadoria.getContratoFP(), recursos);
            }
            itemsDirf.add(item);
        }
        logger.debug("Total de Itens: {}", contador);
        return itemsDirf;
    }

    private void preencherRecuros(ItemDirfCorrecao itemDirf, ContratoFP contratoFP, List<RecursoDoVinculoFP> recursos) {
        try {
            itemDirf.setRecursoAtual(recursos.get(0));
        } catch (IndexOutOfBoundsException idx) {
            logger.debug("Indice 0 para recurso do vinculoFP não encontrado." + contratoFP);
        }
        try {
            itemDirf.setRecursoUltimo(recursos.get(1));
        } catch (IndexOutOfBoundsException idx) {
            logger.debug("Indice 1 para recurso do vinculoFP não encontrado." + contratoFP);
        }
        try {
            itemDirf.setRecursoAntePenultimo(recursos.get(2));
        } catch (IndexOutOfBoundsException idx) {
            logger.debug("Indice 2 para recurso do vinculoFP não encontrado." + contratoFP);
        }

        RecursoFP atual = null;
        if (itemDirf.getRecursoAtual() != null) {
            atual = itemDirf.getRecursoAtual().getRecursoFP();
        } else {
            itemDirf.setNaoDeveExcluirRecurso(true);
        }
        RecursoFP ultimo = null;
        if (itemDirf.getRecursoUltimo() != null) {
            ultimo = itemDirf.getRecursoUltimo().getRecursoFP();
            if (atual == null || ultimo == null) {
                itemDirf.setNaoDeveExcluirRecurso(true);
            }
            if (atual != null && atual.getCodigoOrgao() != CODIGO_RBPREV) {
                itemDirf.setNaoDeveExcluirRecurso(true);
            }
            if (atual != null && atual.getCodigoOrgao() == CODIGO_RBPREV && ultimo != null && ultimo.getCodigoOrgao() == CODIGO_RBPREV) {
                itemDirf.setNaoDeveExcluirRecurso(true);
            }
        } else {
            itemDirf.setNaoDeveExcluirRecurso(true);
        }
    }

    private void preencherItemLotacao(ItemDirfCorrecao itemDirf, ContratoFP contratoFP, List<LotacaoFuncional> lotacoes) {
        ordenarLotacaoDesc(lotacoes);
        try {
            itemDirf.setAtual(lotacoes.get(0));
        } catch (IndexOutOfBoundsException idx) {
            logger.debug("Indice 0 para lotacao funcional não encontrado." + contratoFP);
        }
        try {
            itemDirf.setUltima(lotacoes.get(1));
        } catch (IndexOutOfBoundsException idx) {
            logger.debug("Indice 1 para lotacao funcional não encontrado." + contratoFP);
        }
        try {
            itemDirf.setAntePenultima(lotacoes.get(2));
        } catch (IndexOutOfBoundsException idx) {
            logger.debug("Indice 2 para lotacao funcional não encontrado." + contratoFP);
        }

        HierarquiaOrganizacional atual = null;
        if (itemDirf.getAtual() != null) {
            atual = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(new Date(), itemDirf.getAtual().getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        } else {
            itemDirf.setNaoDeveExcluirLotacao(true);
        }
        HierarquiaOrganizacional ultimo = null;
        if (itemDirf.getUltima() != null) {
            ultimo = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(new Date(), itemDirf.getUltima().getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            if (atual == null || ultimo == null) {
                itemDirf.setNaoDeveExcluirLotacao(true);
            }
            if (atual != null && !atual.getCodigo().startsWith(RBPREV)) {
                itemDirf.setNaoDeveExcluirLotacao(true);
            }

            if (atual != null && atual.getCodigo().startsWith(RBPREV) && ultimo != null && ultimo.getCodigo().startsWith(RBPREV)) {
                itemDirf.setNaoDeveExcluirLotacao(true);
            }
        } else {
            itemDirf.setNaoDeveExcluirLotacao(true);
        }
    }

    private void ordenarLotacaoDesc(List<LotacaoFuncional> lotacoes) {
        Collections.sort(lotacoes, new Comparator<LotacaoFuncional>() {
            @Override
            public int compare(LotacaoFuncional o1, LotacaoFuncional o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public void excluirLotacoesErradas(List<ItemDirfCorrecao> itens) {
        try {
            for (ItemDirfCorrecao item : itens) {
                if (!item.isNaoDeveExcluirLotacao()) {
                    excluirLotacao(item);
                }
                if (!item.isNaoDeveExcluirRecurso()) {
                    excluirRecurso(item);
                }
            }
        } catch (Exception e) {
            logger.error("Não foi possível excluir o item pelo método " + e.getMessage());
        }

    }

    private void excluirLotacao(ItemDirfCorrecao item) {
        if (item.getAtual() != null) {
            lotacaoFuncionalFacade.remover(item.getAtual());
            UnidadeOrganizacional unidadeOrganizacional = item.getUltima().getUnidadeOrganizacional();
            HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoAdministrativoPorUnidadeAndVigencia(unidadeOrganizacional, item.getUltima().getFinalVigencia() != null ? item.getUltima().getFinalVigencia() : new Date());
            item.getVinculoFP().setUnidadeOrganizacional(orgao.getSubordinada());
            vinculoFPFacade.salvar(item.getVinculoFP());
            item.getUltima().setFinalVigencia(null);
            lotacaoFuncionalFacade.salvar(item.getUltima());
        }
    }

    private void excluirRecurso(ItemDirfCorrecao item) {
        if (item.getRecursoAtual() != null) {
            recursoDoVinculoFPFacade.remover(item.getRecursoAtual());
            item.getRecursoUltimo().setFinalVigencia(null);
            recursoDoVinculoFPFacade.salvar(item.getRecursoUltimo());
        }
    }
}
