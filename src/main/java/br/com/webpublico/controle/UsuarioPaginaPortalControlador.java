/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.controle.portaltransparencia.entidades.UsuarioPaginaPortal;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.UsuarioPaginaPrefeituraPortalFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "usuarioPaginaPortalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaUsuarioPaginaPrefeituraPortal", pattern = "/portal/transparencia/usuario/novo/", viewId = "/faces/portaltransparencia/usuario/edita.xhtml"),
    @URLMapping(id = "editarUsuarioPaginaPrefeituraPortal", pattern = "/portal/transparencia/usuario/editar/#{usuarioPaginaPortalControlador.id}/", viewId = "/faces/portaltransparencia/usuario/edita.xhtml"),
    @URLMapping(id = "listarUsuarioPaginaPortal", pattern = "/portal/transparencia/usuario/listar/", viewId = "/faces/portaltransparencia/usuario/lista.xhtml")
})
public class UsuarioPaginaPortalControlador extends PrettyControlador<UsuarioPaginaPortal> implements Serializable, CRUD {

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @EJB
    private UsuarioPaginaPrefeituraPortalFacade facade;

    public UsuarioPaginaPortalControlador() {
        super(UsuarioPaginaPortal.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/portal/transparencia/usuario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaUsuarioPaginaPrefeituraPortal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarUsuarioPaginaPrefeituraPortal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (selecionado.getId() == null) {
            selecionado.setSenha(bCryptPasswordEncoder.encode(selecionado.getSenha()));
        }
        super.salvar();
    }
}
