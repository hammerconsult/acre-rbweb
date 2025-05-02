package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
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
        @URLMapping(id = "novoTermoDeResponsabilidadeLevantamneto",
                pattern = "/termo-responsabilidade-levantamento-de-bens/",
                viewId = "/faces/administrativo/patrimonio/levantamentodebenspatrimoniais/termoresponsabilidade.xhtml")})
public class TermoDeResponsabilidadeLevantamentoControlador extends RelatorioPatrimonioSuperControlador {

    private static final Logger logger = LoggerFactory.getLogger(TermoDeResponsabilidadeLevantamentoControlador.class);

    @URLAction(mappingId = "novoTermoDeResponsabilidadeLevantamneto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtro = new FiltroPatrimonio();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getHierarquiaAdm() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            String nomeRelatorio = "TERMO DE RESPONSABILIDADE";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("DATAREFERENCIA", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("condicao", montarCondicao());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/termo-responsabilidade-levantamento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicao() {
        StringBuilder sql = new StringBuilder();
        String data = DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao());
        sql.append(" where vw.id = ").append(filtro.getHierarquiaAdm().getId());
        sql.append(" and to_date('").append(data).append("', 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,").append(" to_date('").append(data).append("', 'dd/MM/yyyy')) ");
        return sql.toString();
    }
}
