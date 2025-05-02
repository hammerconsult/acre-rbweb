package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.FiltroResumoExecucaoVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFacade;
import br.com.webpublico.negocios.DispensaDeLicitacaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.pncp.service.PncpService;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 11/07/14
 * Time: 09:16
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-dispensa-licitacao", pattern = "/dispensa-licitacao/novo/", viewId = "/faces/administrativo/licitacao/dispensalicitacao/edita.xhtml"),
    @URLMapping(id = "editar-dispensa-licitacao", pattern = "/dispensa-licitacao/editar/#{dispensaDeLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/dispensalicitacao/edita.xhtml"),
    @URLMapping(id = "ver-dispensa-licitacao", pattern = "/dispensa-licitacao/ver/#{dispensaDeLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/dispensalicitacao/visualizar.xhtml"),
    @URLMapping(id = "listar-dispensa-licitacao", pattern = "/dispensa-licitacao/listar/", viewId = "/faces/administrativo/licitacao/dispensalicitacao/lista.xhtml")
})
public class DispensaDeLicitacaoControlador extends PrettyControlador<DispensaDeLicitacao> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(DispensaDeLicitacaoControlador.class);

    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterPessoa;
    private DocumentoDispensaDeLicitacao documentoDispensaDeLicitacaoSelecionado;
    private ParecerDispensaDeLicitacao parecerDispensaDeLicitacaoSelecionado;
    private PublicacaoDispensaDeLicitacao publicacaoDispensaDeLicitacaoSelecionada;
    private FornecedorDispensaDeLicitacao fornecedorDispensaDeLicitacaoSelecionado;
    private Map<LotePropostaFornecedorDispensa, List<ItemPropostaFornecedorDispensa>> mapBackup;
    private String selecionouDeferidoIndeferido;
    private LoteProcessoDeCompra loteProcessoDeCompraSelecionado;
    private DocumentoFornecedorDispensaDeLicitacao documentoFornecedorDispensaDeLicitacaoSelecionado;
    private ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa;
    private String modeloProduto;
    private String descricaoProduto;
    private HierarquiaOrganizacional unidadeAdministrativa;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;
    private FiltroHistoricoProcessoLicitatorio filtroAjusteProcesso;
    private FiltroResumoExecucaoVO filtroResumoExecucaoVO;

    private Boolean bloquearEdicao;

    public DispensaDeLicitacaoControlador() {
        super(DispensaDeLicitacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return dispensaDeLicitacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/dispensa-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-dispensa-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoDispensaDeLicitacao.EM_ELABORACAO);
        selecionado.setDataDaDispensa(dispensaDeLicitacaoFacade.getSistemaFacade().getDataOperacao());
        recuperarProcessoDeCompra();
    }

    private void recuperarProcessoDeCompra() {
        ProcessoDeCompra pdc = (ProcessoDeCompra) Web.pegaDaSessao("PROCESSO_DE_COMPRA");
        if (pdc != null) {
            selecionado.setProcessoDeCompra(pdc);
            atualizarDadosComProcessoDeCompra();
        }
    }

    public void concluirDispensaLicitacao() {
        try {
            validarRegrasAndCamposObrigatorios();
            dispensaDeLicitacaoFacade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
            selecionado = dispensaDeLicitacaoFacade.concluirDispensa(selecionado);
            FacesUtil.addOperacaoRealizada("Registro concluído com sucesso!");
            redirecionarParVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void redirecionarParVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void salvarValidandoProcessoCompra() {
        try {
            validarProcessoCompra();
            dispensaDeLicitacaoFacade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
            selecionado = dispensaDeLicitacaoFacade.salvarDispensa(selecionado);
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso!");
            redirecionarParVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean isPodeSalvarRegistroSemValidacoes() {
        return selecionado.isDispensaEmElaboracao();
    }

    public EventoPncpVO getEventoPncpVO() {
        EventoPncpVO eventoPncpVO = new EventoPncpVO();
        eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.LICITACAO);
        eventoPncpVO.setIdOrigem(selecionado.getId());
        eventoPncpVO.setIdPncp(selecionado.getIdPncp());
        eventoPncpVO.setSequencialIdPncp(selecionado.getSequencialIdPncp());
        eventoPncpVO.setSequencialIdPncpLicitacao(selecionado.getSequencialIdPncp());
        eventoPncpVO.setAnoPncp(selecionado.getExercicioDaDispensa().getAno());
        eventoPncpVO.setIntegradoPNCP(selecionado.getIdPncp() != null && selecionado.getSequencialIdPncp() != null);
        return eventoPncpVO;
    }

    public void confirmarIdPncp(ActionEvent evento) {
        EventoPncpVO eventoPncpVO = (EventoPncpVO) evento.getComponent().getAttributes().get("objeto");
        selecionado.setIdPncp(eventoPncpVO.getIdPncp());
        selecionado.setSequencialIdPncp(eventoPncpVO.getSequencialIdPncp());
        PncpService.getService().criarEventoAtualizacaoIdSequencialPncp(selecionado.getId(), selecionado.getNumeroDaDispensa() + "/" + selecionado.getExercicioDaDispensa().getAno());
        selecionado = dispensaDeLicitacaoFacade.salvarRetornando(selecionado);
        redirecionarParVer();
    }


    private void validarProcessoCompra() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getProcessoDeCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Para salvar o registro o campo Processo de Compra é obrigatório.");
        }
        ve.lancarException();
    }

    private void validarRegrasAndCamposObrigatorios() throws ValidacaoException {
        Util.validarCampos(selecionado);
        documentosValidos();
        publicacoesValidas();
        fornecedoresValidos();
    }

    private boolean existemFornecedoresSemProposta() {
        boolean existe = false;
        List<FacesMessage> mensagens = Lists.newArrayList();
        for (FornecedorDispensaDeLicitacao fornecedor : selecionado.getFornecedores()) {
            if (fornecedor.getPropostaFornecedorDispensa() == null) {
                mensagens.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O fornecedor " + fornecedor.getPessoa().getNomeCpfCnpj() + " está sem proposta definida. "));
                existe = true;
            }
        }
        if (!mensagens.isEmpty()) {
            FacesUtil.printAllFacesMessages(mensagens);
        }
        return existe;
    }

    private boolean existemFornecedoresInabilitados() {

        boolean existe = false;
        List<FacesMessage> mensagens = Lists.newArrayList();
        for (FornecedorDispensaDeLicitacao fornecedor : selecionado.getFornecedores()) {
            if (fornecedor.getTipoClassificacaoFornecedor().equals(TipoClassificacaoFornecedor.INABILITADO)) {
                mensagens.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O fornecedor " + fornecedor.getPessoa().getNomeCpfCnpj() + " está inabilitado. "));
                existe = true;
            }
        }
        if (!mensagens.isEmpty()) {
            FacesUtil.printAllFacesMessages(mensagens);
        }
        return existe;
    }

    private void fornecedoresValidos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFornecedores() == null || selecionado.getFornecedores().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos um fornecedor.");
        }
        ve.lancarException();
        if (existemFornecedoresInabilitados()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe fornecedores inabilitados.");
        }
        if (existemFornecedoresSemProposta()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe fornecedores sem proposta.");
        }
        ve.lancarException();
    }

    private void publicacoesValidas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPublicacoes() == null || selecionado.getPublicacoes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos uma publicação.");
        }
        if (isDispensaComDisputa()) {
            if (selecionado.getEncerradaEm() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data e Hora do Encerramento deve ser informado.");

            } else if (selecionado.getEncerradaEm().before(selecionado.getDataDaDispensa())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data e Hora do Encerramento deve ser posterior a  Data e Hora da Abertura.");
            }
        }
        ve.lancarException();
    }

    private void documentosValidos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDocumentos() == null || selecionado.getDocumentos().isEmpty()) {
            if (estaConfirmandoDocumento()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos um documento.");
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Os documentos devem ser informados antes de habilitar o fornecedor.");
            }
        }
        ve.lancarException();
    }

    private boolean estaConfirmandoDocumento() {
        return documentoDispensaDeLicitacaoSelecionado != null;
    }

    @Override
    @URLAction(mappingId = "editar-dispensa-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        selecionado.setProcessoDeCompra(carregaListasDoProcessoDeCompraSelecionado(selecionado.getProcessoDeCompra()));
        inicializaItensDoLoteSelecionado();
        setUnidadeAdministrativaProcessoCompra();
        bloquearEdicao = !Util.isListNullOrEmpty(contratoFacade.buscarContratoDispensa(selecionado));
    }

    private void inicializaItensDoLoteSelecionado() {
        if (loteProcessoDeCompraSelecionado != null) {
            loteProcessoDeCompraSelecionado.getItensProcessoDeCompra().size();
        }
    }

    @Override
    @URLAction(mappingId = "ver-dispensa-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        selecionado.setProcessoDeCompra(carregaListasDoProcessoDeCompraSelecionado(selecionado.getProcessoDeCompra()));
        inicializaItensDoLoteSelecionado();
        setUnidadeAdministrativaProcessoCompra();
        novoFiltroResumoExecucao();
    }

    public DocumentoDispensaDeLicitacao getDocumentoDispensaDeLicitacaoSelecionado() {
        return documentoDispensaDeLicitacaoSelecionado;
    }

    public void setDocumentoDispensaDeLicitacaoSelecionado(DocumentoDispensaDeLicitacao documentoDispensaDeLicitacaoSelecionado) {
        this.documentoDispensaDeLicitacaoSelecionado = documentoDispensaDeLicitacaoSelecionado;
    }

    public ParecerDispensaDeLicitacao getParecerDispensaDeLicitacaoSelecionado() {
        return parecerDispensaDeLicitacaoSelecionado;
    }

    public void setParecerDispensaDeLicitacaoSelecionado(ParecerDispensaDeLicitacao parecerDispensaDeLicitacaoSelecionado) {
        this.parecerDispensaDeLicitacaoSelecionado = parecerDispensaDeLicitacaoSelecionado;
    }

    public PublicacaoDispensaDeLicitacao getPublicacaoDispensaDeLicitacaoSelecionada() {
        return publicacaoDispensaDeLicitacaoSelecionada;
    }

    public void setPublicacaoDispensaDeLicitacaoSelecionada(PublicacaoDispensaDeLicitacao publicacaoDispensaDeLicitacaoSelecionada) {
        this.publicacaoDispensaDeLicitacaoSelecionada = publicacaoDispensaDeLicitacaoSelecionada;
    }

    public FornecedorDispensaDeLicitacao getFornecedorDispensaDeLicitacaoSelecionado() {
        return fornecedorDispensaDeLicitacaoSelecionado;
    }

    public void setFornecedorDispensaDeLicitacaoSelecionado(FornecedorDispensaDeLicitacao fornecedorDispensaDeLicitacaoSelecionado) {
        this.fornecedorDispensaDeLicitacaoSelecionado = fornecedorDispensaDeLicitacaoSelecionado;
    }

    public String getSelecionouDeferidoIndeferido() {
        return selecionouDeferidoIndeferido;
    }

    public void setSelecionouDeferidoIndeferido(String selecionouDeferidoIndeferido) {
        this.selecionouDeferidoIndeferido = selecionouDeferidoIndeferido;
    }

    public LoteProcessoDeCompra getLoteProcessoDeCompraSelecionado() {
        return loteProcessoDeCompraSelecionado;
    }

    public void setLoteProcessoDeCompraSelecionado(LoteProcessoDeCompra loteProcessoDeCompraSelecionado) {
        this.loteProcessoDeCompraSelecionado = loteProcessoDeCompraSelecionado;
    }

    public DocumentoFornecedorDispensaDeLicitacao getDocumentoFornecedorDispensaDeLicitacaoSelecionado() {
        return documentoFornecedorDispensaDeLicitacaoSelecionado;
    }

    public void setDocumentoFornecedorDispensaDeLicitacaoSelecionado(DocumentoFornecedorDispensaDeLicitacao documentoFornecedorDispensaDeLicitacaoSelecionado) {
        this.documentoFornecedorDispensaDeLicitacaoSelecionado = documentoFornecedorDispensaDeLicitacaoSelecionado;
    }

    public void atualizarDadosComProcessoDeCompra() {
        selecionado.setProcessoDeCompra(dispensaDeLicitacaoFacade.getProcessoDeCompraFacade().recuperar(selecionado.getProcessoDeCompra().getId()));
        selecionado.setValorMaximo(recuperaValorTotalProcessoDeCompra(selecionado.getProcessoDeCompra()));
        selecionado.setModalidade(selecionado.getProcessoDeCompra().getSolicitacaoMaterial().getModalidadeLicitacao());
        selecionado.setNaturezaProcedimento(selecionado.getProcessoDeCompra().getSolicitacaoMaterial().getTipoNaturezaDoProcedimento());
        populaDadosVindoDaSolicitacaoDeCompra(selecionado.getProcessoDeCompra());
    }

    private void populaDadosVindoDaSolicitacaoDeCompra(ProcessoDeCompra processoDeCompra) {
        try {
            SolicitacaoMaterial solicitacao = recuperaSolicitacaoPartindoDoProcessoDeCompra(processoDeCompra);
            selecionado.setTipoDeAvaliacao(solicitacao.getTipoAvaliacao());
            selecionado.setTipoDeApuracao(solicitacao.getTipoApuracao());
            selecionado.setPrazoDeExecucao(solicitacao.getPrazoExecucao());
            selecionado.setTipoPrazoDeExecucao(solicitacao.getTipoPrazoDeExecucao());
            selecionado.setPrazoDeVigencia(solicitacao.getVigencia());
            selecionado.setTipoPrazoDeVigencia(solicitacao.getTipoPrazoDeVigencia());
            selecionado.setLocalDeEntrega(solicitacao.getLocalDeEntrega());
            selecionado.setFormaDePagamento(solicitacao.getFormaDePagamento());
            selecionado.setJustificativa(solicitacao.getJustificativa());
            selecionado.setExercicioDaDispensa(solicitacao.getExercicio());
            setUnidadeAdministrativaProcessoCompra();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private SolicitacaoMaterial recuperaSolicitacaoPartindoDoProcessoDeCompra(ProcessoDeCompra processoDeCompra) {
        try {
            processoDeCompra = carregaListasDoProcessoDeCompraSelecionado(processoDeCompra);
            for (LoteProcessoDeCompra loteProcesso : processoDeCompra.getLotesProcessoDeCompra()) {
                for (ItemProcessoDeCompra itemProcesso : loteProcesso.getItensProcessoDeCompra()) {
                    return itemProcesso.getItemSolicitacaoMaterial().getLoteSolicitacaoMaterial().getSolicitacaoMaterial();
                }
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Ocorreu erro ao recuperar os dados da solicitação de compra.");
        }
        return null;
    }

    public ProcessoDeCompra carregaListasDoProcessoDeCompraSelecionado(ProcessoDeCompra processoDeCompra) {
        processoDeCompra = dispensaDeLicitacaoFacade.getProcessoDeCompraFacade().recuperar(processoDeCompra.getId());
        return processoDeCompra;
    }

    private BigDecimal recuperaValorTotalProcessoDeCompra(ProcessoDeCompra processoDeCompra) {
        BigDecimal total = BigDecimal.ZERO;

        for (LoteProcessoDeCompra loteProcessoDeCompra : processoDeCompra.getLotesProcessoDeCompra()) {
            total = total.add(loteProcessoDeCompra.getValor());
        }
        return total;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, dispensaDeLicitacaoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public boolean isVisualizar() {
        try {
            return Operacoes.VER.equals(operacao);
        } catch (Exception e) {
            return false;
        }
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<UsuarioSistema> completaUsuarioSistema(String parte) {
        return dispensaDeLicitacaoFacade.getUsuarioSistemaFacade().completarUsuarioSistemaPeloNomePessoaFisica(parte.trim());
    }

    public List<Pessoa> completarPessoasFisicas(String parte) {
        return dispensaDeLicitacaoFacade.getPessoaFacade().listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }

    public List<VeiculoDePublicacao> completaVeiculoDePublicacao(String parte) {
        return dispensaDeLicitacaoFacade.getVeiculoDePublicacaoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<Pessoa> completarPessoasFornecedores(String parte) {
        return dispensaDeLicitacaoFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<SelectItem> getRegimeDeExecucao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (RegimeDeExecucao object : RegimeDeExecucao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoPrazo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPrazo tipoPrazo : TipoPrazo.values()) {
            toReturn.add(new SelectItem(tipoPrazo, tipoPrazo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoParecer() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getProcessoDeCompra() != null) {
            if (selecionado.getModalidade().isInexigibilidade()) {
                toReturn.add(new SelectItem(TipoParecerLicitacao.JURIDICO_INEXIGIBILIDADE, TipoParecerLicitacao.JURIDICO_INEXIGIBILIDADE.getDescricao()));
            } else if (selecionado.getModalidade().isDispensaLicitacao()) {
                toReturn.add(new SelectItem(TipoParecerLicitacao.JURIDICO_DISPENSA, TipoParecerLicitacao.JURIDICO_DISPENSA.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<DoctoHabilitacao> completarDocumentos(String parte) {
        return dispensaDeLicitacaoFacade.getDoctoHabilitacaoFacade().buscarDocumentosVigentes(parte.trim(), dispensaDeLicitacaoFacade.getSistemaFacade().getDataOperacao());
    }

    private void validarProcessoCompraInformado() {
        if (selecionado.getProcessoDeCompra() == null) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoRealizada("O campo processo de compra deve ser informado.");
            throw ve;
        }
    }

    public void novoDocumentoDispensa() {
        documentoDispensaDeLicitacaoSelecionado = new DocumentoDispensaDeLicitacao();
        documentoDispensaDeLicitacaoSelecionado.setDispensaDeLicitacao(selecionado);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:documento-dispensa_input')");
    }

    public void novoParecerDispensa() {
        try {
            validarProcessoCompraInformado();
            parecerDispensaDeLicitacaoSelecionado = new ParecerDispensaDeLicitacao();
            parecerDispensaDeLicitacaoSelecionado.setDispensaDeLicitacao(selecionado);

            parecerDispensaDeLicitacaoSelecionado.setParecer(new Parecer());
            parecerDispensaDeLicitacaoSelecionado.getParecer().setDataDoParecer(dispensaDeLicitacaoFacade.getSistemaFacade().getDataOperacao());
            parecerDispensaDeLicitacaoSelecionado.getParecer().setNumero(retornaProximoNumero(selecionado.getPareceres()));
            FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:tipo-parecer')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void novaPublicacaoDispensa() {
        publicacaoDispensaDeLicitacaoSelecionada = new PublicacaoDispensaDeLicitacao();
        publicacaoDispensaDeLicitacaoSelecionada.setDispensaDeLicitacao(selecionado);
        publicacaoDispensaDeLicitacaoSelecionada.setDataDaPublicacao(getSistemaControlador().getDataOperacao());
        FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:veiculo-publicacao_input')");
    }

    public void novoFornecedorDispensa() {
        try {
            validarProcessoCompraInformado();
            fornecedorDispensaDeLicitacaoSelecionado = new FornecedorDispensaDeLicitacao();
            fornecedorDispensaDeLicitacaoSelecionado.setDispensaDeLicitacao(selecionado);
            fornecedorDispensaDeLicitacaoSelecionado.setCodigo(retornaProximoNumero(selecionado.getFornecedores()));
            FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:fornecedor_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private Integer retornaProximoNumero(List lista) {
        if (lista != null && !lista.isEmpty()) {
            return lista.size() + 1;
        }
        return 1;
    }

    private boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }

        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    public void confirmarDocumentoDispensa() {
        if (!validarConfirmacao(this.documentoDispensaDeLicitacaoSelecionado)) {
            return;
        }

        if (podeConfirmarDocumento()) {
            selecionado.setDocumentos(Util.adicionarObjetoEmLista(selecionado.getDocumentos(), documentoDispensaDeLicitacaoSelecionado));
            cancelarDocumentoDispensa();
        }
    }

    public void confirmarParecerDispensa() {
        if (!validarConfirmacao(this.parecerDispensaDeLicitacaoSelecionado.getParecer())) {
            return;
        }

        if (podeConfirmarParecer()) {
            selecionado.setPareceres(Util.adicionarObjetoEmLista(selecionado.getPareceres(), parecerDispensaDeLicitacaoSelecionado));
            cancelarParecerDispensa();
            selecionouDeferidoIndeferido = null;
        }
    }

    public void confirmarPublicacaoDispensa() {
        if (!validarConfirmacao(this.publicacaoDispensaDeLicitacaoSelecionada)) {
            return;
        }

        if (podeConfirmarPublicacao()) {
            selecionado.setPublicacoes(Util.adicionarObjetoEmLista(selecionado.getPublicacoes(), publicacaoDispensaDeLicitacaoSelecionada));
            cancelarPublicacaoDispensa();
        }
    }

    public void confirmarFornecedor() {
        if (!validarConfirmacao(this.fornecedorDispensaDeLicitacaoSelecionado)) {
            return;
        }

        if (podeConfirmarFornecedor()) {
            fornecedorDispensaDeLicitacaoSelecionado.processarClassificacaoDesteFornecedor(dispensaDeLicitacaoFacade.getSistemaFacade().getDataOperacao());
            selecionado.setFornecedores(Util.adicionarObjetoEmLista(selecionado.getFornecedores(), fornecedorDispensaDeLicitacaoSelecionado));
            cancelarFornecedorDispensa();
        }
    }

    public void cancelarFornecedorDispensa() {
        fornecedorDispensaDeLicitacaoSelecionado = null;
    }

    private boolean podeConfirmarFornecedor() {
        if (selecionado.getFornecedores() != null && !selecionado.getFornecedores().isEmpty()) {
            for (FornecedorDispensaDeLicitacao fornecedorDispensa : selecionado.getFornecedores()) {
                if (fornecedorDispensa.getPessoa().equals(fornecedorDispensaDeLicitacaoSelecionado.getPessoa())
                    && !selecionado.getFornecedores().contains(fornecedorDispensaDeLicitacaoSelecionado)) {
                    FacesUtil.addOperacaoNaoPermitida("O fornecedor selecionado já foi adicionado.");
                    return false;
                }
            }
        }
        return true;
    }

    public void cancelarPublicacaoDispensa() {
        this.publicacaoDispensaDeLicitacaoSelecionada = null;
    }

    private boolean podeConfirmarPublicacao() {
        if (mesmaDataDePublicacao()) {
            return false;
        }
        return true;
    }

    private boolean mesmaDataDePublicacao() {
        if (selecionado.getPublicacoes() != null && !selecionado.getPublicacoes().isEmpty()) {
            for (PublicacaoDispensaDeLicitacao publicacaoDispensa : selecionado.getPublicacoes()) {
                if (publicacaoDispensa.getDataDaPublicacao().equals(publicacaoDispensaDeLicitacaoSelecionada.getDataDaPublicacao())
                    && !selecionado.getPublicacoes().contains(publicacaoDispensaDeLicitacaoSelecionada)) {
                    FacesUtil.addOperacaoNaoRealizada("Já existe uma publicação com a data informada.");
                    return true;
                }
            }
        }
        return false;
    }

    public void cancelarParecerDispensa() {
        parecerDispensaDeLicitacaoSelecionado = null;
    }

    private boolean podeConfirmarParecer() {
        if (statusDoParecerNull()) {
            return false;
        }
        if (parecerComMesmoNumero()) {
            return false;
        }
        return true;
    }

    private boolean parecerComMesmoNumero() {
        if (selecionado.getPareceres() != null && !selecionado.getPareceres().isEmpty()) {
            for (ParecerDispensaDeLicitacao parecerDispensa : selecionado.getPareceres()) {
                if (parecerDispensa.getParecer().getNumero().equals(parecerDispensaDeLicitacaoSelecionado.getParecer().getNumero())
                    && !parecerDispensa.getParecer().getDataDoParecer().equals(parecerDispensaDeLicitacaoSelecionado.getParecer().getDataDoParecer())) {
                    FacesUtil.addOperacaoNaoPermitida("Já existe um parecer com o número " + parecerDispensaDeLicitacaoSelecionado.getParecer().getNumero() + ".");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean statusDoParecerNull() {
        if (parecerDispensaDeLicitacaoSelecionado.getParecer().getDeferido() == null) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório informar o status 'Deferido/Indeferido do parecer'.");
            return true;
        }
        return false;
    }

    public boolean podeConfirmarDocumento() {
        if (documentoJaFoiAdicionado()) {
            return false;
        }
        return true;
    }

    private boolean documentoJaFoiAdicionado() {
        if (selecionado.getDocumentos() != null && !selecionado.getDocumentos().isEmpty()) {
            for (DocumentoDispensaDeLicitacao documentoDispensa : selecionado.getDocumentos()) {
                if (documentoDispensa.getDocumentoHabilitacao().equals(documentoDispensaDeLicitacaoSelecionado.getDocumentoHabilitacao())
                    && !selecionado.getDocumentos().contains(documentoDispensaDeLicitacaoSelecionado)) {
                    FacesUtil.addOperacaoNaoPermitida("O documento selecionado já foi adicionado.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean documentoNulo() {
        if (documentoDispensaDeLicitacaoSelecionado.getDocumentoHabilitacao() == null) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório selecionar um documento.");
            return true;
        }
        return false;
    }

    public void cancelarDocumentoDispensa() {
        documentoDispensaDeLicitacaoSelecionado = null;
    }

    public void selecionarDocumentoDispensa(DocumentoDispensaDeLicitacao documento) {
        documentoDispensaDeLicitacaoSelecionado = documento;
    }

    public void selecionarParecerDispensa(ParecerDispensaDeLicitacao parecer) {
        parecerDispensaDeLicitacaoSelecionado = parecer;
        recuperarStatusSelecionado(parecerDispensaDeLicitacaoSelecionado);
    }

    public void selecionarPublicacaoDispensa(PublicacaoDispensaDeLicitacao publicacao) {
        publicacaoDispensaDeLicitacaoSelecionada = publicacao;
    }

    public void selecionarFornecedorDispensa(FornecedorDispensaDeLicitacao fornecedor) {
        fornecedorDispensaDeLicitacaoSelecionado = fornecedor;
    }

    public void removerDocumentoDispensa(DocumentoDispensaDeLicitacao documento) {
        selecionado.getDocumentos().remove(documento);
    }

    public void removerParecerDispensa(ParecerDispensaDeLicitacao parecer) {
        selecionado.getPareceres().remove(parecer);
    }

    public void removerPublicacaoDispensa(PublicacaoDispensaDeLicitacao publicacao) {
        selecionado.getPublicacoes().remove(publicacao);
    }

    public void removerFornecedorDispensa(FornecedorDispensaDeLicitacao fornecedor) {
        selecionado.getFornecedores().remove(fornecedor);
    }

    public void selecionouDeferidoOuIndeferido() {
        if ("DEFERIDO".equals(selecionouDeferidoIndeferido)) {
            parecerDispensaDeLicitacaoSelecionado.getParecer().setDeferido(true);
        }
        if ("INDEFERIDO".equals(selecionouDeferidoIndeferido)) {
            parecerDispensaDeLicitacaoSelecionado.getParecer().setDeferido(false);
        }
    }

    private void recuperarStatus() {
        for (ParecerDispensaDeLicitacao parecerDispensa : selecionado.getPareceres()) {
            if (parecerDispensa.getParecer().getDeferido()) {
                selecionouDeferidoIndeferido = "DEFERIDO";
            } else {
                selecionouDeferidoIndeferido = "INDEFERIDO";
            }
            RequestContext.getCurrentInstance().update("Formulario:tab-view-geral:status-parecer");
        }
    }

    private void recuperarStatusSelecionado(ParecerDispensaDeLicitacao parecerDispensa) {
        if (parecerDispensa.getParecer().getDeferido()) {
            selecionouDeferidoIndeferido = "DEFERIDO";
        } else {
            selecionouDeferidoIndeferido = "INDEFERIDO";
        }
        RequestContext.getCurrentInstance().update(":Formulario:tab-view-geral:status-parecer");
    }


    public void carregarLoteSelecionado(LoteProcessoDeCompra lote) {
        this.loteProcessoDeCompraSelecionado = lote;
    }

    public void carregarFornecedorSelecionado(FornecedorDispensaDeLicitacao fornecedor) {
        this.fornecedorDispensaDeLicitacaoSelecionado = fornecedor;
    }

    public void carregarFornecedorSelecionadoParaProposta(FornecedorDispensaDeLicitacao fornecedor) {
        try {
            fornecedorDispensaDeLicitacaoSelecionado = fornecedor;
            mapBackup = new HashMap<>();
            processoDeCompraValido();
            if (fornecedorHabilitado()) {
                if (fornecedorDispensaDeLicitacaoSelecionado.jaLancouPrecoDePeloMenosUmItem()) {
                    for (LotePropostaFornecedorDispensa lote : fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                        mapBackup.put(lote, clonarListaDeItens(lote.getItensProposta()));
                    }
                } else {
                    novaPropostaDoFornecedor();
                }
                recuperarLoteItemDoProcessoDeCompra();
                RequestContext.getCurrentInstance().update("formulario-proposta-fornecedor");
                FacesUtil.executaJavaScript("dialogPropostaFornecedor.show()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private List<ItemPropostaFornecedorDispensa> clonarListaDeItens(List<ItemPropostaFornecedorDispensa> itensProposta) {
        List<ItemPropostaFornecedorDispensa> retorno = new ArrayList<>();

        for (ItemPropostaFornecedorDispensa item : itensProposta) {
            retorno.add((ItemPropostaFornecedorDispensa) Util.clonarObjeto(item));
        }

        return retorno;
    }

    public void carregarFornecedorSelecionadoParaVisualizarProposta(FornecedorDispensaDeLicitacao fornecedor) {
        carregarFornecedorSelecionado(fornecedor);
        if (fornecedorDispensaDeLicitacaoSelecionado.jaLancouPrecoDePeloMenosUmItem()) {
            RequestContext.getCurrentInstance().update("formulario-visualizar-proposta-fornecedor");
            FacesUtil.executaJavaScript("dialogVisualizarPropostaFornecedor.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório realizar o lançamento da proposta antes de visualizar.");
            cancelarFornecedorDispensa();
        }
    }

    private boolean fornecedorHabilitado() {
        if (TipoClassificacaoFornecedor.HABILITADO.equals(fornecedorDispensaDeLicitacaoSelecionado.getTipoClassificacaoFornecedor())) {
            return true;
        }
        FacesUtil.addOperacaoNaoPermitida("Para lançar a proposta, o fornecedor deve estar habilitado. Situação atual deste fornecedor: <b>" + fornecedorDispensaDeLicitacaoSelecionado.getTipoClassificacaoFornecedor().getDescricao() + "</b>.");
        return false;
    }

    private void recuperarLoteItemDoProcessoDeCompra() {
        for (LoteProcessoDeCompra loteProcesso : selecionado.getProcessoDeCompra().getLotesProcessoDeCompra()) {
            LotePropostaFornecedorDispensa novoLoteProposta = populaDadosLoteProposta(loteProcesso);

            for (ItemProcessoDeCompra itemProcesso : loteProcesso.getItensProcessoDeCompra()) {
                ItemPropostaFornecedorDispensa novoItemProposta = populaDadosItemProposta(novoLoteProposta, itemProcesso);
                if (!esteItemJaEstaEmUmaPropostaDesteFornecedor(novoItemProposta)) {
                    novoLoteProposta.setItensProposta(Util.adicionarObjetoEmLista(novoLoteProposta.getItensProposta(), novoItemProposta));
                }
            }

            fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().setLotesDaProposta(
                Util.adicionarObjetoEmLista(fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta(), novoLoteProposta)
            );
        }
    }

    private boolean esteItemJaEstaEmUmaPropostaDesteFornecedor(ItemPropostaFornecedorDispensa novoItemProposta) {
        if (!fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta().isEmpty()) {
            for (LotePropostaFornecedorDispensa loteProposta : fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                for (ItemPropostaFornecedorDispensa itemProposta : loteProposta.getItensProposta()) {
                    if (loteProposta.equals(novoItemProposta.getLotePropostaFornecedorDispensa()) &&
                        ((itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao().equals(novoItemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao()))
                            && (Util.isStringNulaOuVazia(itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar())
                            || itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar().equals(novoItemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar())))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ItemPropostaFornecedorDispensa populaDadosItemProposta(LotePropostaFornecedorDispensa loteProposta, ItemProcessoDeCompra itemProcesso) {
        ItemPropostaFornecedorDispensa itemProposta = new ItemPropostaFornecedorDispensa();
        itemProposta.setItemProcessoDeCompra(itemProcesso);
        itemProposta.setLotePropostaFornecedorDispensa(loteProposta);
        itemProposta.setNumero(itemProcesso.getNumero());
        itemProposta.setQuantidade(itemProcesso.getQuantidade());
        itemProposta.setPreco(itemProcesso.getItemSolicitacaoMaterial().getPreco());
        return itemProposta;
    }

    public LotePropostaFornecedorDispensa populaDadosLoteProposta(LoteProcessoDeCompra loteProcesso) {
        if (!fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta().isEmpty()) {
            for (LotePropostaFornecedorDispensa loteProposta : fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                if (loteProposta.getLoteProcessoDeCompra().equals(loteProcesso)) {
                    return loteProposta;
                }
            }
        }
        LotePropostaFornecedorDispensa lotePropostaRetorno = new LotePropostaFornecedorDispensa();
        lotePropostaRetorno.setPropostaFornecedorDispensa(fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa());
        lotePropostaRetorno.setLoteProcessoDeCompra(loteProcesso);
        lotePropostaRetorno.setValor(loteProcesso.getValor());
        return lotePropostaRetorno;
    }

    public void novaPropostaDoFornecedor() {
        fornecedorDispensaDeLicitacaoSelecionado.setPropostaFornecedorDispensa(new PropostaFornecedorDispensa());
        fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().setDataDaProposta(dispensaDeLicitacaoFacade.getSistemaFacade().getDataOperacao());
        fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().setLotesDaProposta(new ArrayList<LotePropostaFornecedorDispensa>());
    }

    public void cancelarLoteSelecionado() {
        this.loteProcessoDeCompraSelecionado = null;
    }

    public List<LoteProcessoDeCompra> getLotesDoProcessoDeCompra() {
        if (selecionado != null && selecionado.getProcessoDeCompra() != null) {
            selecionado.setProcessoDeCompra(carregaListasDoProcessoDeCompraSelecionado(selecionado.getProcessoDeCompra()));
            return selecionado.getProcessoDeCompra().getLotesProcessoDeCompra();
        }
        return new ArrayList<>();
    }

    public void carregarDocumentosNecessariosDispensa(FornecedorDispensaDeLicitacao fornecedor) {
        try {
            podeCarregarDocumentos();
            fornecedorDispensaDeLicitacaoSelecionado = fornecedor;
            FacesUtil.executaJavaScript("dialogHabFornecedor.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void podeCarregarDocumentos() throws ValidacaoException {
        processoDeCompraValido();
        documentosValidos();
    }

    private void processoDeCompraValido() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getProcessoDeCompra() == null) {
            if (fornecedorDispensaDeLicitacaoSelecionado != null && fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O processo de compra deve ser informado antes de lançar a proposta do fornecedor.");
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O processo de compra deve ser informado antes de habilitar o fornecedor.");
            }
        }
        ve.lancarException();
    }

    public Boolean desabilitaBotaoAdicionarDocumentoFornecedor(DoctoHabilitacao documentoHabilitacao) {
        try {
            for (DocumentoFornecedorDispensaDeLicitacao documentoFornecedor : fornecedorDispensaDeLicitacaoSelecionado.getDocumentos()) {
                if (documentoFornecedor.getDocumentoHabilitacao().equals(documentoHabilitacao)) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    public String tituloBotaoAdicionarDocumentoFornecedor(DoctoHabilitacao documentoHabilitacao) {
        return desabilitaBotaoAdicionarDocumentoFornecedor(documentoHabilitacao) ? "O documento já foi adicionado a lista de documentos apresentados." : "Clique para vincular este documento.";
    }

    public void passarDocumentoDaDispensaParaFornecedor(DoctoHabilitacao documento) {

        this.documentoFornecedorDispensaDeLicitacaoSelecionado = new DocumentoFornecedorDispensaDeLicitacao();
        this.documentoFornecedorDispensaDeLicitacaoSelecionado.setDocumentoHabilitacao(documento);
        FacesUtil.atualizarComponente("form-documento-fornecedor");
        FacesUtil.executaJavaScript("dialogDocumentoFornecedor.show()");
    }

    public void removerDocumentoDoFornecedor(DocumentoFornecedorDispensaDeLicitacao item) {
        fornecedorDispensaDeLicitacaoSelecionado.getDocumentos().remove(item);
    }

    public void confirmarTodosDocumentosDoFornecedor() {
        fornecedorDispensaDeLicitacaoSelecionado.processarClassificacaoDesteFornecedor(dispensaDeLicitacaoFacade.getSistemaFacade().getDataOperacao());
        notificarUsuarioSobreStatusFornecedor();
        selecionado.setFornecedores(Util.adicionarObjetoEmLista(selecionado.getFornecedores(), fornecedorDispensaDeLicitacaoSelecionado));
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:tabela-fornecedores");
        FacesUtil.executaJavaScript("dialogHabFornecedor.hide()");
        atualizarItens();
    }

    private void notificarUsuarioSobreStatusFornecedor() {
        if (fornecedorDispensaDeLicitacaoSelecionado.isFornecedorInabilitado() && fornecedorDispensaDeLicitacaoSelecionado.temDocumentos()) {
            FacesUtil.addError(fornecedorDispensaDeLicitacaoSelecionado.getTipoClassificacaoFornecedor().getDescricao(), fornecedorDispensaDeLicitacaoSelecionado.getJustificativaDaClassificacao());
            return;
        }
        if (fornecedorDispensaDeLicitacaoSelecionado.isFornecedorInabilitado() && !fornecedorDispensaDeLicitacaoSelecionado.temDocumentos()) {
            FacesUtil.addError(fornecedorDispensaDeLicitacaoSelecionado.getTipoClassificacaoFornecedor().getDescricao(), "Não foram apresentados documentos pelo fornecedor.");
            return;
        }
        if (fornecedorDispensaDeLicitacaoSelecionado.isFornecedorHabilitadoComRessalva()) {
            FacesUtil.addWarn(fornecedorDispensaDeLicitacaoSelecionado.getTipoClassificacaoFornecedor().getDescricao(), fornecedorDispensaDeLicitacaoSelecionado.getJustificativaDaClassificacao());
            return;
        }
        if (fornecedorDispensaDeLicitacaoSelecionado.isFornecedorHabilitado()) {
            FacesUtil.addInfo(fornecedorDispensaDeLicitacaoSelecionado.getTipoClassificacaoFornecedor().getDescricao(), "O fornecedor foi habilitado com sucesso.");
            return;
        }
    }

    private void atualizarItens() {
        if (!fornecedorDispensaDeLicitacaoSelecionado.temDocumentos()) {
            for (ItemPropostaFornecedor ipf : fornecedorDispensaDeLicitacaoSelecionado.getPessoa().getItensPropostaFornecedor()) {
                ipf.getItemPregao().setStatusFornecedorVencedor(TipoClassificacaoFornecedor.INABILITADO);
            }
        }
    }

    public void confirmarDocumentoSelecionadoDoFornecedor() {
        try {
            documentoFornecedorDispensaDeLicitacaoSelecionado.validarCamposObrigatoriosDocumentosSelecionados();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return;
        }
        documentoFornecedorDispensaDeLicitacaoSelecionado.setFornecedorDispensa(fornecedorDispensaDeLicitacaoSelecionado);
        fornecedorDispensaDeLicitacaoSelecionado.setDocumentos(Util.adicionarObjetoEmLista(fornecedorDispensaDeLicitacaoSelecionado.getDocumentos(), documentoFornecedorDispensaDeLicitacaoSelecionado));
        FacesUtil.atualizarComponente("formulario-hab-fornecedor");
        FacesUtil.executaJavaScript("dialogDocumentoFornecedor.hide()");
    }

    public String getCorDaDescricao(String descricao) {
        if ("Habilitado".equalsIgnoreCase(descricao)) {
            return "green";
        }
        if ("Habilitado com Ressalva".equalsIgnoreCase(descricao)) {
            return "goldenrod";
        }
        return "#cd0a0a";
    }

    public void validaValorInformado(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addOperacaoNaoPermitida("O valor deve ser maior que 0.");
        }
    }

    public BigDecimal getValorTotalDoItem(ItemPropostaFornecedorDispensa item) {
        if (item.getPreco() == null || item.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getItemCotacao().isTipoControlePorQuantidade()) {
            return item.getItemProcessoDeCompra().getQuantidade().multiply(item.getPreco()).setScale(2, RoundingMode.HALF_EVEN);
        }
        return item.getPreco();
    }

    public BigDecimal getValorTotalDaProposta() {
        BigDecimal total = BigDecimal.ZERO;

        if (fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa() != null &&
            !fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta().isEmpty()) {
            for (LotePropostaFornecedorDispensa lotePropostaFornecedorDispensa : fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                for (ItemPropostaFornecedorDispensa itemProposta : lotePropostaFornecedorDispensa.getItensProposta()) {
                    total = total.add(getValorTotalDoItem(itemProposta));
                }
            }
        }
        return total;
    }

    public void confirmarProposta() {
        if (podeConfirmarProposta()) {
            removerOsItensQueOFornecedorNaoSelecionou();
            selecionado.setFornecedores(Util.adicionarObjetoEmLista(selecionado.getFornecedores(), fornecedorDispensaDeLicitacaoSelecionado));
            RequestContext.getCurrentInstance().update("Formulario:tab-view-geral:panel-fornecedor");
            FacesUtil.executaJavaScript("dialogPropostaFornecedor.hide()");
            cancelarFornecedorDispensa();
            limparLotesItensOriginaisDoFornecedor();
        }
    }

    private void limparLotesItensOriginaisDoFornecedor() {
        mapBackup.clear();
    }

    public void cancelarProposta() {
        if (fornecedorDispensaDeLicitacaoSelecionado.jaLancouPrecoDePeloMenosUmItem()) {
            fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta().clear();

            for (Map.Entry<LotePropostaFornecedorDispensa, List<ItemPropostaFornecedorDispensa>> entry : mapBackup.entrySet()) {
                LotePropostaFornecedorDispensa lote = entry.getKey();
                lote.setItensProposta(entry.getValue());
                fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta().add(lote);
            }
            atribuirNullPropostaFornecedorDispensa();
        }
        selecionado.setFornecedores(Util.adicionarObjetoEmLista(selecionado.getFornecedores(), fornecedorDispensaDeLicitacaoSelecionado));
        limparLotesItensOriginaisDoFornecedor();
        cancelarFornecedorDispensa();
    }

    private void atribuirNullPropostaFornecedorDispensa() {
        boolean selecionado = false;
        for (Map.Entry<LotePropostaFornecedorDispensa, List<ItemPropostaFornecedorDispensa>> entry : mapBackup.entrySet()) {
            for (ItemPropostaFornecedorDispensa item : entry.getValue()) {
                if (item.getSelecionado()) {
                    selecionado = true;
                    break;
                }
            }
        }
        if (!selecionado) {
            fornecedorDispensaDeLicitacaoSelecionado.setPropostaFornecedorDispensa(null);
        }
    }

    private void removerOsItensQueOFornecedorNaoSelecionou() {
        removerItens();
        removerLotes();
    }

    private void removerItens() {
        List<ItemPropostaFornecedorDispensa> itensRemocao = retornarItensParaRemocao();
        for (ItemPropostaFornecedorDispensa itemProposta : itensRemocao) {
            for (LotePropostaFornecedorDispensa loteProposta : fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                if (loteProposta.getItensProposta().contains(itemProposta)) {
                    loteProposta.getItensProposta().remove(itemProposta);
                }
            }
        }
    }

    private List<ItemPropostaFornecedorDispensa> retornarItensParaRemocao() {
        List<ItemPropostaFornecedorDispensa> itensRemocao = new ArrayList<>();
        for (LotePropostaFornecedorDispensa loteProposta : fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta()) {
            for (ItemPropostaFornecedorDispensa itemProposta : loteProposta.getItensProposta()) {
                if (!itemProposta.getSelecionado()) {
                    itensRemocao.add(itemProposta);
                }
            }
        }
        return itensRemocao;
    }

    private void removerLotes() {
        List<LotePropostaFornecedorDispensa> lotesRemocao = retornarLotesParaRemocao();
        for (LotePropostaFornecedorDispensa loteProposta : lotesRemocao) {
            if (fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta().contains(loteProposta)) {
                fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta().remove(loteProposta);
            }
        }
    }

    private List<LotePropostaFornecedorDispensa> retornarLotesParaRemocao() {
        List<LotePropostaFornecedorDispensa> lotesRemocao = new ArrayList<>();
        for (LotePropostaFornecedorDispensa loteProposta : fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta()) {
            if (loteProposta.isLotePropostaSemItem()) {
                lotesRemocao.add(loteProposta);
            }
        }
        return lotesRemocao;
    }

    private boolean podeConfirmarProposta() {
        if (!validaCampoObrigatorioProposta()) {
            return false;
        }
        return true;
    }

    private boolean validaCampoObrigatorioProposta() {
        if (!validaPrazoDeValidade()) {
            return false;
        }
        if (!validaTipoPrazoDeValidade()) {
            return false;
        }
        if (!validaPrazoDeExecucao()) {
            return false;
        }
        if (!validaTipoPrazoDeExecucao()) {
            return false;
        }
        if (!validaDescricaoProdutoDoItem()) {
            return false;
        }
        return true;
    }

    private boolean validaPrazoDeValidade() {
        if (fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa() != null && fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getPrazoDeValidade() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo prazo de validade é obrigatório");
            return false;
        }
        return true;
    }

    private boolean validaTipoPrazoDeValidade() {
        if (fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa() != null && fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getTipoPrazoDeValidade() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo tipo prazo de validade é obrigatório");
            return false;
        }
        return true;
    }

    private boolean validaPrazoDeExecucao() {
        if (fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa() != null && fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getPrazoDeExecucao() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo prazo de execução é obrigatório");
            return false;
        }
        return true;
    }

    private boolean validaTipoPrazoDeExecucao() {
        if (fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa() != null && fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getTipoPrazoDeExecucao() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo tipo prazo de execução é obrigatório");
            return false;
        }
        return true;
    }

    private boolean validaDescricaoProdutoDoItem() {
        for (LotePropostaFornecedorDispensa lote : fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta()) {
            for (ItemPropostaFornecedorDispensa item : lote.getItensProposta()) {
                if (item.getSelecionado() && (item.getDescricaoProduto() == null || item.getDescricaoProduto().trim().isEmpty())) {
                    FacesUtil.addOperacaoNaoPermitida("O item Nº " + item.getNumero() + " está sem a descrição do produto!");
                    return false;
                }
            }
        }
        return true;
    }

    public List<ItemPropostaFornecedorDispensa> getItensDaProposta() {
        List<ItemPropostaFornecedorDispensa> retorno = new ArrayList<>();
        if (fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa() != null) {
            for (LotePropostaFornecedorDispensa loteProposta : fornecedorDispensaDeLicitacaoSelecionado.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                if (loteProposta.getItensProposta() != null) {
                    for (ItemPropostaFornecedorDispensa itemProposta : loteProposta.getItensProposta()) {
                        if (itemProposta != null)
                            retorno.add(itemProposta);
                    }
                }
            }
        }
        return retorno;
    }

    public boolean itemDisponivelParaProposta(ItemPropostaFornecedorDispensa itemDaLista) {
        for (FornecedorDispensaDeLicitacao fornecedor : selecionado.getFornecedores()) {
            if (!fornecedor.equals(fornecedorDispensaDeLicitacaoSelecionado)
                && fornecedor.jaLancouPrecoDePeloMenosUmItem()) {
                for (LotePropostaFornecedorDispensa loteProposta : fornecedor.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                    for (ItemPropostaFornecedorDispensa itemProposta : loteProposta.getItensProposta()) {
                        if (itemDaLista.getLotePropostaFornecedorDispensa().equals(loteProposta)) {
                            if ((itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao().equals(itemDaLista.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao()))
                                && (Util.isStringNulaOuVazia(itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar())
                                || itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar().equals(itemDaLista.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar()))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public ItemPropostaFornecedorDispensa getItemPropostaFornecedorDispensa() {
        return itemPropostaFornecedorDispensa;
    }

    public void setItemPropostaFornecedorDispensa(ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa) {
        this.itemPropostaFornecedorDispensa = itemPropostaFornecedorDispensa;
    }

    public String getModeloProduto() {
        return modeloProduto;
    }

    public void setModeloProduto(String modeloProduto) {
        this.modeloProduto = modeloProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public void carregaInformacoesProduto(ItemPropostaFornecedorDispensa item, String nomeComponente) {
        itemPropostaFornecedorDispensa = item;
        modeloProduto = itemPropostaFornecedorDispensa.getModelo();
        descricaoProduto = itemPropostaFornecedorDispensa.getDescricaoProduto();
        FacesUtil.atualizarComponente(nomeComponente);
    }

    public void confirmaInformacoesProduto() {
        if (descricaoProduto == null || descricaoProduto.trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("A descrição do produto é obrigatória!");
            return;
        }
        itemPropostaFornecedorDispensa.setModelo(modeloProduto);
        itemPropostaFornecedorDispensa.setDescricaoProduto(descricaoProduto);
        FacesUtil.executaJavaScript("dlgInfoProduto.hide()");
    }

    public void limparInformacoesDaProposta(ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa) {
        if (!itemPropostaFornecedorDispensa.getSelecionado()) {
            itemPropostaFornecedorDispensa.setMarca(null);
            itemPropostaFornecedorDispensa.setModelo(null);
            itemPropostaFornecedorDispensa.setDescricaoProduto(null);
        }
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(HierarquiaOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public void setUnidadeAdministrativaProcessoCompra() {
        setUnidadeAdministrativa(dispensaDeLicitacaoFacade.getUnidadeOrganizacionalFacade().getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getProcessoDeCompra().getUnidadeOrganizacional(),
            dispensaDeLicitacaoFacade.getSistemaFacade().getDataOperacao()
        ));
    }

    public void selecionarTodosItensPropostaFornecedor() {
        for (ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa : getItensDaProposta()
        ) {
            itemPropostaFornecedorDispensa.setSelecionado(true);
        }
    }

    public void desmarcarTodosItensPropostaFornecedor() {
        for (ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa : getItensDaProposta()
        ) {
            itemPropostaFornecedorDispensa.setSelecionado(false);
        }
    }

    public boolean hasTodosItensPropostaFornecedorSelecionados() {
        Integer quantidadeSelecionado = 0;
        for (ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa : getItensDaProposta()
        ) {
            quantidadeSelecionado = itemPropostaFornecedorDispensa.getSelecionado() == true ? quantidadeSelecionado + 1 : quantidadeSelecionado;
        }
        return quantidadeSelecionado - getItensDaProposta().size() == 0;
    }

    private void novoFiltroResumoExecucao() {
        filtroResumoExecucaoVO = new FiltroResumoExecucaoVO(TipoResumoExecucao.PROCESSO);
        filtroResumoExecucaoVO.setIdProcesso(selecionado.getId());
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.DISPENSA_LICITACAO_INEXIGIBILIDADE);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
        if ("tab-ajuste".equals(tab)) {
            filtroAjusteProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.DISPENSA_LICITACAO_INEXIGIBILIDADE);
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public Boolean getBloquearEdicao() {
        return bloquearEdicao;
    }

    public void setBloquearEdicao(Boolean bloquearEdicao) {
        this.bloquearEdicao = bloquearEdicao;
    }

    public boolean isDispensaComDisputa() {
        try {
            return selecionado.getModalidade().isDispensaLicitacao()
                && selecionado.getProcessoDeCompra().getSolicitacaoMaterial().getModoDisputa().isDispensaComDisputa();
        } catch (Exception e) {
            return false;
        }
    }

    public void atualizarDataEncerramento() {
        if (selecionado.getDataDaDispensa() != null) {
            selecionado.setEncerradaEm(DataUtil.adicionarMinutos(selecionado.getDataDaDispensa(), 30));
        }
    }

    public FiltroResumoExecucaoVO getFiltroResumoExecucaoVO() {
        return filtroResumoExecucaoVO;
    }

    public void setFiltroResumoExecucaoVO(FiltroResumoExecucaoVO filtroResumoExecucaoVO) {
        this.filtroResumoExecucaoVO = filtroResumoExecucaoVO;
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroAjusteProcesso() {
        return filtroAjusteProcesso;
    }

    public void setFiltroAjusteProcesso(FiltroHistoricoProcessoLicitatorio filtroAjusteProcesso) {
        this.filtroAjusteProcesso = filtroAjusteProcesso;
    }
}
