/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.LDO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LDOFacade;
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
import java.util.Date;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-metas-anuais", pattern = "/relatorios/metas-anuais/", viewId = "/faces/financeiro/relatorio/relatoriometasanuais.xhtml")
})
@ManagedBean
public class RelatorioMetasAnuaisControlador extends AbstractReport implements Serializable {

    @EJB
    private LDOFacade ldoFacade;
    private Date dataInicial;
    private Date dataFinal;
    private LDO ldo;

    public RelatorioMetasAnuaisControlador() {
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (ldo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo LDO deve ser informado.");
        }
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final.");
        }
        if (DataUtil.getAno(dataInicial).compareTo(ldo.getExercicio().getAno()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Ano da data Inicial deve ser igual o ano do LDO selecionado (" + ldo.getExercicio().getAno() + ").");
        }
        if (DataUtil.getAno(dataFinal).compareTo(ldo.getExercicio().getAno()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Ano da data Final deve ser igual o ano do LDO selecionado (" + ldo.getExercicio().getAno() + ").");
        }
        ve.lancarException();
    }

    public List<SelectItem> getLdos() {
        return Util.getListSelectItem(ldoFacade.lista());
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("ANO_EXERCICIO", ldo.getExercicio().getAno());
            dto.adicionarParametro("LDO_ID", ldo.getId());
            dto.setNomeRelatorio("Relatório de Metas Anuais");
            dto.setApi("contabil/metas-anuais/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    @URLAction(mappingId = "relatorio-metas-anuais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = new Date();
        dataFinal = new Date();
        ldo = null;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }
}
