package br.com.webpublico.controlerelatorio;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.HashMap;

/**
 * Criado por Mateus
 * Data: 24/05/2017.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-auxilio-funeral-por-atendimento", pattern = "/relatorio/auxilio-funeral-por-atendimento", viewId = "/faces/funeral/relatorio/relatorioauxiliofuneralporatendimento.xhtml")
})
public class RelatorioAuxilioFuneralAtendimentoControlador extends AbstractReport {

    private Date dataInicial;
    private Date dataFinal;
    private String filtros;

    public RelatorioAuxilioFuneralAtendimentoControlador() {
    }

    @URLAction(mappingId = "relatorio-auxilio-funeral-por-atendimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = getSistemaFacade().getDataOperacao();
        dataFinal = getSistemaFacade().getDataOperacao();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            String arquivoJasper = "RelatorioAuxilioFuneralAtendimento.jasper";
            HashMap parametros = Maps.newHashMap();
            parametros.put("SQL", buscarClausulaWhere());
            parametros.put("FILTROS", filtros.trim());
            parametros.put("MODULO", "Funeral");
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());

            gerarRelatorio(arquivoJasper, parametros);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String buscarClausulaWhere() {
        filtros = "";
        String sql = " and aux.datadoatendimento between to_date('" + DataUtil.getDataFormatada(dataInicial) +
            "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ";
        filtros = "Data Inicial: " + DataUtil.getDataFormatada(dataInicial) + "; Data Final: " + DataUtil.getDataFormatada(dataFinal) + "; ";
        return sql;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado!");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado!");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial deve ser anterior ou igual a data final!");
        }
        ve.lancarException();
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

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }
}
