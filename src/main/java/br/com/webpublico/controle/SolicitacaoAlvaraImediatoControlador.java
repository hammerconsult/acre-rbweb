package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ParametroRegularizacao;
import br.com.webpublico.enums.SituacaoAlvaraImediato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.SolicitacaoAlvaraImediatoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "verSolicitacaoAlvaraImediato",
        pattern = "/tributario/alvara-imediato/solicitacao/ver/#{solicitacaoAlvaraImediatoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/solicitacao/visualiza.xhtml"),
    @URLMapping(
        id = "listarSolicitacaoAlvaraImediato",
        pattern = "/tributario/alvara-imediato/solicitacao/listar/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/solicitacao/lista.xhtml")
})
public class SolicitacaoAlvaraImediatoControlador extends PrettyControlador<SolicitacaoAlvaraImediato> implements CRUD {

    @EJB
    private SolicitacaoAlvaraImediatoFacade facade;
    private Future<AlvaraConstrucao> futureAlvaraConstrucao;
    private Future<List<FacesMessage>> futureCalculoAlvaraConstrucao;
    private ExigenciaAlvaraImediato exigenciaAlvaraImediato;
    private ExigenciaAlvaraImediatoFormularioCampo exigenciaAlvaraImediatoFormularioCampo;
    private List<SelectItem> siFormularioCampo;
    private UsuarioSistema usuarioSistema;
    private Boolean isDiretorSecretario;

    public SolicitacaoAlvaraImediatoControlador() {
        super(SolicitacaoAlvaraImediato.class);
    }

    @Override
    public SolicitacaoAlvaraImediatoFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/alvara-imediato/solicitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ExigenciaAlvaraImediato getExigenciaAlvaraImediato() {
        return exigenciaAlvaraImediato;
    }

    public void setExigenciaAlvaraImediato(ExigenciaAlvaraImediato exigenciaAlvaraImediato) {
        this.exigenciaAlvaraImediato = exigenciaAlvaraImediato;
    }

    public ExigenciaAlvaraImediatoFormularioCampo getExigenciaAlvaraImediatoFormularioCampo() {
        return exigenciaAlvaraImediatoFormularioCampo;
    }

    public void setExigenciaAlvaraImediatoFormularioCampo(ExigenciaAlvaraImediatoFormularioCampo exigenciaAlvaraImediatoFormularioCampo) {
        this.exigenciaAlvaraImediatoFormularioCampo = exigenciaAlvaraImediatoFormularioCampo;
    }

    public List<SelectItem> getSiFormularioCampo() {
        return siFormularioCampo;
    }

