/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.ExtratoControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.consultaparcela.CalculadorAcrescimos;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author gustavo
 */
@Stateless
public class ExtratoFacade {

    private static final Logger logger = LoggerFactory.getLogger(ExtratoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoAcrescimosFacade acrescimosFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public void setCadastroEconomicoFacade(CadastroEconomicoFacade cadastroEconomicoFacade) {
        this.cadastroEconomicoFacade = cadastroEconomicoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public void setCadastroImobiliarioFacade(CadastroImobiliarioFacade cadastroImobiliarioFacade) {
        this.cadastroImobiliarioFacade = cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public void setCadastroRuralFacade(CadastroRuralFacade cadastroRuralFacade) {
        this.cadastroRuralFacade = cadastroRuralFacade;
    }

    public ContratoRendasPatrimoniaisFacade getContratoRendasPatrimoniaisFacade() {
        return contratoRendasPatrimoniaisFacade;
    }

    public void setContratoRendasPatrimoniaisFacade(ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade) {
        this.contratoRendasPatrimoniaisFacade = contratoRendasPatrimoniaisFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public void setDividaFacade(DividaFacade dividaFacade) {
        this.dividaFacade = dividaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public void setExercicioFacade(ExercicioFacade exercicioFacade) {
        this.exercicioFacade = exercicioFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public void setPessoaFacade(PessoaFacade pessoaFacade) {
        this.pessoaFacade = pessoaFacade;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroRuralFacade.listaFiltrandoPorCodigo(parte.trim());
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return contratoRendasPatrimoniaisFacade.listaFiltrando(parte.trim(), "numeroContrato");
    }

    public List<Exercicio> completaExercicio(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    public List<Pessoa> completaPessoas(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public String teste() {
        return " Opa !!!! ";
    }

    public List<ParcelaValorDivida> recuperaParcelas(String where) {
        String sql = "SELECT parcela.id, ((situacao.saldo/coalesce(moedaDataParcela.valor,1)) * coalesce(moedaAtual.valor,1)), parcela.vencimento, valordivida.divida_id "
            + " FROM parcelavalordivida parcela"
            + " LEFT JOIN temp_acrescimos ac ON ac.parcela_id = parcela.id "
            + " INNER JOIN valordivida ON valordivida.id = parcela.valordivida_id"
            + " INNER JOIN situacaoparcelavalordivida situacao ON situacao.id = parcela.situacaoatual_id "
            + " INNER JOIN calculo ON calculo.id = valordivida.calculo_id"
            + " INNER JOIN cadastro ON cadastro.id = calculo.cadastro_id"
            + " INNER JOIN divida ON divida.id = valordivida.divida_id"
            + " INNER JOIN exercicio ON exercicio.id = valordivida.exercicio_id"
            + " INNER JOIN opcaopagamento op ON op.id = parcela.opcaopagamento_id"
            + " INNER JOIN calculopessoa cp ON cp.calculo_id = calculo.id"
            + " INNER JOIN pessoa ON pessoa.id = cp.pessoa_id"
            + " LEFT JOIN pessoafisica pf ON pf.id = pessoa.id"
            + " LEFT JOIN pessoajuridica pj ON pj.id = pessoa.id"
            + " LEFT JOIN cadastroimobiliario bci ON bci.id = cadastro.id"
            + " LEFT JOIN lote ON lote.id = bci.lote_id"
            + " LEFT JOIN quadra ON quadra.id = lote.quadra_id"
            + " LEFT JOIN cadastroeconomico cmc ON cmc.id = cadastro.id"
            + " LEFT JOIN cadastrorural bcr ON bcr.id = cadastro.id"
            + " LEFT JOIN processoparcelamento parcelamento ON parcelamento.id = calculo.id"
            + " LEFT JOIN iteminscricaodividaativa itemda ON itemda.id = calculo.id "
            + " LEFT JOIN inscricaodividaativa da ON da.id = itemda.inscricaodividaativa_id"
            + " LEFT JOIN inscricaodividaparcela idporiginal ON idporiginal.parcelavalordivida_id = parcela.id"
            + " LEFT JOIN iteminscricaodividaativa itemdaoriginal ON itemdaoriginal.id = idporiginal.iteminscricaodividaativa_id "
            + " LEFT JOIN inscricaodividaativa daoriginal ON daoriginal.id = itemdaoriginal.inscricaodividaativa_id"
            + " LEFT JOIN itemdam itemdam on itemdam.parcela_id = parcela.id "
            + " LEFT JOIN dam dam ON itemdam.dam_id = dam.id "
            + " LEFT JOIN itemlotebaixa itemlote ON itemlote.dam_id = dam.id"
            + " LEFT JOIN lotebaixa ON lotebaixa.id = itemlote.lotebaixa_id"
            + " LEFT JOIN banco ON banco.id = lotebaixa.banco_id"
            + " LEFT JOIN itemprocessodebito ON itemprocessodebito.parcela_id = parcela.id "
            + " LEFT JOIN processodebito ON processodebito.id = itemprocessodebito.processodebito_id"
            + " right join moeda moedaDataParcela on moedaDataParcela.mes = TO_NUMBER(TO_CHAR(parcela.dataRegistro,'MM')) and moedaDataParcela.ano = exercicio.ano "
            + " left join indiceeconomico indiceDataParcela on indiceDataParcela.descricao ='UFM' and indiceDataParcela.id = moedaDataParcela.indiceeconomico_id "
            + " right join moeda moedaAtual on moedaAtual.mes = TO_NUMBER(TO_CHAR(sysdate,'MM')) and moedaAtual.ano = TO_NUMBER(TO_CHAR(sysdate,'yyyy')) "
            + " left join indiceeconomico indiceAtual on indiceAtual.descricao ='UFM' and indiceAtual.id = moedaAtual.indiceeconomico_id "
            + " WHERE (dam.id is null or dam.situacao = 'ABERTO') "
            + where;
        Query q = em.createNativeQuery(sql);
        List<Object[]> listaArrayObjetos = q.getResultList();
        Object[] ob;
        List<ParcelaValorDivida> listaEntidadeNaoMapeada = new ArrayList<>();
        for (int i = 0; i < listaArrayObjetos.size(); i++) {
            ob = listaArrayObjetos.get(i);
            ParcelaValorDivida p = new ParcelaValorDivida(((BigDecimal) ob[0]).longValue(), (BigDecimal) ob[1], (Date) ob[2], ((BigDecimal) ob[3]).longValue());
            listaEntidadeNaoMapeada.add(p);
        }

        if (listaEntidadeNaoMapeada != null && listaEntidadeNaoMapeada.size() > 0) {
            for (ParcelaValorDivida parcela : listaEntidadeNaoMapeada) {
                parcela = CalculadorAcrescimos.calculaAcrescimo(parcela, new Date(), parcela.getValor(), dividaFacade.configuracaoVigente(parcela.getDividaId()));
            }
            return listaEntidadeNaoMapeada;
        }
        return new ArrayList<>();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public void geraExtrato(ExtratoControlador controlador) {
        controlador.montaCondicao();
        HashMap parameters = new HashMap();
        parameters.put("BRASAO", controlador.getBrasao());
        parameters.put("FILTROS", controlador.getFiltros().toString());
        parameters.put("USUARIO", sistemaFacade.getLogin());
        parameters.put("CONDICAO", controlador.getWhere().toString());
        Connection con = controlador.recuperaConexaoJDBC();
        try {
//            con.setAutoCommit(false);
//            List<ParcelaValorDivida> parcelas = recuperaParcelas(controlador.getWhere().toString());
//            AtualizaAcrescimoParcela.atualizaParcelaJDBC(parcelas, con);
            gerarReport(parameters, con, controlador);
        } catch (Exception ex) {
            logger.error("Erro:", ex);
        } finally {
            AbstractReport.fecharConnection(con);
        }
        controlador.setGerando(false);
    }

    public void gerarReport(HashMap parametros, Connection con, ExtratoControlador controlador)
        throws JRException, IOException {
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        //gera relatorio com as classes do jasper
        JasperPrint jasperPrint = JasperFillManager.fillReport(controlador.getReport(), parametros, con);
        controlador.setDadosByte(new ByteArrayOutputStream());
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, controlador.getDadosByte());
        exporter.exportReport();
    }
}
