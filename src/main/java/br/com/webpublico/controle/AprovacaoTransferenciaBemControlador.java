package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoTransferencia;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AprovacaoTransferenciaBemFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-aprovacao-transf-bem", pattern = "/aprovacao-transferencia-bens-moveis/novo/", viewId = "/faces/administrativo/patrimonio/aprovacao-transf-bem/edita.xhtml"),
    @URLMapping(id = "editar-aprovacao-transf-bem", pattern = "/aprovacao-transferencia-bens-moveis/editar/#{aprovacaoTransferenciaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/aprovacao-transf-bem/edita.xhtml"),
    @URLMapping(id = "listar-aprovacao-transf-bem", pattern = "/aprovacao-transferencia-bens-moveis/listar/", viewId = "/faces/administrativo/patrimonio/aprovacao-transf-bem/lista.xhtml"),
    @URLMapping(id = "ver-aprovacao-transf-bem", pattern = "/aprovacao-transferencia-bens-moveis/ver/#{aprovacaoTransferenciaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/aprovacao-transf-bem/visualizar.xhtml")})

public class AprovacaoTransferenciaBemControlador extends PrettyControlador<AprovacaoTransferenciaBem> implements CRUD {

    @EJB
    private AprovacaoTransferenciaBemFacade facade;
    private AssistenteMovimentacaoBens assistente;
    private TipoDocumentoAnexo tipoDocumentoAnexo;
    private Future<List<VOItemSolicitacaoTransferencia>> futurePesquisaBem;
    private Future<AprovacaoTransferenciaBem> futureTransfereBem;
    private List<VOItemSolicitacaoTransferencia> bensSolicitacao;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public AprovacaoTransferenciaBemControlador() {
        super(AprovacaoTransferenciaBem.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aprovacao-transferencia-bens-moveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "nova-aprovacao-transf-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaAprovacaoMovel() {
        try {
            super.novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            selecionado.setDataAprovacao(getDataOperacao());
            selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            bensSolicitacao = Lists.newArrayList();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "ver-aprovacao-transf-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAprovacaoMovel() {
        try {
            operacao = Operacoes.VER;
            recuperarObjeto();
            bensSolicitacao = facade.buscarBensAprovacao(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "editar-aprovacao-transf-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarAprovacaoMovel() {
        super.editar();
        recuperarAtributos();
    }

    @Override
    public void salvar() {
        try {
            novoAssistenteMovimentacao();
            validarSalvar();
            bloquearMovimentacaoBens();
            assistente.setTotal(bensSolicitacao.size());
            futureTransfereBem = facade.salvarAprovacaoAsync(bensSolicitacao, selecionado, assistente);
            FacesUtil.executaJavaScript("iniciaTransferencia()");
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            desbloquearMovimentacaoSingleton();
            assistente.descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            recuperarConfiguracaoMovimentacaoBem();
            facade.remover(selecionado, configMovimentacaoBem);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void pesquisar() {
        try {
            novoAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            iniciarListas();
            futurePesquisaBem = facade.pesquisarBem(assistente, selecionado.getSolicitacaoTransferencia());
            FacesUtil.executaJavaScript("iniciaPesquisa();");
        } catch (MovimentacaoBemException me) {
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            limparDadosSolicitacao();
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (selecionado.isRecusada() && Strings.isNullOrEmpty(selecionado.getMotivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo motivo deve ser informado.");
        }
        if (bensSolicitacao.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Solicitação não possui bens para ser aprovado.");
        }
        if (facade.hasAprovacaoPorSolicitacao(selecionado.getSolicitacaoTransferencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma Aprovação de Transfêrencia de Bens com essa Solicitação.");
        }
        validarObrigatoriedadeAnexo();
        validarDataLancamentoAndDataOperacaoBem();
        ve.lancarException();
        validarFase(ve);
    }

    private void validarFase(ValidacaoException ve) {
        Set<UnidadeOrganizacional> unidadesOrigem = Sets.newHashSet();
        for (VOItemSolicitacaoTransferencia vo : bensSolicitacao) {
            unidadesOrigem.add(vo.getEstadoBem().getDetentoraOrcamentaria());
        }
        validarFasePorUnidade(ve, unidadesOrigem);
        ve.lancarException();

    }

    private void validarFasePorUnidade(ValidacaoException ve, Set<UnidadeOrganizacional> unidadesDestino) {
        for (UnidadeOrganizacional unidade : unidadesDestino) {
            Exercicio exercicio = facade.getExercicioFacade().getExercicioPorAno(DataUtil.getAno(selecionado.getDataAprovacao()));
            if (facade.getFaseFacade().temBloqueioFaseParaRecurso("/administrativo/patrimonio/aprovacao-transf-bem/edita.xhtml", selecionado.getDataAprovacao(), unidade, exercicio)) {
                HierarquiaOrganizacional hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataAprovacao(), unidade, TipoHierarquiaOrganizacional.ORCAMENTARIA);
                ve.adicionarMensagemDeOperacaoNaoRealizada("A data " + DataUtil.getDataFormatada(selecionado.getDataAprovacao()) + " está fora do período fase para a unidade " + hierarquiaOrganizacional.toString() + "!");
                break;
            }
        }
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoDaSolicitacao situacao : SituacaoDaSolicitacao.values()) {
            if (SituacaoDaSolicitacao.ACEITA.equals(situacao) ||
                SituacaoDaSolicitacao.RECUSADA.equals(situacao)) {
                toReturn.add(new SelectItem(situacao, situacao.toString()));
            }
        }
        return toReturn;
    }

    public void limparDadosSolicitacao() {
        selecionado.setSolicitacaoTransferencia(null);
        iniciarListas();
        selecionado.setMotivo(null);
    }

    public List<LoteTransferenciaBem> completarSolicitacaoTransferencia(String parte) {
        return facade.getLoteTransferenciaFacade().buscarSolicitacaoTransferenciaAprovacao(parte.trim());
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataAprovacao(), getDataOperacao(), operacao);
        }
    }

    private void validarObrigatoriedadeAnexo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getSolicitacaoTransferencia().getTipoTransferencia().isExterna() && selecionado.isAceita()) {
            if (selecionado.getAnexos() == null || selecionado.getAnexos().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório anexar um documento da solicitação de transferência assinada pelo secretário autorizando " +
                    "a mudança de unidade.");
            } else {

                facade.getDocumentosNecessariosAnexoFacade().validarSeTodosOsTipoDeDocumentoForamAnexados(TipoFuncionalidadeParaAnexo.APROVACAO_TRANSFERENCIA_EXTERNA, selecionado.getAnexos());
            }
        }
        ve.lancarException();
    }

    public void carregarArquivo(FileUploadEvent event) {
        try {
            Arquivo arquivo = definirArquivo(event);
            AprovacaoTransfBemAnexo anexo = new AprovacaoTransfBemAnexo();
            anexo.setArquivo(arquivo);
            anexo.setAprovacao(selecionado);
            anexo.setTipoDocumentoAnexo(getTipoDocumentoAnexo());
            selecionado.setAnexos(Util.adicionarObjetoEmLista(selecionado.getAnexos(), anexo));
        } catch (Exception ex) {
            descobrirETratarException(ex);
            logger.error(ex.getMessage());
        }
    }

    private Arquivo definirArquivo(FileUploadEvent event) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setMimeType(event.getFile().getContentType());
        arquivo.setNome(event.getFile().getFileName());
        arquivo.setDescricao(event.getFile().getFileName());
        arquivo.setTamanho(event.getFile().getSize());
        arquivo.setInputStream(event.getFile().getInputstream());
        arquivo = facade.getArquivoFacade().novoArquivoMemoria(arquivo);
        return arquivo;
    }

    public List<SelectItem> getTiposDocumentos() {
        List<TipoDocumentoAnexo> anexos = facade.getTipoDocumentoAnexoFacade().buscarTodosTipoDocumentosAnexo();
        return Util.getListSelectItem(anexos, true);
    }

    public void removerAnexo(AprovacaoTransfBemAnexo anexo) {
        this.selecionado.getAnexos().remove(anexo);
    }

    public StreamedContent recuperarArquivo(Arquivo arquivo) {
        return facade.getArquivoFacade().montarArquivoParaDownloadPorArquivo(arquivo);
    }


    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataAprovacao(), OperacaoMovimentacaoBem.APROVACAO_TRANSFERENCIA_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }


