package br.com.webpublico.controle;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaExecucaoAndamentoPermissaoEspecial",
        pattern = "/execucao-contrato-andamento/permissao-especial/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-adm/execucao-andamento-especial.xhtml")
})

public class ExecucaoAndamentoPermissaoEspecialControlador extends AbstractExecucaoAndamentoControlador {

    @URLAction(mappingId = "novaExecucaoAndamentoPermissaoEspecial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaExecucaoAndamentoPermissaoEspecial() {
        novaExecucaoContrato();
        setPermissaoEspecial(true);
    }
}
