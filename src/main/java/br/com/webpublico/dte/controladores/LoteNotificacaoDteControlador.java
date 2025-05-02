package br.com.webpublico.dte.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.dte.entidades.*;
import br.com.webpublico.dte.facades.LoteNotificacaoDteFacade;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarEmissaoNotificacaoDte", pattern = "/dte/emissao-notificacao/listar/",
        viewId = "/faces/tributario/dte/emissaonotificacao/lista.xhtml"),
    @URLMapping(id = "verEmissaoNotificacaoDte", pattern = "/dte/emissao-notificacao/ver/#{loteNotificacaoDteControlador.id}/",
        viewId = "/faces/tributario/dte/emissaonotificacao/visualizar.xhtml"),
    @URLMapping(id = "novaEmissaoNotificacaoDte", pattern = "/dte/emissao-notificacao/novo/",
        viewId = "/faces/tributario/dte/emissaonotificacao/edita.xhtml"),
    @URLMapping(id = "editarEmissaoNotificacaoDte", pattern = "/dte/emissao-notificacao/editar/#{loteNotificacaoDteControlador.id}/",
        viewId = "/faces/tributario/dte/emissaonotificacao/edita.xhtml"),
    @URLMapping(id = "acompanharEmissaoNotificacaoDte", pattern = "/dte/emissao-notificacao/acompanhar/#{loteNotificacaoDteControlador.id}/",
        viewId = "/faces/tributario/dte/emissaonotificacao/acompanha.xhtml")
})
public class LoteNotificacaoDteControlador extends PrettyControlador<LoteNotificacaoDte> implements CRUD {

    @EJB
    private LoteNotificacaoDteFacade facade;
    private LoteNotificacaoDocDte loteNotificacaoDocDte;
    private GrupoContribuinteDte grupoContribuinte;
    private CadastroEconomico cadastroEconomico;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future future;
    private Boolean terminouEnvio = Boolean.FALSE;

    public LoteNotificacaoDteControlador() {
        super(LoteNotificacaoDte.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/dte/emissao-notificacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaEmissaoNotificacaoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setRegistradoEm(facade.getSistemaFacade().getDataOperacao());
        selecionado.setRegistradoPor(facade.getSistemaFacade().getUsuarioCorrente());


    }

    @URLAction(mappingId = "editarEmissaoNotificacaoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verEmissaoNotificacaoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "acompanharEmissaoNotificacaoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void acompanhar() {
        super.ver();
        terminouEnvio = Boolean.FALSE;
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        future = facade.enviarNotificacoes(selecionado, assistenteBarraProgresso);
    }

    public void adicionarCabecalho() {
        String caminhoDaImagem = geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";
        String conteudo =
            "<table style=\"width: 100%;\">"
                + "<tbody>"
                + "<tr>"
                + "<td style=\"text-align: left;\" align=\"right\"><img src=\"../../../../" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /></td>"
                + "<td><span style=\"font-size: small;\">PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO</span><br /><span style=\"font-size: small;\">Secretaria Municipal de Finan&ccedil;as</span> <strong><br /></strong></td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<br>&nbsp;<br><br>"
                + "<br><br><br> *** INSIRA SEU TEXTO AQUI *** ";
        RequestContext.getCurrentInstance().execute("insertAtCursor('" + conteudo + "')");
    }

    public void adicionarRodape() {
        String conteudo =
            "<div id=\"footer\">"
                + "<table style=\"width: 100%;\">"
                + "<tbody>"
                + "<tr>"
                + "<td>*** INSIRA SEU TEXTO AQUI ***</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<div>";
        RequestContext.getCurrentInstance().execute("insertAtCursor('" + conteudo + "')");
    }

    public String geraUrlImagemDir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        final StringBuffer url = request.getRequestURL();
        String test = url.toString();
        String[] quebrado = test.split("/");
        StringBuilder b = new StringBuilder();
        b.append("/").append(quebrado[3]).append("/");
        return b.toString();
    }

    public void adicionarTagDtaHoje(String tag) {
        ModeloDocumentoDte.TagsDataHoje tagsDataHoje = ModeloDocumentoDte.TagsDataHoje.valueOf(tag);
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + tagsDataHoje.name() + "')");
    }