    private void bloquearMovimentacaoBens() {
        facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(AprovacaoTransferenciaBem.class, selecionado.getSolicitacaoTransferencia().getUnidadeOrigem(), assistente);
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistente, AprovacaoTransferenciaBem.class);
    }

    public void consultarAndamentoAprovacao() {
        try {
            if (futureTransfereBem != null && futureTransfereBem.isDone()) {
                selecionado = futureTransfereBem.get();
                if (selecionado != null) {
                    FacesUtil.executaJavaScript("encerrarTransferencia()");
                }
                futurePesquisaBem = null;
            }
        } catch (Exception ex) {
            assistente.setBloquearAcoesTela(true);
            FacesUtil.executaJavaScript("clearInterval(timer)");
            FacesUtil.executaJavaScript("closeDialog(dlgPesquisa)");
            FacesUtil.atualizarComponente("Formulario");
            futureTransfereBem = null;
            desbloquearMovimentacaoSingleton();
            assistente.descobrirETratarException(ex);
        }
    }

    public void redirecionarAposSalvar() {
        desbloquearMovimentacaoSingleton();
        FacesUtil.addOperacaoRealizada("Solicitação de transferência de bens salva com sucesso.");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    private void novoAssistenteMovimentacao() {
        if (configMovimentacaoBem == null) {
            recuperarConfiguracaoMovimentacaoBem();
        }
        assistente = new AssistenteMovimentacaoBens(selecionado.getDataAprovacao(), operacao);
        assistente.zerarContadoresProcesso();
        assistente.setSelecionado(selecionado);
        assistente.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistente.setDataAtual(facade.getSistemaFacade().getDataOperacao());
        assistente.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        if (selecionado.getSolicitacaoTransferencia() != null) {
            assistente.setUnidadeOrganizacional(selecionado.getSolicitacaoTransferencia().getUnidadeOrigem());
        }
    }

    private void recuperarAtributos() {
        iniciarListas();
        novoAssistenteMovimentacao();
    }

    private void iniciarListas() {
        bensSolicitacao = Lists.newArrayList();
    }

    public void consultarAndamentoPesquisa() {
        if (futurePesquisaBem != null && futurePesquisaBem.isDone()) {
            FacesUtil.executaJavaScript("encerrarPesquisa()");
        }
    }

    public void encerrarPesquisa() throws ExecutionException, InterruptedException {
        bensSolicitacao = futurePesquisaBem.get();
        Collections.sort(bensSolicitacao);
        FacesUtil.atualizarComponente("Formulario");
    }

    public BigDecimal valorTotalDosAjustes() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (bensSolicitacao != null && !bensSolicitacao.isEmpty()) {
            for (VOItemSolicitacaoTransferencia bem : bensSolicitacao) {
                valorTotal = valorTotal.add(bem.getValorAjuste());
            }
        }
        return valorTotal;
    }

    public BigDecimal valorTotalDosBens() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (bensSolicitacao != null && !bensSolicitacao.isEmpty()) {
            for (VOItemSolicitacaoTransferencia bem : bensSolicitacao) {
                valorTotal = valorTotal.add(bem.getValorOriginal());
            }
        }
        return valorTotal;
    }

    public void atribuirNullMotivo() {
        selecionado.setMotivo(null);
    }

    public AssistenteMovimentacaoBens getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteMovimentacaoBens assistente) {
        this.assistente = assistente;
    }

    public List<VOItemSolicitacaoTransferencia> getBensSolicitacao() {
        return bensSolicitacao;
    }

    public void setBensSolicitacao(List<VOItemSolicitacaoTransferencia> bensSolicitacao) {
        this.bensSolicitacao = bensSolicitacao;
    }

    public TipoDocumentoAnexo getTipoDocumentoAnexo() {
        return tipoDocumentoAnexo;
    }

    public void setTipoDocumentoAnexo(TipoDocumentoAnexo tipoDocumentoAnexo) {
        this.tipoDocumentoAnexo = tipoDocumentoAnexo;
    }
}
