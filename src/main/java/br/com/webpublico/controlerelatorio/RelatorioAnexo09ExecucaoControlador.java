package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.PortalTipoAnexo;
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
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 19/02/14
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo9", pattern = "/relatorio/lei4320/anexo9/", viewId = "/faces/financeiro/relatorio/relatorioanexo9loaexecucao.xhtml")
})
@ManagedBean
public class RelatorioAnexo09ExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    @URLAction(mappingId = "relatorio-lei4320-anexo9", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO9_LEI_4320;
        super.limparCampos();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        filtrosParametroDatas(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        atualizaFiltrosGerais();
        return parametros;
    }

    private void filtrosParametroDatas(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(" trunc(EMP.DATAEMPENHO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 2, true));
        parametros.add(new ParametrosRelatorios(" trunc(EMPEST.DATAESTORNO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 3, true));
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "09-CSL" : "09-" + unidadeGestora.getCodigo();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo09";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo9/";
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
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + (mes != null ? mes.getNumeroMesString() : "12") + "-ANEXO-" + getNumeroRelatorio());
    }
}