    public List<SelectItem> getTags() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (ModeloDocumentoDte.TagsCadastroEconomico value : ModeloDocumentoDte.TagsCadastroEconomico.values()) {
            toReturn.add(new SelectItem(value, value.getDescricao()));
        }
        return toReturn;
    }

    public LoteNotificacaoDocDte getLoteNotificacaoDocDte() {
        return loteNotificacaoDocDte;
    }

    public void setLoteNotificacaoDocDte(LoteNotificacaoDocDte loteNotificacaoDocDte) {
        this.loteNotificacaoDocDte = loteNotificacaoDocDte;
    }

    public GrupoContribuinteDte getGrupoContribuinte() {
        return grupoContribuinte;
    }

    public void setGrupoContribuinte(GrupoContribuinteDte grupoContribuinte) {
        this.grupoContribuinte = grupoContribuinte;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public void inserirModeloDocumento() {
        loteNotificacaoDocDte = new LoteNotificacaoDocDte();
    }

    public void adicionarModeloDocumento() {
        try {
            validarModeloDocumento();
            loteNotificacaoDocDte.setLote(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDocumentos(), loteNotificacaoDocDte);
            loteNotificacaoDocDte = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void validarModeloDocumento() {
        ValidacaoException ve = new ValidacaoException();
        if (loteNotificacaoDocDte.getModeloDocumento() == null)
            ve.adicionarMensagemDeCampoObrigatorio("O Modelo de Documento deve ser informado.");
        if (selecionado.hasModelo(loteNotificacaoDocDte.getModeloDocumento()))
            ve.adicionarMensagemDeCampoObrigatorio("O Modelo de Documento já foi adicionado.");
        if (loteNotificacaoDocDte.getConteudo() == null || loteNotificacaoDocDte.getConteudo().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Conteúdo deve ser informado.");
        }
    }

    public void removerModeloDocumento(LoteNotificacaoDocDte loteNotificacaoDocDte) {
        selecionado.getDocumentos().remove(loteNotificacaoDocDte);
    }

    public void editarModeloDocumento(LoteNotificacaoDocDte loteNotificacaoDocDte) {
        removerModeloDocumento(loteNotificacaoDocDte);
        this.loteNotificacaoDocDte = loteNotificacaoDocDte;
    }

    public void adicionarGrupoContribuinte() {
        try {
            validarGrupoContribuinte();
            LoteNotificacaoGrupoDte loteNotificacaoGrupoDte = new LoteNotificacaoGrupoDte();
            loteNotificacaoGrupoDte.setLote(selecionado);
            loteNotificacaoGrupoDte.setGrupo(grupoContribuinte);
            selecionado.getGrupos().add(loteNotificacaoGrupoDte);
            grupoContribuinte = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void validarGrupoContribuinte() {
        ValidacaoException ve = new ValidacaoException();
        if (grupoContribuinte == null)
            ve.adicionarMensagemDeCampoObrigatorio("O Grupo de Contribuinte deve ser informado.");
        if (selecionado.hasGrupo(grupoContribuinte))
            ve.adicionarMensagemDeCampoObrigatorio("O Grupo de Contribuinte já foi adicionado.");
    }

    public void removerGrupoContribuinte(LoteNotificacaoGrupoDte loteNotificacaoGrupoDte) {
        selecionado.getGrupos().remove(loteNotificacaoGrupoDte);
    }

    public void adicionarCadastroEconomico() {
        try {
            validarCadastroEconomico();
            LoteNotificacaoCmcDte loteNotificacaoCmcDte = new LoteNotificacaoCmcDte();
            loteNotificacaoCmcDte.setLote(selecionado);
            loteNotificacaoCmcDte.setCadastroEconomico(cadastroEconomico);
            selecionado.getCadastros().add(loteNotificacaoCmcDte);
            cadastroEconomico = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void validarCadastroEconomico() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomico == null)
            ve.adicionarMensagemDeCampoObrigatorio("O Cadastro Econômico deve ser informado.");
        if (selecionado.hasCadastro(cadastroEconomico))
            ve.adicionarMensagemDeCampoObrigatorio("O Cadastro Econômico já foi adicionado.");
    }

    public void removerCadastroEconomico(LoteNotificacaoCmcDte loteNotificacaoCmcDte) {
        selecionado.getCadastros().remove(loteNotificacaoCmcDte);
    }

    public void validarDados() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        if (selecionado.getCienciaAutomaticaEm() == null || selecionado.getCienciaAutomaticaEm() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ciência Automática Em deve ser informado com um valor superior a 0.");
        }
    }


    public void salvar() {
        try {
            validarDados();
            selecionado = facade.salvarRetornando(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(),
                getMensagemSucessoAoSalvar()));
            redireciona(getCaminhoPadrao() + "ver/" + getUrlKeyValue());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void concluir() {
        try {
            validarDados();
            validarDadosConcluir();
            selecionado = facade.salvarRetornando(selecionado);
            redireciona(getCaminhoPadrao() + "acompanhar/" + getUrlKeyValue());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarDadosConcluir() {
        ValidacaoException ve = new ValidacaoException();
        if ((selecionado.getGrupos() == null || selecionado.getGrupos().isEmpty()) &&
            (selecionado.getCadastros() == null || selecionado.getCadastros().isEmpty())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum contribuinte foi adicionado nessa notificação, por favor adicione os contribuintes.");
        }
        ve.lancarException();
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public void acompanharEnvio() {
        logger.debug("acompanharEnvio");
        if (future != null && (future.isDone() || future.isCancelled())) {
            logger.debug("finalizar");
            FacesUtil.executaJavaScript("finalizarEnvio()");
        }
    }

    public void finalizarEnvio() {
        terminouEnvio = Boolean.TRUE;
        FacesUtil.addOperacaoRealizada("As notificações foram enviadas com sucesso!");
    }

    public Boolean getTerminouEnvio() {
        return terminouEnvio;
    }

    public void setTerminouEnvio(Boolean terminouEnvio) {
        this.terminouEnvio = terminouEnvio;
    }

    public void handleModeloDocumento() {
        loteNotificacaoDocDte.setConteudo(null);
        if (loteNotificacaoDocDte.getModeloDocumento() != null) {
            loteNotificacaoDocDte.setConteudo(loteNotificacaoDocDte.getModeloDocumento().getConteudo());
        }
    }
}
