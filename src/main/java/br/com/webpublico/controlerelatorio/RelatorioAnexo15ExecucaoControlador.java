/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo15", pattern = "/relatorio/lei4320/anexo15/", viewId = "/faces/financeiro/relatorio/relatorioanexo15execucao.xhtml")
})
@ManagedBean
public class RelatorioAnexo15ExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioAnexo15ExecucaoControlador() {
        super();
    }

    public void recuperarRelatorioEAnexo() {
        recuperarRelatorio();
        buscarAnexoAnterior();
    }

    public void recuperarRelatorio() {
        itens = Lists.newArrayList();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 15", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo15", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO15_LEI_4320;
        super.limparCampos();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 15", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
        FacesUtil.atualizarComponente(":Formulario:painel-demonstrativo");
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial(getDataInicial());
        itemDemonstrativoFiltros.setDataFinal(getDataFinal());
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(exercicio);
        itemDemonstrativoFiltros.setParametros(getParametros());
        itemDemonstrativoFiltros.setUnidadeGestora(unidadeGestora);
    }

    private List<ParametrosRelatorios> getParametros() {
        List<ParametrosRelatorios> parametros = gerarParametrosPadrao();
        parametros.add(new ParametrosRelatorios("trunc(lanc.datalancamento)", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 2, true));
        parametros.add(new ParametrosRelatorios("trunc(est.dataestorno)", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 3, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        return parametros;
    }

    private List<ParametrosRelatorios> gerarParametrosPadrao() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros = filtrosParametrosUnidade(parametros);
        return parametros;
    }

    private List<ParametrosRelatorios> gerarSqlExAnterior() {
        List<ParametrosRelatorios> parametros = gerarParametrosPadrao();
        Exercicio exercicio = getExercicioFacade().getExercicioPorAno(getExercicio().getAno() - 1);
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.MAIOR_IGUAL, "01/01/" + exercicio.getAno(), "31/12/" + exercicio.getAno(), 2, true));
        return parametros;
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "15-CSL" : "15-" + unidadeGestora.getCodigo();
    }

    @Override
    public void acoesExtrasAoGerarOuSalvar() {
        filtros = "";
        instanciarItemDemonstrativoFiltros();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo15";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo15/";
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        Mes mesReferencia = mes != null ? mes : Mes.DEZEMBRO;
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("EXERCICIO", exercicio.getId());
        dto.adicionarParametro("FILTRO_DATA", getDescricaoMes());
        dto.adicionarParametro("FILTRO_UG", unidadeGestora != null ? unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao() : "");
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("parametros", ParametrosRelatorios.parametrosToDto(getParametros()));
        dto.adicionarParametro("idExercicio", exercicio.getId());
        dto.adicionarParametro("anoExercicio", exercicio.getAno());
        dto.adicionarParametro("dataInicial", "01/01/" + exercicio.getAno());
        dto.adicionarParametro("dataFinal", Util.getDiasMes(mesReferencia.getNumeroMes(), exercicio.getAno()) + "/" + mesReferencia.getNumeroMesString() + "/" + exercicio.getAno());
        dto.adicionarParametro("parametrosExAnterior", ParametrosRelatorios.parametrosToDto(gerarSqlExAnterior()));
        Exercicio exercicioAnterior = getExercicioFacade().getExercicioPorAno(getExercicio().getAno() - 1);
        dto.adicionarParametro("idExercicioAnterior", exercicioAnterior.getId());
        dto.adicionarParametro("anoExercicioAnterior", exercicioAnterior.getAno());
        dto.adicionarParametro("dataInicialAnterior", "01/01/" + exercicioAnterior.getAno());
        dto.adicionarParametro("dataFinalAnterior", "31/12/" + exercicioAnterior.getAno());
        adicionarItemDemonstrativoFiltrosCampoACampo(dto);
        dto.adicionarParametro("FILTRO_GERAL", atualizaFiltrosGerais().trim());
        dto.adicionarParametro("NOTA_EXPLICATIVA", relatoriosItemDemonst.getNotaExplicativa());
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + mesReferencia.getNumeroMesString() + "-ANEXO-" + getNumeroRelatorio());
    }
}
