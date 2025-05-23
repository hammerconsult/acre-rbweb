package br.com.webpublico.controle;

import br.com.webpublico.entidades.MensagemUsuario;
import br.com.webpublico.seguranca.MensagemTodosUsuariosService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;


@ManagedBean
@SessionScoped
public class ComunicacaoTodosUsuariosControlador implements Serializable {

    private MensagemTodosUsuariosService mensagemTodosUsuariosService;
    private boolean visualizado = false;

    public List<MensagemUsuario> getMensagem() {
        return getComunicacaoTodosUsuariosSingleton().getMensagens();
    }

    public MensagemTodosUsuariosService getComunicacaoTodosUsuariosSingleton() {
        if (mensagemTodosUsuariosService == null) {
            mensagemTodosUsuariosService = (MensagemTodosUsuariosService) Util.getSpringBeanPeloNome("mensagemTodosUsuariosService");
        }
        return mensagemTodosUsuariosService;
    }

    public void mostrarMensagemUsuario() {
        if (!getMensagem().isEmpty() && !visualizado) {
            FacesUtil.executaJavaScript("$('#mensagemTodosUsuariosDialog').modal('show')");
        } else {
            FacesUtil.executaJavaScript("$('#mensagemTodosUsuariosDialog').modal('hide')");
        }
    }

    public boolean isVisualizado() {
        return visualizado;
    }

    public void setVisualizado(boolean visualizado) {
        this.visualizado = visualizado;
    }

    public void marcarComoVisualizado() {
        this.visualizado = true;
    }
}