    public void setSiFormularioCampo(List<SelectItem> siFormularioCampo) {
        this.siFormularioCampo = siFormularioCampo;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @URLAction(mappingId = "verSolicitacaoAlvaraImediato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        isDiretorSecretario = null;
        super.ver();
    }

    public void verImovel() {
        Web.navegacao(getCaminhoPadrao() + "ver/" + selecionado.getId(),
            "/cadastro-imobiliario/ver/" + selecionado.getCadastroImobiliario().getId() + "/");
    }

    public void alterarTabs(TabChangeEvent evt) {
        String tab = evt.getTab().getId();
        if ("tabMapa".equals(tab)) {
            verInscricaoMapa();
        }
    }

    public void verInscricaoMapa() {
        int setor = Integer.parseInt(selecionado.getCadastroImobiliario().getLote().getSetor().getCodigo());
        int quadra = selecionado.getCadastroImobiliario().getLote().getQuadra().getCodigoToInteger();
        int lote = Integer.parseInt(selecionado.getCadastroImobiliario().getLote().getCodigoLote());
        String inscricaoMapa = "1-" + setor + "-" + quadra + "-" + lote;
        FacesUtil.executaJavaScript("verInscricao('" + inscricaoMapa + "')");

    }

    public void iniciarAprovacao() {
        FacesUtil.executaJavaScript("dlgAprovacao.show()");
    }

    public void aprovarSolicitacao() {
        futureAlvaraConstrucao = facade.aprovarSolicitacaoAsync(selecionado, facade.getSistemaFacade().getUsuarioCorrente());
        FacesUtil.executaJavaScript("dlgAprovacao.hide()");
        FacesUtil.executaJavaScript("dlgEfetivacaoAprovacao.show()");
        FacesUtil.executaJavaScript("pollAlvaraConstrucao.start()");
    }

    public void consultarAlvaraConstrucao() {
        if (futureAlvaraConstrucao != null &&
            (futureAlvaraConstrucao.isDone() || futureAlvaraConstrucao.isCancelled())) {
            FacesUtil.executaJavaScript("pollAlvaraConstrucao.stop()");
            AlvaraConstrucao alvaraConstrucao = null;
            try {
                alvaraConstrucao = futureAlvaraConstrucao.get();
            } catch (Exception e) {
                logger.debug("Erro ao gerar alvara de construção na solicitação de alvará imediato.", e);
                logger.error("Erro ao gerar alvara de construção na solicitação de alvará imediato.");
                Web.navegacao(getCaminhoPadrao() + "listar/", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
                FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro inesperado ao tentar aprovar a solicitação de alvará " +
                    " imediato. Por favor contate o suporte.");
                return;
            }
            futureCalculoAlvaraConstrucao = facade.gerarCalculoAlvaraConstrucaoAsync(alvaraConstrucao);
            FacesUtil.executaJavaScript("pollCalculoAlvaraConstrucao.start()");
        }
    }

    public void consultarCalculoAlvaraConstrucao() {
        if (futureCalculoAlvaraConstrucao != null &&
            (futureCalculoAlvaraConstrucao.isDone() || futureCalculoAlvaraConstrucao.isCancelled())) {
            FacesUtil.executaJavaScript("pollCalculoAlvaraConstrucao.stop()");
            List<FacesMessage> facesMessages = null;
            try {
                facesMessages = futureCalculoAlvaraConstrucao.get();
            } catch (Exception e) {
                logger.debug("Erro ao gerar calculo de alvara de construção para solicitação de alvará imediato.", e);
                logger.error("Erro ao gerar calculo de alvara de construção para solicitação de alvará imediato.");
                Web.navegacao(getCaminhoPadrao() + "listar/", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
                FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro inesperado ao tentar aprovar a solicitação de alvará " +
                    " imediato. Por favor contate o suporte.");
                return;
            }
            if (facesMessages == null || facesMessages.isEmpty()) {
                Web.navegacao(getCaminhoPadrao() + "listar/", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
                FacesUtil.addOperacaoRealizada("Solicitação de alvará imediato aprovada com sucesso!");
            } else {
                Web.navegacao(getCaminhoPadrao() + "listar/", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
                FacesUtil.printAllFacesMessages(facesMessages);
            }
        }
    }

    public void verAlvaraConstrucao() {
        AlvaraConstrucao alvaraConstrucao = facade.recuperarAlvaraConstrucao(selecionado);
        if (alvaraConstrucao != null) {
            Web.navegacao(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/", "/alvara-construcao/ver/" + alvaraConstrucao.getId() + "/");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Alvará de Construção não localizado!");
        }
    }

    public void iniciarRejeicao() {
        selecionado.setAnoProtocolo(null);
        selecionado.setNumeroProtocolo(null);
        selecionado.setJustificativa(null);
    }

    public void validarRejeicao() {
        if (Strings.isNullOrEmpty(selecionado.getJustificativa())) {
            throw new ValidacaoException("O campo Justificativa deve ser informado.");
        }
    }

    public void rejeitar() {
        try {
            validarRejeicao();
            selecionado.setUsuarioEfetivacao(facade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setDataEfetivacao(new Date());
            selecionado.adicionarHistorico(new Date(), SituacaoAlvaraImediato.REJEITADO);
            facade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("Solicitação rejeitada com sucesso!");
            Web.navegacao(getCaminhoPadrao() + "listar/", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.debug("Erro ao rejeitar processo de solicitação de alvará imediato.", e);
            logger.error("Erro ao rejeitar processo de solicitação de alvará imediato.");
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void buscarSelectItemsFormularioCampo() {
        siFormularioCampo = Lists.newArrayList();
        siFormularioCampo.add(new SelectItem(null, ""));
        Formulario formulario = facade.getFormularioFacade().recuperar(exigenciaAlvaraImediato.getFormulario().getId());
        for (FormularioCampo formularioCampo : formulario.getFormularioCampoList()) {
            siFormularioCampo.add(new SelectItem(formularioCampo, formularioCampo.getTitulo()));
        }
    }

    public void iniciarExigencia() {
        exigenciaAlvaraImediato = new ExigenciaAlvaraImediato();
        exigenciaAlvaraImediato.setFormulario(selecionado.getRespostaFormulario().getFormulario());
        exigenciaAlvaraImediatoFormularioCampo = new ExigenciaAlvaraImediatoFormularioCampo();
        buscarSelectItemsFormularioCampo();
        FacesUtil.executaJavaScript("dlgExigencia.show()");
    }

    public void validarExigenciaFormularioCampo() {
        exigenciaAlvaraImediatoFormularioCampo.realizarValidacoes();
        if (exigenciaAlvaraImediato.hasExigenciaAlvaraImediatoFormularioCampo(exigenciaAlvaraImediatoFormularioCampo)) {
            throw new ValidacaoException("O campo já está adicionado.");
        }

    }

    public void adicionarExigenciaFormularioCampo() {
        try {
            validarExigenciaFormularioCampo();
            exigenciaAlvaraImediatoFormularioCampo.setExigencia(exigenciaAlvaraImediato);
            exigenciaAlvaraImediato.getCampos().add(exigenciaAlvaraImediatoFormularioCampo);
            exigenciaAlvaraImediatoFormularioCampo = new ExigenciaAlvaraImediatoFormularioCampo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void editarExigenciaFormularioCampo(ExigenciaAlvaraImediatoFormularioCampo exigenciaAlvaraImediatoFormularioCampo) {
        this.exigenciaAlvaraImediato.getCampos().remove(exigenciaAlvaraImediatoFormularioCampo);
        this.exigenciaAlvaraImediatoFormularioCampo = exigenciaAlvaraImediatoFormularioCampo;
    }

    public void removerExigenciaFormularioCampo(ExigenciaAlvaraImediatoFormularioCampo exigenciaAlvaraImediatoFormularioCampo) {
        exigenciaAlvaraImediato.getCampos().remove(exigenciaAlvaraImediatoFormularioCampo);
    }

    public void salvarExigencia() {
        try {
            exigenciaAlvaraImediato.realizarValidacoes();
            exigenciaAlvaraImediato.setSolicitacao(selecionado);
            exigenciaAlvaraImediato.setDataRegistro(new Date());
            exigenciaAlvaraImediato.setSequencial(selecionado.getUltimoSequencialExigencia() + 1);
            facade.registrarExigencia(exigenciaAlvaraImediato);
            FacesUtil.executaJavaScript("dlgExigencia.hide()");
            FacesUtil.addOperacaoRealizada("Exigência registrada com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void enviarEmailRegistroExigencia(ExigenciaAlvaraImediato exigenciaAlvaraImediato) {
        facade.enviarEmailRegistroExigencia(exigenciaAlvaraImediato);
        FacesUtil.addOperacaoRealizada("Exigência enviada com sucesso para:" +
            exigenciaAlvaraImediato.getSolicitacao().getEmail() + "!");
    }

    public StreamedContent baixarTodosAnexos() throws IOException {
        return facade.getArquivoFacade().ziparArquivos("Documentos em anexo",
            selecionado.getRespostaFormulario().getArquivosAnexados());
    }

    public Boolean getPodeRejeitar() {
        return SituacaoAlvaraImediato.DEFERIDO.equals(selecionado.getSituacao())
            && validarDiretorSecretario();
    }

    public Boolean getPodeAprovar() {
        return SituacaoAlvaraImediato.DEFERIDO.equals(selecionado.getSituacao())
            && validarDiretorSecretario();
    }

    public Boolean getPodeVerAlvaraConstrucao() {
        return SituacaoAlvaraImediato.APROVADO.equals(selecionado.getSituacao());
    }

    public Boolean getPodeAdicionarExigencia() {
        return !selecionado.getSituacao().equals(SituacaoAlvaraImediato.REJEITADO)
            && !selecionado.getSituacao().equals(SituacaoAlvaraImediato.EM_ESTUDO)
            && !selecionado.getSituacao().equals(SituacaoAlvaraImediato.APROVADO);
    }

    public Boolean getPodeDesignar() {
        return selecionado.getAnalistaResponsavel() == null;
    }

    public Boolean getPodeAlterarDesignacao() {
        return validarDiretorSecretario()
            && (SituacaoAlvaraImediato.DESIGNADO.equals(selecionado.getSituacao())
            || SituacaoAlvaraImediato.EM_EXIGENCIA.equals(selecionado.getSituacao())
            || SituacaoAlvaraImediato.RETORNO_EXIGENCIA.equals(selecionado.getSituacao()));
    }

    public Boolean getPodeDeferir() {
        return facade.getSistemaFacade().getUsuarioCorrente().equals(selecionado.getAnalistaResponsavel())
            && (SituacaoAlvaraImediato.DESIGNADO.equals(selecionado.getSituacao())
            || SituacaoAlvaraImediato.RETORNO_EXIGENCIA.equals(selecionado.getSituacao()));
    }

    public void designar() {
        try {
            UsuarioSistema usuarioCorrente = facade.getSistemaFacade().getUsuarioCorrente();
            facade.designar(selecionado, usuarioCorrente);
            FacesUtil.addOperacaoRealizada("Solicitação de alvará imediato designada com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private Boolean validarDiretorSecretario() {
        if (isDiretorSecretario != null) {
            return isDiretorSecretario;
        }
        ParametroRegularizacao parametroRegularizacao = facade.getParametroRegularizacaoFacade()
            .buscarParametroRegularizacaoPorExercicio(facade.getSistemaFacade().getExercicioCorrente());
        if (parametroRegularizacao != null) {
            UsuarioSistema usuarioCorrente = facade.getSistemaFacade().getUsuarioCorrente();
            if (!usuarioCorrente.equals(parametroRegularizacao.getDiretor())
                && !usuarioCorrente.equals(parametroRegularizacao.getSecretario())) {
                isDiretorSecretario = Boolean.FALSE;
                return isDiretorSecretario;
            }
        } else {
            isDiretorSecretario = Boolean.FALSE;
            return isDiretorSecretario;
        }
        isDiretorSecretario = Boolean.TRUE;
        return isDiretorSecretario;
    }

    public void iniciarAlteracaoDesignacao() {
        usuarioSistema = null;
    }

    public void alterarDesignacao() {
        try {
            if (usuarioSistema == null) {
                FacesUtil.addCampoObrigatorio("O Analista Responsável deve ser informado.");
                return;
            }
            facade.designar(selecionado, usuarioSistema);
            FacesUtil.addOperacaoRealizada("Solicitação de alvará imediato alterada com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void salvarObservacao() {
        try {
            facade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("Observação alterada com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void iniciarDeferimento() {
        selecionado.setNumeroProtocoloDeferimento("");
        selecionado.setAnoProtocoloDeferimento("");
    }

    public void deferir() {
        try {
            facade.deferir(selecionado);
            FacesUtil.addOperacaoRealizada("Solicitação de alvará imediato deferida com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }
}
