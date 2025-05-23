package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.interfaces.GeradorRelatorioAssincrono;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteCalculadorIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.CalculadorIPTU;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

@Stateless
public class NovoCalculoIPTUFacade extends AbstractFacade<ProcessoCalculoIPTU> {

    private static final Logger logger = LoggerFactory.getLogger(NovoCalculoIPTUFacade.class);
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ConfiguracaoEventoCalculoIPTUFacade configuracaoEventoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @Resource
    private SessionContext sctx;
    @EJB
    private ProcessoIsencaoIPTUFacade processoIsencaoIPTUFacade;
    @EJB
    private GeraValorDividaIPTU valorDividaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;


    public NovoCalculoIPTUFacade() {
        super(ProcessoCalculoIPTU.class);
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SessionContext getSctx() {
        return sctx;
    }

    public ValorDividaFacade getValorDividaFacade() {
        return valorDividaFacade;
    }

    public ProcessoIsencaoIPTUFacade getProcessoIsencaoIPTUFacade() {
        return processoIsencaoIPTUFacade;
    }

    public ConfiguracaoEventoCalculoIPTUFacade getConfiguracaoEventoFacade() {
        return configuracaoEventoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<AssistenteCalculadorIPTU> calcularIPTU(List<CadastroImobiliario> cadastros, AssistenteCalculadorIPTU assistente) {
        Future<AssistenteCalculadorIPTU> result = new AsyncResult<>(assistente);
        CalculadorIPTU calculado = new CalculadorIPTU();
        calculado.calcularIPTU(cadastros, assistente, sctx);
        return result;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void gerarReport2(GeradorRelatorioAssincrono controlador)
        throws JRException, IOException {
        Connection con = controlador.recuperaConexaoJDBC();
        controlador.getParametros().put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(controlador.getReport(), controlador.getParametros(), con);
        controlador.setDadosByte(new ByteArrayOutputStream());
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, controlador.getDadosByte());
        exporter.exportReport();
    }

    public CalculoIPTU recuperaIPTU(Long id) {
        CalculoIPTU calculoIPTU = em.find(CalculoIPTU.class, id);
        Hibernate.initialize(calculoIPTU.getItensCalculo());
        return calculoIPTU;
    }

    public ProcessoCalculoIPTU salvarProcesso(ProcessoCalculoIPTU processoCalculoIPTU) {
        return em.merge(processoCalculoIPTU);
    }

    public RevisaoCalculoIPTU efetivarRevisao(RevisaoCalculoIPTU selecionado) {
        selecionado = em.merge(selecionado);
        for (ParcelaValorDivida pvd : selecionado.getValorDivida().getParcelaValorDividas()) {
            if (!SituacaoParcela.AGUARDANDO_REVISAO.equals(pvd.getUltimaSituacao().getSituacaoParcela())
                && !SituacaoParcela.CANCELADO_RECALCULO.equals(pvd.getUltimaSituacao().getSituacaoParcela())
                && !SituacaoParcela.CANCELADO_ISENCAO.equals(pvd.getUltimaSituacao().getSituacaoParcela())) {

                SituacaoParcela situacao = SituacaoParcela.CANCELADO_RECALCULO;
                Calculo calculo = selecionado.getValorDivida().getCalculo();
                calculo = initializeAndUnproxy(calculo);
                if (((CalculoIPTU) calculo).getIsencaoCadastroImobiliario() != null) {
                    situacao = SituacaoParcela.CANCELADO_ISENCAO;
                }
                pvd.getSituacoes().add(new SituacaoParcelaValorDivida(situacao, pvd, pvd.getValor()));
                em.merge(pvd);
            }
        }
        selecionado.getProcessoCalculo().getCalculosIPTU().size();
        return selecionado;
    }

    public RevisaoCalculoIPTU efetivaRevisaoLote(RevisaoCalculoIPTU selecionado) {
        selecionado = em.merge(selecionado);
        selecionado.getProcessoCalculo().getCalculosIPTU().size();
        return selecionado;
    }

    public void cancelarParcelas(RevisaoCalculoIPTU selecionado) {
        ValorDivida vd = recuperarValorDivida(selecionado.getValorDivida().getId());
        for (ParcelaValorDivida pvd : vd.getParcelaValorDividas()) {
            if (!SituacaoParcela.AGUARDANDO_REVISAO.equals(pvd.getUltimaSituacao().getSituacaoParcela())
                && !SituacaoParcela.CANCELADO_RECALCULO.equals(pvd.getUltimaSituacao().getSituacaoParcela())
                && !SituacaoParcela.CANCELADO_ISENCAO.equals(pvd.getUltimaSituacao().getSituacaoParcela())) {

                SituacaoParcela situacao = SituacaoParcela.CANCELADO_RECALCULO;
                Calculo calculo = selecionado.getValorDivida().getCalculo();
                calculo = initializeAndUnproxy(calculo);
                if (((CalculoIPTU) calculo).getIsencaoCadastroImobiliario() != null) {
                    situacao = SituacaoParcela.CANCELADO_ISENCAO;
                }
                pvd.getSituacoes().add(new SituacaoParcelaValorDivida(situacao, pvd, pvd.getValor()));
                em.merge(pvd);
            }
        }
    }

    public void merge(ProcessoRevisao p) {
        em.merge(p);
    }


    @Override
    public ProcessoCalculoIPTU recuperar(Object id) {
        ProcessoCalculoIPTU processo = em.find(ProcessoCalculoIPTU.class, id);
        return processo;
    }

    public ProcessoCalculoIPTU recuperarComDependencias(Object id) {
        em.flush();
        ProcessoCalculoIPTU processo = em.find(ProcessoCalculoIPTU.class, id);
        processo.getCalculosIPTU().size();
        return processo;
    }

    public RevisaoCalculoIPTU recuperaRevisao(Object id) {
        RevisaoCalculoIPTU revisaoCalculoIPTU = em.find(RevisaoCalculoIPTU.class, id);
        revisaoCalculoIPTU.getProcessoCalculo().getCalculosIPTU().size();
        return revisaoCalculoIPTU;
    }

    public ProcessoRevisaoCalculoIPTU recuperaProcessoRevisao(Object id) {
        ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU = em.find(ProcessoRevisaoCalculoIPTU.class, id);
        processoRevisaoCalculoIPTU.getProcessoRevisao().size();
        return processoRevisaoCalculoIPTU;
    }

    @Asynchronous
    public Future<Map<String, List>> consultaParcelas(RevisaoCalculoIPTU revisao) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Operador.IGUAL, revisao.getValorDivida().getId());
        List<ResultadoParcela> originais = consulta.executaConsulta().getResultados();

        consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, idCalculo(revisao));

