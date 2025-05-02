/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatoriosItemDemonstParametroDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo11", pattern = "/relatorio/rreo/anexo11/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo11.xhtml")
})
public class RelatorioRREOAnexo11Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioRREOAnexo11Controlador() {
        super();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        RelatoriosItemDemonst relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 11", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro(RelatoriosItemDemonstParametroDTO.DATAINICIAL, bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro(RelatoriosItemDemonstParametroDTO.DATAFINAL, bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro(RelatoriosItemDemonstParametroDTO.ID_EXERCICIO, exercicio.getId());
        dto.adicionarParametro(RelatoriosItemDemonstParametroDTO.ANO_EXERCICIO, exercicio.getAno());
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        dto.adicionarParametro(RelatoriosItemDemonstParametroDTO.ID_EXERCICIO_ANTERIOR, exAnterior.getId());
        dto.adicionarParametro(RelatoriosItemDemonstParametroDTO.ANO_EXERCICIO_ANTERIOR, exAnterior.getAno());
        dto.adicionarParametro(RelatoriosItemDemonstParametroDTO.MES_FINAL, bimestre.getMesFinal().getNumeroMesString());
        dto.adicionarParametro(RelatoriosItemDemonstParametroDTO.ITENS_DEMONSTRATIVOS_COMPONENTE, ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro(RelatoriosItemDemonstParametroDTO.RELATORIOS_ITEM_DEMONST, relatoriosItemDemonst.toDto());
        dto.setNomeRelatorio("RelatorioRREOAnexo11");
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo11";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo11/";
    }

    @URLAction(mappingId = "relatorio-rreo-anexo11", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO11_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        super.limparCampos();
    }
}
