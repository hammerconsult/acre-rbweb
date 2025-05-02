package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.MalaDiretaRBTrans;
import br.com.webpublico.entidades.MalaDiretaRBTransPermissao;
import br.com.webpublico.entidades.PermissaoTransporte;
import br.com.webpublico.entidadesauxiliares.AssistenteImpressaoMalaDiretaRBTrans;
import br.com.webpublico.entidadesauxiliares.FiltroPermissaoMalaDiretaRBTrans;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoCredencialRBTrans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.transaction.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Wellington on 22/12/2016.
 */
@ManagedBean(name = "malaDiretaRBTransControlador")
@ViewScoped

@URLMappings(mappings = {
    @URLMapping(id = "nova-mala-direta-rbtrans",
        pattern = "/mala-direta-rbtrans/novo/",
        viewId = "/faces/tributario/rbtrans/maladireta/edita.xhtml"),
    @URLMapping(id = "listar-mala-direta-rbtrans",
        pattern = "/mala-direta-rbtrans/listar/",
        viewId = "/faces/tributario/rbtrans/maladireta/lista.xhtml"),
    @URLMapping(id = "ver-mala-direta-por-permissao",
        pattern = "/mala-direta-rbtrans/ver-por-permissao/#{malaDiretaRBTransControlador.idPermissao}/",
        viewId = "/faces/tributario/rbtrans/maladireta/visualizar.xhtml"),
    @URLMapping(id = "ver-mala-direta-rbtrans",
        pattern = "/mala-direta-rbtrans/ver/#{malaDiretaRBTransControlador.id}/",
        viewId = "/faces/tributario/rbtrans/maladireta/visualizar.xhtml"),
    @URLMapping(id = "acompanhamento-mala-direta-rbtrans",
        pattern = "/mala-direta-rbtrans/acompanhamento/#{malaDiretaRBTransControlador.id}/",
        viewId = "/faces/tributario/rbtrans/maladireta/acompanhamento.xhtml")
})
public class MalaDiretaRBTransControlador extends PrettyControlador<MalaDiretaRBTrans> implements CRUD {

    private static final Logger logger = LoggerFactory.getLogger(MalaDiretaRBTransControlador.class);
    private final String ARQUIVODOWNLOAD = "MALADIRETARBTRANS";
    private final String SESSAO = "ListaDePermissoesParcelas";
    @EJB
    private MalaDiretaRBTransFacade malaDiretaRBTransFacade;
    private AssistenteImpressaoMalaDiretaRBTrans assistenteImpressaoMalaDiretaRBTrans;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private PixFacade pixFacade;
    private List<CompletableFuture<List<MalaDiretaRBTransPermissao>>> futuresPermissoesMalaDireta;
    private boolean futuresPermissoesMalaDiretaConcluida;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<Long> idsPermissoes;
    private boolean processoConcluido;
    private Integer quantidadePermissoesMalaDireta;
    private FiltroPermissaoMalaDiretaRBTrans filtroPermissaoMalaDiretaRBTrans;
    private List<MalaDiretaRBTransPermissao> permissoesMalaDiretaRBTrans;
    private int quantidadeTotal = 0;
    private int quantidadePorPagina = 10;
    private int indexInicialDaPesquisa = 0;
    private ConfiguracaoTributario configuracaoTributario;
    private DefaultStreamedContent fileDownload;
    private List<CompletableFuture<AssistenteImpressaoMalaDiretaRBTrans>> listaFuturesGeracaoDam;
    private String pastaMalaDireta;
    private Long idPermissao;
    private Map<PermissaoTransporte, List<ResultadoParcela>> mapaPermissaoParcelas;
    private List<CompletableFuture<Map<PermissaoTransporte, List<ResultadoParcela>>>> futureConsultaDebitosPermissao;
    private ImprimeDAM imprimeDAM = new ImprimeDAM();

    public MalaDiretaRBTransControlador() {
        super(MalaDiretaRBTrans.class);
    }

    public DefaultStreamedContent fazerDownloadDoArquivo() {
        if (fileDownload != null) {
            return fileDownload;
        }
        return null;
    }

    public Long getIdPermissao() {
        return idPermissao;
    }

    public void setIdPermissao(Long idPermissao) {
        this.idPermissao = idPermissao;
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
        return malaDiretaRBTransFacade;
    }

