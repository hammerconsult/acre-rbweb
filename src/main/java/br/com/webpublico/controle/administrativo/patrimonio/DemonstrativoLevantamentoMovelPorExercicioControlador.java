package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoDemonstrativoLevantamentoMovelExercicio",
        pattern = "/demonstrativo-levantamento-moveis-por-exercicio/",
        viewId = "/faces/administrativo/patrimonio/relatorios/demostrativolevantamentomoveisporexercicio.xhtml")})

public class DemonstrativoLevantamentoMovelPorExercicioControlador extends RelatorioPatrimonioControlador {

    private static final Logger logger = LoggerFactory.getLogger(DemonstrativoLevantamentoMovelPorExercicioControlador.class);

    @URLAction(mappingId = "novoDemonstrativoLevantamentoMovelExercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoDemonstrativoLevantamentoMovelExercicio() {
        limparCampos();
        setDetalhar(Boolean.FALSE);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosDemonstrativo();
            StringBuilder filtros = new StringBuilder();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("detalhar", getDetalhar());
            dto.setNomeRelatorio("RELATÓRIO DE LEVANTAMENTO DE BENS MÓVEIS POR EXERCÍCIO");
            dto.adicionarParametro("condicaoGeral", montarCondicaoGeral(filtros));
            dto.adicionarParametro("condicaoLevantamento", montarCondicaoLevantamento());
            dto.adicionarParametro("condicaoAdministrativo", !getDetalhar() ? montarCondicaoAdministrativo() : "");
            dto.adicionarParametro("dataOperacao", getSistemaFacade().getDataOperacao().getTime());
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE LEVANTAMENTO DE BENS MÓVEIS POR EXERCÍCIO");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.setApi("administrativo/demonstrativo-levantamento-moveis-por-exercicio/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarFiltrosDemonstrativo() {
        ValidacaoException ex = new ValidacaoException();
        if (getHierarquiaOrganizacional() == null) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo unidade administrativa deve ser informado.");
        }
        if (getAnoInicial() != null
            && getAnoFinal() != null) {
            if (getAnoInicial().compareTo(getAnoFinal()) > 0) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("Exercício inicial deve ser anterior ao exercício final.");
            }
        }
        if ((getNumeroEmpenho() != null && !getNumeroEmpenho().trim().isEmpty()) && getAnoEmpenho() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O ano do empenho  deve ser informado ");
        }
        if (getAnoEmpenho() != null && (getNumeroEmpenho() == null || getNumeroEmpenho().trim().isEmpty())) {
            ex.adicionarMensagemDeCampoObrigatorio("O número do empenho deve ser informado ");
        }
        ex.lancarException();
    }

    private String montarCondicaoAdministrativo() {
        StringBuilder sql = new StringBuilder();
        if (getAnoInicial() != null && getAnoInicial() > 0) {
            sql.append("  AND EXTRACT(YEAR FROM to_date(bem.dataaquisicao)) >= ").append(getAnoInicial());
        }
        if (getAnoFinal() != null && getAnoFinal() > 0) {
            sql.append("  AND EXTRACT(YEAR FROM to_date(bem.dataaquisicao)) <= ").append(getAnoFinal());
        }
        if (getEstadoConservacaoBem() != null) {
            sql.append(" and est.estadobem = '").append(getEstadoConservacaoBem().name()).append("' ");
        }
        if ((StringUtils.isNotEmpty(getNumeroNotaFiscal())) && !getNumeroNotaFiscal().trim().isEmpty()) {
            sql.append(" and estadoNota.notafiscal like '%").append(getNumeroNotaFiscal().trim()).append("%'");
        }
        if ((getNumeroEmpenho() != null && !getNumeroEmpenho().trim().isEmpty()) && getAnoEmpenho() != null) {
            sql.append(" and notaemp.notaempenho  = '").append(getNumeroEmpenho().trim()).append("'");
            sql.append(" and extract(year from notaemp.datanotaempenho) = ").append(getAnoEmpenho());
        }
        return sql.toString();
    }

    private String montarCondicaoLevantamento() {
        StringBuilder sql = new StringBuilder();
        if (getAnoInicial() != null && getAnoInicial() > 0) {
            sql.append("  AND EXTRACT(YEAR FROM to_date(lev.dataaquisicao)) >= ").append(getAnoInicial());
        }
        if (getAnoFinal() != null && getAnoFinal() > 0) {
            sql.append("  AND EXTRACT(YEAR FROM to_date(lev.dataaquisicao)) <= ").append(getAnoFinal());
        }
        if (getEstadoConservacaoBem() != null) {
            sql.append(" and lev.estadoconservacaobem = '").append(getEstadoConservacaoBem().name()).append("' ");
        }
        if ((StringUtils.isNotEmpty(getNumeroNotaFiscal())) && !getNumeroNotaFiscal().trim().isEmpty()) {
            sql.append(" and lev.notafiscal like '%").append(getNumeroNotaFiscal().trim()).append("%'");
        }
        if ((getNumeroEmpenho() != null && !getNumeroEmpenho().trim().isEmpty()) && getAnoEmpenho() != null) {
            sql.append(" and lev.notaempenho  = '").append(getNumeroEmpenho().trim()).append("'");
            sql.append(" and extract(year from lev.datanotaempenho) = ").append(getAnoEmpenho());
        }
        if ((getObservacao() != null) && !getObservacao().trim().isEmpty()) {
            sql.append(" and lev.observacao like '%").append(getObservacao()).append("%'");
        }
        if ((getLocalizacao() != null) && !getLocalizacao().trim().isEmpty()) {
            sql.append(" and lev.localizacao like '%").append(getLocalizacao()).append("%'");
        }
        return sql.toString();
    }

    private String montarCondicaoGeral(StringBuilder filtros) {
        StringBuilder sql = new StringBuilder();

        if (getHierarquiaOrganizacional() != null && getHierarquiaOrganizacional().getCodigo() != null) {
            sql.append(" and VW.CODIGO LIKE '").append(getHierarquiaOrganizacional().getCodigoSemZerosFinais()).append("%'");
            filtros.append("Unidade Organizacional: ").append(getHierarquiaOrganizacional().toString()).append(", ");
        }
        if (getHierarquiaOrganizacionalOrcamentaria() != null && getHierarquiaOrganizacionalOrcamentaria().getCodigo() != null) {
            sql.append(" and VWORC.CODIGO = '").append(getHierarquiaOrganizacionalOrcamentaria().getCodigo()).append("'");
            filtros.append("Unidade Orçamentária: ").append(getHierarquiaOrganizacionalOrcamentaria().toString()).append(", ");
        }
        if (getAnoInicial() != null && getAnoInicial() > 0) {
            filtros.append("Exercício Inicial: " + getAnoInicial()).append(", ");
        }
        if (getAnoFinal() != null && getAnoFinal() > 0) {
            filtros.append("Exercício Final: " + getAnoFinal()).append(", ");
        }
        if (getGrupoBem() != null) {
            sql.append(" and grupo.id = ").append(getGrupoBem().getId());
            filtros.append(" Grupo Patrimonial ").append(getGrupoBem().toString()).append(", ");
        }
        if (getEstadoConservacaoBem() != null) {
            filtros.append(" Estado de Conservação: ").append(getEstadoConservacaoBem().getDescricao()).append(", ");
        }
        if ((StringUtils.isNotEmpty(getNumeroNotaFiscal())) && !getNumeroNotaFiscal().trim().isEmpty()) {
            filtros.append(" Nº Nota Fiscal: ").append(getNumeroNotaFiscal()).append(", ");
        }
        if ((getNumeroEmpenho() != null && !getNumeroEmpenho().trim().isEmpty()) && getAnoEmpenho() != null) {
            filtros.append(" Empenho: ").append(getNumeroEmpenho()).append("/").append(getAnoEmpenho()).append(", ");
        }
        if (getDetalhar()) {
            filtros.append(" Detalhar: ").append("Sim").append(". ");
        } else {
            filtros.append(" Detalhar: ").append("Não").append(". ");
        }
        return sql.toString();
    }
}
