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
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mateus
 * @since 12/01/2016 14:23
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-acao", pattern = "/relatorio/demonstrativo-acao/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativoacao.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoAcaoControlador extends RelatorioContabilSuperControlador {

    private boolean exibirFontesRecursos;

    public RelatorioDemonstrativoAcaoControlador() {
    }

    @URLAction(mappingId = "relatorio-demonstrativo-acao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        exibirFontesRecursos = false;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDataDeReferencia();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("ANO_EXERCICIO", buscarExercicioPelaDataFinal().getAno() + "");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("exibirFontesRecursos", exibirFontesRecursos);
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-acao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = " Data de Referencia: " + DataUtil.getDataFormatada(dataReferencia) + " -";
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(DataUtil.getAno(dataReferencia)));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.IGUAL, "01/01/" + exercicio.getAno(), DataUtil.getDataFormatada(dataReferencia), 0, true));
        filtrosParametrosUnidade(parametros);
        adicionarExercicio(parametros);
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
        return "demonstrativo-acao";
    }

    public boolean isExibirFontesRecursos() {
        return exibirFontesRecursos;
    }

    public void setExibirFontesRecursos(boolean exibirFontesRecursos) {
        this.exibirFontesRecursos = exibirFontesRecursos;
    }
}
