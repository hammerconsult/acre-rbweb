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
    @URLMapping(id = "novoRelatoriodeMoveisPorGrupoPatrimonial",
        pattern = "/relatorio-de-bens-moveis-por-grupo-patrimonial/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobensmoveisporgrupopatrimonial.xhtml")})
public class RelatorioDeBensMoveisPorGrupoPatrimonialControlador extends RelatorioPatrimonioControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDeBensMoveisPorGrupoPatrimonialControlador.class);

    @URLAction(mappingId = "novoRelatoriodeMoveisPorGrupoPatrimonial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatoriodeMoveisPorGrupoPatrimonial() {
        limparCampos();
        this.setDtReferencia(getSistemaFacade().getDataOperacao());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarRelatorioBensMoveisPorGrupoPatrimonial();
            StringBuffer filtros = new StringBuffer();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio(montarNomeRelatorio());
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE BENS MÓVEIS POR GRUPO PATRIMONIAL");
            dto.adicionarParametro("DATAREFERENCIA", DataUtil.getDataFormatada(getDtReferencia()));
            dto.adicionarParametro("dataReferencia", getDtReferencia());
            dto.adicionarParametro("detalhar", detalhar);
            filtros.append("Data de Referência: ").append(DataUtil.getDataFormatada(getDtReferencia())).append("\n");
            dto.adicionarParametro("CONDICAO", montarCondicaoRelatorioBensMoveisPorGrupoPatrimonial(filtros));
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setApi("administrativo/bens-moveis-por-grupo-patrimonial/");
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

    private String montarNomeRelatorio() {
        if (getHierarquiaOrganizacional() != null && getHierarquiaOrganizacionalOrcamentaria() != null) {
            String codigoAdm = getHierarquiaOrganizacional().getCodigoSemZerosFinais().subSequence(0, getHierarquiaOrganizacional().getCodigoSemZerosFinais().length() - 1).toString();
            String codigoOrc = getHierarquiaOrganizacionalOrcamentaria().getCodigoSemZerosFinais().subSequence(0, getHierarquiaOrganizacionalOrcamentaria().getCodigoSemZerosFinais().length() - 1).toString();
            return "RELATORIO-DE-BEM-MOVEL-POR-GRUPO-PATRIMONIAL" + codigoAdm.replace(".", "-") + "-" + codigoOrc.replace(".", "-") + "-" + DataUtil.getDataFormatada(getDtReferencia(), "yyyy-MM-dd");
        } else if (getHierarquiaOrganizacional() != null) {
            String codigo = getHierarquiaOrganizacional().getCodigoSemZerosFinais().subSequence(0, getHierarquiaOrganizacional().getCodigoSemZerosFinais().length() - 1).toString();
            return "RELATORIO-DE-BEM-MOVEL-POR-GRUPO-PATRIMONIAL" + codigo.replace(".", "-") + "-" + DataUtil.getDataFormatada(getDtReferencia(), "yyyy-MM-dd");
        } else {
            String codigo = getHierarquiaOrganizacionalOrcamentaria().getCodigoSemZerosFinais().subSequence(0, getHierarquiaOrganizacionalOrcamentaria().getCodigoSemZerosFinais().length() - 1).toString();
            return "RELATORIO-DE-BEM-MOVEL-POR-GRUPO-PATRIMONIAL" + codigo.replace(".", "-") + "-" + DataUtil.getDataFormatada(getDtReferencia(), "yyyy-MM-dd");
        }
    }

    private void validarRelatorioBensMoveisPorGrupoPatrimonial() {
        ValidacaoException ve = new ValidacaoException();

        if (getHierarquiaOrganizacional() == null && getHierarquiaOrganizacionalOrcamentaria() == null)
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos uma unidade administrativa ou orçamentária.");

        if (getDtReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A data de referência é obrigatória");
        }
        if ((this.getDtFinal() != null && this.getDtReferencia() != null) && this.getDtFinal().compareTo(this.getDtReferencia()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Data Final' deve ser menor ou igual a 'Data de Referência'.");
        }

        if ((this.getDtinicial() != null && this.getDtFinal() != null && this.getDtReferencia() != null) && (this.getDtinicial().compareTo(this.getDtFinal()) > 0 || this.getDtinicial().compareTo(this.getDtReferencia()) > 0)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Data Inicial' deve ser menor ou igual a 'Data Final' ou a 'Data de Referência'.");
        }

        ve.lancarException();
    }


    public String montarCondicaoRelatorioBensMoveisPorGrupoPatrimonial(StringBuffer filtros) {
        StringBuffer sql = new StringBuffer();
        filtros.append("Detalhar: ").append(getDetalhar() ? "Sim. " : "Não. ").append("\n");

        if (getHierarquiaOrganizacional() != null && getHierarquiaOrganizacional().getCodigo() != null) {
            sql.append(" and VWADM.CODIGO LIKE '").append(getHierarquiaOrganizacional().getCodigoSemZerosFinais()).append("%' ");
            filtros.append("Unidade Organizacional: ").append(getHierarquiaOrganizacional().toString()).append("\n");
        }

        if (getHierarquiaOrganizacionalOrcamentaria() != null && getHierarquiaOrganizacionalOrcamentaria().getCodigo() != null) {
            sql.append(" and VWORC.CODIGO = '").append(getHierarquiaOrganizacionalOrcamentaria().getCodigo()).append("' ");
            filtros.append("Unidade Orçametária: ").append(getHierarquiaOrganizacionalOrcamentaria().toString()).append("\n");
        }

        if (getGrupoBem() != null) {
            sql.append(" and GRUPOBEM.codigo = '").append(getGrupoBem().getCodigo()).append("' ");
            filtros.append("Grupo Patrimonial: ").append(getGrupoBem().toString()).append("\n");
        }

        if (getEstadoConservacaoBem() != null) {
            sql.append(" and ESTADO.ESTADOBEM = '").append(getEstadoConservacaoBem().name()).append("' ");
            filtros.append("Estado de conservação: ").append(getEstadoConservacaoBem().getDescricao()).append("\n");
        }

        if (getSituacaoConservacaoBem() != null) {
            sql.append(" and ESTADO.SITUACAOCONSERVACAOBEM = '").append(getSituacaoConservacaoBem().name()).append("' ");
            filtros.append("Situação de conservação: ").append(getSituacaoConservacaoBem().getDescricao()).append("\n");
        }

        if (getTipoGrupo() != null) {
            sql.append(" and ESTADO.TIPOGRUPO = '").append(getTipoGrupo().name()).append("'");
            filtros.append("Tipo Grupo: ").append(getTipoGrupo().getDescricao()).append("\n");
        }

        if (getTipoAquisicaoBem() != null) {
            sql.append(" and estado.tipoAquisicaoBem =  '").append(getTipoAquisicaoBem().name()).append("' ");
            filtros.append("Tipo de Aquisição: ").append(getTipoAquisicaoBem().getDescricao()).append("\n");
        }

        if (getDtinicial() != null) {
            sql.append(" and trunc(bem.dataaquisicao) >=  ").append(" to_date('").append(DataUtil.getDataFormatada(getDtinicial())).append("', 'dd/MM/yyyy')");
            filtros.append("Data Inicial: ").append(DataUtil.getDataFormatada(getDtinicial())).append("\n");
        }

        if (getDtFinal() != null) {
            sql.append(" and trunc(bem.dataaquisicao) <=  ").append(" to_date('").append(DataUtil.getDataFormatada(getDtFinal())).append("', 'dd/MM/yyyy')");
            filtros.append("Data Final: ").append(DataUtil.getDataFormatada(getDtFinal())).append("\n");
        }
        return sql.toString();
    }

    public void atribuirNullUnidade(){
        setHierarquiaOrganizacional(null);
        setHierarquiaOrganizacionalOrcamentaria(null);
    }
}
