package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.LoteReducaoValorBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CabecalhoRelatorioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class RelatorioReducaoValorBemControlador {

    @EJB
    private SistemaFacade sistemaFacade;

    public void gerarRelatorio(String tipoRelatorioExtensao, LoteReducaoValorBem loteReducaoValorBem) {
        try {

            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("loteReducaoId", loteReducaoValorBem.getId());
            dto.adicionarParametro("dataOperacao", sistemaFacade.getDataOperacao().getTime());
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + sistemaFacade.getMunicipio().toUpperCase());
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
               String nomeRelatorio = "RELATÓRIO DE CONFERÊNCIA DE  " + loteReducaoValorBem.getTipoReducao().getDescricao().toUpperCase()
                + " DE BEM " + loteReducaoValorBem.getTipoBem().getDescricao().toUpperCase();
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.setNomeRelatorio(nomeRelatorio);
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setApi("administrativo/relatorio-reducao-valor-bem/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }
}
