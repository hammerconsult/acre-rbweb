/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.MatriculaFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Peixe
 */
@ManagedBean(name = "declaracaoDeBensControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorioDeclaracaoBens", pattern = "/rh/guia-de-declaracao-de-bens-do-servidor/", viewId = "/faces/rh/relatorios/declaracaodebens.xhtml")
})
public class DeclaracaoDeBensControlador extends AbstractReport implements Serializable {

    private MatriculaFP matriculaFP;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;

    public DeclaracaoDeBensControlador() {
        geraNoDialog = true;
    }

    @URLAction(mappingId = "relatorioDeclaracaoBens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        matriculaFP = null;
    }

    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    public List<MatriculaFP> completaMatricula(String parte) {
        return matriculaFPFacade.recuperaMatriculaFiltroPessoa(parte.trim());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try{
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("ANO", String.valueOf(DataUtil.getAno(UtilRH.getDataOperacao())));
            dto.adicionarParametro("MATRICULA_ID", matriculaFP.getId());
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("RELATÓRIO DE DECLARAÇÃO DE BENS");
            dto.setApi("rh/declaracao-de-bens/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Ocorreu um erro ao gerar o relatorio: " + e);
        }
    }

    private void validarCampos() {
        ValidacaoException vl = new ValidacaoException();
        if (matriculaFP == null) {
            vl.adicionarMensagemDeCampoObrigatorio("Por favor selecione uma pessoa.");
        }
        vl.lancarException();
    }
}
