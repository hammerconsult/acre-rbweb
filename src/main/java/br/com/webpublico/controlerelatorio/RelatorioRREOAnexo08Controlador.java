package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 25/10/13
 * Time: 11:19
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo8", pattern = "/relatorio/rreo/anexo8/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo08.xhtml")
})
public class RelatorioRREOAnexo08Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioRREOAnexo08Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo8", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO8_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        super.limparCampos();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        Exercicio exercicioAnterior = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        RelatoriosItemDemonst relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatoriosItemDemonstExAnterior = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", exercicioAnterior, TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exercicioAnterior.getId());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", exercicioAnterior.getAno().toString());
        dto.adicionarParametro("BIMESTRE", bimestre.getToDto());
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.adicionarParametro("relatoriosItemDemonstDTOExAnterior", relatoriosItemDemonstExAnterior.toDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.setNomeRelatorio("RelatorioRREOAnexo08");
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo08";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo8/";
    }
}
