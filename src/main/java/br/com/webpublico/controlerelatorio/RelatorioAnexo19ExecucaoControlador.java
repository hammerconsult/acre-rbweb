package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Mateus on 24/11/2014.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo19", pattern = "/relatorio/lei4320/anexo19/", viewId = "/faces/financeiro/relatorio/relatorioanexo19execucao.xhtml")
})
@ManagedBean
public class RelatorioAnexo19ExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioAnexo19ExecucaoControlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo19", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO19_LEI_4320;
        super.limparCampos();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 19", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
        FacesUtil.atualizarComponente(":Formulario:painel-demonstrativo");
    }

    public void recuperarRelatorioEAnexo() {
        recuperarRelatorio();
        buscarAnexoAnterior();
    }

    public void recuperarRelatorio() {
        itens = Lists.newArrayList();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 19", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setUnidadeGestora(unidadeGestora);
        itemDemonstrativoFiltros.setDataInicial(getDataInicial());
        itemDemonstrativoFiltros.setDataFinal(getDataFinal());
        itemDemonstrativoFiltros.setExercicio(exercicio);
        itemDemonstrativoFiltros.setParametros(montarParametros());
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros));
        return parametros;
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "19-CSL" : "19-" + unidadeGestora.getCodigo();
    }

    @Override
    public void acoesExtrasAoGerarOuSalvar() {
        filtros = "";
        instanciarItemDemonstrativoFiltros();
        filtros = atualizaFiltrosGerais();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo19";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo19/";
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("parametros", itemDemonstrativoFiltros.toDto());
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
        dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("EXERCICIO", exercicio.getId());
        dto.adicionarParametro("FILTRO_DATA", getDescricaoMes());
        dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
        if (unidadeGestora != null) {
            dto.adicionarParametro("FILTRO_UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
        }
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + (mes != null ? mes.getNumeroMesString() : "12") + "-ANEXO-" + getNumeroRelatorio());
    }
}

