package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoOrcamentario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoOrcamentarioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Mateus on 06/07/2015.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-liberacao-cota-empenho", pattern = "/relatorio/demonstrativo-liberacao-cota-empenho/", viewId = "/faces/financeiro/relatorio/relatorioliberacaocotaempenho.xhtml"),
    @URLMapping(id = "relatorio-demonstrativo-liberacao-cota-empenho-por-grupo", pattern = "/relatorio/demonstrativo-liberacao-cota-empenho-por-grupo/", viewId = "/faces/financeiro/relatorio/relatorioliberacaocotaempenhoporgrupo.xhtml")
})

@ManagedBean
public class RelatorioDemonstrativoLiberacaoCotaEmpenhoControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private GrupoOrcamentarioFacade grupoOrcamentarioFacade;

    private GrupoOrcamentario grupoOrcamentario;

    @URLAction(mappingId = "relatorio-demonstrativo-liberacao-cota-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
    }

    @URLAction(mappingId = "relatorio-demonstrativo-liberacao-cota-empenho-por-grupo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposPorGrupo() {
        limparCamposGeral();
        grupoOrcamentario = null;
    }

    @Override
    public void limparCamposGeral() {
        super.limparCamposGeral();
    }

    @Override
    public String getNomeRelatorio() {
        return "demonstrativo-liberacao-cota-empenho";
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = validarAndCriarRelatorioDTO();
            dto.setNomeRelatorio("demonstrativo-liberacao-cota-empenho");
            dto.setApi("contabil/demonstrativo-liberacao-cota-empenho/");
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

    public void gerarRelatorioPorGrupo() {
        try {
            RelatorioDTO dto = validarAndCriarRelatorioDTO();
            dto.setNomeRelatorio("demonstrativo-liberacao-cota-empenho-por-grupo");
            dto.setApi("contabil/demonstrativo-liberacao-cota-empenho/por-grupo/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
        }
    }

    private RelatorioDTO validarAndCriarRelatorioDTO() {
        validarDatas();
        filtros = "";
        sql = "";
        filtros = getFiltrosPeriodo();
        sqlUnidades();
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
        dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
        dto.adicionarParametro("DATA_INICIAL_SALDO", "01/01/" + getSistemaFacade().getExercicioCorrente().getAno());
        dto.adicionarParametro("DATA_INICIAL", getDataInicialFormatada());
        dto.adicionarParametro("DATA_FINAL", getDataFinalFormatada());
        dto.adicionarParametro("EXERCICIO_ID", getSistemaControlador().getExercicioCorrente().getId());
        dto.adicionarParametro("condicao", sql + geraSql());
        dto.adicionarParametro("FILTRO", atualizaFiltrosGerais());
        return dto;
    }

    private String geraSql() {
        StringBuilder stb = new StringBuilder();
        if (fonteDeRecursos != null) {
            stb.append(" and fte.id = ").append(fonteDeRecursos.getId());
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " - " + fonteDeRecursos.getDescricao() + " -";
        }
        if (grupoOrcamentario != null) {
            stb.append(" and go.id = ").append(grupoOrcamentario.getId());
            filtros += " Grupo Orçamentário: " + grupoOrcamentario.getCodigo() + " - " + grupoOrcamentario.getDescricao() + " -";
        }
        return stb.toString();
    }

    public List<GrupoOrcamentario> completarGruposOrcamentarios(String filtro) {
        return grupoOrcamentarioFacade.listaGrupoPorExercicio(getSistemaControlador().getExercicioCorrente(), filtro);
    }

    public GrupoOrcamentario getGrupoOrcamentario() {
        return grupoOrcamentario;
    }

    public void setGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario) {
        this.grupoOrcamentario = grupoOrcamentario;
    }
}
