package br.com.webpublico.controle.portaltransparencia;

import br.com.webpublico.controle.portaltransparencia.entidades.*;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidadesauxiliares.VOBemPortal;
import br.com.webpublico.entidadesauxiliares.administrativo.ProgressaoLanceCertame;
import br.com.webpublico.entidadesauxiliares.rh.CedenciaPortal;
import br.com.webpublico.entidadesauxiliares.rh.portal.FuncaoGratificadaPortal;
import br.com.webpublico.entidadesauxiliares.rh.portal.VinculoServidorPortal;
import br.com.webpublico.enums.*;
import br.com.webpublico.esocial.service.EmpregadorService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by romanini on 25/08/15.
 */
@Stateless(name = "PortalTransparenciaNovoFacade", mappedName = "ejb/PortalTransparenciaNovoFacade")
public class PortalTransparenciaNovoFacade extends AbstractFacade<PrefeituraPortal> implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(EmpregadorService.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ResponsavelUnidadeFacade responsavelUnidadeFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private PrestacaoDeContasFacade prestacaoDeContasFacade;
    @EJB
    private ProvisaoPPADespesaFacade provisaoPPADespesaFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private EmpenhoEstornoFacade empenhoEstornoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private LiquidacaoEstornoFacade liquidacaoEstornoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private PagamentoEstornoFacade pagamentoEstornoFacade;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;
    @EJB
    private ConfiguracaoTituloSistemaFacade configuracaoTituloSistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    @EJB
    private ConvenioReceitaFacade convenioReceitaFacade;
    @EJB
    private ConvenioDespesaFacade convenioDespesaFacade;
    @EJB
    private PortalTransparenciaFacade portalTransparenciaFacade;
    @EJB
    private PortalTransparenciaNovoSingleton singleton;
    @EJB
    private DocumentoLicitacaoFacade documentoLicitacaoFacade;
    @EJB
    private LiberacaoCotaFinanceiraFacade liberacaoCotaFinanceiraFacade;
    @EJB
    private EstornoLibCotaFinanceiraFacade estornoLibCotaFinanceiraFacade;
    @EJB
    private TransferenciaContaFinanceiraFacade transferenciaContaFinanceiraFacade;
    @EJB
    private EstornoTransferenciaFacade estornoTransferenciaFacade;
    @EJB
    private EstornoAlteracaoOrcFacade estornoAlteracaoOrcFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private MetasFiscaisFacade metasFiscaisFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private ObraFacade obraFacade;
    @EJB
    private CalamidadePublicaFacade calamidadePublicaFacade;
    @EJB
    private DicionarioDeDadosFacade dicionarioDeDadosFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private RegistroSolicitacaoMaterialExternoFacade registroSolicitacaoMaterialExternoFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private CotacaoFacade cotacaoFacade;
    @EJB
    private StatusFornecedorLicitacaoFacade statusFornecedorLicitacaoFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private LDOFacade ldoFacade;
    @EJB
    private GlossarioPortalFacade glossarioPortalFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private LicitacaoDoctoFornecedorFacade licitacaoDoctoFornecedorFacade;
    @EJB
    private EmendaFacade emendaFacade;

    private Map<UnidadeOrganizacional, HierarquiaOrganizacional> mapaUnidades;

    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeBase64URLSafeString(imageByteArray);
    }

    public PortalTransparenciaNovoFacade() {
        super(PrefeituraPortal.class);
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<PortalTransparenciaNovo> enviar(PortalTransparenciaNovo selecionado) {
        singleton.inicializar(selecionado);
        enviarMovimentos();
        return new AsyncResult<>(selecionado);
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<PortalTransparenciaNovo> enviarObjetos(PortalTransparenciaNovo selecionado) {
        singleton.inicializar(selecionado);
        addMensagem("<b>Iniciando envio manual");
        addMensagem("<b>Recuperou " + selecionado.getObjetos().size() + " movimentos para sincronizar! </b>");
        ordenarMovimentos(selecionado.getObjetos());
        addMensagem("<b> Preparando as informações .... </b>");
        movimentarEntidadePortalTransparencia(selecionado.getObjetos());
        for (EntidadePortalTransparencia objeto : selecionado.getObjetos()) {
            sincronizar(objeto);

        }
        return new AsyncResult<>(selecionado);
    }

    public List<SuperEntidade> recuperarObjetosHql(String hql) {
        Query consulta = em.createQuery(hql, SuperEntidade.class);
        return consulta.getResultList();
    }

    public void buscarDadosHql(PortalTransparenciaNovo selecionado) {
        List<SuperEntidade> superEntidades = recuperarObjetosHql(selecionado.getHql());
        selecionado.setObjetos(Lists.<EntidadePortalTransparencia>newArrayList());
        switch (selecionado.getTipo()) {
            case EMPENHO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new EmpenhoPortal((Empenho) superEntidade));
                }
                break;
            case EMPENHO_ESTORNO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new EmpenhoEstornoPortal((EmpenhoEstorno) superEntidade));
                }
                break;
            case LIQUIDACAO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new LiquidacaoPortal((Liquidacao) superEntidade));
                }
                break;
            case LIQUIDACAO_ESTORNO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new LiquidacaoEstornoPortal((LiquidacaoEstorno) superEntidade));
                }
                break;
            case PAGAMENTO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new PagamentoPortal((Pagamento) superEntidade));
                }
                break;
            case PAGAMENTO_ESTORNO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new PagamentoEstornoPortal((PagamentoEstorno) superEntidade));
                }
                break;
            case RECEITA_REALIZADA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new LancamentoReceitaOrcPortal((LancamentoReceitaOrc) superEntidade));
                }
                break;
            case RECEITA_REALIZADA_ESTORNO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new ReceitaOrcEstornoPortal((ReceitaORCEstorno) superEntidade));
                }
                break;
            case ATO_LEGAL:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new AtoLegalPortal((AtoLegal) superEntidade));
                }
                break;
            case CALAMIDADE_PUBLICA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new CalamidadePublicaPortal((CalamidadePublica) superEntidade));
                }
                break;
            case LICITACAO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new LicitacaoPortal((Licitacao) superEntidade));
                }
                break;
            case CONTRATO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new ContratoPortal((Contrato) superEntidade));
                }
                break;
            case ALTERACAO_ORCAMENTARIA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new AlteracaoOrcPortal((AlteracaoORC) superEntidade));
                }
                break;
            case ALTERACAO_ORCAMENTARIA_ESTORNO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new AlteracaoOrcEstornoPortal((EstornoAlteracaoOrc) superEntidade));
                }
                break;
            case DIARIA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new DiariaPortal((PropostaConcessaoDiaria) superEntidade));
                }
                break;
            case CONVENIO_RECEITA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new ConvenioReceitaPortal((ConvenioReceita) superEntidade));
                }
                break;
            case CONVENIO_DESPESA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new ConvenioDespesaPortal((ConvenioDespesa) superEntidade));
                }
                break;
            case DISPENSA_LICITACAO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new DispensaLicitacaoPortal((DispensaDeLicitacao) superEntidade));
                }
                break;
            case ATA_REGISTRO_PRECO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new AtaRegistroPrecoPortal((AtaRegistroPreco) superEntidade));
                }
                break;
            case ATA_REGISTRO_PRECO_EXTERNO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new AtaRegistroDePrecoExternoPortal((RegistroSolicitacaoMaterialExterno) superEntidade));
                }
                break;
            case TRANSFERENCIA_CONTA_FINANCEIRA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new TransferenciaContaFinanceiraPortal((TransferenciaContaFinanceira) superEntidade));
                }
                break;
            case TRANSFERENCIA_CONTA_FINANCEIRA_ESTORNO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new EstornoTransferenciaPortal((EstornoTransferencia) superEntidade));
                }
                break;
            case LIBERACAO_COTA_FINANCEIRA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new LiberacaoCotaFinanceiraPortal((LiberacaoCotaFinanceira) superEntidade));
                }
                break;
            case LIBERACAO_COTA_FINANCEIRA_ESTORNO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new EstornoLiberacaoCotaPortal((EstornoLibCotaFinanceira) superEntidade));
                }
                break;
            case OBRA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new ObraPortal((Obra) superEntidade));
                }
                break;
            case DICIONARIO_DE_DADOS:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new DicionarioDeDadosPortal((DicionarioDeDados) superEntidade));
                }
                break;
            case PPA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new PPAPortal((PPA) superEntidade));
                }
                break;
            case LDO:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new LDOPortal((LDO) superEntidade));
                }
                break;
            case LOA:
                for (SuperEntidade superEntidade : superEntidades) {
                    selecionado.getObjetos().add(new LOAPortal((LOA) superEntidade));
                }
                break;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void enviarMovimentos() {
        enviarExercicio();
        enviarUnidades();
        recuperarEnviarObjetos();
        finalizar();
    }

    public void finalizar() {
        addMensagem("<b>... Finalizado ...</b>");
        atribuirCalculando(false);
    }

    private void enviarListDeObjetos(String nome) {
        if (!getObjetos().isEmpty()) {
            if (!nome.trim().isEmpty()) {
                addMensagem("Enviando " + getObjetos().size() + " " + nome + " ....");
            }
            for (ObjetoPortalTransparencia objetoPortalTransparencia : getObjetos()) {
                if (!singleton.getSelecionado().isCalculando()) {
                    break;
                }
                addMensagemRetorno(objetoPortalTransparencia.getObjeto(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + objetoPortalTransparencia.getUrl(), objetoPortalTransparencia.getJsonObject(), objetoPortalTransparencia.getMetodo()));
            }
            getObjetos().clear();
        }
    }

    private void recuperarEnviarObjetos() {
        addMensagem("<b> Recuperando informações para sincronização do portal da transparência .... </b></br>");
        recuperarAtosLegais();
        enviarListDeObjetos("Atos legais");
        recupearPlanejamento();
        enviarListDeObjetos(" do orçamento");
        recuperarReceitaOrcamentaria();
        enviarListDeObjetos(" da receita do orçamento");
        recuperarAlteracaoOrcamentaria();
        enviarListDeObjetos(" de alteração orçamentária");
        recuperarExecucaoOrcamentaria();
        enviarListDeObjetos(" da execução orçamentária");
        recuperarExecucaoOrcamentariaResto();
        enviarListDeObjetos(" de restos");
        recuperarDiarias();
        enviarListDeObjetos(" de diárias");
        recuperarReceitaRealizada();
        enviarListDeObjetos(" de receita realizada");
        recuperarDadosPrefeitura();
        enviarListDeObjetos(" da Prefeitura");
        recuperarLicitacao();
        enviarListDeObjetos(" de licitação");
        recuperarContratos();
        enviarListDeObjetos(" de contrato");
        recuperarLinks();
        enviarListDeObjetos(" de links");
        recuperarGlossarios();
        enviarListDeObjetos(" de Glossários");
        recuperarAnexos();
        enviarListDeObjetos(" de anexos");
        recuperarConvenio();
        enviarListDeObjetos(" de convênios");
        recuperarFinanceiro();
        enviarListDeObjetos(" de Financeiro");
        recuperarDadosRH();
        enviarListDeObjetos(" de Obras");
        recuperarObras();
        enviarListDeObjetos(" de Dicionário de Dados");
        recuperarDicionarioDeDados();
        enviarListDeObjetos(" de Bens");
        recuperarBens();
        enviarListDeObjetos(" de Calamidade Pública");
        recuperarCalamidadesPublicas();
        enviarListDeObjetos(" de PPA");
        recuperarPPA();
        enviarListDeObjetos(" de LDO");
        recuperarLDO();
        enviarListDeObjetos(" de LOA");
        recuperarLOA();
        enviarListDeObjetos(" de Emenda");
        recuperarEmendaParlamentar();
        enviarListDeObjetos(" Institucional");
        recuperarInstitucional();
    }

    private void recuperarEmendaParlamentar() {
        if (singleton.getSelecionado().getEnviarEmendaParlamentar()) {
            List<Emenda> emendas = emendaFacade.buscarTodasEmendasPorExercicio();
            for (Emenda emenda : emendas) {
                sincronizarEmenda(emenda);
            }
        }
    }

    private void sincronizarEmenda(Emenda emenda) {
        JSONObject conteudo = montarJSONEmenda(emenda);
        addMensagemRetorno(emenda, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_EMENDA_PARLAMENTAR, conteudo, UtilPortalTransparencia.METHOD_POST));
    }

    private JSONObject montarJSONEmenda(Emenda emenda) {
        if (!singleton.contemObjeto(emenda)) {
            emenda = emendaFacade.recuperar(emenda.getId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("numero", emenda.getNumero());
            jsonObject.put("descricao", emenda.getDescricaoDetalhada());
            enviarJsonPessoa(emenda.getVereador().getPessoa());
            jsonObject.put("autor", montarConteudoJSONPessoa(emenda.getVereador().getPessoa()));
            BigDecimal valor = BigDecimal.ZERO;
            for (SuplementacaoEmenda suplementacaoEmenda : emenda.getSuplementacaoEmendas()) {
                valor = valor.add(suplementacaoEmenda.getValor());
            }
            jsonObject.put("valor", valor);
            jsonObject.put("objetoResumido", emenda.getObjetoResumido());
            jsonObject.put("status", emenda.getStatusCamara().getDescricao());
            if (!emenda.getSuplementacaoEmendas().isEmpty()) {
                UnidadeOrganizacional unidadeOrganizacional = emenda.getSuplementacaoEmendas().get(0).getUnidadeOrganizacional();
                HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(unidadeOrganizacional, emenda.getDataEmenda());
                jsonObject.put("unidade", montarConteudoUnidade(hierarquiaDaUnidade));
            }
            jsonObject.put("exercicio", getConteudoJSONExercicio(emenda.getExercicio()));
            jsonObject.put("data", emenda.getDataEmenda() == null ? "" : emenda.getDataEmenda().getTime());
            singleton.adicionarJsonObjet(emenda, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(emenda);
        }
    }

    private void recuperarInstitucional() {
        if (singleton.getSelecionado().getEnviarInstitucional()) {
            List<HierarquiaOrganizacional> hierarquiaOrganizacionals = hierarquiaOrganizacionalFacade.filtraPorNivelTrazendoTodas("", "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), singleton.getSelecionado().getDataOperacao());
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiaOrganizacionals) {
                sincronizarInstitucional(hierarquiaOrganizacional);
            }
        }
    }

    private void sincronizarInstitucional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(hierarquiaOrganizacional.getSubordinada(), singleton.getSelecionado().getDataOperacao());
        if (entidade != null) {
            JSONObject jsonUnidade = montarConteudoLotacao(hierarquiaOrganizacional, entidade.getTipoAdministracao());
            Endereco endereco = hierarquiaOrganizacional.getSubordinada().getEndereco();
            if (endereco != null) {
                jsonUnidade.put("endereco", endereco.getLogradouro());
            } else {
                String descricaoEndereco = "";
                if (hierarquiaOrganizacional.getSubordinada().getEnderecoDesc() != null) {
                    descricaoEndereco += hierarquiaOrganizacional.getSubordinada().getEnderecoDesc();
                }
                if (hierarquiaOrganizacional.getSubordinada().getCidade() != null) {
                    descricaoEndereco += " - " + hierarquiaOrganizacional.getSubordinada().getCidade();
                }
                if (hierarquiaOrganizacional.getSubordinada().getCep() != null) {
                    descricaoEndereco += " - " + hierarquiaOrganizacional.getSubordinada().getCep();
                }
                if (hierarquiaOrganizacional.getSubordinada().getUf() != null) {
                    descricaoEndereco += " - " + hierarquiaOrganizacional.getSubordinada().getUf();
                }
                jsonUnidade.put("endereco", descricaoEndereco);
            }
            jsonUnidade.put("email", hierarquiaOrganizacional.getSubordinada().getEmail());
            jsonUnidade.put("contato", hierarquiaOrganizacional.getSubordinada().getContato());
            jsonUnidade.put("horarioFuncionamento", hierarquiaOrganizacional.getSubordinada().getHorarioFuncionamento());
            jsonUnidade.put("observacao", hierarquiaOrganizacional.getSubordinada().getObservacao());
            addMensagemRetorno(hierarquiaOrganizacional, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_LOTACAO, jsonUnidade, "POST"));

            UnidadeOrganizacional unidade = unidadeOrganizacionalFacade.recuperar(hierarquiaOrganizacional.getSubordinada().getId());
            enviarJSONResponsavelUnidade(unidade, jsonUnidade);
            enviarJSONAnexoUnidade(unidade, jsonUnidade);
        }
    }

    private void enviarJSONAnexoUnidade(UnidadeOrganizacional unidade, JSONObject jsonUnidade) {
        for (UnidadeOrganizacionalAnexo anexo : unidade.getAnexos()) {

            if (anexo.getDetentorArquivoComposicao() != null) {
                for (ArquivoComposicao arquivoComposicao : anexo.getDetentorArquivoComposicao().getArquivosComposicao()) {
                    if (arquivoComposicao.getArquivo() != null) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("lotacao", jsonUnidade);
                        jsonObject.put("descricao", anexo.getDescricao());
                        jsonObject.put("observacao", anexo.getObservacao());
                        jsonObject.put("arquivo", montarJsonArquivo(arquivoComposicao.getArquivo()));
                        addMensagemRetorno(arquivoComposicao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_ANEXO_LOTACAO, jsonObject, "POST"));
                    }
                }
            }
        }
    }

    private void enviarJSONResponsavelUnidade(UnidadeOrganizacional unidade, JSONObject jsonUnidade) {
        for (ResponsavelUnidade responsavel : unidade.getResponsaveisUnidades()) {
            responsavel = responsavelUnidadeFacade.recuperar(responsavel.getId());
            JSONObject jsonResponsavel = new JSONObject();
            jsonResponsavel.put("lotacao", jsonUnidade);
            jsonResponsavel.put("curriculo", responsavel.getCurriculo());
            if (responsavel.getPessoa() != null) {
                enviarJsonPessoa(responsavel.getPessoa());
                jsonResponsavel.put("pessoa", montarConteudoJSONPessoa(responsavel.getPessoa()));
            }
            if (responsavel.getAtoLegal() != null) {
                sincronizarAtoLegal(responsavel.getAtoLegal());
                jsonResponsavel.put("atoLegal", montarJSONAtoLegal(responsavel.getAtoLegal()));
            }
            jsonResponsavel.put("inicioVigencia", responsavel.getInicioVigencia().getTime());
            jsonResponsavel.put("funcao", responsavel.getFuncao() != null ? responsavel.getFuncao().getDescricao() : null);
            jsonResponsavel.put("fimVigencia", responsavel.getFimVigencia() == null ? (" ") : responsavel.getFimVigencia().getTime());

            addMensagemRetorno(responsavel, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_RESPONSAVEL_LOTACAO, jsonResponsavel, "POST"));

            for (AgendaResponsavelUnidade agenda : responsavel.getAgenda()) {
                JSONObject jsonAgenda = new JSONObject();
                jsonAgenda.put("responsavel", jsonResponsavel);
                jsonAgenda.put("data", agenda.getData().getTime());
                jsonAgenda.put("horario", agenda.getHorario().getTime());
                jsonAgenda.put("compromisso", agenda.getCompromisso());
                jsonAgenda.put("local", agenda.getLocal());

                addMensagemRetorno(agenda, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_AGENDA_RESPONSAVEL_LOTACAO, jsonAgenda, "POST"));
            }
            montarJsonResponsavelUnidadeAnexo(responsavel, jsonResponsavel);
        }
    }

    private void montarJsonResponsavelUnidadeAnexo(ResponsavelUnidade responsavelUnidade, JSONObject json) {
        if (responsavelUnidade.getDetentorArquivoComposicao() != null) {
            for (ArquivoComposicao arquivoComposicao : responsavelUnidade.getDetentorArquivoComposicao().getArquivosComposicao()) {
                if (arquivoComposicao.getArquivo() != null) {
                    JSONObject jsonAnexoResponsavel = new JSONObject();
                    jsonAnexoResponsavel.put("responsavel", json);
                    jsonAnexoResponsavel.put("nomeArquivo", arquivoComposicao.getArquivo().getNome());
                    jsonAnexoResponsavel.put("arquivo", montarJsonArquivo(arquivoComposicao.getArquivo()));
                    addMensagemRetorno(arquivoComposicao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_ANEXO_RESPONSAVEL_LOTACAO, jsonAnexoResponsavel, UtilPortalTransparencia.METHOD_POST));
                }
            }
        }
    }

    private void recuperarLOA() {
        if (singleton.getSelecionado().getEnviarLOA()) {
            List<LOA> loas = loaFacade.buscarLoasEfetivadas(singleton.getSelecionado().getExercicio());
            for (LOA loa : loas) {
                sincronizarLoa(loa);
            }
        }
    }

    private void recuperarLDO() {
        if (singleton.getSelecionado().getEnviarLDO()) {
            List<LDO> ldos = ldoFacade.buscarLdosPorLoasEfetivadas(singleton.getSelecionado().getExercicio());
            for (LDO ldo : ldos) {
                sincronizarLdo(ldo);
            }
        }
    }

    private void recuperarPPA() {
        if (singleton.getSelecionado().getEnviarPPA()) {
            List<PPA> ppas = ppaFacade.buscarPpasPorLoasEfetivadas(singleton.getSelecionado().getExercicio());
            for (PPA ppa : ppas) {
                sincronizarPpa(ppa);
            }
        }
    }

    private void recuperarDadosRH() {
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente(singleton.getSelecionado().getDataOperacao());
        if (hasEnviarDadosRHAndBaseVencimentoConfigurado(configuracaoRH)) {
            addMensagem("<b> Recuperando Dados do RH. </b>");
            List<ServidorPortal> servidorPortals = folhaDePagamentoFacade.recuperarDadosPortalTransparencia(singleton.getSelecionado(), null);
            sincronizarServidorPortal(servidorPortals);
        } else {
            addMensagemErro("<b> Não foi localizado BaseFP para compor o vencimento base dos servidores no Portal de Transparência. </b>");
        }
        if (singleton.getSelecionado().getEnviarDadosRh()) {
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private boolean hasEnviarDadosRHAndBaseVencimentoConfigurado(ConfiguracaoRH configuracaoRH) {
        return singleton.getSelecionado().getEnviarDadosRh() && configuracaoRH.getVencimentoBasePortal() != null;
    }

    private void sincronizarServidorPortal(List<ServidorPortal> servidorPortals) {
        HashSet<UnidadeOrganizacional> unidades = new HashSet<>();
        HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos = new HashMap<>();
        HashSet<Cargo> cargos = new HashSet<>();
        HashSet<VinculoServidorPortal> todosVinculos = new HashSet<>();
        addMensagem("<b> Montando envio de " + servidorPortals.size() + " Servidores. </b>");
        prepararServidorPortalCargoLotacao(servidorPortals, unidades, cargos);
        sincronizarCargo(cargos);
        sincronizarLotacao(unidades, orgaos);
        sincronizarServidor(servidorPortals, unidades, orgaos, todosVinculos);
        sincronizarFuncaoGratificada(todosVinculos, orgaos);
    }

    private void sincronizarFuncaoGratificada(HashSet<VinculoServidorPortal> todosVinculos, HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos) {
        addMensagem("<b>Sincronizando os funções gratificadas. </b>");
        List<List<VinculoServidorPortal>> lista = Lists.partition(new ArrayList<VinculoServidorPortal>(todosVinculos), 1000);
        List<FuncaoGratificadaPortal> funcoesRecuperadas = Lists.newArrayList();
        for (List<VinculoServidorPortal> vinculos : lista) {
            funcoesRecuperadas.addAll(funcaoGratificadaFacade.buscarFuncaoGratificadaPortal(vinculos, singleton.getSelecionado().getDataOperacao()));
        }
        for (VinculoServidorPortal vinculo : todosVinculos) {
            for (FuncaoGratificadaPortal funcao : funcoesRecuperadas) {
                if (vinculo.getVinculoFP().getId().compareTo(funcao.getVinculoFPId().longValue()) == 0) {
                    vinculo.getFuncoes().add(funcao);
                }
            }
        }


        for (VinculoServidorPortal vinculoServidorPortal : todosVinculos) {
            for (FuncaoGratificadaPortal funcao : vinculoServidorPortal.getFuncoes()) {
                JSONObject jsonCategoria = montarJSONServidorCategoriaFuncaoGratificada(funcao.getCategoria());
                addMensagemRetorno(funcao.getCategoria(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_CATEGORIA_FUNCAO_GRATIFICADA, jsonCategoria, UtilPortalTransparencia.METHOD_POST));

                JSONObject jsonFuncao = montarJSONServidorFuncaoGratificada(funcao, vinculoServidorPortal.getServidorPortal(), orgaos);
                addMensagemRetorno(funcao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_FUNCAO_GRATIFICADA, jsonFuncao, UtilPortalTransparencia.METHOD_POST));
            }
        }
    }

    private void sincronizarServidor(List<ServidorPortal> servidorPortals, HashSet<UnidadeOrganizacional> unidades, HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos, HashSet<VinculoServidorPortal> todosVinculos) {
        addMensagem("<b>Sincronizando os servidores. </b>");
        for (ServidorPortal servidorPortal : servidorPortals) {
            addMensagemRetorno(servidorPortal.getRecurso(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_RECURSO_FP, montarJSONRecursoFP(servidorPortal.getRecurso()), UtilPortalTransparencia.METHOD_POST));
            sincronizarServidorPortal(servidorPortal, unidades, orgaos);
            todosVinculos.add(new VinculoServidorPortal(servidorPortal.getVinculo(), servidorPortal));
        }
    }

    private void sincronizarLotacao(HashSet<UnidadeOrganizacional> unidades, HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos) {
        addMensagem("<b>Sincronizando as lotações. </b>");
        for (UnidadeOrganizacional lotacao : unidades) {
            HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(lotacao, singleton.getSelecionado().getDataOperacao());
            if (orgao != null) {
                adicionarOrgao(orgaos, orgao, lotacao);
            }
        }
    }

    private void adicionarOrgao(HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos, HierarquiaOrganizacional orgao, UnidadeOrganizacional lotacao) {
        if (!orgaos.containsKey(orgao)) {
            orgaos.put(orgao, Lists.<UnidadeOrganizacional>newArrayList());
            Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(orgao.getSubordinada(), singleton.getSelecionado().getDataOperacao());
            if (entidade != null) {
                JSONObject jsonUnidade = montarConteudoLotacao(orgao, entidade.getTipoAdministracao());
                addMensagemRetorno(orgao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_LOTACAO, jsonUnidade, "POST"));
            }
        }
        orgaos.get(orgao).add(lotacao);
    }

    private void sincronizarCargo(HashSet<Cargo> cargos) {
        addMensagem("<b>Sincronizando os cargos. </b>");
        for (Cargo cargo : cargos) {
            JSONObject jsonCargo = montarJSONServidorCargo(cargo);
            addMensagemRetorno(cargo, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_CARGO, jsonCargo, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private JSONObject montarJSONServidorCategoriaFuncaoGratificada(String descricao) {
        JSONObject json = new JSONObject();
        json.put("descricao", descricao);
        return json;
    }

    private JSONObject montarJSONServidorFuncaoGratificada(FuncaoGratificadaPortal funcaoGratificadaPortal, ServidorPortal servidorPortal, HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos) {
        JSONObject json = new JSONObject();
        json.put("descricao", funcaoGratificadaPortal.getDescricao());
        json.put("servidor", montarJSONServidor(servidorPortal, orgaos));
        json.put("categoria", montarJSONServidorCategoriaFuncaoGratificada(funcaoGratificadaPortal.getCategoria()));
        json.put("inicioVigencia", funcaoGratificadaPortal.getInicioVigencia().getTime());
        json.put("fimVigencia", funcaoGratificadaPortal.getFinalVigencia() != null ? funcaoGratificadaPortal.getFinalVigencia().getTime() : null);
        json.put("valor", funcaoGratificadaPortal.getValor());
        return json;
    }

    private JSONObject montarConteudoLotacao(HierarquiaOrganizacional unidade, TipoAdministracao tipoAdministracao) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("codigo", unidade.getCodigo());
        jsonObject.put("descricao", unidade.getDescricao());
        jsonObject.put("dataInicial", unidade.getInicioVigencia().getTime());
        jsonObject.put("tipoAdministracao", tipoAdministracao != null ? tipoAdministracao.name() : null);
        jsonObject.put("dataFinal", unidade.getFimVigencia() == null ? (" ") : unidade.getFimVigencia().getTime());
        return jsonObject;
    }

    private void prepararServidorPortalCargoLotacao(List<ServidorPortal> servidores, HashSet<UnidadeOrganizacional> unidades, HashSet<Cargo> cargos) {
        for (ServidorPortal servidorPortal : servidores) {
            if (enviarLotacaoServidor(servidorPortal, servidorPortal.getLotacao(), unidades)) {
                Cargo cargo = getCargo(servidorPortal);
                if (cargo != null) {
                    cargos.add(cargo);
                }
            }
        }
    }

    private void sincronizarServidorPortal(ServidorPortal servidorPortal, HashSet<UnidadeOrganizacional> unidades, HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos) {
        if (enviarLotacaoServidor(servidorPortal, servidorPortal.getLotacao(), unidades)) {
            JSONObject conteudoPessoa = montarConteudoJSONPessoa(servidorPortal.getVinculo().getMatriculaFP().getPessoa());
            addMensagemRetorno(servidorPortal.getVinculo().getMatriculaFP().getPessoa(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, conteudoPessoa, UtilPortalTransparencia.METHOD_POST));

            JSONObject jsonServidor = montarJSONServidor(servidorPortal, orgaos);
            addMensagemRetorno(servidorPortal, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR, jsonServidor, UtilPortalTransparencia.METHOD_POST));
            sincronizarCedenciasPortal(servidorPortal, jsonServidor);
        }
    }

    private void sincronizarCedenciasPortal(ServidorPortal servidorPortal, JSONObject jsonServidor) {
        List<CedenciaPortal> cedencias = folhaDePagamentoFacade.buscarCedencias(servidorPortal);
        for (CedenciaPortal cedencia : cedencias) {
            JSONObject jsonCedencia = montarJSONCedencia(cedencia, jsonServidor);
            addMensagemRetorno(cedencia, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_CEDENCIA, jsonCedencia, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private Boolean enviarLotacaoServidor(ServidorPortal servidorPortal, UnidadeOrganizacional lotacao, HashSet<UnidadeOrganizacional> unidades) {
        if (lotacao == null) {
            addMensagemErro("Não foi encontrado unidade administrativa para o servidor " + servidorPortal + ".");
            return false;
        }
        if (getHierarquiaDaUnidadeAdministrativa(lotacao, singleton.getSelecionado().getDataOperacao()) == null) {
            addMensagemErro("Não foi encontrado hierarquia administrativa para o servidor " + servidorPortal + " e lotação " + lotacao + ".");
            return false;
        }

        unidades.add(lotacao);
        return true;
    }

    private JSONObject montarJSONServidorCargo(Cargo cargo) {
        JSONObject json = new JSONObject();
        json.put("codigo", cargo.getCodigoDoCargo());
        json.put("descricao", cargo.getDescricao());
        return json;
    }

    private JSONObject montarJSONServidor(ServidorPortal servidorPortal, HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos) {
        JSONObject json = new JSONObject();
        MatriculaFP matriculaFP = servidorPortal.getVinculo().getMatriculaFP();
        if (servidorPortal.getVinculo() instanceof ContratoFP) {
            montarJsonContratoFP(servidorPortal, json, matriculaFP);
        } else if (servidorPortal.getVinculo() instanceof Aposentadoria) {
            montarJsonAponsentadoria(servidorPortal, json, matriculaFP);
        } else if (servidorPortal.getVinculo() instanceof Pensionista) {
            montarJsonPensionista(servidorPortal, json, matriculaFP);
        } else if (servidorPortal.getVinculo() instanceof Beneficiario) {
            montarJsonBeneficiado(servidorPortal, json, matriculaFP);
        } else if (servidorPortal.getVinculo() instanceof Estagiario) {
            montarJsonEstagiario(servidorPortal, json, matriculaFP);
        } else {
            JSONObject jsonCargo = new JSONObject();
            jsonCargo.put("codigo", "-1");
            jsonCargo.put("descricao", servidorPortal.getVinculo().getClass().getSimpleName());
            UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_CARGO, jsonCargo, UtilPortalTransparencia.METHOD_POST);

            json.put("cargo", jsonCargo);
            json.put("matricula", matriculaFP.getMatricula() + "/" + servidorPortal.getVinculo().getNumero());
            json.put("modalidade", servidorPortal.getTipoCedenciaContratoFP() != null ? "Cedido" : "");
            json.put("tipoCedenciaContratoFP", servidorPortal.getTipoCedenciaContratoFP() != null ? servidorPortal.getTipoCedenciaContratoFP().name() : null);
            json.put("cargaHoraria", "");
            json.put("dataPosse", "");
        }
        montarJsonServidorComplemento(servidorPortal, json, matriculaFP, orgaos);
        return json;
    }

    private void montarJsonServidorComplemento(ServidorPortal servidorPortal, JSONObject json, MatriculaFP matriculaFP, HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos) {
        json.put("pessoa", montarConteudoJSONPessoa(matriculaFP.getPessoa()));
        json.put("tipoFolhaDePagamento", servidorPortal.getTipoFolhaDePagamento().name());
        json.put("lotacao", montarConteudoUnidade(buscarOrgao(servidorPortal.getLotacao(), orgaos)));
        json.put("recurso", montarJSONRecursoFP(servidorPortal.getRecurso()));
        if (servidorPortal.getId() == null) {
            json.put("mes", servidorPortal.getMes() + 1);
        } else {
            json.put("mes", servidorPortal.getMes());
        }
        json.put("exercicio", getConteudoJSONExercicio(servidorPortal.getExercicio()));
        json.put("vencimentoBase", servidorPortal.getVencimentoBase());
        json.put("outrasVerbas", servidorPortal.getOutrasVerbas());
        json.put("salarioBruto", servidorPortal.getSalarioBruto());
        json.put("descontos", servidorPortal.getDescontos());
        json.put("salarioLiquido", servidorPortal.getSalarioLiquido());
        json.put("impostoRenda", servidorPortal.getImpostoRenda());
        json.put("abateTeto", servidorPortal.getAbateTeto());
        json.put("outrosDescontos", servidorPortal.getOutrosDescontos());
        json.put("previdencia", servidorPortal.getPrevidencia());
        if (servidorPortal.getTipoCedenciaContratoFP() != null) {
            json.put("tipoCedenciaContratoFP", servidorPortal.getTipoCedenciaContratoFP().name());
        }
    }

    private JSONObject montarJSONCedencia(CedenciaPortal cedencia, JSONObject jsonServidor) {
        JSONObject json = new JSONObject();
        json.put("inicio", cedencia.getInicio().getTime());
        json.put("termino", cedencia.getTermino().getTime());
        json.put("esfera", cedencia.getEsfera());
        json.put("orgaoReceptor", cedencia.getOrgaoReceptor());
        json.put("finalidade", cedencia.getFinalidade());
        json.put("tipoCedencia", cedencia.getTipoCedencia());
        json.put("servidor", jsonServidor);
        json.put("idExterno", cedencia.getIdExterno());
        return json;
    }

    private HierarquiaOrganizacional buscarOrgao(UnidadeOrganizacional lotacao, HashMap<HierarquiaOrganizacional, List<UnidadeOrganizacional>> orgaos) {
        for (HierarquiaOrganizacional hierarquiaOrganizacional : orgaos.keySet()) {
            for (UnidadeOrganizacional unidadeOrganizacional : orgaos.get(hierarquiaOrganizacional)) {
                if (unidadeOrganizacional.getId().equals(lotacao.getId())) {
                    return hierarquiaOrganizacional;
                }
            }

        }
        return null;
    }

    private void montarJsonEstagiario(ServidorPortal servidorPortal, JSONObject json, MatriculaFP matriculaFP) {
        Estagiario estagiario = (Estagiario) servidorPortal.getVinculo();
        JSONObject jsonCargo = new JSONObject();
        jsonCargo.put("codigo", "-3");
        jsonCargo.put("descricao", estagiario.getCargoVigente(singleton.getSelecionado().getDataOperacao()).getCargo().getDescricao());
        UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_CARGO, jsonCargo, UtilPortalTransparencia.METHOD_POST);

        json.put("cargo", jsonCargo);
        json.put("matricula", matriculaFP.getMatricula() + "/" + estagiario.getNumero());

        montarEnviarJsonModalidade("Estagiario");
        json.put("modalidade", "Estagiario");
        json.put("cargaHoraria", 0);
        json.put("dataPosse", estagiario.getContratoFP().getDataAdmissao());
    }

    private void montarJsonPensionista(ServidorPortal servidorPortal, JSONObject json, MatriculaFP matriculaFP) {
        Pensionista pensionista = (Pensionista) servidorPortal.getVinculo();
        JSONObject jsonCargo = new JSONObject();
        jsonCargo.put("codigo", "-2");
        jsonCargo.put("descricao", "Pensionista");
        UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_CARGO, jsonCargo, UtilPortalTransparencia.METHOD_POST);

        json.put("cargo", jsonCargo);
        json.put("matricula", matriculaFP.getMatricula() + "/" + pensionista.getNumero());
        montarEnviarJsonModalidade("Pensionista");
        json.put("modalidade", "Pensionista");
        json.put("cargaHoraria", 0);
        json.put("dataPosse", pensionista.getContratoFP().getDataAdmissao());
    }

    private void montarJsonBeneficiado(ServidorPortal servidorPortal, JSONObject json, MatriculaFP matriculaFP) {
        Beneficiario beneficiario = (Beneficiario) servidorPortal.getVinculo();
        JSONObject jsonCargo = new JSONObject();
        jsonCargo.put("codigo", "-2");
        jsonCargo.put("descricao", "Pensionista");
        UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_CARGO, jsonCargo, UtilPortalTransparencia.METHOD_POST);
        json.put("cargo", jsonCargo);
        json.put("matricula", matriculaFP.getMatricula() + "/" + beneficiario.getNumero());
        montarEnviarJsonModalidade("Pensionista");
        json.put("modalidade", "Pensionista");
        json.put("cargaHoraria", 0);
    }

    private void montarJsonAponsentadoria(ServidorPortal servidorPortal, JSONObject json, MatriculaFP matriculaFP) {
        Aposentadoria aposentadoria = (Aposentadoria) servidorPortal.getVinculo();

        JSONObject value = montarJSONCargo(aposentadoria.getContratoFP().getCargo());
        value.put("descricao", "Aposentado em " + aposentadoria.getContratoFP().getCargo().getDescricao());
        UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_CARGO, value, UtilPortalTransparencia.METHOD_POST);

        json.put("cargo", value);
        json.put("matricula", matriculaFP.getMatricula() + "/" + aposentadoria.getNumero());
        montarEnviarJsonModalidade("Aposentado(a)");
        json.put("modalidade", "Aposentado(a)");
        json.put("cargaHoraria", 0);
        json.put("dataPosse", aposentadoria.getContratoFP().getDataAdmissao());
    }

    private void montarJsonContratoFP(ServidorPortal servidorPortal, JSONObject json, MatriculaFP matriculaFP) {
        ContratoFP contratoFP = (ContratoFP) servidorPortal.getVinculo();
        json.put("cargo", montarJSONCargo(contratoFP.getCargo()));
        json.put("matricula", matriculaFP.getMatricula() + "/" + contratoFP.getNumero());
        montarEnviarJsonModalidade(contratoFP.getModalidadeContratoFP().getDescricao());
        json.put("modalidade", contratoFP.getModalidadeContratoFP().getDescricao());
        json.put("cargaHoraria", contratoFP.getJornadaDeTrabalho().getHorasSemanal());
        json.put("dataPosse", contratoFP.getDataAdmissao().getTime());
    }

    private void montarEnviarJsonModalidade(String descricao) {
        JSONObject json = new JSONObject();
        json.put("descricao", descricao);
        addMensagemRetorno(descricao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_MODALIDADE, json, UtilPortalTransparencia.METHOD_POST));
    }

    private Cargo getCargo(ServidorPortal servidorPortal) {
        if (servidorPortal.getVinculo() instanceof ContratoFP) {
            ContratoFP contratoFP = (ContratoFP) servidorPortal.getVinculo();
            return contratoFP.getCargo();
        } else if (servidorPortal.getVinculo() instanceof Aposentadoria) {
            Aposentadoria aposentadoria = (Aposentadoria) servidorPortal.getVinculo();
            return aposentadoria.getContratoFP().getCargo();
        } else {
            return null;
        }
    }

    private JSONObject montarJSONRecursoFP(RecursoFP recursoFP) {
        JSONObject json = new JSONObject();
        json.put("descricao", recursoFP.getDescricao());
        return json;
    }

    private JSONObject montarJSONCargo(Cargo cargo) {
        JSONObject json = new JSONObject();
        json.put("codigo", cargo.getCodigoDoCargo());
        json.put("descricao", cargo.getDescricao());
        return json;
    }

    private void recuperarFinanceiro() {
        if (singleton.getSelecionado().getEnviarFinanceiro()) {
            recuperarLiberacaoFinanceira();
            recuperarEstornoLiberacaoFinanceira();
            recuperarTransferenciaFinanceira();
            recuperarEstornoTransferenciaFinanceira();
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void recuperarEstornoLiberacaoFinanceira() {
        List<EstornoLibCotaFinanceira> estornos = estornoLibCotaFinanceiraFacade.listaEstornoLiberacaoPorUnidadesExercicio(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades());
        addMensagem("<b> Montando envio de " + estornos.size() + " Estorno Liberações Financeiras. </b>");
        for (EstornoLibCotaFinanceira estorno : estornos) {
            sincronizarEstornoLiberacaoCotaFinanceira(estorno);
        }
    }

    private void recuperarLiberacaoFinanceira() {
        List<LiberacaoCotaFinanceira> liberacaoCotaFinanceiras = liberacaoCotaFinanceiraFacade.listaLiberacaoPorUnidadesExercicio(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades());
        addMensagem("<b> Montando envio de " + liberacaoCotaFinanceiras.size() + " Liberações Financeiras. </b>");
        for (LiberacaoCotaFinanceira liberacaoCotaFinanceira : liberacaoCotaFinanceiras) {
            sincronizarLiberacaoCotaFinanceira(liberacaoCotaFinanceira);
        }
    }

    private void recuperarTransferenciaFinanceira() {
        List<TransferenciaContaFinanceira> transferenciaContaFinanceiras = transferenciaContaFinanceiraFacade.buscarTransferencia(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades());
        addMensagem("<b> Montando envio de " + transferenciaContaFinanceiras.size() + " Transferência Financeiras. </b>");
        for (TransferenciaContaFinanceira transferenciaContaFinanceira : transferenciaContaFinanceiras) {
            sincronizarTransferenciaContaFinanceira(transferenciaContaFinanceira);
        }
    }

    private void recuperarEstornoTransferenciaFinanceira() {
        List<EstornoTransferencia> estornos = estornoTransferenciaFacade.buscarEstornoTransferencia(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades());
        addMensagem("<b> Montando envio de " + estornos.size() + " Estorno Transfêrencia Financeiras. </b>");
        for (EstornoTransferencia estorno : estornos) {
            sincronizarEstornoTransferenciaContaFinanceira(estorno);
        }
    }

    private JSONObject montarJSONEstornoLiberacaoFinanceira(EstornoLibCotaFinanceira estorno) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", estorno.getNumero());
        jsonObject.put("data", estorno.getDataEstorno().getTime());
        jsonObject.put("exercicio", getConteudoJSONExercicio(estorno.getLiberacao().getExercicio()));
        jsonObject.put("historico", removerCaracteresInvalidos(estorno.getHistorico()));
        jsonObject.put("liberacaoFinanceira", montarJSONLiberacaoFinanceira(estorno.getLiberacao()));
        jsonObject.put("valor", estorno.getValor());
        return jsonObject;
    }

    private JSONObject montarJSONObra(Obra obra) {
        JSONObject jsonObject = new JSONObject();
        montarJSONObraDataLicitacao(obra, jsonObject);
        if (obra.getContrato().getContratado() != null) {
            enviarJsonPessoa(obra.getContrato().getContratado());
            jsonObject.put("empresaLicitadaVencedora", montarConteudoJSONPessoa(obra.getContrato().getContratado()));
        }
        jsonObject.put("custoTotal", obra.getContrato().getValorTotal());
        if (obra.getInicioExecucao() != null) {
            jsonObject.put("inicioExecucao", obra.getInicioExecucao().getTime());
        }
        if (obra.getPrazoEntrega() != null) {
            jsonObject.put("terminoExecucao", obra.getPrazoEntrega().getTime());
        }
        jsonObject.put("nome", obra.getNome());
        jsonObject.put("alcanceSocial", obra.getAlcanceSocial() == null ? "" : obra.getAlcanceSocial());
        jsonObject.put("finalidadeObra", obra.getFinalidadeObra() == null ? "" : obra.getFinalidadeObra());

        HierarquiaOrganizacional unidadeContato = contratoFacade.buscarHierarquiaVigenteContrato(obra.getContrato(), obra.getContrato().getTerminoVigencia());
        jsonObject.put("secretariaFiscalizadora", unidadeContato.getDescricao());
        ResponsavelTecnicoFiscal responsavelTecnicoFiscal = obraFacade.recuperarFiscalObraVigente(obra, singleton.getSelecionado().getDataOperacao());
        if (responsavelTecnicoFiscal != null && responsavelTecnicoFiscal.getResponsavelTecnico().getPessoaFisica() != null) {
            enviarJsonPessoa(responsavelTecnicoFiscal.getResponsavelTecnico().getPessoaFisica());
            jsonObject.put("engenheiroResponsavel", montarConteudoJSONPessoa(responsavelTecnicoFiscal.getResponsavelTecnico().getPessoaFisica()));
        }
        return jsonObject;
    }

    private void montarJSONObraDataLicitacao(Obra obra, JSONObject jsonObject) {
        List<OrdemDeServicoContrato> ordensDeServico = obra.getContrato().getOrdensDeServico();
        if (ordensDeServico != null && !ordensDeServico.isEmpty()) {
            jsonObject.put("dataInicioProcessoLicitatorio", ordensDeServico.get(0).getDataRecebimento().getTime());
        }
        List<ObraTermo> termos = obra.getItemObraTermo();

        if (termos != null && !termos.isEmpty()) {
            for (ObraTermo termo : termos) {
                if (termo.getDefinitivo()) {
                    jsonObject.put("dataFimProcessoLicitatorio", termos.get(0).getDataEmissao().getTime());
                }
            }
        }
    }

    private JSONObject montarJSONItensDaObra(ItemContrato itemContrato, Obra obra) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("material", itemContrato.getItemAdequado().getObjetoCompra().getDescricao());
        jsonObject.put("especificacao", itemContrato.getItemAdequado().getDescricaoComplementar());
        jsonObject.put("quantidade", itemContrato.getQuantidadeTotalContrato());
        jsonObject.put("valorUnitario", itemContrato.getValorUnitario());
        jsonObject.put("valorTotal", itemContrato.getValorTotal());
        jsonObject.put("obra", montarJSONObra(obra));
        return jsonObject;
    }

    private JSONObject montarJSONLiberacaoFinanceira(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", liberacaoCotaFinanceira.getNumero());
        jsonObject.put("data", liberacaoCotaFinanceira.getDataLiberacao().getTime());
        jsonObject.put("exercicio", getConteudoJSONExercicio(liberacaoCotaFinanceira.getExercicio()));
        jsonObject.put("concedido", montarConteudoUnidade(getHierarquiaDaUnidade(liberacaoCotaFinanceira.getUnidadeRetirada(), liberacaoCotaFinanceira.getDataLiberacao())));
        jsonObject.put("recebida", montarConteudoUnidade(getHierarquiaDaUnidade(liberacaoCotaFinanceira.getUnidadeRecebida(), liberacaoCotaFinanceira.getDataLiberacao())));
        jsonObject.put("historico", removerCaracteresInvalidos(liberacaoCotaFinanceira.getSolicitacaoCotaFinanceira().getHistoricoSolicitacao()));
        jsonObject.put("valor", liberacaoCotaFinanceira.getValor());
        return jsonObject;
    }

    private JSONObject montarJSONEstornoTransferenciaFinanceira(EstornoTransferencia estorno) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", estorno.getNumero());
        jsonObject.put("data", estorno.getDataEstorno().getTime());
        jsonObject.put("exercicio", getConteudoJSONExercicio(estorno.getTransferencia().getExercicio()));
        jsonObject.put("historico", removerCaracteresInvalidos(estorno.getHistorico()));
        jsonObject.put("liberacaoFinanceira", montarJSONTransferenciaFinanceira(estorno.getTransferencia()));
        jsonObject.put("valor", estorno.getValor());
        return jsonObject;
    }

    private JSONObject montarJSONTransferenciaFinanceira(TransferenciaContaFinanceira transferenciaContaFinanceira) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", transferenciaContaFinanceira.getNumero());
        jsonObject.put("data", transferenciaContaFinanceira.getDataTransferencia().getTime());
        jsonObject.put("exercicio", getConteudoJSONExercicio(transferenciaContaFinanceira.getExercicio()));
        jsonObject.put("concedido", montarConteudoUnidade(getHierarquiaDaUnidade(transferenciaContaFinanceira.getUnidadeOrgConcedida(), transferenciaContaFinanceira.getDataTransferencia())));
        jsonObject.put("recebida", montarConteudoUnidade(getHierarquiaDaUnidade(transferenciaContaFinanceira.getUnidadeOrganizacional(), transferenciaContaFinanceira.getDataTransferencia())));
        jsonObject.put("historico", removerCaracteresInvalidos(transferenciaContaFinanceira.getHistorico()));
        jsonObject.put("valor", transferenciaContaFinanceira.getValor());
        return jsonObject;
    }

    private void recuperarDadosPrefeitura() {
        if (singleton.getSelecionado().getEnviarDadosDaPrefeitura()) {
            enviarDadosDaPrefeitura();
        } else {
            enviarUltimaAtualizacao();
        }
    }

    private void recuperarAtosLegais() {
        if (singleton.getSelecionado().getEnviarAtoLegal()) {
            if (!singleton.getSelecionado().getAtos().isEmpty()) {
                for (AtoLegal atoLegal : singleton.getSelecionado().getAtos()) {
                    try {
                        atoLegalFacade.salvarNovo(atoLegal);
                        sincronizarAtoLegal(atoLegal);
                    } catch (Exception e) {
                        addMensagemErro("Erro no ato legal " + atoLegal.toString() + ". Detalhes do erro: " + e.getMessage());
                    }
                }
                singleton.getSelecionado().getAtos().clear();
            }
            List<AtoLegal> atoLegals = atoLegalFacade.buscarTodosParaPortalTransparencia(singleton.getSelecionado().getExercicio());
            addMensagem("<b> Montando envio de " + atoLegals.size() + " Atos Legais. </b>");
            for (AtoLegal atoLegal : atoLegals) {
                sincronizarAtoLegal(atoLegal);
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void sincronizarAtoLegal(AtoLegal atoLegal) {
        JSONObject conteudoJSON = montarJSONAtoLegal(atoLegal);
        addMensagemRetorno(atoLegal, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_ATO_LEGAL, conteudoJSON, UtilPortalTransparencia.METHOD_POST));
    }

    private void recuperarConvenio() {
        if (singleton.getSelecionado().getEnviarConvenios()) {
            recuperarConvenioReceita();
            recuperarConvenioDespesa();
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void recuperarConvenioReceita() {
        List<ConvenioReceita> convenios = convenioReceitaFacade.recuperarTodosPorExercicioUnidades(singleton.getSelecionado().getUnidades());
        addMensagem("<b> Montando envio de " + convenios.size() + " Convênios de Receita. </b>");
        for (ConvenioReceita convenio : convenios) {
            sincronizarConvenioReceita(convenio);
        }
    }

    private void sincronizarConvenioReceita(ConvenioReceita convenio) {
        HierarquiaOrganizacional hierarquiaDaUnidade = null;
        if (convenio.getEntidadeConvenente() != null && convenio.getEntidadeConvenente().getUnidadeOrganizacionalOrc() != null) {
            hierarquiaDaUnidade = getHierarquiaDaUnidade(convenio.getEntidadeConvenente().getUnidadeOrganizacionalOrc(), convenio.getVigenciaInicial());
        }
        if (convenio.getPessoa() != null) {
            enviarJsonPessoa(convenio.getPessoa());
        }
        if (convenio.getEntidadeRepassadora() != null && convenio.getEntidadeRepassadora().getPessoaJuridica() != null) {
            enviarJsonPessoa(convenio.getEntidadeRepassadora().getPessoaJuridica());
        }
        JSONObject conteudo = montarConteudoJSONConvenioReceita(convenio, hierarquiaDaUnidade);
        getObjetos().add(new ObjetoPortalTransparencia(convenio, conteudo, PortalTransparenciaNovo.WS_CONVENIO_RECEITA, UtilPortalTransparencia.METHOD_POST));
        enviarJsonSituacaoConvenioReceita(convenio, conteudo);
        enviarJsonRecursoConvenioReceita(convenio, conteudo);
    }

    private void enviarJsonRecursoConvenioReceita(ConvenioReceita convenio, JSONObject conteudo) {
        for (ConvenioReceitaLiberacao recurso : convenio.getConvenioReceitaLiberacoes()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", recurso.getDataLiberacaoRecurso().getTime());
            jsonObject.put("valor", recurso.getValorLiberadoConcedente());
            jsonObject.put("convenioReceita", conteudo);
            getObjetos().add(new ObjetoPortalTransparencia(recurso, jsonObject, PortalTransparenciaNovo.WS_CONVENIO_RECEITA_RECURSO, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void enviarJsonSituacaoConvenioReceita(ConvenioReceita convenio, JSONObject conteudo) {
        for (PrestacaoContas prestacaoContas : convenio.getPrestacaoContas()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", prestacaoContas.getDataLancamento().getTime());
            jsonObject.put("tipoPrestacaoContas", prestacaoContas.getTipoPrestacaoContas().name());
            jsonObject.put("convenioReceita", conteudo);
            getObjetos().add(new ObjetoPortalTransparencia(prestacaoContas, jsonObject, PortalTransparenciaNovo.WS_CONVENIO_RECEITA_SITUACAO, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void recuperarConvenioDespesa() {
        List<ConvenioDespesa> convenios = convenioDespesaFacade.recuperarTodosPorExercicioUnidades(singleton.getSelecionado().getUnidades());
        addMensagem("<b> Montando envio de " + convenios.size() + " Convênios de Despesa. </b>");
        for (ConvenioDespesa convenio : convenios) {
            sincronizarConvenioDespesa(convenio);
        }
    }

    private void sincronizarConvenioDespesa(ConvenioDespesa convenio) {
        HierarquiaOrganizacional hierarquiaDaUnidade = null;
        if (convenio.getOrgaoConvenente() != null && convenio.getOrgaoConvenente().getUnidadeOrganizacionalOrc() != null) {
            hierarquiaDaUnidade = getHierarquiaDaUnidade(convenio.getOrgaoConvenente().getUnidadeOrganizacionalOrc(), convenio.getDataVigenciaInicial());
        }
        if (convenio.getOrgaoConvenente() != null && convenio.getOrgaoConvenente().getPessoaJuridica() != null) {
            enviarJsonPessoa(convenio.getOrgaoConvenente().getPessoaJuridica());
        }
        if (convenio.getEntidadeBeneficiaria() != null && convenio.getEntidadeBeneficiaria().getPessoaJuridica() != null) {
            enviarJsonPessoa(convenio.getEntidadeBeneficiaria().getPessoaJuridica());
        }

        JSONObject conteudo = montarConteudoJSONConvenioDespesa(convenio, hierarquiaDaUnidade);
        getObjetos().add(new ObjetoPortalTransparencia(convenio, conteudo, PortalTransparenciaNovo.WS_CONVENIO_DESPESA, UtilPortalTransparencia.METHOD_POST));
        enviarJsonSituacaoConvenioDespesa(convenio, conteudo);
        enviarJsonRecursoConvenioDespesa(convenio, conteudo);
        enviarJsonAditivoConvenioDespesa(convenio, conteudo);
    }

    public void enviarJsonPessoa(Pessoa pessoa) {
        JSONObject conteudoPessoa = montarConteudoJSONPessoa(pessoa);
        addMensagemRetorno(pessoa, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, conteudoPessoa, UtilPortalTransparencia.METHOD_POST));
    }

    private void enviarJsonAditivoConvenioDespesa(ConvenioDespesa convenio, JSONObject conteudo) {
        for (AditivosConvenioDespesa aditivosConvenioDespesa : convenio.getAditivosConvenioDespesas()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("numero", aditivosConvenioDespesa.getAditivos().getNumero());
            jsonObject.put("descricao", aditivosConvenioDespesa.getAditivos().getDescricao());
            jsonObject.put("dataInicioVigencia", aditivosConvenioDespesa.getAditivos().getDataIniVigencia() == null ? null : aditivosConvenioDespesa.getAditivos().getDataIniVigencia().getTime());
            jsonObject.put("dataFimVigencia", aditivosConvenioDespesa.getAditivos().getDataFimVigencia() == null ? null : aditivosConvenioDespesa.getAditivos().getDataFimVigencia().getTime());
            jsonObject.put("valor", aditivosConvenioDespesa.getAditivos().getValorAditivo());
            jsonObject.put("valorContrapartida", aditivosConvenioDespesa.getAditivos().getValorContrapartida());
            jsonObject.put("convenioDespesa", conteudo);
            getObjetos().add(new ObjetoPortalTransparencia(aditivosConvenioDespesa.getAditivos(), jsonObject, PortalTransparenciaNovo.WS_CONVENIO_DESPESA_ADITIVO, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void enviarJsonRecursoConvenioDespesa(ConvenioDespesa convenio, JSONObject conteudo) {
        for (ConvenioDespesaLiberacao recurso : convenio.getConvenioDespesaLiberacoes()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", recurso.getDataLiberacaoRecurso().getTime());
            jsonObject.put("valor", recurso.getValorLiberadoConcedente());
            jsonObject.put("convenioDespesa", conteudo);
            getObjetos().add(new ObjetoPortalTransparencia(recurso, jsonObject, PortalTransparenciaNovo.WS_CONVENIO_DESPESA_RECURSO, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void enviarJsonSituacaoConvenioDespesa(ConvenioDespesa convenio, JSONObject conteudo) {
        for (PrestacaoContas prestacaoContas : convenio.getPrestacaoContas()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", prestacaoContas.getDataLancamento().getTime());
            jsonObject.put("tipoPrestacaoContas", prestacaoContas.getTipoPrestacaoContas().name());
            jsonObject.put("convenioDespesa", conteudo);
            getObjetos().add(new ObjetoPortalTransparencia(prestacaoContas, jsonObject, PortalTransparenciaNovo.WS_CONVENIO_DESPESA_SITUACAO, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void sincronizarCalamidadePublica(CalamidadePublica calamidadePublica) {
        JSONObject conteudo = new JSONObject();
        conteudo.put("descricao", calamidadePublica.getDescricao());
        conteudo.put("abreviacao", calamidadePublica.getAbreviacao().getDescricao().toLowerCase());
        conteudo.put("inicioVigencia", calamidadePublica.getInicioVigencia().getTime());
        conteudo.put("finalVigencia", calamidadePublica.getFinalVigencia() != null ? calamidadePublica.getFinalVigencia().getTime() : null);
        addMensagemRetorno(calamidadePublica, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CALAMIDADE_PUBLICA, conteudo, UtilPortalTransparencia.METHOD_POST));
        enviarCalamidadePublicaContrato(calamidadePublica, conteudo);
        enviarCalamidadePublicaAtoLegal(calamidadePublica, conteudo);
        enviarCalamidadePublicaRecurso(calamidadePublica, conteudo);
        enviarCalamidadePublicaBemServico(calamidadePublica, conteudo);
        enviarCalamidadePublicaBemDoado(calamidadePublica, conteudo);
    }

    private void enviarCalamidadePublicaBemDoado(CalamidadePublica calamidadePublica, JSONObject conteudo) {
        for (CalamidadePublicaBemDoado item : calamidadePublica.getBensDoados()) {
            if (item.getQuantidade() > 0 && item.getUnidadeOrganizacional() != null) {
                HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(item.getUnidadeOrganizacional(), calamidadePublica.getInicioVigencia());
                if (hierarquiaDaUnidade != null) {
                    JSONObject json = new JSONObject();
                    json.put("calamidadePublica", conteudo);
                    json.put("descricao", item.getDescricao());
                    json.put("quantidade", item.getQuantidade());
                    json.put("orgao", hierarquiaDaUnidade.getDescricao());
                    json.put("unidadeMedida", item.getUnidadeMedida() != null ? item.getUnidadeMedida().getDescricao() : "");
                    json.put("donatario", item.getDonatario());
                    json.put("idOrigem", item.getId());
                    addMensagemRetorno(calamidadePublica, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CALAMIDADE_PUBLICA_BEM_DOADO, json, UtilPortalTransparencia.METHOD_POST));
                    montarJsonBemDoadoArquivo(item, json);
                }
            }
        }
    }

    private void enviarCalamidadePublicaBemServico(CalamidadePublica calamidadePublica, JSONObject conteudo) {
        for (CalamidadePublicaBemServico item : calamidadePublica.getBensServicosRecebidos()) {
            if (item.getQuantidade() > 0 && item.getUnidadeOrganizacional() != null) {
                HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(item.getUnidadeOrganizacional(), calamidadePublica.getInicioVigencia());
                if (hierarquiaDaUnidade != null) {
                    JSONObject json = new JSONObject();
                    json.put("calamidadePublica", conteudo);
                    json.put("descricao", item.getDescricao());
                    json.put("quantidade", item.getQuantidade());
                    json.put("orgao", hierarquiaDaUnidade.getDescricao());
                    json.put("unidadeMedida", item.getUnidadeMedida() != null ? item.getUnidadeMedida().getDescricao() : "");
                    json.put("entidadeTransferidor", item.getPessoa() != null ? item.getPessoa() : "");
                    json.put("idOrigem", item.getId());
                    addMensagemRetorno(calamidadePublica, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CALAMIDADE_PUBLICA_BEM_SERVICO, json, UtilPortalTransparencia.METHOD_POST));
                }
            }
        }
    }

    private void enviarCalamidadePublicaRecurso(CalamidadePublica calamidadePublica, JSONObject conteudo) {
        for (CalamidadePublicaRecurso recurso : calamidadePublica.getRecursos()) {
            JSONObject json = new JSONObject();
            json.put("calamidadePublica", conteudo);
            if (recurso.isInterna()) {
                HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(recurso.getUnidadeOrganizacional(), calamidadePublica.getInicioVigencia());
                if (hierarquiaDaUnidade != null) {
                    json.put("unidade", hierarquiaDaUnidade.getDescricao());
                }
            } else {
                json.put("unidade", recurso.getUnidadeAsString());
            }
            json.put("valor", recurso.getValor());
            json.put("dataRecebimento", recurso.getDataRecebimento().getTime());
            json.put("fonteDeRecurso", montarJSONFonteDeRecurso(recurso.getFonteDeRecursos()));
            json.put("banco", recurso.getBanco());
            json.put("agencia", recurso.getAgencia());
            json.put("contaCorrente", recurso.getContaCorrente());
            json.put("dataTransferencia", recurso.getDataTransferencia() != null ? recurso.getDataTransferencia().getTime() : null);
            json.put("valorDisponibilizado", recurso.getValorDisponibilizado());
            json.put("valorBloqueado", recurso.getValorBloqueado());
            json.put("tipoConta", recurso.getTipoConta());
            addMensagemRetorno(calamidadePublica, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CALAMIDADE_PUBLICA_RECURSO, json, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void enviarCalamidadePublicaAtoLegal(CalamidadePublica calamidadePublica, JSONObject conteudo) {
        for (CalamidadePublicaAtoLegal atoLegal : calamidadePublica.getAtosLegais()) {
            JSONObject json = new JSONObject();
            json.put("calamidadePublica", conteudo);
            sincronizarAtoLegal(atoLegal.getAtoLegal());
            json.put("atoLegal", montarJSONAtoLegal(atoLegal.getAtoLegal()));
            addMensagemRetorno(calamidadePublica, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CALAMIDADE_PUBLICA_ATO_LEGAL, json, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void enviarCalamidadePublicaContrato(CalamidadePublica calamidadePublica, JSONObject conteudo) {
        for (CalamidadePublicaContrato calamidadeContrato : calamidadePublica.getContratos()) {
            Contrato contrato = contratoFacade.recuperarPortalTransparencia(calamidadeContrato.getContrato().getId());
            JSONObject json = new JSONObject();
            json.put("calamidadePublica", conteudo);
            sincronizarContrato(contrato);
            json.put("contrato", montarConteudoContrato(contrato));
            json.put("situacao", calamidadeContrato.getSituacao() != null ? calamidadeContrato.getSituacao().name() : "");
            addMensagemRetorno(calamidadePublica, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CALAMIDADE_PUBLICA_CONTRATO, json, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void montarJsonBemDoadoArquivo(CalamidadePublicaBemDoado bemDoado, JSONObject json) {
        if (bemDoado.getDetentorArquivoComposicao() != null) {
            for (ArquivoComposicao arquivoComposicao : bemDoado.getDetentorArquivoComposicao().getArquivosComposicao()) {
                if (arquivoComposicao.getArquivo() != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("calamidadePublicaBemDoado", json);
                    jsonObject.put("tipoArquivo", arquivoComposicao.getArquivo().getNome());
                    jsonObject.put("arquivo", montarJsonArquivo(arquivoComposicao.getArquivo()));
                    addMensagemRetorno(arquivoComposicao.getArquivo(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CALAMIDADE_PUBLICA_BEM_DOADO_ARQUIVO, jsonObject, UtilPortalTransparencia.METHOD_UPDATE));
                }
            }
        }
    }

    private void sincronizarDicionarioDeDados(DicionarioDeDados dicionario) {
        JSONObject conteudo = new JSONObject();
        conteudo.put("tipoDicionarioDeDados", dicionario.getTipoDicionarioDeDados().name());
        addMensagemRetorno(dicionario, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_DICIONARIO_DADOS, conteudo, UtilPortalTransparencia.METHOD_POST));
        for (ColunaDicionarioDeDados coluna : dicionario.getColunas()) {
            JSONObject json = new JSONObject();
            json.put("dicionarioDeDados", conteudo);
            json.put("ordem", coluna.getOrdem());
            json.put("coluna", coluna.getColuna());
            json.put("descricao", coluna.getDescricao());
            json.put("tipo", coluna.getTipo());
            addMensagemRetorno(dicionario, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_DICIONARIO_DADOS_COLUNA, json, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private JSONObject montarConteudoJSONConvenioReceita(ConvenioReceita convenio, HierarquiaOrganizacional hierarquiaDaUnidade) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numeroConvenio", convenio.getNumeroTermo());
        jsonObject.put("numero", convenio.getNumero());
        jsonObject.put("objeto", convenio.getNomeConvenio());
        jsonObject.put("unidade", hierarquiaDaUnidade != null ? montarConteudoUnidade(hierarquiaDaUnidade) : null);
        jsonObject.put("valor", convenio.getValorConvenio());
        jsonObject.put("data", convenio.getDataAssinatura());
        jsonObject.put("valorContrapartida", convenio.getValorContrapartida());
        jsonObject.put("valorRepasse", convenio.getValorRepasse());
        jsonObject.put("vigenciaInicial", convenio.getVigenciaInicial());
        jsonObject.put("vigenciaFinal", convenio.getVigenciaFinal());
        jsonObject.put("executora", convenio.getPessoa() != null ? montarConteudoJSONPessoa(convenio.getPessoa()) : null);
        jsonObject.put("concedente", convenio.getEntidadeRepassadora() != null ? montarConteudoJSONPessoa(convenio.getEntidadeRepassadora().getPessoaJuridica()) : null);
        return jsonObject;
    }

    private JSONObject montarConteudoJSONConvenioDespesa(ConvenioDespesa convenio, HierarquiaOrganizacional hierarquiaDaUnidade) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numeroConvenio", convenio.getNumConvenio());
        jsonObject.put("numero", convenio.getNumero());
        jsonObject.put("objeto", convenio.getObjeto());
        jsonObject.put("unidade", hierarquiaDaUnidade != null ? montarConteudoUnidade(hierarquiaDaUnidade) : null);
        jsonObject.put("valor", convenio.getValorConvenio());
        jsonObject.put("valor", convenio.getValorConvenio());
        jsonObject.put("data", convenio.getDataAss());
        jsonObject.put("valorContrapartida", convenio.getValorContrapartida());
        jsonObject.put("valorRepasse", convenio.getValorRepasse());
        jsonObject.put("vigenciaInicial", convenio.getDataVigenciaInicial());
        jsonObject.put("vigenciaFinal", convenio.getDataVigenciaFinal());
        jsonObject.put("executora", convenio.getOrgaoConvenente() != null ? montarConteudoJSONPessoa(convenio.getOrgaoConvenente().getPessoaJuridica()) : null);
        jsonObject.put("concedente", convenio.getEntidadeBeneficiaria() != null ? montarConteudoJSONPessoa(convenio.getEntidadeBeneficiaria().getPessoaJuridica()) : null);
        return jsonObject;
    }

    private void recuperarAnexos() {
        if (singleton.getSelecionado().getEnviarAnexos()) {
            List<PortalTransparencia> anexos = portalTransparenciaFacade.recuperarTodosPorExercicio(singleton.getSelecionado().getExercicio());
            addMensagem("<b> Montando envio de " + anexos.size() + " Anexos. </b>");
            for (PortalTransparencia anexo : anexos) {
                JSONObject conteudo = montarJSONAnexoContabilidade(anexo);
                addMensagemRetorno(anexo, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_ANEXO, conteudo, UtilPortalTransparencia.METHOD_POST));
                anexo.setSituacao(PortalTransparenciaSituacao.PUBLICADO);
                portalTransparenciaFacade.salvar(anexo);
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private JSONObject montarJSONAnexoContabilidade(PortalTransparencia anexo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nome", anexo.getNome());
        jsonObject.put("arquivo", montarJsonArquivo(anexo.getArquivo()));
        jsonObject.put("exercicio", getConteudoJSONExercicio(anexo.getExercicio()));
        jsonObject.put("observacao", anexo.getObservacoes());
        jsonObject.put("tipo", anexo.getTipo().name());
        jsonObject.put("tipoAnexo", anexo.getTipoAnexo() != null ? anexo.getTipoAnexo().name() : null);
        jsonObject.put("mes", anexo.getMes() != null ? anexo.getMes().name() : "");
        return jsonObject;
    }

    private void recuperarDiarias() {
        if (singleton.getSelecionado().getEnviarDiaria()) {
            List<PropostaConcessaoDiaria> propostaConcessaoDiarias = propostaConcessaoDiariaFacade.listaPropostaConcessaoDiariaExercicioUnidadesPagas(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades());
            addMensagem("<b> Montando envio de " + propostaConcessaoDiarias.size() + " Diárias. </b>");
            for (PropostaConcessaoDiaria diaria : propostaConcessaoDiarias) {
                sincronizarDiaria(diaria);
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void sincronizarDiaria(PropostaConcessaoDiaria diaria) {
        sincronizarAtoLegal(diaria.getAtoLegal());

        if (diaria.getFornecedor() != null) {
            JSONObject conteudoPessoa = montarConteudoJSONPessoa(diaria.getFornecedor());
            addMensagemRetorno(diaria.getFornecedor(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, conteudoPessoa, UtilPortalTransparencia.METHOD_POST));
        }


        JSONObject conteudo = montarConteudoDiaria(diaria);
        addMensagemRetorno(diaria, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_DIARIA, conteudo, UtilPortalTransparencia.METHOD_POST));

        montarConteudoArquivosDiaria(diaria);
    }

    private void montarConteudoArquivosDiaria(PropostaConcessaoDiaria diaria) {
        diaria = propostaConcessaoDiariaFacade.recuperar(diaria.getId());
        for (DiariaArquivo diariaArquivo : diaria.getArquivos()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("descricao", diariaArquivo.getDescricao());
            jsonObject.put("diaria", montarConteudoDiaria(diaria));
            jsonObject.put("arquivo", montarJsonArquivo(diariaArquivo.getArquivo()));
            addMensagemRetorno(diariaArquivo, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_DIARIA_ARQUIVO, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }

    }

    private JSONObject montarConteudoDiaria(PropostaConcessaoDiaria diaria) {
        diaria = propostaConcessaoDiariaFacade.recuperar(diaria.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", diaria.getCodigo());
        jsonObject.put("data", diaria.getDataLancamento().getTime());
        jsonObject.put("tipoProposta", diaria.getTipoProposta().name());
        if (!diaria.getViagens().isEmpty()) {
            jsonObject.put("meioDeTransporte", diaria.getViagens().get(0).getMeioDeTransporte() == null ? MeioDeTransporte.OUTROS.name() : diaria.getViagens().get(0).getMeioDeTransporte().name());
        }
        jsonObject.put("tipoViagem", diaria.getTipoViagem() == null ? TipoViagem.ESTADUAL.name() : diaria.getTipoViagem().name());
        jsonObject.put("pessoa", montarConteudoJSONPessoa(diaria.getPessoaFisica()));
        jsonObject.put("unidade", montarConteudoUnidade(getHierarquiaDaUnidade(diaria.getUnidadeOrganizacional(), diaria.getDataLancamento())));
        jsonObject.put("valor", diaria.getValor());
        if (!diaria.getViagens().isEmpty()) {
            jsonObject.put("itinerario", diaria.getViagens().get(0).getItinerario());
        }
        jsonObject.put("objetivo", diaria.getObjetivo());
        jsonObject.put("atoLegal", montarJSONAtoLegal(diaria.getAtoLegal()));
        jsonObject.put("exercicio", getConteudoJSONExercicio(diaria.getExercicio()));
        if (!diaria.getViagens().isEmpty()) {
            jsonObject.put("inicio", diaria.getViagens().get(0).getDataInicialSaida().getTime());
            jsonObject.put("fim", diaria.getViagens().get(0).getDataInicialRetorno().getTime());
        }
        jsonObject.put("situacaoDiaria", diaria.getSituacaoDiaria().name());
        if (diaria.getFornecedor() != null) {
            addMensagemRetorno(diaria.getFornecedor(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, montarConteudoJSONPessoa(diaria.getFornecedor()), UtilPortalTransparencia.METHOD_POST));
            jsonObject.put("fornecedor", montarConteudoJSONPessoa(diaria.getFornecedor()));
            jsonObject.put("numeroContrato", diaria.getNumeroContrato());
        }
        return jsonObject;
    }

    private HierarquiaOrganizacional getHierarquiaDaUnidade(UnidadeOrganizacional unidadeOrganizacional, Date dataOperacao) {
        return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), unidadeOrganizacional, dataOperacao);
    }

    private HierarquiaOrganizacional getHierarquiaDaUnidadeAdministrativa(UnidadeOrganizacional unidadeOrganizacional, Date dataOperacao) {
        if (mapaUnidades == null) {
            mapaUnidades = new HashMap<>();
        }
        if (mapaUnidades.containsKey(unidadeOrganizacional)) {
            return mapaUnidades.get(unidadeOrganizacional);
        } else {
            HierarquiaOrganizacional hierarquiaDaUnidade = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidadeOrganizacional, dataOperacao);
            mapaUnidades.put(unidadeOrganizacional, hierarquiaDaUnidade);
            return hierarquiaDaUnidade;
        }
    }

    private void recuperarLinks() {
        if (singleton.getSelecionado().getEnviarLink()) {
            for (LinkPortal linkPortal : singleton.getSelecionado().getLinks()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nome", linkPortal.getNomeLink());
                jsonObject.put("site", linkPortal.getSiteLink());
                jsonObject.put("arquivo", montarJsonArquivo(linkPortal.getLogo()));
                addMensagemRetorno(linkPortal, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LINK, jsonObject, UtilPortalTransparencia.METHOD_POST));
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void recuperarGlossarios() {
        if (singleton.getSelecionado().getEnviarGlossario()) {
            for (GlossarioPortal glossarioPortal : singleton.getSelecionado().getGlossarios()) {
                glossarioPortalFacade.salvar(glossarioPortal);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nome", glossarioPortal.getNome());
                jsonObject.put("descricao", glossarioPortal.getDescricao());
                addMensagemRetorno(glossarioPortal, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_GLOSSARIO, jsonObject, UtilPortalTransparencia.METHOD_POST));
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void recuperarBens() {
        if (singleton.getSelecionado().getEnviarBem()) {
            String condicao = " trunc(bem.DATAAQUISICAO) between to_date('01/01/" + singleton.getSelecionado().getExercicio().getAno() + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(singleton.getSelecionado().getDataOperacao()) + "', 'dd/MM/yyyy')";
            List<VOBemPortal> bens = bemFacade.buscarVOBensPortal(condicao, singleton.getSelecionado().getDataOperacao());
            addMensagem("<b> Montando envio de " + bens.size() + " bens. </b>");
            for (VOBemPortal bem : bens) {
                sincronizarBem(bem, singleton.getSelecionado().getDataOperacao());
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void sincronizarBem(VOBemPortal bem, Date dataOperacao) {
        HierarquiaOrganizacional hierarquiaAdm = hierarquiaOrganizacionalFacade.buscarHierarquiaPorTipoIdUnidadeEData(TipoHierarquiaOrganizacional.ADMINISTRATIVA, bem.getIdUnidadeAdm(), dataOperacao);
        HierarquiaOrganizacional hierarquiaOrc = hierarquiaOrganizacionalFacade.buscarHierarquiaPorTipoIdUnidadeEData(TipoHierarquiaOrganizacional.ORCAMENTARIA, bem.getIdunidadeOrc(), dataOperacao);
        Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(hierarquiaAdm.getSubordinada(), singleton.getSelecionado().getDataOperacao());
        JSONObject jsonObject = montarConteudoLotacao(hierarquiaAdm, entidade.getTipoAdministracao());
        addMensagemRetorno(hierarquiaAdm, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_LOTACAO, jsonObject, UtilPortalTransparencia.METHOD_POST));
        jsonObject = new JSONObject();
        jsonObject.put("identificacao", bem.getIdentificacao());
        jsonObject.put("descricao", bem.getDescricao());
        jsonObject.put("dataAquisicao", bem.getDataAquisicao().getTime());
        jsonObject.put("situacaoConservacaoBem", bem.getSituacaoDeConservacao());
        jsonObject.put("estadoConservacaoBem", bem.getEstadoDoBem());
        jsonObject.put("tipoBem", bem.getTipoDeBem());
        jsonObject.put("tipoAquisicaoBem", bem.getTipoDeAquisicaoDoBem());
        jsonObject.put("valorAquisicao", bem.getValorDeAquisicao());
        jsonObject.put("valorAjustes", bem.getValorAjustes());
        jsonObject.put("valorLiquido", bem.getValorLiquido());
        jsonObject.put("lotacao", montarConteudoLotacao(hierarquiaAdm, entidade.getTipoAdministracao()));
        jsonObject.put("unidade", montarConteudoUnidade(hierarquiaOrc));
        jsonObject.put("responsavel", bem.getResponsavel());
        jsonObject.put("tipoPropriedade", bem.getTipoPropriedade());
        addMensagemRetorno(bem, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_BEM, jsonObject, UtilPortalTransparencia.METHOD_POST));
    }

    private void recuperarContratos() {
        if (singleton.getSelecionado().getEnviarContrato()) {
            List<Contrato> contratoes = contratoFacade.buscarContratoPortalTransparencia(singleton.getSelecionado().getExercicio());
            addMensagem("<b> Montando envio de " + contratoes.size() + " contratos. </b>");
            for (Contrato contrato : contratoes) {
                sincronizarContrato(contrato);
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void sincronizarContrato(Contrato contrato) {
        contrato = contratoFacade.recuperarPortalTransparencia(contrato.getId());
        JSONObject json = montarConteudoContrato(contrato);
        addMensagemRetorno(contrato, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CONTRATO, json, UtilPortalTransparencia.METHOD_POST));
        montarJsonDocumentoAnexos(contrato, json);
        montarJsonItens(contrato, json);
        montarJsonOrdensDeServicos(contrato, json);
        montarJsonGestores(contrato, json);
        montarJsonAlteracoesContratuais(contrato, json);
    }

    public void removerContrato(Contrato contrato, HierarquiaOrganizacional hierarquiaOrganizacional) {
        singleton.inicializar(new PortalTransparenciaNovo(sistemaFacade.getDataOperacao(), contrato.getExercicioContrato()));
        singleton.definirUrl(sistemaFacade.getPerfilAplicacao(), singleton.getSelecionado());
        JSONObject json = montarConteudorRemoverContrato(contrato, hierarquiaOrganizacional);
        String urlServer = singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CONTRATO_REMOVER;
        logger.info("urlServer .: " + urlServer);
        logger.info("json .: " + json);
        addMensagemRetorno(contrato, UtilPortalTransparencia.enviar(urlServer, json, UtilPortalTransparencia.METHOD_POST));
    }

    public String removerServidor(ServidorPortal servidorPortal, Date data) {
        singleton.inicializar(new PortalTransparenciaNovo(data, servidorPortal.getExercicio()));
        singleton.definirUrl(sistemaFacade.getPerfilAplicacao(), singleton.getSelecionado());
        JSONObject json = montarConteudorRemoverServidor(servidorPortal);
        String urlServer = singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_REMOVER;
        String retorno = UtilPortalTransparencia.enviar(urlServer, json, UtilPortalTransparencia.METHOD_POST);
        addMensagemRetorno(servidorPortal, retorno);
        return retorno;
    }

    private JSONObject montarConteudorRemoverContrato(Contrato contrato, HierarquiaOrganizacional hierarquiaOrganizacional) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", contrato.getNumeroTermo());
        jsonObject.put("numeroContrato", contrato.getNumeroContrato());
        jsonObject.put("exercicio", getConteudoJSONExercicio(contrato.getExercicioContrato()));
        jsonObject.put("modalidade", contrato.getModalidadeLicitacao().name());

        if (hierarquiaOrganizacional != null) {
            UnidadeOrganizacional unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
            HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(unidadeOrganizacional, singleton.getSelecionado().getDataOperacao());
            Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(orgao.getSubordinada(), singleton.getSelecionado().getDataOperacao());
            if (entidade != null) {
                JSONObject jsonUnidade = montarConteudoLotacao(orgao, entidade.getTipoAdministracao());
                jsonObject.put("secretaria", jsonUnidade);
            }
        }
        return jsonObject;
    }

    private JSONObject montarConteudorRemoverServidor(ServidorPortal servidorPortal) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("matricula", servidorPortal.getVinculo().getMatriculaFP().getMatricula() + "/" + servidorPortal.getVinculo().getNumero());
        jsonObject.put("mes", servidorPortal.getMes());
        jsonObject.put("exercicio", getConteudoJSONExercicio(servidorPortal.getExercicio()));
        jsonObject.put("tipoFolhaDePagamento", servidorPortal.getTipoFolhaDePagamento().name());
        return jsonObject;
    }

    private void montarJsonDocumentoAnexos(Contrato contrato, JSONObject json) {
        if (contrato.getDetentorDocumentoLicitacao() == null) {
            return;
        }
        for (DocumentoLicitacao documentoLicitacao : contrato.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList()) {
            if (documentoLicitacao.getExibirPortalTransparencia() && documentoLicitacao.getTipoDocumentoAnexo() != null &&
                documentoLicitacao.getArquivo() != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("contrato", json);
                jsonObject.put("tipoArquivo", documentoLicitacao.getTipoDocumentoAnexo().getDescricao());
                jsonObject.put("arquivo", montarJsonArquivo(documentoLicitacao.getArquivo()));
                jsonObject.put("id", documentoLicitacao.getArquivo().getId());
                addMensagemRetorno(documentoLicitacao.getArquivo(),
                    UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() +
                            PortalTransparenciaNovo.WS_CONTRATO_ARQUIVO, jsonObject,
                        UtilPortalTransparencia.METHOD_UPDATE));
            }
        }
    }

    private void montarJsonItens(Contrato contrato, JSONObject json) {
        for (ItemContrato itemContrato : contrato.getItens()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contrato", json);
            jsonObject.put("quantidade", itemContrato.getQuantidadeTotalContrato());
            jsonObject.put("valorUnitario", itemContrato.getValorUnitario());
            jsonObject.put("valorTotal", itemContrato.getValorTotal());
            jsonObject.put("descricao", itemContrato.getItemAdequado().getDescricao());
            jsonObject.put("codigoCmed", itemContrato.getCodigoCmed());
            addMensagemRetorno(contrato, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CONTRATO_ITEM, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void montarJsonOrdensDeServicos(Contrato contrato, JSONObject jsonContrato) {
        for (OrdemDeServicoContrato ordemDeServico : contrato.getOrdensDeServico()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contrato", jsonContrato);
            jsonObject.put("numero", ordemDeServico.getNumero() != null ? ordemDeServico.getNumero().toString() : null);
            jsonObject.put("dataRecebimento", ordemDeServico.getDataRecebimento() != null ? ordemDeServico.getDataRecebimento().getTime() : null);
            jsonObject.put("dataExpedicao", ordemDeServico.getDataExpedicao() != null ? ordemDeServico.getDataExpedicao().getTime() : null);
            jsonObject.put("tipo", ordemDeServico.getTipo() != null ? ordemDeServico.getTipo().name() : null);
            addMensagemRetorno(contrato, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CONTRATO_ORDEM_SERVICO, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void montarJsonGestores(Contrato contrato, JSONObject jsonContrato) {
        for (GestorContrato gestor : contrato.getGestores()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contrato", jsonContrato);
            jsonObject.put("inicioVigencia", gestor.getInicioVigencia() != null ? gestor.getInicioVigencia().getTime() : null);
            jsonObject.put("finalVigencia", gestor.getFinalVigencia() != null ? gestor.getFinalVigencia().getTime() : null);
            jsonObject.put("servidor", gestor.getGestor());
            addMensagemRetorno(contrato, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CONTRATO_GESTOR, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void montarJsonAlteracoesContratuais(Contrato contrato, JSONObject jsonContrato) {
        for (AlteracaoContratual alteracao : contrato.getAlteracoesContratuais()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contrato", jsonContrato);
            jsonObject.put("numero", alteracao.getNumero());
            jsonObject.put("numeroTermo", alteracao.getNumeroTermo());
            jsonObject.put("dataLancamento", alteracao.getDataLancamento() != null ? alteracao.getDataLancamento().getTime() : null);
            jsonObject.put("dataAprovacao", alteracao.getDataAprovacao() != null ? alteracao.getDataAprovacao().getTime() : null);
            jsonObject.put("dataDeferimento", alteracao.getDataDeferimento() != null ? alteracao.getDataDeferimento().getTime() : null);
            jsonObject.put("justificativa", alteracao.getJustificativa());
            jsonObject.put("tipoAlteracaoContratual", alteracao.getTipoAlteracaoContratual() != null ? alteracao.getTipoAlteracaoContratual().name() : null);
            jsonObject.put("situacao", alteracao.getSituacao() != null ? alteracao.getSituacao().name() : null);
            jsonObject.put("exercicio", getConteudoJSONExercicio(alteracao.getExercicio()));
            addMensagemRetorno(contrato, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_CONTRATO_ALTERACAO_CONTRATUAL, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private JSONObject montarConteudoContrato(Contrato selecionado) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", selecionado.getNumeroTermo());
        jsonObject.put("numeroContrato", selecionado.getNumeroContrato() != null ? selecionado.getNumeroContrato() : null);
        jsonObject.put("numeroProcesso", selecionado.getNumeroProcesso() != null ? selecionado.getNumeroProcesso() : null);
        jsonObject.put("exercicio", getConteudoJSONExercicio(selecionado.getExercicioContrato()));
        jsonObject.put("dataAssinatura", selecionado.getDataAssinatura() != null ? selecionado.getDataAssinatura().getTime() : null);
        jsonObject.put("prazoVigencia", selecionado.getTerminoVigenciaAtual() != null ? selecionado.getTerminoVigenciaAtual().getTime() : null);
        jsonObject.put("situacaoContrato", selecionado.getSituacaoContrato() != null ? selecionado.getSituacaoContrato().name() : null);
        jsonObject.put("dataLancamento", selecionado.getDataLancamento() != null ? selecionado.getDataLancamento().getTime() : null);

        JSONObject conteudoPessoa = montarConteudoJSONPessoa(selecionado.getContratado());
        addMensagemRetorno(selecionado.getContratado(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, conteudoPessoa, UtilPortalTransparencia.METHOD_POST));

        jsonObject.put("fornecedor", conteudoPessoa);
        jsonObject.put("resumoDoObjeto", selecionado.getObjeto());
        jsonObject.put("localEntrega", selecionado.getLocalEntrega());
        if (selecionado.getLicitacao() != null) {
            Licitacao licitacao = licitacaoFacade.recuperarParaPortal(selecionado.getLicitacao().getId());
            JSONObject jsonLicitacao = montarConteudoLicitacao(licitacao);
            if (singleton.getSelecionado().getEnviarContratoLicitacao()) {
                sincronizarLicitacao(licitacao);
            }
            jsonObject.put("licitacao", jsonLicitacao);
            jsonObject.put("tipoSituacaoLicitacao", licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().name());
        }
        jsonObject.put("modalidade", selecionado.getModalidadeLicitacao().name());
        if (selecionado.getTerminoVigencia() != null) {
            HierarquiaOrganizacional hierarquiaOrganizacional = contratoFacade.buscarHierarquiaVigenteContrato(selecionado, selecionado.getTerminoVigencia());
            if (hierarquiaOrganizacional != null) {
                UnidadeOrganizacional unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
                HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(unidadeOrganizacional, selecionado.getInicioVigencia());
                Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(orgao.getSubordinada(), selecionado.getInicioVigencia());
                if (entidade != null) {
                    JSONObject jsonUnidade = montarConteudoLotacao(orgao, entidade.getTipoAdministracao());
                    addMensagemRetorno(orgao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SERVIDOR_LOTACAO, jsonUnidade, "POST"));
                    jsonObject.put("secretaria", jsonUnidade);
                }
            }
        }
        jsonObject.put("valor", selecionado.getValorTotalItensContrato());
        jsonObject.put("variacaoAtualContrato", selecionado.getVariacaoAtualContrato());
        jsonObject.put("valorAtualContrato", selecionado.getValorAtualContrato());
        jsonObject.put("saldoAtualContrato", selecionado.getSaldoAtualContrato());
        return jsonObject;
    }

    private void recuperarLicitacao() {
        if (singleton.getSelecionado().getEnviarLicitacao()) {
            List<Licitacao> licitacaos = licitacaoFacade.buscarLicitacoesPortalTransparencia(singleton.getSelecionado().getExercicio());
            addMensagem("<b> Montando envio de " + licitacaos.size() + " Licitações. </b>");
            for (Licitacao licitacaoDaVez : licitacaos) {
                sincronizarLicitacao(licitacaoDaVez);
            }

            List<DispensaDeLicitacao> dispensas = dispensaDeLicitacaoFacade.buscarDispensaLicitacoesPortalTransparencia(singleton.getSelecionado().getExercicio());
            addMensagem("<b> Montando envio de " + dispensas.size() + " dispensa de Licitações. </b>");
            for (DispensaDeLicitacao dispensaDeLicitacao : dispensas) {
                sincronizarDispensaLicitacao(dispensaDeLicitacao);
            }

            List<AtaRegistroPreco> atas = ataRegistroPrecoFacade.buscarAtaPortalTransparencia(singleton.getSelecionado().getExercicio());
            addMensagem("<b> Montando envio de " + atas.size() + " ata de registro de preço. </b>");
            for (AtaRegistroPreco ata : atas) {
                sincronizarAtaRegistroPreco(ata);
            }

            List<RegistroSolicitacaoMaterialExterno> atasExternas = registroSolicitacaoMaterialExternoFacade.buscarAtaPortalTransparencia(singleton.getSelecionado().getExercicio());
            addMensagem("<b> Montando envio de " + atasExternas.size() + " ata de registro de preço externo. </b>");
            for (RegistroSolicitacaoMaterialExterno ata : atasExternas) {
                sincronizarAtaRegistroPrecoExterno(ata);
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void sincronizarAtaRegistroPrecoExterno(RegistroSolicitacaoMaterialExterno ata) {
        ata = registroSolicitacaoMaterialExternoFacade.recuperar(ata.getId());
        JSONObject jsonObject = montarConteudoAtaRegistroPrecoExterno(ata);

        addMensagemRetorno(ata, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO, jsonObject, UtilPortalTransparencia.METHOD_POST));

        montarJsonAtaRegistroPrecoExternoPublicacao(ata, jsonObject);
        montarJsonAtaRegistroPrecoExternoFornecedor(ata, jsonObject);
    }

    private void montarJsonAtaRegistroPrecoExternoFornecedor(RegistroSolicitacaoMaterialExterno ata, JSONObject jsonLicitacao) {
        for (RegistroSolicitacaoMaterialExternoFornecedor fornecedor : ata.getFornecedores()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licitacao", jsonLicitacao);
            jsonObject.put("pessoa", montarConteudoJSONPessoa(fornecedor.getPessoa()));
            enviarJsonPessoa(fornecedor.getPessoa());
            addMensagemRetorno(fornecedor, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_FORNECEDOR, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }

    }

    private void montarJsonAtaRegistroPrecoExternoPublicacao(RegistroSolicitacaoMaterialExterno ata, JSONObject jsonLicitacao) {
        for (RegistroSolicitacaoMaterialExternoPublicacao publicacao : ata.getPublicacoes()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licitacao", jsonLicitacao);
            if (publicacao.getVeiculoDePublicacao() != null) {
                jsonObject.put("veiculoDePublicacao", publicacao.getVeiculoDePublicacao().getNome());
            }
            if (publicacao.getDataPublicacao() != null) {
                jsonObject.put("dataPublicacao", publicacao.getDataPublicacao().getTime());
            }
            if (publicacao.getTipoPublicacao() != null) {
                jsonObject.put("tipo", publicacao.getTipoPublicacao().name());
            }
            jsonObject.put("observacoes", publicacao.getObservacao());
            jsonObject.put("numeroEdicaoPublicacao", publicacao.getNumeroEdicaoPublicacao());
            jsonObject.put("numeroPagina", publicacao.getNumeroPagina());
            addMensagemRetorno(publicacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_PUBLICACAO, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private JSONObject montarConteudoAtaRegistroPrecoExterno(RegistroSolicitacaoMaterialExterno ata) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numeroModalidade", ata.getNumeroModalidade());
        jsonObject.put("numero", ata.getNumeroAtaCarona());
        jsonObject.put("exercicio", getConteudoJSONExercicio(ata.getExercicioRegistro()));
        jsonObject.put("modalidade", ata.getModalidadeCarona().name());
        jsonObject.put("valor", BigDecimal.ZERO);
        jsonObject.put("dataAbertura", ata.getDataAutorizacaoCarona().getTime());
        jsonObject.put("resumoDoObjeto", ata.getObjeto());

        UnidadeOrganizacional unidadeOrganizacional = ata.getUnidadeOrganizacional();
        HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(unidadeOrganizacional, singleton.getSelecionado().getDataOperacao());
        Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(orgao.getSubordinada(), singleton.getSelecionado().getDataOperacao());
        if (entidade != null) {
            jsonObject.put("secretaria", montarConteudoLotacao(orgao, entidade.getTipoAdministracao()));
        }
        jsonObject.put("apuracao", ata.getTipoApuracao().name());
        jsonObject.put("avaliacao", ata.getTipoAvaliacao().name());
        jsonObject.put("tipoAdesaoCarona", TipoSolicitacaoRegistroPreco.EXTERNA.name());
        return jsonObject;
    }

    private void sincronizarAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        ataRegistroPreco = ataRegistroPrecoFacade.recuperar(ataRegistroPreco.getId());
        JSONObject jsonObject = montarConteudoAtaRegistroPreco(ataRegistroPreco);
        addMensagemRetorno(ataRegistroPreco, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO, jsonObject, UtilPortalTransparencia.METHOD_POST));

        LOA loa = recuperarLoa();

        List<DotacaoSolicitacaoMaterial> dotacoes = dotacaoSolicitacaoMaterialFacade.recuperarDotacoesAPartirDaLicitacao(ataRegistroPreco.getLicitacao());
        Set<Conta> contas = Sets.newHashSet();
        Set<FonteDeRecursos> fontes = Sets.newHashSet();
        Set<ProvisaoPPADespesa> provisaoPPADespesas = Sets.newHashSet();
        for (DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial : dotacoes) {
            dotacaoSolicitacaoMaterial = dotacaoSolicitacaoMaterialFacade.recuperar(dotacaoSolicitacaoMaterial.getId());
            for (DotacaoSolicitacaoMaterialItem item : dotacaoSolicitacaoMaterial.getItens()) {
                for (DotacaoSolicitacaoMaterialItemFonte detalhe : item.getFontes()) {
                    contas.add(detalhe.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa());
                    provisaoPPADespesas.add(detalhe.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa());
                    fontes.add(detalhe.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos());
                }

            }
        }
        Licitacao licitacaoDaVez = licitacaoFacade.recuperarParaPortal(ataRegistroPreco.getLicitacao().getId());
        JSONObject jsonLicitacao = montarConteudoLicitacao(licitacaoDaVez);

        montarJsonDispensaLicitacaoProgramaTrabalho(jsonObject, provisaoPPADespesas, loa);
        montarJsonLicitacaoNaturezaEFonte(jsonObject, contas, fontes);
        montarJsonLicitacaoStatus(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoPublicacao(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoDocumentos(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoAnexos(licitacaoDaVez, jsonLicitacao);
    }

    private JSONObject montarConteudoAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        Licitacao selecionado = ataRegistroPreco.getLicitacao();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numeroModalidade", selecionado.getProcessoDeCompra().getNumero());
        jsonObject.put("numero", ataRegistroPreco.getNumero());
        jsonObject.put("exercicio", getConteudoJSONExercicio(selecionado.getProcessoDeCompra().getExercicio()));
        jsonObject.put("modalidade", selecionado.getModalidadeLicitacao().name());
        jsonObject.put("valor", selecionado.getValorMaximo());
        jsonObject.put("dataAbertura", ataRegistroPreco.getDataInicio().getTime());
        jsonObject.put("resumoDoObjeto", selecionado.getResumoDoObjeto());

        UnidadeOrganizacional unidadeOrganizacional = selecionado.getProcessoDeCompra().getUnidadeOrganizacional();
        HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(unidadeOrganizacional, singleton.getSelecionado().getDataOperacao());
        Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(orgao.getSubordinada(), singleton.getSelecionado().getDataOperacao());
        if (entidade != null) {
            jsonObject.put("secretaria", montarConteudoLotacao(orgao, entidade.getTipoAdministracao()));
        }
        jsonObject.put("apuracao", selecionado.getTipoApuracao().name());
        jsonObject.put("avaliacao", selecionado.getTipoAvaliacao().name());
        jsonObject.put("tipoAdesaoCarona", TipoSolicitacaoRegistroPreco.INTERNA.name());
        return jsonObject;
    }

    private void sincronizarDispensaLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        dispensaDeLicitacao = dispensaDeLicitacaoFacade.recuperar(dispensaDeLicitacao.getId());
        JSONObject jsonLicitacao = montarConteudoDispensaLicitacao(dispensaDeLicitacao);
        addMensagemRetorno(dispensaDeLicitacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO, jsonLicitacao, UtilPortalTransparencia.METHOD_POST));

        List<DotacaoSolicitacaoMaterial> dotacoes = dotacaoSolicitacaoMaterialFacade.recuperarDotacoesAPartirDaDispensaLicitacao(dispensaDeLicitacao);
        Set<Conta> contas = Sets.newHashSet();
        Set<FonteDeRecursos> fontes = Sets.newHashSet();
        Set<ProvisaoPPADespesa> provisaoPPADespesas = Sets.newHashSet();

        LOA loa = recuperarLoa();

        for (DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial : dotacoes) {
            dotacaoSolicitacaoMaterial = dotacaoSolicitacaoMaterialFacade.recuperar(dotacaoSolicitacaoMaterial.getId());
            for (DotacaoSolicitacaoMaterialItem item : dotacaoSolicitacaoMaterial.getItens()) {
                for (DotacaoSolicitacaoMaterialItemFonte detalhe : item.getFontes()) {
                    contas.add(detalhe.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa());
                    provisaoPPADespesas.add(detalhe.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa());
                    fontes.add(detalhe.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos());
                }

            }
        }

        montarJsonDispensaLicitacaoProgramaTrabalho(jsonLicitacao, provisaoPPADespesas, loa);
        montarJsonLicitacaoNaturezaEFonte(jsonLicitacao, contas, fontes);
        montarJsonDispensaLicitacaoPublicacao(dispensaDeLicitacao, jsonLicitacao);
        montarJsonDispensaLicitacaoFornecedor(dispensaDeLicitacao, jsonLicitacao);
        montarJsonDispensaLicitacaoStatus(dispensaDeLicitacao, jsonLicitacao);
        /*  montarJsonDispensaLicitacaoDocumentos(licitacaoDaVez, jsonLicitacao);*/
        montarJsonDispensaLicitacaoAnexos(dispensaDeLicitacao, jsonLicitacao);
    }

    private void montarJsonDispensaLicitacaoProgramaTrabalho(JSONObject jsonLicitacao, Set<ProvisaoPPADespesa> provisaoPPADespesas, LOA loa) {
        for (ProvisaoPPADespesa conta : provisaoPPADespesas) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licitacao", jsonLicitacao);
            jsonObject.put("previsaoDespesa", montaJSOProvisaoPPADespesa(conta, loa));
            addMensagemRetorno(conta, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_PROGRAMA_TRABALHO, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void montarJsonDispensaLicitacaoFornecedor(DispensaDeLicitacao dispensaDeLicitacao, JSONObject jsonLicitacao) {
        for (FornecedorDispensaDeLicitacao fornecedor : dispensaDeLicitacao.getFornecedores()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licitacao", jsonLicitacao);
            jsonObject.put("pessoa", montarConteudoJSONPessoa(fornecedor.getPessoa()));
            enviarJsonPessoa(fornecedor.getPessoa());
            addMensagemRetorno(fornecedor, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_FORNECEDOR, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }

    }

    private void montarJsonDispensaLicitacaoAnexos(DispensaDeLicitacao dispensaDeLicitacao, JSONObject jsonLicitacao) {
        if (dispensaDeLicitacao.getDetentorDocumentoLicitacao() == null) {
            return;
        }
        for (DocumentoLicitacao documentoLicitacao : dispensaDeLicitacao.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList()) {
            if (documentoLicitacao.getExibirPortalTransparencia()) {
                if (documentoLicitacao.getArquivo() != null && documentoLicitacao.getTipoDocumentoAnexo() != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("licitacao", jsonLicitacao);
                    jsonObject.put("tipoArquivo", documentoLicitacao.getTipoDocumentoAnexo().getDescricao());
                    jsonObject.put("arquivo", montarJsonArquivo(documentoLicitacao.getArquivo()));
                    addMensagemRetorno(documentoLicitacao.getArquivo(),
                        UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() +
                                PortalTransparenciaNovo.WS_LICITACAO_ARQUIVO, jsonObject,
                            UtilPortalTransparencia.METHOD_UPDATE));
                }
            }
        }
    }

    private void montarJsonDispensaLicitacaoPublicacao(DispensaDeLicitacao dispensaDeLicitacao, JSONObject jsonLicitacao) {
        for (PublicacaoDispensaDeLicitacao publicacao : dispensaDeLicitacao.getPublicacoes()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licitacao", jsonLicitacao);
            if (publicacao.getVeiculoDePublicacao() != null) {
                jsonObject.put("veiculoDePublicacao", publicacao.getVeiculoDePublicacao().getNome());
            }
            if (publicacao.getDataDaPublicacao() != null) {
                jsonObject.put("dataPublicacao", publicacao.getDataDaPublicacao().getTime());
            }
            jsonObject.put("tipo", "OUTROS");
            jsonObject.put("observacoes", publicacao.getObservacoes());
            jsonObject.put("numeroEdicaoPublicacao", publicacao.getNumeroDaEdicaoDaPublicacao());
            jsonObject.put("numeroPagina", publicacao.getNumeroDaPagina());
            addMensagemRetorno(publicacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_PUBLICACAO, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void montarJsonDispensaLicitacaoStatus(DispensaDeLicitacao dispensaDeLicitacao, JSONObject jsonLicitacao) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("licitacao", jsonLicitacao);
        jsonObject.put("numero", 1);
        jsonObject.put("situacao", dispensaDeLicitacao.getSituacao().name());
        jsonObject.put("data", dispensaDeLicitacao.getDataDaDispensa().getTime());
        jsonObject.put("motivo", "");
        jsonObject.put("numeroEdicaoPublicacao", "");
        jsonObject.put("numeroPagina", "");
        addMensagemRetorno(dispensaDeLicitacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_STATUS, jsonObject, UtilPortalTransparencia.METHOD_POST));
    }

    private JSONObject montarConteudoDispensaLicitacao(DispensaDeLicitacao selecionado) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numeroModalidade", selecionado.getProcessoDeCompra().getNumero());
        jsonObject.put("numero", selecionado.getNumeroDaDispensa());
        jsonObject.put("exercicio", getConteudoJSONExercicio(selecionado.getProcessoDeCompra().getExercicio()));
        jsonObject.put("modalidade", selecionado.getModalidade().name());
        jsonObject.put("valor", selecionado.getValorMaximo());
        if (selecionado.getDataDaDispensa() != null) {
            jsonObject.put("dataAbertura", selecionado.getDataDaDispensa().getTime());
        }
        jsonObject.put("resumoDoObjeto", selecionado.getResumoDoObjeto());
        jsonObject.put("fundamentacaoLegal", selecionado.getFundamentacaoLegal());
        UnidadeOrganizacional unidadeOrganizacional = selecionado.getProcessoDeCompra().getUnidadeOrganizacional();
        HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(unidadeOrganizacional, singleton.getSelecionado().getDataOperacao());
        Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(orgao.getSubordinada(), singleton.getSelecionado().getDataOperacao());
        if (entidade != null) {
            jsonObject.put("secretaria", montarConteudoLotacao(orgao, entidade.getTipoAdministracao()));
        }
        jsonObject.put("apuracao", selecionado.getTipoDeApuracao().name());
        jsonObject.put("avaliacao", selecionado.getTipoDeAvaliacao().name());
        enviarJsonPessoa(selecionado.getUsuarioSistema().getPessoaFisica());
        jsonObject.put("responsavel", montarConteudoJSONPessoa(selecionado.getUsuarioSistema().getPessoaFisica()));
        return jsonObject;
    }

    private void recuperarObras() {
        if (singleton.getSelecionado().getEnviarObra()) {
            List<Obra> obras = obraFacade.buscarTodasObras();
            addMensagem("<b> Montando envio de " + obras.size() + " Obras. </b>");
            for (Obra obra : obras) {
                sincronizarObra(obra);
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void recuperarCalamidadesPublicas() {
        if (singleton.getSelecionado().getEnviarCalamidadePublica()) {
            List<CalamidadePublica> calamidades = calamidadePublicaFacade.lista();
            addMensagem("<b> Montando envio de " + calamidades.size() + " Calamidades Públicas. </b>");
            for (CalamidadePublica calamidade : calamidades) {
                calamidade = calamidadePublicaFacade.recuperar(calamidade.getId());

                sincronizarCalamidadePublica(calamidade);
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void recuperarDicionarioDeDados() {
        if (singleton.getSelecionado().getEnviarDicionarioDeDados()) {
            List<DicionarioDeDados> dicionarios = dicionarioDeDadosFacade.lista();
            addMensagem("<b> Montando envio de " + dicionarios.size() + " Dicionários de Dados. </b>");
            for (DicionarioDeDados dicionario : dicionarios) {
                dicionario = dicionarioDeDadosFacade.recuperar(dicionario.getId());
                sincronizarDicionarioDeDados(dicionario);
            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void sincronizarLicitacao(Licitacao licitacaoDaVez) {
        licitacaoDaVez = licitacaoFacade.recuperarParaPortal(licitacaoDaVez.getId());
        JSONObject jsonLicitacao = montarConteudoLicitacao(licitacaoDaVez);
        addMensagemRetorno(licitacaoDaVez, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO, jsonLicitacao, UtilPortalTransparencia.METHOD_POST));

        List<DotacaoSolicitacaoMaterial> dotacoes = dotacaoSolicitacaoMaterialFacade.recuperarDotacoesAPartirDaLicitacao(licitacaoDaVez);
        Set<Conta> contas = Sets.newHashSet();
        Set<FonteDeRecursos> fontes = Sets.newHashSet();
        for (DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial : dotacoes) {
            dotacaoSolicitacaoMaterial = dotacaoSolicitacaoMaterialFacade.recuperar(dotacaoSolicitacaoMaterial.getId());
            for (DotacaoSolicitacaoMaterialItem item : dotacaoSolicitacaoMaterial.getItens()) {
                for (DotacaoSolicitacaoMaterialItemFonte detalhe : item.getFontes()) {
                    contas.add(detalhe.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa());
                    fontes.add(detalhe.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos());
                }

            }
        }

        montarJsonLicitacaoNaturezaEFonte(jsonLicitacao, contas, fontes);
        montarJsonLicitacaoStatus(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoPublicacao(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoDocumentos(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoAnexos(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoMapaComparativo(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoCotacao(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoPropostas(licitacaoDaVez, jsonLicitacao);
        montarJsonLicitacaoVencedor(licitacaoDaVez, jsonLicitacao);
        montarJsonExtratoLicitacao(licitacaoDaVez, jsonLicitacao);
    }

    private void montarJsonExtratoLicitacao(Licitacao licitacaoDaVez, JSONObject jsonLicitacao) {
        List<Object[]> extratos = licitacaoFacade.buscarExtratoLicitacaoParaPortal(licitacaoDaVez);
        for (Object[] obj : extratos) {
            JSONObject jsonExtrato = new JSONObject();
            jsonExtrato.put("licitacao", jsonLicitacao);
            jsonExtrato.put("funcional", (String) obj[0]);
            jsonExtrato.put("fonte", (String) obj[1]);
            jsonExtrato.put("conta", (String) obj[2]);
            jsonExtrato.put("dataRegistro", obj[3] != null ? ((Date) obj[3]).getTime() : null);
            jsonExtrato.put("numero", (String) obj[4]);
            jsonExtrato.put("operacao", (String) obj[5]);
            jsonExtrato.put("valor", (BigDecimal) obj[6]);
            addMensagemRetorno(licitacaoDaVez, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_EXTRATO, jsonExtrato, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private JSONObject montarConteudoJsonProcessoDeCompra(ProcessoDeCompra processoDeCompra) {
        if (processoDeCompra != null) {
            JSONObject jsonProcessoDeCompra = new JSONObject();
            jsonProcessoDeCompra.put("numero", processoDeCompra.getNumero() != null ? processoDeCompra.getNumero().toString() : "");
            jsonProcessoDeCompra.put("dataProcesso", processoDeCompra.getDataProcesso() != null ? processoDeCompra.getDataProcesso().getTime() : null);
            jsonProcessoDeCompra.put("situacao", "Concluído");
            jsonProcessoDeCompra.put("exercicio", getConteudoJSONExercicio(processoDeCompra.getExercicio()));
            jsonProcessoDeCompra.put("solicitacaoMaterial", montarConteudoJsonSolicitacaoMaterial(processoDeCompra.getSolicitacaoMaterial()));
            if (processoDeCompra.getResponsavel() != null && processoDeCompra.getResponsavel().getPessoaFisica() != null) {
                jsonProcessoDeCompra.put("pessoa", montarConteudoJSONPessoa(processoDeCompra.getResponsavel().getPessoaFisica()));
            }
            addMensagemRetorno(processoDeCompra, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_PROCESSO_DE_COMPRA, jsonProcessoDeCompra, UtilPortalTransparencia.METHOD_POST));
            return jsonProcessoDeCompra;
        }
        return null;
    }

    private JSONObject montarConteudoJsonSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        if (solicitacaoMaterial != null) {
            solicitacaoMaterial = solicitacaoMaterialFacade.recuperarInicializandoAvaliacoes(solicitacaoMaterial.getId());
            AvaliacaoSolicitacaoDeCompra avaliacaoAtual = solicitacaoMaterial.getAvaliacaoAtual();
            JSONObject jsonSolicitacaoMaterial = new JSONObject();
            jsonSolicitacaoMaterial.put("numero", solicitacaoMaterial.getNumero() != null ? solicitacaoMaterial.getNumero().toString() : "");
            jsonSolicitacaoMaterial.put("dataSolicitacao", solicitacaoMaterial.getDataSolicitacao() != null ? solicitacaoMaterial.getDataSolicitacao().getTime() : null);
            jsonSolicitacaoMaterial.put("exercicio", getConteudoJSONExercicio(solicitacaoMaterial.getExercicio()));
            jsonSolicitacaoMaterial.put("situacaoAtual", (avaliacaoAtual != null && avaliacaoAtual.getTipoStatusSolicitacao() != null) ? avaliacaoAtual.getTipoStatusSolicitacao().getDescricao() : null);
            if (solicitacaoMaterial.getUsuarioCriacao() != null && solicitacaoMaterial.getUsuarioCriacao().getPessoaFisica() != null) {
                jsonSolicitacaoMaterial.put("pessoa", montarConteudoJSONPessoa(solicitacaoMaterial.getUsuarioCriacao().getPessoaFisica()));
            }

            addMensagemRetorno(solicitacaoMaterial, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PROCESSO_DE_COMPRA_SOLICITACAO_DE_COMPRA, jsonSolicitacaoMaterial, UtilPortalTransparencia.METHOD_POST));
            montarConteudoJsonDotacaoSolicitacaoMaterial(solicitacaoMaterial, jsonSolicitacaoMaterial);
            if (solicitacaoMaterial.getAvaliacoes() != null && !solicitacaoMaterial.getAvaliacoes().isEmpty()) {
                for (AvaliacaoSolicitacaoDeCompra avaliacao : solicitacaoMaterial.getAvaliacoes()) {
                    if (!TipoStatusSolicitacao.AGUARDANDO_AVALIACAO.equals(avaliacao.getTipoStatusSolicitacao())) {
                        montarConteudoJsonAvaliacaoSolicitacaoDeCompra(avaliacao, jsonSolicitacaoMaterial);
                    }
                }
            }
            return jsonSolicitacaoMaterial;
        }
        return null;
    }

    private void montarConteudoJsonDotacaoSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial, JSONObject jsonSolicitacao) {
        if (solicitacaoMaterial != null) {
            DotacaoSolicitacaoMaterial dotacao = dotacaoSolicitacaoMaterialFacade.recuperarDotacaoPorSolicitacaoCompra(solicitacaoMaterial);
            if (dotacao != null) {
                JSONObject jsonDotacao = new JSONObject();
                jsonDotacao.put("solicitacaoMaterial", jsonSolicitacao);
                jsonDotacao.put("codigo", dotacao.getCodigo() != null ? dotacao.getCodigo().toString() : "");
                jsonDotacao.put("realizadaEm", dotacao.getRealizadaEm() != null ? dotacao.getRealizadaEm().getTime() : null);
                jsonDotacao.put("situacao", "Concluída");
                addMensagemRetorno(dotacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SOLICITACAO_DE_COMPRA_DOTACAO, jsonDotacao, UtilPortalTransparencia.METHOD_POST));
            }
        }
    }

    private void montarConteudoJsonAvaliacaoSolicitacaoDeCompra(AvaliacaoSolicitacaoDeCompra avaliacao, JSONObject jsonSolicitacao) {
        if (avaliacao != null) {
            JSONObject jsonAvaliacao = new JSONObject();
            jsonAvaliacao.put("dataAvaliacao", avaliacao.getDataAvaliacao() != null ? avaliacao.getDataAvaliacao().getTime() : null);
            jsonAvaliacao.put("situacao", avaliacao.getTipoStatusSolicitacao() != null ? avaliacao.getTipoStatusSolicitacao().name() : null);
            jsonAvaliacao.put("solicitacaoMaterial", jsonSolicitacao);
            if (avaliacao.getUsuarioSistema() != null && avaliacao.getUsuarioSistema().getPessoaFisica() != null) {
                jsonAvaliacao.put("pessoa", montarConteudoJSONPessoa(avaliacao.getUsuarioSistema().getPessoaFisica()));
            }
            addMensagemRetorno(avaliacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_SOLICITACAO_DE_COMPRA_AVALIACAO_SOLICITACAO, jsonAvaliacao, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void montarJsonLicitacaoVencedor(Licitacao licitacaoDaVez, JSONObject jsonLicitacao) {
        List<LicitacaoFornecedor> licitacaoFornecedors = licitacaoFacade.getFornecedoresVencedoresHabilitados(licitacaoDaVez);
        for (LicitacaoFornecedor fornecedor : licitacaoFornecedors) {
            enviarJsonPessoa(fornecedor.getEmpresa());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tipoClassificacaoFornecedor", fornecedor.getTipoClassificacaoFornecedor() != null ? fornecedor.getTipoClassificacaoFornecedor().name() : null);
            jsonObject.put("classificacaoTecnica", fornecedor.getClassificacaoTecnica() != null ? fornecedor.getClassificacaoTecnica().name() : null);
            jsonObject.put("licitacao", jsonLicitacao);
            jsonObject.put("empresa", montarConteudoJSONPessoa(fornecedor.getEmpresa()));
            addMensagemRetorno(fornecedor, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_VENCEDOR, jsonObject, UtilPortalTransparencia.METHOD_POST));

            for (LicitacaoDoctoFornecedor licitacaoDoctoFornecedor : fornecedor.getDocumentosFornecedor()) {
                JSONObject jsonLicitacaoDoctoFornecedor = new JSONObject();
                jsonLicitacaoDoctoFornecedor.put("doctoHabilitacao", montarConteudoJsonDoctoHabilitacao(licitacaoDoctoFornecedor.getDoctoHabilitacao()));
                jsonLicitacaoDoctoFornecedor.put("licitacaoFornecedor", jsonObject);
                addMensagemRetorno(licitacaoDoctoFornecedor, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_DOCTO_FORNECEDOR, jsonLicitacaoDoctoFornecedor, UtilPortalTransparencia.METHOD_POST));
            }
        }
    }

    private JSONObject montarConteudoJsonDoctoHabilitacao(DoctoHabilitacao doctoHabilitacao) {
        if (doctoHabilitacao != null) {
            JSONObject jsonDoctoHabilitacao = new JSONObject();
            jsonDoctoHabilitacao.put("doctoHabilitacao", doctoHabilitacao.getDescricao());
            jsonDoctoHabilitacao.put("tipoDoctoHabilitacao", doctoHabilitacao.getTipoDoctoHabilitacao() != null ? doctoHabilitacao.getTipoDoctoHabilitacao().toString() : null);
            jsonDoctoHabilitacao.put("inicioVigencia", doctoHabilitacao.getInicioVigencia() != null ? doctoHabilitacao.getInicioVigencia().getTime() : null);
            jsonDoctoHabilitacao.put("fimVigencia", doctoHabilitacao.getFimVigencia() != null ? doctoHabilitacao.getFimVigencia().getTime() : null);
            addMensagemRetorno(doctoHabilitacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_DOCTO_HABILITACAO, jsonDoctoHabilitacao, UtilPortalTransparencia.METHOD_POST));
            return jsonDoctoHabilitacao;
        }
        return null;
    }

    private void montarJsonLicitacaoStatus(Licitacao licitacaoDaVez, JSONObject jsonLicitacao) {
        for (StatusLicitacao statusLicitacao : licitacaoDaVez.getListaDeStatusLicitacao()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licitacao", jsonLicitacao);
            jsonObject.put("numero", statusLicitacao.getNumero());
            jsonObject.put("situacao", statusLicitacao.getTipoSituacaoLicitacao().name());
            if (statusLicitacao.getDataStatus() != null) {
                jsonObject.put("data", statusLicitacao.getDataStatus().getTime());
            }
            jsonObject.put("motivo", statusLicitacao.getMotivoStatusLicitacao());
            if (statusLicitacao.getDataPublicacao() != null) {
                jsonObject.put("dataPublicacao", statusLicitacao.getDataPublicacao().getTime());
            }
            if (statusLicitacao.getVeiculoDePublicacao() != null) {
                jsonObject.put("veiculoDePublicacao", statusLicitacao.getVeiculoDePublicacao().getNome());
            }
            jsonObject.put("numeroEdicaoPublicacao", statusLicitacao.getNumeroEdicao());
            jsonObject.put("numeroPagina", statusLicitacao.getPaginaPublicacao());
            addMensagemRetorno(statusLicitacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_STATUS, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }

        List<StatusFornecedorLicitacao> statusFornecedorLicitacaos = statusFornecedorLicitacaoFacade.buscarPorLicitacao(licitacaoDaVez);
        if (statusFornecedorLicitacaos != null) {
            for (StatusFornecedorLicitacao statusLicitacao : statusFornecedorLicitacaos) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("licitacao", jsonLicitacao);
                jsonObject.put("numero", statusLicitacao.getNumero());
                jsonObject.put("situacao", statusLicitacao.getTipoSituacao().name());
                if (statusLicitacao.getDataSituacao() != null) {
                    jsonObject.put("data", statusLicitacao.getDataSituacao().getTime());
                }
                jsonObject.put("motivo", statusLicitacao.getMotivo());
                if (statusLicitacao.getDataSituacao() != null) {
                    jsonObject.put("dataPublicacao", statusLicitacao.getDataSituacao().getTime());
                }
                addMensagemRetorno(statusLicitacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_STATUS, jsonObject, UtilPortalTransparencia.METHOD_POST));
            }
        }
    }

    private void montarJsonLicitacaoPublicacao(Licitacao licitacaoDaVez, JSONObject jsonLicitacao) {
        for (PublicacaoLicitacao publicacaoLicitacao : licitacaoDaVez.getPublicacoes()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licitacao", jsonLicitacao);
            if (publicacaoLicitacao.getVeiculoDePublicacao() != null) {
                jsonObject.put("veiculoDePublicacao", publicacaoLicitacao.getVeiculoDePublicacao().getNome());
            }
            if (publicacaoLicitacao.getDataPublicacao() != null) {
                jsonObject.put("dataPublicacao", publicacaoLicitacao.getDataPublicacao().getTime());
            }
            if (publicacaoLicitacao.getTipoPublicacaoLicitacao() != null) {
                jsonObject.put("tipo", publicacaoLicitacao.getTipoPublicacaoLicitacao().name());
            }
            jsonObject.put("observacoes", publicacaoLicitacao.getObservacoes());
            jsonObject.put("numeroEdicaoPublicacao", publicacaoLicitacao.getNumeroEdicaoPublicacao());
            jsonObject.put("numeroPagina", publicacaoLicitacao.getNumeroPagina());
            addMensagemRetorno(publicacaoLicitacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_PUBLICACAO, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void montarJsonLicitacaoNaturezaEFonte(JSONObject jsonLicitacao, Set<Conta> contas, Set<FonteDeRecursos> fontes) {


        for (Conta conta : contas) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licitacao", jsonLicitacao);
            jsonObject.put("elementoDespesa", montarJSONElementoDespesa(conta));
            addMensagemRetorno(conta, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_NATUREZA_OBJETO, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }

        for (FonteDeRecursos fonte : fontes) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licitacao", jsonLicitacao);
            jsonObject.put("fonteDeRecurso", montarJSONFonteDeRecurso(fonte));
            addMensagemRetorno(fonte, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_FONTE_DE_RECURSO, jsonObject, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void montarJsonLicitacaoDocumentos(Licitacao licitacaoDaVez, JSONObject jsonLicitacao) {
        for (DoctoLicitacao docto : licitacaoDaVez.getListaDeDocumentos()) {

            List<ItemProcessoDeCompra> itensProcessoCompra = licitacaoFacade.ordenaLotesEItensDoProcessoEPreencheLista(licitacaoDaVez.getProcessoDeCompra());

            ModeloDocto novoModelo = new ModeloDocto();
            novoModelo.setModelo(documentoLicitacaoFacade.mesclaTagsModelo(docto.getModeloDocto(), licitacaoDaVez, itensProcessoCompra));
            novoModelo.setNome(docto.getModeloDocto().getNome());
            novoModelo.setModelo(FacesUtil.alteraURLImagens(novoModelo.getModelo()));
            Arquivo arquivo = montarArquivoEdital(novoModelo);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", arquivo.getId());
            jsonObject.put("licitacao", jsonLicitacao);
            jsonObject.put("tipoArquivo", docto.getModeloDocto().getNome());
            jsonObject.put("arquivo", montarJsonArquivo(arquivo));
            addMensagemRetorno(docto, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_ARQUIVO, jsonObject, UtilPortalTransparencia.METHOD_UPDATE));
        }
    }

    private Arquivo montarArquivoEdital(ModeloDocto modelo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
                + " <html>"
                + " <head>"
                + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
                + " <title>"
                + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">"
                + modelo.getNome()
                + " </title>"
                + " </head>"
                + " <body>"
                + modelo.getModelo()
                + " </body>"
                + " </html>", baos);
            byte[] bytes = baos.toByteArray();

            Arquivo arquivo = new Arquivo();
            arquivo.setDescricao(modelo.getNome());
            arquivo.setMimeType("application/pdf");
            arquivo.setNome(modelo.getNome());
            return arquivoFacade.novoArquivoMemoria(arquivo, new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void montarJsonLicitacaoAnexos(Licitacao licitacaoDaVez, JSONObject jsonLicitacao) {
        if (licitacaoDaVez.getDetentorDocumentoLicitacao() == null) {
            return;
        }
        for (DocumentoLicitacao documentoLicitacao : licitacaoDaVez.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList()) {
            if (documentoLicitacao.getExibirPortalTransparencia()) {
                if (documentoLicitacao.getArquivo() != null && documentoLicitacao.getTipoDocumentoAnexo() != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", documentoLicitacao.getArquivo().getId());
                    jsonObject.put("licitacao", jsonLicitacao);
                    jsonObject.put("tipoArquivo", documentoLicitacao.getTipoDocumentoAnexo().getDescricao());
                    jsonObject.put("arquivo", montarJsonArquivo(documentoLicitacao.getArquivo()));
                    addMensagemRetorno(documentoLicitacao.getArquivo(),
                        UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() +
                                PortalTransparenciaNovo.WS_LICITACAO_ARQUIVO, jsonObject,
                            UtilPortalTransparencia.METHOD_UPDATE));
                }
            }
        }
    }

    private void montarJsonLicitacaoCotacao(Licitacao licitacao, JSONObject jsonLicitacao) {
        if (licitacao != null) {
            addMensagem("<b> Montando envio de 1 Cotação. </b>");
            JSONObject json = new JSONObject();
            Cotacao cotacao = cotacaoFacade.recuperar(licitacao.getProcessoDeCompra().getSolicitacaoMaterial().getCotacao().getId());
            json.put("licitacao", jsonLicitacao);
            json.put("formularioCotacao", montarConteudoJsonFormularioCotacao(cotacao));
            json.put("numero", cotacao.getNumero());
            json.put("dataCotacao", cotacao.getDataCotacao().getTime());
            json.put("descricao", cotacao.getDescricao());
            json.put("situacao", cotacao.getSituacao() != null ? cotacao.getSituacao().getDescricao() : null);
            json.put("exercicio", getConteudoJSONExercicio(cotacao.getExercicio()));
            addMensagemRetorno(cotacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_COTACAO, json, UtilPortalTransparencia.METHOD_POST));
            HashMap<Pessoa, JSONObject> mapPessoa = Maps.newHashMap();
            for (Pessoa pessoa : cotacao.getFornecedores()) {
                JSONObject conteudoPessoa = montarConteudoJSONPessoa(pessoa);
                addMensagemRetorno(pessoa, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, conteudoPessoa, UtilPortalTransparencia.METHOD_POST));

                JSONObject jsonFornecedor = new JSONObject();
                jsonFornecedor.put("cotacao", json);
                jsonFornecedor.put("pessoa", conteudoPessoa);
                addMensagemRetorno(pessoa, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_COTACAO_FORNECEDOR, jsonFornecedor, UtilPortalTransparencia.METHOD_POST));
                mapPessoa.put(pessoa, jsonFornecedor);
            }
            for (LoteCotacao loteCotacao : cotacao.getLotes()) {
                for (ItemCotacao itemCotacao : loteCotacao.getItens()) {
                    for (ValorCotacao valorCotacao : itemCotacao.getValoresCotacao()) {
                        JSONObject jsonLote = new JSONObject();
                        jsonLote.put("cotacao", json);
                        jsonLote.put("fornecedor", mapPessoa.get(valorCotacao.getFornecedor()));
                        jsonLote.put("numero", loteCotacao.getNumero());
                        jsonLote.put("descricao", loteCotacao.getDescricao());
                        jsonLote.put("tipoObjetoCompra", itemCotacao.getObjetoCompra().getTipoObjetoCompra().name());
                        jsonLote.put("valor", loteCotacao.getValor());
                        addMensagemRetorno(loteCotacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_COTACAO_LOTE, jsonLote, UtilPortalTransparencia.METHOD_POST));
                        JSONObject jsonItem = new JSONObject();
                        jsonItem.put("lote", jsonLote);
                        jsonItem.put("numero", itemCotacao.getNumero());
                        jsonItem.put("objetoDeCompra", itemCotacao.getObjetoCompra().getCodigo() + " - " + itemCotacao.getObjetoCompra().getDescricao());
                        jsonItem.put("quantidade", itemCotacao.getQuantidade());
                        jsonItem.put("valorUnitario", itemCotacao.getValorUnitario());
                        jsonItem.put("valorTotal", itemCotacao.getValorTotal());
                        addMensagemRetorno(itemCotacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_COTACAO_ITEM, jsonItem, UtilPortalTransparencia.METHOD_POST));
                    }
                }
            }
        }
    }

    private JSONObject montarConteudoJsonFormularioCotacao(Cotacao cotacao) {
        if (cotacao.getFormularioCotacao() != null) {
            FormularioCotacao formulario = cotacao.getFormularioCotacao();
            JSONObject jsonFormulario = new JSONObject();
            jsonFormulario.put("intencaoRegistroPreco", montarConteudoJsonIRP(formulario.getIntencaoRegistroPreco()));
            jsonFormulario.put("numero", formulario.getNumero() != null ? formulario.getNumero().toString() : null);
            jsonFormulario.put("dataFormulario", formulario.getDataFormulario() != null ? formulario.getDataFormulario().getTime() : null);
            jsonFormulario.put("situacao", "Concluído");
            jsonFormulario.put("exercicio", getConteudoJSONExercicio(formulario.getExercicio()));
            if (formulario.getUsuarioCriacao() != null && formulario.getUsuarioCriacao().getPessoaFisica() != null) {
                jsonFormulario.put("pessoa", montarConteudoJSONPessoa(formulario.getUsuarioCriacao().getPessoaFisica()));
            }
            addMensagemRetorno(formulario, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_FORMULARIO_COTACAO, jsonFormulario, UtilPortalTransparencia.METHOD_POST));
            return jsonFormulario;
        }
        return null;
    }

    private JSONObject montarConteudoJsonIRP(IntencaoRegistroPreco irp) {
        if (irp != null) {
            JSONObject jsonIrp = new JSONObject();
            jsonIrp.put("numero", irp.getNumero() != null ? irp.getNumero().toString() : null);
            jsonIrp.put("dataLancamento", irp.getDataLancamento() != null ? irp.getDataLancamento().getTime() : null);
            jsonIrp.put("situacao", irp.getSituacaoIRP() != null ? irp.getSituacaoIRP().name() : null);
            if (irp.getResponsavel() != null && irp.getResponsavel().getPessoaFisica() != null) {
                jsonIrp.put("pessoaFisica", montarConteudoJSONPessoa(irp.getResponsavel().getPessoaFisica()));
            }
            addMensagemRetorno(irp, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_IRP, jsonIrp, UtilPortalTransparencia.METHOD_POST));
            return jsonIrp;
        }
        return null;
    }

    private void montarJsonLicitacaoPropostas(Licitacao licitacao, JSONObject jsonLicitacao) {
        if (licitacao != null) {
            List<PropostaFornecedor> propostas = propostaFornecedorFacade.buscarPorLicitacao(licitacao);
            if (propostas != null && !propostas.isEmpty()) {
                addMensagem("<b> Montando envio de " + propostas.size() + " Propostas dos Fornecedores. </b>");
                for (PropostaFornecedor proposta : propostas) {
                    JSONObject json = new JSONObject();
                    json.put("dataProposta", proposta.getDataProposta().getTime());
                    json.put("licitacao", jsonLicitacao);
                    JSONObject fornecedor = montarConteudoJSONPessoa(proposta.getFornecedor());
                    addMensagemRetorno(proposta.getFornecedor(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, fornecedor, UtilPortalTransparencia.METHOD_POST));
                    json.put("fornecedor", fornecedor);
                    json.put("instrumentoRepresentacao", proposta.getInstrumentoRepresentacao());
                    json.put("prazoValidade", proposta.getValidadeDaProposta() + " - " + proposta.getTipoPrazoValidade().getDescricao());
                    json.put("prazoExecucao", proposta.getPrazoDeExecucao() + " - " + proposta.getTipoPrazoExecucao().getDescricao());
                    addMensagemRetorno(proposta, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_PROPOSTA_FORNECEDOR, json, UtilPortalTransparencia.METHOD_POST));

                    for (ItemPropostaFornecedor itemPropostaFornecedor : proposta.getListaDeItensPropostaFornecedor()) {
                        JSONObject jsonLote = new JSONObject();
                        jsonLote.put("propostaFornecedor", json);
                        jsonLote.put("numero", itemPropostaFornecedor.getLotePropostaFornecedor().getLoteProcessoDeCompra().getNumero());
                        jsonLote.put("descricao", itemPropostaFornecedor.getLotePropostaFornecedor().getLoteProcessoDeCompra().getDescricao());
                        jsonLote.put("valor", !licitacao.getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto() ? itemPropostaFornecedor.getLotePropostaFornecedor().getValor() : null);
                        jsonLote.put("desconto", licitacao.getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto() &&
                            licitacao.getTipoApuracao().isPorLote() ? itemPropostaFornecedor.getLotePropostaFornecedor().getPercentualDesconto() : null);
                        addMensagemRetorno(itemPropostaFornecedor.getLotePropostaFornecedor(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_PROPOSTA_LOTE, jsonLote, UtilPortalTransparencia.METHOD_POST));

                        JSONObject jsonItem = new JSONObject();

                        jsonItem.put("lotePropostaFornecedor", jsonLote);
                        jsonItem.put("numero", itemPropostaFornecedor.getItemProcessoDeCompra().getNumero());
                        jsonItem.put("descricao", itemPropostaFornecedor.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao());
                        jsonItem.put("valor", !licitacao.getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto() ? itemPropostaFornecedor.getPreco() : null);
                        jsonItem.put("valorTotal", !licitacao.getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto() ? itemPropostaFornecedor.getPrecoTotal() : null);
                        jsonItem.put("quantidade", !licitacao.getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto() ? itemPropostaFornecedor.getItemProcessoDeCompra().getQuantidade() : null);
                        jsonItem.put("percentualDesconto", licitacao.getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto() &&
                            licitacao.getTipoApuracao().isPorLote() ? itemPropostaFornecedor.getPercentualDesconto() : null);
                        addMensagemRetorno(itemPropostaFornecedor.getLotePropostaFornecedor(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_PROPOSTA_ITEM, jsonItem, UtilPortalTransparencia.METHOD_POST));
                    }
                }
            }
        }
    }

    private void montarJsonLicitacaoMapaComparativo(Licitacao licitacao, JSONObject jsonLicitacao) {
        if (licitacao != null) {
            if (singleton.getSelecionado().getEnviarItensLotesMapaComparativo()) {
                if (TipoApuracaoLicitacao.ITEM.equals(licitacao.getTipoApuracao())) {
                    List<ProgressaoLanceCertame> certames = licitacaoFacade.buscarLancesCertame(licitacao, true);
                    if (certames != null && !certames.isEmpty()) {
                        addMensagem("<b> Montando envio de " + certames.size() + " Mapas Comparativos por Item. </b>");
                        for (ProgressaoLanceCertame certame : certames) {
                            JSONObject json = new JSONObject();
                            json.put("licitacao", jsonLicitacao);
                            json.put("rodada", certame.getRodada());
                            json.put("lote", certame.getNumeroLote());
                            json.put("item", certame.getItem());
                            json.put("marca", certame.getMarca());
                            JSONObject participante = montarConteudoJSONPessoa(certame.getParticipante());
                            addMensagemRetorno(certame.getParticipante(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, participante, UtilPortalTransparencia.METHOD_POST));
                            json.put("participante", participante);
                            json.put("responsavel", certame.getResponsavel());
                            json.put("valorPrevisto", certame.getValorPrevisto());
                            json.put("lanceAnterior", certame.getLanceAnterior());
                            json.put("lanceAtual", certame.getLanceAtual());
                            json.put("situacao", certame.getSituacao());
                            addMensagemRetorno(certame, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_MAPA_COMPARATIVO_ITEM, json, UtilPortalTransparencia.METHOD_POST));
                        }
                    }
                } else {
                    List<ProgressaoLanceCertame> certames = licitacaoFacade.buscarLancesCertame(licitacao, false);
                    if (certames != null && !certames.isEmpty()) {
                        addMensagem("<b> Montando envio de " + certames.size() + " Mapas Comparativos por Lote. </b>");
                        for (ProgressaoLanceCertame certame : certames) {
                            JSONObject json = new JSONObject();
                            json.put("licitacao", jsonLicitacao);
                            json.put("numero", certame.getNumeroLote());
                            json.put("descricao", certame.getDescricao());
                            JSONObject participante = montarConteudoJSONPessoa(certame.getParticipante());
                            addMensagemRetorno(certame.getParticipante(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, participante, UtilPortalTransparencia.METHOD_POST));
                            json.put("participante", participante);
                            json.put("situacao", certame.getSituacao());
                            json.put("valor", certame.getValor());
                            addMensagemRetorno(certame, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LICITACAO_MAPA_COMPARATIVO_LOTE, json, UtilPortalTransparencia.METHOD_POST));
                        }
                    }
                }
            }
        }
    }

    private JSONObject montarConteudoLicitacao(Licitacao selecionado) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numeroModalidade", selecionado.getNumero());
        jsonObject.put("numero", selecionado.getNumeroLicitacao());
        jsonObject.put("exercicio", getConteudoJSONExercicio(selecionado.getExercicio()));
        jsonObject.put("modalidade", selecionado.getModalidadeLicitacao().name());
        jsonObject.put("valor", selecionado.getValorMaximo());
        if (selecionado.getAbertaEm() != null) {
            jsonObject.put("dataAbertura", selecionado.getAbertaEm().getTime());
        }
        jsonObject.put("resumoDoObjeto", selecionado.getResumoDoObjeto());
        UnidadeOrganizacional unidadeOrganizacional = selecionado.getProcessoDeCompra().getUnidadeOrganizacional();
        try {
            HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(unidadeOrganizacional, selecionado.getAbertaEm());
            Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(orgao.getSubordinada(), selecionado.getAbertaEm());
            if (entidade != null) {
                jsonObject.put("secretaria", montarConteudoLotacao(orgao, entidade.getTipoAdministracao()));
            }
        } catch (ExcecaoNegocioGenerica e) {
            logger.error("Erro ao encontrar órgão. " + e.getMessage());
            HierarquiaOrganizacional unidade = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidadeOrganizacional, selecionado.getAbertaEm());
            Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(unidadeOrganizacional, selecionado.getAbertaEm());
            jsonObject.put("secretaria", montarConteudoLotacao(unidade, entidade.getTipoAdministracao()));
        } catch (Exception e) {
            logger.error("Erro ao encontrar órgão. ", e);
        }
        jsonObject.put("apuracao", selecionado.getTipoApuracao().name());
        jsonObject.put("avaliacao", selecionado.getTipoAvaliacao().name());
        jsonObject.put("processoDeCompra", montarConteudoJsonProcessoDeCompra(selecionado.getProcessoDeCompra()));
        return jsonObject;
    }

    private void enviarUltimaAtualizacao() {
        addMensagem("<b> Atualizando dados da prefeitura... </b>");
        for (PrefeituraPortal prefeituraPortal : singleton.recuperarTodasPrefeitura()) {
            JSONObject jsonObject = new JSONObject();
            addMensagem("<b> Atualizando " + prefeituraPortal.getNome() + " ... </b>");
            prefeituraPortal.setUltimaAtualizacao(new Date());
            jsonObject.put("identificador", prefeituraPortal.getIdentificador());
            jsonObject.put("ultimaAtualizacao", prefeituraPortal.getUltimaAtualizacao().getTime());
            singleton.salvarPrefeitura(prefeituraPortal);
            getObjetos().add(new ObjetoPortalTransparencia(prefeituraPortal.getNome(), jsonObject, PortalTransparenciaNovo.WS_PREFEITURA, UtilPortalTransparencia.METHOD_UPDATE));
        }
    }

    private void enviarDadosDaPrefeitura() {
        for (PrefeituraPortal prefeituraPortal : singleton.getSelecionado().getPrefeituras()) {
            JSONObject jsonObject = montarJsonPrefeitura(prefeituraPortal);
            getObjetos().add(new ObjetoPortalTransparencia(prefeituraPortal.getNome(), jsonObject, PortalTransparenciaNovo.WS_PREFEITURA, UtilPortalTransparencia.METHOD_POST));
            enviarPrefeituraUnidade(prefeituraPortal, jsonObject);
            enviarPrefeituraModulo(prefeituraPortal, jsonObject);
            singleton.salvarPrefeitura(prefeituraPortal);
        }
    }

    private JSONObject montarJsonPrefeitura(PrefeituraPortal prefeituraPortal) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nome", prefeituraPortal.getNome());
        jsonObject.put("endereco", prefeituraPortal.getEndereco());
        jsonObject.put("identificador", prefeituraPortal.getIdentificador());
        jsonObject.put("nomeReduzido", prefeituraPortal.getNomereduzido());
        jsonObject.put("principal", prefeituraPortal.getPrincipal());
        jsonObject.put("site", prefeituraPortal.getSite());
        jsonObject.put("urlAcessoInformacao", prefeituraPortal.getUrlAcessoInformacao());
        jsonObject.put("habilitarBannerCalamidade", prefeituraPortal.getHabilitarBannerCalamidade());
        jsonObject.put("urlESic", prefeituraPortal.getUrlESic());
        jsonObject.put("urlOuvidoria", prefeituraPortal.getUrlOuvidoria());
        jsonObject.put("atendimentoPresencial", prefeituraPortal.getAtendimentoPresencial());
        jsonObject.put("descricaoEouv", prefeituraPortal.getDescricaoEouv());
        jsonObject.put("esferaDoPoder", prefeituraPortal.getEsferaDoPoder().name());
        jsonObject.put("contato", prefeituraPortal.getContato());
        jsonObject.put("horarioAtendimento", prefeituraPortal.getHorarioAtendimento());
        jsonObject.put("emailTecnologia", prefeituraPortal.getEmailTecnologia());
        jsonObject.put("contatoTecnologia", prefeituraPortal.getContatoTecnologia());
        jsonObject.put("ultimaAtualizacao", new Date().getTime());
        String replace = prefeituraPortal.getPaginainicial().replace("\"", "'").replace("\r", "").replace("\n", "");
        jsonObject.put("paginaInicial", replace);
        jsonObject.put("logoTopo", montarJsonArquivo(prefeituraPortal.getLogoTopo()));
        jsonObject.put("organograma", montarJsonArquivo(prefeituraPortal.getOrganograma()));
        return jsonObject;
    }

    private void enviarPrefeituraUnidade(PrefeituraPortal prefeituraPortal, JSONObject jsonPrefeitura) {
        for (UnidadePrefeituraPortal unidadePrefeituraPortal : prefeituraPortal.getUnidades()) {
            JSONObject jsonObject = new JSONObject();
            HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(unidadePrefeituraPortal.getUnidade(), singleton.getSelecionado().getDataOperacao());
            if (hierarquiaDaUnidade != null) {
                jsonObject.put("unidade", montarConteudoUnidade(hierarquiaDaUnidade));
                jsonObject.put("prefeitura", jsonPrefeitura);
                getObjetos().add(new ObjetoPortalTransparencia(prefeituraPortal.getNome(), jsonObject, PortalTransparenciaNovo.WS_PREFEITURA_UNIDADE, UtilPortalTransparencia.METHOD_POST));
            }
        }
    }

    private void enviarPrefeituraModulo(PrefeituraPortal prefeituraPortal, JSONObject jsonPrefeitura) {
        for (ModuloPrefeituraPortal modulo : prefeituraPortal.getModulos()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("moduloSistema", modulo.getModulo().name());
            jsonObject.put("prefeitura", jsonPrefeitura);
            getObjetos().add(new ObjetoPortalTransparencia(prefeituraPortal.getNome(), jsonObject, PortalTransparenciaNovo.WS_PREFEITURA_MODULO, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void recuperarAlteracaoOrcamentaria() {
        if (singleton.getSelecionado().getEnviarAlteracaoOrcamentaria()) {
            recuperarAlteracaoOrcNormal();
            recuperarAlteracaoOrcEstorno();
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void recuperarAlteracaoOrcEstorno() {

        List<EstornoAlteracaoOrc> estornos = estornoAlteracaoOrcFacade.buscarEstornoAlteracaoOrcamentaria(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades());
        addMensagem("<b> Montando envio de " + estornos.size() + " Estorno de Alterações Orçamentárias. </b>");
        LOA loa = recuperarLoa();
        for (EstornoAlteracaoOrc estorno : estornos) {
            sincronizarAlteracaoOrcamentariaEstorno(loa, estorno);
        }
    }

    private void recuperarAlteracaoOrcNormal() {
        List<AlteracaoORC> alteracaoORCs = alteracaoORCFacade.buscarAlteracaoOrcamentaria(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades());
        addMensagem("<b> Montando envio de " + alteracaoORCs.size() + " Alterações Orçamentárias. </b>");
        LOA loa = recuperarLoa();
        for (AlteracaoORC alteracaoORC : alteracaoORCs) {
            sincronizarAlteracaoOrcamentaria(loa, alteracaoORC);
        }
    }

    private void sincronizarAlteracaoOrcamentariaEstorno(LOA loa, EstornoAlteracaoOrc estorno) {
        estorno = estornoAlteracaoOrcFacade.recuperar(estorno.getId());
        JSONObject jsonObject = montarConteudoJSONEstornoAlteracaoOrcamentaria(estorno);
        Util.imprime(jsonObject.toString());
        getObjetos().add(new ObjetoPortalTransparencia(estorno, jsonObject, PortalTransparenciaNovo.WS_ALTERACAO_ORC_ESTORNO, UtilPortalTransparencia.METHOD_POST));

        for (EstornoSuplementacaoOrc estornoSuplementacaoOrc : estorno.getListaEstornoSuplementacaoOrc()) {
            getObjetos().add(new ObjetoPortalTransparencia(estornoSuplementacaoOrc, montarConteudoJSONEstornoSuplementacao(estornoSuplementacaoOrc, loa), PortalTransparenciaNovo.WS_SUPLEMENTACAO_ORC_ESTORNO, UtilPortalTransparencia.METHOD_POST));
        }
        for (EstornoAnulacaoOrc estornoAnulacaoOrc : estorno.getListaEstornoAnulacaoOrc()) {
            getObjetos().add(new ObjetoPortalTransparencia(estornoAnulacaoOrc, montarConteudoJSONEstornoAnuluacao(estornoAnulacaoOrc, loa), PortalTransparenciaNovo.WS_ANULACAO_ORC_ESTORNO, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void sincronizarAlteracaoOrcamentaria(LOA loa, AlteracaoORC alteracaoORC) {
        alteracaoORC = alteracaoORCFacade.recuperar(alteracaoORC.getId());

        getObjetos().add(new ObjetoPortalTransparencia(alteracaoORC.getAtoLegal(), montarJSONAtoLegal(alteracaoORC.getAtoLegal()), PortalTransparenciaNovo.WS_ATO_LEGAL, UtilPortalTransparencia.METHOD_POST));
        getObjetos().add(new ObjetoPortalTransparencia(alteracaoORC, montarConteudoJSONAlteracaoOrcamentaria(alteracaoORC), PortalTransparenciaNovo.WS_ALTERACAO_ORC, UtilPortalTransparencia.METHOD_POST));

        for (SuplementacaoORC suplementacaoORC : alteracaoORC.getSuplementacoesORCs()) {
            sincronizarProvisaoPPADespesa(suplementacaoORC.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa(), loa);
            sincronizarProvisaoPPADespesaFonte(suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte(), loa);

            getObjetos().add(new ObjetoPortalTransparencia(suplementacaoORC, montarConteudoJSONSuplementacao(suplementacaoORC, loa), PortalTransparenciaNovo.WS_SUPLEMENTACAO_ORC, UtilPortalTransparencia.METHOD_POST));
        }
        for (AnulacaoORC anulacaoORC : alteracaoORC.getAnulacoesORCs()) {
            sincronizarProvisaoPPADespesa(anulacaoORC.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa(), loa);
            sincronizarProvisaoPPADespesaFonte(anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte(), loa);
            getObjetos().add(new ObjetoPortalTransparencia(anulacaoORC, montarConteudoJSONAnulacao(anulacaoORC, loa), PortalTransparenciaNovo.WS_ANULACAO_ORC, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private JSONObject montarConteudoJSONSuplementacao(SuplementacaoORC suplementacaoORC, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("alteracaoOrcamentaria", montarConteudoJSONAlteracaoOrcamentaria(suplementacaoORC.getAlteracaoORC()));
        jsonObject.put("previsaoDespesaFonte", montaConteudoJSONPrevisaoDespesaFonte(suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte(), loa));
        jsonObject.put("tipoDespesaORC", suplementacaoORC.getTipoDespesaORC().name());
        jsonObject.put("origem", suplementacaoORC.getOrigemSuplemtacao().name());
        jsonObject.put("historico", suplementacaoORC.getAlteracaoORC().getDescricao());
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("valor", suplementacaoORC.getValor());
        return jsonObject;
    }

    private JSONObject montarConteudoJSONEstornoSuplementacao(EstornoSuplementacaoOrc estornoSuplementacaoOrc, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("alteracaoOrcEstorno", montarConteudoJSONEstornoAlteracaoOrcamentaria(estornoSuplementacaoOrc.getEstornoAlteracaoOrc()));
        jsonObject.put("suplementacaoORC", montarConteudoJSONSuplementacao(estornoSuplementacaoOrc.getSuplementacaoORC(), loa));
        jsonObject.put("historico", estornoSuplementacaoOrc.getEstornoAlteracaoOrc().getAlteracaoORC().getDescricao());
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("valor", estornoSuplementacaoOrc.getValor());
        return jsonObject;
    }

    private JSONObject montarConteudoJSONEstornoAnuluacao(EstornoAnulacaoOrc estornoAnulacaoOrc, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("alteracaoOrcEstorno", montarConteudoJSONEstornoAlteracaoOrcamentaria(estornoAnulacaoOrc.getEstornoAlteracaoOrc()));
        jsonObject.put("anulacaoORC", montarConteudoJSONAnulacao(estornoAnulacaoOrc.getAnulacaoORC(), loa));
        jsonObject.put("historico", estornoAnulacaoOrc.getEstornoAlteracaoOrc().getAlteracaoORC().getDescricao());
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("valor", estornoAnulacaoOrc.getValor());
        return jsonObject;
    }

    private JSONObject montarConteudoJSONAnulacao(AnulacaoORC anulacaoORC, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("alteracaoOrcamentaria", montarConteudoJSONAlteracaoOrcamentaria(anulacaoORC.getAlteracaoORC()));
        jsonObject.put("previsaoDespesaFonte", montaConteudoJSONPrevisaoDespesaFonte(anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte(), loa));
        jsonObject.put("tipoDespesaORC", anulacaoORC.getTipoDespesaORC().name());
        jsonObject.put("historico", anulacaoORC.getAlteracaoORC().getDescricao());
        jsonObject.put("eventoContabil", "test3");
        jsonObject.put("valor", anulacaoORC.getValor());
        return jsonObject;
    }

    private JSONObject montarConteudoJSONAlteracaoOrcamentaria(AlteracaoORC alteracaoORC) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", alteracaoORC.getDataEfetivacao().getTime());
        jsonObject.put("codigo", alteracaoORC.getCodigo());
        jsonObject.put("descricao", alteracaoORC.getDescricao());
        jsonObject.put("atoLegal", montarJSONAtoLegal(alteracaoORC.getAtoLegal()));
        jsonObject.put("exercicio", getConteudoJSONExercicio(alteracaoORC.getExercicio()));
        return jsonObject;
    }

    private JSONObject montarConteudoJSONEstornoAlteracaoOrcamentaria(EstornoAlteracaoOrc estorno) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", estorno.getDataEstorno().getTime());
        jsonObject.put("codigo", estorno.getCodigo());
        jsonObject.put("exercicio", getConteudoJSONExercicio(estorno.getExercicio()));
        jsonObject.put("alteracaoOrc", montarConteudoJSONAlteracaoOrcamentaria(estorno.getAlteracaoORC()));
        return jsonObject;
    }

    private void recuperarReceitaRealizada() {
        if (singleton.getSelecionado().getEnviarReceitaRealizada()) {
            recuperarEnviarReceitaRealizadaNormal();
            recuperarEnviarReceitaRealizadaEstorno();
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void recuperarEnviarReceitaRealizadaEstorno() {
        Date dataInicial = singleton.getSelecionado().getDataInicialFiltro();
        Date dataFim = singleton.getSelecionado().getDataFinalFiltro();
        List<ReceitaORCEstorno> estornos = receitaORCEstornoFacade.buscarTodasReceitaORCEstornoPorExercicioUnidades(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades(), dataInicial, dataFim);

        addMensagem("<b> Montando envio de " + estornos.size() + " Estorno de Lançamentos de receitas. </b>");
        for (ReceitaORCEstorno estorno : estornos) {

            sincronizarReceitaRealizadaEstorno(estorno);
        }
    }

    private void sincronizarReceitaRealizadaEstorno(ReceitaORCEstorno estorno) {
        estorno = receitaORCEstornoFacade.recuperar(estorno.getId());
        JSONObject json = montarJSONLancamentoReceitaRealizadaEstorno(estorno);
        Util.imprime(json.toString());
        getObjetos().add(new ObjetoPortalTransparencia(estorno, json, PortalTransparenciaNovo.WS_LANCAMENTO_RECEITA_ESTORNO, UtilPortalTransparencia.METHOD_POST));

        for (ReceitaORCFonteEstorno receitaORCFonteEstorno : estorno.getReceitaORCFonteEstorno()) {
            JSONObject jsonFonte = montarJSONLancamentoReceitaRealizadaFonteEstorno(receitaORCFonteEstorno);
            Util.imprime(jsonFonte.toString());
            getObjetos().add(new ObjetoPortalTransparencia(receitaORCFonteEstorno, jsonFonte, PortalTransparenciaNovo.WS_LANCAMENTO_RECEITA_FONTE_ESTORNO, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void recuperarEnviarReceitaRealizadaNormal() {
        Date dataInicial = singleton.getSelecionado().getDataInicialFiltro();
        Date dataFim = singleton.getSelecionado().getDataFinalFiltro();

        List<LancamentoReceitaOrc> lancamentoReceitaOrcs = lancamentoReceitaOrcFacade.buscarTodasReceitaRealizadaPorExercicioUnidades(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades(), dataInicial, dataFim);

        addMensagem("<b> Montando envio de " + lancamentoReceitaOrcs.size() + " Lançamentos de receitas. </b>");
        for (LancamentoReceitaOrc lancamentoReceitaOrc : lancamentoReceitaOrcs) {

            sincronizarReceitaRealizada(lancamentoReceitaOrc);
        }
    }

    private void sincronizarReceitaRealizada(LancamentoReceitaOrc lancamentoReceitaOrc) {
        lancamentoReceitaOrc = lancamentoReceitaOrcFacade.recuperar(lancamentoReceitaOrc.getId());
        JSONObject conteudoPessoa = montarConteudoJSONPessoa(lancamentoReceitaOrc.getPessoa());
        getObjetos().add(new ObjetoPortalTransparencia(lancamentoReceitaOrc.getPessoa(), conteudoPessoa, PortalTransparenciaNovo.WS_PESSOA, UtilPortalTransparencia.METHOD_POST));

        JSONObject jsonReceitaLoa = montarJSONReceitaLOA(lancamentoReceitaOrc.getReceitaLOA());
        getObjetos().add(new ObjetoPortalTransparencia(lancamentoReceitaOrc.getReceitaLOA(), jsonReceitaLoa, PortalTransparenciaNovo.WS_RECEITA_LOA, UtilPortalTransparencia.METHOD_POST));

        JSONObject json = montarJSONLancamentoReceitaRealizada(lancamentoReceitaOrc);
        getObjetos().add(new ObjetoPortalTransparencia(lancamentoReceitaOrc, json, PortalTransparenciaNovo.WS_LANCAMENTO_RECEITA, UtilPortalTransparencia.METHOD_POST));

        for (LancReceitaFonte lancReceitaFonte : lancamentoReceitaOrc.getLancReceitaFonte()) {
            JSONObject jsonFonte = montarJSONLancamentoReceitaRealizadaFonte(lancReceitaFonte);
            getObjetos().add(new ObjetoPortalTransparencia(lancReceitaFonte, jsonFonte, PortalTransparenciaNovo.WS_LANCAMENTO_RECEITA_FONTE, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private JSONObject montarJSONLancamentoReceitaRealizadaFonte(LancReceitaFonte lancReceitaFonte) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lancamentoReceita", montarJSONLancamentoReceitaRealizada(lancReceitaFonte.getLancReceitaOrc()));
        jsonObject.put("receitaLoaFonte", montarJSONReceitaLOAFonte(lancReceitaFonte.getReceitaLoaFonte()));
        jsonObject.put("historico", removerCaracteresInvalidos(lancReceitaFonte.getLancReceitaOrc().getComplemento().replace("\"", "")));
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("item", lancReceitaFonte.getItem());
        jsonObject.put("valor", lancReceitaFonte.getValor());
        return jsonObject;

    }

    private JSONObject montarJSONLancamentoReceitaRealizadaFonteEstorno(ReceitaORCFonteEstorno receitaORCFonteEstorno) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lancamentoReceitaEstorno", montarJSONLancamentoReceitaRealizadaEstorno(receitaORCFonteEstorno.getReceitaORCEstorno()));
        jsonObject.put("receitaLoaFonte", montarJSONReceitaLOAFonte(receitaORCFonteEstorno.getReceitaLoaFonte()));
        jsonObject.put("historico", removerCaracteresInvalidos(receitaORCFonteEstorno.getReceitaORCEstorno().getComplementoHistorico().replace("\"", "")));
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("item", receitaORCFonteEstorno.getItem());
        jsonObject.put("valor", receitaORCFonteEstorno.getValor());
        return jsonObject;

    }

    private JSONObject montarJSONLancamentoReceitaRealizadaEstorno(ReceitaORCEstorno estorno) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", estorno.getNumero());
        jsonObject.put("data", estorno.getDataEstorno().getTime());
        jsonObject.put("valor", estorno.getValor());
        jsonObject.put("receitaLoa", montarJSONReceitaLOA(estorno.getReceitaLOA()));
        HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(estorno.getUnidadeOrganizacionalOrc(), estorno.getDataEstorno());
        jsonObject.put("unidade", montarConteudoUnidade(hierarquiaDaUnidade));

        jsonObject.put("exercicio", getConteudoJSONExercicio(estorno.getExercicio()));
        return jsonObject;
    }

    private JSONObject montarJSONLancamentoReceitaRealizada(LancamentoReceitaOrc lancamentoReceitaOrc) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", lancamentoReceitaOrc.getNumero());
        jsonObject.put("receitaLoa", montarJSONReceitaLOA(lancamentoReceitaOrc.getReceitaLOA()));
        jsonObject.put("dataLancamento", lancamentoReceitaOrc.getDataLancamento().getTime());
        jsonObject.put("dataConciliacao", lancamentoReceitaOrc.getDataConciliacao() == null ? "" : lancamentoReceitaOrc.getDataConciliacao().getTime());
        jsonObject.put("pessoa", montarConteudoJSONPessoa(lancamentoReceitaOrc.getPessoa()));
        jsonObject.put("valor", lancamentoReceitaOrc.getValor());
        jsonObject.put("contaFinanceira", lancamentoReceitaOrc.getSubConta().toStringAutoCompleteContaFinanceira());
        jsonObject.put("tipoLancamento", "NORMAL");

        HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(lancamentoReceitaOrc.getUnidadeOrganizacional(), lancamentoReceitaOrc.getDataLancamento());
        jsonObject.put("unidade", montarConteudoUnidade(hierarquiaDaUnidade));

        jsonObject.put("exercicio", getConteudoJSONExercicio(lancamentoReceitaOrc.getExercicio()));
        jsonObject.put("lote", lancamentoReceitaOrc.getLote());
        return jsonObject;
    }

    private JSONObject montarJSONAtoLegal(AtoLegal atoLegal) {
        if (!singleton.contemObjeto(atoLegal) && atoLegal != null) {
            atoLegal = atoLegalFacade.recuperar(atoLegal.getId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("numero", atoLegal.getNumero());
            jsonObject.put("dataSancao", atoLegal.getDataPromulgacao() != null ? atoLegal.getDataPromulgacao().getTime() : null);
            jsonObject.put("propositoAtoLegal", atoLegal.getPropositoAtoLegal().name());
            jsonObject.put("tipoAtoLegal", atoLegal.getTipoAtoLegal().name());
            jsonObject.put("esferaGoverno", atoLegal.getEsferaGoverno());
            jsonObject.put("veiculoPublicacao", atoLegal.getVeiculoDePublicacao() == null ? " " : atoLegal.getVeiculoDePublicacao().getNome());
            jsonObject.put("exercicio", getConteudoJSONExercicio(atoLegal.getExercicio()));
            jsonObject.put("nome", atoLegal.getNome());

            jsonObject.put("fundamentacaoLegal", atoLegal.getFundamentacaoLegal());
            jsonObject.put("numeroPublicacao", atoLegal.getDescricaoConformeDo());

            if (atoLegal.getArquivo() != null) {
                jsonObject.put("arquivo", montarJsonArquivo(atoLegal.getArquivo()));
            }
            if (!atoLegal.getRepublicacoes().isEmpty()) {
                AtoLegalRepublicacao ultimaRepublicacao = null;
                for (AtoLegalRepublicacao atoLegalRepublicacao : atoLegal.getRepublicacoes()) {
                    if (ultimaRepublicacao == null || ultimaRepublicacao.getDataRepublicacao().before(atoLegalRepublicacao.getDataRepublicacao())) {
                        ultimaRepublicacao = atoLegalRepublicacao;
                    }
                }
                jsonObject.put("dataPublicacao", ultimaRepublicacao.getDataRepublicacao().getTime());
            } else {
                jsonObject.put("dataPublicacao", atoLegal.getDataPublicacao() == null ? " " : atoLegal.getDataPublicacao().getTime());
            }
            singleton.adicionarJsonObjet(atoLegal, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(atoLegal);
        }
    }

    private byte[] montarJSONArquivo(Arquivo arquivo) {
        try {

            if (arquivo == null) {
                return null;
            }
            arquivo = arquivoFacade.recuperaDependencias(arquivo.getId());
            arquivo.montarImputStream();

            return IOUtils.toByteArray(arquivo.getInputStream());
        } catch (Exception e) {
            return null;
        }
    }

    private void recuperarReceitaOrcamentaria() {
        if (singleton.getSelecionado().getEnviarPlanejamentoReceita()) {
            List<ReceitaLOA> receitaLOAs = receitaLOAFacade.listaReceitaLOAPorExercicioUnidades(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades());
            addMensagem("<b> Montando envio de  " + receitaLOAs.size() + " Previsão de Receita. </b>");
            for (ReceitaLOA receitaLOA : receitaLOAs) {
                JSONObject montarJSONContaReceita = montarJSONContaReceita(receitaLOA.getContaDeReceita());
                getObjetos().add(new ObjetoPortalTransparencia(receitaLOA.getContaDeReceita(), montarJSONContaReceita, PortalTransparenciaNovo.WS_CONTA_RECEITA, UtilPortalTransparencia.METHOD_POST));

                JSONObject montarJSONReceitaLOA = montarJSONReceitaLOA(receitaLOA);
                getObjetos().add(new ObjetoPortalTransparencia(receitaLOA, montarJSONReceitaLOA, PortalTransparenciaNovo.WS_RECEITA_LOA, UtilPortalTransparencia.METHOD_POST));

                ReceitaLOA recuperar = receitaLOAFacade.recuperar(receitaLOA.getId());
                for (ReceitaLOAFonte receitaLOAFonte : recuperar.getReceitaLoaFontes()) {
                    JSONObject montarJSONFonteDeRecurso = montarJSONFonteDeRecurso(receitaLOAFonte.getFonteRecurso());
                    getObjetos().add(new ObjetoPortalTransparencia(receitaLOAFonte.getFonteRecurso(), montarJSONFonteDeRecurso, PortalTransparenciaNovo.WS_FONTE_DE_RECURSO, UtilPortalTransparencia.METHOD_POST));

                    JSONObject montarJSONReceitaLOAFonte = montarJSONReceitaLOAFonte(receitaLOAFonte);
                    getObjetos().add(new ObjetoPortalTransparencia(receitaLOAFonte, montarJSONReceitaLOAFonte, PortalTransparenciaNovo.WS_RECEITA_LOA_FONTE, UtilPortalTransparencia.METHOD_POST));
                }

            }
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    public Boolean isCalculando() {
        if (singleton.getSelecionado() == null) {
            return false;
        }
        if (singleton.getSelecionado().isCalculando() != null) {
            return singleton.getSelecionado().isCalculando();
        } else {
            return false;
        }
    }

    private void recuperarExecucaoOrcamentaria() {
        if (singleton.getSelecionado().getEnviarExecucaoOrcamentaria()) {
            addMensagem("<b> Atualizando Execução orçamentária. </b>");
            LOA loa = recuperarLoa();
            enviarEmpenho(loa, CategoriaOrcamentaria.NORMAL);
            enviarEmpenhoEstorno(loa, CategoriaOrcamentaria.NORMAL);
            enviarLiquidacao(loa, CategoriaOrcamentaria.NORMAL);
            enviarLiquidacaoEstorno(loa, CategoriaOrcamentaria.NORMAL);
            enviarPagamento(loa, CategoriaOrcamentaria.NORMAL);
            enviarPagamentoEstorno(loa, CategoriaOrcamentaria.NORMAL);
            addMensagem("<b> Finalizado a atualização da Execução Orçamentária. </b>");
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private LOA recuperarLoa() {

        Exercicio exercicio = singleton.getSelecionado().getExercicio();
        if (singleton.getLoas().containsKey(exercicio)) {
            return singleton.getLoas().get(exercicio);
        }

        LOA loa = projetoAtividadeFacade.getLoaFacade().listaUltimaLoaPorExercicio(exercicio);
        singleton.getLoas().put(exercicio, loa);
        return loa;
    }

    private void enviarEmpenhoEstorno(LOA loa, CategoriaOrcamentaria categoriaOrcamentaria) {
        List<EmpenhoEstorno> estornos = empenhoEstornoFacade.buscarEmpenhoEstornoPorUnidadesExercicio(singleton.getSelecionado().getUnidades(), singleton.getSelecionado().getExercicio(), categoriaOrcamentaria);
        addMensagem("<b> Montando envio de " + estornos.size() + " estornos de Empenhos. </b>");
        for (EmpenhoEstorno estorno : estornos) {
            sincronizarEmpenhoEstorno(loa, estorno);
        }
    }

    private void sincronizarEmpenhoEstorno(LOA loa, EmpenhoEstorno estorno) {
        JSONObject json = montarJsonEstornoEmpenho(estorno, loa);
        addMensagemRetorno(json, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_EMPENHO_ESTORNO, json, UtilPortalTransparencia.METHOD_POST));
    }

    private void enviarLiquidacaoEstorno(LOA loa, CategoriaOrcamentaria categoriaOrcamentaria) {
        List<LiquidacaoEstorno> estornos = liquidacaoEstornoFacade.buscarLiquidacaoEstornoPorUnidadesExercicio(singleton.getSelecionado().getUnidades(), singleton.getSelecionado().getExercicio(), categoriaOrcamentaria);
        addMensagem("<b> Montando envio de " + estornos.size() + " estornos de Liquidações. </b>");
        for (LiquidacaoEstorno estorno : estornos) {
            sincronizarLiquidacaoEstorno(loa, estorno);
        }
    }

    private void sincronizarLiquidacaoEstorno(LOA loa, LiquidacaoEstorno estorno) {
        JSONObject json = montarJsonEstornoLiquidacao(estorno, loa);
        addMensagemRetorno(json, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LIQUIDACAO_ESTORNO, json, UtilPortalTransparencia.METHOD_POST));
    }

    private void enviarPagamentoEstorno(LOA loa, CategoriaOrcamentaria categoriaOrcamentaria) {
        List<PagamentoEstorno> estornos = pagamentoEstornoFacade.buscarPagamentoEstornoPorUnidadesExercicio(singleton.getSelecionado().getUnidades(), singleton.getSelecionado().getExercicio(), categoriaOrcamentaria);
        addMensagem("<b> Montando envio de " + estornos.size() + " estornos de Pagamentos. </b>");
        for (PagamentoEstorno estorno : estornos) {
            sincronizarPagamentoEstorno(loa, estorno);
        }
    }

    private void sincronizarPagamentoEstorno(LOA loa, PagamentoEstorno estorno) {
        JSONObject json = montarJsonEstornoPagamento(estorno, loa);
        addMensagemRetorno(json, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PAGAMENTO_ESTORNO, json, UtilPortalTransparencia.METHOD_POST));
    }

    private JSONObject montarJsonEstornoEmpenho(EmpenhoEstorno estorno, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", estorno.getNumero());
        jsonObject.put("data", estorno.getDataEstorno().getTime());
        jsonObject.put("historico", removerCaracteresInvalidos(estorno.getComplementoHistorico()));
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("valor", estorno.getValor());
        jsonObject.put("empenho", montarJSONEmpenho(estorno.getEmpenho(), loa));
        return jsonObject;
    }

    private JSONObject montarJsonEstornoLiquidacao(LiquidacaoEstorno estorno, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", estorno.getNumero());
        jsonObject.put("data", estorno.getDataEstorno().getTime());
        jsonObject.put("historico", removerCaracteresInvalidos(estorno.getComplementoHistorico()));
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("valor", estorno.getValor());
        jsonObject.put("liquidacao", montarJSONLiquidacao(estorno.getLiquidacao(), loa));
        return jsonObject;
    }

    private JSONObject montarJsonEstornoPagamento(PagamentoEstorno estorno, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", estorno.getNumero());
        jsonObject.put("data", estorno.getDataEstorno().getTime());
        jsonObject.put("historico", removerCaracteresInvalidos(estorno.getComplementoHistorico()));
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("valor", estorno.getValor());
        jsonObject.put("pagamento", montarJSONPagamento(estorno.getPagamento(), loa));
        return jsonObject;
    }

    private void recuperarExecucaoOrcamentariaResto() {
        if (singleton.getSelecionado().getEnviarRestoAPagar()) {
            addMensagem("<b> Atualizando Restos a Pagar. </b>");
            LOA loa = recuperarLoaRestos();
            enviarEmpenho(loa, CategoriaOrcamentaria.RESTO);
            enviarEmpenhoEstorno(loa, CategoriaOrcamentaria.RESTO);
            enviarLiquidacao(loa, CategoriaOrcamentaria.RESTO);
            enviarLiquidacaoEstorno(loa, CategoriaOrcamentaria.RESTO);
            enviarPagamento(loa, CategoriaOrcamentaria.RESTO);
            enviarPagamentoEstorno(loa, CategoriaOrcamentaria.RESTO);
            addMensagem("<b> Finalizado a atualização dos Restos a Pagar. </b>");
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private LOA recuperarLoaRestos() {
        Integer ano = DataUtil.getAno(singleton.getSelecionado().getDataOperacao());
        ano = ano - 1;
        Exercicio exercicio = empenhoFacade.getExercicioFacade().getExercicioPorAno(ano);
        if (singleton.getLoas().containsKey(exercicio)) {
            return singleton.getLoas().get(exercicio);
        }

        LOA loa = projetoAtividadeFacade.getLoaFacade().listaUltimaLoaPorExercicio(exercicio);
        singleton.getLoas().put(exercicio, loa);
        return loa;
    }

    private void enviarPagamento(LOA loa, CategoriaOrcamentaria categoria) {
        List<Pagamento> pagamentos = pagamentoFacade.buscarPagamentosPorUnidadesExercicio(singleton.getSelecionado().getUnidades(), singleton.getSelecionado().getExercicio(), categoria);
        addMensagem("<b> Montando envio de   " + pagamentos.size() + " Pagamentos. </b>");
        for (Pagamento pagamento : pagamentos) {
            sincronizarPagamento(loa, pagamento);
        }
    }

    private void sincronizarPagamento(LOA loa, Pagamento pagamento) {
        JSONObject json = montarJSONPagamento(pagamento, loa);
        addMensagemRetorno(pagamento, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PAGAMENTO, json, UtilPortalTransparencia.METHOD_POST));
    }

    private JSONObject montarJSONReceitaLOA(ReceitaLOA receitaLOA) {
        JSONObject jsonObject = new JSONObject();
        JSONObject conteudoContaReceita = montarJSONContaReceita(receitaLOA.getContaDeReceita());
        jsonObject.put("contaReceita", conteudoContaReceita);
        jsonObject.put("operacaoReceita", receitaLOA.getOperacaoReceita().name());
        jsonObject.put("exercicio", getConteudoJSONExercicio(receitaLOA.getLoa().getLdo().getExercicio()));

        HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(receitaLOA.getEntidade(), singleton.getSelecionado().getDataOperacao());
        jsonObject.put("unidade", montarConteudoUnidade(hierarquiaDaUnidade));
        return jsonObject;
    }

    private JSONObject montarJSONReceitaLOAFonte(ReceitaLOAFonte receitaLOAFonte) {
        JSONObject jsonObject = new JSONObject();
        JSONObject conteudoJSONReceitaLoa = montarJSONReceitaLOA(receitaLOAFonte.getReceitaLOA());
        jsonObject.put("receitaLoa", conteudoJSONReceitaLoa);

        JSONObject conteudoJSOFonteDeRecurso = montarJSONFonteDeRecurso(receitaLOAFonte.getFonteRecurso());
        jsonObject.put("fonteDeRecurso", conteudoJSOFonteDeRecurso);
        jsonObject.put("valor", receitaLOAFonte.getValor());
        jsonObject.put("conjunto", receitaLOAFonte.getItem());
        return jsonObject;
    }

    private JSONObject montarJSONContaReceita(Conta contaReceita) {
        if (!singleton.contemObjeto(contaReceita)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo", contaReceita.getCodigo());
            jsonObject.put("descricao", contaReceita.getDescricao().replace("\"", ""));
            singleton.adicionarJsonObjet(contaReceita, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(contaReceita);
        }
    }

    private JSONObject montarJSONPagamento(Pagamento pagamento, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", pagamento.getNumeroPagamento());
        jsonObject.put("dataPagamento", pagamento.getDataPagamento().getTime());
        jsonObject.put("dataBaixa", pagamento.getDataConciliacao() == null ? null : pagamento.getDataConciliacao().getTime());
        jsonObject.put("historico", removerCaracteresInvalidos(pagamento.getHistoricoNota()));
        jsonObject.put("eventoContabil", montarStringEventoContabil(pagamento.getEventoContabil()));
        jsonObject.put("valor", pagamento.getValor());
        jsonObject.put("conta", pagamento.getSubConta().getContaBancariaEntidade().getNumeroContaComDigitoVerificador());
        jsonObject.put("contaFavorecido", pagamento.getContaPgto() != null ? pagamento.getContaPgto().toString() : " - ");
        jsonObject.put("tipoDocumento", pagamento.getTipoDocPagto().getDescricao());
        jsonObject.put("operacao", pagamento.getTipoOperacaoPagto().getDescricao());
        jsonObject.put("categoriaOrcamentaria", pagamento.getCategoriaOrcamentaria().name());
        jsonObject.put("liquidacao", montarJSONLiquidacao(pagamento.getLiquidacao(), loa));
        return jsonObject;
    }

    private String removerCaracteresInvalidos(String conteudo) {
        return StringUtil.removeCaracteresEspeciais(StringUtil.removerQuebraDeLinha(conteudo));
    }

    private void enviarLiquidacao(LOA loa, CategoriaOrcamentaria categoria) {
        List<Liquidacao> liquidacaos = liquidacaoFacade.buscarLiquidacaoPorUnidadesExercicio(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades(), categoria);
        addMensagem("<b> Montando envio de " + liquidacaos.size() + " liquidações. </b>");
        for (Liquidacao liquidacao : liquidacaos) {
            sincronizarLiquidacao(loa, liquidacao);
        }

    }

    private void sincronizarLiquidacao(LOA loa, Liquidacao liquidacao) {
        liquidacao = liquidacaoFacade.recuperar(liquidacao.getId());
        JSONObject conteudoLiquidacao = montarJSONLiquidacao(liquidacao, loa);
        addMensagemRetorno(liquidacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LIQUIDACAO, conteudoLiquidacao, UtilPortalTransparencia.METHOD_POST));
        for (LiquidacaoDoctoFiscal liquidacaoDoctoFiscal : liquidacao.getDoctoFiscais()) {
            JSONObject json = new JSONObject();
            json.put("liquidacao", conteudoLiquidacao);
            json.put("tipo", liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal().getDescricao());
            json.put("chave", liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getChaveDeAcesso());
            json.put("data", liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getDataDocto().getTime());
            json.put("dataAtesto", liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getDataAtesto() != null ? liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getDataAtesto().getTime() : null);
            json.put("numero", liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getNumero());
            json.put("valor", liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getValor());
            addMensagemRetorno(conteudoLiquidacao, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LIQUIDACAO_DOCTO_FISCAL, json, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private JSONObject montarJSONLiquidacao(Liquidacao liquidacao, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", liquidacao.getNumero());
        jsonObject.put("dataLiquidacao", liquidacao.getDataLiquidacao().getTime());
        jsonObject.put("historico", removerCaracteresInvalidos(liquidacao.getHistoricoRazao()));
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("categoriaOrcamentaria", liquidacao.getCategoriaOrcamentaria().name());
        jsonObject.put("valor", liquidacao.getValor());
        jsonObject.put("empenho", montarJSONEmpenho(liquidacao.getEmpenho(), loa));
        return jsonObject;
    }

    private void enviarEmpenho(LOA loa, CategoriaOrcamentaria categoria) {
        List<Empenho> empenhos = empenhoFacade.listaEmpenhoPorUnidadesExercicio(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades(), categoria);
        addMensagem("<b> Montando envio de " + empenhos.size() + " empenhos. </b>");

        for (Empenho empenho : empenhos) {
            if (empenho.getFonteDespesaORC() != null) {
                sincronizarEmpenho(loa, empenho);
            }
        }

    }

    private void sincronizarEmpenho(LOA loa, Empenho empenho) {
        JSONObject conteudoPessoa = montarConteudoJSONPessoa(empenho.getFornecedor());
        addMensagemRetorno(empenho.getFornecedor(), UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PESSOA, conteudoPessoa, UtilPortalTransparencia.METHOD_POST));

        JSONObject conteudoEmpenho = montarJSONEmpenho(empenho, loa);
        addMensagemRetorno(empenho, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_EMPENHO, conteudoEmpenho, UtilPortalTransparencia.METHOD_POST));
    }

    private JSONObject montarJSONEmpenho(Empenho empenho, LOA loa) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numero", empenho.getNumero());
        jsonObject.put("dataEmpenho", empenho.getDataEmpenho().getTime());
        jsonObject.put("tipoEmpenho", empenho.getTipoEmpenho().name());
        jsonObject.put("historico", removerCaracteresInvalidos(empenho.getComplementoHistorico()));
        jsonObject.put("categoriaOrcamentaria", empenho.getCategoriaOrcamentaria().name());
        jsonObject.put("eventoContabil", "teste");
        jsonObject.put("valor", empenho.getValor());

        JSONObject conteudoPessoa = montarConteudoJSONPessoa(empenho.getFornecedor());

        HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(empenho.getUnidadeOrganizacional(), empenho.getDataEmpenho());
        String codigoFonte = empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo();
        String codigoConta = empenho.getDespesaORC().getContaDeDespesa().getCodigo();
        String codigo = montarCodigoUnidadeDespesaOrcamentaria(empenho, hierarquiaDaUnidade);

        jsonObject.put("despesaOrcamentaria", empenho.getDespesaORC().getExercicio().getAno() + "." + codigo + "." + codigoConta + "." + codigoFonte);
        jsonObject.put("pessoa", conteudoPessoa);
        if (empenho.getPropostaConcessaoDiaria() != null) {
            sincronizarDiaria(empenho.getPropostaConcessaoDiaria());
            jsonObject.put("diaria", montarConteudoDiaria(empenho.getPropostaConcessaoDiaria()));
        }
        if (empenho.getContrato() != null) {
            Contrato contrato = contratoFacade.recuperarPortalTransparencia(empenho.getContrato().getId());
            sincronizarContrato(contrato);
            jsonObject.put("contrato", montarConteudoContrato(contrato));
        }
        return jsonObject;
    }

    private String montarCodigoUnidadeDespesaOrcamentaria(Empenho empenho, HierarquiaOrganizacional hierarquiaDaUnidade) {
        String codigo = empenho.getDespesaORC().getCodigo();
        String codigoAntigo = codigo.substring(0, 10);
        String codigoNovo = hierarquiaDaUnidade.getCodigo().substring(0, 10);
        codigo = codigo.replace(codigoAntigo, codigoNovo);
        return codigo;
    }

    private String montarStringEventoContabil(EventoContabil eventoContabil) {
        if (eventoContabil != null) {
            return "(" + eventoContabil.getEventoTce() + ") - " + eventoContabil.getCodigo() + " - " + eventoContabil.getDescricao();
        } else {
            return " - ";
        }
    }

    private JSONObject montarConteudoJSONPessoa(Pessoa fornecedor) {
        if (!singleton.contemObjeto(fornecedor)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nome", removerCaracteresInvalidos(fornecedor.getNome()));
            jsonObject.put("cpfCnpj", fornecedor.getCpf_Cnpj());
            if (fornecedor.getEnderecoPrincipal() != null) {
                jsonObject.put("endereco", fornecedor.getEnderecoPrincipal().toString());
            }
            if (fornecedor.getTelefonePrincipal() != null) {
                jsonObject.put("telefone", fornecedor.getTelefonePrincipal().toString());
            }
            if (fornecedor.getEmail() != null) {
                jsonObject.put("email", fornecedor.getEmail());
            }
            singleton.adicionarJsonObjet(fornecedor, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(fornecedor);
        }
    }

    public void atribuirCalculando(Boolean valor) {
        singleton.getSelecionado().setCalculando(valor);
        singleton.getSelecionado().getAssistente().setExecutando(valor);
    }

    public void abortar() {
        try {
            atribuirCalculando(false);
            addMensagem("<b> ABORTADO. </b>");
        } catch (Exception e) {
            logger.error("Erro ao abortar integração portal transparência.", e);
        }
    }

    public void limpar() {
        singleton.limpar();
    }

    private void addMensagem(String mensagem) {
        mensagem = DataUtil.getDataFormatadaDiaHora(new Date()) + ": " + mensagem;
        singleton.addMensagem(mensagem);
    }

    private void addMensagemErro(String mensagem) {
        mensagem = DataUtil.getDataFormatadaDiaHora(new Date()) + ": " + mensagem;
        singleton.addMensagemErro(mensagem);
    }

    private void addMensagemErroVermelho(String mensagem) {
        mensagem = DataUtil.getDataFormatadaDiaHora(new Date()) + ": " + mensagem;
        mensagem = "<font color=\"red\"> <b> " + mensagem + "</b> </font>";
        singleton.addMensagemErro(mensagem);
    }

    private void addMensagemRetorno(Object object, String mensagem) {
        try {
            if (singleton.getLogDetalhado()) {
                if (isMensagemDeErro(mensagem)) {
                    String mensagem1 = "<font color=\"red\">" + mensagem + " para a(o) <b> " + object.toString() + "</b> </font>";
                    addMensagem(mensagem1);
                    addMensagemErro(mensagem1);
                } else {
                    addMensagem(mensagem);
                }
            }
        } catch (Exception e) {
            addMensagem(mensagem);
        }
    }

    private boolean isMensagemDeErro(String mensagem) {
        return mensagem.toUpperCase().contains("Server returned HTTP response code".toUpperCase())
            || mensagem.toUpperCase().contains("erro".toUpperCase());
    }

    private void enviarExercicio() {
        singleton.getSelecionado().setExercicio(empenhoFacade.getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(singleton.getSelecionado().getDataOperacao())));
        addMensagem(UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_EXERCICIO, getConteudoJSONExercicio(singleton.getSelecionado().getExercicio()), "POST"));
    }

    private JSONObject getConteudoJSONExercicio(Exercicio exercicio) {
        if (!singleton.contemObjeto(exercicio)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ano", exercicio.getAno());
            singleton.adicionarJsonObjet(exercicio, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(exercicio);
        }
    }

    private void recupearPlanejamento() {
        if (singleton.getSelecionado().getEnviarPlanejamentoDespesa()) {
            addMensagem("<b> Atualizando PPA. </b>");

            PPA ppa = ppaFacade.ultimoPpaDoExercicio(singleton.getSelecionado().getExercicio());
            HashSet<ProgramaPPA> programaPPAs = new HashSet<ProgramaPPA>();
            HashSet<AcaoPPA> acaoPPAs = new HashSet<AcaoPPA>();

            LOA loa = recuperarLoa();

            List<ProvisaoPPADespesa> provisaoPPADespesas = provisaoPPADespesaFacade.provisaoPPADespesasPorExercicioUnidades(singleton.getSelecionado().getExercicio(), singleton.getSelecionado().getUnidades());
            addMensagem("<b> Montando envio de " + provisaoPPADespesas.size() + " Previsão de Despesa. </b>");
            for (ProvisaoPPADespesa provisaoPPADespesa : provisaoPPADespesas) {
                if (!singleton.getSelecionado().isCalculando()) {
                    break;
                }
                programaPPAs.add(provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA().getPrograma());
                acaoPPAs.add(provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA());
            }

            recuperarPPA(ppa);
            recuperarProgramaPPA(programaPPAs);
            recuperarAcaoPPA(acaoPPAs);
            recuperarLOALDO(loa);
            recuperarMetaFiscal(loa);
            recuperarPrevisaoDespesa(provisaoPPADespesas, loa);
            addMensagem("<b> Finalizado a recuperação dos dados do PPA. </b>");
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private void recuperarMetaFiscal(LOA loa) {
        List<MetasFiscais> metas = metasFiscaisFacade.buscarMetasFiscaisPorExercicio(loa.getLdo().getExercicio());
        for (MetasFiscais meta : metas) {
            JSONObject jsonObject = montarJsonMetaFiscal(meta);
            getObjetos().add(new ObjetoPortalTransparencia(meta, jsonObject, PortalTransparenciaNovo.WS_META_FISCAL, UtilPortalTransparencia.METHOD_POST));
        }

    }

    private JSONObject montarJsonMetaFiscal(MetasFiscais meta) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("exercicio", getConteudoJSONExercicio(meta.getExercicio()));
        jsonObject.put("tipoMetasFiscais", meta.getTipoMetasFiscais().name());
        jsonObject.put("receitaTotal", meta.getReceitaTotal());
        jsonObject.put("despesaTotal", meta.getDespesaTotal());
        jsonObject.put("receitaPrimaria", meta.getReceitaPrimaria());
        jsonObject.put("despesaPrimaria", meta.getDespesaPrimaria());
        jsonObject.put("resultadoNominal", meta.getResultadoNominal());
        jsonObject.put("dividaPublicaConsolidada", meta.getDividaPublicaConsolidada());
        jsonObject.put("dividaPublicaLiquida", meta.getDividaPublicaLiquida());
        jsonObject.put("fonteInformacao", meta.getFonteInformacao());
        return jsonObject;
    }

    private void recuperarLOALDO(LOA loa) {

        if (loa.getLdo().getAtoLegal() != null) {
            getObjetos().add(new ObjetoPortalTransparencia(loa.getLdo().getAtoLegal().getExercicio(), getConteudoJSONExercicio(loa.getLdo().getAtoLegal().getExercicio()), PortalTransparenciaNovo.WS_EXERCICIO, UtilPortalTransparencia.METHOD_POST));
            getObjetos().add(new ObjetoPortalTransparencia(loa.getLdo().getAtoLegal(), montarJSONAtoLegal(loa.getLdo().getAtoLegal()), PortalTransparenciaNovo.WS_ATO_LEGAL, UtilPortalTransparencia.METHOD_POST));
        }

        if (loa.getAtoLegal() != null) {
            getObjetos().add(new ObjetoPortalTransparencia(loa.getAtoLegal().getExercicio(), getConteudoJSONExercicio(loa.getAtoLegal().getExercicio()), PortalTransparenciaNovo.WS_EXERCICIO, UtilPortalTransparencia.METHOD_POST));
            getObjetos().add(new ObjetoPortalTransparencia(loa.getAtoLegal(), montarJSONAtoLegal(loa.getAtoLegal()), PortalTransparenciaNovo.WS_ATO_LEGAL, UtilPortalTransparencia.METHOD_POST));
        }

        getObjetos().add(new ObjetoPortalTransparencia(loa.getLdo(), montarJSONLDO(loa.getLdo()), PortalTransparenciaNovo.WS_LDO, UtilPortalTransparencia.METHOD_POST));
        getObjetos().add(new ObjetoPortalTransparencia(loa, montarJSONLOA(loa), PortalTransparenciaNovo.WS_LOA, UtilPortalTransparencia.METHOD_POST));
    }

    private void recuperarPPA(PPA ppa) {
        if (ppa.getAtoLegal() != null) {
            getObjetos().add(new ObjetoPortalTransparencia(ppa.getAtoLegal().getExercicio(), getConteudoJSONExercicio(ppa.getAtoLegal().getExercicio()), PortalTransparenciaNovo.WS_EXERCICIO, UtilPortalTransparencia.METHOD_POST));
            getObjetos().add(new ObjetoPortalTransparencia(ppa.getAtoLegal(), montarJSONAtoLegal(ppa.getAtoLegal()), PortalTransparenciaNovo.WS_ATO_LEGAL, UtilPortalTransparencia.METHOD_POST));
        }
        getObjetos().add(new ObjetoPortalTransparencia(ppa, montarJSONPPA(ppa), PortalTransparenciaNovo.WS_PPA, UtilPortalTransparencia.METHOD_POST));
    }

    private List<ObjetoPortalTransparencia> getObjetos() {
        return singleton.getObjetos();
    }

    private JSONObject montarJSONLDO(LDO ldo) {
        if (!singleton.contemObjeto(ldo)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ppa", montarJSONPPA(ldo.getPpa()));
            jsonObject.put("exercicio", getConteudoJSONExercicio(ldo.getExercicio()));

            if (ldo.getAtoLegal() != null) {
                jsonObject.put("atoLegal", montarJSONAtoLegal(ldo.getAtoLegal()));
            }
            jsonObject.put("dataAprovacao", ldo.getAprovacao() == null ? "" : ldo.getAprovacao().getTime());
            singleton.adicionarJsonObjet(ldo, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(ldo);
        }
    }

    private JSONObject montarJSONLOA(LOA loa) {
        if (!singleton.contemObjeto(loa)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ldo", montarJSONLDO(loa.getLdo()));
            jsonObject.put("atoLegal", montarJSONAtoLegal(loa.getAtoLegal()));
            jsonObject.put("dataAprovacao", loa.getAprovacao() == null ? "" : loa.getAprovacao().getTime());
            jsonObject.put("valorReceita", loa.getValorDaReceita());
            jsonObject.put("valorDespesa", loa.getValorDaDespesa());
            singleton.adicionarJsonObjet(loa, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(loa);
        }
    }

    private JSONObject montarJSONPPA(PPA ppa) {
        if (!singleton.contemObjeto(ppa)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("descricao", ppa.getPlanejamentoEstrategico().getDescricao());
            jsonObject.put("visao", ppa.getPlanejamentoEstrategico().getVisao());
            jsonObject.put("missao", ppa.getPlanejamentoEstrategico().getMissao());
            jsonObject.put("valores", ppa.getPlanejamentoEstrategico().getValores());
            jsonObject.put("atoLegal", montarJSONAtoLegal(ppa.getAtoLegal()));
            jsonObject.put("dataAprovacao", ppa.getAprovacao() == null ? "" : ppa.getAprovacao().getTime());
            singleton.adicionarJsonObjet(ppa, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(ppa);
        }

    }

    private void recuperarPrevisaoDespesa(List<ProvisaoPPADespesa> provisaoPPADespesas, LOA loa) {

        for (ProvisaoPPADespesa provisaoPPADespesa : provisaoPPADespesas) {
            provisaoPPADespesa = provisaoPPADespesaFacade.recuperarComFontes(provisaoPPADespesa.getId());
            if (!singleton.getSelecionado().isCalculando()) {
                break;
            }
            sincronizarProvisaoPPADespesa(provisaoPPADespesa, loa);

            for (ProvisaoPPAFonte provisaoPPAFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
                sincronizarProvisaoPPADespesaFonte(provisaoPPAFonte, loa);
            }
        }
    }

    public void sincronizarProvisaoPPADespesa(ProvisaoPPADespesa provisaoPPADespesa, LOA loa) {
        getObjetos().add(new ObjetoPortalTransparencia(provisaoPPADespesa.getContaDeDespesa(), montarJSONElementoDespesa(provisaoPPADespesa.getContaDeDespesa()), PortalTransparenciaNovo.WS_ELEMENTO_DESPESA, UtilPortalTransparencia.METHOD_POST));
        getObjetos().add(new ObjetoPortalTransparencia(provisaoPPADespesa.getSubAcaoPPA(), montaJSONSubAcaoPPA(provisaoPPADespesa.getSubAcaoPPA()), PortalTransparenciaNovo.WS_SUBACAO, UtilPortalTransparencia.METHOD_POST));
        getObjetos().add(new ObjetoPortalTransparencia(provisaoPPADespesa, montaJSOProvisaoPPADespesa(provisaoPPADespesa, loa), PortalTransparenciaNovo.WS_PREVISAO_DESPESA, UtilPortalTransparencia.METHOD_POST));
    }

    public void sincronizarProvisaoPPADespesaFonte(ProvisaoPPAFonte provisaoPPAFonte, LOA loa) {
        JSONObject jsonObject = montarJSONFonteDeRecurso(provisaoPPAFonte.getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos());
        getObjetos().add(new ObjetoPortalTransparencia(provisaoPPAFonte.getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos(), jsonObject, PortalTransparenciaNovo.WS_FONTE_DE_RECURSO, UtilPortalTransparencia.METHOD_POST));

        getObjetos().add(new ObjetoPortalTransparencia(provisaoPPAFonte, montaConteudoJSONPrevisaoDespesaFonte(provisaoPPAFonte, loa), PortalTransparenciaNovo.WS_PREVISAO_DESPESA_FONTE, UtilPortalTransparencia.METHOD_POST));
    }

    private void recuperarAcaoPPA(HashSet<AcaoPPA> acaoPPAs) {
        for (AcaoPPA acaoPPA : acaoPPAs) {
            if (!singleton.getSelecionado().isCalculando()) {
                break;
            }
            getObjetos().add(new ObjetoPortalTransparencia(acaoPPA.getFuncao(), montaJSOFuncaoPPA(acaoPPA.getFuncao()), PortalTransparenciaNovo.WS_FUNCAO, UtilPortalTransparencia.METHOD_POST));
            getObjetos().add(new ObjetoPortalTransparencia(acaoPPA.getSubFuncao(), montaJSOSubFuncaoPPA(acaoPPA.getSubFuncao()), PortalTransparenciaNovo.WS_SUBFUNCAO, UtilPortalTransparencia.METHOD_POST));
            getObjetos().add(new ObjetoPortalTransparencia(acaoPPA, montaJSONAcaoPPA(acaoPPA, acaoPPA.getPrograma()), PortalTransparenciaNovo.WS_ACAO, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private void recuperarProgramaPPA(HashSet<ProgramaPPA> programaPPAs) {
        for (ProgramaPPA programa : programaPPAs) {
            if (!singleton.getSelecionado().isCalculando()) {
                break;
            }
            getObjetos().add(new ObjetoPortalTransparencia(programa, montarJSOProgramaPPA(programa), PortalTransparenciaNovo.WS_PROGRAMA, UtilPortalTransparencia.METHOD_POST));
        }
    }

    private JSONObject montaConteudoJSONPrevisaoDespesaFonte(ProvisaoPPAFonte provisaoPPAFonte, LOA loa) {
        if (!singleton.contemObjeto(provisaoPPAFonte)) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("fonteDeRecurso", montarJSONFonteDeRecurso(provisaoPPAFonte.getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos()));
            jsonObject.put("previsaoDespesa", montaJSOProvisaoPPADespesa(provisaoPPAFonte.getProvisaoPPADespesa(), loa));
            jsonObject.put("valor", provisaoPPAFonte.getValor());
            singleton.adicionarJsonObjet(provisaoPPAFonte, jsonObject);

            return jsonObject;
        } else {
            return singleton.getJsonObject(provisaoPPAFonte);
        }
    }

    private JSONObject montaJSOProvisaoPPADespesa(ProvisaoPPADespesa provisaoPPADespesa, LOA loaDoExercicio) {
        if (!singleton.contemObjeto(provisaoPPADespesa)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("loa", montarJSONLOA(loaDoExercicio));
            jsonObject.put("acao", montaJSONAcaoPPA(provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA(), provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA().getPrograma()));


            HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(provisaoPPADespesa.getUnidadeOrganizacional(), singleton.getSelecionado().getDataOperacao());
            jsonObject.put("unidade", montarConteudoUnidade(hierarquiaDaUnidade));


            jsonObject.put("elementoDespesa", montarJSONElementoDespesa(provisaoPPADespesa.getContaDeDespesa()));
            jsonObject.put("subAcao", montaJSONSubAcaoPPA(provisaoPPADespesa.getSubAcaoPPA()));
            jsonObject.put("exercicio", getConteudoJSONExercicio(provisaoPPADespesa.getSubAcaoPPA().getExercicio()));
            singleton.adicionarJsonObjet(provisaoPPADespesa, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(provisaoPPADespesa);
        }
    }

    private JSONObject montarJSONElementoDespesa(Conta contaDeDespesa) {
        if (!singleton.contemObjeto(contaDeDespesa)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo", contaDeDespesa.getCodigo());
            jsonObject.put("descricao", contaDeDespesa.getDescricao());
            singleton.adicionarJsonObjet(contaDeDespesa, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(contaDeDespesa);
        }
    }

    private JSONObject montarJSONFonteDeRecurso(FonteDeRecursos fonteDeRecursos) {
        if (!singleton.contemObjeto(fonteDeRecursos)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo", fonteDeRecursos.getCodigo());
            jsonObject.put("descricao", fonteDeRecursos.getDescricao());
            singleton.adicionarJsonObjet(fonteDeRecursos, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(fonteDeRecursos);
        }
    }

    private void enviarUnidades() {
        if (singleton.getSelecionado().getEnviarUnidades()) {
            for (HierarquiaOrganizacional unidade : singleton.getSelecionado().getUnidades()) {
                addMensagem(UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_UNIDADE, montarConteudoUnidade(unidade), "POST"));
            }
            addMensagem("<b> Finalizado a atualização de unidades. </b>");
            singleton.getSelecionado().getAssistente().conta();
        }
    }

    private JSONObject montarConteudoUnidade(HierarquiaOrganizacional unidade) {
        if (!singleton.contemObjeto(unidade)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo", unidade.getCodigo());
            jsonObject.put("identificador", unidade.getSubordinada() != null ? unidade.getSubordinada().getId() : null);
            jsonObject.put("descricao", unidade.getDescricao());
            jsonObject.put("dataInicial", unidade.getInicioVigencia() != null ? unidade.getInicioVigencia().getTime() : null);
            jsonObject.put("dataFinal", unidade.getFimVigencia() == null ? (" ") : unidade.getFimVigencia().getTime());
            singleton.adicionarJsonObjet(unidade, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(unidade);
        }
    }

    private JSONObject montarJSOProgramaPPA(ProgramaPPA programa) {
        if (!singleton.contemObjeto(programa)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ppa", montarJSONPPA(programa.getPpa()));
            jsonObject.put("codigo", programa.getCodigo());
            jsonObject.put("descricao", programa.getObjetivo());
            jsonObject.put("denominacao", programa.getDenominacao());
            jsonObject.put("tipoPrograma", programa.getTipoPrograma().name());
            jsonObject.put("itemPlanejamento", programa.getItemPlanejamentoEstrategico().getDescricao());
            jsonObject.put("horizonteTemporal", programa.getHorizonteTemporal().name());
            jsonObject.put("baseGeografica", programa.getBaseGeografica().name());
            jsonObject.put("dataInicio", programa.getInicio().getTime());
            jsonObject.put("dataFim", programa.getFim().getTime());
            UnidadeOrganizacional responsavel = programa.getResponsavel();
            if (responsavel == null) {
                responsavel = prestacaoDeContasFacade.buscarUnidadeDaProvisaoPPA(programa);
            }
            HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(responsavel, singleton.getSelecionado().getDataOperacao());
            jsonObject.put("responsavel", montarConteudoUnidade(hierarquiaDaUnidade));
            singleton.adicionarJsonObjet(programa, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(programa);
        }
    }

    private JSONObject montaJSONAcaoPPA(AcaoPPA acaoPPA, ProgramaPPA programaPPA) {
        if (!singleton.contemObjeto(acaoPPA)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo", acaoPPA.getTipoAcaoPPA().getCodigo() + "" + acaoPPA.getCodigo());
            jsonObject.put("descricao", acaoPPA.getDescricao());
            jsonObject.put("tipo", acaoPPA.getTipoAcaoPPA().getDescricao());
            jsonObject.put("programa", montarJSOProgramaPPA(programaPPA));
            jsonObject.put("funcao", montaJSOFuncaoPPA(acaoPPA.getFuncao()));
            jsonObject.put("subFuncao", montaJSOSubFuncaoPPA(acaoPPA.getSubFuncao()));

            UnidadeOrganizacional responsavel = acaoPPA.getResponsavel();
            if (responsavel == null) {
                responsavel = prestacaoDeContasFacade.buscarUnidadeDaProvisaoPPA(acaoPPA.getPrograma());
            }
            HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(responsavel, singleton.getSelecionado().getDataOperacao());
            jsonObject.put("unidade", montarConteudoUnidade(hierarquiaDaUnidade));
            jsonObject.put("totalFinanceiro", acaoPPA.getTotalFinanceiro());
            jsonObject.put("totalFisico", acaoPPA.getTotalFisico());
            singleton.adicionarJsonObjet(acaoPPA, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(acaoPPA);
        }
    }

    private JSONObject montaJSOSubFuncaoPPA(SubFuncao subfuncao) {
        if (!singleton.contemObjeto(subfuncao)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo", subfuncao.getCodigo());
            jsonObject.put("descricao", subfuncao.getDescricao());
            singleton.adicionarJsonObjet(subfuncao, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(subfuncao);
        }
    }

    private JSONObject montaJSONSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        if (!singleton.contemObjeto(subAcaoPPA)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo", subAcaoPPA.getCodigo());
            jsonObject.put("descricao", subAcaoPPA.getDescricao());
            singleton.adicionarJsonObjet(subAcaoPPA, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(subAcaoPPA);
        }
    }

    private JSONObject montaJSOFuncaoPPA(Funcao funcao) {
        if (!singleton.contemObjeto(funcao)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo", funcao.getCodigo());
            jsonObject.put("descricao", funcao.getDescricao());
            singleton.adicionarJsonObjet(funcao, jsonObject);
            return jsonObject;
        } else {
            return singleton.getJsonObject(funcao);
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfiguracaoTituloSistemaFacade getConfiguracaoTituloSistemaFacade() {
        return configuracaoTituloSistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    private JSONObject montarJsonArquivo(Arquivo arq) {
        try {
            if (arq != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nome", StringUtil.removeCaracteresEspeciais(arq.getNome()));
                jsonObject.put("tipo", arq.getMimeType());
                jsonObject.put("bytes", montarBytesArquivoToString(arq));
                return jsonObject;
            }
        } catch (Exception e) {
            logger.error("Erro ao montar arquivo: ", e);
        }
        return null;
    }

    private String montarBytesArquivoToString(Arquivo arquivo) throws IOException {
        if (arquivo.getId() != null) {
            arquivo = arquivoFacade.recuperaDependencias(arquivo.getId());
        }
        arquivo.montarImputStream();
        StreamedContent streamedContent = new DefaultStreamedContent(arquivo.getInputStream(), arquivo.getMimeType(), arquivo.getNome());

        File file = File.createTempFile("temp", "webpublico");
        FileInputStream imageInFile = new FileInputStream(file);
        byte[] bytes = IOUtils.toByteArray(streamedContent.getStream());
        imageInFile.read(bytes);


        imageInFile.close();
        return encodeImage(bytes);
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public PortalTransparenciaNovoSingleton getSingleton() {
        return singleton;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public void sincronizarLancamento(SistemaFacade.PerfilApp perfilDev) {
        try {
            Date data = new Date();
            Integer ano = DataUtil.getAno(data);
            Exercicio exercicio = empenhoFacade.getExercicioFacade().getExercicioPorAno(ano);
            singleton.inicializar(new PortalTransparenciaNovo(data, exercicio));
            addMensagem("<b>Iniciando sincronização automatica. </b>");
            List<EntidadePortalTransparencia> entidadePortalTransparencias = recuperarObjetos();
            addMensagem("<b>Recuperou " + entidadePortalTransparencias.size() + " movimentos para sincronizar! </b>");
            singleton.definirUrl(sistemaFacade.getPerfilAplicacao(), singleton.getSelecionado());
            if (SistemaFacade.PerfilApp.DEV.equals(perfilDev)) {
                addMensagem("<b>Sincronização em ambiente de TESTE ...</b>");
            }
            ordenarMovimentos(entidadePortalTransparencias);
            addMensagem("<b> Preparando as informações .... </b>");
            movimentarEntidadePortalTransparencia(entidadePortalTransparencias);
        } catch (Exception e) {
            e.printStackTrace();
            addMensagemErro(e.getMessage());
        }

    }

    private void ordenarMovimentos(List<EntidadePortalTransparencia> entidadePortalTransparencias) {
        Collections.sort(entidadePortalTransparencias, new Comparator<EntidadePortalTransparencia>() {
            @Override
            public int compare(EntidadePortalTransparencia o1, EntidadePortalTransparencia o2) {
                if (o1.getTipo().getOrdem() != null && o2.getTipo().getOrdem() != null) {
                    return o1.getTipo().getOrdem().compareTo(o2.getTipo().getOrdem());
                }
                return 0;
            }
        });
    }

    private void movimentarEntidadePortalTransparencia(List<EntidadePortalTransparencia> entidadePortalTransparencias) {

        List<ServidorPortal> servidores = Lists.newArrayList();
        for (EntidadePortalTransparencia entidadePortalTransparencia : entidadePortalTransparencias) {
            if (entidadePortalTransparencia.getTipo() == null) {
                throw new ExcecaoNegocioGenerica("O campo Tipo é obrigatório.");
            }
            if (TipoObjetoPortalTransparencia.SERVIDOR.equals(entidadePortalTransparencia.getTipo())) {
                servidores.add((ServidorPortal) entidadePortalTransparencia);
            } else {
                sincronizar(entidadePortalTransparencia);
                singleton.alterarStatusEntidadeSalvar(entidadePortalTransparencia);
            }
            int posicao = entidadePortalTransparencias.indexOf(entidadePortalTransparencia) + 1;
            addMensagem("Sincronizando registro " + posicao + "/" + entidadePortalTransparencias.size());
        }
        if (!servidores.isEmpty()) {
            sincronizarServidorPortal(servidores);
            for (ServidorPortal servidor : servidores) {
                singleton.alterarStatusEntidadeSalvar(servidor);
            }
        }
        enviarUltimaAtualizacao();
        enviarListDeObjetos("");
        finalizar();
    }

    private void sincronizar(EntidadePortalTransparencia entidadePortalTransparencia) {
        switch (entidadePortalTransparencia.getTipo()) {
            case EMPENHO:
                Empenho empenho = ((EmpenhoPortal) entidadePortalTransparencia).getEmpenho();
                sincronizarEmpenho(getLoa(empenho.getCategoriaOrcamentaria()), empenho);
                break;
            case EMPENHO_ESTORNO:
                EmpenhoEstorno empenhoEstorno = ((EmpenhoEstornoPortal) entidadePortalTransparencia).getEmpenhoEstorno();
                sincronizarEmpenhoEstorno(getLoa(empenhoEstorno.getCategoriaOrcamentaria()), empenhoEstorno);
                break;
            case LIQUIDACAO:
                Liquidacao liquidacao = ((LiquidacaoPortal) entidadePortalTransparencia).getLiquidacao();
                sincronizarLiquidacao(getLoa(liquidacao.getCategoriaOrcamentaria()), liquidacao);
                break;
            case LIQUIDACAO_ESTORNO:
                LiquidacaoEstorno liquidacaoEstorno = ((LiquidacaoEstornoPortal) entidadePortalTransparencia).getLiquidacaoEstorno();
                sincronizarLiquidacaoEstorno(getLoa(liquidacaoEstorno.getCategoriaOrcamentaria()), liquidacaoEstorno);
                break;
            case PAGAMENTO:
                Pagamento pagamento = ((PagamentoPortal) entidadePortalTransparencia).getPagamento();
                sincronizarPagamento(getLoa(pagamento.getCategoriaOrcamentaria()), pagamento);
                break;
            case PAGAMENTO_ESTORNO:
                PagamentoEstorno pagamentoEstorno = ((PagamentoEstornoPortal) entidadePortalTransparencia).getPagamentoEstorno();
                sincronizarPagamentoEstorno(getLoa(pagamentoEstorno.getCategoriaOrcamentaria()), pagamentoEstorno);
                break;
            case RECEITA_REALIZADA:
                LancamentoReceitaOrc lancamentoReceitaOrc = ((LancamentoReceitaOrcPortal) entidadePortalTransparencia).getLancamentoReceitaOrc();
                sincronizarReceitaRealizada(lancamentoReceitaOrc);
                enviarListDeObjetos("");
                break;
            case RECEITA_REALIZADA_ESTORNO:
                ReceitaORCEstorno receitaOrc = ((ReceitaOrcEstornoPortal) entidadePortalTransparencia).getReceitaORCEstorno();
                sincronizarReceitaRealizadaEstorno(receitaOrc);
                enviarListDeObjetos("");
                break;
            case ALTERACAO_ORCAMENTARIA:
                AlteracaoORC alteracaoORC = ((AlteracaoOrcPortal) entidadePortalTransparencia).getAlteracaoORC();
                sincronizarAlteracaoOrcamentaria(recuperarLoa(), alteracaoORC);
                enviarListDeObjetos("");
                break;
            case ALTERACAO_ORCAMENTARIA_ESTORNO:
                EstornoAlteracaoOrc estornoAlteracaoOrc = ((AlteracaoOrcEstornoPortal) entidadePortalTransparencia).getEstornoAlteracaoOrc();
                sincronizarAlteracaoOrcamentariaEstorno(recuperarLoa(), estornoAlteracaoOrc);
                enviarListDeObjetos("");
                break;
            case DIARIA:
                PropostaConcessaoDiaria diaria = ((DiariaPortal) entidadePortalTransparencia).getPropostaConcessaoDiaria();
                sincronizarDiaria(diaria);
                enviarListDeObjetos("");
                break;
            case ATO_LEGAL:
                AtoLegal atoLegal = ((AtoLegalPortal) entidadePortalTransparencia).getAtoLegal();
                sincronizarAtoLegal(atoLegal);
                enviarListDeObjetos("");
                break;
            case CONVENIO_DESPESA:
                ConvenioDespesa convenioDespesa = ((ConvenioDespesaPortal) entidadePortalTransparencia).getConvenioDespesa();
                sincronizarConvenioDespesa(convenioDespesa);
                enviarListDeObjetos("");
                break;
            case CONVENIO_RECEITA:
                ConvenioReceita convenioReceita = ((ConvenioReceitaPortal) entidadePortalTransparencia).getConvenioReceita();
                sincronizarConvenioReceita(convenioReceita);
                enviarListDeObjetos("");
                break;
            case LICITACAO:
                Licitacao licitacao = ((LicitacaoPortal) entidadePortalTransparencia).getLicitacao();
                addMensagem(" <b> Sincronizando a licitação " + licitacao.toString() + " </b> ");
                sincronizarLicitacao(licitacao);
                enviarListDeObjetos("");
                break;
            case SERVIDOR:
                List<ServidorPortal> servidorPortals = Lists.newArrayList();
                servidorPortals.add(((ServidorPortal) entidadePortalTransparencia));
                sincronizarServidorPortal(servidorPortals);
                break;
            case TRANSFERENCIA_CONTA_FINANCEIRA: {
                TransferenciaContaFinanceira transferenciaContaFinanceira = ((TransferenciaContaFinanceiraPortal) entidadePortalTransparencia).getTransContaFinanceira();
                sincronizarTransferenciaContaFinanceira(transferenciaContaFinanceira);
                enviarListDeObjetos("");
                break;
            }
            case TRANSFERENCIA_CONTA_FINANCEIRA_ESTORNO: {
                EstornoTransferencia estornoTransferencia = ((EstornoTransferenciaPortal) entidadePortalTransparencia).getEstornoTransferencia();
                sincronizarEstornoTransferenciaContaFinanceira(estornoTransferencia);
                enviarListDeObjetos("");
                break;
            }
            case LIBERACAO_COTA_FINANCEIRA: {
                LiberacaoCotaFinanceira liberacaoCotaFinanceira = ((LiberacaoCotaFinanceiraPortal) entidadePortalTransparencia).getLiberacaoCotaFinanceira();
                sincronizarLiberacaoCotaFinanceira(liberacaoCotaFinanceira);
                enviarListDeObjetos("");
                break;
            }
            case LIBERACAO_COTA_FINANCEIRA_ESTORNO: {
                EstornoLibCotaFinanceira estornoLibCotaFinanceira = ((EstornoLiberacaoCotaPortal) entidadePortalTransparencia).getEstornoLibCotaFinanceira();
                sincronizarEstornoLiberacaoCotaFinanceira(estornoLibCotaFinanceira);
                enviarListDeObjetos("");
                break;
            }
            case OBRA: {
                Obra obra = ((ObraPortal) entidadePortalTransparencia).getObra();
                sincronizarObra(obra);
                enviarListDeObjetos("");
                break;
            }

            case CONTRATO: {
                Contrato contrato = ((ContratoPortal) entidadePortalTransparencia).getContrato();
                sincronizarContrato(contrato);
                enviarListDeObjetos("");
                break;
            }
            case DISPENSA_LICITACAO: {
                DispensaDeLicitacao dispensaDeLicitacao = ((DispensaLicitacaoPortal) entidadePortalTransparencia).getDispensaDeLicitacao();
                sincronizarDispensaLicitacao(dispensaDeLicitacao);
                enviarListDeObjetos("");
                break;
            }
            case ATA_REGISTRO_PRECO: {
                AtaRegistroPreco ataRegistroPreco = ((AtaRegistroPrecoPortal) entidadePortalTransparencia).getAtaRegistroPreco();
                sincronizarAtaRegistroPreco(ataRegistroPreco);
                enviarListDeObjetos("");
                break;
            }
            case ATA_REGISTRO_PRECO_EXTERNO: {
                RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno = ((AtaRegistroDePrecoExternoPortal) entidadePortalTransparencia).getRegistroSolicitacaoMaterialExterno();
                sincronizarAtaRegistroPrecoExterno(registroSolicitacaoMaterialExterno);
                enviarListDeObjetos("");
                break;
            }
            case CALAMIDADE_PUBLICA:
                CalamidadePublica calamidadePublica = ((CalamidadePublicaPortal) entidadePortalTransparencia).getCalamidadePublica();
                sincronizarCalamidadePublica(calamidadePublica);
                enviarListDeObjetos("");
                break;
            case DICIONARIO_DE_DADOS:
                DicionarioDeDados dicionarioDeDados = ((DicionarioDeDadosPortal) entidadePortalTransparencia).getDicionarioDeDados();
                sincronizarDicionarioDeDados(dicionarioDeDados);
                enviarListDeObjetos("");
                break;
            case LOA:
                LOA loa = ((LOAPortal) entidadePortalTransparencia).getLoa();
                sincronizarLoa(loa);
                enviarListDeObjetos("");
                break;
            case LDO:
                LDO ldo = ((LDOPortal) entidadePortalTransparencia).getLdo();
                sincronizarLdo(ldo);
                enviarListDeObjetos("");
                break;
            case PPA:
                PPA ppa = ((PPAPortal) entidadePortalTransparencia).getPpa();
                sincronizarPpa(ppa);
                enviarListDeObjetos("");
                break;
            case EMENDA:
                Emenda emenda = ((EmendaPortal) entidadePortalTransparencia).getEmenda();
                sincronizarEmenda(emenda);
                enviarListDeObjetos("");
                break;
            case UNIDADE:
                UnidadeOrganizacional unidade = ((UnidadePortal) entidadePortalTransparencia).getUnidade();
                HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaDaUnidade(unidade, singleton.getSelecionado().getDataOperacao());
                if (hierarquiaDaUnidade != null && TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(hierarquiaDaUnidade.getTipoHierarquiaOrganizacional())) {
                    sincronizarInstitucional(hierarquiaDaUnidade);
                    enviarListDeObjetos("");
                }
                break;
            case BEM:
                Bem bem = ((BemPortal) entidadePortalTransparencia).getBem();
                List<VOBemPortal> bens = bemFacade.buscarVOBensPortal(" bem.id = " + bem.getId(), singleton.getSelecionado().getDataOperacao());
                for (VOBemPortal voBem : bens) {
                    sincronizarBem(voBem, singleton.getSelecionado().getDataOperacao());
                }
                break;
        }
    }

    private void sincronizarLoa(LOA loa) {
        JSONObject conteudo = montarJSONLOA(loa);
        addMensagemRetorno(loa, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LOA, conteudo, UtilPortalTransparencia.METHOD_POST));
    }

    private void sincronizarLdo(LDO ldo) {
        JSONObject conteudo = montarJSONLDO(ldo);
        addMensagemRetorno(ldo, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_LDO, conteudo, UtilPortalTransparencia.METHOD_POST));
    }

    private void sincronizarPpa(PPA ppa) {
        JSONObject conteudo = montarJSONPPA(ppa);
        addMensagemRetorno(ppa, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_PPA, conteudo, UtilPortalTransparencia.METHOD_POST));
    }

    private void sincronizarEstornoLiberacaoCotaFinanceira(EstornoLibCotaFinanceira estornoLibCotaFinanceira) {
        JSONObject json = montarJSONEstornoLiberacaoFinanceira(estornoLibCotaFinanceira);
        getObjetos().add(new ObjetoPortalTransparencia(estornoLibCotaFinanceira, json,
            PortalTransparenciaNovo.WS_LIBERACAO_FINANCEIRA_ESTORNO, UtilPortalTransparencia.METHOD_POST));
    }

    private void sincronizarObra(Obra obra) {
        obra = obraFacade.recuperarPortalTransparencia(obra.getId());
        JSONObject json = montarJSONObra(obra);

        addMensagemRetorno(obra, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_OBRA, json, UtilPortalTransparencia.METHOD_POST));
        for (ItemContrato item : obra.getContrato().getItens()) {
            JSONObject jsonItem = montarJSONItensDaObra(item, obra);

            addMensagemRetorno(item, UtilPortalTransparencia.enviar(singleton.getSelecionado().getUrl() + PortalTransparenciaNovo.WS_OBRA_ITEM, jsonItem, UtilPortalTransparencia.METHOD_POST));
        }
    }


    private void sincronizarLiberacaoCotaFinanceira(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        JSONObject json = montarJSONLiberacaoFinanceira(liberacaoCotaFinanceira);
        getObjetos().add(new ObjetoPortalTransparencia(liberacaoCotaFinanceira, json,
            PortalTransparenciaNovo.WS_LIBERACAO_FINANCEIRA, UtilPortalTransparencia.METHOD_POST));
    }

    private void sincronizarEstornoTransferenciaContaFinanceira(EstornoTransferencia estornoTransferencia) {
        JSONObject json = montarJSONEstornoTransferenciaFinanceira(estornoTransferencia);
        getObjetos().add(new ObjetoPortalTransparencia(estornoTransferencia, json,
            PortalTransparenciaNovo.WS_LIBERACAO_FINANCEIRA_ESTORNO, UtilPortalTransparencia.METHOD_POST));
    }

    private void sincronizarTransferenciaContaFinanceira(TransferenciaContaFinanceira transferenciaContaFinanceira) {
        JSONObject json = montarJSONTransferenciaFinanceira(transferenciaContaFinanceira);
        getObjetos().add(new ObjetoPortalTransparencia(transferenciaContaFinanceira, json,
            PortalTransparenciaNovo.WS_LIBERACAO_FINANCEIRA, UtilPortalTransparencia.METHOD_POST));
    }

    public LOA getLoa(CategoriaOrcamentaria categoriaOrcamentaria) {
        if (categoriaOrcamentaria.equals(CategoriaOrcamentaria.NORMAL)) {
            return recuperarLoa();
        } else {
            return recuperarLoaRestos();
        }
    }

    private List<EntidadePortalTransparencia> recuperarObjetos() {
        String hql = "select obj from EntidadePortalTransparencia obj where obj.situacao <> 'PUBLICADO' order by obj.id";
        Query consulta = em.createQuery(hql, EntidadePortalTransparencia.class);
        return consulta.getResultList();
    }

    public List<EntidadePortalTransparencia> buscarEntidadesPortalPorTipoSituacao(TipoObjetoPortalTransparencia tipo, PortalTransparenciaSituacao situacao) {
        String hql = "select obj from EntidadePortalTransparencia obj where obj.situacao = :situacao and obj.tipo = :tipo order by obj.id";
        Query consulta = em.createQuery(hql, EntidadePortalTransparencia.class);
        consulta.setParameter("tipo", tipo);
        consulta.setParameter("situacao", situacao);
        return consulta.getResultList();
    }

    public void salvarPortal(EntidadePortalTransparencia entity) {
        em.persist(entity);
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public void validarAtoLegal(AtoLegal atoLegal) {
        ValidacaoException ve = new ValidacaoException();
        if (atoLegal.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo exercício deve ser informado.");
        }
        if (atoLegal.getPropositoAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Propósito deve ser informado.");
        }
        if (atoLegal.getEsferaGoverno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Esfera deve ser informado.");
        }
        if (atoLegal.getVeiculoDePublicacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Veículo de Publicação deve ser informado.");
        }
        if (atoLegal.getDataEmissao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Data deve ser informado.");
        }
        if (atoLegalFacade.existeNumeroDoAtoLegalRepetido(atoLegal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existem um Ato Legal cadastrado com o número <b>" + atoLegal.getNumero() + "</b>" +
                " do Tipo <b>" + atoLegal.getTipoAtoLegal().getDescricao() + "</b>" +
                " para o exercício de <b>" + atoLegal.getExercicio().getAno() + "</b>.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void salvarPrefeitura(PrefeituraPortal prefeituraPortal) {
        singleton.salvarPrefeitura(prefeituraPortal);
    }

    public List<PrefeituraPortal> buscarPorNomeLike(String parte) {
        String sql = " select p.* from PrefeituraPortal p "
            + " where p.nome like :parte ";
        Query q = em.createNativeQuery(sql, PrefeituraPortal.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public String getUrlExternoPortalTransparenciaPrefeitura() {
        String sql = "SELECT URLEXTERNOPORTALTRANSPARENCIA FROM PREFEITURAPORTAL WHERE PRINCIPAL = 1";

        Query q = em.createNativeQuery(sql);

        return (String) q.getSingleResult();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public PrefeituraPortal recuperar(Object id) {
        PrefeituraPortal obj = super.recuperar(id);
        Hibernate.initialize(obj.getUnidades());
        Hibernate.initialize(obj.getUnidadesAdm());
        Hibernate.initialize(obj.getModulos());
        return obj;
    }
}
