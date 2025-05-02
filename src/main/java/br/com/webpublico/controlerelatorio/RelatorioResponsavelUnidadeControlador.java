package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.administrativo.patrimonio.RelatorioPatrimonioSuperControlador;
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
 * @author Alex
 * @since 10/02/2017 14:13
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioResponsavelUnidade", pattern = "/relatorio-responsavel-unidade/", viewId = "/faces/administrativo/patrimonio/relatorios/responsavel-pela-unidade.xhtml")})
public class RelatorioResponsavelUnidadeControlador extends RelatorioPatrimonioSuperControlador {

    @URLAction(mappingId = "novoRelatorioResponsavelUnidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        novoFiltroRelatorio();
        filtro.setInicioVigencia(getSistemaFacade().getDataOperacao());
        filtro.setFimVigencia(getSistemaFacade().getDataOperacao());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDataInicialPosteriorDataFinal();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("RELATÓRIO DE RESPONSAVEL PELA UNIDADE");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("DATAREFERENCIA", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("MUNICIPIO", montarNomeMunicipio());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE RESPONSAVEL PELA UNIDADE");
            dto.adicionarParametro("CONDICAO_GERAL", montarCondicaoGeral());
            dto.adicionarParametro("CONDICAO_RESPONSAVEL", montarCondicaoVigenciaResponsavel());
            dto.adicionarParametro("FILTROS", filtro.getFiltros());
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setApi("administrativo/responsavel-unidade-patrimonio/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            logger.error("gerarRelatorio {}", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public String montarCondicaoGeral() {
        String condicao = "";
        String criterios = "";

        if (filtro.getInicioVigencia() == null) {
            filtro.setInicioVigencia(getSistemaFacade().getDataOperacao());
        }
        if (filtro.getFimVigencia() == null) {
            filtro.setFimVigencia(getSistemaFacade().getDataOperacao());
        }
        condicao = " where to_date('" + DataUtil.getDataFormatada(filtro.getInicioVigencia()) + "', 'dd/MM/yyyy') between trunc(vwAdm.iniciovigencia) and coalesce(vwAdm.fimvigencia, to_date('" + DataUtil.getDataFormatada(filtro.getFimVigencia()) + "','dd/MM/yyyy')) ";
        criterios += " Início de Vigência: " + DataUtil.getDataFormatada(filtro.getInicioVigencia()) + ", ";
        criterios += " Fim de Vigência: " + DataUtil.getDataFormatada(filtro.getFimVigencia()) + ", ";

        if (filtro.getHierarquiaAdm() != null) {
            condicao += " and vwAdm.codigo like '" + filtro.getHierarquiaAdm().getCodigoSemZerosFinais() + "%'";
            criterios += " Unidade Administrativa: " + filtro.getHierarquiaAdm() + ", ";
        }
        if (filtro.getPessoaFisica() != null) {
            condicao += " and pessoa.id = " + filtro.getPessoaFisica().getId();
            criterios += " Responsável: " + filtro.getPessoaFisica() + ", ";
        }
        criterios = criterios.substring(0, criterios.length() - 2);
        filtro.setFiltros(criterios);
        return condicao;
    }

    public String montarCondicaoVigenciaResponsavel() {
        return " and to_date('" + DataUtil.getDataFormatada(filtro.getInicioVigencia()) + "', 'dd/MM/yyyy')  between trunc(responsavel.iniciovigencia) and coalesce(responsavel.fimvigencia, to_date('" + DataUtil.getDataFormatada(filtro.getFimVigencia()) + "','dd/MM/yyyy')) ";
    }
}
