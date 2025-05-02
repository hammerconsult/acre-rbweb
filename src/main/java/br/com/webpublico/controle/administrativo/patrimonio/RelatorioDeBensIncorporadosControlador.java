package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
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
    @URLMapping(id = "novoRelatorioBensIncorporados",
        pattern = "/relatorio-bens-incorporados/novo/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobensincorporados.xhtml")})

public class RelatorioDeBensIncorporadosControlador extends RelatorioPatrimonioSuperControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDeBensIncorporadosControlador.class);

    @URLAction(mappingId = "novoRelatorioBensIncorporados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioBensIncorporados() {
        novoFiltro();
    }

    public void novoFiltro() {
        setFiltro(new FiltroPatrimonio());
        getFiltro().setDataReferencia(getSistemaFacade().getDataOperacao());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("RELATÓRIO DE BENS INCORPORADOS");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("MUNICIPIO", montarNomeMunicipio());
            dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
            dto.adicionarParametro("DATAREFERENCIA", getFiltro().getDataReferencia());
            dto.adicionarParametro("CONDICAO", montarCondicao());
            dto.adicionarParametro("FILTROS", (getFiltro().getHierarquiaAdm() != null ? "Unidade Organizacional " + getFiltro().getHierarquiaAdm().toString() : "Unidade Orçamentária " + getFiltro().getHierarquiaOrc().toString()) + " agrupado por unidade organizacional e ordenado por patrimônio.");
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setApi("administrativo/bens-incorporados/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("gerarRelatorio {}", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private String montarCondicao() {
        StringBuffer sql = new StringBuffer();
        if (getFiltro().getHierarquiaAdm() != null && getFiltro().getHierarquiaAdm().getCodigo() != null) {
            sql.append(" and vwadm.codigo like '").append(getFiltro().getHierarquiaAdm().getCodigoSemZerosFinais()).append("%'");
        }
        if (getFiltro().getHierarquiaOrc() != null && getFiltro().getHierarquiaOrc().getCodigo() != null) {
            sql.append(" and vworc.codigo = '").append(getFiltro().getHierarquiaOrc().getCodigo()).append("'");
        }
        return sql.toString();
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (getFiltro().getHierarquiaAdm() == null && getFiltro().getHierarquiaOrc() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa ou Unidade Orçamentária deve ser informado.");
        }
        ve.lancarException();
    }
}
