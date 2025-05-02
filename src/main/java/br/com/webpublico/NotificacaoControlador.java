/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico;


import br.com.webpublico.consultaentidade.FiltroConsultaEntidade;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "paginacao",
        pattern = "/notificacao/paginacao/#{notificacaoControlador.tipo}/",
        viewId = "/faces/admin/notificacao/portipo.xhtml")
})

public class NotificacaoControlador implements Serializable {

    private String tipo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @URLAction(mappingId = "paginacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void paginacao() {

    }

    public void navega(Notificacao notificacao) {
        FacesUtil.redirecionamentoInterno(notificacao.getLink());
    }

    public void marcarComoLida(Long id) {
        NotificacaoService.getService().marcarComoLida(new Notificacao(id));
    }

    public void navega(NotificacaoService.NotificacaoDTO notificacao) {
        FacesUtil.redirecionamentoInterno("notificacao/paginacao/" + notificacao.getTipoNotificacao().name());
    }

    public void addFiltro(FiltroConsultaEntidade filtro) {
        filtro.setValor(TipoNotificacao.valueOf(tipo));
    }
}
