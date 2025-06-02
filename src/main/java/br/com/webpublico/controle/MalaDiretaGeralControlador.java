package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteImpressaoMalaDiretaGeral;
import br.com.webpublico.entidadesauxiliares.ContribuinteTributario;
import br.com.webpublico.entidadesauxiliares.FiltroMalaDiretaGeral;
import br.com.webpublico.entidadesauxiliares.ImpressaoMalaDiretaGeral;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.model.DefaultStreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by William on 07/06/2016.
 */
@ManagedBean(name = "malaDiretaGeralControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-mala-direta-geral",
        pattern = "/mala-direta/novo/",
        viewId = "/faces/tributario/maladiretageral/edita.xhtml"),
    @URLMapping(id = "listar-mala-direta-geral",
        pattern = "/mala-direta/listar/",
        viewId = "/faces/tributario/maladiretageral/lista.xhtml"),
    @URLMapping(id = "ver-mala-direta-por-cadastro-geral",
        pattern = "/mala-direta/ver-por-cadastro/#{malaDiretaGeralControlador.idCadastro}/",
        viewId = "/faces/tributario/maladiretageral/visualizar.xhtml"),
    @URLMapping(id = "ver-mala-direta-geral",
        pattern = "/mala-direta/ver/#{malaDiretaGeralControlador.id}/",
        viewId = "/faces/tributario/maladiretageral/visualizar.xhtml"),
    @URLMapping(id = "acompanhamento-mala-direta-geral",
        pattern = "/mala-direta/acompanhamento/#{malaDiretaGeralControlador.id}/",
        viewId = "/faces/tributario/maladiretageral/acompanhamento.xhtml")
})
public class MalaDiretaGeralControlador extends PrettyControlador<MalaDiretaGeral> implements CRUD {

    private static final Logger logger = LoggerFactory.getLogger(MalaDiretaGeralControlador.class);
    private final String ARQUIVODOWNLOAD = "MALADIRETA";
    private final String SESSAO = "ListaDeCadastrosParcelas";
    @EJB
    private MalaDiretaGeralFacade malaDiretaGeralFacade;
    @EJB
    private ParametroMalaDiretaFacade parametroMalaDiretaFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private PixFacade pixFacade;
    private AssistenteImpressaoMalaDiretaGeral assistenteImpressaoMalaDireta;
    private List<Future<List<ItemMalaDiretaGeral>>> futuresItemMalaDireta;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<ImpressaoMalaDiretaGeral> idsCadastros;

    private Integer quantidadeCadastrosDaMalaDireta;
    private FiltroMalaDiretaGeral filtro;
    private List<ItemMalaDiretaGeral> itensMalaDireta;

    private int quantidadeTotal = 0;
    private int quantidadePorPagina = 10;
    private int indexInicialDaPesquisa = 0;

    private ConfiguracaoTributario configuracaoTributario;
    private DefaultStreamedContent fileDownload;
    private List<CompletableFuture<AssistenteImpressaoMalaDiretaGeral>> listaFuturesGeracaoDam;
    private String pastaMalaDireta;
    private String email;
    private Long idCadastro;
    private ItemMalaDiretaGeral item;
    private Map<ContribuinteTributario, List<ResultadoParcela>> mapaCadastroParcelas;
    private List<CompletableFuture<Map<ContribuinteTributario, List<ResultadoParcela>>>> futureConsultaDebitosCadastro;
    private ImprimeDAM imprimeDAM = new ImprimeDAM();

    public MalaDiretaGeralControlador() {
        super(MalaDiretaGeral.class);
    }

