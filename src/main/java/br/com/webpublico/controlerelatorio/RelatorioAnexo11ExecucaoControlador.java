package br.com.webpublico.controlerelatorio;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 13/12/13
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo11", pattern = "/relatorio/lei4320/anexo11/", viewId = "/faces/financeiro/relatorio/relatorioanexo11loaexecucao.xhtml")
})
@ManagedBean
public class RelatorioAnexo11ExecucaoControlador extends AbstractRelatorioItemDemonstrativo {

    @URLAction(mappingId = "relatorio-lei4320-anexo11", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO11_LEI_4320;
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
        parametros.add(new ParametrosRelatorios(" trunc(ALT.DATAEFETIVACAO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 2, true));
        parametros.add(new ParametrosRelatorios(" trunc(EMP.DATAEMPENHO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 3, true));
        parametros.add(new ParametrosRelatorios(" trunc(EST.DATAESTORNO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 5, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 6, true));
        parametros.add(new ParametrosRelatorios(" DESP.EXERCICIO_ID ", ":EXERCICIO_ID", null, OperacaoRelatorio.IGUAL, exercicio.getId(), null, 4, false));
        parametros.add(new ParametrosRelatorios(" EMP.EXERCICIO_ID ", ":EXERCICIO_ID", null, OperacaoRelatorio.IGUAL, exercicio.getId(), null, 7, false));
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "11-CSL" : "11-" + unidadeGestora.getCodigo();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo11";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo11/";
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
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
        dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + (mes != null ? mes.getNumeroMesString() : "12") + "-ANEXO-" + getNumeroRelatorio());
    }
}
