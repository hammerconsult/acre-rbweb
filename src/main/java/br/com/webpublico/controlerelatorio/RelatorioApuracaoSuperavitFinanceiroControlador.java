package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoExibicao;
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
import java.util.List;

/**
 * Criado por Mateus
 * Data: 17/05/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-apuracao-superavit-financeiro", pattern = "/relatorio/apuracao-superavit-financeiro/", viewId = "/faces/financeiro/relatorio/relatorio-apuracao-superavit-financeiro.xhtml")
})
public class RelatorioApuracaoSuperavitFinanceiroControlador extends RelatorioContabilSuperControlador {
    private TipoExibicao tipoExibicao;
    private boolean exibirDetalhesPorFonteTotal;
    private Exercicio exercicioAtualDataReferencia;

    @URLAction(mappingId = "relatorio-apuracao-superavit-financeiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        this.dataInicial = null;
        this.dataFinal = null;
        tipoExibicao = TipoExibicao.CONTA_DE_DESTINACAO;
        exibirDetalhesPorFonteTotal = Boolean.TRUE;
        exercicioAtualDataReferencia = buscarExercicioPelaDataDeReferencia();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDataDeReferencia();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
        dto.adicionarParametro("EXIBIR_DETALHES_FONTE", exibirDetalhesPorFonteTotal);
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("TIPOEXIBICAO", tipoExibicao.getToDto());
        filtros = getFiltrosPeriodo() + filtros;
        dto.adicionarParametro("FILTRO", atualizaFiltrosGerais());
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi("contabil/apuracao-superavit-financeiro/");
        return dto;
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":dataReferencia", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(getDataReferencia()), null, 0, true));
        filtros = " Exercício: " + getExercicioCorrente().getAno() + " -";
        filtros += " Exibir: " + tipoExibicao.getDescricao() + " -";
        adicionarFontes(parametros, " fonte.codigo ", ":codigosFontes", 1);
        adicionarContasDestinacoes(parametros, " co.codigo ", ":codigosContasDeDestinacoes", 1);
        filtrosParametrosUnidade(parametros);
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicioAtualDataReferencia.getId(), null, 0, false));
        return parametros;
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String filtro) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(filtro, exercicioAtualDataReferencia);
    }

    public List<SelectItem> getTiposDeExibicao() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicao.values(), false);
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), exercicioAtualDataReferencia);
    }

    public void atualizarExercicioAtualDataReferenciaELimparFiltros() {
        Exercicio exercicio = buscarExercicioPelaDataDeReferencia();
        if (!exercicio.equals(exercicioAtualDataReferencia)) {
            exercicioAtualDataReferencia = exercicio;
            setFontes(Lists.newArrayList());
            setContasDestinacoes(Lists.newArrayList());
            setListaUnidades(Lists.newArrayList());
            setUnidadeGestora(null);
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "Relatório de Apuração do Superávit Financeiro de Exercício Anterior";
    }

    public TipoExibicao getTipoExibicao() {
        return tipoExibicao;
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = tipoExibicao;
    }

    public boolean isExibirDetalhesPorFonteTotal() {
        return exibirDetalhesPorFonteTotal;
    }

    public void setExibirDetalhesPorFonteTotal(boolean exibirDetalhesPorFonteTotal) {
        this.exibirDetalhesPorFonteTotal = exibirDetalhesPorFonteTotal;
    }
}
