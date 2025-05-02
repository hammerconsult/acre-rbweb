package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.*;
import br.com.webpublico.entidadesauxiliares.DAMResultadoParcela;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.tributario.OrigemProcessoLicenciamentoAmbiental;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.tributario.ProcessoLicenciamentoAmbientalFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.Hibernate;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProcessoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/processo/novo/",
        viewId = "/faces/tributario/licenciamentoambiental/processo/edita.xhtml"),
    @URLMapping(id = "editarProcessoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/processo/editar/#{processoLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/processo/edita.xhtml"),
    @URLMapping(id = "verProcessoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/processo/ver/#{processoLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/processo/visualizar.xhtml"),
    @URLMapping(id = "listarProcessoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/processo/listar/",
        viewId = "/faces/tributario/licenciamentoambiental/processo/lista.xhtml")
})
public class ProcessoLicenciamentoAmbientalControlador extends PrettyControlador<ProcessoLicenciamentoAmbiental> implements CRUD, Serializable {

    @EJB
    private ProcessoLicenciamentoAmbientalFacade facade;
    private Boolean permitirAtribuirMaisTecnicos;
    private ConverterAutoComplete converterPessoa;
    private ProcessoLicenciamentoAmbientalDocumento processoLicenciamentoAmbientalDocumento;
    private ProcessoLicenciamentoAmbientalPessoa processoLicenciamentoAmbientalPessoa;
    private List<ControleLicenciamentoAmbiental> controles;
    private ControleLicenciamentoAmbiental controleSelecionado;
    private RevisaoControleLicenciamentoAmbiental revisaoParecer;
    private List<DAMResultadoParcela> parcelas;
    private ProcessoLicenciamentoAmbientalUnidadeMedida processoUnidadeMedidaAmbiental;
    private TecnicoProcessoLicenciamentoAmbiental tecnicoProcessoLicenciamentoAmbiental;
    private CNAE cnaeSelecionado;
    private final HashMap<Long, BigDecimal> mapValorTotalUnidadeMedida = Maps.newHashMap();
    ParametroLicenciamentoAmbiental parametroLicenciamentoAmbiental;
    private AnexoControleLicenciamentoAmbiental anexoRevisaoSelecionado;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-ambiental/processo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ProcessoLicenciamentoAmbientalControlador() {
        super(ProcessoLicenciamentoAmbiental.class);
    }

    public List<ControleLicenciamentoAmbiental> getControles() {
        return controles;
    }

    public void setControles(List<ControleLicenciamentoAmbiental> controles) {
        this.controles = controles;
    }

    public ControleLicenciamentoAmbiental getControleSelecionado() {
        return controleSelecionado;
    }

    public void setControleSelecionado(ControleLicenciamentoAmbiental controleSelecionado) {
        this.controleSelecionado = controleSelecionado;
    }

    public RevisaoControleLicenciamentoAmbiental getRevisaoParecer() {
        return revisaoParecer;
    }

    public void setRevisaoParecer(RevisaoControleLicenciamentoAmbiental revisaoParecer) {
        this.revisaoParecer = revisaoParecer;
    }

    public List<DAMResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<DAMResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public ProcessoLicenciamentoAmbientalUnidadeMedida getProcessoUnidadeMedidaAmbiental() {
        return processoUnidadeMedidaAmbiental;
    }

    public void setProcessoUnidadeMedidaAmbiental(ProcessoLicenciamentoAmbientalUnidadeMedida processoUnidadeMedidaAmbiental) {
        this.processoUnidadeMedidaAmbiental = processoUnidadeMedidaAmbiental;
    }

    public CNAE getCnaeSelecionado() {
        return cnaeSelecionado;
    }

    public void setCnaeSelecionado(CNAE cnaeSelecionado) {
        this.cnaeSelecionado = cnaeSelecionado;
    }

    public TecnicoProcessoLicenciamentoAmbiental getTecnicoProcessoLicenciamentoAmbiental() {
        return tecnicoProcessoLicenciamentoAmbiental;
    }

    public void setTecnicoProcessoLicenciamentoAmbiental(TecnicoProcessoLicenciamentoAmbiental tecnicoProcessoLicenciamentoAmbiental) {
        this.tecnicoProcessoLicenciamentoAmbiental = tecnicoProcessoLicenciamentoAmbiental;
    }

    public Boolean getPermitirAtribuirMaisTecnicos() {
        return permitirAtribuirMaisTecnicos;
    }

    public void setPermitirAtribuirMaisTecnicos(Boolean permitirAtribuirMaisTecnicos) {
        this.permitirAtribuirMaisTecnicos = permitirAtribuirMaisTecnicos;
    }

