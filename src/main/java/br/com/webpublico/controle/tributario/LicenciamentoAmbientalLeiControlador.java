
package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.tributario.LicenciamentoAmbientalLei;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.tributario.LicenciamentoAmbientalLeiFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarLicenciamentoAmbientalLei", pattern = "/tributario/licenciamento-ambiental/legislacao/listar/",
        viewId = "/faces/tributario/licenciamentoambiental/legislacao/lista.xhtml"),
    @URLMapping(id = "editarLicenciamentoAmbientalLei", pattern = "/tributario/licenciamento-ambiental/legislacao/editar/#{licenciamentoAmbientalLeiControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/legislacao/edita.xhtml"),
    @URLMapping(id = "verLicenciamentoAmbientalLei", pattern = "/tributario/licenciamento-ambiental/legislacao/ver/#{licenciamentoAmbientalLeiControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/legislacao/visualizar.xhtml"),
    @URLMapping(id = "novoLicenciamentoAmbientalLei", pattern = "/tributario/licenciamento-ambiental/legislacao/novo/",
        viewId = "/faces/tributario/licenciamentoambiental/legislacao/edita.xhtml")
})
public class LicenciamentoAmbientalLeiControlador extends PrettyControlador<LicenciamentoAmbientalLei> implements Serializable, CRUD {

    @EJB
    private LicenciamentoAmbientalLeiFacade facade;

    public LicenciamentoAmbientalLeiControlador() {
        super(LicenciamentoAmbientalLei.class);
    }

    @Override
    public LicenciamentoAmbientalLeiFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-ambiental/legislacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoLicenciamentoAmbientalLei", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarLicenciamentoAmbientalLei", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verLicenciamentoAmbientalLei", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void uploadLei(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            selecionado.setArquivo(arquivo);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void removerArquivoLei() {
        selecionado.setArquivo(null);
    }

    @Override
    public void salvar() {
        try {
            validarLei();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar o Lei: " + ex.getMessage());
        }
    }

    public void validarLei() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (isOperacaoNovo() && facade.jaExisteLeiComMesmaDescricao(selecionado.getDescricaoReduzida())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma lei adicionada com essa mesma descrição resumida");
        }
        ve.lancarException();
    }
}