    public DefaultStreamedContent fazerDownloadDoArquivo() {
        if (fileDownload != null) {
            return fileDownload;
        }
        return null;
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    public DefaultStreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(DefaultStreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return malaDiretaGeralFacade;
    }

    public boolean isFuturesCadastrosMalaDiretaIPTUConcluida() {
        if (futuresItemMalaDireta == null) {
            return false;
        }
        if (futuresItemMalaDireta.size() == 0) {
            return true;
        }

        for (Future<List<ItemMalaDiretaGeral>> futureDaVez : futuresItemMalaDireta) {
            if (!futureDaVez.isDone()) {
                return false;
            }
        }

        return true;
    }

    public void setFuturesCadastrosMalaDiretaIPTUConcluida(boolean futuresCadastrosMalaDiretaIPTUConcluida) {
        //apenas para o componente hiden da tela
    }

    public boolean isProcessoConcluido() {
        if (!isFuturesCadastrosMalaDiretaIPTUConcluida()) {
            return false;
        }
        return true;
    }

    public void setProcessoConcluido(boolean processoConcluido) {
        //apenas para o componente hiden da tela
    }


    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<ImpressaoMalaDiretaGeral> getIdsCadastros() {
        return idsCadastros;
    }


    public Integer getQuantidadeCadastrosDaMalaDireta() {
        return quantidadeCadastrosDaMalaDireta;
    }

    public FiltroMalaDiretaGeral getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroMalaDiretaGeral filtro) {
        this.filtro = filtro;
    }

    public List<ItemMalaDiretaGeral> getItensMalaDireta() {
        return itensMalaDireta;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(int quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public int getQuantidadePorPagina() {
        return quantidadePorPagina;
    }

    public void setQuantidadePorPagina(int quantidadePorPagina) {
        this.quantidadePorPagina = quantidadePorPagina;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        if (configuracaoTributario == null) {
            configuracaoTributario = malaDiretaGeralFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        }
        return configuracaoTributario;
    }

    public Map<ContribuinteTributario, List<ResultadoParcela>> getMapaCadastroParcelas() {
        return mapaCadastroParcelas;
    }

    public void setMapaCadastroParcelas(Map<ContribuinteTributario, List<ResultadoParcela>> mapaCadastroParcelas) {
        this.mapaCadastroParcelas = mapaCadastroParcelas;
    }

    public List<SelectItem> getTiposMala() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoMalaDireta object : TipoMalaDireta.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.getTodosSemRural());
    }

    public String getDetalhesTipoMala() {
        StringBuilder sb = new StringBuilder("Utilização do Tipo da Mala Direta!");
        for (TipoMalaDireta tipoMalaDireta : TipoMalaDireta.values()) {
            sb.append("<br/>")
                .append("<strong>")
                .append(tipoMalaDireta.getDescricao())
                .append(": </strong>")
                .append(tipoMalaDireta.getUtilizacao());
        }
        return sb.toString();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/mala-direta/";
    }

    @URLAction(mappingId = "nova-mala-direta-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        filtro = new FiltroMalaDiretaGeral();
        selecionado.setTipoMalaDireta(TipoMalaDireta.NOTIFICACAO);
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        Date hoje = new Date();
        selecionado.setGeradoEm(hoje);
        selecionado.setVencimento(DataUtil.adicionaDias(hoje, 30));
    }

    @URLAction(mappingId = "ver-mala-direta-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = malaDiretaGeralFacade.recuperar(getId());
        quantidadeCadastrosDaMalaDireta = malaDiretaGeralFacade.buscarQuantidadeDeCadastrosNaMalaDireta(selecionado);
        filtro = new FiltroMalaDiretaGeral();
        filtrarCadastros();
    }

    @URLAction(mappingId = "ver-mala-direta-por-cadastro-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPorCadastro() {
        operacao = Operacoes.VER;
        ItemMalaDiretaGeral item = malaDiretaGeralFacade.recuperarItemMalaDireta(getIdCadastro());
        selecionado = malaDiretaGeralFacade.recuperar(item.getMalaDiretaGeral().getId());
        quantidadeCadastrosDaMalaDireta = malaDiretaGeralFacade.buscarQuantidadeDeCadastrosNaMalaDireta(selecionado);
        filtro = new FiltroMalaDiretaGeral();
        filtrarCadastros();
    }

