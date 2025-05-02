package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.services.ServiceIntegracaoTributarioContabil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "integracaoArrecadacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "tentarNovamenteIntegracaoArrecadacao",
                pattern = "/integracao-tributario-contabil/insconsistencias/",
                viewId = "/faces/tributario/arrecadacao/baixa/tentarnovamente.xhtml"),
})
public class IntegracaoArrecadacaoControlador implements Serializable {


    private Map<Tributo, BigDecimal> tributos;
    @EJB
    private IntegracaoTributarioContabilFacade facade;
    private List<String> logReprocessamento;

    @URLAction(mappingId = "tentarNovamenteIntegracaoArrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void lista() {
        tributos = facade.getMapaTributoValorNaoIntegrados();
        //System.out.println("tributos " + tributos);
    }

    public Map<Tributo, BigDecimal> getTributos() {
        return tributos;
    }

    public List<String> getLogReprocessamento() {
        return logReprocessamento;
    }

    public void reprocessa() {
        logReprocessamento = Lists.newArrayList();
//        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
//        ServiceIntegracaoTributarioContabil service = (ServiceIntegracaoTributarioContabil) ap.getBean("serviceIntegracaoTributarioContabil");
//        for (Tributo tributo : tributos.keySet()) {
//            logReprocessamento.addAll(Lists.newArrayList(service.reprocessaNaoIntegrados(tributo)));
//        }
        lista();
        if (logReprocessamento == null || logReprocessamento.isEmpty()) {
            FacesUtil.addInfo("Operação concluída", "Todos os itens foram reprocessados");
        }else{
            FacesUtil.addWarn("Atenção","Houve alguma falha durante o reprocessamento");
        }
    }


}
