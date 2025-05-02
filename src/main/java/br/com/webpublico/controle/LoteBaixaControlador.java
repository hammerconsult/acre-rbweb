package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteGeracaoCadastrosDaf607;
import br.com.webpublico.entidadesauxiliares.FiltroConsultaDAM;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.arrecadacao.ArquivoArrecadacaoExecutor;
import br.com.webpublico.negocios.tributario.arrecadacao.DepoisDePagarQueue;
import br.com.webpublico.negocios.tributario.arrecadacao.IntegracaoTributarioContabilQueueManager;
import br.com.webpublico.util.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@ManagedBean(name = "loteBaixaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaArrecadacao", pattern = "/tributario/arrecadacao/novo/",
        viewId = "/faces/tributario/arrecadacao/baixa/edita.xhtml"),
    @URLMapping(id = "editarArrecadacao", pattern = "/tributario/arrecadacao/editar/#{loteBaixaControlador.id}/",
        viewId = "/faces/tributario/arrecadacao/baixa/edita.xhtml"),
    @URLMapping(id = "listarArrecadacao", pattern = "/tributario/arrecadacao/listar/",
        viewId = "/faces/tributario/arrecadacao/baixa/lista.xhtml"),
    @URLMapping(id = "verArrecadacao", pattern = "/tributario/arrecadacao/ver/#{loteBaixaControlador.id}/",
        viewId = "/faces/tributario/arrecadacao/baixa/visualizar.xhtml"),
    @URLMapping(id = "logPagamento", pattern = "/tributario/arrecadacao/log-pagamento/",
        viewId = "/faces/tributario/arrecadacao/baixa/logpagamento.xhtml"),
    @URLMapping(id = "consultaParcelaArrecadacao", pattern = "/tributario/arrecadacao/consulta/",
        viewId = "/faces/tributario/arrecadacao/baixa/consulta.xhtml"),
    @URLMapping(id = "logArquivoRetornoBancario", pattern = "/tributario/arrecadacao/log-arquivo/",
        viewId = "/faces/tributario/arrecadacao/baixa/logarquivo.xhtml"),
    @URLMapping(id = "arquivosArrecadacao", pattern = "/tributario/arrecadacao/arquivos/",
        viewId = "/faces/tributario/arrecadacao/baixa/listaarquivos.xhtml")

})
public class LoteBaixaControlador extends PrettyControlador<LoteBaixa> implements Serializable {

    public static final int DEZEMBRO = 12;
    private static final Logger logger = LoggerFactory.getLogger(LoteBaixaControlador.class);
    private final BigDecimal CEM = new BigDecimal("100");
    boolean integrou = false;
    boolean concluiuPosPagamento = false;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private LoteBaixaFacade facade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<LoteBaixa> lista;
    private LoteBaixa selecionado;
    private ItemLoteBaixa itemLoteBaixa;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private SubContaFacade subContaFacade;
    private ConverterAutoComplete converterBanco, converterCC;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private ConsultaDAMFacade consultaDAMFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private DAMFacade damFacade;
    private List<DAM> dams;
    private FileUploadEvent fileUploadEvent;
    private List<FileUploadEvent> fileUploadEvents;
    private SituacaoParcela situacaoParcela;
    private String filtroCodigoBarras;
    private Pessoa filtroContribuinte;
    private Cadastro cadastro;
    private String filtroNumeroDAM;
    private TipoCadastroTributario tipoCadastroTributario;
    private ConverterAutoComplete converterCadastroImobiliario, converterCadastroEconomico, converterCadastroRural, converterPessoa;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterAutoComplete converterDivida;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private LoteFacade loteFacade;
    private ConverterAutoComplete converterExercicio;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    private Boolean habilitaPesquisa;
    private Date dataPagamento;
    private ArquivoLoteBaixa arquivoLoteBaixa;
    private transient Converter converterConta;
    private ImprimeRelatoriosArrecadacao imprimeRelatoriosArrecadacao;
    private Integer ano;
    private LoteBaixaFacade.ProcessaArquivo processoArquivo;
    private CompletableFuture<LoteBaixaFacade.ProcessaArquivo> futureProcesso;
    private CompletableFuture<ArrecadacaoFacade.AssistenteArrecadacao> futurePagamento;
    private Long numeroArquivo;
    private Date dataPagamentoInicial;
    private Date dataPagamentoFinal;
    private Date dataMovimentoInicial;
    private Date dataMovimentoFinal;
    private SituacaoLoteBaixa situacaoLoteBaixa;
    private List<LoteBaixaDoArquivo> arquivosProcessados;
    private Long quantidadeFiltroArquivos;
    private List<LoteBaixaDoArquivo> arquivosAProcessar;
    private LoteBaixaDoArquivo[] arquivosAProcessarSelecionados;
    private ArrecadacaoFacade.AssistenteArrecadacao assistenteArrecadacao;
    private ConverterAutoComplete loteBaixaConverte;
    private List<String> mensagens;
    private FiltroConsultaDAM filtroConsultaDAM;
    private Operacoes operacao;
    private LoteBaixa loteAntigo;
    private PagamentoCartao pagamentoCartao;
    private DAM dam;
    private Future<AssistenteGeracaoCadastrosDaf607> futureCadastrosDaf607;
    private Future<AssistenteGeracaoCadastrosDaf607> futureDebitosDaf607;
    private ConverterAutoComplete converterLoteBaixa;
    private Map<String, BancoContaConfTributario> agrupadoresDeConta;
    private Map<String, List<SelectItem>> contasPorBanco;
    private List<BancoContaConfTributario> bancosContaConfTributario;
    private List<LoteBaixaDoArquivo> arquivosNaoProcessados;
    private final Map<Integer, Date> mapaDataVencimentoParcelaItemPagamentoCartao = Maps.newLinkedHashMap();

    @EJB
    private DepoisDePagarQueue depoisDePagarQueue;
    @EJB
    private IntegracaoTributarioContabilQueueManager integracaoTributarioContabilQueueManager;

