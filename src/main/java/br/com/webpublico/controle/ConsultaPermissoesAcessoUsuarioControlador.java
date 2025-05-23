package br.com.webpublico.controle;


import br.com.webpublico.entidades.GrupoUsuario;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.RecursoSistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andregustavo on 08/05/2015.
 */
@ManagedBean(name = "consultaPermissoesAcessoUsuarioControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "consultaPermissoesUsuario", pattern = "/usuario/consulta-permissoes/", viewId = "/faces/admin/controleusuario/usuariosistema/consultapermissoes.xhtml")
        }
)
public class ConsultaPermissoesAcessoUsuarioControlador implements Serializable {
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private RecursoSistemaFacade recursoSistemaFacade;

    private UsuarioSistema usuarioSistema;
    private RecursoSistema recursoSistema;
    private List<GrupoUsuario> gruposUsuario;


    @URLAction(mappingId = "novoModelo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsulta() {
        usuarioSistema = null;
        recursoSistema = null;
        gruposUsuario = new ArrayList<>();
    }

    public boolean validaConsulta() {
        boolean valida = true;
        if (usuarioSistema == null) {
            FacesUtil.addCampoObrigatorio("O Usuário é obrigatório.");
            valida = false;
        }
        return valida;
    }

    public void executaConsulta() {
        if(validaConsulta()) {
            usuarioSistema = usuarioSistemaFacade.recuperaDependenciasDeUsuario(usuarioSistema.getId());
            gruposUsuario = usuarioSistema.getGrupos();
        }

    }

    public Boolean getRenderizaResultado() {
        if (gruposUsuario != null) {
            return !gruposUsuario.isEmpty();
        }
        return false;
    }

    public String retornaCorBooleano(Boolean param) {
        if (param) {
            return "green";
        }
        return "red";
    }

    public String retornaCorRecursoEncontrado(RecursoSistema recurso) {
        if (recursoSistema != null && recursoSistema.getCaminho().equals(recurso.getCaminho())) {
            return "green";
        }
        return "white";
    }

    public List<UsuarioSistema> completeUsuarioSistema(String filtro) {
        return usuarioSistemaFacade.completarUsuarioSistemaPeloNomePessoaFisica(filtro);
    }

    public List<RecursoSistema> completeRecursoSistema(String filtro) {
        return recursoSistemaFacade.listaFiltrando(filtro.trim(), "nome", "caminho");
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public RecursoSistema getRecursoSistema() {
        return recursoSistema;
    }

    public void setRecursoSistema(RecursoSistema recursoSistema) {
        this.recursoSistema = recursoSistema;
    }

    public List<GrupoUsuario> getGruposUsuario() {
        return gruposUsuario;
    }

    public void setGruposUsuario(List<GrupoUsuario> gruposUsuario) {
        this.gruposUsuario = gruposUsuario;
    }
}
