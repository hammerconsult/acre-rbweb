/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Edi
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-resto-pagar", pattern = "/relatorio/restos-a-pagar-inscrito/", viewId = "/faces/financeiro/relatorio/relatorio-resto-pagar.xhtml"),
})

@ManagedBean
public class RelatorioRestoPagarControlador extends RelatorioRestoPagarSuperControlador implements Serializable {

    @URLAction(mappingId = "relatorio-resto-pagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        FacesUtil.executaJavaScript("setaFoco('Formulario:dataInicial_input')");
        dataReferencia = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDataIniciaAndFinal(false);
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("FILTRO_PERIODO", getFiltroPeriodo());
            dto.adicionarParametro("TIPOEXIBICAO", tipoExibicao.getToDto());
            if (unidadeGestora != null) {
                dto.adicionarParametro("FILTRO_UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
            }
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            dto.setNomeRelatorio(getNomeArquivo());
            dto.setApi("contabil/resto-pagar/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String getCaminhoRelatorio() {
        return "/relatorio/restos-a-pagar-inscrito/";
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        filtrosParametrosDatas(parametros);
        filtrosParametrosUnidade(parametros);
        montarParametrosGeraisEFiltros(parametros);
        return parametros;
    }

    private void filtrosParametrosDatas(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(" trunc(EMP.DATAEMPENHO)", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicialFormatada(), getDataFinalFormatada(), 2, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getIdExercicioCorrente(), null, 0, false));
    }

    @Override
    public String getNumeroRelatorio() {
        return "";
    }

    @Override
    public String getNomeArquivo() {
        return "RESTOS-A-PAGAR-INSCRITO-" + getNumeroRelatorio() + "PERIODO" + getDataPorPeriodoParaImprimir();
    }
}
