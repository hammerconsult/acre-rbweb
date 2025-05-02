package br.com.webpublico.controle;

import br.com.webpublico.entidades.AgrupadorRelatorioCredito;
import br.com.webpublico.entidades.AgrupadorRelatorioCreditoDivida;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AgrupadorRelatorioCreditoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-agrupador-relatorio-credito", pattern = "/agrupador-relatorio-credito/novo/", viewId = "/faces/tributario/agrupador-relatorio-credito/edita.xhtml"),
    @URLMapping(id = "editar-agrupador-relatorio-credito", pattern = "/agrupador-relatorio-credito/editar/#{agrupadorRelatorioCreditoControlador.id}/", viewId = "/faces/tributario/agrupador-relatorio-credito/edita.xhtml"),
    @URLMapping(id = "listar-agrupador-relatorio-credito", pattern = "/agrupador-relatorio-credito/listar/", viewId = "/faces/tributario/agrupador-relatorio-credito/lista.xhtml"),
    @URLMapping(id = "ver-agrupador-relatorio-credito", pattern = "/agrupador-relatorio-credito/ver/#{agrupadorRelatorioCreditoControlador.id}/", viewId = "/faces/tributario/agrupador-relatorio-credito/visualizar.xhtml")
})
public class AgrupadorRelatorioCreditoControlador extends PrettyControlador<AgrupadorRelatorioCredito> implements Serializable, CRUD {

    @EJB
    private AgrupadorRelatorioCreditoFacade agrupadorRelatorioCreditoFacade;
    private AgrupadorRelatorioCreditoDivida agrupadorRelatorioCreditoDivida;

    public AgrupadorRelatorioCreditoControlador() {
        super(AgrupadorRelatorioCredito.class);
    }

    @URLAction(mappingId = "novo-agrupador-relatorio-credito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cancelarDivida();
    }

    @URLAction(mappingId = "editar-agrupador-relatorio-credito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cancelarDivida();
    }

    @URLAction(mappingId = "ver-agrupador-relatorio-credito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<Divida> completarDividas(String filtro) {
        return agrupadorRelatorioCreditoFacade.getDividaFacade().listaFiltrandoDividas(filtro.trim(), "codigo", "descricao");
    }

    public void instanciarDivida() {
        agrupadorRelatorioCreditoDivida = new AgrupadorRelatorioCreditoDivida();
        agrupadorRelatorioCreditoDivida.setAgrupadorRelatorioCredito(selecionado);
    }

    public void cancelarDivida() {
        agrupadorRelatorioCreditoDivida = null;
    }

    public void editarDivida(AgrupadorRelatorioCreditoDivida obj) {
        agrupadorRelatorioCreditoDivida = (AgrupadorRelatorioCreditoDivida) Util.clonarObjeto(obj);
    }

    public void removerDivida(AgrupadorRelatorioCreditoDivida obj) {
        selecionado.getDividas().remove(obj);
    }

    public void adicionarDivida() {
        try {
            validarDivida();
            Util.adicionarObjetoEmLista(selecionado.getDividas(), agrupadorRelatorioCreditoDivida);
            cancelarDivida();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDivida() {
        ValidacaoException ve = new ValidacaoException();
        if (agrupadorRelatorioCreditoDivida.getDivida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dívida deve ser informado!");
        } else {
            for (AgrupadorRelatorioCreditoDivida divida : selecionado.getDividas()) {
                if (!agrupadorRelatorioCreditoDivida.equals(divida) && divida.getDivida().equals(agrupadorRelatorioCreditoDivida.getDivida())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A dívida selecionada já está adicionada!");
                }
            }
        }
        ve.lancarException();
    }

    public AgrupadorRelatorioCreditoDivida getAgrupadorRelatorioCreditoDivida() {
        return agrupadorRelatorioCreditoDivida;
    }

    public void setAgrupadorRelatorioCreditoDivida(AgrupadorRelatorioCreditoDivida agrupadorRelatorioCreditoDivida) {
        this.agrupadorRelatorioCreditoDivida = agrupadorRelatorioCreditoDivida;
    }

    @Override
    public void salvar() {
        try {
            salvarAgrupador();
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void salvarDialog() {
        try {
            salvarAgrupador();
            FacesUtil.executaJavaScript("dialogCadastrarAgrupador.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void salvarAgrupador() {
        validarCampos();
        if (isOperacaoNovo()) {
            agrupadorRelatorioCreditoFacade.salvarNovo(selecionado);
        } else {
            agrupadorRelatorioCreditoFacade.salvar(selecionado);
        }
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDividas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma dívida!");
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return agrupadorRelatorioCreditoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/agrupador-relatorio-credito/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
