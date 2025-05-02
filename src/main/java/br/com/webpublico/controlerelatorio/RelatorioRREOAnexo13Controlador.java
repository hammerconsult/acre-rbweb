package br.com.webpublico.controlerelatorio;

/**
 * Created by Mateus on 17/08/2014.
 */

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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo13", pattern = "/relatorio/rreo/anexo13/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo13.xhtml")})
public class RelatorioRREOAnexo13Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioRREOAnexo13Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo13", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO13_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        super.limparCampos();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 13", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo3 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        RelatoriosItemDemonst relatorioAnexo03ExAnterior = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exAnterior, TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exAnterior.getId());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", exAnterior.getAno());
        dto.adicionarParametro("mesFinal", bimestre.getMesFinal().getToDto());
        dto.adicionarParametro("relatoriosItemDemonstAnexo3", relatorioAnexo3.toDto());
        dto.adicionarParametro("relatorioAnexo03ExAnterior", relatorioAnexo03ExAnterior.toDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("ANO-1", (exercicio.getAno() - 1));
        dto.adicionarParametro("ANO+1", (exercicio.getAno() + 1));
        dto.adicionarParametro("ANO+2", (exercicio.getAno() + 2));
        dto.adicionarParametro("ANO+3", (exercicio.getAno() + 3));
        dto.adicionarParametro("ANO+4", (exercicio.getAno() + 4));
        dto.adicionarParametro("ANO+5", (exercicio.getAno() + 5));
        dto.adicionarParametro("ANO+6", (exercicio.getAno() + 6));
        dto.adicionarParametro("ANO+7", (exercicio.getAno() + 7));
        dto.adicionarParametro("ANO+8", (exercicio.getAno() + 8));
        dto.adicionarParametro("ANO+9", (exercicio.getAno() + 9));
        dto.setNomeRelatorio("RelatorioRREOAnexo13");
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo13";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo13/";
    }
}
