/*
 * Codigo gerado automaticamente em Mon Jun 13 16:53:22 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HistoricoContabil;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HistoricoContabilFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "historicoContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-historico-contabil", pattern = "/historico-contabil/novo/", viewId = "/faces/financeiro/planodecontas/historicocontabil/edita.xhtml"),
    @URLMapping(id = "editar-historico-contabil", pattern = "/historico-contabil/editar/#{historicoContabilControlador.id}/", viewId = "/faces/financeiro/planodecontas/historicocontabil/edita.xhtml"),
    @URLMapping(id = "ver-historico-contabil", pattern = "/historico-contabil/ver/#{historicoContabilControlador.id}/", viewId = "/faces/financeiro/planodecontas/historicocontabil/visualizar.xhtml"),
    @URLMapping(id = "listar-historico-contabil", pattern = "/historico-contabil/listar/", viewId = "/faces/financeiro/planodecontas/historicocontabil/lista.xhtml")
})
public class HistoricoContabilControlador extends PrettyControlador<HistoricoContabil> implements Serializable, CRUD {

    @EJB
    private HistoricoContabilFacade historicoContabilFacade;

    public HistoricoContabilControlador() {
        super(HistoricoContabil.class);
    }

    public HistoricoContabilFacade getFacade() {
        return historicoContabilFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return historicoContabilFacade;
    }

    @URLAction(mappingId = "novo-historico-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        HistoricoContabil hc = (HistoricoContabil) selecionado;
//        hc.setCodigo(historicoContabilFacade.proximoCodigo());
        Long codigo = historicoContabilFacade.retornaUltimoCodigoLong();
        hc.setCodigo(String.valueOf(codigo));
    }

    @URLAction(mappingId = "ver-historico-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-historico-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/historico-contabil/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (selecionado.getId() == null) {
                    getFacede().salvarNovo(selecionado);
                } else {
                    selecionado = historicoContabilFacade.meuSalvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com sucesso!", ""));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Ocorreu um erro: ", e.getMessage()));
            }
            redireciona();
        }
    }
}
