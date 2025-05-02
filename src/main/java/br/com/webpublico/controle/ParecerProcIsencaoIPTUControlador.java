package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.IsencaoCadastroImobiliario;
import br.com.webpublico.entidades.ParecerProcIsencaoIPTU;
import br.com.webpublico.entidades.ProcessoIsencaoIPTU;
import br.com.webpublico.entidadesauxiliares.AssistenteParecerIsencaoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParecerProcIsencaoIPTUFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParecerIsencaoIPTU", pattern = "/parecer-processo-de-isencao-de-iptu/novo/", viewId = "/faces/tributario/iptu/isencaoimpostos/parecerprocessoisencao/edita.xhtml"),
    @URLMapping(id = "editarParecerIsencaoIPTU", pattern = "/parecer-processo-de-isencao-de-iptu/editar/#{parecerProcIsencaoIPTUControlador.id}/", viewId = "/faces/tributario/iptu/isencaoimpostos/parecerprocessoisencao/edita.xhtml"),
    @URLMapping(id = "verParecerIsencaoIPTU", pattern = "/parecer-processo-de-isencao-de-iptu/ver/#{parecerProcIsencaoIPTUControlador.id}/", viewId = "/faces/tributario/iptu/isencaoimpostos/parecerprocessoisencao/visualizar.xhtml"),
    @URLMapping(id = "listarParecerIsencaoIPTU", pattern = "/parecer-processo-de-isencao-de-iptu/listar/", viewId = "/faces/tributario/iptu/isencaoimpostos/parecerprocessoisencao/lista.xhtml")
})
public class ParecerProcIsencaoIPTUControlador extends PrettyControlador<ParecerProcIsencaoIPTU> implements CRUD {

    @EJB
    private ParecerProcIsencaoIPTUFacade parecerFacade;

    private AbstractReport abstractReport;
    private Future<AssistenteParecerIsencaoIPTU> futureIsencoes;
    private AssistenteParecerIsencaoIPTU assitenteIsencao;
    private String caminhoListarIsencoes;
    private String caminhoNovoIsencoes;
    private Boolean recalculado;
    private ProcessoIsencaoIPTU solicitacaoSelecionada;
    private IsencaoCadastroImobiliario isencao;

