/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.DocumentoOficialFacade;
import br.com.webpublico.negocios.EmpenhoEstornoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-empenho-estorno-intervalo",
        pattern = "/relatorio/nota-empenho-estorno-intervalo/",
        viewId = "/faces/financeiro/relatorio/relatorionotaempenhoestorno.xhtml"),
})

public class RelatorioNotaEmpenhoEstornoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EmpenhoEstornoFacade empenhoEstornoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    private List<HierarquiaOrganizacional> listaUnidades;
    private Date dataInicial;
    private Date dataFinal;
    private String numeroInicial;
    private String numeroFinal;

    @URLAction(mappingId = "relatorio-empenho-estorno-intervalo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.dataInicial = DataUtil.montaData(1, 0, sistemaFacade.getExercicioCorrente().getAno()).getTime();
        this.dataFinal = new Date();
        this.numeroInicial = null;
        this.numeroFinal = null;
        listaUnidades = new ArrayList<>();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        if (!Strings.isNullOrEmpty(numeroInicial.trim()) && Strings.isNullOrEmpty(numeroFinal.trim())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar o campo Número Final.");
        }
        if (Strings.isNullOrEmpty(numeroInicial.trim()) && !Strings.isNullOrEmpty(numeroFinal.trim())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar o campo Número Inicial");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser menor que a Data Final.");
        }
        ve.lancarException();
    }

    private String montarCondicao() {
        StringBuilder stb = new StringBuilder();
        stb.append(" and trunc(nota.dataestorno) between to_date( \'").append(DataUtil.getDataFormatada(dataInicial)).append("\' ,\'dd/MM/yyyy\') and to_date(\'").append(DataUtil.getDataFormatada(dataFinal)).append("\',\'dd/MM/yyyy\')");
        if (!Strings.isNullOrEmpty(numeroInicial.trim()) && !Strings.isNullOrEmpty(numeroFinal.trim())) {
            stb.append(" AND nota.numero between \'").append(numeroInicial.trim()).append("\' and \'").append(numeroFinal.trim()).append("\'");
        }
        if (listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            String concatena = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concatena).append(lista.getSubordinada().getId());
                concatena = ",";
            }
            stb.append(" and VW.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        } else {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            StringBuilder idUnidades = new StringBuilder();
            String concatena = "";
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                idUnidades.append(concatena).append(lista.getSubordinada().getId());
                concatena = ", ";
            }
            stb.append(" and VW.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        }
        return stb.toString();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            List<NotaExecucaoOrcamentaria> notas = empenhoEstornoFacade.buscarNotaEstornoEmpenho(montarCondicao(), CategoriaOrcamentaria.NORMAL, "NOTA DE ESTORNO DE EMPENHO");
            documentoOficialFacade.gerarRelatorioDasNotasOrcamentarias(notas, ModuloTipoDoctoOficial.NOTA_ESTORNO_PAGAMENTO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }
}
