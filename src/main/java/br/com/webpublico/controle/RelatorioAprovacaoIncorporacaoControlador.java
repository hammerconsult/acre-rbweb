package br.com.webpublico.controle;

import br.com.webpublico.entidades.AprovacaoSolicitacaoIncorporacaoMovel;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by mga on 04/06/2018.
 */
@ManagedBean
@ViewScoped
public class RelatorioAprovacaoIncorporacaoControlador {

    @EJB
    private BemFacade bemFacade;

    public RelatorioAprovacaoIncorporacaoControlador() {
    }

    public void gerarRelatorio(String tipoRelatorioExtensao, AprovacaoSolicitacaoIncorporacaoMovel selecionado) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", bemFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + bemFacade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("SECRETARIA", bemFacade.montarNomeSecretaria(getHierarquiaDaAprovacao(selecionado), null));
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("NOME_RELATORIO", "RELATÓRIO DE APROVAÇÃO DE INCORPORAÇÃO DE BENS MÓVEIS");
            dto.adicionarParametro("idAprovacao", selecionado.getId());
            dto.setNomeRelatorio("RELATORIO-APROVACAO-INCORPORACAO-BEM-MOVEL");
            dto.setApi("administrativo/aprovacao-incorporacao/");
            ReportService.getInstance().gerarRelatorio(bemFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private HierarquiaOrganizacional getHierarquiaDaAprovacao(AprovacaoSolicitacaoIncorporacaoMovel selecionado) {
        return bemFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getSolicitacao().getUnidadeAdministrativa(),
            selecionado.getDataAprovacao());
    }
}
