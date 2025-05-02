package br.com.webpublico.controle.comum;

import br.com.webpublico.negocios.comum.DiagnosticoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author Daniel Franco
 * @since 10/02/2017 11:07
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "diagnostico-sistema", pattern = "/diagnostico/", viewId = "/faces/diagnostico/diagnostico.xhtml")
})
public class DiagnosticoControlador {

    @EJB
    private DiagnosticoFacade diagnosticoFacade;

    public void executarDiagnosticoLatenciaUsuarioServidor() {

    }

    @URLAction(mappingId = "diagnostico-sistema", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
    }


    public long executarDiagnosticoPerformanceJVM() {
        return diagnosticoFacade.performanceJVM();
    }

    public long executarDiagnosticoLatenciaServidorBanco() {
        return diagnosticoFacade.pingBancoDeDados();
    }

    public long executarDiagnosticoPerformanceBanco() {
        return diagnosticoFacade.performanceBancoDeDados();
    }
}
