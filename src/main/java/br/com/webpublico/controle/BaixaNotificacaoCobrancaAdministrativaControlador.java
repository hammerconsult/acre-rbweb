package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoSituacaoProgramacaoCobranca;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BaixaNotificacaoCobrancaAdministrativaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 04/12/13
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "baixaNotificacaoCobrancaAdministrativaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaBaixa", pattern = "/baixa-notificacao-administrativa/novo/", viewId = "/faces/tributario/contacorrente/baixanotificacaocobrancaadministrativa/edita.xhtml"),
    @URLMapping(id = "verBaixaNotificacao", pattern = "/baixa-notificacao-administrativa/ver/#{baixaNotificacaoCobrancaAdministrativaControlador.id}/", viewId = "/faces/tributario/contacorrente/baixanotificacaocobrancaadministrativa/visualizar.xhtml"),
    @URLMapping(id = "listarBaixaNotificacao", pattern = "/baixa-notificacao-administrativa/listar/", viewId = "/faces/tributario/contacorrente/baixanotificacaocobrancaadministrativa/lista.xhtml")

})
public class BaixaNotificacaoCobrancaAdministrativaControlador extends PrettyControlador<BaixaNotificacaoCobrancaAdministrativa> implements Serializable, CRUD {

    @EJB
    private BaixaNotificacaoCobrancaAdministrativaFacade baixaNotificacaoCobrancaAdmFacade;
    private NotificacaoCobrancaAdmin notificacaoCobrancaAdmin;
    private ItemNotificacao itemNotificacao;
    private ConverterAutoComplete converterItemNotificacao;
    private List<ItemProgramacaoCobranca> listaItemProgramacao;
    private List<ItemNotificacao> listaItemNotificacao;
    private List<ItemNotificacao> listaAceites;
    private SistemaControlador sistemaControlador;
    private Date dataAceite;
    private String observacoes;
    private List<ResultadoParcela> resultadoParcelas;
    private AssistenteBarraProgresso assistente;
    private Future<BaixaNotificacaoCobrancaAdministrativa> futureBaixa;

    public BaixaNotificacaoCobrancaAdministrativaControlador() {
        super(BaixaNotificacaoCobrancaAdministrativa.class);
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    public AbstractFacade getFacede() {
        return baixaNotificacaoCobrancaAdmFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/baixa-notificacao-administrativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaBaixa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().isEmpty()) {
            pegarNotificacaoAdmUrl();
        }
        super.novo();
        inicializar();
    }

    public void pegarNotificacaoAdmUrl() {
        NotificacaoCobrancaAdmin notificacao = baixaNotificacaoCobrancaAdmFacade.recuperarNotificaPeloId(
            Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("notificacaoCobrancaAdmin")));

