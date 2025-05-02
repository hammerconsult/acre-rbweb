package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteImpressaoMalaDiretaRBTrans;
import br.com.webpublico.entidadesauxiliares.FiltroPermissaoMalaDiretaRBTrans;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcMalaDiretaRBTransDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wellington on 22/12/2016.
 */
@Stateless
public class MalaDiretaRBTransFacade extends AbstractFacade<MalaDiretaRBTrans> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ConfiguracaoDAMFacade configuracaoDAMFacade;
    private JdbcMalaDiretaRBTransDAO daoMalaDiretaRBTRans;
    private JdbcDamDAO daoDAM;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private PixFacade pixFacade;
    @EJB
    private DAMFacade damFacade;

    public MalaDiretaRBTransFacade() {
        super(MalaDiretaRBTrans.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<Long> buscarIdsDePermissoesParaMalaDiretaDeRBTrans(MalaDiretaRBTrans malaDiretaRBTrans) {
        String sql = "  select pt.id " +
            "   from permissaotransporte pt " +
            "  inner join permissionario perm on pt.id = perm.permissaotransporte_id " +
            "  inner join cadastroeconomico ce on ce.id = perm.cadastroeconomico_id " +
            "where :data_corrente between perm.iniciovigencia and coalesce(perm.finalvigencia, :data_corrente) ";
        if (malaDiretaRBTrans.getTipoPermissaoRBTrans() != null) {
            sql += "  and pt.tipoPermissaoRBTrans = :tipoPermissao ";
        }
        sql += " and exists (select pvd.id from parcelavalordivida pvd " +
            "            inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "            inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "            inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
            "            inner join calculo cal on cal.id = vd.calculo_id " +
            "            left join calculorbtrans rb on rb.id = cal.id  " +
            "            where spvd.situacaoparcela = :situacao " +
            " and ((cal.tipoCalculo = :tipoCalculoRbtrans and rb.tipoCalculoRBTRans in (:tiposCalculoRBtrans)) " +
            "       or (cal.tipocalculo = :tipoCalculoISS) )" +
            "              and cal.cadastro_id = ce.id " +
            "              and vd.exercicio_id = :idExercicio)";
        if (malaDiretaRBTrans.getTipoCredencialRBTrans() != null) {
            sql += " and exists ( select cred.id from credencialrbtrans cred " +
                " where cred.permissaotransporte_id = pt.id " +
                "  and cred.tipocredencialrbtrans = :tipoCredencial )";
        }
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("data_corrente", new Date());
        if (malaDiretaRBTrans.getTipoPermissaoRBTrans() != null) {
            parameters.put("tipoPermissao", malaDiretaRBTrans.getTipoPermissaoRBTrans().name());
        }
        if (malaDiretaRBTrans.getTipoCredencialRBTrans() != null) {
            parameters.put("tipoCredencial", malaDiretaRBTrans.getTipoCredencialRBTrans().name());
        }
        parameters.put("situacao", SituacaoParcela.EM_ABERTO.name());
        parameters.put("tipoCalculoRbtrans", Calculo.TipoCalculo.RB_TRANS.name());
        parameters.put("tiposCalculoRBtrans", TipoCalculoRBTRans.buscarTipoCalculoRBTransMalaDireta());
        parameters.put("tipoCalculoISS", Calculo.TipoCalculo.ISS.name());
        parameters.put("idExercicio", malaDiretaRBTrans.getExercicio().getId());
        if (malaDiretaRBTrans.getNumeroPermissaoInicial() != null && malaDiretaRBTrans.getNumeroPermissaoInicial() > 0) {
            sql += " and pt.numero >= :numeroInicial ";
            parameters.put("numeroInicial", malaDiretaRBTrans.getNumeroPermissaoInicial());
        }
        if (malaDiretaRBTrans.getNumeroPermissaoFinal() != null && malaDiretaRBTrans.getNumeroPermissaoFinal() > 0) {
            sql += " and pt.numero <= :numeroFinal ";
            parameters.put("numeroFinal", malaDiretaRBTrans.getNumeroPermissaoFinal());
        }
        if (malaDiretaRBTrans.getDigitoInicial() != null && malaDiretaRBTrans.getDigitoInicial() >= 0) {
            sql += " and substr(pt.numero, length(pt.numero), 1) >= :digito_inicial ";
            parameters.put("digito_inicial", malaDiretaRBTrans.getDigitoInicial());
        }
        if (malaDiretaRBTrans.getDigitoFinal() != null && malaDiretaRBTrans.getDigitoFinal() >= 0) {
            sql += " and substr(pt.numero, length(pt.numero), 1) <= :digito_final ";
            parameters.put("digito_final", malaDiretaRBTrans.getDigitoFinal());
        }
        Query q = em.createNativeQuery(sql);

        for (String parametro : parameters.keySet()) {
            q.setParameter(parametro, parameters.get(parametro));
        }
        List<Long> toReturn = Lists.newArrayList();
        for (Object id : q.getResultList()) {
            toReturn.add(((BigDecimal) id).longValue());
        }
        return toReturn;
    }

    public List<Long> buscarIdsDosDamDaMalaDireta(MalaDiretaRBTrans malaDiretaRBTrans) {
        List<Long> toReturn = Lists.newArrayList();
        String sql = "select id from MalaDiretaRBTransPermissao " +
            " where maladiretarbtrans_id = :idMalaDiretaRBTrans";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMalaDiretaRBTrans", malaDiretaRBTrans.getId());
        for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
            toReturn.add(new Long(id.longValue()));
        }
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteImpressaoMalaDiretaRBTrans imprimirDamsMalaDiretaRBTrans(AssistenteImpressaoMalaDiretaRBTrans assistente,
                                                                              List<Long> lista,
                                                                              String pastaMalaDireta) {

        List<List<Long>> particoes = Lists.partition(lista, 500);
        int qtdePartes = 0;
        for (List<Long> parte : particoes) {

            qtdePartes++;
            String nomeDoArquivo = pastaMalaDireta + "DAM_MALA_DEIRETA_RB_TRANS" +
                "_" + assistente.getIdMala() + "_" +
                StringUtil.preencheString(assistente.getNumFuture() + "", 2, '0') +
                StringUtil.preencheString(qtdePartes + "", 5, '0') + ".pdf";

            try {
                byte[] dados = assistente.getImprimeDAM().gerarBytesImpressaoMalaDiretaRbTrans(assistente.getUsuario(),
                    assistente.getIdMala(), parte);

                try (FileOutputStream outputStream = new FileOutputStream(nomeDoArquivo)) {
                    outputStream.write(dados);
                }
            } catch (Exception e) {
                logger.error("Erro ao gerar o arquivo " + nomeDoArquivo + ". {}", e.getMessage());
                logger.debug("Stacktrace.", e);
            } finally {
                assistente.contar();
            }
            assistente.contar(parte.size());
        }
        return assistente;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public JdbcMalaDiretaRBTransDAO getDaoMalaDiretaRBTrans() {
        if (daoMalaDiretaRBTRans == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            daoMalaDiretaRBTRans = (JdbcMalaDiretaRBTransDAO) ap.getBean("malaDiretaRBTransDAO");
        }
        return daoMalaDiretaRBTRans;
    }

    public JdbcDamDAO getDaoDAM() {
        if (daoDAM == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            daoDAM = (JdbcDamDAO) ap.getBean("damDAO");
        }
        return daoDAM;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public Map<PermissaoTransporte, List<ResultadoParcela>> buscarDebitosDaMalaDiretaDeRBTrans(MalaDiretaRBTrans malaDiretaRBTrans, List<Long> idsPermissoes) {
        Map<PermissaoTransporte, List<ResultadoParcela>> toReturn = Maps.newHashMap();

        for (Long idPermissao : idsPermissoes) {
            PermissaoTransporte permissaoTransporte = em.find(PermissaoTransporte.class, idPermissao);
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addComplementoJoin(" left join calculorbtrans rb on rb.id = " + ConsultaParcela.Campo.CALCULO_ID.getCampo());
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, malaDiretaRBTrans.getExercicio().getAno());
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, permissaoTransporte.getPermissionarioVigente().getCadastroEconomico().getId());
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
            consultaParcela.addComplementoDoWhere(" and (   (" + ConsultaParcela.Campo.TIPO_CALCULO.getCampo() + " = '" + Calculo.TipoCalculo.RB_TRANS.name() + "' " +
                "                                             and rb.tipoCalculoRBTRans " + TipoCalculoRBTRans.clausulaInTiposCalculoRBTransMalaDireta() +
                "                                            ) " +
                "                                        or ( " + ConsultaParcela.Campo.TIPO_CALCULO.getCampo() + " = '" + Calculo.TipoCalculo.ISS.name() + "') " +
                "                                       ) ");
            List<ResultadoParcela> parcelas = consultaParcela.executaConsulta().getResultados();
            toReturn.put(permissaoTransporte, parcelas);
        }
        return toReturn;
    }

    private List<MalaDiretaRBTransParcela> criarMalaDiretaRBTransParcela(MalaDiretaRBTransPermissao malaDiretaRBTransPermissao, List<ResultadoParcela>
        debitos, UsuarioSistema usuario) {
        List<MalaDiretaRBTransParcela> toReturn = Lists.newArrayList();
        Map<Long, Divida> dividas = Maps.newHashMap();
        for (ResultadoParcela resultadoParcela : debitos) {
            MalaDiretaRBTransParcela malaDiretaRBTransParcela = new MalaDiretaRBTransParcela();
            malaDiretaRBTransParcela.setMalaDiretaRBTransPermissao(malaDiretaRBTransPermissao);
            malaDiretaRBTransParcela.setParcelaValorDivida(em.find(ParcelaValorDivida.class, resultadoParcela.getIdParcela()));
            if (dividas.get(resultadoParcela.getIdDivida()) == null) {
                dividas.put(resultadoParcela.getIdDivida(), em.find(Divida.class, resultadoParcela.getIdDivida()));
            }
            malaDiretaRBTransParcela.setDam(criarDAM(resultadoParcela, usuario));
            toReturn.add(malaDiretaRBTransParcela);
        }

        return toReturn;
    }

    public BigDecimal getValorTotal(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorTotal());
        }

        return toReturn;
    }

    public BigDecimal getValorOriginal(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorOriginal());
        }

        return toReturn;
    }

    public BigDecimal getValorJuros(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorJuros());
        }

        return toReturn;
    }

    public BigDecimal getValorMulta(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorMulta());
        }

        return toReturn;
    }

    public BigDecimal getValorCorrecao(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorCorrecao());
        }

        return toReturn;
    }

    public BigDecimal getValorDesconto(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorDesconto());
        }

        return toReturn;
    }

    public BigDecimal getValorHonorarios(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorHonorarios());
        }

        return toReturn;
    }

    public DAM criarDAM(ResultadoParcela resultadoParcela, UsuarioSistema usuario) {
        return damFacade.gerarDAMUnicoViaApi(usuario, resultadoParcela);
    }

    public List<ItemParcelaValorDivida> buscarItensParcelaValorDividaPorIdParcela(Long idParcela) {
        String sql = "select ipvd.* " +
            "   from itemvalordivida ivd " +
            "  inner join tributo t on t.id = ivd.tributo_id " +
            "  inner join itemparcelavalordivida ipvd on ipvd.itemvalordivida_id = ivd.id " +
            "where ipvd.parcelavalordivida_id = :id_parcela ";
        Query q = em.createNativeQuery(sql, ItemParcelaValorDivida.class);
        q.setParameter("id_parcela", idParcela);
        return q.getResultList();
    }

    private TributoDAM criarTributoItemDAM(ItemDAM itemDAM, Long idTributo, BigDecimal valorTributo) {
        TributoDAM tributoDAM = new TributoDAM();
        tributoDAM.setDesconto(BigDecimal.ZERO);
        tributoDAM.setItemDAM(itemDAM);
        tributoDAM.setTributo(new Tributo(idTributo));
        tributoDAM.setValorOriginal(valorTributo);
        getDaoDAM().persisteTributoDAM(tributoDAM);
        return tributoDAM;
    }

    public MalaDiretaRBTransPermissao gerarMalaDiretaRBTransPermissao(MalaDiretaRBTrans malaDiretaRBTrans, PermissaoTransporte permissaoTransporte,
                                                                      List<ResultadoParcela> debitosDaPermissao, UsuarioSistema usuario) throws HeuristicRollbackException, HeuristicMixedException, NotSupportedException, RollbackException, SystemException {
        MalaDiretaRBTransPermissao malaDiretaRBTransPermissao = new MalaDiretaRBTransPermissao();
        malaDiretaRBTransPermissao.setMalaDiretaRBTrans(malaDiretaRBTrans);
        malaDiretaRBTransPermissao.setPermissaoTransporte(permissaoTransporte);
        malaDiretaRBTransPermissao.setParcelas(criarMalaDiretaRBTransParcela(malaDiretaRBTransPermissao, debitosDaPermissao, usuario));
        getDaoMalaDiretaRBTrans().persistirMalaDiretaRBTransPermissao(malaDiretaRBTransPermissao);
        return malaDiretaRBTransPermissao;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public List<MalaDiretaRBTransPermissao> criarMalaDiretaRBTransPermissao(MalaDiretaRBTrans malaDiretaRBTrans,
                                                                            Map<PermissaoTransporte, List<ResultadoParcela>> mapaParcelasPorPermissao,
                                                                            AssistenteBarraProgresso assistenteBarraProgresso) throws HeuristicRollbackException, HeuristicMixedException, NotSupportedException, RollbackException, SystemException {
        List<MalaDiretaRBTransPermissao> toReturn = Lists.newArrayList();

        for (PermissaoTransporte permissaoTransporte : mapaParcelasPorPermissao.keySet()) {
            toReturn.add(gerarMalaDiretaRBTransPermissao(malaDiretaRBTrans, permissaoTransporte,
                mapaParcelasPorPermissao.get(permissaoTransporte), assistenteBarraProgresso.getUsuarioSistema()));
            assistenteBarraProgresso.conta();
        }

        return toReturn;
    }

    public MalaDiretaRBTrans salvarRetornando(MalaDiretaRBTrans malaDiretaRBTrans) {
        return em.merge(malaDiretaRBTrans);
    }

    public Integer buscarQuantidadeDePermissoesNaMalaDireta(MalaDiretaRBTrans malaDiretaRBTrans) {
        String sql = " select count(1) from maladiretarbtranspermissao perm " +
            " where perm.maladiretarbtrans_id = :maladiretarbtrans_id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("maladiretarbtrans_id", malaDiretaRBTrans.getId());
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public int buscarQuantidadePermissoesDaMalaDiretaRBTrans(MalaDiretaRBTrans malaDiretaRBTrans,
                                                             FiltroPermissaoMalaDiretaRBTrans filtroPermissaoMalaDiretaRBTrans) {
        String sql = " select count(1) " +
            "    from maladiretarbtranspermissao perm " +
            "   inner join permissaotransporte pt on pt.id = perm.permissaotransporte_id " +
            " where perm.maladiretarbtrans_id = :maladiretarbtrans_id ";
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("maladiretarbtrans_id", malaDiretaRBTrans.getId());
        if (filtroPermissaoMalaDiretaRBTrans.getPermissaoInicial() != null && filtroPermissaoMalaDiretaRBTrans.getPermissaoInicial() > 0) {
            sql += " and pt.numero >= :permissao_inicial ";
            parameters.put("permissao_inicial", filtroPermissaoMalaDiretaRBTrans.getPermissaoInicial());
        }
        if (filtroPermissaoMalaDiretaRBTrans.getPermissaoFinal() != null && filtroPermissaoMalaDiretaRBTrans.getPermissaoFinal() > 0) {
            sql += " and pt.numero <= :permissao_final ";
            parameters.put("permissao_final", filtroPermissaoMalaDiretaRBTrans.getPermissaoFinal());
        }

        Query q = em.createNativeQuery(sql);
        for (String param : parameters.keySet()) {
            q.setParameter(param, parameters.get(param));
        }
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public List<MalaDiretaRBTransPermissao> buscarPermissoesMalaDiretaRBTrans(MalaDiretaRBTrans malaDiretaRBTrans, FiltroPermissaoMalaDiretaRBTrans filtroPermissaoMalaDiretaRBTrans,
                                                                              int indexInicial, int quantidade) {
        String sql = " select perm.* " +
            "    from maladiretarbtranspermissao perm " +
            "   inner join permissaotransporte pt on pt.id = perm.permissaotransporte_id " +
            " where perm.maladiretarbtrans_id = :maladiretarbtrans_id ";
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("maladiretarbtrans_id", malaDiretaRBTrans.getId());
        if (filtroPermissaoMalaDiretaRBTrans.getPermissaoInicial() != null && filtroPermissaoMalaDiretaRBTrans.getPermissaoInicial() > 0) {
            sql += " and pt.numero >= :permissao_inicial ";
            parameters.put("permissao_inicial", filtroPermissaoMalaDiretaRBTrans.getPermissaoInicial());
        }
        if (filtroPermissaoMalaDiretaRBTrans.getPermissaoFinal() != null && filtroPermissaoMalaDiretaRBTrans.getPermissaoFinal() > 0) {
            sql += " and pt.numero <= :permissao_final ";
            parameters.put("permissao_final", filtroPermissaoMalaDiretaRBTrans.getPermissaoFinal());
        }
        sql += " order by pt.numero ";
        Query q = em.createNativeQuery(sql, MalaDiretaRBTransPermissao.class);
        for (String param : parameters.keySet()) {
            q.setParameter(param, parameters.get(param));
        }
        q.setFirstResult(indexInicial);
        q.setMaxResults(quantidade);
        return q.getResultList();
    }

    public MalaDiretaRBTransPermissao recuperarMalaDiretaRBTransPermissao(Long id) {
        return em.find(MalaDiretaRBTransPermissao.class, id);
    }

    public String getTextoMalaDiretaPadrao() {
        return "Permissionário, <br/> Fique atento e auxilie no embarque e desembarque de idosos em seu veículo, eles necessitam de maior atenção, " +
            "dessa forma você evita que alguém se machuque e ainda colabora com a Campanha Sobre Acidentes e Violência na 3ª idade!";
    }
}