    public LoteBaixaControlador() {
        super(LoteBaixa.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    public ArquivoLoteBaixa getArquivoLoteBaixa() {
        return arquivoLoteBaixa;
    }

    public void setArquivoLoteBaixa(ArquivoLoteBaixa arquivoLoteBaixa) {
        this.arquivoLoteBaixa = arquivoLoteBaixa;
    }

    public ArrecadacaoFacade.AssistenteArrecadacao getAssistenteArrecadacao() {
        return assistenteArrecadacao;
    }

    public PagamentoCartao getPagamentoCartao() {
        return pagamentoCartao;
    }

    public void setPagamentoCartao(PagamentoCartao pagamentoCartao) {
        this.pagamentoCartao = pagamentoCartao;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public Converter getConverterConta() {
        if (converterConta == null) {
            converterConta = new Converter() {
                @Override
                public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
                    if (string == null || string.isEmpty()) {
                        return null;
                    }
                    return facade.recuperar(BancoContaConfTributario.class, Long.parseLong(string));
                }

                @Override
                public String getAsString(FacesContext fc, UIComponent uic, Object o) {
                    if (o == null) {
                        return null;
                    } else {
                        return String.valueOf(((BancoContaConfTributario) o).getId());
                    }
                }
            };
        }
        return converterConta;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCadastroEconomico;
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroRuralFacade.listaFiltrandoPorCodigo(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, cadastroRuralFacade);
        }
        return converterCadastroRural;
    }

    public List<Pessoa> recuperaPessoasCadastro() {
        List<Pessoa> retorno = new ArrayList<Pessoa>();
        if (cadastro != null && cadastro.getId() != null) {
            if (cadastro instanceof CadastroImobiliario) {
                CadastroImobiliario cadastroIm = cadastroImobiliarioFacade.recuperar(cadastro.getId());
                for (Propriedade p : cadastroIm.getPropriedade()) {
                    retorno.add(p.getPessoa());
                }
            }
            if (cadastro instanceof CadastroRural) {
                CadastroRural cadastroRu = cadastroRuralFacade.recuperar(cadastro.getId());
                for (PropriedadeRural p : cadastroRu.getPropriedade()) {
                    retorno.add(p.getPessoa());
                }
            }
            if (cadastro instanceof CadastroEconomico) {
                CadastroEconomico cadastroEco = cadastroEconomicoFacade.recuperar(cadastro.getId());
                for (SociedadeCadastroEconomico sociedadeCadastroEconomico : cadastroEco.getSociedadeCadastroEconomico()) {
                    retorno.add(sociedadeCadastroEconomico.getSocio());
                }
            }
        }
        return retorno;
    }

    public LoteBaixaFacade getFacade() {
        return facade;
    }

    public ItemLoteBaixa getItemLoteBaixa() {
        return itemLoteBaixa;
    }

    public void setItemLoteBaixa(ItemLoteBaixa itemLoteBaixa) {
        this.itemLoteBaixa = itemLoteBaixa;
    }

    public Converter getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, bancoFacade);
        }
        return converterBanco;
    }

    public Converter getConverterCC() {
        if (converterCC == null) {
            converterCC = new ConverterAutoComplete(SubConta.class, subContaFacade);
        }
        return converterCC;
    }

    public List<LoteBaixa> getLista() {
        if (lista == null) {
            lista = facade.lista();
        }
        return lista;
    }

    public LoteBaixa getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(LoteBaixa selecionado) {
        this.selecionado = selecionado;
        itemLoteBaixa = new ItemLoteBaixa();
    }

    public List<LoteBaixaDoArquivo> getArquivosAProcessar() {
        return arquivosAProcessar;
    }

    public LoteBaixaDoArquivo[] getArquivosAProcessarSelecionados() {
        return arquivosAProcessarSelecionados;
    }

    public void setArquivosAProcessarSelecionados(LoteBaixaDoArquivo[] arquivosAProcessarSelecionados) {
        this.arquivosAProcessarSelecionados = arquivosAProcessarSelecionados;
    }

    public List<LoteBaixaDoArquivo> getArquivosAProcessarParaSelecao() {
        if (arquivosAProcessarSelecionados == null) {
            arquivosAProcessarSelecionados = new LoteBaixaDoArquivo[0];
        }
        List<LoteBaixaDoArquivo> retorno = Lists.newArrayList();
        List<String> conferencia = Lists.newArrayList();
        for (LoteBaixaDoArquivo loteBaixaDoArquivo : arquivosAProcessarSelecionados) {
            if (!conferencia.contains(loteBaixaDoArquivo.getArquivoLoteBaixa().getHashMd5())) {
                retorno.add(loteBaixaDoArquivo);
                conferencia.add(loteBaixaDoArquivo.getArquivoLoteBaixa().getHashMd5());
            }
        }
        return retorno;
    }

    public List<Banco> completaBanco(String parte) {
        return bancoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<SubConta> completaSubConta(String parte) {
        List<SubConta> lista = new ArrayList<SubConta>();
        if (selecionado.getBanco() == null || selecionado.getBanco().getId() == null) {
            SubConta c = new SubConta();
            c.setCodigo("Nenhum Banco foi Selecionado");
            lista.add(c);
        } else {
            lista = subContaFacade.listaFiltrandoSubContasPorBanco(selecionado.getBanco(), parte.trim());
        }
        return lista;
    }

    public List<SubConta> completaConta(String parte) {
        List<SubConta> lista = new ArrayList<SubConta>();
        if (arquivoLoteBaixa.getTransientBanco() == null || arquivoLoteBaixa.getTransientBanco().getId() == null) {
            SubConta c = new SubConta();
            c.setCodigo("Nenhum Banco foi encontrado");
            lista.add(c);
        } else {
            lista = subContaFacade.listaFiltrandoSubContasPorBanco(arquivoLoteBaixa.getTransientBanco(), parte.trim());
        }
        return lista;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    @URLAction(mappingId = "logArquivoRetornoBancario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        processoArquivo = (LoteBaixaFacade.ProcessaArquivo) Web.pegaDaSessao("processoArquivo");
        futureProcesso = (CompletableFuture<LoteBaixaFacade.ProcessaArquivo>) Web.pegaDaSessao("futureProcesso");

    }

    @URLAction(mappingId = "novaArrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        operacao = Operacoes.NOVO;
        arquivoLoteBaixa = new ArquivoLoteBaixa();
        selecionado = (LoteBaixa) Web.pegaDaSessao(LoteBaixa.class);
        if (selecionado != null) {
            selecionar(selecionado);
        } else {
            habilitaPesquisa = false;
            selecionado = new LoteBaixa();
            selecionado.setSituacaoLoteBaixa(SituacaoLoteBaixa.EM_ABERTO);
            itemLoteBaixa = new ItemLoteBaixa();
            selecionado.setTipoDePagamentoBaixa(TipoDePagamentoBaixa.BAIXA_MANUAL);
            arquivoLoteBaixa.setConta(new BancoContaConfTributario());
            imprimeRelatoriosArrecadacao = new ImprimeRelatoriosArrecadacao();
        }
        selecionado.setIntegraContasAcrecimos(true);
        assistenteArrecadacao = null;
        bancosContaConfTributario = facade.recuperaContasConfiguracao();
    }

    @URLAction(mappingId = "editarArrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void selecionar() {
        super.editar();
        operacao = Operacoes.EDITAR;
        recuperaPorId();
        if (SituacaoLoteBaixa.BAIXADO.equals(selecionado.getSituacaoLoteBaixa())
            || SituacaoLoteBaixa.ESTORNADO.equals(selecionado.getSituacaoLoteBaixa())
            || SituacaoLoteBaixa.BAIXADO_INCONSITENTE.equals(selecionado.getSituacaoLoteBaixa())) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        }
        filtroConsultaDAM = new FiltroConsultaDAM();
        bancosContaConfTributario = facade.recuperaContasConfiguracao();
    }

    private void recuperaPorId() {
        arquivoLoteBaixa = new ArquivoLoteBaixa();
        LoteBaixa lote = facade.recuperar(getId());
        selecionar(lote);
    }

    @URLAction(mappingId = "verArrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        operacao = Operacoes.VER;
        recuperaPorId();
        filtroConsultaDAM = new FiltroConsultaDAM();
    }

    public void selecionar(LoteBaixa selecionado) {
        mensagens = Lists.newArrayList();
        habilitaPesquisa = false;
        this.selecionado = selecionado;
        itemLoteBaixa = new ItemLoteBaixa();
        arquivoLoteBaixa.setConta(facade.recuperaBancoContaConfTributarioPorSubConta(selecionado.getSubConta()));
        imprimeRelatoriosArrecadacao = new ImprimeRelatoriosArrecadacao();
        String numero = facade.retornaNumeroArquivoLote(selecionado);
        if (numero != null && !numero.isEmpty()) {
            numeroArquivo = Long.valueOf(numero);
        }
        addMensagem(selecionado);
    }

    private void addMensagem(LoteBaixa selecionado) {
        String dams = "";
        Map<String, List<String>> mapDansPagos = Maps.newLinkedHashMap();
        for (ItemLoteBaixa loteBaixa : selecionado.getItemLoteBaixa()) {
            if (loteBaixa.getDam() == null && loteBaixa.getDamInformadoSoNumero() != null && loteBaixa.getDamInformadoSoNumero() >= LoteBaixaFacade.NUMEROLIMITEDAMNFSE) {
                dams += loteBaixa.getDamInformado() + ", ";
            }
            if (loteBaixa.getDam() != null) {
                List<String> lotes = facade.buscarLotesOndeODamJaFoiPago(loteBaixa.getDam().getId(), loteBaixa.getId());
                if (lotes != null && !lotes.isEmpty()) {
                    mapDansPagos.put(loteBaixa.getDam().getNumeroDAM(), lotes);
                }
            }
        }
        if (!dams.isEmpty()) {
            mensagens.add("Atenção, os DAMs " + dams + " são de Nota Fiscal Eletrônica e ainda " +
                " não foram enviados para o WebPublico, se o lote for efetivado estes serão arrecadados como débito não" +
                " identificado.");
        }
        if (!mapDansPagos.isEmpty()) {
            mensagens.add("Atenção, os DAMs abaixo já foram pagos em outro lote.");
            for (Map.Entry<String, List<String>> entryDam : mapDansPagos.entrySet()) {
                mensagens.add("DAM " + entryDam.getKey() + " já foi pago no(s) lote(s): " + Joiner.on(" ,").join(entryDam.getValue()));
            }
        }
    }

    public void cancelar() {
        navegaEmbora();
    }

    private void navegaEmbora() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public void salvar() {
        if (validaCampos()) {
            try {
                selecionado.setSituacaoLoteBaixa(SituacaoLoteBaixa.EM_ABERTO);
                if (selecionado.getId() == null) {
                    selecionado.setCodigoLote(facade.gerarCodigoLote(selecionado.getDataPagamento()));
                }
                selecionado = facade.salvaRetornando(selecionado);
                lista = null;
                FacesUtil.addInfo("Salvo com sucesso!", "");
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/ " + selecionado.getId() + "/");
            } catch (Exception e) {
                logger.error("Erro ao salvar: {}", e);
                FacesUtil.addFatal("Exceção do sistema!", e.getMessage());
            }
        }
    }

    public void excluir() {
        try {
            facade.remover(selecionado);
            navegaEmbora();
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Excluído com sucesso");
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void conferirAndamentoDosCadastros() {
        if (futureCadastrosDaf607 != null && futureCadastrosDaf607.isDone()) {
            FacesUtil.executaJavaScript("gerarCalculos()");
        }
    }

    public void conferirAndamentoDosDebitos() {
        if (futureDebitosDaf607 != null && futureDebitosDaf607.isDone()) {
            try {
                FacesUtil.executaJavaScript("encerrarDaf607()");
                encerrarLoteManual();
                selecionado = futureCadastrosDaf607.get().getLoteBaixa();
                futureDebitosDaf607 = null;
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Erro ao gerar os débitos do DAF607 ", e);
            }
        }
    }

    public void encerrarLoteManual() {
        selecionado.setValorTotal(getTotalLancado());
        try {
            if (validaCampos()) {
                UsuarioSistema usuarioSistema = sistemaFacade.getUsuarioCorrente();
                selecionado = facade.encerrarLoteManual(selecionado);
                assistenteArrecadacao = new ArrecadacaoFacade.AssistenteArrecadacao(sistemaFacade.getUsuarioCorrente(), selecionado, selecionado.getItemLoteBaixa().size());
                futurePagamento = AsyncExecutor.getInstance().execute(assistenteArrecadacao,
                    () -> {
                        ArrecadacaoFacade.AssistenteArrecadacao assistente = facade.getArrecadacaoFacade()
                            .efetuaPagamento(assistenteArrecadacao, usuarioSistema);
                        gerarDamsInconsistentes(assistente);
                        depoisDePagarQueue.enqueueProcess(facade.buildDepoisDePagar(selecionado));
                        gerarIntegracaoContabil();
                        return assistente;
                    });
            }
        } catch (Exception e) {
            Util.loggingError(this.getClass(), "Erro ao encerrar manual:", e);
        }
    }

    private void gerarIntegracaoContabil() {
        if (!facade.getArrecadacaoFacade().lotePossuiValorValorOriginalTributoMenorQueDesconto(selecionado.getId())) {
            integracaoTributarioContabilQueueManager.enqueueProcess(() ->
            {
                AssistenteBarraProgresso assistenteBarraProgresso = new AssistenteBarraProgresso(1);
                facade.getArrecadacaoFacade().gerarIntegracao(selecionado, TipoIntegracao.ARRECADACAO);
                assistenteBarraProgresso.contar(1);
            });
        }
    }


    private void gerarDamsInconsistentes(ArrecadacaoFacade.AssistenteArrecadacao assistente) {
        facade.getArrecadacaoFacade().gerarDamsDeInconsistencia(selecionado, sistemaFacade.getUsuarioCorrente(), assistente.getValoresDividaInconsistencia());
    }

    public void posPagamento() {
        if (futurePagamento != null && futurePagamento.isDone() && !integrou) {
            try {
                futurePagamento = null;
                concluiuPosPagamento = true;
                FacesUtil.executaJavaScript("terminaPagamento();");
            } catch (Exception e) {
                logger.error("Erro no posPagamento: {}", e);
            }
        }
    }

    protected void descobrirETratarException(Exception e) {
        try {
            Util.getRootCauseDataBaseException(e);
        } catch (SQLIntegrityConstraintViolationException sql) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), sql.getMessage());
        } catch (Exception throwable) {
            logger.error("Erro ao descobrirETratarException: {}", throwable);
            FacesUtil.addError("Não foi possível realizar a exclusão!", "");
        }
    }

    private boolean validaCampos() {
        boolean resultado = true;

        if (selecionado.getFormaPagamento() == null) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a Forma de Pagamento.");
        }

        if (selecionado.getDataPagamento() == null) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a Data de Pagamento.");
        }

        if (selecionado.getBanco() == null || selecionado.getBanco().getId() == null) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Banco!");
        }

        if (selecionado.getSubConta() == null || selecionado.getSubConta().getId() == null) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a Conta Corrente.");
        }

        if (selecionado.getDataFinanciamento() == null) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a Data Financeira.");
        }

        if (selecionado.getTipoDePagamentoBaixa() == null) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Tipo de pagamento.");
        }

        if (selecionado.getValorTotal() == null || selecionado.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O valor total do Lote deve ser maior ou igual a zero.");
        }

        if (selecionado.getQuantidadeParcelas() == null || selecionado.getQuantidadeParcelas() < 0) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "A quantidade de DAMs do Lote deve ser maior ou igual a zero.");
        }

        if (selecionado.getItemLoteBaixa().isEmpty()) {
            List<LoteBaixaDoArquivo> arquivosLoteBaixa = facade.buscarLoteBaixaDoArquivo(selecionado);
            for (LoteBaixaDoArquivo loteBaixaDoArquivo : arquivosLoteBaixa) {
                if (loteBaixaDoArquivo.getArquivoLoteBaixa().getTipoArquivoBancarioTributario().equals(TipoArquivoBancarioTributario.RCB001)) {
                    resultado = false;
                    FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Nenhum débito foi adicionado ao lote.");
                    break;
                }
            }
        }
        if (!verificaContasDoLote(selecionado)) {
            resultado = false;
        }

        if (TipoDePagamentoBaixa.BAIXA_MANUAL.equals(selecionado.getTipoDePagamentoBaixa()) &&
            selecionado.getDataPagamento() != null && selecionado.getDataPagamento().after(new Date())) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Data de Pagamento deve ser menor ou igual a data atual.");
        }
        return resultado;
    }

    public boolean mostrarMensagemValorValorOriginalTributoMenorQueDesconto() {
        return concluiuPosPagamento && facade.getArrecadacaoFacade().lotePossuiValorValorOriginalTributoMenorQueDesconto(selecionado.getId());
    }

    public String mensagemValoresInconsistentesTributos() {
        return "Existem valores inconsistentes nos tributos do(s) DAM(s), a integração contábil não foi gerada, entre em contato com o suporte!";
    }

    public void ajustarDataVencimentoDemaisParcelas(PagamentoCartaoItem pagamentoCartaoItem) {
        try {
            ValidacaoException ve = new ValidacaoException();
            Exercicio exercicioCorrente = exercicioFacade.getExercicioCorrente();
            if (pagamentoCartaoItem.getParcela() == 1 && pagamentoCartaoItem.getVencimento() != null) {
                Calendar vencimentoPrimeiraParcela = Calendar.getInstance();
                vencimentoPrimeiraParcela.setTime(pagamentoCartaoItem.getVencimento());
                pagamentoCartaoItem.setVencimento(DataUtil.ajustarDataUtil(vencimentoPrimeiraParcela.getTime(), facade.getFeriadoFacade()));
                int mesParaAdcionar = 0;
                Calendar dataVencimento = Calendar.getInstance();
                for (PagamentoCartaoItem cartaoItem : pagamentoCartao.getItem()) {
                    dataVencimento.setTime(DataUtil.adicionarMeses(pagamentoCartaoItem.getVencimento(), mesParaAdcionar));
                    if (dataVencimento.get(Calendar.YEAR) != exercicioCorrente.getAno()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade de parcela não pode exceder o exercício vigente.");
                        adicionarQuantidadeParcelasDaBandeira();
                        ve.lancarException();
                    }
                    cartaoItem.setVencimento(dataVencimento.getTime());
                    cartaoItem.setVencimento(DataUtil.ajustarDataUtilPraTras(cartaoItem.getVencimento(), facade.getFeriadoFacade(), true));
                    mesParaAdcionar++;
                }
                validarQuantidadeParcelas(true);
            } else if (pagamentoCartaoItem.getVencimento() != null) {
                Calendar vencimento = Calendar.getInstance();
                vencimento.setTime(DataUtil.ajustarDataUtil(pagamentoCartaoItem.getVencimento(), facade.getFeriadoFacade()));
                if (vencimento.get(Calendar.YEAR) != exercicioCorrente.getAno()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data de vencimento deve estar dentro do exercício de " + exercicioCorrente.getAno() + ".");
                    pagamentoCartaoItem.setVencimento(mapaDataVencimentoParcelaItemPagamentoCartao.get(pagamentoCartaoItem.getParcela()));
                    ve.lancarException();
                }
                pagamentoCartaoItem.setVencimento(vencimento.getTime());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void validarQuantidadeParcelas(boolean lancarExceptionValidacao) {
        ValidacaoException ve = new ValidacaoException();
        if (pagamentoCartao.getQuantidadeParcela() != null && pagamentoCartao.getQuantidadeParcela() > 0) {
            for (PagamentoCartaoItem pagamentoCartaoItem : pagamentoCartao.getItem()) {
                if (pagamentoCartaoItem.getParcela() == 1 && pagamentoCartaoItem.getVencimento() != null) {
                    Calendar dataVencimento = Calendar.getInstance();
                    dataVencimento.add(Calendar.MONTH, 1);
                    if (pagamentoCartao.getBandeiraCartao() != null && pagamentoCartao.getQuantidadeParcela() > pagamentoCartao.getBandeiraCartao().getQuantidadeParcelas()) {
                        pagamentoCartao.setQuantidadeParcela(pagamentoCartao.getBandeiraCartao().getQuantidadeParcelas());
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade máxima de parcelas para a bandeira selecionada é " + pagamentoCartao.getBandeiraCartao().getQuantidadeParcelas() + ".");
                        criarItemPagamentoCartao(lancarExceptionValidacao);
                        FacesUtil.atualizarComponente("FormularioDialog:qtdeParcelas");
                    } else if (dataVencimento.get(Calendar.MONTH) + pagamentoCartao.getQuantidadeParcela() > DEZEMBRO) {
                        int qtdeParcelasPermitidas = dataVencimento.get(Calendar.MONTH) + pagamentoCartao.getQuantidadeParcela();
                        for (int i = qtdeParcelasPermitidas; i > 0; i--) {
                            if (i < DEZEMBRO) {
                                pagamentoCartao.setQuantidadeParcela(i);
                                break;
                            }
                        }
                        adicionarMnesagemExceptionECriaitemPagamentoCartao(ve, "A quantidade de parcela não pode exceder o exercício vigente.", lancarExceptionValidacao);
                    }
                    break;
                }
            }
        }
        if (lancarExceptionValidacao) ve.lancarException();
    }

    private void adicionarMnesagemExceptionECriaitemPagamentoCartao(ValidacaoException ve, String mensagem, boolean lancarExceptionValidacao) {
        ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        criarItemPagamentoCartao(lancarExceptionValidacao);
        FacesUtil.atualizarComponente("FormularioDialog:qtdeParcelas");
    }

    public void adicionarQuantidadeParcelasDaBandeira() {
        try {
            if (pagamentoCartao.getBandeiraCartao() == null) return;
            pagamentoCartao.setQuantidadeParcela(pagamentoCartao.getBandeiraCartao().getQuantidadeParcelas());
            criarItemPagamentoCartao(false);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void criarItemPagamentoCartao(boolean lancarExceptionValidacao) {
        try {
            validarQuantidadeParcelas(lancarExceptionValidacao);
            pagamentoCartao.getItem().clear();
            if ((pagamentoCartao.getQuantidadeParcela() != null && pagamentoCartao.getQuantidadeParcela() > 0) && pagamentoCartao.getValorPago() != null) {
                Date vencimento = DataUtil.adicionarMeses(new Date(), 1);
                vencimento = DataUtil.ajustarDataUtilPraTras(vencimento, facade.getFeriadoFacade(), true);
                mapaDataVencimentoParcelaItemPagamentoCartao.clear();
                for (int numeroParcela = 1; numeroParcela <= pagamentoCartao.getQuantidadeParcela(); numeroParcela++) {
                    PagamentoCartaoItem item = new PagamentoCartaoItem();

                    BigDecimal valor = pagamentoCartao.getValorPago();
                    BigDecimal valorParcela = valor.divide(new BigDecimal(pagamentoCartao.getQuantidadeParcela()), 2, BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal somaParcelas = valorParcela.multiply(new BigDecimal(pagamentoCartao.getQuantidadeParcela()));
                    BigDecimal primeiraParcela = valor.subtract(somaParcelas);
                    primeiraParcela = primeiraParcela.add(valorParcela);

                    if (numeroParcela == 1) {
                        item.setValor(primeiraParcela);
                    } else {
                        item.setValor(valorParcela);
                    }
                    item.setParcela(numeroParcela);
                    item.setVencimento(vencimento);
                    item.setPagamentoCartao(pagamentoCartao);
                    pagamentoCartao.getItem().add(item);
                    mapaDataVencimentoParcelaItemPagamentoCartao.put(item.getParcela(), item.getVencimento());
                    vencimento = DataUtil.adicionarMeses(new Date(), numeroParcela + 1);
                    vencimento = DataUtil.ajustarDataUtilPraTras(vencimento, facade.getFeriadoFacade(), true);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void criarPagamentoCartao() {
        pagamentoCartao = new PagamentoCartao();
        pagamentoCartao.setItemLoteBaixa(itemLoteBaixa);
        pagamentoCartao.setValorPago(dam.getValorTotal());
        pagamentoCartao.setQuantidadeParcela(1);
        criarItemPagamentoCartao(true);
        FacesUtil.atualizarComponente("idDialogCartaoCredito");
        FacesUtil.executaJavaScript("dialogCartaoCredito.show()");
    }

    public void definirFormaPagamento(DAM damPagamento) {
        if (LoteBaixa.FormaPagamento.CARTAO_CREDITO.equals(selecionado.getFormaPagamento())) {
            dam = damPagamento;
            criarPagamentoCartao();
        } else {
            adicionarDAM(damPagamento);
            FacesUtil.atualizarComponente("formConsulta:tabela-parcelas");
        }
    }


    public void adicionarDAM(DAM dam) {
        if (validaItem(dam)) {
            try {
                adicionarItem(dam);
                dams.remove(dam);
                FacesUtil.addInfo("", "O DAM de número " + dam.getNumeroDAM() + " foi adicionada com sucesso ao lote.");
            } catch (Exception e) {
                logger.error("Erro ao adicionarDAM: {}", e);
                FacesUtil.addError("Ocorreu um erro ao adicionar a parcela:", "" + e.getMessage());
            }
        }
    }

    public void addTodosItem() {
        for (DAM dam : dams) {
            try {
                adicionarItem(dam);
                FacesUtil.addInfo("", "O DAM de número " + dam.getNumeroDAM() + " foi adicionada com sucesso ao lote.");
            } catch (Exception e) {
                logger.error("Erro ao addTodosItem: {}", e);
                FacesUtil.addError("Ocorreu um erro ao adicionar a parcela:", "" + e.getMessage());
            }
        }
    }

    public void removeItem(ActionEvent e) {
        itemLoteBaixa = (ItemLoteBaixa) e.getComponent().getAttributes().get("objeto");
        selecionado.getItemLoteBaixa().remove(itemLoteBaixa);
        itemLoteBaixa = new ItemLoteBaixa();
    }

    private boolean validaItem(DAM dam) {
        if (dam != null) {
            for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
                if (item.getDam() != null) {
                    if (item.getDam().equals(dam)) {
                        FacesUtil.addError("Não foi possível continuar!", "Esta parcela já foi adicionada.");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<SelectItem> getSituacoesLote() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(" ", null));
        for (SituacaoLoteBaixa situacao : SituacaoLoteBaixa.values()) {
            toReturn.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return toReturn;
    }

    public BigDecimal getTotalLancado() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            total = total.add(item.getValorPago());
        }
        return total;
    }

    public BigDecimal getTotalDesconto() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            if (item.getDam() != null && item.getDam().getDesconto() != null) {
                total = total.add(item.getDam().getDesconto());
            }
        }
        return total;
    }

    public BigDecimal getTotalDiferenca() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            total = total.add(item.getValorDiferenca());
        }
        return total;
    }

    public BigDecimal getTotalJuros() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            if (item.getDam() != null && item.getDam().getJuros() != null) {
                total = total.add(item.getDam().getJuros());
            }
        }
        return total;
    }

    public BigDecimal getTotalMulta() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            if (item.getDam() != null && item.getDam().getMulta() != null) {
                total = total.add(item.getDam().getMulta());
            }
        }
        return total;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            if (item.getDam() != null && item.getDam().getValorOriginal() != null) {
                total = total.add(item.getDam().getValorOriginal());
            }
        }
        return total;
    }

    public BigDecimal getTotalCorrecao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            if (item.getDam() != null && item.getDam().getCorrecaoMonetaria() != null) {
                total = total.add(item.getDam().getCorrecaoMonetaria());
            }
        }
        return total;
    }

    public BigDecimal getTotalHonorarios() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            if (item.getDam() != null && item.getDam().getHonorarios() != null) {
                total = total.add(item.getDam().getHonorarios());
            }
        }
        return total;
    }

    public String infoTipoPagamento() {
        String retorno = selecionado.getTipoDePagamentoBaixa().getDescricao();
        if (TipoDePagamentoBaixa.BAIXA_ARQUIVO_RETORNO.equals(selecionado.getTipoDePagamentoBaixa()) &&
            selecionado.getArquivosLoteBaixa() != null &&
            !selecionado.getArquivosLoteBaixa().isEmpty()) {
            retorno += " (" + selecionado.getArquivosLoteBaixa().get(0).getArquivoLoteBaixa().getTipoArquivoBancarioTributario().name() + ")";
        }
        return retorno;
    }

    public void iniciarPesquisa() {
        try {
            validarCamposPreenchidos();
            dams = damFacade.buscarDamPorCadastroPessoaNumero(cadastro, filtroContribuinte, filtroNumeroDAM);
            validarDAMPago(dams);
            for (DAM dam : dams) {
                dam.setItens(damFacade.itensDoDam(dam));
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    private void validarDAMPago(List<DAM> dams) {
        if (dams.isEmpty() && (filtroNumeroDAM != null && !filtroNumeroDAM.trim().isEmpty())) {
            ValidacaoException ve = new ValidacaoException();
            List<String> lotes = facade.buscarLotesOndeODamJaFoiPago(filtroNumeroDAM);
            if (!lotes.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O DAM " + filtroNumeroDAM.trim() + " já pago no lote " + lotes.get(0) + ".");
            }
            ve.lancarException();
        }
    }

    public void setaInscricaoCadastro() {
        cadastro = null;
        filtroContribuinte = null;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, dividaFacade);
        }
        return converterDivida;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    private void validarCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if ((filtroContribuinte == null || filtroContribuinte.getId() == null)
            && (cadastro == null)
            && (filtroNumeroDAM == null || filtroNumeroDAM.trim().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um filtro.");
        }
        ve.lancarException();
    }

    public String getFiltroCodigoBarras() {
        return filtroCodigoBarras;
    }

    public void setFiltroCodigoBarras(String filtroCodigoBarras) {
        this.filtroCodigoBarras = filtroCodigoBarras;
    }

    public String getFiltroNumeroDAM() {
        return filtroNumeroDAM;
    }

    public void setFiltroNumeroDAM(String filtroNumeroDAM) {
        this.filtroNumeroDAM = filtroNumeroDAM;
    }

    public boolean isBaixado() {
        if (selecionado == null || selecionado.getSituacaoLoteBaixa() == null) {
            return false;
        }
        return selecionado.getSituacaoLoteBaixa().equals(SituacaoLoteBaixa.BAIXADO);
    }

    public boolean isEstornado() {
        if (selecionado == null || selecionado.getSituacaoLoteBaixa() == null) {
            return false;
        }
        return selecionado.isEstornado();
    }

    public boolean isAberto() {
        if (selecionado == null || selecionado.getSituacaoLoteBaixa() == null) {
            return false;
        }
        return selecionado.isAberto();
    }

    public void upload(FileUploadEvent event) throws FileNotFoundException, IOException {
        fileUploadEvent = event;
        byte[] buffer = fileUploadEvent.getFile().getContents();
        InputStream input = fileUploadEvent.getFile().getInputstream();
        input.read(buffer);
        String temp = new String(buffer).toString();
        input.close();

    }

    public void sobeArquivos(FileUploadEvent event) throws FileNotFoundException, IOException {
        if (fileUploadEvents == null) {
            fileUploadEvents = new ArrayList<FileUploadEvent>();
        }
        fileUploadEvents.add(event);
    }

    public void gerarArquivos() {
        try {
            ValidacaoException geraArquivos = facade.gerarArquivos(Lists.<FileUploadEvent>newArrayList(fileUploadEvents));
            if (geraArquivos.temMensagens()) {
                FacesUtil.printAllFacesMessages(geraArquivos.getMensagens());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addError("Ocorreu um erro ao salvar: ", ex.getMessage());
            logger.error("Erro ao gerarArquivos: {}", ex);
        }
        novoViaArquivo();
    }

    @URLAction(mappingId = "arquivosArrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoViaArquivo() {
        fileUploadEvents = Lists.newArrayList();
        arquivosProcessados = Lists.newArrayList();
        quantidadeFiltroArquivos = 10L;
        arquivoLoteBaixa = new ArquivoLoteBaixa();
        agrupadoresDeConta = Maps.newHashMap();
        bancosContaConfTributario = facade.recuperaContasConfiguracao();
        arquivosNaoProcessados = facade.listaArquivosNaoProcessado();
        arquivosAProcessar = Lists.newArrayList();
        arquivosAProcessar.addAll(getArquivosNaoProcessados());
    }

    public List<LoteBaixaDoArquivo> getArquivosNaoProcessados() {
        return arquivosNaoProcessados;
    }

    public List<LoteBaixaDoArquivo> getArquivosProcessados() {
        return arquivosProcessados;
    }

    public List<ArquivoLoteBaixa> getTodosArquivos() {
        return facade.listaTodosArquivos();
    }

    public List<FileUploadEvent> getFileUploadEvents() {
        return fileUploadEvents;
    }

    public void processaArquivo(ActionEvent evento) {
        arquivoLoteBaixa = (ArquivoLoteBaixa) evento.getComponent().getAttributes().get("objeto");
        try {
            arquivoLoteBaixa = facade.recuperarArquivo(arquivoLoteBaixa);
            arquivoLoteBaixa.setTransientBanco(bancoFacade.retornaBancoPorNumero(arquivoLoteBaixa.getLotes().get(0).getCodigoBanco()));
            if (arquivoLoteBaixa.getLotes().get(0) == null) {
                FacesUtil.addError("Não foi possível continuar!", "Nenhum Banco foi encontrado com o código informado no arquivo (" + arquivoLoteBaixa.getLotes().get(0).getCodigoBanco() + ").");
            } else {
                RequestContext.getCurrentInstance().execute("dialogoLancaLote.show()");
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.executaJavaScript("dialogoLancaTodosLotes.hide();");
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao importar arquivo ", e);
            FacesUtil.addError("Impossível continuar!", "Algo inesperado aconteceu ao gerar o arquivo de baixa, verifique se o arquivo de retorno bancário está correto antes de continuar.");
        }
    }

    public void excluirArquivo(ActionEvent evento) {
        ArquivoLoteBaixa arquivo = (ArquivoLoteBaixa) evento.getComponent().getAttributes().get("objeto");
        try {
            arquivo = facade.recuperarArquivo(arquivo);
            for (LoteBaixaDoArquivo loteBaixaDoArquivo : arquivo.getLotes()) {
                arquivosAProcessar.remove(loteBaixaDoArquivo);
            }
            facade.removerArquivo(arquivo);
            FacesUtil.addInfo("Arquivo removido com sucesso!", "");
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível continuar!", e.getMessage());
        }
    }

    public void excluiParcela(ActionEvent evento) {
        itemLoteBaixa = (ItemLoteBaixa) evento.getComponent().getAttributes().get("objeto");
        selecionado.getItemLoteBaixa().remove(itemLoteBaixa);
        itemLoteBaixa = new ItemLoteBaixa();
    }

    public boolean habilitaPesquisaDebitos() {
        if (selecionado == null || selecionado.getTipoDePagamentoBaixa() == null) {
            return false;
        }
        return selecionado.getTipoDePagamentoBaixa().equals(TipoDePagamentoBaixa.BAIXA_MANUAL);
    }

    public void gerarRelatorio(String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
        JasperPrint jasperPrint = JasperFillManager.fillReport(scontext.getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, jrbc);
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        exporter.exportReport();
        byte[] bytes = dadosByte.toByteArray();
        if (bytes != null && bytes.length > 0) {
            int recorte = arquivoJasper.indexOf(".");
            String nomePDF = arquivoJasper.substring(0, recorte);
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline; filename=\"" + nomePDF + ".pdf\"");
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
        }
    }

    public String getCaminhoImagem() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String caminho = ((ServletContext) facesContext.getExternalContext().getContext()).getRealPath("/img/");
        caminho += "/";
        return caminho;
    }


    public void consultaAndamentoEstorno() {
        if (futurePagamento != null && futurePagamento.isDone()) {
            if (loteAntigo != null) {
                if (!facade.getArrecadacaoFacade().lotePossuiValorValorOriginalTributoMenorQueDesconto(selecionado.getId())) {
                    facade.getArrecadacaoFacade().gerarIntegracao(loteAntigo, TipoIntegracao.ESTORNO_ARRECADACAO);
                }
            }
            FacesUtil.executaJavaScript("terminaPagamento()");
        }
    }

    public void executarDependenciasEstorno() {
        AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
                "Executando dependências pós estorno do lote de arrecadação ID [" + selecionado.getId() + "].", 0),
            () -> {
                facade.getArrecadacaoFacade().executarDependenciasEstorno(loteAntigo);
                return null;
            });
    }

    public void estornarLote(boolean criaNovoLote) {
        try {
            validarFase();
            loteAntigo = facade.estornarLoteAntigo(selecionado);
            if (criaNovoLote) {
                selecionado = facade.criarNovoLoteDepoisEstorno(loteAntigo);
            }
            UsuarioSistema usuario = sistemaFacade.getUsuarioCorrente();
            assistenteArrecadacao = new ArrecadacaoFacade.AssistenteArrecadacao(usuario, selecionado, selecionado.getItemLoteBaixa().size());
            futurePagamento = AsyncExecutor.getInstance().execute(assistenteArrecadacao,
                () -> facade.getArrecadacaoFacade().estornarPagamento(assistenteArrecadacao, usuario));
            FacesUtil.executaJavaScript("iniciaPagamento()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("validacaoFase()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao processar as informações!", "Ocorreu um erro durante o estorno do lote: " + e.getMessage());
        }
    }

    private void validarFase() {
        List<UnidadeOrganizacional> unidadesOrcamentarias = facade.buscarUnidadesOrcamentariasPorLote(selecionado);
        if (!unidadesOrcamentarias.isEmpty()) {
            for (UnidadeOrganizacional unidadeOrcamentaria : unidadesOrcamentarias) {
                validarFasePorUnidade(unidadeOrcamentaria);
            }
        }
    }

    private void validarFasePorUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        ValidacaoException ve = new ValidacaoException();
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(selecionado.getDataPagamento()));
        if (facade.getFaseFacade().temBloqueioFaseParaRecurso("/tributario/arrecadacao/baixa/visualizar.xhtml", selecionado.getDataPagamento(), unidadeOrganizacional, exercicio)) {
            HierarquiaOrganizacional hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataPagamento(), unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
            ve.adicionarMensagemDeOperacaoNaoRealizada("A data " + DataUtil.getDataFormatada(selecionado.getDataPagamento()) + " está fora do período fase para a unidade " + hierarquiaOrganizacional.toString() + "!");
        }
        ve.lancarException();
    }

    public SituacaoLoteBaixa getSituacaoBaixado() {
        return SituacaoLoteBaixa.BAIXADO;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public String retornaData(Date data) {
        if (data != null) {
            return new SimpleDateFormat("dd/MM/yyyy").format(data);
        } else {
            return "";
        }
    }

    public String buscarInscricaoDoCadastro(ParcelaValorDivida parcela) {
        return consultaDebitoFacade.buscarNumeroTipoCadastroPorParcela(parcela);
    }

    public String retornaSituaçãoDaDivida(ParcelaValorDivida parcela) {
        if (parcela == null) {
            return " - ";
        }
        if (parcela.isDividaAtiva()) {
            return "DÍVIDA ATIVA";
        }
        if (parcela.isDividaAtivaAjuizada()) {
            return "DÍVIDA ATIVA AJUIZADA";
        }
        return "DO EXERCÍCIO";
    }

    public String recuperaUltimaSituação(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = consultaDebitoFacade.getUltimaSituacao(parcela);
        return ultimaSituacao.getSituacaoParcela().getDescricao();
    }

    public BigDecimal recuperaSaldoUltimaSituação(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = consultaDebitoFacade.getUltimaSituacao(parcela);
        if (ultimaSituacao != null) {
            return ultimaSituacao.getSaldo();
        }
        return BigDecimal.ZERO;
    }

    public SelectItem[] retornaTipoDeSituacaoDaDivida() {
        SelectItem retorno[] = new SelectItem[4];
        retorno[0] = new SelectItem("", "TODOS");
        retorno[1] = new SelectItem("DÍVIDA ATIVA", "DÍVIDA ATIVA");
        retorno[2] = new SelectItem("DÍVIDA ATIVA AJUIZADA", "DÍVIDA ATIVA AJUIZADA");
        retorno[3] = new SelectItem("DO EXERCÍCIO", "DO EXERCÍCIO");
        return retorno;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        if (cadastro != null) {
            return cadastroEconomicoFacade.recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) cadastro);
        } else {
            return new SituacaoCadastroEconomico();
        }
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getFormasPagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (LoteBaixa.FormaPagamento forma : LoteBaixa.FormaPagamento.values()) {
            toReturn.add(new SelectItem(forma, forma.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "consultaParcelaArrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void consultaParcelas() {
        selecionado = (LoteBaixa) Web.pegaDaSessao(LoteBaixa.class);
        inicializaFiltros();
    }

    public void inicializaFiltros() {
        filtroCodigoBarras = null;
        filtroContribuinte = null;
        filtroNumeroDAM = null;
        tipoCadastroTributario = null;
        cadastro = null;
        dams = Lists.newArrayList();
    }

    public void navegarParaConsulta() {
        try {
            validarPesquisaDebitos();
            Web.poeNaSessao(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "consulta/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    private void validarPesquisaDebitos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFormaPagamento() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Antes de pesquisar os Débitos é necessário informar a Forma de Pagamento");
        }
        ve.lancarException();
    }

    public void navegaParaEdita() {
        Web.poeNaSessao(selecionado);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/");
    }

    public Pessoa getFiltroContribuinte() {
        return filtroContribuinte;
    }

    public void setFiltroContribuinte(Pessoa filtroContribuinte) {
        this.filtroContribuinte = filtroContribuinte;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public void abrePesquisa() {
        habilitaPesquisa = true;
    }

    public Boolean getHabilitaPesquisa() {
        return habilitaPesquisa;
    }

    public void setHabilitaPesquisa(Boolean habilitaPesquisa) {
        this.habilitaPesquisa = habilitaPesquisa;
    }

    public void adicionarItem(DAM dam) {
        itemLoteBaixa = new ItemLoteBaixa();
        itemLoteBaixa.setValorPago(dam.getValorTotal());
        if (pagamentoCartao != null) {
            itemLoteBaixa.setPagamentoCartao(pagamentoCartao);
            pagamentoCartao.setItemLoteBaixa(itemLoteBaixa);
            itemLoteBaixa.setValorPago(pagamentoCartao.getValorPago());
        }
        itemLoteBaixa.setDam(dam);
        itemLoteBaixa.setCodigoBarrasInformado(dam.getCodigoBarras());
        itemLoteBaixa.setDamInformado(dam.getNumeroDAM());
        for (ItemDAM itemDam : dam.getItens()) {
            ItemLoteBaixaParcela it = new ItemLoteBaixaParcela();

            BigDecimal valorProporcional = (itemDam.getValorTotal().multiply(CEM)).divide(itemLoteBaixa.getValorPago(), 6, RoundingMode.HALF_UP);
            valorProporcional = valorProporcional.divide(CEM, 6, RoundingMode.HALF_UP);
            valorProporcional = valorProporcional.multiply(itemLoteBaixa.getValorPago());
            it.setValorPago(valorProporcional);

            it.setItemDam(itemDam);
            it.setItemLoteBaixa(itemLoteBaixa);
            itemLoteBaixa.getItemParcelas().add(it);
            itemLoteBaixa.setDataPagamento(sistemaFacade.getDataOperacao());
        }
        itemLoteBaixa.setLoteBaixa(selecionado);
        selecionado.getItemLoteBaixa().add(itemLoteBaixa);
    }

    public void proporcionalizarValorDoItemLoteBaixaParcela(ItemLoteBaixa itemLoteBaixa) {
        for (ItemDAM itemDam : itemLoteBaixa.getDam().getItens()) {
            for (ItemLoteBaixaParcela itemParcela : itemLoteBaixa.getItemParcelas()) {
                if (itemDam.equals(itemParcela.getItemDam())) {
                    BigDecimal comparador = itemLoteBaixa.getValorPago().add(itemLoteBaixa.getValorDiferenca().abs());
                    BigDecimal valorProporcional = (itemDam.getValorTotal().multiply(CEM)).divide(comparador, 6, RoundingMode.HALF_UP);
                    valorProporcional = valorProporcional.divide(CEM, 6, RoundingMode.HALF_UP);
                    valorProporcional = valorProporcional.multiply(itemLoteBaixa.getValorPago());
                    itemParcela.setValorPago(valorProporcional);
                }
            }
        }
    }

    public void processaTodosArquivos() {
        try {
            recuperarContaDoAgrupador();
            validarConfiguracaoTributaria();
            validarSeTemArquivosAProcessar();
            FacesUtil.executaJavaScript("dialogoLancaTodosLotes.hide()");
            FacesUtil.executaJavaScript("iniciaPagamento()");
            if (validaProcessaTodos()) {
                salvaTodosArquivos();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("dialogPagamento.hide(); dialogoLancaTodosLotes.hide(); aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao importar arquivo ", e);
            FacesUtil.addError("Impossível continuar!", "Algo inesperado aconteceu ao gerar o arquivo de baixa, verifique se o arquivo de retorno bancário está correto antes de continuar.");
        }
    }

    private void recuperarContaDoAgrupador() {
        for (int i = 0; i < arquivosAProcessarSelecionados.length; i++) {
            LoteBaixaDoArquivo arq = arquivosAProcessarSelecionados[i];
            arq.getArquivoLoteBaixa()
                .setConta(agrupadoresDeConta.get(arq.getBanco()));
            arq.getArquivoLoteBaixa().setTransientBanco(arq.getArquivoLoteBaixa().getConta().getSubConta().getContaBancariaEntidade().getAgencia().getBanco());
            arq.getArquivoLoteBaixa().setTransientSubConta(arq.getArquivoLoteBaixa().getConta().getSubConta());
        }
    }


    private void validarSeTemArquivosAProcessar() {
        ValidacaoException ve = new ValidacaoException();
        if (arquivosAProcessarSelecionados == null || arquivosAProcessarSelecionados.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe arquivo para ser processado.");
        }
        ve.lancarException();
    }

    public void salvaArquivo() {
        if (arquivoLoteBaixa.getTransientBanco() != null && arquivoLoteBaixa.getTransientSubConta() != null) {
            try {
                validarConfiguracaoTributaria();
                processoArquivo = new LoteBaixaFacade.ProcessaArquivo(sistemaFacade.getUsuarioCorrente(),
                    sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioBancoDeDados());
                processoArquivo.addArquivo(arquivoLoteBaixa);
                futureProcesso = new ArquivoArrecadacaoExecutor(facade, bancoFacade, configuracaoTributarioFacade)
                    .execute(processoArquivo);
                FacesUtil.executaJavaScript("aguarde.hide(); dialogoLancaLote.hide(); iniciaPagamento();");
            } catch (ValidacaoException ve) {
                FacesUtil.executaJavaScript("aguarde.hide(); dialogoLancaLote.hide(); dialogPagamento.hide();");
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception e) {
                logger.error("Erro ao salvaArquivo: {}", e);
                FacesUtil.addError("Não foi possível continuar!", e.getMessage());
            }
        } else {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "Informe o Banco e a Conta Bancária antes de continuar.");
        }
    }


    public void salvaTodosArquivos() {
        try {
            processoArquivo = new LoteBaixaFacade.ProcessaArquivo(sistemaFacade.getUsuarioCorrente(),
                sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioBancoDeDados());
            for (LoteBaixaDoArquivo loteBaixaDoArquivo : arquivosAProcessarSelecionados) {
                arquivoLoteBaixa = facade.recuperarArquivo(loteBaixaDoArquivo.getArquivoLoteBaixa());
                arquivoLoteBaixa.setTransientBanco(loteBaixaDoArquivo.getArquivoLoteBaixa().getTransientBanco());
                arquivoLoteBaixa.setTransientSubConta(loteBaixaDoArquivo.getArquivoLoteBaixa().getTransientSubConta());
                processoArquivo.getArquivos().add(arquivoLoteBaixa);
            }
            futureProcesso = new ArquivoArrecadacaoExecutor(facade,
                bancoFacade, configuracaoTributarioFacade).execute(processoArquivo);
        } catch (Exception e) {
            logger.error("Erro ao salvaTodosArquivos: {}", e);
            FacesUtil.addError("Não foi possível continuar!", e.getMessage());
        }

    }

    public void imprime() throws JRException, IOException {
        new ImprimeMapaArrecadacaoMovimentoFinanceiro(selecionado.getBanco(),
            selecionado.getDataPagamento(),
            selecionado.getDataPagamento(),
            sistemaFacade.getLogin()).imprime();
    }

    public List<SelectItem> getContas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        Collections.sort(bancosContaConfTributario, (o1, o2) -> o1.toString().compareTo(o2.toString()));
        for (BancoContaConfTributario object : bancosContaConfTributario) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getContasPorBanco() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        if (arquivoLoteBaixa.getTransientBanco() != null) {
            List<BancoContaConfTributario> lista = bancosContaConfTributario.stream().filter(b -> {
                if (b.getSubConta() != null &&
                    b.getSubConta().getContaBancariaEntidade() != null &&
                    b.getSubConta().getContaBancariaEntidade().getAgencia() != null &&
                    b.getSubConta().getContaBancariaEntidade().getAgencia().getBanco() != null) {
                    return b.getSubConta().getContaBancariaEntidade().getAgencia().getBanco().equals(arquivoLoteBaixa.getTransientBanco());
                }
                return false;
            }).collect(Collectors.toList());
            Collections.sort(lista, (o1, o2) -> o1.toString().compareTo(o2.toString()));
            for (BancoContaConfTributario object : lista) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getContasPorBanco(String banco) {
        return contasPorBanco.get(banco);
    }

    public void selecionaContaArquivo() {
        if (arquivoLoteBaixa.getConta() != null && arquivoLoteBaixa.getConta().getSubConta().getContaBancariaEntidade() != null && arquivoLoteBaixa.getConta().getSubConta().getContaBancariaEntidade().getAgencia() != null && arquivoLoteBaixa.getConta().getSubConta().getContaBancariaEntidade().getAgencia().getBanco() != null) {
            arquivoLoteBaixa.setTransientBanco(arquivoLoteBaixa.getConta().getSubConta().getContaBancariaEntidade().getAgencia().getBanco());
            arquivoLoteBaixa.setTransientSubConta(arquivoLoteBaixa.getConta().getSubConta());
        } else {
            arquivoLoteBaixa.setTransientBanco(null);
            arquivoLoteBaixa.setTransientSubConta(null);
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "A conta selecionada não está relacionada a um banco.");
        }
    }

    public void selecionaContaArquivoAProcessar(ArquivoLoteBaixa alb) {
        if (alb.getConta() != null &&
            alb.getConta().getSubConta() != null &&
            alb.getConta().getSubConta().getContaBancariaEntidade() != null &&
            alb.getConta().getSubConta().getContaBancariaEntidade().getAgencia() != null &&
            alb.getConta().getSubConta().getContaBancariaEntidade().getAgencia().getBanco() != null) {

            alb.setTransientBanco(alb.getConta().getSubConta().getContaBancariaEntidade().getAgencia().getBanco());
            alb.setTransientSubConta(alb.getConta().getSubConta());
        } else {
            alb.setTransientBanco(null);
            alb.setTransientSubConta(null);
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "A conta selecionada não está relacionada a um banco.");
        }
    }

    public void selecionaConta() {
        if (arquivoLoteBaixa.getConta() != null && arquivoLoteBaixa.getConta().getSubConta().getContaBancariaEntidade() != null && arquivoLoteBaixa.getConta().getSubConta().getContaBancariaEntidade().getAgencia() != null && arquivoLoteBaixa.getConta().getSubConta().getContaBancariaEntidade().getAgencia().getBanco() != null) {
            selecionado.setBanco(arquivoLoteBaixa.getConta().getSubConta().getContaBancariaEntidade().getAgencia().getBanco());
            selecionado.setSubConta(arquivoLoteBaixa.getConta().getSubConta());
        } else {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "A conta selecionada não está relacionada a um banco.");
        }
    }

    public void atualizaCampos(int index) {
        FacesUtil.atualizarComponente("Formulario:tabelaParcelas:totalLancado");
        FacesUtil.atualizarComponente("Formulario:tabelaParcelas:totalDiferenca");
        FacesUtil.atualizarComponente("Formulario:tabelaParcelas:" + index + ":diferenca");
        FacesUtil.atualizarComponente("Formulario:diferencaLote");
    }

    public void consisteLote(LoteBaixa lote) {
        selecionado = lote;
        consisteSelecionado();
    }

    public void consisteSelecionado() {
        selecionado.getInconsistencias().clear();
        selecionado = facade.consisteLote(selecionado);
        corrigirDAMs();
        if (validaCampos()) {
            if (!selecionado.isConsistente()) {
                FacesUtil.addWarn("Atenção", "'Foram encontradas " + selecionado.getInconsistencias().size() + " inconsistências no lote. Por favor, pressione o botão Imprimir Inconsistências para verificar");
                selecionado.setSituacaoLoteBaixa(SituacaoLoteBaixa.INCONSISTENTE);
            } else {
                FacesUtil.addInfo("Consistido com Sucesso", "Não foram encontradas inconsistências no lote. Você já pode baixar a arrecadação.");
                selecionado.setSituacaoLoteBaixa(SituacaoLoteBaixa.CONSISTENTE);
            }
        }
    }

    public void montaRelatorioInconsistencias() {
        Date hoje = new Date();

        StringBuilder listItens = new StringBuilder("");
        StringBuilder listLote = new StringBuilder("");

        for (InconsistenciaArrecadacao inconsistencia : selecionado.getInconsistencias()) {
            if (inconsistencia.getItemLoteBaixa() != null) {
                listItens.append("<tr>");
                listItens.append(inconsistencia.getDescricaoCompletaParaTable());
                listItens.append("</tr>");
            } else {
                listLote.append("<li>");
                listLote.append(inconsistencia.getDescricaoCompletaParaTable());
                listLote.append("</li>");
            }
        }

        StringBuilder conteudo = new StringBuilder("");
        conteudo.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
        conteudo.append(" <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        conteudo.append(" <html>");
        conteudo.append(" <head>");
        conteudo.append(" <style type=\"text/css\"  media=\"all\">");
        conteudo.append(" @page{");
        conteudo.append(" size: A4 landscape; ");
        conteudo.append(" margin-top: 1.0in;");
        conteudo.append(" margin-bottom: 1.0in;");
        conteudo.append(" @bottom-center {");
        conteudo.append(" content: element(footer);");
        conteudo.append(" }");
        conteudo.append(" @top-center {");
        conteudo.append(" content: element(header);");
        conteudo.append(" }");
        conteudo.append("}");
        conteudo.append("#page-header {");
        conteudo.append(" display: block;");
        conteudo.append(" position: running(header);");
        conteudo.append(" }");
        conteudo.append(" #page-footer {");
        conteudo.append(" display: block;");
        conteudo.append(" position: running(footer);");
        conteudo.append(" }");
        conteudo.append(" .page-number:before {");
        conteudo.append("  content: counter(page) ");
        conteudo.append(" }");
        conteudo.append(" .page-count:before {");
        conteudo.append(" content: counter(pages);  ");
        conteudo.append("}");
        conteudo.append("</style>");
        conteudo.append(" <style type=\"text/css\">");
        conteudo.append(".igualDataTable{");
        conteudo.append("    border-collapse: collapse; /* CSS2 */");
        conteudo.append("    width: 100%;");
        conteudo.append("    ;");
        conteudo.append("}");
        conteudo.append(".igualDataTable th{");
        conteudo.append("    border: 0px solid #aaaaaa; ");
        conteudo.append("    height: 20px;");
        conteudo.append("    background: #ebebeb 50% 50% repeat-x;");
        conteudo.append("}");
        conteudo.append(".igualDataTable td{");
        conteudo.append("    padding: 2px;");
        conteudo.append("    border: 0px; ");
        conteudo.append("    height: 20px;");
        conteudo.append("}");
        conteudo.append(".igualDataTable thead td{");
        conteudo.append("    border: 1px solid #aaaaaa; ");
        conteudo.append("    background: #6E95A6 repeat-x scroll 50% 50%; ");
        conteudo.append("    border: 0px; ");
        conteudo.append("    text-align: center; ");
        conteudo.append("    height: 20px;");
        conteudo.append("}");
        conteudo.append(" .igualDataTable tr:nth-child(2n+1) {");
        conteudo.append(" background:lightgray;");
        conteudo.append(" }");
        conteudo.append("body{");
        conteudo.append("font-size: 8pt; font-family:\"Arial\", Helvetica, sans-serif");
        conteudo.append("}");
        conteudo.append("");
        conteudo.append("</style>");
        conteudo.append(" <title>");
        conteudo.append(" < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">");
        conteudo.append(" </title>");
        conteudo.append(" </head>");
        conteudo.append(" <body>");
        conteudo.append(" <div id=\"page-header\">");
        conteudo.append(" <table style='line-height:15%; width: 100%;'>");
        conteudo.append("<tr>");
        conteudo.append("<td >");
        conteudo.append(adicionaCabecalho());
        conteudo.append("</td >");
        conteudo.append("<td style='text-align: right'>");
        conteudo.append("<p>Referência: <b> ");
        conteudo.append(Util.dateToString(selecionado.getDataPagamento())).append("</b></p>");
        conteudo.append("</td>");
        conteudo.append("</tr>");
        conteudo.append(" </table>");
        conteudo.append(" <hr noshade/>");
        conteudo.append(" </div>");
        conteudo.append(" <div id=\"page-footer\">");
        conteudo.append(" <hr noshade/>");
        conteudo.append(" <table style='line-height:15%; width: 100%;'>");
        conteudo.append("<tr>");
        conteudo.append("<td >");
        conteudo.append("<p>");
        conteudo.append("WebPúblico - Módulo: TRIBUTÁRIO");
        conteudo.append("</p>");
        conteudo.append("</td>");
        conteudo.append("<td style='text-align: right'>");
        conteudo.append("<p>");
        conteudo.append("Usuário: ");
        conteudo.append(sistemaFacade.getLogin());
        conteudo.append(" - Emitido em ");
        conteudo.append(Util.formatterDiaMesAno.format(hoje));
        conteudo.append(" às ").append(Util.formatterHoraMinuto.format(hoje));
        conteudo.append(" - Página: <span class=\"page-number\"/> de <span class=\"page-count\"/>");
        conteudo.append("</p>");
        conteudo.append("</td>");
        conteudo.append("</tr>");
        conteudo.append(" </table>");
        conteudo.append(" </div>");
        conteudo.append(" <div id=\"page-content\">");
        conteudo.append(" <br/>");
        conteudo.append("<p><b>Informações do Lote</b></p>");
        montarDescricaoLote(conteudo);
        conteudo.append(" <hr noshade/>");
        if (!listLote.toString().isEmpty()) {
            conteudo.append("<p><b>Inconsistências do Lote</b></p>");
            conteudo.append("<ul>");
            conteudo.append(listLote);
            conteudo.append("</ul>");
            conteudo.append("<br/>");
            conteudo.append(" <hr noshade/>");
        }
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<p><b>Inconsistências dos DAMs</b></p>");
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(InconsistenciaArrecadacao.getHeaderTable());
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("</div>");

        conteudo.append(" </body> </html>");
        Util.geraPDF("Inconsistencias do Lote", conteudo.toString(), FacesContext.getCurrentInstance());
    }

    private void montarDescricaoLote(StringBuilder conteudo) {
        conteudo.append("<table>");
        if (selecionado.getCodigoLote() != null) {
            conteudo.append("<tr>");
            conteudo.append("<td>");
            conteudo.append("Número do Lote:");
            conteudo.append("</td>");
            conteudo.append("<td>");
            conteudo.append(selecionado.getCodigoLote());
            conteudo.append("</td>");
            conteudo.append("</tr>");
        }
        if (selecionado.getTipoDePagamentoBaixa() != null) {
            conteudo.append("<tr>");
            conteudo.append("<td>");
            conteudo.append("Tipo de Pagamento:");
            conteudo.append("</td>");
            conteudo.append("<td>");
            conteudo.append(selecionado.getTipoDePagamentoBaixa().getDescricao());
            conteudo.append("</td>");
            conteudo.append("</tr>");
        }
        if (selecionado.getDataPagamento() != null) {
            conteudo.append("<tr>");
            conteudo.append("<td>");
            conteudo.append("Data do Pagamento:");
            conteudo.append("</td>");
            conteudo.append("<td>");
            conteudo.append(Util.formatterDiaMesAno.format(selecionado.getDataPagamento()));
            conteudo.append("</td>");
            conteudo.append("</tr>");
        }
        if (arquivoLoteBaixa.getConta() != null) {
            conteudo.append("<tr>");
            conteudo.append("<td>");
            conteudo.append("Banco/Conta:");
            conteudo.append("</td>");
            conteudo.append("<td>");
            conteudo.append(arquivoLoteBaixa.getConta().toString());
            conteudo.append("</td>");
            conteudo.append("</tr>");
        }
        if (selecionado.getFormaPagamento() != null) {
            conteudo.append("<tr>");
            conteudo.append("<td>");
            conteudo.append("Forma de Pagamento:");
            conteudo.append("</td>");
            conteudo.append("<td>");
            conteudo.append(selecionado.getFormaPagamento().getDescricao());
            conteudo.append("</td>");
            conteudo.append("</tr>");
        }
        conteudo.append("</table>");
    }

    public void imprimeRelatorioPagamentoPorLote() {
        imprimeRelatoriosArrecadacao.imprimePagamentoPorLote(selecionado);
    }

    public void imprimeRelatorioPagamentoPorLoteDetalhado() {
        imprimeRelatoriosArrecadacao.imprimePagamentoPorLoteDetalhado(selecionado);
    }

    public void imprimeMapaArrecadacao() {
        imprimeRelatoriosArrecadacao.imprimeMapaArrecadacao(selecionado);
    }

    public List<DAM> getDams() {
        return dams;
    }

    public String adicionaCabecalho() {
        String caminhoDaImagem = documentoOficialFacade.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";
        String secretaria = "Secretaria Municipal de Finanças";

        String conteudo =
            "<table>"
                + "<tbody>"
                + "<tr>"
                + "<td style=\"text-align: left;\" align=\"right\"><img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /></td>"
                + "<td style=\"line-height:100%; margin-left: 50px;\">"
                + "<h2> PREFEITURA MUNICIPAL DE RIO BRANCO</h2>"
                + " <h3> Secretaria de Finanças </h3>"
                + " <h3> Relatório de Inconsistências no Lote </h3>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>";
        return conteudo;
    }

    public void retornaEdita() {
        Web.poeNaSessao(selecionado);
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Long getNumeroArquivo() {
        return numeroArquivo;
    }

    public void setNumeroArquivo(Long numeroArquivo) {
        this.numeroArquivo = numeroArquivo;
    }

    public LoteBaixaFacade.ProcessaArquivo getProcessoArquivo() {
        return processoArquivo;
    }

    public Date getDataPagamentoInicial() {
        return dataPagamentoInicial;
    }

    public void setDataPagamentoInicial(Date dataPagamentoInicial) {
        this.dataPagamentoInicial = dataPagamentoInicial;
    }

    public Date getDataPagamentoFinal() {
        return dataPagamentoFinal;
    }

    public void setDataPagamentoFinal(Date dataPagamentoFinal) {
        this.dataPagamentoFinal = dataPagamentoFinal;
    }

    public Date getDataMovimentoInicial() {
        return dataMovimentoInicial;
    }

    public void setDataMovimentoInicial(Date dataMovimentoInicial) {
        this.dataMovimentoInicial = dataMovimentoInicial;
    }

    public Date getDataMovimentoFinal() {
        return dataMovimentoFinal;
    }

    public void setDataMovimentoFinal(Date dataMovimentoFinal) {
        this.dataMovimentoFinal = dataMovimentoFinal;
    }

    public SituacaoLoteBaixa getSituacaoLoteBaixa() {
        return situacaoLoteBaixa;
    }

    public void setSituacaoLoteBaixa(SituacaoLoteBaixa situacaoLoteBaixa) {
        this.situacaoLoteBaixa = situacaoLoteBaixa;
    }

    public Long getQuantidadeFiltroArquivos() {
        return quantidadeFiltroArquivos;
    }

    public void consultaProcessoUnico() {
        if (futureProcesso != null && futureProcesso.isDone()) {
            try {
                LoteBaixaFacade.ProcessaArquivo processaArquivo = futureProcesso.get();
                futureProcesso = null;
                FacesUtil.executaJavaScript("terminaPagamento();");
                AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
                    "Regularizando cadastros do daf", 0), () -> facade.regularizarCadastrosDoDaf(processaArquivo));
            } catch (Exception e) {
                FacesUtil.executaJavaScript("terminaPagamento();");
                logger.error("Erro ao processar o arquivo", e);
            }
        }
    }

    public void consultaEstorno() {
        boolean terminou = false;
        if (futureProcesso != null && futureProcesso.isDone() && !terminou) {
            FacesUtil.executaJavaScript("terminaEstorno();");
            arquivoLoteBaixa = facade.recuperarArquivo(arquivoLoteBaixa);
            for (LoteBaixaDoArquivo loteBaixaDoArquivo : arquivoLoteBaixa.getLotes()) {
                if (!facade.getArrecadacaoFacade().lotePossuiValorValorOriginalTributoMenorQueDesconto(selecionado.getId())) {
                    facade.getArrecadacaoFacade().gerarIntegracao(loteBaixaDoArquivo.getLoteBaixa(), TipoIntegracao.ESTORNO_ARRECADACAO);
                }
            }
            facade.geraNovoArquivo(arquivoLoteBaixa);
            FacesUtil.redirecionamentoInterno("/tributario/arrecadacao/arquivos/");
        }
    }

    public Double getPorcentagem() {
        try {
            return processoArquivo.getPorcentagemDoCalculo();
        } catch (Exception e) {
            return 0d;
        }
    }


    public void defineNumeroPesquisa(Long max) {
        quantidadeFiltroArquivos = max;
        pesquisaArquivos();
    }

    public void pesquisaArquivos() {
        LoteBaixaFacade.ConsultaLoteBaixa consultaLoteBaixa = new LoteBaixaFacade.ConsultaLoteBaixa();
        consultaLoteBaixa.banco = arquivoLoteBaixa.getTransientBanco();
        consultaLoteBaixa.dataMovimentoFinal = dataMovimentoFinal;
        consultaLoteBaixa.dataMovimentoInicial = dataMovimentoInicial;
        consultaLoteBaixa.dataPagamentoInicial = dataPagamentoInicial;
        consultaLoteBaixa.dataPagamentoFinal = dataPagamentoFinal;
        consultaLoteBaixa.numeroArquivo = numeroArquivo;
        consultaLoteBaixa.situacaoLoteBaixa = situacaoLoteBaixa;
        consultaLoteBaixa.subConta = arquivoLoteBaixa.getTransientSubConta();
        arquivosProcessados = facade.listaArquivosProcessados(consultaLoteBaixa, quantidadeFiltroArquivos);
    }

    public boolean validaProcessaTodos() {
        for (LoteBaixaDoArquivo alb : arquivosAProcessarSelecionados) {
            if (alb.getArquivoLoteBaixa().getConta() == null) {
                FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), "Informe a conta de cada banco.");
                return false;
            }
        }
        return true;
    }


    public boolean getLoteNormal() {
        return !TipoDePagamentoBaixa.BAIXA_ARQUIVO_RETORNO.equals(selecionado.getTipoDePagamentoBaixa());
    }

    public boolean isHabilitarFormaPagamento() {
        return !TipoDePagamentoBaixa.BAIXA_ARQUIVO_RETORNO.equals(selecionado.getTipoDePagamentoBaixa())
            || !selecionado.getItemLoteBaixa().isEmpty();
    }

    public String getCaminhoPadrao() {
        return "/tributario/arrecadacao/";
    }

    public ConverterAutoComplete getLoteBaixaConverte() {
        if (loteBaixaConverte == null) {
            loteBaixaConverte = new ConverterAutoComplete(LoteBaixa.class, facade);
        }
        return loteBaixaConverte;
    }

    public void gerarImprimirMapaArrecadacao() {
        facade.gerarImprimirMapaArrecadacao(selecionado, null, facade.arquivoDoLote(selecionado));
    }

    public void verIntegracao() {
        FacesUtil.redirecionamentoInterno("/integracao-tributario-contabil/" + selecionado.getId() + "/");
    }

    public void reprocessarIntegracao() {
        if (verificaContasDoLote(selecionado)) {
            if (podeApagarIntegracao(selecionado)) {
                try {
                    if (!facade.getArrecadacaoFacade().lotePossuiValorValorOriginalTributoMenorQueDesconto(selecionado.getId())) {
                        facade.getArrecadacaoFacade().apagarIntegracaoECria(selecionado);
                        FacesUtil.addInfo("Sucesso!", "Reprocessamento concluído!");
                    } else {
                        FacesUtil.addAtencao(mensagemValoresInconsistentesTributos());
                    }
                } catch (Exception e) {
                    logger.error("Erro ao reprocessarIntegracao: {}", e);
                    FacesUtil.addOperacaoNaoPermitida("Erro Interno do Servidor! " + e.getMessage());
                }
            }
        }
    }

    private boolean podeApagarIntegracao(LoteBaixa loteBaixa) {
        boolean retorno = facade.podeApagarIntegracao(loteBaixa);
        if (!retorno) {
            FacesUtil.addOperacaoNaoPermitida("Já foi gerado uma Receita Realizada para esse lote! Não é permitido o reprocessamento!");
        }
        return retorno;
    }

    public boolean verificaContasDoLote(LoteBaixa loteBaixa) {
        List<Exception> exceptions = facade.verificaContasDoLote(loteBaixa);
        if (exceptions.isEmpty()) {
            return true;
        } else {
            for (Exception e : exceptions) {
                FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            }
            return false;
        }
    }

    public void reprocessaVariosLotes() {
        assistenteArrecadacao = new ArrecadacaoFacade.AssistenteArrecadacao(sistemaFacade.getUsuarioCorrente(),
            dataPagamentoInicial, dataPagamentoFinal);
        futurePagamento = AsyncExecutor.getInstance().execute(assistenteArrecadacao,
            () -> facade.getArrecadacaoFacade().reprocessaVariosLotes(assistenteArrecadacao));
    }

    public void consultaReprocessamento() {
        if (futurePagamento != null && futurePagamento.isDone()) {
            FacesUtil.executaJavaScript("terminaPagamento();");
        }
    }

    public void procurarDamNaoEncontradosInicialmente() {
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            DAM dam = facade.buscarDamPeloNumero(item.getDamInformado());
            item.setDam(dam);
        }
        selecionado = facade.salvarCriarItensParcelasCasoNaoExista(selecionado);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId());
    }

    public void setaItemLote(ItemLoteBaixa item) {
        itemLoteBaixa = item;
        filtroConsultaDAM = new FiltroConsultaDAM();
        dams = Lists.newArrayList();
    }

    public FiltroConsultaDAM getFiltroConsultaDAM() {
        return filtroConsultaDAM;
    }

    public void setFiltroConsultaDAM(FiltroConsultaDAM filtroConsultaDAM) {
        this.filtroConsultaDAM = filtroConsultaDAM;
    }

    public void pesquisaDam() {
        if (filtroConsultaDAM.getNumeroDAM() != null) {
            dams = consultaDAMFacade.listaDamPorFiltroETipoDeCadastroNumeroDAM(filtroConsultaDAM);
        }
    }

    public void setaDamAoItemLoteBaixa(DAM dam) {
        for (ItemLoteBaixa item : selecionado.getItemLoteBaixa()) {
            if (item.getId().equals(itemLoteBaixa.getId())) {
                item.setDam(dam);
                break;
            }
        }
    }

    public List<SelectItem> getBandeiraCartao() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (BandeiraCartao bandeiraCartao : facade.getBandeiraCartaoFacade().lista()) {
            retorno.add(new SelectItem(bandeiraCartao, bandeiraCartao.getDescricao()));
        }
        return retorno;
    }

    public void ajustarValoresDemaisParcelas(PagamentoCartaoItem pagamentoCartaoItem) {
        try {
            if (pagamentoCartaoItem.getParcela() == 1) {
                validarValorPrimeiraParcela(pagamentoCartaoItem);
                if ((pagamentoCartaoItem.getValor() != null && pagamentoCartaoItem.getValor().compareTo(BigDecimal.ZERO) >= 0)) {
                    BigDecimal total = pagamentoCartao.getValorPago();
                    BigDecimal primeiraParcela = pagamentoCartaoItem.getValor();
                    for (PagamentoCartaoItem cartaoItem : pagamentoCartao.getItem()) {
                        if (cartaoItem.getParcela() != 1) {
                            BigDecimal valorASerDivido = total.subtract(primeiraParcela);
                            BigDecimal valorParcela = valorASerDivido.divide(new BigDecimal(pagamentoCartao.getQuantidadeParcela() - 1), 2, BigDecimal.ROUND_HALF_EVEN);
                            BigDecimal somaParcelas = valorParcela.multiply(new BigDecimal(pagamentoCartao.getQuantidadeParcela() - 1));
                            BigDecimal segundaParcela = valorASerDivido.subtract(somaParcelas);
                            segundaParcela = segundaParcela.add(valorParcela);
                            if (cartaoItem.getParcela() == 2) {
                                cartaoItem.setValor(segundaParcela);
                            } else {
                                cartaoItem.setValor(valorParcela);
                            }
                        }
                    }
                }
            }
            calcularValorTotalPago();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarValorPrimeiraParcela(PagamentoCartaoItem pagamentoCartaoItem) {
        ValidacaoException ve = new ValidacaoException();
        if (pagamentoCartaoItem.getValor() != null && pagamentoCartaoItem.getValor().compareTo(pagamentoCartao.getValorPago()) > 0) {
            pagamentoCartaoItem.setValor(pagamentoCartao.getValorPago());
            ajustarValoresDemaisParcelas(pagamentoCartaoItem);
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da primeira parcela não pode ser maior que <b>" + Util.formataValor(pagamentoCartao.getValorPago()) + "</b>");
        }
        ve.lancarException();
    }

    private void calcularValorTotalPago() {
        BigDecimal valorPago = BigDecimal.ZERO;
        for (PagamentoCartaoItem cartaoItem : pagamentoCartao.getItem()) {
            if (cartaoItem.getValor() != null) {
                valorPago = valorPago.add(cartaoItem.getValor());
            }
        }
        pagamentoCartao.setValorPago(valorPago);
    }

    public void adicionarDamPagamentoCartao() {
        try {
            validarDamPagamentoCartao();
            adicionarDAM(dam);
            FacesUtil.executaJavaScript("dialogCartaoCredito.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void validarDamPagamentoCartao() {
        ValidacaoException ve = new ValidacaoException();
        if (pagamentoCartao.getBandeiraCartao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Bandeira do Cartão.");
        }
        if (pagamentoCartao.getValorPago() == null || pagamentoCartao.getValorPago().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O Valor Pago deve ser maior que Zero.");
        }
        if (pagamentoCartao.getQuantidadeParcela() == null || pagamentoCartao.getQuantidadeParcela() < 1) {
            ve.adicionarMensagemDeCampoObrigatorio("A Quantidade de Parcelas deve ser maior que Zero.");
        }
        for (PagamentoCartaoItem pagamentoCartaoItem : pagamentoCartao.getItem()) {
            if (pagamentoCartaoItem.getVencimento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Data de Vencimento deve ser informada para todas as parcelas.");
                break;
            }
            if (!DataUtil.isDataValida(pagamentoCartaoItem.getVencimento())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data <b>" + DataUtil.getDataFormatada(pagamentoCartaoItem.getVencimento()) + "</b> não é válida.");
                break;
            }
            if (pagamentoCartaoItem.getValor() == null || pagamentoCartaoItem.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor das parcelas devem ser maiores que Zero.");
                break;
            }
        }
        ve.lancarException();
    }


    public void recuperarInformacoesPagamentoCartao(ItemLoteBaixa itemLoteBaixa) {
        pagamentoCartao = itemLoteBaixa.getPagamentoCartao();
    }

    public boolean isFormaPagamentoCartaoCredito() {
        return LoteBaixa.FormaPagamento.CARTAO_CREDITO.equals(selecionado.getFormaPagamento());
    }

    public void redirecionarParaVisualizar() {
        if (processoArquivo != null
            && processoArquivo.getArquivos() != null
            && processoArquivo.getArquivos().size() == 1
            && processoArquivo.getArquivos().get(0).getLotes() != null
            && processoArquivo.getArquivos().get(0).getLotes().size() == 1) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/"
                + processoArquivo.getArquivos().get(0).getLotes().get(0).getLoteBaixa().getId());
        } else if (selecionado != null) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } else {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
        }
    }

    private void validarConfiguracaoTributaria() {
        for (LoteBaixaDoArquivo loteBaixaDoArquivo : arquivosAProcessarSelecionados) {
            if (loteBaixaDoArquivo.getArquivoLoteBaixa() != null && loteBaixaDoArquivo.getArquivoLoteBaixa().getTipoArquivoBancarioTributario().equals(TipoArquivoBancarioTributario.DAF607)) {
                ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
                if (configuracaoTributario != null && configuracaoTributario.getServicoNaoIndentificadoIss() == null) {
                    ValidacaoException ve = new ValidacaoException();
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum serviço cadastrado para Cadastro Econômico Não Identificado.");
                    ve.lancarException();
                }
            }
        }
    }

    public boolean isLoteProntoParaBaixar() {
        return SituacaoLoteBaixa.INCONSISTENTE.equals(selecionado.getSituacaoLoteBaixa())
            || SituacaoLoteBaixa.CONSISTENTE.equals(selecionado.getSituacaoLoteBaixa());
    }

    public boolean isLoteDAF607() {
        LoteBaixaDoArquivo loteBaixaDoArquivo = ((selecionado.getArquivosLoteBaixa() != null) && !selecionado.getArquivosLoteBaixa().isEmpty()) ? selecionado.getArquivosLoteBaixa().get(0) : null;
        if (loteBaixaDoArquivo == null) {
            return false;
        }
        return
            TipoArquivoBancarioTributario.DAF607.equals(loteBaixaDoArquivo.getArquivoLoteBaixa().getTipoArquivoBancarioTributario());
    }

    public boolean isLoteAbertoDAF607() {
        try {
            boolean aberto = SituacaoLoteBaixa.EM_ABERTO.equals(selecionado.getSituacaoLoteBaixa());
            LoteBaixaDoArquivo loteBaixaDoArquivo = selecionado.getArquivosLoteBaixa() != null &&
                !selecionado.getArquivosLoteBaixa().isEmpty() ? selecionado.getArquivosLoteBaixa().get(0) : null;
            if (loteBaixaDoArquivo == null) {
                return false;
            }
            return aberto && TipoArquivoBancarioTributario.DAF607.equals(loteBaixaDoArquivo.getArquivoLoteBaixa().getTipoArquivoBancarioTributario());
        } catch (Exception ex) {
            return false;
        }
    }

    public void corrigirDAMs() {
        List<DAM> dams = Lists.newArrayList();
        for (ItemLoteBaixa itemLoteBaixa : selecionado.getItemLoteBaixa()) {
            if (itemLoteBaixa.getDam() != null) {
                dams.add(itemLoteBaixa.getDam());
            }
        }

        for (DAM dam : dams) {
            DAM corrigir = damFacade.recuperar(dam.getId());
            if (corrigir != null) {
                facade.corrigirTributosDAM(corrigir);
            }
        }
    }

    public ConverterAutoComplete getConverterLoteBaixa() {
        if (converterLoteBaixa == null) {
            converterLoteBaixa = new ConverterAutoComplete(LoteBaixa.class, facade);
        }
        return converterLoteBaixa;
    }

    public void agruparContas() {
        contasPorBanco = Maps.newHashMap();
        if (arquivosAProcessarSelecionados == null || arquivosAProcessarSelecionados.length == 0) {
            FacesUtil.addOperacaoNaoPermitida("Selecione ao menos um arquivo para ser processado");
            return;
        }
        for (LoteBaixaDoArquivo arquivosAProcessarSelecionado : arquivosAProcessarSelecionados) {
            if (!contasPorBanco.containsKey(arquivosAProcessarSelecionado.getBanco())) {
                List<BancoContaConfTributario> lista = facade.buscarContasConfiguracaoPorBanco(arquivosAProcessarSelecionado.getCodigoBanco());
                contasPorBanco.put(arquivosAProcessarSelecionado.getBanco(), Util.getListSelectItem(lista));
            }
            if (!agrupadoresDeConta.containsKey(arquivosAProcessarSelecionado.getBanco())) {
                if (contasPorBanco.containsKey(arquivosAProcessarSelecionado.getBanco()) &&
                    !contasPorBanco.get(arquivosAProcessarSelecionado.getBanco()).isEmpty()) {
                    agrupadoresDeConta.put(arquivosAProcessarSelecionado.getBanco(), (BancoContaConfTributario) contasPorBanco.get(arquivosAProcessarSelecionado.getBanco()).get(0).getValue());
                } else {
                    agrupadoresDeConta.put(arquivosAProcessarSelecionado.getBanco(), null);
                }

            }
        }

    }

    public List<String> getBancosAgrupados() {
        if (agrupadoresDeConta == null) {
            agrupadoresDeConta = Maps.newHashMap();
        }
        return Lists.newArrayList(agrupadoresDeConta.keySet());
    }

    public Map<String, BancoContaConfTributario> getAgrupadoresDeConta() {
        return agrupadoresDeConta;
    }
}