        if (notificacao != null) {
            this.notificacaoCobrancaAdmin = (NotificacaoCobrancaAdmin) Util.clonarObjeto(notificacao);
        }
    }

    @URLAction(mappingId = "verBaixaNotificacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializar();
    }

    private void recuperarCobrancaAdm() {
        if (notificacaoCobrancaAdmin != null) {
            notificacaoCobrancaAdmin = selecionado.getProgramacaoCobranca().getNotificacaoCobrancaAdmin();
        }
    }

    public void inicializar() {
        selecionado.setDataOperacao(baixaNotificacaoCobrancaAdmFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(baixaNotificacaoCobrancaAdmFacade.getSistemaFacade().getUsuarioCorrente());
        resultadoParcelas = Lists.newArrayList();
        listaItemProgramacao = Lists.newArrayList();
        listaItemNotificacao = Lists.newArrayList();
        listaAceites = Lists.newArrayList();
        itemNotificacao = new ItemNotificacao();
    }

    private void atribuirResultadoParcelaItemNotificacao() {
        resultadoParcelas = recuperaResultaParcelaItemNotificacao(selecionado.getItensNotificacao().get(0).getItemNotificacao());
    }

    public List<NotificacaoCobrancaAdmin> completaNotificacao(String parte) {
        return baixaNotificacaoCobrancaAdmFacade.getNotificacaoCobrancaAdministrativaFacade().listaNotificacaoParaAceite(parte);
    }

    public List<ItemNotificacao> completaItemNotificacao(String parte) {
        return baixaNotificacaoCobrancaAdmFacade.getNotificacaoCobrancaAdministrativaFacade().recuperaItemNotificacaoSemAceite(notificacaoCobrancaAdmin);
    }

    public ItemNotificacao getItemNotificacao() {
        return itemNotificacao;
    }

    public void setItemNotificacao(ItemNotificacao itemNotificacao) {
        this.itemNotificacao = itemNotificacao;
    }

    public List<ItemProgramacaoCobranca> getListaItemProgramacao() {
        return listaItemProgramacao;
    }

    public void setListaItemProgramacao(List<ItemProgramacaoCobranca> listaItemProgramacao) {
        this.listaItemProgramacao = listaItemProgramacao;
    }

    public List<ItemNotificacao> getListaItemNotificacao() {
        return listaItemNotificacao;
    }

    public void setListaItemNotificacao(List<ItemNotificacao> listaItemNotificacao) {
        this.listaItemNotificacao = listaItemNotificacao;
    }

    public List<ItemNotificacao> getListaAceites() {
        return listaAceites;
    }

    public void setListaAceites(List<ItemNotificacao> listaAceites) {
        this.listaAceites = listaAceites;
    }

    public NotificacaoCobrancaAdmin getNotificacaoCobrancaAdmin() {
        return notificacaoCobrancaAdmin;
    }

    public void setNotificacaoCobrancaAdmin(NotificacaoCobrancaAdmin notificacaoCobrancaAdmin) {
        this.notificacaoCobrancaAdmin = notificacaoCobrancaAdmin;
    }

    public Date getDataAceite() {
        return dataAceite;
    }

    public void setDataAceite(Date dataAceite) {
        this.dataAceite = dataAceite;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public ConverterAutoComplete getConverterItemNotificacao() {
        if (converterItemNotificacao == null) {
            converterItemNotificacao = new ConverterAutoComplete(ItemNotificacao.class, baixaNotificacaoCobrancaAdmFacade);
        }
        return converterItemNotificacao;
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            validarItensSalvar();
            selecionado.setBaixado(Boolean.FALSE);
            selecionado.setProgramacaoCobranca(notificacaoCobrancaAdmin.getProgramacaoCobranca());
            selecionado = baixaNotificacaoCobrancaAdmFacade.salvarSelecionado(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (notificacaoCobrancaAdmin == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma programação de Cobrança!");
        }
        ve.lancarException();
    }

    public void baixar() {
        try {
            validarBaixa();
            Aceite aceite = criarAceite();
            assistente = new AssistenteBarraProgresso();
            assistente.setExecutando(true);
            assistente.setUsuarioSistema(baixaNotificacaoCobrancaAdmFacade.getSistemaFacade().getUsuarioCorrente());
            assistente.setDataAtual(baixaNotificacaoCobrancaAdmFacade.getSistemaFacade().getDataOperacao());
            assistente.setSelecionado(selecionado);
            futureBaixa = baixaNotificacaoCobrancaAdmFacade.baixar(aceite, assistente);
            FacesUtil.executaJavaScript("openDialog(dlgBaixar)");
            FacesUtil.executaJavaScript("acompanharBaixar()");

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private Aceite criarAceite() {
        Aceite aceite = new Aceite();
        aceite.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        aceite.setDataOperacao(new Date());
        if (!Strings.isNullOrEmpty(observacoes)) {
            aceite.setObservacoes(observacoes);
        }
        return aceite;
    }

    private void validarBaixa() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(observacoes)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Observação deve ser informado.");
        }
        ve.lancarException();
    }

    public List<ResultadoParcela> recuperaResultaParcelaItemNotificacao(ItemNotificacao item) {
        if (item != null && item.getId() != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            List<ParcelaValorDivida> parcelas = parcelas(baixaNotificacaoCobrancaAdmFacade.getNotificacaoCobrancaAdministrativaFacade().buscarItemProgramacaoDoItemNotificacao(item));
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, getIdsParcela(parcelas));
            consultaParcela.executaConsulta();
            return consultaParcela.getResultados();
        }
        return new ArrayList<>();
    }

    public List<Long> getIdsParcela(List<ParcelaValorDivida> parcela) {
        List<Long> ids = new ArrayList<>();
        for (ParcelaValorDivida pvd : parcela) {
            ids.add(pvd.getId());
        }
        return ids;
    }

    public List<ParcelaValorDivida> parcelas(List<ItemProgramacaoCobranca> list) {
        List<ParcelaValorDivida> retorno = new ArrayList<>();
        for (ItemProgramacaoCobranca item : list) {
            retorno.add(item.getParcelaValorDivida());
        }
        return retorno;
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (obj instanceof NotificacaoCobrancaAdmin) {
            notificacaoCobrancaAdmin = (NotificacaoCobrancaAdmin) obj;
        }
        if (obj instanceof ItemNotificacao) {
            itemNotificacao = (ItemNotificacao) obj;
        }
    }

    public void colocarObjetoNaSessao() {
        Web.poeNaSessao(notificacaoCobrancaAdmin);
    }

    public void adicionarItensNotificacao() {
        try {
            selecionado.setItensNotificacao(Lists.<BaixaCobrancaAdministrativaItemNotificacao>newArrayList());
            for (ItemNotificacao item : notificacaoCobrancaAdmin.getItemNotificacaoLista()) {
                BaixaCobrancaAdministrativaItemNotificacao baixa = new BaixaCobrancaAdministrativaItemNotificacao();
                baixa.setBaixaNotificacao(selecionado);
                baixa.setItemNotificacao(item);
                Util.adicionarObjetoEmLista(selecionado.getItensNotificacao(), baixa);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void adicionarItemNotificacao() {
        try {
            validarItensNotificacao();
            BaixaCobrancaAdministrativaItemNotificacao baixa = new BaixaCobrancaAdministrativaItemNotificacao();
            baixa.setItemNotificacao(itemNotificacao);
            baixa.setBaixaNotificacao(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getItensNotificacao(), baixa);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarItensNotificacao() {
        ValidacaoException ve = new ValidacaoException();
        for (BaixaCobrancaAdministrativaItemNotificacao item : selecionado.getItensNotificacao()) {
            if (item.getItemNotificacao().equals(itemNotificacao)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Item: " + itemNotificacao + " já está na lista de Aviso/Notificação de Cobrança.");
            }
        }
        ve.lancarException();
    }

    private void validarItensSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItensNotificacao() == null || selecionado.getItensNotificacao().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um Item para continuar a operação.");
        }
        ve.lancarException();
    }

    public void limparItens() {
        selecionado.setItensNotificacao(Lists.<BaixaCobrancaAdministrativaItemNotificacao>newArrayList());
        setItemNotificacao(null);
    }

    public void removerItemNotificao(BaixaCobrancaAdministrativaItemNotificacao item) {
        selecionado.getItensNotificacao().remove(item);
    }

    public List<ResultadoParcela> getResultadoParcelas() {
        return resultadoParcelas;
    }

    public void setResultadoParcelas(List<ResultadoParcela> resultadoParcelas) {
        this.resultadoParcelas = resultadoParcelas;
    }

    public void consultarFutureBaixar() {
        if (futureBaixa != null && futureBaixa.isDone()) {
            try {
                FacesUtil.executaJavaScript("finalizarBaixar()");
            } catch (Exception ex) {
                descobrirETratarException(ex);
            }
        }
    }

    public void finalizarFutureBaixar() {
        futureBaixa = null;
        FacesUtil.executaJavaScript("closeDialog(dlgBaixar)");
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        FacesUtil.redirecionamentoInterno("/baixa-notificacao-administrativa/listar/");
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public Future getFutureBaixa() {
        return futureBaixa;
    }

    public void setFutureBaixa(Future futureBaixa) {
        this.futureBaixa = futureBaixa;
    }
}
