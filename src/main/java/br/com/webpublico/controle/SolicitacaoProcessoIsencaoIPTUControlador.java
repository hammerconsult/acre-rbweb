package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.ProcessoIsencaoIPTUFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean(name = "solicitacaoProcessoIsencaoIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSolicitacaoProcessoIsencaoIPTU", pattern = "/solicitacao-processo-de-isencao-de-iptu/novo/", viewId = "/faces/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoProcessoIsencaoIPTU", pattern = "/solicitacao-processo-de-isencao-de-iptu/editar/#{solicitacaoProcessoIsencaoIPTUControlador.id}/", viewId = "/faces/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoProcessoIsencaoIPTU", pattern = "/solicitacao-processo-de-isencao-de-iptu/listar/", viewId = "/faces/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/lista.xhtml"),
    @URLMapping(id = "listarSolicitacaoIsencaoIPTU", pattern = "/solicitacao-processo-de-isencao-de-iptu/listar-isencao/", viewId = "/faces/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/listasolicitacaoisencao.xhtml"),
    @URLMapping(id = "verSolicitacaoProcessoIsencaoIPTU", pattern = "/solicitacao-processo-de-isencao-de-iptu/ver/#{solicitacaoProcessoIsencaoIPTUControlador.id}/", viewId = "/faces/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/visualizar.xhtml"),
    @URLMapping(id = "verSolicitacaoIsencaoIPTU", pattern = "/solicitacao-processo-de-isencao-de-iptu/ver-isencao/#{solicitacaoProcessoIsencaoIPTUControlador.id}/", viewId = "/faces/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/visualizarisencao.xhtml"),
    @URLMapping(id = "verProcessoSolicitacaoIsencaoIPTU", pattern = "/solicitacao-processo-de-isencao-de-iptu/ver-processo-do-cadastro-isencao/#{solicitacaoProcessoIsencaoIPTUControlador.id}/", viewId = "/faces/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/visualizar.xhtml")
})
public class SolicitacaoProcessoIsencaoIPTUControlador extends PrettyControlador<ProcessoIsencaoIPTU> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(SolicitacaoProcessoIsencaoIPTUControlador.class);

    @EJB
    private ProcessoIsencaoIPTUFacade facade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private List<IsencaoCadastroImobiliario> isencaoCadastroImobiliario;
    private CadastroImobiliario cadastroImobiliario;
    private ConverterExercicio converterExercicio;
    private transient Converter converterCategoria;
    private Long numeroProcesso;
    private Exercicio exercicio;
    private Exercicio exercicioReferencia;
    private CategoriaIsencaoIPTU categoriaIsencaoIPTU;
    private CompletableFuture<ProcessoIsencaoIPTUFacade.GerenciadorProcessoIsencaoIPTU> future;
    private CompletableFuture<List<DocumentoOficial>> futureDocumentos;
    private ProcessoIsencaoIPTUFacade.GerenciadorProcessoIsencaoIPTU gerenciador;
    private IsencaoCadastroImobiliario isencao;
    private List<Propriedade> propriedades;
    private List<CadastroImobiliario> cadastrosEnquadrados;
    private List<CadastroImobiliario> cadastrosNaoEnquadrados;

    public SolicitacaoProcessoIsencaoIPTUControlador() {
        super(ProcessoIsencaoIPTU.class);
    }

    public List<CadastroImobiliario> getCadastrosEnquadrados() {
        return Lists.transform(selecionado.getCadastrosEnquadrados(), new Function<CadastroIsencaoIPTU, CadastroImobiliario>() {
            @Override
            public CadastroImobiliario apply(@Nullable CadastroIsencaoIPTU cadastroIsencaoIPTU) {
                return cadastroIsencaoIPTU.getCadastro();
            }
        });
    }

    public List<CadastroImobiliario> getCadastrosNaoEnquadrados() {
        return Lists.transform(selecionado.getCadastrosNaoEnquadrados(), new Function<CadastroIsencaoIPTU, CadastroImobiliario>() {
            @Override
            public CadastroImobiliario apply(@Nullable CadastroIsencaoIPTU cadastroIsencaoIPTU) {
                return cadastroIsencaoIPTU.getCadastro();
            }
        });
    }

    public IsencaoCadastroImobiliario getIsencao() {
        return isencao;
    }

    public void setIsencao(IsencaoCadastroImobiliario isencao) {
        this.isencao = isencao;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ProcessoIsencaoIPTUFacade.GerenciadorProcessoIsencaoIPTU getGerenciador() {
        return gerenciador;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public CategoriaIsencaoIPTU getCategoriaIsencaoIPTU() {
        return categoriaIsencaoIPTU;
    }

    public void setCategoriaIsencaoIPTU(CategoriaIsencaoIPTU categoriaIsencaoIPTU) {
        this.categoriaIsencaoIPTU = categoriaIsencaoIPTU;
    }

    public List<Propriedade> getPropriedades() {
        return propriedades;
    }

    public void setPropriedades(List<Propriedade> propriedades) {
        this.propriedades = propriedades;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-processo-de-isencao-de-iptu/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @URLAction(mappingId = "listarSolicitacaoProcessoIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        exercicio = getSistemaControlador().getExercicioCorrente();
        exercicioReferencia = getSistemaControlador().getExercicioCorrente();
        cadastroImobiliario = new CadastroImobiliario();
        numeroProcesso = null;
    }

    @URLAction(mappingId = "listarSolicitacaoIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listarSolicitacaoIPTU() {
        exercicio = getSistemaControlador().getExercicioCorrente();
        exercicioReferencia = getSistemaControlador().getExercicioCorrente();
        cadastroImobiliario = new CadastroImobiliario();
        numeroProcesso = null;
    }

    @URLAction(mappingId = "novaSolicitacaoProcessoIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cadastroImobiliario = new CadastroImobiliario();
        selecionado.setExercicioProcesso(getSistemaControlador().getExercicioCorrente());
        selecionado.setExercicioReferencia(getSistemaControlador().getExercicioCorrente());
        selecionado.setUsuarioResponsavel(getSistemaControlador().getUsuarioCorrente());
        inicializarCadastros();
    }

    private void inicializarCadastros() {
        cadastrosEnquadrados = Lists.newArrayList();
        cadastrosNaoEnquadrados = Lists.newArrayList();
    }

    public Converter getConverterCategoria() {
        if (converterCategoria == null) {
            converterCategoria = new ConverterAutoComplete(CategoriaIsencaoIPTU.class, facade.getCategoriaIsencaoIPTUFacade());
        }
        return converterCategoria;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public List<SelectItem> getCategorias() {
        List<SelectItem> retorno = Lists.newArrayList();
        List<CategoriaIsencaoIPTU> categorias = facade.getCategoriaIsencaoIPTUFacade().listaPorExercicio(getSelecionado().getExercicioReferencia());
        retorno.add(new SelectItem(null, " "));
        for (CategoriaIsencaoIPTU cat : categorias) {
            retorno.add(new SelectItem(cat, cat.getDescricao()));
        }
        return retorno;
    }

    @URLAction(mappingId = "editarSolicitacaoProcessoIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarCadastros();
    }

    @URLAction(mappingId = "verSolicitacaoProcessoIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "verSolicitacaoIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verSolicitacaoIsencao() {
        isencao = facade.recuperaIsencao(getId());
        selecionado = facade.recuperar(isencao.getProcessoIsencaoIPTU().getId());
        propriedades = cadastroImobiliarioFacade.recuperarProprietariosVigentes(isencao.getCadastroImobiliario());
    }

    @URLAction(mappingId = "verProcessoSolicitacaoIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verProcessoDaSolicitacao() {
        CadastroIsencaoIPTU cadastro = facade.recuperarCadastro(getId());
        selecionado = facade.recuperar(cadastro.getProcesso().getId());
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    @Override
    public void salvar() {
        try {
            validarProcesso(true);
            validarProcessoPorCadastro();
            if (selecionado.getId() == null) {
                selecionado.setNumero(facade.buscarUltimoCodigo(selecionado.getExercicioProcesso()));
            }
            validarNumeroProcesso();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("Erro: ", e);
        }
    }

    private List<Long> idsCadastros() {
        List<Long> ids = new ArrayList<>();
        if (!cadastrosEnquadrados.isEmpty()) {
            for (CadastroImobiliario cadEnquadrados : cadastrosEnquadrados) {
                ids.add(cadEnquadrados.getId());
            }
        }
        if (!cadastrosNaoEnquadrados.isEmpty()) {
            for (CadastroImobiliario cadNaoEnquadrados : cadastrosNaoEnquadrados) {
                ids.add(cadNaoEnquadrados.getId());
            }
        }
        return ids;
    }

    private void validarProcessoPorCadastro() {
        ValidacaoException ve = new ValidacaoException();
        if (!idsCadastros().isEmpty()) {
            Integer exercicio = (selecionado != null && selecionado.getExercicioReferencia() != null) ? selecionado.getExercicioReferencia().getAno() : null;
            CadastroIsencaoIPTU cadastroIsencaoIPTU = facade.buscarProcessoPorCadastro(idsCadastros(), exercicio);
            if (cadastroIsencaoIPTU != null) {
                if (cadastroIsencaoIPTU.getProcesso().getSituacao().isAberto()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma solicitação de isenção Em Aberto com os Cadastros Imobiliários informados.");
                }
                ve.lancarException();
            }
        }
    }

    private void hasCadastrosIsencao() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastrosEnquadrados.isEmpty() && cadastrosNaoEnquadrados.isEmpty()) {
            FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Nenhum cadastro foi encontrado para a categoria de isenção informada!");
        }
        ve.lancarException();
    }

    public void limparCadastrosFiltro() {
        selecionado.getCadastrosEnquadrados().clear();
        selecionado.getCadastrosNaoEnquadrados().clear();
        inicializarCadastros();
    }

    public void filtrarCadastros() {
        try {
            validarProcesso(false);
            limparCadastrosFiltro();
            cadastrosEnquadrados = facade.buscarCadastros(selecionado.getCategoriaIsencaoIPTU(), selecionado.getInscricaoInicial(), selecionado.getInscricaoFinal(), true);
            List<CadastroImobiliario> proprietariosDuplicados = facade.retirarCadastrosDeProprietariosDuplicadosDeAcordoComCategoria(cadastrosEnquadrados, selecionado.getCategoriaIsencaoIPTU());
            cadastrosNaoEnquadrados = facade.buscarCadastros(selecionado.getCategoriaIsencaoIPTU(), selecionado.getInscricaoInicial(), selecionado.getInscricaoFinal(), false);
            cadastrosNaoEnquadrados.addAll(proprietariosDuplicados);
            Collections.sort(cadastrosNaoEnquadrados);

            hasCadastrosIsencao();
            selecionado.setCadastrosEnquadrados(Lists.newArrayList(Lists.transform(cadastrosEnquadrados, new Function<CadastroImobiliario, CadastroIsencaoIPTU>() {
                @Override
                public CadastroIsencaoIPTU apply(@Nullable CadastroImobiliario cadastroImobiliario) {
                    CadastroIsencaoIPTU cadastroIsencaoIPTU = new CadastroIsencaoIPTU();
                    cadastroIsencaoIPTU.setCadastro(cadastroImobiliario);
                    cadastroIsencaoIPTU.setEnquadra(true);
                    cadastroIsencaoIPTU.setProcesso(selecionado);
                    return cadastroIsencaoIPTU;
                }
            })));
            selecionado.setCadastrosNaoEnquadrados(Lists.newArrayList(Lists.transform(cadastrosNaoEnquadrados, new Function<CadastroImobiliario, CadastroIsencaoIPTU>() {
                @Override
                public CadastroIsencaoIPTU apply(@Nullable CadastroImobiliario cadastroImobiliario) {
                    CadastroIsencaoIPTU cadastroIsencaoIPTU = new CadastroIsencaoIPTU();
                    cadastroIsencaoIPTU.setCadastro(cadastroImobiliario);
                    cadastroIsencaoIPTU.setEnquadra(false);
                    cadastroIsencaoIPTU.setProcesso(selecionado);
                    return cadastroIsencaoIPTU;
                }
            })));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("Erro: ", e);
        }
    }

    public void criarSolicitacoesDeIsencaoDosCadastros() {
        try {
            selecionado.setSituacao(ProcessoIsencaoIPTU.Situacao.EFETIVADO);
            selecionado = facade.atualizar(selecionado);
            gerenciador = new ProcessoIsencaoIPTUFacade.GerenciadorProcessoIsencaoIPTU(sistemaFacade.getUsuarioCorrente().getLogin(),
                "Efetivação da Solicitação de Processo de Isenção de Calculo de IPTU [" + selecionado.getNumeroProcessoComExercicio() + "]",
                getCadastrosEnquadrados(), selecionado);
            future = AsyncExecutor.getInstance().execute(gerenciador, () -> facade.criarIsencoesCadastros(gerenciador));
            FacesUtil.executaJavaScript("inicia()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void emitirTodosDocumentosOficiais() {
        futureDocumentos = AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
                "Recuperando documentos para impressão do termo de isenção [" + selecionado.toString() + "]", 0),
            () -> {
                try {
                    return facade.recuperaTodosDocumentos(selecionado, sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getIp());
                } catch (Exception e) {
                    logger.error("Erro ao emitir todos documentos do processo de isenção de iptu. Erro {}", e.getMessage());
                    logger.debug("Stacktrace ->", e);
                }
                return null;
            });
        FacesUtil.executaJavaScript("iniciaDocumentos()");
    }

    public void imprimirTodosDocumentosOficiais() {
        try {
            DocumentoOficial[] documentoOficials = futureDocumentos.get().toArray(new DocumentoOficial[futureDocumentos.get().size()]);
            facade.getDocumentoOficialFacade().emiteDocumentoOficial(documentoOficials);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

    public void consultaRecuperaDocumento() {
        if (futureDocumentos != null && futureDocumentos.isDone()) {
            FacesUtil.executaJavaScript(" aguarde.hide();");
            FacesUtil.executaJavaScript("termina()");
            FacesUtil.executaJavaScript("imprimeTermos.show();");
        }
    }

    public void consultaProcesso() {
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("termina();");
            FacesUtil.executaJavaScript(" processando.hide();");
            FacesUtil.executaJavaScript("conclusao.show();");
            FacesUtil.atualizarComponente("formConclusao");
        }
    }

    private void validarNumeroProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getNumero() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Código");
        } else if (facade.existeCodigo(selecionado)) {
            selecionado.setNumero(selecionado.getNumero() + 1);
            ve.adicionarMensagemDeOperacaoNaoRealizada("Já existia um processo com o mesmo código, foi gerado um novo automaticamente, clique em salvar outra vez para concluir a operação");
        }
        ve.lancarException();
    }

    private void validarProcesso(boolean validarItens) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Início da Vigência");
        }
        if (selecionado.getValidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Fim da Vigência");
        }
        if (selecionado.getDataLancamento() != null && selecionado.getValidade() != null && selecionado.getDataLancamento().after(selecionado.getValidade())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Final de Vigência deve ser posterior ao Início de Vigência.");
        }
        if (selecionado.getExercicioProcesso() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício do Processo");
        }
        if (selecionado.getNumeroProtocolo() == null || selecionado.getNumeroProtocolo().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Número do Protocolo");
        }
        if (selecionado.getAnoProtocolo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano do Protocolo");
        }
        if (selecionado.getCategoriaIsencaoIPTU() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Categoria de Isenção");
        }
        if (validarItens) {
            if (selecionado.getCadastrosEnquadrados().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione ao menos um Cadastro Imobiliário ao processo.");
            }
        }
        ve.lancarException();
    }

    public void imprimirDocumentoOficial(IsencaoCadastroImobiliario isencao) {
        if (isencao != null) {
            ProcessoIsencaoIPTU processoIsencaoIPTU = facade.getIsencaoIPTUFacade().recupararProcessoIsencaoPeloIdIsencao(isencao.getId());
            if (processoIsencaoIPTU != null && processoIsencaoIPTU.getCategoriaIsencaoIPTU() != null && processoIsencaoIPTU.getCategoriaIsencaoIPTU().getTipoDoctoOficial() != null) {
                try {
                    facade.getIsencaoIPTUFacade().imprimirDoctoOficial(isencao);

                } catch (Exception e) {
                    logger.error("Erro ao imprimir docto. oficial. ", e);
                    FacesUtil.addOperacaoNaoRealizada("Erro ao imprimir Documento Oficial. Detalhes: " + e.getMessage());
                }
            } else {
                FacesUtil.addOperacaoNaoRealizada("A Categoria de Isenção não possui um Tipo de Documento Oficial informado!");
            }
        }
    }

    public List<IsencaoCadastroImobiliario> getIsencaoCadastroImobiliario() {
        return isencaoCadastroImobiliario;
    }

    public void setIsencaoCadastroImobiliario(List<IsencaoCadastroImobiliario> isencaoCadastroImobiliario) {
        this.isencaoCadastroImobiliario = isencaoCadastroImobiliario;
    }

    public void limpaCampos() {
        cadastroImobiliario = new CadastroImobiliario();
        isencaoCadastroImobiliario = null;
        numeroProcesso = null;
        exercicio = getSistemaControlador().getExercicioCorrente();
        categoriaIsencaoIPTU = new CategoriaIsencaoIPTU();
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(facade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = Lists.newArrayList();
        for (Exercicio ex : facade.getExercicioFacade().listaExerciciosAtual()) {
            lista.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return lista;
    }

    public Long getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(Long numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Exercicio getExercicioReferencia() {
        return exercicioReferencia;
    }

    public void setExercicioReferencia(Exercicio exercicioReferencia) {
        this.exercicioReferencia = exercicioReferencia;
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(facade.getArquivoFacade().getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            ProcessoIsencaoIPTUArquivos arquivo = new ProcessoIsencaoIPTUArquivos();
            arquivo.setArquivo(facade.getArquivoFacade().novoArquivoMemoria(arq, file.getFile().getInputstream()));
            arquivo.setProcessoIsencaoIPTU(selecionado);

            selecionado.getArquivos().add(arquivo);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void removeArquivo(ActionEvent evento) {
        ProcessoIsencaoIPTUArquivos arq = (ProcessoIsencaoIPTUArquivos) evento.getComponent().getAttributes().get("objeto");
        selecionado.getArquivos().remove(arq);
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        cadastroImobiliario = ((CadastroImobiliario) obj);
    }

    public void imprimeRelacao(boolean enquadra) {
        String where = facade.montarWhere(selecionado.getCategoriaIsencaoIPTU(), selecionado.getInscricaoInicial(), selecionado.getInscricaoFinal(), enquadra);
        new GeraRelatorio().geraRelatorio(where,
            facade.getSistemaFacade().getUsuarioCorrente().getLogin(),
            (enquadra ? "Relação de Conferência da Solicitação do Processo de Isenção - Imóveis Enquadrados"
                : "Relação de Conferência da Solicitação do Processo de Isenção - Imóveis Não Enquadrados"));
    }

    public void imprimirRelacaoSolicitacao() {
        String where = facade.montarWhereProcessoSalvo(selecionado);
        new GeraRelatorio().geraRelatorio(where,
            facade.getSistemaFacade().getUsuarioCorrente().getLogin(),
            "Relação de Conferência da Solicitação do Processo de Isenção - Imóveis Enquadrados");
    }

    public boolean isPossivelEmitirTermo(IsencaoCadastroImobiliario isencaoCadastroImobiliario) {
        if (IsencaoCadastroImobiliario.Situacao.CANCELADO.equals(isencaoCadastroImobiliario.getSituacao())) {
            return false;
        }
        return isPreenchidoTipoDoctoNaCategoria(isencaoCadastroImobiliario);
    }

    public boolean isPreenchidoTipoDoctoNaCategoria(IsencaoCadastroImobiliario isencaoCadastroImobiliario) {
        return isencaoCadastroImobiliario.getProcessoIsencaoIPTU().getCategoriaIsencaoIPTU() != null &&
            isencaoCadastroImobiliario.getProcessoIsencaoIPTU().getCategoriaIsencaoIPTU().getTipoDoctoOficial() != null;
    }

    public class GeraRelatorio extends AbstractReport {
        public void geraRelatorio(String where, String usuario, String nomeRelatorio) {
            String arquivoJasper = "RelacaoConferenciaProcessoIsencao.jasper";
            HashMap parameters = new HashMap();
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("USUARIO", usuario);
            parameters.put("NOME_RELATORIO", nomeRelatorio);
            parameters.put("WHERE", where);
            try {
                gerarRelatorio(arquivoJasper, parameters);
            } catch (Exception ex) {
                FacesUtil.addError("Um problema inesperado aconteceu ao gerar o relatório", " O arquivo gerou " + ex.getMessage() + " ");
                logger.error("Erro: ", ex);
            }
        }
    }

    public void removerEnquadrados(CadastroImobiliario cadastroImobiliario) {
        if (selecionado.getCadastrosEnquadrados() != null) {
            for (CadastroIsencaoIPTU cadastro : Lists.newArrayList(selecionado.getCadastrosEnquadrados())) {
                if (cadastro.getCadastro().equals(cadastroImobiliario)) {
                    selecionado.getCadastrosEnquadrados().remove(cadastro);
                    break;
                }
            }
        }
    }

    public void redirecionarParaVisualizar() {
        if (selecionado != null && selecionado.getId() != null) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }
}
