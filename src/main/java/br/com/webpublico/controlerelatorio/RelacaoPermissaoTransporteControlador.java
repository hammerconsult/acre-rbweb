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
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Wellington
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novorelatorioRelacaoPermissaoTransporte", pattern = "/rbtrans/relatorio/relacao-permissao-de-transporte/",
        viewId = "/faces/tributario/rbtrans/relatorio/relacaopermissaotransporte.xhtml"),
})
public class RelacaoPermissaoTransporteControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelacaoPermissaoTransporteControlador.class);

    private String tipoTransporte;
    private String tipoVigencia;
    private String tipoRelatorio;

    @EJB
    private SistemaFacade sistemaFacade;

    public RelacaoPermissaoTransporteControlador() {
    }

    public String getTipoTransporte() {
        return tipoTransporte;
    }

    public void setTipoTransporte(String tipoTransporte) {
        this.tipoTransporte = tipoTransporte;
    }

    public String getTipoVigencia() {
        return tipoVigencia;
    }

    public void setTipoVigencia(String tipoVigencia) {
        this.tipoVigencia = tipoVigencia;
    }

    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }


    @URLAction(mappingId = "novorelatorioRelacaoPermissaoTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.tipoTransporte = "T";
        this.tipoVigencia = "V";
        this.tipoRelatorio = "R";
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("tributario/relacao-permissao-transporte/");
            dto.setNomeRelatorio("Relação de Permissão de Transporte");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.adicionarParametro("SECRETARIA", "SUPERINTENDÊNCIA MUNICIPAL DE TRANSPORTE E TRÂNSITO - RBTRANS");
            dto.adicionarParametro("NOMERELATORIO", "RELAÇÃO DE PERMISSÃO DE TRANSPORTE");
            dto.adicionarParametro("TIPOTRANSPORTE", this.tipoTransporte);
            dto.adicionarParametro("TIPOVIGENCIA", this.tipoVigencia);
            dto.adicionarParametro("TIPORELATORIO", this.tipoRelatorio);
            dto.adicionarParametro("MODULO", "RBTRANS");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin().toUpperCase());
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio de relação de permissão de transporte. Erro {}", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }
}