        List<ResultadoParcela> originadas = consulta.executaConsulta().getResultados();

        Map<String, List> mapa = Maps.newHashMap();

        mapa.put("Originadas", originadas);
        mapa.put("Originais", originais);

        return new AsyncResult<>(mapa);
    }

    private Long idCalculo(RevisaoCalculoIPTU revisao) {
        String sql = ("  SELECT CALC.ID FROM REVISAOCALCULOIPTU REVISAO " +
            "  INNER JOIN PROCESSOCALCULO P ON REVISAO.PROCESSOCALCULO_ID = P.ID " +
            "  INNER JOIN CALCULO CALC ON CALC.PROCESSOCALCULO_ID = P.ID " +
            "  where  revisao.id = :revisao and CALC.CADASTRO_ID = :cadastro ");
        Query q = em.createNativeQuery(sql);
        q.setParameter("revisao", revisao.getId());
        q.setParameter("cadastro", revisao.getCadastro().getId());
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getResultList().get(0)).longValue();
        }
        return 0l;
    }

    public CalculoIPTU recuperarCalculo(RevisaoCalculoIPTU revisao) {
        String hql = " SELECT calculo FROM RevisaoCalculoIPTU revisao " +
            " inner join revisao.processoCalculo processo " +
            " inner join processo.calculosIPTU calculo " +
            " where revisao = :revisao and calculo.cadastro = :cadastro";
        Query q = em.createQuery(hql, CalculoIPTU.class);
        q.setParameter("revisao", revisao);
        q.setParameter("cadastro", revisao.getCadastro());
        if (!q.getResultList().isEmpty()) {
            return (CalculoIPTU) q.getResultList().get(0);
        }
        return null;
    }


    public ProcessoRevisaoCalculoIPTU merge(ProcessoRevisaoCalculoIPTU processo) {
        em.flush();
        return em.merge(processo);
    }

    public ValorDivida recuperarValorDivida(Long id) {
        ValorDivida vd = em.find(ValorDivida.class, id);
        Hibernate.initialize(vd.getCalculo());// para carregar o calculo (LAZY)
        return vd;
    }

    public boolean hasParcelaIPTUPaga(Exercicio exercicio,
                                      String inscricaoInicial,
                                      String inscricaoFinal) {
        Divida dividaIPTU = configuracaoTributarioFacade.retornaUltimo().getDividaIPTU();
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.BCI_INSCRICAO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.MAIOR_IGUAL, inscricaoInicial);
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.BCI_INSCRICAO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.MENOR_IGUAL, inscricaoFinal);
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.EXERCICIO_ANO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, exercicio.getAno());
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.DIVIDA_ID,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, dividaIPTU.getId());
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.PARCELA_SITUACAO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IN, Lists.newArrayList(SituacaoParcela.getSituacoesPagas()));
        consultaParcela.executaConsulta();
        return consultaParcela.getResultados() != null && !consultaParcela.getResultados().isEmpty();
    }

    public void verificarEfefetivacao(ProcessoCalculoIPTU processo) {
        String sql = "select count(iptu.id), count(vd.id) from processocalculoiptu processo " +
            "inner join calculoiptu iptu on iptu.PROCESSOCALCULOIPTU_ID = processo.id " +
            "inner join calculo on calculo.id = iptu.id " +
            "left join valordivida vd on vd.calculo_id = iptu.id " +
            "where processo.id = :processo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("processo", processo.getId());
        List<Object[]> listaArrayObjetos = q.getResultList();
        BigDecimal quantidadeCalculos = (BigDecimal) listaArrayObjetos.get(0)[0];
        BigDecimal quantidadeEfetivacoes = (BigDecimal) listaArrayObjetos.get(0)[1];
        if (quantidadeCalculos != null) {
            processo.setQuantidadeCalculos(quantidadeCalculos.intValue());
        }
        if (quantidadeEfetivacoes != null && quantidadeEfetivacoes.compareTo(BigDecimal.ZERO) > 0) {
            processo.setEfetivado(ProcessoCalculoIPTU.Efetivado.TOTAL);
        }
    }
}

