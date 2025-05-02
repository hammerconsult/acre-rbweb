/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.enums.BimestreAnexosLei;
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
    @URLMapping(id = "relatorio-rreo-anexo4", pattern = "/relatorio/rreo/anexo4/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo04.xhtml")
})
public class RelatorioRREOAnexo04Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioRREOAnexo04Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo4", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO4_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        super.limparCampos();
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), exercicio.getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setRelatorio(getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", exercicio, TipoRelatorioItemDemonstrativo.RREO));
        itemDemonstrativoFiltros.setExercicio(exercicio);
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("BIMESTRE", bimestre.getToDto());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        adicionarItemDemonstrativoFiltrosCampoACampo(dto);
        dto.setNomeRelatorio("RELATÓRIO-RREO-ANEXO-04");
    }

    private String getNomeRelatorio() {
        if (exercicio.getAno() > 2022) {
            return "DEMONSTRATIVO DAS RECEITAS E DESPESAS PREVIDENCIÁRIAS";
        }
        return "DEMONSTRATIVO DAS RECEITAS E DESPESAS PREVIDENCIÁRIAS DO REGIME PRÓPRIO DE PREVIDÊNCIA DOS SERVIDORES";
    }

    @Override
    public void acoesExtrasAoGerarOuSalvar() {
        instanciarItemDemonstrativoFiltros();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo04";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo4/";
    }
}
