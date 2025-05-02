/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RelatorioRestosAPagarItem;
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
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
    @URLMapping(id = "resto-pagar-por-fonte", pattern = "/relatorio/resto-pagar-por-fonte/", viewId = "/faces/financeiro/relatorio/relatorio-resto-pagar-por-fonte.xhtml")})
@ManagedBean
public class RelatorioRestoPagarPorFonteControlador extends RelatorioRestoPagarSuperControlador implements Serializable {

    public RelatorioRestoPagarPorFonteControlador() {
    }

    @URLAction(mappingId = "resto-pagar-por-fonte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {

            validarDataIniciaAndFinal(false);
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("MODULO", "Execução Orçamentária");
            dto.adicionarParametro("FILTRO_PERIODO", getFiltroPeriodo());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            dto.adicionarParametro("TIPOEXIBICAO", tipoExibicao.getToDto());
            dto.adicionarParametro("DESCRICAO_TIPOEXIBICAO", tipoExibicao.getDescricao());
            if (unidadeGestora != null) {
                dto.adicionarParametro("FILTRO_UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
            }
            dto.setNomeRelatorio(getNomeArquivo());
            dto.setApi("contabil/resto-pagar-por-fonte/");
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

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        filtrosParametrosDatas(parametros);
        filtrosParametrosUnidade(parametros);
        montarParametrosGeraisEFiltros(parametros);
        if (unidadeGestora != null) {
            filtros += "".equals(filtros.trim()) ? unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao() : " - " + unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao();
        }
        return parametros;
    }

    private void filtrosParametrosDatas(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicialFormatada(), getDataFinalFormatada(), 0, true));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO", null, null, getIdExercicioCorrente(), null, 0, false));
    }

    public String getCaminhoRelatorio() {
        return "/relatorio/resto-pagar-por-fonte/";
    }

    @Override
    public String getNumeroRelatorio() {
        return " ";
    }

    @Override
    public String getNomeArquivo() {
        return "RESTO-A-PAGAR-POR-FONTE_" + "PERIODO" + getDataPorPeriodoParaImprimir();
    }

}
