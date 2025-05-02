package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteImpressaoMalaDireta;
import br.com.webpublico.entidadesauxiliares.FiltroCadastroMalaDiretaIPTU;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoCobrancaTributario;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.enums.tributario.TipoEnderecoExportacaoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MalaDiretaIptuFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by William on 07/06/2016.
 */
@ManagedBean(name = "malaDiretaIptuControlador")
@ViewScoped

@URLMappings(mappings = {
    @URLMapping(id = "nova-mala-direta", pattern = "/mala-direta-iptu/novo/", viewId = "/faces/tributario/iptu/maladireta/edita.xhtml"),
    @URLMapping(id = "listar-mala-direta", pattern = "/mala-direta-iptu/listar/", viewId = "/faces/tributario/iptu/maladireta/lista.xhtml"),
    @URLMapping(id = "ver-mala-direta-por-cadastro", pattern = "/mala-direta-iptu/ver-por-cadastro/#{malaDiretaIptuControlador.idCadastro}/", viewId = "/faces/tributario/iptu/maladireta/visualizar.xhtml"),
    @URLMapping(id = "ver-mala-direta", pattern = "/mala-direta-iptu/ver/#{malaDiretaIptuControlador.id}/", viewId = "/faces/tributario/iptu/maladireta/visualizar.xhtml"),
    @URLMapping(id = "acompanhamento-mala-direta", pattern = "/mala-direta-iptu/acompanhamento/#{malaDiretaIptuControlador.id}/", viewId = "/faces/tributario/iptu/maladireta/acompanhamento.xhtml")
})
public class MalaDiretaIptuControlador extends PrettyControlador<MalaDiretaIPTU> implements CRUD {

    private static final Logger logger = LoggerFactory.getLogger(MalaDiretaIptuControlador.class);
    private final String ARQUIVODOWNLOAD = "MALADIRETA";
    private final String SESSAO = "ListaDeCadastrosParcelas";
    @EJB
    private MalaDiretaIptuFacade malaDiretaIptuFacade;
    private AssistenteImpressaoMalaDireta assistenteImpressaoMalaDireta;
    private List<Future<List<CadastroMalaDiretaIPTU>>> futuresCadastrosMalaDiretaIPTU;
    private boolean futuresCadastrosMalaDiretaIPTUConcluida;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<Long> idsCadastroImobiliarios;
    private boolean processoConcluido;
    private Integer quantidadeCadastrosDaMalaDireta;
    private FiltroCadastroMalaDiretaIPTU filtroCadastroMalaDiretaIPTU;
    private List<CadastroMalaDiretaIPTU> cadastrosDaMalaDiretaIPTU;
    private int quantidadeTotal = 0;
    private int quantidadePorPagina = 10;
    private int indexInicialDaPesquisa = 0;
    private ConfiguracaoTributario configuracaoTributario;
    private DefaultStreamedContent fileDownload;
    private List<Future<AssistenteImpressaoMalaDireta>> listaFuturesGeracaoDam;
    private String pastaMalaDireta;
    private Long idCadastro;
    private Map<CadastroImobiliario, List<ResultadoParcela>> mapaCadastroParcelas;
    private List<Future<Map<CadastroImobiliario, List<ResultadoParcela>>>> futureConsultaDebitosCadastro;

    public MalaDiretaIptuControlador() {
        super(MalaDiretaIPTU.class);
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

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return malaDiretaIptuFacade;
    }

    public List<Future<List<CadastroMalaDiretaIPTU>>> getFuturesCadastrosMalaDiretaIPTU() {
        return futuresCadastrosMalaDiretaIPTU;
    }

    public void setFuturesCadastrosMalaDiretaIPTU(List<Future<List<CadastroMalaDiretaIPTU>>> futuresCadastrosMalaDiretaIPTU) {
        this.futuresCadastrosMalaDiretaIPTU = futuresCadastrosMalaDiretaIPTU;
    }

    public boolean isFuturesCadastrosMalaDiretaIPTUConcluida() {
        if (futuresCadastrosMalaDiretaIPTU == null) {
            return false;
        }
        if (futuresCadastrosMalaDiretaIPTU.isEmpty()) {
            return true;
        }

        for (Future<List<CadastroMalaDiretaIPTU>> futureDaVez : futuresCadastrosMalaDiretaIPTU) {
            if (!futureDaVez.isDone()) {
                return false;
            }
        }
        return true;
    }

    public void setFuturesCadastrosMalaDiretaIPTUConcluida(boolean futuresCadastrosMalaDiretaIPTUConcluida) {
        this.futuresCadastrosMalaDiretaIPTUConcluida = futuresCadastrosMalaDiretaIPTUConcluida;
    }

