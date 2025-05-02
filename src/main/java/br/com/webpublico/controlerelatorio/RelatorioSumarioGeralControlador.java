package br.com.webpublico.controlerelatorio;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 30/10/13
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */

import br.com.webpublico.enums.LogoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-sumario-geral", pattern = "/relatorio/planejamento/sumario-geral/", viewId = "/faces/financeiro/relatorio/relatoriosumariogeral.xhtml")
})
public class RelatorioSumarioGeralControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    private LogoRelatorio logoRelatorio;
    private Boolean mostrarUsuario;

    @URLAction(mappingId = "relatorio-sumario-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        logoRelatorio = LogoRelatorio.PREFEITURA;
        mostrarUsuario = Boolean.FALSE;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", mostrarUsuario ? sistemaFacade.getUsuarioCorrente().getNome() : "", false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("EXERCICIO", sistemaFacade.getExercicioCorrente().getAno());
            dto.adicionarParametro("EXERCICIO_ID", sistemaFacade.getExercicioCorrente().getId());
            dto.adicionarParametro("DATA", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("isCamara", LogoRelatorio.CAMARA.equals(logoRelatorio));
            dto.adicionarParametro("MUNICIPIO", LogoRelatorio.PREFEITURA.equals(logoRelatorio) ? "MUNICÍPIO DE RIO BRANCO" : "CÂMARA MUNICIPAL DE RIO BRANCO");
            dto.setNomeRelatorio("Relatório de Sumário Geral");
            dto.setApi("contabil/sumario-geral/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getLogos() {
        return Util.getListSelectItemSemCampoVazio(LogoRelatorio.values());
    }

    public LogoRelatorio getLogoRelatorio() {
        return logoRelatorio;
    }

    public void setLogoRelatorio(LogoRelatorio logoRelatorio) {
        this.logoRelatorio = logoRelatorio;
    }

    public Boolean getMostrarUsuario() {
        return mostrarUsuario;
    }

    public void setMostrarUsuario(Boolean mostrarUsuario) {
        this.mostrarUsuario = mostrarUsuario;
    }
}
