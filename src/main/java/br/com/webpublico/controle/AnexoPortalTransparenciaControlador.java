package br.com.webpublico.controle;

import br.com.webpublico.controle.portaltransparencia.entidades.PaginaPrefeituraPortal;
import br.com.webpublico.entidades.AnexoPortalTransparencia;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaSituacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AnexoPortalTransparenciaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAnexoPortalTransparencia", pattern = "/anexo-geral-portal-transparencia/novo/", viewId = "/faces/financeiro/anexo-portal-transparencia/edita.xhtml"),
    @URLMapping(id = "editarAnexoPortalTransparencia", pattern = "/anexo-geral-portal-transparencia/editar/#{anexoPortalTransparenciaControlador.id}/", viewId = "/faces/financeiro/anexo-portal-transparencia/edita.xhtml"),
    @URLMapping(id = "listarAnexoPortalTransparencia", pattern = "/anexo-geral-portal-transparencia/listar/", viewId = "/faces/financeiro/anexo-portal-transparencia/lista.xhtml"),
    @URLMapping(id = "verAnexoPortalTransparencia", pattern = "/anexo-geral-portal-transparencia/ver/#{anexoPortalTransparenciaControlador.id}/", viewId = "/faces/financeiro/anexo-portal-transparencia/visualizar.xhtml")
})
public class AnexoPortalTransparenciaControlador extends PrettyControlador<AnexoPortalTransparencia> implements Serializable, CRUD {
    @EJB
    private AnexoPortalTransparenciaFacade facade;

    public AnexoPortalTransparenciaControlador() {
        super(AnexoPortalTransparencia.class);
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/anexo-geral-portal-transparencia/";
    }

    @URLAction(mappingId = "novoAnexoPortalTransparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getExercicioCorrente());
        selecionado.setUsuarioSistema(facade.getUsuarioCorrente());
    }

    @URLAction(mappingId = "verAnexoPortalTransparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado.setHierarquia(facade.getHierarquiaDaUnidade(selecionado));
    }

    @URLAction(mappingId = "editarAnexoPortalTransparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado.setHierarquia(facade.getHierarquiaDaUnidade(selecionado));
    }

    public void apagaArquivo() {
        selecionado.setArquivo(null);
    }

    public void uploadArquivo(FileUploadEvent evento) {
        try {
            selecionado.setArquivo(facade.criarArquivo(evento.getFile()));
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public List<HierarquiaOrganizacional> completarHierarquiasAdministrativas(String parte) {
        return facade.buscarHierarquiasAdministrativas(parte);
    }

    public List<PaginaPrefeituraPortal> completarPaginas(String parte) {
        return facade.buscarPaginas(parte);
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(PortalTransparenciaSituacao.values(), false);
    }
}
