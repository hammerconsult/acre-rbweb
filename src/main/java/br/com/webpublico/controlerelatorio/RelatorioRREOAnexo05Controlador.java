/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.ReferenciaAnual;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
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
    @URLMapping(id = "relatorio-rreo-anexo5", pattern = "/relatorio/rreo/anexo5/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo05.xhtml")
})
public class RelatorioRREOAnexo05Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioRREOAnexo05Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo5", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO5_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        super.limparCampos();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        ReferenciaAnual referenciaAnual = getReferenciaAnualFacade().recuperaReferenciaPorExercicio(exercicio);
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MESINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("MESFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
        dto.adicionarParametro("PARAMMESFINAL", Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), exercicio.getAno()) + "/" + bimestre.getMesFinal().getDescricao().substring(0, 3) + "/" + exercicio.getAno());
        String bimestreAnterior = alterarMesFinal(alterarMesFinal(bimestre.getMesFinal().getNumeroMesString()));
        if ("12".equals(bimestreAnterior)) {
            dto.adicionarParametro("BIMESTREANTERIOR", Util.getDiasMes(new Integer(bimestreAnterior), exercicio.getAno() - 1) + "/" + Mes.getMesToInt(Integer.parseInt(bimestreAnterior)).getDescricao().substring(0, 3) + "/" + (exercicio.getAno() - 1));
        } else {
            dto.adicionarParametro("BIMESTREANTERIOR", Util.getDiasMes(new Integer(bimestreAnterior), exercicio.getAno()) + "/" + Mes.getMesToInt(Integer.parseInt(bimestreAnterior)).getDescricao().substring(0, 3) + "/" + exercicio.getAno());
        }
        dto.adicionarParametro("ANOBIMESTREANTERIOR", (exercicio.getAno() - 1));
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("mesInicial", bimestre.getMesInicial().getNumeroMesString());
        dto.adicionarParametro("mesFinal", bimestre.getMesFinal().getNumeroMesString());
        dto.adicionarParametro("metaResultadoNominalRA", referenciaAnual.getMetaResultadoNominal());
        dto.setNomeRelatorio("RelatorioRREOAnexo05");
    }

    private String alterarMesFinal(String mes) {
        Integer mesARetornar;
        if ("01".equals(mes)) {
            mesARetornar = 12;
        } else {
            mesARetornar = Integer.parseInt(mes) - 1;
        }
        if (mesARetornar < 10) {
            return "0" + mesARetornar;
        } else {
            return mesARetornar.toString();
        }
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo05";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo5/";
    }
}
