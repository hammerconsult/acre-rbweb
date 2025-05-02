package br.com.webpublico.controle;

import br.com.webpublico.entidades.RodapeAlvara;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.RodapeAlvaraFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRodapeAlvara", pattern = "/rodape-alvara/novo/", viewId = "/faces/tributario/alvara/rodapealvara/edita.xhtml"),
    @URLMapping(id = "editaRodapeAlvara", pattern = "/rodape-alvara/editar/#{rodapeAlvaraControlador.id}/", viewId = "/faces/tributario/alvara/rodapealvara/edita.xhtml"),
    @URLMapping(id = "listarRodapeAlvara", pattern = "/rodape-alvara/listar/", viewId = "/faces/tributario/alvara/rodapealvara/lista.xhtml"),
    @URLMapping(id = "verRodapeAlvara", pattern = "/rodape-alvara/ver/#{rodapeAlvaraControlador.id}/", viewId = "/faces/tributario/alvara/rodapealvara/visualizar.xhtml")
})
public class RodapeAlvaraControlador extends PrettyControlador<RodapeAlvara> implements CRUD {

    @EJB
    private RodapeAlvaraFacade rodapeAlvaraFacade;

    public RodapeAlvaraControlador() {
        super(RodapeAlvara.class);
    }

    @Override
    public RodapeAlvaraFacade getFacede() {
        return rodapeAlvaraFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rodape-alvara/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoRodapeAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editaRodapeAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verRodapeAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarRodapeAlvara();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar rodape alvara. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Rodapé do Alvará. Detalhes: " + e.getMessage());
        }
    }

    private void validarRodapeAlvara() {
        ValidacaoException ve = new ValidacaoException();
        if (rodapeAlvaraFacade.hasCombinacaoCheckboxJaSalva(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um rodapé salvo com essas configurações. ");
        }
        if (Strings.isNullOrEmpty(selecionado.getTextoRodape())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Texto do Rodapé deve ser informado.");
        }
        ve.lancarException();
    }
}
