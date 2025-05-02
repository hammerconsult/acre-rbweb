package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.FiltroConsultaMovimentacaoEstoqueContabil;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoExibicaoDemonstrativoBensEstoque;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created  by Edi on 03/08/2015.
 */
@ManagedBean(name = "relatorioDemonstrativoBensEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-bens-estoque", pattern = "/relatorio/demonstrativo-bens-estoque/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativobensestoque.xhtml")
})
public class RelatorioDemonstrativoBensEstoqueControlador extends RelatorioContabilSuperControlador implements Serializable {
    private TipoExibicaoDemonstrativoBensEstoque tipoExibicaoDemonstrativoBensEstoque;
    private Boolean exibirAquisicaoDetalhada;

    public RelatorioDemonstrativoBensEstoqueControlador() {
    }

    @URLAction(mappingId = "relatorio-demonstrativo-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        limparCamposGeral();
        dataReferencia = null;
        tipoExibicaoDemonstrativoBensEstoque = TipoExibicaoDemonstrativoBensEstoque.TIPO_GRUPO;
        exibirAquisicaoDetalhada = false;
    }

    public void gerarRelatorio(FiltroConsultaMovimentacaoEstoqueContabil filtroMov) {
        limparCamposGeral();
        dataReferencia = filtroMov.getDataFinal();
        dataInicial = filtroMov.getDataInicial();
        dataFinal = filtroMov.getDataFinal();
        listaUnidades = Lists.newArrayList(filtroMov.getHierarquiaOrcamentaria());
        tipoExibicaoDemonstrativoBensEstoque = TipoExibicaoDemonstrativoBensEstoque.TIPO_GRUPO;
        exibirAquisicaoDetalhada = false;
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        gerarRelatorio("PDF");
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.adicionarParametro("USER", getNomeUsuarioLogado());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("tipoOrdenacaoDemonstrativoBensEstoque", tipoExibicaoDemonstrativoBensEstoque.name());
            dto.adicionarParametro("exibirTipo", tipoExibicaoDemonstrativoBensEstoque.isExibirTipo());
            dto.adicionarParametro("exibirGrupo", tipoExibicaoDemonstrativoBensEstoque.isExibirGrupo());
            dto.adicionarParametro("exibirOperacao", tipoExibicaoDemonstrativoBensEstoque.isExibirOperacao());
            dto.adicionarParametro("exibirAquisicaoDetalhada", exibirAquisicaoDetalhada);
            dto.adicionarParametro("NOME_RELATORIO", getNomeRelatorioCabecalho());
            dto.adicionarParametro("DATA_INICIAL", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("DATA_FINAL", DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-bens-estoque/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorioCabecalho() {
        String retorno;
        switch (tipoExibicaoDemonstrativoBensEstoque) {
            case TIPO_GRUPO:
                retorno = "Demonstrativo de Bem de Estoque por Grupo de Material";
                break;

            case OPERACAO:
                retorno = "Demonstrativo de Bem de Estoque por Operação";
                break;

            default:
                retorno = "Demonstrativo de Bem de Estoque por Grupo de Material e Operação";
                break;
        }
        return retorno + (exibirAquisicaoDetalhada ? " Detalhado" : "");
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "Ordenação: " + tipoExibicaoDemonstrativoBensEstoque.getDescricao() + " -";
        parametros.add(new ParametrosRelatorios(null, ":DATA_INICIAL", ":DATA_FINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        parametros = filtrosParametrosUnidade(parametros);
        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
        }
        filtros = getFiltrosPeriodo() + filtros;
        atualizaFiltrosGerais();
        return parametros;
    }

    public List<SelectItem> getTiposDeExibicao() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicaoDemonstrativoBensEstoque.values(), false);
    }

    @Override
    public String getNomeRelatorio() {
        return "DEMONSTRATIVO-DE-BEM-DE-ESTOQUE";
    }

    public TipoExibicaoDemonstrativoBensEstoque getTipoExibicaoDemonstrativoBensEstoque() {
        return tipoExibicaoDemonstrativoBensEstoque;
    }

    public void setTipoExibicaoDemonstrativoBensEstoque(TipoExibicaoDemonstrativoBensEstoque tipoExibicaoDemonstrativoBensEstoque) {
        this.tipoExibicaoDemonstrativoBensEstoque = tipoExibicaoDemonstrativoBensEstoque;
    }

    public Boolean getExibirAquisicaoDetalhada() {
        return exibirAquisicaoDetalhada;
    }

    public void setExibirAquisicaoDetalhada(Boolean exibirAquisicaoDetalhada) {
        this.exibirAquisicaoDetalhada = exibirAquisicaoDetalhada;
    }
}
