package br.com.webpublico.controle;

import br.com.webpublico.entidades.ProvimentoFP;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 14/07/15
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "enquadramentoControlador")
@ViewScoped

@URLMappings(mappings = {
    @URLMapping(id = "novo-enquadramento", pattern = "/enquadramento/novo/", viewId = "/faces/rh/administracaodepagamento/enquadramento/edita.xhtml"),
    @URLMapping(id = "editar-enquadramento", pattern = "/enquadramento/editar/#{enquadramentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/enquadramento/edita.xhtml"),
    @URLMapping(id = "ver-enquadramento", pattern = "/enquadramento/ver/#{enquadramentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/enquadramento/visualizar.xhtml"),
    @URLMapping(id = "listar-enquadramento", pattern = "/enquadramento/listar/", viewId = "/faces/rh/administracaodepagamento/enquadramento/lista.xhtml")
})
public class EnquadramentoControlador extends AlteracaoCargoControlador {

    private RevisaoAuditoria ultimaRevisaoProvimento;

    @Override
    public String getCaminhoPadrao() {
        return "/enquadramento/";
    }

    @Override
    @URLAction(mappingId = "novo-enquadramento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.getProvimentoFP().setTipoProvimento(this.getAlteracaoCargoFacade().getTipoProvimentoFacade().recuperaTipoProvimentoPorCodigo(25));
    }

    @Override
    @URLAction(mappingId = "editar-enquadramento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-enquadramento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();
            if (selecionado.getProvimentoFP() != null) {
                ultimaRevisaoProvimento = buscarUltimaAuditoria(ProvimentoFP.class, selecionado.getProvimentoFP().getId());
                if (ultimaRevisaoProvimento != null && ultimaRevisao != null &&
                    ultimaRevisaoProvimento.getDataHora().after(ultimaRevisao.getDataHora())) {
                    ultimaRevisao = ultimaRevisaoProvimento;
                }else{
                    ultimaRevisaoProvimento = null;
                }
            }
        }
        return ultimaRevisao;
    }

    public void verRevisao() {
        if (ultimaRevisaoProvimento == null) {
            super.verRevisao();
        } else {
            Web.getSessionMap().put("pagina-anterior-auditoria-listar", PrettyContext.getCurrentInstance().getRequestURL().toString());
            FacesUtil.redirecionamentoInterno("/auditoria/ver/" + ultimaRevisaoProvimento.getId() + "/" + selecionado.getProvimentoFP().getId() + "/" + ProvimentoFP.class .getSimpleName()+ "/");
        }

    }
}
