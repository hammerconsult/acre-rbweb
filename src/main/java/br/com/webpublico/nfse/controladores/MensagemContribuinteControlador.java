package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.ListaPaginada;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.MensagemContribuinte;
import br.com.webpublico.nfse.domain.MensagemContribuinteDocumento;
import br.com.webpublico.nfse.domain.MensagemContribuinteUsuario;
import br.com.webpublico.nfse.domain.dtos.FiltroMensagemContribuinte;
import br.com.webpublico.nfse.enums.TipoMensagemContribuinte;
import br.com.webpublico.nfse.facades.MensagemContribuinteFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "mensagemContribuinteNovo",
        pattern = "/nfse/mensagem-contribuinte/novo/",
        viewId = "/faces/tributario/nfse/mensagem-contribuinte/edita.xhtml"),
    @URLMapping(id = "mensagemContribuinteListar",
        pattern = "/nfse/mensagem-contribuinte/listar/",
        viewId = "/faces/tributario/nfse/mensagem-contribuinte/lista.xhtml"),
    @URLMapping(id = "mensagemContribuinteVer",
        pattern = "/nfse/mensagem-contribuinte/ver/#{mensagemContribuinteControlador.id}/",
        viewId = "/faces/tributario/nfse/mensagem-contribuinte/visualizar.xhtml"),
})
public class MensagemContribuinteControlador extends PrettyControlador<MensagemContribuinte> implements CRUD {

