package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.ProcessoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.TecnicoLicenciamentoAmbiental;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.tributario.TecnicoLicenciamentoAmbientalFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTecnicoLicenciamentoAmbiental",
        pattern = "/tributario/licenciamento-ambiental/tecnico/novo/",
        viewId = "/faces/tributario/licenciamentoambiental/tecnico/edita.xhtml"),
    @URLMapping(id = "editarTecnicoLicenciamentoAmbiental",
        pattern = "/tributario/licenciamento-ambiental/tecnico/editar/#{tecnicoLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/tecnico/edita.xhtml"),
    @URLMapping(id = "verTecnicoLicenciamentoAmbiental",
        pattern = "/tributario/licenciamento-ambiental/tecnico/ver/#{tecnicoLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/tecnico/visualizar.xhtml"),
    @URLMapping(id = "listarTecnicoLicenciamentoAmbiental",
        pattern = "/tributario/licenciamento-ambiental/tecnico/listar/",
        viewId = "/faces/tributario/licenciamentoambiental/tecnico/lista.xhtml")
})
public class TecnicoLicenciamentoAmbientalControlador extends PrettyControlador<TecnicoLicenciamentoAmbiental> implements CRUD, Serializable {

    @EJB
    private TecnicoLicenciamentoAmbientalFacade facade;

    public TecnicoLicenciamentoAmbientalControlador() {
        super(TecnicoLicenciamentoAmbiental.class);
    }

    @Override
    public TecnicoLicenciamentoAmbientalFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-ambiental/tecnico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verTecnicoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTecnicoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "novoTecnicoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    public List<TecnicoLicenciamentoAmbiental> completarTecnicos(String parte) {
        return facade.buscarTecnicosPorParte(parte);
    }
}
