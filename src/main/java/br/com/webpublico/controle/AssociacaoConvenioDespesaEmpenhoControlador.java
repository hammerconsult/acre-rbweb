package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConvenioDespesa;
import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AssociacaoConvenioDespesaEmpenhoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMapping(id = "associacao-convenio-despesa-empenho", pattern = "/associacao-convenio-despesa-empenho/", viewId = "/faces/financeiro/orcamentario/associacao-convenio-despesa-empenho/edita.xhtml")
public class AssociacaoConvenioDespesaEmpenhoControlador {

    @EJB
    private AssociacaoConvenioDespesaEmpenhoFacade facade;
    private Empenho empenho;
    private ConvenioDespesa convenioDespesa;

    @URLAction(mappingId = "associacao-convenio-despesa-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        empenho = null;
        convenioDespesa = null;
    }

    public void atualizarConvenioDespesa() {
        if (empenho != null) {
            convenioDespesa = empenho.getConvenioDespesa();
        }
    }

    public List<Empenho> completarEmpenhosNormaisAndRestos(String filtro) {
        return facade.buscarEmpenhosNormaisAndRestos(filtro);
    }

    public List<ConvenioDespesa> completarConveniosDeDespesa(String filtro) {
        return facade.buscarConveniosDeDespesa(filtro);
    }

    public void salvar() {
        try {
            validarCampos();
            empenho = facade.recuperarEmpenho(empenho);
            empenho.setConvenioDespesa(convenioDespesa);
            facade.salvarEmpenho(empenho);
            FacesUtil.addOperacaoRealizada("O empenho foi atualizado com sucesso!");
            limparCampos();
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (empenho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empenho deve ser informado!");
        }
        ve.lancarException();
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }
}