    public boolean isProcessoConcluido() {
        return isFuturesCadastrosMalaDiretaIPTUConcluida();
    }

    public void setProcessoConcluido(boolean processoConcluido) {
        this.processoConcluido = processoConcluido;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<Long> getIdsCadastroImobiliarios() {
        return idsCadastroImobiliarios;
    }

    public void setIdsCadastroImobiliarios(List<Long> idsCadastroImobiliarios) {
        this.idsCadastroImobiliarios = idsCadastroImobiliarios;
    }

    public Integer getQuantidadeCadastrosDaMalaDireta() {
        return quantidadeCadastrosDaMalaDireta;
    }

    public void setQuantidadeCadastrosDaMalaDireta(Integer quantidadeCadastrosDaMalaDireta) {
        this.quantidadeCadastrosDaMalaDireta = quantidadeCadastrosDaMalaDireta;
    }

    public FiltroCadastroMalaDiretaIPTU getFiltroCadastroMalaDiretaIPTU() {
        return filtroCadastroMalaDiretaIPTU;
    }

    public void setFiltroCadastroMalaDiretaIPTU(FiltroCadastroMalaDiretaIPTU filtroCadastroMalaDiretaIPTU) {
        this.filtroCadastroMalaDiretaIPTU = filtroCadastroMalaDiretaIPTU;
    }

    public List<CadastroMalaDiretaIPTU> getCadastrosDaMalaDiretaIPTU() {
        return cadastrosDaMalaDiretaIPTU;
    }

    public void setCadastrosDaMalaDiretaIPTU(List<CadastroMalaDiretaIPTU> cadastrosDaMalaDiretaIPTU) {
        this.cadastrosDaMalaDiretaIPTU = cadastrosDaMalaDiretaIPTU;
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
            configuracaoTributario = malaDiretaIptuFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        }
        return configuracaoTributario;
    }

    public Map<CadastroImobiliario, List<ResultadoParcela>> getMapaCadastroParcelas() {
        return mapaCadastroParcelas;
    }

    public void setMapaCadastroParcelas(Map<CadastroImobiliario, List<ResultadoParcela>> mapaCadastroParcelas) {
        this.mapaCadastroParcelas = mapaCadastroParcelas;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/mala-direta-iptu/";
    }

    @URLAction(mappingId = "nova-mala-direta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        selecionado.setInscricaoInicial("100000000000000");
        selecionado.setInscricaoFinal("999999999999999");
        selecionado.setValorInicial(BigDecimal.ZERO);
        selecionado.setValorFinal(BigDecimal.ZERO.valueOf(9999999.99));
        atualizarTextoMalaDireta();
    }

    @URLAction(mappingId = "ver-mala-direta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = malaDiretaIptuFacade.recuperar(getId());
        quantidadeCadastrosDaMalaDireta = malaDiretaIptuFacade.buscarQuantidadeDeCadastrosNaMalaDireta(selecionado);
        filtroCadastroMalaDiretaIPTU = new FiltroCadastroMalaDiretaIPTU();
        filtrarCadastros();
    }

    @URLAction(mappingId = "ver-mala-direta-por-cadastro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPorCadastro() {
        operacao = Operacoes.VER;
        CadastroMalaDiretaIPTU cadastroMalaDiretaIPTU = malaDiretaIptuFacade.recuperarCadastroMalaDiretaIPTU(getIdCadastro());
        selecionado = malaDiretaIptuFacade.recuperar(cadastroMalaDiretaIPTU.getMalaDiretaIPTU().getId());
        quantidadeCadastrosDaMalaDireta = malaDiretaIptuFacade.buscarQuantidadeDeCadastrosNaMalaDireta(selecionado);
        filtroCadastroMalaDiretaIPTU = new FiltroCadastroMalaDiretaIPTU();
        filtroCadastroMalaDiretaIPTU.setInscricaoInicial(cadastroMalaDiretaIPTU.getCadastroImobiliario().getInscricaoCadastral());
        filtroCadastroMalaDiretaIPTU.setInscricaoFinal(cadastroMalaDiretaIPTU.getCadastroImobiliario().getInscricaoCadastral());
        filtrarCadastros();
    }