    public List<CompletableFuture<List<MalaDiretaRBTransPermissao>>> getFuturesPermissoesMalaDireta() {
        return futuresPermissoesMalaDireta;
    }

    public boolean isFuturesPermissoesMalaDiretaConcluida() {
        if (futuresPermissoesMalaDireta == null) {
            return false;
        }
        if (futuresPermissoesMalaDireta.size() == 0) {
            return true;
        }

        for (Future<List<MalaDiretaRBTransPermissao>> futureDaVez : futuresPermissoesMalaDireta) {
            if (!futureDaVez.isDone()) {
                return false;
            }
        }

        return true;
    }

    public void setFuturesPermissoesMalaDiretaConcluida(boolean futuresPermissoesMalaDiretaConcluida) {
        this.futuresPermissoesMalaDiretaConcluida = futuresPermissoesMalaDiretaConcluida;
    }

    public boolean isProcessoConcluido() {
        if (!isFuturesPermissoesMalaDiretaConcluida()) {
            return false;
        }
        return true;
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

    public List<Long> getIdsPermissoes() {
        return idsPermissoes;
    }

    public void setIdsPermissoes(List<Long> idsPermissoes) {
        this.idsPermissoes = idsPermissoes;
    }

    public Integer getQuantidadePermissoesMalaDireta() {
        return quantidadePermissoesMalaDireta;
    }

    public void setQuantidadePermissoesMalaDireta(Integer quantidadePermissoesMalaDireta) {
        this.quantidadePermissoesMalaDireta = quantidadePermissoesMalaDireta;
    }

    public FiltroPermissaoMalaDiretaRBTrans getFiltroPermissaoMalaDiretaRBTrans() {
        return filtroPermissaoMalaDiretaRBTrans;
    }

    public void setFiltroPermissaoMalaDiretaRBTrans(FiltroPermissaoMalaDiretaRBTrans filtroPermissaoMalaDiretaRBTrans) {
        this.filtroPermissaoMalaDiretaRBTrans = filtroPermissaoMalaDiretaRBTrans;
    }

    public List<MalaDiretaRBTransPermissao> getPermissoesMalaDiretaRBTrans() {
        return permissoesMalaDiretaRBTrans;
    }

    public void setPermissoesMalaDiretaRBTrans(List<MalaDiretaRBTransPermissao> permissoesMalaDiretaRBTrans) {
        this.permissoesMalaDiretaRBTrans = permissoesMalaDiretaRBTrans;
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
            configuracaoTributario = malaDiretaRBTransFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        }
        return configuracaoTributario;
    }

    public Map<PermissaoTransporte, List<ResultadoParcela>> getMapaPermissaoParcelas() {
        return mapaPermissaoParcelas;
    }

    public void setMapaPermissaoParcelas(Map<PermissaoTransporte, List<ResultadoParcela>> mapaPermissaoParcelas) {
        this.mapaPermissaoParcelas = mapaPermissaoParcelas;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/mala-direta-rbtrans/";
    }

    @URLAction(mappingId = "nova-mala-direta-rbtrans", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        atualizarTextoMalaDireta();
    }

    @URLAction(mappingId = "ver-mala-direta-rbtrans", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        quantidadePermissoesMalaDireta = malaDiretaRBTransFacade.buscarQuantidadeDePermissoesNaMalaDireta(selecionado);
        filtroPermissaoMalaDiretaRBTrans = new FiltroPermissaoMalaDiretaRBTrans();
        filtrarPermissoes();
    }

    @URLAction(mappingId = "ver-mala-direta-por-permissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPorPermissao() {
        operacao = Operacoes.VER;
        MalaDiretaRBTransPermissao malaDiretaRBTransPermissao = malaDiretaRBTransFacade.recuperarMalaDiretaRBTransPermissao(getIdPermissao());
        selecionado = malaDiretaRBTransFacade.recuperar(malaDiretaRBTransPermissao.getMalaDiretaRBTrans().getId());
        quantidadePermissoesMalaDireta = malaDiretaRBTransFacade.buscarQuantidadeDePermissoesNaMalaDireta(selecionado);
        filtroPermissaoMalaDiretaRBTrans = new FiltroPermissaoMalaDiretaRBTrans();
        filtroPermissaoMalaDiretaRBTrans.setPermissaoInicial(malaDiretaRBTransPermissao.getPermissaoTransporte().getNumero());
        filtroPermissaoMalaDiretaRBTrans.setPermissaoFinal(malaDiretaRBTransPermissao.getPermissaoTransporte().getNumero());
        filtrarPermissoes();
    }