    @URLAction(mappingId = "verProcessoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado = facade.recuperar(super.getId());
        controles = facade.getControleLicenciamentoAmbientalFacade().buscarControlesPorProcesso(selecionado);
        parcelas = buscarParcelas();
        tecnicoProcessoLicenciamentoAmbiental = new TecnicoProcessoLicenciamentoAmbiental();
        parametroLicenciamentoAmbiental = facade.getParametroLicenciamentoAmbientalFacade().buscarParametroPeloExercicio(selecionado.getExercicio());
        permitirAtribuirMaisTecnicos = facade.getUsuarioSistemaFacade()
            .validaAutorizacaoUsuario(facade.getSistemaFacade().getUsuarioCorrente(),
                AutorizacaoTributario.PERMITIR_ATRIBUIR_MAIS_TECNICOS_AO_PROCESSO_LICENCIAMENTO_AMBIENTAL);
        for (ProcessoLicenciamentoAmbientalUnidadeMedida processoUnidadeMedida : selecionado.getProcessoUnidadeMedidas()) {
            popularMapValorTotalUnidadeMedida(processoUnidadeMedida);
        }
    }

    private List<DAMResultadoParcela> buscarParcelas() {
        List<ProcessoCalculoLicenciamentoAmbiental> processosCalculoLicenciamentoAmbiental = facade.recuperarProcessoCalculo(selecionado);
        if (processosCalculoLicenciamentoAmbiental.isEmpty()) return Lists.newArrayList();
        List<Long> idsCalculos = Lists.newArrayList();
        for (ProcessoCalculoLicenciamentoAmbiental processoCalculoLicenciamentoAmbiental : processosCalculoLicenciamentoAmbiental) {
            idsCalculos.add(processoCalculoLicenciamentoAmbiental.getCalculos().get(0).getId());
        }
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCalculos);
        List<ResultadoParcela> resultados = consultaParcela.executaConsulta().getResultados();
        return resultados.stream().map((r) -> {
            DAMResultadoParcela damResultadoParcela = new DAMResultadoParcela();
            damResultadoParcela.setResultadoParcela(r);
            damResultadoParcela.setDam(facade.getDamFacade().recuperarDAM(r.getIdParcela()));
            return damResultadoParcela;
        }).collect(Collectors.toList());
    }

    @URLAction(mappingId = "editarProcessoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        controles = facade.getControleLicenciamentoAmbientalFacade().buscarControlesPorProcesso(selecionado);
        tecnicoProcessoLicenciamentoAmbiental = new TecnicoProcessoLicenciamentoAmbiental();
        permitirAtribuirMaisTecnicos = facade.getUsuarioSistemaFacade()
            .validaAutorizacaoUsuario(facade.getSistemaFacade().getUsuarioCorrente(),
                AutorizacaoTributario.PERMITIR_ATRIBUIR_MAIS_TECNICOS_AO_PROCESSO_LICENCIAMENTO_AMBIENTAL);
        for (ProcessoLicenciamentoAmbientalUnidadeMedida processoUnidadeMedida : selecionado.getProcessoUnidadeMedidas()) {
            popularMapValorTotalUnidadeMedida(processoUnidadeMedida);
        }
        criarNovaUnidadeMedida();
    }

    @URLAction(mappingId = "novoProcessoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setStatus(StatusLicenciamentoAmbiental.ABERTO);
        controles = Lists.newArrayList();
        parcelas = Lists.newArrayList();
        criarNovaUnidadeMedida();
        mudouOrigemProcesso();
    }

    public AnexoControleLicenciamentoAmbiental getAnexoRevisaoSelecionado() {
        return anexoRevisaoSelecionado;
    }

    public void setAnexoRevisaoSelecionado(AnexoControleLicenciamentoAmbiental anexoRevisaoSelecionado) {
        this.anexoRevisaoSelecionado = anexoRevisaoSelecionado;
    }

    public ProcessoLicenciamentoAmbientalDocumento getProcessoLicenciamentoAmbientalDocumento() {
        return processoLicenciamentoAmbientalDocumento;
    }

    public void setProcessoLicenciamentoAmbientalDocumento(ProcessoLicenciamentoAmbientalDocumento processoLicenciamentoAmbientalDocumento) {
        this.processoLicenciamentoAmbientalDocumento = processoLicenciamentoAmbientalDocumento;
    }

    public List<SelectItem> getTodasOrigensProcesso() {
        return Util.getListSelectItem(OrigemProcessoLicenciamentoAmbiental.values(), false);
    }


    public boolean renderDescricao() {
        return selecionado.getOrigemProcesso() != null
            && !selecionado.getOrigemProcesso().equals(OrigemProcessoLicenciamentoAmbiental.ONLINE);
    }

    public boolean renderNumeroDocumento() {
        return selecionado.getOrigemProcesso() != null
            && OrigemProcessoLicenciamentoAmbiental.RBDOC.equals(selecionado.getOrigemProcesso());
    }

    public void carregarDocumentosObrigatorios() {
        selecionado.getProcessoUnidadeMedidas().clear();
        facade.inicializarDocumentosObrigatorios(selecionado);
        novoAnexoRevisaoSelecionado();
    }

    public void selecionarDocumento(ProcessoLicenciamentoAmbientalDocumento processoLicenciamentoAmbientalDocumento) {
        this.processoLicenciamentoAmbientalDocumento = processoLicenciamentoAmbientalDocumento;
    }

    public void substituirDocumento(ProcessoLicenciamentoAmbientalDocumento processoLicenciamentoAmbientalDocumento) {
        selecionarDocumento(processoLicenciamentoAmbientalDocumento);
        this.processoLicenciamentoAmbientalDocumento.setDocumento(null);
        FacesUtil.executaJavaScript("dlgUploadDocumento.show()");
        FacesUtil.atualizarComponente("formUploadDocumento");
    }

    public void selecionarProcessoPessoa(ProcessoLicenciamentoAmbientalPessoa processoLicenciamentoAmbientalPessoa) {
        this.processoLicenciamentoAmbientalPessoa = processoLicenciamentoAmbientalPessoa;
    }

    public void substituirDocumentoPessoal(ProcessoLicenciamentoAmbientalPessoa processoLicenciamentoAmbientalPessoa) {
        selecionarProcessoPessoa(processoLicenciamentoAmbientalPessoa);
        this.processoLicenciamentoAmbientalPessoa.setArquivo(null);
        FacesUtil.executaJavaScript("dlgUploadDocumento.show()");
        FacesUtil.atualizarComponente("formUploadDocumentoPessoal");
    }

    public boolean desabilitarSelecaoOrigem() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public void redirecionarParaPortalContribuinte() {
        FacesUtil.redirecionamentoExterno(facade.getConfiguracaoTributarioFacade().recuperarUrlPortal() + "/solicitacao-cadastro/", true);
    }

    public List<Pessoa> completarPessoa(String parte) {
        if (OrigemProcessoLicenciamentoAmbiental.REDE_SIM.equals(selecionado.getOrigemProcesso())) {
            return facade.getPessoaFacade().listaPessoasJuridicas(parte, SituacaoCadastralPessoa.ATIVO);
        }
        return facade.getPessoaFacade().listaTodasPessoasAtivas(parte);
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, facade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public void uploadDocumento(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            processoLicenciamentoAmbientalDocumento.setDocumento(arquivo);
            FacesUtil.executaJavaScript("dlgUploadDocumento.hide()");
            FacesUtil.atualizarComponente("Formulario:tbView:opDadosGerais");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void uploadDocumentoPessoal(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            processoLicenciamentoAmbientalPessoa.setArquivo(arquivo);
            FacesUtil.executaJavaScript("dlgUploadDocumentoPessoal.hide()");
            FacesUtil.atualizarComponente("Formulario:tbView:opRequerente");
            FacesUtil.atualizarComponente("Formulario:tbView:opRepresentante");
            FacesUtil.atualizarComponente("Formulario:tbView:opProcurador");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public String buscarExtensoesPermitidas(ProcessoLicenciamentoAmbientalDocumento processoDocumento) {
        String extensoesPermitidas = processoDocumento.getDocumentoLicenciamentoAmbiental().getExtensoesPermitidas();
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        extensoesPermitidas = extensoesPermitidas.replace(".", "");
        extensoesPermitidas = extensoesPermitidas.replace(",", "|");
        extensoesPermitidas = extensoesPermitidas.replace(" ", "");
        return "/(\\.|\\/)(" + extensoesPermitidas + ")$/";
    }

    public String buscarMensagemExtensoesPermitidas(ProcessoLicenciamentoAmbientalDocumento processoDocumento) {
        String extensoesPermitidas = processoDocumento.getDocumentoLicenciamentoAmbiental().getExtensoesPermitidas();
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        return "Extensão invalida, apenas as extenções (" + extensoesPermitidas + ") são permitidas.";
    }

    public void mudouOrigemProcesso() {
        selecionado.setNumeroDocumento(null);
        selecionado.setDescricao(null);
        selecionado.setRequerente(new ProcessoLicenciamentoAmbientalPessoa());
        selecionado.getRequerente().setEnderecoCorreio(new EnderecoCorreio());
        selecionado.getRequerente().setEnderecoCorrespondencia(new EnderecoCorreio());
        selecionado.setRepresentante(new ProcessoLicenciamentoAmbientalPessoa());
        selecionado.getRepresentante().setEnderecoCorreio(new EnderecoCorreio());
        selecionado.setProcurador(new ProcessoLicenciamentoAmbientalPessoa());
        selecionado.getProcurador().setEnderecoCorreio(new EnderecoCorreio());
    }

    public void mudouRequerente() {
        selecionado.getRequerente().setTelefone("");
        selecionado.getRequerente().setEmail("");
        selecionado.getRequerente().setEnderecoCorreio(new EnderecoCorreio());
        selecionado.getRequerente().setEnderecoCorrespondencia(new EnderecoCorreio());
        selecionado.getRequerente().setArquivo(null);
        if (selecionado.getRequerente().getPessoa() != null) {
            EnderecoCorreio enderecoPorPrioridade = selecionado.getRequerente().getPessoa().getEnderecoPorPrioridade();
            if (enderecoPorPrioridade != null) {
                selecionado.getRequerente().getEnderecoCorrespondencia().setLogradouro(enderecoPorPrioridade.getLogradouro());
                selecionado.getRequerente().getEnderecoCorrespondencia().setBairro(enderecoPorPrioridade.getBairro());
                selecionado.getRequerente().getEnderecoCorrespondencia().setNumero(enderecoPorPrioridade.getNumero());
                selecionado.getRequerente().getEnderecoCorrespondencia().setComplemento(enderecoPorPrioridade.getComplemento());
                selecionado.getRequerente().getEnderecoCorrespondencia().setCep(enderecoPorPrioridade.getCep());
                selecionado.getRequerente().getEnderecoCorrespondencia().setLocalidade(enderecoPorPrioridade.getLocalidade());
                selecionado.getRequerente().getEnderecoCorrespondencia().setUf(enderecoPorPrioridade.getUf());
            }
        }
    }

    public void mudouRepresentante() {
        selecionado.getRepresentante().setTelefone("");
        selecionado.getRepresentante().setEmail("");
        selecionado.getRepresentante().setEnderecoCorreio(new EnderecoCorreio());
        selecionado.getRepresentante().setArquivo(null);
    }

    public void mudouProcurador() {
        selecionado.getProcurador().setTelefone("");
        selecionado.getProcurador().setEmail("");
        selecionado.getProcurador().setEnderecoCorreio(new EnderecoCorreio());
        selecionado.getProcurador().setArquivo(null);
    }

    public boolean tipoIsQuantidade() {
        return hasTipoUnidadeSelecionada() &&
            processoUnidadeMedidaAmbiental.getUnidadeMedidaAmbiental().getTipoUnidade().equals(UnidadeMedidaAmbiental.TipoUnidade.QUANTIDADE);
    }

    public List<SelectItem> getUnidadesMedida() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        if (selecionado.getAssuntoLicenciamentoAmbiental() != null) {
            for (UnidadeMedidaAmbiental unidadeMedida : facade.getUnidadeMedidaAmbientalFacade().unidadesMedidaAmbientalAtivasPorAssunto(selecionado.getAssuntoLicenciamentoAmbiental().getId())) {
                retorno.add(new SelectItem(unidadeMedida, unidadeMedida.getDescricao()));
            }
        }
        return retorno;
    }

    public void alterouTipoDeUnidade() {
        processoUnidadeMedidaAmbiental.setValorUnidadeMedidaAmbiental(null);
        FacesUtil.atualizarComponente("Formulario:tbView:opAtividade");
    }

    public void alterouCategoriaUnidade() {
        AssuntoLicenciamentoAmbiental assunto = selecionado.getAssuntoLicenciamentoAmbiental();
        if (assunto != null && processoUnidadeMedidaAmbiental.getCategoria() != null) {
            if (!Hibernate.isInitialized(assunto.getCategorias())) {
                assunto = facade.getAssuntoLicenciamentoAmbientalFacade().recuperar(assunto.getId());
            }
            for (CategoriaAssuntoLicenciamentoAmbiental categoria : assunto.getCategorias()) {
                if (categoria.getCategoria().equals(processoUnidadeMedidaAmbiental.getCategoria())) {
                    processoUnidadeMedidaAmbiental.setValorUfmCategoria(categoria.getValorUFM());
                    break;
                }
            }
        }
        if (processoUnidadeMedidaAmbiental.getCategoria() == null) {
            processoUnidadeMedidaAmbiental.setValorUfmCategoria(null);
        }
        FacesUtil.atualizarComponente("Formulario:tbView:opAtividade");
    }

    public boolean hasTipoUnidadeSelecionada() {
        return processoUnidadeMedidaAmbiental != null &&
            processoUnidadeMedidaAmbiental.getUnidadeMedidaAmbiental() != null;
    }

    public List<ProcessoLicenciamentoAmbiental> completarProcessos(String parte) {
        return facade.buscarProcessosPorParte(parte);
    }

    public StreamedContent baixarAnexosParecer(ControleLicenciamentoAmbiental controle) throws IOException {
        List<Arquivo> arquivos = controle.getAnexos()
            .stream().map(AnexoControleLicenciamentoAmbiental::getArquivo)
            .collect(Collectors.toList());
        return facade.getArquivoFacade().ziparArquivos("Documentos em anexo", arquivos);
    }

    @Override
    public void salvar() {
        try {
            validarDadosGeracaoDebito();
            selecionado = facade.salvarRetornando(selecionado);
            if (isOperacaoNovo()) facade.definirStatusAtual(selecionado, selecionado.getStatus());
            facade.gerarDebito(selecionado, facade.getSistemaFacade().getUsuarioCorrente(), false);
            if (isOperacaoNovo() && selecionado.getAssuntoLicenciamentoAmbiental().getGeraTaxaExpediente()) {
                facade.gerarDebito(selecionado, facade.getSistemaFacade().getUsuarioCorrente(), true);
            }
            salvarRevisoes();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarDadosGeracaoDebito() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAssuntoLicenciamentoAmbiental().getTributo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Nenhum tributo definido para o assunto " +
                selecionado.getAssuntoLicenciamentoAmbiental().getDescricao());
        }
        DividaLicenciamentoAmbiental dividaLicenciamento = facade.getDividaLicenciamentoAmbientalFacade().recuperarUltimo();
        if (dividaLicenciamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Dívida de licenciamento ambiental não encontrada.");
        } else {
            if (dividaLicenciamento.getDivida() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Dívida de licenciamento ambiental não configurada.");
            }
        }
        ve.lancarException();
    }

    private void salvarRevisoes() {
        for (ControleLicenciamentoAmbiental controle : controles) {
            for (RevisaoControleLicenciamentoAmbiental revisao : controle.getRevisoes()) {
                if (revisao.getId() == null) {
                    facade.definirStatusAtual(selecionado, revisao.getSituacao());
                }
            }
            facade.getControleLicenciamentoAmbientalFacade().salvarSemMudarSituacaoProcesso(controle);
        }
        try {
            facade.gerarDebito(selecionado, facade.getSistemaFacade().getUsuarioCorrente(), false);
        } catch (Exception e) {
            logger.error("Não foi possivel gerar débito a partir da revisão", e);
        }
    }

    public List<SelectItem> buscarCategoriasDoAssunto() {
        List<SelectItem> retorno = Lists.newArrayList(new SelectItem(null, ""));
        AssuntoLicenciamentoAmbiental assunto = selecionado.getAssuntoLicenciamentoAmbiental();
        if (assunto != null) {
            if (!Hibernate.isInitialized(assunto.getCategorias())) {
                assunto = facade.getAssuntoLicenciamentoAmbientalFacade().recuperar(assunto.getId());
            }
            for (CategoriaAssuntoLicenciamentoAmbiental categoria : assunto.getCategorias()) {
                retorno.add(new SelectItem(categoria.getCategoria(), categoria.getCategoria().getDescricao()));
            }
        }
        return retorno;
    }

    public void emitirDAM() {
        try {
            List<DAM> dams = parcelas.stream()
                .filter(p -> p.getResultadoParcela().isEmAberto())
                .map((p) -> facade.getDamFacade().buscarOuGerarDam(p.getResultadoParcela()))
                .collect(Collectors.toList());
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dams);
            parcelas = buscarParcelas();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void editarUnidadeMedida(ProcessoLicenciamentoAmbientalUnidadeMedida processoUnidadeMedidaAmbiental) {
        this.processoUnidadeMedidaAmbiental = processoUnidadeMedidaAmbiental;
        removerUnidadeMedida(this.processoUnidadeMedidaAmbiental);
    }

    public BigDecimal valorTotalUnidadeMedida(Long idUnidadeMedida) {
        if (idUnidadeMedida != null) {
            if (mapValorTotalUnidadeMedida.containsKey(idUnidadeMedida)) {
                return mapValorTotalUnidadeMedida.get(idUnidadeMedida);
            }
        }
        return BigDecimal.ZERO;
    }

    private void popularMapValorTotalUnidadeMedida(ProcessoLicenciamentoAmbientalUnidadeMedida unidadeMedida) {
        try {
            if (unidadeMedida.getValorUfmCategoria() == null) unidadeMedida.setValorUfmCategoria(BigDecimal.ZERO);
            BigDecimal valorEmReaisCategoria = facade.getMoedaFacade().converterToRealPorExercicio(unidadeMedida.getValorUfmCategoria(),
                facade.getSistemaFacade().getExercicioCorrente());
            BigDecimal valorTotalUnidadeMedida = valorEmReaisCategoria.multiply(unidadeMedida.getValorUnidadeMedidaAmbiental());
            mapValorTotalUnidadeMedida.put(unidadeMedida.getUnidadeMedidaAmbiental().getId(), valorTotalUnidadeMedida);
        } catch (UFMException e) {
            throw new ValidacaoException(e.getMessage());
        }
    }

    public void adicionarUnidadeMedida() {
        try {
            validarUnidadeMedida();
            popularMapValorTotalUnidadeMedida(processoUnidadeMedidaAmbiental);
            selecionado.getProcessoUnidadeMedidas().add(processoUnidadeMedidaAmbiental);
            criarNovaUnidadeMedida();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarUnidadeMedida() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(processoUnidadeMedidaAmbiental);
        if (processoUnidadeMedidaAmbiental.getValorUnidadeMedidaAmbiental().compareTo(BigDecimal.ZERO) == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor deve ser maior que zero (0).");
        }
        if (selecionado.hasUnidadeMedida(processoUnidadeMedidaAmbiental)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade de medida " +
                processoUnidadeMedidaAmbiental.getUnidadeMedidaAmbiental().getDescricao() + " ja foi adicionada.");
        }
        ve.lancarException();
    }

    private void criarNovaUnidadeMedida() {
        processoUnidadeMedidaAmbiental = new ProcessoLicenciamentoAmbientalUnidadeMedida();
        processoUnidadeMedidaAmbiental.setProcessoLicenciamentoAmbiental(selecionado);
    }

    public void removerUnidadeMedida(ProcessoLicenciamentoAmbientalUnidadeMedida processoUnidadeMedidaAmbiental) {
        selecionado.getProcessoUnidadeMedidas().remove(processoUnidadeMedidaAmbiental);
    }

    public void adicionarTecnico() {
        try {
            validarTecnico();
            tecnicoProcessoLicenciamentoAmbiental.setProcessoLicenciamentoAmbiental(selecionado);
            tecnicoProcessoLicenciamentoAmbiental.setDataRegistro(new Date());
            tecnicoProcessoLicenciamentoAmbiental.setUsuarioRegistro(facade.getSistemaFacade().getUsuarioCorrente());
            selecionado.getTecnicos().add(tecnicoProcessoLicenciamentoAmbiental);
            tecnicoProcessoLicenciamentoAmbiental = new TecnicoProcessoLicenciamentoAmbiental();
            selecionado = facade.salvarRetornando(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void validarTecnico() {
        ValidacaoException ve = new ValidacaoException();
        if (tecnicoProcessoLicenciamentoAmbiental.getTecnicoLicenciamentoAmbiental() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Técnico deve ser informado.");
        } else {
            if (selecionado.getTecnicos().stream().anyMatch(t ->
                t.getTecnicoLicenciamentoAmbiental()
                    .equals(tecnicoProcessoLicenciamentoAmbiental.getTecnicoLicenciamentoAmbiental()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Técnico informado já está vinculado ao processo.");
            }
        }
        ve.lancarException();
    }

    public void removerTecnico(TecnicoProcessoLicenciamentoAmbiental tecnicoProcessoLicenciamentoAmbiental) {
        selecionado.getTecnicos().remove(tecnicoProcessoLicenciamentoAmbiental);
        selecionado = facade.salvarRetornando(selecionado);
    }

    public List<CNAE> completarCnaes(String parte) {
        if (selecionado.getRequerente() != null && selecionado.getRequerente().getPessoa() != null) {
            CadastroEconomico cmcRequerente = facade.getCadastroEconomicoFacade().buscarCadastroEconomicoAtivoPorPessoa(selecionado.getRequerente().getPessoa());
            if (cmcRequerente != null) {
                List<CNAE> cnaesCmc = facade.getCnaeFacade().getCnaeAtivosPorCadastroEconomicoFiltrandoCodigoAndDescricaoCnae(parte, cmcRequerente.getId());
                List<CNAE> cnaesJaAdicionados = Lists.newArrayList();
                List<CNAE> retorno = Lists.newArrayList();
                retorno.addAll(cnaesCmc);
                for (ProcessoLicenciamentoAmbientalCNAE procCnae : selecionado.getCnaes()) {
                    cnaesJaAdicionados.add(procCnae.getCnae());
                }
                for (CNAE cnaeCmc : cnaesCmc) {
                    for (CNAE cnaesJaAdicionado : cnaesJaAdicionados) {
                        if (cnaesJaAdicionado.getId().equals(cnaeCmc.getId())) {
                            retorno.remove(cnaeCmc);
                            break;
                        }
                    }
                }
                return retorno;
            } else {
                List<CNAE> cnaes = facade.getCnaeFacade().listaCnaesAtivos(parte);
                List<CNAE> cnaesJaAdicionados = Lists.newArrayList();
                List<CNAE> retorno = Lists.newArrayList();
                retorno.addAll(cnaes);
                for (ProcessoLicenciamentoAmbientalCNAE procCnae : selecionado.getCnaes()) {
                    cnaesJaAdicionados.add(procCnae.getCnae());
                }
                for (CNAE cnaeCmc : cnaes) {
                    for (CNAE cnaesJaAdicionado : cnaesJaAdicionados) {
                        if (cnaesJaAdicionado.getId().equals(cnaeCmc.getId())) {
                            retorno.remove(cnaeCmc);
                            break;
                        }
                    }
                }
                return retorno;
            }
        }
        return Lists.newArrayList();
    }

    public void adicionarCnae() {
        if (cnaeSelecionado != null) {
            ProcessoLicenciamentoAmbientalCNAE procCnae = new ProcessoLicenciamentoAmbientalCNAE();
            procCnae.setCnae(cnaeSelecionado);
            procCnae.setProcessoLicenciamentoAmbiental(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getCnaes(), procCnae);
            setCnaeSelecionado(null);
            selecionado.setDescricaoAtividade("");
        }
    }

    public void removerCnae(ActionEvent evento) {
        ProcessoLicenciamentoAmbientalCNAE procCnae = (ProcessoLicenciamentoAmbientalCNAE) evento.getComponent().getAttributes().get("vCnae");
        selecionado.getCnaes().remove(procCnae);
    }

    private void validarRevisao() {
        ValidacaoException ve = new ValidacaoException();
        if (revisaoParecer.getSituacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a situação da revisão.");
        }
        if (StatusLicenciamentoAmbiental.REJEITADO.equals(revisaoParecer.getSituacao())) {
            if (revisaoParecer.getAnexos() == null || revisaoParecer.getAnexos().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe um anexo.");
            }
            if (revisaoParecer.getJustificativa() == null || revisaoParecer.getJustificativa().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe uma justificativa.");
            }
        }
        ve.lancarException();
    }

    public void adicionarRevisao() {
        try {
            if (!podeVerELancarRevisaoDoParecer()) return;
            validarRevisao();
            revisaoParecer.setControleLicenciamentoAmbiental(controleSelecionado);
            Util.adicionarObjetoEmLista(controleSelecionado.getRevisoes(), revisaoParecer);
            limparRevisao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void limparRevisao() {
        revisaoParecer = new RevisaoControleLicenciamentoAmbiental();
        novoAnexoRevisaoSelecionado();
    }

    public boolean podeExcluirRevisaoDoParecer(RevisaoControleLicenciamentoAmbiental revisao) {
        return revisao.getId() == null && podeVerELancarRevisaoDoParecer();
    }

    public List<SelectItem> getSituacoesRevisao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (StatusLicenciamentoAmbiental situacao : StatusLicenciamentoAmbiental.getSituacoesRevisao()) {
            toReturn.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return toReturn;
    }

    public void editarRevisao(RevisaoControleLicenciamentoAmbiental revisao) {
        if (!podeVerELancarRevisaoDoParecer()) return;
        revisaoParecer = (RevisaoControleLicenciamentoAmbiental) Util.clonarObjeto(revisao);
        novoAnexoRevisaoSelecionado();
    }

    public void removeRevisao(ActionEvent evento) {
        if (!podeVerELancarRevisaoDoParecer()) return;
        RevisaoControleLicenciamentoAmbiental revisao = (RevisaoControleLicenciamentoAmbiental) evento.getComponent().getAttributes().get("vrevisao");
        controleSelecionado.getRevisoes().remove(revisao);
    }

    public void selecionarParecer(SelectEvent event) {
        if (!podeVerELancarRevisaoDoParecer()) return;
        controleSelecionado = (ControleLicenciamentoAmbiental) event.getObject();
        limparRevisao();
    }

    public void selecionarRevisao(SelectEvent event) {
        if (!podeVerELancarRevisaoDoParecer()) return;
        revisaoParecer = (RevisaoControleLicenciamentoAmbiental) event.getObject();
    }

    public boolean podeVerELancarRevisaoDoParecer() {
        UsuarioSistema usuarioSistema = Util.recuperarUsuarioCorrente();
        if (usuarioSistema != null) {
            return facade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(usuarioSistema, AutorizacaoTributario.PERMITIR_LANCAR_REVISAO_PARECER_LICENCIAMENTO_AMBIENTAL);
        }
        return false;
    }

    public boolean podeEmitirDocumento() {
        List<SituacaoEmissaoLicencaLicenciamentoAmbiental> situacoes = selecionado.getAssuntoLicenciamentoAmbiental().getSituacoesEmissaoLicenca();
        return selecionado.getAssuntoLicenciamentoAmbiental().getTipoDoctoOficial() != null && !temParcelaAberto() && !parcelas.isEmpty()
            && (situacoes.isEmpty() || situacoes.stream().anyMatch(s -> s.getStatus().equals(selecionado.getStatus())));
    }

    public boolean podeAssinarDocumento() {
        if (parametroLicenciamentoAmbiental == null) return false;
        UsuarioSistema usuarioLogado = facade.getSistemaFacade().getUsuarioCorrente();
        return (selecionado.getAssinaturaDiretor() == null || selecionado.getAssinaturaSecretario() == null) &&
            (usuarioLogado.getPessoaFisica().getId().equals(parametroLicenciamentoAmbiental.getDiretor().getSecretario().getId())
                || usuarioLogado.getPessoaFisica().getId().equals(parametroLicenciamentoAmbiental.getSecretario().getSecretario().getId()));
    }

    public List<AssinaturaSecretarioLicenciamentoAmbiental> assinaturas() {
        List<AssinaturaSecretarioLicenciamentoAmbiental> retorno = Lists.newArrayList();
        if (selecionado.getAssinaturaDiretor() != null) retorno.add(selecionado.getAssinaturaDiretor());
        if (selecionado.getAssinaturaSecretario() != null) retorno.add(selecionado.getAssinaturaSecretario());
        return retorno;
    }

    public String cargoAssinatura(AssinaturaSecretarioLicenciamentoAmbiental assinatura) {
        if (selecionado.getAssinaturaDiretor() != null && selecionado.getAssinaturaDiretor().getId().equals(assinatura.getId())) {
            return "Diretor";
        }
        if (selecionado.getAssinaturaSecretario() != null && selecionado.getAssinaturaSecretario().getId().equals(assinatura.getId())) {
            return "Secretário";
        }
        return "";
    }

    public void assinarDocumento() {
        if (!podeAssinarDocumento()) return;
        UsuarioSistema usuarioLogado = facade.getSistemaFacade().getUsuarioCorrente();
        if (parametroLicenciamentoAmbiental.getDiretor().getSecretario().getId().equals(usuarioLogado.getPessoaFisica().getId())) {
            if (selecionado.getAssinaturaDiretor() == null) {
                selecionado.setAssinaturaDiretor(new AssinaturaSecretarioLicenciamentoAmbiental(SecretarioLicenciamentoAmbiental.clonar(parametroLicenciamentoAmbiental.getDiretor())));
            } else {
                FacesUtil.addOperacaoNaoRealizada("O documento ja foi assinado pelo Diretor(a).");
                return;
            }
        }
        if (parametroLicenciamentoAmbiental.getSecretario().getSecretario().getId().equals(usuarioLogado.getPessoaFisica().getId())) {
            if (selecionado.getAssinaturaSecretario() == null) {
                selecionado.setAssinaturaSecretario(new AssinaturaSecretarioLicenciamentoAmbiental(SecretarioLicenciamentoAmbiental.clonar(parametroLicenciamentoAmbiental.getSecretario())));
            } else {
                FacesUtil.addOperacaoNaoRealizada("O documento ja foi assinado pelo Secretário(a).");
                return;
            }
        }
        super.salvar(Redirecionar.VER);
    }

    public boolean temParcelaAberto() {
        for (DAMResultadoParcela parcela : parcelas) {
            if (parcela.getResultadoParcela().isEmAberto()) return true;
        }
        return false;
    }

    public void emitirDocumento() {
        try {
            if (!podeEmitirDocumento()) return;
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getAssinaturaDiretor() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possivel emitir o documento porque não foi assinado pelo diretor.");
            }
            if (selecionado.getAssinaturaSecretario() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possivel emitir o documento porque não foi assinado pelo secretário.");
            }
            ve.lancarException();
            selecionado = facade.gerarDocumentoOficial(selecionado);
            facade.emitirDocumentoOficial(selecionado.getDocumentoOficial());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarAnexoRevisao() {
        ValidacaoException ve = new ValidacaoException();
        if (anexoRevisaoSelecionado.getArquivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um arquivo.");
        }
        for (AnexoControleLicenciamentoAmbiental anexo : revisaoParecer.getAnexos()) {
            if (anexo.getArquivo().getNome().equals(anexoRevisaoSelecionado.getArquivo().getNome()) && !anexo.equals(anexoRevisaoSelecionado)) {
                ve.adicionarMensagemDeCampoObrigatorio("Arquivo " + anexo.getArquivo().getNome() + " já foi adicionado.");
                break;
            }
        }
        ve.lancarException();
    }

    public void uploadAnexoRevisao(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            anexoRevisaoSelecionado.setArquivo(arquivo);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void adicionarAnexoRevisao() {
        try {
            validarAnexoRevisao();
            Util.adicionarObjetoEmLista(revisaoParecer.getAnexos(), anexoRevisaoSelecionado);
            novoAnexoRevisaoSelecionado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removerAnexoRevisao(ActionEvent evento) {
        AnexoControleLicenciamentoAmbiental anexo = (AnexoControleLicenciamentoAmbiental) evento.getComponent().getAttributes().get("vAnexoRevisao");
        if (!podeRemoverAnexoRevisao(anexo)) return;
        revisaoParecer.getAnexos().remove(anexo);
    }

    public boolean podeRemoverAnexoRevisao(AnexoControleLicenciamentoAmbiental anexo) {
        return anexo.getId() == null;
    }

    public StreamedContent baixarArquivo(Arquivo arq) {
        if (arq == null) return null;
        return facade.getArquivoFacade().montarArquivoParaDownloadPorArquivo(arq);
    }

    public void editarAnexoRevisao(AnexoControleLicenciamentoAmbiental anexo) {
        anexoRevisaoSelecionado = (AnexoControleLicenciamentoAmbiental) Util.clonarObjeto(anexo);
    }

    public void novoAnexoRevisaoSelecionado() {
        AnexoControleLicenciamentoAmbiental novoAnexo = new AnexoControleLicenciamentoAmbiental();
        novoAnexo.setRevisaoControleLicenciamentoAmbiental(revisaoParecer);
        if (selecionado.getAssuntoLicenciamentoAmbiental() != null) {
            novoAnexo.setMostrarNoPortalContribuinte(selecionado.getAssuntoLicenciamentoAmbiental().getMostrarAnexosPortal());
        }
        setAnexoRevisaoSelecionado(novoAnexo);
    }

    public String montarNomesListaArquivosComposicao(RevisaoControleLicenciamentoAmbiental revisao) {
        StringBuilder retorno = new StringBuilder();
        for (int i = 0; i < revisao.getAnexos().size(); i++) {
            if (i != 0) retorno.append("</br>");
            Arquivo arquivo = revisao.getAnexos().get(i).getArquivo();
            if (arquivo.getId() != null) {
                retorno.append("<a title='Download' ");
                retorno.append("target='_blank' ");
                retorno.append("download='").append(arquivo.getNome()).append("' ");
                retorno.append("href='").append("/arquivos/")
                    .append(arquivo.getNome())
                    .append("?id=").append(arquivo.getId()).append("'>");
            }
            retorno.append(arquivo.getNome()).append(" (Mostrar no portal: ")
                .append(revisao.getAnexos().get(i).isMostrarNoPortalContribuinte() ? "Sim)" : "Não)");

            if (arquivo.getId() != null) retorno.append("</a>");
        }
        return retorno.toString();
    }

    public void salvarFlagMostrarNoPortal(AnexoControleLicenciamentoAmbiental anexo, boolean mostrar) {
        anexo.setMostrarNoPortalContribuinte(mostrar);
        facade.salvarFlagMostrarNoPortal(anexo, mostrar);
    }
}
