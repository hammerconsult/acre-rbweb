/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.seguranca.service.UsuarioSistemaService;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@ManagedBean
@SessionScoped
public class SegurancaControlador implements Serializable {

    @ManagedProperty(name = "usuarioSistemaService", value = "#{usuarioSistemaService}")
    private UsuarioSistemaService usuarioSistemaService;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<String> recursosDoUsuario;

    /**
     * Creates a new instance of SegurancaControlador
     */
    public SegurancaControlador() {
    }

    public UsuarioSistemaService getUsuarioSistemaService() {
        return usuarioSistemaService;
    }

    public void setUsuarioSistemaService(UsuarioSistemaService usuarioSistemaService) {
        this.usuarioSistemaService = usuarioSistemaService;
    }

    public void recursosoDoUsuarioToNull() {
        this.recursosDoUsuario = null;
    }

    public List<String> getRecursosDoUsuario() {
        if (recursosDoUsuario == null) {
            UsuarioSistema usuarioLogado = sistemaFacade.getUsuarioCorrente();
            this.recursosDoUsuario = getTodosOsRecursosDe(usuarioLogado);
        }
        return recursosDoUsuario;
    }

    public void setRecursosDoUsuario(List<String> recursosDoUsuario) {
        this.recursosDoUsuario = recursosDoUsuario;
    }

    private List<String> getTodosOsRecursosDe(UsuarioSistema usuarioLogado) {
        List<String> retorno = new ArrayList<>();

        retorno.addAll(usuarioSistemaService.getRecursosSistemaDiretosDoUsuario(usuarioLogado));
        retorno.addAll(usuarioSistemaService.getRecursosSistemaDosGruposDeRecursoDoUsuario(usuarioLogado));
        retorno.addAll(usuarioSistemaService.getRecursosSistemaDosGruposDeUsuariosDoUsuario(usuarioLogado));

        return retorno;

    }
}
