package br.com.webpublico.relatoriofacade;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.DetalhadoResumido;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by mga on 30/06/2017.
 */
@Stateless
public class RelatorioNotificacaoCobrancaFacade implements Serializable {


    private static final Logger log = LoggerFactory.getLogger(RelatorioNotificacaoCobrancaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;

    public RelatorioNotificacaoCobrancaFacade() {
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteRelatorioNotificacaoCobranca> buscarNotificacaoCobrancaPorContribuinte(FiltroMalaDiretaGeral filtro, String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(filtro.getTipoRelatorio().equals(DetalhadoResumido.DETALHADO) ? " select " : " select distinct ")
            .append(" formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as inscricao, ")
            .append(" formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpfCnpj, ")
            .append(" coalesce(pf.nome, pj.razaosocial) as contribuinte, ")
            .append(" pvd.id as idParcela ")
            .append(" from parcelavalordivida pvd ")
            .append(getJoinsComuns())
            .append(" inner join pessoa p on p.id = item.pessoa_id ")
            .append(" left join pessoaFisica pf on pf.id = p.id ")
            .append(" left join pessoaJuridica pj on pj.id = p.id ")
            .append(condicao);
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = q.getResultList();
        List<NotificacaoCobrancaMalaDireta> retorno = Lists.newArrayList();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                NotificacaoCobrancaMalaDireta item = new NotificacaoCobrancaMalaDireta();
                item.setTipoCadastroTributario(TipoCadastroTributario.PESSOA);
                preencherObjeto(obj, item, retorno);
            }
        }
        return new AsyncResult<>(new AssistenteRelatorioNotificacaoCobranca(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteRelatorioNotificacaoCobranca> buscarNotificacaoCobrancaIPorCadastroImobiliario(FiltroMalaDiretaGeral filtro, String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(filtro.getTipoRelatorio().equals(DetalhadoResumido.DETALHADO) ? " select " : " select distinct ")
            .append(" imo.inscricaocadastral as inscricao, ")
            .append(" formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpfCnpj, ")
            .append(" coalesce(pf.nome, pj.razaosocial) as nome, ")
            .append(" pvd.id as idParcela ")
            .append(" from parcelavalordivida pvd ")
            .append(getJoinsComuns())
            .append(" inner join cadastroimobiliario imo on item.cadastro_id = imo.id and coalesce(imo.ativo,0) = 1 ")
            .append(" inner join propriedade prop on prop.imovel_id = imo.id and prop.id = (select max(p.id) from propriedade p where p.imovel_id = imo.id and current_date BETWEEN p.iniciovigencia and coalesce(p.finalvigencia, CURRENT_DATE)) ")
            .append(" inner join vwenderecobci vwend on vwend.cadastroimobiliario_id = imo.id ")
            .append(" inner join pessoa p on p.id = prop.pessoa_id ")
            .append(" left join  pessoaFisica pf on pf.id = p.id ")
            .append(" left join  pessoaJuridica pj on pj.id = p.id ");
        sql.append(condicao);

        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = q.getResultList();
        List<NotificacaoCobrancaMalaDireta> retorno = Lists.newArrayList();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                NotificacaoCobrancaMalaDireta item = new NotificacaoCobrancaMalaDireta();
                item.setTipoCadastroTributario(TipoCadastroTributario.IMOBILIARIO);
                preencherObjeto(obj, item, retorno);
            }
        }
        return new AsyncResult<>(new AssistenteRelatorioNotificacaoCobranca(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteRelatorioNotificacaoCobranca> buscarNotificacaoCobrancaPorCadastroEconomico(FiltroMalaDiretaGeral filtro, String condicao) {
        StringBuilder sql = new StringBuilder();
        Map<String, Object> parameters = Maps.newHashMap();
        sql.append(filtro.getTipoRelatorio().equals(DetalhadoResumido.DETALHADO) ? " select " : " select distinct ")
            .append(" cmc.inscricaocadastral as inscricao, ")
            .append(" formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpfcnpj, ")
            .append(" coalesce(pf.nome, pj.razaosocial) as nome, ")
            .append(" pvd.id as idParcela ")
            .append(" from parcelavalordivida pvd ")
            .append(getJoinsComuns())
            .append(" inner join cadastroeconomico cmc on item.cadastro_id = cmc.id ")
            .append(" inner join pessoa p on p.id = cmc.pessoa_id ")
            .append(" left join  pessoafisica pf on pf.id = p.id ")
            .append(" left join  pessoajuridica pj on pj.id = p.id ");
        sql.append(condicao);

        Query q = em.createNativeQuery(sql.toString());
        for (String parametro : parameters.keySet()) {
            q.setParameter(parametro, parameters.get(parametro));
        }
        List<Object[]> lista = q.getResultList();
        List<NotificacaoCobrancaMalaDireta> retorno = Lists.newArrayList();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                NotificacaoCobrancaMalaDireta item = new NotificacaoCobrancaMalaDireta();
                item.setTipoCadastroTributario(TipoCadastroTributario.ECONOMICO);
                preencherObjeto(obj, item, retorno);

            }
        }
        return new AsyncResult<>(new AssistenteRelatorioNotificacaoCobranca(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteRelatorioNotificacaoCobranca> buscarNotificacaoCobrancaPorCadastroRural(FiltroMalaDiretaGeral filtro, String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(filtro.getTipoRelatorio().equals(DetalhadoResumido.DETALHADO) ? " select " : " select distinct ")
            .append(" rural.codigo as codigoRural, ")
            .append(" formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpfCnpj, ")
            .append(" coalesce(pf.nome, pj.razaosocial) as contribuinte, ")
            .append(" pvd.id as idParcela ")
            .append(" from parcelavalordivida pvd ")
            .append(getJoinsComuns())
            .append(" inner join cadastrorural rural on item.cadastro_id = rural.id ")
            .append(" inner join vwenderecobcr vwend on vwend.cadastrorural_id = rural.id ")
            .append(" inner join propriedaderural prop on prop.imovel_id = rural.id ")
            .append(" inner join pessoa p on p.id = prop.pessoa_id ")
            .append(" left join  pessoaFisica pf on pf.id = p.id ")
            .append(" left join  pessoaJuridica pj on pj.id = p.id ");
        sql.append(condicao);
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = q.getResultList();
        List<NotificacaoCobrancaMalaDireta> retorno = Lists.newArrayList();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                NotificacaoCobrancaMalaDireta item = new NotificacaoCobrancaMalaDireta();
                item.setTipoCadastroTributario(TipoCadastroTributario.RURAL);
                preencherObjeto(obj, item, retorno);
            }
        }
        return new AsyncResult<>(new AssistenteRelatorioNotificacaoCobranca(retorno));
    }

    private StringBuilder getJoinsComuns() {
        StringBuilder sql = new StringBuilder();
        sql.append(" inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id ")
            .append(" inner join valordivida vd on vd.id = pvd.valordivida_id ")
            .append(" inner join calculo calc on calc.id = vd.calculo_id ")
            .append(" inner join parcelamaladiretageral parc on parc.parcela_id = pvd.id ")
            .append(" inner join itemmaladiretageral item on item.id = parc.itemmaladiretageral_id ")
            .append(" inner join maladiretageral mala on mala.id = item.maladiretageral_id ")
            .append(" left join dam on dam.id = item.dam_id ")
            .append(" left join itemlotebaixa itLote on itlote.dam_id = dam.id ")
            .append(" left join lotebaixa lote on lote.id = itlote.lotebaixa_id ");
        return sql;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ByteArrayOutputStream> gerarRelatorio(AssistenteGeracaoRelatorio assistenteRelatorio, List<NotificacaoCobrancaMalaDireta> resultado, UnidadeOrganizacional unidadeOrganizacional) throws IOException, JRException {

        ByteArrayOutputStream byteArrayOutputStream;
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            HashMap parameters = new HashMap();
            parameters.put("USUARIO", assistenteRelatorio.getUsuario());
            parameters.put("BRASAO", assistenteRelatorio.getCaminhoImagem());
            parameters.put("FILTROS", assistenteRelatorio.getFiltros());
            parameters.put("SUBREPORT_DIR", assistenteRelatorio.getCaminhoReport());
            parameters.put("LABEL_INSCRICAO", assistenteRelatorio.getLabeInscricao());
            parameters.put("TIPO_CADASTRO", assistenteRelatorio.getTipoCadastroTributario().name());
            parameters.put("NOME_PREFEITURA", "PREFEITURA MUNICIAPAL DE RIO BRANCO");
            parameters.put("NOME_SECRETARIA", "SECRETARIA MUNICIPAL DE DESENVOLVIMENTO ECONÔMICO E FINANÇAS");
            parameters.put("NOME_RELATORIO", assistenteRelatorio.getNomeRelatorio());
            Collections.sort(resultado);
            JasperPrint jasperPrint = abstractReport.gerarBytesDeRelatorioComDadosEmCollectionView(
                assistenteRelatorio.getCaminhoReport(),
                assistenteRelatorio.getArquivoJasper(),
                parameters,
                new JRBeanCollectionDataSource(resultado), unidadeOrganizacional);
            byteArrayOutputStream = abstractReport.exportarJasperParaPDF(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new AsyncResult<>(byteArrayOutputStream);
    }

    private void preencherObjeto(Object[] registro, NotificacaoCobrancaMalaDireta item, List<NotificacaoCobrancaMalaDireta> retorno) {
        item.setInscricaoCadastral((String) registro[0]);
        item.setCpfCnpj((String) registro[1]);
        item.setNomeContribuinte((String) registro[2]);
        item.setIdCadastro(((BigDecimal) registro[3]).longValue());
        retorno.add(item);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 12)
    public Future<AssistenteRelatorioNotificacaoCobranca> atualizarValores(FiltroMalaDiretaGeral filtro,
                                                                           AssistenteBarraProgresso assistente,
                                                                           List<NotificacaoCobrancaMalaDireta> notificacoes) {
        for (NotificacaoCobrancaMalaDireta obj : notificacoes) {
            atualizarValor(obj, filtro);
            assistente.conta();
        }
        return new AsyncResult<>(new AssistenteRelatorioNotificacaoCobranca(notificacoes));
    }

    private void atualizarValor(NotificacaoCobrancaMalaDireta notificacao, FiltroMalaDiretaGeral filtro) {
        try {
            ConsultaParcela consulta = new ConsultaParcela();
            adicionarParamentroAndOrdenacaoConsultaParcela(notificacao, consulta);
            consulta.executaConsulta();
            notificacao.zerarValores();
            ResultadoParcela resultado = consulta.getResultados().get(0);
            notificacao.setTotalLancado(resultado.getValorTotal());
            notificacao.setDivida(resultado.getDivida());
            notificacao.setSituacaoPagamento(resultado.getSituacaoDescricaoEnum());
            notificacao.setDataLancamento(resultado.getEmissao());
            notificacao.setDataVencimento(resultado.getVencimento());
            notificacao.setReferenciaDebito(resultado.getReferencia());
            notificacao.setParcela(resultado.getParcela());

            if (DetalhadoResumido.DETALHADO.equals(filtro.getTipoRelatorio())) {
                notificacao.setAtraso(resultado.getDiasAtraso());
                notificacao.setImposto(resultado.getValorImposto());
                notificacao.setTaxa(resultado.getValorTaxa());
                notificacao.setCorrecao(resultado.getValorCorrecao());
                notificacao.setJuros(resultado.getValorJuros());
                notificacao.setMulta(resultado.getValorMulta());
                notificacao.setHonorarios(resultado.getValorHonorarios());
            }
        } catch (Exception e) {
            log.error("Não foi possível calcular os valores" + e.getMessage());
        }
    }

    private void adicionarParamentroAndOrdenacaoConsultaParcela(NotificacaoCobrancaMalaDireta notificacao, ConsultaParcela consulta) {
        adicionarParametroConsultaParcela(notificacao, consulta);
    }

    private void adicionarParametroConsultaParcela(NotificacaoCobrancaMalaDireta notificacao, ConsultaParcela consulta) {
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, notificacao.getIdCadastro());
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public TipoAutonomoFacade getTipoAutonomoFacade() {
        return tipoAutonomoFacade;
    }

    public NaturezaJuridicaFacade getNaturezaJuridicaFacade() {
        return naturezaJuridicaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

}