    @URLAction(mappingId = "acompanhamento-mala-direta-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void acompanhamento() {
        try {
            selecionado = malaDiretaGeralFacade.recuperar(getId());
            mapaCadastroParcelas = (Map<ContribuinteTributario, List<ResultadoParcela>>) Web.pegaDaSessao(SESSAO);
            filtro = (FiltroMalaDiretaGeral) Web.pegaDaSessao(FiltroMalaDiretaGeral.class);
            adicionarCadastrosNaMalaDireta();
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void iniciarBuscaDosCadastrosDaMalaDiretaDeIPTU() {
        try {
            validarDadosDaMalaDireta();
            FacesUtil.executaJavaScript("acompanhaConsultaDebitos()");
            filtro.setMalaDiretaGeral(selecionado);
            if (filtro.getTipoCadastroTributario().isImobiliario()) {
                idsCadastros = malaDiretaGeralFacade.buscarIdsDeBcisParaMalaDireta(filtro);
            }
            if (filtro.getTipoCadastroTributario().isEconomico()) {
                idsCadastros = malaDiretaGeralFacade.buscarIdsDeBcesParaMalaDireta(filtro);
            }
            if (filtro.getTipoCadastroTributario().isPessoa()) {
                idsCadastros = malaDiretaGeralFacade.buscarIdsDePessoasParaMalaDireta(filtro);
            }
            if (!idsCadastros.isEmpty()) {
                mapaCadastroParcelas = new HashMap<>();
                if (selecionado.getTipoMalaDireta().isCobranca()) {
                    futureConsultaDebitosCadastro = Lists.newArrayList();
                    for (List<ImpressaoMalaDiretaGeral> listaIdsCadastrosParcial : Lists.partition(idsCadastros, idsCadastros.size() > 5 ? idsCadastros.size() / 5 : 1)) {
                        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
                            "Buscando cadastros para mala direta geral", 0);
                        futureConsultaDebitosCadastro.add(AsyncExecutor.getInstance().execute(assistente,
                            () -> malaDiretaGeralFacade.buscarDebitosDaMalaDiretaDe(filtro, listaIdsCadastrosParcial)));
                    }
                } else {
                    for (ImpressaoMalaDiretaGeral idsCadastro : idsCadastros) {
                        mapaCadastroParcelas.put(
                            new ContribuinteTributario(idsCadastro.getId(), idsCadastro.getIdPessoa(), filtro.getTipoCadastroTributario()),
                            Lists.<ResultadoParcela>newArrayList());
                    }
                    finalizarConsultaNoJavaScript();
                }
            } else {
                finalizarConsultaNoJavaScript();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            finalizarConsultaNoJavaScript();
        } catch (Exception e) {
            e.printStackTrace();
            finalizarConsultaNoJavaScript();
        }
    }

    private void finalizarConsultaNoJavaScript() {
        FacesUtil.executaJavaScript("finalizaConsultaParcelas()");
    }

    public void limparCadastrosSelecionados() {
        idsCadastros = null;
        mapaCadastroParcelas = Maps.newHashMap();
    }

    private void validarDadosDaMalaDireta() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        if (selecionado.getTexto() == null || "".equals(selecionado.getTexto().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o texto da mala direta");
        }
        if (selecionado.getTipoMalaDireta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo da mala direta");
        } else if (TipoMalaDireta.NOTIFICACAO_COBRANCA.equals(selecionado.getTipoMalaDireta())
            || TipoMalaDireta.COBRANCA.equals(selecionado.getTipoMalaDireta())) {
            if (filtro.getDividas() == null || filtro.getDividas().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe pelo menos uma dívida");
            }
            if (filtro.getExercicioInicial() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício inicial");
            }
            if (filtro.getExercicioFinal() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício final");
            }
        }
        if (selecionado.getParametroMalaDireta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o parâmetro da mala direta");
        }
        if (selecionado.getVencimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o vencimento da mala direta");
        } else if (selecionado.getVencimento().before(new Date())) {
            ve.adicionarMensagemDeCampoObrigatorio("O vencimento da mala direta deve ser posterior a data de hoje");
        }
        if (filtro.getTipoCadastroTributario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro que a mala direta se destinará");
        } else {
            if (filtro.getTipoCadastroTributario().isImobiliario()) {
                if (Strings.isNullOrEmpty(filtro.getFiltroImovel().getInscricaoIncial())) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe a inscrição inicial do cadastro");
                }
                if (Strings.isNullOrEmpty(filtro.getFiltroImovel().getInscricaoFinal())) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe a inscrição final do cadastro");
                }
            }
            if (filtro.getTipoCadastroTributario().isEconomico()) {
                if (Strings.isNullOrEmpty(filtro.getFiltroEconomico().getInscricaoIncial())) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe a inscrição inicial do cadastro");
                }
                if (Strings.isNullOrEmpty(filtro.getFiltroEconomico().getInscricaoFinal())) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe a inscrição final do cadastro");
                }
            }
            if (filtro.getTipoCadastroTributario().isPessoa()) {
                if (filtro.getFiltroPessoa().getPessoas().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe a pessoa que será inclusa na mala direta");
                }
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void iniciarGeracaoDaMalaDireta() {
        try {
            validarDadosDaMalaDireta();
            selecionado.setTipoCadastro(filtro.getTipoCadastroTributario());
            selecionado = malaDiretaGeralFacade.salvarRetornando(selecionado);

            System.out.println("Vai colocar na sessão: " + mapaCadastroParcelas.size());

            Web.poeNaSessao(SESSAO, mapaCadastroParcelas);
            Web.poeNaSessao(filtro);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "acompanhamento/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorPadrao(e);
        }
    }

    private List<Map<ContribuinteTributario, List<ResultadoParcela>>> particionarMapaDeCadastrosComDebitos(Map<ContribuinteTributario, List<ResultadoParcela>> mapaParaProcessamento) {
        List<Map<ContribuinteTributario, List<ResultadoParcela>>> mapasParticionados = Lists.newArrayList();

        List<ContribuinteTributario> aParticionar = Lists.newArrayList(mapaParaProcessamento.keySet());
        for (List<ContribuinteTributario> parte : Lists.partition(aParticionar, aParticionar.size() > 5 ? aParticionar.size() / 5 : 1)) {
            Map<ContribuinteTributario, List<ResultadoParcela>> mapa = Maps.newHashMap();
            for (ContribuinteTributario contribuinte : parte) {
                mapa.put(contribuinte, mapaParaProcessamento.get(contribuinte));
            }
            mapasParticionados.add(mapa);
        }
        return mapasParticionados;
    }

    public void adicionarCadastrosNaMalaDireta() throws Exception {
        if (!filtro.getDividas().isEmpty()) {
            selecionado.setConfiguracaoDAM(filtro.getDividas().get(0).getConfiguracaoDAM());
        }
        assistenteBarraProgresso = new AssistenteBarraProgresso("Gerando mala direta", 1);
        assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        futuresItemMalaDireta = Lists.newArrayList();

        if (!mapaCadastroParcelas.isEmpty()) {
            List<Map<ContribuinteTributario, List<ResultadoParcela>>> mapaParticionado = particionarMapaDeCadastrosComDebitos(mapaCadastroParcelas);
            assistenteBarraProgresso.setTotal(mapaCadastroParcelas.size());
            for (Map<ContribuinteTributario, List<ResultadoParcela>> mapa : mapaParticionado) {
                futuresItemMalaDireta.add(AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                    () -> malaDiretaGeralFacade.criarItemMalaDireta(selecionado, mapa, assistenteBarraProgresso)));
            }
        } else {
            assistenteBarraProgresso.setDescricaoProcesso("Nenhum débito encontrado para os cadastros");
            assistenteBarraProgresso.conta();
        }
        FacesUtil.executaJavaScript("acompanhaAdicaoDeCadastros()");
    }

