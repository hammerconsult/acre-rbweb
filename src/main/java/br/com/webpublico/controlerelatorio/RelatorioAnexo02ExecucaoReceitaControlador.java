/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo2-receita", pattern = "/relatorio/lei4320/anexo2-receita/", viewId = "/faces/financeiro/relatorio/relatorioanexo2loareceitageralexecucao.xhtml")
})
@ManagedBean
public class RelatorioAnexo02ExecucaoReceitaControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    private TipoRelatorioAnexoDois tipoRelatorioAnexoDois;

    @URLAction(mappingId = "relatorio-lei4320-anexo2-receita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        tipoRelatorioAnexoDois = TipoRelatorioAnexoDois.GERAL;
        portalTipoAnexo = PortalTipoAnexo.ANEXO2_REC_LEI_4320;
        super.limparCampos();
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        filtrosParametroDatas(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    private void filtrosParametroDatas(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(" trunc(LANC.DATALANCAMENTO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 2, true));
        parametros.add(new ParametrosRelatorios(" trunc(RLEST.DATAESTORNO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 3, true));
    }

    public List<SelectItem> getTiposRelatorios() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioAnexoDois.values());
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo02Receita";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo2-receita/";
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("EXERCICIO", exercicio.getId());
        dto.adicionarParametro("FILTRO_DATA", getDescricaoMes());
        dto.adicionarParametro("FILTRO_UG", unidadeGestora != null ? unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao() : "");
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("consolidado", TipoRelatorioAnexoDois.GERAL.equals(tipoRelatorioAnexoDois));
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
        dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + (mes != null ?  mes.getNumeroMesString() : "12") + "-ANEXO-" + getNumeroRelatorio());
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "02-RECEITA-CSL" : "02-RECEITA-" + unidadeGestora.getCodigo();
    }

    private enum TipoRelatorioAnexoDois {
        GERAL("Consolidado"),
        ORGAO_E_UNIDADE("Órgão e Unidade");
        private String descricao;

        TipoRelatorioAnexoDois(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public TipoRelatorioAnexoDois getTipoRelatorioAnexoDois() {
        return tipoRelatorioAnexoDois;
    }

    public void setTipoRelatorioAnexoDois(TipoRelatorioAnexoDois tipoRelatorioAnexoDois) {
        this.tipoRelatorioAnexoDois = tipoRelatorioAnexoDois;
    }
}
