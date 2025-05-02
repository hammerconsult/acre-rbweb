/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.FichaRPA;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FichaRPAFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author boy
 */
@ManagedBean(name = "relatorioFichaRPAControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioFichaRPA", pattern = "/rh/relatorio-de-ficha-rpa/", viewId = "/faces/rh/relatorios/relatorioficharpa.xhtml")
})
public class RelatorioFichaRPAControlador implements Serializable {

    @EJB
    private FichaRPAFacade fichaRPAFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FichaRPA fichaRPA;

    public RelatorioFichaRPAControlador() {
    }

    @URLAction(mappingId = "relatorioFichaRPA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void fichaRPA() {
        limparCampos();
    }

    public void limparCampos() {
        fichaRPA = null;
    }

    public List<FichaRPA> completaFichas(String parte) {
        return fichaRPAFacade.listaFiltrando(parte.trim(), "descricao");
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (fichaRPA == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a ficha RPA para continuar!");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("RELATÓRIO-FICHA-RPA");
            dto.adicionarParametro("NOMERELATORIO", "RECIBO DE PAGAMENTO DE AUTÔNOMO - RPA");
            dto.adicionarParametro("FICHA_ID", fichaRPA.getId());
            dto.setApi("rh/relatorio-ficha-rpa/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public FichaRPA getFichaRPA() {
        return fichaRPA;
    }

    public void setFichaRPA(FichaRPA fichaRPA) {
        this.fichaRPA = fichaRPA;
    }
}
