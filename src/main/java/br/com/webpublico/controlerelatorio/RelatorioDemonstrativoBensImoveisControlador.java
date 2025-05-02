package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created  by Mateus on 29/06/2015.
 */
@ManagedBean(name = "relatorioDemonstrativoBensImoveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-bens-imoveis", pattern = "/relatorio/demonstrativo-bens-imoveis/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativobensimoveis.xhtml")
})
public class RelatorioDemonstrativoBensImoveisControlador extends RelatorioContabilSuperControlador implements Serializable {

    private Boolean exibirOperacao;

    public RelatorioDemonstrativoBensImoveisControlador() {
    }

    @URLAction(mappingId = "relatorio-demonstrativo-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        dataReferencia = null;
        exibirOperacao = false;
    }

    public void gerarRelatorio() {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("BENSMOVEIS", false);
            dto.adicionarParametro("NOME_RELATORIO", exibirOperacao ? "Demonstrativo de Bens Imóveis por Grupo Patrimonial e Operação" : "Demonstrativo de Bens Imóveis por Grupo Patrimonial");
            dto.adicionarParametro("DATA_INICIAL", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("DATA_FINAL", DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("exibirOperacao", exibirOperacao != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", atualizaFiltrosGerais());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-bens-imoveis/");
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

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        parametros.add(new ParametrosRelatorios(null, ":DATA_INICIAL", null, OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), null, 3, true));
        parametros.add(new ParametrosRelatorios(null, ":DATA_FINAL", null, OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataFinal), null, 0, true));
        parametros = filtrosParametrosUnidade(parametros);
        if (unidadeGestora == null && getApresentacao().isApresentacaoUnidadeGestora()) {
            adicionarExercicio(parametros);
        }
        filtros = getFiltrosPeriodo() + filtros;
        atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(new SimpleDateFormat("yyyy").format(dataInicial)));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
    }

    @Override
    public String getNomeRelatorio() {
        if (exibirOperacao) {
            return "DEMONSTRATIVO-BENS-IMOVEIS-POR-OPERACAO";
        }
        return "DEMONSTRATIVO-BENS-IMOVEIS";
    }

    public Boolean getExibirOperacao() {
        return exibirOperacao;
    }

    public void setExibirOperacao(Boolean exibirOperacao) {
        this.exibirOperacao = exibirOperacao;
    }
}
