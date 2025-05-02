package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.enums.TipoBem;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Desenvolvimento on 14/03/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoInventarioDeBensImoveis",
        pattern = "/inventario-de-bens-imoveis/",
        viewId = "/faces/administrativo/patrimonio/relatorios/imovel/inventariodebensimoveis.xhtml")})
public class RelatorioInventarioBemImovelControlador extends SuperRelatorioInventarioBemControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioInventarioBemMovelControlador.class);

    @URLAction(mappingId = "novoInventarioDeBensImoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoInventarioDeBensImoveis() {
        tipoBem = TipoBem.IMOVEIS;
        limparCampos();
    }

    public void gerarInventarioDeBemImovel(String tipoRelatorioExtensao) {
        gerarInventarioDeBem(tipoRelatorioExtensao);
    }
}
