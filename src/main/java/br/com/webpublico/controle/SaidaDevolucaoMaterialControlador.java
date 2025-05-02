package br.com.webpublico.controle;

import br.com.webpublico.entidades.SaidaDevolucaoMaterial;
import br.com.webpublico.util.EntidadeMetaData;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 18/11/13
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "saidaDevolucaoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaSaidaDevolucao", pattern = "/saida-por-devolucao/novo/", viewId = "/faces/administrativo/materiais/saidaMaterial/devolucao/edita.xhtml"),
        @URLMapping(id = "editarSaidaDevolucao", pattern = "/saida-por-devolucao/editar/#{saidaDevolucaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/devolucao/edita.xhtml"),
        @URLMapping(id = "listarSaidaDevolucao", pattern = "/saida-por-devolucao/listar/", viewId = "/faces/administrativo/materiais/saidaMaterial/devolucao/lista.xhtml"),
        @URLMapping(id = "verSaidaDevolucao", pattern = "/saida-por-devolucao/ver/#{saidaDevolucaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/devolucao/visualizar.xhtml")
})
public class SaidaDevolucaoMaterialControlador extends SaidaMaterialControlador {

    public SaidaDevolucaoMaterialControlador() {
        metadata = new EntidadeMetaData(SaidaDevolucaoMaterial.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/saida-por-devolucao/";
    }

    @Override
    @URLAction(mappingId = "novaSaidaDevolucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarSaidaDevolucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verSaidaDevolucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }
}
