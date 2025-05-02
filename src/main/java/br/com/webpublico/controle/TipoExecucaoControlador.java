/*
 * Codigo gerado automaticamente em Thu Apr 05 14:04:23 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoExecucaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "tipoExecucaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-tipo-execucao", pattern = "/tipo-execucao/novo/", viewId = "/faces/financeiro/convenios/despesa/tipoexecucao/edita.xhtml"),
    @URLMapping(id = "editar-tipo-execucao", pattern = "/tipo-execucao/editar/#{tipoExecucaoControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/tipoexecucao/edita.xhtml"),
    @URLMapping(id = "ver-tipo-execucao", pattern = "/tipo-execucao/ver/#{tipoExecucaoControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/tipoexecucao/visualizar.xhtml"),
    @URLMapping(id = "listar-tipo-execucao", pattern = "/tipo-execucao/listar/", viewId = "/faces/financeiro/convenios/despesa/tipoexecucao/lista.xhtml")
})
public class TipoExecucaoControlador extends PrettyControlador<TipoExecucao> implements Serializable, CRUD {

    @EJB
    private TipoExecucaoFacade tipoExecucaoFacade;
    private TipoExecucaoTipoCertidao tipoExecucaoTipoCertidaoSelecionada;
    private ConverterAutoComplete converterClausula;
    private ConverterAutoComplete converterTipoCertidao;
    private TipoExecucaoClausulaBen tipoExecucaoClausulaBen;
    private RevisaoAuditoria ultimaRevisaoProvimento;

    public TipoExecucaoControlador() {
        super(TipoExecucao.class);
    }

    public TipoExecucaoFacade getFacade() {
        return tipoExecucaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoExecucaoFacade;
    }


    public void recuperaEditarVer() {
        selecionado = tipoExecucaoFacade.recuperar(super.getId());
        tipoExecucaoClausulaBen = new TipoExecucaoClausulaBen();
        tipoExecucaoTipoCertidaoSelecionada = new TipoExecucaoTipoCertidao();
    }

    public void adicionaClausula() {
        TipoExecucao te = ((TipoExecucao) selecionado);
        if (tipoExecucaoClausulaBen.getClausulaBenificiario() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Cláusula deve ser informado para adicionar.");
        } else if (te.getTipoExecucaoClausulaBens().contains(tipoExecucaoClausulaBen)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "A clausula " + tipoExecucaoClausulaBen.getClausulaBenificiario() + " ja existe na lista", null));
        } else {
            if (podeAdicionarClausula(tipoExecucaoClausulaBen)) {
                tipoExecucaoClausulaBen.setTipoExecucao(te);
                te.getTipoExecucaoClausulaBens().add(tipoExecucaoClausulaBen);
                tipoExecucaoClausulaBen = new TipoExecucaoClausulaBen();
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "A clausula " + tipoExecucaoClausulaBen.getClausulaBenificiario() + " ja existe na lista", null));
            }
        }
    }

    private boolean podeAdicionarClausula(TipoExecucaoClausulaBen tipoExecucaoClausulaBen) {
        for (TipoExecucaoClausulaBen execucaoClausulaBen : selecionado.getTipoExecucaoClausulaBens()) {
            if (execucaoClausulaBen.getClausulaBenificiario().equals(tipoExecucaoClausulaBen.getClausulaBenificiario())) {
                return false;
            }
        }
        return true;
    }

    public void adicionaCertidao() {
        TipoExecucao te = ((TipoExecucao) selecionado);
        if (tipoExecucaoTipoCertidaoSelecionada.getTipoDoctoHabilitacao() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Cetidão deve ser informado para adicionar.");
        } else if (te.getTipoExecucaoTipoCertidao().contains(tipoExecucaoTipoCertidaoSelecionada)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A certidão: " + tipoExecucaoTipoCertidaoSelecionada.getTipoDoctoHabilitacao() + " já foi adicionada na lista.");
        } else {
            if (podeadicionarCertidao(tipoExecucaoTipoCertidaoSelecionada)) {
                tipoExecucaoTipoCertidaoSelecionada.setTipoExecucao(te);
                te.getTipoExecucaoTipoCertidao().add(tipoExecucaoTipoCertidaoSelecionada);
                tipoExecucaoTipoCertidaoSelecionada = new TipoExecucaoTipoCertidao();
            } else {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A certidão: " + tipoExecucaoTipoCertidaoSelecionada.getTipoDoctoHabilitacao() + " já foi adicionada na lista.");
            }
        }
    }

    private boolean podeadicionarCertidao(TipoExecucaoTipoCertidao tipoExecucaoTipoCertidaoSelecionada) {
        for (TipoExecucaoTipoCertidao tipoExecucaoTipoCertidao : selecionado.getTipoExecucaoTipoCertidao()) {
            if (tipoExecucaoTipoCertidao.getTipoDoctoHabilitacao().equals(tipoExecucaoTipoCertidaoSelecionada.getTipoDoctoHabilitacao())) {
                return false;
            }
        }
        return true;
    }

    public void removeClausula(ActionEvent evento) {
        TipoExecucao te = ((TipoExecucao) selecionado);
        tipoExecucaoClausulaBen = (TipoExecucaoClausulaBen) evento.getComponent().getAttributes().get("obj");
        te.getTipoExecucaoClausulaBens().remove(tipoExecucaoClausulaBen);
    }

    public void removeCertidao(ActionEvent evento) {
        TipoExecucao te = ((TipoExecucao) selecionado);
        tipoExecucaoTipoCertidaoSelecionada = (TipoExecucaoTipoCertidao) evento.getComponent().getAttributes().get("obj");
        te.getTipoExecucaoTipoCertidao().remove(tipoExecucaoTipoCertidaoSelecionada);
    }

    public TipoExecucaoFacade getTipoExecucaoFacade() {
        return tipoExecucaoFacade;
    }

    public void setTipoExecucaoFacade(TipoExecucaoFacade tipoExecucaoFacade) {
        this.tipoExecucaoFacade = tipoExecucaoFacade;
    }

    public ConverterAutoComplete getConverterClausula() {
        if (converterClausula == null) {
            converterClausula = new ConverterAutoComplete(ClausulaBenificiario.class, tipoExecucaoFacade.getClausulaBenificiarioFacade());
        }
        return converterClausula;
    }

    public ConverterAutoComplete getConverterTipoCertidao() {
        if (converterTipoCertidao == null) {
            converterTipoCertidao = new ConverterAutoComplete(TipoDoctoHabilitacao.class, tipoExecucaoFacade.getTipoDoctoHabilitacaoFacade());
        }
        return converterTipoCertidao;
    }

    public List<ClausulaBenificiario> completaClausulas(String parte) {
        return tipoExecucaoFacade.getClausulaBenificiarioFacade().listaFiltrando(parte.trim(), "codigo", "conteudo", "observacao");
    }

    public List<TipoDoctoHabilitacao> completaCertidoes(String parte) {
        return tipoExecucaoFacade.getTipoDoctoHabilitacaoFacade().listaFiltrando(parte.trim());
    }

    public TipoExecucaoTipoCertidao getTipoExecucaoTipoCertidaoSelecionada() {
        return tipoExecucaoTipoCertidaoSelecionada;
    }

    public void setTipoExecucaoTipoCertidaoSelecionada(TipoExecucaoTipoCertidao tipoExecucaoTipoCertidaoSelecionada) {
        this.tipoExecucaoTipoCertidaoSelecionada = tipoExecucaoTipoCertidaoSelecionada;
    }

    public TipoExecucaoClausulaBen getTipoExecucaoClausulaBen() {
        return tipoExecucaoClausulaBen;
    }

    public void setTipoExecucaoClausulaBen(TipoExecucaoClausulaBen tipoExecucaoClausulaBen) {
        this.tipoExecucaoClausulaBen = tipoExecucaoClausulaBen;
    }


    @URLAction(mappingId = "novo-tipo-execucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        tipoExecucaoTipoCertidaoSelecionada = new TipoExecucaoTipoCertidao();
        tipoExecucaoClausulaBen = new TipoExecucaoClausulaBen();
        recuperarClausulaECerditaoSessoa();
    }

    private void recuperarClausulaECerditaoSessoa() {
        tipoExecucaoTipoCertidaoSelecionada.setTipoDoctoHabilitacao((TipoDoctoHabilitacao) Web.pegaDaSessao(TipoDoctoHabilitacao.class));
        tipoExecucaoClausulaBen.setClausulaBenificiario((ClausulaBenificiario) Web.pegaDaSessao(ClausulaBenificiario.class));
    }

    @URLAction(mappingId = "ver-tipo-execucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-tipo-execucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-execucao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();
        }
        return ultimaRevisao;
    }
}
