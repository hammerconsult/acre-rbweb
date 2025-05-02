/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.Anexo13ExecucaoItem;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
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
    @URLMapping(id = "relatorio-lei4320-anexo13", pattern = "/relatorio/lei4320/anexo13/", viewId = "/faces/financeiro/relatorio/relatorioanexo13execucao.xhtml")
})
@ManagedBean
public class RelatorioAnexoTrezeExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    private List<ParametrosRelatorios> parametroUnidades;

    public RelatorioAnexoTrezeExecucaoControlador() {
        super();
    }

    public void recuperarRelatorioEAnexo() {
        recuperarRelatorio();
        buscarAnexoAnterior();
    }

    public void recuperarRelatorio() {
        itens = Lists.newArrayList();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 13", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
        unidadeGestora = null;
        unidades = Lists.newArrayList();
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo13", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO13_LEI_4320;
        super.limparCampos();
        parametroUnidades = Lists.newArrayList();
        recuperarRelatorio();
        FacesUtil.atualizarComponente(":Formulario:painel-demonstrativo");
    }

    private String getFiltrosAtualizados() {
        filtros = atualizaFiltrosGerais();
        return filtros.trim();
    }

    private void instanciarItemDemonstrativoFiltros() {
        parametroUnidades = Lists.newArrayList();
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setUnidadeGestora(unidadeGestora);
    }

    public List<Anexo13ExecucaoItem> prepararDadosParaEmissao() {
        List<Anexo13ExecucaoItem> toReturn = Lists.newArrayList();
        Anexo13ExecucaoItem item = new Anexo13ExecucaoItem();
        item.setDescricao("");
        toReturn.add(item);
        return toReturn;
    }

    private List<ParametrosRelatorios> buscarParametrosPadrao(Mes mesReferencia) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade());
        itemDemonstrativoFiltros.setExercicio(exercicio);
        itemDemonstrativoFiltros.setDataInicial("01/01/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mesReferencia.getNumeroMes(), exercicio.getAno()) + "/" + mesReferencia.getNumeroMesString() + "/" + exercicio.getAno());
        return parametros;
    }

    private List<ParametrosRelatorios> buscarParametrosPadraoExercicioAnterior() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        parametros.addAll(filtrosParametrosUnidade());
        adicionarParametroExercicioAnterior();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + (exercicio.getAno() - 1));
        itemDemonstrativoFiltros.setDataFinal("31/12/" + (exercicio.getAno() - 1));
        return parametros;
    }

    private void adicionarParametroExercicioAnterior() {
        itemDemonstrativoFiltros.setExercicio(getExercicioFacade().getExercicioPorAno(this.exercicio.getAno() - 1));
    }

    public List<ParametrosRelatorios> filtrosParametrosUnidade() {
        if (parametroUnidades == null || parametroUnidades.isEmpty()) {
            atualizarParametroUnidades();
        }
        return parametroUnidades;
    }

    private void atualizarParametroUnidades() {
        parametroUnidades = Lists.newArrayList();
        List<Long> idsUnidades = Lists.newArrayList();
        if (this.unidadeGestora == null) {
            if (!unidades.isEmpty()) {
                String codigosUnidades = "";
                for (HierarquiaOrganizacional hierarquiaOrganizacional : unidades) {
                    idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                    codigosUnidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
                }
                parametroUnidades.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
                if (!filtros.contains("Unidade(s)")) {
                    filtros += " Unidade(s): " + codigosUnidades;
                }

            } else {
                List<HierarquiaOrganizacional> unidadesDoUsuario = getRelatoriosItemDemonstFacade().getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getExercicio(), getDataExercicioAndMes(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
                for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                    idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                }
                if (!idsUnidades.isEmpty()) {
                    parametroUnidades.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
                }
            }
        } else {
            parametroUnidades.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
        }
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "13-CSL" : "13-" + unidadeGestora.getCodigo();
    }

    @Override
    public void acoesExtrasAoGerarOuSalvar() {
        filtros = "";
        parametroUnidades = Lists.newArrayList();
        instanciarItemDemonstrativoFiltros();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo13";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo13/";
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
        dto.adicionarParametro("parametrosExercicioAnterior", ParametrosRelatorios.parametrosToDto(buscarParametrosPadraoExercicioAnterior()));
        dto.adicionarParametro("parametros", ParametrosRelatorios.parametrosToDto(buscarParametrosPadrao(mesReferencia)));
        dto.adicionarParametro("idExercicio", exercicio.getId());
        dto.adicionarParametro("anoExercicio", exercicio.getAno());
        dto.adicionarParametro("dataInicial", "01/01/" + exercicio.getAno());
        dto.adicionarParametro("dataFinal", Util.getDiasMes(mesReferencia.getNumeroMes(), exercicio.getAno()) + "/" + mesReferencia.getNumeroMesString() + "/" + exercicio.getAno());
        Exercicio exercicioAnterior = getExercicioFacade().getExercicioPorAno(this.exercicio.getAno() - 1);
        dto.adicionarParametro("idExercicioAnterior", exercicioAnterior.getId());
        dto.adicionarParametro("anoExercicioAnterior", exercicioAnterior.getAno());
        dto.adicionarParametro("dataInicialAnterior", "01/01/" + (exercicio.getAno() - 1));
        dto.adicionarParametro("dataFinalAnterior", "31/12/" + (exercicio.getAno() - 1));
        adicionarItemDemonstrativoFiltrosCampoACampo(dto);
        dto.adicionarParametro("FILTRO_GERAL", getFiltrosAtualizados());
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + mesReferencia.getNumeroMesString() + "-ANEXO-" + getNumeroRelatorio());
    }
}