    @URLAction(mappingId = "acompanhamento-mala-direta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void acompanhamento() {
        try {
            selecionado = malaDiretaIptuFacade.recuperar(getId());

            mapaCadastroParcelas = (Map<CadastroImobiliario, List<ResultadoParcela>>) Web.pegaDaSessao(SESSAO);

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
            idsCadastroImobiliarios = malaDiretaIptuFacade.buscarIdsDeBcisParaMalaDiretaDeIPTU(selecionado, getConfiguracaoTributario());
            if (!idsCadastroImobiliarios.isEmpty()) {
                futureConsultaDebitosCadastro = Lists.newArrayList();

                mapaCadastroParcelas = new HashMap<>();
                for (List<Long> listaIdsCadastrosParcial : Lists.partition(idsCadastroImobiliarios, idsCadastroImobiliarios.size() > 5 ? idsCadastroImobiliarios.size() / 5 : 1)) {
                    futureConsultaDebitosCadastro.add(malaDiretaIptuFacade.buscarDebitosDaMalaDiretaDeIPTU(getConfiguracaoTributario(), selecionado, listaIdsCadastrosParcial));
                }
            } else {
                FacesUtil.executaJavaScript("finalizaConsultaParcelas()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void limparCadastrosSelecionados() {
        idsCadastroImobiliarios = null;
    }

    private void validarDadosDaMalaDireta() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);

        if (selecionado.getValorInicial().compareTo(selecionado.getValorFinal()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Valor Inicial não pode ser maior que o Valor Final!");
        }
        if (selecionado.getTexto() == null || selecionado.getTexto().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Texto da Mala Direta!");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void iniciarGeracaoDaMalaDireta() {
        try {
            validarDadosDaMalaDireta();
            selecionado = malaDiretaIptuFacade.salvarRetornando(selecionado);
            Web.poeNaSessao(SESSAO, mapaCadastroParcelas);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "acompanhamento/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorPadrao(e);
        }
    }

    private List<Map<CadastroImobiliario, List<ResultadoParcela>>> particionarMapaDeCadastrosComDebitos(Map<CadastroImobiliario, List<ResultadoParcela>> mapaParaProcessamento) {
        List<Map<CadastroImobiliario, List<ResultadoParcela>>> mapasParticionados = Lists.newArrayList();

        List<CadastroImobiliario> aParticionar = Lists.newArrayList(mapaParaProcessamento.keySet());
        for (List<CadastroImobiliario> parte : Lists.partition(aParticionar, aParticionar.size() > 5 ? aParticionar.size() / 5 : 1)) {
            Map<CadastroImobiliario, List<ResultadoParcela>> mapa = Maps.newHashMap();
            for (CadastroImobiliario cadastroImobiliario : parte) {
                mapa.put(cadastroImobiliario, mapaParaProcessamento.get(cadastroImobiliario));
            }
            mapasParticionados.add(mapa);
        }
        return mapasParticionados;
    }

    public void adicionarCadastrosNaMalaDireta() throws Exception {
        assistenteBarraProgresso = new AssistenteBarraProgresso("Gerando mala direta", 1);
        futuresCadastrosMalaDiretaIPTU = Lists.newArrayList();
        UsuarioSistema usuarioCorrente = malaDiretaIptuFacade.getSistemaFacade().getUsuarioCorrente();

        if (!mapaCadastroParcelas.isEmpty()) {
            List<Map<CadastroImobiliario, List<ResultadoParcela>>> mapaParticionado = particionarMapaDeCadastrosComDebitos(mapaCadastroParcelas);

            assistenteBarraProgresso.setTotal(mapaCadastroParcelas.size());
            for (Map<CadastroImobiliario, List<ResultadoParcela>> mapa : mapaParticionado) {
                futuresCadastrosMalaDiretaIPTU.add(malaDiretaIptuFacade.criarCadastrosMalaDireraIPTU(selecionado, mapa, assistenteBarraProgresso, usuarioCorrente));
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

    public List<SelectItem> getTipoEndereco() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoEnderecoExportacaoIPTU tipoEndereco : TipoEnderecoExportacaoIPTU.values()) {
            retorno.add(new SelectItem(tipoEndereco, tipoEndereco.getDescricao()));
        }
        return retorno;
    }

    public void copiarInscricaoInicialParaInscricaoFinal() {
        selecionado.setInscricaoFinal(selecionado.getInscricaoInicial());
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void imprimirDam(CadastroMalaDiretaIPTU cadastroMalaDiretaIPTU) {
        imprimirMalaDireta(Lists.newArrayList(cadastroMalaDiretaIPTU), true);
    }

    private byte[] imprimirMalaDireta(List<CadastroMalaDiretaIPTU> cadastros, boolean async) {
        try {
            List<Long> idsCadastros = Lists.newArrayList();
            for (CadastroMalaDiretaIPTU cadastro : cadastros) {
                idsCadastros.add(cadastro.getId());
            }
            UsuarioSistema usuario = malaDiretaIptuFacade.getSistemaFacade().getUsuarioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Mala direta de IPTU");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MSG_PIX", "Pagamento Via QrCode PIX");
            dto.adicionarParametro("USUARIO", usuario.getNome());
            dto.adicionarParametro("IDS_CADASTROS", idsCadastros);
            dto.adicionarParametro("TIPO_COBRANCA", selecionado.getTipoCobranca().name());
            dto.adicionarParametro("EXERCICIO_MALA_DIRETA", selecionado.getExercicio().getAno());
            dto.setApi("tributario/mala-direta-iptu/");
            if (async) {
                ReportService.getInstance().gerarRelatorio(usuario, dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            } else {
                return ReportService.getInstance().gerarRelatorioSincrono(dto);
            }
        } catch (
            WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (
            ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
        return null;
    }

    public void imprimirDams() {
        criarPastaDaMalaDireta();
        assistenteImpressaoMalaDireta = new AssistenteImpressaoMalaDireta();
        assistenteImpressaoMalaDireta.setUsuario(malaDiretaIptuFacade.getSistemaFacade().getUsuarioCorrente().getLogin());
        assistenteImpressaoMalaDireta.setIdMalaDireta(selecionado.getId());
        listaFuturesGeracaoDam = Lists.newArrayList();

        List<CadastroMalaDiretaIPTU> cadastros = selecionado.getCadastroMalaDiretaIPTU();
        assistenteImpressaoMalaDireta.setTotal(cadastros.size());
        List<List<CadastroMalaDiretaIPTU>> idsCadastrosParticionados = Lists.partition(cadastros,
            cadastros.size() > 5 ? cadastros.size() / 5 : cadastros.size());
        int numFuture = 1;
        for (List<CadastroMalaDiretaIPTU> parte : idsCadastrosParticionados) {
            String nomeDoArquivo = pastaMalaDireta + "MALA_DIRETA_IPTU" +
                "_" + selecionado.getId() + "_" +
                StringUtil.preencheString(numFuture + "", 2, '0') + ".pdf";
            byte[] bytes = imprimirMalaDireta(parte, false);
            try (FileOutputStream outputStream = new FileOutputStream(nomeDoArquivo)) {
                outputStream.write(bytes);
            } catch (Exception e) {
                logger.error("Erro ao gerar o arquivo " + nomeDoArquivo + " " + e.getMessage());
            }
            assistenteImpressaoMalaDireta.contar(parte.size());
            listaFuturesGeracaoDam.add(new AsyncResult<>(new AssistenteImpressaoMalaDireta()));
            numFuture++;
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
        quantidadeTotal = malaDiretaIptuFacade.buscarQuantidadeCadastrosDaMalaDiretaDeIPTU(selecionado, filtroCadastroMalaDiretaIPTU);
        cadastrosDaMalaDiretaIPTU = malaDiretaIptuFacade.buscarCadastrosDaMalaDiretaDeIPTU(selecionado, filtroCadastroMalaDiretaIPTU, indexInicialDaPesquisa, quantidadePorPagina);
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
            for (Future<AssistenteImpressaoMalaDireta> future : listaFuturesGeracaoDam) {
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

    public void terminarConsultaDebitos() {
        boolean terminou = false;
        if (futureConsultaDebitosCadastro != null && !futureConsultaDebitosCadastro.isEmpty()) {
            terminou = true;
            for (Future<Map<CadastroImobiliario, List<ResultadoParcela>>> future : futureConsultaDebitosCadastro) {
                if (!future.isDone()) {
                    terminou = false;
                }
            }
        }
        if (terminou) {
            for (Future<Map<CadastroImobiliario, List<ResultadoParcela>>> future : futureConsultaDebitosCadastro) {
                try {
                    mapaCadastroParcelas.putAll(future.get());
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                } catch (ExecutionException e) {
                    logger.error(e.getMessage());
                }
            }
            FacesUtil.executaJavaScript("finalizaConsultaParcelas()");
            if (mapaCadastroParcelas.isEmpty()) {
                idsCadastroImobiliarios = Lists.newArrayList();
            }
        }
    }

    public AssistenteImpressaoMalaDireta getAssistenteImpressaoMalaDireta() {
        return assistenteImpressaoMalaDireta;
    }

    public void atualizarTextoMalaDireta() {
        ParametroMalaDiretaIPTU parametro = malaDiretaIptuFacade.getParametroMalaDiretaIptuFacade().buscarParametroPeloExercicio(selecionado.getExercicio());
        if (parametro == null) return;
        String texto = parametro.getTextoMalaDireta();
        texto = texto.replace("${EXERCICIO_PARAMETRO}", selecionado.getExercicio().getAno().toString());
        texto = texto.replace("${EXERCICIO_CORRENTE}", malaDiretaIptuFacade.getSistemaFacade().getExercicioCorrente().getAno().toString());
        selecionado.setTexto(texto);
    }
}
