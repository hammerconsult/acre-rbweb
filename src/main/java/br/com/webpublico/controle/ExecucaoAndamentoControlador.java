package br.com.webpublico.controle;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaExecucaoAndamento",
        pattern = "/execucao-andamento/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-adm/execucao-andamento.xhtml")
})

public class ExecucaoAndamentoControlador extends AbstractExecucaoAndamentoControlador {

    @URLAction(mappingId = "novaExecucaoAndamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaExecucaoAndamento() {
        novaExecucaoContrato();
        setPermissaoEspecial(false);
    }
}
