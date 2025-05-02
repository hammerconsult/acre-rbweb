package br.com.webpublico.controle;

import br.com.webpublico.entidades.MensagemUsuario;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.seguranca.MensagemTodosUsuariosService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


@ManagedBean
@SessionScoped
public class ComunicacaoTodosUsuariosControlador implements Serializable {

    private MensagemTodosUsuariosService mensagemTodosUsuariosService;
    private UsuarioSistema usuarioSistema;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private boolean visualizado = false;

    private Boolean marcouVisualizado;

    public Boolean getMarcouVisualizado() {
        return marcouVisualizado;
    }

    public void setMarcouVisualizado(Boolean marcouVisualizado) {
        this.marcouVisualizado = marcouVisualizado;
    }

    public List<MensagemUsuario> getMensagem() {
        return getComunicacaoTodosUsuariosSingleton().getMensagens().stream().filter(m -> !getComunicacaoTodosUsuariosSingleton().getMensagensLidas(getUsuarioSistema()).contains(m)).collect(Collectors.toList());
    }

    public MensagemTodosUsuariosService getComunicacaoTodosUsuariosSingleton() {
        if (mensagemTodosUsuariosService == null) {
            mensagemTodosUsuariosService = (MensagemTodosUsuariosService) Util.getSpringBeanPeloNome("mensagemTodosUsuariosService");
        }
        return mensagemTodosUsuariosService;
    }

    public void mostrarMensagemUsuario() {
        if (!getMensagem().isEmpty() && !isVisualizado()) {
            FacesUtil.executaJavaScript("$('#mensagemTodosUsuariosDialog').modal('show')");
        } else {
            FacesUtil.executaJavaScript("$('#mensagemTodosUsuariosDialog').modal('hide')");
        }
    }

    public void setVisualizado(boolean visualizado) {
        this.visualizado = visualizado;
    }

    public boolean isVisualizado() {
        return visualizado || getComunicacaoTodosUsuariosSingleton().getMensagensLidas(getUsuarioSistema()).containsAll(getMensagem());
    }

    public void marcarComoVisualizado() {
        setVisualizado(true);
    }

    public void marcarComoLidaAteReconstrucaoDoSistema(){
        if(marcouVisualizado){
            getComunicacaoTodosUsuariosSingleton().getMensagensLidas(getUsuarioSistema()).addAll(getMensagem());
        }
        marcarComoVisualizado();
    }

    public void recuperarUsuario() {
        usuarioSistema = sistemaFacade.getUsuarioCorrente();
    }

    public UsuarioSistema getUsuarioSistema() {
        if (usuarioSistema == null)
            recuperarUsuario();
        return usuarioSistema;
    }

}
