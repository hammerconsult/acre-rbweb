package br.com.webpublico.controle;

import br.com.webpublico.entidades.BloqueioBeneficio;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.OpcaoValeTransporteFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ValeTransporteFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 08/03/2017.
 */
@ManagedBean(name = "valeTransporteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-vale-transporte", pattern = "/vale-transporte/novo/", viewId = "/faces/rh/administracaodepagamento/valetransporte/edita.xhtml"),
    @URLMapping(id = "editar-vale-transporte", pattern = "/vale-transporte/editar/#{valeTransporteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/valetransporte/edita.xhtml"),
    @URLMapping(id = "listar-vale-transporte", pattern = "/vale-transporte/listar/", viewId = "/faces/rh/administracaodepagamento/valetransporte/lista.xhtml"),
    @URLMapping(id = "ver-vale-transporte", pattern = "/vale-transporte/ver/#{valeTransporteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/valetransporte/visualizar.xhtml")
})
public class ValeTransporteControlador extends PrettyControlador<OpcaoValeTransporteFP> implements Serializable, CRUD {

    @EJB
    private ValeTransporteFacade valeTransporteFacade;

    public ValeTransporteControlador() {
        super(OpcaoValeTransporteFP.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/vale-transporte/";
    }

    @URLAction(mappingId = "novo-vale-transporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-vale-transporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-vale-transporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarBloqueios();
            Util.adicionarObjetoEmLista(selecionado.getContratoFP().getOpcaoValeTransporteFPs(), selecionado);
            valeTransporteFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        if (selecionado.getFinalVigencia() != null && selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Final da Vigência deve ser maior ou igual ao Início da Vigência!");
        }
        if (selecionado.getComplementoQuantidade() != null && selecionado.getComplementoQuantidade().compareTo(0) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Quantidade Complementar deve ser maior ou igual a zero(0)!");
        }
        ve.lancarException();
        for (OpcaoValeTransporteFP p : selecionado.getContratoFP().getOpcaoValeTransporteFPs()) {
            if (p.getFinalVigencia() != null) {
                if (!p.equals(selecionado) && p.getFinalVigencia().after(selecionado.getInicioVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data Final da vigência anterior não pode ser superior a data inicial da nova vigência!");
                }
            } else if (!p.equals(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato selecionado contem registro(s) com vigência aberta!");
            }
        }
        ve.lancarException();
    }

    private void validarBloqueios() {
        ValidacaoException ve = new ValidacaoException();
        BloqueioBeneficio bb = valeTransporteFacade.buscarBloqueios(selecionado);
        if (bb != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe um bloqueio de benefício de vale transporte vigente para este servidor no período selecionado. " +
                "<a href='"+ FacesUtil.getRequestContextPath() + "/bloqueio-beneficio/ver/" + bb.getId() + "/' target='_blank'>/bloqueio-beneficio/ver/" + bb.getId() + "</a>");
        }
        ve.lancarException();
    }

    public List<ContratoFP> completarContratosFP(String parte) {
        return valeTransporteFacade.getContratoFPFacade().recuperarFiltrandoContratosVigentesEm(parte.trim(), UtilRH.getDataOperacao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return valeTransporteFacade;
    }
}
