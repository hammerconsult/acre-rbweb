/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.LDO;
import br.com.webpublico.enums.ModalidadeRenunciaLDO;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LDOFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-ecrr", pattern = "/relatorio/ecrr/", viewId = "/faces/financeiro/relatorio/relatoriocompensacaoreceita.xhtml")
})
@ManagedBean
public class RelatorioEstimativaCompensacaoRenunciaReceitaControlador extends AbstractReport implements Serializable {

    @EJB
    private LDOFacade ldoFacade;
    private LDO ldo;
    private ModalidadeRenunciaLDO modalidade;

    @URLAction(mappingId = "relatorio-ecrr", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        ldo = null;
        modalidade = null;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno());
            dto.adicionarParametro("EXERCICIO_ID", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("SQL", gerarSql());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("ESTIMATIVA E COMPENSAÇÃO DA RENÚNCIA DE RECEITA");
            dto.setApi("contabil/estimativa-compensacao-renuncia-receita/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getLdos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (LDO obj : ldoFacade.lista()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeRenunciaLDO.values());
    }

    private String gerarSql() {
        String sql = "";
        if (ldo != null) {
            sql += " and LDO.ID = " + ldo.getId();
        }
        if (modalidade != null) {
            sql += " and RENLDO.MODALIDADERENUNCIA = '" + modalidade + "'";
        }
        return sql;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    public ModalidadeRenunciaLDO getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeRenunciaLDO modalidade) {
        this.modalidade = modalidade;
    }
}
