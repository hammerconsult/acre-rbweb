package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.ReferenciaAnual;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mateus on 12/09/2014.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo14", pattern = "/relatorio/rreo/anexo14/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo14.xhtml")
})
public class RelatorioRREOAnexo14Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;

    public RelatorioRREOAnexo14Controlador() {
        super();
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), exercicio.getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setRelatorio(getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 14", exercicio, TipoRelatorioItemDemonstrativo.RREO));
        itemDemonstrativoFiltros.setExercicio(exercicio);
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        ReferenciaAnual referenciaAnual = getReferenciaAnualFacade().recuperaReferenciaPorExercicio(exercicio);
        List<ItemDemonstrativoComponente> itensAnexo5e6 = popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 6 - Novo", TipoRelatorioItemDemonstrativo.RREO));
        List<ItemDemonstrativoComponente> itensAnexo08 = popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 8", TipoRelatorioItemDemonstrativo.RREO));
        List<ItemDemonstrativoComponente> itensAnexo12 = popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 12", TipoRelatorioItemDemonstrativo.RREO));
        RelatoriosItemDemonst relatorioAnexo01 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo02 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo03 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo04 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo06Novo = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 6 - Novo", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo08 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo09 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 9", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo11 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 11", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo12 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exAnterior.getId());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", exAnterior.getAno());
        dto.adicionarParametro("mesInicial", bimestre.getMesInicial().getNumeroMesString());
        dto.adicionarParametro("mesFinal", bimestre.getMesFinal().getNumeroMesString());
        dto.adicionarParametro("metaResultadoPrimario", referenciaAnual != null ? referenciaAnual.getMetaResultadoPrimario() : BigDecimal.ZERO);
        dto.adicionarParametro("metaResultadoNominal", referenciaAnual != null ? referenciaAnual.getMetaResultadoNominal() : BigDecimal.ZERO);
        dto.adicionarParametro("BIMESTRE", bimestre.getToDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("itensAnexo5e6", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo5e6));
        dto.adicionarParametro("itensAnexo08", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo08));
        dto.adicionarParametro("itensAnexo12", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo12));
        dto.adicionarParametro("relatorioAnexo01", relatorioAnexo01.toDto());
        dto.adicionarParametro("relatorioAnexo02", relatorioAnexo02.toDto());
        dto.adicionarParametro("relatorioAnexo03", relatorioAnexo03.toDto());
        dto.adicionarParametro("relatorioAnexo04", relatorioAnexo04.toDto());
        dto.adicionarParametro("relatorioAnexo06Novo", relatorioAnexo06Novo.toDto());
        dto.adicionarParametro("relatorioAnexo08", relatorioAnexo08.toDto());
        dto.adicionarParametro("relatorioAnexo09", relatorioAnexo09.toDto());
        dto.adicionarParametro("relatorioAnexo11", relatorioAnexo11.toDto());
        dto.adicionarParametro("relatorioAnexo12", relatorioAnexo12.toDto());
        dto.setNomeRelatorio("RelatorioRREOAnexo14");
    }

    private List<ItemDemonstrativoComponente> popularComponentes(List<ItemDemonstrativo> itensDemonstrativos) {
        List<ItemDemonstrativoComponente> retorno = Lists.newArrayList();
        if (itensDemonstrativos != null && !itensDemonstrativos.isEmpty()) {
            for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
                retorno.add(new ItemDemonstrativoComponente(itemDemonstrativo));
            }
        }
        return retorno;
    }

    @URLAction(mappingId = "relatorio-rreo-anexo14", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO14_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        super.limparCampos();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo14";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo14/";
    }
}
