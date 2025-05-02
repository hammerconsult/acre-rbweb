package br.com.webpublico.controlerelatorio;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 06/01/14
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo10", pattern = "/relatorio/lei4320/anexo10/", viewId = "/faces/financeiro/relatorio/relatorioanexo10loaexecucao.xhtml")
})
@ManagedBean
public class RelatorioAnexo10ExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    @URLAction(mappingId = "relatorio-lei4320-anexo10", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO10_LEI_4320;
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
        parametros.add(new ParametrosRelatorios(" trunc(ALTORC.DATAEFETIVACAO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 2, true));
        parametros.add(new ParametrosRelatorios(" trunc(LANC.DATALANCAMENTO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 6, true));
        parametros.add(new ParametrosRelatorios(" trunc(RLEST.DATAESTORNO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 7, true));
        parametros.add(new ParametrosRelatorios(" trunc(EST.DATAESTORNO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 3, true));
        parametros.add(new ParametrosRelatorios(" LDO.EXERCICIO_ID ", ":EXERCICIO_ID", null, OperacaoRelatorio.IGUAL, exercicio.getId(), null, 4, false));
        parametros.add(new ParametrosRelatorios(" PCE.EXERCICIO_ID ", ":EXERCICIO_ID", null, OperacaoRelatorio.IGUAL, exercicio.getId(), null, 5, false));
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "10-CSL" : "10-" + unidadeGestora.getCodigo();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo10";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo10/";
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
