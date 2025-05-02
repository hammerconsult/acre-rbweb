package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoIncorporacaoMovel;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
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
public class RelatorioSolicitacaoIncorporacaoControlador extends AbstractReport {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public RelatorioSolicitacaoIncorporacaoControlador() {
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio, SolicitacaoIncorporacaoMovel selecionado) {
        try {
            HierarquiaOrganizacional hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getUnidadeAdministrativa(), selecionado.getDataSolicitacao());
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("SECRETARIA", montarNomeSecretaria(hierarquiaOrganizacional, null));
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("NOME_RELATORIO", "RELATÓRIO DE SOLICITAÇÃO DE INCORPORAÇÃO DE BENS MÓVEIS");
            dto.adicionarParametro("idSolicitacao", selecionado.getId());
            dto.setNomeRelatorio("RELATORIO-SOLICITACAO-INCORPORACAO-BEM-MOVEL");
            dto.setApi("administrativo/solicitacao-incorporacao-bem-movel/");
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

    private String montarNomeSecretaria(HierarquiaOrganizacional hierarquiaAdministrativa, HierarquiaOrganizacional hierarquiaOrcamentaria) {
        String nome = "";
        if (hierarquiaAdministrativa != null) {
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(hierarquiaAdministrativa, getSistemaFacade().getDataOperacao());
            if (secretaria != null) {
                nome = secretaria.getDescricao().toUpperCase();
            }
        }
        if (hierarquiaAdministrativa == null && hierarquiaOrcamentaria != null) {
            HierarquiaOrganizacional hierarquiaAdministrativaDaOrcamentaria = hierarquiaOrganizacionalFacade.recuperarAdministrativaDaOrcamentariaVigente(hierarquiaOrcamentaria, getSistemaFacade().getDataOperacao());
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(hierarquiaAdministrativaDaOrcamentaria, getSistemaFacade().getDataOperacao());
            if (secretaria != null) {
                nome = secretaria.getDescricao().toUpperCase();
            }
        }
        return nome;
    }
}