    public List<SelectItem> getTipoImovel() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoImovel tipoImovel : TipoImovel.values()) {
            retorno.add(new SelectItem(tipoImovel, tipoImovel.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoCobranca() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoCobrancaTributario tipoCobranca : TipoCobrancaTributario.values()) {
            retorno.add(new SelectItem(tipoCobranca, tipoCobranca.getDescricao()));
        }
        return retorno;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void imprimirCadastros() {
        try {
            if (selecionado.getId() != null) {
                idsCadastros = malaDiretaGeralFacade.listaImpressaoMalaDireta(null, selecionado);
            }
            String arquivoJasper = "lista_cadastros_mala_direta";
            AbstractReport report = AbstractReport.getAbstractReport();
            HashMap parameters = new HashMap<>();
            parameters.put("SECRETARIA", "Secretaria de Finanças");
            parameters.put("MODULO", "Tributário");
            parameters.put("NOMERELATORIO", "Demonstrativo de Cadastros da Mala Direta");
            parameters.put("USUARIO", getSistemaControlador().getUsuarioCorrente().getLogin());
            parameters.put("BRASAO", report.getCaminhoImagem());
            report.setGeraNoDialog(true);
            JasperPrint jasperPrint = report.gerarBytesDeRelatorioComDadosEmCollectionView(report.getCaminho(),
                arquivoJasper + ".jasper", parameters, new JRBeanCollectionDataSource(idsCadastros));
            ByteArrayOutputStream byteArrayOutputStream = report.exportarJasperParaPDF(jasperPrint);
            report.escreveNoResponse(arquivoJasper, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void imprimirDam(ItemMalaDiretaGeral item) {
        try {
            imprimeDAM.imprimirDamMalaDiretaGeral(sistemaFacade.getUsuarioCorrente().getLogin(),
                selecionado.getId(), Lists.newArrayList(item.getId()));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void imprimirDams() {
        listaFuturesGeracaoDam = new ArrayList<>();
        criarPastaDaMalaDireta();
        List<ImpressaoMalaDiretaGeral> impressoes = Lists.newArrayList();
        impressoes.addAll(malaDiretaGeralFacade.listaImpressaoMalaDireta(null, selecionado));
        int partes = impressoes.size() > 50 ? impressoes.size() / 4 : impressoes.size();
        List<List<ImpressaoMalaDiretaGeral>> impressoesParticionadas = Lists.partition(impressoes, partes);
        int numFuture = 1;
        UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
        for (List<ImpressaoMalaDiretaGeral> parte : impressoesParticionadas) {
            assistenteImpressaoMalaDireta = new AssistenteImpressaoMalaDiretaGeral(imprimeDAM,
                selecionado.getId(), parte, usuarioCorrente, getPastaMalaDireta(), numFuture++);
            listaFuturesGeracaoDam.add(AsyncExecutor.getInstance().execute(assistenteImpressaoMalaDireta,
                () -> malaDiretaGeralFacade.imprimirDamsMalaDireta(assistenteImpressaoMalaDireta)));
        }
    }

    private void criarPastaDaMalaDireta() {
        File pasta = new File(getPastaMalaDireta());
        if (!pasta.exists()) {
            pasta.mkdir();
        } else {
            for (File file : new File(getPastaMalaDireta()).listFiles()) {
                file.delete();
            }
        }
    }

    public void navegarParaVisualizacao() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver-por-cadastro/" + getIdCadastro() + "/");
    }

    private void pesquisarCadastrosDaMalaDireta() {
        quantidadeTotal = malaDiretaGeralFacade.buscarQuantidadeCadastrosDaMalaDireta(selecionado, filtro);
        itensMalaDireta = malaDiretaGeralFacade.buscarCadastrosDaMalaDireta(selecionado, filtro, indexInicialDaPesquisa, quantidadePorPagina);
    }

    public boolean isPermitidoIrParaPaginaAnterior() {
        return indexInicialDaPesquisa > 0;
    }


    public void irParaPaginaAnterior() {
        indexInicialDaPesquisa = indexInicialDaPesquisa - quantidadePorPagina;
        if (indexInicialDaPesquisa < 0) {
            indexInicialDaPesquisa = 0;
        }
        pesquisarCadastrosDaMalaDireta();
    }


    public boolean isPermitidoIrParaPaginaPosterior() {
        return (indexInicialDaPesquisa + quantidadePorPagina) < quantidadeTotal;
    }

    public void irParaPaginaPosterior() {
        indexInicialDaPesquisa = indexInicialDaPesquisa + quantidadePorPagina;
        pesquisarCadastrosDaMalaDireta();
    }

    public void alterarQuantidadeDeRegistrosDaTabela(Long quantidade) {
        quantidadePorPagina = quantidade.intValue();
        pesquisarCadastrosDaMalaDireta();
    }

    public void filtrarCadastros() {
        indexInicialDaPesquisa = 0;
        pesquisarCadastrosDaMalaDireta();
    }

    public void addToZip() {
        try {
            File zipFile = File.createTempFile(ARQUIVODOWNLOAD, "zip");

            byte[] buffer = new byte[1024];

            FileOutputStream fout = new FileOutputStream(zipFile);
            ZipOutputStream zout = new ZipOutputStream(fout);
            for (File file : new File(getPastaMalaDireta()).listFiles()) {
                if (file.getName().contains(selecionado.getId() + "")) {
                    FileInputStream fin = new FileInputStream(file);
                    zout.putNextEntry(new ZipEntry(file.getName()));
                    int length;
                    while ((length = fin.read(buffer)) > 0) {
                        zout.write(buffer, 0, length);
                    }
                    zout.closeEntry();
                    fin.close();
                }
            }
            FileInputStream fis = new FileInputStream(zipFile);
            fileDownload = new DefaultStreamedContent(fis, "application/zip", ARQUIVODOWNLOAD + ".zip");
            zout.close();
        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo ZIP da Mala Direta, comunique o administrador.");
        }
    }

    private String getPastaMalaDireta() {
        if (pastaMalaDireta == null || "".equals(pastaMalaDireta)) {
            pastaMalaDireta = AbstractReport.getAbstractReport().getCaminho() + ARQUIVODOWNLOAD + "/";
        }
        return pastaMalaDireta;
    }

    public void acompanharGeracaoDam() {
        boolean terminou = false;
        if (listaFuturesGeracaoDam != null && !listaFuturesGeracaoDam.isEmpty()) {
            terminou = true;
            for (Future<AssistenteImpressaoMalaDiretaGeral> future : listaFuturesGeracaoDam) {
                if (!future.isDone()) {
                    terminou = false;
                }
            }
        }
        if (terminou) {
            addToZip();
            FacesUtil.executaJavaScript("finalizaGeracaoDam()");
        }
    }


    public void acompanharImpressaoCadastros() {
        boolean terminou = false;
        if (listaFuturesGeracaoDam != null && !listaFuturesGeracaoDam.isEmpty()) {
            terminou = true;
            for (Future<AssistenteImpressaoMalaDiretaGeral> future : listaFuturesGeracaoDam) {
                if (!future.isDone()) {
                    terminou = false;
                }
            }
        }
        if (terminou) {
            imprimirCadastros();
            FacesUtil.executaJavaScript("finalizaGeracaoDam()");
        }
    }

    public void terminarConsultaDebitos() {
        boolean terminou = false;
        if (futureConsultaDebitosCadastro != null && !futureConsultaDebitosCadastro.isEmpty()) {
            terminou = true;
            for (Future<Map<ContribuinteTributario, List<ResultadoParcela>>> future : futureConsultaDebitosCadastro) {
                if (!future.isDone()) {
                    terminou = false;
                }
            }
        }
        if (terminou) {
            for (Future<Map<ContribuinteTributario, List<ResultadoParcela>>> future : futureConsultaDebitosCadastro) {
                try {
                    mapaCadastroParcelas.putAll(future.get());
                } catch (InterruptedException e) {
                    logger.error("{}", e);
                } catch (ExecutionException e) {
                    logger.error("{}", e);
                }
            }
            finalizarConsultaNoJavaScript();
            if (mapaCadastroParcelas.isEmpty()) {
                idsCadastros = Lists.newArrayList();
            }
        }
    }

    public AssistenteImpressaoMalaDiretaGeral getAssistenteImpressaoMalaDireta() {
        return assistenteImpressaoMalaDireta;
    }


    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (filtro.getTipoCadastroTributario() != null) {
            List<Divida> dividas = filtro.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)
                ? malaDiretaGeralFacade.getDividaFacade().lista() :
                malaDiretaGeralFacade.getDividaFacade().listaDividasPorTipoCadastro(filtro.getTipoCadastroTributario());
            for (Divida divida : dividas) {
                toReturn.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<Logradouro> completaLogradouro(String parte) {
        if (!filtro.getFiltroImovel().getBairros().isEmpty()) {
            return malaDiretaGeralFacade.getLogradouroFacade().completaLogradourosPorBairros(parte, filtro.getFiltroImovel().getBairros());
        } else if (!filtro.getFiltroEconomico().getBairros().isEmpty()) {
            return malaDiretaGeralFacade.getLogradouroFacade().completaLogradourosPorBairros(parte, filtro.getFiltroEconomico().getBairros());
        } else {
            return malaDiretaGeralFacade.getLogradouroFacade().listaLogradourosAtivos(parte);
        }
    }

    public void limparCadastro() {
        TipoCadastroTributario tipo = filtro.getTipoCadastroTributario();
        filtro = new FiltroMalaDiretaGeral();
        filtro.setTipoCadastroTributario(tipo);
    }

    public void limparParametro() {
        selecionado.setParametroMalaDireta(null);
        selecionado.setCabecalho("");
        selecionado.setTexto("");
    }

    public List<SelectItem> getTipoIss() {
        return Util.getListSelectItem(TipoIssqn.values());
    }

    public void selecionarParametro() {
        selecionado.setTexto(selecionado.getParametroMalaDireta().getCorpo());
        selecionado.setCabecalho(selecionado.getParametroMalaDireta().getCabecalho());
    }


    public List<ParametroMalaDireta> completarParametroPorTipo(String parte) {
        return parametroMalaDiretaFacade.completarParametroPorTipo(parte, selecionado.getTipoMalaDireta());
    }


    public void prepararEmail(ItemMalaDiretaGeral item) {
        this.item = item;
        this.email = item.getPessoa().getEmail();
    }

    public void enviarTodosEmails() {
       AbstractReport report = AbstractReport.getAbstractReport();
        List<ImpressaoMalaDiretaGeral> impressaoMalaDiretaGerals = malaDiretaGeralFacade.listaImpressaoMalaDireta(null, selecionado);
        List<ImpressaoMalaDiretaGeral> enviarEmail = Lists.newArrayList();
        for (ImpressaoMalaDiretaGeral impressao : impressaoMalaDiretaGerals) {
            if (Strings.isNullOrEmpty(impressao.getEmailContribuinte())) {
                continue;
            }
            enviarEmail.add(impressao);
        }
        UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
        malaDiretaGeralFacade.enviarEmail(imprimeDAM, selecionado.getId(),
            enviarEmail, usuarioCorrente.getLogin(), getAssunto());
        FacesUtil.addOperacaoRealizada("TODOS os e-mails estão sendo enviados em segundo plano");
    }

    public void enviarEmail() {
        try {
            List<ImpressaoMalaDiretaGeral> listaDams = malaDiretaGeralFacade
                .listaImpressaoMalaDireta(Lists.newArrayList(item.getId()), selecionado);
            ImpressaoMalaDiretaGeral impressao = listaDams.get(0);
            byte[] bytes = imprimeDAM.gerarBytesImpressaoMalaDiretaGeral(sistemaFacade.getUsuarioCorrente().getLogin(),
                selecionado.getId(), Lists.newArrayList(item.getId()));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
            byteArrayOutputStream.write(bytes);
            EmailService.getInstance().enviarEmail(email, getAssunto(), impressao.getTexto(), byteArrayOutputStream);
            FacesUtil.addOperacaoRealizada("E-mail enviado com sucesso!");
        } catch (Exception e) {
            logger.error("Ocorreu um erro ao enviar o e-mail: {}", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar o e-mail com o DAM ");
        }
    }

    public String getAssunto() {
        return "Prefeitura Municipal de " + configuracaoTributarioFacade.retornaUltimo().getCidade().getNome() + " / " + configuracaoTributarioFacade.retornaUltimo().getCidade().getUf() + " - Documento de Arrecadação";
    }

    @Override
    public void cancelar() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }
}
