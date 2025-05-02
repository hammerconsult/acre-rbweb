package br.com.webpublico.controle;

import br.com.webpublico.entidades.OperadoraTecnologiaTransporte;
import br.com.webpublico.entidades.VeiculoOperadoraTecnologiaTransporte;
import br.com.webpublico.entidades.VistoriaVeiculoOtt;
import br.com.webpublico.enums.SituacaoVistoriaOtt;
import br.com.webpublico.enums.TipoParecerVistoriaOtt;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.VistoriaVeiculoOttFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "vistoriaVeiculoOttControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaVistoriaVeiculoOtt", pattern = "/vistoria-veiculo-operadora-tecnologia-transporte/novo/",
        viewId = "/faces/tributario/rbtrans/vistoriaveiculoott/edita.xhtml"),
    @URLMapping(id = "editarVistoriaVeiculoOtt", pattern = "/vistoria-veiculo-operadora-tecnologia-transporte/editar/#{vistoriaVeiculoOttControlador.id}/",
        viewId = "/faces/tributario/rbtrans/vistoriaveiculoott/edita.xhtml"),
    @URLMapping(id = "listarVistoriaVeiculoOtt", pattern = "/vistoria-veiculo-operadora-tecnologia-transporte/listar/",
        viewId = "/faces/tributario/rbtrans/vistoriaveiculoott/lista.xhtml"),
    @URLMapping(id = "verVistoriaVeiculoOtt", pattern = "/vistoria-veiculo-operadora-tecnologia-transporte/ver/#{vistoriaVeiculoOttControlador.id}/",
        viewId = "/faces/tributario/rbtrans/vistoriaveiculoott/visualizar.xhtml")
})
public class VistoriaVeiculoOttControlador extends PrettyControlador<VistoriaVeiculoOtt> implements Serializable, CRUD {

    @EJB
    private VistoriaVeiculoOttFacade vistoriaVeiculoOttFacade;

    public VistoriaVeiculoOttControlador() {
        super(VistoriaVeiculoOtt.class);
    }

    @URLAction(mappingId = "novaVistoriaVeiculoOtt", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoVistoriaOtt.ABERTA);
    }

    @URLAction(mappingId = "verVistoriaVeiculoOtt", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarVistoriaVeiculoOtt", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/vistoria-veiculo-operadora-tecnologia-transporte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return vistoriaVeiculoOttFacade;
    }

    public List<VeiculoOperadoraTecnologiaTransporte> completarVeiculos(String parte) {
        if (selecionado.getOperadoraTecTransporte() != null) {
            return vistoriaVeiculoOttFacade.getVeiculoOperadoraTecnologiaTransporteFacade().listarVeiculosPorOtt(parte, selecionado.getOperadoraTecTransporte());
        }
        return Lists.newArrayList();
    }


    public List<OperadoraTecnologiaTransporte> completarOperadora(String parte) {
        return vistoriaVeiculoOttFacade.getOperadoraTecnologiaTransporteFacade().listarOperadoras(parte);
    }

    public List<SelectItem> getTipoParecer() {
        return Util.getListSelectItem(TipoParecerVistoriaOtt.values());
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarVeiculo();
            vistoriaVeiculoOttFacade.calcularDataVencimentoVistoria(selecionado.getVeiculoOtTransporte().getPlacaVeiculo(), selecionado);
            selecionado = vistoriaVeiculoOttFacade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void finalizarVistoria() {
        try {
            validarCamposFinalizacao();
            selecionado.setSituacao(SituacaoVistoriaOtt.FINALIZADA);
            selecionado = vistoriaVeiculoOttFacade.salvarRetornando(selecionado);
            if (TipoParecerVistoriaOtt.FAVORAVEL.equals(selecionado.getTipoDeParecer())) {
                vistoriaVeiculoOttFacade.getVeiculoOperadoraTecnologiaTransporteFacade().aprovarVeiculoOTT(selecionado.getVeiculoOtTransporte());
                FacesUtil.addOperacaoRealizada("Veículo aprovado com sucesso!");
            }
            FacesUtil.addOperacaoRealizada("Vistoria finalizada com sucesso!");
            FacesUtil.executaJavaScript("dlgFinalizar.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void cancelarVistoria() {
        try {
            validarCamposCancelamento();
            selecionado.setSituacao(SituacaoVistoriaOtt.CANCELADA);
            selecionado = vistoriaVeiculoOttFacade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada("Vistoria cancelada com sucesso!");
            FacesUtil.executaJavaScript("dlgCancelar.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOperadoraTecTransporte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a operadora de Transporte!");
        }
        if (selecionado.getVeiculoOtTransporte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um veículo!");
        }
        if (selecionado.getDataVistoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data da vistoria!");
        }
        if (selecionado.getResponsavel() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o responsável!");
        }
        ve.lancarException();
    }

    public void validarCamposFinalizacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoDeParecer() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de parecer");
        }

        if (selecionado.getParecer() == null || selecionado.getParecer().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o parecer!");
        }
        ve.lancarException();
    }

    public void validarCamposCancelamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getParecer() == null || selecionado.getParecer().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o parecer!");
        }
        ve.lancarException();
    }

    private void validarVeiculo() {
        ValidacaoException ve = new ValidacaoException();
        if (vistoriaVeiculoOttFacade.verificarVistoriaNaoVencidaPorVeiculo(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma vistoria não vencida para este veículo.");
        }
        ve.lancarException();
    }
}
