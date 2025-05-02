/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo17", pattern = "/relatorio/lei4320/anexo17/", viewId = "/faces/financeiro/relatorio/relatorioanexo17execucao.xhtml")
})
@ManagedBean
public class RelatorioAnexo17ExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioAnexo17ExecucaoControlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo17", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO17_LEI_4320;
        super.limparCampos();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 17", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
        FacesUtil.atualizarComponente(":Formulario:painel-demonstrativo");
    }

    public void recuperarRelatorioEAnexo() {
        buscarRelatorio();
        buscarAnexoAnterior();
    }

    public void buscarRelatorio() {
        itens = Lists.newArrayList();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 17", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros));
        return parametros;
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "17-CSL" : "17-" + unidadeGestora.getCodigo();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo17";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo17/";
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder().toUpperCase());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("EXERCICIO", exercicio.getId());
        dto.adicionarParametro("dataInicial", getDataInicial());
        dto.adicionarParametro("dataFinal", getDataFinal());
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("FILTRO_DATA", getDescricaoMes());
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        filtros = atualizaFiltrosGerais();
        dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
        if (unidadeGestora != null) {
            dto.adicionarParametro("idUndiadeGestora", unidadeGestora.getId());
            dto.adicionarParametro("FILTRO_UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
        }
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + (mes != null ? mes.getNumeroMesString() : "12") + "-ANEXO-" + getNumeroRelatorio());
    }
}
