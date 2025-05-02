package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
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
 * Created by Mateus on 19/11/2014.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo18", pattern = "/relatorio/lei4320/anexo18/", viewId = "/faces/financeiro/relatorio/relatorioanexo18execucao.xhtml")
})
@ManagedBean
public class RelatorioAnexo18ExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    private ItemDemonstrativoFiltros itemDemonstrativoFiltrosExecicioAnterior;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltrosSaldoContabil;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltrosSaldoContabilInicial;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltrosSaldoContabilExercicioAnterior;

    public RelatorioAnexo18ExecucaoControlador() {
        super();
    }

    public void recuperarRelatorioEAnexo() {
        recuperarRelatorio();
        buscarAnexoAnterior();
    }

    public void recuperarRelatorio() {
        itens = Lists.newArrayList();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 18", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo18", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO18_LEI_4320;
        super.limparCampos();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 18", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
        FacesUtil.atualizarComponente(":Formulario:painel-demonstrativo");
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setUnidadeGestora(unidadeGestora);
        itemDemonstrativoFiltros.setParametros(montarParametros());
        itemDemonstrativoFiltros.setDataInicial(getDataInicial());
        itemDemonstrativoFiltros.setDataFinal(getDataFinal());
        itemDemonstrativoFiltros.setExercicio(exercicio);
        itemDemonstrativoFiltros.setParametros(montarParametros());
    }

    private void instanciarItemDemonstrativoFiltrosExecicioAnterior() {
        itemDemonstrativoFiltrosExecicioAnterior = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltrosExecicioAnterior.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltrosExecicioAnterior.setUnidadeGestora(unidadeGestora);
        Exercicio ex = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        itemDemonstrativoFiltrosExecicioAnterior.setDataInicial("01/01/" + ex.getAno());
        itemDemonstrativoFiltrosExecicioAnterior.setExercicio(ex);
        itemDemonstrativoFiltrosExecicioAnterior.setDataFinal("31/12/" + ex.getAno());
        itemDemonstrativoFiltrosExecicioAnterior.setParametros(gerarSqlExAnterior());
    }

    private void instanciarItemDemonstrativoFiltrosSaldoContabil() {
        itemDemonstrativoFiltrosSaldoContabil = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltrosSaldoContabil.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltrosSaldoContabil.setUnidadeGestora(unidadeGestora);
        itemDemonstrativoFiltrosSaldoContabil.setParametros(montarParametros());
        itemDemonstrativoFiltrosSaldoContabil.setDataInicial(getDataInicial());
        itemDemonstrativoFiltrosSaldoContabil.setDataFinal(getDataFinal());
        itemDemonstrativoFiltrosSaldoContabil.setExercicio(exercicio);
        itemDemonstrativoFiltrosSaldoContabil.setParametros(gerarSqlContabil());
    }

    private void instanciarItemDemonstrativoFiltrosSaldoContabilInicial() {
        itemDemonstrativoFiltrosSaldoContabilInicial = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltrosSaldoContabilInicial.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltrosSaldoContabilInicial.setUnidadeGestora(unidadeGestora);
        itemDemonstrativoFiltrosSaldoContabilInicial.setParametros(montarParametros());
        itemDemonstrativoFiltrosSaldoContabilInicial.setDataInicial(getDataInicial());
        itemDemonstrativoFiltrosSaldoContabilInicial.setDataFinal(getDataInicial());
        itemDemonstrativoFiltrosSaldoContabilInicial.setExercicio(exercicio);
        itemDemonstrativoFiltrosSaldoContabilInicial.setParametros(gerarSqlContabilSaldoInicial());
    }

    private void instanciarItemDemonstrativoFiltrosSaldoContabilExecicioAnterior() {
        itemDemonstrativoFiltrosSaldoContabilExercicioAnterior = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltrosSaldoContabilExercicioAnterior.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltrosSaldoContabilExercicioAnterior.setUnidadeGestora(unidadeGestora);
        itemDemonstrativoFiltrosSaldoContabilExercicioAnterior.setParametros(montarParametros());
        Exercicio ex = getExercicioFacade().getExercicioPorAno(getExercicio().getAno() - 1);
        itemDemonstrativoFiltrosSaldoContabilExercicioAnterior.setDataInicial("01/01/" + ex.getAno());
        itemDemonstrativoFiltrosSaldoContabilExercicioAnterior.setDataFinal("31/12/" + ex.getAno());
        itemDemonstrativoFiltrosSaldoContabilExercicioAnterior.setExercicio(ex);
        itemDemonstrativoFiltrosSaldoContabilExercicioAnterior.setParametros(gerarSqlContabilExAnterior());
    }

    private void instanciarItemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior() {
        itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior.setUnidadeGestora(unidadeGestora);
        itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior.setParametros(montarParametros());
        Exercicio ex = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior.setExercicio(ex);
        itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior.setDataInicial("01/01/" + ex.getAno());
        itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior.setDataFinal("01/01/" + ex.getAno());
        itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior.setParametros(gerarSqlContabilSaldoInicialExercicioAnterior());
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 0, true));
        return parametros;
    }

    private List<ParametrosRelatorios> gerarSqlExAnterior() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros, false));
        Exercicio ex = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", null, "01/01/" + ex.getAno(), "31/12/" + ex.getAno(), 0, true));
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, ex.getId(), null, 0, false, Long.class));
        }
        return parametros;
    }

    private List<ParametrosRelatorios> gerarSqlContabilSaldoInicial() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros, false));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", null, getDataInicial(), getDataInicial(), 0, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false, Long.class));
        return parametros;
    }

    private List<ParametrosRelatorios> gerarSqlContabilSaldoInicialExercicioAnterior() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros, false));
        Exercicio ex = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", null, "01/01/" + ex.getAno(), "01/01/" + ex.getAno(), 0, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, ex.getId(), null, 0, false, Long.class));
        return parametros;
    }

    private List<ParametrosRelatorios> gerarSqlContabilExAnterior() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros, false));
        Exercicio ex = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", null, "01/01/" + ex.getAno(), "31/12/" + ex.getAno(), 0, true));
        parametros.add(new ParametrosRelatorios(null, ":eercicio", null, null, ex.getId(), null, 0, false, Long.class));
        return parametros;
    }

    private List<ParametrosRelatorios> gerarSqlContabil() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros, false));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", null, getDataInicial(), getDataFinal(), 0, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicio().getId(), null, 0, false, Long.class));
        return parametros;
    }

    public List<ParametrosRelatorios> filtrosParametrosUnidade(List<ParametrosRelatorios> parametros, Boolean filtrarExercicio) {
        List<Long> idsUnidades = Lists.newArrayList();
        if (!unidades.isEmpty()) {
            String codigosUnidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                codigosUnidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            filtros += " Unidade(s): " + codigosUnidades;

        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = getRelatoriosItemDemonstFacade().getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), exercicio, getDataExercicioAndMes(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            if (filtrarExercicio) {
                parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false, Long.class));
            }
        }
        return parametros;
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "18-CSL" : "18-" + unidadeGestora.getCodigo();
    }

    @Override
    public void acoesExtrasAoGerarOuSalvar() {
        filtros = "";
        instanciarItemDemonstrativoFiltros();
        filtros = atualizaFiltrosGerais();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo18";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo18/";
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
        dto.adicionarParametro("FILTRO_DATA", getDescricaoMes());
        instanciarItemDemonstrativoFiltrosExecicioAnterior();
        instanciarItemDemonstrativoFiltrosSaldoContabil();
        instanciarItemDemonstrativoFiltrosSaldoContabilExecicioAnterior();
        instanciarItemDemonstrativoFiltrosSaldoContabilInicial();
        instanciarItemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior();
        dto.adicionarParametro("parametros", itemDemonstrativoFiltros.toDto());
        dto.adicionarParametro("parametrosExercicioAnterior", itemDemonstrativoFiltrosExecicioAnterior.toDto());
        dto.adicionarParametro("parametrosSaldoContabil", itemDemonstrativoFiltrosSaldoContabil.toDto());
        dto.adicionarParametro("parametrosSaldoContabilInicial", itemDemonstrativoFiltrosSaldoContabilInicial.toDto());
        dto.adicionarParametro("parametrosSaldoContabilExercicioAnterior", itemDemonstrativoFiltrosSaldoContabilExercicioAnterior.toDto());
        dto.adicionarParametro("parametrosSaldoContabilInicialExercicioAnterior", itemDemonstrativoFiltrosSaldoContabilInicialExercicioAnterior.toDto());
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", (getExercicio().getAno() - 1) + "");
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + (mes != null ? mes.getNumeroMesString() : "12") + "-ANEXO-" + getNumeroRelatorio());
    }
}