    @URLAction(mappingId = "acompanhamento-mala-direta-rbtrans", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void acompanhamento() {
        try {
            selecionado = malaDiretaRBTransFacade.recuperar(getId());

            mapaPermissaoParcelas = (Map<PermissaoTransporte, List<ResultadoParcela>>) Web.pegaDaSessao(SESSAO);

            adicionarPermissoesNaMalaDireta();
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void iniciarBuscaDasPermissoesDaMalaDiretaDeRBTrans() {
        try {
            validarDadosDaMalaDireta();
            FacesUtil.executaJavaScript("acompanhaConsultaDebitos()");
            idsPermissoes = malaDiretaRBTransFacade.buscarIdsDePermissoesParaMalaDiretaDeRBTrans(selecionado);
            assistenteBarraProgresso = new AssistenteBarraProgresso(malaDiretaRBTransFacade.getSistemaFacade().getUsuarioCorrente(),
                "Buscando débitos da mala direta de permissões do RBTrans", 0);
            if (!idsPermissoes.isEmpty()) {
                futureConsultaDebitosPermissao = Lists.newArrayList();

                mapaPermissaoParcelas = new HashMap<>();
                List<List<Long>> listaParticionada = Lists.partition(idsPermissoes, idsPermissoes.size() > 5 ? idsPermissoes.size() / 5 : 1);
                for (int i = 0; i < listaParticionada.size(); i++) {
                    int index = i;
                    AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
                    assistente.setUsuarioSistema(assistenteBarraProgresso.getUsuarioSistema());
                    assistente.setTotal(0);
                    assistente.setDescricaoProcesso(assistenteBarraProgresso.getDescricao() + " Parte " + (index + 1) + "/" + listaParticionada.size());
                    futureConsultaDebitosPermissao.add(AsyncExecutor.getInstance().execute(assistente,
                        () -> malaDiretaRBTransFacade.buscarDebitosDaMalaDiretaDeRBTrans(selecionado, listaParticionada.get(index))));
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

    public void limparPermissoesSelecionadas() {
        idsPermissoes = null;
        mapaPermissaoParcelas = Maps.newHashMap();
    }

    private void validarDadosDaMalaDireta() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);

        if (selecionado.getTexto() == null || "".equals(selecionado.getTexto().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Texto da Mala Direta!");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void iniciarGeracaoDaMalaDireta() {
        try {
            validarDadosDaMalaDireta();
            selecionado = malaDiretaRBTransFacade.salvarRetornando(selecionado);
            Web.poeNaSessao(SESSAO, mapaPermissaoParcelas);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "acompanhamento/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorPadrao(e);
        }
    }

    private List<Map<PermissaoTransporte, List<ResultadoParcela>>> particionarMapaDePermissoesComDebitos(Map<PermissaoTransporte, List<ResultadoParcela>> mapaParaProcessamento) {
        List<Map<PermissaoTransporte, List<ResultadoParcela>>> mapasParticionados = Lists.newArrayList();

        List<PermissaoTransporte> aParticionar = Lists.newArrayList(mapaParaProcessamento.keySet());
        for (List<PermissaoTransporte> parte : Lists.partition(aParticionar, aParticionar.size() > 5 ? aParticionar.size() / 5 : 1)) {
            Map<PermissaoTransporte, List<ResultadoParcela>> mapa = Maps.newHashMap();
            for (PermissaoTransporte permissaoTransporte : parte) {
                mapa.put(permissaoTransporte, mapaParaProcessamento.get(permissaoTransporte));
            }
            mapasParticionados.add(mapa);
        }
        return mapasParticionados;
    }

    public void adicionarPermissoesNaMalaDireta() throws Exception {
        assistenteBarraProgresso = new AssistenteBarraProgresso("Gerando mala direta", 0);
        futuresPermissoesMalaDireta = Lists.newArrayList();
        assistenteBarraProgresso.setUsuarioSistema(malaDiretaRBTransFacade.getSistemaFacade().getUsuarioCorrente());

        if (!mapaPermissaoParcelas.isEmpty()) {
            List<Map<PermissaoTransporte, List<ResultadoParcela>>> mapaParticionado = particionarMapaDePermissoesComDebitos(mapaPermissaoParcelas);

            assistenteBarraProgresso.setTotal(mapaPermissaoParcelas.size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            for (int i = 0; i < mapaParticionado.size(); i++) {
                int index = i;
                AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
                assistente.setUsuarioSistema(assistenteBarraProgresso.getUsuarioSistema());
                assistente.setTotal(mapaParticionado.size());
                assistente.removerProcessoDoAcompanhamento();
                assistente.setDescricaoProcesso(assistenteBarraProgresso.getDescricao() + "Parte " + (index + 1) + "/" + mapaParticionado.size());
                futuresPermissoesMalaDireta.add(AsyncExecutor.getInstance().execute(assistente,
                    () -> {
                        try {
                            return malaDiretaRBTransFacade.criarMalaDiretaRBTransPermissao(selecionado, mapaParticionado.get(index), assistente);
                        } catch (HeuristicRollbackException | HeuristicMixedException | NotSupportedException |
                                 RollbackException | SystemException e) {
                            throw new RuntimeException(e);
                        }
                    }
                ));
            }
        } else {
            assistenteBarraProgresso.setDescricaoProcesso("Nenhum débito encontrado para as permissões");
            assistenteBarraProgresso.conta();
        }
        FacesUtil.executaJavaScript("acompanhaAdicaoDePermissoes()");
    }

    public List<SelectItem> getTipoPermissao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoCredencialRBTrans tipo : TipoCredencialRBTrans.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoTransporte() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoPermissaoRBTrans tipo : TipoPermissaoRBTrans.values()) {
            if (!TipoPermissaoRBTrans.NAO_ESPECIFICADO.equals(tipo)) {
                retorno.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return retorno;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void imprimirDam(MalaDiretaRBTransPermissao malaDiretaRBTransPermissao) {
        try {
            imprimeDAM.imprimirDamMalaDiretaRBTrans(malaDiretaRBTransFacade.getSistemaFacade().getUsuarioCorrente().getLogin(),
                selecionado.getId(), Lists.newArrayList(malaDiretaRBTransPermissao.getId()));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error("Não foi possível gerar o PDF da mala direta: {}", e);
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void imprimirDams() {
        assistenteImpressaoMalaDiretaRBTrans = new AssistenteImpressaoMalaDiretaRBTrans();
        List<Long> idsDams = Lists.newArrayList(malaDiretaRBTransFacade.buscarIdsDosDamDaMalaDireta(selecionado));
        if (!idsDams.isEmpty()) {
            assistenteImpressaoMalaDiretaRBTrans.setTotal(idsDams.size());
            assistenteImpressaoMalaDiretaRBTrans.setGerados(0);
            listaFuturesGeracaoDam = Lists.newArrayList();

            criarPastaDaMalaDireta();

            int partes = idsDams.size() > 500 ? ((idsDams.size() / 2) + 1) : idsDams.size();
            List<List<Long>> listaDamsPartes = Lists.partition(idsDams, partes);
            assistenteImpressaoMalaDiretaRBTrans.setNumFuture(1);
            assistenteImpressaoMalaDiretaRBTrans.setUsuarioSistema(malaDiretaRBTransFacade.getSistemaFacade().getUsuarioCorrente());
            assistenteImpressaoMalaDiretaRBTrans.setDescricao("Imprimindo dams da mala direta");
            assistenteImpressaoMalaDiretaRBTrans.setImprimeDAM(imprimeDAM);
            assistenteImpressaoMalaDiretaRBTrans.setIdMala(selecionado.getId());
            for (int i = 0; i < listaDamsPartes.size(); i++) {
                int index = i;
                AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
                assistente.setTotal(0);
                assistente.setUsuarioSistema(assistenteImpressaoMalaDiretaRBTrans.getUsuarioSistema());
                assistente.setDescricaoProcesso(assistenteImpressaoMalaDiretaRBTrans.getDescricao() + " Parte " + (index + 1) + "/" + listaDamsPartes.size());
                listaFuturesGeracaoDam.add(AsyncExecutor.getInstance().execute(assistente,
                    () -> malaDiretaRBTransFacade.imprimirDamsMalaDiretaRBTrans(assistenteImpressaoMalaDiretaRBTrans,
                        listaDamsPartes.get(index), getPastaMalaDireta())));
                assistenteImpressaoMalaDiretaRBTrans.setNumFuture(assistenteImpressaoMalaDiretaRBTrans.getNumFuture() + 1);
            }
        }
    }

    private void criarPastaDaMalaDireta() {
        File pasta = new File(getPastaMalaDireta());
        if (!pasta.exists()) {
            pasta.mkdir();
        }
    }

    public void navegarParaVisualizacao() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver-por-permissao/" + getIdPermissao() + "/");
    }

    private void pesquisarPermissoesDaMalaDireta() {
        quantidadeTotal = malaDiretaRBTransFacade.buscarQuantidadePermissoesDaMalaDiretaRBTrans(selecionado, filtroPermissaoMalaDiretaRBTrans);
        permissoesMalaDiretaRBTrans = malaDiretaRBTransFacade.buscarPermissoesMalaDiretaRBTrans(selecionado, filtroPermissaoMalaDiretaRBTrans, indexInicialDaPesquisa, quantidadePorPagina);
    }

    public boolean isPermitidoIrParaPaginaAnterior() {
        return indexInicialDaPesquisa > 0;
    }


    public void irParaPaginaAnterior() {
        indexInicialDaPesquisa = indexInicialDaPesquisa - quantidadePorPagina;
        if (indexInicialDaPesquisa < 0) {
            indexInicialDaPesquisa = 0;
        }
        pesquisarPermissoesDaMalaDireta();
    }


    public boolean isPermitidoIrParaPaginaPosterior() {
        return (indexInicialDaPesquisa + quantidadePorPagina) < quantidadeTotal;
    }

    public void irParaPaginaPosterior() {
        indexInicialDaPesquisa = indexInicialDaPesquisa + quantidadePorPagina;
        pesquisarPermissoesDaMalaDireta();
    }

    public void alterarQuantidadeDeRegistrosDaTabela(Long quantidade) {
        quantidadePorPagina = quantidade.intValue();
        pesquisarPermissoesDaMalaDireta();
    }

    public void filtrarPermissoes() {
        indexInicialDaPesquisa = 0;
        pesquisarPermissoesDaMalaDireta();
    }

    public void addToZip() {
        try {
            File zipFile = File.createTempFile(ARQUIVODOWNLOAD, "zip");

            byte[] buffer = new byte[1024];

            FileOutputStream fout = new FileOutputStream(zipFile);
            ZipOutputStream zout = new ZipOutputStream(fout);
            for (File file : new File(getPastaMalaDireta()).listFiles()) {
                if (file.getName().contains(selecionado.getId().toString())) {
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
        if (pastaMalaDireta == null || pastaMalaDireta.isEmpty()) {
            pastaMalaDireta = AbstractReport.getAbstractReport().getCaminho() + ARQUIVODOWNLOAD + "/";
        }
        return pastaMalaDireta;
    }

    public void acompanharGeracaoDam() {
        boolean terminou = false;
        if (listaFuturesGeracaoDam != null && !listaFuturesGeracaoDam.isEmpty()) {
            terminou = true;
            for (Future<AssistenteImpressaoMalaDiretaRBTrans> future : listaFuturesGeracaoDam) {
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
        if (futureConsultaDebitosPermissao != null && !futureConsultaDebitosPermissao.isEmpty()) {
            terminou = true;
            for (Future<Map<PermissaoTransporte, List<ResultadoParcela>>> future : futureConsultaDebitosPermissao) {
                if (!future.isDone()) {
                    terminou = false;
                }
            }
        }
        if (terminou) {
            for (Future<Map<PermissaoTransporte, List<ResultadoParcela>>> future : futureConsultaDebitosPermissao) {
                try {
                    mapaPermissaoParcelas.putAll(future.get());
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                } catch (ExecutionException e) {
                    logger.error(e.getMessage());
                }
            }
            FacesUtil.executaJavaScript("finalizaConsultaParcelas()");
            if (mapaPermissaoParcelas.isEmpty()) {
                idsPermissoes = Lists.newArrayList();
            }
        }
    }

    public AssistenteImpressaoMalaDiretaRBTrans getAssistenteImpressaoMalaDiretaRBTrans() {
        return assistenteImpressaoMalaDiretaRBTrans;
    }

    public void atualizarTextoMalaDireta() {
        selecionado.setTexto(malaDiretaRBTransFacade.getTextoMalaDiretaPadrao());
    }
}
