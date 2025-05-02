package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.entidadesauxiliares.AssistenteConsultaMovimentacaoBemContabil;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.enums.TipoBem;
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

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioDeBensMoveisPorGrupoBemComparativo",
        pattern = "/relatorio-de-bens-moveis-por-grupo-patrimonial-comparativo/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobemmovelporgrupobemcomparativo.xhtml")})
public class RelatorioDeBensMoveisPorGrupoBemComparativoControlador extends RelatorioPatrimonioControlador {

    private FiltroPatrimonio filtroPatrimonio;

    @URLAction(mappingId = "novoRelatorioDeBensMoveisPorGrupoBemComparativo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioDeBensMoveisPorGrupoBemComparativo() {
        limparCampos();
        novoFiltroRelatorio();
    }

    private void novoFiltroRelatorio() {
        filtroPatrimonio = new FiltroPatrimonio();
        filtroPatrimonio.setDataReferencia(getSistemaFacade().getDataOperacao());
    }

    public void gerarRelatorio(AssistenteConsultaMovimentacaoBemContabil assistenteConsulta) {
        try {
            novoFiltroRelatorio();
            filtroPatrimonio.setDataReferencia(assistenteConsulta.getDataFinal());
            filtroPatrimonio.setGrupoBem(assistenteConsulta.getGrupoBem());
            filtroPatrimonio.setHierarquiaOrc(assistenteConsulta.getHierarquiaOrcamentaria());
            gerarRelatorio("PDF");
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioDeBensMoveisPorGrupoBemComparativo();
            RelatorioDTO dto = montarParametros();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public RelatorioDTO montarParametros() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.setNomeRelatorio("RELATÓRIO DE BENS MÓVEIS POR GRUPO PATRIMONIAL COMPARATIVO");
        dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
        dto.adicionarParametro("MODULO", "ADMINISTRATIVO");
        dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
        montarClausulaWhere(filtroPatrimonio, dto);
        dto.adicionarParametro("USUARIO",  getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setApi("administrativo/bens-moveis-por-grupo-bem-comparativo/");
        return dto;
    }

    private void montarClausulaWhere(FiltroPatrimonio filtroPatrimonio, RelatorioDTO dto) {
        StringBuilder where = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        where.append(" where grupobem.tipobem  = '").append(TipoBem.MOVEIS.name()).append("' ");

        if (filtroPatrimonio.getHierarquiaOrc() != null && filtroPatrimonio.getHierarquiaOrc().getCodigo() != null) {
            where.append(" AND RELATORIO.UNIDADE = ").append(filtroPatrimonio.getHierarquiaOrc().getSubordinada().getId());
            filtros.append("Unidade Orçamentária: ").append(filtroPatrimonio.getHierarquiaOrc().toString()).append(". ").append("\n");
        }

        if (filtroPatrimonio.getGrupoBem() != null) {
            where.append(" AND RELATORIO.GRUPOBEM = ").append(filtroPatrimonio.getGrupoBem().getId());
            filtros.append("Grupo Patrimonial ").append(filtroPatrimonio.getGrupoBem().toString()).append(". ").append("\n");
        }
        filtros.append("Data de Referência: ").append(DataUtil.getDataFormatada(filtroPatrimonio.getDataReferencia())).append("\n");
        dto.adicionarParametro("dataReferencia", DataUtil.dataSemHorario(filtroPatrimonio.getDataReferencia()));
        dto.adicionarParametro("FILTROS", filtros.toString());
        dto.adicionarParametro("complementoQuery", where.toString());
    }

    private void validarFiltrosRelatorioDeBensMoveisPorGrupoBemComparativo() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroPatrimonio.getHierarquiaOrc() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade orçamentária é obrigatório!");
        }
        if (filtroPatrimonio.getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de referência é obrigatório!");
        }
        ve.lancarException();
    }

    public FiltroPatrimonio getFiltroPatrimonio() {
        return filtroPatrimonio;
    }

    public void setFiltroPatrimonio(FiltroPatrimonio filtroPatrimonio) {
        this.filtroPatrimonio = filtroPatrimonio;
    }

}
