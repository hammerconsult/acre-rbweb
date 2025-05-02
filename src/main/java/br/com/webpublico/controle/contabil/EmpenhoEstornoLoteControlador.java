package br.com.webpublico.controle.contabil;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.EmpenhoEstornoLote;
import br.com.webpublico.entidades.EmpenhoEstornoLoteEmpenhoEstorno;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.execucao.EmpenhoEstornoLoteFacade;
import br.com.webpublico.util.FacesUtil;
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
    @URLMapping(id = "novoEmpenhoEstornoLote", pattern = "/contabil/cancelamento-empenho-restos-a-pagar-por-lote/novo/", viewId = "/faces/financeiro/cancelamento-empenho-restos-a-pagar-por-lote/edita.xhtml"),
    @URLMapping(id = "listarEmpenhoEstornoLote", pattern = "/contabil/cancelamento-empenho-restos-a-pagar-por-lote/listar/", viewId = "/faces/financeiro/cancelamento-empenho-restos-a-pagar-por-lote/lista.xhtml"),
    @URLMapping(id = "verEmpenhoEstornoLote", pattern = "/contabil/cancelamento-empenho-restos-a-pagar-por-lote/ver/#{empenhoEstornoLoteControlador.id}/", viewId = "/faces/financeiro/cancelamento-empenho-restos-a-pagar-por-lote/visualizar.xhtml")
})
public class EmpenhoEstornoLoteControlador extends PrettyControlador<EmpenhoEstornoLote> implements Serializable, CRUD {
    @EJB
    private EmpenhoEstornoLoteFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public EmpenhoEstornoLoteControlador() {
        super(EmpenhoEstornoLote.class);
    }

    @URLAction(mappingId = "novoEmpenhoEstornoLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(facade.getUsuarioCorrente());
        hierarquiaOrganizacional = null;
        selecionado.setErros("");
        selecionado.setUnidadeOrganizacional(null);
    }

    @URLAction(mappingId = "verEmpenhoEstornoLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        hierarquiaOrganizacional = facade.getHierarquiaDaUnidade(selecionado);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/cancelamento-empenho-restos-a-pagar-por-lote/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrcamentarias(String parte) {
        return facade.buscarHierarquiasOrcamentarias(parte);
    }

    public void buscarEmpenhosEstornos() {
        try {
            validarCamposBuscar();
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            selecionado.getEmpenhosEstornos().clear();
            facade.buscarEMontarEmpenhosEstornos(selecionado);
            if (selecionado.getErros() != null && !selecionado.getErros().trim().isEmpty()) {
                FacesUtil.addWarn("Atenção", "Não foi possível realizar o estorno automatico de alguns Empenhos, consulte a aba de erros para mais informações.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarCamposBuscar() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade deve ser informado.");
        }
        ve.lancarException();
    }

    public void removerEmpenhoEstorno(EmpenhoEstornoLoteEmpenhoEstorno esl) {
        selecionado.getEmpenhosEstornos().remove(esl);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
