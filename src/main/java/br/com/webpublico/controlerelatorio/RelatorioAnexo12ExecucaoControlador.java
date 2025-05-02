/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
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
 * @author major
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo12", pattern = "/relatorio/lei4320/anexo12/", viewId = "/faces/financeiro/relatorio/relatorioanexo12execucao.xhtml")
})
public class RelatorioAnexo12ExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioAnexo12ExecucaoControlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-lei4320-anexo12", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO12_LEI_4320;
        super.limparCampos();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
        FacesUtil.atualizarComponente(":Formulario:painel-demonstrativo");
    }

    public void recuperarRelatorioEAnexo() {
        recuperarRelatorio();
        buscarAnexoAnterior();
    }

    public void recuperarRelatorio() {
        itens = Lists.newArrayList();
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
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

    @Override
    public List<ParametrosRelatorios> filtrosParametrosUnidade(List<ParametrosRelatorios> parametros) {
        List<Long> idsDeUnidades = Lists.newArrayList();
        if (!unidades.isEmpty()) {
            String codigosUnidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidades) {
                idsDeUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                codigosUnidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            parametros.add(new ParametrosRelatorios(" und.id ", ":undId", null, OperacaoRelatorio.IN, idsDeUnidades, null, 1, false));
            if (!filtros.contains("Unidade(s)")) {
                filtros += " Unidade(s): " + codigosUnidades;
            }
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> uniadadesDoUsuario = getRelatoriosItemDemonstFacade().getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), exercicio, getDataExercicioAndMes(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : uniadadesDoUsuario) {
                idsDeUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsDeUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" und.id ", ":undId", null, OperacaoRelatorio.IN, idsDeUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.LIKE, this.unidadeGestora.getCodigo(), null, 1, false));
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicio().getId(), null, 0, false));
        }
        return parametros;
    }

    private List<ParametrosRelatorios> getParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros = filtrosParametrosGerais(parametros);
        filtrosParametrosUnidade(parametros);
        return parametros;
    }

    public String getNumeroRelatorio() {
        return unidadeGestora == null ? "12-CSL" : "12-" + unidadeGestora.getCodigo();
    }

    @Override
    public void acoesExtrasAoGerarOuSalvar() {
        instanciarItemDemonstrativoFiltros();
        filtros = "";
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo12";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo12/";
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
        adicionarItemDemonstrativoFiltrosCampoACampo(dto);
        dto.adicionarParametro("FILTRO_GERAL", atualizaFiltrosGerais().trim());
        dto.setNomeRelatorio("LEI4320-" + exercicio.getAno().toString() + "-" + mesReferencia.getNumeroMesString() + "-ANEXO-" + getNumeroRelatorio());
    }
}
