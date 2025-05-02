package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Mateus
 * @since 12/01/2016 14:23
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-programa", pattern = "/relatorio/demonstrativo-programa/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativoprograma.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoProgramaControlador extends RelatorioContabilSuperControlador {

    public RelatorioDemonstrativoProgramaControlador() {
    }

    @URLAction(mappingId = "relatorio-demonstrativo-programa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("contabil/detalhamento-programa/");
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getUsername(), Boolean.FALSE);
            dto.adicionarParametro("FILTRO", filtros);
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = " Data de Referencia: " + DataUtil.getDataFormatada(dataReferencia) + " -";
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(formato.format(dataReferencia)));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.IGUAL, "01/01/" + exercicio.getAno(), DataUtil.getDataFormatada(dataReferencia), 0, true));
        filtrosParametrosUnidade(parametros);
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, OperacaoRelatorio.IGUAL, exercicio.getId(), null, 0, true));
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fte.codigo ", ":fonteCodigo", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(formato.format(dataReferencia)));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
    }

    @Override
    public String getNomeRelatorio() {
        return "demonstrativo-programa";
    }
}
