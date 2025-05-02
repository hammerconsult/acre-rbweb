/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.LDO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LDOFacade;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author andregustavo
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-riscos-fiscais", pattern = "/relatorio/riscos-fiscais/", viewId = "/faces/financeiro/relatorio/relatorioriscofiscal.xhtml")
})
public class RelatorioRiscosFiscaisControlador implements Serializable {

    @EJB
    private LDOFacade ldoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private LDO ldo;

    @URLAction(mappingId = "relatorio-riscos-fiscais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        ldo = null;
    }

    public List<SelectItem> getLdos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (LDO obj : ldoFacade.lista()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public void gerarRelatorioRiscosFiscais() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("ANO_EXERCICIO", ldo.getExercicio().getAno());
            dto.adicionarParametro("LDO_ID", ldo.getId());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.setNomeRelatorio("Relatório de Riscos Fiscais e Providência");
            dto.setApi("contabil/riscos-fiscais/");
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

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (ldo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo LDO deve ser informado.");
        }
        ve.lancarException();
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }
}
