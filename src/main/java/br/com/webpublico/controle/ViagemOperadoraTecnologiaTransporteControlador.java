package br.com.webpublico.controle;

import br.com.webpublico.entidades.OperadoraTecnologiaTransporte;
import br.com.webpublico.entidades.ViagemOperadoraTecnologiaTransporte;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OperadoraTecnologiaTransporteFacade;
import br.com.webpublico.negocios.ViagemOperadoraTecnologiaTransporteFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author octavio
 */
@ManagedBean(name = "viagemOperadoraTecnologiaTransporteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoViagemOperadoraTransporte", pattern = "/viagem-operadora-tecnologia-transporte/novo/",
        viewId = "/faces/tributario/rbtrans/viagemoperadotransporte/edita.xhtml"),
    @URLMapping(id = "editarViagemOperadoraTransporte", pattern = "/viagem-operadora-tecnologia-transporte/editar/#{viagemOperadoraTecnologiaTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/viagemoperadotransporte/edita.xhtml"),
    @URLMapping(id = "listarViagemOperadoraTransporte", pattern = "/viagem-operadora-tecnologia-transporte/listar/",
        viewId = "/faces/tributario/rbtrans/viagemoperadotransporte/lista.xhtml"),
    @URLMapping(id = "verViagemOperadoraTransporte", pattern = "/viagem-operadora-tecnologia-transporte/ver/#{viagemOperadoraTecnologiaTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/viagemoperadotransporte/visualizar.xhtml")
})
public class ViagemOperadoraTecnologiaTransporteControlador extends PrettyControlador<ViagemOperadoraTecnologiaTransporte> implements Serializable, CRUD {

    @EJB
    private ViagemOperadoraTecnologiaTransporteFacade viagemOperadoraTecnologiaTransporteFacade;
    @EJB
    private OperadoraTecnologiaTransporteFacade operadoraTecnologiaTransporteFacade;

    public ViagemOperadoraTecnologiaTransporteControlador() {
        super(ViagemOperadoraTecnologiaTransporte.class);
    }

    @URLAction(mappingId = "novoViagemOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verViagemOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarViagemOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/viagem-operadora-tecnologia-transporte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return viagemOperadoraTecnologiaTransporteFacade;
    }

    public List<OperadoraTecnologiaTransporte> completaOperadora(String parte) {
        return operadoraTecnologiaTransporteFacade.listarOperadoras(parte);
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOperadoraTecTransporte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione a operadora de tecnologia de transporte!");
        }
        if (selecionado.getDataChamadas() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Digite a data do chamado!");
        }
        if (selecionado.getQtdCorridas() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Digite a quantidade de corridas!");
        }
        if (selecionado.getQtdCorridas() != null && selecionado.getQtdCorridas().equals(0L)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade de corridas deve ser maior que zero!");
        }
        if (selecionado.getValorTotalCorridas() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Digite o valor total das corridas");
        }
        if (selecionado.getValorTotalCorridas() != null && selecionado.getValorTotalCorridas().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Valor Total não pode ser menor ou igual a zero!");
        }
        ve.lancarException();
    }


}
