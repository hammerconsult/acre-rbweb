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

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-acmfea", pattern = "/relatorio/acmfea/", viewId = "/faces/financeiro/relatorio/relatorioavaliacaometasfiscais.xhtml")
})
@ManagedBean
public class RelatorioAvaliacaoCumprimentoMetasFiscaisControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LDOFacade ldoFacade;
    private LDO ldo;

    public RelatorioAvaliacaoCumprimentoMetasFiscaisControlador() {
    }

    @URLAction(mappingId = "relatorio-acmfea", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (ldo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo LDO deve ser informado.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getLDOs() {
        return Util.getListSelectItem(ldoFacade.lista());
    }

    public void gerarRelatorio() {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("ANO_EXERCICIO", ldo.getExercicio().getAno());
            dto.adicionarParametro("DATA_INICIAL", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("DATA_FINAL", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("LDO_ID", ldo.getId());
            dto.setNomeRelatorio("Relatório de Avaliação do Cumprimento das Metas Fiscais do Exercício Anterior");
            dto.setApi("contabil/avaliacao-cumprimento-metas-fiscais/");
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

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }
}
