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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-mfacftea", pattern = "/relatorio/mfacftea/", viewId = "/faces/financeiro/relatorio/relatoriometasfiscaiscompexanteriores.xhtml")
})
@ManagedBean
public class RelatorioMetasFiscaisCompExAnterioresControlador extends AbstractReport implements Serializable {

    @EJB
    private LDOFacade ldoFacade;
    private Date dataInicial;
    private Date dataFinal;
    private LDO ldo;

    @URLAction(mappingId = "relatorio-mfacftea", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = getSistemaFacade().getDataOperacao();
        dataFinal = getSistemaFacade().getDataOperacao();
        ldo = null;
    }

    private void validarFiltros() {
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
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser menor ou igual à Data Final.");
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
            dto.adicionarParametro("LDO_ID", ldo.getId());
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO - AC");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("dataFinal", DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("dataInicialMaisUm", dataFormatadaMaisUm(dataInicial));
            dto.adicionarParametro("dataFinalMaisUm", dataFormatadaMaisUm(dataFinal));
            dto.adicionarParametro("dataInicialMaisDois", dataFormatadaMaisUm(dataFormatadaMaisUm(dataInicial)));
            dto.adicionarParametro("dataFinalMaisDois", dataFormatadaMaisUm(dataFormatadaMaisUm(dataFinal)));
            dto.adicionarParametro("dataInicialMenosUm", dataFormatadaMenosUm(dataInicial));
            dto.adicionarParametro("dataFinalMenosUm", dataFormatadaMenosUm(dataFinal));
            dto.adicionarParametro("dataInicialMenosDois", dataFormatadaMaisUm(dataFormatadaMenosUm(dataInicial)));
            dto.adicionarParametro("dataFinalMenosDois", dataFormatadaMaisUm(dataFormatadaMenosUm(dataFinal)));
            dto.adicionarParametro("dataInicialMenosTres", dataFormatadaMaisUm(dataFormatadaMaisUm(dataFormatadaMenosUm(dataInicial))));
            dto.adicionarParametro("dataFinalMenosTres", dataFormatadaMaisUm(dataFormatadaMaisUm(dataFormatadaMenosUm(dataFinal))));
            dto.adicionarParametro("ANO_EXERCICIO", ldo.getExercicio().getAno());
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeRelatorio("METAS FISCAIS ATUAIS COMPARADAS COM AS FIXADAS NOS TRÊS EXERCÍCIOS ANTERIORES");
            dto.setApi("contabil/metais-fiscais-anuais-comparadas-fixadas-tres-exercicios-anteriores/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String dataFormatadaMenosUm(Date data) {
        if (data != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = format.format(data).substring(0, 6);
            Integer ano = Integer.parseInt(format.format(data).substring(6, 10)) - 1;
            return dataFormatada + ano;
        } else {
            return "";
        }
    }

    private String dataFormatadaMaisUm(Date data) {
        if (data != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = format.format(data).substring(0, 6);
            Integer ano = Integer.parseInt(format.format(data).substring(6, 10)) + 1;
            return dataFormatada + ano;
        } else {
            return "";
        }
    }

    private String dataFormatadaMaisUm(String data) {
        if (data != null) {
            String dataFormatada = data.substring(0, 6);
            Integer ano = Integer.parseInt(data.substring(6, 10)) + 1;
            return dataFormatada + ano;
        } else {
            return "";
        }
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
