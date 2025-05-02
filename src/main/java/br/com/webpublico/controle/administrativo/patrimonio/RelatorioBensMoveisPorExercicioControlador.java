package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
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
        @URLMapping(id = "novoRelatoriotivoBensMoveisExercicio",
                pattern = "/relatorio-bens-moveis-por-exercicio/",
                viewId = "/faces/administrativo/patrimonio/relatorios/relatoriodebensmoveisporexercicio.xhtml")})
public class RelatorioBensMoveisPorExercicioControlador extends RelatorioPatrimonioControlador {

    @URLAction(mappingId = "novoRelatoriotivoBensMoveisExercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatoriotivoBensMoveisExercicio() {
        limparCampos();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioBensMoveisPorExercicio();
            StringBuffer filtros = new StringBuffer();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE BENS MÓVEIS POR EXERCÍCIO");
            dto.adicionarParametro("condicao", montarCondicaovoRelatorioBensMoveisPorExercicio(filtros));
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(getDtReferencia()));
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("consolidado", !getDetalhar());
            dto.setNomeRelatorio("RELATÓRIO-DE-BENS-MÓVEIS-POR-EXERCÍCIO");
            dto.setApi("administrativo/bens-moveis-por-exercicio/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    private void validarFiltrosRelatorioBensMoveisPorExercicio() {
        ValidacaoException ve = new ValidacaoException();
        if (getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado.");
        }
        if (getDtReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }
        if (getExercicioInicial() == null && getExercicioFinal() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o exercício inicial e/ou final.");
        } else if(getExercicioInicial() != null && getExercicioFinal() != null) {
            if(getExercicioInicial().getAno().compareTo(getExercicioFinal().getAno()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Exercício inicial deve ser anterior ao exercício final.");
            }
        }
        ve.lancarException();
    }

    private String montarCondicaovoRelatorioBensMoveisPorExercicio(StringBuffer filtros) {
        StringBuffer sql = new StringBuffer();

        if (getHierarquiaOrganizacional() != null && getHierarquiaOrganizacional().getCodigo() != null) {
            sql.append(" and VW.CODIGO LIKE '").append(getHierarquiaOrganizacional().getCodigoSemZerosFinais()).append("%'");
            filtros.append("Unidade Organizacional: ").append(getHierarquiaOrganizacional().toString()).append(". ");
        }

        if (getHierarquiaOrganizacionalOrcamentaria() != null && getHierarquiaOrganizacionalOrcamentaria().getCodigo() != null) {
            sql.append(" and VWORC.CODIGO = '").append(getHierarquiaOrganizacionalOrcamentaria().getCodigo()).append("'");
            filtros.append("Unidade Orçamentária: ").append(getHierarquiaOrganizacionalOrcamentaria().toString()).append(". ");
        }

        if (getDtReferencia() != null) {
            filtros.append("Data de Referência: ").append(DataUtil.getDataFormatada(getDtReferencia())).append(". ");
        }

        if (getExercicioInicial() != null) {
            sql.append("  AND EXTRACT(YEAR FROM to_date(bem.dataaquisicao)) >= ").append(getExercicioInicial().getAno());
            filtros.append("Exercício Inicial: " + getExercicioInicial().getAno()).append(". ");
        }

        if (getExercicioFinal() != null) {
            sql.append("  AND EXTRACT(YEAR FROM to_date(bem.dataaquisicao)) <= ").append(getExercicioFinal().getAno());
            filtros.append("Exercício Final: " + getExercicioFinal().getAno()).append(". ");
        }

        if (getGrupoBem() != null) {
            sql.append(" and g.id = ").append(getGrupoBem().getId());
            filtros.append(" Grupo Patrimonial ").append(getGrupoBem().toString()).append(". ");
        }

        if (getEstadoConservacaoBem() != null) {
            sql.append(" and ESTADO.ESTADOBEM = '").append(getEstadoConservacaoBem().name()).append("' ");
            filtros.append(" Estado de Conservação: ").append(getEstadoConservacaoBem().getDescricao()).append(". ");
        }

        if (getDetalhar()) {
            filtros.append(" Detalhar: ").append("Sim").append(". ");
        } else {
            filtros.append(" Detalhar: ").append("Não").append(". ");
        }
        return sql.toString();
    }
}
