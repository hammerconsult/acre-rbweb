/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo14", pattern = "/relatorio/lei4320/anexo14/", viewId = "/faces/financeiro/relatorio/relatorioanexo14execucao.xhtml")
})
@ManagedBean
public class RelatorioAnexo14ExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioAnexo14ExecucaoControlador() {
        super();
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial(getDataInicial());
        itemDemonstrativoFiltros.setDataFinal(getDataFinal());
        itemDemonstrativoFiltros.setRelatorio(getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 14", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320));
        itemDemonstrativoFiltros.setExercicio(exercicio);
        itemDemonstrativoFiltros.setParametros(parametros());
        itemDemonstrativoFiltros.setUnidadeGestora(unidadeGestora);
    }

    private List<ParametrosRelatorios> parametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros));
        return parametros;
    }

    private List<ParametrosRelatorios> getParametrosExercicioAtual() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 2, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        return parametros;
    }

    private List<ParametrosRelatorios> getParametrosExercicioAnterior() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade(parametros));
        Exercicio exercicio = getExercicioFacade().getExercicioPorAno(getExercicio().getAno() - 1);
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), "31/12/" + exercicio.getAno(), 2, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        return parametros;
    }

    public void recuperarRelatorioEAnexo() {
        recuperarRelatorio();
        buscarAnexoAnterior();
    }

    public void recuperarRelatorio() {
        itens = Lists.newArrayList();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 14", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo14", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO14_LEI_4320;
        super.limparCampos();
        recuperarRelatorio();
        FacesUtil.atualizarComponente(":Formulario:painel-demonstrativo");
    }

    private String getFiltrosAtualizados() {
        filtros = atualizaFiltrosGerais();
        return filtros.trim();
    }

    @Override
    public List<ParametrosRelatorios> filtrosParametrosUnidade(List<ParametrosRelatorios> parametros) {
        List<Long> idsUnidades = new ArrayList<>();
        if (!unidades.isEmpty()) {
            String codigosUnidades = "";
            for (HierarquiaOrganizacional lista : unidades) {
                idsUnidades.add(lista.getSubordinada().getId());
                codigosUnidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            if (!filtros.contains("Unidade(s)")) {
                filtros += " Unidade(s): " + codigosUnidades;
            }

        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = getRelatoriosItemDemonstFacade().getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), exercicio, getDataExercicioAndMes(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                idsUnidades.add(lista.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.LIKE, this.unidadeGestora.getCodigo(), null, 1, false));
        }
        return parametros;
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "14-CSL" : "14-" + unidadeGestora.getCodigo();
    }

    @Override
    public void acoesExtrasAoGerarOuSalvar() {
        instanciarItemDemonstrativoFiltros();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo14";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo14/";
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("EXERCICIO", exercicio.getId());
        Exercicio exercicioAnterior = getExercicioFacade().getExercicioPorAno(getExercicio().getAno() - 1);
        dto.adicionarParametro("relatoriosItemDemonst", relatoriosItemDemonst.toDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        adicionarItemDemonstrativoFiltrosCampoACampo(dto);
        dto.adicionarParametro("idExercicioAnterior", exercicioAnterior.getId());
        dto.adicionarParametro("anoExercicioAnterior", exercicioAnterior.getAno());
        dto.adicionarParametro("dataInicialAnterior", "01/01/" + exercicioAnterior.getAno());
        dto.adicionarParametro("dataFinalAnterior", "31/12/" + exercicioAnterior.getAno());
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("parametrosExercicioAtual", ParametrosRelatorios.parametrosToDto(getParametrosExercicioAtual()));
        dto.adicionarParametro("parametrosExercicioAnterior", ParametrosRelatorios.parametrosToDto(getParametrosExercicioAnterior()));
        if (unidadeGestora != null) {
            dto.adicionarParametro("codigoUg", unidadeGestora.getCodigo());
            dto.adicionarParametro("FILTRO_UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
        }
        dto.adicionarParametro("FILTRO_DATA", getDescricaoMes());
        dto.adicionarParametro("FILTRO_GERAL", getFiltrosAtualizados());
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + (mes != null ? mes.getNumeroMesString() : "12") + "-ANEXO-" + getNumeroRelatorio());
    }
}
