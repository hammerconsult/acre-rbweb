/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author leonardo
 */
@ManagedBean(name = "relatorioServidorComMaisContratoAtivoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioServidorComMaisContratoAtivo", pattern = "/rh/relatorio-de-servidores-com-mais-de-um-contrato-ativo/", viewId = "/faces/rh/relatorios/relatorioservidorcommaisdeumcontratoativo.xhtml")
})
public class RelatorioServidorComMaisContratoAtivoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;

    public RelatorioServidorComMaisContratoAtivoControlador() {
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("dataOperacao", sistemaFacade.getDataOperacao());
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.setNomeRelatorio("SERVIDORES-COM-MAIS-DE-UM-CONTRATO-ATIVO");
            dto.setApi("rh/servidores-com-mais-de-um-contrato-ativo/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