    public ParecerProcIsencaoIPTUControlador() {
        super(ParecerProcIsencaoIPTU.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return parecerFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parecer-processo-de-isencao-de-iptu/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AssistenteParecerIsencaoIPTU getAssitenteIsencao() {
        return assitenteIsencao;
    }

    public void setAssitenteIsencao(AssistenteParecerIsencaoIPTU assitenteIsencao) {
        this.assitenteIsencao = assitenteIsencao;
    }

    public String getCaminhoListarIsencoes() {
        return caminhoListarIsencoes;
    }

    public void setCaminhoListarIsencoes(String caminhoListarIsencoes) {
        this.caminhoListarIsencoes = caminhoListarIsencoes;
    }

    public String getCaminhoNovoIsencoes() {
        return caminhoNovoIsencoes;
    }

    public void setCaminhoNovoIsencoes(String caminhoNovoIsencoes) {
        this.caminhoNovoIsencoes = caminhoNovoIsencoes;
    }

    public Boolean getRecalculado() {
        return recalculado;
    }

    public void setRecalculado(Boolean recalculado) {
        this.recalculado = recalculado;
    }

    public ProcessoIsencaoIPTU getSolicitacaoSelecionada() {
        return solicitacaoSelecionada;
    }

    public void setSolicitacaoSelecionada(ProcessoIsencaoIPTU solicitacaoSelecionada) {
        this.solicitacaoSelecionada = solicitacaoSelecionada;
    }

    @Override
    @URLAction(mappingId = "novoParecerIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarDadosNovo();
    }

    @Override
    @URLAction(mappingId = "editarParecerIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        solicitacaoSelecionada = (ProcessoIsencaoIPTU) Util.clonarObjeto(selecionado.getSolicitacaoIsencao());
    }

    @Override
    @URLAction(mappingId = "verParecerIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        inicializarDadosVer();
    }

    @Override
    public void salvar() {
        try {
            validarParecer();
            selecionado.setSolicitacaoIsencao(solicitacaoSelecionada);
            salvarRetornando(getMensagemSucessoAoSalvar());
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar parecer de isenção de IPTU. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Parecer de Isenção de IPTU. Detalhes: " + e.getMessage());
        }
    }

    private void validarParecer() {
        ValidacaoException ve = new ValidacaoException();
        if (this.solicitacaoSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Solicitação de Isenção de IPTU deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getJustificativa())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo justificativa deve ser informado.");
        }
        ve.lancarException();
    }

    private void salvarRetornando(String mensagem) {
        selecionado = parecerFacade.salvarRetornando(selecionado);
        if (!Strings.isNullOrEmpty(mensagem))
            FacesUtil.addOperacaoRealizada(mensagem);
    }

    private void redirecionarParaVer() {
        Web.limpaNavegacao();
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void inicializarDadosNovo() {
        selecionado.setDataParecer(parecerFacade.recuperarDataLogada());
        selecionado.setUsuarioParecer(parecerFacade.recuperarUsuarioLogado());
    }

    private void inicializarDadosVer() {
        abstractReport = AbstractReport.getAbstractReport();
        if (caminhoListarIsencoes == null) {
            caminhoListarIsencoes = caminhaPadraoSolicitacao() + "listar-isencao/";
        }
        if (caminhoNovoIsencoes == null) {
            caminhoNovoIsencoes = caminhaPadraoSolicitacao() + "novo/";
        }
        if(recalculado == null) {
            recalculado = parecerFacade.isRecalculado(selecionado.getId());
        }
        isencao = parecerFacade.recuperarIsencao(selecionado.getSolicitacaoIsencao().getId());
    }

    private String caminhaPadraoSolicitacao() {
        return "/solicitacao-processo-de-isencao-de-iptu/";
    }

    public List<ProcessoIsencaoIPTU> buscarSolicitacoesIsencao(String parte) {
        return parecerFacade.buscarSolicitacoesIsencao(parte);
    }

    public boolean isSolicitacaoEmAberto() {
        return selecionado != null && selecionado.getJustificativa() != null && ProcessoIsencaoIPTU.Situacao.EFETIVADO.equals(selecionado.getSolicitacaoIsencao().getSituacao());
    }

    public void deferirIsencaoIPTU() {
        try {
            deferirOrIndeferirIsencaoIPTU(ProcessoIsencaoIPTU.Situacao.DEFERIDO, IsencaoCadastroImobiliario.Situacao.ATIVO,
                "Deferindo Isenções de IPTU...");
            executarPoll("pollDeferir.start()");
        } catch (Exception e) {
            logger.error("Erro ao deferir isenção de IPTU. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao Deferir Isenção de IPTU. Detalhes: " + e.getMessage());
        }
    }

    public void indeferirIsencaoIPTU() {
        try {
            deferirOrIndeferirIsencaoIPTU(ProcessoIsencaoIPTU.Situacao.INDEFERIDO, IsencaoCadastroImobiliario.Situacao.CANCELADO,
                "Indeferindo Isenções de IPTU...");
            executarPoll("pollIndeferir.start()");
        } catch (Exception e) {
            logger.error("Erro ao indeferir isenção de IPTU. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao Indeferir Isenção de IPTU. Detalhes: " + e.getMessage());
        }
    }

    private void deferirOrIndeferirIsencaoIPTU(ProcessoIsencaoIPTU.Situacao deferido, IsencaoCadastroImobiliario.Situacao ativo, String s) {
        assitenteIsencao = new AssistenteParecerIsencaoIPTU();
        assitenteIsencao.setSituacaoSolicitacao(deferido);
        assitenteIsencao.setSituacaoIsencao(ativo);
        assitenteIsencao.setMensagem(s);
        assitenteIsencao.setParecerIsencaoIPTU(selecionado);
        futureIsencoes = parecerFacade.deferirOrIndeferirIsencao(assitenteIsencao);

        abrirDialogProgressBar();
    }

    private void abrirDialogProgressBar() {
        FacesUtil.executaJavaScript("dialogProgres.show()");
    }

    private void executarPoll(String poll) {
        FacesUtil.executaJavaScript(poll);
    }

    public void abortar() {
        if (futureIsencoes != null) {
            futureIsencoes.cancel(true);
            assitenteIsencao.getBarraProgresso().finaliza();
        }
    }

    public void finalizarDeferimento() {
        if (futureIsencoes != null && futureIsencoes.isDone()) {
            if (!assitenteIsencao.getBarraProgresso().getCalculando()) {
                fecharDialog();
                try {
                    if (futureIsencoes.get().getParecerIsencaoIPTU() != null) {
                        salvarRetornando("");
                        FacesUtil.executaJavaScript("dialogPosDeferir.show()");
                    }
                } catch (Exception e) {
                    FacesUtil.addError("Erro ao deferir parecer...", e.getMessage());
                }
            }
        }
    }

    public void finalizarIndeferimento() {
        if (futureIsencoes != null && futureIsencoes.isDone()) {
            if (!assitenteIsencao.getBarraProgresso().getCalculando()) {
                fecharDialog();
                try {
                    if (futureIsencoes.get().getParecerIsencaoIPTU() != null) {
                        salvarRetornando("Todas as Isenções foram Indeferidas com sucesso.");
                        redirecionarParaVer();
                    }
                } catch (Exception e) {
                    FacesUtil.addError("Erro ao indeferir parecer...", e.getMessage());
                }
            }
        }
    }

    private void fecharDialog() {
        FacesUtil.executaJavaScript("dialogProgres.hide()");
        FacesUtil.atualizarComponente("Formulario");
    }

    public void imprimirRelacao(boolean enquadra) {
        String where = parecerFacade.montarWhere(selecionado.getSolicitacaoIsencao(), enquadra);
        geraRelatorio(where, parecerFacade.recuperarUsuarioLogado().getLogin(),
            (enquadra ? "Relação de Conferência da Solicitação do Processo de Isenção - Imóveis Enquadrados"
                : "Relação de Conferência da Solicitação do Processo de Isenção - Imóveis Não Enquadrados"));
    }

    public void geraRelatorio(String where, String usuario, String nomeRelatorio) {
        String arquivoJasper = "RelacaoConferenciaProcessoIsencao.jasper";
        HashMap<String, Object> parametros = Maps.newHashMap();
        parametros.put("BRASAO", abstractReport.getCaminhoImagem());
        parametros.put("USUARIO", usuario);
        parametros.put("NOME_RELATORIO", nomeRelatorio);
        parametros.put("WHERE", where);
        try {
            abstractReport.gerarRelatorio(arquivoJasper, parametros);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error("Erro: ", ex);
        }
    }

    public String verParecer(Long idIsencao) {
        Long idParecer = parecerFacade.buscarIdParecerPeloIdIsencao(idIsencao);
        if (idParecer != null) {
            return getCaminhoPadrao() + "ver/" + idParecer + "/";
        }
        return getCaminhoListarIsencoes();
    }

    public void visualizarSolicitacao() {
        Web.navegacao(getUrlAtual(), caminhaPadraoSolicitacao() + "ver/" + selecionado.getSolicitacaoIsencao().getId() + "/");
    }

    public void validarSolicitacao() {
        if (solicitacaoSelecionada != null) {
            ParecerProcIsencaoIPTU parecer = parecerFacade.buscarParecerDaSolicitacao(solicitacaoSelecionada.getId());
            if ((isOperacaoEditar() && parecer != null && !parecer.getId().equals(selecionado.getId())) || (isOperacaoNovo() && parecer != null)) {
                FacesUtil.addOperacaoNaoRealizada("A Solicitação de Isenção de IPTU selecionada já possui Parecer " +
                    "aguardando Deferimento/Indeferimento.");
                solicitacaoSelecionada = isOperacaoEditar() ? selecionado.getSolicitacaoIsencao() : null;
            }
        }
    }

    public boolean canEmitirTermo() {
        if (isencao == null) {
            return false;
        }
        if (!IsencaoCadastroImobiliario.Situacao.ATIVO.equals(isencao.getSituacao()) &&
            !ProcessoIsencaoIPTU.Situacao.DEFERIDO.equals(selecionado.getSolicitacaoIsencao().getSituacao())) {
            return false;
        }
        if(!isPreenchidoTipoDoctoNaCategoria()) {
            return false;
        }
        return !isRecalculado();
    }

    public boolean isRecalculado() {
        return parecerFacade.isRecalculado(selecionado.getId());
    }

    public boolean isPreenchidoTipoDoctoNaCategoria() {
        return selecionado != null && selecionado.getSolicitacaoIsencao() != null && selecionado.
            getSolicitacaoIsencao().getCategoriaIsencaoIPTU() != null && selecionado.getSolicitacaoIsencao().getCategoriaIsencaoIPTU().
            getTipoDoctoOficial() != null;
    }

    public void imprimirDocumentoOficial() {
        if (selecionado.getSolicitacaoIsencao().getCategoriaIsencaoIPTU().getTipoDoctoOficial() != null && isencao != null) {
            try {
                parecerFacade.imprimirDoctoOficial(isencao);
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada("Erro ao imprimir o Documento Oficial. Detalhes: " + e.getMessage());
                logger.error("Erro ao imprimir o Documento Oficial. ", e);
            }
        } else {
            FacesUtil.addError("Operação não realizada!", "A Categoria de Isenção não possui um Tipo de Documento Oficial informado!");
        }
    }
}