    @EJB
    private MensagemContribuinteFacade facade;
    private LazyDataModel<MensagemContribuinteUsuario> usuarios;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    CompletableFuture<MensagemContribuinte> completableFuture;
    private MensagemContribuinteDocumento documento;
    private FiltroMensagemContribuinte filtroMensagemContribuinte;
    private ListaPaginada<CadastroEconomico> cadastros;
    private Integer countCadastrosEconomico;

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/mensagem-contribuinte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public MensagemContribuinteControlador() {
        super(MensagemContribuinte.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public LazyDataModel<MensagemContribuinteUsuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(LazyDataModel<MensagemContribuinteUsuario> usuarios) {
        this.usuarios = usuarios;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public MensagemContribuinteDocumento getDocumento() {
        return documento;
    }

    public void setDocumento(MensagemContribuinteDocumento documento) {
        this.documento = documento;
    }

    public FiltroMensagemContribuinte getFiltroMensagemContribuinte() {
        return filtroMensagemContribuinte;
    }

    public void setFiltroMensagemContribuinte(FiltroMensagemContribuinte filtroMensagemContribuinte) {
        this.filtroMensagemContribuinte = filtroMensagemContribuinte;
    }

    public ListaPaginada<CadastroEconomico> getCadastros() {
        return cadastros;
    }

    public void setCadastros(ListaPaginada<CadastroEconomico> cadastros) {
        this.cadastros = cadastros;
    }

    public Integer getCountCadastrosEconomico() {
        return countCadastrosEconomico;
    }

    public void setCountCadastrosEconomico(Integer countCadastrosEconomico) {
        this.countCadastrosEconomico = countCadastrosEconomico;
    }

    public void criarDataModelUsuarios() {
        usuarios = new LazyDataModel<MensagemContribuinteUsuario>() {
            @Override
            public List<MensagemContribuinteUsuario> load(int first, int max, String s, SortOrder sortOrder, Map<String, String> map) {
                setRowCount(facade.contarMensagemContribuinteUsuarios(selecionado));
                return facade.buscarMensagemContribuinteUsuarios(selecionado, first, max);
            }
        };
    }

    @Override
    @URLAction(mappingId = "mensagemContribuinteNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setEmitidaEm(new Date());
        selecionado.setEnviadaPor(facade.getSistemaFacade().getUsuarioCorrente());
        documento = new MensagemContribuinteDocumento();
        filtroMensagemContribuinte = new FiltroMensagemContribuinte();
        criarListaPaginadaCadastros();
    }

    @Override
    @URLAction(mappingId = "mensagemContribuinteVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        criarDataModelUsuarios();
    }

    private void criarListaPaginadaCadastros() {
        cadastros = new ListaPaginada<CadastroEconomico>() {
            @Override
            public List<CadastroEconomico> buscarDados(int firstResult, int maxResult) {
                return facade.buscarCadastrosEconomico(filtroMensagemContribuinte, firstResult, maxResult);
            }

            @Override
            protected Integer contarRegistros() {
                return facade.contarCadastrosEconomico(filtroMensagemContribuinte);
            }
        };
        cadastros.pesquisar();
    }

    private void validarDocumento() {
        if (Strings.isNullOrEmpty(documento.getDescricaoDocumento())) {
            throw new ValidacaoException("A Descrição do Documento deve ser informada.");
        }
    }

    public void adicionarDocumento() {
        try {
            validarDocumento();
            documento.setMensagemContribuinte(selecionado);
            if (selecionado.getDocumentos() == null) {
                selecionado.setDocumentos(new ArrayList<>());
            }
            selecionado.getDocumentos().add(documento);
            documento = new MensagemContribuinteDocumento();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        }
    }

    public void removerDocumento(MensagemContribuinteDocumento documento) {
        selecionado.getDocumentos().remove(documento);
    }

    public void preEnviarMensagens() {
        try {
            validar();
            countCadastrosEconomico = selecionado.getEnviarTodosUsuarios() ?
                facade.contarCadastrosEconomico(filtroMensagemContribuinte) : cadastros.getRegistrosSelecionados().size();
            if (countCadastrosEconomico > 0) {
                if (countCadastrosEconomico == 1) {
                    enviarMensagens();
                } else {
                    FacesUtil.executaJavaScript("dialogConfirmacao.show()");
                }
            } else {
                FacesUtil.addWarn("Atenção!", "Nenhum contribuinte encontrado com os filtros selecionados.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validar() {
        selecionado.realizarValidacoes();
        if (!selecionado.getEnviarTodosUsuarios() && cadastros.getRegistrosSelecionados().isEmpty()) {
            throw new ValidacaoException("Por favor selecione ao menos 1 contribuinte para receber a mensagem.");
        }
    }

    public void enviarMensagens() {
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.setUsuarioSistema(facade.getUsuarioCorrente());
        completableFuture = AsyncExecutor.getInstance()
            .execute(assistenteBarraProgresso,
                () -> facade.enviarMensagens(assistenteBarraProgresso, selecionado,
                    filtroMensagemContribuinte, cadastros.getRegistrosSelecionados()));
        FacesUtil.executaJavaScript("pollEnvio.start()");
        FacesUtil.executaJavaScript("openDialog(dialogAcompanhamento)");
    }

    public void acompanharEnvio() {
        if (completableFuture.isDone()) {
            FacesUtil.executaJavaScript("pollEnvio.stop()");
            finalizarEnvio();
        }
        if (completableFuture.isCancelled()) {
            FacesUtil.executaJavaScript("pollEnvio.stop()");
            FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
        }
    }

    private void finalizarEnvio() {
        try {
            selecionado = completableFuture.get();
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            FacesUtil.addOperacaoRealizada("Mensagens enviadas com sucesso!");
        } catch (Exception e) {
            FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
            FacesUtil.addErrorPadrao(e);
        }
    }

    public List<SelectItem> getTiposMensagem() {
        return Util.getListSelectItem(TipoMensagemContribuinte.values());
    }

    public void tratarTipoMensagem() {
        selecionado.setDocumentos(Lists.newArrayList());
    }

    public List<SelectItem> getTiposIssqn() {
        return Util.getListSelectItem(TipoIssqn.values());
    }

    public void pesquisarCadastros() {
        cadastros.pesquisar();
    }

    public void limparCampos() {
        filtroMensagemContribuinte = new FiltroMensagemContribuinte();
        cadastros.pesquisar();
    }

    public List<UsuarioSistema> completarEnviadaPor(String parte) {
        return facade.getUsuarioSistemaFacade().buscarUsuarioSistemaEnviouMensagemContribuinte(parte);
    }

    public void mudouEnviarTodosUsuarios() {
        filtroMensagemContribuinte = new FiltroMensagemContribuinte();
    }

}
